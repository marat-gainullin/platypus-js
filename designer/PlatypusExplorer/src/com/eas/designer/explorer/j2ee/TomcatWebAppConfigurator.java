/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.j2ee;

import com.eas.designer.explorer.project.PlatypusProject;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        Logger.getLogger(getClass().getName()).log(Level.INFO, "Starting configuring an application for Tomcat."); //NOI18N
    }
    
}
