package com.quakearts.webtools.codegenerators.model;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import com.quakearts.tools.CodeGenerators;

public class BeanModelFactory {
	private BeanModelFactory() {
	}
	
	public static BeanModel createBeanModel(String fqn, IProject project)
			throws ClassNotFoundException, IntrospectionException {
		final ClassLoader loader;
		if(project==null){
			loader = Thread.currentThread().getContextClassLoader();
		} else {
			loader = CodeGenerators.getProjectClassLoader(project);
		}
		
		Class<?> beanClass = loader.loadClass(fqn);
		BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
		List<BeanElement> beanElements = new ArrayList<BeanElement>();
		Map<String, Object> generatorProperties = new HashMap<>();
		IdElement idElement = null;
		String name="";
		
		Class<? extends Annotation> propertyClass = checkForAnnotation("com.quakearts.webapp.codegeneration.annotations.CodeGeneratorProperty", loader);
		Class<? extends Annotation> propertiesClass = checkForAnnotation("com.quakearts.webapp.codegeneration.annotations.CodeGeneratorProperties", loader);
		Class<? extends Annotation> orderClass = checkForAnnotation("com.quakearts.webapp.codegeneration.annotations.Order", loader);
		Class<? extends Annotation> skipClass = checkForAnnotation("com.quakearts.webapp.codegeneration.annotations.Skip", loader);
		Class<? extends Annotation> idClass =  checkForAnnotation("javax.persistence.Id", loader);
		Class<? extends Annotation> generatedValueClass = checkForAnnotation("javax.persistence.GeneratedValue", loader);
		Class<? extends Annotation> oneToOneClass = checkForAnnotation("javax.persistence.OneToOne", loader);
				
		for(PropertyDescriptor descriptor:beanInfo.getPropertyDescriptors()){
			if(!descriptor.getName().equals("class")){
				boolean isIdentity = false;
				boolean isId = false;

				Field field=null;
				try {
					field = getField(beanClass, descriptor.getName());
				} catch (SecurityException e) {
				}
				
				HashMap<String, String> fieldProperties = new HashMap<>();
				
				int order = 99;
				boolean nonFk=false;
				if(field!=null && skipClass!=null && field.isAnnotationPresent(skipClass))
					continue;
				else if(skipClass!=null && descriptor.getReadMethod().isAnnotationPresent(skipClass))
					continue;
				
				if(!isId){
					if(field!=null && idClass!=null)
						try {
							isId = field.isAnnotationPresent(idClass);
							isIdentity = field.isAnnotationPresent(generatedValueClass);
						} catch (Exception e) {
							CodeGenerators.logError("Exception of type " + e.getClass().getName() + " was thrown. Message is "
									+ e.getMessage() + ". Exception occured whiles checking identity specification", e);
						}
				}
				
				if(propertyClass != null){
					Annotation property = descriptor.getReadMethod().getAnnotation(propertyClass);
					if(field!=null && property == null)
						property = field.getAnnotation(propertyClass);
					
					if(property != null){
						Object key = getAnnotationProperty(propertyClass, property, "key");
						Object value = getAnnotationProperty(propertyClass, property, "property");
						if(key !=null && value!=null){
							if(key.equals("identity") && !isId){
								isId = Boolean.parseBoolean(value.toString());
								isIdentity = isId;
							} else if(key.equals("id") && !isId){
								isId = Boolean.parseBoolean(value.toString());
							}
							
							if(!key.equals("identity") && !key.equals("id")) {
								fieldProperties.put(key.toString(), value.toString());
							}
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
								if(key.equals("identity") && !isId){
									isId = Boolean.parseBoolean(keyValue.toString());
									isIdentity = isId;
								} else if(key.equals("id") && !isId){
									isId = Boolean.parseBoolean(keyValue.toString());
								}
								
								if(!key.equals("identity") && !key.equals("id")) {
									fieldProperties.put(key.toString(), keyValue.toString());
								}
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
		
		Class<? extends Annotation> managedBeanClass = checkForAnnotation("javax.faces.bean.ManagedBean", loader);
		if(managedBeanClass!=null)
			try {
				Annotation bean = beanClass.getAnnotation(managedBeanClass);
				if(bean!=null){
					Object value = getAnnotationProperty(managedBeanClass, bean, "name");
					name = value!=null?value.toString():beanClass.getSimpleName();
				} else {
					name = beanClass.getSimpleName();
				}	
			} catch (Exception e) {
				CodeGenerators.logError("Exception of type " + e.getClass().getName() + " was thrown. Message is " + e.getMessage()
						+ ". Exception occured whiles checking managed bean name: for "+beanClass.getName(),null);
				name = beanClass.getSimpleName();
			}
		
		if(propertiesClass!=null && propertyClass!=null) {
			Annotation property = beanClass.getAnnotation(propertyClass);
			if(property!=null ){
				Object key = getAnnotationProperty(propertyClass, property, "key");
				Object value = getAnnotationProperty(propertyClass, property, "property");
				if(key !=null && value!=null){
					generatorProperties.put(key.toString(), value.toString());
				}
			}
			
			Annotation properties = beanClass.getAnnotation(propertiesClass);
			if(properties!=null){
				Object value = getAnnotationProperty(propertiesClass, properties, "value");
				if(value instanceof Annotation[])
					for(Annotation cproperty:(Annotation[])value){
						Object key = getAnnotationProperty(propertyClass, cproperty, "key");
						Object keyValue = getAnnotationProperty(propertyClass, cproperty, "property");
						if(key !=null && keyValue!=null)
							generatorProperties.put(key.toString(), keyValue.toString());
					}
			}		
		}
		Collections.sort(beanElements);
		
		return new BeanModel(name, idElement, beanElements, beanClass, beanInfo, generatorProperties);
	}
	

	private static Field getField(Class<?> clazz, String name) throws SecurityException {
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
	private static Class<? extends Annotation> checkForAnnotation(String className, ClassLoader loader){
		Class<? extends Annotation> annotationClass;
		try {
			annotationClass = (Class<? extends Annotation>) loader.loadClass(className);
			return annotationClass;
		} catch (Exception e) {
			return null;
		}
	}
	
	private static Object getAnnotationProperty(Class<?> targetClass, Annotation annotation, String property){
		try {
			Method method = targetClass.getMethod(property);
			Object value = method.invoke(annotation);
			return value;
		} catch (Exception e) {
			return null;
		}
	}

}
