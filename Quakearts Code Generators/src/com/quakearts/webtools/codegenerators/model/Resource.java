//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.09.08 at 07:48:42 AM UTC 
//


package com.quakearts.webtools.codegenerators.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for resource complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="resource">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="folderID" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="location" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="template" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="filename" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resource", namespace = "http://quakearts.com/scaffolding")
public class Resource {

    @XmlAttribute(required = true)
    protected String folderID;
    @XmlAttribute(required = true)
    protected String location;
    @XmlAttribute
    protected Boolean template;
    @XmlAttribute
    protected String filename;
    @XmlTransient
    private boolean predefined;

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
     * Gets the value of the location property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the value of the location property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocation(String value) {
        this.location = value;
    }

    /**
     * Gets the value of the template property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTemplate() {
        return template!=null?template:false;
    }

    /**
     * Sets the value of the template property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTemplate(Boolean value) {
        this.template = value;
    }
    
    /**
     * Gets the value of the filename property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFilename() {
		return filename;
	}

    /**
     * Sets the value of the filename property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFilename(String filename) {
		this.filename = filename;
	}
    
    public boolean isPredefined() {
		return predefined;
	}
    
    public void setPredefined(boolean predefined) {
		this.predefined = predefined;
	}
    
}
