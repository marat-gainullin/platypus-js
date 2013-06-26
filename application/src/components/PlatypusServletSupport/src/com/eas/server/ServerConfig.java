/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.eas.client.ClientConstants;
import com.eas.client.settings.DbConnectionSettings;
import com.eas.util.StringUtils;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;

/**
 *
 * @author ml
 */
public class ServerConfig {

    public static final String APPELEMENT_CONF_PARAM_NAME = "appelement";
    public static final String TASKS_PARAM = "tasks";
    protected String appElementId;
    protected String appPath;
    protected Set<String> tasks = new HashSet<>();
    ;
    protected DbConnectionSettings dbSettings;

    public static ServerConfig parse(ServletConfig aConfig) throws Exception {
        return new ServerConfig(aConfig);
    }

    private ServerConfig(ServletConfig aConfig) throws Exception {
        Enumeration<String> paramNames = aConfig.getServletContext().getInitParameterNames();
        if (paramNames != null && paramNames.hasMoreElements()) {
            String dbUrl = null;
            String dbSchema = null;
            String dbUser = null;
            String dbPsw = null;
            String dbDialect = null;
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                if (paramName != null) {
                    String paramValue = aConfig.getServletContext().getInitParameter(paramName);
                    if (ServerConfig.TASKS_PARAM.equals(paramName)) {
                        paramValue = paramValue.replaceAll(",", " ");
                        tasks.addAll(StringUtils.split(paramValue, " "));
                    } else if (ClientConstants.DB_CONNECTION_URL_PROP_NAME.equalsIgnoreCase(paramName)) {
                        dbUrl = paramValue;
                    } else if (ClientConstants.DB_CONNECTION_USER_PROP_NAME.equalsIgnoreCase(paramName)) {
                        dbUser = paramValue;
                    } else if (ClientConstants.DB_CONNECTION_PASSWORD_PROP_NAME.equalsIgnoreCase(paramName)) {
                        dbPsw = paramValue;
                    } else if (ClientConstants.DB_CONNECTION_SCHEMA_PROP_NAME.equalsIgnoreCase(paramName)) {
                        dbSchema = paramValue;
                    } else if (ClientConstants.DB_CONNECTION_DIALECT_PROP_NAME.equalsIgnoreCase(paramName)) {
                        dbDialect = paramValue;
                    } else if (APPELEMENT_CONF_PARAM_NAME.equalsIgnoreCase(paramName)) {
                        appElementId = paramValue;
                    } else if (ClientConstants.APP_PATH_CMD_PROP_NAME.equalsIgnoreCase(paramName) || ClientConstants.APP_PATH_CMD_PROP_NAME1.equalsIgnoreCase(paramName)) {
                        appPath = paramValue;
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
        }
    }

    public String getAppElementId() {
        return appElementId;
    }

    public DbConnectionSettings getDbSettings() {
        return dbSettings;
    }

    public Set<String> getTasks() {
        return tasks;
    }
}