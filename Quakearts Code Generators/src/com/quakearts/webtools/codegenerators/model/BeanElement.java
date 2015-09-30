package com.quakearts.webtools.codegenerators.model;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

public class BeanElement {
	private String value;
	private String renderedText;
	private PropertyDescriptor descriptor;
	private Map<String, String> generatorProperties = new HashMap<>();
	private boolean identity, id;
	
	public BeanElement(PropertyDescriptor descriptor, boolean identity, boolean id) {
		this.descriptor = descriptor;
		this.value = descriptor.getName();
		this.identity = identity;
		this.id = id;
	}
	
	public String getRenderedText() {
		return renderedText;
	}
	
	public void setRenderedText(String renderedText) {
		this.renderedText = renderedText;
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
	
	public boolean isString(){
		return descriptor.getPropertyType().getName().equals("java.lang.String")
				|| descriptor.getPropertyType().getName().equals("char")
				|| descriptor.getPropertyType().getName().equals("java.lang.Character");
	}
	
	public boolean isNumeric(){
		return (descriptor.getPropertyType().getName().equals("double")
				|| descriptor.getPropertyType().getName().equals("long")
				|| descriptor.getPropertyType().getName().equals("int")
				|| descriptor.getPropertyType().getName().equals("byte") 
				|| descriptor.getPropertyType().getName().equals("short")
				|| descriptor.getPropertyType().getName().equals("float") 
				|| descriptor.getPropertyType().getName().equals("java.lang.Double")
				|| descriptor.getPropertyType().getName().equals("java.lang.Long")
				|| descriptor.getPropertyType().getName().equals("java.lang.Short")
				|| descriptor.getPropertyType().getName().equals("java.lang.Float")
				|| descriptor.getPropertyType().getName().equals("java.lang.Integer")
				|| descriptor.getPropertyType().getName().equals("java.lang.Byte"));
	}
	
	public boolean isPrimitiveOrString(){
		return descriptor.getPropertyType().isPrimitive()
				|| descriptor.getPropertyType().getName().equals("java.lang.String")
				|| descriptor.getPropertyType().getName().equals("java.lang.Boolean")
				|| descriptor.getPropertyType().getName().equals("java.lang.Byte")
				|| descriptor.getPropertyType().getName().equals("java.lang.Double")
				|| descriptor.getPropertyType().getName().equals("java.lang.Long")
				|| descriptor.getPropertyType().getName().equals("java.lang.Character")
				|| descriptor.getPropertyType().getName().equals("java.lang.Short")
				|| descriptor.getPropertyType().getName().equals("java.lang.Float")
				|| descriptor.getPropertyType().getName().equals("java.lang.Integer");
	}
	
	public boolean isKnownInputType(){
		return isPrimitiveOrString() || isDateTimeType();
	}
	
	public boolean isDateTimeType(){
		return descriptor.getPropertyType().getName().equals("java.util.Date")
		|| descriptor.getPropertyType().getName().equals("java.sql.Date") 
		|| descriptor.getPropertyType().getName().equals("java.sql.Timestamp");
	}
	
	public boolean isCollection(){
		return hasInterface("java.util.Collection");
	}
	
	public boolean isMap(){
		return hasInterface("java.util.Map");
	}

	public boolean hasInterface(String interfaceName){
		Class<?> elementClass = descriptor.getPropertyType();
		if(elementClass.isInterface())
			return hasInterface(interfaceName, elementClass);
		
		if(elementClass.getInterfaces().length>0){
			for(Class<?> interfase:elementClass.getInterfaces()){
				if(hasInterface(interfaceName, interfase))
					return true;
			}
		}
		
		Class<?> parent =elementClass.getSuperclass();
		if(parent!=null)
			do {
				if(parent.getInterfaces().length>0){
					for(Class<?> interfase:parent.getInterfaces()){
						if(hasInterface(interfaceName, interfase))
							return true;
					}
				}
				
				parent = parent.getSuperclass();
			} while (parent!=null);
		return false;
	}
	
	private boolean hasInterface(String interfase, Class<?> clazz){
		if(clazz.getInterfaces().length==0)
			return false;
		
		for(Class<?> ifaceClass:clazz.getInterfaces()){
			if(ifaceClass.getName().equals(interfase))
				return true;
			if(hasInterface(interfase, ifaceClass))
				return true;
		}
		
		return false;
	}
	
	public boolean isBoolean(){
		return descriptor.getPropertyType().getName().equals("java.lang.Boolean") || descriptor.getPropertyType().getName().equals("boolean");
	}
	
	public Map<String, String> getGeneratorProperties() {
		return generatorProperties;
	}
}
