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
package com.quakearts.tools.data.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

import com.quakearts.tools.data.wizard.pages.AdvancedConfigurationWizardPage;
import com.quakearts.tools.data.wizard.pages.ProcedureClassWizardPage;
import com.quakearts.tools.data.wizard.pages.ProcedureResultWizardPage;
import com.quakearts.tools.data.wizard.pages.ProcedureWizardPage;

import org.eclipse.ui.*;

/**
 * This is a sample new wizard. Its role is to create a new file 
 * resources in the provided container. If the container resources
 * (a folders or a project) is selected in the workspace 
 * when the wizard is opened, it will accept it as the target
 * container. The wizard creates one file with the extension
 * "java". If a sample multi-pages editor (also available
 * as a template) is registered for the same extension, it will
 * be able to open it.
 */

public class ProcedureWrapperWizard extends AbstractWrapperWizard{
	private ProcedureClassWizardPage classPage;
	private ProcedureWizardPage procedurePage;
	private ProcedureResultWizardPage procedureResultPage;
	private AdvancedConfigurationWizardPage advancedPage;
	public static final String TITLE = "Procedure Wrapper class Generator";
	
	@Override
	public boolean canFinish() {
		if(!wrapperClass.isValid())
			return false;
		return super.canFinish();
	}

	/**
	 * Constructor for ProcedureWrapperWizard.
	 */
	public ProcedureWrapperWizard() {
		setNeedsProgressMonitor(true);
		classPage = new ProcedureClassWizardPage();
		procedurePage = new ProcedureWizardPage(wrapperClass);
		procedureResultPage = new ProcedureResultWizardPage(wrapperClass);
		advancedPage = new AdvancedConfigurationWizardPage(wrapperClass);
	}
	
	/**
	 * Adding the pages to the wizard.
	 */

	public void addPages() {
		addPage(classPage);
		addPage(procedurePage);
		addPage(procedureResultPage);
		addPage(advancedPage);
	}

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	public boolean performFinish() {
		wrapperClass.setJavaClassName(classPage.getTypeName());
		wrapperClass.setPackageName(classPage.getPackageText());
		wrapperClass.setMultipleSets(advancedPage.isMultipleSets());
		wrapperClass.setFacesType(advancedPage.isFacesType());
		wrapperClass.setUpdater(advancedPage.isUpdater());
		wrapperClass.setExecutor(advancedPage.isExecutor());
		return completeProcessing(classPage.getJavaProject().getProject(),
				classPage.getPackageFragmentRoot().getElementName());
	}
	
	/**
	 * We will accept the selection in the workbench to see if
	 * we can initialize from it.
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		classPage.init(selection);
	}
}
