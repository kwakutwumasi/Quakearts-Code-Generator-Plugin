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
package com.quakearts.tools.data.navigator;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.IManagedConnection;
import org.eclipse.datatools.connectivity.ProfileManager;

import com.quakearts.tools.CodeGenerators;

public abstract class NavigatorBase {
	
	private static ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<String, Object>();

	public static Map<String, Object> getCache() {
		return cache;
	}

	protected void setupAndPerformDBAction(String profileName){
		IConnectionProfile profile = getConnectionProfile(profileName);
		if(profile != null){
			IStatus status = profile.connect();
			if (status.getCode() == IStatus.OK) {
				Connection connection = getConnection(profile);
				if(connection==null)
					return;
				
				try {
					performDBAction(connection);
				} catch (SQLException e) {
					CodeGenerators.logError(
							"Exception of type "
							+ e.getClass()
									.getName()
							+ " was thrown. Message is "
							+ e.getMessage()
							+ ". Exception occured whiles performing action in "
							+this.getClass().getName()
							+";profile "+ profileName, status.getException());
					e.printStackTrace();
				}
			} else {
				if (status.getException() != null) {
					CodeGenerators.logError(
							"Exception of type "
							+ status.getException().getClass()
									.getName()
							+ " was thrown. Message is "
							+ status.getException().getMessage()
							+ ". Exception occured whiles getting connection for profile "
							+";profile "+ profileName, status.getException());
				} else {
					CodeGenerators.logError(
							"Cannot get connection for profile ", null);
				}
			}
		
		}else{
			CodeGenerators.logError("Cannot get profile "+profileName, null);
		}
	}
	
	protected abstract void performDBAction(Connection connection) throws SQLException;
	
	private IConnectionProfile getConnectionProfile(String profileName){
		return ProfileManager.getInstance().getProfileByName(profileName);
	}
	
	private Connection getConnection(IConnectionProfile profile) {
		IManagedConnection connection = profile.getManagedConnection(Connection.class.getName());
		Object object = connection.getConnection().getRawConnection();
		if(object instanceof Connection)
			return (Connection) object;
		else
			CodeGenerators.logError("Cannot get a java.sql.Connection from profile "+profile.getName()+". "+(object!=null?object.getClass().getName()+" was returned instead.":""), null);
		return null;
	}
	
	protected String[] getInfo(int type){
		String[] info = new String[3];
		
		switch(type){
        case Types.INTEGER:
        case Types.SMALLINT:
        case Types.TINYINT:
        	info[0]=null;
        	info[1]="int";
        	info[2]="Types.INTEGER";
        	return info;
        case Types.BIGINT:
        	info[0]=null;
        	info[1]="long";
        	info[2]="Types.BIGINT";
        	return info;
        case Types.FLOAT:
        case Types.REAL:
        	info[0]="java.math.BigDecimal";
        	info[1]="BigDecimal";
        	info[2]= type==Types.FLOAT?"Types.FLOAT":"Types.REAL";
            return info;
        case Types.BIT:
        case Types.BOOLEAN:
        	info[0]=null;
        	info[1]="boolean";
        	info[2]="Types.BOOLEAN";
            return info;
        case Types.DOUBLE:
        case Types.DECIMAL:
        case Types.NUMERIC:
        	info[0]=null;
        	info[1]="double";
        	if(type==Types.DOUBLE)
        		info[2]="Types.DOUBLE";
        	else if(type==Types.DECIMAL)
        		info[2]="Types.DECIMAL";
        	else
        		info[2]="Types.NUMERIC";
            return info;
        case Types.TIME:
        	info[0]="java.sql.Time";
        	info[1]="Time";
        	info[2]="Types.TIME";
            return info;
        case Types.TIMESTAMP:
        	info[0]="java.sql.Timestamp";
        	info[1]="Timestamp";
        	info[2]="Types.TIMESTAMP";
            return info;
        case Types.DATE:
        	info[0]="java.sql.Date";
        	info[1]="Date";
        	info[2]="Types.DATE";
        	return info;
        default:
        	info[0]=null;
        	info[1]="String";
        	info[2]="Types.VARCHAR";
            return info;
		}
	}
	
	protected static String camelize(String stringToCamelize,boolean capitalizeFirst){
		char[] characters = stringToCamelize.toCharArray();
		StringBuilder camelizedString= new StringBuilder();
		boolean firstChar = true, boundary=false,previouswasLower=false;
		for(char charct:characters){
			if(charct == '_' || charct == ' '){
				boundary=true;
				continue;
			}
			if(!new String(new char[]{charct}).matches("[a-zA-Z0-9]")){
				continue;
			}
			if(firstChar){
				firstChar=false;
				if(capitalizeFirst)
					camelizedString.append(Character.toUpperCase(charct));
				else
					camelizedString.append(Character.toLowerCase(charct));
			}else{					
				if(boundary){
					boundary=false;
					camelizedString.append(Character.toUpperCase(charct));
				} else if(previouswasLower){
					previouswasLower = false;
					camelizedString.append(charct);
				} else{
					camelizedString.append(Character.toLowerCase(charct));					
				}
			}
			if(Character.isLowerCase(charct))
				previouswasLower = true;
		}
		return camelizedString.toString();
	}
}
