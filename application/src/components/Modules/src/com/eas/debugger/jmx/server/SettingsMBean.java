/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.debugger.jmx.server;

/**
 *
 * @author mg
 */
public interface SettingsMBean {

    public static final String SETTINGS_MBEAN_NAME = "Platypus debugger:name=Settings";

    public String getSettingsData() throws Exception;
}
