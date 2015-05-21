/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.eas.client.resourcepool.BearResourcePool;
import com.eas.client.resourcepool.GeneralResourceProvider;
import com.eas.client.settings.DbConnectionSettings;
import com.eas.util.IDGenerator;

/**
 *
 * @author mg
 */
public class DatabasesClientWithResource implements AutoCloseable {

    protected String resourceName;
    protected DatabasesClient client;

    public DatabasesClientWithResource(DbConnectionSettings aSettings) throws Exception {
        super();
        resourceName = "TestDb-" + String.valueOf(IDGenerator.genID());
        GeneralResourceProvider.getInstance().registerDatasource(resourceName, aSettings);
        client = new DatabasesClient(resourceName, true, BearResourcePool.DEFAULT_MAXIMUM_SIZE);
    }

    public DatabasesClient getClient() {
        return client;
    }

    @Override
    public void close() throws Exception {
        GeneralResourceProvider.getInstance().unregisterDatasource(resourceName);
    }

}
