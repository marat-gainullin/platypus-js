package com.eas.form.store;

import com.eas.form.FormReader;
import com.eas.ui.JsUi;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;

public class XmlDom2Form {

	public static FormReader transform(Document aDocument, String aModuleName, JavaScriptObject aModel) throws Exception {
		Element layoutElement = aModuleName != null ? JsUi.findLayoutElementByBundleName(aDocument.getDocumentElement(), aModuleName) : aDocument.getDocumentElement();
		if (layoutElement != null) {
			FormReader factory = new FormReader(layoutElement, aModel);
			factory.parse();
			return factory;
		} else {
			return null;
		}
	}
	
}
