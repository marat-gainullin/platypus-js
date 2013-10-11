/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.connection;

import com.eas.client.settings.ConnectionSettings2XmlDom;
import com.eas.client.settings.DbConnectionSettings;
import com.eas.client.settings.EasSettings;
import com.eas.client.settings.XmlDom2ConnectionSettings;
import com.eas.designer.application.HandlerRegistration;
import com.eas.designer.application.PlatypusUtils;
import com.eas.designer.explorer.PlatypusDataObject;
import com.eas.xml.dom.Source2XmlDom;
import com.eas.xml.dom.XmlDom2String;
import java.io.OutputStream;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.CookieSet;
import org.w3c.dom.Document;

@DataObject.Registration(displayName = "#CTL_PlatypusConnectionAction", iconBase = "com/eas/designer/application/connection/connection.png", mimeType = "text/connection+xml")
@MIMEResolver.ExtensionRegistration(displayName="LBL_PlatypusConnection_file", extension="pc", mimeType="text/connection+xml")
public class PlatypusConnectionDataObject extends PlatypusDataObject {

    public static final String PROP_APP_ELEMENT_NAME = "appElementName";
    public static final String PROP_URL = "url";
    public static final String PROP_USER = "user";
    public static final String PROP_PASSWORD = "password";
    public static final String PROP_SCHEMA = "schema";
    protected DbConnectionSettings settings;

    public PlatypusConnectionDataObject(FileObject pf, MultiFileLoader loader) throws Exception {
        super(pf, loader);
        CookieSet cookies = getCookieSet();
        cookies.add(new PlatypusConnectionSupport(this));
    }

    public String getAppElementName() throws Exception {
        DbConnectionSettings lsettings = getSettings();
        return lsettings.getName();
    }

    public void setAppElementName(String aValue) throws Exception {
        String oldValue = getAppElementName();
        DbConnectionSettings lsettings = getSettings();
        lsettings.setName(aValue);
        firePropertyChange(PROP_APP_ELEMENT_NAME, oldValue, aValue);
    }

    @Override
    protected void clientChanged() {
    }

    @Override
    protected void validateModel() throws Exception {
        // no op
    }
        
    public String getUrl() throws Exception {
        DbConnectionSettings lsettings = getSettings();
        return lsettings.getUrl();
    }

    public String getSchema() throws Exception {
        DbConnectionSettings lsettings = getSettings();
        return lsettings.getSchema();
    }

    public String getUser() throws Exception {
        DbConnectionSettings lsettings = getSettings();
        return lsettings.getUser();
    }

    public String getPassword() throws Exception {
        DbConnectionSettings lsettings = getSettings();
        return lsettings.getPassword();
    }

    public void setPassword(String aValue) throws Exception {
        String oldValue = getPassword();
        DbConnectionSettings lsettings = getSettings();
        lsettings.setPassword(aValue);
        firePropertyChange(PROP_PASSWORD, oldValue, aValue);
    }

    public void setUrl(String aValue) throws Exception {
        String oldValue = getUrl();
        DbConnectionSettings lsettings = getSettings();
        lsettings.setUrl(aValue);
        firePropertyChange(PROP_URL, oldValue, aValue);
    }

    public void setSchema(String aValue) throws Exception {
        String oldValue = getSchema();
        DbConnectionSettings lsettings = getSettings();
        lsettings.setSchema(aValue);
        firePropertyChange(PROP_SCHEMA, oldValue, aValue);
    }

    public void setUser(String aValue) throws Exception {
        String oldValue = getUser();
        DbConnectionSettings lsettings = getSettings();
        lsettings.setUser(aValue);
        firePropertyChange(PROP_USER, oldValue, aValue);
    }

    public void readSettings() throws Exception {
        FileObject pf = getPrimaryFile();
        settings = (DbConnectionSettings)XmlDom2ConnectionSettings.document2Settings(Source2XmlDom.transform(pf.asText(PlatypusUtils.COMMON_ENCODING_NAME)));
    }

    public DbConnectionSettings getSettings() throws Exception {
        if (settings == null) {
            readSettings();
        }
        return settings;
    }

    public void shrink() {
        settings = null;
    }

    @Override
    protected void dispose() {
        getLookup().lookup(PlatypusConnectionSupport.class).closeAllViews();
        super.dispose();
    }

    public void saveSettings() throws Exception {
        EasSettings lsettings = getSettings();
        Document doc = ConnectionSettings2XmlDom.settingsToDocument(lsettings);
        String sData = XmlDom2String.transform(doc);
        FileObject fo = getPrimaryFile();
        try (OutputStream out = fo.getOutputStream()) {
            byte[] data = sData.getBytes(PlatypusUtils.COMMON_ENCODING_NAME);
            out.write(data);
            out.flush();
        }
    }
}
