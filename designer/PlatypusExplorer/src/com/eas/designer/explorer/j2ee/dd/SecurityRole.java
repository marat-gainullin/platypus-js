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
public class SecurityRole implements ElementConvertable {

    public static final String TAG_NAME = "security-role";//NOI18N
    public static final String DESCRIPTION_TAG_NAME = "description";//NOI18N
    public static final String ROLE_NAME_TAG_NAME = "role-name";//NOI18N
    private String description;
    private String roleName;

    public SecurityRole(String aRoleName) {
        roleName = aRoleName;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String aDescription) {
        description = aDescription;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String aRoleName) {
        roleName = aRoleName;
    }

    @Override
    public Element getElement(Document aDoc) {
        Element element = aDoc.createElement(TAG_NAME);
        if (description != null) {
            Element descriptionElement = aDoc.createElement(DESCRIPTION_TAG_NAME);
            descriptionElement.setTextContent(description);
            element.appendChild(descriptionElement);
        }
        Element roleNameElement = aDoc.createElement(ROLE_NAME_TAG_NAME);
        roleNameElement.setTextContent(roleName);
        element.appendChild(roleNameElement);
        return element;
    }
}
