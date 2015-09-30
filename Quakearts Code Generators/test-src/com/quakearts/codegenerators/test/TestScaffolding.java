package com.quakearts.codegenerators.test;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.junit.Test;
import com.quakearts.webtools.codegenerators.model.Scaffolding;

public class TestScaffolding {

	@Test
	public void testXml() {
		FileInputStream fis=null;
		FileOutputStream fos=null;
		try {
			JAXBContext context = JAXBContext.newInstance(Scaffolding.class);
			fis = new FileInputStream("scaffolding.xml");
			Unmarshaller unmarshaller = context.createUnmarshaller();
			Scaffolding scaffolding = (Scaffolding) unmarshaller.unmarshal(fis);
			assertTrue(scaffolding.getProperties().getEntries().get(0).getValue()!=null);
			
			fos = new FileOutputStream("scaffolding-out.xml");
			Marshaller marshaller = context.createMarshaller();
			marshaller.marshal(scaffolding, fos);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception of type " + e.getClass().getName() + " was thrown. Message is " + e.getMessage()
					+ ". Exception occured whiles testing xml");
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
			try {
				fos.close();
			} catch (Exception e) {
			}
		}

	}

}
