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
import com.bearsoft.rowset.exceptions.ResourceUnavalableException;
import com.bearsoft.rowset.jdbc.JdbcReader;
import com.bearsoft.rowset.jdbc.StatementsGenerator;
import com.bearsoft.rowset.jdbc.StatementsGenerator.StatementsLogEntry;
import com.bearsoft.rowset.metadata.*;
import com.eas.client.cache.DatabaseMdCache;
import com.eas.client.cache.FilesAppCache;
import com.eas.client.cache.FilesAppCache.ScanCallback;
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
import com.eas.client.sqldrivers.SqlDriver;
import com.eas.util.ListenerRegistration;
import com.eas.util.StringUtils;
import java.security.AccessControlException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
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
    protected boolean autoFillMetadata = true;
    protected AppCache appCache;
    // queries
    protected StoredQueryFactory queries;
    // callback interface for context
    protected ContextHost contextHost;
    // callback interface for principal
    protected PrincipalHost principalHost;
    // transactions
    // Map<String - session id, Map<String - database id, ...
    protected final Map<String, Map<String, List<Change>>> transacted = new HashMap<>();
    protected final Set<TransactionListener> transactionListeners = new HashSet<>();
    protected final Set<QueriesListener> queriesListeners = new HashSet<>();
    // datasource name used by default. E.g. in queries with null datasource name
    protected String defaultDatasourceName;

    /**
     * Constructs <code>DatabasesClient</code> (two-tier mode).
     *
     * @param anAppCache
     * @param aDefaultDatasourceName Datasource name used by default. E.g. in
     * queries with null datasource name
     * @param aAutoFillMetadata If true, metadatacache will be filled with
     * tables, keys and other metadata in schema automatically. Otherwise it
     * will query metadata table by table in each case. Default is true.
     * @throws java.lang.Exception
     */
    public DatabasesClient(AppCache anAppCache, String aDefaultDatasourceName, boolean aAutoFillMetadata) throws Exception {
        this(anAppCache, aDefaultDatasourceName, aAutoFillMetadata, null);
    }

    /**
     *
     * @param anAppCache
     * @param aDefaultDatasourceName
     * @param aAutoFillMetadata If true, metadatacache will be filled with
     * tables, keys and other metadata in schema automatically. Otherwise it
     * will query metadata table by table in each case. Default is true.
     * @param aScanCallback
     * @throws Exception
     */
    public DatabasesClient(AppCache anAppCache, String aDefaultDatasourceName, boolean aAutoFillMetadata, ScanCallback aScanCallback) throws Exception {
        super();
        autoFillMetadata = aAutoFillMetadata;
        defaultDatasourceName = aDefaultDatasourceName;
        appCache = anAppCache;
        if (appCache != null) {
            queries = new StoredQueryFactory(this, appCache);
        }
    }

    public String getDefaultDatasourceName() {
        return defaultDatasourceName;
    }

    public void setDefaultDatasourceName(String aValue) throws Exception {
        if (defaultDatasourceName == null ? aValue != null : !defaultDatasourceName.equals(aValue)) {
            defaultDatasourceName = aValue;
            DatabaseMdCache mdCache = mdCaches.get(null);
            if (mdCache != null) {
                mdCache.clear();
            }
            mdCaches.remove(null);
            appEntityChanged(null);
        }
    }

    public boolean isAutoFillMetadata() {
        return autoFillMetadata;
    }

    public DataSource obtainDataSource(String aDataSourceId) throws Exception {
        if (aDataSourceId == null) {
            aDataSourceId = defaultDatasourceName;
        }
        if (aDataSourceId != null) {
            Context initContext = new InitialContext();
            try {
                // J2EE servers
                return (DataSource) initContext.lookup(aDataSourceId);
            } catch (NamingException ex) {
                try {
                    // Apache Tomcat component's JNDI context 
                    Context envContext = (Context) initContext.lookup("java:/comp/env"); //NOI18N
                    return (DataSource) envContext.lookup(aDataSourceId);
                } catch (NamingException ex1) {
                    // Platypus standalone server or client
                    return GeneralResourceProvider.getInstance().getPooledDataSource(aDataSourceId);
                }
            }
        } else {
            throw new NamingException("Null datasource name is not allowed (Default datasource name is null also).");
        }
    }

    @Override
    public ListenerRegistration addTransactionListener(final TransactionListener aListener) {
        synchronized (transacted) {
            transactionListeners.add(aListener);
            return new ListenerRegistration() {
                @Override
                public void remove() {
                    synchronized (transacted) {
                        transactionListeners.remove(aListener);
                    }
                }
            };
        }
    }

    @Override
    public ListenerRegistration addQueriesListener(final QueriesListener aListener) {
        synchronized (queriesListeners) {
            queriesListeners.add(aListener);
            return new ListenerRegistration() {
                @Override
                public void remove() {
                    synchronized (queriesListeners) {
                        queriesListeners.remove(aListener);
                    }
                }
            };
        }
    }

    protected void fireQueriesCleared() {
        synchronized (queriesListeners) {
            for (QueriesListener l : queriesListeners.toArray(new QueriesListener[]{})) {
                l.cleared();
            }
        }
    }

    /**
     * @param aContextHost <code>ConnectionPrepender</code> instance to be used
     * in flow providers, created by the client.
     */
    public void setContextHost(ContextHost aContextHost) {
        contextHost = aContextHost;
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
    public synchronized List<Change> getChangeLog(String aDatasourceId, String aSessionId) {
        Map<String, List<Change>> enqueuedByDb = transacted.get(aSessionId);
        if (enqueuedByDb == null) {
            enqueuedByDb = new HashMap<>();
            transacted.put(aSessionId, enqueuedByDb);
        }
        List<Change> enqueued = enqueuedByDb.get(aDatasourceId);
        if (enqueued == null) {
            enqueued = new CopyOnWriteArrayList<>();
            enqueuedByDb.put(aDatasourceId, enqueued);
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
        return getAppQuery(aQueryId, true);
    }

    public SqlQuery getAppQuery(String aQueryId, boolean aCopy) throws Exception {
        if (queries == null) {
            throw new IllegalStateException("Query factory is absent, so don't call getAppQuery.");
        }
        return queries.getQuery(aQueryId, aCopy);
    }

    /**
     * Factory method for DatabaseFlowProvider. Intended to incapsulate flow
     * provider creation in two tier or three tier applications.
     *
     * @param aDatasourceId
     * @param aSessionId
     * @param aEntityId
     * @param aSqlClause
     * @param aExpectedFields
     * @param aReadRoles
     * @param aWriteRoles
     * @return FlowProvider created.
     * @throws Exception
     */
    @Override
    public FlowProvider createFlowProvider(String aDatasourceId, String aSessionId, String aEntityId, String aSqlClause, Fields aExpectedFields, Set<String> aReadRoles, Set<String> aWriteRoles) throws Exception {
        return new PlatypusJdbcFlowProvider(this, aDatasourceId, aSessionId, aEntityId, obtainDataSource(aDatasourceId), getDbMetadataCache(aDatasourceId), aSqlClause, aExpectedFields, contextHost, aReadRoles, aWriteRoles);
    }

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

    public static DbPlatypusPrincipal credentialsToPrincipalWithBasicAuthentication(DatabasesClient aClient, String aUserName, String password) throws Exception {
        final SqlQuery q = new SqlQuery(aClient, USER_QUERY_TEXT);
        q.putParameter(USERNAME_PARAMETER_NAME, DataTypeInfo.VARCHAR, aUserName.toUpperCase());
        aClient.initUsersSpace(q.getDbId());
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

    public static DbPlatypusPrincipal userNameToPrincipal(DatabasesClient aClient, String aUserName) throws Exception {
        final SqlQuery q = new SqlQuery(aClient, USER_QUERY_TEXT);
        q.putParameter(USERNAME_PARAMETER_NAME, DataTypeInfo.VARCHAR, aUserName.toUpperCase());
        aClient.initUsersSpace(q.getDbId());
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
        if (appCache instanceof FilesAppCache) {
            try {
                ((FilesAppCache) appCache).unwatch();
            } catch (Exception ex) {
                Logger.getLogger(DatabasesClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public int executeUpdate(SqlCompiledQuery aQuery) throws Exception {
        int rowsAffected = 0;
        Converter converter = getDbMetadataCache(aQuery.getDatabaseId()).getConnectionDriver().getConverter();
        DataSource dataSource = obtainDataSource(aQuery.getDatabaseId());
        if (dataSource != null) {
            try (Connection connection = dataSource.getConnection(); PreparedStatement stmt = connection.prepareStatement(aQuery.getSqlClause())) {
                Parameters params = aQuery.getParameters();
                for (int i = 1; i <= params.getParametersCount(); i++) {
                    Parameter param = params.get(i);
                    converter.convert2JdbcAndAssign(param.getValue(), param.getTypeInfo(), connection, i, stmt);
                }
                try {
                    rowsAffected += stmt.executeUpdate();
                    connection.commit();
                } catch (SQLException ex) {
                    connection.rollback();
                    throw ex;
                }
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
    public synchronized DbMetadataCache getDbMetadataCache(String aDatasourceId) throws Exception {
        if (!mdCaches.containsKey(aDatasourceId)) {
            DatabaseMdCache cache = new DatabaseMdCache(this, aDatasourceId);
            mdCaches.put(aDatasourceId, cache);
            if (autoFillMetadata) {
                try {
                    cache.fillTablesCacheByConnectionSchema(true);
                } catch (ResourceUnavalableException ex) {
                    Logger.getLogger(DatabasesClient.class.getName()).log(Level.WARNING, ex.getMessage());
                }
            }
        }
        return mdCaches.get(aDatasourceId);
    }

    @Override
    public int commit(String aSessionId) throws Exception {
        synchronized (transacted) {
            try {
                int rowsAffected = 0;
                if (!transacted.containsKey(aSessionId)) {
                    return rowsAffected;
                }
                Map<String, List<Change>> sessionLog = transacted.get(aSessionId);
                assert sessionLog != null;
                for (final String dbId : sessionLog.keySet()) {
                    List<Change> dbLog = sessionLog.get(dbId);
                    rowsAffected += commit(aSessionId, dbId, dbLog);
                }
                for (TransactionListener l : transactionListeners.toArray(new TransactionListener[]{})) {
                    try {
                        l.commited();
                    } catch (Exception ex) {
                        Logger.getLogger(DatabasesClient.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                return rowsAffected;
            } catch (Exception ex) {
                rollback(aSessionId);
                throw ex;
            }
        }
    }

    protected int commit(String aSessionId, final String aDatasourceId, List<Change> aLog) throws Exception {
        int rowsAffected = 0;
        SqlDriver driver = getDbMetadataCache(aDatasourceId).getConnectionDriver();
        if (driver != null) {
            Converter converter = driver.getConverter();
            assert aLog != null;
            DataSource dataSource = obtainDataSource(aDatasourceId);
            try (Connection connection = dataSource.getConnection()) {
                connection.setAutoCommit(false);
                try {
                    List<StatementsLogEntry> statements = new ArrayList<>();
                    // This structure helps us to avoid actuality check for queries while 
                    // processing each statement in transaction. Thus, we can avoid speed degradation.
                    // It doesn't break security, because such "unactual" lookup takes place ONLY
                    // while transaction processing.
                    final Map<String, SqlQuery> entityQueries = new HashMap<>();
                    for (Change change : aLog) {
                        StatementsGenerator generator = new StatementsGenerator(converter, new EntitiesHost() {
                            @Override
                            public void checkRights(String aEntityId) throws Exception {
                                if (queries != null && aEntityId != null) {
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
                                    SqlQuery query;
                                    if (queries != null) {
                                        query = entityQueries.get(aEntityId);
                                        if (query == null) {
                                            query = queries.getQuery(aEntityId, false);
                                            if (query != null) {
                                                entityQueries.put(aEntityId, query);
                                            }
                                        }
                                    } else {
                                        query = null;
                                    }
                                    Fields fields;
                                    if (query != null && query.getEntityId() != null) {
                                        fields = query.getFields();
                                    } else {// It seems, that aEntityId is a table name...
                                        fields = mdCaches.get(aDatasourceId).getTableMetadata(aEntityId);
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
                                        Logger.getLogger(DatabasesClient.class.getName()).log(Level.WARNING, "Cant find fields for entity id:{0}", aEntityId);
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
                    throw ex;
                }
            }
            aLog.clear();
            return rowsAffected;
        } else {
            Logger.getLogger(DatabasesClient.class.getName()).log(Level.INFO, "Unknown datasource: {0}. Can't commit to it", aDatasourceId);
            return 0;
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
    public Rowset getDbTypesInfo(String aDatasourceId) throws Exception {
        Logger.getLogger(DatabasesClient.class.getName()).fine(String.format(TYPES_INFO_TRACE_MSG, aDatasourceId));
        Rowset lrowSet = new Rowset();
        JdbcReader rsReader = new JdbcReader(getDbMetadataCache(aDatasourceId).getConnectionDriver().getConverter());
        DataSource dataSource = obtainDataSource(aDatasourceId);
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
        if (appCache != null) {
            appCache.clear();
        }
        clearQueries();
    }

    @Override
    public synchronized void appEntityChanged(String aEntityId) throws Exception {
        if (aEntityId != null) {
            if (appCache != null) {
                ApplicationElement appElement = appCache.get(aEntityId);
                if (appElement != null && (appElement.getType() == ClientConstants.ET_QUERY || appElement.getType() == ClientConstants.ET_COMPONENT)) {
                    clearQueries();
                }
                appCache.remove(aEntityId);
            }
        } else {
            clearCaches();
        }
    }

    public void clearQueries() throws Exception {
        if (queries != null) {
            queries.clearCache();
            fireQueriesCleared();
        }
    }

    @Override
    public void dbTableChanged(String aDatasourceName, String aSchema, String aTable) throws Exception {
        DbMetadataCache cache = getDbMetadataCache(aDatasourceName);
        String fullTableName = aTable;
        if (aSchema != null && !aSchema.isEmpty()) {
            fullTableName = aSchema + "." + fullTableName;
        }
        cache.removeTableMetadata(fullTableName);
        cache.removeTableIndexes(fullTableName);
        clearQueries();
    }

    /**
     * Returns StoredQueryFactory instance, used by this client. Such
     * functionality is specific to two-tier client. So don't move it to Client
     * interface.
     *
     * @return StoredQueryFactory instance.
     * @see StoredQueryFactory
     * @see Client
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
    public String getConnectionSchema(String aDatasourceId) throws Exception {
        DataSource ds = obtainDataSource(aDatasourceId);
        if (ds != null) {
            try (Connection conn = ds.getConnection()) {
                return schemaByConnection(conn);
            }
        } else {
            return null;
        }
    }

    @Override
    public String getConnectionDialect(String aDatasourceId) throws Exception {
        DataSource ds = obtainDataSource(aDatasourceId);
        try (Connection conn = ds.getConnection()) {
            return dialectByConnection(conn);
        }
    }

    @Override
    public SqlDriver getConnectionDriver(String aDatasourceId) throws Exception {
        DataSource ds = obtainDataSource(aDatasourceId);
        try (Connection conn = ds.getConnection()) {
            return SQLUtils.getSqlDriver(dialectByConnection(conn));
        }
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

    public static String dialectByConnection(Connection aConnection) throws SQLException {
        String dialect = SQLUtils.dialectByUrl(aConnection.getMetaData().getURL());
        if (dialect == null) {
            dialect = SQLUtils.dialectByProductName(aConnection.getMetaData().getDatabaseProductName());
        }
        return dialect;
    }

    public static String schemaByConnection(Connection aConnection) throws SQLException {
        String dialect = dialectByConnection(aConnection);
        SqlDriver driver = SQLUtils.getSqlDriver(dialect);
        if (driver != null) {
            String getSchemaClause = driver.getSql4GetConnectionContext();
            try (Statement stmt = aConnection.createStatement()) {
                try (ResultSet rs = stmt.executeQuery(getSchemaClause)) {
                    if (rs.next() && rs.getMetaData().getColumnCount() > 0) {
                        return rs.getString(1);
                    }
                }
            }
        } else {
            Logger.getLogger(GeneralResourceProvider.class.getName()).log(Level.SEVERE, String.format("Can't obtain sql driver for %s", aConnection.toString()));
        }
        return null;
    }

    public static void initApplication(DataSource aSource) throws Exception {
        try (Connection lconn = aSource.getConnection()) {
            lconn.setAutoCommit(false);
            String dialect = dialectByConnection(lconn);
            SqlDriver driver = SQLUtils.getSqlDriver(dialect);
            driver.initializeApplication(lconn);
        }
    }

    public void initUsersSpace(String aDatasourceName) throws Exception {
        if (aDatasourceName == null) {
            aDatasourceName = defaultDatasourceName;
        }
        initUsersSpace(obtainDataSource(aDatasourceName));
    }

    public static void initUsersSpace(DataSource aSource) throws Exception {
        try (Connection lconn = aSource.getConnection()) {
            lconn.setAutoCommit(false);
            String dialect = dialectByConnection(lconn);
            SqlDriver driver = SQLUtils.getSqlDriver(dialect);
            driver.initializeUsersSpace(lconn);
        }
    }

    public static void initVersioning(DataSource aSource) throws Exception {
        try (Connection lconn = aSource.getConnection()) {
            lconn.setAutoCommit(false);
            String dialect = dialectByConnection(lconn);
            SqlDriver driver = SQLUtils.getSqlDriver(dialect);
            driver.initializeVersion(lconn);
        }
    }
}
