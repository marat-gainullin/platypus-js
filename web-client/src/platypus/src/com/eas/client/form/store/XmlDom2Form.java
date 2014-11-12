package com.eas.client.form.store;

import com.eas.client.form.PlatypusWindow;
import com.eas.client.form.factories.ModelWidgetsFactory;
import com.eas.client.form.factories.WidgetsFactory;
import com.eas.client.model.Model;
import com.google.gwt.xml.client.Document;

public class XmlDom2Form {

	public static PlatypusWindow transform(String aModuleName, Document aDoc, Model aModel) throws Exception {
		WidgetsFactory factory = new ModelWidgetsFactory(aModuleName, aDoc.getDocumentElement(), aModel);
		factory.parse();
		return factory.getForm();
	}
}
