package com.eas.form.store;

import com.eas.form.FormFactory;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;

public class XmlDom2Form {

	public static FormFactory transform(Document aDocument, String aModuleName, JavaScriptObject aModel) throws Exception {
		Element layoutElement = aModuleName != null ? findLayoutElementByBundleName(aDocument.getDocumentElement(), aModuleName) : aDocument.getDocumentElement();
		if (layoutElement != null) {
			FormFactory factory = new FormFactory(layoutElement, aModel);
			factory.parse();
			return factory;
		} else {
			return null;
		}
	}
	
    private static Element findLayoutElementByBundleName(Element aElement, String aBundleName) {
        if (aElement.getTagName().equals("layout")) {
            return aElement;// the high level code had to do everything in the right way
        } else {
            Node child = aElement.getFirstChild();
            while (child != null) {
                if (child instanceof Element) {
                    Element el = (Element) child;
                    if (el.hasAttribute("bundle-name")) {
                        String bundleName = el.getAttribute("bundle-name");
                        if (bundleName.equals(aBundleName)) {
                            return el;
                        }
                    }
                }
                child = child.getNextSibling();
            }
        }
        return null;
    }
}
