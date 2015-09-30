package com.quakearts.webtools.codegenerators.wizard.saxhandler;

import java.io.IOException;
import java.io.StringReader;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.quakearts.webtools.codegenerators.model.TagLibrary;
import com.quakearts.webtools.codegenerators.model.TagTemplate;

public class TemplateHandler extends DefaultHandler{
	private TagTemplate template = new TagTemplate();
	public TagTemplate getTemplate() {
		return template;
	}

	private StringBuilder templateOpenBuilder=new StringBuilder(), tagBuilder, templateCloseBuilder=new StringBuilder();
	private String type, tagName, templateOpenName;
	private boolean hasDefault=false, parsingTag=false, templateCloseComplete=false, templateOpenComplete=false, stopindent=false;
	private int depthLevel=0;
	
	@Override
	public InputSource resolveEntity(String publicId, String systemId)
			throws IOException, SAXException {
		
		return new InputSource(new StringReader(""));
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if(templateCloseComplete) //ignore all other contents, including closing html tag
			return;
		
		StringBuilder builder;
		
		if(parsingTag){
			builder = tagBuilder;
		}else{
			if(templateOpenComplete)
				builder=templateCloseBuilder;
			else
				builder=templateOpenBuilder;
		}
		
		for(int i=0;i<length;i++){
			char c = ch[start+i];
			if(c!='\n'&&c!='\t')
			builder.append(c);
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if(qName.equals("html")){//Parsing the html component. This contains the namespaces for the pages
			for(int i=0;i<attributes.getLength();i++){
				TagLibrary library = new TagLibrary();
				String namespace = attributes.getQName(i);
				String url = attributes.getValue(i);
				
				if(namespace.equals("xmlns"))
					continue;
				
				if(url.equals("http://java.sun.com/jsf/facelets"))
					continue;
				
				if(namespace.indexOf(':')!=-1 && namespace.indexOf(':')+1<namespace.length())
					namespace = namespace.substring(namespace.indexOf(':')+1);
				
				library.setNamespace(namespace);
				library.setUrl(url);
				template.getLibraries().add(library);
			}
		} else {//all other tags
			int idlocation;
			StringBuilder builder;
			if((idlocation=attributes.getIndex("id"))>-1){//Special case. The tag might be the marker
				String value=attributes.getValue(idlocation);
				if(value.startsWith("tag:")){
					parsingTag=true;
					templateOpenComplete=true;
					stopindent=true;
					if(tagBuilder!=null){
						template.addTagText(type,tagBuilder.toString());
					}
					if(value.length()>5){
						type = attributes.getValue(idlocation).substring(4);						
					}else{
						throw new SAXException("invalid template definition: <"+qName+">. id tag is empty");
					}
					
					if(type.equals("default"))
						hasDefault=true;
					tagName=qName+depthLevel;
					tagBuilder = new StringBuilder();
				} else if(value.equals("formopen")||value.equals("listopen")){
					templateOpenName = qName+depthLevel;
				}
			}
			if(parsingTag){
				builder=tagBuilder;
			}else{
				if(templateOpenComplete)
					builder=templateCloseBuilder;
				else
					builder=templateOpenBuilder;
			}
			int indent=0;
			if(depthLevel>0){
				if(parsingTag ||!stopindent){
					indent=3;
				}
			}			
			for(int i=0;i<depthLevel+indent;i++)
				builder.append(i==0&&(!stopindent||parsingTag) ?"\n":"").append("\t");

			if(!parsingTag && stopindent){
				stopindent=false;
			}
			depthLevel++;
			
			builder.append("<"+qName);
			for(int i=0;i<attributes.getLength();i++){
				String value=attributes.getValue(i);
				if(value.startsWith("tag:")||value.equals("formopen")||value.equals("listopen"))
					continue;

				builder.append(" ").append(attributes.getQName(i)).append("=\"").append(attributes.getValue(i)).append("\"");
			}
			builder.append(">");
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {	
		StringBuilder builder;
		if(templateCloseComplete){
			return;
		}
		if(parsingTag){
			builder=tagBuilder;
		}else{
			if(templateOpenComplete){
				builder=templateCloseBuilder;
			} else {
				builder=templateOpenBuilder;
			}
		}
		
		if(depthLevel>0)
			depthLevel--;
		int indent=0;
		if(depthLevel>=0){
			if(parsingTag ||!stopindent){
				indent=3;
			}
		}			
		for(int i=0;i<depthLevel+indent;i++)
			builder.append(i==0&&(!stopindent||parsingTag) ?"\n":"").append("\t");

		if(!parsingTag && stopindent){
			stopindent=false;
		}

		builder.append("</").append(qName).append(">");
		if((qName+depthLevel).equals(tagName)){
			parsingTag = false;
		}
		if((qName+depthLevel).equals(templateOpenName)){
			templateCloseComplete = true;
		}

	}

	@Override
	public void endDocument() throws SAXException {
		if(!hasDefault)
			throw new SAXException("Invalid template. No default beans handler specified");
		if(tagBuilder!=null){
			template.addTagText(type,tagBuilder.toString());
		}else{
			throw new SAXException("Invalid template. No beans handlers were specified");
		}

		if(templateOpenBuilder.length()==0 || templateCloseBuilder.length()==0)
			throw new SAXException("Invalid template. No form or list view opening and closing tags were specified");
		
		template.setTemplateOpen(templateOpenBuilder.toString());
		template.setTemplateClose(templateCloseBuilder.toString());
	}		
}