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

import javax.xml.bind.JAXBException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IWorkbench;

import com.quakearts.tools.data.wizard.pages.AdvancedConfigurationWizardPage;
import com.quakearts.tools.data.wizard.pages.ProcedureResultWizardPage;
import com.quakearts.tools.data.wizard.pages.ProcedureXMLSelectionWizardPage;

public class ProcedureResultsWizard extends AbstractWrapperWizard {

	private IProject project;
	private IPackageFragmentRoot sourceFolder;
	private ProcedureResultWizardPage resultPage;
	private AdvancedConfigurationWizardPage configurationPage;
	private ProcedureXMLSelectionWizardPage xmlSelectionPage;
	
	@Override
	public void addPages() {
		if(project==null||sourceFolder==null){
			xmlSelectionPage = new ProcedureXMLSelectionWizardPage();
			addPage(xmlSelectionPage);
		} else{
			resultPage = new ProcedureResultWizardPage(wrapperClass);
			configurationPage = new AdvancedConfigurationWizardPage(wrapperClass);
			addPage(resultPage);
			addPage(configurationPage);
		}
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if(page.getName().equals(ProcedureXMLSelectionWizardPage.PAGENAME)&&project==null&&sourceFolder==null){
			try {
				setWrapperClass(((ProcedureXMLSelectionWizardPage)page).getWrapperFile());
				resultPage = new ProcedureResultWizardPage(wrapperClass);
				configurationPage = new AdvancedConfigurationWizardPage(wrapperClass);
				project = ((ProcedureXMLSelectionWizardPage)page).getProject();
				sourceFolder = ((ProcedureXMLSelectionWizardPage)page).getSourceFolder();
				addPage(resultPage);
				addPage(configurationPage);
			} catch (Exception e) {
				MessageDialog.openError(getShell(), "Error reading xml", "Exception of type " + e.getClass().getName()
						+ " was thrown. Message is " + e.getMessage()
						+ ". Exception occured whiles marshalling "+((ProcedureXMLSelectionWizardPage)page).getWrapperFile());
				return page;
			}
		}
		return super.getNextPage(page);
	}
	
	

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		Object element = selection.getFirstElement();
		if (element instanceof ICompilationUnit){
			ICompilationUnit javaElement = (ICompilationUnit)element;
	
			IJavaElement sourceElement = javaElement.getParent();
			while(sourceElement!=null){
				if(sourceElement.getElementType() == IJavaElement.PACKAGE_FRAGMENT_ROOT){
					break;
				}
				sourceElement = sourceElement.getParent();
			}
			if(sourceElement==null)
				return;
			
			try {
				setWrapperClass(javaElement);
			} catch (JAXBException e) {
				MessageDialog.openError(getShell(), "Cannot get wrapper class", "Exception of type " + e.getClass().getName()
						+ " was thrown. Message is " + e.getMessage()
						+ ". Exception occured whiles reading "+javaElement.getResource().getProjectRelativePath().toString());
				return;
			} catch (CoreException e) {
				MessageDialog.openError(getShell(), "Cannot get wrapper class", "Exception of type " + e.getClass().getName()
						+ " was thrown. Message is " + e.getMessage()
						+ ". Exception occured whiles reading "+javaElement.getResource().getProjectRelativePath().toString());
				return;
			}
			
			sourceFolder = (IPackageFragmentRoot) sourceElement;
			project = javaElement.getResource().getProject();
		}
	}

	@Override
	public boolean performFinish() {
		wrapperClass.setMultipleSets(configurationPage.isMultipleSets());
		wrapperClass.setFacesType(configurationPage.isFacesType());
		wrapperClass.setUpdater(configurationPage.isUpdater());
		wrapperClass.setExecutor(configurationPage.isExecutor());
		return completeProcessing(project, sourceFolder.getElementName());
	}

}
