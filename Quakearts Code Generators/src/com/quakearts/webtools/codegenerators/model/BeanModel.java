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
package com.quakearts.webtools.codegenerators.model;

import java.beans.BeanInfo;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BeanModel {

	private String name;
	private IdElement idElement;
	private List<BeanElement> beanElements;
	private Class<?> beanClass;
	private BeanInfo beanInfo;
	private String renderedText;
	private Map<String, Object> generatorProperties;

	BeanModel(String name, IdElement idElement, List<BeanElement> beanElements, Class<?> beanClass, BeanInfo beanInfo,
			Map<String, Object> generatorProperties) {
		setName(name);
		this.idElement = idElement;
		this.beanElements = beanElements;
		this.beanClass = beanClass;
		this.beanInfo = beanInfo;
		this.generatorProperties = generatorProperties;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if(name!=null && name.length()>1)
			this.name = new StringBuilder(name.substring(0, 1).toLowerCase())
				.append(name.substring(1)).toString();
	}
	
	public IdElement getIdElement() {
		return idElement;
	}

	public Collection<BeanElement> getBeanElements() {
		return Collections.unmodifiableCollection(beanElements);
	}

	public Class<?> getBeanClass() {
		return beanClass;
	}
	
	public String getBeanClassName(){
		return beanClass.getCanonicalName();
	}
	
	public BeanInfo getBeanInfo() {
		return beanInfo;
	}
	
	public Map<String, Object> getGeneratorProperties() {
		return generatorProperties;
	}
	
	public String getRenderedText() {
		return renderedText;
	}
	
	public void setRenderedText(String renderedText) {
		this.renderedText = renderedText;
	}
}
