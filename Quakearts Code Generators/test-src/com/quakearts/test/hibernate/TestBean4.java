package com.quakearts.test.hibernate;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.quakearts.test.beans.TestParent;
import com.quakearts.webapp.codegeneration.annotations.Skip;

@Entity
@Skip("stringForSkippedInheritedMethod")
public class TestBean4 extends TestParent {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3085941617673556705L;
	private int id;
	private String aString;
	@Skip
	private String skippedString;
	
	@Id
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

	public String getSkippedString() {
		return skippedString;
	}

	public void setSkippedString(String skippedString) {
		this.skippedString = skippedString;
	}

	@Skip
	public String getStringForSkippedGet() {
		return "Skipped";
	}
	
	public void setStringForSkippedGet(String stringForSkippedGet) {
	}
	
}
