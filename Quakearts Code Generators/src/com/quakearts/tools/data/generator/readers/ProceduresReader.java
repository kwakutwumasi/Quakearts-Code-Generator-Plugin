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
package com.quakearts.tools.data.generator.readers;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.quakearts.tools.data.navigator.NavigatorBase;

public class ProceduresReader extends NavigatorBase {
	private String database, schema;
	private List<String[]> currentCachedProcedures;
	
	public List<String[]> getCachedProcedures() {
		return currentCachedProcedures;
	}

	public ProceduresReader() {
	}

	@SuppressWarnings("unchecked")
	public List<String[]> getProcedures(String database, String schema,
			String profileName, boolean useCache) {
		if(useCache){
			List<String[]> cachedProcedures=(List<String[]>) getCache().get("procedures.list."+database+"."+schema);
			if(cachedProcedures!=null){
				currentCachedProcedures = cachedProcedures;
				if(currentCachedProcedures!=null)
					return currentCachedProcedures;
			}
		}
		
		this.database = database;
		this.schema = schema;
		setupAndPerformDBAction(profileName);
		return currentCachedProcedures;
	}

	@Override
	protected void performDBAction(Connection connection) throws SQLException {
		DatabaseMetaData metadata = connection.getMetaData();
		ResultSet rs = metadata.getProcedures(database, schema == null || schema.trim().isEmpty()?null:schema, null);
		currentCachedProcedures = new ArrayList<String[]>();
		if (rs.next()) {
			do {
				String[] procedureparts = new String[3];
				procedureparts[0]=database;
				procedureparts[1]=schema;
				procedureparts[2]=rs.getString("PROCEDURE_NAME");
				if(procedureparts[2].indexOf(';')!=-1)
					procedureparts[2] = procedureparts[2].substring(0, procedureparts[2].indexOf(';'));
				currentCachedProcedures.add(procedureparts);
			} while (rs.next());
		}
		getCache().put("procedures.list."+database+"."+schema, currentCachedProcedures);
	}

}
