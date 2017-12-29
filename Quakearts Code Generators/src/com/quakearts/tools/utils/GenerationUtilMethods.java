package com.quakearts.tools.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.quakearts.tools.web.model.BeanElement;
import com.quakearts.tools.web.model.BeanModel;

public class GenerationUtilMethods {
	public String camelCase(String javaName) {
		String name = javaName;
		if(name.startsWith("_"))
			name = name.substring(1);
		
		if(name.length()>1 && Character.isLowerCase(name.charAt(1)))
			return name.substring(0,1).toUpperCase()+name.substring(1);
		else
			return name;
	}
	
	public String reverseCamelCase(String javaName) {
		return javaName.substring(0,1).toLowerCase()+javaName.substring(1);
	}
	
	public String splitCamelCase(String string){
		StringBuilder builder = new StringBuilder();
		char[] chars = string.toCharArray();
		boolean start=true;
		for(char c:chars){
			if(start){
				builder.append(Character.toUpperCase(c));
				start=false;
			}else{
				if(Character.isUpperCase(c))
					builder.append(" ");
				builder.append(c);
			}
		}
		
		return builder.toString();
	}
	
	public boolean verifyMethod(BeanModel model, String methodName){
		for(Method method:model.getBeanClass().getMethods()){
			if(method.getName().equals(methodName))
				return true;
		}
		return false;
	}
	
	public boolean verifyAnnotation(BeanModel model, String annotationName){
		for(Annotation annotation:model.getBeanClass().getAnnotations()){
			if(annotation.annotationType().getCanonicalName().equals(annotationName))
				return true;
		}
		return false;
	}
	
	public boolean verifyChildOf(BeanModel model, String parentName){
		Class<?> parent = model.getBeanClass().getSuperclass();
		int count =0;
		do {
			if(parent.getCanonicalName().equals(parentName))
				return true;
			count++;
		} while((parent = parent.getSuperclass())!=null && count<100);
		return false;
	}
	
	public Collection<BeanElement> findElementsOfType(BeanModel beanModel, String types){
		ArrayList<BeanElement> foundElements = new ArrayList<>();

		for(BeanElement element:beanModel.getBeanElements()){
			if(types.contains(element.getElementClass())){
				foundElements.add(element);
			}
		}
		
		return foundElements;
	}
	
	public Collection<String> getEnumImports(List<BeanModel> beanModels){
		ArrayList<String> imports = new ArrayList<>();
		for(BeanModel beanModel:beanModels){
			for(BeanElement beanElement:beanModel.getBeanElements()){
				if(beanElement.getDescriptor().getPropertyType().isEnum() 
					&& !imports.contains(beanElement.getDescriptor().getPropertyType().getCanonicalName())){
					imports.add(beanElement.getDescriptor().getPropertyType().getCanonicalName());
				}
			}
		}
		
		return imports;
	}
	
	public Collection<String> getEnums(List<BeanModel> beanModels){
		ArrayList<String> enumNames = new ArrayList<>();
		for(BeanModel beanModel:beanModels){
			for(BeanElement beanElement:beanModel.getBeanElements()){
				if(beanElement.getDescriptor().getPropertyType().isEnum() 
					&& !enumNames.contains(beanElement.getDescriptor().getPropertyType().getSimpleName())){
					enumNames.add(beanElement.getDescriptor().getPropertyType().getSimpleName());
				}
			}
		}
		return enumNames;
	}
}
