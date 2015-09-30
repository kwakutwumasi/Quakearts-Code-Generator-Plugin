package com.quakearts.datatools.abstraction;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="parameterType", namespace="http://quakearts.com/xml/wrapperClassSchema")
public class Parameter {
	@XmlAttribute(required=true)
	protected String variableName="";
	@XmlAttribute(required=true)
	protected int type;
	@XmlAttribute(required=true)
	protected String javaTypeName="";
	@XmlAttribute
	protected String javaTypeImport="";
	@XmlAttribute(required=true)
	protected String sqlTypesString="";
	@XmlAttribute(required=true)
	protected int callPosition;
	@XmlAttribute
	protected boolean outputEnabled;
	@XmlAttribute
	protected boolean inoutEnabled;
	@XmlAttribute
	protected boolean defaultParameter;
	@XmlAttribute
	protected boolean nullable;
	@XmlAttribute
	protected String defaultValue="";

	public boolean isDefaultParameter() {
		return defaultParameter;
	}

	public void setDefaultParameter(boolean defaultParameter) {
		this.defaultParameter = defaultParameter;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String javaName) {
		this.variableName = javaName;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isOutputEnabled() {
		return outputEnabled;
	}

	public void setOutputEnabled(boolean outputEnabled) {
		this.outputEnabled = outputEnabled;
		if(outputEnabled)
			inoutEnabled = false;
	}

	public int getCallPosition() {
		return callPosition;
	}

	public void setCallPosition(int callPosition) {
		this.callPosition = callPosition;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public boolean isInoutEnabled() {
		return inoutEnabled;
	}

	public void setInoutEnabled(boolean inoutEnabled) {
		this.inoutEnabled = inoutEnabled;
		if(inoutEnabled)
			outputEnabled = false;
	}

	public String getJavaTypeName() {
		return javaTypeName;
	}

	public void setJavaTypeName(String javaTypeName) {
		if(javaTypeName!=null)
			this.javaTypeName = javaTypeName;
	}

	public String getSqlTypesString() {
		return sqlTypesString;
	}

	public void setSqlTypesString(String sqlTypesString) {
		if(sqlTypesString!=null)
			this.sqlTypesString = sqlTypesString;
	}

	public String getJavaTypeImport() {
		return javaTypeImport;
	}

	public void setJavaTypeImport(String javaTypeImport) {
		if(javaTypeImport!=null)
			this.javaTypeImport = javaTypeImport;
	}

	@Override
	public int hashCode() {
		int hashcode = variableName.hashCode()+callPosition*10+type*100;
		return hashcode;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj ==null)
			return false;
		
		if(obj == this)
			return true;
		
		if(!(obj instanceof Parameter))
			return false;
		
		Parameter subject=(Parameter) obj;
		if(!subject.variableName.equals(variableName)||subject.callPosition!=callPosition||subject.type!=subject.type)
			return false;
		
		return true;
	}
}
