/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.j2ee.tomcat;

import org.w3c.dom.Element;

/**
 * Supports creation of Realm from XML element.
 * @author vv
 */
public class RealmFactory {
    
    /**
     * Creates an instance of Realm.
     * @param element XML element
     * @return an instance of Realm or null if Realm type is unknown.
     */
    public static Realm getRealm(Element element) {
        String className = element.getAttribute(Realm.CLASS_NAME_ATTR_NAME);
        Realm realm = null;
        switch (className) {
            case DataSourceRealm.ALL_ROLES_MODE_ATTR_NAME:
                realm = new DataSourceRealm();
                realm.load(element);
                break;
        }
        return realm;
    }
}
