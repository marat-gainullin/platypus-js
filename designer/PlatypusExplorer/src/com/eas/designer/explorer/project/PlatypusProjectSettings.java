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

    public static final int CLIENT_APP_DEFAULT_DEBUG_PORT = 8900;
    public static final int SERVER_APP_DEFAULT_DEBUG_PORT = 8901;
    public static final String PROJECT_SETTINGS_FILE = "project.properties"; //NOI18N
    public static final String PROJECT_PRIVATE_SETTINGS_FILE = "private.properties"; //NOI18N
    public static final String PROJECT_DISPLAY_NAME_KEY = "projectDisplayName"; //NOI18N
    public static final String START_SERVER_KEY = "startServer"; //NOI18N
    public static final String DEBUG_CLIENT_PORT_KEY = "debugClientPort"; //NOI18N
    public static final String DEBUG_SERVER_PORT_KEY = "debugServerPort"; //NOI18N
    public static final String J2EE_SERVER_ID_KEY = "j2eeServerId"; //NOI18N
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

    public void load() {
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
