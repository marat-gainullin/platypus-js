/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.j2ee;

import com.eas.designer.application.PlatypusUtils;
import com.eas.designer.explorer.j2ee.dd.AppListener;
import com.eas.designer.explorer.j2ee.dd.AuthConstraint;
import com.eas.designer.explorer.j2ee.dd.ContextParam;
import com.eas.designer.explorer.j2ee.dd.FormLoginConfig;
import com.eas.designer.explorer.j2ee.dd.LoginConfig;
import com.eas.designer.explorer.j2ee.dd.MultipartConfig;
import com.eas.designer.explorer.j2ee.dd.ResourceRef;
import com.eas.designer.explorer.j2ee.dd.SecurityConstraint;
import com.eas.designer.explorer.j2ee.dd.SecurityRole;
import com.eas.designer.explorer.j2ee.dd.Servlet;
import com.eas.designer.explorer.j2ee.dd.ServletMapping;
import com.eas.designer.explorer.j2ee.dd.WebApplication;
import com.eas.designer.explorer.j2ee.dd.WebResourceCollection;
import com.eas.designer.application.project.ClientType;
import com.eas.designer.application.project.PlatypusProject;
import com.eas.designer.application.project.PlatypusProjectSettings;
import com.eas.designer.explorer.j2ee.dd.WelcomeFile;
import com.eas.designer.explorer.j2ee.tomcat.TomcatWebAppManager;
import com.eas.designer.explorer.project.PlatypusProjectImpl;
import com.eas.server.httpservlet.PlatypusServerConfig;
import com.eas.server.httpservlet.PlatypusHttpServlet;
import com.eas.server.httpservlet.PlatypusSessionsSynchronizer;
import com.eas.util.FileUtils;
import com.eas.xml.dom.XmlDom2String;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.netbeans.api.db.explorer.ConnectionManager;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

/**
 * A tool to prepare and deploy the Platypus web module.
 *
 * @author vv
 */
public class PlatypusWebModuleManager {

    public static final String WAR_FILE_NAME = "PlatypusServlet.war"; //NOI18N
    public static final String PLATYPUS_SERVLET_URL_PATTERN = "/application/*"; //NOI18N
    public static final String PLATYPUS_WEB_CLIENT_DIR_NAME = "pwc"; //NOI18N
    public static final String J2EE_RESOURCES_PACKAGE = "/com/eas/designer/explorer/j2ee/resources/"; //NOI18N
    public static final String START_PAGE_FILE_NAME = "application-start.html"; //NOI18N
    public static final String LOGIN_PAGE_FILE_NAME = "login.html"; //NOI18N
    public static final String LOGIN_FAIL_PAGE_FILE_NAME = "login-failed.html"; //NOI18N
    public static final String WEB_XML_FILE_NAME = "web.xml"; //NOI18N
    public static final String SERVLET_BEAN_NAME = "Servlet"; //NOI18N
    public static final String MULTIPART_CONFIG_BEAN_NAME = "MultipartConfig"; //NOI18N
    public static final String SERVLET_MAPPING_BEAN_NAME = "ServletMapping"; //NOI18N
    public static final String PLATYPUS_SERVLET_NAME = "PlatypusServlet"; //NOI18N
    public static final String CONTAIER_RESOURCE_SECURITY_TYPE = "Container"; //NOI18N
    public static final String PLATYPUS_WEB_RESOURCE_NAME = "platypus"; //NOI18N
    public static final String ANY_SIGNED_USER_ROLE = "*"; //NOI18N
    public static final String FORM_AUTH_METHOD = "FORM"; //NOI18N
    public static final String BASIC_AUTH_METHOD = "BASIC"; //NOI18N
    public static final long MULTIPART_MAX_FILE_SIZE = 2097152;
    public static final long MULTIPART_MAX_REQUEST_SIZE = 2165824;
    public static final long MULTIPART_MAX_FILE_THRESHOLD = 1048576;
    public static final int PLATYPUS_SERVLET_LOAD_ON_STARTUP_ORDER = 1;
    protected final PlatypusProjectImpl project;
    protected final FileObject projectDir;
//    protected FileObject webInfDir;
    protected FileObject metaInfDir;
    protected FileObject publicDir;

    public PlatypusWebModuleManager(PlatypusProjectImpl aProject) {
        super();
        project = aProject;
        projectDir = aProject.getProjectDirectory();
    }

    public void undeploy() throws Exception {
        PlatypusWebModule webModule = project.getLookup().lookup(PlatypusWebModule.class);
        assert webModule != null : "J2eeModuleProvider instance should be in the project's lookup.";

        // Crazy NetBeans architecture of "Deployment" pushs us to do this dirty hack :(
        FileObject contextXml = metaInfDir.getFileObject(TomcatWebAppManager.CONTEXT_FILE_NAME);
        try {
            webModule.forceServerChanged();
            Deployment.getDefault().undeploy(webModule, false, (String message) -> {
                if (message != null) {
                    if (message.contains("FAIL")) {// Crazy NetBeans' Tomcat manager module architecture push us to do this dirty hack :(
                        project.getOutputWindowIO().getErr().println(message);
                    } else {
                        project.getOutputWindowIO().getOut().println(message);
                    }
                }
            });
        } finally {
            if (contextXml == null) {
                FileObject recreatedContextXml = metaInfDir.getFileObject(TomcatWebAppManager.CONTEXT_FILE_NAME);
                if (recreatedContextXml != null) {
                    recreatedContextXml.delete();
                }
            }
        }
    }

    /**
     * Runs the web application.
     *
     * @param debug true if debug mode to be activated
     * @return URL to open in browser
     */
    public String start(boolean debug) {
        PlatypusWebModule webModule = project.getLookup().lookup(PlatypusWebModule.class);
        String webAppRunUrl = null;
        assert webModule != null : "J2eeModuleProvider instance should be in the project's lookup.";
        try {
            project.getSettings().load();
            metaInfDir = createFolderIfNotExists(projectDir, PlatypusWebModule.META_INF_DIRECTORY);
            publicDir = createFolderIfNotExists(projectDir, PlatypusWebModule.PUBLIC_DIRECTORY);
            prepareWebApplication();
            if (webModule.getServerID() != null && !webModule.getServerID().isEmpty()) {
                setupWebApplication(webModule);
                webModule.forceServerChanged();// Crazy NetBeans architecture of "Deployment" pushs us to do this dirty hack :(
                webAppRunUrl = Deployment.getDefault().deploy(webModule,
                        debug ? Deployment.Mode.DEBUG : Deployment.Mode.RUN,
                        webModule.getUrl(),
                        ClientType.PLATYPUS_CLIENT.equals(project.getSettings().getRunClientType()) ? "" : START_PAGE_FILE_NAME,
                        true,
                        (String message) -> {
                            if (message != null) {
                                if (message.contains("FAIL")) {
                                    project.getOutputWindowIO().getErr().println(message);
                                } else {
                                    project.getOutputWindowIO().getOut().println(message);
                                }
                            }
                        }, null);
                String deployResultMessage = NbBundle.getMessage(PlatypusWebModuleManager.class, "MSG_Web_App_Deployed");//NOI18N
                Logger.getLogger(PlatypusWebModuleManager.class.getName()).log(Level.INFO, deployResultMessage);
                project.getOutputWindowIO().getOut().println(deployResultMessage);
            } else {
                project.getOutputWindowIO().getErr().println(NbBundle.getMessage(PlatypusWebModuleManager.class, "MSG_App_Server_Not_Set"));//NOI18N
                return null;
            }
        } catch (Exception ex) {
            project.getOutputWindowIO().getErr().println(ex.toString());
            project.getOutputWindowIO().select();
            project.getOutputWindowIO().setErrVisible(true);
        }
        return webAppRunUrl;
    }

    /**
     * Creates an web application skeleton if not created yet.
     *
     * @throws java.lang.Exception
     */
    public void prepareWebApplication() throws Exception {
        undeploy();
        project.forceUpdatePlatypusRuntime();
        project.getOutputWindowIO().getOut().println(NbBundle.getMessage(PlatypusWebModuleManager.class, "MSG_Preparing_Web_App"));//NOI18N
        prepareResources();
    }

    private void prepareResources() throws IOException {
        copyResourceIfNotExists(START_PAGE_FILE_NAME);
        copyResourceIfNotExists(LOGIN_PAGE_FILE_NAME);
        copyResourceIfNotExists(LOGIN_FAIL_PAGE_FILE_NAME);
    }

    private void copyResourceIfNotExists(String filePath) throws IOException {
        FileObject fo = projectDir.getFileObject(filePath);
        if (fo == null) {
            fo = projectDir.createData(filePath);
            try (InputStream is = PlatypusWebModuleManager.class.getResourceAsStream(J2EE_RESOURCES_PACKAGE + filePath);
                    OutputStream os = fo.getOutputStream()) {
                FileUtil.copy(is, os);
            }
        }
    }

    private static FileObject createFolderIfNotExists(FileObject dir, String name) throws IOException {
        FileObject fo = dir.getFileObject(name);
        if (fo == null) {
            fo = dir.createFolder(name);
        }
        return fo;
    }

    /**
     * Sets up an web application.
     *
     * @param aModuleProvider Web Module
     * @param aContextXml
     * @throws java.lang.Exception
     */
    protected void setupWebApplication(J2eeModuleProvider aModuleProvider) throws Exception {
        if (TomcatWebAppManager.TOMCAT_SERVER_ID.equals(aModuleProvider.getServerID())) {
            TomcatWebAppManager webAppConfigurator = new TomcatWebAppManager(project, aModuleProvider.getServerInstanceID());
            //webAppConfigurator.deployJdbcDrivers(); // since jdbc drivers are in bundled version of tomcat there is no need to deploy them right now
            webAppConfigurator.configure();
        } else {
            String errorMessage = String.format(NbBundle.getMessage(PlatypusWebModuleManager.class, "MSG_Web_App_Config_Not_Supported"), aModuleProvider.getServerID());//NOI18N
            Logger.getLogger(PlatypusWebModuleManager.class.getName()).log(Level.WARNING, errorMessage);
            project.getOutputWindowIO().getErr().println(errorMessage);
        }
        configureDeploymentDescriptor();
    }

    private void configureDeploymentDescriptor() throws Exception {
        FileObject webInfDir = projectDir.getFileObject(PlatypusProject.WEB_INF_DIRECTORY);
        FileObject webXml = webInfDir.getFileObject(WEB_XML_FILE_NAME);
        if (webXml == null || project.getSettings().isAutoApplyWebSettings()) {
            WebApplication wa = new WebApplication();
            configureParams(wa);
            wa.addAppListener(new AppListener(PlatypusSessionsSynchronizer.class.getName()));
            configureServlet(wa);
            configureDatasources(wa);
            if (project.getSettings().isSecurityRealmEnabled()) {
                configureSecurity(wa);
            }
            if (webXml == null) {
                webXml = webInfDir.createData(WEB_XML_FILE_NAME);
            }
            FileUtils.writeString(FileUtil.toFile(webXml), XmlDom2String.transform(wa.toDocument()), PlatypusUtils.COMMON_ENCODING_NAME);
        }
    }

    private void configureParams(WebApplication wa) throws Exception {
        wa.addInitParam(new ContextParam(PlatypusServerConfig.DEF_DATASOURCE_CONF_PARAM, project.getSettings().getDefaultDataSourceName()));
        wa.addInitParam(new ContextParam(PlatypusServerConfig.APPELEMENT_CONF_PARAM, PlatypusProjectSettings.START_JS_FILE_NAME));
        if (project.getSettings().getGlobalAPI()) {
            wa.addInitParam(new ContextParam(PlatypusServerConfig.GLOBAL_API_CONF_PARAM, "" + true));
        }
        if (project.getSettings().getSourcePath() != null && !project.getSettings().getSourcePath().isEmpty()) {
            wa.addInitParam(new ContextParam(PlatypusServerConfig.SOURCE_PATH_CONF_PARAM, project.getSettings().getSourcePath()));
        }
    }

    private void configureServlet(WebApplication wa) {
        Servlet platypusServlet = new Servlet(PLATYPUS_SERVLET_NAME, PlatypusHttpServlet.class.getName());
        MultipartConfig multiPartConfig = new MultipartConfig();
        multiPartConfig.setLocation(publicDir.getPath());
        multiPartConfig.setMaxFileSize(Long.toString(MULTIPART_MAX_FILE_SIZE));
        multiPartConfig.setMaxRequestSize(Long.toString(MULTIPART_MAX_REQUEST_SIZE));
        multiPartConfig.setFileSizeThreshold(Long.toString(MULTIPART_MAX_FILE_THRESHOLD));
        platypusServlet.setMultipartConfig(multiPartConfig);
        wa.addServlet(platypusServlet);
        wa.addServletMapping(new ServletMapping(PLATYPUS_SERVLET_NAME, PLATYPUS_SERVLET_URL_PATTERN));
        wa.addWelcomeFile(new WelcomeFile(START_PAGE_FILE_NAME));
    }

    private void configureDatasources(WebApplication wa) {
        for (DatabaseConnection conn : ConnectionManager.getDefault().getConnections()) {
            ResourceRef resourceRef = new ResourceRef(conn.getDisplayName(), DataSource.class.getName(), CONTAIER_RESOURCE_SECURITY_TYPE);
            resourceRef.setDescription(conn.getName()); //NOI18N
            wa.addResourceRef(resourceRef);
        }
    }

    private void configureSecurity(WebApplication aWebApplication) {
        // Protect all web application resources
        SecurityConstraint scWholeResources = new SecurityConstraint();
        WebResourceCollection wrcWholeResources = new WebResourceCollection(PLATYPUS_WEB_RESOURCE_NAME);
        wrcWholeResources.setUrlPattern("/*"); //NOI18N
        scWholeResources.addWebResourceCollection(wrcWholeResources);
        scWholeResources.setAuthConstraint(new AuthConstraint(ANY_SIGNED_USER_ROLE));
        aWebApplication.addSecurityConstraint(scWholeResources);
        // Unprotect login page
        SecurityConstraint scLogin = new SecurityConstraint();
        WebResourceCollection wrcLogin = new WebResourceCollection(PLATYPUS_WEB_RESOURCE_NAME + "-login");
        wrcLogin.setUrlPattern("/" + LOGIN_PAGE_FILE_NAME); //NOI18N
        scLogin.addWebResourceCollection(wrcLogin);
        aWebApplication.addSecurityConstraint(scLogin);
        // Unprotect login failed page
        SecurityConstraint scLoginFailed = new SecurityConstraint();
        WebResourceCollection wrcLoginFailed = new WebResourceCollection(PLATYPUS_WEB_RESOURCE_NAME + "-login-failed");
        wrcLoginFailed.setUrlPattern("/" + LOGIN_FAIL_PAGE_FILE_NAME); //NOI18N
        scLoginFailed.addWebResourceCollection(wrcLoginFailed);
        aWebApplication.addSecurityConstraint(scLoginFailed);
        //
        LoginConfig loginConfig = new LoginConfig();
        loginConfig.setAuthMethod(FORM_AUTH_METHOD);
        loginConfig.setFormLoginConfig(new FormLoginConfig("/" + LOGIN_PAGE_FILE_NAME, "/" + LOGIN_FAIL_PAGE_FILE_NAME));//NOI18N
        //
        aWebApplication.addSecurityRole(new SecurityRole(ANY_SIGNED_USER_ROLE));
        aWebApplication.setLoginConfig(loginConfig);
    }
}
