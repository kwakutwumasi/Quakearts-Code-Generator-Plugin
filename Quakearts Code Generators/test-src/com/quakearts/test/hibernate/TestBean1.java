package com.quakearts.test.hibernate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class TestBean1 implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4106652493728675219L;
	@Id
	@GeneratedValue
	private int intid;
	private String aString;
	private int anInt;
	private short aShort;
	private long aLong;
	private byte aByte;
	private Integer anIntWrapper;
	private Long aLongWrapper;
	private Short aShortWrapper;
	private Byte aByteWrapper;
	private BigInteger aBigInteger;
	private BigDecimal aBigDecimal;
	private Date aDate;
	private TestEnum testEnum;
	@ManyToOne
	private TestBean2 testBean2;

	public int getIntid() {
		return intid;
	}

	public void setIntid(int intid) {
		this.intid = intid;
	}

	public String getaString() {
		return aString;
	}

	public void setaString(String aString) {
		this.aString = aString;
	}

	@GeneratedValue
	public int getAnInt() {
		return anInt;
	}

	public void setAnInt(int anInt) {
		this.anInt = anInt;
	}

	public short getaShort() {
		return aShort;
	}

	public void setaShort(short aShort) {
		this.aShort = aShort;
	}

	public long getaLong() {
		return aLong;
	}

	public void setaLong(long aLong) {
		this.aLong = aLong;
	}

	public byte getaByte() {
		return aByte;
	}

	public void setaByte(byte aByte) {
		this.aByte = aByte;
	}

	public Integer getAnIntWrapper() {
		return anIntWrapper;
	}

	public void setAnIntWrapper(Integer anIntWrapper) {
		this.anIntWrapper = anIntWrapper;
	}

	public Long getaLongWrapper() {
		return aLongWrapper;
	}

	public void setaLongWrapper(Long aLongWrapper) {
		this.aLongWrapper = aLongWrapper;
	}

	public Short getaShortWrapper() {
		return aShortWrapper;
	}

	public void setaShortWrapper(Short aShortWrapper) {
		this.aShortWrapper = aShortWrapper;
	}

	public Byte getaByteWrapper() {
		return aByteWrapper;
	}

	public void setaByteWrapper(Byte aByteWrapper) {
		this.aByteWrapper = aByteWrapper;
	}

	public BigInteger getaBigInteger() {
		return aBigInteger;
	}

	public void setaBigInteger(BigInteger aBigInteger) {
		this.aBigInteger = aBigInteger;
	}

	public BigDecimal getaBigDecimal() {
		return aBigDecimal;
	}

	public void setaBigDecimal(BigDecimal aBigDecimal) {
		this.aBigDecimal = aBigDecimal;
	}

	public Date getaDate() {
		return aDate;
	}

	public void setaDate(Date aDate) {
		this.aDate = aDate;
	}

	public TestEnum getTestEnum() {
		return testEnum;
	}

	public void setTestEnum(TestEnum testEnum) {
		this.testEnum = testEnum;
	}

	public TestBean2 getTestBean2() {
		return testBean2;
	}

	public void setTestBean2(TestBean2 testBean2) {
		this.testBean2 = testBean2;
	}

}
