/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.metadata.Fields;
import com.eas.client.metadata.DbTableIndexes;
import com.eas.client.sqldrivers.SqlDriver;

/**
 *
 * @author mg
 */
public interface DbMetadataCache {

    /**
     * Achieves table metadata like fields, primary keys, foregn keys, field's comments.
     * This information may be achieved in parts with <code>getTableComments</code> and <code>getTableKeys</code> methods.
     * @param aTableName Table, or query name in "Q<id>" format.
     * @return Fields instance with comments, primary and foreign key resolved
     * @throws Exception
     * @see Fields
     * @see #getTableComments(java.lang.String)
     * @see #getTableKeys(java.lang.String)
     */
    public Fields getTableMetadata(String aTableName) throws Exception;

    /**
     * Removes table's metadata from the cache.
     * @param aTableName Table, or query name in "Q<id>" format.
     * @throws Exception
     */
    public void removeTableMetadata(String aTableName) throws Exception;

    /**
     * Removes table's indexes information from the cache.
     * @param aTableName Table, or query name in "Q<id>" format.
     * @throws Exception
     */
    public void removeTableIndexes(String aTableName) throws Exception;

    /**
     * Teets if table metadata is present in the cache
     * @param aTableName Table, or query name in "Q<id>" format.
     * @return True if metadata is present, false otherwise.
     * @throws Exception
     */
    public boolean containsTableMetadata(String aTableName) throws Exception;

    /**
     * Returns information about table indexes.
     * @param aTableName Table, or query name in "Q<id>" format.
     * @return DbTableIndexes instacne, containing information about the table indexes.
     * @throws Exception
     * @see DbTableIndexes
     */
    public DbTableIndexes getTableIndexes(String aTableName) throws Exception;

    /**
     * Retrieves DBMS supported data types through ordinary query.
     * @return Rowset instance, containing all of the needed information about DBMS supported data types.
     */
    public Rowset getDbTypesInfo() throws Exception;

    /**
     * Returns connection schema explicitly
     * @return Connection schema name.
     */
    public String getConnectionSchema() throws Exception;

    /**
     * Returns connection driver, according to DBMS dialect.
     * @return DBMS sql driver.
     * @see EasSqlDriver
     */
    public SqlDriver getConnectionDriver() throws Exception;

    public void fillTablesCacheByConnectionSchema(boolean aFullMetadata) throws Exception;

    public void fillTablesCacheBySchema(String aSchema, boolean aFullMetadata) throws Exception;
    /**
     * clears all cached data (tables, fields, schema name, driver instance etc).
     */
    public void clear() throws Exception;
}
