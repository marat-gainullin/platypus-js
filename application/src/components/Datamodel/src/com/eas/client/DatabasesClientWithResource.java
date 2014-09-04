/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.queries.QueriesProxy;
import com.eas.client.queries.SqlQuery;
import com.eas.client.resourcepool.GeneralResourceProvider;
import com.eas.client.settings.DbConnectionSettings;

/**
 *
 * @author mg
 */
public class DatabasesClientWithResource implements AutoCloseable {

    protected String resourceName;
    protected DatabasesClient client;

    public DatabasesClientWithResource(DbConnectionSettings aSettings, QueriesProxy<SqlQuery> aQueries) throws Exception {
        super();
        resourceName = "TestDb-" + String.valueOf(IDGenerator.genID());
        GeneralResourceProvider.getInstance().registerDatasource(resourceName, aSettings);
        client = new DatabasesClient(resourceName, aQueries, true);
    }

    public DatabasesClient getClient() {
        return client;
    }

    @Override
    public void close() throws Exception {
        client.shutdown();
        GeneralResourceProvider.getInstance().unregisterDatasource(resourceName);
    }

}
