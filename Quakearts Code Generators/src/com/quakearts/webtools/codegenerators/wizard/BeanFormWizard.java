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
