package com.quakearts.webtools.codegenerators.wizard.pages;

import java.io.IOException;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.xml.sax.SAXException;
import com.quakearts.webtools.codegenerators.BeanListViewPageGenerator;
import com.quakearts.webtools.codegenerators.GenericPageGenerator;

public class BeanListViewWizardPage extends BeanBaseWizardPage {
	
	public BeanListViewWizardPage(IProject project, IContainer folder, GenericPageGenerator generator) {
		super("listViewPage", "Create a new list view pages", "Select a beans and a template to create a new beans list view....", project, folder, generator);
	}

	@Override
	protected List<String[]> getDefaultTemplates()
			throws ParserConfigurationException, SAXException, IOException {
		return ((BeanListViewPageGenerator)pageGenerator).getDefaultListTemplates();
	}
}
