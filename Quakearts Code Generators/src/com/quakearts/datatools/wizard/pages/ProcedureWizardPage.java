package com.quakearts.datatools.wizard.pages;

import java.lang.reflect.InvocationTargetException;
import java.sql.Types;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.datatools.connectivity.ui.dse.dialogs.ProfileSelectionDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.quakearts.datatools.abstraction.Parameter;
import com.quakearts.datatools.abstraction.Procedure;
import com.quakearts.datatools.abstraction.WrapperClass;
import com.quakearts.datatools.generator.readers.DatabasesReader;
import com.quakearts.datatools.generator.readers.ProcedureParameterReader;
import com.quakearts.datatools.generator.readers.ProceduresReader;
import com.quakearts.tools.CodeGenerators;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public final class ProcedureWizardPage extends WizardPage {
	private DatabasesReader databasesReader = new DatabasesReader();
	private ProceduresReader proceduresReader = new ProceduresReader();
	private ProcedureParameterReader parameterReader = new ProcedureParameterReader();
	private Tree databasesTree;
	private Tree proceduresTree;
	private Tree procedureViewTree;
	private WrapperClass wrapperClass;
	private Button btnUseCache;
	private Text filterText;
	
	/**
	 * Create the wizard.
	 * @param wrapperClass 
	 */
	public ProcedureWizardPage(WrapperClass wrapperClass) {
		super("procedureWizardPage");
		setTitle("Get procedure");
		setDescription("Locate a procedure to wrap");
		this.wrapperClass = wrapperClass;
		setImageDescriptor(CodeGenerators.getQuakeartDescriptor());
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		final ProfileSelectionDialog selectionDialog = new ProfileSelectionDialog(getShell());
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(6, false));
		
		Label lblDatasourceProfile = new Label(container, SWT.NONE);
		lblDatasourceProfile.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDatasourceProfile.setText("Datasource profile");
		
		final Text profileText = new Text(container, SWT.BORDER);
		profileText.setEditable(false);
		GridData gd_profileText = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gd_profileText.widthHint = 150;
		profileText.setLayoutData(gd_profileText);
		
		Button btnBrowse = new Button(container, SWT.NONE);
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectionDialog.open();
				if(selectionDialog.getReturnCode()==ProfileSelectionDialog.OK){
					profileText.setText(selectionDialog.getCPName());
					IRunnableWithProgress op = new IRunnableWithProgress() {
						
						@Override
						public void run(IProgressMonitor monitor) throws InvocationTargetException,
								InterruptedException {
							monitor.beginTask("Loading databases....", 2);
							List<String[]> databases = databasesReader.getDatabases(selectionDialog.getCPName(),filterText.getText(),btnUseCache.getSelection());
							monitor.worked(1);
							if(databases!=null){								
								databasesTree.removeAll();
								TreeItem rootItem = new TreeItem(databasesTree, 0);
								rootItem.setImage(CodeGenerators.getDatabasesImage());
								String currentDatabase=null;
								TreeItem databaseItem = null;
								for(String[] database:databases){
									if(currentDatabase==null||!currentDatabase.equals(database[1])){
										databaseItem = new TreeItem(rootItem,0);
										databaseItem.setImage(CodeGenerators.getDatabaseImage());
										databaseItem.setText(database[1]);
										currentDatabase = database[1];
									}
									TreeItem item = new TreeItem(databaseItem, 0);
									item.setImage(CodeGenerators.getSchemaImage());
									item.setText(database[1]+"."+database[0]);
									item.setData(database);
								}
								rootItem.setExpanded(true);
								monitor.worked(1);
							} else {
								monitor.worked(1);
								MessageDialog.openError(getShell(),"Cannot load databases", "Cannot load databases. Database reader returned null");
							}
							monitor.done();
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
					if(!databasesTree.isEnabled()){
						databasesTree.setEnabled(true);
					}
				}
			}
		});
		
		GridData gd_btnBrowse = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnBrowse.widthHint = 96;
		btnBrowse.setLayoutData(gd_btnBrowse);
		btnBrowse.setText("Browse...");
		
		btnUseCache = new Button(container, SWT.CHECK);
		btnUseCache.setSelection(true);
		btnUseCache.setText("Use cache");
		new Label(container, SWT.NONE);
		
		Label lblDatabases = new Label(container, SWT.NONE);
		lblDatabases.setText("Databases:");
		new Label(container, SWT.NONE);
		
		Label lblProcedures = new Label(container, SWT.NONE);
		lblProcedures.setText("Procedures:");
		new Label(container, SWT.NONE);
		
		Label lblProcedure = new Label(container, SWT.NONE);
		lblProcedure.setText("Procedure:");
		new Label(container, SWT.NONE);
		
		databasesTree = new Tree(container, SWT.BORDER);
		databasesTree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(databasesTree.getSelection()[0].getData() !=null){
					final String[] database = (String[]) databasesTree.getSelection()[0].getData();
					IRunnableWithProgress op = new IRunnableWithProgress() {
						@Override
						public void run(IProgressMonitor monitor) throws InvocationTargetException,
								InterruptedException {
							monitor.beginTask("Fetching procedures...", 2);
							
							if(database!=null){
								List<String[]> procedures = proceduresReader.getProcedures(database[1], database[0], profileText.getText(),btnUseCache.getSelection());
								monitor.worked(1);
								
								proceduresTree.removeAll();
								TreeItem rootItem = new TreeItem(proceduresTree, 0);
								rootItem.setImage(CodeGenerators.getDatabaseImage());
								rootItem.setText(database[1]+"."+database[0]);
								for(String[] procedure:procedures){
									TreeItem item = new TreeItem(rootItem, 0);
									item.setImage(CodeGenerators.getScriptImage());
									item.setText((procedure[1] != null && !procedure[1].trim().isEmpty()
											? procedure[1] + "." : "") + procedure[2]);
									item.setData(procedure);
								}
								monitor.worked(1);
							}else {
								monitor.worked(2);
							}
							monitor.done();
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
					if(!proceduresTree.isEnabled())
						proceduresTree.setEnabled(true);

				}
			}
		});
		
		databasesTree.setEnabled(false);
		GridData gd_databasesTree = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd_databasesTree.widthHint = 150;
		databasesTree.setLayoutData(gd_databasesTree);
		
		proceduresTree = new Tree(container, SWT.BORDER);
		proceduresTree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Procedure procedure = wrapperClass.getProcedure();
				if(!procedure.getProcedureName().isEmpty()||procedure.getParameters().size()!=0||procedure.getProcedureResults().size()!=0){
					procedure.setProcedureName("");
					procedure.getParameters().clear();
					procedure.getProcedureResults().clear();
					setPageComplete(false);
				}
			}
		});
		proceduresTree.setEnabled(false);
		GridData gd_proceduresTree = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd_proceduresTree.widthHint = 150;
		proceduresTree.setLayoutData(gd_proceduresTree);
		
		procedureViewTree = new Tree(container, SWT.BORDER);
		procedureViewTree.setEnabled(false);
		GridData gd_procedureViewTree = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd_procedureViewTree.widthHint = 150;
		procedureViewTree.setLayoutData(gd_procedureViewTree);
		
		final TreeEditor procedureViewEditor = new TreeEditor(procedureViewTree);
		procedureViewEditor.grabHorizontal = true;
        procedureViewEditor.horizontalAlignment = SWT.LEFT;
        procedureViewEditor.minimumWidth = 50;

        procedureViewTree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
                if (!(e.item instanceof TreeItem)) return;
                
                final TreeItem item = (TreeItem)e.item;
                if(item.getData()==null) return;

                final Text parameterText = new Text(procedureViewTree, SWT.NONE);
                parameterText.setText(item.getText());
                
                parameterText.addFocusListener(new FocusListener() {
					@Override
					public void focusLost(FocusEvent paramFocusEvent) {
                        Text text = (Text)procedureViewEditor.getEditor();
                        if(!text.getText().isEmpty()){
                        	item.setText(text.getText());
                        	if(item.getData() instanceof Parameter){
                        		if(text.getText().matches("[a-zA-Z0-9_]+"))
                        			((Parameter)item.getData()).setVariableName(text.getText());
                        		else
                                	MessageDialog.openError(getShell(),"Invalid entry", "Text must be a valid java name (no spaces, capital or small letters, underscore and or numbers)");
                        			
                        	} else if(item.getData() instanceof Procedure)
                        		((Procedure)item.getData()).setProcedureName(text.getText());
                        } else {
                        	MessageDialog.openError(getShell(),"Invalid entry", "Text cannot be empty");
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
                procedureViewEditor.setEditor(parameterText, item);
			}
		});
		
		Label lblFilter = new Label(container, SWT.NONE);
		lblFilter.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblFilter.setText("Schema Filter");
		
		filterText = new Text(container, SWT.BORDER);
		filterText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Button btnGetMetaData = new Button(container, SWT.NONE);
		btnGetMetaData.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(proceduresTree.getSelection()==null || proceduresTree.getSelection().length<=0)
					MessageDialog.openError(getShell(),"Invalid selection", "You must select a procedure to continue...");
				else{
					final String[] procedure = (String[]) proceduresTree.getSelection()[0].getData();
					IRunnableWithProgress op = new IRunnableWithProgress() {
						
						@Override
						public void run(IProgressMonitor monitor) throws InvocationTargetException,
								InterruptedException {
							monitor.beginTask("Loading procedure meta data...", 2);
							
							parameterReader.getProcedureAndParameters(procedure[0], procedure[1], procedure[2], profileText.getText(), wrapperClass.getProcedure());
							monitor.worked(1);
							
							procedureViewTree.removeAll();
							TreeItem rootItem = new TreeItem(procedureViewTree, 0);
							rootItem.setText(wrapperClass.getProcedure().getProcedureName());
							rootItem.setImage(CodeGenerators.getScriptImage());
							rootItem.setData(wrapperClass.getProcedure());
							for(Parameter parameter:wrapperClass.getProcedure().getParameters()){
								TreeItem item = new TreeItem(rootItem, 0);
								item.setText(parameter.getVariableName());
								item.setImage(parameter.getType() == Types.DATE 
										|| parameter.getType() == Types.TIME
										|| parameter.getType() == Types.TIMESTAMP
										?CodeGenerators.getCalendarImage():CodeGenerators.getTextfieldImage());
								item.setData(parameter);
							}
							monitor.worked(1);
							monitor.done();
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
					if(!procedureViewTree.isEnabled())
						procedureViewTree.setEnabled(true);
					
					setPageComplete(true);
				}
			}
		});
		btnGetMetaData.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		btnGetMetaData.setText("Get meta-data...");
		setPageComplete(false);
	}
	
}
