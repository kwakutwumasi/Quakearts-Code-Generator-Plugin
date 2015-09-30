package com.quakearts.datatools.abstraction.util;

import java.util.Comparator;
import com.quakearts.datatools.abstraction.ProcedureResult;

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
