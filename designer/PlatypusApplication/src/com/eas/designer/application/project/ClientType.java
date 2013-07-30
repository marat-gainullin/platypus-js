/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.project;

import org.openide.util.NbBundle;

/**
 * Represents a types of a client application in Platypus.
 * @author vv
 */
public enum ClientType {
    
    PLATYPUS_CLIENT("platypus", "clientType_platypusClientName"), //NOI18N
    WEB_BROWSER("html", "clientType_webBrowserName"); //NOI18N
    
    private final String id;
    private final String resName;
    
    ClientType(String anId, String aResName) {
        id = anId;
        resName = aResName;
    }
    
    /**
     * Identifier can be used for example in persistence of ClientType object.
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
     * @param anId Identifier of an ClientType object.
     * @return Corresponding object of null if nothing is found. 
     */
    public static ClientType getById(String anId) {
        for (ClientType i : values()) {
            if (i.getId().equals(anId)) {
                return i;
            }
        }
        return null;
    }
}
