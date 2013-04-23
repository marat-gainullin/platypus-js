/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.eas.client.ClientConstants;
import com.eas.client.settings.DbConnectionSettings;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;

/**
 *
 * @author ml
 */
public class ServerConfig {

    public static final String APP_PATH_CMD_PARAM_NAME = "applicationpath";
    public static final String APP_PATH_CMD_PARAM_NAME1 = "ap";
    public static final String APPELEMENT_CONF_PARAM_NAME = "appelement";
    public static final String MODULES_PARAM = "modules";
    protected String appElementId = null;
    protected String appPath = null;
    protected Set<ModuleConfig> moduleConfigs;
    protected DbConnectionSettings dbSettings;

    public static ServerConfig parse(ServletConfig aConfig) throws Exception {
        return new ServerConfig(aConfig);
    }

    private ServerConfig(ServletConfig aConfig) throws Exception {
        Enumeration<String> paramNames = aConfig.getServletContext().getInitParameterNames();
        if (paramNames != null && paramNames.hasMoreElements()) {
            Map<String, BufConfig> hmConfigs = new HashMap<>();
            String dbUrl = null;
            String dbSchema = null;
            String dbUser = null;
            String dbPsw = null;
            String dbDialect = null;
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                String[] pNames = paramName.split("\\.");
                if (paramName != null) {
                    String paramValue = aConfig.getServletContext().getInitParameter(paramName);
                    if (ServerConfig.MODULES_PARAM.equals(pNames[0])) {
                        if (!pNames[1].isEmpty()) {
                            if (!hmConfigs.containsKey(pNames[1])) {
                                hmConfigs.put(pNames[1], new BufConfig());
                            }
                            if (!pNames[2].isEmpty()) {
                                if (pNames[2].equalsIgnoreCase(ModuleConfig.MODULE_LOAD_ON_STARTUP_PARAM_NAME)) {
                                    hmConfigs.get(pNames[1]).setLoadOnStartup(Boolean.valueOf(paramValue));
                                } else if (pNames[2].equalsIgnoreCase(ModuleConfig.MODULE_ACCEPTOR_PARAM_NAME)) {
                                    hmConfigs.get(pNames[1]).setAcceptor(Boolean.valueOf(paramValue));
                                } else if (pNames[2].equalsIgnoreCase(ModuleConfig.MODULE_STATELESS_PARAM_NAME)) {
                                    hmConfigs.get(pNames[1]).setStateless(Boolean.valueOf(paramValue));
                                } else if (pNames[2].equalsIgnoreCase(ModuleConfig.MODULE_ACCEPT_PROTOCOL_NAME_PARAM_NAME)) {
                                    hmConfigs.get(pNames[1]).setAcceptProtocol(paramValue);
                                } else if (pNames[2].equalsIgnoreCase(ModuleConfig.MODULE_ID_PARAM_NAME)) {
                                    hmConfigs.get(pNames[1]).setModuleId(paramValue);
                                }
                            }
                        }
                    } else {
                        if (ClientConstants.DB_CONNECTION_URL_PROP_NAME.equalsIgnoreCase(pNames[0])) {
                            dbUrl = paramValue;
                        } else if (ClientConstants.DB_CONNECTION_USER_PROP_NAME.equalsIgnoreCase(pNames[0])) {
                            dbUser = paramValue;
                        } else if (ClientConstants.DB_CONNECTION_PASSWORD_PROP_NAME.equalsIgnoreCase(pNames[0])) {
                            dbPsw = paramValue;
                        } else if (ClientConstants.DB_CONNECTION_SCHEMA_PROP_NAME.equalsIgnoreCase(pNames[0])) {
                            dbSchema = paramValue;
                        } else if (ClientConstants.DB_CONNECTION_DIALECT_PROP_NAME.equalsIgnoreCase(pNames[0])) {
                            dbDialect = paramValue;
                        } else if (APPELEMENT_CONF_PARAM_NAME.equalsIgnoreCase(pNames[0])) {
                            appElementId = paramValue;
                        } else if (APP_PATH_CMD_PARAM_NAME.equalsIgnoreCase(pNames[0]) || APP_PATH_CMD_PARAM_NAME1.equalsIgnoreCase(pNames[0])) {
                            appPath = paramValue;
                        }
                    }
                }
            }
            if (dbUrl != null && dbSchema != null && dbDialect != null) {// There are cases, when url is container's resource name, and so we don't need user and password.
                dbSettings = new DbConnectionSettings(dbUrl, dbSchema, dbUser, dbPsw, dbDialect);
                if (appPath != null) {
                    dbSettings.setApplicationPath(Paths.get(appPath).isAbsolute() ? appPath : aConfig.getServletContext().getRealPath(appPath));
                }
            } else {
                String msg = String.format("Required settings missing. %s, %s and %s are required", ClientConstants.DB_CONNECTION_URL_PROP_NAME, ClientConstants.DB_CONNECTION_SCHEMA_PROP_NAME, ClientConstants.DB_CONNECTION_DIALECT_PROP_NAME);
                Logger.getLogger(ServerConfig.class.getName()).severe(msg);
                throw new Exception(msg);
            }
            moduleConfigs = new HashSet<>();
            for (Map.Entry<String, BufConfig> me : hmConfigs.entrySet()) {
                BufConfig bc = me.getValue();
                if (bc.getModuleId() != null && !bc.getModuleId().isEmpty()) {
                    moduleConfigs.add(new ModuleConfig(
                            bc.isLoadOnStartup(),
                            bc.isStateless(),
                            bc.isAcceptor(),
                            bc.getAcceptProtocol(),
                            bc.getModuleId()));
                } else {
                    String msg = "[" + me.getKey() + "]" + " module has " + "bad configuration. Missing value of '" + ModuleConfig.MODULE_ID_PARAM_NAME + "' parameter";
                    Logger.getLogger(ServerConfig.class.getName()).severe(msg);
                }
            }
        }
    }

    public String getAppElementId() {
        return appElementId;
    }

    public DbConnectionSettings getDbSettings() {
        return dbSettings;
    }

    public Set<ModuleConfig> getModuleConfigs() {
        return moduleConfigs;
    }

    private class BufConfig {

        protected boolean loadOnStartup = false;
        protected boolean stateless = false;
        protected boolean acceptor = false;
        protected String acceptProtocol;
        protected String moduleId;

        public BufConfig() {
        }

        public void setAcceptProtocol(String anAcceptProtocol) {
            acceptProtocol = anAcceptProtocol;
        }

        public void setAcceptor(boolean isAcceptor) {
            acceptor = isAcceptor;
        }

        public void setLoadOnStartup(boolean isLoadOnStartup) {
            loadOnStartup = isLoadOnStartup;
        }

        public void setModuleId(String aModuleId) {
            moduleId = aModuleId;
        }

        public void setStateless(boolean isStateless) {
            stateless = isStateless;
        }

        public String getAcceptProtocol() {
            return acceptProtocol;
        }

        public boolean isAcceptor() {
            return acceptor;
        }

        public boolean isLoadOnStartup() {
            return loadOnStartup;
        }

        public String getModuleId() {
            return moduleId;
        }

        public boolean isStateless() {
            return stateless;
        }
    }
}