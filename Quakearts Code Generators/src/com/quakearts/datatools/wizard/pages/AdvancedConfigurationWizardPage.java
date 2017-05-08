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

import java.sql.Types;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.quakearts.datatools.abstraction.Parameter;
import com.quakearts.datatools.abstraction.ProcedureResult;
import com.quakearts.datatools.abstraction.ResultColumn;
import com.quakearts.datatools.abstraction.WrapperClass;
import com.quakearts.tools.CodeGenerators;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class AdvancedConfigurationWizardPage extends WizardPage {

	private boolean facesType, multipleSets, updater, executor;
	private WrapperClass wrapperClass;
	private Tree procedureTree;
	private Button btnMultipleSets;
	private Button btnUpdater;
	private Button btnExecutor;
	
	public boolean isFacesType() {
		return facesType;
	}

	public boolean isMultipleSets() {
		return multipleSets;
	}
	
	public boolean isExecutor() {
		return executor;
	}
	
	public boolean isUpdater() {
		return updater;
	}
	
	/**
	 * Create the wizard.
	 * @param wrapperClass 
	 */
	public AdvancedConfigurationWizardPage(WrapperClass wrapperClass) {
		super("advancedConfigurationPage");
		setTitle("Advanced settings");
		setDescription("Advanced wrapper settings...");
		this.wrapperClass = wrapperClass;
		setImageDescriptor(CodeGenerators.getQuakeartDescriptor());
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(2, false));
		
		final Button btnFacesType = new Button(container, SWT.CHECK);
		btnFacesType.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				facesType = btnFacesType.getSelection();
			}
		});
		btnFacesType.setText("Faces Type");
		
		btnMultipleSets = new Button(container, SWT.CHECK);
		btnMultipleSets.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				multipleSets = btnMultipleSets.getSelection();
				if(multipleSets){
					btnUpdater.setSelection(false);
					btnExecutor.setSelection(false);
				}	
			}
		});
		btnMultipleSets.setText("Custom results processing");

		btnUpdater = new Button(container, SWT.CHECK);
		btnUpdater.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updater = btnUpdater.getSelection();
				if(updater){
					btnMultipleSets.setSelection(false);
					btnExecutor.setSelection(false);
				}	
			}
		});
		btnUpdater.setText("Use updater pattern");
		
		btnExecutor = new Button(container, SWT.CHECK);
		btnExecutor.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				executor = btnExecutor.getSelection();
				if(executor){
					btnMultipleSets.setSelection(false);
					btnUpdater.setSelection(false);
				}	
			}
		});
		btnExecutor.setText("Use executor pattern");
		
		procedureTree = new Tree(container, SWT.BORDER);
		GridData gd_procedureTree = new GridData(SWT.LEFT, SWT.FILL, true, true, 2, 1);
		gd_procedureTree.widthHint = 282;
		procedureTree.setLayoutData(gd_procedureTree);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		TreeItem rootItem = new TreeItem(procedureTree, 0);
		rootItem.setImage(CodeGenerators.getScriptImage());
		rootItem.setText(wrapperClass.getJavaClassName()+": "+wrapperClass.getProcedure().getProcedureName());
		
		for(Parameter parameter:wrapperClass.getProcedure().getParameters()){
			TreeItem item = new TreeItem(rootItem, 0);
			item.setText(parameter.getVariableName());
			item.setImage(parameter.getType() == Types.DATE 
					|| parameter.getType() == Types.TIME
					|| parameter.getType() == Types.TIMESTAMP
					?CodeGenerators.getCalendarImage():CodeGenerators.getTextfieldImage());
			item.setData(parameter);					
		}
		
		for(ProcedureResult result:wrapperClass.getProcedure().getProcedureResults()){
			TreeItem resultItem = new TreeItem(rootItem, 0);
			resultItem.setText(result.getJavaClassName());
			resultItem.setData(result);
			resultItem.setImage(CodeGenerators.getTextColumnsImage());
			for(ResultColumn column:result.getResultColumns()){
				TreeItem columnItem = new TreeItem(resultItem, 0);
				columnItem.setText(column.getVariableName());
				columnItem.setImage(CodeGenerators.getTagImage());
				columnItem.setData(column);
			}
			resultItem.setExpanded(true);
		}
		rootItem.setExpanded(true);
	}
	
	
}
