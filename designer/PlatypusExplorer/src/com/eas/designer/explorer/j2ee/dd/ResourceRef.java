/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.j2ee.dd;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author vv
 */
public class ResourceRef implements ElementConvertable {
    public static final String TAG_NAME = "resource-ref"; //NOI18N
    public static final String DESCRIPTION_TAG_NAME = "description"; //NOI18N
    public static final String RES_REF_NAME_TAG_NAME = "res-ref-name"; //NOI18N
    public static final String RES_TYPE_TAG_NAME = "res-type"; //NOI18N
    public static final String RES_AUTH_TAG_NAME = "res-auth"; //NOI18N
    public static final String RES_SHARING_SCOPE_TAG_NAME = "res-sharing-scope"; //NOI18N
    
    private String resRefName;
    private String resType;
    private String resAuth;
    private String resSharingScope;
    private String description;

    public ResourceRef(String aResRefName, String aResType, String aResAuth) {
        resRefName = aResRefName;
        resType = aResType;
        resAuth = aResAuth;
    }

    public String getResRefName() {
        return resRefName;
    }

    public void setResRefName(String aResRefName) {
        resRefName = aResRefName;
    }

    public String getResType() {
        return resType;
    }

    public void setResType(String aResType) {
        resType = aResType;
    }

    public String getResAuth() {
        return resAuth;
    }

    public void setResAuth(String aResAuth) {
        resAuth = aResAuth;
    }

    public String getResSharingScope() {
        return resSharingScope;
    }

    public void setResSharingScope(String aResSharingScope) {
        resSharingScope = aResSharingScope;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String aDescription) {
        description = aDescription;
    }
    
    @Override
    public Element getElement(Document aDoc) {
        Element element = aDoc.createElement(TAG_NAME);
        if (description != null) {
            Element descriptionElement = aDoc.createElement(DESCRIPTION_TAG_NAME);
            descriptionElement.setTextContent(description);
            element.appendChild(descriptionElement);
        }
        Element resRefNameElement = aDoc.createElement(RES_REF_NAME_TAG_NAME);
        resRefNameElement.setTextContent(resRefName);
        element.appendChild(resRefNameElement);
        Element resTypeElement = aDoc.createElement(RES_TYPE_TAG_NAME);
        resTypeElement.setTextContent(resType);
        element.appendChild(resTypeElement);
        Element resAuthElement = aDoc.createElement(RES_AUTH_TAG_NAME);
        resAuthElement.setTextContent(resAuth);
        element.appendChild(resAuthElement);
        if (resSharingScope != null) {
            Element resSharingScopeElement = aDoc.createElement(RES_SHARING_SCOPE_TAG_NAME);
            resSharingScopeElement.setTextContent(resSharingScope);
            element.appendChild(resSharingScopeElement);
        }
        return element;
    }
    
    
}
