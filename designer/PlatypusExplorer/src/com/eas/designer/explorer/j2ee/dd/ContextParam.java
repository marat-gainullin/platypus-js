/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.j2ee.dd;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Web application's parameter.
 * @author vv
 */
public class ContextParam implements ElementConvertable {

    public static final String TAG_NAME = "context-param";//NOI18N
    public static final String NAME_TAG_NAME = "param-name";//NOI18N
    public static final String VALUE_TAG_NAME = "param-value";//NOI18N
    public static final String DESCRIPTION_TAG_NAME = "description";//NOI18N
    private final String name;
    private String value;

    public ContextParam(String aName, String aValue) {
        name = aName;
        value = aValue;
        descriptionTag = null;
    }

    public ContextParam(String aName, String aValue, String aDescription) {
        name = aName;
        value = aValue;
        descriptionTag = aDescription;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return descriptionTag;
    }

    public void setDescription(String description) {
        this.descriptionTag = description;
    }
    public String descriptionTag;

    @Override
    public Element getElement(Document aDoc) {
        Element element = aDoc.createElement(TAG_NAME);
        if (descriptionTag != null) {
            Element desc = aDoc.createElement(DESCRIPTION_TAG_NAME);
            desc.setTextContent(descriptionTag);
            element.appendChild(desc);
        }
        Element nameTag = aDoc.createElement(NAME_TAG_NAME);
        nameTag.setTextContent(name);
        element.appendChild(nameTag);
        Element valueTag = aDoc.createElement(VALUE_TAG_NAME);
        valueTag.setTextContent(value != null ? value : ""); //NOI18N
        element.appendChild(valueTag);
        return element;
    }
}
