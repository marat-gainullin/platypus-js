/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.queries;

/**
 * Methods of this class take care of context access by internals of database client.
 * Such internals may switch schema/user context while quering data and/or updating database tables.
 * @author mg
 */
public interface ContextHost {
    
    /**
     * @return Context name for straight preparation.
     * @throws Exception 
     */
    public String preparationContext() throws Exception;
    
    /**
     * @return Context name for returning to previous state while cleaning operation.
     * @throws Exception 
     */
    public String unpreparationContext() throws Exception;
}
