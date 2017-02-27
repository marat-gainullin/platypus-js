package com.eas.client.settings;

/**
 *
 * @author mg
 */
public class ConnectionSettings {

    protected String name;
    protected String url = "";
    protected boolean editable = true;

    public ConnectionSettings() {
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

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean aValue) {
        editable = aValue;
    }

}
