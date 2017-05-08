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
package com.quakearts.datatools.abstraction;

import java.util.Collection;
import java.util.HashMap;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

import com.quakearts.datatools.abstraction.util.ParameterComparator;
import com.quakearts.datatools.abstraction.util.ProcedureResultComparator;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "procedureType", namespace="http://quakearts.com/xml/wrapperClassSchema", propOrder = {
    "parameters",
    "procedureResults"
})
public class Procedure {
	@XmlElementWrapper(name="parameters")
	@XmlElements({@XmlElement(name="parameter",type=Parameter.class)})
	private Collection<Parameter> parameters = new TreeSet<Parameter>(new ParameterComparator());
	@XmlElements({@XmlElement(name="procedureResult",type=ProcedureResult.class)})
	@XmlElementWrapper(name="procedureResults")
	private Collection<ProcedureResult> procedureResults = new TreeSet<ProcedureResult>(new ProcedureResultComparator());
	@XmlAttribute
	private boolean returnCodeEnabled;
	@XmlAttribute(required=true)
	private String procedureName="";
	
	public String getProcedureName() {
		return procedureName;
	}

	public void setProcedureName(String procedureName) {
		this.procedureName = procedureName;
	}

	public Collection<Parameter> getParameters() {
		return parameters;
	}

	public Collection<ProcedureResult> getProcedureResults() {
		return procedureResults;
	}

	public boolean isReturnCodeEnabled() {
		return returnCodeEnabled;
	}

	public void setReturnCodeEnabled(boolean returnCodeEnabled) {
		this.returnCodeEnabled = returnCodeEnabled;
	}

	public boolean isValid() {
		Parameter[] parametersPositions = new Parameter[parameters.size()+(returnCodeEnabled?2:1)];
		for (Parameter parameter : parameters) {
			if(parameter.getJavaTypeName()==null||
			parameter.getSqlTypesString()==null||
			parameter.getVariableName()==null)
				return false;

			if(parameter.getJavaTypeName().isEmpty()||
			parameter.getSqlTypesString().isEmpty()||
			parameter.getVariableName().isEmpty())
				return false;

			if(parametersPositions[parameter.getCallPosition()]==null)
				parametersPositions[parameter.getCallPosition()]=parameter;
			else{
				return false;
			}
		}
		
		for (ProcedureResult result : procedureResults) {
			if(result.getJavaClassName()==null||result.getJavaClassName().trim().isEmpty())
				return false;
			
			HashMap<Integer, ResultColumn> resultColumns = new HashMap<Integer, ResultColumn>();
			if(result.getResultColumns().size()==0){
				return false;
			}
			for(ResultColumn column: result.getResultColumns()){
				if(column.getJavaTypeName()==null||column.getVariableName()==null)
					return false;

				if(column.getJavaTypeName().isEmpty()||column.getVariableName().isEmpty())
					return false;
				
				ResultColumn checkColumn = resultColumns.get(new Integer(column.getColumnNumber()));
				if(checkColumn!=null){
					return false;
				}else{
					resultColumns.put(new Integer(column.getColumnNumber()), column);
				}
			}
		}
		
		return !procedureName.isEmpty();
	}

	@Override
	public int hashCode() {
		long hashCode=0;
		hashCode = procedureName.hashCode();
		for(Parameter parameter:parameters){
			hashCode = parameter.hashCode()*10;
		}
		for(ProcedureResult procedureResult:procedureResults){
			hashCode = procedureResult.hashCode()*100;
		}
		if(returnCodeEnabled)
			hashCode++;
		return (int)hashCode;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj ==null)
			return false;
		
		if(obj == this)
			return true;
		
		if(!(obj instanceof ProcedureResult))
			return false;

		Procedure subject = (Procedure) obj;
		
		for(Parameter parameter:parameters){
			if(!subject.parameters.contains(parameter))
				return false;
		}

		for(ProcedureResult procedureResult:procedureResults){
			if(!subject.procedureResults.contains(procedureResult))
				return false;
		}
		
		if(!procedureName.equals(subject.procedureName))
			return false;
		
		return subject.returnCodeEnabled == returnCodeEnabled;
	}
}
