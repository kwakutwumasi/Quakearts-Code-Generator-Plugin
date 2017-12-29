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
package com.quakearts.tools.web.wizard.pages;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.widgets.Composite;
import com.quakearts.tools.CodeGenerators;
import com.quakearts.tools.web.context.ScaffoldingContext;
import com.quakearts.tools.web.model.BeanModel;
import com.quakearts.tools.web.model.BeanModelBuilder;
import com.quakearts.tools.web.model.Scaffolding;
import com.quakearts.tools.web.wizard.ScaffoldingWizard;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.ResourceSelectionDialog;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class ScaffoldingWizardPage extends WizardPage {
	private Text customScaffoldingText;
	private IProject project;
	private String defaultDescription;
	private IResource scaffoldingFile;
	private Scaffolding scaffolding;
	private ScaffoldingContext scaffoldingContext = new ScaffoldingContext();
	private Tree beanTree;
	private Button btnUpdateAndMarkChanges;

	/**
	 * Create the wizard.
	 */
	public ScaffoldingWizardPage(IProject project) {
		super("scaffoldingWizardPage");
		setTitle("Scaffolding Code Generation");
		setDescription("Select beans to be used in the page generation.");
		setImageDescriptor(CodeGenerators.getQuakeartDescriptor());
		this.project=project;
	}

	public ScaffoldingContext getScaffoldingContext() {
		return scaffoldingContext;
	}
	
	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(3, false));
		
		Label lblPredefinedScaffolding = new Label(container, SWT.NONE);
		lblPredefinedScaffolding.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPredefinedScaffolding.setText("Predefined Scaffolding");
		
		final Combo combo = new Combo(container, SWT.NONE);
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int i;
				if((i=combo.getSelectionIndex())!=-1){
					scaffolding = getWizard().getScaffoldingTemplates().get(combo.getItem(i));
					checkPageComplete();
				}
			}
		});
		
		GridData gd_combo = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_combo.widthHint = 200;
		for(String template:getWizard().getPredefinedScaffoldingTemplates()){
			combo.add(template);
		}
		
		combo.setLayoutData(gd_combo);
		
		btnUpdateAndMarkChanges = new Button(container, SWT.CHECK);
		btnUpdateAndMarkChanges.setText("Update and mark changes");
		
		Label lblCustomScaffolding = new Label(container, SWT.NONE);
		lblCustomScaffolding.setText("Custom Scaffolding");
		
		customScaffoldingText = new Text(container, SWT.BORDER);
		customScaffoldingText.setEditable(false);
		GridData gd_txtCwindowseclipsejavatemplateslongpathname = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_txtCwindowseclipsejavatemplateslongpathname.widthHint = 215;
		customScaffoldingText.setLayoutData(gd_txtCwindowseclipsejavatemplateslongpathname);
		
		Button btnBrowseScaffolding = new Button(container, SWT.NONE);
		btnBrowseScaffolding.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(project==null){
					ContainerSelectionDialog containerDialog = new ContainerSelectionDialog(getShell(), null, false, "Select a project");
					if(containerDialog.open()==SelectionDialog.OK){
						IContainer selectedContainer = (IContainer) containerDialog.getResult()[0];
						project = selectedContainer.getProject();
					} else {
						showError("You must select a project to continue");
						return;
					}
				}
				
				ResourceSelectionDialog dialog = new ResourceSelectionDialog(getShell(), project, "Select scaffolding descriptor file");
				if(dialog.open()==SelectionDialog.OK){
					scaffoldingFile = (IResource) dialog.getResult()[0];
					if(scaffoldingFile.getType() != IResource.FILE)
						return;
					
					customScaffoldingText.setText(scaffoldingFile.getName());
					
					if(getWizard().getScaffoldingTemplates().containsKey(project.getName()+":"+scaffoldingFile.getName())){
						scaffolding = getWizard().getScaffoldingTemplates().get(scaffoldingFile.getName());
					}
				}
				
				checkPageComplete();
			}
		});
		
		btnBrowseScaffolding.setAlignment(SWT.LEFT);
		GridData gd_btnBrowseScaffolding = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnBrowseScaffolding.widthHint = 80;
		btnBrowseScaffolding.setLayoutData(gd_btnBrowseScaffolding);
		btnBrowseScaffolding.setText("  Browse...");
		
		Label lblBeans = new Label(container, SWT.NONE);
		lblBeans.setText("Beans");
		
		Button btnBrowseClasses = new Button(container, SWT.NONE);
		btnBrowseClasses.setAlignment(SWT.LEFT);
		GridData gd_btnBrowseClasses = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnBrowseClasses.widthHint = 80;
		btnBrowseClasses.setLayoutData(gd_btnBrowseClasses);
		btnBrowseClasses.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ProgressMonitorDialog monitorDialog = new ProgressMonitorDialog(getShell());
				SelectionDialog dialog;
				try {
					dialog = JavaUI.createTypeDialog(getShell(),monitorDialog, project, IJavaElementSearchConstants.CONSIDER_CLASSES, true);
					if(dialog.open()==SelectionDialog.OK){
						getWizard().getContainer().run(false, true, new GenerateBeanModels(dialog.getResult()));
					}
					checkPageComplete();
				} catch (JavaModelException ex) {
					showError("Cannot create dialog.");
					CodeGenerators.logError("Exception of type " + ex.getClass().getName()
							+ " was thrown. Message is " + ex.getMessage()
							+ ". Exception occured whiles creating type selection dialog", ex);
				} catch (InvocationTargetException ex) {
					showError("Error generating beans models.");
					CodeGenerators.logError("Exception of type " + ex.getClass().getName()
							+ " was thrown. Message is " + ex.getMessage()
							+ ". Exception occured whiles creating type selection dialog", ex);
				} catch (InterruptedException e1) {
				}
			}
		});
		btnBrowseClasses.setText("  Browse...");
		
		Button btnClearCache = new Button(container, SWT.NONE);
		btnClearCache.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CodeGenerators.clearCache();
			}
		});
		
		btnClearCache.setAlignment(SWT.LEFT);
		GridData gd_btnClearCache = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnClearCache.widthHint = 80;
		btnClearCache.setLayoutData(gd_btnClearCache);
		btnClearCache.setText("  Clear Cache");
		new Label(container, SWT.NONE);
		
		beanTree = new Tree(container, SWT.BORDER);
		beanTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		final TreeEditor beanTreeEditor = new TreeEditor(beanTree);
		beanTreeEditor.grabHorizontal = true;
        beanTreeEditor.horizontalAlignment = SWT.LEFT;
        beanTreeEditor.minimumWidth = 50;
        
        beanTree.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseDoubleClick(MouseEvent e) {
        		final TreeItem item = beanTree.getSelection()[0];

                final Text beanNameText = new Text(beanTree, SWT.NONE);
                beanNameText.setText(item.getText());
                
                beanNameText.addFocusListener(new FocusListener() {
					@Override
					public void focusLost(FocusEvent paramFocusEvent) {
                        Text text = (Text)beanTreeEditor.getEditor();
                        if(!text.getText().isEmpty() && text.getText().matches("[a-zA-Z0-9_]+")){
	                        item.setText(text.getText());
	                        if(item.getData() instanceof BeanModel){
	                        	BeanModel beanModel = (BeanModel)item.getData();
	                            beanModel.setName(text.getText());
	                        }                   
                        } else {
                        	MessageDialog.openError(getShell(),"Invalid entry",
                        			"Text must be a valid java name (no spaces, capital or small letters, underscore and or numbers)");
                        }
                        beanNameText.dispose();
					}
					@Override
					public void focusGained(FocusEvent paramFocusEvent) {
						beanNameText.selectAll();
					}
				});
                beanNameText.selectAll();
                beanNameText.setFocus();
                beanTreeEditor.setEditor(beanNameText, item);
        	}
		});
		
		setPageComplete(false);
	}
	
	public boolean updateAndMarkChangesSelected(){
		return btnUpdateAndMarkChanges!=null? btnUpdateAndMarkChanges.getSelection():false;
	}
	
	private void showError(String errorMessage) {
		if(defaultDescription==null)
			defaultDescription=getDescription();
		setDescription(errorMessage);
	}
	
	private void restoreDescription() {
		if(defaultDescription!=null)
			setDescription(defaultDescription);
	}
	
	private void checkPageComplete(){
		setPageComplete(project!=null 
				&& (scaffoldingFile!=null 
					|| scaffolding!=null) 
				&& scaffoldingContext.getBeanModels().size()>0);
		restoreDescription();
	}
	
	public IResource getScaffoldingFile() {
		return scaffoldingFile;
	}
	
	public Scaffolding getScaffolding() {
		return scaffolding;
	}
	
	public IProject getProject() {
		return project;
	}
	
	@Override
	public ScaffoldingWizard getWizard() {
		if(super.getWizard() instanceof ScaffoldingWizard)
			return ((ScaffoldingWizard)super.getWizard());
		else
			throw new IllegalArgumentException("Expected "+ScaffoldingWizard.class.getName()+". Got "+super.getWizard().getClass().getName());
	}
	
	private final class GenerateBeanModels implements IRunnableWithProgress{
		private Object[] beanTypes;
		
		public GenerateBeanModels(Object[] beanTypes) {
			this.beanTypes = beanTypes;
		}

		@Override
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			monitor.beginTask("Loading bean models....", beanTypes.length);
			for(Object beanTypeObject:beanTypes){
				IType beanType=(IType) beanTypeObject;
				try {
					BeanModel model = CodeGenerators.getBeanModels().get(beanType.getFullyQualifiedName());
					if(model == null){
						model = BeanModelBuilder.createBeanModel(beanType.getFullyQualifiedName(),
								CodeGenerators.getProjectClassLoader(project));
						CodeGenerators.getBeanModels().put(beanType.getFullyQualifiedName(),model);
					}

					scaffoldingContext.addBeanModel(model);
					
					TreeItem item = new TreeItem(beanTree, 0);
					item.setImage(CodeGenerators.getBeanImage());
					item.setText(model.getName());
					item.setData(model);
				} catch (ClassNotFoundException | IntrospectionException ex) {
					CodeGenerators.logError("Exception of type " + ex.getClass().getName()
							+ " was thrown. Message is " + ex.getMessage()
							+ ". Exception occured whiles creating type selection dialog", ex);
				}
				monitor.worked(1);
			}
			monitor.done();
		}
		
	}
}
