/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.eas.client.settings.DbConnectionSettings;
import com.eas.client.settings.PlatypusConnectionSettings;

/**
 *
 * @author mg
 */
public interface ConnectionSettingsVisitor {

    public void visit(DbConnectionSettings aSettings);

    public void visit(PlatypusConnectionSettings aSettings);
}
