/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.j2ee;

import com.eas.designer.explorer.platform.EmptyPlatformHomePathException;
import com.eas.designer.explorer.platform.PlatypusPlatform;
import com.eas.designer.explorer.project.PlatypusProject;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipException;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment.DeploymentException;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleBase;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * A tool to prepare and deploy the Platypus web module.
 *
 * @author vv
 */
public class PlatypusWebModuleManager {

    protected static final String WAR_FILE_NAME = "PlatypusServlet.war"; //NOI18N
    protected static final String WEB_DESCRIPTOR_FILE_NAME = "web.xml"; //NOI18N
    protected static final String START_PAGE_FILE_NAME = "index.html"; //NOI18N
    protected static final String LOGIN_PAGE_FILE_NAME = "login.html"; //NOI18N
    protected static final String LOGIN_FAIL_PAGE_FILE_NAME = "loginFail.html"; //NOI18N
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
     * @return URL to open in browser
     *
     */
    public String run(boolean isDebug) {
        J2eeModuleProvider jmp = project.getLookup().lookup(J2eeModuleProvider.class);
        J2eeModuleBase mb = project.getLookup().lookup(J2eeModuleBase.class);
        String webAppRunUrl = null;
        if (jmp != null) {
            if (jmp.getServerID() == null || jmp.getServerID().isEmpty()) {
                project.getOutputWindowIO().getOut().println("Application server is not set. Check J2EE Server settings at Project's properties.");
                return null;
            }
            if (mb.getUrl() == null || mb.getUrl().isEmpty()) {
                project.getOutputWindowIO().getOut().println("J2EE Server context is not configured for the project.");
                return null;
            }
            try {
                prepareWebApplication();
                configureWebApplication(jmp);
                webAppRunUrl = Deployment.getDefault().deploy(jmp, Deployment.Mode.RUN, null, null, false);
                String deployResultMessage = String.format("Web application deployed. URL: %s", webAppRunUrl);
                Logger.getLogger(PlatypusWebModuleManager.class.getName()).log(Level.INFO, deployResultMessage);
                project.getOutputWindowIO().getOut().println(deployResultMessage);

            } catch (IOException | EmptyPlatformHomePathException | DeploymentException ex) {
                ErrorManager.getDefault().notify(ex);
            }
        } else {
            throw new IllegalStateException("J2eeModuleProvider instance should be in the project's lookup.");
        }
        return webAppRunUrl;
    }

    /**
     * Creates an web application skeleton if not created yet.
     */
    protected void prepareWebApplication() throws IOException, EmptyPlatformHomePathException {
        webAppDir = createFolderIfNotExists(projectDir, PlatypusWebModule.WEB_DIRECTORY);
        FileObject platformBinDir = FileUtil.toFileObject(PlatypusPlatform.getPlatformBinDirectory());
        FileObject referenceWar = platformBinDir.getFileObject(WAR_FILE_NAME);
        if (referenceWar != null) {
            FileObject war = FileUtil.getArchiveRoot(referenceWar);
            if (war != null) {
                copyContent(war, webAppDir);
            } else {
                throw new ZipException("Error reading web application archive.");
            }
        } else {
            throw new FileNotFoundException("Web application archive is not found at: " + referenceWar.getPath());
        }
        createFolderIfNotExists(webAppDir, PlatypusWebModule.META_INF_DIRECTORY);
    }

    /**
     * Recursively copies web application structure from war archive. If
     * destination file exists it isn't overwritten.
     *
     * @throws IOException if some I/O problem occurred.
     */
    protected void copyContent(FileObject sourceDir, FileObject targetDir) throws IOException {
        assert sourceDir.isFolder() && targetDir.isFolder();
        FileObject targetFile;
        for (FileObject childFile : sourceDir.getChildren()) {
            if (childFile.isFolder()) {
                targetFile = targetDir.getFileObject(childFile.getName(), childFile.getExt());
                if (targetFile == null) {
                    targetFile = targetDir.createFolder(childFile.getNameExt());
                }
                assert targetFile.isFolder();
                copyContent(childFile, targetFile);
            } else {
                copyIfNotExists(targetDir, childFile);
            }
        }
    }

    private FileObject copyIfNotExists(FileObject dir, FileObject file) throws IOException {
        FileObject target = dir.getFileObject(file.getNameExt());
        if (target == null) {
            target = file.copy(dir, file.getName(), file.getExt());
        }
        return target;
    }

    private FileObject createFolderIfNotExists(FileObject dir, String name) throws IOException {
        FileObject fo = dir.getFileObject(name);
        if (fo == null) {
            fo = dir.createFolder(name);
        }
        return fo;
    }

    /**
     * Configures an web application.
     *
     * @param aJmp Web Module
     */
    protected void configureWebApplication(J2eeModuleProvider aJmp) {
        WebAppConfigurator webAppConfigurator = WebAppConfiguratorFactory.getInstance().createWebConfigurator(project, aJmp.getServerID());
        if (webAppConfigurator != null) {
            webAppConfigurator.configure();
        } else {
            String errorMessage = String.format("Web application configuration is not supported for application server: %s", aJmp.getServerID());
            Logger.getLogger(PlatypusWebModuleManager.class.getName()).log(Level.WARNING, errorMessage);
            project.getOutputWindowIO().getErr().println(errorMessage);
        }
    }
}
