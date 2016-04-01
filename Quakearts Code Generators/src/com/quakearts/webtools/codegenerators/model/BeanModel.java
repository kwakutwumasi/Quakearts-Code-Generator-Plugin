package com.quakearts.webtools.codegenerators.model;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import org.eclipse.core.resources.IProject;
import org.xml.sax.SAXException;
import com.quakearts.tools.CodeGenerators;
import com.quakearts.webtools.codegenerators.wizard.saxhandler.HibernateMappingHandler;

public class BeanModel {

	private String name;
	private List<BeanElement> beanElements = new ArrayList<BeanElement>();
	private Class<?> beanClass;
	private BeanInfo beanInfo;
	private String renderedText;
	private Map<String, Object> generatorProperties = new HashMap<>();

	public BeanModel(String fqn, IProject project) throws ClassNotFoundException, IntrospectionException {
		final ClassLoader loader;
		if(project==null){
			loader = Thread.currentThread().getContextClassLoader();
		} else {
			loader = CodeGenerators.getProjectClassLoader(project);
		}
		
		this.beanClass = loader.loadClass(fqn);
		beanInfo = Introspector.getBeanInfo(beanClass);
		Class<? extends Annotation> propertyClass = checkForAnnotation("com.quakearts.webapp.codegeneration.annotations.CodeGeneratorProperty", loader);
		Class<? extends Annotation> propertiesClass = checkForAnnotation("com.quakearts.webapp.codegeneration.annotations.CodeGeneratorProperties", loader);
		Class<? extends Annotation> orderClass = checkForAnnotation("com.quakearts.webapp.codegeneration.annotations.Order", loader);
		Class<? extends Annotation> skipClass = checkForAnnotation("com.quakearts.webapp.codegeneration.annotations.Skip", loader);
		Class<? extends Annotation> idClass =  checkForAnnotation("javax.persistence.Id", loader);
		Class<? extends Annotation> generatedValueClass = checkForAnnotation("javax.persistence.GeneratedValue", loader);
		Class<? extends Annotation> oneToOneClass = checkForAnnotation("javax.persistence.OneToOne", loader);
		
		HibernateMappingHandler handler = new HibernateMappingHandler();
		if(!beanClass.isPrimitive() && !beanClass.isArray()){
			String hbmFile = beanClass.getName().replace(".", "/")+".hbm.xml";
			InputStream is = loader.getResourceAsStream(hbmFile);
			try {
				if(is != null){
					SAXParser parser = CodeGenerators.getFactory().newSAXParser();
					parser.parse(is, handler);
				}
			} catch (ParserConfigurationException | SAXException | IOException e) {
				CodeGenerators.logError("Error parsing hbm.xml file for "+beanClass.getName(), e);
			} finally {
				try {
					is.close();
				} catch (Exception e) {
				}
			}
		}
		
		for(PropertyDescriptor descriptor:beanInfo.getPropertyDescriptors()){
			if(!descriptor.getName().equals("class")){
				boolean isIdentity = false;
				boolean isId = false;

				if(handler.getFieldName()!=null && handler.getFieldName().equals(descriptor.getName())){
					isId=true;
					isIdentity = handler.isIdentity();
				}
				
				Field field=null;
				try {
					field = getField(beanClass, descriptor.getName());
				} catch (SecurityException e) {
				}
				
				HashMap<String, String> fieldProperties = new HashMap<>();
				
				int order = 0;
				boolean nonFk=false;
				if(field!=null){
					if(skipClass!=null && (field.getAnnotation(skipClass)!=null))
						continue;
					
					if(!isId){
						if(idClass!=null)
							try {
								Annotation id = field.getAnnotation(idClass);
								isId = id !=null;
								Annotation generatedValue = field.getAnnotation(generatedValueClass);
								isIdentity = generatedValue != null;
							} catch (Exception e) {
								CodeGenerators.logError("Exception of type " + e.getClass().getName() + " was thrown. Message is "
										+ e.getMessage() + ". Exception occured whiles checking identity specification", e);
							}
					}
					
					if(propertyClass!=null){
						Annotation property = field.getAnnotation(propertyClass);
						if(property!=null ){
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
						Annotation properties = field.getAnnotation(propertiesClass);
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
						Annotation orderAnno = field.getAnnotation(orderClass);
						Object value = getAnnotationProperty(orderClass, orderAnno, "value");
						if(value instanceof Integer){
							order = ((Integer)value);
						}
					}
					
					if(oneToOneClass!=null){
						Annotation oneToOneAnno = field.getAnnotation(oneToOneClass);
						if(oneToOneAnno!=null){
							Object value = getAnnotationProperty(oneToOneClass, oneToOneAnno, "mappedBy");
							nonFk = value!=null && !value.toString().trim().isEmpty();
						}
					}
				}
				
				BeanElement beanElement = new BeanElement(descriptor, isIdentity, isId, nonFk, order);
				if(fieldProperties.size()>0)
					beanElement.getGeneratorProperties().putAll(fieldProperties);

				if(isId)
					generatorProperties.put("model.id", beanElement);
				
				beanElements.add(beanElement);
			}
		}
		
		Class<? extends Annotation> managedBeanClass = checkForAnnotation("javax.faces.bean.ManagedBean", loader);
		if(managedBeanClass!=null)
		try {
			Annotation bean = beanClass.getAnnotation(managedBeanClass);
			String name;
			if(bean!=null){
				Object value = getAnnotationProperty(managedBeanClass, bean, "name");
				name = value!=null?value.toString():beanClass.getSimpleName();
			} else {
				name = beanClass.getSimpleName();
			}	
			setName(name);
		} catch (Exception e) {
			CodeGenerators.logError("Exception of type " + e.getClass().getName() + " was thrown. Message is " + e.getMessage()
					+ ". Exception occured whiles checking managed bean name: for "+beanClass.getName(),null);
			setName(beanClass.getSimpleName());
		}
		
		if(propertiesClass!=null && propertyClass!=null){
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
	}

	private Field getField(Class<?> clazz, String name) throws SecurityException {
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
	private Class<? extends Annotation> checkForAnnotation(String className, ClassLoader loader){
		Class<? extends Annotation> annotationClass;
		try {
			annotationClass = (Class<? extends Annotation>) loader.loadClass(className);
			return annotationClass;
		} catch (Exception e) {
			return null;
		}
	}
	
	private Object getAnnotationProperty(Class<?> targetClass, Annotation annotation, String property){
		try {
			Method method = targetClass.getMethod(property);
			Object value = method.invoke(annotation);
			return value;
		} catch (Exception e) {
			return null;
		}
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		if(name!=null && name.length()>1)
			this.name = new StringBuilder(name.substring(0, 1).toLowerCase())
				.append(name.substring(1)).toString();
	}

	public Collection<BeanElement> getBeanElements() {
		return Collections.unmodifiableCollection(beanElements);
	}

	public Class<?> getBeanClass() {
		return beanClass;
	}
	
	public String getBeanClassName(){
		return beanClass.getCanonicalName();
	}
	
	public BeanInfo getBeanInfo() {
		return beanInfo;
	}
	
	public Map<String, Object> getGeneratorProperties() {
		return generatorProperties;
	}
	
	public String getRenderedText() {
		return renderedText;
	}
	
	public void setRenderedText(String renderedText) {
		this.renderedText = renderedText;
	}
}
