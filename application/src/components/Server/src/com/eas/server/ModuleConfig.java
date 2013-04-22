/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import java.util.prefs.Preferences;

/**
 *
 * @author mg
 */
public class ModuleConfig {

    public static final String MODULE_LOAD_ON_STARTUP_PARAM_NAME = "load-on-startup";
    public static final String MODULE_STATELESS_PARAM_NAME = "stateless";
    public static final String MODULE_ACCEPTOR_PARAM_NAME = "acceptor";
    public static final String MODULE_ACCEPT_PROTOCOL_NAME_PARAM_NAME = "accept-protocol";
    public static final String MODULE_ID_PARAM_NAME = "module-id";
        protected boolean loadOnStartup = false;
    protected boolean stateless = false;
    protected boolean acceptor = false;
    protected String acceptProtocol;
    protected String moduleId;

    public ModuleConfig(Preferences aPrefs) {
        if (aPrefs != null) {
            // read config from preferences
            loadOnStartup = aPrefs.getBoolean(MODULE_LOAD_ON_STARTUP_PARAM_NAME, false);
            stateless = aPrefs.getBoolean(MODULE_STATELESS_PARAM_NAME, false);
            acceptor = aPrefs.getBoolean(MODULE_ACCEPTOR_PARAM_NAME, false);
            acceptProtocol = aPrefs.get(MODULE_ACCEPT_PROTOCOL_NAME_PARAM_NAME, "");
            moduleId = aPrefs.get(MODULE_ID_PARAM_NAME, null);
        }
    }

    public ModuleConfig(boolean isLoadOnStartup, boolean isStateless,
                        boolean isAcceptor, String anAcceptProtocol, String aModuleId) {
        loadOnStartup = isLoadOnStartup;
        stateless = isStateless;
        acceptor = isAcceptor;
        acceptProtocol = anAcceptProtocol;
        moduleId = aModuleId;
    }

    public boolean isLoadOnStartup() {
        return loadOnStartup;
    }

    public boolean isStateless() {
        return stateless;
    }

    public boolean isAcceptor() {
        return acceptor;
    }

    public String getAcceptProtocol() {
        return acceptProtocol;
    }

    public String getModuleId() {
        return moduleId;
    }
}
