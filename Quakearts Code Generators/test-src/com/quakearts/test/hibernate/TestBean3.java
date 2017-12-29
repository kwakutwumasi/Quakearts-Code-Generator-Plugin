package com.quakearts.test.hibernate;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.quakearts.webapp.codegeneration.annotations.CodeGeneratorProperties;
import com.quakearts.webapp.codegeneration.annotations.CodeGeneratorProperty;

@Entity
@CodeGeneratorProperties({
	@CodeGeneratorProperty(key="testClassKey1",property="testCassProperty1"),
	@CodeGeneratorProperty(key="testClassKey2",property="testCassProperty2")
})
public class TestBean3 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2165482054340952213L;
	@Id
	private int id1;
	@ManyToOne
	@CodeGeneratorProperty(key="testFieldKey",property="testFieldProperty")
	private TestBean4 testBean4;
	@OneToOne
	private TestBean5 testBean5;

	public int getId1() {
		return id1;
	}

	public void setId1(int id1) {
		this.id1 = id1;
	}

	public TestBean4 getTestBean4() {
		return testBean4;
	}

	public void setTestBean4(TestBean4 testBean4) {
		this.testBean4 = testBean4;
	}

	public TestBean5 getTestBean5() {
		return testBean5;
	}

	public void setTestBean5(TestBean5 testBean5) {
		this.testBean5 = testBean5;
	}

}
