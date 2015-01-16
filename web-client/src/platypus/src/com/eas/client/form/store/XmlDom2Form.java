package com.eas.client.form.store;

import com.eas.client.form.PlatypusWindow;
import com.eas.client.form.factories.FormFactory;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.xml.client.Document;

public class XmlDom2Form {

	public static PlatypusWindow transform(Document aDoc, JavaScriptObject aModel) throws Exception {
		FormFactory factory = new FormFactory(aDoc.getDocumentElement(), aModel);
		factory.parse();
		return factory.getForm();
	}
}
