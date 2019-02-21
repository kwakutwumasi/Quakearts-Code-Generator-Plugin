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
package com.quakearts.tools.web.generator;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;

import com.quakearts.tools.CodeGenerators;
import com.quakearts.tools.generatorbase.GenericGenerator;
import com.quakearts.tools.web.context.ScaffoldingContext;
import com.quakearts.tools.web.model.BeanModel;
import com.quakearts.tools.web.model.PropertyEntry;
import com.quakearts.tools.web.model.Scaffolding;

public class ScaffoldingTemplateGenerator extends GenericGenerator {

	private VelocityContext context;

	public ScaffoldingTemplateGenerator() {
		getEngine();
	}
	
	public void loadTemplate(String templateName, String templateBody) {
		StringResourceLoader.getRepository().putStringResource(templateName,
				templateBody);
	}
	
	public boolean isNotLoaded(String name) {
		return StringResourceLoader.getRepository()
		.getStringResource(name) == null;
	}
	
	public void populateContext(Scaffolding scaffolding, ScaffoldingContext scaffoldingContext) {
		context = new VelocityContext();
		
		context.put("beanModels", scaffoldingContext.getBeanModels());
		context.put("scaffolding", scaffolding);
		context.put("nameModelMapping", scaffoldingContext.getNameModelMappings());
		context.put("classModelMapping", scaffoldingContext.getClassModelMappings());
		
		if(scaffolding.getProperties()!=null)
			for(PropertyEntry entry:scaffolding.getProperties().getEntries()){
				context.put(entry.getProperty(), entry.getValue());
			}
		
		context.put("util", getGenerationUtilMethods());
	}
	
	public InputStream generatePage(String templateName, BeanModel beanModel){
		return new ByteArrayInputStream(generatePageAsString(templateName, beanModel).getBytes());
	}
	
	public String generatePageAsString(String templateName, BeanModel beanModel) {
		context.put("beanModel", beanModel);
		StringWriter writer = new StringWriter();
		Template template = getEngine().getTemplate(templateName);
		if(template!=null){
			try {
				template.merge(context, writer);				
			} catch (Throwable e) {
				CodeGenerators.logError("Exception of type " + e.getClass().getName() + " was thrown. Message is " + e.getMessage()
						+ ". Exception occured whiles generating "+templateName, e);
			}
		}
		
		return writer.toString();
	}
}
