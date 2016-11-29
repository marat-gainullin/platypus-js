/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.resourcepool;

import com.eas.client.settings.DbConnectionSettings;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author mg
 */
public class GeneralResourceProvider {

    protected static class InstanceHolder {

        private static final GeneralResourceProvider instance = new GeneralResourceProvider();
    }
    // dom constants
    public static transient final String DB_DRIVER_TAG_NAME = "driver";
    public static transient final String DB_DRIVER_DIALECT_ATTR_NAME = "dialect";
    public static transient final String[] driversClasses = new String[]{
        "oracle.jdbc.OracleDriver",
        "net.sourceforge.jtds.jdbc.Driver",
        "org.postgresql.Driver",
        "com.mysql.jdbc.Driver",
        "com.ibm.db2.jcc.DB2Driver",
        "org.h2.Driver"
    };

    /**
     *
     * @throws SQLException
     */
    public static void registerDrivers() throws SQLException {
        for (String driverClassName : driversClasses) {
            try {
                Class<?> clazz = Class.forName(driverClassName);
                if (clazz != null) {
                    try {
                        Driver dr = (Driver) clazz.newInstance();
                        try {
                            DriverManager.registerDriver(dr);
                        } catch (SQLException ex) {
                            Logger.getLogger(GeneralResourceProvider.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } catch (InstantiationException | IllegalAccessException ex) {
                        Logger.getLogger(GeneralResourceProvider.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(GeneralResourceProvider.class.getName()).log(Level.WARNING, "JDBC driver class not found: {0}", driverClassName);
            }
        }
    }

    private final Map<String, PlatypusNativeDataSource> connectionPools = new ConcurrentHashMap<>();

    public static GeneralResourceProvider getInstance() {
        return InstanceHolder.instance;
    }

    protected GeneralResourceProvider() {
        super();
    }

    public void registerDatasource(String aName, DbConnectionSettings aSettings) {
        connectionPools.put(aName, constructDataSource(aSettings));
    }

    public void registerDatasource(String aName, PlatypusNativeDataSource aPool) {
        connectionPools.put(aName, aPool);
    }

    public void unregisterDatasource(String aName) throws SQLException {
        PlatypusNativeDataSource removed = connectionPools.remove(aName);
        if (removed != null) {
            removed.shutdown();
        }
    }

    private PlatypusNativeDataSource constructDataSource(DbConnectionSettings aSettings) {
        return new PlatypusNativeDataSource(aSettings.getMaxConnections(), aSettings.getMaxStatements(), aSettings.getUrl(), aSettings.getUser(), aSettings.getPassword(), aSettings.getSchema(), aSettings.getProperties());
    }

    public DataSource getPooledDataSource(String aDatasourceName) throws Exception {
        DataSource ds = connectionPools.get(aDatasourceName);
        if (ds == null) {
            throw new NamingException("Datasource " + aDatasourceName + " is not registered");
        } else {
            return ds;
        }
    }
}
