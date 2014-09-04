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
 * Interface, declaring work with queries, transactions and some common methods.
 *
 * @author mg
 */
public interface Client {

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

    public ListenerRegistration addTransactionListener(TransactionListener aListener);
}
