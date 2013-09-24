/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.j2ee;

import com.eas.designer.explorer.j2ee.tomcat.TomcatWebAppManager;
import com.eas.designer.explorer.project.PlatypusProjectImpl;
import org.netbeans.modules.j2ee.deployment.devmodules.api.InstanceRemovedException;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;

/**
 * Provides concrete web application configurator. 
 * @author vv
 */
public class WebAppManagerFactory {
    
    private static WebAppManagerFactory instance = new WebAppManagerFactory();
    
    private WebAppManagerFactory() {
    }
    
    /**
     * Gets factory instance.
     * @return WebAppConfiguratorFactory instance
     */
    public static WebAppManagerFactory getInstance() {
        return instance;
    }
    
    /**
     * Creates a new configurator instance.
     * @param aProject
     * @param aServerId
     * @return 
     */
    public WebAppManager createWebAppManager(PlatypusProjectImpl aProject, J2eeModuleProvider aJmp) throws InstanceRemovedException {
        WebAppManager webAppConfigurator = null;
        switch (aJmp.getServerID()) {
            case TomcatWebAppManager.TOMCAT_SERVER_ID :
              webAppConfigurator = new TomcatWebAppManager(aProject, aJmp.getServerInstanceID());
            break;             
        }
        return webAppConfigurator;
    }
    
}
