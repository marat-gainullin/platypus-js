package com.eas.client.form.store;

import com.bearsoft.rowset.Utils;
import com.eas.client.form.PlatypusWindow;
import com.eas.client.form.factories.GxtControlsFactory;
import com.eas.client.form.factories.GxtModelControlsFactory;
import com.eas.client.model.Model;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;

public class XmlDom2Form {

	public static PlatypusWindow transform(Document aDoc, Model aModel) throws Exception {
		Element layoutTag = Utils.scanForElementByTagName(aDoc.getDocumentElement(), "layout");
		if (layoutTag != null) {
			GxtControlsFactory factory = new GxtModelControlsFactory(layoutTag, aModel);
			factory.parse();
			return factory.getForm();
		} else
			throw new IllegalStateException(
					"'layout' tag missing. Can't process form without the layout tag in form document.");
	}
}
