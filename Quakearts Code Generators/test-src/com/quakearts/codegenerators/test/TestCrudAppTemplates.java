package com.quakearts.codegenerators.test;

import static org.junit.Assert.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.velocity.VelocityContext;
import org.junit.Before;
import org.junit.Test;

import com.quakearts.webtools.codegenerators.model.BeanModel;

public class TestCrudAppTemplates extends AbstractTestTemplates {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testCrudXhtml() {	
		String template = "C:\\Users\\Public\\luna-workspace\\Quakearts Code Generators\\src\\com\\quakearts\\webtools\\codegenerators\\scaffolding\\crudapp\\crud.xhtml";
		getContext();
		loadTemplate(template);
		for(BeanModel beanModel:getBeanModels()){
			loadForModel(template, "test-out\\"+beanModel.getName()+".crud.xhtml", beanModel);
		}
	}
	
	@Test
	public void testCreateXhtml() {	
		String template = "C:\\Users\\Public\\luna-workspace\\Quakearts Code Generators\\src\\com\\quakearts\\webtools\\codegenerators\\scaffolding\\crudapp\\create.xhtml";
		getContext();
		loadTemplate(template);
		for(BeanModel beanModel:getBeanModels()){
			loadForModel(template, "test-out\\"+beanModel.getName()+".create.xhtml", beanModel);
		}
	}
	
	@Test
	public void testTemplateXhtml() {	
		String template = "C:\\Users\\Public\\luna-workspace\\Quakearts Code Generators\\src\\com\\quakearts\\webtools\\codegenerators\\scaffolding\\crudapp\\template.xhtml";
		getContext();
		loadTemplate(template);
		loadForModel(template, "test-out\\template.xhtml", null);
	}
	
	@Test
	public void testCrudAppBeanXhtml() {	
		String template = "C:\\Users\\Public\\luna-workspace\\Quakearts Code Generators\\src\\com\\quakearts\\webtools\\codegenerators\\scaffolding\\crudapp\\crudappapplicationbean.vm";
		getContext();
		loadTemplate(template);
		loadForModel(template, "test-out\\crudappapplicationbean.vm.java", null);
	}

	@Test
	public void testListXhtml() {	
		String template = "C:\\Users\\Public\\luna-workspace\\Quakearts Code Generators\\src\\com\\quakearts\\webtools\\codegenerators\\scaffolding\\crudapp\\list.xhtml";
		getContext();
		loadTemplate(template);
		for(BeanModel beanModel:getBeanModels()){
			loadForModel(template, "test-out\\"+beanModel.getName()+".list.xhtml", beanModel);
		}
	}
	
	private void loadForModel(String template, String fileName, BeanModel beanModel){
		FileOutputStream fos=null;
		try {
			fos= new FileOutputStream(fileName);
			VelocityContext context = getContext();
			context.put("beanModel", beanModel);
			InputStream is = generator.generatePage(context, template);
			int read;
			byte[] buffer = new byte[1024];
			while((read=is.read(buffer))!=-1){
				fos.write(buffer,0,read);
			}
		} catch (IOException e) {
			fail("Exception of type " + e.getClass().getName() + " was thrown. Message is " + e.getMessage()
					+ ". Exception occured whiles generating "+fileName);
		} finally {
			try {
				fos.close();
			} catch (Exception e2) {
			}
		}
	}

	@Override
	protected void loadScaffolding() {
		loadScaffolding("C:\\Users\\Public\\luna-workspace\\Quakearts Code Generators\\src\\com\\quakearts\\webtools\\codegenerators\\scaffolding\\hibernate-crudapp-scaffolding.xml");
	}

}
