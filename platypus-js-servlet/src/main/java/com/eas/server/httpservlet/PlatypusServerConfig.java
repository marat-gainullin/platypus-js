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
    public static final String RUN_ON_STARTUP_MODULE_PARAM = "run-on-startup-module";
    public static final String DEF_DATASOURCE_CONF_PARAM = "default-datasource";
    public static final String GLOBAL_API_CONF_PARAM = "global-api";
    public static final String SOURCE_PATH_CONF_PARAM = "source-path";
    public static final String MAX_JDBC_THREADS_CONF_PARAM = "max-jdbc-threads";
    public static final String MAX_BIO_THREADS_CONF_PARAM = "max-bio-threads";
    public static final String MAX_LPC_THREADS_CONF_PARAM = "max-lpc-threads";
    public static final String MAX_SPACES_CONF_PARAM = "max-script-contexts";
    public static final String LPC_QUEUE_SIZE_CONF_PARAM = "lpc-queue-size";
    public static final String WATCH_CONF_PARAM = "watch";
    //
    protected String appElementName;
    protected String runOnStartupModule;
    protected String defaultDatasourceName;
    protected String sourcePath;
    protected boolean globalAPI;
    protected int maximumJdbcThreads = 25;
    protected int maximumBIOTreads = 25;
    protected int maximumSpaces = (Runtime.getRuntime().availableProcessors() + 1) * 2;
    protected int maximumLpcQueueSize = Integer.MAX_VALUE;
    protected int maximumLpcThreads = Runtime.getRuntime().availableProcessors() + 1;
    protected boolean watch = false;

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
                    } else if (MAX_SPACES_CONF_PARAM.equalsIgnoreCase(paramName)) {
                        maximumSpaces = Double.valueOf(paramValue).intValue();
                    } else if (MAX_LPC_THREADS_CONF_PARAM.equalsIgnoreCase(paramName)) {
                        maximumLpcThreads = Double.valueOf(paramValue).intValue();
                    } else if (LPC_QUEUE_SIZE_CONF_PARAM.equalsIgnoreCase(paramName)) {
                        maximumLpcQueueSize = Double.valueOf(paramValue).intValue();
                    } else if (DEF_DATASOURCE_CONF_PARAM.equalsIgnoreCase(paramName)) {
                        defaultDatasourceName = paramValue;
                    } else if (SOURCE_PATH_CONF_PARAM.equalsIgnoreCase(paramName)) {
                        sourcePath = paramValue;
                    } else if (GLOBAL_API_CONF_PARAM.equalsIgnoreCase(paramName)) {
                        globalAPI = Boolean.valueOf(paramValue);
                    } else if (APPELEMENT_CONF_PARAM.equalsIgnoreCase(paramName)) {
                        appElementName = paramValue;
                    if (appElementName.toLowerCase().endsWith(".js")) {
                        appElementName = appElementName.substring(0, appElementName.length() - 3);
                    }
                    } else if (RUN_ON_STARTUP_MODULE_PARAM.equalsIgnoreCase(paramName)) {
                        runOnStartupModule = paramValue;
                    if (runOnStartupModule.toLowerCase().endsWith(".js")) {
                        runOnStartupModule = runOnStartupModule.substring(0, runOnStartupModule.length() - 3);
                    }
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

    public String getRunOnStartupModuleName() {
        return runOnStartupModule;
    }
    
    public String getDefaultDatasourceName() {
        return defaultDatasourceName;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public boolean isGlobalAPI() {
        return globalAPI;
    }

    public int getMaximumJdbcThreads() {
        return maximumJdbcThreads;
    }

    public int getMaximumBIOTreads() {
        return maximumBIOTreads;
    }

    public int getMaximumSpaces() {
        return maximumSpaces;
    }

    public int getMaximumLpcQueueSize() {
        return maximumLpcQueueSize;
    }

    public int getMaximumLpcThreads() {
        return maximumLpcThreads;
    }

    public boolean isWatch() {
        return watch;
    }
}
