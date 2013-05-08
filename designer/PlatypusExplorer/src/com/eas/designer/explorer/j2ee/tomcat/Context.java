/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.j2ee.tomcat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tomcat 7 web application context.
 *
 * @author vv
 */
public class Context {

    private Realm realm;
    private List<Resource> resources = new ArrayList<>();
    private String docBase;
    
    
    /**
     * Gets the Document Base value, representing directory (also known as the Context Root) for this web application.
     * @return docBase attribute value
     */
    public String getDocBase() {
        return docBase;
    }
    
    /**
     * Sets the Document Base, representing directory (also known as the Context Root) for this web application.
     * @param aDocBase a docBase attribute value
     */
    public void setDocBase(String aDocBase) {
        docBase = aDocBase;
    }
        
    /**
     * Gets the Realm for web application.
     * @return realm value
     */
    public Realm getRealm() {
        return realm;
    }
    
    /**
     * Sets the Realm for web application. 
     * @param aRealm 
     */
    public void setRealm(Realm aRealm) {
        realm = aRealm;
    }
    
    /**
     * Gets JNDI resources.
     * @return resources list
     */
    public List<Resource> getResources() {
        return Collections.unmodifiableList(resources);
    }
    
    /**
     * Adds a Resource to web application's context.
     * @param aResource resource
     */
    public synchronized void addResource(Resource aResource) {
        resources.add(aResource);
    }
    
    /**
     * Deletes a Resource from web application's context.
     * @param aResource resource
     */
    public synchronized void deleteResource(Resource aResource) {
        resources.remove(aResource);
    }
    
    /**
     * A Realm element represents a "database" of usernames, passwords, and roles (similar to Unix groups) assigned to those users.
     */
    public static class Realm {
    }

    /**
     * JNDI resource.
     */
    public static class Resource {
    }
}
