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
package com.quakearts.webtools.codegenerators.wizard.pages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.ResourceListSelectionDialog;

import com.quakearts.tools.CodeGenerators;
import com.quakearts.webtools.codegenerators.BasePageGenerator;

import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;

public class BaseTemplateWizardPage extends WizardPage {

	private IContainer location;
	private List<String> jsFiles=new ArrayList<String>(), cssFiles= new ArrayList<String>();
	private String fileName;
	protected String title="";

	public String getTitle() {
		return title;
	}

	public IContainer getLocation() {
		return location;
	}

	public List<String> getJsFiles() {
		return jsFiles;
	}

	public List<String> getCssFiles() {
		return cssFiles;
	}

	public String getFileName() {
		return fileName;
	}

	/**
	 * Create the wizard.
	 */
	public BaseTemplateWizardPage(IContainer location) {
		super("beanBaseTemplate");
		setTitle("Select base pages elements...");
		setDescription("Populate css files, javascript files, and classes for the pages elements");
		setImageDescriptor(CodeGenerators.getQuakeartDescriptor());
		this.location = location;
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		GridLayout gl_container = new GridLayout(4, false);
		gl_container.horizontalSpacing = 4;
		container.setLayout(gl_container);
		
		Label lblSaveLocation = new Label(container, SWT.NONE);
		lblSaveLocation.setText("Save location");
		
		final Text folderText = new Text(container, SWT.BORDER);
		if(location!=null)
			folderText.setText(location.toString());
		
		folderText.setEditable(false);
		folderText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		
		Button btnBrowseFolders = new Button(container, SWT.NONE);
		btnBrowseFolders.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), ResourcesPlugin.getWorkspace().getRoot(), true, "Select a folders to save the file in...");
				dialog.setTitle("Select file location");
				if(dialog.open()==ContainerSelectionDialog.OK){
					location = ResourcesPlugin.getWorkspace().getRoot().getContainerForLocation(((IPath)dialog.getResult()[0]));
					folderText.setText(location.toString());
					checkPageComplete();
				}
			}
		});
		btnBrowseFolders.setText("Browse...");
		
		Label lblFileName = new Label(container, SWT.NONE);
		lblFileName.setText("File Name");
		
		final Text fileNameText = new Text(container, SWT.BORDER);
		fileNameText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if(fileNameText.getText().isEmpty()){
					MessageDialog.openError(getShell(),"Invalid file name","File name cannot be null");
					fileNameText.setFocus();
				} else {
					fileName = fileNameText.getText();
					checkPageComplete();
				}
			}
		});
		fileNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		new Label(container, SWT.NONE);
		
		Label divider1 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		divider1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
		
		Label lblJavaScriptFiles = new Label(container, SWT.NONE);
		lblJavaScriptFiles.setText("Javascript files:");
		
		Button btnAddJsFiles = new Button(container, SWT.NONE);
		btnAddJsFiles.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(location==null){
					MessageDialog.openError(getShell(),"Invalid operation", "Select a container first.");
					return;
				}
				
				ResourceListSelectionDialog dialog = new ResourceListSelectionDialog(getShell(), location.getProject(), IResource.FILE){

					@Override
					protected boolean select(IResource resource) {
						return "js".equals(resource.getFileExtension());
					}
					
				};
				dialog.setMessage("Select a .js file...");
				dialog.setTitle("Add javascript");
				
				if(dialog.open() == ResourceListSelectionDialog.OK){
					IResource resource = (IResource) dialog.getResult()[0];
					jsFiles.add(CodeGenerators.relativePath(resource.getProjectRelativePath().segments(), location.getProjectRelativePath().segments()));
				}
				
			}
		});
		btnAddJsFiles.setText("Add...");
		
		Label lblCssFiles = new Label(container, SWT.NONE);
		lblCssFiles.setText("CSS files:");
		
		Button btnAddCssFiles = new Button(container, SWT.NONE);
		btnAddCssFiles.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(location==null){
					MessageDialog.openError(getShell(),"Invalid operation", "Select a container first.");
					return;
				}

				ResourceListSelectionDialog dialog = new ResourceListSelectionDialog(getShell(), location.getProject(), IResource.FILE){

					@Override
					protected boolean select(IResource resource) {
						return "css".equals(resource.getFileExtension());
					}
					
				};
				dialog.setMessage("Select a .css file...");
				dialog.setTitle("Add CSS");
				
				if(dialog.open() == ResourceListSelectionDialog.OK){
					IResource resource = (IResource) dialog.getResult()[0];
					cssFiles.add(CodeGenerators.relativePath(resource.getProjectRelativePath().segments(), location.getProjectRelativePath().segments()));
				}
			}
		});
		btnAddCssFiles.setText("Add...");
		
		Tree jsFilesTree = new Tree(container, SWT.BORDER);
		GridData gd_jsFilesTree = new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1);
		gd_jsFilesTree.widthHint = 72;
		jsFilesTree.setLayoutData(gd_jsFilesTree);
		
		Tree cssFilesTree = new Tree(container, SWT.BORDER);
		GridData gd_cssFilesTree = new GridData(SWT.FILL, SWT.FILL, false, true, 2, 1);
		gd_cssFilesTree.widthHint = 91;
		cssFilesTree.setLayoutData(gd_cssFilesTree);
		
		Label divider2 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		divider2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 4, 1));
		
		Label lblHeaderClass = new Label(container, SWT.NONE);
		lblHeaderClass.setText("Header class");
		
		Label lblTitle = new Label(container, SWT.NONE);
		lblTitle.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblTitle.setText("Title");
		
		final Text titleText = new Text(container, SWT.BORDER);
		titleText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				title = titleText.getText();
			}
		});
		
		titleText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		setPageComplete(false);
	}

	public InputStream generateTemplate() throws IOException{
		BasePageGenerator generator = new BasePageGenerator();
		ByteArrayOutputStream bos= new ByteArrayOutputStream();
		generator.generateBasePage(bos, title, cssFiles, jsFiles);
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		return bis;
	}
	
	private void checkPageComplete(){
		setPageComplete(location!=null && location.exists() && fileName!=null && !fileName.isEmpty());
	}
}
