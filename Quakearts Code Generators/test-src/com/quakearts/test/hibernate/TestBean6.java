package com.quakearts.test.hibernate;

import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

public class TestBean6 implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1216323117932782424L;
	@Id
	private int id;
	@Id
	private String aString;

	@ManyToOne
	@JoinColumn(insertable = false, updatable = false, name = "id")
	private TestBean4 testBean4;
	@ManyToOne
	@JoinColumn(insertable = false, updatable = false, name = "aString")
	private TestBean5 testBean5;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getaString() {
		return aString;
	}

	public void setaString(String aString) {
		this.aString = aString;
	}

	public TestBean4 getTestBean4() {
		return testBean4;
	}

	public void setTestBean4(TestBean4 testBean4) {
		this.testBean4 = testBean4;
		if(testBean4 != null)
			id = testBean4.getId();
	}

	public TestBean5 getTestBean5() {
		return testBean5;
	}

	public void setTestBean5(TestBean5 testBean5) {
		this.testBean5 = testBean5;
		if(testBean5 != null)
			aString = testBean5.getaString();
	}

}
