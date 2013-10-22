/* Datamodel license.
 * Exclusive rights on this code in any form
 * are belong to it's author. This code was
 * developed for commercial purposes only. 
 * For any questions and any actions with this
 * code in any form you have to contact to it's
 * author.
 * All rights reserved.
 */
package com.eas.client;

import com.bearsoft.rowset.Converter;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.changes.Command;
import com.bearsoft.rowset.changes.EntitiesHost;
import com.bearsoft.rowset.dataflow.FlowProvider;
import com.bearsoft.rowset.dataflow.TransactionListener;
import com.bearsoft.rowset.jdbc.JdbcReader;
import com.bearsoft.rowset.jdbc.StatementsGenerator;
import com.bearsoft.rowset.jdbc.StatementsGenerator.StatementsLogEntry;
import com.bearsoft.rowset.metadata.*;
import com.eas.client.cache.DatabaseAppCache;
import com.eas.client.cache.DatabaseMdCache;
import com.eas.client.cache.FilesAppCache;
import com.eas.client.cache.FilesAppCache.ScanCallback;
import com.eas.client.cache.PlatypusFiles;
import com.eas.client.login.DbPlatypusPrincipal;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.login.PrincipalHost;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.model.StoredQueryFactory;
import com.eas.client.queries.ContextHost;
import com.eas.client.queries.PlatypusJdbcFlowProvider;
import com.eas.client.queries.SqlCompiledQuery;
import com.eas.client.queries.SqlQuery;
import com.eas.client.resourcepool.GeneralResourceProvider;
import com.eas.client.settings.DbConnectionSettings;
import com.eas.client.settings.EasSettings;
import com.eas.client.sqldrivers.SqlDriver;
import com.eas.util.StringUtils;
import java.io.File;
import java.security.AccessControlException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 * Two tier implementation of Client interface.
 *
 * @author mg
 * @see Client
 */
public class DatabasesClient implements DbClient {

    public static final String BAD_LOGIN_MSG = "Login incorrect";
    private static final String USERNAME_PARAMETER_NAME = "userName";
    private static final String SELECT_BY_FIELD_QUERY = "select * from %s where Upper(%s) = :%s";
    private static final String USER_QUERY_TEXT = String.format(SELECT_BY_FIELD_QUERY, ClientConstants.T_MTD_USERS, ClientConstants.F_USR_NAME, USERNAME_PARAMETER_NAME);
    private static final String USER_GROUPS_QUERY_TEXT = String.format(SELECT_BY_FIELD_QUERY, ClientConstants.T_MTD_GROUPS, ClientConstants.F_USR_NAME, USERNAME_PARAMETER_NAME);
    public static final String TYPES_INFO_TRACE_MSG = "Getting types info. DbId %s";
    public static final String USER_MISSING_MSG = "No user found (%s)";
    // metadata
    protected Map<String, DatabaseMdCache> mdCaches = new HashMap<>();
    protected AppCache appCache = null;
    // resources
    protected GeneralResourceProvider resourceProvider;
    // settings
    protected DbConnectionSettings dbSettings = null;
    // queries
    protected StoredQueryFactory queries;
    // callback interface for context
    protected ContextHost contextHost;
    // callback interface for principal
    protected PrincipalHost principalHost;
    // transactions
    // Map<String - session id, Map<String - database id, ...
    protected final Map<String, Map<String, List<Change>>> transacted = new HashMap<>();
    protected Set<TransactionListener> transactionListeners = new HashSet<>();
    protected Set<QueriesListener> queriesListeners = new HashSet<>();

    /**
     * Constructs
     * <code>DatabasesClient</code> (two-tier mode).
     *
     * @param aSettings <code>DbConnectionSettings</code> instance, describing
     * url for connection and user credentials. There may be some additional
     * attributes, such as database dialect, etc.
     * @see ConnectionPrepender
     */
    public DatabasesClient(DbConnectionSettings aSettings) throws Exception {
        this(aSettings, false);
    }

    /**
     * Constructs
     * <code>DatabasesClient</code> (two-tier mode).
     *
     * @param aSettings <code>DbConnectionSettings</code> instance, describing
     * url for connection and user credentials. There may be some additional
     * attributes, such as database dialect, etc.
     * @param inContainer flag indicating that <code>DatabasesClient</code>
     * instance is creating in a container, i.e. J2EE application server.
     * @see ConnectionPrepender
     */
    public DatabasesClient(DbConnectionSettings aSettings, boolean inContainer) throws Exception {
        this(aSettings, inContainer, null);
    }

    public DatabasesClient(DbConnectionSettings aSettings, boolean inContainer, ScanCallback aScanCallback) throws Exception {
        super();
        dbSettings = aSettings;
        if (!inContainer) {
            DbConnectionSettings.registerDrivers(dbSettings.getDrivers().values());
        }
        resourceProvider = new GeneralResourceProvider(dbSettings, this);
        if (aSettings.getApplicationPath() != null && !aSettings.getApplicationPath().isEmpty()) {
            File f = new File(aSettings.getApplicationPath());
            if (f.exists() && f.isDirectory()) {
                FilesAppCache filesAppCache = new FilesAppCache(f.getPath() + File.separator + PlatypusFiles.PLATYPUS_PROJECT_SOURCES_ROOT, aScanCallback);
                filesAppCache.watch();
                appCache = filesAppCache;
            } else {
                appCache = new DatabaseAppCache(this);
                Logger.getLogger(DatabasesClient.class.getName()).log(Level.INFO, String.format("Path %s doesn't exist or it's not a directory. Falling back database application store", aSettings.getApplicationPath()));
            }
        } else {
            appCache = new DatabaseAppCache(this);
        }
        DatabaseMdCache dbMdCache = new DatabaseMdCache(this, null);
        mdCaches.put(null, dbMdCache);
        if (!dbSettings.isDeferCache()) {
            dbMdCache.fillTablesCacheByConnectionSchema(true);
        }
        queries = new StoredQueryFactory(this);
    }

    @Override
    public TransactionListener.Registration addTransactionListener(final TransactionListener aListener) {
        transactionListeners.add(aListener);
        return new TransactionListener.Registration() {
            @Override
            public void remove() {
                transactionListeners.remove(aListener);
            }
        };
    }

    public QueriesListener.Registration addQueriesListener(final QueriesListener aListener) {
        queriesListeners.add(aListener);
        return new QueriesListener.Registration() {
            @Override
            public void remove() {
                queriesListeners.remove(aListener);
            }
        };
    }

    protected void fireQueriesCleared() {
        for (QueriesListener l : queriesListeners.toArray(new QueriesListener[]{})) {
            l.cleared();
        }
    }

    @Override
    public void setAppCache(AppCache aAppCache) {
        appCache = aAppCache;
    }

    /**
     * @param aContextHost <code>ConnectionPrepender</code> instance to be used
     * in flow providers, created by the client.
     */
    public void setContextHost(ContextHost aContextHost) {
        contextHost = aContextHost;
    }

    @Override
    public EasSettings getSettings() {
        return dbSettings;
    }

    @Override
    public String getStartAppElement() throws Exception {
        String userName = principalHost.getPrincipal().getName();
        SqlQuery query = new SqlQuery(this, String.format(SQLUtils.SQL_SELECT_COMMON_WHERE_BY_FIELD, ClientConstants.T_MTD_USERS, ClientConstants.T_MTD_USERS, ClientConstants.F_USR_NAME, SQLUtils.SQL_PARAMETER_FIELD_VALUE));
        query.putParameter(SQLUtils.SQL_PARAMETER_FIELD_VALUE, DataTypeInfo.VARCHAR, userName);
        Rowset rowset = query.compile().executeQuery();
        if (!rowset.isEmpty()) {
            rowset.first();
            return rowset.getString(rowset.getFields().find(ClientConstants.F_USR_FORM));
        }
        return null;
    }

    @Override
    public synchronized List<Change> getChangeLog(String aDatabaseId, String aSessionId) {
        Map<String, List<Change>> enqueuedByDb = transacted.get(aSessionId);
        if (enqueuedByDb == null) {
            enqueuedByDb = new HashMap<>();
            transacted.put(aSessionId, enqueuedByDb);
        }
        List<Change> enqueued = enqueuedByDb.get(aDatabaseId);
        if (enqueued == null) {
            enqueued = new CopyOnWriteArrayList<>();
            enqueuedByDb.put(aDatabaseId, enqueued);
        }
        return enqueued;
    }

    /**
     * In server environment, there are needs to clear a part of change log
     * (logouted or dead client, for example).
     *
     * @param aSessionId
     */
    public synchronized void removeChangeLog(String aSessionId) {
        transacted.remove(aSessionId);
    }

    @Override
    public SqlQuery getAppQuery(String aQueryId) throws Exception {
        return queries.getQuery(aQueryId);
    }

    /**
     * Factory method for DatabaseFlowProvider. Intended to incapsulate flow
     * provider creation in two tier or three tier applications.
     *
     * @param aDbId
     * @param aSqlClause
     * @param aMainTable
     * @return FlowProvider created.
     * @throws Exception
     */
    @Override
    public FlowProvider createFlowProvider(String aDbId, String aSessionId, String aEntityId, String aSqlClause, Fields aExpectedFields, Set<String> aReadRoles, Set<String> aWriteRoles) throws Exception {
        return new PlatypusJdbcFlowProvider(this, aDbId, aSessionId, aEntityId, resourceProvider.getPooledDataSource(aDbId), getDbMetadataCache(aDbId), aSqlClause, aExpectedFields, contextHost, aReadRoles, aWriteRoles);
    }

    /*
     protected void convertPkFields2PkCols(Rowset aRowSet, String[] aPkNames) {
     if (aRowSet != null && aPkNames != null) {
     for (int i = 0; i < aPkNames.length; i++) {
     int colIndex = aRowSet.getFields().find(aPkNames[i]);
     if (colIndex > 0) {
     aRowSet.getFields().get(colIndex).setPk(true);
     }
     }
     }
     }
     */
    public String getSqlLogMessage(SqlCompiledQuery query) {
        StringBuilder sb = new StringBuilder("Executing SQL: ");
        sb.append(query.getSqlClause());
        if (query.getParameters().getParametersCount() > 0) {
            sb.append(" {");
            String delimiter = "";
            for (int i = 1; i <= query.getParameters().getParametersCount(); i++) {
                Parameter param = query.getParameters().get(i);
                sb.append(delimiter).append(Integer.toString(i)).append('@').append(param.getValue() == null ? "null" : param.getValue().toString());
                delimiter = ", ";
            }
            sb.append("}");
        }
        return sb.toString();
    }

    public static DbPlatypusPrincipal credentialsToPrincipalWithBasicAuthentication(DbClient aClient, String aUserName, String password) throws Exception {
        final SqlQuery q = new SqlQuery(aClient, USER_QUERY_TEXT);
        q.putParameter(USERNAME_PARAMETER_NAME, DataTypeInfo.VARCHAR, aUserName.toUpperCase());
        final Rowset rs = q.compile().executeQuery();
        if (rs.first() && password.equals(rs.getString(rs.getFields().find(ClientConstants.F_USR_PASSWD)))) {
            return new DbPlatypusPrincipal(aUserName,
                    rs.getString(rs.getFields().find(ClientConstants.F_USR_CONTEXT)),
                    rs.getString(rs.getFields().find(ClientConstants.F_USR_EMAIL)),
                    rs.getString(rs.getFields().find(ClientConstants.F_USR_PHONE)),
                    rs.getString(rs.getFields().find(ClientConstants.F_USR_FORM)),
                    getUserRoles(aClient, aUserName));
        }
        return null;
    }

    public static DbPlatypusPrincipal userNameToPrincipal(DbClient aClient, String aUserName) throws Exception {
        final SqlQuery q = new SqlQuery(aClient, USER_QUERY_TEXT);
        q.putParameter(USERNAME_PARAMETER_NAME, DataTypeInfo.VARCHAR, aUserName.toUpperCase());
        final Rowset rs = q.compile().executeQuery();
        if (rs.first()) {
            return new DbPlatypusPrincipal(aUserName,
                    rs.getString(rs.getFields().find(ClientConstants.F_USR_CONTEXT)),
                    rs.getString(rs.getFields().find(ClientConstants.F_USR_EMAIL)),
                    rs.getString(rs.getFields().find(ClientConstants.F_USR_PHONE)),
                    rs.getString(rs.getFields().find(ClientConstants.F_USR_FORM)),
                    getUserRoles(aClient, aUserName));
        }
        return null;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void shutdown() {
        assert resourceProvider != null;
        if (appCache instanceof FilesAppCache) {
            try {
                ((FilesAppCache) appCache).unwatch();
            } catch (Exception ex) {
                Logger.getLogger(DatabasesClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        resourceProvider.shutdown();
    }

    public void clearDbStatements(String aDbId) throws Exception {
        assert resourceProvider != null;
        if (aDbId == null) {
            //resourceProvider.clearPoolStatements(aDbId);
        } else {
            // removePool will clear statments, so no need to additional and non-atomic call
            //resourceProvider.removePool(aDbId);
        }
    }

    @Override
    public int executeUpdate(SqlCompiledQuery query) throws Exception {
        int rowsAffected = 0;
        Converter converter = getDbMetadataCache(query.getDatabaseId()).getConnectionDriver().getConverter();
        DataSource dataSource = resourceProvider.getPooledDataSource(query.getDatabaseId());
        if (dataSource != null) {
            try (Connection connection = dataSource.getConnection(); PreparedStatement stmt = connection.prepareStatement(query.getSqlClause())) {
                Parameters params = query.getParameters();
                for (int i = 1; i <= params.getParametersCount(); i++) {
                    Parameter param = params.get(i);
                    converter.convert2JdbcAndAssign(param.getValue(), param.getTypeInfo(), connection, i, stmt);
                }
                rowsAffected += stmt.executeUpdate();
                connection.commit();
            }
        }
        return rowsAffected;
    }

    @Override
    public void enqueueUpdate(SqlCompiledQuery aQuery) throws Exception {
        List<Change> log = getChangeLog(aQuery.getDatabaseId(), aQuery.getSessionId());
        Command command = new Command(aQuery.getEntityId());
        command.command = aQuery.getSqlClause();
        command.parameters = new Change.Value[aQuery.getParameters().getParametersCount()];
        for (int i = 0; i < command.parameters.length; i++) {
            Parameter param = aQuery.getParameters().get(i + 1);
            command.parameters[i] = new Change.Value(param.getName(), param.getValue(), param.getTypeInfo());
        }
        log.add(command);
    }

    @Override
    public synchronized AppCache getAppCache() throws Exception {
        return appCache;
    }

    @Override
    public synchronized DbMetadataCache getDbMetadataCache(String aDbId) throws Exception {
        if (!mdCaches.containsKey(aDbId)) {
            DatabaseMdCache cache = new DatabaseMdCache(this, aDbId);
            mdCaches.put(aDbId, cache);
            cache.fillTablesCacheByConnectionSchema(true);
        }
        return mdCaches.get(aDbId);
    }

    @Override
    public int commit(String aSessionId) throws Exception {
        synchronized (transacted) {
            int rowsAffected = 0;
            if (!transacted.containsKey(aSessionId)) {
                return rowsAffected;
            }
            Map<String, List<Change>> logByDb = transacted.get(aSessionId);
            assert logByDb != null;
            for (final String dbId : logByDb.keySet()) {
                SqlDriver driver = getDbMetadataCache(dbId).getConnectionDriver();
                Converter converter = driver.getConverter();
                List<Change> log = logByDb.get(dbId);
                assert log != null;
                DataSource dataSource = resourceProvider.getPooledDataSource(dbId);
                try (Connection connection = dataSource.getConnection()) {
                    connection.setAutoCommit(false);
                    try {
                        List<StatementsLogEntry> statements = new ArrayList<>();
                        // This structure helps us to avoid actuality check for queries while 
                        // processing each statement in transaction. Thus, we can avoid speed degradation.
                        // It doesn't break security, because such "unactual" lookup takes place ONLY
                        // while transaction processing.
                        final Map<String, SqlQuery> entityQueries = new HashMap<>();
                        for (Change change : log) {
                            StatementsGenerator generator = new StatementsGenerator(converter, new EntitiesHost() {
                                @Override
                                public void checkRights(String aEntityId) throws Exception {
                                    if (aEntityId != null) {
                                        SqlQuery query = entityQueries.get(aEntityId);
                                        if (query == null) {
                                            query = queries.getQuery(aEntityId, false);
                                            if (query != null) {
                                                entityQueries.put(aEntityId, query);
                                            }
                                        }
                                        if (query != null) {
                                            checkWritePrincipalPermission(query.getWriteRoles());
                                        }
                                    }
                                }

                                @Override
                                public Field resolveField(String aEntityId, String aFieldName) throws Exception {
                                    if (aEntityId != null) {
                                        Fields fields;
                                        SqlQuery query = entityQueries.get(aEntityId);
                                        if (query == null) {
                                            query = queries.getQuery(aEntityId, false);
                                            if (query != null) {
                                                entityQueries.put(aEntityId, query);
                                            }
                                        }
                                        if (query != null && query.getEntityId() != null) {
                                            fields = query.getFields();
                                        } else {
                                            fields = mdCaches.get(dbId).getTableMetadata(aEntityId);
                                        }
                                        if (fields != null) {
                                            Field resolved = fields.get(aFieldName);
                                            String resolvedTableName = resolved != null ? resolved.getTableName() : null;
                                            resolvedTableName = resolvedTableName != null ? resolvedTableName.toLowerCase() : "";
                                            if (query != null && query.getWritable() != null && !query.getWritable().contains(resolvedTableName)) {
                                                return null;
                                            } else {
                                                return resolved;
                                            }
                                        } else {
                                            Logger.getLogger(DatabasesClient.class.getName()).log(Level.WARNING, "Cant find fields for entity id:" + aEntityId);
                                            return null;
                                        }
                                    } else {
                                        return null;
                                    }
                                }
                            }, ClientConstants.F_USR_CONTEXT, contextHost != null ? contextHost.preparationContext() : null);
                            change.accept(generator);
                            statements.addAll(generator.getLogEntries());
                        }
                        rowsAffected = riddleStatements(statements, connection);
                        connection.commit();
                    } catch (Exception ex) {
                        connection.rollback();
                        rollback(aSessionId);
                        throw ex;
                    }
                }
                log.clear();
            }
            for (TransactionListener l : transactionListeners.toArray(new TransactionListener[]{})) {
                try {
                    l.commited();
                } catch (Exception ex) {
                    Logger.getLogger(DatabasesClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return rowsAffected;
        }
    }

    private void checkWritePrincipalPermission(Set<String> writeRoles) throws Exception {
        if (getPrincipalHost() != null && writeRoles != null && !writeRoles.isEmpty()) {
            PlatypusPrincipal principal = getPrincipalHost().getPrincipal();
            if (principal != null && principal.hasAnyRole(writeRoles)) {
                return;
            }
            throw new AccessControlException(String.format("Access denied for write query for %s PlatypusPrincipal.",//NOI18N
                    principal != null ? principal.getName() : null));
        }
    }

    @Override
    public void rollback(String aSessionId) {
        synchronized (transacted) {
            Map<String, List<Change>> logByDb = transacted.get(aSessionId);
            if (logByDb != null) {
                for (List<Change> log : logByDb.values()) {
                    log.clear();
                }
            }
            for (TransactionListener l : transactionListeners.toArray(new TransactionListener[]{})) {
                try {
                    l.rolledback();
                } catch (Exception ex) {
                    Logger.getLogger(DatabasesClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @Override
    public Rowset getDbTypesInfo(String aDbId) throws Exception {
        Logger.getLogger(DatabasesClient.class.getName()).fine(String.format(TYPES_INFO_TRACE_MSG, aDbId));
        Rowset lrowSet = new Rowset();
        JdbcReader rsReader = new JdbcReader(getDbMetadataCache(aDbId).getConnectionDriver().getConverter());
        DataSource dataSource = resourceProvider.getPooledDataSource(aDbId);
        if (dataSource != null) {
            try (Connection lconn = dataSource.getConnection()) {
                DatabaseMetaData dbmd = lconn.getMetaData();
                if (dbmd != null) {
                    try (ResultSet rs = dbmd.getTypeInfo()) {
                        lrowSet = rsReader.readRowset(rs, -1);
                    }
                }
            }
        }
        return lrowSet;
    }

    protected void clearCaches() throws Exception {
        for (DatabaseMdCache cache : mdCaches.values()) {
            cache.clear();
        }
        appCache.clear();
        queries.clearCache();
        clearDbStatements(null);
        fireQueriesCleared();
    }

    @Override
    public void appEntityChanged(String aEntityId) throws Exception {
        if (aEntityId != null) {
            AppCache cache = getAppCache();
            ApplicationElement appElement = cache.get(aEntityId);
            if (appElement != null) {
                if (appElement.getType() == ClientConstants.ET_QUERY) {
                    //queries.clearCache(aEntityId);// Bad solution. There are may be some queries, using this query and so on.
                    clearQueries(null);// possible overhead, but this is better way than previous.
                } else if (appElement.getType() == ClientConstants.ET_CONNECTION) {
                    DbMetadataCache dbMdCache = getDbMetadataCache(aEntityId);
                    if (dbMdCache != null) {
                        dbMdCache.clear();
                    }
                    clearQueries(null);// possible overhead, but this is better way than previous.
                }
            } else {
                clearQueries(null);// possible overhead, but this is better way than previous.
            }
            cache.remove(aEntityId);
        } else {
            clearCaches();
        }
    }

    protected void clearQueries(String aDbId) throws Exception {
        clearDbStatements(null);
        queries.clearCache();
        fireQueriesCleared();
    }

    @Override
    public void dbTableChanged(String aDbId, String aSchema, String aTable) throws Exception {
        DbMetadataCache cache = getDbMetadataCache(aDbId);
        String fullTableName = aTable;
        if (aSchema != null && !aSchema.isEmpty()) {
            fullTableName = aSchema + "." + fullTableName;
        }
        cache.removeTableMetadata(fullTableName);
        cache.removeTableIndexes(fullTableName);
        clearQueries(aDbId);
    }

    /**
     * Returns StoredQueryFactory instance, used by this client. Such
     * functionality is specific to two-tier client. So do move it to ClientIntf
     * interface.
     *
     * @return StoredQueryFactory instance.
     * @see StoredQueryFactory
     * @see ClientIntf
     */
    public StoredQueryFactory getQueryFactory() {
        return queries;
    }

    public void setQueryFactory(StoredQueryFactory aQueries) {
        queries = aQueries;
    }

    @Override
    public PrincipalHost getPrincipalHost() {
        return principalHost;
    }

    public void setPrincipalHost(PrincipalHost aPrincipalHost) {
        principalHost = aPrincipalHost;
    }

    @Override
    public String getConnectionSchema(String aDbId) throws Exception {
        return resourceProvider.getPoolSchema(aDbId);
    }

    @Override
    public String getConnectionDialect(String aDbId) throws Exception {
        return resourceProvider.getPoolDialect(aDbId);
    }

    protected static Set<String> getUserRoles(DbClient aClient, String aUserName) throws Exception {
        Set<String> roles = new HashSet<>();
        final SqlQuery q = new SqlQuery(aClient, USER_GROUPS_QUERY_TEXT);
        q.putParameter(USERNAME_PARAMETER_NAME, DataTypeInfo.VARCHAR, aUserName.toUpperCase());
        final Rowset rs = q.compile().executeQuery();
        int roleNameColumnIndex = rs.getFields().find(ClientConstants.F_GROUP_NAME);
        for (int i = 1; i <= rs.size(); i++) {
            roles.add((String) rs.getRow(i).getColumnObject(roleNameColumnIndex));
        }
        return roles;
    }

    private int riddleStatements(List<StatementsLogEntry> aStatements, Connection aConnection) throws Exception {
        int rowsAffected = 0;
        if (!aStatements.isEmpty()) {
            List<StatementsLogEntry> errorStatements = new ArrayList<>();
            List<String> errors = new ArrayList<>();
            for (StatementsLogEntry entry : aStatements) {
                try {
                    rowsAffected += entry.apply(aConnection);
                } catch (Exception ex) {
                    errorStatements.add(entry);
                    errors.add(ex.getMessage());
                    Logger.getLogger(DatabasesClient.class.getName()).log(Level.SEVERE, ex.getMessage());
                }
            }
            if (errorStatements.size() == aStatements.size()) {
                throw new SQLException(StringUtils.join(System.getProperty(ClientConstants.LINE_SEPARATOR_PROP_NAME), errors.toArray(new String[]{})));
            } else if (errorStatements.size() < aStatements.size()) {
                rowsAffected += riddleStatements(errorStatements, aConnection);
            }
        }
        return rowsAffected;
    }
}
