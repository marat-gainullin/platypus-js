/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.dataflow.FlowProvider;
import com.bearsoft.rowset.metadata.Fields;
import com.eas.client.login.PrincipalHost;
import com.eas.client.queries.SqlCompiledQuery;
import com.eas.client.queries.SqlQuery;
import java.util.List;
import java.util.Set;

/**
 *
 * @author mg
 */
public interface DbClient extends Client {
    
    /**
     * Returns conection schema name.
     * @param aDbId Identifier of database connection. Null means application database connection.
     * @return Schema name, that is used to connect to database (default schema).
     */
    public String getConnectionSchema(String aDbId) throws Exception;
    
    /**
     * Returns conection dialect name.
     * @param aDbId Identifier of database connection. Null means application database connection.
     * @return Schema name, that is used to connect to database (dialect of default connection).
     */
    public String getConnectionDialect(String aDbId) throws Exception;
    
    /**
     * Returns SqlQuery instance, containing fields and parameters description.
     * It returned without sql text and main table.
     * @return SqlQuery instance.
     */
    @Override
    public SqlQuery getAppQuery(String aQueryId) throws Exception;
    
    /**
     * Creates and returns new data flow provider, setted up with settings passed through parameters
     * @param aDatabaseId Connection identifier. May be null for metabase.
     * @param aSessionId Session identifier. May be null for system session.
     * @param aEntityId Data entity identifier. Table name or query identifier. Table name is allowed in two-tier mode only.
     * @param aSqlClause Query text for execute as select.
     * @param aExpectedFields Fields instance to be used as rowset's fields. If it is null, than database driven fields will be used as rowset's fields.
     * @param aReadRoles A set of roles allowed to select data with this provider.
     * @param aWriteRoles A set of roles allowed to update data with this provider.
     * @return Data flow provider instance created.
     */
    public FlowProvider createFlowProvider(String aDatabaseId, String aSessionId, String aEntityId, String aSqlClause, Fields aExpectedFields, Set<String> aReadRoles, Set<String> aWriteRoles) throws Exception;

    public List<Change> getChangeLog(String aDatabaseId, String aSessionId);
    
    /**
     * Returns metadata cache for the specified database.
     * @param aDbId Databse identifier
     * @return DbMetadataCache instance.
     * @throws Exception
     * @see DbMetadataCache
     */
    public DbMetadataCache getDbMetadataCache(String aDbId) throws Exception;

    public void setAppCache(AppCache aCache);
    
    /**
     * Enqueues an arbitrary sql statement through a SqlCompiledQuery instance.
     * Begins a transaction. Returns nothing. The affected rows amount is returned by commit method.
     * Database id and session id are incapsulated in SqlCompiledQuery instance
     * @param aQuery SqlCompiledQuery instance for execute i.e. insert, update or delete dml statements.
     * @throws Exception
     * @see SqlCompiledQuery
     * @see #commit(java.lang.String)
     * @see #rollback(java.lang.String)
     */
    public void enqueueUpdate(SqlCompiledQuery aQuery) throws Exception;

    /**
     * Executes an arbitrary sql statement through a SqlCompiledQuery instance.
     * Wraps query executing in a single transaction. Commit is called immidiatly after 
     * executing the query.
     * @param aQuery SqlCompiledQuery instance for execute i.e. insert, update or delete dml statements.
     * @return Rows count affected by this query.
     * @throws Exception
     * @see SqlCompiledQuery
     */
    public int executeUpdate(SqlCompiledQuery aQuery) throws Exception;
    
    /**
     * Commits all previous calls to executeUpdate and enqueueRowsetUpdate methods in aSessionId context.
     * @param aSessionId A session id in wich context the changes are to be applied.
     * @throws Exception
     * @return Affected in this transaction rows count from commited calls to enqueueUpdate, not to enqueueRowsetUpdate.
     * Number of affected rows by rowset's changes you can take from the rowset directly.
     * @see #enqueueRowsetUpdate(com.bearsoft.rowset.Rowset)
     */
    public int commit(String aSessionId) throws Exception;

    /**
     * Forgets all previous calls to executeUpdate in aSessionId context.
     * @param aSessionId  A session id in wich context the changes are to be forget.
     */
    public void rollback(String aSessionId);

    /**
     * Returns PrincipalHost of the DbCilent implementation.
     * Service for providing access to Principal in the current environment.  
     * @return Current PrincipalHost
     */
    public PrincipalHost getPrincipalHost();

    public Rowset getDbTypesInfo(String dbId) throws Exception;
}
