package com.quakearts.webtools.codegenerators.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class TagTemplate {
	private Collection<TagLibrary> libraries = new HashSet<TagLibrary>();
	private HashMap<String, String> map = new HashMap<String, String>();
	
	public Collection<TagLibrary> getLibraries() {
		return libraries;
	}

	private String templateOpen, templateClose;
	
	public String getTemplateOpen() {
		return templateOpen;
	}

	public void setTemplateOpen(String templateOpen) {
		this.templateOpen = templateOpen;
	}

	public String getTemplateClose() {
		return templateClose;
	}

	public void setTemplateClose(String templateClose) {
		this.templateClose = templateClose;
	}
	
	public String getTagText(String type){
		String data = map.get(type);
		if(data==null)
			data = map.get("default");
		
		return data;
	}
	
	public boolean isDefault(String type){
		return !map.containsKey(type);
	}
	
	public void addTagText(String type, String tagText) {
		map.put(type, tagText);
	}

}
