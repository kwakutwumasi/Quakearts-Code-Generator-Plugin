package com.quakearts.test.beans;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;

import com.quakearts.webapp.codegeneration.annotations.CodeGeneratorProperties;
import com.quakearts.webapp.codegeneration.annotations.CodeGeneratorProperty;

@ManagedBean(name="testFacesManagedBeanName")
@CodeGeneratorProperty(key="id", property="idString")
public class TestBean7 implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2980318230325086020L;
	@CodeGeneratorProperties({
		@CodeGeneratorProperty(key="testFieldPropertiesKey1", property="testFieldPropertiesValue1"),
		@CodeGeneratorProperty(key="testFieldPropertiesKey2", property="testFieldPropertiesValue2")
	})
	private String idString;
	private String stringForIgnoredProperty;

	public String getIdString() {
		return idString;
	}

	public void setIdString(String idString) {
		this.idString = idString;
	}
	
	public void setStringForIgnoredProperty(String stringForIgnoredProperty) {
		this.stringForIgnoredProperty = stringForIgnoredProperty;
	}
	
	@Override
	public String toString() {
		return stringForIgnoredProperty;
	}
}
