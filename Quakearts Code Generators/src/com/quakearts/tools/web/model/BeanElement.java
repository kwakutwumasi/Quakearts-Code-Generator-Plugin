/*******************************************************************************
* Copyright (C) 2016 Kwaku Twumasi-Afriyie <kwaku.twumasi@quakearts.com>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Kwaku Twumasi-Afriyie <kwaku.twumasi@quakearts.com> - initial API and implementation
 ******************************************************************************/
package com.quakearts.tools.web.model;

import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BeanElement implements Comparable<BeanElement> {
	private String value;
	private PropertyDescriptor descriptor;
	private Map<String, String> generatorProperties = new HashMap<>();
	private boolean identity, id, nonFk;
	private int order;
	
	BeanElement(PropertyDescriptor descriptor, boolean identity, boolean id, boolean nonFk, int order) {
		this.descriptor = descriptor;
		this.value = descriptor.getName();
		this.identity = identity;
		this.id = id;
		this.order=order;
		this.nonFk = nonFk;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getElementClass() {
		return descriptor.getPropertyType().getCanonicalName();
	}

	public boolean isReadOnly() {
		return descriptor.getWriteMethod()==null;
	}
	
	public PropertyDescriptor getDescriptor() {
		return descriptor;
	}
	
	public boolean isIdentity() {
		return identity;
	}
	
	public boolean isId() {
		return id;
	}
	
	public boolean isNonFk() {
		return nonFk;
	}

	public boolean isPrimitiveNumber(){
		return isNumeric() && descriptor.getPropertyType().isPrimitive();
	}
	
	public boolean isString(){
		return descriptor.getPropertyType() == String.class
				|| descriptor.getPropertyType() == char.class
				|| descriptor.getPropertyType() == Character.class;
	}
	
	public boolean isNumeric(){
		return (descriptor.getPropertyType() == double.class
				|| descriptor.getPropertyType() == long.class
				|| descriptor.getPropertyType() == int.class
				|| descriptor.getPropertyType() == byte.class 
				|| descriptor.getPropertyType() == short.class
				|| descriptor.getPropertyType() == float.class 
				|| descriptor.getPropertyType() == Double.class
				|| descriptor.getPropertyType() == Long.class
				|| descriptor.getPropertyType() == Short.class
				|| descriptor.getPropertyType() == Float.class
				|| descriptor.getPropertyType() == Integer.class
				|| descriptor.getPropertyType() == Byte.class
				|| descriptor.getPropertyType() == java.math.BigInteger.class
				|| descriptor.getPropertyType() == java.math.BigDecimal.class);
	}
	
	public boolean isPrimitiveOrString(){
		return descriptor.getPropertyType().isPrimitive()
				|| descriptor.getPropertyType() == String.class
				|| descriptor.getPropertyType() == Boolean.class
				|| descriptor.getPropertyType() == Byte.class
				|| descriptor.getPropertyType() == Double.class
				|| descriptor.getPropertyType() == Long.class
				|| descriptor.getPropertyType() == Character.class
				|| descriptor.getPropertyType() == Short.class
				|| descriptor.getPropertyType() == Float.class
				|| descriptor.getPropertyType() == Integer.class;
	}
	
	public boolean isKnownInputType(){
		return isPrimitiveOrString() || isDateTimeType()
				|| descriptor.getPropertyType() == java.math.BigInteger.class
				|| descriptor.getPropertyType() == java.math.BigDecimal.class;
	}
	
	public boolean isDateTimeType(){
		return descriptor.getPropertyType() == java.util.Date.class
		|| descriptor.getPropertyType() == java.sql.Date.class 
		|| descriptor.getPropertyType() == java.sql.Timestamp.class;
	}
	
	public boolean isEnum(){
		return descriptor.getPropertyType().isEnum();
	}
	
	public boolean isArray(){
		return descriptor.getPropertyType().isArray();		
	}
	
	public boolean isCollection(){
		return Collection.class.isAssignableFrom(descriptor.getPropertyType());
	}
	
	public boolean isMap(){
		return Map.class.isAssignableFrom(descriptor.getPropertyType());
	}
	
	public boolean isBoolean(){
		return descriptor.getPropertyType() == Boolean.class || descriptor.getPropertyType() == boolean.class;
	}
	
	public Map<String, String> getGeneratorProperties() {
		return generatorProperties;
	}

	@Override
	public String toString() {
		return (value!=null?value:"")+":"+(getElementClass());
	}
	
	@Override
	public int compareTo(BeanElement o) {
		if(o==null)
			return 1;
		
		if(o==this)
			return 0;
		
		if(this.order!= o.order)
			return this.order - o.order;
		
		return this.value.compareTo(o.value);
	}
}
