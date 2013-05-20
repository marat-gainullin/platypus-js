/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.j2ee.dd;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Specifies the login and error pages that should be used when form-based login is specified.
 * @author vv
 */
public class FormLoginConfig implements ElementConvertable {

    public static final String TAG_NAME = "form-login-config"; //NOI18N
    public static final String FORM_LOGIN_PAGE_TAG_NAME = "form-login-page"; //NOI18N
    public static final String FORM_ERROR_PAGE_TAG_NAME = "form-error-page"; //NOI18N
    private String formLoginPage;
    private String formErrorPage;
    
    public FormLoginConfig(String aFormLoginPage, String aFormErrorPage) {
        formLoginPage = aFormLoginPage;
        formErrorPage = aFormErrorPage;
    }
    
    public String getFormLoginPage() {
        return formLoginPage;
    }

    public void setFormLoginPage(String aFormLoginPage) {
        formLoginPage = aFormLoginPage;
    }

    public String getFormErrorPage() {
        return formErrorPage;
    }

    public void setFormErrorPage(String aFormErrorPage) {
        formErrorPage = aFormErrorPage;
    }
    
    @Override
    public Element getElement(Document aDoc) {
        Element element = aDoc.createElement(TAG_NAME);
        Element formLoginPageElement = aDoc.createElement(FORM_LOGIN_PAGE_TAG_NAME);
        formLoginPageElement.setTextContent(formLoginPage);
        element.appendChild(formLoginPageElement);
        Element formErrorPageElement = aDoc.createElement(FORM_ERROR_PAGE_TAG_NAME);
        formErrorPageElement.setTextContent(formErrorPage);
        element.appendChild(formErrorPageElement);
        return element;
    }
    
}
