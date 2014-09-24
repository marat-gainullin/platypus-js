package com.eas.client.dbstructure.gui;

import com.eas.client.DatabasesClient;


/**
 * A callback for table data query.
 *
 * @author vv
 */
public interface RunQueryCallback {

    public void runQuery(DatabasesClient aClient, String aDbId, String aSchemaName, String aTableName);
}
