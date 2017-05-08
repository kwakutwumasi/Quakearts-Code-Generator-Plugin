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
package com.quakearts.datatools.wizard.pages;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.ResourceListSelectionDialog;
import org.eclipse.ui.dialogs.SelectionDialog;

import com.quakearts.tools.CodeGenerators;

public class ProcedureXMLSelectionWizardPage extends WizardPage {
	private IProject project;
	private IPackageFragmentRoot sourceFolder;
	private ICompilationUnit wrapperFile;
	public static final String PAGENAME ="procedureXmlPage";
	private Text containterText;
	private Text xmlFileText;
	
	public ICompilationUnit getWrapperFile() {
		return wrapperFile;
	}

	/**
	 * Create the wizard.
	 */
	public ProcedureXMLSelectionWizardPage() {
		super(PAGENAME);
		setTitle("Select procedure xml doc");
		setDescription("Select a procedure xml definition to load....");
		setImageDescriptor(CodeGenerators.getQuakeartDescriptor());
	}

	public IProject getProject() {
		return project;
	}

	public IPackageFragmentRoot getSourceFolder() {
		return sourceFolder;
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(3, false));
		
		Label lblContainer = new Label(container, SWT.NONE);
		lblContainer.setText("Container");
		
		containterText = new Text(container, SWT.BORDER);
		containterText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnBrowseContainer = new Button(container, SWT.NONE);
		btnBrowseContainer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), ResourcesPlugin.getWorkspace().getRoot(), false, "Select project...");
				if(dialog.open()==ContainerSelectionDialog.OK){
					IPath path = ((IPath)dialog.getResult()[0]);
					IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
					project = resource.getProject();
					containterText.setText(path.toString());
				}
			}
		});
		btnBrowseContainer.setText("Browse...");
		
		Label lblProcedureXmlFile = new Label(container, SWT.NONE);
		lblProcedureXmlFile.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblProcedureXmlFile.setText("Procedure xml file (*.prc.xml)");
		
		xmlFileText = new Text(container, SWT.BORDER);
		GridData gd_xmlFileText = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_xmlFileText.widthHint = 233;
		xmlFileText.setLayoutData(gd_xmlFileText);
		
		Button btnBrowse = new Button(container, SWT.NONE);
		btnBrowse.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		btnBrowse.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if(project==null){
					MessageDialog.openError(getShell(),"Invalid selection", "Select a project first...");
					return;
				}
				try {
					ProgressMonitorDialog monitorDialog = new ProgressMonitorDialog(getShell());
					
					SelectionDialog dialog = JavaUI.createTypeDialog(getShell(), monitorDialog, project, IJavaElementSearchConstants.CONSIDER_CLASSES, false);
					
					if(dialog.open()==ResourceListSelectionDialog.OK){
						IType type = (IType) dialog.getResult()[0];
						xmlFileText.setText(type.getResource().getName());
						IJavaElement parent = type.getParent();
						while(parent!=null){
							if(parent.getElementType()==IJavaElement.PACKAGE_FRAGMENT_ROOT){
								sourceFolder = (IPackageFragmentRoot) parent;
								break;
							}
							parent = parent.getParent();
						}
						project = type.getResource().getProject();
						wrapperFile = type.getCompilationUnit();
					}
					setPageComplete(project!=null&&sourceFolder!=null&&wrapperFile!=null);
				} catch (Exception ex) {
					MessageDialog.openError(getShell(),"Error creating type dialog", "Exception of type " + ex.getClass().getName()
							+ " was thrown. Message is " + ex.getMessage()
							+ ". Exception occured whiles creating type dialog");
				}
			}
		});
		btnBrowse.setText("Browse...");
		setPageComplete(false);
	}

}
