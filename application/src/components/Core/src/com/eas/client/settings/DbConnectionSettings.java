/* Datamodel license
 * Exclusive rights on this code in any form
 * are belong to it's athor.
 * This code was developed for commercial purposes only 
 * For any questions and any actions with this code in any form
 * you have to contact it's athor.
 * All rights reserved
 */
package com.eas.client.settings;

import com.bearsoft.rowset.resourcepool.BearResourcePool;
import com.eas.client.ClientConstants;
import com.eas.client.ConnectionSettingsVisitor;
import com.eas.util.BinaryUtils;
import com.eas.xml.dom.Source2XmlDom;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author mg
 */
public class DbConnectionSettings extends EasSettings {
    // dom constants

    public static transient final String DB_DRIVER_TAG_NAME = "driver";
    public static transient final String DB_DRIVER_DIALECT_ATTR_NAME = "dialect";
    // file constants
    public static transient final String DB_DRIVERS_FILE_NAME = "DbDrivers.xml";
    protected transient Map<String, String> drivers;
    protected int maxConnections = BearResourcePool.DEFAULT_MAXIMUM_SIZE;
    protected int maxStatements = BearResourcePool.DEFAULT_MAXIMUM_SIZE * 5;
    protected String applicationPath;
    private boolean initSchema = true;
    private boolean deferCache;

    public DbConnectionSettings() throws Exception {
        super();
        drivers = readDrivers();
    }

    public DbConnectionSettings(String anUrl, String aSchema, String anUser, String aPsw, String aDialect) throws Exception {
        this();
        url = anUrl;
        if (aSchema != null) {
            info.put(ClientConstants.DB_CONNECTION_SCHEMA_PROP_NAME, aSchema);
        }
        if (anUser != null) {
            info.put(ClientConstants.DB_CONNECTION_USER_PROP_NAME, anUser);
        }
        if (aPsw != null) {
            info.put(ClientConstants.DB_CONNECTION_PASSWORD_PROP_NAME, aPsw);
        }
        if (aDialect != null) {
            info.put(ClientConstants.DB_CONNECTION_DIALECT_PROP_NAME, aDialect);
        }
    }

    public DbConnectionSettings(String anUrl, String aSchema, String anUser, String aPsw, String aDialect, boolean aInitSchema) throws Exception {
        this(anUrl, aSchema, anUser, aPsw, aDialect);
        initSchema = aInitSchema;
    }

    public static void registerDrivers(Collection<String> aDrivers) throws SQLException {
        if (aDrivers != null) {
            for (String driverClassName : aDrivers) {
                try {
                    Class<?> clazz = Class.forName(driverClassName);
                    if (clazz != null) {
                        try {
                            Driver dr = (Driver) clazz.newInstance();
                            try {
                                DriverManager.registerDriver(dr);
                            } catch (SQLException ex) {
                                Logger.getLogger(DbConnectionSettings.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } catch (InstantiationException | IllegalAccessException ex) {
                            Logger.getLogger(DbConnectionSettings.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(DbConnectionSettings.class.getName()).log(Level.WARNING, "JDBC driver class not found: {0}",  driverClassName);
                }
            }
        }
    }

    public boolean isDeferCache() {
        return deferCache;
    }

    public void setDeferCache(boolean aValue) {
        deferCache = aValue;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int aMaxConnections) {
        maxConnections = aMaxConnections;
    }

    public int getMaxStatements() {
        return maxStatements;
    }

    public void setMaxStatements(int aMaxStatements) {
        maxStatements = aMaxStatements;
    }

    public void generateSampleSettings() {
        url = "jdbc:oracle:thin:@<HOST>:<PORT>:<SID>";
        info.put("user", "SomeUser");
        info.put("schema", "SomeSchema");
    }

    public String getApplicationPath() {
        return applicationPath;
    }

    public void setApplicationPath(String aPath) {
        applicationPath = aPath;
    }

    public Map<String, String> getDrivers() {
        return drivers;
    }

    public void setDrivers(Map<String, String> aDrivers) {
        drivers = aDrivers;
    }
    
    /**
     * Gets information about JDBC drivers supported by Platypus Platform.
     * @return Dictionary where the key is database dialect and value is JDBC driver class name
     * @throws Exception if something goes wrong
     */
    public static Map<String, String> readDrivers() throws Exception {
        InputStream is = DbConnectionSettings.class.getResourceAsStream(DbConnectionSettings.DB_DRIVERS_FILE_NAME);
        Map<String, String> drivers = new HashMap<>();
        if (is.available() > 0) {
            try {
                String driversDataString = new String(BinaryUtils.readStream(is, -1), SettingsConstants.COMMON_ENCODING);
                Document driversDoc = Source2XmlDom.transform(driversDataString);
                Node jdbcNode = driversDoc.getFirstChild();
                if (jdbcNode != null && "jdbc".equals(jdbcNode.getNodeName())) { //NOI18N
                    NodeList driversNodes = jdbcNode.getChildNodes();
                    drivers.clear();
                    for (int i = 0; i < driversNodes.getLength(); i++) {
                        Node driverNode = driversNodes.item(i);
                        if (driverNode instanceof Element && DB_DRIVER_TAG_NAME.equals(driverNode.getNodeName())) {
                            Element element = (Element)driverNode;
                            String dialect = element.getAttribute(DB_DRIVER_DIALECT_ATTR_NAME);
                            String driverClassName = element.getTextContent();
                            if (dialect != null && !dialect.isEmpty() && driverClassName != null && !driverClassName.isEmpty()) {
                                drivers.put(dialect, driverClassName.replaceAll("[\\s\\r\\n\\t]", "")); //NOI18N
                            }
                        }
                    }
                } else {
                    throw new Exception("jdbc root node expected, but none found");
                }
            } finally {
                is.close();
            }
        } else {
            is.close();
            throw new Exception("jdbc drivers description file is empty");
        }
        return drivers;
    }

    @Override
    public void accept(ConnectionSettingsVisitor v) {
        v.visit(this);
    }

    public static DbConnectionSettings read(Document doc) throws Exception {
        if (doc != null) {
            EasSettings lsettings = XmlDom2ConnectionSettings.document2Settings(doc);
            if (lsettings instanceof DbConnectionSettings) {
                return (DbConnectionSettings) lsettings;
            }
        }
        return null;
    }

    public static DbConnectionSettings read(String aContent) throws Exception {
        if (aContent != null && !aContent.isEmpty()) {
            return read(Source2XmlDom.transform(aContent));
        } else {
            return null;
        }
    }

    public static DbConnectionSettings read(Reader aContentReader) throws IOException, Exception {
        if (aContentReader != null && aContentReader.ready()) {
            Document doc = Source2XmlDom.transform(aContentReader);
            EasSettings lsettings = XmlDom2ConnectionSettings.document2Settings(doc);
            if (lsettings instanceof DbConnectionSettings) {
                return (DbConnectionSettings) lsettings;
            }
        }
        return null;
    }

    /**
     * Checks if initialization of the schema is required.
     * @return The initSchema flag
     */
    public boolean isInitSchema() {
        return initSchema;
    }

    /**
     * Sets if initialization of the schema is required.
     * @param aValue The initSchema flag
     */
    public void setInitSchema(boolean aValue) {
        initSchema = aValue;
    }
}
