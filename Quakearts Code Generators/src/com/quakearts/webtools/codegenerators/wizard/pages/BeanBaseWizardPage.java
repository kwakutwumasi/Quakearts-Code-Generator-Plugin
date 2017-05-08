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

import java.beans.IntrospectionException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Tree;
import com.quakearts.tools.CodeGenerators;
import com.quakearts.webtools.codegenerators.GenericPageGenerator;
import com.quakearts.webtools.codegenerators.model.BeanElement;
import com.quakearts.webtools.codegenerators.model.BeanModel;
import com.quakearts.webtools.codegenerators.model.BeanModelFactory;
import com.quakearts.webtools.codegenerators.wizard.saxhandler.FacesConfigHandler;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.dialogs.ResourceListSelectionDialog;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.swt.widgets.Combo;
import org.xml.sax.SAXException;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Image;

public abstract class BeanBaseWizardPage extends WizardPage {

	public String getCustomTemplatefileName() {
		return customTemplatefileName;
	}

	private IProject project;
	private IContainer location;
	private BeanModel model;
	private Tree beanTree;
	private String template, uiTemplateSrc="", fileName, defaultDescription;
	private boolean customized;
	protected String customTemplatefileName;
	protected GenericPageGenerator pageGenerator;
	
	protected BeanBaseWizardPage(String pageName, String title, String description, IProject project, IContainer folder, GenericPageGenerator generator) {
		super(pageName);
		setTitle(title);
		setDescription(description);
		this.project = project;
		this.location = folder;
		this.pageGenerator = generator;
	}

	public BeanModel getModel() {
		return model;
	}
	
	public IProject getProject() {
		return project;
	}
	public IContainer getLocation() {
		return location;
	}
	public String getTemplate() {
		return template;
	}
	
	public String getFileName() {
		return fileName;
	}

	public String getUiTemplateSrc() {
		return uiTemplateSrc;
	}
	
	public boolean useCustomizedTemplate() {
		return customized;
	}

	/**
	 * Create the wizard.
	public BeanBaseWizardPage() {
	}

	 * Create contents of the wizard.
	 * @param parent
	 */	
	public void createControl(Composite parent) {
		setImageDescriptor(CodeGenerators.getQuakeartDescriptor());
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		GridLayout gl_container = new GridLayout(4, false);
		gl_container.horizontalSpacing = 3;
		container.setLayout(gl_container);
		
		Label lblSaveLocation = new Label(container, SWT.NONE);
		lblSaveLocation.setText("Save Location:");
		
		final Text saveLocationText = new Text(container, SWT.BORDER);
		saveLocationText.setEditable(false);
		GridData gd_saveLocationText = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_saveLocationText.widthHint = 150;
		saveLocationText.setLayoutData(gd_saveLocationText);
		if(location!=null)
			saveLocationText.setText(location.toString());
		
		Button btnBrowse = new Button(container, SWT.NONE);
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {				
				ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), ResourcesPlugin.getWorkspace().getRoot(), true, "Select a folders to save the file in...");
				dialog.setTitle("Select file location");
				if(dialog.open()==ContainerSelectionDialog.OK){
					IPath path = ((IPath)dialog.getResult()[0]);
					location = ResourcesPlugin.getWorkspace().getRoot().getFolder(path);
					if(location instanceof IProject)
						project = (IProject) location;
					else if(location!=null){
						project =  location.getProject();
						saveLocationText.setText(location.toString());
					}
				}
			}
		});
		btnBrowse.setText("Browse...");
		
		beanTree = new Tree(container, SWT.BORDER);
		GridData gd_beanTree = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 11);
		gd_beanTree.widthHint = 139;
		beanTree.setLayoutData(gd_beanTree);
		
		Label lblFileName = new Label(container, SWT.NONE);
		lblFileName.setText("File name:");
		
		final Text fileNameText = new Text(container, SWT.BORDER);
		fileNameText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if(fileNameText.getText().isEmpty()){
					showError("* Filename cannot be null.");
					return;
				}
				
				fileName = fileNameText.getText();
				if(!fileName.endsWith(".xhtml")){
					fileName = fileName+".xhtml";
					fileNameText.setText(fileName);
				}

				if(project==null){
					showError("* Select a container first.");
					return;
				}
				
				if(project.getFile(location.getProjectRelativePath().append(fileName)).exists()){
					MessageBox box = new MessageBox(getShell(), SWT.ICON_WARNING|SWT.YES|SWT.NO);
					box.setMessage(fileName+" already exists. Do you want to overwrite?");
					box.setText("Overwrite file?");
					if(box.open()==SWT.NO){
						fileName = null;
						fileNameText.selectAll();
						fileNameText.setFocus();
					}
				}
				checkPageComplete();
			}
		});
		
		GridData gd_fileNameText = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_fileNameText.widthHint = 150;
		fileNameText.setLayoutData(gd_fileNameText);
		new Label(container, SWT.NONE);
		
		Label divider1 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		divider1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
		
		Label lblBeanName = new Label(container, SWT.NONE);
		lblBeanName.setText("Bean name:");
		
		final Text beanClassText = new Text(container, SWT.BORDER);
		beanClassText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if(beanClassText.getText().isEmpty()){
					showError("* Bean name is required");
					return;
				}
				
				if(model!=null){
					model.setName(beanClassText.getText());
					checkPageComplete();
				}
			}
		});
		
		beanClassText.setEditable(false);
		GridData gd_beanClassText = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_beanClassText.widthHint = 150;
		beanClassText.setLayoutData(gd_beanClassText);
		
		Button btnBrowseBeans = new Button(container, SWT.NONE);
		btnBrowseBeans.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ProgressMonitorDialog monitorDialog = new ProgressMonitorDialog(getShell());
				try {
					if(project==null){
						showError("* Select a container first.");
						return;
					}
					
					SelectionDialog dialog = JavaUI.createTypeDialog(getShell(),monitorDialog, project, IJavaElementSearchConstants.CONSIDER_CLASSES, false);
					if(dialog.open()==SelectionDialog.OK){
						IType iType = (IType)dialog.getResult()[0];
						model = BeanModelFactory.createBeanModel(iType.getFullyQualifiedName(),project);
						beanClassText.setEditable(true);
						populateTree();
						beanClassText.setFocus();
						checkPageComplete();
					}					
				} catch (JavaModelException | ClassNotFoundException | IntrospectionException ex) {
					showError("Cannot create dialog");
					CodeGenerators.logError("Exception of type " + ex.getClass().getName()
							+ " was thrown. Message is " + ex.getMessage()
							+ ". Exception occured whiles creating type selection dialog", ex);
				}
			}
		});
		btnBrowseBeans.setText("Browse...");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Button btnLoadConfig = new Button(container, SWT.NONE);
		btnLoadConfig.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnLoadConfig.setToolTipText("Load from faces config");
		btnLoadConfig.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(project==null){
					showError("* Select a container first.");
					return;
				}
				try {
					List<String[]> managedBeans = getManagedBeans();
					ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), new LabelProvider(){
						@Override
						public Image getImage(Object element) {
							return CodeGenerators.getBeanImage();
						}

						@Override
						public String getText(Object element) {
							return ((String[])element)[0];
						}	
					});
					
					dialog.setElements(managedBeans.toArray());
					dialog.setEmptyListMessage("No managed beans found");
					dialog.setBlockOnOpen(true);
					dialog.setTitle("Select a managed beans");
					dialog.setMessage("Select a managed beans (* = any string, ? = any char):");
					
					if(dialog.open()==ListSelectionDialog.OK){
						String[] managedBean=(String[]) dialog.getFirstResult();
						if(managedBean != null){
							model = BeanModelFactory.createBeanModel(managedBean[1],project);
							model.setName(managedBean[0]);
							populateTree();
							beanClassText.setText(managedBean[0]);
							checkPageComplete();
							
						}
					}
				} catch (Exception ex) {
					showError("* There was an error getting managed beans"); CodeGenerators.logError("Exception of type " + ex.getClass().getName()
							+ " was thrown. Message is " + ex.getMessage()
							+ ". Exception occured whiles fetching matching beans", ex);
				}
			}
		});
		btnLoadConfig.setText("Load...");
		
		Label divider2 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		divider2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
		
		Label lblUiTemplateSource = new Label(container, SWT.NONE);
		lblUiTemplateSource.setText("UI Template Source:");
		
		final Text uiTemplateSrcText = new Text(container, SWT.BORDER);
		uiTemplateSrcText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				uiTemplateSrc = uiTemplateSrcText.getText();
			}
		});
		GridData gd_uiTemplateSrcText = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_uiTemplateSrcText.widthHint = 150;
		uiTemplateSrcText.setLayoutData(gd_uiTemplateSrcText);
		
		Button btnBrowseUiSrc = new Button(container, SWT.NONE);
		btnBrowseUiSrc.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(project==null){
					showError("* Select a container first.");
					return;
				}

				ResourceListSelectionDialog dialog = new ResourceListSelectionDialog(getShell(), project, IResource.FILE){

					@Override
					protected boolean select(IResource resource) {
						return "xhtml".equals(resource.getFileExtension());
					}					
				};
				dialog.setTitle("Select template file...");
				
				if(dialog.open()==ResourceListSelectionDialog.OK){
					if(dialog.getResult()!=null && dialog.getResult().length>0){
						IResource selectedResource = (IResource) dialog.getResult()[0];
						IContainer parent = selectedResource.getParent();
						String[] selectedSegments = parent.getProjectRelativePath().segments();
						String[] folderSegments = location.getProjectRelativePath().segments();
						uiTemplateSrcText.setText(CodeGenerators.relativePath(selectedSegments, folderSegments)+selectedResource.getName());
						uiTemplateSrc = uiTemplateSrcText.getText();
						checkPageComplete();
					}
				}
			}
		});
		
		btnBrowseUiSrc.setText("Browse...");
		
		Label lblPredifinedTemplates = new Label(container, SWT.NONE);
		lblPredifinedTemplates.setText("Predifined Templates:");
		
		final Combo predifinedTemplatesCombo = new Combo(container, SWT.READ_ONLY);
		predifinedTemplatesCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int selection=predifinedTemplatesCombo.getSelectionIndex();
				try {
					template = getDefaultTemplates().get(selection)[1];
					checkPageComplete();
				} catch (Exception ex) {
					showError("* Cannot load default templates");
					CodeGenerators.logError("Exception of type " + ex.getClass().getName()
							+ " was thrown. Message is " + ex.getMessage()
							+ ". Exception occured whiles fetching default templates", ex);
				}
			}
		});
		
		GridData gdPredefinedTemplatesCombo = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gdPredefinedTemplatesCombo.widthHint = 150;
		predifinedTemplatesCombo.setLayoutData(gdPredefinedTemplatesCombo);

		try {
			List<String[]> defaultTemplates = getDefaultTemplates();
			for(String[] templateData:defaultTemplates){
				predifinedTemplatesCombo.add(templateData[0]);
			}
		} catch (Exception ex) {
			showError("* Cannot load default templates"); CodeGenerators.logError("Exception of type " + ex.getClass().getName()
					+ " was thrown. Message is " + ex.getMessage()
					+ ". Exception occured whiles fetching default templates", ex);
		}
		new Label(container, SWT.NONE);
		
		Label lblCustomTemplate = new Label(container, SWT.NONE);
		lblCustomTemplate.setText("Custom Template");
		
		final Text customTemplateText = new Text(container, SWT.BORDER);
		customTemplateText.setEditable(false);
		customTemplateText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnBrowseCustomTemplates = new Button(container, SWT.NONE);
		btnBrowseCustomTemplates.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(pageGenerator !=null){
					FileDialog dialog = new FileDialog(getShell(),SWT.OPEN);
					dialog.setFilterPath(CodeGenerators.getDefault().getStateLocation().makeAbsolute().toFile().toString());
					String selectedFileName;
					if((selectedFileName=dialog.open())!=null){
						if(selectedFileName.lastIndexOf(File.separatorChar)!=-1)
							customTemplatefileName=selectedFileName.substring(selectedFileName.lastIndexOf(File.separatorChar)+1);
						else
							customTemplatefileName = selectedFileName;
						try {
							pageGenerator.loadCustomTemplate(selectedFileName, customTemplatefileName);
							customized = true;
							customTemplateText.setText(customTemplatefileName);
							checkPageComplete();
						} catch (IOException ex) {
							MessageDialog.openError(getShell(),"Error loading template", "Exception of type " + ex.getClass().getName()
									+ " was thrown. Message is "
									+ ex.getMessage()
									+ ". Exception occured whiles loading template "+customTemplatefileName);
						}
					}
				}
			}
		});
		btnBrowseCustomTemplates.setText("Browse...");

		Label lblCustomTagTemplateText = new Label(container, SWT.NONE);
		lblCustomTagTemplateText.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblCustomTagTemplateText.setText("Custom Tag Template:");
		
		final Text templateText = new Text(container, SWT.BORDER);
		templateText.setEditable(false);
		GridData gd_templateText = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_templateText.widthHint = 150;
		templateText.setLayoutData(gd_templateText);
		
		Button btnBrowseTagTemplates = new Button(container, SWT.NONE);
		btnBrowseTagTemplates.setToolTipText("Load a custom template");
		btnBrowseTagTemplates.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(getShell(),SWT.OPEN);
				dialog.setFilterExtensions(new String[]{"*.template.xhtml"});
				dialog.setFilterPath(CodeGenerators.getDefault().getStateLocation().makeAbsolute().toFile().toString());
				String file;
				if((file= dialog.open())!=null){
					try {
						loadTemplate(file);
						templateText.setText(file);
						template = file;
						checkPageComplete();
					} catch (Exception ex) {
						showError("* Cannot load template");
						CodeGenerators.logError("Exception of type " + ex.getClass().getName()
								+ " was thrown. Message is " + ex.getMessage()
								+ ". Exception occured whiles loading template: "+file, ex);
					}
				}
			}
		});
		btnBrowseTagTemplates.setSelection(true);
		btnBrowseTagTemplates.setText("Browse...");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		setPageComplete(false);
	}
	
	private void populateTree() {
		beanTree.removeAll();
		TreeItem rootItem = new TreeItem(beanTree, 0);
		rootItem.setImage(CodeGenerators.getBeanImage());
		rootItem.setText(model.getBeanClassName());
		for(BeanElement element:model.getBeanElements()){
			TreeItem item = new TreeItem(rootItem, 0);
			item.setImage(CodeGenerators.getTextfieldImage());
			item.setText(element.getValue()+":"+element.getElementClass());
		}
		rootItem.setExpanded(true);
	}

	private List<String[]> getManagedBeans() throws ParserConfigurationException, SAXException, IOException{
		File facesconfig = findFacesFile(project.getLocation().toFile());
		if(facesconfig != null){
			FileInputStream stream = new FileInputStream(facesconfig);
			SAXParser parser = CodeGenerators.getFactory().newSAXParser();
			FacesConfigHandler handler = new FacesConfigHandler();
			parser.parse(stream, handler);
			
			return handler.getManagedBeans();
		}
		return new ArrayList<String[]>();
	}
	
	private File findFacesFile(File file){
		if(file.isFile()&&file.getName().equals("faces-config.xml"))
			return file;
		if(file.isDirectory()){
			for(File subfile:file.listFiles()){
				File found;
				if((found=findFacesFile(subfile))!=null)
					return found;
			}
		}
		return null;
	}
	
	private void checkPageComplete(){
		boolean complete = template != null && !template.isEmpty() && model != null
				&& model.getName() != null && !model.getName().isEmpty()
				&& fileName != null && !fileName.isEmpty();
		setPageComplete(complete);
		if(complete)
			restoreDescription();
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
	
	protected void loadTemplate(String templateFile)
			throws ParserConfigurationException, SAXException, IOException {
		if(pageGenerator !=null)
			pageGenerator.loadTagTemplate(templateFile);
	}
	
	protected abstract List<String[]> getDefaultTemplates() throws ParserConfigurationException, SAXException, IOException;
}
