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
