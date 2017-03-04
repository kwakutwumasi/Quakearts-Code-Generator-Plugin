package com.quakearts.webtools.codegenerators.model;

import java.util.ArrayList;
import java.util.List;

public class IdElement {
	private List<BeanElement> ids = new ArrayList<>();
	
	public IdElement(BeanElement beanElement) {
		ids.add(beanElement);
	}
	
	public boolean isCompositeId() {
		return ids.size()>1;
	}
	
	public BeanElement getId(){
		return ids.get(0);
	}
	
	public List<BeanElement> getIds() {
		return ids;
	}
}
