/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.eas.client.settings.DbConnectionSettings;
import com.eas.client.settings.EasSettings;
import com.eas.client.settings.PlatypusConnectionSettings;
import com.eas.client.threetier.PlatypusNativeClient;
import com.eas.client.threetier.http.PlatypusHttpClient;
import com.eas.client.threetier.http.PlatypusHttpConstants;
import com.eas.client.threetier.http.PlatypusHttpsClient;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 *
 * @author mg
 */
public class ClientFactory {

    public static final String SETTINGS_NODE = "/com/eas/client";
    public static final String CONNECTIONS_SETTINGS_NODE = SETTINGS_NODE + "/connections";
    public static final String DEFAULT_CONNECTION_INDEX_SETTING = "defaultConnectionIndex";
    public static final String DEFAULT_CONNECTION_DB_PASSWORD = "dbPassword";
    public static final String DEFAULT_CONNECTION_USER_PASSWORD = "userPassword";
    public static final String CONNECTION_TITLE_SETTING = "title";
    public static final String CONNECTION_URL_SETTING = "url";
    public static final String CONNECTION_SCHEMA_SETTING = "schema";
    public static final String CONNECTION_USER_SETTING = "user";
    private static EasSettings[] settings = null;
    private static EasSettings defaultSettings;

    public static Client getInstance(EasSettings aSettings) throws Exception {
        if (aSettings instanceof DbConnectionSettings) {
            DbConnectionSettings dbSettings = (DbConnectionSettings) aSettings;
            return new DatabasesClient(dbSettings);
        } else if (aSettings instanceof PlatypusConnectionSettings) {
            String url = aSettings.getUrl();
            if (PlatypusHttpConstants.PROTOCOL_HTTPS.equals(url.substring(0, 5).toLowerCase())) {
                return new PlatypusHttpsClient((PlatypusConnectionSettings) aSettings);
            } else if (PlatypusHttpConstants.PROTOCOL_HTTP.equals(url.substring(0, 4).toLowerCase())) {
                return new PlatypusHttpClient((PlatypusConnectionSettings) aSettings);
            } else {
                return new PlatypusNativeClient((PlatypusConnectionSettings) aSettings);
            }
        } else {
            throw new Exception("Unknown settings instance: " + String.valueOf(aSettings));
        }
    }

    public static EasSettings[] getSettings() throws Exception {
        if (settings == null) {
            try {
                readSettings();
            } catch (BackingStoreException ex) {
                Logger.getLogger(ClientFactory.class.getName()).log(Level.SEVERE, null, ex);
                settings = null;
            }
        }
        return settings;
    }

    public static void setDefaultSettings(EasSettings settings) {
        defaultSettings = settings;
    }

    public static EasSettings getDefaultSettings() {
        return defaultSettings;
    }

    public static void reset() {
        settings = null;
        defaultSettings = null;
    }

    public static void readSettings() throws Exception {
        settings = null;
        defaultSettings = null;

        int defaultConnectionIndex = Preferences.userRoot().node(SETTINGS_NODE).getInt(DEFAULT_CONNECTION_INDEX_SETTING, 0);
        if (defaultConnectionIndex < 0) {
            defaultConnectionIndex = 0;
        }
        Map<String, EasSettings> settingsMap = new TreeMap<>();
        Preferences userConnectionsPrefs = Preferences.userRoot().node(CONNECTIONS_SETTINGS_NODE);

        settingsNodeToSettings(userConnectionsPrefs, settingsMap, true);
        if (settingsMap.isEmpty()) {
            Preferences defaultConnectionsPrefs = Preferences.systemRoot().node(CONNECTIONS_SETTINGS_NODE);
            try {
                settingsNodeToSettings(defaultConnectionsPrefs, settingsMap, false);
            } catch (Exception ex) {
                Logger.getLogger(ClientFactory.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        settings = new EasSettings[settingsMap.size()];
        int i = 0;
        for (String connNodeName : settingsMap.keySet()) {
            settings[i++] = settingsMap.get(connNodeName);
        }
        if (settings.length > 0) {
            // check default index
            if (defaultConnectionIndex >= settings.length) {
                defaultConnectionIndex = settings.length - 1;
            }
            if (defaultConnectionIndex < 0) {
                defaultConnectionIndex = 0;
            }
            defaultSettings = settings[defaultConnectionIndex];
        } else {
            defaultSettings = null;
        }
    }

    private static void settingsNodeToSettings(Preferences connectionsPrefs, Map<String, EasSettings> settingsMap, boolean aEditable) throws Exception {
        String[] settingsNodesNames = connectionsPrefs.childrenNames();
        for (int i = 0; i < settingsNodesNames.length; i++) {
            Preferences connectionPrefs = connectionsPrefs.node(settingsNodesNames[i]);
            String connUrl = connectionPrefs.get(ClientFactory.CONNECTION_URL_SETTING, "jdbc");
            connUrl = connUrl.replaceAll("[\\s\\r\\n\\t]", "");
            EasSettings connectionsettings = EasSettings.createInstance(connUrl);
            if (connectionsettings != null) {
                settingsMap.put(settingsNodesNames[i], connectionsettings);
                connectionsettings.setUrl(connUrl);
                connectionsettings.setName(connectionPrefs.get(ClientFactory.CONNECTION_TITLE_SETTING, ""));
                connectionsettings.setUser(connectionPrefs.get(ClientFactory.CONNECTION_USER_SETTING, ""));
                if (connectionsettings instanceof DbConnectionSettings) {
                    ((DbConnectionSettings) connectionsettings).setSchema(connectionPrefs.get(ClientFactory.CONNECTION_SCHEMA_SETTING, ""));
                }
                connectionsettings.setEditable(aEditable);
            } else {
                Logger.getLogger(ClientFactory.class.getName()).log(Level.SEVERE, "Invalid connection url: {0}", connUrl);
            }
        }
    }

    /**
     * Reads default settings from backing store.
     *
     * @return EasSettings instance as part of settings array, that have been
     * read previously.
     */
    public static EasSettings readDefaultSettings() throws Exception {
        int defaultConnectionIndex = Preferences.userRoot().node(SETTINGS_NODE).getInt(DEFAULT_CONNECTION_INDEX_SETTING, 0);
        if (defaultConnectionIndex < 0) {
            defaultConnectionIndex = 0;
        }
        if (settings == null) {
            readSettings();
        }
        return settings[defaultConnectionIndex];
    }
}
