package com.quakearts.codegenerators.test;

//import static org.junit.Assert.*;
//import static org.hamcrest.core.Is.*;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.junit.Test;

import com.quakearts.tools.data.generator.WrapperGenerator;
import com.quakearts.tools.data.model.Parameter;
import com.quakearts.tools.data.model.Procedure;
import com.quakearts.tools.data.model.ProcedureResult;
import com.quakearts.tools.data.model.ResultColumn;
import com.quakearts.tools.data.model.WrapperClass;

public class WrapperGeneratorTest {

	@Test
	public void testProcedureGeneration() throws IOException, JAXBException {
		WrapperClass wrapperClass = new WrapperClass();
		wrapperClass.setCallName("Test Procedure");
		wrapperClass.setExecutor(false);
		wrapperClass.setSeparate(false);
		wrapperClass.setUpdater(false);
		wrapperClass.setJavaClassName("TestProcedureWrapper");
		wrapperClass.setMultipleSets(false);
		wrapperClass.setPackageName("com.test.procedure");
		
		Procedure procedure = new Procedure();
		procedure.setProcedureName("testProcedure");
		procedure.setReturnCodeEnabled(false);

		Parameter parameter = new Parameter();
		parameter.setCallPosition(1);
		parameter.setDefaultParameter(false);
		parameter.setDefaultValue("testDefaultValue");
		parameter.setInoutEnabled(false);
		parameter.setJavaTypeImport("java.sql.Time");
		parameter.setJavaTypeName("Time");
		parameter.setNullable(false);
		parameter.setOutputEnabled(false);
		parameter.setSqlTypesString("Types.TIME");
		parameter.setType(92);
		parameter.setVariableName("testTime");

		procedure.getParameters().add(parameter);
		
		parameter = new Parameter();
		parameter.setCallPosition(2);
		parameter.setDefaultParameter(false);
		parameter.setInoutEnabled(true);
		parameter.setJavaTypeName("int");
		parameter.setNullable(true);
		parameter.setOutputEnabled(false);
		parameter.setSqlTypesString("Types.INTEGER");
		parameter.setType(4);
		parameter.setVariableName("testInt");

		procedure.getParameters().add(parameter);
		
		parameter = new Parameter();
		parameter.setCallPosition(3);
		parameter.setDefaultParameter(false);
		parameter.setDefaultValue("testDefaultValue");
		parameter.setInoutEnabled(false);
		parameter.setJavaTypeImport("java.math.BigDecimal");
		parameter.setJavaTypeName("BigDecimal");
		parameter.setNullable(false);
		parameter.setOutputEnabled(true);
		parameter.setSqlTypesString("Types.DECIMAL");
		parameter.setType(3);
		parameter.setVariableName("testDecimal");

		procedure.getParameters().add(parameter);
		
		wrapperClass.setProcedure(procedure);
		
		ProcedureResult procedureResult = new ProcedureResult();
		procedureResult.setCondition("testVariableName == null");
		procedureResult.setJavaClassName("TestProcedureResult");
		procedureResult.setMultiRow(false);
		procedureResult.setRowCount(1);
		
		ResultColumn resultColumn = new ResultColumn();
		resultColumn.setColumnNumber(1);
		resultColumn.setJavaTypeImport("java.sql.Time");
		resultColumn.setJavaTypeName("Time");
		resultColumn.setType(92);
		resultColumn.setVariableName("testResultVariableName");
		procedureResult.getResultColumns().add(resultColumn);
		
		procedure.getProcedureResults().add(procedureResult);
		
		StringBuilder stringBuilder = new StringBuilder();
		
		WrapperGenerator generator = new WrapperGenerator();
		
		String result = generator.generateWrapperAsString(wrapperClass);
		stringBuilder.append(result).append("\r\n")
		.append("-------------------------------------------------------------------------------------------------")
		.append("\r\n");
		
		procedureResult.setMultiRow(true);

		result = generator.generateWrapperAsString(wrapperClass);
		stringBuilder.append(result).append("\r\n")
		.append("-------------------------------------------------------------------------------------------------")
		.append("\r\n");
		
		procedureResult.setMultiRow(false);

		procedureResult = new ProcedureResult();
		procedureResult.setCondition("testVariableName != null");
		procedureResult.setJavaClassName("TestProcedureResult2");
		procedureResult.setMultiRow(true);
		procedureResult.setRowCount(10);
		
		resultColumn = new ResultColumn();
		resultColumn.setColumnNumber(1);
		resultColumn.setJavaTypeImport(null);
		resultColumn.setJavaTypeName("int");
		resultColumn.setType(4);
		resultColumn.setVariableName("testResultVariableName2");
		procedureResult.getResultColumns().add(resultColumn);
		
		procedure.getProcedureResults().add(procedureResult);
		
		result = generator.generateWrapperAsString(wrapperClass);
		stringBuilder.append(result).append("\r\n")
		.append("-------------------------------------------------------------------------------------------------")
		.append("\r\n");

		wrapperClass.setSeparate(true);
		
		result = generator.generateWrapperAsString(wrapperClass);
		stringBuilder.append(result).append("\r\n")
		.append("-------------------------------------------------------------------------------------------------")
		.append("\r\n");

		procedure.getProcedureResults().clear();

		result = generator.generateWrapperAsString(wrapperClass);
		stringBuilder.append(result).append("\r\n")
		.append("-------------------------------------------------------------------------------------------------")
		.append("\r\n");
				
		wrapperClass.setExecutor(true);
		
		result = generator.generateWrapperAsString(wrapperClass);
		stringBuilder.append(result).append("\r\n")
		.append("-------------------------------------------------------------------------------------------------")
		.append("\r\n");
				
		wrapperClass.setUpdater(true);

		result = generator.generateWrapperAsString(wrapperClass);
		stringBuilder.append(result).append("\r\n")
		.append("-------------------------------------------------------------------------------------------------")
		.append("\r\n");
		
		System.out.println(stringBuilder);
	}

}
