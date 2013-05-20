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
public class UserDataConstraint implements ElementConvertable {

    public static final String TAG_NAME = "login-config"; //NOI18N
    
    @Override
    public Element getElement(Document aDoc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
