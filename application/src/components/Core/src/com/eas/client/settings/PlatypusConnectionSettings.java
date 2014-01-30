/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.settings;

import com.eas.client.ConnectionSettingsVisitor;

/**
 *
 * @author pk
 */
public class PlatypusConnectionSettings extends ConnectionSettings {

    public static transient final String PLATYPUS_CONNECTION_SETTINGS_FILE_NAME = "PlatypusConnectionSettings.xml";

    public PlatypusConnectionSettings() {
        super();
    }

    @Override
    public void accept(ConnectionSettingsVisitor v) {
        v.visit(this);
    }
}
