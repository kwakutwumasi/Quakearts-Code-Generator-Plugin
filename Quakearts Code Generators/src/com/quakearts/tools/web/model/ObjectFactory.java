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

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.quakearts.tools.web.model package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.quakearts.tools.web.model
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Bean }
     * 
     */
    public Bean createBean() {
        return new Bean();
    }

    /**
     * Create an instance of {@link Template }
     * 
     */
    public Template createTemplate() {
        return new Template();
    }

    /**
     * Create an instance of {@link ScaffoldingProperties }
     * 
     */
    public ScaffoldingProperties createScaffoldingProperties() {
        return new ScaffoldingProperties();
    }

    /**
     * Create an instance of {@link FolderStructure }
     * 
     */
    public FolderStructure createFolderStructure() {
        return new FolderStructure();
    }

    /**
     * Create an instance of {@link PropertyEntry }
     * 
     */
    public PropertyEntry createPropertyEntry() {
        return new PropertyEntry();
    }

    /**
     * Create an instance of {@link BeanType }
     * 
     */
    public BeanType createBeanType() {
        return new BeanType();
    }

    /**
     * Create an instance of {@link TemplateGroup }
     * 
     */
    public TemplateGroup createPage() {
        return new TemplateGroup();
    }

    /**
     * Create an instance of {@link Scaffolding }
     * 
     */
    public Scaffolding createScaffolding() {
        return new Scaffolding();
    }

    /**
     * Create an instance of {@link Folder }
     * 
     */
    public Folder createFolder() {
        return new Folder();
    }

    /**
     * Create an instance of {@link Resource }
     * 
     */
    public Resource createResource() {
        return new Resource();
    }

}
