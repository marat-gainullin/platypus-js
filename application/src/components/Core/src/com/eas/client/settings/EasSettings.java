/* Datamodel license
 * Exclusive rights on this code in any form
 * are belong to it's athor.
 * This code was developed for commercial purposes only 
 * For any questions and any actions with this code in any form
 * you have to contact it's athor.
 * All rights reserved
 */
package com.eas.client.settings;

import com.eas.client.ConnectionSettingsVisitor;

/**
 *
 * @author mg
 */
public abstract class EasSettings {

    public static transient final String EAS_SETTINGS_FILE_NAME = "EasSettings.xml";
    protected String url = "";
    protected String name;
    protected String user;
    protected String password;
    protected boolean editable = true;

    protected EasSettings() {
        super();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String aUrl) {
        if (aUrl != null) {
            aUrl = aUrl.replace('\\', '/');
            aUrl = aUrl.replace("\\s\\r\\n\\t", "");
        }
        url = aUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String aValue) {
        name = aValue;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static EasSettings createInstance(String connectionString) throws Exception {
        if (isJdbcUrl(connectionString)) {
            DbConnectionSettings settings = new DbConnectionSettings();
            settings.setUrl(connectionString);
            return settings;
        } else if (isAppServerUrl(connectionString)) {
            PlatypusConnectionSettings settings = new PlatypusConnectionSettings();
            settings.setUrl(connectionString);
            return settings;
        } else {
            return null;
        }
    }
    
    public static boolean isJdbcUrl(String url) {
        return url.startsWith("jdbc:");//NOI18N
    }
    
    public static boolean isAppServerUrl(String url) {
        return url.startsWith("platypus") || url.startsWith("http");//NOI18N
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean aValue) {
        editable = aValue;
    }

    public abstract void accept(ConnectionSettingsVisitor v);
}
