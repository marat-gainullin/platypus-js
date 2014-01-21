/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.debugger.jmx.server;

import com.eas.client.settings.ConnectionSettings2XmlDom;
import com.eas.client.settings.ConnectionSettings;
import com.eas.client.Client;
import com.eas.xml.dom.XmlDom2String;
import org.w3c.dom.Document;

/**
 *
 * @author mg
 */
public class Settings implements SettingsMBean {

    protected Client client;

    public Settings(Client aClient) {
        super();
        client = aClient;
    }

    @Override
    public String getSettingsData() throws Exception {
        return "";
        /*
         EasSettings settings = client.getSettings();
         if (settings != null) {
         Document doc = ConnectionSettings2XmlDom.settingsToDocument(settings);
         return XmlDom2String.transform(doc);
         } else {
         return null;
         }
         */
    }

}
