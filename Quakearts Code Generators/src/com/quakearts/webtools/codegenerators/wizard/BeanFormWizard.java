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
package com.quakearts.webtools.codegenerators.wizard;

import com.quakearts.webtools.codegenerators.BeanFormPageGenerator;
import com.quakearts.webtools.codegenerators.wizard.pages.BeanFormWizardPage;

public class BeanFormWizard extends BeanBaseWizard {
	
	public BeanFormWizard() {
		setWindowTitle("New beans form");
		pageGenerator = new BeanFormPageGenerator();
	}

	@Override
	public void addPages() {
		formPage = new BeanFormWizardPage(project, folder, pageGenerator);
		addPage(formPage);
	}
	
}
