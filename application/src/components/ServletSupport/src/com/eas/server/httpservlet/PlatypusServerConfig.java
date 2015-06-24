/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.httpservlet;

import java.util.*;
import javax.servlet.ServletConfig;

/**
 * Servlet configuration parser.
 *
 * @author ml
 */
public class PlatypusServerConfig {

    // configuration parameters
    public static final String APPELEMENT_CONF_PARAM = "appelement";
    public static final String DEF_DATASOURCE_CONF_PARAM = "default-datasource";
    public static final String MAX_JDBC_THREADS_CONF_PARAM = "max-jdbc-threads";
    public static final String MAX_BIO_THREADS_CONF_PARAM = "max-bio-threads";
    public static final String WATCH_CONF_PARAM = "watch-source";
    //
    protected String appElementName;
    protected String defaultDatasourceName;
    protected int maximumJdbcThreads = 25;
    protected int maximumBIOTreads = 25;
    protected boolean watch = true;

    public static PlatypusServerConfig parse(ServletConfig aConfig) throws Exception {
        return new PlatypusServerConfig(aConfig);
    }

    private PlatypusServerConfig(ServletConfig aConfig) throws Exception {
        Enumeration<String> paramNames = aConfig.getServletContext().getInitParameterNames();
        if (paramNames != null && paramNames.hasMoreElements()) {
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                if (paramName != null) {
                    String paramValue = aConfig.getServletContext().getInitParameter(paramName);
                    if (MAX_JDBC_THREADS_CONF_PARAM.equals(paramName)) {
                        maximumJdbcThreads = Double.valueOf(paramValue).intValue();
                    } else if (MAX_BIO_THREADS_CONF_PARAM.equalsIgnoreCase(paramName)) {
                        maximumBIOTreads = Double.valueOf(paramValue).intValue();
                    } else if (DEF_DATASOURCE_CONF_PARAM.equalsIgnoreCase(paramName)) {
                        defaultDatasourceName = paramValue;
                    } else if (APPELEMENT_CONF_PARAM.equalsIgnoreCase(paramName)) {
                        appElementName = paramValue;
                    } else if (WATCH_CONF_PARAM.equalsIgnoreCase(paramName)) {
                        watch = Boolean.valueOf(paramValue);
                    }
                }
            }
        }
    }

    public String getAppElementName() {
        return appElementName;
    }

    public String getDefaultDatasourceName() {
        return defaultDatasourceName;
    }

    public int getMaximumJdbcThreads() {
        return maximumJdbcThreads;
    }

    public int getMaximumBIOTreads() {
        return maximumBIOTreads;
    }

    public boolean isWatch() {
        return watch;
    }
    
}
