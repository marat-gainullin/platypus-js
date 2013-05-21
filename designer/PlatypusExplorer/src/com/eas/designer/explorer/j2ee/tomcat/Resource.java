/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.j2ee.tomcat;

import com.eas.designer.explorer.j2ee.dd.ElementConvertable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * JNDI resource.
 *
 * @author vv
 */
public class Resource implements ElementConvertable {

    public static final String TAG_NAME = "Resource";//NOI18N
    public static final String NAME_ATTR_NAME = "name";//NOI18N
    public static final String TYPE_ATTR_NAME = "type";//NOI18N
    private String auth;
    private String closeMethod;
    private String description;
    private String name;
    private String scope;
    private String singleton;
    private String type;

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getCloseMethod() {
        return closeMethod;
    }

    public void setCloseMethod(String closeMethod) {
        this.closeMethod = closeMethod;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getSingleton() {
        return singleton;
    }

    public void setSingleton(String singleton) {
        this.singleton = singleton;
    }

    public String getType() {
        return type;
    }

    public void setType(String aClassName) {
        type = aClassName;
    }

    public void load(Element tag) {
        name = tag.getAttribute(NAME_ATTR_NAME);
        type = tag.getAttribute(TYPE_ATTR_NAME);
    }

    public Element getElement(Document aDoc) {
        Element element = aDoc.createElement(TAG_NAME);
        if (name != null) {
            element.setAttribute(NAME_ATTR_NAME, name);
        }
        if (type != null) {
            element.setAttribute(TYPE_ATTR_NAME, type);
        }
        return element;
    }
}
