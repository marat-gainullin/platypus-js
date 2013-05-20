/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.j2ee.dd;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Configure how the user is authenticated; the realm name that should be used for this application; and the attributes that are needed by the form login mechanism. 
 * @author vv
 */
public class LoginConfig implements ElementConvertable {

    public static final String TAG_NAME = "login-config";//NOI18N
    public static final String AUTHT_METHOD_TAG_NAME = "auth-method";//NOI18N
    public static final String REALM_NAME_TAG_NAME = "realm-name";//NOI18N
    private String authMethod;
    private String realmName;
    private FormLoginConfig formLoginConfig;

    public String getAuthMethod() {
        return authMethod;
    }

    public void setAuthMethod(String anAuthMethod) {
        authMethod = anAuthMethod;
    }

    public String getRealmName() {
        return realmName;
    }

    public void setRealmName(String aEealmName) {
        realmName = aEealmName;
    }

    public FormLoginConfig getFormLoginConfig() {
        return formLoginConfig;
    }

    public void setFormLoginConfig(FormLoginConfig aFormLoginConfig) {
        formLoginConfig = aFormLoginConfig;
    }
    
    @Override
    public Element getElement(Document aDoc) {
        Element element = aDoc.createElement(TAG_NAME);
        if (authMethod != null) {
            Element authMethodElement = aDoc.createElement(AUTHT_METHOD_TAG_NAME);
            authMethodElement.setTextContent(authMethod);
            element.appendChild(authMethodElement);
        }
        if (realmName != null) {
            Element realmNameElement = aDoc.createElement(REALM_NAME_TAG_NAME);
            realmNameElement.setTextContent(realmName);
            element.appendChild(realmNameElement);
        }
        if (formLoginConfig != null) {
            element.appendChild(formLoginConfig.getElement(aDoc));
        }
        return element;
    }
    
}
