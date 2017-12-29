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
package com.quakearts.tools.data.wizard.pages;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.datatools.connectivity.ui.dse.dialogs.ProfileSelectionDialog;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.quakearts.tools.CodeGenerators;
import com.quakearts.tools.data.generator.readers.ProcedureResultsReader;
import com.quakearts.tools.data.model.ProcedureResult;
import com.quakearts.tools.data.model.ResultColumn;
import com.quakearts.tools.data.model.WrapperClass;
import com.quakearts.tools.data.wizard.dialogs.ParameterDialog;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class ProcedureResultWizardPage extends WizardPage {
	private WrapperClass wrapperClass;
	private ProcedureResultsReader reader = new ProcedureResultsReader();
	private Tree resultsTree;
	private Button btnUseMultipleResultsStyle;
	private Text conditionText;
	private Button btnSeparateResultClasses;
	
	/**
	 * Create the wizard.
	 * @wbp.parser.constructor
	 */
	public ProcedureResultWizardPage(WrapperClass wrapperClass) {
		super("procedureResultPage");
		setTitle("Generate result objects");
		setDescription("Generate result objects for the procedure....");
		this.wrapperClass = wrapperClass;
		setImageDescriptor(CodeGenerators.getQuakeartDescriptor());
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {		
		final ProfileSelectionDialog selectionDialog = new ProfileSelectionDialog(getShell());
		final ParameterDialog parameterDialog = new ParameterDialog(getShell(), wrapperClass);
		
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(2, false));
		
		Label lblParameters = new Label(container, SWT.NONE);
		lblParameters.setText("Parameters");
		
		Label lblResults = new Label(container, SWT.NONE);
		lblResults.setText("Results:");
		
		final Tree parameterTree = new Tree(container, SWT.BORDER);
		GridData gd_parameterTree = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_parameterTree.widthHint = 100;
		parameterTree.setLayoutData(gd_parameterTree);
		
		resultsTree = new Tree(container, SWT.BORDER);
		GridData gd_resultsTree = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_resultsTree.widthHint = 100;
		resultsTree.setLayoutData(gd_resultsTree);
		
		final TreeEditor resultsTreeEditor = new TreeEditor(resultsTree);
		resultsTreeEditor.grabHorizontal = true;
        resultsTreeEditor.horizontalAlignment = SWT.LEFT;
        resultsTreeEditor.minimumWidth = 50;

        resultsTree.addMouseListener(new MouseAdapter(){

			@Override
			public void mouseDoubleClick(MouseEvent e) {
                if (resultsTree.getSelectionCount()==0) return;
                
                final TreeItem item = resultsTree.getSelection()[0];
                if(!(item.getData() instanceof ResultColumn) && !(item.getData() instanceof ProcedureResult)) return;

                final Text parameterText = new Text(resultsTree, SWT.NONE);
                parameterText.setText(item.getText());
                
                parameterText.addFocusListener(new FocusListener() {
					@Override
					public void focusLost(FocusEvent paramFocusEvent) {
                        Text text = (Text)resultsTreeEditor.getEditor();
                        boolean wasEmpty;
                        if(!text.getText().isEmpty() && text.getText().matches("[a-zA-Z0-9_]+")){
	                        item.setText(text.getText());
	                        if(item.getData() instanceof ResultColumn){
	                        	ResultColumn column = (ResultColumn)item.getData();
	                        	wasEmpty = column.getVariableName().isEmpty();	                        	
	                            column.setVariableName(text.getText());

	                            if(wasEmpty)
	                        		canComplete();
	                        } else if(item.getData() instanceof ProcedureResult){
	                        	ProcedureResult result = (ProcedureResult)item.getData();
	                        	wasEmpty = result.getJavaClassName().isEmpty();
	                        	
	                            result.setJavaClassName(text.getText());
	                        	if(wasEmpty)
	                        		canComplete();
	                        }                   
                        } else {
                        	MessageDialog.openError(getShell(),"Invalid entry", "Text must be a valid java name (no spaces, capital or small letters, underscore and or numbers)");
                        }
                        parameterText.dispose();

					}
					@Override
					public void focusGained(FocusEvent paramFocusEvent) {
						parameterText.selectAll();
					}
				});
                parameterText.selectAll();
                parameterText.setFocus();
                resultsTreeEditor.setEditor(parameterText, item);
			}
        	
        });
        
        resultsTree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
                if (!(e.item instanceof TreeItem)) return;
                
                final TreeItem item = (TreeItem)e.item;
                if(!(item.getData() instanceof ProcedureResult)) return;

            	ProcedureResult result = (ProcedureResult)item.getData();
                conditionText.setText(result.getCondition());
			}
		});

		
		Button btnGetMetaData = new Button(container, SWT.NONE);
		btnGetMetaData.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				parameterDialog.open();
				
				if(parameterDialog.getReturnCode()==Dialog.OK){
					final Object[] callParameters = parameterDialog.getCallParameters();
					selectionDialog.open();
					if(selectionDialog.getReturnCode()==ProfileSelectionDialog.OK){
						parameterTree.removeAll();
						for(Object object:callParameters){
							TreeItem item = new TreeItem(parameterTree, 0);
							item.setImage(CodeGenerators.getTextfieldImage());
							if(object==null){
								item.setText("NULL");
							}else{
								item.setText(object.toString());
							}
						}
							
						IRunnableWithProgress op  = new IRunnableWithProgress() {
							
							@Override
							public void run(IProgressMonitor monitor) throws InvocationTargetException,
									InterruptedException {
								reader.getProcedureResult(wrapperClass.getProcedure(), callParameters, selectionDialog.getCPName(),btnUseMultipleResultsStyle.getSelection());
							}
						};
						
						try {
							getContainer().run(false, false, op);
						} catch (InvocationTargetException ex) {
							CodeGenerators.logError("Exception of type " + ex.getClass().getName()
									+ " was thrown. Message is " + ex.getMessage()
									+ ". Exception occured whiles fetching databases.", ex);
						} catch (InterruptedException ex) {
							CodeGenerators.logError("Exception of type " + ex.getClass().getName()
									+ " was thrown. Message is " + ex.getMessage()
									+ ". Exception occured whiles fetching databases.", ex);
						}
						if(reader.getReturnCode()>0)
							MessageDialog.openError(getShell(),"Result", "Procedure exited with error code "+reader.getReturnCode());
							
						redrawResultsTree();
					}
				}
			}
		});
		btnGetMetaData.setText("Get meta data...");
		
		Label lblProcessingCondition = new Label(container, SWT.NONE);
		lblProcessingCondition.setText("Processing Condition");
		
		btnUseMultipleResultsStyle = new Button(container, SWT.CHECK);
		btnUseMultipleResultsStyle.setText("Use multiple results style");
		
		conditionText = new Text(container, SWT.BORDER | SWT.MULTI);
		GridData gd_conditionText = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_conditionText.heightHint = 29;
		conditionText.setLayoutData(gd_conditionText);
		
		btnSeparateResultClasses = new Button(container, SWT.CHECK);
		btnSeparateResultClasses.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				wrapperClass.setSeparate(btnSeparateResultClasses.getSelection());
			}
		});
		btnSeparateResultClasses.setText("Separate result classes");
		
		Button btnUpdate = new Button(container, SWT.NONE);
		btnUpdate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(resultsTree.getSelectionCount()>0 && (resultsTree.getSelection()[0].getData() instanceof ProcedureResult) ){
					ProcedureResult result = (ProcedureResult)resultsTree.getSelection()[0].getData();
					result.setCondition(conditionText.getText());
				}
			}
		});
		btnUpdate.setText("Update");
	}

	private void canComplete() {
		boolean canComplete = true;
		for(ProcedureResult result:wrapperClass.getProcedure().getProcedureResults()){
			if(result.getJavaClassName().isEmpty())
				canComplete = false;
		}
		
		setPageComplete(canComplete);
	}

	private void redrawResultsTree() {
		resultsTree.removeAll();
		
		TreeItem rootItem = new TreeItem(resultsTree, 0);
		rootItem.setImage(CodeGenerators.getScriptImage());
		rootItem.setText(wrapperClass.getProcedure().getProcedureName());
		boolean complete=true;
		for(ProcedureResult result:wrapperClass.getProcedure().getProcedureResults()){
			TreeItem resultItem = new TreeItem(rootItem, 0);
			resultItem.setText(result.getJavaClassName());
			if(resultItem.getText().isEmpty())
				complete=false;
			
			resultItem.setData(result);
			resultItem.setImage(CodeGenerators.getTextColumnsImage());
			for(ResultColumn column:result.getResultColumns()){
				TreeItem columnItem = new TreeItem(resultItem, 0);
				columnItem.setText(column.getVariableName());
				columnItem.setImage(CodeGenerators.getTagImage());
				columnItem.setData(column);
			}
		}
		setPageComplete(complete);
	}	
}
