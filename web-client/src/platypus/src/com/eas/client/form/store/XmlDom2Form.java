package com.eas.client.form.store;

import com.eas.client.form.PlatypusWindow;
import com.eas.client.form.factories.ModelWidgetsFactory;
import com.eas.client.form.factories.WidgetsFactory;
import com.eas.client.model.Model;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.xml.client.Document;

public class XmlDom2Form {

	public static PlatypusWindow transform(String aModuleName, Document aDoc, Model aModel, JavaScriptObject aTarget) throws Exception {
		WidgetsFactory factory = new ModelWidgetsFactory(aModuleName, aDoc.getDocumentElement(), aModel, aTarget);
		factory.parse();
		return factory.getForm();
	}
}
