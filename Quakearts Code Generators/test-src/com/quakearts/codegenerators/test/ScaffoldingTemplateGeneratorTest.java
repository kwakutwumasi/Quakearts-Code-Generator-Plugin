package com.quakearts.codegenerators.test;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;

import java.beans.IntrospectionException;

import org.junit.Test;

import com.quakearts.test.beans.TestBean7;
import com.quakearts.test.hibernate.TestBean1;
import com.quakearts.test.hibernate.TestBean3;
import com.quakearts.tools.web.context.ScaffoldingContext;
import com.quakearts.tools.web.generator.ScaffoldingTemplateGenerator;
import com.quakearts.tools.web.model.BeanModel;
import com.quakearts.tools.web.model.BeanModelBuilder;
import com.quakearts.tools.web.model.Folder;
import com.quakearts.tools.web.model.FolderStructure;
import com.quakearts.tools.web.model.PropertyEntry;
import com.quakearts.tools.web.model.Scaffolding;
import com.quakearts.tools.web.model.ScaffoldingProperties;

public class ScaffoldingTemplateGeneratorTest {
	
	@Test
	public void testScaffoldingGenerator() throws ClassNotFoundException, IntrospectionException {
		ScaffoldingTemplateGenerator generator = new ScaffoldingTemplateGenerator();
		generator.loadTemplate("testTemplate", "Scaffolding ID:\r\n" + 
				"$scaffolding.id\r\n" + 
				"Bean Models:\r\n" + 
				"#foreach($beanModel in $beanModels)\r\n" + 
				"$beanModel.name\r\n" + 
				"#end\r\n" + 
				"Name Model Mapping:\r\n" + 
				"$nameModelMapping.get('testBean1').name\r\n" + 
				"$nameModelMapping.get('testBean2').name\r\n" + 
				"$nameModelMapping.get('testBean3').name\r\n" + 
				"Class Model Mapping:\r\n" + 
				"$classModelMapping.get('com.quakearts.test.hibernate.TestBean1').name\r\n" + 
				"$classModelMapping.get('com.quakearts.test.hibernate.TestBean2').name\r\n" + 
				"$classModelMapping.get('com.quakearts.test.hibernate.TestBean3').name\r\n" + 
				"Utils:\r\n" + 
				"#foreach($beanModel in $beanModels)\r\n" + 
				"camelCase: $util.camelCase($beanModel.name)\r\n" + 
				"reverseCamelCase: $util.reverseCamelCase($beanModel.beanClass.simpleName)\r\n" + 
				"splitCamelCase: $util.splitCamelCase($beanModel.name)\r\n" + 
				"verifyMethod :$util.verifyMethod($beanModel,'getClass')\r\n" + 
				"verifyAnnotation: $util.verifyAnnotation($beanModel,'javax.persistence.Entity')\r\n" + 
				"verifyChildOf:$util.verifyChildOf($beanModel,'java.lang.Object')\r\n" + 
				"\r\n" + 
				"Elements of Type String:\r\n" + 
				"#foreach($element in $util.findElementsOfType($beanModel, 'java.lang.String'))\r\n" + 
				"$element.value\r\n" + 
				"#end" +
				"\r\n" + 
				"\r\n" + 
				"#end\r\n" + 
				"\r\n" + 
				"Enum Imports\r\n" + 
				"#foreach($import in $util.getEnumImports($beanModels))\r\n" + 
				"$import\r\n" + 
				"#end\r\n" + 
				"\r\n" + 
				"Enums\r\n" + 
				"#foreach($import in $util.getEnums($beanModels))\r\n" + 
				"$import\r\n" + 
				"#end");
		
		generator.loadTemplate("testTemplateBeanModel", "Model Name:\r\n" + 
				"$beanModel.name\r\n" + 
				"Model Class:\r\n" + 
				"$beanModel.beanClass\r\n" + 
				"Model Class Name:\r\n" + 
				"$beanModel.BeanClassName\r\n" + 
				"Model Generator Properties\r\n" + 
				"$beanModel.generatorProperties\r\n" + 
				"ID Element:\r\n" + 
				"$beanModel.idElement.value\r\n" + 
				"Bean Elements\r\n" + 
				"#foreach($element in $beanModel.beanElements)\r\n" + 
				"Value:$element.value\r\n" + 
				"ElementClass:$element.elementClass\r\n" + 
				"Is Read Only:$element.readOnly\r\n" + 
				"Is Identity:$element.identity\r\n" + 
				"Is ID:$element.id\r\n" + 
				"Is Non FK:$element.nonFk\r\n" + 
				"Is Primitive Number: $element.primitiveNumber\r\n" + 
				"Is String: $element.string\r\n" + 
				"Is Numeric: $element.numeric\r\n" + 
				"Is Primitive or String: $element.primitiveOrString\r\n" + 
				"Is Date Time Type: $element.dateTimeType\r\n" + 
				"Is Enum: $element.enum\r\n" + 
				"Is Array: $element.array\r\n" + 
				"Is Collection: $element.collection\r\n" + 
				"Is Map: $element.map\r\n" + 
				"Is Boolean: $element.boolean\r\n" + 
				"Generator Properties: $element.generatorProperties\r\n" + 
				"#end\r\n" + 
				"Bean Element Classes: $beanModel.beanElementClasses\r\n" + 
				"Bean Element Classes for Known Types: $beanModel.beanElementClassesForKnownTypes\r\n" + 
				"Bean Element Classes for Unknown Types: $beanModel.beanElementClassesForUnKnownTypes");
		
		Scaffolding scaffolding = new Scaffolding();
		scaffolding.setId("testScaffoldingId");
		
		FolderStructure folderStructure = new FolderStructure();
		Folder folder = new Folder();
		folder.setFolderID("testFolderId");
		folderStructure.setFolder(folder);
		scaffolding.setFolderStructure(folderStructure);
		
		ScaffoldingProperties scaffoldingProperties = new ScaffoldingProperties();
		PropertyEntry entry = new PropertyEntry();
		entry.setDisplayName("Test Key");
		entry.setProperty("testKey");
		entry.setValue("testValue");
		scaffoldingProperties.getEntries().add(entry);		
		scaffolding.setProperties(scaffoldingProperties);
		
		ScaffoldingContext scaffoldingContext = new ScaffoldingContext();
		BeanModel beanModel = BeanModelBuilder.createBeanModel(TestBean1.class.getName(), Thread.currentThread().getContextClassLoader());
		scaffoldingContext.addBeanModel(beanModel);
		beanModel = BeanModelBuilder.createBeanModel(TestBean3.class.getName(), Thread.currentThread().getContextClassLoader());
		scaffoldingContext.addBeanModel(beanModel);
		beanModel = BeanModelBuilder.createBeanModel(TestBean7.class.getName(), Thread.currentThread().getContextClassLoader());
		scaffoldingContext.addBeanModel(beanModel);
				
		generator.populateContext(scaffolding, scaffoldingContext);
		String result = generator.generatePageAsString("testTemplate", null);
		assertThat(result, is("Scaffolding ID:\r\n" + 
				"testScaffoldingId\r\n" + 
				"Bean Models:\r\n" + 
				"testBean1\r\n" + 
				"testBean3\r\n" + 
				"testFacesManagedBeanName\r\n" + 
				"Name Model Mapping:\r\n" + 
				"testBean1\r\n" + 
				"$nameModelMapping.get('testBean2').name\r\n" + 
				"testBean3\r\n" + 
				"Class Model Mapping:\r\n" + 
				"testBean1\r\n" + 
				"$classModelMapping.get('com.quakearts.test.hibernate.TestBean2').name\r\n" + 
				"testBean3\r\n" + 
				"Utils:\r\n" + 
				"camelCase: TestBean1\r\n" + 
				"reverseCamelCase: testBean1\r\n" + 
				"splitCamelCase: Test Bean1\r\n" + 
				"verifyMethod :true\r\n" + 
				"verifyAnnotation: true\r\n" + 
				"verifyChildOf:true\r\n" + 
				"\r\n" + 
				"Elements of Type String:\r\n" + 
				"aString\r\n" + 
				"\r\n" + 
				"camelCase: TestBean3\r\n" + 
				"reverseCamelCase: testBean3\r\n" + 
				"splitCamelCase: Test Bean3\r\n" + 
				"verifyMethod :true\r\n" + 
				"verifyAnnotation: true\r\n" + 
				"verifyChildOf:true\r\n" + 
				"\r\n" + 
				"Elements of Type String:\r\n" + 
				"\r\n" + 
				"camelCase: TestFacesManagedBeanName\r\n" + 
				"reverseCamelCase: testBean7\r\n" + 
				"splitCamelCase: Test Faces Managed Bean Name\r\n" + 
				"verifyMethod :true\r\n" + 
				"verifyAnnotation: false\r\n" + 
				"verifyChildOf:true\r\n" + 
				"\r\n" + 
				"Elements of Type String:\r\n" + 
				"idString\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"Enum Imports\r\n" + 
				"com.quakearts.test.hibernate.TestEnum\r\n" + 
				"\r\n" + 
				"Enums\r\n" + 
				"TestEnum\r\n"));
		StringBuilder builderResult = new StringBuilder();
		for(BeanModel beanModelEntry:scaffoldingContext.getBeanModels()) {
			builderResult.append(generator.generatePageAsString("testTemplateBeanModel", beanModelEntry))
			.append("\r\n")
			.append("-------------------------------------------------------------------------------------------------------------------")
			.append("\r\n");
		}
		
		assertThat(builderResult.toString(), is("Model Name:\r\n" + 
				"testBean1\r\n" + 
				"Model Class:\r\n" + 
				"class com.quakearts.test.hibernate.TestBean1\r\n" + 
				"Model Class Name:\r\n" + 
				"com.quakearts.test.hibernate.TestBean1\r\n" + 
				"Model Generator Properties\r\n" + 
				"{}\r\n" + 
				"ID Element:\r\n" + 
				"$beanModel.idElement.value\r\n" + 
				"Bean Elements\r\n" + 
				"Value:aBigDecimal\r\n" + 
				"ElementClass:java.math.BigDecimal\r\n" + 
				"Is Read Only:false\r\n" + 
				"Is Identity:false\r\n" + 
				"Is ID:false\r\n" + 
				"Is Non FK:false\r\n" + 
				"Is Primitive Number: false\r\n" + 
				"Is String: false\r\n" + 
				"Is Numeric: true\r\n" + 
				"Is Primitive or String: false\r\n" + 
				"Is Date Time Type: false\r\n" + 
				"Is Enum: false\r\n" + 
				"Is Array: false\r\n" + 
				"Is Collection: false\r\n" + 
				"Is Map: false\r\n" + 
				"Is Boolean: false\r\n" + 
				"Generator Properties: {}\r\n" + 
				"Value:aBigInteger\r\n" + 
				"ElementClass:java.math.BigInteger\r\n" + 
				"Is Read Only:false\r\n" + 
				"Is Identity:false\r\n" + 
				"Is ID:false\r\n" + 
				"Is Non FK:false\r\n" + 
				"Is Primitive Number: false\r\n" + 
				"Is String: false\r\n" + 
				"Is Numeric: true\r\n" + 
				"Is Primitive or String: false\r\n" + 
				"Is Date Time Type: false\r\n" + 
				"Is Enum: false\r\n" + 
				"Is Array: false\r\n" + 
				"Is Collection: false\r\n" + 
				"Is Map: false\r\n" + 
				"Is Boolean: false\r\n" + 
				"Generator Properties: {}\r\n" + 
				"Value:aByte\r\n" + 
				"ElementClass:byte\r\n" + 
				"Is Read Only:false\r\n" + 
				"Is Identity:false\r\n" + 
				"Is ID:false\r\n" + 
				"Is Non FK:false\r\n" + 
				"Is Primitive Number: true\r\n" + 
				"Is String: false\r\n" + 
				"Is Numeric: true\r\n" + 
				"Is Primitive or String: true\r\n" + 
				"Is Date Time Type: false\r\n" + 
				"Is Enum: false\r\n" + 
				"Is Array: false\r\n" + 
				"Is Collection: false\r\n" + 
				"Is Map: false\r\n" + 
				"Is Boolean: false\r\n" + 
				"Generator Properties: {}\r\n" + 
				"Value:aByteWrapper\r\n" + 
				"ElementClass:java.lang.Byte\r\n" + 
				"Is Read Only:false\r\n" + 
				"Is Identity:false\r\n" + 
				"Is ID:false\r\n" + 
				"Is Non FK:false\r\n" + 
				"Is Primitive Number: false\r\n" + 
				"Is String: false\r\n" + 
				"Is Numeric: true\r\n" + 
				"Is Primitive or String: true\r\n" + 
				"Is Date Time Type: false\r\n" + 
				"Is Enum: false\r\n" + 
				"Is Array: false\r\n" + 
				"Is Collection: false\r\n" + 
				"Is Map: false\r\n" + 
				"Is Boolean: false\r\n" + 
				"Generator Properties: {}\r\n" + 
				"Value:aDate\r\n" + 
				"ElementClass:java.util.Date\r\n" + 
				"Is Read Only:false\r\n" + 
				"Is Identity:false\r\n" + 
				"Is ID:false\r\n" + 
				"Is Non FK:false\r\n" + 
				"Is Primitive Number: false\r\n" + 
				"Is String: false\r\n" + 
				"Is Numeric: false\r\n" + 
				"Is Primitive or String: false\r\n" + 
				"Is Date Time Type: true\r\n" + 
				"Is Enum: false\r\n" + 
				"Is Array: false\r\n" + 
				"Is Collection: false\r\n" + 
				"Is Map: false\r\n" + 
				"Is Boolean: false\r\n" + 
				"Generator Properties: {}\r\n" + 
				"Value:aLong\r\n" + 
				"ElementClass:long\r\n" + 
				"Is Read Only:false\r\n" + 
				"Is Identity:false\r\n" + 
				"Is ID:false\r\n" + 
				"Is Non FK:false\r\n" + 
				"Is Primitive Number: true\r\n" + 
				"Is String: false\r\n" + 
				"Is Numeric: true\r\n" + 
				"Is Primitive or String: true\r\n" + 
				"Is Date Time Type: false\r\n" + 
				"Is Enum: false\r\n" + 
				"Is Array: false\r\n" + 
				"Is Collection: false\r\n" + 
				"Is Map: false\r\n" + 
				"Is Boolean: false\r\n" + 
				"Generator Properties: {}\r\n" + 
				"Value:aLongWrapper\r\n" + 
				"ElementClass:java.lang.Long\r\n" + 
				"Is Read Only:false\r\n" + 
				"Is Identity:false\r\n" + 
				"Is ID:false\r\n" + 
				"Is Non FK:false\r\n" + 
				"Is Primitive Number: false\r\n" + 
				"Is String: false\r\n" + 
				"Is Numeric: true\r\n" + 
				"Is Primitive or String: true\r\n" + 
				"Is Date Time Type: false\r\n" + 
				"Is Enum: false\r\n" + 
				"Is Array: false\r\n" + 
				"Is Collection: false\r\n" + 
				"Is Map: false\r\n" + 
				"Is Boolean: false\r\n" + 
				"Generator Properties: {}\r\n" + 
				"Value:aShort\r\n" + 
				"ElementClass:short\r\n" + 
				"Is Read Only:false\r\n" + 
				"Is Identity:false\r\n" + 
				"Is ID:false\r\n" + 
				"Is Non FK:false\r\n" + 
				"Is Primitive Number: true\r\n" + 
				"Is String: false\r\n" + 
				"Is Numeric: true\r\n" + 
				"Is Primitive or String: true\r\n" + 
				"Is Date Time Type: false\r\n" + 
				"Is Enum: false\r\n" + 
				"Is Array: false\r\n" + 
				"Is Collection: false\r\n" + 
				"Is Map: false\r\n" + 
				"Is Boolean: false\r\n" + 
				"Generator Properties: {}\r\n" + 
				"Value:aShortWrapper\r\n" + 
				"ElementClass:java.lang.Short\r\n" + 
				"Is Read Only:false\r\n" + 
				"Is Identity:false\r\n" + 
				"Is ID:false\r\n" + 
				"Is Non FK:false\r\n" + 
				"Is Primitive Number: false\r\n" + 
				"Is String: false\r\n" + 
				"Is Numeric: true\r\n" + 
				"Is Primitive or String: true\r\n" + 
				"Is Date Time Type: false\r\n" + 
				"Is Enum: false\r\n" + 
				"Is Array: false\r\n" + 
				"Is Collection: false\r\n" + 
				"Is Map: false\r\n" + 
				"Is Boolean: false\r\n" + 
				"Generator Properties: {}\r\n" + 
				"Value:aString\r\n" + 
				"ElementClass:java.lang.String\r\n" + 
				"Is Read Only:false\r\n" + 
				"Is Identity:false\r\n" + 
				"Is ID:false\r\n" + 
				"Is Non FK:false\r\n" + 
				"Is Primitive Number: false\r\n" + 
				"Is String: true\r\n" + 
				"Is Numeric: false\r\n" + 
				"Is Primitive or String: true\r\n" + 
				"Is Date Time Type: false\r\n" + 
				"Is Enum: false\r\n" + 
				"Is Array: false\r\n" + 
				"Is Collection: false\r\n" + 
				"Is Map: false\r\n" + 
				"Is Boolean: false\r\n" + 
				"Generator Properties: {}\r\n" + 
				"Value:anInt\r\n" + 
				"ElementClass:int\r\n" + 
				"Is Read Only:false\r\n" + 
				"Is Identity:true\r\n" + 
				"Is ID:false\r\n" + 
				"Is Non FK:false\r\n" + 
				"Is Primitive Number: true\r\n" + 
				"Is String: false\r\n" + 
				"Is Numeric: true\r\n" + 
				"Is Primitive or String: true\r\n" + 
				"Is Date Time Type: false\r\n" + 
				"Is Enum: false\r\n" + 
				"Is Array: false\r\n" + 
				"Is Collection: false\r\n" + 
				"Is Map: false\r\n" + 
				"Is Boolean: false\r\n" + 
				"Generator Properties: {}\r\n" + 
				"Value:anIntWrapper\r\n" + 
				"ElementClass:java.lang.Integer\r\n" + 
				"Is Read Only:false\r\n" + 
				"Is Identity:false\r\n" + 
				"Is ID:false\r\n" + 
				"Is Non FK:false\r\n" + 
				"Is Primitive Number: false\r\n" + 
				"Is String: false\r\n" + 
				"Is Numeric: true\r\n" + 
				"Is Primitive or String: true\r\n" + 
				"Is Date Time Type: false\r\n" + 
				"Is Enum: false\r\n" + 
				"Is Array: false\r\n" + 
				"Is Collection: false\r\n" + 
				"Is Map: false\r\n" + 
				"Is Boolean: false\r\n" + 
				"Generator Properties: {}\r\n" + 
				"Value:intid\r\n" + 
				"ElementClass:int\r\n" + 
				"Is Read Only:false\r\n" + 
				"Is Identity:true\r\n" + 
				"Is ID:true\r\n" + 
				"Is Non FK:false\r\n" + 
				"Is Primitive Number: true\r\n" + 
				"Is String: false\r\n" + 
				"Is Numeric: true\r\n" + 
				"Is Primitive or String: true\r\n" + 
				"Is Date Time Type: false\r\n" + 
				"Is Enum: false\r\n" + 
				"Is Array: false\r\n" + 
				"Is Collection: false\r\n" + 
				"Is Map: false\r\n" + 
				"Is Boolean: false\r\n" + 
				"Generator Properties: {}\r\n" + 
				"Value:testBean2\r\n" + 
				"ElementClass:com.quakearts.test.hibernate.TestBean2\r\n" + 
				"Is Read Only:false\r\n" + 
				"Is Identity:false\r\n" + 
				"Is ID:false\r\n" + 
				"Is Non FK:false\r\n" + 
				"Is Primitive Number: false\r\n" + 
				"Is String: false\r\n" + 
				"Is Numeric: false\r\n" + 
				"Is Primitive or String: false\r\n" + 
				"Is Date Time Type: false\r\n" + 
				"Is Enum: false\r\n" + 
				"Is Array: false\r\n" + 
				"Is Collection: false\r\n" + 
				"Is Map: false\r\n" + 
				"Is Boolean: false\r\n" + 
				"Generator Properties: {}\r\n" + 
				"Value:testEnum\r\n" + 
				"ElementClass:com.quakearts.test.hibernate.TestEnum\r\n" + 
				"Is Read Only:false\r\n" + 
				"Is Identity:false\r\n" + 
				"Is ID:false\r\n" + 
				"Is Non FK:false\r\n" + 
				"Is Primitive Number: false\r\n" + 
				"Is String: false\r\n" + 
				"Is Numeric: false\r\n" + 
				"Is Primitive or String: false\r\n" + 
				"Is Date Time Type: false\r\n" + 
				"Is Enum: true\r\n" + 
				"Is Array: false\r\n" + 
				"Is Collection: false\r\n" + 
				"Is Map: false\r\n" + 
				"Is Boolean: false\r\n" + 
				"Generator Properties: {}\r\n" + 
				"Bean Element Classes: [class java.math.BigDecimal, class java.math.BigInteger, byte, class java.lang.Byte, class java.util.Date, long, class java.lang.Long, short, class java.lang.Short, class java.lang.String, int, class java.lang.Integer, class com.quakearts.test.hibernate.TestBean2, class com.quakearts.test.hibernate.TestEnum]\r\n" + 
				"Bean Element Classes for Known Types: [class java.math.BigDecimal, class java.math.BigInteger, byte, class java.lang.Byte, class java.util.Date, long, class java.lang.Long, short, class java.lang.Short, class java.lang.String, int, class java.lang.Integer]\r\n" + 
				"Bean Element Classes for Unknown Types: [class com.quakearts.test.hibernate.TestBean2, class com.quakearts.test.hibernate.TestEnum]\r\n" + 
				"-------------------------------------------------------------------------------------------------------------------\r\n" + 
				"Model Name:\r\n" + 
				"testBean3\r\n" + 
				"Model Class:\r\n" + 
				"class com.quakearts.test.hibernate.TestBean3\r\n" + 
				"Model Class Name:\r\n" + 
				"com.quakearts.test.hibernate.TestBean3\r\n" + 
				"Model Generator Properties\r\n" + 
				"{testClassKey1=testCassProperty1, testClassKey2=testCassProperty2}\r\n" + 
				"ID Element:\r\n" + 
				"$beanModel.idElement.value\r\n" + 
				"Bean Elements\r\n" + 
				"Value:id1\r\n" + 
				"ElementClass:int\r\n" + 
				"Is Read Only:false\r\n" + 
				"Is Identity:false\r\n" + 
				"Is ID:true\r\n" + 
				"Is Non FK:false\r\n" + 
				"Is Primitive Number: true\r\n" + 
				"Is String: false\r\n" + 
				"Is Numeric: true\r\n" + 
				"Is Primitive or String: true\r\n" + 
				"Is Date Time Type: false\r\n" + 
				"Is Enum: false\r\n" + 
				"Is Array: false\r\n" + 
				"Is Collection: false\r\n" + 
				"Is Map: false\r\n" + 
				"Is Boolean: false\r\n" + 
				"Generator Properties: {}\r\n" + 
				"Value:testBean4\r\n" + 
				"ElementClass:com.quakearts.test.hibernate.TestBean4\r\n" + 
				"Is Read Only:false\r\n" + 
				"Is Identity:false\r\n" + 
				"Is ID:false\r\n" + 
				"Is Non FK:false\r\n" + 
				"Is Primitive Number: false\r\n" + 
				"Is String: false\r\n" + 
				"Is Numeric: false\r\n" + 
				"Is Primitive or String: false\r\n" + 
				"Is Date Time Type: false\r\n" + 
				"Is Enum: false\r\n" + 
				"Is Array: false\r\n" + 
				"Is Collection: false\r\n" + 
				"Is Map: false\r\n" + 
				"Is Boolean: false\r\n" + 
				"Generator Properties: {testFieldKey=testFieldProperty}\r\n" + 
				"Value:testBean5\r\n" + 
				"ElementClass:com.quakearts.test.hibernate.TestBean5\r\n" + 
				"Is Read Only:false\r\n" + 
				"Is Identity:false\r\n" + 
				"Is ID:false\r\n" + 
				"Is Non FK:false\r\n" + 
				"Is Primitive Number: false\r\n" + 
				"Is String: false\r\n" + 
				"Is Numeric: false\r\n" + 
				"Is Primitive or String: false\r\n" + 
				"Is Date Time Type: false\r\n" + 
				"Is Enum: false\r\n" + 
				"Is Array: false\r\n" + 
				"Is Collection: false\r\n" + 
				"Is Map: false\r\n" + 
				"Is Boolean: false\r\n" + 
				"Generator Properties: {}\r\n" + 
				"Bean Element Classes: [int, class com.quakearts.test.hibernate.TestBean4, class com.quakearts.test.hibernate.TestBean5]\r\n" + 
				"Bean Element Classes for Known Types: [int]\r\n" + 
				"Bean Element Classes for Unknown Types: [class com.quakearts.test.hibernate.TestBean4, class com.quakearts.test.hibernate.TestBean5]\r\n" + 
				"-------------------------------------------------------------------------------------------------------------------\r\n" + 
				"Model Name:\r\n" + 
				"testFacesManagedBeanName\r\n" + 
				"Model Class:\r\n" + 
				"class com.quakearts.test.beans.TestBean7\r\n" + 
				"Model Class Name:\r\n" + 
				"com.quakearts.test.beans.TestBean7\r\n" + 
				"Model Generator Properties\r\n" + 
				"{}\r\n" + 
				"ID Element:\r\n" + 
				"$beanModel.idElement.value\r\n" + 
				"Bean Elements\r\n" + 
				"Value:idString\r\n" + 
				"ElementClass:java.lang.String\r\n" + 
				"Is Read Only:false\r\n" + 
				"Is Identity:false\r\n" + 
				"Is ID:true\r\n" + 
				"Is Non FK:false\r\n" + 
				"Is Primitive Number: false\r\n" + 
				"Is String: true\r\n" + 
				"Is Numeric: false\r\n" + 
				"Is Primitive or String: true\r\n" + 
				"Is Date Time Type: false\r\n" + 
				"Is Enum: false\r\n" + 
				"Is Array: false\r\n" + 
				"Is Collection: false\r\n" + 
				"Is Map: false\r\n" + 
				"Is Boolean: false\r\n" + 
				"Generator Properties: {testFieldPropertiesKey2=testFieldPropertiesValue2, testFieldPropertiesKey1=testFieldPropertiesValue1}\r\n" + 
				"Bean Element Classes: [class java.lang.String]\r\n" + 
				"Bean Element Classes for Known Types: [class java.lang.String]\r\n" + 
				"Bean Element Classes for Unknown Types: []\r\n" + 
				"-------------------------------------------------------------------------------------------------------------------\r\n"));
	}

}
