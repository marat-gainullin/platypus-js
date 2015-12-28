/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.project;

import org.openide.util.NbBundle;

/**
 *
 * @author vv
 */
public enum AppServerType {

    NONE("none", "appServerType_none"), //NOI18N
    PLATYPUS_SERVER("platypus", "appServerType_platypusServer"), //NOI18N
    J2EE_SERVER("j2ee", "appServerType_j2eeServer"); //NOI18N

    private final String id;
    private final String resName;

    /**
     *
     * @param anId
     * @param aResName
     */
    AppServerType(String anId, String aResName) {
        id = anId;
        resName = aResName;
    }

    /**
     * Identifier can be used for example in persistence of AppServerType
     * object.
     *
     * @return String Id
     */
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return NbBundle.getMessage(ClientType.class, resName);
    }

    /**
     * Finds an instance by identifier.
     *
     * @param anId Identifier of an AppServerType object.
     * @return Corresponding object of null if nothing is found.
     */
    public static AppServerType getById(String anId) {
        for (AppServerType i : values()) {
            if (i.getId().equals(anId)) {
                return i;
            }
        }
        return null;
    }
}
