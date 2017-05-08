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
package com.quakearts.webtools.codegenerators;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import org.apache.velocity.Template;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.xml.sax.SAXException;

import com.quakearts.tools.CodeGenerators;
import com.quakearts.webtools.codegenerators.model.BeanModel;

public class BeanFormPageGenerator extends GenericPageGenerator {
	
	private Template template;
	private static List<String[]> defaultFormTemplates;

	public BeanFormPageGenerator(){
		template = getEngine().getTemplate("com/quakearts/webtools/codegenerators/templates/beantemplate.vm");
	}
	
	public void generate(OutputStream stream, BeanModel beanModel, String uiTemplateSrc, String templateFile) throws ParserConfigurationException, SAXException, IOException{
		generateBeanPage(stream, beanModel, uiTemplateSrc, templateFile, template);
	}
	
	protected void loadDefaultTemplates() throws IOException, ParserConfigurationException, SAXException{
		if(defaultFormTemplates == null){
			List<String[]> formTemplates= new ArrayList<String[]>();
			formTemplates.add(new String[]{"Basic Bootstrap Form","baseform.template.xhtml"});
			formTemplates.add(new String[]{"Procedure Form (Procedure Wrapper)","procedure.form.template.xhtml"});
			formTemplates.add(new String[]{"Procedure AJAX Form (Procedure Wrapper)","procedure.form.ajax.template.xhtml"});
			formTemplates.add(new String[]{"Bootstrap CRUD Form","quakearts.crud.form.template.xhtml"});
			formTemplates.add(new String[]{"Bootstrap CRUD AJAX Form","quakearts.crud.form.ajax.template.xhtml"});
			formTemplates.add(new String[]{"Bootstrap Pills CRUD Form","quakearts.pillsform.crud.template.xhtml"});
			formTemplates.add(new String[]{"Bootstrap Pills CRUD AJAX Form","quakearts.pillsform.crud.ajax.template.xhtml"});
			formTemplates.add(new String[]{"Bootstrap Pills Form","quakearts.pillsform.template.xhtml"});
	
			Bundle bundle = Platform.getBundle(CodeGenerators.PLUGIN_ID);
			if (bundle != null) {
				for(String[] templateDetails:formTemplates){
					InputStream stream;
					URL url = bundle.getResource("com/quakearts/webtools/codegenerators/templates/"+templateDetails[1]);
					if(url!=null){
						stream = url.openStream();
						try {
							parseTemplate(stream, templateDetails[1]);
						} finally {
							try {
								stream.close();
							} catch (Exception e) {
							}
						}
					}
				}
				defaultFormTemplates = formTemplates;
			}else{
				throw new IOException("Cannot get bundle for "+CodeGenerators.PLUGIN_ID);
			}
		}
	}

	public List<String[]> getDefaultFormTemplates() throws IOException, ParserConfigurationException, SAXException {
		if(defaultFormTemplates==null)
			loadDefaultTemplates();
		
		return defaultFormTemplates;
	}

}
