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
package com.quakearts.tools.web.model;

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
