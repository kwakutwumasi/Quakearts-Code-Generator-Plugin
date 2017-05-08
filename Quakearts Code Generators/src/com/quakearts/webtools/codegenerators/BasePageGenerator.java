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

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Collection;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import com.quakearts.tools.generatorbase.GenericGenerator;

public class BasePageGenerator extends GenericGenerator {
	Template template;
	
	public BasePageGenerator() {
		template = getEngine().getTemplate("com/quakearts/webtools/codegenerators/templates/basepagetemplate.vm");
	}
	
	public void generateBasePage(OutputStream stream, String defaultTitle, Collection<String> cssFiles,Collection<String> jsFiles) throws IOException{
		VelocityContext context = new VelocityContext();
		
		context.put("defaulttitle",defaultTitle);
		context.put("jsFiles",jsFiles);
		context.put("cssFiles",cssFiles);
		
		StringWriter writer = new StringWriter();
		
		template.merge(context, writer);
		
		stream.write(writer.toString().getBytes());
	}

}
