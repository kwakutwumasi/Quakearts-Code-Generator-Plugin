/*******************************************************************************
 * Copyright (C) 2017 Kwaku Twumasi-Afriyie <kwaku.twumasi@quakearts.com>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Kwaku Twumasi-Afriyie <kwaku.twumasi@quakearts.com> - initial API and implementation
 ******************************************************************************/
package com.quakearts.webtools.codegenerators;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.xml.sax.SAXException;
import com.quakearts.tools.CodeGenerators;
import com.quakearts.tools.generatorbase.GenericGenerator;
import com.quakearts.webtools.codegenerators.model.BeanElement;
import com.quakearts.webtools.codegenerators.model.BeanModel;
import com.quakearts.webtools.codegenerators.model.ResolvedText;
import com.quakearts.webtools.codegenerators.model.TagLibrary;
import com.quakearts.webtools.codegenerators.model.TagTemplate;
import com.quakearts.webtools.codegenerators.wizard.saxhandler.TemplateHandler;

public abstract class GenericPageGenerator extends GenericGenerator {
	private static final Map<String, TagTemplate> map = new HashMap<String, TagTemplate>();
	
	public void loadTagTemplate(String templateFile) throws ParserConfigurationException, SAXException, IOException{
		FileInputStream stream  = new FileInputStream(templateFile);
		try{
			parseTemplate(stream, templateFile);
		}finally{
			stream.close();
		}
	}

	protected abstract void loadDefaultTemplates() throws IOException, ParserConfigurationException, SAXException;

	protected static void parseTemplate(InputStream stream, String templateFile) throws ParserConfigurationException, SAXException, IOException{
		SAXParser parser = CodeGenerators.getFactory().newSAXParser();
		TemplateHandler handler = new TemplateHandler();
		parser.parse(stream, handler);
		
		map.put(templateFile, handler.getTemplate());
	}
	
	public boolean isLoaded(String templateFile){
		return map.containsKey(templateFile);
	}
		
	protected void prepare(BeanModel model, ResolvedText resolvedText, String templateFile, VelocityContext context, Collection<TagLibrary> libraries) throws ParserConfigurationException, SAXException, IOException{
		TagTemplate tagTemplate = map.get(templateFile);
		if(tagTemplate == null){
			loadTagTemplate(templateFile);
			tagTemplate = map.get(templateFile);
			if(tagTemplate == null)
				throw new IOException("No template with name "+templateFile+" has been loaded.");
		}
		
		libraries.addAll(tagTemplate.getLibraries());
		
		VelocityContext prepareContext = new VelocityContext(context);
		prepareContext.put("name", model.getName());
		
		resolvedText.setOpenText(resolve(templateFile+".open", tagTemplate.getTemplateOpen(), prepareContext));
		resolvedText.setCloseText(resolve(templateFile+".close", tagTemplate.getTemplateClose(), prepareContext));
				
		for(BeanElement element: model.getBeanElements()){
			prepareContext.put("value", element.getValue());
			if(tagTemplate.isDefault(element.getElementClass())){
				element.setRenderedText(resolve(templateFile+".default", tagTemplate.getTagText(element.getElementClass()), prepareContext));
			} else {
				element.setRenderedText(resolve(templateFile+"."+element.getElementClass(), tagTemplate.getTagText(element.getElementClass()), prepareContext));
			}
		}
	}
	
	private String resolve(String templateFile, String templateBody, Context prepareContext){
		StringResourceRepository repository = StringResourceLoader.getRepository();
		if(repository.getStringResource(templateFile)==null)
			repository.putStringResource(templateFile, templateBody);
		
		StringWriter writer = new StringWriter();
		Template template = getEngine().getTemplate(templateFile);
		template.merge(prepareContext, writer);
		
		return writer.toString();
	}

	protected void generateBeanPage(OutputStream stream, BeanModel beanModel, String templatesrc, String templateFile, Template template) throws ParserConfigurationException, SAXException, IOException{
		VelocityContext context = new VelocityContext();
		
		context.put("beanModel",beanModel);
		context.put("util", this);
		ResolvedText resolvedText = new ResolvedText();
		HashSet<TagLibrary> tagLibraries = new HashSet<TagLibrary>();
		prepare(beanModel, resolvedText, templateFile, context, tagLibraries);
		
		context.put("taglibraries",tagLibraries);
		context.put("templatesrc",templatesrc);
		context.put("textopen",resolvedText.getOpenText());
		context.put("textclose",resolvedText.getCloseText());
		
		StringWriter stringWriter = new StringWriter();
		
		template.merge(context, stringWriter);
		
		stream.write(stringWriter.toString().getBytes());
	}
	
	public abstract void generate(OutputStream stream, BeanModel beanModel, String uiTemplateSrc, String templateFile) throws ParserConfigurationException, SAXException, IOException;
	
	public void generateCustom(OutputStream stream, BeanModel beanModel, String uiTemplateSrc, String templateFile,String customTemplateFile) throws ParserConfigurationException, SAXException, IOException{
		Template customTemplate = getEngine().getTemplate("custom_template/"+customTemplateFile);
		generateBeanPage(stream, beanModel, uiTemplateSrc, templateFile,customTemplate);
	}
	
	public void loadCustomTemplate(String file, String name) throws IOException{
		if(file!=null && !file.isEmpty()){
			StringResourceRepository repository = StringResourceLoader.getRepository();
			BufferedReader reader = new BufferedReader(new FileReader(file));
			StringBuilder builder = new StringBuilder();
			String line;
			try{
				while ((line=reader.readLine())!=null) {
					builder.append(line);
				}
			}finally{
				reader.close();
			}
			try {
				repository.removeStringResource("custom_template/"+name);
			} catch (Exception e) {
			}
			repository.putStringResource("custom_template/"+name, builder.toString());
		}
	}
	
	public String toLabel(String camelCaseString){
		if(camelCaseString==null)
			return "";
		
		char[] chars = camelCaseString.toCharArray();
		StringBuilder label = new StringBuilder();
		boolean firstChar = true;
		for(char c:chars){
			if(firstChar){
				label.append(Character.toUpperCase(c));
				firstChar = false;
			} else {
				if(Character.isUpperCase(c))
					label.append(' ');
				label.append(c);
			}
		}
		return label.toString();
	}
}
