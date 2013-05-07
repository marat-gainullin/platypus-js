/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.j2ee;

import com.eas.designer.explorer.project.PlatypusProject;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleImplementation2;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;

/**
 * Configures web application for Tomcat 7.
 * @author vv
 */
public class TomcatWebAppConfigurator implements WebAppConfigurator {

    protected final PlatypusProject project;
    
    public TomcatWebAppConfigurator(PlatypusProject aProject) {
        project = aProject;
    }
    
    @Override
    public void configure() {
        Logger.getLogger(getClass().getName()).log(Level.INFO, "Starting configuring an application for Tomcat.");
    }
    
    protected J2eeModuleProvider getWebMobdule() {
        return project.getLookup().lookup(J2eeModuleProvider.class);
    }
    
    protected J2eeModuleImplementation2 getWebMobduleImpl() {
        return project.getLookup().lookup(J2eeModuleImplementation2.class);
    }
}
