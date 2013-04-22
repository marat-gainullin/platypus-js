package com.eas.client.dbstructure.gui;

import com.eas.client.DbClient;

/**
 *  A callback for table data query. 
 * @author vv
 */
public interface RunQueryCallback {
        public void runQuery(DbClient aClient, String aDbId, String aSchemaName, String aTableName);
}
