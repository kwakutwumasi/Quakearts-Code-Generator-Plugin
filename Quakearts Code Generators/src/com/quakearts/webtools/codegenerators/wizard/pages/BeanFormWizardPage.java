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
package com.quakearts.webtools.codegenerators.wizard.pages;

import java.io.IOException;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.xml.sax.SAXException;
import com.quakearts.webtools.codegenerators.BeanFormPageGenerator;
import com.quakearts.webtools.codegenerators.GenericPageGenerator;

public class BeanFormWizardPage extends BeanBaseWizardPage {
	
	public BeanFormWizardPage(IProject project, IContainer folder, GenericPageGenerator pageGenerator) {
		super("beanFormPage", "Create a new beans form pages", "Select a beans and a template to create a new beans form....",project, folder, pageGenerator);
	}

	@Override
	protected List<String[]> getDefaultTemplates()
			throws ParserConfigurationException, SAXException, IOException {
		return ((BeanFormPageGenerator)pageGenerator).getDefaultFormTemplates();
	}

}
