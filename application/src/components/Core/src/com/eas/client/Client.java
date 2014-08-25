/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.bearsoft.rowset.dataflow.TransactionListener;
import com.eas.client.queries.Query;
import com.eas.util.ListenerRegistration;
import java.util.function.Consumer;

/**
 * Interface, declaring work with metadata caches, login/logout, resources and
 * transactions
 *
 * @author mg
 * @param <Q>
 */
public interface Client<Q extends Query> {

    public static final String APPLICATION_LOGGER_NAME = "Application";

    /**
     * Returns an applaication alement, that should be used to start
     * applicationwork.
     *
     * @param onSuccess
     * @param onFailure
     * @return Application element identifier,
     * @throws Exception
     */
    public String getStartAppElement(Consumer<String> onSuccess, Consumer<Exception> onFailure) throws Exception;

    /**
     * Frees all resources and disconnects from any servers.
     */
    public void shutdown();

    /**
     * Returns Query instance, containing fields and parameters description. It
     * returned without sql text and main table.
     *
     * @param aQueryId
     * @param onSuccess
     * @param onFailure
     * @return Query instance.
     * @throws java.lang.Exception
     */
    public Q getAppQuery(String aQueryId, Consumer<Q> onSuccess, Consumer<Exception> onFailure) throws Exception;

    /**
     * Returns application elements cache.
     *
     * @return AppCache instance.
     * @throws java.lang.Exception
     * @see AppCache
     */
    public AppCache getAppCache() throws Exception;

    public ListenerRegistration addTransactionListener(TransactionListener aListener);

    /**
     * Performs all necessary work on inner structures, such as caches,
     * according to changing of the application element. If the element is null
     * than whole cleening is performed.
     *
     * @param aEntityId Identifier of the application element.
     * @param onSuccess
     * @param onFailure
     * @throws Exception
     */
    public void appEntityChanged(String aEntityId, Consumer<Void> onSuccess, Consumer<Exception> onFailure) throws Exception;

    /**
     * Performs all necessary work on inner structures, such as caches,
     * according to changing of the database table.
     *
     * @param aDbId A database identifier.
     * @param aSchema Schema name, the changing table belongs to.
     * @param aTable Changing table name
     * @param onSuccess
     * @param onFailure
     * @throws Exception
     */
    public void dbTableChanged(String aDbId, String aSchema, String aTable, Consumer<Void> onSuccess, Consumer<Exception> onFailure) throws Exception;
}
