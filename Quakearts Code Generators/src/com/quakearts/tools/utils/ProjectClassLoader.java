package com.quakearts.tools.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import com.quakearts.tools.CodeGenerators;

public class ProjectClassLoader extends ClassLoader {
	private IProject project;
	private List<String> notFoundClasses = new ArrayList<>();
	
	public ProjectClassLoader(IProject project, ClassLoader loader) {
		super(loader);
		if(project==null)
			throw new NullPointerException("Project cannot be null");
		
		this.project = project;
	}
	
	@Override
	protected Class<?> findClass(String className) throws ClassNotFoundException {
		if(notFoundClasses.contains(className)) //Speed up processing
			throw new ClassNotFoundException("Class "+className+" not found in binary files of reference project "+project.getName());
			
		byte[] classBytes = findBytesInProject(className);
		if(classBytes!=null)
			return defineClass(className, classBytes, 0, classBytes.length);
		else {
			notFoundClasses.add(className);
			throw new ClassNotFoundException("Class "+className+" not found in binary files of reference project "+project.getName());
		}
	}
	
	private byte[] findBytesInProject(String className) throws ClassNotFoundException{
		if(project==null)
			throw new ClassNotFoundException("No project has been specified");
		
		byte[] classBytes = null;
		int dotIndex = className.lastIndexOf(".");
		String simplName = dotIndex!=-1?className.substring(dotIndex+1):className;
		String packageName = dotIndex!=-1?className.substring(0, dotIndex):"";
		IJavaProject javaProject = JavaCore.create(project);
		
		try{
			for(IPackageFragmentRoot root:javaProject.getPackageFragmentRoots()){
				if(root.getKind()==IPackageFragmentRoot.K_SOURCE){
					IPath outputPath = javaProject.getOutputLocation();
					IFile file;
					
					IPath path = outputPath
							.append(new Path(className.replace(".", "/") + ".class"));
					file = project.getFile(path.removeFirstSegments(1));
					if(file==null||!file.exists()){
						IClasspathEntry entry = root.getRawClasspathEntry();
						outputPath = entry.getOutputLocation();
						if(outputPath!=null){
							path = outputPath
									.append(new Path(className.replace(".", "/") + ".class"));
							file = project.getFile(path.removeFirstSegments(1));
						}
					}
					
					if(file!=null && file.exists()){
						ByteArrayOutputStream bos = new ByteArrayOutputStream();
						InputStream is = file.getContents();
						byte[] buffer=new byte[1024];
						int read;
						while((read=is.read(buffer))!=-1){
							bos.write(buffer,0,read);
						}
						classBytes = bos.toByteArray();
						break;
					}
				} else if(root.getKind()==IPackageFragmentRoot.K_BINARY) {
					for(IJavaElement child:root.getChildren()){
						if(child.getElementType()==IJavaElement.PACKAGE_FRAGMENT){
							IClassFile file = ((IPackageFragment)child).getClassFile(simplName+".class");
							if(file!=null && file.exists()
									&& child.getElementName().equals(packageName)){
								classBytes = file.getBytes();
								break;
							}
						}
					}
				}
			}
		} catch (CoreException | IOException e){
			throw new ClassNotFoundException("Error finding bytes for class "+className, e);
		}
		return classBytes;
	}
	
	@Override
	protected URL findResource(String name) {
		IJavaProject javaProject = JavaCore.create(project);
		try {
			for(IPackageFragmentRoot root:javaProject.getPackageFragmentRoots()){
				if(root.getKind()==IPackageFragmentRoot.K_SOURCE){
					IPath path = root.getRawClasspathEntry().getPath().removeFirstSegments(1).append(new Path(name));
					IFile file = project.getFile(path);
					if(file.exists()){
						return file.getLocationURI().toURL();
					}
				}  else if(root.getKind()==IPackageFragmentRoot.K_BINARY) {
					IPath path = root.getPath();
					if(!root.isExternal()){
						path = project.getLocation().append(path.removeFirstSegments(1));
					}
					
					if("jar".equals(path.getFileExtension())){
						JarFile file = new JarFile(path.toFile());
						try {
							ZipEntry entry = file.getEntry(name);
							if(entry !=null){
								return new URL("jar:file:"+path.toString()+"!/"+name);
							}
						} finally{
							file.close();
						}
					} else {
						File resource = path.append(name).makeAbsolute().toFile();
						if(resource.exists()){
							return resource.toURI().toURL();
						}
					}
				}
			}
		} catch (JavaModelException | IOException e) {
			CodeGenerators.logError("Exception of type " + e.getClass().getName() + " was thrown. Message is " + e.getMessage()
					+ ". Exception occured whiles getting resource "+name, null);
		}
		return null;
	}
}
