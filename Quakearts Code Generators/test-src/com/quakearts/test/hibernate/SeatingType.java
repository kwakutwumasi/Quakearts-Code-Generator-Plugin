package com.quakearts.test.hibernate;
// Generated Sep 30, 2015 12:15:28 PM by Hibernate Tools 3.4.0.CR1

import java.util.HashSet;
import java.util.Set;

/**
 * SeatingType generated by hbm2java
 */
public class SeatingType implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5483472786501658271L;
	private long id;
	private String description;
	private boolean valid;
	private Set<Seating> seatings = new HashSet<Seating>(0);

	public SeatingType() {
	}

	public SeatingType(boolean valid) {
		this.valid = valid;
	}

	public SeatingType(String description, boolean valid, Set<Seating> seatings) {
		this.description = description;
		this.valid = valid;
		this.seatings = seatings;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isValid() {
		return this.valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public Set<Seating> getSeatings() {
		return this.seatings;
	}

	public void setSeatings(Set<Seating> seatings) {
		this.seatings = seatings;
	}

}
