/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.j2ee.tomcat;

import com.eas.designer.explorer.j2ee.PlatypusWebModule;
import com.eas.designer.explorer.j2ee.WebAppConfigurator;
import com.eas.designer.explorer.project.PlatypusProject;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleImplementation2;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;

/**
 * Configures web application for Tomcat 7.
 * @author vv
 */
public class TomcatWebAppConfigurator implements WebAppConfigurator {

    public final String CONTEXT_FILE_NAME = "context.xml"; //NOI18N 
    protected final PlatypusProject project;
    
    public TomcatWebAppConfigurator(PlatypusProject aProject) {
        project = aProject;
    }
    
    @Override
    public void configure() {
        try {
            FileObject contexFileObject = getWebMobdule().getMetaInfDir().getFileObject(CONTEXT_FILE_NAME);

            Logger.getLogger(getClass().getName()).log(Level.INFO, "Starting configuring an application for Tomcat.");
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }
    
    protected PlatypusWebModule getWebMobdule() {
        return project.getLookup().lookup(PlatypusWebModule.class);
    }
    
    protected J2eeModuleImplementation2 getWebMobduleImpl() {
        return project.getLookup().lookup(J2eeModuleImplementation2.class);
    }
}
