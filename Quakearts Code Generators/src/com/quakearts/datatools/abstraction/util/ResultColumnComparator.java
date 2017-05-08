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
