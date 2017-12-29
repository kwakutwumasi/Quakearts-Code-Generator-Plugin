/*******************************************************************************
* Copyright (C) 2016 Kwaku Twumasi-Afriyie <kwaku.twumasi@quakearts.com>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Kwaku Twumasi-Afriyie <kwaku.twumasi@quakearts.com> - initial API and implementation
 ******************************************************************************/
package com.quakearts.tools.data.generator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashSet;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import com.quakearts.tools.CodeGenerators;
import com.quakearts.tools.data.model.Parameter;
import com.quakearts.tools.data.model.Procedure;
import com.quakearts.tools.data.model.ProcedureResult;
import com.quakearts.tools.data.model.ResultColumn;
import com.quakearts.tools.data.model.WrapperClass;
import com.quakearts.tools.generatorbase.GenericGenerator;

public class WrapperGenerator extends GenericGenerator {
	private Template resultClassTemplate;
	
	public WrapperGenerator() {
		template = getEngine().getTemplate("com/quakearts/tools/data/generator/templates/procedure.vm");
		resultClassTemplate = getEngine().getTemplate("com/quakearts/tools/data/generator/templates/procedureresult.vm");
	}
	
	public InputStream generateWrapper(WrapperClass wrapperClass) throws IOException, JAXBException{
		return new ByteArrayInputStream(generateWrapperAsString(wrapperClass).getBytes());
	}
	
	public String generateWrapperAsString(WrapperClass wrapperClass) 
			throws IOException, JAXBException{
		if(!wrapperClass.isValid())
			throw new IOException("Invalid procedure definition. Please check model.");
		
		Procedure procedure = wrapperClass.getProcedure();
		
		HashSet<String> imports = new HashSet<String>();
		StringBuilder callStatement = new StringBuilder();
		
		callStatement.append("{");
		if(procedure.isReturnCodeEnabled()){
			callStatement.append("?= ");
			imports.add("java.sql.Types");
		}
		callStatement.append("call ").append(procedure.getProcedureName());

		boolean isFristParam=true;
		for(Parameter parameter:procedure.getParameters()){
			if(isFristParam){
				callStatement.append(" (?");
				isFristParam=false;
			} else{
				callStatement.append(",?");
			}
			if(parameter.getJavaTypeImport()!=null&& !parameter.getJavaTypeImport().isEmpty())
				imports.add(parameter.getJavaTypeImport());
		}
		callStatement.append(procedure.getParameters().size()>0?")":"").append("}");
		
		if(!wrapperClass.isExecutor()){
			imports.add("java.sql.ResultSet");
		}
		
		for(ProcedureResult result:procedure.getProcedureResults()){
			if(result.isMultiRow()){
				imports.add("java.util.List");
				imports.add("java.util.ArrayList");
			}
			
			if(!wrapperClass.isSeparate())
				for(ResultColumn column:result.getResultColumns()){
					if(column.getJavaTypeImport()!=null && !column.getJavaTypeImport().isEmpty())
						imports.add(column.getJavaTypeImport());
				}
		}
		
		if(!wrapperClass.isMultipleSets())
			wrapperClass.setMultipleSets(procedure.getProcedureResults().size()>1);
		
		VelocityContext context = new VelocityContext();
		context.put("imports", imports);
		context.put("wrapperClass", wrapperClass);
		context.put("callStatement", callStatement);
		context.put("wrapperdata", getWrapperData(wrapperClass));
		context.put("util", getGenerationUtilMethods());
				
		StringWriter writer = new StringWriter();
		
		try {
			template.merge(context, writer);			
		} catch (Exception e) {
			throw new IOException("Exception of type " + e.getClass().getName()
					+ " was thrown. Message is " + e.getMessage()
					+ ". Exception occured whiles merging");
		}		

		return writer.toString();
	}

	public InputStream generateResultClass(WrapperClass wrapperClass, ProcedureResult result) throws IOException, JAXBException{
		return new ByteArrayInputStream(generateResultClassAsString(wrapperClass, result).getBytes());	
	}

	public String generateResultClassAsString(WrapperClass wrapperClass, ProcedureResult result) 
			throws IOException, JAXBException{
		if(!wrapperClass.isValid())
			throw new IOException("Invalid procedure definition. Please check model.");
				
		HashSet<String> imports = new HashSet<String>();
			
		for(ResultColumn column:result.getResultColumns()){
			if(column.getJavaTypeImport()!=null && !column.getJavaTypeImport().isEmpty())
				imports.add(column.getJavaTypeImport());
		}
				
		VelocityContext context = new VelocityContext();
		context.put("imports", imports);
		context.put("wrapperClass", wrapperClass);
		context.put("procedureResult", result);
		context.put("wrapperdata", getWrapperData(wrapperClass));
		context.put("util", getGenerationUtilMethods());
				
		StringWriter classDefinition = new StringWriter();
		
		try {
			resultClassTemplate.merge(context, classDefinition);			
		} catch (Exception e) {
			throw new IOException("Exception of type " + e.getClass().getName()
					+ " was thrown. Message is " + e.getMessage()
					+ ". Exception occured whiles merging");
		}		
		return classDefinition.toString();
	}
	
	private String getWrapperData(WrapperClass wrapperClass) throws JAXBException{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Marshaller marshaller = CodeGenerators.getJAXBContext().createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(wrapperClass, bos);
		return new String(bos.toByteArray()).replace("\n", "\r\n");
	}

}
