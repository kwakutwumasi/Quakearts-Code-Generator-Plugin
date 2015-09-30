package com.quakearts.webtools.codegenerators.wizard.saxhandler;

import java.io.IOException;
import java.io.StringReader;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class HibernateMappingHandler extends DefaultHandler {
	
	private String fieldName;
	private boolean identity;
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if(qName.equals("id")){
			fieldName = attributes.getValue("name");
		} else if(qName.equals("generator")){
			String generatorClass = attributes.getValue("class");
			if(fieldName!=null && generatorClass !=null && generatorClass.equals("identity")){
				identity = true;
			}
		}
	}
	
	public String getFieldName() {
		return fieldName;
	}
	
	public boolean isIdentity() {
		return identity;
	}
	
	@Override
	public InputSource resolveEntity(String publicId, String systemId)
			throws IOException, SAXException {
		return new InputSource(new StringReader(""));
	}
}
