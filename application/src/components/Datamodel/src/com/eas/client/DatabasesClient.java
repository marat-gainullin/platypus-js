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
import com.bearsoft.rowset.dataflow.FlowProvider;
import com.bearsoft.rowset.exceptions.ResourceUnavalableException;
import com.bearsoft.rowset.jdbc.JdbcReader;
import com.bearsoft.rowset.jdbc.StatementsGenerator;
import com.bearsoft.rowset.jdbc.StatementsGenerator.StatementsLogEntry;
import com.bearsoft.rowset.metadata.*;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.queries.ContextHost;
import com.eas.client.queries.QueriesProxy;
import com.eas.client.resourcepool.GeneralResourceProvider;
import com.eas.client.sqldrivers.SqlDriver;
import com.eas.concurrent.CallableConsumer;
import com.eas.concurrent.DeamonThreadFactory;
import com.eas.script.ScriptUtils;
import com.eas.util.StringUtils;
import java.sql.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
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
public class DatabasesClient {

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
    // callback interface for context
    protected ContextHost contextHost;
    // datasource name used by default. E.g. in queries with null datasource name
    protected String defaultDatasourceName;
    protected QueriesProxy<SqlQuery> queries;
    protected ExecutorService jdbcProcessor = Executors.newCachedThreadPool(new DeamonThreadFactory("jdbc-"));

    /**
     *
     * @param aDefaultDatasourceName
     * @param aAutoFillMetadata If true, metadatacache will be filled with
     * tables, keys and other metadata in schema automatically. Otherwise it
     * will query metadata table by table in each case. Default is true.
     * @throws Exception
     */
    public DatabasesClient(String aDefaultDatasourceName, boolean aAutoFillMetadata) throws Exception {
        super();
        defaultDatasourceName = aDefaultDatasourceName;
        autoFillMetadata = aAutoFillMetadata;
    }

    public QueriesProxy<SqlQuery> getQueries() {
        return queries;
    }

    public void setQueries(QueriesProxy<SqlQuery> aQueries) {
        queries = aQueries;
    }

    public String getDefaultDatasourceName() {
        return defaultDatasourceName;
    }

    public void setDefaultDatasourceName(String aValue) throws Exception {
        setDefaultDatasourceName(aValue, true);
    }

    public void setDefaultDatasourceName(String aValue, boolean fireEvents) throws Exception {
        if (defaultDatasourceName == null ? aValue != null : !defaultDatasourceName.equals(aValue)) {
            String oldDefaultDatasourceName = defaultDatasourceName;
            DatabaseMdCache oldMdCache = mdCaches.remove(null);
            DatabaseMdCache newMdCache = mdCaches.remove(aValue);
            defaultDatasourceName = aValue;
            if (oldMdCache != null) {
                mdCaches.put(oldDefaultDatasourceName, oldMdCache);
            }
            if (newMdCache != null) {
                mdCaches.put(null, newMdCache);
            }
        }
    }

    public boolean isAutoFillMetadata() {
        return autoFillMetadata;
    }

    public DataSource obtainDataSource(String aDataSourceName) throws Exception {
        if (aDataSourceName == null) {
            aDataSourceName = defaultDatasourceName;
        }
        if (aDataSourceName != null) {
            Context initContext = new InitialContext();
            try {
                // J2EE servers
                return (DataSource) initContext.lookup(aDataSourceName);
            } catch (NamingException ex) {
                try {
                    // Apache Tomcat component's JNDI context 
                    Context envContext = (Context) initContext.lookup("java:/comp/env"); //NOI18N
                    return (DataSource) envContext.lookup(aDataSourceName);
                } catch (NamingException ex1) {
                    // Platypus standalone server or client
                    return GeneralResourceProvider.getInstance().getPooledDataSource(aDataSourceName);
                }
            }
        } else {
            throw new NamingException("Null datasource name is not allowed (Default datasource name is null also).");
        }
    }

    /**
     * @param aContextHost <code>ConnectionPrepender</code> instance to be used
     * in flow providers, created by the client.
     */
    public void setContextHost(ContextHost aContextHost) {
        contextHost = aContextHost;
    }

    /**
     * Factory method for DatabaseFlowProvider. Intended to incapsulate flow
     * provider creation in two tier or three tier applications.
     *
     * @param aDatasourceName
     * @param aEntityName
     * @param aSqlClause
     * @param aExpectedFields
     * @return FlowProvider created.
     * @throws Exception
     */
    public FlowProvider createFlowProvider(String aDatasourceName, String aEntityName, String aSqlClause, Fields aExpectedFields) throws Exception {
        return new PlatypusJdbcFlowProvider(this, aDatasourceName, aEntityName, obtainDataSource(aDatasourceName), (Runnable aTask) -> {
            startJdbcTask(aTask);
        }, getDbMetadataCache(aDatasourceName), aSqlClause, aExpectedFields, contextHost);
    }

    public String getSqlLogMessage(SqlCompiledQuery query) {
        StringBuilder sb = new StringBuilder("Executing SQL query: ");
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

    public static Map<String, String> getUserProperties(DatabasesClient aClient, String aUserName, Consumer<Map<String, String>> onSuccess, Consumer<Exception> onFailure) throws Exception {
        final SqlQuery q = new SqlQuery(aClient, USER_QUERY_TEXT);
        q.putParameter(USERNAME_PARAMETER_NAME, DataTypeInfo.VARCHAR, aUserName.toUpperCase());
        aClient.initUsersSpace(q.getDbId());
        SqlCompiledQuery compiled = q.compile();
        CallableConsumer<Map<String, String>, Rowset> doWork = (Rowset rs) -> {
            Map<String, String> properties = new HashMap<>();
            if (rs.first()) {
                properties.put(ClientConstants.F_USR_NAME, aUserName);
                properties.put(ClientConstants.F_USR_CONTEXT, rs.getString(rs.getFields().find(ClientConstants.F_USR_CONTEXT)));
                properties.put(ClientConstants.F_USR_EMAIL, rs.getString(rs.getFields().find(ClientConstants.F_USR_EMAIL)));
                properties.put(ClientConstants.F_USR_PHONE, rs.getString(rs.getFields().find(ClientConstants.F_USR_PHONE)));
                properties.put(ClientConstants.F_USR_FORM, rs.getString(rs.getFields().find(ClientConstants.F_USR_FORM)));
                properties.put(ClientConstants.F_USR_PASSWD, rs.getString(rs.getFields().find(ClientConstants.F_USR_PASSWD)));
            }
            return properties;
        };
        if (onSuccess != null) {
            compiled.executeQuery((Rowset rs) -> {
                try {
                    onSuccess.accept(doWork.call(rs));
                } catch (Exception ex) {
                    if (onFailure != null) {
                        onFailure.accept(ex);
                    }
                }
            }, onFailure);
            return null;
        } else {
            final Rowset rs = compiled.executeQuery(null, null);
            return doWork.call(rs);
        }
    }

    private static class UserInfo {

        public Map<String, String> props;
        public Set<String> roles;

        public PlatypusPrincipal principal(String aUserName) {
            if (roles != null && props != null) {
                return new PlatypusPrincipal(aUserName,
                        props.get(ClientConstants.F_USR_CONTEXT),
                        roles);
            } else {
                return null;
            }
        }
    }

    public static PlatypusPrincipal credentialsToPrincipalWithBasicAuthentication(DatabasesClient aClient, String aUserName, String password, Consumer<PlatypusPrincipal> onSuccess, Consumer<Exception> onFailure) throws Exception {
        final UserInfo ui = new UserInfo();
        if (onSuccess != null) {
            getUserProperties(aClient, aUserName, (Map<String, String> userProperties) -> {
                synchronized (ui) {
                    ui.props = userProperties;
                    if (ui.roles != null) {
                        if (password.equals(ui.props.get(ClientConstants.F_USR_PASSWD))) {
                            onSuccess.accept(ui.principal(aUserName));
                        } else {
                            onSuccess.accept(null);
                        }
                    }
                }
            }, onFailure);
            getUserRoles(aClient, aUserName, (Set<String> aRoles) -> {
                synchronized (ui) {
                    ui.roles = aRoles;
                    if (ui.props != null) {
                        if (password.equals(ui.props.get(ClientConstants.F_USR_PASSWD))) {
                            onSuccess.accept(ui.principal(aUserName));
                        } else {
                            onSuccess.accept(null);
                        }
                    }
                }
            }, onFailure);
            return null;
        } else {
            ui.props = getUserProperties(aClient, aUserName, null, null);
            ui.roles = getUserRoles(aClient, aUserName, null, null);
            if (password.equals(ui.props.get(ClientConstants.F_USR_PASSWD))) {
                return ui.principal(aUserName);
            } else {
                return null;
            }
        }
    }

    public static PlatypusPrincipal userNameToPrincipal(DatabasesClient aClient, String aUserName, Consumer<PlatypusPrincipal> onSuccess, Consumer<Exception> onFailure) throws Exception {
        final UserInfo ui = new UserInfo();
        if (onSuccess != null) {
            getUserProperties(aClient, aUserName, (Map<String, String> userProperties) -> {
                synchronized (ui) {
                    ui.props = userProperties;
                    if (ui.roles != null) {
                        onSuccess.accept(ui.principal(aUserName));
                    }
                }
            }, onFailure);
            getUserRoles(aClient, aUserName, (Set<String> aRoles) -> {
                synchronized (ui) {
                    ui.roles = aRoles;
                    if (ui.props != null) {
                        onSuccess.accept(ui.principal(aUserName));
                    }
                }
            }, onFailure);
            return null;
        } else {
            ui.props = getUserProperties(aClient, aUserName, null, null);
            ui.roles = getUserRoles(aClient, aUserName, null, null);
            return ui.principal(aUserName);
        }
    }

    public int executeUpdate(SqlCompiledQuery aQuery, Consumer<Integer> onSuccess, Consumer<Exception> onFailure) throws Exception {
        Callable<Integer> doWork = () -> {
            int rowsAffected = 0;
            Converter converter = getDbMetadataCache(aQuery.getDatabaseId()).getConnectionDriver().getConverter();
            DataSource dataSource = obtainDataSource(aQuery.getDatabaseId());
            if (dataSource != null) {
                try (Connection connection = dataSource.getConnection(); PreparedStatement stmt = connection.prepareStatement(aQuery.getSqlClause())) {
                    connection.setAutoCommit(false);
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
        };
        if (onSuccess != null) {
            startJdbcTask(() -> {
                try {
                    Integer affected = doWork.call();
                    final Object lock = ScriptUtils.getLock() != null ? ScriptUtils.getLock() : this;
                    synchronized (lock) {
                        onSuccess.accept(affected);
                    }
                } catch (Exception ex) {
                    if (onFailure != null) {
                        final Object lock = ScriptUtils.getLock() != null ? ScriptUtils.getLock() : this;
                        synchronized (lock) {
                            onFailure.accept(ex);
                        }
                    }
                }
            });
            return 0;
        } else {
            return doWork.call();
        }
    }

    public synchronized DatabaseMdCache getDbMetadataCache(String aDatasourceName) throws Exception {
        if (!mdCaches.containsKey(aDatasourceName)) {
            DatabaseMdCache cache = new DatabaseMdCache(this, aDatasourceName);
            mdCaches.put(aDatasourceName, cache);
            if (autoFillMetadata) {
                try {
                    cache.fillTablesCacheByConnectionSchema(true);
                } catch (ResourceUnavalableException ex) {
                    Logger.getLogger(DatabasesClient.class.getName()).log(Level.WARNING, ex.getMessage());
                }
            }
        }
        return mdCaches.get(aDatasourceName);
    }

    protected void startJdbcTask(Runnable aTask) {
        PlatypusPrincipal closurePrincipal = PlatypusPrincipal.getInstance();
        Object closureRequest = ScriptUtils.getRequest();
        Object closureResponse = ScriptUtils.getResponse();
        Object closureLock = ScriptUtils.getLock();
        jdbcProcessor.submit(() -> {
            ScriptUtils.setLock(closureLock);
            ScriptUtils.setRequest(closureRequest);
            ScriptUtils.setResponse(closureResponse);
            PlatypusPrincipal.setInstance(closurePrincipal);
            try {
                aTask.run();
            } finally {
                ScriptUtils.setLock(null);
                ScriptUtils.setRequest(null);
                ScriptUtils.setResponse(null);
                PlatypusPrincipal.setInstance(null);
            }
        });
    }

    protected static class CommitProcess extends AsyncProcess<Integer, Integer> {

        public int rowsAffected;

        public CommitProcess(int aExpected, Consumer<Integer> aOnSuccess, Consumer<Exception> aOnFailure) {
            super(aExpected, aOnSuccess, aOnFailure);
        }

        @Override
        public synchronized void complete(Integer aRowsAffected, Exception aFailureCause) {
            rowsAffected += aRowsAffected != null ? aRowsAffected : 0;
            if (aFailureCause != null) {
                exceptions.add(aFailureCause);
            }
            if (++completed == expected) {
                doComplete(rowsAffected);
            }
        }
    }

    protected static class ApplyProcess extends AsyncProcess<ApplyResult, List<ApplyResult>> {

        public List<ApplyResult> results = new ArrayList<>();

        public ApplyProcess(int aExpected, Consumer<List<ApplyResult>> aOnSuccess, Consumer<Exception> aOnFailure) {
            super(aExpected, aOnSuccess, aOnFailure);
        }

        /**
         *
         * @param aApplyResult
         * @param aFailureCause
         */
        @Override
        public synchronized void complete(ApplyResult aApplyResult, Exception aFailureCause) {
            if (aApplyResult != null) {
                results.add(aApplyResult);
            }
            if (aFailureCause != null) {
                exceptions.add(aFailureCause);
            }
            if (++completed == expected) {
                doComplete(results);
            }
        }
    }

    public int commit(Map<String, List<Change>> aDatasourcesChangeLogs, Consumer<Integer> onSuccess, Consumer<Exception> onFailure) throws Exception {
        if (onSuccess != null) {
            if (!aDatasourcesChangeLogs.isEmpty()) {
                ApplyProcess applyProcess = new ApplyProcess(aDatasourcesChangeLogs.size(), (List<ApplyResult> aApplyResults) -> {
                    assert aDatasourcesChangeLogs.size() == aApplyResults.size();
                    CommitProcess commitProcess = new CommitProcess(aApplyResults.size(), (Integer aRowsAffected) -> {
                        final Object lock = ScriptUtils.getLock() != null ? ScriptUtils.getLock() : this;
                        synchronized (lock) {
                            onSuccess.accept(aRowsAffected);
                        }

                    }, (Exception aFailureCause) -> {
                        if (onFailure != null) {
                            final Object lock = ScriptUtils.getLock() != null ? ScriptUtils.getLock() : this;
                            synchronized (lock) {
                                onFailure.accept(aFailureCause);
                            }
                        }
                    });
                    aApplyResults.stream().forEach((ApplyResult aResult) -> {
                        startJdbcTask(() -> {
                            try {
                                try {
                                    aResult.connection.commit();
                                    commitProcess.complete(aResult.rowsAffected, null);
                                } catch (SQLException ex) {
                                    aResult.connection.rollback();
                                    commitProcess.complete(null, ex);
                                } finally {
                                    aResult.connection.close();
                                }
                            } catch (Exception ex) {
                                Logger.getLogger(CommitProcess.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        });
                    });
                }, null);
                applyProcess.onFailure = (Exception aFailureCause) -> {
                    applyProcess.results.stream().forEach((ApplyResult aResult) -> {
                        try {
                            try {
                                aResult.connection.rollback();
                            } finally {
                                aResult.connection.close();
                            }
                        } catch (SQLException ex1) {
                            Logger.getLogger(DatabasesClient.class.getName()).log(Level.SEVERE, null, ex1);
                        }
                    });
                    if (onFailure != null) {
                        onFailure.accept(aFailureCause);
                    }
                };
                aDatasourcesChangeLogs.entrySet().stream().forEach((Map.Entry<String, List<Change>> aLogEntry) -> {
                    try {
                        apply(aLogEntry.getKey(), aLogEntry.getValue(), (ApplyResult aResult) -> {
                            applyProcess.complete(aResult, null);
                        }, (Exception ex) -> {
                            applyProcess.complete(null, ex);
                        });
                    } catch (Exception ex) {
                        Logger.getLogger(DatabasesClient.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            } else {
                onSuccess.accept(0);
            }
            return 0;
        } else {
            int rowsAffected = 0;
            List<ApplyResult> results = new ArrayList<>();
            try {
                for (Map.Entry<String, List<Change>> logEntry : aDatasourcesChangeLogs.entrySet()) {
                    results.add(apply(logEntry.getKey(), logEntry.getValue(), null, null));
                }
                for (ApplyResult r : results) {
                    r.connection.commit();
                    rowsAffected += r.rowsAffected;
                }
                return rowsAffected;
            } catch (Exception ex) {
                for (ApplyResult r : results) {
                    r.connection.rollback();
                }
                throw ex;
            } finally {
                for (ApplyResult r : results) {
                    r.connection.close();
                }
            }
        }
    }

    protected static class ApplyResult {

        public int rowsAffected;
        public Connection connection;

        public ApplyResult(int rowsAffected, Connection connection) {
            this.rowsAffected = rowsAffected;
            this.connection = connection;
        }
    }

    protected ApplyResult apply(final String aDatasourceName, List<Change> aLog, Consumer<ApplyResult> onSuccess, Consumer<Exception> onFailure) throws Exception {
        Callable<ApplyResult> doWork = () -> {
            int rowsAffected = 0;
            DatabaseMdCache mdCache = getDbMetadataCache(aDatasourceName);
            if (mdCache == null) {
                throw new IllegalStateException(String.format(UNKNOWN_DATASOURCE_IN_COMMIT, aDatasourceName));
            }
            SqlDriver driver = mdCache.getConnectionDriver();
            if (driver == null) {
                throw new IllegalStateException(String.format(UNSUPPORTED_DATASOURCE_IN_COMMIT, aDatasourceName));
            }
            Converter converter = driver.getConverter();
            assert aLog != null;
            DataSource dataSource = obtainDataSource(aDatasourceName);
            Connection connection = dataSource.getConnection();
            try {
                connection.setAutoCommit(false);
                List<StatementsLogEntry> statements = new ArrayList<>();
                // This structure helps us to avoid actuality check for queries while
                // processing each statement in transaction. Thus, we can avoid speed degradation.
                // It doesn't break security, because such "unactual" lookup takes place ONLY
                // while transaction processing.
                final Map<String, SqlQuery> entityQueries = new HashMap<>();
                for (Change change : aLog) {
                    StatementsGenerator generator = new StatementsGenerator(converter, (String aEntityName, String aFieldName) -> {
                        if (aEntityName != null) {
                            SqlQuery query;
                            if (queries != null) {
                                query = entityQueries.get(aEntityName);
                                if (query == null) {
                                    query = queries.getQuery(aEntityName, null, null);
                                    if (query != null) {
                                        entityQueries.put(aEntityName, query);
                                    }
                                }
                            } else {
                                query = null;
                            }
                            Fields fields;
                            if (query != null && query.getEntityId() != null) {
                                fields = query.getFields();
                            } else {// It seems, that aEntityId is a table name...
                                fields = mdCaches.get(aDatasourceName).getTableMetadata(aEntityName);
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
                                Logger.getLogger(DatabasesClient.class.getName()).log(Level.WARNING, "Cant find fields for entity id:{0}", aEntityName);
                                return null;
                            }
                        } else {
                            return null;
                        }
                    }, ClientConstants.F_USR_CONTEXT, contextHost != null ? contextHost.preparationContext() : null);
                    change.accept(generator);
                    statements.addAll(generator.getLogEntries());
                }
                rowsAffected = riddleStatements(statements, connection);
                aLog.clear();
                return new ApplyResult(rowsAffected, connection);
            } catch (Exception ex) {
                connection.rollback();
                connection.close();
                throw ex;
            }
        };
        if (onSuccess != null) {
            startJdbcTask(() -> {
                try {
                    ApplyResult applyResult = doWork.call();
                    try {// We have to handle commit exceptions and onSuccess.accept() exceptions separatly.
                        onSuccess.accept(applyResult);
                    } catch (Exception ex) {
                        Logger.getLogger(DatabasesClient.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (Exception ex) {
                    if (onFailure != null) {
                        onFailure.accept(ex);
                    }
                }
            });
            return null;
        } else {
            return doWork.call();
        }
    }
    protected static final String UNKNOWN_DATASOURCE_IN_COMMIT = "Unknown datasource: %s. Can't commit to it.";
    protected static final String UNSUPPORTED_DATASOURCE_IN_COMMIT = "Unsupported datasource: %s. Can't commit to it.";

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

    public void dbTableChanged(String aDatasourceName, String aSchema, String aTable) throws Exception {
        DatabaseMdCache cache = getDbMetadataCache(aDatasourceName);
        String fullTableName = aTable;
        if (aSchema != null && !aSchema.isEmpty()) {
            fullTableName = aSchema + "." + fullTableName;
        }
        cache.removeTableMetadata(fullTableName);
        cache.removeTableIndexes(fullTableName);
    }

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

    public String getConnectionDialect(String aDatasourceId) throws Exception {
        DataSource ds = obtainDataSource(aDatasourceId);
        try (Connection conn = ds.getConnection()) {
            return dialectByConnection(conn);
        }
    }

    public SqlDriver getConnectionDriver(String aDatasourceId) throws Exception {
        DataSource ds = obtainDataSource(aDatasourceId);
        try (Connection conn = ds.getConnection()) {
            return SQLUtils.getSqlDriver(dialectByConnection(conn));
        }
    }

    protected static Set<String> getUserRoles(DatabasesClient aClient, String aUserName, Consumer<Set<String>> onSuccess, Consumer<Exception> onFailure) throws Exception {
        CallableConsumer<Set<String>, Rowset> doWork = (Rowset rs) -> {
            Set<String> roles = new HashSet<>();
            int roleNameColumnIndex = rs.getFields().find(ClientConstants.F_GROUP_NAME);
            for (int i = 1; i <= rs.size(); i++) {
                roles.add((String) rs.getRow(i).getColumnObject(roleNameColumnIndex));
            }
            return roles;
        };
        final SqlQuery q = new SqlQuery(aClient, USER_GROUPS_QUERY_TEXT);
        q.putParameter(USERNAME_PARAMETER_NAME, DataTypeInfo.VARCHAR, aUserName.toUpperCase());
        SqlCompiledQuery compiled = q.compile();
        if (onSuccess != null) {
            compiled.executeQuery((Rowset rs) -> {
                try {
                    onSuccess.accept(doWork.call(rs));
                } catch (Exception ex) {
                    if (onFailure != null) {
                        onFailure.accept(ex);
                    }
                }
            }, onFailure);
            return null;
        } else {
            Rowset rs = compiled.executeQuery(null, null);
            return doWork.call(rs);
        }
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
