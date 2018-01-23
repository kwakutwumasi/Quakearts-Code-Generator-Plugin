package com.quakearts.codegenerators.test;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;

import java.io.File;

import javax.xml.bind.Unmarshaller;

import org.junit.Test;

import com.quakearts.tools.CodeGenerators;
import com.quakearts.tools.web.model.Folder;
import com.quakearts.tools.web.model.Scaffolding;

public class TestScaffoldingXMLReading {

	@Test
	public void testScaffoldingXMLReading() throws Exception {
		Unmarshaller unmarshaller = CodeGenerators.getJAXBContext().createUnmarshaller();
		Scaffolding scaffolding = (Scaffolding) unmarshaller.unmarshal(new File("src"+File.separator+"com"
				+File.separator+"quakearts"+File.separator+"tools"
				+File.separator+"web"+File.separator+"generator"
				+File.separator+"scaffolding"+File.separator+"hibernate-crudapp-scaffolding.xml"));
		
		for(Folder folder:scaffolding.getFolderStructure().getFolder().getFolders())
			assertThat(folder.getParentFolder()!=null, is(true));
	}

}
