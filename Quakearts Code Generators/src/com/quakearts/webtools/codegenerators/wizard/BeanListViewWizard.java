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

import com.quakearts.webtools.codegenerators.BeanListViewPageGenerator;
import com.quakearts.webtools.codegenerators.wizard.pages.BeanListViewWizardPage;

public class BeanListViewWizard extends BeanBaseWizard {
	
	public BeanListViewWizard() {
		setWindowTitle("New beans list view");
		pageGenerator = new BeanListViewPageGenerator();
	}

	@Override
	public void addPages() {
		formPage = new BeanListViewWizardPage(project, folder, pageGenerator);
		addPage(formPage);
	}	
}
