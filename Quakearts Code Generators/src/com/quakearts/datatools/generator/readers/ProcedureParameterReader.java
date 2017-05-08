/*******************************************************************************
* Copyright (C) 2016 Kwaku Twumasi-Afriyie <kwaku.twumasi@quakearts.com>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Kwaku Twumasi-Afriyie <kwaku.twumasi@quakearts.com> - initial API and implementation
 ******************************************************************************/
package com.quakearts.datatools.generator.readers;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.quakearts.datatools.abstraction.Parameter;
import com.quakearts.datatools.abstraction.Procedure;
import com.quakearts.datatools.navigator.NavigatorBase;

public class ProcedureParameterReader extends NavigatorBase {
	private String procedureName, schema, catalog;
	private Procedure procedure;

	public Procedure getProcedure() {
		return procedure;
	}

	public ProcedureParameterReader() {
	}
	
	public void getProcedureAndParameters(String catalog,String schema,
			String procedureName, String profileName,final Procedure procedure) {
		this.catalog=catalog;
		this.procedureName = procedureName;
		this.schema = schema;
		this.procedure = procedure;
		procedure.setProcedureName((catalog != null && !catalog.trim().isEmpty() ? catalog + "." : "")
				+ (schema != null && !schema.trim().isEmpty() ? schema + "." : "")
				+ (procedureName.indexOf(' ') != -1 ? "[" + procedureName + "]" : procedureName));
		setupAndPerformDBAction(profileName);
	}

	@Override
	protected void performDBAction(Connection connection) throws SQLException {
		ResultSet rs;
		DatabaseMetaData metaData = connection.getMetaData();
		rs = metaData.getProcedureColumns(catalog, schema == null || schema.trim().isEmpty() ? null : schema,
				procedureName, null);
    	int i=1;
		if (rs.next()) {
			do {
				Parameter parameter = new Parameter();
				int column = rs.getInt("COLUMN_TYPE");
				if(column == DatabaseMetaData.procedureColumnReturn){
					procedure.setReturnCodeEnabled(true);
					i++;
					continue;
				} else if(column == DatabaseMetaData.procedureColumnInOut) {
					parameter.setInoutEnabled(true);
				} else if (column == DatabaseMetaData.procedureColumnOut){
					parameter.setOutputEnabled(true);
				}
				
				parameter.setCallPosition(i++);
				String variableName = camelize(rs.getString("COLUMN_NAME"), false);
				if(KeywordCorrecter.getJavakeywords().contains(variableName))
					variableName = KeywordCorrecter.convertKeyWord(variableName);
				
				parameter.setVariableName(variableName);
				parameter.setType(rs.getInt("DATA_TYPE"));
				parameter.setNullable(rs.getInt("NULLABLE") != DatabaseMetaData.procedureNoNulls);
				String[] info = getInfo(parameter.getType());
				parameter.setJavaTypeImport(info[0]);
				parameter.setJavaTypeName(info[1]);
				parameter.setSqlTypesString(info[2]);
				procedure.getParameters().add(parameter);
			} while (rs.next());
		}
	}

}
