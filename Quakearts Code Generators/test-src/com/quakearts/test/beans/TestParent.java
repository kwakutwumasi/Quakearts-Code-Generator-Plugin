package com.quakearts.test.beans;

import java.io.Serializable;

public class TestParent implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6781872407021380580L;
	private String inheritedString;
	private String stringForSkippedInheritedMethod;
	
	public String getInheritedString() {
		return inheritedString;
	}
	
	public void setInheritedString(String inheritedString) {
		this.inheritedString = inheritedString;
	}
	
	public String getStringForSkippedInheritedMethod() {
		return stringForSkippedInheritedMethod;
	}
	
	public void setStringForSkippedInheritedMethod(String stringForSkippedInheritedMethod) {
		this.stringForSkippedInheritedMethod = stringForSkippedInheritedMethod;
	}
}
