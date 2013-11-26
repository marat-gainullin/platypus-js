/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.resourcepool;

import com.bearsoft.rowset.exceptions.ResourceUnavalableException;
import com.eas.client.AppCache;
import com.eas.client.Client;
import com.eas.client.ClientConstants;
import com.eas.client.SQLUtils;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.settings.DbConnectionSettings;
import com.eas.client.sqldrivers.SqlDriver;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 *
 * @author mg
 */
public class GeneralResourceProvider {

    public static final String BAD_APPLICATION_RESOURCE_POOL_MSG = "Application database resource pool hasn't been initialized! It has to be initialized while client creation !ONLY!.";
    private Client client;
    /* db connections pools
     * <Long, >  - is connection pool id. It's the same as a connection descriptor db id.
     * "null" - is the metabase connections pool. It's connection descriptor is in the server settings
     */
    private Map<String, DataSource> connectionPools = new HashMap<>();
    private Map<String, DbConnectionSettings> connectionPoolsSettings = new HashMap<>();

    public GeneralResourceProvider(DbConnectionSettings aSettings, Client aClient) throws Exception {
        super();
        client = aClient;
        DataSource lmdSource = constructDataSource(aSettings);
        testDataSource(lmdSource, aSettings);
        if (aSettings.isInitSchema()) {
            initApplicationSchema(lmdSource);
        }
        connectionPools.put(null, lmdSource);
        connectionPoolsSettings.put(null, aSettings);
    }

    private DataSource constructDataSource(DbConnectionSettings aSettings) throws Exception {
        try {
            Context initContext = new InitialContext();
            DataSource ds;
            try {
                // J2EE servers
                ds = (DataSource) initContext.lookup(aSettings.getUrl());
            } catch (javax.naming.NamingException ex) {
                // Apache Tomcat component's JNDI context 
                Context envContext = (Context) initContext.lookup("java:/comp/env"); //NOI18N
                ds = (DataSource) envContext.lookup(aSettings.getUrl());
            }
            return ds;
        } catch (Exception ex) {
            return new PlatypusNativeDataSource(aSettings.getMaxConnections(), aSettings.getMaxStatements(), aSettings.getResourceTimeout(), aSettings.getUrl(), aSettings.getUser(), aSettings.getPassword());
        }
    }

    /**
     * This constructor is intended for descendants with custom functioning and
     * without (may be) connection pools.
     *
     * @throws Exception
     */
    GeneralResourceProvider() throws Exception {
        super();
    }

    public synchronized DataSource getPooledDataSource(String aDbId) throws ResourceUnavalableException {
        try {
            DataSource dbPool = connectionPools.get(aDbId);
            if (dbPool == null) {
                if (aDbId != null) {
                    dbPool = try2CreatePool(aDbId);
                } else {
                    throw new ResourceUnavalableException(BAD_APPLICATION_RESOURCE_POOL_MSG);
                }
            }
            return dbPool;
        } catch (Exception ex) {
            throw new ResourceUnavalableException(ex);
        }
    }

    private void testDataSource(DataSource aSource, DbConnectionSettings aSettings) throws Exception {
        try (Connection lconn = aSource.getConnection()) {
            String dialect = dialectByConnection(lconn);
            if (dialect != null) {
                aSettings.setDialect(dialect);
                String schemaName = aSettings.getSchema();
                if (schemaName == null) {
                    SqlDriver driver = SQLUtils.getSqlDriver(dialect);
                    if (driver != null) {
                        String getSchemaClause = driver.getSql4GetConnectionContext();
                        try (Statement stmt = lconn.createStatement()) {
                            try (ResultSet rs = stmt.executeQuery(getSchemaClause)) {
                                if (rs.next() && rs.getMetaData().getColumnCount() > 0) {
                                    schemaName = rs.getString(1);
                                    if (schemaName != null && !schemaName.isEmpty()) {
                                        aSettings.setSchema(schemaName);
                                    }
                                }
                            }
                        }
                    } else {
                        Logger.getLogger(GeneralResourceProvider.class.getName()).log(Level.SEVERE, String.format("Can't obtain sql driver for %s", aSettings.getUrl()));
                    }
                }
            } else {
                Logger.getLogger(GeneralResourceProvider.class.getName()).log(Level.SEVERE, String.format("Can't determine sql dialect for %s. May be it is unsuuported RDBMS.", aSettings.getUrl()));
            }
        }
    }

    private void initApplicationSchema(DataSource aSource) throws Exception {
        try (Connection lconn = aSource.getConnection()) {
            lconn.setAutoCommit(false);
            String dialect = dialectByConnection(lconn);
            SqlDriver driver = SQLUtils.getSqlDriver(dialect);
            driver.initializeApplicationSchema(lconn);
        }
    }

    public synchronized String getPoolSchema(String aDbId) throws Exception {
        getPooledDataSource(aDbId);
        DbConnectionSettings settings = connectionPoolsSettings.get(aDbId);
        if (settings != null) {
            return settings.getSchema();
        } else {
            return null;
        }
    }

    public synchronized String getPoolDialect(String aDbId) throws Exception {
        getPooledDataSource(aDbId);
        DbConnectionSettings settings = connectionPoolsSettings.get(aDbId);
        if (settings != null) {
            return settings.getDialect();
        } else {
            return null;
        }
    }

    private DataSource try2CreatePool(String aDbId) throws Exception {
        AppCache cache = client.getAppCache();
        if (cache != null) {
            ApplicationElement appConnection = cache.get(aDbId);
            if (appConnection.getType() == ClientConstants.ET_CONNECTION) {
                DbConnectionSettings lsettings = DbConnectionSettings.read(appConnection.getContent());
                if (lsettings != null) {
                    DataSource lPool = constructDataSource(lsettings);
                    testDataSource(lPool, lsettings);
                    connectionPools.put(aDbId, lPool);
                    connectionPoolsSettings.put(aDbId, lsettings);
                    return lPool;
                }
            }
        }
        return null;
    }

    /**
     * Frees any resources, holded by this resource provider. Calls shutdown
     * method on all db connection pools.
     */
    public void shutdown() {
        assert connectionPools != null;
        connectionPools.clear();
    }

    /*
     public static Properties constructPropertiesByDbConnectionSettings(DbConnectionSettings dbSettings) {
     if (dbSettings != null) {
     Properties props = dbSettings.getInfo();
     String jdbcUrl = dbSettings.getUrl();
     if (props != null && jdbcUrl != null) {
     Properties serverProps = new Properties();
     Set<Entry<Object, Object>> entries = props.entrySet();
     for (Entry<Object, Object> entry : entries) {
     serverProps.put(entry.getKey(), entry.getValue());
     }
     serverProps.remove(ClientConstants.DB_CONNECTION_USER_PROP_NAME);
     serverProps.remove(ClientConstants.DB_CONNECTION_PASSWORD_PROP_NAME);
     return serverProps;
     }
     }
     return null;
     }
     */
    private String dialectByConnection(Connection aConnection) throws SQLException {
        String dialect = SQLUtils.dialectByUrl(aConnection.getMetaData().getURL());
        if (dialect == null) {
            dialect = SQLUtils.dialectByProductName(aConnection.getMetaData().getDatabaseProductName());
        }
        return dialect;
    }
}
