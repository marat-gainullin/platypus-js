/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.j2ee;

import com.eas.designer.explorer.platform.EmptyPlatformHomePathException;
import com.eas.designer.explorer.platform.PlatypusPlatform;
import com.eas.designer.explorer.project.PlatypusProject;
import com.sun.istack.internal.logging.Logger;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment.DeploymentException;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.openide.ErrorManager;
import org.openide.awt.HtmlBrowser;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * A tool to prepare and deploy the Platypus web module.
 *
 * @author vv
 */
public class PlatypusWebModuleManager {

    private static final String WAR_FILE_NAME = "PlatypusServlet.war"; //NOI18N
    private static final String WEB_DESCRIPTOR_FILE_NAME = "web.xml"; //NOI18N
    protected final PlatypusProject project;
    protected final FileObject projectDir;
    protected FileObject webAppDir;
    protected FileObject webInfDir;
    protected FileObject metaInfDir;

    public PlatypusWebModuleManager(PlatypusProject aProject) {
        project = aProject;
        projectDir = aProject.getProjectDirectory();
    }

    /**
     * Runs the web application.
     *
     * @param isDebug true if debug mode to be activated
     * @param isOpenBrowser true if default browser should be opened after
     * running the application.
     */
    public void run(boolean isDebug, boolean isOpenBrowser) {
        J2eeModuleProvider jmp = project.getLookup().lookup(J2eeModuleProvider.class);
        if (jmp != null) {
            try {
                prepareWebApplication();
                configureWebApplication(jmp);
                String url = Deployment.getDefault().deploy(jmp, Deployment.Mode.RUN, null, "", false);
                String deployResultMessage = String.format("Web application deployed. URL: %s", url); //NOI18N
                Logger.getLogger(getClass()).log(Level.INFO, deployResultMessage);
                project.getOutputWindowIO().getOut().println(deployResultMessage);
                if (isOpenBrowser) {
                    HtmlBrowser.URLDisplayer.getDefault().showURL(new URL(url));
                }
            } catch (IOException | EmptyPlatformHomePathException | DeploymentException ex) {
                ErrorManager.getDefault().notify(ex);
            }
        } else {
            throw new IllegalStateException("J2eeModuleProvider instance should be in the project's lookup."); //NOI18N
        }
    }

    /**
     * Creates an web application skeleton if not created yet.
     */
    protected void prepareWebApplication() throws IOException, EmptyPlatformHomePathException {
        prepareDirectories();
        prepareContent();
    }

    private void prepareDirectories() throws IOException {
        webAppDir = projectDir.getFileObject(PlatypusWebModule.WEB_DIRECTORY);
        if (webAppDir == null) {
            webAppDir = projectDir.createFolder(PlatypusWebModule.WEB_DIRECTORY);
        }
        webInfDir = webAppDir.getFileObject(PlatypusWebModule.WEB_INF_DIRECTORY);
        if (webInfDir == null) {
            webInfDir = webAppDir.createFolder(PlatypusWebModule.WEB_INF_DIRECTORY);
        }
        metaInfDir = webAppDir.getFileObject(PlatypusWebModule.META_INF_DIRECTORY);
        if (metaInfDir == null) {
            metaInfDir = webAppDir.createFolder(PlatypusWebModule.META_INF_DIRECTORY);
        }
    }

    private void prepareContent() throws IOException, EmptyPlatformHomePathException {
        File platformBinDir = PlatypusPlatform.getPlatformBinDirectory();
        File referenceWar = new File(platformBinDir, WAR_FILE_NAME);
        if (referenceWar.exists()) {
            FileObject warFileObject = FileUtil.getArchiveRoot(FileUtil.toFileObject(referenceWar));
            if (FileUtil.isArchiveFile(warFileObject)) {
                copyIfNotExists(warFileObject.getFileObject(WEB_DESCRIPTOR_FILE_NAME).getFileObject(WEB_DESCRIPTOR_FILE_NAME), webInfDir);
            }
        } else {
            throw new FileNotFoundException();
        }
    }

    private FileObject copyIfNotExists(FileObject file, FileObject dir) throws IOException {
        FileObject target = dir.getFileObject(file.getNameExt());
        if (target == null) {
            target = file.copy(dir, file.getName(), file.getExt());
        }
        return target;
    }

    /**
     * Configures an web application.
     *
     * @param aJmp Web Module
     */
    protected void configureWebApplication(J2eeModuleProvider aJmp) {
        if (aJmp.getServerInstanceID() != null && !aJmp.getServerInstanceID().isEmpty()) {
            WebAppConfigurator webAppConfigurator = WebAppConfiguratorFactory.getInstance().createWebConfigurator(project, aJmp.getServerID());
            if (webAppConfigurator != null) {
                webAppConfigurator.configure();
            } else {
                String errorMessage = String.format("Web application configuration is not supported for application server: %s", aJmp.getServerID()); //NOI18N
                Logger.getLogger(getClass()).log(Level.WARNING, errorMessage);
                project.getOutputWindowIO().getErr().println(errorMessage);
            }
        } else {
            String errorMessage = "Application server is not set. Check J2EE Server settings at Project's properties"; //NOI18N
            Logger.getLogger(getClass()).log(Level.WARNING, errorMessage);
            project.getOutputWindowIO().getErr().println(errorMessage);
        }
    }
}
