package com.quakearts.test.beans;

import java.io.Serializable;

import javax.annotation.ManagedBean;

@ManagedBean("testManagedBeanNames")
public class TestBean8 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9056793199208132065L;

	private String aString;
	
	public String getaString() {
		return aString;
	}
	
	public void setaString(String aString) {
		this.aString = aString;
	}
}
