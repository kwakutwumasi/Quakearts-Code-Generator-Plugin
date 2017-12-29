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
package com.quakearts.tools;

import java.util.HashMap;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.SAXParserFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wb.swt.ResourceManager;
import org.osgi.framework.BundleContext;

import com.quakearts.tools.data.model.WrapperClass;
import com.quakearts.tools.utils.ProjectClassLoader;
import com.quakearts.tools.web.model.BeanModel;
import com.quakearts.tools.web.model.Scaffolding;

/**
 * The activator class controls the plug-in life cycle
 */
public class CodeGenerators extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.quakearts.plugins.codegenerators"; //$NON-NLS-1$
	private static Image beanImage, calendarImage, databasesImage, databaseImage, scriptImage, textfieldImage,tagImage,textColumnsImage, schemaImage, folderImage;

	private static ImageDescriptor quakeartDescriptor;
	// The shared instance
	private static CodeGenerators plugin;
	private static SAXParserFactory factory = SAXParserFactory.newInstance();
	private static JAXBContext context;
	private static HashMap<String, BeanModel> beanModels = new HashMap<String, BeanModel>();
	private static final HashMap<String, ProjectClassLoader> classLoaders = new HashMap<>();

	/**
	 * The constructor
	 */
	public CodeGenerators() {
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		loadImages();
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		ResourceManager.dispose();
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static CodeGenerators getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	public static void logInfo(String message){
		log(Status.INFO, Status.OK, message, null);
	}

	public static void logWarn(String message){
		log(Status.WARNING, Status.OK, message, null);
	}
	
	public static void logError(String message, Throwable e){
		log(Status.ERROR, Status.OK, message, e);
	}
	
	private static void log(int severity, int code, String message, Throwable e){
		if(plugin!=null)
			plugin.getLog().log(new Status(severity, PLUGIN_ID, code, message, e));
	}

	public static ImageDescriptor getQuakeartDescriptor() {
		if(quakeartDescriptor==null)
			quakeartDescriptor=ResourceManager.getPluginImageDescriptor(PLUGIN_ID, "icons/quake-arts.png");
		
		return quakeartDescriptor;
	}
	
	public static Image getCalendarImage() {
		return calendarImage;
	}

	public static Image getDatabasesImage() {
		return databasesImage;
	}

	public static Image getDatabaseImage() {
		return databaseImage;
	}

	public static Image getScriptImage() {
		return scriptImage;
	}

	public static Image getTextfieldImage() {
		return textfieldImage;
	}

	public static Image getTagImage(){
		return tagImage;
	}
	
	public static Image getTextColumnsImage() {
		return textColumnsImage;
	}

	public static Image getSchemaImage() {
		return schemaImage;
	}

	public static Image getBeanImage() {
		return beanImage;
	}
	
	public static Image getFolderImage() {
		return folderImage;
	}

	public static CodeGenerators getPlugin() {
		return plugin;
	}
	
	public static SAXParserFactory getFactory() {
		return factory;
	}

	private static void loadImages(){
		if(calendarImage==null || calendarImage.isDisposed())
			calendarImage = ResourceManager.getPluginImage(PLUGIN_ID,"icons/calendar_1.png");
		if(databaseImage==null || databaseImage.isDisposed())
			databaseImage = ResourceManager.getPluginImage(PLUGIN_ID,"icons/database.png");
		if(databasesImage==null || databasesImage.isDisposed())
			databasesImage = ResourceManager.getPluginImage(PLUGIN_ID,"icons/databases.png");
		if(scriptImage==null || scriptImage.isDisposed())
			scriptImage = ResourceManager.getPluginImage(PLUGIN_ID,"icons/script.png");
		if(textfieldImage==null || textfieldImage.isDisposed())
			textfieldImage = ResourceManager.getPluginImage(PLUGIN_ID,"icons/textfield.png");
		if(tagImage==null || tagImage.isDisposed())
			tagImage = ResourceManager.getPluginImage(PLUGIN_ID,"icons/tag.png");
		if(textColumnsImage==null || textColumnsImage.isDisposed())
			textColumnsImage = ResourceManager.getPluginImage(PLUGIN_ID,"icons/text_columns.png");
		if(schemaImage==null || schemaImage.isDisposed())
			schemaImage = ResourceManager.getPluginImage(PLUGIN_ID,"icons/schema.png");
		if(beanImage==null|| beanImage.isDisposed())
			beanImage = ResourceManager.getPluginImage(PLUGIN_ID, "icons/bean.png");
		if(folderImage==null || folderImage.isDisposed())
			folderImage= ResourceManager.getPluginImage(PLUGIN_ID, "icons/folder.png");
	}
	
	public static String relativePath(String[] selectedSegments, String[] folderSegments){
		StringBuilder builder = new StringBuilder();
		int i=0;
		while(i<selectedSegments.length&&i<folderSegments.length){
			if(!selectedSegments[i].equals(folderSegments[i]))
				break;
			i++;
		}
		for(int k=folderSegments.length-1;k>=i;k--){
			builder.append("../");
		}
		for(int k=i;k<selectedSegments.length;k++)
			builder.append(selectedSegments[k]).append("/");
		return builder.toString();
	}
	
	public static JAXBContext getJAXBContext() throws JAXBException{
		if(context==null){
			context = JAXBContext.newInstance(WrapperClass.class,Scaffolding.class);
		}
		return context;
	}

	public static HashMap<String, BeanModel> getBeanModels() {
		return beanModels;
	}
	
	public static ProjectClassLoader getProjectClassLoader(IProject project){
		ProjectClassLoader classLoader = classLoaders.get(project.getFullPath().toOSString());
		if(classLoader == null){
			classLoader = new ProjectClassLoader(project, CodeGenerators.class.getClassLoader());
			classLoaders.put(project.getFullPath().toOSString(), classLoader);
		}
		
		return classLoader;
	}
	
	public static void clearCache(){
		classLoaders.clear();
		beanModels.clear();
	}

}
