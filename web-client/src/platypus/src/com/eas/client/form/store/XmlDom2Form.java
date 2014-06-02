package com.eas.client.form.store;

import com.bearsoft.rowset.Utils;
import com.eas.client.form.PlatypusWindow;
import com.eas.client.form.factories.WidgetsFactory;
import com.eas.client.form.factories.ModelWidgetsFactory;
import com.eas.client.model.Model;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;

public class XmlDom2Form {

	public static PlatypusWindow transform(Document aDoc, Model aModel, JavaScriptObject aTarget) throws Exception {
		Element layoutTag = Utils.scanForElementByTagName(aDoc.getDocumentElement(), "layout");
		if (layoutTag != null) {
			WidgetsFactory factory = new ModelWidgetsFactory(layoutTag, aModel, aTarget);
			factory.parse();
			return factory.getForm();
		} else
			throw new IllegalStateException(
					"'layout' tag missing. Can't process form without the layout tag in form document.");
	}
}
