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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="result-column",namespace="http://quakearts.com/xml/wrapperClassSchema")
public class ResultColumn {
	@XmlAttribute(required=true)
	private String variableName="";
	@XmlAttribute(required=true)
	private int columnNumber;
	@XmlAttribute(required=true)
	private int type;
	@XmlAttribute(required=true)
	private String javaTypeName="";
	@XmlAttribute
	private String javaTypeImport="";
	
	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String javaName) {
		if(javaName!=null)
			this.variableName = javaName;
	}

	public int getColumnNumber() {
		return columnNumber;
	}

	public void setColumnNumber(int columnNumber) {
		this.columnNumber = columnNumber;
	}

	public int getType() {
		return type;
	}

	public String getJavaTypeName() {
		return javaTypeName;
	}

	public void setJavaTypeName(String javaTypeName) {
		if(javaTypeName!=null)
			this.javaTypeName = javaTypeName;
	}

	public String getJavaTypeImport() {
		return javaTypeImport;
	}

	public void setJavaTypeImport(String javaTypeImport) {
		if(javaTypeImport!=null)
			this.javaTypeImport = javaTypeImport;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	@Override
	public int hashCode() {
		int hashcode = 0;
		hashcode = 2*hashcode +variableName.hashCode();
		hashcode = 2*hashcode +type;
		hashcode = 2*hashcode +columnNumber;
		
		return hashcode;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj ==null)
			return false;	
		if(obj == this)
			return true;
		if(!(obj instanceof ResultColumn)){
			return false;
		}
		ResultColumn subject = (ResultColumn) obj;
		if(!subject.variableName.equals(variableName)||subject.columnNumber!=columnNumber||subject.type!=type)
			return false;
		return true;
	}
}
