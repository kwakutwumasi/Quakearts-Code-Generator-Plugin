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

public class BeanListViewPageGenerator extends GenericPageGenerator {
	
	private Template template;
	private static List<String[]> defaultListTemplates;
	
	public BeanListViewPageGenerator(){
		template = getEngine().getTemplate("com/quakearts/webtools/codegenerators/templates/beantemplate.vm");
	}
	
	@Override
	public void generate(OutputStream stream, BeanModel beanModel, String templatesrc, String templateFile) throws ParserConfigurationException, SAXException, IOException{
		generateBeanPage(stream, beanModel, templatesrc, templateFile, template);
	}
	
	@Override
	protected void loadDefaultTemplates() throws IOException, ParserConfigurationException, SAXException{
		if(defaultListTemplates == null){
			List<String[]> listTemplates= new ArrayList<String[]>();
			listTemplates.add(new String[]{"Basic Table List","baselist.template.xhtml"});
			listTemplates.add(new String[]{"Bootstrap Table List","quakearts.bootlist.template.xhtml"});
			listTemplates.add(new String[]{"Bootstrap CRUD List","quakearts.crud.list.template.xhtml"});
			listTemplates.add(new String[]{"Bootstrap Action List","quakearts.action.list.template.xhtml"});
	
			Bundle bundle = Platform.getBundle(CodeGenerators.PLUGIN_ID);
			if (bundle != null) {
				for(String[] templateDetails:listTemplates){
					URL templateUrl = bundle.getResource("com/quakearts/webtools/codegenerators/templates/"+templateDetails[1]);
					if(templateUrl !=null){
						InputStream stream = templateUrl.openStream();
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
				defaultListTemplates = listTemplates;
			}else{
				throw new IOException("Cannot get bundle for "+CodeGenerators.PLUGIN_ID);
			}
		}
	}
	
	public List<String[]> getDefaultListTemplates() throws IOException, ParserConfigurationException, SAXException {
		if(defaultListTemplates==null)
			loadDefaultTemplates();

		return defaultListTemplates;
	}
}
