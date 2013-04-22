/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.bearsoft.rowset.dataflow.TransactionListener;
import com.eas.client.queries.Query;
import com.eas.client.settings.EasSettings;

/**
 * Interface, declaring work with metadata caches, login/logout, resources and transactions
 * @author mg
 */
public interface Client {

    public static final String APPLICATION_LOGGER_NAME = "Application";

    /**
     * Returns an applaication alement, that should be used to start applicationwork.
     * @return Application element identifier,
     * @throws Exception 
     */
    public String getStartAppElement() throws Exception;
    
    /**
     * Returns settings, used to connect to the server (dbms or application server).
     * @return Settings, have been used to connect to the server.
     */
    public EasSettings getSettings();
    
    /**
     * Shuts application down. Frees all resources and disconnects from any servers.
     */
    public void shutdown();

    /**
     * Returns Query instance, containing fields and parameters description.
     * It returned without sql text and main table.
     * @return Query instance.
     */
    public Query getAppQuery(String aQueryId) throws Exception;

    /**
     * Returns application elements cache.
     * @return AppCache instance.
     * @see AppCache
     */
    public AppCache getAppCache() throws Exception;

    public TransactionListener.Registration addTransactionListener(TransactionListener aListener);
    
    /**
     * Performs all necessary work on inner structures, such as caches, according to
     * changing of the application element. If the element is null than whole cleening is performed.
     * @param aEntityId Identifier of the application element.
     * @throws Exception
     */
    public void appEntityChanged(String aEntityId) throws Exception;

    /**
     * Performs all necessary work on inner structures, such as caches, according to
     * changing of the database table.
     * @param aDbId A database identifier.
     * @param aSchema Schema name, the changing table belongs to.
     * @param aTable Changing table name
     * @throws Exception
     */
    public void dbTableChanged(String aDbId, String aSchema, String aTable) throws Exception;
}
