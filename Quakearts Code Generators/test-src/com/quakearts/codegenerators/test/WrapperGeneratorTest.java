package com.quakearts.codegenerators.test;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;

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
		wrapperClass.setFacesType(false);
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
		parameter.setVariableName("testVariableName");

		procedure.getParameters().add(parameter);
		
		wrapperClass.setProcedure(procedure);
		wrapperClass.setSeparate(false);
		wrapperClass.setUpdater(false);
		
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
		
		wrapperClass.setExecutor(true);
		wrapperClass.setFacesType(true);
		wrapperClass.setMultipleSets(true);
		procedure.setReturnCodeEnabled(true);
		parameter.setDefaultParameter(true);
		parameter.setInoutEnabled(true);
		parameter.setNullable(true);
		parameter.setOutputEnabled(true);
		wrapperClass.setSeparate(true);
		wrapperClass.setUpdater(true);
		procedureResult.setMultiRow(true);

		result = generator.generateWrapperAsString(wrapperClass);
		stringBuilder.append(result).append("\r\n")
		.append("-------------------------------------------------------------------------------------------------")
		.append("\r\n");
		
		procedureResult = new ProcedureResult();
		procedureResult.setCondition("testVariableName != null");
		procedureResult.setJavaClassName("TestProcedureResult");
		procedureResult.setMultiRow(false);
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

		procedure.getProcedureResults().clear();

		result = generator.generateWrapperAsString(wrapperClass);
		stringBuilder.append(result).append("\r\n")
		.append("-------------------------------------------------------------------------------------------------")
		.append("\r\n");

		assertThat(stringBuilder.toString(), is("package com.test.procedure; \r\n" + 
				"import java.sql.CallableStatement;\r\n" + 
				"import java.sql.Connection;\r\n" + 
				"import java.sql.SQLException;\r\n" + 
				"import java.sql.Time;\r\n" + 
				"import java.sql.ResultSet;\r\n" + 
				"\r\n" + 
				"public class TestProcedureWrapper {\r\n" + 
				"	private static final String CALLSTATEMENT = \"{call testProcedure (?)}\";\r\n" + 
				"	\r\n" + 
				"	public static class TestProcedureResult {\r\n" + 
				"		private Time testResultVariableName;\r\n" + 
				"\r\n" + 
				"		public Time getTestResultVariableName(){\r\n" + 
				"			return testResultVariableName;\r\n" + 
				"		}\r\n" + 
				"\r\n" + 
				"		public void setTestResultVariableName(Time testResultVariableName){\r\n" + 
				"			this.testResultVariableName = testResultVariableName;\r\n" + 
				"        }\r\n" + 
				"\r\n" + 
				"	}\r\n" + 
				"\r\n" + 
				"	private TestProcedureResult testProcedureResult = new TestProcedureResult();\r\n" + 
				"	private CallableStatement call;\r\n" + 
				"\r\n" + 
				"	private Time testVariableName;\r\n" + 
				"\r\n" + 
				"	public TestProcedureResult getTestProcedureResult(){\r\n" + 
				"		return testProcedureResult;\r\n" + 
				"	}\r\n" + 
				"\r\n" + 
				"	public Time getTestVariableName(){\r\n" + 
				"		return testVariableName;\r\n" + 
				"	}\r\n" + 
				"	\r\n" + 
				"	public void setTestVariableName(Time testVariableName){\r\n" + 
				"		this.testVariableName = testVariableName;\r\n" + 
				"	}\r\n" + 
				"\r\n" + 
				"	public CallableStatement getCall() {\r\n" + 
				"		return call;\r\n" + 
				"	}\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"	public boolean execute(Connection con) throws SQLException{\r\n" + 
				"		if(testVariableName == null)\r\n" + 
				"			throw new SQLException(\"Parameter testVariableName cannot be null\");\r\n" + 
				"		call = con.prepareCall(CALLSTATEMENT);\r\n" + 
				"\r\n" + 
				"		call.setTime(1,testVariableName);\r\n" + 
				"		ResultSet rs = call.executeQuery();\r\n" + 
				"		boolean resultSets = rs.next();\r\n" + 
				"		if(resultSets){\r\n" + 
				"		if(testVariableName == null){\r\n" + 
				"\r\n" + 
				"				testProcedureResult.setTestResultVariableName(rs.getTime(1));\r\n" + 
				"		}\r\n" + 
				"		}\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"		return resultSets;\r\n" + 
				"	}\r\n" + 
				"\r\n" + 
				"}\r\n" + 
				"/* DO NOT EDIT OR REMOVE ANYTHING BELOW THIS LINE\r\n" + 
				" * wrapperdata[<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n" + 
				"<ns2:wrapper-class xmlns:ns2=\"http://quakearts.com/xml/wrapperClassSchema\" xmlns:ns3=\"http://quakearts.com/scaffolding\" packageName=\"com.test.procedure\" javaClassName=\"TestProcedureWrapper\" callName=\"Test Procedure\" multipleSets=\"false\" executor=\"false\" updater=\"false\" facesType=\"false\" separate=\"false\">\r\n" + 
				"    <procedure returnCodeEnabled=\"false\" procedureName=\"testProcedure\">\r\n" + 
				"        <parameters>\r\n" + 
				"            <parameter variableName=\"testVariableName\" type=\"92\" javaTypeName=\"Time\" javaTypeImport=\"java.sql.Time\" sqlTypesString=\"Types.TIME\" callPosition=\"1\" outputEnabled=\"false\" inoutEnabled=\"false\" defaultParameter=\"false\" nullable=\"false\" defaultValue=\"testDefaultValue\"/>\r\n" + 
				"        </parameters>\r\n" + 
				"        <procedureResults>\r\n" + 
				"            <procedureResult rowCount=\"1\" javaClassName=\"TestProcedureResult\" condition=\"testVariableName == null\" multiRow=\"false\">\r\n" + 
				"                <resultColumns>\r\n" + 
				"                    <resultColumn variableName=\"testResultVariableName\" columnNumber=\"1\" type=\"92\" javaTypeName=\"Time\" javaTypeImport=\"java.sql.Time\"/>\r\n" + 
				"                </resultColumns>\r\n" + 
				"            </procedureResult>\r\n" + 
				"        </procedureResults>\r\n" + 
				"    </procedure>\r\n" + 
				"</ns2:wrapper-class>\r\n" + 
				"]*/\r\n" + 
				"\r\n" + 
				"-------------------------------------------------------------------------------------------------\r\n" + 
				"package com.test.procedure; \r\n" + 
				"import java.sql.CallableStatement;\r\n" + 
				"import java.sql.Connection;\r\n" + 
				"import java.sql.SQLException;\r\n" + 
				"import javax.faces.application.FacesMessage;\r\n" + 
				"import javax.sql.DataSource;\r\n" + 
				"import javax.faces.context.FacesContext;\r\n" + 
				"import javax.faces.event.ActionEvent;\r\n" + 
				"import com.quakearts.webapp.facelets.util.UtilityMethods; import java.util.List;\r\n" + 
				"import java.sql.Time;\r\n" + 
				"import java.sql.ResultSet;\r\n" + 
				"import java.sql.Types;\r\n" + 
				"import java.util.ArrayList;\r\n" + 
				"\r\n" + 
				"public class TestProcedureWrapper {\r\n" + 
				"	private static final String CALLSTATEMENT = \"{?= call testProcedure (?)}\";\r\n" + 
				"	\r\n" + 
				"	private CallableStatement call;\r\n" + 
				"	private String jndiName;\r\n" + 
				"	private int returnCode;\r\n" + 
				"	private int updateCount;\r\n" + 
				"\r\n" + 
				"	private Time testVariableName;\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"	public Time getTestVariableName(){\r\n" + 
				"		return testVariableName;\r\n" + 
				"	}\r\n" + 
				"	\r\n" + 
				"	public void setTestVariableName(Time testVariableName){\r\n" + 
				"		this.testVariableName = testVariableName;\r\n" + 
				"	}\r\n" + 
				"\r\n" + 
				"	public String getJndiName() {\r\n" + 
				"		return jndiName;\r\n" + 
				"	}\r\n" + 
				"	\r\n" + 
				"	public void setJndiName(String jndiName) {\r\n" + 
				"		this.jndiName = jndiName;\r\n" + 
				"	}\r\n" + 
				"\r\n" + 
				"	public CallableStatement getCall() {\r\n" + 
				"		return call;\r\n" + 
				"	}\r\n" + 
				"\r\n" + 
				"	public int getReturnCode(){\r\n" + 
				"		return returnCode;\r\n" + 
				"	}\r\n" + 
				"\r\n" + 
				"	public boolean execute(Connection con) throws SQLException{\r\n" + 
				"		call = con.prepareCall(CALLSTATEMENT);\r\n" + 
				"\r\n" + 
				"		call.registerOutParameter(1,Types.INTEGER);\r\n" + 
				"		call.registerOutParameter(1,92);\r\n" + 
				"		updateCount = call.executeUpdate;\r\n" + 
				"		returnCode = call.getInt(1);\r\n" + 
				"		testVariableName = call.getTime(1);\r\n" + 
				"		return false\r\n" + 
				"	}\r\n" + 
				"\r\n" + 
				"	public void executeListener(ActionEvent event){\r\n" + 
				"		FacesContext ctx=FacesContext.getCurrentInstance();\r\n" + 
				"		DataSource ds;\r\n" + 
				"		try {\r\n" + 
				"			ds =(DataSource) UtilityMethods.getInitialContext().lookup(jndiName);\r\n" + 
				"		} catch (Exception e) {\r\n" + 
				"			addError(\"Cannot get datasource\", \"Exception of type \" + e.getClass().getName()\r\n" + 
				"					+ \" was thrown. Message is \" + e.getMessage()\r\n" + 
				"					+ \". Exception occured whiles getting datasource\",ctx);\r\n" + 
				"			return;\r\n" + 
				"		}\r\n" + 
				"		Connection con;\r\n" + 
				"		try {\r\n" + 
				"			con = ds.getConnection();\r\n" + 
				"		} catch (SQLException e) {\r\n" + 
				"			addError(\"Cannot get connection\", \"Exception of type \" + e.getClass().getName()\r\n" + 
				"					+ \" was thrown. Message is \" + e.getMessage()\r\n" + 
				"					+ \". Exception occured whiles getting connection\",ctx);\r\n" + 
				"			return;\r\n" + 
				"		}\r\n" + 
				"		try {\r\n" + 
				"			execute(con);\r\n" + 
				"			addInfo(\"Procedure executed\", \"Test Procedure execution completed successfully\", ctx);\r\n" + 
				"		} catch (SQLException e) {\r\n" + 
				"			addError(\"Cannot run call\", \"Exception of type \" + e.getClass().getName()\r\n" + 
				"					+ \" was thrown. Message is \" + e.getMessage()\r\n" + 
				"					+ \". Test Procedure loading did not complete successfully\",ctx);\r\n" + 
				"			return;\r\n" + 
				"		} finally {\r\n" + 
				"			try {\r\n" + 
				"				con.close();\r\n" + 
				"			} catch (Exception e2) {\r\n" + 
				"			}\r\n" + 
				"		}\r\n" + 
				"	}\r\n" + 
				"	\r\n" + 
				"	private void addError(String title, String details, FacesContext ctx){\r\n" + 
				"		ctx.addMessage(\"this\", new FacesMessage(FacesMessage.SEVERITY_ERROR, title, details));\r\n" + 
				"	}\r\n" + 
				"\r\n" + 
				"	private void addInfo(String title, String details, FacesContext ctx){\r\n" + 
				"		ctx.addMessage(\"this\", new FacesMessage(FacesMessage.SEVERITY_INFO, title, details));\r\n" + 
				"	}	\r\n" + 
				"}\r\n" + 
				"/* DO NOT EDIT OR REMOVE ANYTHING BELOW THIS LINE\r\n" + 
				" * wrapperdata[<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n" + 
				"<ns2:wrapper-class xmlns:ns2=\"http://quakearts.com/xml/wrapperClassSchema\" xmlns:ns3=\"http://quakearts.com/scaffolding\" packageName=\"com.test.procedure\" javaClassName=\"TestProcedureWrapper\" callName=\"Test Procedure\" multipleSets=\"false\" executor=\"false\" updater=\"true\" facesType=\"true\" separate=\"true\">\r\n" + 
				"    <procedure returnCodeEnabled=\"true\" procedureName=\"testProcedure\">\r\n" + 
				"        <parameters>\r\n" + 
				"            <parameter variableName=\"testVariableName\" type=\"92\" javaTypeName=\"Time\" javaTypeImport=\"java.sql.Time\" sqlTypesString=\"Types.TIME\" callPosition=\"1\" outputEnabled=\"true\" inoutEnabled=\"false\" defaultParameter=\"true\" nullable=\"true\" defaultValue=\"testDefaultValue\"/>\r\n" + 
				"        </parameters>\r\n" + 
				"        <procedureResults>\r\n" + 
				"            <procedureResult rowCount=\"1\" javaClassName=\"TestProcedureResult\" condition=\"testVariableName == null\" multiRow=\"true\">\r\n" + 
				"                <resultColumns>\r\n" + 
				"                    <resultColumn variableName=\"testResultVariableName\" columnNumber=\"1\" type=\"92\" javaTypeName=\"Time\" javaTypeImport=\"java.sql.Time\"/>\r\n" + 
				"                </resultColumns>\r\n" + 
				"            </procedureResult>\r\n" + 
				"        </procedureResults>\r\n" + 
				"    </procedure>\r\n" + 
				"</ns2:wrapper-class>\r\n" + 
				"]*/\r\n" + 
				"\r\n" + 
				"-------------------------------------------------------------------------------------------------\r\n" + 
				"package com.test.procedure; \r\n" + 
				"import java.sql.CallableStatement;\r\n" + 
				"import java.sql.Connection;\r\n" + 
				"import java.sql.SQLException;\r\n" + 
				"import javax.faces.application.FacesMessage;\r\n" + 
				"import javax.sql.DataSource;\r\n" + 
				"import javax.faces.context.FacesContext;\r\n" + 
				"import javax.faces.event.ActionEvent;\r\n" + 
				"import com.quakearts.webapp.facelets.util.UtilityMethods; import java.util.List;\r\n" + 
				"import java.sql.Time;\r\n" + 
				"import java.sql.ResultSet;\r\n" + 
				"import java.sql.Types;\r\n" + 
				"import java.util.ArrayList;\r\n" + 
				"\r\n" + 
				"public class TestProcedureWrapper {\r\n" + 
				"	private static final String CALLSTATEMENT = \"{?= call testProcedure (?)}\";\r\n" + 
				"	\r\n" + 
				"	private CallableStatement call;\r\n" + 
				"	private String jndiName;\r\n" + 
				"	private int returnCode;\r\n" + 
				"\r\n" + 
				"	private Time testVariableName;\r\n" + 
				"\r\n" + 
				"	public List<TestProcedureResult> getTestProcedureResults(){\r\n" + 
				"		return testProcedureResults;\r\n" + 
				"	}\r\n" + 
				"	public TestProcedureResult getTestProcedureResult(){\r\n" + 
				"		return testProcedureResult;\r\n" + 
				"	}\r\n" + 
				"\r\n" + 
				"	public Time getTestVariableName(){\r\n" + 
				"		return testVariableName;\r\n" + 
				"	}\r\n" + 
				"	\r\n" + 
				"	public void setTestVariableName(Time testVariableName){\r\n" + 
				"		this.testVariableName = testVariableName;\r\n" + 
				"	}\r\n" + 
				"\r\n" + 
				"	public String getJndiName() {\r\n" + 
				"		return jndiName;\r\n" + 
				"	}\r\n" + 
				"	\r\n" + 
				"	public void setJndiName(String jndiName) {\r\n" + 
				"		this.jndiName = jndiName;\r\n" + 
				"	}\r\n" + 
				"\r\n" + 
				"	public CallableStatement getCall() {\r\n" + 
				"		return call;\r\n" + 
				"	}\r\n" + 
				"\r\n" + 
				"	public int getReturnCode(){\r\n" + 
				"		return returnCode;\r\n" + 
				"	}\r\n" + 
				"\r\n" + 
				"	public boolean execute(Connection con) throws SQLException{\r\n" + 
				"		call = con.prepareCall(CALLSTATEMENT);\r\n" + 
				"\r\n" + 
				"		call.registerOutParameter(1,Types.INTEGER);\r\n" + 
				"		call.registerOutParameter(1,92);\r\n" + 
				"		boolean resultSets = call.execute();\r\n" + 
				"		if(resultSets){\r\n" + 
				"			do {\r\n" + 
				"//TODO: Complete results processing for multiple sets\r\n" + 
				"/*				ResultSet rs = call.getResultSet();\r\n" + 
				"		if(testVariableName == null){\r\n" + 
				"\r\n" + 
				"			do {\r\n" + 
				"				TestProcedureResult testProcedureResult = new TestProcedureResult();\r\n" + 
				"				testProcedureResult.setTestResultVariableName(rs.getTime(1));\r\n" + 
				"				testProcedureResults.add(testProcedureResult);\r\n" + 
				"			} while(rs.next());\r\n" + 
				"		}\r\n" + 
				"\r\n" + 
				"		if(testVariableName != null){\r\n" + 
				"\r\n" + 
				"				testProcedureResult.setTestResultVariableName2(rs.getInt(1));\r\n" + 
				"		}\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"*/\r\n" + 
				"			} while(call.getMoreResults());\r\n" + 
				"		}\r\n" + 
				"		returnCode = call.getInt(1);\r\n" + 
				"		testVariableName = call.getTime(1);\r\n" + 
				"		return resultSets;\r\n" + 
				"	}\r\n" + 
				"\r\n" + 
				"	public void executeListener(ActionEvent event){\r\n" + 
				"		FacesContext ctx=FacesContext.getCurrentInstance();\r\n" + 
				"		DataSource ds;\r\n" + 
				"		try {\r\n" + 
				"			ds =(DataSource) UtilityMethods.getInitialContext().lookup(jndiName);\r\n" + 
				"		} catch (Exception e) {\r\n" + 
				"			addError(\"Cannot get datasource\", \"Exception of type \" + e.getClass().getName()\r\n" + 
				"					+ \" was thrown. Message is \" + e.getMessage()\r\n" + 
				"					+ \". Exception occured whiles getting datasource\",ctx);\r\n" + 
				"			return;\r\n" + 
				"		}\r\n" + 
				"		Connection con;\r\n" + 
				"		try {\r\n" + 
				"			con = ds.getConnection();\r\n" + 
				"		} catch (SQLException e) {\r\n" + 
				"			addError(\"Cannot get connection\", \"Exception of type \" + e.getClass().getName()\r\n" + 
				"					+ \" was thrown. Message is \" + e.getMessage()\r\n" + 
				"					+ \". Exception occured whiles getting connection\",ctx);\r\n" + 
				"			return;\r\n" + 
				"		}\r\n" + 
				"		try {\r\n" + 
				"			execute(con);\r\n" + 
				"			addInfo(\"Procedure executed\", \"Test Procedure execution completed successfully\", ctx);\r\n" + 
				"		} catch (SQLException e) {\r\n" + 
				"			addError(\"Cannot run call\", \"Exception of type \" + e.getClass().getName()\r\n" + 
				"					+ \" was thrown. Message is \" + e.getMessage()\r\n" + 
				"					+ \". Test Procedure loading did not complete successfully\",ctx);\r\n" + 
				"			return;\r\n" + 
				"		} finally {\r\n" + 
				"			try {\r\n" + 
				"				con.close();\r\n" + 
				"			} catch (Exception e2) {\r\n" + 
				"			}\r\n" + 
				"		}\r\n" + 
				"	}\r\n" + 
				"	\r\n" + 
				"	private void addError(String title, String details, FacesContext ctx){\r\n" + 
				"		ctx.addMessage(\"this\", new FacesMessage(FacesMessage.SEVERITY_ERROR, title, details));\r\n" + 
				"	}\r\n" + 
				"\r\n" + 
				"	private void addInfo(String title, String details, FacesContext ctx){\r\n" + 
				"		ctx.addMessage(\"this\", new FacesMessage(FacesMessage.SEVERITY_INFO, title, details));\r\n" + 
				"	}	\r\n" + 
				"}\r\n" + 
				"/* DO NOT EDIT OR REMOVE ANYTHING BELOW THIS LINE\r\n" + 
				" * wrapperdata[<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n" + 
				"<ns2:wrapper-class xmlns:ns2=\"http://quakearts.com/xml/wrapperClassSchema\" xmlns:ns3=\"http://quakearts.com/scaffolding\" packageName=\"com.test.procedure\" javaClassName=\"TestProcedureWrapper\" callName=\"Test Procedure\" multipleSets=\"true\" executor=\"false\" updater=\"false\" facesType=\"true\" separate=\"true\">\r\n" + 
				"    <procedure returnCodeEnabled=\"true\" procedureName=\"testProcedure\">\r\n" + 
				"        <parameters>\r\n" + 
				"            <parameter variableName=\"testVariableName\" type=\"92\" javaTypeName=\"Time\" javaTypeImport=\"java.sql.Time\" sqlTypesString=\"Types.TIME\" callPosition=\"1\" outputEnabled=\"true\" inoutEnabled=\"false\" defaultParameter=\"true\" nullable=\"true\" defaultValue=\"testDefaultValue\"/>\r\n" + 
				"        </parameters>\r\n" + 
				"        <procedureResults>\r\n" + 
				"            <procedureResult rowCount=\"1\" javaClassName=\"TestProcedureResult\" condition=\"testVariableName == null\" multiRow=\"true\">\r\n" + 
				"                <resultColumns>\r\n" + 
				"                    <resultColumn variableName=\"testResultVariableName\" columnNumber=\"1\" type=\"92\" javaTypeName=\"Time\" javaTypeImport=\"java.sql.Time\"/>\r\n" + 
				"                </resultColumns>\r\n" + 
				"            </procedureResult>\r\n" + 
				"            <procedureResult rowCount=\"10\" javaClassName=\"TestProcedureResult\" condition=\"testVariableName != null\" multiRow=\"false\">\r\n" + 
				"                <resultColumns>\r\n" + 
				"                    <resultColumn variableName=\"testResultVariableName2\" columnNumber=\"1\" type=\"4\" javaTypeName=\"int\" javaTypeImport=\"\"/>\r\n" + 
				"                </resultColumns>\r\n" + 
				"            </procedureResult>\r\n" + 
				"        </procedureResults>\r\n" + 
				"    </procedure>\r\n" + 
				"</ns2:wrapper-class>\r\n" + 
				"]*/\r\n" + 
				"\r\n" + 
				"-------------------------------------------------------------------------------------------------\r\n" + 
				"package com.test.procedure; \r\n" + 
				"import java.sql.CallableStatement;\r\n" + 
				"import java.sql.Connection;\r\n" + 
				"import java.sql.SQLException;\r\n" + 
				"import javax.faces.application.FacesMessage;\r\n" + 
				"import javax.sql.DataSource;\r\n" + 
				"import javax.faces.context.FacesContext;\r\n" + 
				"import javax.faces.event.ActionEvent;\r\n" + 
				"import com.quakearts.webapp.facelets.util.UtilityMethods; import java.sql.Time;\r\n" + 
				"import java.sql.ResultSet;\r\n" + 
				"import java.sql.Types;\r\n" + 
				"\r\n" + 
				"public class TestProcedureWrapper {\r\n" + 
				"	private static final String CALLSTATEMENT = \"{?= call testProcedure (?)}\";\r\n" + 
				"	\r\n" + 
				"	private CallableStatement call;\r\n" + 
				"	private String jndiName;\r\n" + 
				"	private int returnCode;\r\n" + 
				"\r\n" + 
				"	private Time testVariableName;\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"	public Time getTestVariableName(){\r\n" + 
				"		return testVariableName;\r\n" + 
				"	}\r\n" + 
				"	\r\n" + 
				"	public void setTestVariableName(Time testVariableName){\r\n" + 
				"		this.testVariableName = testVariableName;\r\n" + 
				"	}\r\n" + 
				"\r\n" + 
				"	public String getJndiName() {\r\n" + 
				"		return jndiName;\r\n" + 
				"	}\r\n" + 
				"	\r\n" + 
				"	public void setJndiName(String jndiName) {\r\n" + 
				"		this.jndiName = jndiName;\r\n" + 
				"	}\r\n" + 
				"\r\n" + 
				"	public CallableStatement getCall() {\r\n" + 
				"		return call;\r\n" + 
				"	}\r\n" + 
				"\r\n" + 
				"	public int getReturnCode(){\r\n" + 
				"		return returnCode;\r\n" + 
				"	}\r\n" + 
				"\r\n" + 
				"	public boolean execute(Connection con) throws SQLException{\r\n" + 
				"		call = con.prepareCall(CALLSTATEMENT);\r\n" + 
				"\r\n" + 
				"		call.registerOutParameter(1,Types.INTEGER);\r\n" + 
				"		call.registerOutParameter(1,92);\r\n" + 
				"		boolean resultSets = call.execute();\r\n" + 
				"		if(resultSets){\r\n" + 
				"			do {\r\n" + 
				"//TODO: Complete results processing for multiple sets\r\n" + 
				"/*				ResultSet rs = call.getResultSet();\r\n" + 
				"\r\n" + 
				"*/\r\n" + 
				"			} while(call.getMoreResults());\r\n" + 
				"		}\r\n" + 
				"		returnCode = call.getInt(1);\r\n" + 
				"		testVariableName = call.getTime(1);\r\n" + 
				"		return resultSets;\r\n" + 
				"	}\r\n" + 
				"\r\n" + 
				"	public void executeListener(ActionEvent event){\r\n" + 
				"		FacesContext ctx=FacesContext.getCurrentInstance();\r\n" + 
				"		DataSource ds;\r\n" + 
				"		try {\r\n" + 
				"			ds =(DataSource) UtilityMethods.getInitialContext().lookup(jndiName);\r\n" + 
				"		} catch (Exception e) {\r\n" + 
				"			addError(\"Cannot get datasource\", \"Exception of type \" + e.getClass().getName()\r\n" + 
				"					+ \" was thrown. Message is \" + e.getMessage()\r\n" + 
				"					+ \". Exception occured whiles getting datasource\",ctx);\r\n" + 
				"			return;\r\n" + 
				"		}\r\n" + 
				"		Connection con;\r\n" + 
				"		try {\r\n" + 
				"			con = ds.getConnection();\r\n" + 
				"		} catch (SQLException e) {\r\n" + 
				"			addError(\"Cannot get connection\", \"Exception of type \" + e.getClass().getName()\r\n" + 
				"					+ \" was thrown. Message is \" + e.getMessage()\r\n" + 
				"					+ \". Exception occured whiles getting connection\",ctx);\r\n" + 
				"			return;\r\n" + 
				"		}\r\n" + 
				"		try {\r\n" + 
				"			execute(con);\r\n" + 
				"			addInfo(\"Procedure executed\", \"Test Procedure execution completed successfully\", ctx);\r\n" + 
				"		} catch (SQLException e) {\r\n" + 
				"			addError(\"Cannot run call\", \"Exception of type \" + e.getClass().getName()\r\n" + 
				"					+ \" was thrown. Message is \" + e.getMessage()\r\n" + 
				"					+ \". Test Procedure loading did not complete successfully\",ctx);\r\n" + 
				"			return;\r\n" + 
				"		} finally {\r\n" + 
				"			try {\r\n" + 
				"				con.close();\r\n" + 
				"			} catch (Exception e2) {\r\n" + 
				"			}\r\n" + 
				"		}\r\n" + 
				"	}\r\n" + 
				"	\r\n" + 
				"	private void addError(String title, String details, FacesContext ctx){\r\n" + 
				"		ctx.addMessage(\"this\", new FacesMessage(FacesMessage.SEVERITY_ERROR, title, details));\r\n" + 
				"	}\r\n" + 
				"\r\n" + 
				"	private void addInfo(String title, String details, FacesContext ctx){\r\n" + 
				"		ctx.addMessage(\"this\", new FacesMessage(FacesMessage.SEVERITY_INFO, title, details));\r\n" + 
				"	}	\r\n" + 
				"}\r\n" + 
				"/* DO NOT EDIT OR REMOVE ANYTHING BELOW THIS LINE\r\n" + 
				" * wrapperdata[<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n" + 
				"<ns2:wrapper-class xmlns:ns2=\"http://quakearts.com/xml/wrapperClassSchema\" xmlns:ns3=\"http://quakearts.com/scaffolding\" packageName=\"com.test.procedure\" javaClassName=\"TestProcedureWrapper\" callName=\"Test Procedure\" multipleSets=\"true\" executor=\"false\" updater=\"false\" facesType=\"true\" separate=\"true\">\r\n" + 
				"    <procedure returnCodeEnabled=\"true\" procedureName=\"testProcedure\">\r\n" + 
				"        <parameters>\r\n" + 
				"            <parameter variableName=\"testVariableName\" type=\"92\" javaTypeName=\"Time\" javaTypeImport=\"java.sql.Time\" sqlTypesString=\"Types.TIME\" callPosition=\"1\" outputEnabled=\"true\" inoutEnabled=\"false\" defaultParameter=\"true\" nullable=\"true\" defaultValue=\"testDefaultValue\"/>\r\n" + 
				"        </parameters>\r\n" + 
				"        <procedureResults/>\r\n" + 
				"    </procedure>\r\n" + 
				"</ns2:wrapper-class>\r\n" + 
				"]*/\r\n" + 
				"\r\n" + 
				"-------------------------------------------------------------------------------------------------\r\n"));
	}

}
