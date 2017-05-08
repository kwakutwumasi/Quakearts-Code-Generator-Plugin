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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class KeywordCorrecter {
	private static final Collection<String> javaKeywords = setJavakeywords();
	
	public static String convertKeyWord(String keyWord){
		if(keyWord.equalsIgnoreCase("class"))
			return "clazz";
		
		return "_"+keyWord;
	}
	
	private static Collection<String> setJavakeywords() {
		HashSet<String> javaKeywords = new HashSet<String>();
		javaKeywords.add("abstract");
		javaKeywords.add("continue");
		javaKeywords.add("for");
		javaKeywords.add("new");
		javaKeywords.add("switch");
		javaKeywords.add("assert");
		javaKeywords.add("default");
		javaKeywords.add("goto");
		javaKeywords.add("package");
		javaKeywords.add("synchronized");
		javaKeywords.add("boolean");
		javaKeywords.add("do");
		javaKeywords.add("if");
		javaKeywords.add("private");
		javaKeywords.add("this");
		javaKeywords.add("break");
		javaKeywords.add("double");
		javaKeywords.add("implements");
		javaKeywords.add("protected");
		javaKeywords.add("throw");
		javaKeywords.add("byte");
		javaKeywords.add("else");
		javaKeywords.add("import");
		javaKeywords.add("public");
		javaKeywords.add("throws");
		javaKeywords.add("case");
		javaKeywords.add("enum");
		javaKeywords.add("instanceof");
		javaKeywords.add("return");
		javaKeywords.add("transient");
		javaKeywords.add("catch");
		javaKeywords.add("extends");
		javaKeywords.add("int");
		javaKeywords.add("short");
		javaKeywords.add("try");
		javaKeywords.add("char");
		javaKeywords.add("final");
		javaKeywords.add("interface");
		javaKeywords.add("static");
		javaKeywords.add("void");
		javaKeywords.add("class");
		javaKeywords.add("finally");
		javaKeywords.add("long");
		javaKeywords.add("strictfp");
		javaKeywords.add("volatile");
		javaKeywords.add("const");
		javaKeywords.add("float");
		javaKeywords.add("native");
		javaKeywords.add("super");
		javaKeywords.add("while");
		return Collections.unmodifiableCollection(javaKeywords);
	}
	
	public static Collection<String> getJavakeywords() {
		return javaKeywords;
	}
}
