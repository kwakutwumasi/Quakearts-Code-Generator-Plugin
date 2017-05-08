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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;

import com.quakearts.tools.CodeGenerators;

public class ProcedureClassWizardPage extends NewTypeWizardPage {

	/**
	 * Create the wizard.
	 */
	public ProcedureClassWizardPage() {
		super(true, "newClassPage");
		setTitle("Select new class");
		setDescription("Enter class details....");
		setImageDescriptor(CodeGenerators.getQuakeartDescriptor());
	}

	
	public void init(IStructuredSelection selection) {
		IJavaElement jelem= getInitialJavaElement(selection);
		initContainerPage(jelem);
		initTypePage(jelem);
		doStatusUpdate();
	}
		
	private void doStatusUpdate() {
		// status of all used components
		IStatus[] status= new IStatus[] {
			fContainerStatus,
			isEnclosingTypeSelected() ? fEnclosingTypeStatus : fPackageStatus,
			fTypeNameStatus,
			fModifierStatus,
			fSuperClassStatus,
			fSuperInterfacesStatus
		};

		// the mode severe status will be displayed and the OK button enabled/disabled.
		updateStatus(status);
	}
	
	@Override
	protected void handleFieldChanged(String fieldName) {
		super.handleFieldChanged(fieldName);

		doStatusUpdate();
	}


	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		Composite container= new Composite(parent, SWT.NONE);
		container.setFont(parent.getFont());

		int nColumns= 4;

		GridLayout layout= new GridLayout();
		layout.numColumns= nColumns;
		container.setLayout(layout);
		
		createContainerControls(container, nColumns);
		createPackageControls(container, nColumns);

		createSeparator(container, nColumns);
		
		createTypeNameControls(container, nColumns);
		
		setControl(container);
		setPageComplete(false);
		Dialog.applyDialogFont(parent);
	}


	@Override
	protected IStatus typeNameChanged() {
		IStatus status = super.typeNameChanged();
		
		if(status.isOK())
			setPageComplete(true);
		
		return status;
	}

}
