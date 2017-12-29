package com.quakearts.codegenerators.test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import javax.persistence.Entity;

import static org.hamcrest.core.Is.*;

import org.junit.Test;

import com.quakearts.test.hibernate.TestBean1;
import com.quakearts.tools.utils.GenerationUtilMethods;
import com.quakearts.tools.web.model.BeanElement;
import com.quakearts.tools.web.model.BeanModel;
import com.quakearts.tools.web.model.BeanModelBuilder;

public class TestGeneratorUtils {

	private GenerationUtilMethods generationUtilMethods = new GenerationUtilMethods();
	
	public GenerationUtilMethods getGenerationUtilMethods() {
		return generationUtilMethods;
	}
	
	@Test
	public void testCamelCase() throws Exception {
		assertThat(getGenerationUtilMethods().camelCase("testCase1"), is("TestCase1"));
		assertThat(getGenerationUtilMethods().camelCase("tEstCase1"), is("tEstCase1"));
	}

	@Test
	public void testReverseCamelCase() throws Exception {
		assertThat(getGenerationUtilMethods().reverseCamelCase("TestCase1"), is("testCase1"));
		assertThat(getGenerationUtilMethods().reverseCamelCase("TEstCase1"), is("tEstCase1"));
	}
	
	@Test
	public void testSplitCamelCase() throws Exception {
		assertThat(getGenerationUtilMethods().splitCamelCase("testCase1"), is("Test Case1"));
		assertThat(getGenerationUtilMethods().splitCamelCase("tEstCase1"), is("T Est Case1"));		
	}
	
	@Test
	public void testVerifyMethod() throws Exception {
		BeanModel beanModel = BeanModelBuilder.createBeanModel(TestBean1.class.getName(), Thread.currentThread().getContextClassLoader());
		assertThat(getGenerationUtilMethods().verifyMethod(beanModel, "getClass"), is(true));
		assertThat(getGenerationUtilMethods().verifyMethod(beanModel, "notThere"), is(false));
	}
	
	@Test
	public void testVerifyAnnotation() throws Exception {
		BeanModel beanModel = BeanModelBuilder.createBeanModel(TestBean1.class.getName(), Thread.currentThread().getContextClassLoader());
		assertThat(getGenerationUtilMethods().verifyAnnotation(beanModel, Entity.class.getName()), is(true));
		assertThat(getGenerationUtilMethods().verifyAnnotation(beanModel, "com.not.There"), is(false));
	}
	
	@Test
	public void testVerifyChildOf() throws Exception {
		BeanModel beanModel = BeanModelBuilder.createBeanModel(TestBean1.class.getName(), Thread.currentThread().getContextClassLoader());
		assertThat(getGenerationUtilMethods().verifyChildOf(beanModel, Object.class.getName()), is(true));
		assertThat(getGenerationUtilMethods().verifyChildOf(beanModel, String.class.getName()), is(false));
	}
	
	@Test
	public void testFindElementsOfType() throws Exception {
		BeanModel beanModel = BeanModelBuilder.createBeanModel(TestBean1.class.getName(), Thread.currentThread().getContextClassLoader());
		Collection<BeanElement> collection = getGenerationUtilMethods().findElementsOfType(beanModel, "java.lang.String");
		
		assertThat(collection.size(), is(1));
	}
	
	@Test
	public void testGetEnumImports() throws Exception {
		BeanModel beanModel = BeanModelBuilder.createBeanModel(TestBean1.class.getName(), Thread.currentThread().getContextClassLoader());
		Collection<String> collection = getGenerationUtilMethods().getEnumImports(Arrays.asList(beanModel));
		
		assertThat(collection.size(), is(1));
		assertThat(collection.iterator().next(), is("com.quakearts.test.hibernate.TestEnum"));
	}
	
	@Test
	public void testGetEnums() throws Exception {
		BeanModel beanModel = BeanModelBuilder.createBeanModel(TestBean1.class.getName(), Thread.currentThread().getContextClassLoader());
		Collection<String> collection = getGenerationUtilMethods().getEnums(Arrays.asList(beanModel));
		
		assertThat(collection.size(), is(1));		
		assertThat(collection.iterator().next(), is("TestEnum"));
	}
}
