package com.quakearts.codegenerators.test;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsNot.*;

import org.junit.Test;

import java.beans.IntrospectionException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.quakearts.test.beans.TestBean7;
import com.quakearts.test.beans.TestBean8;
import com.quakearts.test.hibernate.TestBean1;
import com.quakearts.test.hibernate.TestBean2;
import com.quakearts.test.hibernate.TestBean3;
import com.quakearts.test.hibernate.TestBean4;
import com.quakearts.test.hibernate.TestBean5;
import com.quakearts.test.hibernate.TestBean6;
import com.quakearts.test.hibernate.TestEnum;
import com.quakearts.tools.web.model.BeanElement;
import com.quakearts.tools.web.model.BeanModel;
import com.quakearts.tools.web.model.BeanModelBuilder;

public class BeanModelBuilderTest {

	@Test
	public void testBuilderWithDifferentElements() throws Exception {
		/*TestParent:
		 * Skipped elements
		 * Properties
		 * Id Class
		 * GeneratedValue/Identity
		 * OneToOne relations
		 */
		
		BeanModel beanModel;
		beanModel = (BeanModelBuilder.createBeanModel(TestBean1.class.getName(), Thread.currentThread().getContextClassLoader()));
		assertThat(beanModel.getBeanClass() == TestBean1.class, is(true));
		assertThat(beanModel.getBeanClassName(), is(TestBean1.class.getName()));
		assertThat(beanModel.getBeanElements().size(), is(15));
		assertThat(beanModel.getName(), is("testBean1"));

		Map<String, Class<?>> elementsMap = new HashMap<>();
		elementsMap.put("intid",int.class);
		elementsMap.put("aString",String.class);
		elementsMap.put("anInt",int.class);
		elementsMap.put("aShort",short.class);
		elementsMap.put("aLong",long.class);
		elementsMap.put("aByte",byte.class);
		elementsMap.put("anIntWrapper",Integer.class);
		elementsMap.put("aLongWrapper",Long.class);
		elementsMap.put("aShortWrapper",Short.class);
		elementsMap.put("aByteWrapper",Byte.class);
		elementsMap.put("aBigInteger",BigInteger.class);
		elementsMap.put("aBigDecimal",BigDecimal.class);
		elementsMap.put("aDate",Date.class);
		elementsMap.put("testEnum",TestEnum.class);
		elementsMap.put("testBean2",TestBean2.class);
		
		for(BeanElement element:beanModel.getBeanElements()) {
			assertThat(elementsMap.containsKey(element.getValue()), is(true));
			Class<?> clazz = elementsMap.get(element.getValue());
			assertThat(element.getElementClass(), is(clazz.getName()));
		}
	
		assertThat(beanModel.getIdElement() == null, is(false));
		assertThat(beanModel.getIdElement().getId() == null, is(false));
		assertThat(beanModel.getIdElement().getId().getValue(), is("intid"));

		beanModel = (BeanModelBuilder.createBeanModel(TestBean6.class.getName(), Thread.currentThread().getContextClassLoader()));
		assertThat(beanModel.getIdElement()==null, is(false));
		assertThat(beanModel.getIdElement().getIds().size(), is(2));
		
		beanModel = (BeanModelBuilder.createBeanModel(TestBean7.class.getName(), Thread.currentThread().getContextClassLoader()));
		assertThat(beanModel.getName(), is("testFacesManagedBeanName"));
		assertThat(beanModel.getIdElement() == null, is(false));
		assertThat(beanModel.getIdElement().getId() == null, is(false));
		assertThat(beanModel.getIdElement().getId().getValue(), is("idString"));

		for(BeanElement element:beanModel.getBeanElements()) {
			assertThat(element.getValue(), is(not("stringForIgnoredProperty")));
		}
		
		beanModel = (BeanModelBuilder.createBeanModel(TestBean8.class.getName(), Thread.currentThread().getContextClassLoader()));
		assertThat(beanModel.getName(), is("testManagedBeanNames"));
	}

	@Test
	public void testOrder() throws ClassNotFoundException, IntrospectionException {
		BeanModel beanModel;
		beanModel = (BeanModelBuilder.createBeanModel(TestBean5.class.getName(), Thread.currentThread().getContextClassLoader()));
		
		int iteration = 1;
		for(BeanElement element:beanModel.getBeanElements()) {
			switch (iteration) {
			case 1:
				assertThat(element.getValue(), is("firstString"));
				break;
			case 2:
				assertThat(element.getValue(), is("secondString"));
				break;
			case 3:
				assertThat(element.getValue(), is("thirdString"));
				break;
			case 4:
				assertThat(element.getValue(), is("testBean3"));
				break;
			case 5:
				assertThat(element.getValue(), is("aString"));
				break;
			default:
				break;
			}
			iteration++;
		}
		
	}

	@Test
	public void testBuilderForPropertiesExtraction() throws Exception {
		BeanModel beanModel;
		beanModel = (BeanModelBuilder.createBeanModel(TestBean3.class.getName(), Thread.currentThread().getContextClassLoader()));
		
		for(BeanElement element:beanModel.getBeanElements()) {
			if(element.getValue().equals("testBean4"))
			assertThat(element.getGeneratorProperties().get("testFieldKey"), is("testFieldProperty"));
		}

		assertThat(beanModel.getGeneratorProperties().size(), is(2));
		assertThat(beanModel.getGeneratorProperties().get("testClassKey1"), is("testCassProperty1"));
		assertThat(beanModel.getGeneratorProperties().get("testClassKey2"), is("testCassProperty2"));
		
		beanModel = (BeanModelBuilder.createBeanModel(TestBean7.class.getName(), Thread.currentThread().getContextClassLoader()));
		
		for(BeanElement element:beanModel.getBeanElements()) {
			if(element.getValue().equals("idString")) {
				assertThat(element.getGeneratorProperties().get("testFieldPropertiesKey1"), is("testFieldPropertiesValue1"));
				assertThat(element.getGeneratorProperties().get("testFieldPropertiesKey2"), is("testFieldPropertiesValue2"));
			}
		}

	}
	
	@Test
	public void testBuilderForSkippedElements() throws ClassNotFoundException, IntrospectionException {
		BeanModel beanModel;
		beanModel = (BeanModelBuilder.createBeanModel(TestBean4.class.getName(), Thread.currentThread().getContextClassLoader()));
		assertThat(beanModel.getBeanElements().size(), is(3));
		
		for(BeanElement element:beanModel.getBeanElements()) {
			assertThat(element.getValue(), is(not("skippedString")));
			assertThat(element.getValue(), is(not("stringForSkippedInheritedMethod")));
			assertThat(element.getValue(), is(not("stringForSkippedGet")));
		}
	}
	
	@Test
	public void testAnnotationNotFound(){
		
	}

}
