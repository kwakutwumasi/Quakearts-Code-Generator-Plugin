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

import java.lang.reflect.InvocationTargetException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import com.quakearts.webtools.codegenerators.wizard.pages.BaseTemplateWizardPage;

public class BaseTemplateWizard extends Wizard implements INewWizard {
	
	protected IContainer folder;
	protected BaseTemplateWizardPage basePage;

	@Override
	public void addPages(){
		 basePage = new BaseTemplateWizardPage(folder);
		 addPage(basePage);
	}

	@Override
	public boolean performFinish() {
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {		
					monitor.beginTask("Creating "+ basePage.getFileName(), 2);
					final IFile file = basePage.getLocation().getFile(new Path(basePage.getFileName()));
					if(file.exists()){
						file.setContents(basePage.generateTemplate(), true, true, monitor);
					}else{
						file.create(basePage.generateTemplate(), true, monitor);
					}
					monitor.worked(1);
					monitor.setTaskName("Opening file for editing...");
					getShell().getDisplay().asyncExec(new Runnable() {
						public void run() {
							IWorkbenchPage page =
								PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
							try {
								IDE.openEditor(page, file, true);
							} catch (PartInitException e) {
							}
						}
					});
					monitor.worked(1);
				} catch (Exception e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		Object element = selection.getFirstElement();
		IResource resource;
		if (element instanceof IResource){
			resource = (IResource) element;
			if(resource instanceof IContainer)
				folder = (IFolder) resource;
			else {
				folder = resource.getParent();
			}
		} else if (!(element instanceof IAdaptable)){
			return;
		} else {
			IAdaptable adaptable = (IAdaptable) element;
			Object adapter = adaptable.getAdapter(IResource.class);
			resource = (IResource) adapter;
			folder = resource.getParent();
		}
	}
	@Override
	public boolean needsProgressMonitor() {
		return true;
	}
}
