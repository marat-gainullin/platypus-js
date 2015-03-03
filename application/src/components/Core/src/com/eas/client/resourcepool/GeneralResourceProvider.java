/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.resourcepool;

import com.bearsoft.rowset.exceptions.ResourceUnavalableException;
import com.eas.client.settings.DbConnectionSettings;
import com.eas.client.settings.SettingsConstants;
import com.eas.util.BinaryUtils;
import com.eas.xml.dom.Source2XmlDom;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author mg
 */
public class GeneralResourceProvider {

    private static GeneralResourceProvider instance;
    // file constants
    public static transient final String DB_DRIVERS_FILE_NAME = "DbDrivers.xml";
    // dom constants
    public static transient final String DB_DRIVER_TAG_NAME = "driver";
    public static transient final String DB_DRIVER_DIALECT_ATTR_NAME = "dialect";

    /**
     * Gets information about JDBC drivers supported by Platypus Platform.
     *
     * @return Dictionary where the key is database dialect and value is JDBC
     * driver class name
     * @throws SQLException if something goes wrong
     */
    private static Map<String, String> readDrivers() throws SQLException {
        try {
            Map<String, String> ldrivers = new HashMap<>();
            try (InputStream is = GeneralResourceProvider.class.getResourceAsStream(DB_DRIVERS_FILE_NAME)) {
                if (is.available() > 0) {
                    String driversDataString = new String(BinaryUtils.readStream(is, -1), SettingsConstants.COMMON_ENCODING);
                    Document driversDoc = Source2XmlDom.transform(driversDataString);
                    Node jdbcNode = driversDoc.getFirstChild();
                    if (jdbcNode != null && "jdbc".equals(jdbcNode.getNodeName())) { //NOI18N
                        NodeList driversNodes = jdbcNode.getChildNodes();
                        ldrivers.clear();
                        for (int i = 0; i < driversNodes.getLength(); i++) {
                            Node driverNode = driversNodes.item(i);
                            if (driverNode instanceof Element && DB_DRIVER_TAG_NAME.equals(driverNode.getNodeName())) {
                                Element element = (Element) driverNode;
                                String dialect = element.getAttribute(DB_DRIVER_DIALECT_ATTR_NAME);
                                String driverClassName = element.getTextContent();
                                if (dialect != null && !dialect.isEmpty() && driverClassName != null && !driverClassName.isEmpty()) {
                                    ldrivers.put(dialect, driverClassName.replaceAll("[\\s\\r\\n\\t]", "")); //NOI18N
                                }
                            }
                        }
                    } else {
                        throw new SQLException("jdbc root node expected, but none found");
                    }
                } else {
                    throw new SQLException("jdbc drivers description file is empty");
                }
                return ldrivers;
            }
        } catch (IOException ex) {
            throw new SQLException(ex);
        }
    }

    static void registerDrivers(Collection<String> aDrivers) throws SQLException {
        if (aDrivers != null) {
            aDrivers.stream().forEach((String driverClassName) -> {
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
            });
        }
    }

    /* db connections pools
     * <String, >  - is connection pool id. It's the same as a connection descriptor db id.
     */
    private final Map<String, PlatypusNativeDataSource> connectionPools = new HashMap<>();
    private final Map<String, DbConnectionSettings> connectionPoolsSettings = new HashMap<>();

    public static GeneralResourceProvider getInstance() throws SQLException {
        if (instance == null) {
            return getInstanceSync();
        } else {
            return instance;
        }
    }

    private static synchronized GeneralResourceProvider getInstanceSync() throws SQLException {
        if (instance == null) {
            Map<String, String> ldrivers = readDrivers();
            registerDrivers(ldrivers.values());
            instance = new GeneralResourceProvider();
        }
        return instance;
    }

    protected GeneralResourceProvider() {
        super();
    }

    public void registerDatasource(String aName, DbConnectionSettings aSettings) {
        connectionPoolsSettings.put(aName, aSettings);
    }

    public void unregisterDatasource(String aName) throws SQLException {
        disconnectDatasource(aName);
        connectionPoolsSettings.remove(aName);
    }

    private PlatypusNativeDataSource constructDataSource(DbConnectionSettings aSettings) throws Exception {
        return new PlatypusNativeDataSource(aSettings.getMaxConnections(), aSettings.getMaxStatements(), aSettings.getUrl(), aSettings.getUser(), aSettings.getPassword(), aSettings.getSchema(), aSettings.getProperties());
    }

    public synchronized DataSource getPooledDataSource(String aDatasourceName) throws Exception {
        if (connectionPoolsSettings.containsKey(aDatasourceName)) {
            try {
                DataSource dbPool = connectionPools.get(aDatasourceName);
                if (dbPool == null) {
                    dbPool = try2CreatePool(aDatasourceName);
                }
                return dbPool;
            } catch (Exception ex) {
                throw new ResourceUnavalableException(ex);
            }
        } else {
            throw new NamingException("Datasource " + aDatasourceName + " is not registered");
        }
    }

    private void testDataSource(DataSource aSource) throws Exception {
        try (Connection lconn = aSource.getConnection()) {
        }
    }

    private DataSource try2CreatePool(String aDataSourceId) throws Exception {
        DbConnectionSettings lsettings = connectionPoolsSettings.get(aDataSourceId);
        if (lsettings != null) {
            PlatypusNativeDataSource lPool = constructDataSource(lsettings);
            testDataSource(lPool);
            connectionPools.put(aDataSourceId, lPool);
            return lPool;
        } else {
            throw new NamingException("Datasource " + aDataSourceId + " is not registered");
        }
    }

    public synchronized boolean disconnectDatasource(String aName) throws SQLException {
        if (connectionPools.containsKey(aName)) {
            PlatypusNativeDataSource pool = connectionPools.remove(aName);
            pool.shutdown();
            return true;
        } else {
            return false;
        }
    }

    public synchronized boolean isDatasourceConnected(String aName) {
        return connectionPools.containsKey(aName);
    }

}
