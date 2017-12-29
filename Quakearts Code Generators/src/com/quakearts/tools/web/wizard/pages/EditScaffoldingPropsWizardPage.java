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

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;

import com.quakearts.tools.CodeGenerators;
import com.quakearts.tools.web.model.Folder;
import com.quakearts.tools.web.model.PropertyEntry;
import com.quakearts.tools.web.model.Scaffolding;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.layout.GridData;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class EditScaffoldingPropsWizardPage extends WizardPage {

	/**
	 * Create the wizard.
	 */
	
	private IProject project;
	private Scaffolding scaffolding;
	private Table table;
	private TableViewer tableViewer;
	private Tree folderTree;
	private Text propertyText;
	
	public EditScaffoldingPropsWizardPage(IProject project) {
		super("editPropertiesWizardPage");
		setTitle("Scaffolding Code Generation");
		setDescription("Edit Scaffolding properties");
		setImageDescriptor(CodeGenerators.getQuakeartDescriptor());
		this.project=project;
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(3, false));
		
		Label lblProperties = new Label(container, SWT.NONE);
		lblProperties.setText("Properties");
		
		Label label = new Label(container, SWT.SEPARATOR | SWT.VERTICAL);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 2));
		
		Label lblFolderStructure = new Label(container, SWT.NONE);
		lblFolderStructure.setText("Folder Structure");
		
		tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_table.widthHint = 140;
		table.setLayoutData(gd_table);
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
				PropertyEntry entry =(PropertyEntry) selection.getFirstElement();
				if(entry.getValue()!=null && entry.getValue().length()>35){
					propertyText.setText(entry.getValue());
					propertyText.setEnabled(true);
					propertyText.setData(entry);
				} else {
					propertyText.setText("");
					propertyText.setEnabled(false);
					propertyText.setData(null);
				}
			}
		});
		
		TableViewerColumn tableViewerPropertyColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnProperty = tableViewerPropertyColumn.getColumn();
		tblclmnProperty.setWidth(100);
		tblclmnProperty.setText("Property");
		tableViewerPropertyColumn.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				PropertyEntry entry = ((PropertyEntry)element);
				return entry.getDisplayName()!=null?entry.getDisplayName():entry.getProperty();
			}
		});
		
		TableViewerColumn tableViewerValueColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnValue = tableViewerValueColumn.getColumn();
		tblclmnValue.setWidth(100);
		tblclmnValue.setText("Value");
		tableViewerValueColumn.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return ((PropertyEntry)element).getValue();
			}
		});
		tableViewerValueColumn.setEditingSupport(new ValueEditingSupport(tableViewer));
		
		folderTree = new Tree(container, SWT.BORDER);
		GridData gd_folderTree = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2);
		gd_folderTree.heightHint = 302;
		folderTree.setLayoutData(gd_folderTree);
		
		final TreeEditor folderTreeEditor = new TreeEditor(folderTree);
		
		propertyText = new Text(container, SWT.BORDER | SWT.MULTI);
		GridData gd_propertyText = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_propertyText.heightHint = 98;
		propertyText.setLayoutData(gd_propertyText);
		propertyText.setEnabled(false);
		propertyText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				PropertyEntry entry = (PropertyEntry) propertyText.getData();
				if(entry!=null){
					entry.setValue(propertyText.getText());
					scaffolding.getProperties().resetPropertyMap();
					tableViewer.refresh();
				}
			}
		});
		
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Button btnNewFolder = new Button(container, SWT.NONE);
		btnNewFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(folderTree.getSelection() != null) {
					if(folderTree.getSelection().length==1) {
						TreeItem item = folderTree.getSelection()[0];
						Folder folder = (Folder) item.getData();
						Folder parentFolder = folder.getParentFolder();
						if(parentFolder != null) {
							parentFolder.getFolders().remove(folder);
							Folder newFolder = new Folder();
							newFolder.setFolderID("newFolder"+parentFolder.getFolders().size()+1);
							newFolder.setName("New Folder");
							newFolder.setParentFolder(parentFolder);
							newFolder.getFolders().add(folder);

							folderTree.clearAll(true);
							TreeItem rootItem = new TreeItem(folderTree, 0);
							rootItem.setData(null);
							rootItem.setText(project.getName());
							rootItem.setImage(CodeGenerators.getFolderImage());
							if(scaffolding.getFolderStructure()!=null && scaffolding.getFolderStructure().getFolder()!=null){
								for(Folder sfolder:scaffolding.getFolderStructure().getFolder().getFolders())
									createFolderView(sfolder, rootItem);
							}
							
						} else {
							MessageBox box = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.OK);
							box.setText("Invalid Action");
							box.setMessage("Please select a single folder other than the root folder");
							box.open();							
						}
					} else {
						MessageBox box = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.OK);
						box.setText("Invalid Action");
						box.setMessage("Please select a single folder as the insertion point");
						box.open();						
					}
				} else {
					MessageBox box = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.OK);
					box.setText("Invalid Action");
					box.setMessage("Please select a position for the new folder");
					box.open();
				}
			}
		});
		GridData gd_btnNewFolder = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnNewFolder.widthHint = 108;
		btnNewFolder.setLayoutData(gd_btnNewFolder);
		btnNewFolder.setText("New Folder");
		folderTreeEditor.grabHorizontal = true;
        folderTreeEditor.horizontalAlignment = SWT.LEFT;
        folderTreeEditor.minimumWidth = 50;
        
        folderTree.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseDoubleClick(MouseEvent e) {
        		final TreeItem item = folderTree.getSelection()[0];

                final Text beanNameText = new Text(folderTree, SWT.NONE);
                beanNameText.setText(item.getText());
                
                beanNameText.addFocusListener(new FocusListener() {
					@Override
					public void focusLost(FocusEvent paramFocusEvent) {
                        Text text = (Text)folderTreeEditor.getEditor();
                        if(!text.getText().isEmpty()){
	                        item.setText(text.getText());
	                        if(item.getData() instanceof Folder){
	                        	Folder folder = (Folder)item.getData();
	                            folder.setName(text.getText());
	                        }                   
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
                folderTreeEditor.setEditor(beanNameText, item);
        	}
		});
	}
	
	private void createFolderView(Folder folder, TreeItem parentItem){
		TreeItem item = new TreeItem(parentItem, 0);
		item.setImage(CodeGenerators.getFolderImage());
		item.setText(folder.getName());
		item.setData(folder);
		for(Folder subFolder:folder.getFolders()){
			createFolderView(subFolder, item);
		}
	}
	
	private class ValueEditingSupport extends EditingSupport {

		CellEditor cellEditor;
		TableViewer tableViewer;
		
		public ValueEditingSupport(TableViewer viewer) {
			super(viewer);
			cellEditor = new TextCellEditor(viewer.getTable());
			tableViewer = viewer;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return cellEditor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return element instanceof PropertyEntry;
		}

		@Override
		protected Object getValue(Object element) {
			return ((PropertyEntry)element).getValue();
		}

		@Override
		protected void setValue(Object element, Object value) {
			((PropertyEntry) element).setValue(value == null ? null : value.toString());
			scaffolding.getProperties().resetPropertyMap();
			tableViewer.update(element, null);
		}
	}
	
	public  Scaffolding getScaffolding() {
		return scaffolding;
	}
	
	public IProject getProject() {
		return project;
	}
	
	public void setProject(IProject project) {
		this.project = project;
	}
	
	public void setScaffolding(Scaffolding scaffolding) {
		this.scaffolding = scaffolding;
		tableViewer.setInput(scaffolding.getProperties().getEntries());
		
		folderTree.clearAll(true);
		TreeItem rootItem = new TreeItem(folderTree, 0);
		rootItem.setData(null);
		rootItem.setText(project.getName());
		rootItem.setImage(CodeGenerators.getFolderImage());
		
		if(scaffolding.getFolderStructure()!=null && scaffolding.getFolderStructure().getFolder()!=null){
			for(Folder folder:scaffolding.getFolderStructure().getFolder().getFolders())
				createFolderView(folder, rootItem);
		}
	}
}
