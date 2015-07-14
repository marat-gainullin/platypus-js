/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.j2ee.dd;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author user
 */
public class WelcomeFile implements ElementConvertable {

    public static final String TAG_NAME = "welcome-file"; //NOI18N

    private String name;

    public WelcomeFile(String aName) {
        name = aName;
    }

    public String getName() {
        return name;
    }

    public void setName(String aValue) {
        name = aValue;
    }

    @Override
    public Element getElement(Document aDoc) {
        Element element = aDoc.createElement(TAG_NAME);
        element.setTextContent(name);
        return element;
    }

}
