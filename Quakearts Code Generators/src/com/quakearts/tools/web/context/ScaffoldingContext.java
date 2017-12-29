package com.quakearts.tools.web.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.quakearts.tools.web.model.BeanModel;

public class ScaffoldingContext {
	private List<BeanModel> beanModels = new ArrayList<>();
	private Map<String, BeanModel> nameModelMappings = new HashMap<>(), classModelMappings = new HashMap<>();
	
	public List<BeanModel> getBeanModels() {
		return beanModels;
	}
	
	public void addBeanModel(BeanModel beanModel) {
		beanModels.add(beanModel);
		nameModelMappings.put(beanModel.getName(), beanModel);
		classModelMappings.put(beanModel.getBeanClassName(), beanModel);
	}
	
	public Map<String, BeanModel> getNameModelMappings() {
		return nameModelMappings;
	}
	
	public Map<String, BeanModel> getClassModelMappings() {
		return classModelMappings;
	}
}
