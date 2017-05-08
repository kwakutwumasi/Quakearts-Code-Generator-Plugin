/*******************************************************************************
 * Copyright (C) 2017 Kwaku Twumasi-Afriyie <kwaku.twumasi@quakearts.com>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Kwaku Twumasi-Afriyie <kwaku.twumasi@quakearts.com> - initial API and implementation
 ******************************************************************************/
package com.quakearts.datatools.generator.readers;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

import com.quakearts.datatools.abstraction.Parameter;
import com.quakearts.datatools.abstraction.Procedure;
import com.quakearts.datatools.abstraction.ProcedureResult;
import com.quakearts.datatools.abstraction.ResultColumn;
import com.quakearts.datatools.navigator.NavigatorBase;

public class ProcedureResultsReader extends NavigatorBase {

	private Object[] parameterDatas;
	private Procedure procedure;
	private int returnCode=-1;
	private boolean useMultipleResultsStyle = false;
	
	public Procedure getProcedure() {
		return procedure;
	}

	public ProcedureResultsReader() {
	}

	public int getReturnCode() {
		return returnCode;
	}
	
	public Procedure getProcedureResult(Procedure procedure, Object[] parameters,
			String profileName, boolean useMultipleResultsStyle) {
		if(parameters==null)
			this.parameterDatas = new Object[0];
		else
			this.parameterDatas = parameters;
		
		this.procedure = procedure;
		this.useMultipleResultsStyle = useMultipleResultsStyle;
		
		setupAndPerformDBAction(profileName);
		return procedure;
	}

	@Override
	protected void performDBAction(Connection connection) throws SQLException {
		StringBuilder callStatementString;
		returnCode = -1;
		int offset = 1;
		if(procedure.isReturnCodeEnabled()) {
			 callStatementString = new StringBuilder("{ ?= call "+procedure.getProcedureName() +(parameterDatas.length>0?" (":""));
			 offset = 2;
		} else
			 callStatementString = new StringBuilder("{ call "+procedure.getProcedureName() +(parameterDatas.length>0?" (":""));
		
		for(int i=0;i<parameterDatas.length;i++){
			callStatementString.append(i>0?",":"").append("?").append(i==parameterDatas.length-1?")":"");
		}
		
		callStatementString.append("}");
		
		CallableStatement callableStatement = connection.prepareCall(callStatementString.toString());
		
		if(procedure.isReturnCodeEnabled())
			callableStatement.registerOutParameter(1, Types.INTEGER);
				
		for(Parameter parameter:procedure.getParameters()){
			if(parameter.isInoutEnabled() || parameter.isOutputEnabled())
				callableStatement.registerOutParameter(parameter.getCallPosition(), parameter.getType());
		}
		
		for(Parameter parameter:procedure.getParameters()){
			Object parameterData = parameterDatas[parameter.getCallPosition()-offset];

			if(!parameter.isOutputEnabled()){
				if(parameterData instanceof Integer){
					callableStatement.setInt(parameter.getCallPosition(),(Integer) parameterData);
				} else if (parameterData instanceof Long){
					callableStatement.setLong(parameter.getCallPosition(),(Long) parameterData);
				} else if (parameterData instanceof Double){
					callableStatement.setDouble(parameter.getCallPosition(),(Double) parameterData);
				} else if (parameterData instanceof Date){
					callableStatement.setDate(parameter.getCallPosition(), new java.sql.Date(((Date) parameterData).getTime()));
				} else if (parameterData instanceof Boolean){
					callableStatement.setBoolean(parameter.getCallPosition(),(Boolean)parameterData);
				} else if (parameterData !=null && !parameterData.toString().isEmpty()){
					callableStatement.setString(parameter.getCallPosition(),parameterData.toString());
				} else {
					callableStatement.setNull(parameter.getCallPosition(), parameter.getType());
				}
			}
		}
		
		if(useMultipleResultsStyle){
			boolean results = callableStatement.execute();
			if(results){
				do{
					ResultSet rs = callableStatement.getResultSet();
					processResultSet(rs);
				}while(callableStatement.getMoreResults());
			}
		} else {
			ResultSet rs = callableStatement.executeQuery();
			if(rs.next()){
				processResultSet(rs);
			}
		}
		if(procedure.isReturnCodeEnabled())
			returnCode = callableStatement.getInt(1);
	}
	
	private void processResultSet(ResultSet rs) throws SQLException{
		int rowcount=1;
		ProcedureResult result = new ProcedureResult();
		ResultSetMetaData metaData = rs.getMetaData();
		for(int i=1;i<=metaData.getColumnCount();i++){
			ResultColumn column = new ResultColumn();
			String columnName = metaData.getColumnName(i);
			if(columnName !=null && !columnName.trim().isEmpty()){
				String variableName = camelize(metaData.getColumnLabel(i), false);
				if(KeywordCorrecter.getJavakeywords().contains(variableName))
					variableName = KeywordCorrecter.convertKeyWord(variableName);
				
				column.setVariableName(variableName);
			}else{
				column.setVariableName("column"+i);
			}
			
			column.setColumnNumber(i);
			column.setType(metaData.getColumnType(i));
			String[] info = getInfo(column.getType());
			column.setJavaTypeImport(info[0]);
			column.setJavaTypeName(info[1]);
			result.getResultColumns().add(column);					
		}
		while (rs.next()) {
			rowcount++;
		}
		result.setMultiRow(rowcount>1);
		result.setRowCount(rowcount);
		procedure.getProcedureResults().add(result);
	}
}
