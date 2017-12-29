package com.quakearts.tools.text;

public class DifferenceConsolidatorFactory {
	private DifferenceConsolidatorFactory() {
	}
	
	private static DifferenceConsolidatorFactory instance = new DifferenceConsolidatorFactory();
	
	public static DifferenceConsolidatorFactory getInstance() {
		return instance;
	}
	
	private DifferenceConsolidator differenceConsolidator = new DifferenceConsolidatorImpl();
	
	public DifferenceConsolidator getDifferenceConsolidator() {
		return differenceConsolidator;
	}
	
	public void setDifferenceConsolidator(DifferenceConsolidator differenceConsolidator) {
		this.differenceConsolidator = differenceConsolidator;
	}
}
