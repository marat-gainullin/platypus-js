/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.project;

import com.eas.deploy.BaseDeployer;
import com.eas.deploy.project.PlatypusSettings;
import com.eas.designer.application.PlatypusUtils;
import com.eas.util.StringUtils;
import com.eas.xml.dom.Source2XmlDom;
import com.eas.xml.dom.XmlDom2String;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.openide.filesystems.FileObject;
import org.openide.util.EditableProperties;
import org.w3c.dom.Document;

/**
 * Facade for all project's settings.
 *
 * @author vv
 */
public class PlatypusProjectSettings {

    public static final int DEFAULT_PLATYPUS_SERVER_PORT = 8500;
    public static final int CLIENT_APP_DEFAULT_DEBUG_PORT = 8900;
    public static final int SERVER_APP_DEFAULT_DEBUG_PORT = 8901;
    public static final String PROJECT_SETTINGS_FILE = "project.properties"; //NOI18N
    public static final String PROJECT_PRIVATE_SETTINGS_FILE = "private.properties"; //NOI18N
    public static final String PROJECT_DISPLAY_NAME_KEY = "projectDisplayName"; //NOI18N
    public static final String RUN_USER_KEY = "runUser"; //NOI18N
    public static final String RUN_PASSWORD_KEY = "runPassword"; //NOI18N
    public static final String RUN_CLIENT_OPTIONS_KEY = "runClientOptions"; //NOI18N
    public static final String RUN_SERVER_OPTIONS_KEY = "runServerOptions"; //NOI18N
    public static final String DB_APP_SOURCES_KEY = "dbAppSources"; //NOI18N
    public static final String APP_SERVER_PROTOCOL_KEY = "appServerProtocol";//NOI18N
    public static final String SERVER_PORT_KEY = "serverPort";//NOI18N
    public static final String APP_SERVER_HOST_KEY = "appServerHost";//NOI18N
    public static final String START_SERVER_KEY = "startServer"; //NOI18N
    public static final String DEBUG_CLIENT_PORT_KEY = "debugClientPort"; //NOI18N
    public static final String DEBUG_SERVER_PORT_KEY = "debugServerPort"; //NOI18N
    public static final String J2EE_SERVER_ID_KEY = "j2eeServerId"; //NOI18N
    public static final String CLIENT_TYPE_KEY = "clientType"; //NOI18N
    public static final String SERVER_TYPE_KEY = "serverType"; //NOI18N
    protected final PlatypusSettings platypusSettings;
    protected final FileObject projectDir;
    protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    protected EditableProperties projectProperties;
    protected EditableProperties projectPrivateProperties;
    private boolean platypusSettingsIsDirty;
    private boolean projectPropertiesIsDirty;
    private boolean projectPrivatePropertiesIsDirty;

    public PlatypusProjectSettings(FileObject aProjectDir) throws Exception {
        if (aProjectDir == null) {
            throw new IllegalArgumentException("Project directory file object is null."); //NOI18N
        }
        projectDir = aProjectDir;
        String appSettingsStr = new String(getPlatypusSettingsFileObject().asBytes(), PlatypusUtils.COMMON_ENCODING_NAME);
        if (!appSettingsStr.trim().isEmpty()) {
            Document doc = Source2XmlDom.transform(appSettingsStr);
            platypusSettings = PlatypusSettings.valueOf(doc);
        } else {
            platypusSettings = new PlatypusSettings();
        }
        platypusSettings.getChangeSupport().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                platypusSettingsIsDirty = true;
                changeSupport.firePropertyChange(evt);
            }
        });
        projectProperties = new EditableProperties(false);
        try (InputStream is = getProjectSettingsFileObject().getInputStream()) {
            projectProperties.load(is);
        }
        projectPrivateProperties = new EditableProperties(false);
        try (InputStream is = getProjectPrivateSettingsFileObject().getInputStream()) {
            projectPrivateProperties.load(is);
        }
    }

    public PlatypusSettings getAppSettings() {
        return platypusSettings;
    }

    /**
     * Gets the project's display name.
     *
     * @return title for the project
     */
    public String getDisplayName() {
        return projectProperties.get(PROJECT_DISPLAY_NAME_KEY);
    }

    /**
     * Sets the project's display name.
     *
     * @param aValue title for the project
     */
    public void setDisplayName(String aValue) {
        String oldValue = getDisplayName();
        projectProperties.setProperty(PROJECT_DISPLAY_NAME_KEY, aValue);
        projectPropertiesIsDirty = true;
        changeSupport.firePropertyChange(PROJECT_DISPLAY_NAME_KEY, oldValue, aValue);
    }

    /**
     * Gets username for the Platypus user to login on application run.
     *
     * @return Platypus user name
     */
    public String getRunUser() {
        return projectProperties.get(RUN_USER_KEY);
    }

    /**
     * Sets username for the Platypus user to login on application run.
     *
     * @param aValue Platypus user name
     */
    public void setRunUser(String aValue) {
        String oldValue = getRunUser();
        projectProperties.setProperty(RUN_USER_KEY, aValue);
        projectPropertiesIsDirty = true;
        changeSupport.firePropertyChange(RUN_USER_KEY, oldValue, aValue);
    }

    /**
     * Gets password for the Platypus user to login on application run.
     *
     * @return Platypus user name
     */
    public String getRunPassword() {
        return projectProperties.get(RUN_PASSWORD_KEY);
    }

    /**
     * Sets password for the Platypus user to login on application run.
     *
     * @param aValue Platypus user name
     */
    public void setRunPassword(String aValue) {
        String oldValue = getRunPassword();
        projectProperties.setProperty(RUN_PASSWORD_KEY, aValue);
        projectPropertiesIsDirty = true;
        changeSupport.firePropertyChange(RUN_PASSWORD_KEY, oldValue, aValue);
    }

    /**
     * Gets optional parameters provided to Platypus Client.
     *
     * @return parameters string
     */
    public String getRunClientOptions() {
        return projectProperties.get(RUN_CLIENT_OPTIONS_KEY);
    }

    /**
     * Sets optional parameters provided to Platypus Client.
     *
     * @param aValue
     */
    public void setClientOptions(String aValue) {
        String oldValue = getRunClientOptions();
        projectProperties.setProperty(RUN_CLIENT_OPTIONS_KEY, aValue);
        projectPropertiesIsDirty = true;
        changeSupport.firePropertyChange(RUN_CLIENT_OPTIONS_KEY, oldValue, aValue);
    }

    /**
     * Gets optional parameters provided to Platypus Application Server.
     *
     * @return parameters string
     */
    public String getRunServerOptions() {
        return projectProperties.get(RUN_SERVER_OPTIONS_KEY);
    }

    /**
     * Sets optional parameters provided to Platypus Application Server.
     *
     * @param aValue
     */
    public void setServerOptions(String aValue) {
        String oldValue = getRunServerOptions();
        projectProperties.setProperty(RUN_SERVER_OPTIONS_KEY, aValue);
        projectPropertiesIsDirty = true;
        changeSupport.firePropertyChange(RUN_SERVER_OPTIONS_KEY, oldValue, aValue);
    }

    /**
     * Checks if runtime to use application from database.
     *
     * @return true if run application from database
     */
    public boolean isDbAppSources() {
        return Boolean.valueOf(projectProperties.get(DB_APP_SOURCES_KEY));
    }

    /**
     * Sets flag for runtime to use application from database.
     *
     * @param aValue true if run application from database
     */
    public void setDbAppSources(boolean aValue) {
        boolean oldValue = isDbAppSources();
        projectProperties.setProperty(DB_APP_SOURCES_KEY, Boolean.valueOf(aValue).toString());
        projectPropertiesIsDirty = true;
        changeSupport.firePropertyChange(DB_APP_SOURCES_KEY, oldValue, aValue);
    }
    
    /**
     * Gets application server protocol.
     *
     * @return communication protocol name
     */
    public String getServerProtocol() {
        return projectProperties.get(APP_SERVER_PROTOCOL_KEY);
    }

    /**
     * Sets application server's host.
     *
     * @param aValue Url string
     */
    public void setServerProtocol(String aValue) {
        String oldValue = getServerProtocol();
        projectProperties.setProperty(APP_SERVER_PROTOCOL_KEY, aValue);
        projectPropertiesIsDirty = true;
        changeSupport.firePropertyChange(APP_SERVER_PROTOCOL_KEY, oldValue, aValue);
    }
    
        /**
     * Gets application server's host.
     *
     * @return Url string
     */
    public String getServerHost() {
        return projectProperties.get(APP_SERVER_HOST_KEY);
    }

    /**
     * Sets application server host.
     *
     * @param aValue Url string
     */
    public void setServerHost(String aValue) {
        String oldValue = getServerHost();
        projectProperties.setProperty(APP_SERVER_HOST_KEY, aValue);
        projectPropertiesIsDirty = true;
        changeSupport.firePropertyChange(APP_SERVER_HOST_KEY, oldValue, aValue);
    }
    
    /**
     * Gets application server port.
     *
     * @return server port
     */
    public int getServerPort() {
        return StringUtils.parseInt(projectProperties.get(SERVER_PORT_KEY), DEFAULT_PLATYPUS_SERVER_PORT);
    }

    /**
     * Sets application server port.
     *
     * @param aValue server port
     */
    public void setServerPort(int aValue) {
        int oldValue = getServerPort();
        projectProperties.setProperty(SERVER_PORT_KEY, String.valueOf(aValue));
        projectPropertiesIsDirty = true;
        changeSupport.firePropertyChange(SERVER_PORT_KEY, Integer.valueOf(oldValue), Integer.valueOf(aValue));
    }
    
    /**
     * Checks if to start local development application server on application
     * run.
     *
     * @return true to start server
     */
    public boolean isStartServer() {
        return Boolean.valueOf(projectProperties.get(START_SERVER_KEY));
    }

    /**
     * Sets flag to start local development application server on application
     * run.
     *
     * @param aValue true to start server
     */
    public void setStartServer(boolean aValue) {
        boolean oldValue = isStartServer();
        projectProperties.setProperty(START_SERVER_KEY, String.valueOf(aValue));
        projectPropertiesIsDirty = true;
        changeSupport.firePropertyChange(START_SERVER_KEY, oldValue, aValue);
    }

    /**
     * Gets JMX debugging port for Platypus Client on local computer on
     * development if null or empty, use default value.
     *
     * @return JMX debugging port
     */
    public int getDebugClientPort() {
        return StringUtils.parseInt(projectProperties.get(DEBUG_CLIENT_PORT_KEY), CLIENT_APP_DEFAULT_DEBUG_PORT);
    }

    /**
     * Sets JMX debugging port for Platypus Client on local computer on
     * development.
     *
     * @param aValue JMX debugging port
     */
    public void setDebugClientPort(int aValue) {
        int oldValue = getDebugClientPort();
        projectProperties.setProperty(DEBUG_CLIENT_PORT_KEY, String.valueOf(aValue));
        projectPropertiesIsDirty = true;
        changeSupport.firePropertyChange(DEBUG_CLIENT_PORT_KEY, Integer.valueOf(oldValue), Integer.valueOf(aValue));
    }

    /**
     * Gets JMX debugging port for Platypus Application Server on local computer
     * on development if null or empty, use default value.
     *
     * @return JMX debugging port
     */
    public int getDebugServerPort() {
        return StringUtils.parseInt(projectProperties.get(DEBUG_SERVER_PORT_KEY), SERVER_APP_DEFAULT_DEBUG_PORT);
    }

    /**
     * Sets JMX debugging port for Platypus Application Server on local computer
     * on development.
     *
     * @param aValue JMX debugging port
     */
    public void setDebugServerPort(int aValue) {
        int oldValue = getDebugServerPort();
        projectProperties.setProperty(DEBUG_SERVER_PORT_KEY, String.valueOf(aValue));
        projectPropertiesIsDirty = true;
        changeSupport.firePropertyChange(DEBUG_SERVER_PORT_KEY, Integer.valueOf(oldValue), Integer.valueOf(aValue));
    }

    /**
     * Gets J2EE server instance ID.
     *
     * @return J2EE server ID
     */
    public String getJ2eeServerId() {
        return projectPrivateProperties.get(J2EE_SERVER_ID_KEY);
    }

    /**
     * Sets J2EE server instance ID.
     *
     * @param aValue J2EE server ID
     */
    public void setJ2eeServerId(String aValue) {
        String oldValue = getJ2eeServerId();
        projectPrivateProperties.setProperty(J2EE_SERVER_ID_KEY, aValue);
        projectPrivatePropertiesIsDirty = true;
        changeSupport.firePropertyChange(J2EE_SERVER_ID_KEY, aValue, oldValue);
    }
    
    /**
     * Gets client type to be run.
     * @return ClientType instance
     */
    public ClientType getRunClientType() {
        ClientType val = ClientType.getById(projectProperties.get(CLIENT_TYPE_KEY));
        return val != null ? val : ClientType.PLATYPUS_CLIENT;
    }
    
    /**
     * Sets client type to be run.
     * @param aValue ClientType instance
     */
    public void setRunClientType(ClientType aValue) {
        ClientType oldValue = getRunClientType();
        projectProperties.setProperty(CLIENT_TYPE_KEY, aValue.getId());
        projectPropertiesIsDirty = true;
        changeSupport.firePropertyChange(CLIENT_TYPE_KEY, aValue, oldValue);
    }
    
    /**
     * Gets application server type to be run.
     * @return AppServerType instance
     */
    public AppServerType getRunAppServerType() {
        AppServerType val = AppServerType.getById(projectProperties.get(SERVER_TYPE_KEY));
        return val != null ? val : AppServerType.NONE;
    }
    
    /**
     * Sets application server type to be run.
     * @param aValue AppServerType instance
     */
    public void setRunAppServerType(AppServerType aValue) {
        AppServerType oldValue = getRunAppServerType();
        projectProperties.setProperty(SERVER_TYPE_KEY, aValue.getId());
        projectPrivatePropertiesIsDirty = true;
        changeSupport.firePropertyChange(SERVER_TYPE_KEY, aValue, oldValue);
    }

    public void save() throws Exception {
        if (platypusSettingsIsDirty) {
            try (OutputStream os = getPlatypusSettingsFileObject().getOutputStream()) {
                String sData = XmlDom2String.transform(platypusSettings.toDocument());
                os.write(sData.getBytes(PlatypusUtils.COMMON_ENCODING_NAME));
            }
            platypusSettingsIsDirty = false;
        }
        if (projectPropertiesIsDirty) {
            try (OutputStream os = getProjectSettingsFileObject().getOutputStream()) {
                projectProperties.store(os);
            }
            projectPropertiesIsDirty = false;
        }
        if (projectPrivatePropertiesIsDirty) {
            try (OutputStream os = getProjectPrivateSettingsFileObject().getOutputStream()) {
                projectPrivateProperties.store(os);
            }
            projectPrivatePropertiesIsDirty = false;
        }
    }

    public PropertyChangeSupport getChangeSupport() {
        return changeSupport;
    }

    protected final FileObject getPlatypusSettingsFileObject() throws IOException {
        FileObject fo = projectDir.getFileObject(BaseDeployer.PLATYPUS_SETTINGS_FILE);
        if (fo == null) {
            fo = projectDir.createData(BaseDeployer.PLATYPUS_SETTINGS_FILE);
        }
        return fo;
    }

    protected final FileObject getProjectSettingsFileObject() throws IOException {
        FileObject fo = projectDir.getFileObject(PROJECT_SETTINGS_FILE);
        if (fo == null) {
            fo = projectDir.createData(PROJECT_SETTINGS_FILE);
        }
        return fo;
    }

    protected final FileObject getProjectPrivateSettingsFileObject() throws IOException {
        FileObject fo = projectDir.getFileObject(PROJECT_PRIVATE_SETTINGS_FILE);
        if (fo == null) {
            fo = projectDir.createData(PROJECT_PRIVATE_SETTINGS_FILE);
        }
        return fo;
    }
}
