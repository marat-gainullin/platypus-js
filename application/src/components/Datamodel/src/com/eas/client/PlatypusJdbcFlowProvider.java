/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.eas.client.dataflow.JdbcFlowProvider;
import com.eas.client.metadata.Fields;
import com.eas.client.queries.ContextHost;
import java.sql.Connection;
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

    protected String entityName;
    protected DatabasesClient client;
    protected DatabaseMdCache cache;
    protected ContextHost contextHost;

    public PlatypusJdbcFlowProvider(DatabasesClient aClient, String aDataSourceName, String aEntityName, DataSource aDataSource, Consumer<Runnable> aDataPuller, DatabaseMdCache aCache, String aClause, Fields aExpectedFields, ContextHost aContextHost) throws Exception {
        super(aDataSourceName, aDataSource, aDataPuller, aClause, aExpectedFields);
        entityName = aEntityName;
        client = aClient;
        cache = aCache;
        contextHost = aContextHost;
    }

    @Override
    public String getEntityName() {
        return entityName;
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
