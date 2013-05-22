/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.j2ee.dd;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Defines which groups or principals have access to the collection of Web resources defined in this security constraint.
 * @author vv
 */
public class AuthConstraint implements ElementConvertable {

    public static final String TAG_NAME = "auth-constraint";// NOI18N
    public static final String ROLE_NAME_TAG_NAME = "role-name";// NOI18N
    public static final String DESCRIPTION_TAG_NAME = "description";// NOI18N
    private String roleName;
    private String description;

    public AuthConstraint(String aRoleName) {
        roleName = aRoleName;
    }
    
    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public Element getElement(Document aDoc) {
        Element element = aDoc.createElement(TAG_NAME);
        if (roleName != null) {
            Element roleNameElement = aDoc.createElement(ROLE_NAME_TAG_NAME);
            roleNameElement.setTextContent(roleName);
            element.appendChild(roleNameElement);
        }
        if (description != null) {
            Element descriptionElement = aDoc.createElement(DESCRIPTION_TAG_NAME);
            descriptionElement.setTextContent(description);
            element.appendChild(descriptionElement);
        }
        return element;
    }
    
}
