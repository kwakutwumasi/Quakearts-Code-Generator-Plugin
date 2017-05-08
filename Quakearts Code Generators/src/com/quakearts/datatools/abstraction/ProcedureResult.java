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
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

import com.quakearts.datatools.abstraction.util.ResultColumnComparator;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="procedureResultType", namespace="http://quakearts.com/xml/wrapperClassSchema", propOrder={
	"resultColumns"
})
public class ProcedureResult {
	@XmlElementWrapper(name="resultColumns",required=true)
	@XmlElements({@XmlElement(name="resultColumn",type=ResultColumn.class,required=true)})
	private Collection<ResultColumn> resultColumns = new TreeSet<ResultColumn>(new ResultColumnComparator());
	@XmlAttribute
	private int rowCount;
	@XmlAttribute(required=true)
	private String javaClassName="";
	@XmlAttribute
	private String condition="";
	@XmlAttribute
	private boolean multiRow;
	
	public String getJavaClassName() {
		return javaClassName;
	}

	public void setJavaClassName(String javaName) {
		this.javaClassName = javaName;
	}

	public Collection<ResultColumn> getResultColumns() {
		return resultColumns;
	}

	public int getRowCount() {
		return rowCount;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public boolean isMultiRow() {
		return multiRow;
	}

	public void setMultiRow(boolean multiRow) {
		this.multiRow = multiRow;
	}

	@Override
	public int hashCode() {
		int hashcode = 0;
		
		for(ResultColumn column:resultColumns)
			hashcode= 2*hashcode+column.hashCode();

		hashcode = 2*hashcode+javaClassName.hashCode(); 
		return hashcode;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null)
			return false;
		
		if(obj == this)
			return true;
		
		if(!(obj instanceof ProcedureResult))
			return false;
		
		ProcedureResult subject =  (ProcedureResult) obj;
		if(subject.resultColumns.size()!=resultColumns.size())
			return false;
		
		for(ResultColumn column:resultColumns){
			if(!subject.resultColumns.contains(column))
				return false;
		}
		
		return true;
	}
	
	
}
