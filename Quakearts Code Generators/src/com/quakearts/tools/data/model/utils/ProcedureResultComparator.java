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
package com.quakearts.tools.data.model.utils;

import java.util.Comparator;

import com.quakearts.tools.data.model.ProcedureResult;

public class ProcedureResultComparator implements Comparator<ProcedureResult> {

	@Override
	public int compare(ProcedureResult proc1, ProcedureResult proc2) {
		if(proc1==null && proc2==null)
			return 0;

		if(proc1==null && proc2!=null)
			return -1;

		if(proc1!=null && proc2==null)
			return 1;

		return proc1.hashCode() - proc2.hashCode();
	}

}
