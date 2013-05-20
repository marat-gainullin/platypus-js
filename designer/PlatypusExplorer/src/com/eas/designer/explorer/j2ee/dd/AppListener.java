/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.j2ee.dd;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Defines an application listener.
 * @author vv
 */
public class AppListener implements ElementConvertable {
    public static final String TAG_NAME = "listener"; //NOI18N
    public static final String CLASS_NAME_TAG_NAME = "listener-class"; //NOI18N
    private String className;

    public AppListener(String aClassName) {
        className = aClassName;
    }
    
    public String getClassName() {
        return className;
    }

    public void setClassName(String aClassName) {
        className = aClassName;
    }
    
    @Override
    public Element getElement(Document aDoc) {
        Element element = aDoc.createElement(TAG_NAME);
        if (className != null) {
            Element classNameElement = aDoc.createElement(CLASS_NAME_TAG_NAME);
            classNameElement.setTextContent(className);
            element.appendChild(classNameElement);
        }
        return element;
    }
}
