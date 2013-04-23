package com.eas.deploy.project;

import com.eas.client.settings.ConnectionSettings2XmlDom;
import com.eas.client.settings.DbConnectionSettings;
import com.eas.client.settings.EasSettings;
import com.eas.client.settings.XmlDom2ConnectionSettings;
import com.eas.server.PlatypusServer;
import com.eas.util.StringUtils;
import java.beans.PropertyChangeSupport;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;

/**
 *
 * @author mg
 */
public class PlatypusSettings {

    public static final int CLIENT_APP_DEFAULT_DEBUG_PORT = 8900;
    public static final int SERVER_APP_DEFAULT_DEBUG_PORT = 8901;
    public static final String CONTEXT_TAG_NAME = "context";
    public static final String DISPLAY_NAME_ATTR_NAME = "displayName";
    public static final String RUN_CONFIGURATION_TAG_NAME = "run";
    public static final String DB_APP_ATTR_NAME = "dbApp";
    public static final String APP_SERVER_ATTR_NAME = "appServer";
    public static final String RUNNING_ELEMENT_ATTR_NAME = "appElement";
    public static final String USER_ATTR_NAME = "user";
    public static final String PASSWORD_ATTR_NAME = "password";
    public static final String START_APP_SERVER_ATTR_NAME = "startServer";
    public static final String SERVER_HOST_ATTR_NAME = "serverHost";
    public static final String SERVER_PROTOCOL_ATTR_NAME = "protocol";
    public static final String SERVER_PORT_ATTR_NAME = "serverPort";
    public static final String CLIENT_OPTIONS_ATTR_NAME = "clientOptions";
    public static final String SERVER_OPTIONS_ATTR_NAME = "serverOptions";
    public static final String DEBUGGING_CONFIGURATION_TAG_NAME = "debug";
    public static final String CLIENT_DEBUG_PORT_ATTR_NAME = "clientPort";
    public static final String SERVER_DEBUG_PORT_ATTR_NAME = "serverPort";
    // Running configuration
    protected Boolean dbAppSources;
    protected Boolean appServerMode;
    protected String runElement;
    protected String runUser;
    protected String runPassword;
    protected String runClientOptions;
    protected String serverProtocol;
    protected String serverHost;
    protected Integer serverPort;
    protected String runServerOptions;
    protected static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    protected DocumentBuilder builder;
    protected DbConnectionSettings dbSettings;
    protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    public PlatypusSettings() throws Exception {
        super();
        builder = factory.newDocumentBuilder();
    }

    public PropertyChangeSupport getChangeSupport() {
        return changeSupport;
    }

    public Document toDocument() {
        Document doc = builder.newDocument();
        doc.setXmlStandalone(true);
        Element rootTag = doc.createElement(CONTEXT_TAG_NAME);
        doc.appendChild(rootTag);
        Element runTag = doc.createElement(RUN_CONFIGURATION_TAG_NAME);
        rootTag.appendChild(runTag);
        if (dbAppSources != null) {
            runTag.setAttribute(DB_APP_ATTR_NAME, dbAppSources.toString());
        }
        if (appServerMode != null) {
            runTag.setAttribute(APP_SERVER_ATTR_NAME, appServerMode.toString());
        }
        if (runUser != null && !runUser.isEmpty()) {
            runTag.setAttribute(USER_ATTR_NAME, runUser);
        }
        if (runPassword != null && !runPassword.isEmpty()) {
            runTag.setAttribute(PASSWORD_ATTR_NAME, runPassword);
        }
        if (runElement != null) {
            runTag.setAttribute(RUNNING_ELEMENT_ATTR_NAME, runElement);
        }
        if (runClientOptions != null) {
            runTag.setAttribute(CLIENT_OPTIONS_ATTR_NAME, runClientOptions);
        }
        if (serverProtocol != null) {
            runTag.setAttribute(SERVER_PROTOCOL_ATTR_NAME, serverProtocol);
        }
        if (serverHost != null) {
            runTag.setAttribute(SERVER_HOST_ATTR_NAME, serverHost);
        }
        if (serverPort != null) {
            runTag.setAttribute(SERVER_PORT_ATTR_NAME, String.valueOf(serverPort));
        }
        if (runServerOptions != null) {
            runTag.setAttribute(SERVER_OPTIONS_ATTR_NAME, runServerOptions);
        }
        Document dbDoc = ConnectionSettings2XmlDom.settingsToDocument(dbSettings);
        Element dbTag = dbDoc.getDocumentElement();
        doc.adoptNode(dbTag);
        rootTag.appendChild(dbTag);
        return doc;
    }

    private static Element getElementByName(Element aParent, String aName) {
        NodeList nl = aParent.getElementsByTagName(aName);
        if (nl != null && nl.getLength() == 1 && nl.item(0) instanceof Element) {
            return (Element) nl.item(0);
        } else {
            return null;
        }
    }

    public static PlatypusSettings valueOf(Document aDoc) throws Exception {
        PlatypusSettings platypusSettings = new PlatypusSettings();
        NodeList projectNl = aDoc.getElementsByTagName(CONTEXT_TAG_NAME);
        if (projectNl != null && projectNl.getLength() == 1 && projectNl.item(0) instanceof Element) {
            Element projectTag = (Element) projectNl.item(0);
            Element runTag = getElementByName(projectTag, RUN_CONFIGURATION_TAG_NAME);
            if (runTag != null) {
                String sDbAppSources = runTag.getAttribute(DB_APP_ATTR_NAME);
                if (sDbAppSources != null && !sDbAppSources.isEmpty()) {
                    platypusSettings.dbAppSources = Boolean.parseBoolean(sDbAppSources);
                }
                String sAppServerMode = runTag.getAttribute(APP_SERVER_ATTR_NAME);
                if (sAppServerMode != null && !sAppServerMode.isEmpty()) {
                    platypusSettings.appServerMode = Boolean.parseBoolean(sAppServerMode);
                }
                String sRunningElement = runTag.getAttribute(RUNNING_ELEMENT_ATTR_NAME);
                if (sRunningElement != null && !sRunningElement.isEmpty()) {
                    platypusSettings.runElement = sRunningElement;
                }
                String sRunUser = runTag.getAttribute(USER_ATTR_NAME);
                if (sRunUser != null && !sRunUser.isEmpty()) {
                    platypusSettings.runUser = sRunUser;
                }
                String sRunPassword = runTag.getAttribute(PASSWORD_ATTR_NAME);
                if (sRunPassword != null && !sRunPassword.isEmpty()) {
                    platypusSettings.runPassword = sRunPassword;
                }
                String sRunClientOptions = runTag.getAttribute(CLIENT_OPTIONS_ATTR_NAME);
                if (sRunClientOptions != null && !sRunClientOptions.isEmpty()) {
                    platypusSettings.runClientOptions = sRunClientOptions;
                }
                String sServerProtocol = runTag.getAttribute(SERVER_PROTOCOL_ATTR_NAME);
                if (sServerProtocol != null && !sServerProtocol.trim().isEmpty()) {
                    platypusSettings.serverProtocol = sServerProtocol;
                }
                String sServerHost = runTag.getAttribute(SERVER_HOST_ATTR_NAME);
                if (sServerHost != null && !sServerHost.isEmpty()) {
                    platypusSettings.serverHost = sServerHost;
                }
                String sServerPort = runTag.getAttribute(SERVER_PORT_ATTR_NAME);
                if (sServerPort != null && !sServerPort.isEmpty()) {
                    platypusSettings.serverPort = StringUtils.parseInt(sServerPort, null);
                }
                String sRunServerOptions = runTag.getAttribute(SERVER_OPTIONS_ATTR_NAME);
                if (sRunServerOptions != null && !sRunServerOptions.isEmpty()) {
                    platypusSettings.runServerOptions = sRunServerOptions;
                }
            }
            Node settingsTag = getElementByName(projectTag, ConnectionSettings2XmlDom.DB_SETTINGS_TAG_NAME);
            Document dbDoc = factory.newDocumentBuilder().newDocument();
            dbDoc.adoptNode(settingsTag);
            dbDoc.appendChild(settingsTag);
            EasSettings settings = XmlDom2ConnectionSettings.document2Settings(dbDoc); // deserialize connection settings. throw exceptions of non-database connection settings.
            if (!(settings instanceof DbConnectionSettings)) {
                throw new Exception("Platypus designer projects must contain information about two-tier connection to database");
            }
            DbConnectionSettings dbSettings = (DbConnectionSettings) settings;
            platypusSettings.dbSettings = dbSettings;
            return platypusSettings;
        }
        return null;
    }

    /**
     * Gets default application element to run.
     *
     * @return application element name
     */
    public String getRunElement() {
        return runElement;
    }

    /**
     * Sets default application element to run.
     *
     * @param aValue application element name
     */
    public void setRunElement(String aValue) {
        if (runElement == null ? aValue != null : !runElement.equals(aValue)) {
            String oldValue = runElement;
            runElement = aValue;
            changeSupport.firePropertyChange("runningElement", oldValue, runElement);
        }
    }

    /**
     * Gets username for the Platypus user to login on application run.
     *
     * @return Platypus user name
     */
    public String getRunUser() {
        return runUser;
    }

    /**
     * Sets username for the Platypus user to login on application run.
     *
     * @param aValue Platypus user name
     */
    public void setRunUser(String aValue) {
        if (runUser == null ? aValue != null : !runUser.equals(aValue)) {
            String oldValue = runUser;
            runUser = aValue;
            changeSupport.firePropertyChange("runUser", oldValue, runUser);
        }
    }

    /**
     * Gets password for the Platypus user to login on application run.
     *
     * @return Platypus user name
     */
    public String getRunPassword() {
        return runPassword;
    }

    /**
     * Sets password for the Platypus user to login on application run.
     *
     * @param aValue Platypus user name
     */
    public void setRunPassword(String aValue) {
        if (runPassword == null ? aValue != null : !runPassword.equals(aValue)) {
            String oldValue = runPassword;
            runPassword = aValue;
            changeSupport.firePropertyChange("runPassword", oldValue, runPassword);
        }
    }

    /**
     * Gets optional parameters provided to Platypus Client.
     *
     * @return parameters string
     */
    public String getRunClientOptions() {
        return runClientOptions;
    }

    /**
     * Sets optional parameters provided to Platypus Client.
     *
     * @param aValue
     */
    public void setClientOptions(String aValue) {
        if (runClientOptions == null ? aValue != null : !runClientOptions.equals(aValue)) {
            String oldValue = runClientOptions;
            runClientOptions = aValue;
            changeSupport.firePropertyChange("runClientOptions", oldValue, runClientOptions);
        }
    }

    /**
     * Gets optional parameters provided to Platypus Application Server.
     *
     * @return parameters string
     */
    public String getRunServerOptions() {
        return runServerOptions;
    }

    /**
     * Sets optional parameters provided to Platypus Application Server.
     *
     * @param aValue
     */
    public void setServerOptions(String aValue) {
        if (runServerOptions == null ? aValue != null : !runServerOptions.equals(aValue)) {
            String oldValue = runServerOptions;
            runServerOptions = aValue;
            changeSupport.firePropertyChange("runServerOptions", oldValue, runServerOptions);
        }
    }

    /**
     * Checks if runtime to use application from database.
     *
     * @return true if run application from database
     */
    public Boolean isDbAppSources() {
        return dbAppSources;
    }

    /**
     * Sets flag for runtime to use application from database.
     *
     * @param aValue true if run application from database
     */
    public void setDbAppSources(Boolean aValue) {
        if (dbAppSources == null ? aValue != null : !dbAppSources.equals(aValue)) {
            Boolean oldValue = dbAppSources;
            dbAppSources = aValue;
            changeSupport.firePropertyChange("dbAppSources", oldValue, dbAppSources);
        }
    }

    /**
     * Checks if runtime to use application server.
     *
     * @return true to use server
     */
    public Boolean isUseAppServer() {
        return appServerMode;
    }

    /**
     * Sets flag for runtime to use application server.
     *
     * @param aValue true to use server
     */
    public void setUseAppServer(Boolean aValue) {
        if (appServerMode == null ? aValue != null : !appServerMode.equals(aValue)) {
            Boolean oldValue = appServerMode;
            appServerMode = aValue;
            changeSupport.firePropertyChange("appServerMode", oldValue, appServerMode);
        }
    }
    
    /**
     * Gets application server protocol.
     *
     * @return communication protocol name
     */
    public String getServerProtocol() {
        return serverProtocol != null && !serverProtocol.isEmpty() ? serverProtocol : PlatypusServer.DEFAULT_PROTOCOL;
    }

    /**
     * Sets application server's host.
     *
     * @param aValue Url string
     */
    public void setServerProtocol(String aValue) {
        if (serverProtocol == null ? aValue != null : !serverProtocol.equals(aValue)) {
            String oldValue = serverProtocol;
            serverProtocol = aValue;
            changeSupport.firePropertyChange("serverProtocol", oldValue, serverProtocol);
        }
    }

    /**
     * Gets application server's host.
     *
     * @return Url string
     */
    public String getServerHost() {
        return serverHost;
    }

    /**
     * Sets application server host.
     *
     * @param aValue Url string
     */
    public void setServerHost(String aValue) {
        if (serverHost == null ? aValue != null : !serverHost.equals(aValue)) {
            String oldValue = serverHost;
            serverHost = aValue;
            changeSupport.firePropertyChange("serverHost", oldValue, serverHost);
        }
    }
    
    /**
     * Gets application server port.
     *
     * @return JMX debugging port
     */
    public Integer getServerPort() {
        return serverPort != null ? serverPort : PlatypusServer.DEFAULT_PORT;
    }

    /**
     * Sets application server port.
     *
     * @param aValue JMX debugging port
     */
    public void setServerPort(Integer aValue) {
        if (serverPort == null ? aValue != null : !serverPort.equals(aValue)) {
            Integer oldValue = serverPort;
            serverPort = aValue;
            changeSupport.firePropertyChange("serverPort", oldValue, serverPort);
        }
    }

    /**
     * Gets project's database settings.
     *
     * @return Db settings
     */
    public DbConnectionSettings getDbSettings() {
        return dbSettings;
    }

    /**
     * Sets project's database settings.
     *
     * @param aValue Db settings
     */
    public void setDbSettings(DbConnectionSettings aValue) {
        if (dbSettings != aValue) {
            DbConnectionSettings oldValue = dbSettings;
            dbSettings = aValue;
            changeSupport.firePropertyChange("dbSettings", oldValue, dbSettings);
        }
    }
}
