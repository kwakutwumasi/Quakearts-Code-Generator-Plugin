package com.quakearts.webtools.codegenerators.wizard;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
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
import org.xml.sax.SAXException;

import com.quakearts.webtools.codegenerators.GenericPageGenerator;
import com.quakearts.webtools.codegenerators.wizard.pages.BeanBaseWizardPage;

public abstract class BeanBaseWizard extends Wizard implements INewWizard {
	
	protected IProject project;
	protected IContainer folder;
	protected BeanBaseWizardPage formPage;
	protected GenericPageGenerator pageGenerator;

	@Override
	public abstract void addPages();

	@Override
	public boolean performFinish() {
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {		
					monitor.beginTask("Creating "+ formPage.getFileName(), 2);
					final IFile file = formPage.getProject().getFile(formPage.getLocation().getProjectRelativePath().append(formPage.getFileName()));
					if(file.exists()){
						file.setContents(generateForm(), true, true, monitor);
					}else{
						file.create(generateForm(), true, monitor);
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
					throw new InvocationTargetException(e,"Exception of type " + e.getClass().getName()
							+ " was thrown. Message is " + e.getMessage()
							+ ". Exception occured whiles generating form element.");
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

	private InputStream generateForm()
			throws ParserConfigurationException, SAXException, IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		if(formPage.useCustomizedTemplate()){
			pageGenerator.generateCustom(bos, formPage.getModel(), formPage.getUiTemplateSrc(), formPage.getTemplate(),formPage.getCustomTemplatefileName());
		} else {
			pageGenerator.generate(bos, formPage.getModel(), formPage.getUiTemplateSrc(), formPage.getTemplate());
		}
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		return bis;
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		Object element = selection.getFirstElement();
		IResource resource;
		if (element instanceof IResource){
			resource = (IResource) element;
			if(resource instanceof IContainer)
				folder = (IContainer) resource;
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
		
		project = resource.getProject();
	}

}
