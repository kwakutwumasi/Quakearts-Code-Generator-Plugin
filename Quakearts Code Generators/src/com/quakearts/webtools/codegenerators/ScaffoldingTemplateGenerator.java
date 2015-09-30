package com.quakearts.webtools.codegenerators;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.quakearts.tools.CodeGenerators;
import com.quakearts.tools.generatorbase.GenericGenerator;
import com.quakearts.webtools.codegenerators.model.BeanElement;
import com.quakearts.webtools.codegenerators.model.BeanModel;
import com.quakearts.webtools.codegenerators.model.Resource;
import com.quakearts.webtools.codegenerators.model.Scaffolding;
import com.quakearts.webtools.codegenerators.model.Template;
import com.quakearts.webtools.codegenerators.model.TemplateGroup;
import static com.quakearts.tools.CodeGenerators.*;

public class ScaffoldingTemplateGenerator extends GenericGenerator {
	private static HashMap<String, String> forbiddenIds = new HashMap<String, String>();
	private static String[] predefinedScaffoldingTemplates;
	private static final String EMPTY ="";
	private static HashMap<String, Scaffolding> scaffoldingTemplates = new HashMap<String, Scaffolding>();

	public ScaffoldingTemplateGenerator() {
		getEngine();
	}
	
	public InputStream generatePage(VelocityContext context, String templateName){
		context.put("util", this);
		StringWriter writer = new StringWriter();
		org.apache.velocity.Template template = getEngine().getTemplate(templateName);
		if(template!=null){
			try {
				template.merge(context, writer);				
			} catch (Throwable e) {
				CodeGenerators.logError("Exception of type " + e.getClass().getName() + " was thrown. Message is " + e.getMessage()
						+ ". Exception occured whiles generating "+templateName, e);
			}
		}
		return new ByteArrayInputStream(writer.toString().getBytes());
	}
	
	public boolean verifyMethod(BeanModel model, String methodName){
		for(Method method:model.getBeanClass().getMethods()){
			if(method.getName().equals(methodName))
				return true;
		}
		return false;
	}
	
	public boolean verifyAnnotation(BeanModel model, String annotationName){
		for(Annotation annotation:model.getBeanClass().getAnnotations()){
			if(annotation.annotationType().getCanonicalName().equals(annotation))
				return true;
		}
		return false;
	}
	
	public boolean verifyChildOf(BeanModel model, String parentName){
		Class<?> parent = model.getBeanClass().getSuperclass();
		int count =0;
		do {
			if(parent.getCanonicalName().equals(parentName))
				return true;
			count++;
		} while((parent = parent.getSuperclass())!=null && count<100);
		return false;
	}
	
	public Collection<BeanElement> findElementsOfType(String types, BeanModel beanModel){
		ArrayList<BeanElement> foundElements = new ArrayList<>();
		HashMap<String, String> typeMap = new HashMap<>();
		String[] typeArray = types.split("|");
		for(String typeString:typeArray){
			typeMap.put(typeString, "");
		}
		
		for(BeanElement element:beanModel.getBeanElements()){
			if(typeMap.get(element.getElementClass())!=null){
				foundElements.add(element);
			}
		}
		
		return foundElements;
	}
	
	public String[] getPredefinedScaffoldingTemplates() {
		if(predefinedScaffoldingTemplates==null){
			
			Bundle bundle = Platform.getBundle(PLUGIN_ID);
			Properties props = new Properties();
			InputStream is = null;
			Unmarshaller unmarshaller;
			try {
				unmarshaller = getJAXBContext().createUnmarshaller();
			} catch (JAXBException e) {
				logError("Could not load predifined scaffolding entries", e);
				return null;
			}
			
			try {
				is = bundle.getResource("com/quakearts/webtools/codegenerators/scaffolding/predefined.properties").openStream();
				props.load(is);
				String[] templates = props.getProperty("templates").split(";");
				ArrayList<String> tempList = new ArrayList<String>();
				for(String templateEntry:templates){
					String[] nameValue = templateEntry.split("/",2);
					InputStream fis = bundle.getResource("com/quakearts/webtools/codegenerators/scaffolding/"+nameValue[0]).openStream();
					try {
						Object scaffoldingObject= unmarshaller.unmarshal(fis);
						if(scaffoldingObject instanceof Scaffolding){
							tempList.add(nameValue[1]);
							
							Scaffolding scaffolding=(Scaffolding) scaffoldingObject;
							forbiddenIds.put(scaffolding.getId(), EMPTY);
							
							for(Resource resource:scaffolding.getResources()){
								if(resource.isTemplate()){
									InputStream ris = bundle.getResource("com/quakearts/webtools/codegenerators/scaffolding/"
											+resource.getLocation()).openStream();
									try {
										loadTemplate(ris, scaffolding.getId()+"/"+resource.getLocation());
									} finally {
										ris.close();
									}
								}
							}
							
							for(TemplateGroup templateGroup:scaffolding.getTemplateGroups()){
								for(Template template:templateGroup.getTemplates()){
									InputStream pis = bundle.getResource("com/quakearts/webtools/codegenerators/scaffolding/"
											+template.getLocation()).openStream();
									try {
										loadTemplate(pis, scaffolding.getId() + "/" + template.getLocation());
									} finally {
										try {
											pis.close();
										} catch (Exception e) {
										}
									}
								}
							}
							scaffoldingTemplates.put(nameValue[1], (Scaffolding) scaffoldingObject);
						}
					} finally {
						try {
							fis.close();
						} catch (Exception e) {
						}
					}
				}
				
				predefinedScaffoldingTemplates = tempList.toArray(new String[tempList.size()]);
			} catch (Exception e) {
				logError("Could not load predifined scaffolding entries", e);
			} finally {
				try {
					is.close();
				} catch (Exception e2) {
				}
			}
			
		}
		return predefinedScaffoldingTemplates;
	}
	
	public void loadTemplate(InputStream in, String name) throws IOException{
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line;
		while((line=reader.readLine())!=null){
			builder.append(line).append("\r\n");
		}
		StringResourceLoader.getRepository().putStringResource(name,
				builder.toString());
	}
	
	public boolean isInValidId(String scaffoldingId){
		return forbiddenIds.containsKey(scaffoldingId);
	}
	
	public HashMap<String, Scaffolding> getScaffoldingTemplates() {
		return scaffoldingTemplates;
	}
	
}