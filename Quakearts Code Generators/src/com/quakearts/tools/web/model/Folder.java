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
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.09.08 at 07:48:42 AM UTC 
//


package com.quakearts.tools.web.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for folder complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="folder">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="folderID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="folder" type="{http://quakearts.com/scaffolding}folder" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "folder", namespace = "http://quakearts.com/scaffolding", propOrder = {
    "name",
    "folderID",
    "folders"
})
public class Folder {

    @XmlAttribute(required = true)
    protected String name;
    @XmlAttribute(required = true)
    protected String folderID;
    @XmlElement(namespace = "http://quakearts.com/scaffolding", name="folder")
    protected List<Folder> folders;
    @XmlTransient
    protected Folder parentFolder;
    
    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the folderID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFolderID() {
        return folderID;
    }

    /**
     * Sets the value of the folderID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFolderID(String value) {
        this.folderID = value;
    }

    /**
     * Gets the value of the folder property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the folder property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFolder().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Folder }
     * 
     * 
     */
    public List<Folder> getFolders() {
        if (folders == null) {
            folders = new ArrayList<Folder>() {
            	/**
				 * 
				 */
				private static final long serialVersionUID = -6757140835883970165L;

				@Override
            	public boolean add(Folder e) {
            		e.setParentFolder(Folder.this);
            		return super.add(e);
            	}
            	
            	@Override
            	public void add(int index, Folder element) {
            		element.setParentFolder(Folder.this);
            		super.add(index, element);
            	}
            	
            	@Override
            	public boolean addAll(Collection<? extends Folder> c) {
            		c.stream().parallel().forEach((e)->{
            			e.setParentFolder(Folder.this);
            		});
            		return super.addAll(c);
            	}
            	
            	@Override
            	public boolean addAll(int index, Collection<? extends Folder> c) {
            		c.stream().parallel().forEach((e)->{
            			e.setParentFolder(Folder.this);
            		});
            		return super.addAll(index, c);
            	}
            	
            	@Override
            	public Folder set(int index, Folder element) {
            		element.setParentFolder(Folder.this);
            		return super.set(index, element);
            	}
            };
        }
        return this.folders;
    }
    
    public int getFolderCount(){
    	int count=1;
    	for(Folder folder:getFolders()){
    		count += folder.getFolderCount();
    	}
    	return count;
    }

	public Folder getParentFolder() {
		return parentFolder;
	}

	public void setParentFolder(Folder parentFolder) {
		this.parentFolder = parentFolder;
	}
}