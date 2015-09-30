package com.quakearts.datatools.abstraction;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "procedure"
})
@XmlRootElement(name ="wrapper-class", namespace = "http://quakearts.com/xml/wrapperClassSchema")
public class WrapperClass {
    @XmlElement(required=true)
    private Procedure procedure=new Procedure();
    @XmlAttribute
	private String packageName="";
    @XmlAttribute(required = true)
    private String javaClassName="";
    @XmlAttribute
    private String callName="";
    @XmlAttribute
    private boolean multipleSets;
    @XmlAttribute
    private boolean executor;
    @XmlAttribute
    private boolean updater;
    @XmlAttribute
    private boolean facesType;
    @XmlAttribute
    private boolean separate;

	public Procedure getProcedure() {
		return procedure;
	}

	public void setProcedure(Procedure procedure) {
		this.procedure = procedure;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getJavaClassName() {
		return javaClassName;
	}

	public void setJavaClassName(String javaName) {
		this.javaClassName = javaName;
	}

	public String getCallName() {
		return callName;
	}

	public void setCallName(String callName) {
		this.callName = callName;
	}

	public boolean isValid() {
		return procedure.isValid();
	}

	public boolean isMultipleSets() {
		return multipleSets;
	}

	public void setMultipleSets(boolean multipleSets) {
		this.multipleSets = multipleSets;
		if(multipleSets){
			this.executor = false;
			this.updater = false;
		}
	}

	public boolean isExecutor() {
		return executor;
	}

	public void setExecutor(boolean executor) {
		this.executor = executor;
		if(executor){
			this.updater = false;
			this.multipleSets = false;
		}
	}

	public boolean isUpdater() {
		return updater;
	}

	public void setUpdater(boolean updater) {
		this.updater = updater;
		if(updater){
			this.executor = false;
			this.multipleSets = false;
		}
	}

	public boolean isFacesType() {
		return facesType;
	}

	public void setFacesType(boolean facesType) {
		this.facesType = facesType;
	}
	
	public boolean isSeparate() {
		return separate;
	}
	
	public void setSeparate(boolean separate) {
		this.separate = separate;
	}
	
}
