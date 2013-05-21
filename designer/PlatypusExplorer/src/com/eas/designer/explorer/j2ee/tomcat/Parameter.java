/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.j2ee.tomcat;

import com.eas.designer.explorer.j2ee.dd.ElementConvertable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The equivalent to the inclusion of the <context-param> in the web application deployment descriptor (/WEB-INF/web.xml).
 * @author vv
 */
public class Parameter implements ElementConvertable {
    
    public static final String TAG_NAME = "Parameter";//NOI18N
    public static final String DESCRIPTION_ATTR_NAME = "description";//NOI18N
    public static final String NAME_ATTR_NAME = "name";//NOI18N
    public static final String OVERRIDE_ATTR_NAME = "override";//NOI18N
    public static final String VALUE_ATTR_NAME = "value";//NOI18N
    private String description;
    private String name;
    private String override;
    private String value;
    
    public Parameter(String aName, String aValue) {
        name = aName;
        value = aValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String aDescription) {
        description = aDescription;
    }

    public String getName() {
        return name;
    }

    public void setName(String aName) {
        name = aName;
    }

    public String getOverride() {
        return override;
    }

    public void setOverride(String anOverride) {
        override = anOverride;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String aValue) {
        value = aValue;
    }
    
    @Override
    public Element getElement(Document aDoc) {
        Element element = aDoc.createElement(TAG_NAME);
        if (description != null) {
            element.setAttribute(DESCRIPTION_ATTR_NAME, description);
        }
        element.setAttribute(NAME_ATTR_NAME, name);
        if (override != null) {
            element.setAttribute(OVERRIDE_ATTR_NAME, override);
        }
        element.setAttribute(VALUE_ATTR_NAME, value);
        return element;
    }
    
}
