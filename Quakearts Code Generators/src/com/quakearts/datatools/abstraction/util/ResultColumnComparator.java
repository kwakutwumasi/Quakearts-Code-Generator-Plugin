package com.quakearts.datatools.abstraction.util;

import java.util.Comparator;
import com.quakearts.datatools.abstraction.ResultColumn;

public class ResultColumnComparator implements Comparator<ResultColumn> {

	@Override
	public int compare(ResultColumn column1, ResultColumn column2) {
		if(column1==null && column2==null)
			return 0;

		if(column1==null && column2!=null)
			return -1;

		if(column1!=null && column2==null)
			return 1;

		return column1.getColumnNumber()-column2.getColumnNumber();
	}

}
