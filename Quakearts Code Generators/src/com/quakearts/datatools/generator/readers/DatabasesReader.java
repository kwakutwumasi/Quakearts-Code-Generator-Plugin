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
package com.quakearts.datatools.generator.readers;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.quakearts.datatools.navigator.NavigatorBase;

public class DatabasesReader extends NavigatorBase {
	private List<String[]> currentCachedDatabases;
	private String schemaFilter, profileName;
	
	public List<String[]> getCachedDatabases() {
		return currentCachedDatabases;
	}

	public DatabasesReader() {
	}

	@SuppressWarnings("unchecked")
	public List<String[]> getDatabases(String profileName, String schemaFilter, boolean useCache) {
		this.schemaFilter = schemaFilter;
		if(schemaFilter!=null && schemaFilter.trim().isEmpty())
			this.schemaFilter=null;
		
		this.profileName = profileName;
		if(useCache){
			List<String[]> cachedDatabases=(List<String[]>) getCache().get("database.list."+profileName+(schemaFilter==null?"":schemaFilter));
			if(cachedDatabases!=null){
				currentCachedDatabases=cachedDatabases;
			}
			if(currentCachedDatabases!=null)
				return currentCachedDatabases;
		}
			
		setupAndPerformDBAction(profileName);
		return currentCachedDatabases;
	}
	
	@Override
	protected void performDBAction(Connection connection) throws SQLException {
		DatabaseMetaData metaData = connection.getMetaData();
		ResultSet set = metaData.getCatalogs();
		currentCachedDatabases = new ArrayList<String[]>();
		ArrayList<String> databases = new ArrayList<String>();
		
		if (set.next()) {
			do {
				databases.add(set.getString("TABLE_CAT"));
			} while (set.next());
		}
		set.close();
		for(String database:databases){
			set = metaData.getSchemas(database, schemaFilter);
			if (set.next()) {
				do {
					String[] databasedef = new String[2];
					databasedef[0] = set.getString("TABLE_SCHEM");
					databasedef[1] = database;
					databasedef[0]=databasedef[0]==null?"":databasedef[0];
					currentCachedDatabases.add(databasedef);
				} while (set.next());
			} else {
				currentCachedDatabases.add(new String[]{"",database});
			}
			set.close();
		}
		getCache().put("database.list."+profileName+(schemaFilter==null?"":schemaFilter), currentCachedDatabases);
	}
}
