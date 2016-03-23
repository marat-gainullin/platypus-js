/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.project;

import java.beans.PropertyChangeSupport;
import java.util.logging.Level;

/**
 *
 * @author vv
 */
public interface PlatypusProjectSettings {
    
    public static final String START_JS_FILE_NAME = "start.js"; //NOI18N

    PropertyChangeSupport getChangeSupport();

    /**
     * Gets default application element to run.
     *
     * @return application element name
     */
    String getRunElement();

    /**
     * Sets default application element to run.
     *
     * @param aValue application element name
     * @throws java.lang.Exception
     */
    void setRunElement(String aValue) throws Exception;

    /**
     * Gets sources path within the project.
     *
     * @return Source path subfolder
     */
    String getSourcePath();

    /**
     * Sets sources path within the project.
     *
     * @param aValue Subfolder value
     */
    void setSourcePath(String aValue);

    /**
     * Get the default data source name
     *
     * @return string of the default data source name
     */
    String getDefaultDataSourceName();

    /**
     * Sets the default data source name for a project
     *
     * @param aValue a default data source name
     */
    void setDefaultDatasourceName(String aValue);

    /**
     * Gets the application server's host.
     *
     * @return Url string
     */
    String getClientUrl();

    /**
     * Gets the log level for Platypus Client.
     *
     * @return Log level value
     */
    Level getClientLogLevel();

    /**
     * Sets a log level for Platypus Client.
     *
     * @param aValue Log level value
     */
    void setClientLogLevel(Level aValue);

    /**
     * Checks if security realm is enabled.
     *
     * @return true to enable configure security realm
     */
    boolean isSecurityRealmEnabled();

    /**
     * Sets if security realm is enabled.
     *
     * @param aValue true to enable configure security realm
     */
    void setSecurityRealmEnabled(boolean aValue);

    /**
     * Gets the log level for Platypus Server.
     *
     * @return Log level value
     */
    Level getServerLogLevel();

    /**
     * Sets a log level for Platypus Server.
     *
     * @param aValue Log level value
     */
    void setServerLogLevel(Level aValue);

    /**
     * Gets JMX debugging port for Platypus Client on local computer on
     * development if null or empty, use default value.
     *
     * @return JMX debugging port
     */
    int getDebugClientPort();

    boolean getBrowserCacheBusting();
    
    boolean getGlobalAPI();
    
    /**
     * Gets JMX debugging port for Platypus Application Server on local computer
     * on development if null or empty, use default value.
     *
     * @return JMX debugging port
     */
    int getDebugServerPort();

    /**
     * Gets the project's display name.
     *
     * @return title for the project
     */
    String getDisplayName();

    /**
     * Gets J2EE server instance ID.
     *
     * @return J2EE server ID
     */
    String getJ2eeServerId();

    /**
     * Gets application server type to be run.
     *
     * @return AppServerType instance
     */
    AppServerType getRunAppServerType();

    /**
     * Gets optional parameters provided to Platypus Client.
     *
     * @return parameters string
     */
    String getRunClientOptions();

    /**
     * Gets client type to be run.
     *
     * @return ClientType instance
     */
    ClientType getRunClientType();

    /**
     * Gets JVM options provided to Platypus Client.
     *
     * @return parameters string
     */
    String getRunClientVmOptions();

    /**
     * Gets password for the Platypus user to login on application run.
     *
     * @return Platypus user name
     */
    String getRunPassword();

    /**
     * Gets optional parameters provided to Platypus Application Server.
     *
     * @return parameters string
     */
    String getRunServerOptions();

    /**
     * Gets JVM options provided to Platypus Application Server.
     *
     * @return parameters string
     */
    String getRunServerVmOptions();

    /**
     * Gets username for the Platypus user to login on application run.
     *
     * @return Platypus user name
     */
    String getRunUser();

    /**
     * Gets application's context name.
     *
     * @return The name of the context string
     */
    String getServerContext();

    /**
     * Gets application's server port.
     *
     * @return server port
     */
    int getServerPort();

    /**
     * Checks if NOT to start local development application server on
     * application run.
     *
     * @return true not to start server
     */
    boolean isNotStartServer();

    /**
     * Saves the project settings.
     *
     * @throws Exception if something goes wrong
     */
    void save() throws Exception;

    /**
     * Sets optional parameters provided to Platypus Client.
     *
     * @param aValue
     */
    void setClientOptions(String aValue);

    /**
     * Sets application's server host.
     *
     * @param aValue Url string
     */
    void setClientUrl(String aValue);

    /**
     * Sets JVM options provided to Platypus Client.
     *
     * @param aValue
     */
    void setClientVmOptions(String aValue);

    /**
     * Sets JPDA debugging port for Platypus Client on local computer on
     * development.
     *
     * @param aValue JPDA debugging port
     */
    void setDebugClientPort(int aValue);

    void setBrowserCacheBusting(boolean aValue);
    
    void setGlobalAPI(boolean aValue);
    
    /**
     * Sets JMX debugging port for Platypus Application Server on local computer
     * on development.
     *
     * @param aValue JMX debugging port
     */
    void setDebugServerPort(int aValue);

    /**
     * Sets the project's display name.
     *
     * @param aValue title for the project
     */
    void setDisplayName(String aValue);

    /**
     * Sets J2EE server instance ID.
     *
     * @param aValue J2EE server ID
     */
    void setJ2eeServerId(String aValue);

    /**
     * Sets flag NOT to start local development application server on
     * application run.
     *
     * @param aValue true not to start server
     */
    void setNotStartServer(boolean aValue);

    /**
     * Sets application server type to be run.
     *
     * @param aValue AppServerType instance
     */
    void setRunAppServerType(AppServerType aValue);

    /**
     * Sets client type to be run.
     *
     * @param aValue ClientType instance
     */
    void setRunClientType(ClientType aValue);

    /**
     * Sets password for the Platypus user to login on application run.
     *
     * @param aValue Platypus user name
     */
    void setRunPassword(String aValue);

    /**
     * Sets username for the Platypus user to login on application run.
     *
     * @param aValue Platypus user name
     */
    void setRunUser(String aValue);

    /**
     * Sets application's context name.
     *
     * @param aValue The name of the context string
     */
    void setServerContext(String aValue);

    /**
     * Sets optional parameters provided to Platypus Application Server.
     *
     * @param aValue
     */
    void setServerOptions(String aValue);

    /**
     * Sets application's server port.
     *
     * @param aValue server port
     */
    void setServerPort(int aValue);

    /**
     * Sets JVM options provided to Platypus Application Server.
     *
     * @param aValue
     */
    void setServerVmOptions(String aValue);

}
