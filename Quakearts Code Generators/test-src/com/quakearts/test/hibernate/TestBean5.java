package com.quakearts.test.hibernate;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import com.quakearts.webapp.codegeneration.annotations.Order;

@Entity
public class TestBean5 implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5528082111473758116L;
	@Id
	private String aString;
	@Order(4)
	@OneToOne(mappedBy = "testBean5")
	private TestBean3 testBean3;
	@Order(2)
	private String secondString;
	@Order(1)
	private String firstString;
	@Order(3)
	private String thirdString;

	public String getaString() {
		return aString;
	}

	public void setaString(String aString) {
		this.aString = aString;
	}

	public TestBean3 getTestBean3() {
		return testBean3;
	}

	public void setTestBean3(TestBean3 testBean3) {
		this.testBean3 = testBean3;
	}

	public String getSecondString() {
		return secondString;
	}

	public void setSecondString(String secondString) {
		this.secondString = secondString;
	}

	public String getFirstString() {
		return firstString;
	}

	public void setFirstString(String firstString) {
		this.firstString = firstString;
	}

	public String getThirdString() {
		return thirdString;
	}

	public void setThirdString(String thirdString) {
		this.thirdString = thirdString;
	}
}
