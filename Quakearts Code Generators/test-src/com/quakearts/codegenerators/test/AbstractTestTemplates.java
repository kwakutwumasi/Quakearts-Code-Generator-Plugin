package com.quakearts.codegenerators.test;

import static org.junit.Assert.*;

import java.beans.IntrospectionException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import com.quakearts.test.hibernate.Guests;
import com.quakearts.test.hibernate.Menu;
import com.quakearts.test.hibernate.Party;
import com.quakearts.test.hibernate.Seating;
import com.quakearts.test.hibernate.SeatingType;
import com.quakearts.webtools.codegenerators.ScaffoldingTemplateGenerator;
import com.quakearts.webtools.codegenerators.model.BeanModel;
import com.quakearts.webtools.codegenerators.model.PropertyEntry;
import com.quakearts.webtools.codegenerators.model.Scaffolding;

public abstract class AbstractTestTemplates {

	protected static Scaffolding scaffolding;
	protected static ScaffoldingTemplateGenerator generator = new ScaffoldingTemplateGenerator();
	
	private static ArrayList<BeanModel> beanModels;
	private static Map<String, BeanModel> nameModelMapping;
	private static Map<String, BeanModel> classModelMapping;

	protected void loadTemplate(String templateName){
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(templateName);
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			StringBuilder builder = new StringBuilder();
			String line;
			while((line=reader.readLine())!=null){
				builder.append(line).append("\r\n");
			}
			
			StringResourceLoader.getRepository().putStringResource(templateName, builder.toString());
			reader.close();
		} catch (IOException e) {
			fail("Exception of type " + e.getClass().getName() + " was thrown. Message is " + e.getMessage()
					+ ". Exception occured whiles loading "+templateName);
		} finally {
			try {
				stream.close();
			} catch (Exception e2) {
			}
		}
	}
	
	protected void loadScaffolding(String fileName){
		if(scaffolding==null){
			FileInputStream stream = null;
			try {
				stream = new FileInputStream(fileName);
				JAXBContext jaxbContext = JAXBContext.newInstance(Scaffolding.class);
				Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
				scaffolding = (Scaffolding) unmarshaller.unmarshal(stream);
			} catch (FileNotFoundException e) {
				fail("Exception of type " + e.getClass().getName() + " was thrown. Message is " + e.getMessage()
						+ ". Exception occured whiles loading "+fileName);;
			} catch (JAXBException e) {
				fail("Exception of type " + e.getClass().getName() + " was thrown. Message is " + e.getMessage()
				+ ". Exception occured whiles loading "+fileName);;
			} finally {
				try {
					stream.close();
				} catch (Exception e2) {
				}
			}
		}
	}

	private void createContextVariables(){
		loadScaffolding();
		if(beanModels==null){
			beanModels = new ArrayList<>();
			nameModelMapping = new HashMap<>();
			classModelMapping = new HashMap<>();
	
			try {
				BeanModel beanModel;
				beanModel = new BeanModel(Guests.class.getName(), null);
				beanModels.add(beanModel);
				nameModelMapping.put(beanModel.getName(), beanModel);
				classModelMapping.put(beanModel.getBeanClass().getName(), beanModel);
	
				beanModel = new BeanModel(Menu.class.getName(), null);
				beanModels.add(beanModel);
				nameModelMapping.put(beanModel.getName(), beanModel);
				classModelMapping.put(beanModel.getBeanClass().getName(), beanModel);
	
				beanModel = new BeanModel(Party.class.getName(), null);
				beanModels.add(beanModel);
				nameModelMapping.put(beanModel.getName(), beanModel);
				classModelMapping.put(beanModel.getBeanClass().getName(), beanModel);
	
				beanModel = new BeanModel(Seating.class.getName(), null);
				beanModels.add(beanModel);
				nameModelMapping.put(beanModel.getName(), beanModel);
				classModelMapping.put(beanModel.getBeanClass().getName(), beanModel);
	
				beanModel = new BeanModel(SeatingType.class.getName(), null);
				beanModels.add(beanModel);
				nameModelMapping.put(beanModel.getName(), beanModel);
				classModelMapping.put(beanModel.getBeanClass().getName(), beanModel);
			} catch (ClassNotFoundException | IntrospectionException e) {
				beanModels=null;
				fail("Exception of type " + e.getClass().getName() + " was thrown. Message is " + e.getMessage()
						+ ". Exception occured whiles creating BeanModel");
			}
		
		}
	}
	
	protected VelocityContext getContext(){
		createContextVariables();
		VelocityContext context = new VelocityContext();
		for(PropertyEntry entry:scaffolding.getProperties().getEntries()){
			context.put(entry.getProperty(), entry.getValue());
		}
		
		context.put("beanModels", beanModels);
		context.put("scaffolding", scaffolding);
		context.put("nameModelMapping", nameModelMapping);
		context.put("classModelMapping", classModelMapping);
		
		return context;
	}
	
	protected abstract void loadScaffolding();

	protected static ArrayList<BeanModel> getBeanModels() {
		return beanModels;
	}
}
