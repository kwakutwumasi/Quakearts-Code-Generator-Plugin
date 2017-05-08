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
package com.quakearts.webtools.codegenerators.wizard.saxhandler;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class FacesConfigHandler extends DefaultHandler {
	private List<String[]> managedBeans = new ArrayList<String[]>();
	private String[] currentManagedBean = new String[2];
	private boolean getBeanName, getBeanClass;
	
	public List<String[]> getManagedBeans() {
		return managedBeans;
	}

	@Override
	public InputSource resolveEntity(String publicId, String systemId)
			throws IOException, SAXException {
		return new InputSource(new StringReader(""));
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if(qName.contains("managed-beans-name")){
			getBeanName = true;
			getBeanClass = false;
		} else if(qName.contains("managed-beans-class")){
			getBeanName = false;
			getBeanClass = true;
		} else{
			getBeanName = false;
			getBeanClass = false;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		String contents = new String(ch, start, length);
		if(contents.trim().isEmpty())
			return;
		
		if(getBeanClass){
			if(currentManagedBean[1]!=null)
				currentManagedBean[1]=currentManagedBean[1]+" "+contents;
			else
				currentManagedBean[1]=contents;
			
			managedBeans.add(currentManagedBean);
			currentManagedBean = new String[2];
		} else if(getBeanName){
			if(currentManagedBean[0]!=null)
				currentManagedBean[0]=currentManagedBean[0]+" "+contents;
			else
				currentManagedBean[0]=contents;
		}
	}
}
