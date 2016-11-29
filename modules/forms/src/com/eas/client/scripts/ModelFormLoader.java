/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.scripts;

import com.eas.client.forms.FormFactory;
import jdk.nashorn.api.scripting.JSObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author mg
 */
public class ModelFormLoader {

    public static FormFactory load(Document aDoc, String aModuleName, JSObject aModel) throws Exception {
        Element layoutElement = aModuleName != null ? findLayoutElementByBundleName(aDoc.getDocumentElement(), aModuleName) : aDoc.getDocumentElement();
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
