/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.j2ee.tomcat;

import com.eas.designer.explorer.j2ee.dd.ElementConvertable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A Realm element represents a "database" of usernames, passwords, and roles
 * (similar to Unix groups) assigned to those users.
 *
 * @author vv
 */

public abstract class Realm implements ElementConvertable {

    public static final String TAG_NAME = "Realm";//NOI18N
    public static final String CLASS_NAME_ATTR_NAME = "className";//NOI18N
    private String className;
    
    /**
     * Sets java class name of the implementation to use.
     * @return a class name
     */
    public String getClassName() {
        return className;
    }
    
    /**
     * Sets java class name of the implementation to use.
     * @param aClassName a class name
     */
    public void setClassName(String aClassName) {
        className = aClassName;
    }

    public void load(Element tag) {
        className = tag.getAttribute(CLASS_NAME_ATTR_NAME);
    }
    
    @Override
    public Element getElement(Document aDoc) {
        Element element = aDoc.createElement(TAG_NAME);
        if (className != null) {
            element.setAttribute(CLASS_NAME_ATTR_NAME, className);
        }
        return element;
    }
}
