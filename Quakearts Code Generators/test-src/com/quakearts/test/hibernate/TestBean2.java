package com.quakearts.test.hibernate;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class TestBean2 implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6532477579493697412L;
	@Id
	private int intid;
	@ManyToOne
	private TestBean3 testBean31;
	@ManyToOne
	private TestBean3 testBean32;
	private String aString;

	public int getIntid() {
		return intid;
	}

	public void setIntid(int intid) {
		this.intid = intid;
	}

	public TestBean3 getTestBean31() {
		return testBean31;
	}

	public void setTestBean31(TestBean3 testBean31) {
		this.testBean31 = testBean31;
	}

	public TestBean3 getTestBean32() {
		return testBean32;
	}

	public void setTestBean32(TestBean3 testBean32) {
		this.testBean32 = testBean32;
	}

	public String getaString() {
		return aString;
	}

	public void setaString(String aString) {
		this.aString = aString;
	}

}
