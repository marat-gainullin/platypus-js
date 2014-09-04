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
import com.eas.client.sqldrivers.SqlDriver;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 *
 * @author mg
 */
public interface DbClient extends Client {

    /**
     * Returns conection schema name.
     *
     * @param aDatasource Identifier of database connection. Null means
     * application database connection.
     * @return Schema name, that is used to connect to database (default
     * schema).
     * @throws java.lang.Exception
     */
    public String getConnectionSchema(String aDatasource) throws Exception;

    /**
     * Returns conection dialect name.
     *
     * @param aDatasource Identifier of database connection. Null means
     * application database connection.
     * @return Schema name, that is used to connect to database (dialect of
     * default connection).
     * @throws java.lang.Exception
     */
    public String getConnectionDialect(String aDatasource) throws Exception;

    /**
     * Returns conection sql driver. Selection is based on dialect name.
     *
     * @param aDatasource Identifier of database connection. Null means
     * application database connection.
     * @return Schema name, that is used to connect to database (dialect of
     * default connection).
     * @throws java.lang.Exception
     */
    public SqlDriver getConnectionDriver(String aDatasource) throws Exception;

    /**
     * Creates and returns new data flow provider, setted up with settings
     * passed through parameters
     *
     * @param aDatasourceId Datasource JNDI name. May be null for default
     * database.
     * @param aEntityId Data entity identifier. Table name or query identifier.
     * Table name is allowed in two-tier mode only.
     * @param aSqlClause Query text for execute as select.
     * @param aExpectedFields Fields instance to be used as rowset's fields. If
     * it is null, than database driven fields will be used as rowset's fields.
     * @param aReadRoles A set of roles allowed to select data with this
     * provider.
     * @param aWriteRoles A set of roles allowed to update data with this
     * provider.
     * @return Data flow provider instance created.
     * @throws java.lang.Exception
     */
    public FlowProvider createFlowProvider(String aDatasourceId, String aEntityId, String aSqlClause, Fields aExpectedFields, Set<String> aReadRoles, Set<String> aWriteRoles) throws Exception;

    /**
     * Returns metadata cache for the specified database.
     *
     * @param aDatasourceId Datasource JNDI name. May be null for default
     * database.
     * @return DbMetadataCache instance.
     * @throws Exception
     * @see DbMetadataCache
     */
    public DbMetadataCache getDbMetadataCache(String aDatasourceId) throws Exception;

    /**
     * Executes an arbitrary sql statement through a SqlCompiledQuery instance.
     * Wraps query executing in a single transaction. Commit is called
     * immidiatly after executing the query.
     *
     * @param aQuery SqlCompiledQuery instance for execute i.e. insert, update
     * or delete dml statements.
     * @param onSuccess
     * @param onFailure
     * @return Rows count affected by this query.
     * @throws Exception
     * @see SqlCompiledQuery
     */
    public int executeUpdate(SqlCompiledQuery aQuery, Consumer<Integer> onSuccess, Consumer<Exception> onFailure) throws Exception;

    /**
     * Commits all previous calls to executeUpdate and enqueueRowsetUpdate
     * methods in aSessionId context.
     *
     * @param aDatasourcesChangeLogs Changes to be commited to various
     * datasources.
     * @param onSuccess
     * @param onFailure
     * @throws Exception
     * @return Affected in this transaction rows count from commited calls to
     * enqueueUpdate, not to enqueueRowsetUpdate. Number of affected rows by
     * rowset's changes you can take from the rowset directly.
     * @see #enqueueRowsetUpdate(com.bearsoft.rowset.Rowset)
     *
     */
    public int commit(Map<String, List<Change>> aDatasourcesChangeLogs, Consumer<Integer> onSuccess, Consumer<Exception> onFailure) throws Exception;

    public void rollback();

    /**
     * Returns PrincipalHost of the DbCilent implementation. Service for
     * providing access to Principal in the current environment.
     *
     * @return Current PrincipalHost
     */
    public PrincipalHost getPrincipalHost();

    /**
     *
     * @param aDatasourceId Datasource JNDI name. May be null for default
     * database.
     * @return Results rowset
     * @throws Exception
     */
    public Rowset getDbTypesInfo(String aDatasourceId) throws Exception;
}
