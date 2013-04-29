/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.j2ee;

import com.eas.designer.explorer.project.PlatypusProject;

/**
 *
 * @author vv
 */
public class WebAppConfiguratorFactory {
    
    private static final String TOMCAT_SERVER_ID = "Tomcat"; //NOI18N
    
    private static WebAppConfiguratorFactory instance = new WebAppConfiguratorFactory();
    
    private WebAppConfiguratorFactory() {
    }
    
    public static WebAppConfiguratorFactory getInstance() {
        return instance;
    }
    
    public WebAppConfigurator createWebConfigurator(PlatypusProject aProject, String aServerId) {
        WebAppConfigurator webAppConfigurator = null;
        switch (aServerId) {
            case TOMCAT_SERVER_ID :
              webAppConfigurator = new TomcatWebAppConfigurator(aProject);
            break;             
        }
        return webAppConfigurator;
    }
    
}
