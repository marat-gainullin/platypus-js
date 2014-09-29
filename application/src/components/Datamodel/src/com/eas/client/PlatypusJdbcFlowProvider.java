/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.dataflow.JdbcFlowProvider;
import com.bearsoft.rowset.dataflow.TransactionListener;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameters;
import com.eas.client.queries.ContextHost;
import com.eas.util.ListenerRegistration;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import javax.sql.DataSource;

/**
 * This flow provider implements transaction capability for standard JdbcFlowProvider.
 * It enqueues changes in transactional queue instead of actual writing
 * to underlying database. It relies on transactional assumption: all enqueued changes
 * will be actually applied at commmit or reverted at rollback.
 * @author mg
 */
public class PlatypusJdbcFlowProvider extends JdbcFlowProvider<String> {

    protected String entityId;
    protected DatabasesClient client;
    protected DatabaseMdCache cache;
    protected ContextHost contextHost;
    protected List<Change> changeLog = new ArrayList<>();
    protected Set<String> readRoles = new HashSet<>();
    protected Set<String> writeRoles = new HashSet<>();

    public PlatypusJdbcFlowProvider(DatabasesClient aClient, String aJdbcSourceId, String aEntityId, DataSource aDataSource, ExecutorService aDataPuller, DatabaseMdCache aCache, String aClause, Fields aExpectedFields, ContextHost aContextHost, Set<String> aReadRoles, Set<String> aWriteRoles) throws Exception {
        super(aJdbcSourceId, aDataSource, aDataPuller, aCache.getConnectionDriver().getConverter(), aClause, aExpectedFields);
        entityId = aEntityId;
        client = aClient;
        cache = aCache;
        contextHost = aContextHost;
        readRoles = aReadRoles;
        writeRoles = aWriteRoles;
    }

    @Override
    public ListenerRegistration addTransactionListener(TransactionListener tl) {
        return client.addTransactionListener(tl);
    }

    @Override
    public String getEntityId() {
        return entityId;
    }

    public Set<String> getReadRoles() {
        return readRoles != null ? Collections.unmodifiableSet(readRoles) : null;
    }
    
    public Set<String> getWriteRoles() {
        return writeRoles != null ? Collections.unmodifiableSet(writeRoles) : null;
    }

    @Override
    public List<Change> getChangeLog() {
        return changeLog;
    }
    
    @Override
    public Rowset refresh(Parameters aParams, Consumer<Rowset> onSuccess, Consumer<Exception> onFailure) throws Exception {
        return super.refresh(aParams, onSuccess, onFailure);
    }

    @Override
    protected void prepareConnection(Connection aConnection) throws Exception {
        if (contextHost != null && contextHost.preparationContext() != null && !contextHost.preparationContext().isEmpty()) {
            cache.getConnectionDriver().applyContextToConnection(aConnection, contextHost.preparationContext());
        }
    }

    @Override
    protected void unprepareConnection(Connection aConnection) throws Exception {
        // In the following condition, _PR_eparation context is checked. It's right, because we need to cancel _PR_eparation
        // if it has been made. And so, condition checks a _PR_eparation context, but _UN_preparation context is applied.
        // If no preparation has been made, no unpreparation should occur!
        if (contextHost != null && contextHost.preparationContext() != null && !contextHost.preparationContext().isEmpty()) {
            cache.getConnectionDriver().applyContextToConnection(aConnection, contextHost.unpreparationContext());
        }
    }
}
