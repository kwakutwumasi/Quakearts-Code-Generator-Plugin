package com.quakearts.webtools.codegenerators.model;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import org.eclipse.core.resources.IProject;
import org.xml.sax.SAXException;
import com.quakearts.tools.CodeGenerators;
import com.quakearts.webapp.annotations.CodeGeneratorProperties;
import com.quakearts.webapp.annotations.CodeGeneratorProperty;
import com.quakearts.webtools.codegenerators.wizard.saxhandler.HibernateMappingHandler;

public class BeanModel {

	private String name;
	private List<BeanElement> beanElements = new ArrayList<BeanElement>();
	private Class<?> beanClass;
	private BeanInfo beanInfo;
	private String renderedText;
	private Map<String, Object> generatorProperties = new HashMap<>();

	public BeanModel(String fqn, IProject project) throws ClassNotFoundException, IntrospectionException {
		ClassLoader loader;
		if(project==null){
			loader = Thread.currentThread().getContextClassLoader();
		} else {
			loader = CodeGenerators.getProjectClassLoader(project);
		}
		
		this.beanClass = loader.loadClass(fqn);
		beanInfo = Introspector.getBeanInfo(beanClass);
		
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
					field = beanClass.getDeclaredField(descriptor.getName());
				} catch (NoSuchFieldException e) {
				} catch (SecurityException e) {
				}
				
				HashMap<String, String> fieldProperties = new HashMap<>();
				
				if(field!=null){
					if(!isId){
						Id id = field.getAnnotation(Id.class);
						isId = id !=null;
						GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
						isIdentity = generatedValue != null;
					}
					
					CodeGeneratorProperty property = field.getAnnotation(CodeGeneratorProperty.class);
					if(property!=null ){
						if(property.key().equals("identity") && !isId){
							isId = Boolean.parseBoolean(property.property());
							isIdentity = isId;
						} else if(property.key().equals("id") && !isId){
							isId = Boolean.parseBoolean(property.property());
						}
						
						if(!property.key().equals("identity") && property.key().equals("id")) {
							fieldProperties.put(property.key(), property.property());
						}
					}
					
					CodeGeneratorProperties properties = field.getAnnotation(CodeGeneratorProperties.class);
					if(properties!=null){
						for(CodeGeneratorProperty prop:properties.value()){
							if(prop.key().equals("identity") && !isId){
								isId = Boolean.parseBoolean(prop.property());
								isIdentity = isId;
							} else if(property.key().equals("id") && !isId){
								isId = Boolean.parseBoolean(prop.property());
							}
							
							if(!prop.key().equals("identity") && prop.key().equals("id")) {
								fieldProperties.put(prop.key(), prop.property());
							}
						}
					}
				}
				
				BeanElement beanElement = new BeanElement(descriptor, isIdentity, isId);
				if(fieldProperties.size()>0)
					beanElement.getGeneratorProperties().putAll(fieldProperties);

				if(isId)
					generatorProperties.put("model.id", beanElement);
				
				beanElements.add(beanElement);
			}
		}
		
		ManagedBean bean = beanClass.getAnnotation(ManagedBean.class);
		if(bean!=null){
			this.name = bean.name();
		} else {
			this.name = beanClass.getSimpleName().toLowerCase();
		}
		
		CodeGeneratorProperty property = beanClass.getAnnotation(CodeGeneratorProperty.class);
		if(property!=null ){
			generatorProperties.put(property.key(), property.property());
		}
		
		CodeGeneratorProperties properties = beanClass.getAnnotation(CodeGeneratorProperties.class);
		if(properties!=null){
			for(CodeGeneratorProperty prop:properties.value()){
				generatorProperties.put(prop.key(), prop.property());
			}
		}
		
		if(this.name==null)
			this.name = beanClass.getSimpleName().toLowerCase();
		else
			this.name = this.name.toLowerCase();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if(name!=null)
			this.name = name.toLowerCase();
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
