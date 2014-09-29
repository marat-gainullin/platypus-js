/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.eas.util.StringUtils;
import java.util.*;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;

/**
 * Servlet configuration parser.
 *
 * @author ml
 */
public class ServerConfig {

    // configuration parameters
    public static final String APPELEMENT_CONF_PARAM = "appelement";
    public static final String TASKS_PARAM = "tasks";
    public static final String APP_URL_CONF_PARAM = "url";
    public static final String DEF_DATASOURCE_CONF_PARAM = "default-datasource";
    //
    protected String appElementName;
    protected String url;
    protected String defaultDatasourceName;
    protected Set<String> tasks = new HashSet<>();

    public static ServerConfig parse(ServletConfig aConfig) throws Exception {
        return new ServerConfig(aConfig);
    }

    private ServerConfig(ServletConfig aConfig) throws Exception {
        Enumeration<String> paramNames = aConfig.getServletContext().getInitParameterNames();
        if (paramNames != null && paramNames.hasMoreElements()) {
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                if (paramName != null) {
                    String paramValue = aConfig.getServletContext().getInitParameter(paramName);
                    if (TASKS_PARAM.equals(paramName)) {
                        paramValue = paramValue.replaceAll(",", " ");
                        tasks.addAll(StringUtils.split(paramValue, " "));
                    } else if (APP_URL_CONF_PARAM.equalsIgnoreCase(paramName)) {
                        url = paramValue;
                    } else if (DEF_DATASOURCE_CONF_PARAM.equalsIgnoreCase(paramName)) {
                        defaultDatasourceName = paramValue;
                    } else if (APPELEMENT_CONF_PARAM.equalsIgnoreCase(paramName)) {
                        appElementName = paramValue;
                    }
                }
            }
            if (url == null) {
                String msg = String.format("Required settings missing. %s is required", APP_URL_CONF_PARAM);
                Logger.getLogger(ServerConfig.class.getName()).severe(msg);
                throw new Exception(msg);
            }
        }
    }

    public String getAppElementName() {
        return appElementName;
    }

    public String getUrl() {
        return url;
    }

    public String getDefaultDatasourceName() {
        return defaultDatasourceName;
    }

    public Set<String> getTasks() {
        return tasks;
    }
}
