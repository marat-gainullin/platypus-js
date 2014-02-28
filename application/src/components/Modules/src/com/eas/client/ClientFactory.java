/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.eas.client.cache.FilesAppCache;
import com.eas.client.resourcepool.GeneralResourceProvider;
import com.eas.client.settings.ConnectionSettings;
import com.eas.client.settings.DbConnectionSettings;
import com.eas.client.settings.PlatypusConnectionSettings;
import com.eas.client.threetier.PlatypusNativeClient;
import com.eas.client.threetier.http.PlatypusHttpClient;
import com.eas.client.threetier.http.PlatypusHttpConstants;
import com.eas.client.threetier.http.PlatypusHttpsClient;
import java.io.File;
import java.net.URI;
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
    public static final String DEFAULT_USERNAME_SETTING = "defaultUserName";
    public static final String DEFAULT_PASSWORD_SETTING = "defaultPassword";
    public static final String CONNECTION_TITLE_SETTING = "title";
    public static final String CONNECTION_URL_SETTING = "url";
    public static final String CONNECTION_USER_SETTING = "user";
    public static final String CONNECTION_PASSWORD_SETTING = "password";
    private static ConnectionSettings[] settings;
    private static ConnectionSettings defaultSettings;

    public static Client getInstance(String aApplicationUrl, String aDefaultDatasourceName) throws Exception {
        if (aApplicationUrl != null) {
            if (aApplicationUrl.endsWith(H2DB_FILE_SUFFIX) && (new File(aApplicationUrl)).exists()) {
                aDefaultDatasourceName = "ds-" + Math.abs(aApplicationUrl.hashCode());
                String jndiUrl = "jndi://" + aDefaultDatasourceName;
                GeneralResourceProvider.getInstance().registerDatasource(aDefaultDatasourceName, new DbConnectionSettings("jdbc:h2:/" + aApplicationUrl.substring(0, aApplicationUrl.length() - H2DB_FILE_SUFFIX.length()), "sa", "sa", "PUBLIC", null));
                AppCache appCache = obtainTwoTierAppCache(jndiUrl, null);
                return new ScriptedDatabasesClient(appCache, aDefaultDatasourceName, true);
            } else {
                if (aApplicationUrl.toLowerCase().startsWith(PlatypusHttpConstants.PROTOCOL_HTTP)) {
                    return new PlatypusHttpsClient(aApplicationUrl);
                } else if (aApplicationUrl.toLowerCase().startsWith(PlatypusHttpConstants.PROTOCOL_HTTPS)) {
                    return new PlatypusHttpClient(aApplicationUrl);
                } else if (aApplicationUrl.toLowerCase().startsWith("platypus")) {
                    return new PlatypusNativeClient(aApplicationUrl);
                } else if (aApplicationUrl.toLowerCase().startsWith("jndi") || aApplicationUrl.toLowerCase().startsWith("file")) {
                    AppCache appCache = obtainTwoTierAppCache(aApplicationUrl, null);
                    return new ScriptedDatabasesClient(appCache, aDefaultDatasourceName, true);
                } else {
                    throw new Exception("Unknown protocol in url: " + aApplicationUrl);
                }
            }
        } else {
            throw new IllegalArgumentException("Application url is missing. url is a required parameter.");
        }
    }
    public static final String H2DB_FILE_SUFFIX = ".h2.db";

    public static AppCache obtainTwoTierAppCache(String aApplicationUrl, FilesAppCache.ScanCallback aCallBack) throws Exception {
        AppCache appCache;
        if (aApplicationUrl.startsWith("jndi")) {
            appCache = new DatabaseAppCache(aApplicationUrl);
        } else {// file://
            File f = new File(new URI(aApplicationUrl));
            if (f.exists() && f.isDirectory()) {
                FilesAppCache filesAppCache = new FilesAppCache(f.getPath(), aCallBack);
                filesAppCache.watch();
                appCache = filesAppCache;
            } else {
                throw new IllegalArgumentException("applicationUrl: " + aApplicationUrl + " doesn't point to existent directory or JNDI resource.");
            }
        }
        return appCache;
    }

    public static ConnectionSettings[] getSettings() throws Exception {
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

    public static void setDefaultSettings(ConnectionSettings settings) {
        defaultSettings = settings;
    }

    public static ConnectionSettings getDefaultSettings() {
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
        Map<Integer, ConnectionSettings> settingsMap = new TreeMap<>();
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
        settings = new ConnectionSettings[settingsMap.size()];
        int i = 0;
        for (Integer connNodeName : settingsMap.keySet()) {
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

    private static void settingsNodeToSettings(Preferences connectionsPrefs, Map<Integer, ConnectionSettings> settingsMap, boolean aEditable) throws Exception {
        String[] settingsNodesNames = connectionsPrefs.childrenNames();
        for (int i = 0; i < settingsNodesNames.length; i++) {
            Preferences connectionPrefs = connectionsPrefs.node(settingsNodesNames[i]);
            String connUrl = connectionPrefs.get(ClientFactory.CONNECTION_URL_SETTING, "jdbc");
            connUrl = connUrl.replaceAll("[\\s\\r\\n\\t]", "");
            ConnectionSettings connectionsettings = new PlatypusConnectionSettings();
            settingsMap.put(Integer.valueOf(settingsNodesNames[i]), connectionsettings);
            connectionsettings.setUrl(connUrl);
            connectionsettings.setName(connectionPrefs.get(ClientFactory.CONNECTION_TITLE_SETTING, ""));
            connectionsettings.setUser(connectionPrefs.get(ClientFactory.CONNECTION_USER_SETTING, ""));
            connectionsettings.setPassword(connectionPrefs.get(ClientFactory.CONNECTION_PASSWORD_SETTING, ""));
            connectionsettings.setEditable(aEditable);
        }
    }

    /**
     * Reads default settings from backing store.
     *
     * @return EasSettings instance as part of settings array, that have been
     * read previously.
     */
    public static ConnectionSettings readDefaultSettings() throws Exception {
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
