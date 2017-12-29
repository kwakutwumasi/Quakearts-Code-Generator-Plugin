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
package com.quakearts.tools.generatorbase;

import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;

import com.quakearts.tools.CodeGenerators;
import com.quakearts.tools.utils.GenerationUtilMethods;

public abstract class GenericGenerator implements LogChute{

	protected Template template;
	private VelocityEngine engine;
	private static GenerationUtilMethods generationUtilMethods = new GenerationUtilMethods();

	public static GenerationUtilMethods getGenerationUtilMethods() {
		return generationUtilMethods;
	}
	
	protected VelocityEngine getEngine() {
		ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(VelocityEngine.class.getClassLoader());
		if(engine==null){
			engine = new VelocityEngine();
			
			engine.setProperty(Velocity.RUNTIME_LOG_LOGSYSTEM, this);
			engine.setProperty("resource.loader", "classpath,string");
			engine.setProperty("classpath.loader.description", "Classpath loader");
			engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
			engine.setProperty("string.loader.description", "String loader");
			engine.setProperty("string.resource.loader.class", StringResourceLoader.class.getName());

			engine.init();
		}
		Thread.currentThread().setContextClassLoader(oldClassLoader);
		return engine;
	}

	@Override
	public void init(RuntimeServices rs) throws Exception {
	}

	@Override
	public void log(int level, String message) {
		doLog(level,message,null);
	}

	@Override
	public void log(int level, String message, Throwable t) {
		doLog(level,message,t);
	}

	private void doLog(int level, String message, Throwable t) {
		if(level==LogChute.INFO_ID)
			CodeGenerators.logInfo(message);
		else if(level==LogChute.ERROR_ID){
			CodeGenerators.logError(message, t);
		} else if(level==LogChute.WARN_ID)
			CodeGenerators.logWarn(message);
	}

	@Override
	public boolean isLevelEnabled(int level) {
		if(level==LogChute.INFO_ID||level==LogChute.ERROR_ID||level==LogChute.WARN_ID)
			return true;
		
		return false;
	}
}
