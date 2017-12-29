package com.quakearts.codegenerators.test;

import java.beans.IntrospectionException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

import com.quakearts.test.beans.TestBean7;
import com.quakearts.test.beans.TestBean8;
import com.quakearts.test.hibernate.TestBean1;
import com.quakearts.test.hibernate.TestBean2;
import com.quakearts.test.hibernate.TestBean3;
import com.quakearts.test.hibernate.TestBean4;
import com.quakearts.test.hibernate.TestBean5;
import com.quakearts.test.hibernate.TestBean6;
import com.quakearts.tools.web.context.ScaffoldingContext;
import com.quakearts.tools.web.generator.ScaffoldingTemplateGenerator;
import com.quakearts.tools.web.model.BeanModel;
import com.quakearts.tools.web.model.BeanModelBuilder;
import com.quakearts.tools.web.model.Folder;
import com.quakearts.tools.web.model.FolderStructure;
import com.quakearts.tools.web.model.PropertyEntry;
import com.quakearts.tools.web.model.Scaffolding;
import com.quakearts.tools.web.model.ScaffoldingProperties;

public class ScaffoldingTemplateTest {
	

	private String generateTestTemplates(String templateName, boolean generateForEach, Collection<? extends PropertyEntry> entries) throws IOException, ClassNotFoundException, IntrospectionException {
		ScaffoldingTemplateGenerator generator = new ScaffoldingTemplateGenerator();
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("com/quakearts/tools/web/generator/scaffolding/"+templateName);
		
		if(is == null)
			throw new RuntimeException("Unable to load "+templateName);
		
		byte[] buffer;
		buffer = new byte[is.available()];
		is.read(buffer);
		generator.loadTemplate(templateName, new String(buffer));
		
		Scaffolding scaffolding = new Scaffolding();
		scaffolding.setId("testScaffoldingId");
		
		FolderStructure folderStructure = new FolderStructure();
		Folder folder = new Folder();
		folder.setFolderID("testFolderId");
		folderStructure.setFolder(folder);
		scaffolding.setFolderStructure(folderStructure);
		
		ScaffoldingProperties scaffoldingProperties = new ScaffoldingProperties();
		scaffoldingProperties.getEntries().addAll(entries);		
		scaffolding.setProperties(scaffoldingProperties);
		
		ScaffoldingContext scaffoldingContext = new ScaffoldingContext();
		BeanModel beanModel;
		beanModel = BeanModelBuilder.createBeanModel(TestBean1.class.getName(), Thread.currentThread().getContextClassLoader());
		scaffoldingContext.addBeanModel(beanModel);
		beanModel = BeanModelBuilder.createBeanModel(TestBean2.class.getName(), Thread.currentThread().getContextClassLoader());
		scaffoldingContext.addBeanModel(beanModel);
		beanModel = BeanModelBuilder.createBeanModel(TestBean3.class.getName(), Thread.currentThread().getContextClassLoader());
		scaffoldingContext.addBeanModel(beanModel);
		beanModel = BeanModelBuilder.createBeanModel(TestBean4.class.getName(), Thread.currentThread().getContextClassLoader());
		scaffoldingContext.addBeanModel(beanModel);
		beanModel = BeanModelBuilder.createBeanModel(TestBean5.class.getName(), Thread.currentThread().getContextClassLoader());
		scaffoldingContext.addBeanModel(beanModel);
		beanModel = BeanModelBuilder.createBeanModel(TestBean6.class.getName(), Thread.currentThread().getContextClassLoader());
		scaffoldingContext.addBeanModel(beanModel);
		beanModel = BeanModelBuilder.createBeanModel(TestBean7.class.getName(), Thread.currentThread().getContextClassLoader());
		scaffoldingContext.addBeanModel(beanModel);
		beanModel = BeanModelBuilder.createBeanModel(TestBean8.class.getName(), Thread.currentThread().getContextClassLoader());
		scaffoldingContext.addBeanModel(beanModel);
				
		generator.populateContext(scaffolding, scaffoldingContext);
		if(generateForEach) {
			StringBuilder resultStringBuilder = new StringBuilder();
			for(BeanModel beanModelEntry:scaffoldingContext.getBeanModels()) {
				resultStringBuilder.append(generator.generatePageAsString(templateName,beanModelEntry))
				.append("\r\n")
				.append("---------------------------------------------------------------------------------------------------------------------------------------------------------")
				.append("\r\n");
			}
			
			return resultStringBuilder.toString();
		} else {
			return generator.generatePageAsString(templateName,null);
		}
	}
	
	private void generateAndWriteToFile(String templateName, String outputFileName, boolean generateForEach, Collection<? extends PropertyEntry> entries) throws ClassNotFoundException, IOException, IntrospectionException {
		String result = generateTestTemplates(templateName, generateForEach, entries);
		try(FileOutputStream fos = new FileOutputStream("test-out"+File.separator+outputFileName)) {
			fos.write(result.getBytes());
			fos.flush();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private Collection<PropertyEntry> getHibernateCrudAppProperties(){
		ArrayList<PropertyEntry> entries = new ArrayList<>();
		PropertyEntry entry = new PropertyEntry();
		entry.setDisplayName("Application Title");
		entry.setProperty("apptitle");
		entry.setValue("Database Application");
		entries.add(entry);
		
		entry = new PropertyEntry();
		entry.setDisplayName("Company");
		entry.setProperty("company");
		entry.setValue("Quake Arts Inc.");
		entries.add(entry);
		
		entry = new PropertyEntry();
		entry.setDisplayName("CRUD Managed Bean Class Name");
		entry.setProperty("crudappClassName");
		entry.setValue("CRUDApplicationBean");
		entries.add(entry);

		entry = new PropertyEntry();
		entry.setDisplayName("CRUD Managed Bean Name");
		entry.setProperty("crudappBeanName");
		entry.setValue("crudapp");
		entries.add(entry);
		
		return entries;
	}
	
	@Test
	public void testCrudXHTMLScaffoldingBasics() throws Exception {
		generateAndWriteToFile("basic/crud.xhtml","crud.basic.template.out", true, getHibernateCrudAppProperties());
	}

	@Test
	public void testFinderJAVAScaffoldingBasics() throws Exception {
		generateAndWriteToFile("basic/finder.vm","finder.basic.template.out", true, getHibernateCrudAppProperties());
	}

	@Test
	public void testCreateXHTMLScaffoldingCrudapp() throws Exception {
		generateAndWriteToFile("crudapp/create.xhtml","create.crudapp.xhtml.template.out", true, getHibernateCrudAppProperties());
	}

	@Test
	public void testCrudXHTMLScaffoldingCrudapp() throws Exception {
		generateAndWriteToFile("crudapp/crud.xhtml","crud.crudapp.xhtml.template.out", true, getHibernateCrudAppProperties());
	}

	@Test
	public void testCrudappAplicationBeanJAVAScaffoldingCrudapp() throws Exception {
		generateAndWriteToFile("crudapp/crudappapplicationbean.vm","crudappapplicationbean.crudapp.xhtml.template.out", false, getHibernateCrudAppProperties());
	}

	@Test
	public void testDropDownHelperJAVAScaffoldingCrudapp() throws Exception {
		generateAndWriteToFile("crudapp/dropdownhelper.vm","dropdownhelper.crudapp.xhtml.template.out", false, getHibernateCrudAppProperties());
	}

	@Test
	public void testFinderJAVAScaffoldingCrudapp() throws Exception {
		generateAndWriteToFile("crudapp/finder.vm","finder.crudapp.xhtml.template.out", false, getHibernateCrudAppProperties());
	}
	
	@Test
	public void testListXHTMLScaffoldingCrudapp() throws Exception {
		generateAndWriteToFile("crudapp/list.xhtml","list.crudapp.xhtml.template.out", false, getHibernateCrudAppProperties());
	}
	
	@Test
	public void testPageJAVAScaffoldingCrudapp() throws Exception {
		generateAndWriteToFile("crudapp/page.vm","page.crudapp.xhtml.template.out", false, getHibernateCrudAppProperties());
	}

	@Test
	public void testTemplateXHTMLScaffoldingCrudapp() throws Exception {
		generateAndWriteToFile("crudapp/template.xhtml","template.crudapp.xhtml.template.out", false, getHibernateCrudAppProperties());
	}

	@Test
	public void testCreateXHTMLScaffoldingCrudappSecure() throws Exception {
		generateAndWriteToFile("crudappsecure/create.xhtml","create.crudappsecure.xhtml.template.out", true, getHibernateCrudAppProperties());
	}

	@Test
	public void testListXHTMLScaffoldingCrudappSecure() throws Exception {
		generateAndWriteToFile("crudappsecure/list.xhtml","list.crudappsecure.xhtml.template.out", false, getHibernateCrudAppProperties());
	}

	@Test
	public void testTemplateXHTMLScaffoldingCrudappSecure() throws Exception {
		generateAndWriteToFile("crudappsecure/template.xhtml","template.crudappsecure.xhtml.template.out", false, getHibernateCrudAppProperties());
	}

}
