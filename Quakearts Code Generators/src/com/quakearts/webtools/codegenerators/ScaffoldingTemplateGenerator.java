package com.quakearts.webtools.codegenerators;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.apache.velocity.VelocityContext;
import com.quakearts.tools.CodeGenerators;
import com.quakearts.tools.generatorbase.GenericGenerator;
import com.quakearts.webtools.codegenerators.model.BeanElement;
import com.quakearts.webtools.codegenerators.model.BeanModel;

public class ScaffoldingTemplateGenerator extends GenericGenerator {

	public ScaffoldingTemplateGenerator() {
		getEngine();
	}
	
	public InputStream generatePage(VelocityContext context, String templateName){
		context.put("util", this);
		StringWriter writer = new StringWriter();
		org.apache.velocity.Template template = getEngine().getTemplate(templateName);
		if(template!=null){
			try {
				template.merge(context, writer);				
			} catch (Throwable e) {
				CodeGenerators.logError("Exception of type " + e.getClass().getName() + " was thrown. Message is " + e.getMessage()
						+ ". Exception occured whiles generating "+templateName, e);
			}
		}
		return new ByteArrayInputStream(writer.toString().getBytes());
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
			if(annotation.annotationType().getCanonicalName().equals(annotation))
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
	
	public Collection<BeanElement> findElementsOfType(String types, BeanModel beanModel){
		ArrayList<BeanElement> foundElements = new ArrayList<>();
		HashMap<String, String> typeMap = new HashMap<>();
		String[] typeArray = types.split("|");
		for(String typeString:typeArray){
			typeMap.put(typeString, "");
		}
		
		for(BeanElement element:beanModel.getBeanElements()){
			if(typeMap.get(element.getElementClass())!=null){
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