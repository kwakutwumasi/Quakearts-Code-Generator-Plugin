package com.quakearts.webtools.codegenerators.wizard;

import static com.quakearts.tools.CodeGenerators.PLUGIN_ID;
import static com.quakearts.tools.CodeGenerators.getJAXBContext;
import static com.quakearts.tools.CodeGenerators.logError;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.osgi.framework.Bundle;
import com.quakearts.tools.CodeGenerators;
import com.quakearts.webtools.codegenerators.ScaffoldingTemplateGenerator;
import com.quakearts.webtools.codegenerators.model.Bean;
import com.quakearts.webtools.codegenerators.model.BeanModel;
import com.quakearts.webtools.codegenerators.model.BeanType;
import com.quakearts.webtools.codegenerators.model.Folder;
import com.quakearts.webtools.codegenerators.model.TemplateGroup;
import com.quakearts.webtools.codegenerators.model.PropertyEntry;
import com.quakearts.webtools.codegenerators.model.Resource;
import com.quakearts.webtools.codegenerators.model.Scaffolding;
import com.quakearts.webtools.codegenerators.model.Template;
import com.quakearts.webtools.codegenerators.wizard.pages.EditScaffoldingPropsWizardPage;
import com.quakearts.webtools.codegenerators.wizard.pages.ScaffoldingWizardPage;

public class ScaffoldingWizard extends Wizard implements INewWizard {

	private static final Pattern VARIABLE = Pattern.compile("@\\{([a-zA-Z0-9]+\\.*)*\\}");
	private static Map<String, String> forbiddenIds = new ConcurrentHashMap<String, String>();
	private static String[] predefinedScaffoldingTemplates;
	private static final String EMPTY ="";
	private static Map<String, Scaffolding> scaffoldingTemplates = new ConcurrentHashMap<String, Scaffolding>();

	private IProject project;
	private Scaffolding scaffolding;
	private ScaffoldingWizardPage scaffoldingPage;
	private ScaffoldingTemplateGenerator generator;
	private EditScaffoldingPropsWizardPage editScaffoldingPage;
	private boolean errors;

	public ScaffoldingWizard() {
		setWindowTitle("Scaffolding Tool");
		generator = new ScaffoldingTemplateGenerator();
	}
	
	@Override
	public void addPages() {
		addPage((scaffoldingPage=new ScaffoldingWizardPage(project)));
		addPage((editScaffoldingPage=new EditScaffoldingPropsWizardPage(project)));
	}
	
	@Override
	public boolean needsProgressMonitor() {
		return true;
	}
	
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if(page == scaffoldingPage && editScaffoldingPage.getScaffolding()==null){
			errors = false;
			if(scaffoldingPage.getScaffolding()==null){
				IRunnableWithProgress op = new IRunnableWithProgress() {
					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException,
						InterruptedException {
						monitor.beginTask("Loading scaffolding file", IProgressMonitor.UNKNOWN);
						try {
							InputStream is = scaffoldingPage.getScaffoldingFile().getLocationURI().toURL().openStream();
							try {
								Unmarshaller unmarshaller = CodeGenerators.getJAXBContext().createUnmarshaller();
								scaffolding = (Scaffolding) unmarshaller.unmarshal(is);
								if(isInValidId(scaffolding.getId())){
									CodeGenerators.logError("Invalid scaffolding ID specified. Scaffolding ID is a default ID and cannot be specified", null);
									errors = true;
									return;
								}
														
								if(scaffolding.getTemplateGroups().isEmpty()){
									CodeGenerators.logError("Invalid scaffolding. Scaffolding does not contain any pages for generation.", null);
									errors = true;
									return;
								}
								
								for(Resource resource:scaffolding.getResources()){
									if(resource.isTemplate()){
										IFile file = project.getFile(resource.getLocation());
										if(file.exists()){
											InputStream ris = null;;
											try {
												ris = file.getContents();
												loadTemplate(ris, scaffolding.getId()+"/"+resource.getLocation());
											} catch (CoreException e) {
												CodeGenerators.logError(
														"Error processing resource. Resource "
																+ resource.getLocation() + " could not be loaded.",
														null);
												errors = true;
												return;
											} finally {
												try {
													ris.close();
												} catch (Exception e2) {
												}
											}
										} else {
											if (StringResourceLoader.getRepository()
													.getStringResource(resource.getLocation()) == null) {
												CodeGenerators.logError(
														"Invalid scaffolding template specified. Scaffolding resource "
																+ resource.getLocation() + " cannot be found.",
														null);
												errors = true;
												return;
											}
											resource.setPredefined(true);
										}
									}
								}
								
								for(TemplateGroup page:scaffolding.getTemplateGroups()) {
									for(Template template:page.getTemplates()) {
										if(template.getLocation()==null) {
											CodeGenerators.logError("Invalid scaffolding. Scaffolding page is missing name attribute.", null);
											errors = true;
											return;
										}
										
										InputStream pis = null;
										try {
											IFile file = project.getFile(template.getLocation());
											if(file.exists()) {
												pis = file.getContents();
												loadTemplate(pis, scaffolding.getId()+"/"+template.getLocation());
											} else {
												if (StringResourceLoader.getRepository()
														.getStringResource(template.getLocation()) == null) {
													CodeGenerators.logError(
															"Invalid scaffolding template specified. Scaffolding template "
																	+ template.getLocation() + " cannot be found.",
															null);
													errors = true;
													return;
												}
												template.setPredefined(true);
											}
										} catch (CoreException e) {
											CodeGenerators.logError("Could not obtain input file "+template.getLocation(), e);
											errors = true;
											return;									
										} finally {
											try {
												pis.close();
											} catch (Exception e) {
											}
										}
									}
								}
								
								getScaffoldingTemplates().put(project.getName() 
										+ ":" + scaffoldingPage.getScaffoldingFile().getName(), scaffolding);
							} finally {
								is.close();
							}
						} catch (IOException e) {
							CodeGenerators.logError("Could not get scaffolding file from location "
									+ scaffoldingPage.getScaffoldingFile().getLocationURI(), e);
							return;
						} catch (JAXBException e){
							CodeGenerators.logError("Parse error whiles obtaining scaffolding file from location "
									+ scaffoldingPage.getScaffoldingFile().getLocationURI(), e);
							return;
						} finally {
							monitor.done();
						}
					}
				};
				
				if(scaffolding==null && scaffoldingPage.getScaffoldingFile()!=null && scaffoldingPage.getScaffoldingFile().getLocationURI()!=null){
					try{
						getContainer().run(false, false, op);
						if(errors)
							MessageDialog.openError(getShell(),"Error", "There were errors during page generation. Check logs for more details");

					} catch (InterruptedException e) {
					} catch (InvocationTargetException e) {
						Throwable realException = e.getTargetException();
						MessageDialog.openError(getShell(), "Error", realException.getMessage());
					}
				}
				editScaffoldingPage.setScaffolding(scaffolding);
			} else
				editScaffoldingPage
				.setScaffolding(scaffoldingPage.getScaffolding());
			
			editScaffoldingPage
				.setProject(scaffoldingPage.getProject());
		}
		return super.getNextPage(page);
	}
	
	@Override
	public boolean performFinish() {
		scaffolding = scaffoldingPage.getScaffolding();
		if(project==null && scaffoldingPage.getProject()==null){
			MessageDialog.openError(getShell(),"Fatal error", "No project has been selected for this action");
			CodeGenerators.logError("No project has been selected for this action.", null);
			return false;
		}
		
		errors = false;
		
		final boolean markChanges = scaffoldingPage.markChangesSelected();
		
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {							
				Bundle bundle = Platform.getBundle(CodeGenerators.PLUGIN_ID);
				
				if(scaffolding==null){
					CodeGenerators.logError("Parse error whiles obtaining scaffolding file from location "
							+ scaffoldingPage.getScaffoldingFile().getLocationURI(), null);
					return;
				}
				
				if(scaffoldingPage.getBeanModels().isEmpty()){
					CodeGenerators.logError("No beans elements was selected", null);
					MessageDialog.openError(getShell(),"Error","Parse error whiles obtaining scaffolding file ");
					return;
				}
				
				if(scaffolding.getFolderStructure()==null || scaffolding.getFolderStructure().getFolder()==null){
					for(TemplateGroup templateGroup:scaffolding.getTemplateGroups()){
						if(templateGroup.getFolderID()!=null){
							CodeGenerators.logError("Scaffolding file is missing a folder with ID " + templateGroup.getFolderID(), null);
							MessageDialog.openError(getShell(),"Error",
									"The scaffolding file is missing folder entry with ID " + templateGroup.getFolderID());
							return;
						}
					}
				}
								
				monitor.beginTask("Building site...", scaffolding.getTotalUnits()+1);
				try{
					VelocityContext context = new VelocityContext();
					
					for(PropertyEntry entry:scaffolding.getProperties().getEntries()){
						context.put(entry.getProperty(), entry.getValue());
					}
					
					Map<String, IFolder> folderStructure = new HashMap<String, IFolder>();
					Folder rootFolder = null;
					
					monitor.worked(1);
					if(scaffolding.getFolderStructure().getFolder()!=null) {
						rootFolder = scaffolding.getFolderStructure().getFolder();
						for(Folder relativeFolder:rootFolder.getFolders()){
							IFolder folder = project.getFolder(relativeFolder.getName());
							if(!createFolder(folder, relativeFolder, monitor, folderStructure)){
								MessageDialog.openError(getShell(),"Error", "An error occured. Check logs for details.");
								return;
							}
						}
					}
					
					context.put("beanModels", scaffoldingPage.getBeanModels());
					context.put("scaffolding", scaffolding);
					context.put("nameModelMapping", scaffoldingPage.getNameModelMapping());
					context.put("classModelMapping", scaffoldingPage.getClassModelMapping());
										
					for(BeanModel beanModel: scaffoldingPage.getBeanModels()){
						context.put("beanModel", beanModel);
						for(TemplateGroup templateGroup:scaffolding.getTemplateGroups()) {
							if(canGenerate(beanModel,templateGroup)) {
								for(Template template:templateGroup.getTemplates()){
									String filename;
									
									filename = resolve(template.getFilename(), beanModel, scaffolding.getProperties().getPropertyMap());
									String name = (template.isPredefined()?"":scaffolding.getId()+"/")+template.getLocation();
									
									IFile file;
									if(templateGroup.getFolderID()==null 
											|| templateGroup.getFolderID().trim().isEmpty() 
											|| templateGroup.getFolderID().equals("root"))
										file = project.getFile(filename);
									else {
										IFolder folder = folderStructure.get(templateGroup.getFolderID());
										if(folder==null){
											CodeGenerators.logError("Scaffolding file is missing folder with folderID "+templateGroup.getFolderID(), null);
											errors=true;
											monitor.worked(1);
											continue;
										}
										file = folder.getFile(filename);
									}
									
									InputStream is = generator.generatePage(context, name);
									
									if(file.exists()){
										try(InputStream oldIs = file.getContents();) {
											is = consolidateDifferences(oldIs, is, markChanges);
											file.setContents(is, true, true, monitor);
										} catch (CoreException | IOException e) {
											CodeGenerators.logError("Error generating page "+template.getLocation()+" for beans "+beanModel.getName(), e);
											errors = true;
										}
									} else {
										try {
											file.create(is, true, monitor);
										} catch (CoreException e) {
											CodeGenerators.logError("Error generating page "+template.getLocation()+" for beans "+beanModel.getName(), e);
											errors = true;
										}
									}
																
									monitor.worked(1);
								}
							} else {
								monitor.worked(templateGroup.getTemplates().size());
							}
						}
					}		
					
					for(Resource resource:scaffolding.getResources()){
						IFile file;
						String resourceFilename = resource.getLocation();
						
						if(resourceFilename == null){
							CodeGenerators.logError("Error generating resource. Resource does not have a location.", null);
							errors = true;
							monitor.worked(1);
							continue;
						}
												
						String filename;
						if(resource.getFilename()==null){
							if(resourceFilename.indexOf("/")!=-1)
								filename = resource.getLocation().substring(resource.getLocation().lastIndexOf("/")+1);
							else 
								filename = resourceFilename;
						} else {
							filename = resource.getFilename();
						}
						filename = resolve(filename, null, scaffolding.getProperties().getPropertyMap());
						
						if(resource.getFolderID()==null 
								|| resource.getFolderID().trim().isEmpty()
								|| resource.getFolderID().equals("root")){
							file = project.getFile(filename);
						} else {
							IFolder folder = folderStructure.get(resource.getFolderID());
							file = folder.getFile(filename);
						}
						
						InputStream is;
						if(!resource.isTemplate()){
							URL resourceURL = bundle.getResource("com/quakearts/webtools/codegenerators/scaffolding/"+resourceFilename);
							if(resourceURL==null){
								IFile resourcefile = project.getFile(resourceFilename);
								
								if(resourcefile.exists()){
									try {
										is = resourcefile.getContents();
									} catch (CoreException e) {
										CodeGenerators.logError("Error generating resource "+resourceFilename+". Resource could not be loaded.", e);
										errors = true;
										monitor.worked(1);
										continue;
									}
								} else {
									try {
										is = new FileInputStream(resourceFilename);
									} catch (FileNotFoundException e) {
										CodeGenerators.logError("Error generating resource "+resourceFilename+". Resource not found.", e);
										errors = true;
										monitor.worked(1);
										continue;
									}
								}
								
								if(is==null){
									CodeGenerators.logError("Error generating resource "+resourceFilename+". Resource not found.", null);
									errors = true;
									monitor.worked(1);
									continue;
								}
							} else {
								try {
									is = resourceURL.openStream();
								} catch (IOException e) {
									CodeGenerators.logError("Error generating resource "+resourceFilename+". Resource not found.", e);
									errors = true;
									monitor.worked(1);
									continue;
								}
							}
						} else {
							if(resource.isPredefined()){
								is = generator.generatePage(context, resource.getLocation());
							} else {
								is = generator.generatePage(context, scaffolding.getId()+"/"+resource.getLocation());
							}
						}
						
						if(file.exists()){
							if(resource.isTemplate()){
								try(InputStream oldIs = file.getContents();) {
									is = consolidateDifferences(oldIs, is, markChanges);
									file.setContents(is, true, true, monitor);
								} catch (CoreException | IOException e) {
									CodeGenerators.logError("Error generating resource "+resourceFilename+". Resource could not be written.", e);
									errors = true;
								} finally {
									try {
										is.close();
									} catch (IOException e) {
									}
								}
							}
						} else {
							try {
								file.create(is, true, monitor);
							} catch (CoreException e) {
								CodeGenerators.logError("Error generating resource "+resourceFilename+". Resource could not be written.", e);
								errors = true;
							} finally {
								try {
									is.close();
								} catch (IOException e) {
								}
							}
						}
						monitor.worked(1);
					}					
				} finally {
					monitor.done();
				}
			}
		};
		
		try {			
			getContainer().run(true, false, op);
			if(errors)
				MessageDialog.openError(getShell(),"Error", "There were errors during page generation. Check logs for more details");
			
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		return true;
	}
	
	private boolean createFolder(IFolder folder, Folder scaffoldFolder, IProgressMonitor monitor, Map<String, IFolder> folderStructure){
		if(!folder.exists()){
			try {
				folder.create(false, true, monitor);
				monitor.worked(1);
			} catch (CoreException e) {
				CodeGenerators.logError("Could not create folder "+folder.getName(), e);
				return false;									
			}
		}
		folderStructure.put(scaffoldFolder.getFolderID(), folder);
		boolean result = true;
		for(Folder subFolder:scaffoldFolder.getFolders()){
			result = result && createFolder(folder.getFolder(subFolder.getName()), subFolder, monitor, folderStructure); 
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private InputStream consolidateDifferences(InputStream oldIs, InputStream newIs, boolean markChanges) throws IOException {
		BufferedReader oldBuffer = new BufferedReader(new InputStreamReader(oldIs)),
				newBuffer = new BufferedReader(new InputStreamReader(newIs));
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream(oldIs.available()>newIs.available()?oldIs.available():newIs.available());
		
		ArrayList<String> newLines = new ArrayList<>();
		HashMap<String, Object> newLinesMap = new HashMap<>();
		String line, comparator;
		boolean ineditedportion = false;
		
		int lineCount = 0;
		while ((line = newBuffer.readLine())!=null) {
			newLines.add(line);
			if(!newLinesMap.containsKey(line)){
				newLinesMap.put(line, lineCount++);
			} else {
				Object obj = newLinesMap.get(line);
				LinkedList<Integer> lineCounts;
				if(obj instanceof Integer){
					lineCounts = new LinkedList<>();
					lineCounts.add((Integer) obj);
					newLinesMap.put(line, lineCounts);
				} else {
					lineCounts = (LinkedList<Integer>) obj;
				}
				
				lineCounts.add(lineCount++);
			}
		}
		
		lineCount = 0;
		int scantill;
		while ((line = oldBuffer.readLine())!=null && lineCount < newLines.size()) {
			comparator = newLines.get(lineCount);
			if(line.equals(comparator)){
				if(markChanges && ineditedportion){
					bos.write("<<<End: Edited Portion>>>\r\n".getBytes());
					ineditedportion = false;
				}

				bos.write(line.getBytes());
				bos.write("\r\n".getBytes());
				lineCount++;
			} else {
				if(newLinesMap.containsKey(line)){
					if(markChanges && ineditedportion){
						bos.write("<<<End: Edited Portion>>>\r\n".getBytes());
						ineditedportion = false;
					}

					Object obj = newLinesMap.get(line);
					if(obj instanceof Integer){
						scantill = (Integer) obj;
						newLinesMap.remove(line);
					} else {
						LinkedList<Integer> lineCounts = (LinkedList<Integer>) obj;
						scantill = lineCounts.removeFirst();
						if(lineCounts.isEmpty()){
							newLinesMap.remove(line);
						}
					}
					
					if(markChanges)
						bos.write("<<<Start: New Changes>>>\r\n".getBytes());
					for(;lineCount<scantill;lineCount++){
						line = newLines.get(lineCount);
						bos.write(line.getBytes());
						bos.write("\r\n".getBytes());
					}

					if(markChanges)
						bos.write("<<<End: New Changes>>>\r\n".getBytes());

					line = newLines.get(lineCount);
					bos.write(line.getBytes());
					bos.write("\r\n".getBytes());
					
					lineCount++;					

				} else {
					if(markChanges && !ineditedportion){
						bos.write("<<<Start: Edited Portion>>>\r\n".getBytes());
						ineditedportion = true;
					}
					
					bos.write(line.getBytes());
					bos.write("\r\n".getBytes());
					
				}
			}
		}

		if(markChanges && ineditedportion){
			bos.write("<<<End: Edited Portion>>>\r\n".getBytes());
			ineditedportion = false;
		}
		
		if(line!=null){
			if(markChanges)
				bos.write("<<<Start: New Changes>>>\r\n".getBytes());
			
			do {				
				bos.write(line.getBytes());
				bos.write("\r\n".getBytes());
			} while((line = oldBuffer.readLine())!=null);

			if(markChanges)
				bos.write("<<<End: New Changes>>>\r\n".getBytes());
		} else if(lineCount < newLines.size()){
			for(;lineCount<newLines.size();lineCount++){
				line = newLines.get(lineCount);
				bos.write(line.getBytes());
				bos.write("\r\n".getBytes());
			}
		}
				
		return new ByteArrayInputStream(bos.toByteArray());
	}


	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		getPredefinedScaffoldingTemplates();
		Object element = selection.getFirstElement();
		IResource resource;
		if (element instanceof IResource){
			resource = (IResource) element;
		} else if (!(element instanceof IAdaptable)){
			return;
		} else {
			IAdaptable adaptable = (IAdaptable) element;
			Object adapter = adaptable.getAdapter(IResource.class);
			resource = (IResource) adapter;
		}
		
		project = resource.getProject();
	}
	
    public boolean canGenerate(BeanModel model, TemplateGroup templateGroup){
    	if(model==null || model.getBeanClass()==null)
    		return false;
    	
    	if(!templateGroup.getBeans().isEmpty()){
    		for(Bean bean:templateGroup.getBeans()){
    			BeanType type;
    			String restrictionType;
    			String value;
    			if(bean.getBeanClass()!=null){
    				type = bean.getBeanClass();
    			} else if(bean.getBeanInterface()!=null) {
    				type = bean.getBeanInterface();
    			} else {
    				return true;
    			}
    			
    			restrictionType = type.getRestrictionType();
    			value = type.getValue();
    			
    			if(restrictionType.equals(BeanType.MUSTBE)){
    				return type.translate(model.getBeanClass().getName().equals(value));
    			} else if(restrictionType.equals(BeanType.CHILDOF)){
    				Class<?> parent = model.getBeanClass().getSuperclass();
    				do {
    					if(parent.getName().equals(value))
    						return type.translate(true);
    				} while((parent=parent.getSuperclass())!=null);
    				return type.translate(false);
    			} else if(restrictionType.equals(BeanType.NAMELIKE)){
    				return type.translate(model.getBeanClass().getName().matches(value));
    			} else if(restrictionType.equals(BeanType.ANNOTATION)){
					for(Annotation annotation:model.getClass().getAnnotations()){
						if(annotation.annotationType().getName().equals(value))
							return type.translate(true);
					}
					return type.translate(false);
    			} else if(restrictionType.equals(BeanType.HASMETHOD)){
					for(Method method:model.getBeanClass().getMethods()){
						if(method.getName().equals(value))
							return type.translate(true);
					}
    				return type.translate(false);
    			}
    		}
    	}
    	return true;
    }
	
	private String resolve(String variableString, BeanModel beanModel, Map<String, String> context){
    	Matcher matcher = VARIABLE.matcher(variableString);
    	while(matcher.find()){
    		String variable = matcher.group();
    		if(variable.length()>3){
    			String wrappedVariable = variable.substring(2,variable.length()-1);
    			String resolved;
    			if(beanModel != null && wrappedVariable.equals("beanName")){
    				resolved = beanModel.getName();
    			} else if (beanModel != null && wrappedVariable.equals("beanClass")){
    				resolved = beanModel.getBeanClassName();
    			} else {
	    			resolved = context.get(wrappedVariable);
    			}
    			if(resolved!=null){
    				variableString = variableString.replace(variable, resolved);
    			}
    		}
    	}
    	return variableString;
    }

	public static String[] getPredefinedScaffoldingTemplates() {
		if(predefinedScaffoldingTemplates==null){
			
			Bundle bundle = Platform.getBundle(PLUGIN_ID);
			Properties props = new Properties();
			InputStream is = null;
			Unmarshaller unmarshaller;
			try {
				unmarshaller = getJAXBContext().createUnmarshaller();
			} catch (JAXBException e) {
				logError("Could not load predifined scaffolding entries", e);
				return null;
			}
			
			try {
				is = bundle.getResource("com/quakearts/webtools/codegenerators/scaffolding/predefined.properties").openStream();
				props.load(is);
				String[] templates = props.getProperty("templates").split(";");
				ArrayList<String> tempList = new ArrayList<String>();
				for(String templateEntry:templates){
					String[] nameValue = templateEntry.split("/",2);
					InputStream fis = bundle.getResource("com/quakearts/webtools/codegenerators/scaffolding/"+nameValue[0]).openStream();
					try {
						Object scaffoldingObject = unmarshaller.unmarshal(fis);
						if(scaffoldingObject instanceof Scaffolding){
							tempList.add(nameValue[1]);
							
							Scaffolding scaffolding=(Scaffolding) scaffoldingObject;
							forbiddenIds.put(scaffolding.getId(), EMPTY);
							
							for(Resource resource:scaffolding.getResources()){
								if(resource.isTemplate()){
									InputStream ris = bundle.getResource("com/quakearts/webtools/codegenerators/scaffolding/"
											+resource.getLocation()).openStream();
									try {
										loadTemplate(ris, scaffolding.getId()+"/"+resource.getLocation());
									} finally {
										ris.close();
									}
								}
							}
							
							for(TemplateGroup templateGroup:scaffolding.getTemplateGroups()){
								for(Template template:templateGroup.getTemplates()){
									InputStream pis = bundle.getResource("com/quakearts/webtools/codegenerators/scaffolding/"
											+template.getLocation()).openStream();
									try {
										loadTemplate(pis, scaffolding.getId() + "/" + template.getLocation());
									} finally {
										try {
											pis.close();
										} catch (Exception e) {
										}
									}
								}
							}
							scaffoldingTemplates.put(nameValue[1], (Scaffolding) scaffoldingObject);
						}
					} finally {
						try {
							fis.close();
						} catch (Exception e) {
						}
					}
				}
				
				predefinedScaffoldingTemplates = tempList.toArray(new String[tempList.size()]);
			} catch (Exception e) {
				logError("Could not load predifined scaffolding entries", e);
			} finally {
				try {
					is.close();
				} catch (Exception e2) {
				}
			}
			
		}
		return predefinedScaffoldingTemplates;
	}
	
	private static void loadTemplate(InputStream in, String name) throws IOException{
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line;
		while((line=reader.readLine())!=null){
			builder.append(line).append("\r\n");
		}
		StringResourceLoader.getRepository().putStringResource(name,
				builder.toString());
	}
	
	public static boolean isInValidId(String scaffoldingId){
		return forbiddenIds.containsKey(scaffoldingId);
	}
	
	public static Map<String, Scaffolding> getScaffoldingTemplates() {
		return scaffoldingTemplates;
	}
	
	public static void clearUserTemplates(){
		for(String key:scaffoldingTemplates.keySet()){
			for(String predifinedKey:predefinedScaffoldingTemplates){
				if(key.equals(predifinedKey))
					continue;
			}
			
			scaffoldingTemplates.remove(key);
		}
	}
	
}
