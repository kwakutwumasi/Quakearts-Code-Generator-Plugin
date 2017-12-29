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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanModelBuilder {
	private BeanModelBuilder() {
	}
	
	public static BeanModel createBeanModel(String fqn, final ClassLoader loader)
			throws ClassNotFoundException, IntrospectionException {		
		Class<?> beanClass = loader.loadClass(fqn);
		BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
		List<BeanElement> beanElements = new ArrayList<BeanElement>();
		Map<String, Object> generatorProperties = new HashMap<>();
		IdElement idElement = null;
		String name = beanClass.getSimpleName();
		
		Class<? extends Annotation> propertyClass = checkForAnnotation("com.quakearts.webapp.codegeneration.annotations.CodeGeneratorProperty", loader);
		Class<? extends Annotation> propertiesClass = checkForAnnotation("com.quakearts.webapp.codegeneration.annotations.CodeGeneratorProperties", loader);
		Class<? extends Annotation> orderClass = checkForAnnotation("com.quakearts.webapp.codegeneration.annotations.Order", loader);
		Class<? extends Annotation> skipClass = checkForAnnotation("com.quakearts.webapp.codegeneration.annotations.Skip", loader);
		Class<? extends Annotation> idClass =  checkForAnnotation("javax.persistence.Id", loader);
		Class<? extends Annotation> generatedValueClass = checkForAnnotation("javax.persistence.GeneratedValue", loader);
		Class<? extends Annotation> oneToOneClass = checkForAnnotation("javax.persistence.OneToOne", loader);
		
		List<String> skipElementList = new ArrayList<>();
		if(skipClass!=null) {
			Annotation skip = beanClass.getAnnotation(skipClass);
			if(skip!=null) {
				String[] skipElements = (String[]) getAnnotationProperty(skipClass, skip, "value");
				if(skipElements.length!=0) {
					skipElementList = Arrays.asList(skipElements);
				}
			}
		}
		
		Class<? extends Annotation> facesManagedBeanClass = checkForAnnotation("javax.faces.bean.ManagedBean", loader);
		if(facesManagedBeanClass!=null) {
			Annotation managedBean = beanClass.getAnnotation(facesManagedBeanClass);
			if(managedBean!=null){
				Object value = getAnnotationProperty(facesManagedBeanClass, managedBean, "name");
				if(value != null)
					name = value.toString();
			}	
		}

		Class<? extends Annotation> cdiManagedBeanClass = checkForAnnotation("javax.annotation.ManagedBean", loader);
		if(cdiManagedBeanClass!=null) {
			Annotation managedBean = beanClass.getAnnotation(cdiManagedBeanClass);
			if(managedBean!=null){
				Object value = getAnnotationProperty(cdiManagedBeanClass, managedBean, "value");
				if(value != null)
					name = value.toString();
			}	
		}

		List<String> ids = new ArrayList<>();
		if(propertiesClass!=null && propertyClass!=null) {
			Annotation property = beanClass.getAnnotation(propertyClass);
			if(property!=null ){
				processPropertyOnClass(generatorProperties, propertyClass, property, ids);
			}
			
			Annotation properties = beanClass.getAnnotation(propertiesClass);
			if(properties!=null){
				Object value = getAnnotationProperty(propertiesClass, properties, "value");
				if(value instanceof Annotation[])
					for(Annotation cproperty:(Annotation[])value){
						processPropertyOnClass(generatorProperties, propertyClass, cproperty, ids);
					}
			}		
		}
		
		for(PropertyDescriptor descriptor:beanInfo.getPropertyDescriptors()){
			if(skipElementList.contains(descriptor.getName()))
				continue;
			
			if(!descriptor.getName().equals("class")){
				boolean isIdentity = false;
				boolean isId = false;

				Field field=null;
				try {
					field = getField(beanClass, descriptor.getName());
				} catch (SecurityException e) {
				}
				
				if(descriptor.getReadMethod()==null)
					continue;
				
				HashMap<String, String> fieldProperties = new HashMap<>();
				
				int order = 99;
				boolean nonFk=false;
				if(skipClass!=null) {
					if(field!=null && field.isAnnotationPresent(skipClass))
						continue;
					else if(descriptor.getReadMethod().isAnnotationPresent(skipClass))
						continue;					
				}
				
				if(ids.contains(descriptor.getName())) {
					isId = true;
				}
				
				if(field != null) { 
					if(!isId && idClass != null)
						isId = field.isAnnotationPresent(idClass);
					if(generatedValueClass!=null)
						isIdentity = field.isAnnotationPresent(generatedValueClass);
				}
				
				if(!isId && idClass != null)
					isId = descriptor.getReadMethod().isAnnotationPresent(idClass);
					
				if(!isIdentity && generatedValueClass!=null)
					isIdentity = descriptor.getReadMethod().isAnnotationPresent(generatedValueClass);
				
				if(propertyClass != null){
					Annotation property = descriptor.getReadMethod().getAnnotation(propertyClass);
					if(property == null && field!=null )
						property = field.getAnnotation(propertyClass);
					
					if(property != null){
						Object key = getAnnotationProperty(propertyClass, property, "key");
						Object value = getAnnotationProperty(propertyClass, property, "property");
						if(key !=null && value!=null){
							fieldProperties.put(key.toString(), value.toString());
						}
					}
				}
				
				if(propertiesClass!=null && propertyClass!=null){
					Annotation properties = descriptor.getReadMethod().getAnnotation(propertiesClass);
					if(field!=null && properties == null)
						properties = field.getAnnotation(propertiesClass);
					
					if(properties!=null){
						Object value = getAnnotationProperty(propertiesClass, properties, "value");
						if(value instanceof Annotation[])
						for(Annotation property:(Annotation[])value){
							Object key = getAnnotationProperty(propertyClass, property, "key");
							Object keyValue = getAnnotationProperty(propertyClass, property, "property");
							if(key !=null && value!=null){
								fieldProperties.put(key.toString(), keyValue.toString());
							}
						}
					}
				}
				
				if(orderClass!=null){
					Annotation orderAnno = descriptor.getReadMethod().getAnnotation(orderClass);
					if(field!=null && orderAnno == null)
						orderAnno = field.getAnnotation(orderClass);
					
					Object value = getAnnotationProperty(orderClass, orderAnno, "value");
					if(value instanceof Integer){
						order = ((Integer)value);
					}
				}
				
				if(oneToOneClass!=null){
					Annotation oneToOneAnno = descriptor.getReadMethod().getAnnotation(oneToOneClass);
					if(field!=null && oneToOneAnno == null)
						oneToOneAnno = field.getAnnotation(oneToOneClass);
						
					if(oneToOneAnno!=null){
						Object value = getAnnotationProperty(oneToOneClass, oneToOneAnno, "mappedBy");
						nonFk = value!=null && !value.toString().trim().isEmpty();
					}
				}
				
				BeanElement beanElement = new BeanElement(descriptor, isIdentity, isId, nonFk, order);
				if(fieldProperties.size()>0)
					beanElement.getGeneratorProperties().putAll(fieldProperties);

				if(isId)
					if(idElement == null)
						idElement = new IdElement(beanElement);
					else
						idElement.getIds().add(beanElement);
				
				beanElements.add(beanElement);
			}
		}
		Collections.sort(beanElements);
		
		return new BeanModel(name, idElement, beanElements, beanClass, beanInfo, generatorProperties);
	}

	public static void processPropertyOnClass(Map<String, Object> generatorProperties,
			Class<? extends Annotation> propertyClass, Annotation property, List<String> ids) {
		Object key = getAnnotationProperty(propertyClass, property, "key");
		Object value = getAnnotationProperty(propertyClass, property, "property");
		if(key !=null && value!=null){
			if(key.equals("id")) {
				ids.addAll(Arrays.asList(value.toString().split(",")));
			} else {
				generatorProperties.put(key.toString(), value.toString());
			}
		}
	}
	

	public static Field getField(Class<?> clazz, String name) throws SecurityException {
		Field field;
		try {
			field = clazz.getDeclaredField(name);
		} catch (NoSuchFieldException e) {
			if(clazz.getSuperclass()!=Object.class){
				return getField(clazz.getSuperclass(), name);
			} else
				return null;
		}
		
		return field;
	}
	
	@SuppressWarnings("unchecked")
	public static Class<? extends Annotation> checkForAnnotation(String className, ClassLoader loader){
		Class<? extends Annotation> annotationClass;
			try {
				annotationClass = (Class<? extends Annotation>) loader.loadClass(className);
				return annotationClass;
			} catch (ClassNotFoundException e) {
				return null;
			}
	}
	
	public static Object getAnnotationProperty(Class<?> targetClass, Annotation annotation, String property){
		try {
			Method method = targetClass.getMethod(property);
			Object value = method.invoke(annotation);
			return value;
		} catch (Exception e) {
			return null;
		}
	}

}
