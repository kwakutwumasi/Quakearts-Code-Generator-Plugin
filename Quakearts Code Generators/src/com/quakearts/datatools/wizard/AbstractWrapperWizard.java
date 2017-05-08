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
package com.quakearts.datatools.wizard;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.quakearts.datatools.abstraction.ProcedureResult;
import com.quakearts.datatools.abstraction.WrapperClass;
import com.quakearts.datatools.generator.WrapperGenerator;
import com.quakearts.tools.CodeGenerators;

public abstract class AbstractWrapperWizard extends Wizard implements INewWizard {
	protected WrapperClass wrapperClass = new WrapperClass();
	protected WrapperGenerator generator = new WrapperGenerator();

	protected void setWrapperClass(ICompilationUnit file) throws JAXBException, CoreException{
		if(file.exists()){
			String source = file.getSource();
			String wrapperData=source.substring(source.lastIndexOf("<?xml"),source.lastIndexOf("]*/"));
			Unmarshaller unmarshaller = CodeGenerators.getJAXBContext().createUnmarshaller();
			wrapperClass = (WrapperClass) unmarshaller.unmarshal(new StringReader(wrapperData));
		}
	}

	protected boolean completeProcessing(final IProject project, final String packageFragmentRoot){
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					monitor.beginTask("Creating " + wrapperClass.getJavaClassName() + ".java",
							2 + (wrapperClass.isSeparate() ? 
									wrapperClass.getProcedure().getProcedureResults().size(): 0));
					StringBuilder fileNameBuilder = new StringBuilder();
					String[] packageSegments = wrapperClass.getPackageName().split("\\.");
					String[] segments = new String[1+packageSegments.length];
					segments[0] = packageFragmentRoot;
					System.arraycopy(packageSegments, 0, segments, 1, packageSegments.length);
					
					IFolder folder = null;
					for(String segment:segments){
						fileNameBuilder.append(segment).append(IPath.SEPARATOR);
						folder = project.getFolder(fileNameBuilder.toString());
						if(!folder.exists())
							folder.create(true, true, monitor);
					}
					
					final IFile wrapperFile;
					if(folder!=null)
						wrapperFile = folder.getFile(wrapperClass.getJavaClassName()+".java");
					else 
						wrapperFile = project.getFile(wrapperClass.getJavaClassName()+".java");
										
					InputStream stream = null;
					try {
						stream = generator.generateWrapper(wrapperClass);
						if (wrapperFile.exists()) {
							wrapperFile.setContents(stream, true, true, monitor);
						} else {
							wrapperFile.create(stream, true, monitor);
						}
						stream.close();
					} catch (IOException e) {
						throwCoreException("Exception of type " + e.getClass().getName()
								+ " was thrown. Message is " + e.getMessage()
								+ ". Exception occured whiles writing to file");
					} catch (JAXBException e) {
						throwCoreException("Exception of type " + e.getClass().getName()
								+ " was thrown. Message is " + e.getMessage()
								+ ". Exception occured whiles writing to file");
					} finally {
						try {
							stream.close();
						} catch (Exception e2) {
						}
					}
					monitor.worked(1);

					if(wrapperClass.isSeparate()){
						IFile file;
						for(ProcedureResult result:wrapperClass.getProcedure().getProcedureResults()){
							if(folder!=null)
								file = folder.getFile(wrapperClass.getJavaClassName()+".java");
							else 
								file = project.getFile(wrapperClass.getJavaClassName()+".java");
												
							stream = null;
							try {
								stream = generator.generateResultClass(wrapperClass, result);;
								if (file.exists()) {
									file.setContents(stream, true, true, monitor);
								} else {
									file.create(stream, true, monitor);
								}
								stream.close();
							} catch (IOException e) {
								throwCoreException("Exception of type " + e.getClass().getName()
										+ " was thrown. Message is " + e.getMessage()
										+ ". Exception occured whiles writing to file");
							} catch (JAXBException e) {
								throwCoreException("Exception of type " + e.getClass().getName()
										+ " was thrown. Message is " + e.getMessage()
										+ ". Exception occured whiles writing to file");
							} finally {
								try {
									stream.close();
								} catch (Exception e2) {
								}
							}
						}
					}
					
					monitor.setTaskName("Opening file for editing...");
					getShell().getDisplay().asyncExec(new Runnable() {
						public void run() {
							IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
							try {
								IDE.openEditor(page, wrapperFile, true);
							} catch (PartInitException e) {
							}
						}
					});
					
					monitor.worked(1);
				} catch (CoreException e) {
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
	
	private void throwCoreException(String message) throws CoreException {
		IStatus status =
			new Status(IStatus.ERROR, CodeGenerators.PLUGIN_ID, IStatus.OK, message, null);
		throw new CoreException(status);
	}

	@Override
	public boolean needsProgressMonitor() {
		return true;
	}
}
