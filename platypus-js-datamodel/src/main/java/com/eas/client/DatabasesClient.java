package com.eas.client;

import com.eas.client.changes.Change;
import com.eas.client.changes.EntitiesHost;
import com.eas.client.dataflow.ColumnsIndicies;
import com.eas.client.dataflow.JdbcFlowProvider;
import com.eas.client.dataflow.StatementsGenerator;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.client.metadata.Parameter;
import com.eas.client.metadata.Parameters;
import com.eas.client.queries.ContextHost;
import com.eas.client.queries.QueriesProxy;
import com.eas.client.resourcepool.GeneralResourceProvider;
import com.eas.client.resourcepool.ResourceUnavalableException;
import com.eas.client.sqldrivers.SqlDriver;
import com.eas.concurrent.CallableConsumer;
import com.eas.concurrent.PlatypusThreadFactory;
import com.eas.script.Scripts;
import com.eas.util.StringUtils;
import java.sql.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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
 */
public class DatabasesClient {

    public static final String BAD_LOGIN_MSG = "Login incorrect";
    private static final String USERNAME_PARAMETER_NAME = "userName";
    private static final String SELECT_BY_FIELD_QUERY = "select * from %s where Upper(%s) = :%s";
    private static final String USER_QUERY_TEXT = String.format(SELECT_BY_FIELD_QUERY, ClientConstants.T_MTD_USERS, ClientConstants.F_USR_NAME, USERNAME_PARAMETER_NAME);
    private static final String USER_GROUPS_QUERY_TEXT = String.format(SELECT_BY_FIELD_QUERY, ClientConstants.T_MTD_GROUPS, ClientConstants.F_USR_NAME, USERNAME_PARAMETER_NAME);
    public static final String TYPES_INFO_TRACE_MSG = "Getting types info. DatasourceName %s";
    public static final String USER_MISSING_MSG = "No user found (%s)";
    // metadata
    protected Map<String, MetadataCache> mdCaches = new ConcurrentHashMap<>();
    protected boolean autoFillMetadata = true;
    // callback interface for context
    protected ContextHost contextHost;
    // datasource name used by default. E.g. in queries with null datasource name
    protected String defaultDatasourceName;
    protected QueriesProxy<SqlQuery> queries;
    protected final ThreadPoolExecutor jdbcProcessor;

    /**
     *
     * @param aDefaultDatasourceName
     * @param aAutoFillMetadata If true, metadatacache will be filled with
     * tables, keys and other metadata in schema automatically. Otherwise it
     * will query metadata table by table in each case. Default is true.
     * @param aMaxJdbcThreads
     * @throws Exception
     */
    public DatabasesClient(String aDefaultDatasourceName, boolean aAutoFillMetadata, int aMaxJdbcThreads) throws Exception {
        super();
        jdbcProcessor = new ThreadPoolExecutor(aMaxJdbcThreads, aMaxJdbcThreads,
                3L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new PlatypusThreadFactory("jdbc-", false));
        jdbcProcessor.allowCoreThreadTimeOut(true);
        defaultDatasourceName = aDefaultDatasourceName;
        autoFillMetadata = aAutoFillMetadata;
    }

    public void shutdown() throws InterruptedException {
        jdbcProcessor.shutdown();
        jdbcProcessor.awaitTermination(30L, TimeUnit.SECONDS);
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
            defaultDatasourceName = aValue;
        }
    }

    public boolean isAutoFillMetadata() {
        return autoFillMetadata;
    }

    public DataSource obtainDataSource(String aDataSourceName) throws Exception {
        if (aDataSourceName == null) {
            aDataSourceName = defaultDatasourceName;
        }
        if (aDataSourceName != null && !aDataSourceName.isEmpty()) {
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
            throw new NamingException("Datasource name missing.");
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
    public PlatypusJdbcFlowProvider createFlowProvider(String aDatasourceName, String aEntityName, String aSqlClause, Fields aExpectedFields) throws Exception {
        return new PlatypusJdbcFlowProvider(this, aDatasourceName, aEntityName, obtainDataSource(aDatasourceName), (Runnable aTask) -> {
            startJdbcTask(aTask);
        }, getMetadataCache(aDatasourceName), aSqlClause, aExpectedFields, contextHost);
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

    public static Map<String, String> getUserProperties(DatabasesClient aClient, String aUserName, Scripts.Space aSpace, Consumer<Map<String, String>> onSuccess, Consumer<Exception> onFailure) throws Exception {
        if (aUserName != null && aClient != null) {
            final SqlQuery q = new SqlQuery(aClient, USER_QUERY_TEXT);
            q.putParameter(USERNAME_PARAMETER_NAME, Scripts.STRING_TYPE_NAME, aUserName.toUpperCase());
            SqlCompiledQuery compiled = q.compile();
            CallableConsumer<Map<String, String>, ResultSet> doWork = (ResultSet r) -> {
                Map<String, String> properties = new HashMap<>();
                ColumnsIndicies idxs = new ColumnsIndicies(r.getMetaData());
                if (r.next()) {
                    properties.put(ClientConstants.F_USR_NAME, aUserName);
                    properties.put(ClientConstants.F_USR_CONTEXT, r.getString(idxs.find(ClientConstants.F_USR_CONTEXT)));
                    properties.put(ClientConstants.F_USR_EMAIL, r.getString(idxs.find(ClientConstants.F_USR_EMAIL)));
                    properties.put(ClientConstants.F_USR_PHONE, r.getString(idxs.find(ClientConstants.F_USR_PHONE)));
                    properties.put(ClientConstants.F_USR_FORM, r.getString(idxs.find(ClientConstants.F_USR_FORM)));
                    properties.put(ClientConstants.F_USR_PASSWD, r.getString(idxs.find(ClientConstants.F_USR_PASSWD)));
                }
                return properties;
            };
            if (onSuccess != null) {
                try {
                    compiled.<Map<String, String>>executeQuery(doWork, (Runnable aTask) -> {
                        aSpace.process(aTask);
                    }, onSuccess, onFailure);
                    return null;
                } catch (Exception ex) {
                    aSpace.process(() -> {
                        onFailure.accept(ex);
                    });
                    return null;
                }
            } else {
                return compiled.<Map<String, String>>executeQuery(doWork, null, null, null);
            }
        } else if (onSuccess != null) {
            aSpace.process(() -> {
                onSuccess.accept(new HashMap<>());
            });
            return null;
        } else {
            return null;
        }
    }

    private static class UserInfo {

        protected boolean strict = true;
        protected String userName;
        protected String password;
        protected Map<String, String> props;
        protected Set<String> roles;
        protected Consumer<PlatypusPrincipal> onSuccess;

        public UserInfo(String aUserName, String aPassword, Consumer<PlatypusPrincipal> aOnSuccess, boolean aStrict) {
            super();
            userName = aUserName;
            password = aPassword;
            onSuccess = aOnSuccess;
            strict = aStrict;
        }

        public PlatypusPrincipal principal(String aUserName) {
            if (roles != null && props != null) {
                return new PlatypusPrincipal(aUserName,
                        props.get(ClientConstants.F_USR_CONTEXT),
                        roles, null);
            } else {
                return null;
            }
        }

        public void complete(Map<String, String> aProps, Set<String> aRoles) {
            if (aProps != null) {
                props = aProps;
            }
            if (aRoles != null) {
                roles = aRoles;
            }
            if (props != null && roles != null) {
                if (!strict || password.equals(props.get(ClientConstants.F_USR_PASSWD))) {
                    onSuccess.accept(principal(userName));
                } else {
                    onSuccess.accept(null);
                }
            }
        }
    }

    public static PlatypusPrincipal credentialsToPrincipalWithBasicAuthentication(DatabasesClient aClient, String aUserName, String aPassword, Scripts.Space aSpace, Consumer<PlatypusPrincipal> aOnSuccess, Consumer<Exception> aOnFailure) throws Exception {
        final UserInfo ui = new UserInfo(aUserName, aPassword, aOnSuccess, true);
        if (aOnSuccess != null) {
            getUserProperties(aClient, aUserName, aSpace, (Map<String, String> userProperties) -> {
                ui.complete(userProperties, null);
            }, aOnFailure);
            getUserRoles(aClient, aUserName, aSpace, (Set<String> aRoles) -> {
                ui.complete(null, aRoles);
            }, aOnFailure);
            return null;
        } else {
            ui.props = getUserProperties(aClient, aUserName, null, null, null);
            ui.roles = getUserRoles(aClient, aUserName, null, null, null);
            if (aPassword.equals(ui.props.get(ClientConstants.F_USR_PASSWD))) {
                return ui.principal(aUserName);
            } else {
                return null;
            }
        }
    }

    public static PlatypusPrincipal userNameToPrincipal(DatabasesClient aClient, String aUserName, Scripts.Space aSpace, Consumer<PlatypusPrincipal> aOnSuccess, Consumer<Exception> aOnFailure) throws Exception {
        final UserInfo ui = new UserInfo(aUserName, null, aOnSuccess, false);
        if (aOnSuccess != null) {
            getUserProperties(aClient, aUserName, aSpace, (Map<String, String> userProperties) -> {
                ui.complete(userProperties, null);
            }, aOnFailure);
            getUserRoles(aClient, aUserName, aSpace, (Set<String> aRoles) -> {
                ui.complete(null, aRoles);
            }, aOnFailure);
            return null;
        } else {
            ui.props = getUserProperties(aClient, aUserName, null, null, null);
            ui.roles = getUserRoles(aClient, aUserName, null, null, null);
            return ui.principal(aUserName);
        }
    }

    public int executeUpdate(SqlCompiledQuery aQuery, Consumer<Integer> onSuccess, Consumer<Exception> onFailure) throws Exception {
        Callable<Integer> doWork = () -> {
            int rowsAffected = 0;
            DataSource dataSource = obtainDataSource(aQuery.getDatasourceName());
            if (dataSource != null) {
                try (Connection connection = dataSource.getConnection()) {
                    boolean autoCommit = connection.getAutoCommit();
                    try {
                        connection.setAutoCommit(false);
                        try (PreparedStatement stmt = connection.prepareStatement(aQuery.getSqlClause())) {
                            Parameters params = aQuery.getParameters();
                            for (int i = 1; i <= params.getParametersCount(); i++) {
                                Parameter param = params.get(i);
                                int jdbcType = JdbcFlowProvider.calcJdbcType(param.getType(), param.getValue());
                                JdbcFlowProvider.assign(param.getValue(), i, stmt, jdbcType, null);
                            }
                            try {
                                rowsAffected += stmt.executeUpdate();
                                connection.commit();
                            } catch (SQLException ex) {
                                connection.rollback();
                                throw ex;
                            }
                        }
                    } finally {
                        connection.setAutoCommit(autoCommit);
                    }
                }
            }
            return rowsAffected;
        };
        if (onSuccess != null) {
            Scripts.Space space = Scripts.getSpace();
            startJdbcTask(() -> {
                try {
                    Integer affected = doWork.call();
                    space.process(() -> {
                        onSuccess.accept(affected);
                    });
                } catch (Exception ex) {
                    if (onFailure != null) {
                        space.process(() -> {
                            onFailure.accept(ex);
                        });
                    }
                }
            });
            return 0;
        } else {
            return doWork.call();
        }
    }

    public MetadataCache getMetadataCache(String aDatasourceName) throws Exception {
        if (aDatasourceName == null) {
            aDatasourceName = defaultDatasourceName;
        }
        if (aDatasourceName != null) {
            if (!mdCaches.containsKey(aDatasourceName)) {
                MetadataCache cache = new MetadataCache(this, aDatasourceName);
                if (autoFillMetadata) {
                    try {
                        cache.fillTablesCacheByConnectionSchema();
                    } catch (ResourceUnavalableException ex) {
                        Logger.getLogger(DatabasesClient.class.getName()).log(Level.WARNING, ex.getMessage());
                    }
                }
                mdCaches.put(aDatasourceName, cache);
            }
            return mdCaches.get(aDatasourceName);
        } else {
            return null;
        }
    }

    private void startJdbcTask(Runnable aTask) {
        Scripts.LocalContext context = Scripts.getContext();
        if (context != null) {
            context.incAsyncsCount();
        }
        jdbcProcessor.submit(() -> {
            Scripts.setContext(context);
            try {
                aTask.run();
            } finally {
                Scripts.setContext(null);
            }
        });
    }

    protected static class CommitProcess extends AsyncProcess<Integer, Integer> {

        public int rowsAffected;

        public CommitProcess(int aExpected, Consumer<Integer> aOnSuccess, Consumer<Exception> aOnFailure) {
            super(aExpected, aOnSuccess, aOnFailure);
        }

        @Override
        public void complete(Integer aRowsAffected, Exception aFailureCause) {
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

        public final List<ApplyResult> results = new ArrayList<>();

        public ApplyProcess(int aExpected, Consumer<List<ApplyResult>> aOnSuccess, Consumer<Exception> aOnFailure) {
            super(aExpected, aOnSuccess, aOnFailure);
        }

        /**
         *
         * @param aApplyResult
         * @param aFailureCause
         */
        @Override
        public void complete(ApplyResult aApplyResult, Exception aFailureCause) {
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

    public int commit(Map<String, List<Change>> aChangeLogs, Consumer<Integer> onSuccess, Consumer<Exception> onFailure) throws Exception {
        Callable<Integer> doWork = () -> {
            int rowsAffected = 0;
            List<ApplyResult> results = new ArrayList<>(aChangeLogs.size());
            try {
                for (Map.Entry<String, List<Change>> logEntry : aChangeLogs.entrySet()) {
                    results.add(apply(logEntry.getKey(), logEntry.getValue()));
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
                    r.connection.setAutoCommit(r.autoCommit);
                    r.connection.close();
                }
            }
        };
        if (onSuccess != null) {
            Scripts.Space space = Scripts.getSpace();
            startJdbcTask(() -> {
                try {
                    int affected = doWork.call();
                    space.process(() -> {
                        onSuccess.accept(affected);
                    });
                } catch (Exception ex) {
                    if (onFailure != null) {
                        space.process(() -> {
                            onFailure.accept(ex);
                        });
                    }
                }
            });
            return 0;
        } else {
            return doWork.call();
        }
    }

    protected static class ApplyResult {

        public final int rowsAffected;
        public final Connection connection;
        public final boolean autoCommit;

        public ApplyResult(int aRowsAffected, Connection aConnection, boolean aAutoCommit) {
            super();
            rowsAffected = aRowsAffected;
            connection = aConnection;
            autoCommit = aAutoCommit;
        }
    }

    protected ApplyResult apply(final String aDatasourceName, List<Change> aLog) throws Exception {
        int rowsAffected;
        MetadataCache mdCache = getMetadataCache(aDatasourceName);
        if (mdCache == null) {
            throw new IllegalStateException(String.format(UNKNOWN_DATASOURCE_IN_COMMIT, aDatasourceName));
        }
        SqlDriver driver = mdCache.getDatasourceSqlDriver();
        if (driver == null) {
            throw new IllegalStateException(String.format(UNSUPPORTED_DATASOURCE_IN_COMMIT, aDatasourceName));
        }
        assert aLog != null;
        DataSource dataSource = obtainDataSource(aDatasourceName);
        Connection connection = dataSource.getConnection();
        boolean autoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        try {
            List<StatementsGenerator.StatementsLogEntry> statements = new ArrayList<>();
            // This structure helps us to avoid actuality check for queries while
            // processing each statement in transaction. Thus, we can avoid speed degradation.
            // It doesn't break security, because such "unactual" lookup takes place ONLY
            // while transaction processing.
            final Map<String, SqlQuery> entityQueries = new HashMap<>();
            for (Change change : aLog) {
                StatementsGenerator generator = new StatementsGenerator(new EntitiesHost() {

                    @Override
                    public Parameter resolveParameter(String aEntityName, String aParamName) throws Exception {
                        if (aEntityName != null) {
                            SqlQuery query = query(aEntityName);
                            if (query != null && query.getEntityName() != null) {
                                Parameter p = query.getParameters().get(aParamName);
                                return p;
                            } else {
                                return null;
                            }
                        } else {
                            return null;
                        }
                    }

                    @Override
                    public Field resolveField(String aEntityName, String aFieldName) throws Exception {
                        if (aEntityName != null) {
                            SqlQuery query = query(aEntityName);
                            Fields fields;
                            if (query != null && query.getEntityName() != null) {
                                fields = query.getFields();
                            } else {// It seems, that aEntityName is a table name...
                                fields = mdCache.getTableMetadata(aEntityName);
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
                    }

                    private SqlQuery query(String aEntityName) throws Exception {
                        SqlQuery query;
                        if (queries != null) {
                            query = entityQueries.get(aEntityName);
                            if (query == null) {
                                query = queries.getQuery(aEntityName, null, null, null);
                                if (query != null) {
                                    entityQueries.put(aEntityName, query);
                                }
                            }
                        } else {
                            query = null;
                        }
                        return query;
                    }
                }, ClientConstants.F_USR_CONTEXT, contextHost != null ? contextHost.preparationContext() : null, mdCache, driver);
                change.accept(generator);
                statements.addAll(generator.getLogEntries());
            }
            rowsAffected = riddleStatements(statements, connection);
            return new ApplyResult(rowsAffected, connection, autoCommit);
        } catch (Exception ex) {
            connection.rollback();
            connection.setAutoCommit(autoCommit);
            connection.close();
            throw ex;
        }
    }
    protected static final String UNKNOWN_DATASOURCE_IN_COMMIT = "Unknown datasource: %s. Can't commit to it.";
    protected static final String UNSUPPORTED_DATASOURCE_IN_COMMIT = "Unsupported datasource: %s. Can't commit to it.";

    public void tableChanged(String aDatasourceName, String aTable) throws Exception {
        MetadataCache cache = getMetadataCache(aDatasourceName);
        cache.refreshTableMetadata(aTable);
    }

    public void tableRemoved(String aDatasourceName, String aTable) throws Exception {
        MetadataCache cache = getMetadataCache(aDatasourceName);
        cache.removeTableMetadata(aTable);
    }

    public void tableAdded(String aDatasourceName, String aTable) throws Exception {
        MetadataCache cache = getMetadataCache(aDatasourceName);
        cache.refreshTableMetadata(aTable);
    }

    public String getConnectionSchema(String aDatasourceName) throws Exception {
        DataSource ds = obtainDataSource(aDatasourceName);
        if (ds != null) {
            try (Connection conn = ds.getConnection()) {
                return schemaByConnection(conn);
            }
        } else {
            return null;
        }
    }

    public String getConnectionDialect(String aDatasourceName) throws Exception {
        DataSource ds = obtainDataSource(aDatasourceName);
        try (Connection conn = ds.getConnection()) {
            return dialectByConnection(conn);
        }
    }

    public SqlDriver getConnectionDriver(String aDatasourceName) throws Exception {
        DataSource ds = obtainDataSource(aDatasourceName);
        try (Connection conn = ds.getConnection()) {
            return SQLUtils.getSqlDriver(dialectByConnection(conn));
        }
    }

    protected static Set<String> getUserRoles(DatabasesClient aClient, String aUserName, Scripts.Space aSpace, Consumer<Set<String>> onSuccess, Consumer<Exception> onFailure) throws Exception {
        CallableConsumer<Set<String>, ResultSet> doWork = (ResultSet rs) -> {
            Set<String> roles = new HashSet<>();
            ColumnsIndicies idxs = new ColumnsIndicies(rs.getMetaData());
            int roleNameColumnIndex = idxs.find(ClientConstants.F_GROUP_NAME);
            while (rs.next()) {
                roles.add(rs.getString(roleNameColumnIndex));
            }
            return roles;
        };
        final SqlQuery q = new SqlQuery(aClient, USER_GROUPS_QUERY_TEXT);
        q.putParameter(USERNAME_PARAMETER_NAME, Scripts.STRING_TYPE_NAME, aUserName.toUpperCase());
        SqlCompiledQuery compiled = q.compile();
        if (onSuccess != null) {
            compiled.<Set<String>>executeQuery(doWork, (Runnable aTask) -> {
                aSpace.process(aTask);
            }, (Set<String> aRoles) -> {
                onSuccess.accept(aRoles);
            }, onFailure);
            return null;
        } else {
            return compiled.<Set<String>>executeQuery(doWork, null, null, null);
        }
    }

    private int riddleStatements(List<StatementsGenerator.StatementsLogEntry> aStatements, Connection aConnection) throws Exception {
        int rowsAffected = 0;
        if (!aStatements.isEmpty()) {
            List<StatementsGenerator.StatementsLogEntry> errorStatements = new ArrayList<>();
            List<String> errors = new ArrayList<>();
            for (StatementsGenerator.StatementsLogEntry entry : aStatements) {
                try {
                    rowsAffected += entry.apply(aConnection);
                } catch (Exception ex) {
                    errorStatements.add(entry);
                    errors.add(ex.getMessage());
                    Logger.getLogger(DatabasesClient.class.getName()).log(Level.WARNING, ex.getMessage());
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
            if (getSchemaClause != null) {
                try (Statement stmt = aConnection.createStatement()) {
                    try (ResultSet rs = stmt.executeQuery(getSchemaClause)) {
                        if (rs.next() && rs.getMetaData().getColumnCount() > 0) {
                            return rs.getString(1);
                        }
                    }
                }
            } else {
                return null;
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
            boolean autoCommit = lconn.getAutoCommit();
            try {
                lconn.setAutoCommit(false);
                String dialect = dialectByConnection(lconn);
                SqlDriver driver = SQLUtils.getSqlDriver(dialect);
                driver.initializeUsersSpace(lconn);
            } finally {
                lconn.setAutoCommit(autoCommit);
            }
        }
    }

    public static void initVersioning(DataSource aSource) throws Exception {
        try (Connection lconn = aSource.getConnection()) {
            boolean autoCommit = lconn.getAutoCommit();
            try {
                lconn.setAutoCommit(false);
                String dialect = dialectByConnection(lconn);
                SqlDriver driver = SQLUtils.getSqlDriver(dialect);
                driver.initializeVersion(lconn);
            } finally {
                lconn.setAutoCommit(autoCommit);
            }
        }
    }
}
