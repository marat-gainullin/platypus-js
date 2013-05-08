/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.project;

import com.eas.client.ClientConstants;
import com.eas.client.application.PlatypusClientApplication;
import com.eas.deploy.project.PlatypusSettings;
import com.eas.designer.debugger.DebuggerEnvironment;
import com.eas.designer.debugger.DebuggerUtils;
import com.eas.designer.explorer.j2ee.PlatypusWebModuleManager;
import com.eas.designer.explorer.platform.EmptyPlatformHomePathException;
import com.eas.designer.explorer.platform.PlatypusPlatform;
import com.eas.designer.explorer.server.PlatypusServerInstance;
import com.eas.designer.explorer.server.PlatypusServerInstanceProvider;
import com.eas.designer.explorer.server.ServerSupport;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Future;
import java.util.logging.Level;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.ExecutionService;
import org.netbeans.api.extexecution.ExternalProcessBuilder;
import org.openide.ErrorManager;
import org.openide.awt.HtmlBrowser;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

/**
 *
 * @author mg
 */
public class ProjectRunner {

    public static final String JVM_RUN_COMMAND_NAME = "java"; //NOI18N
    public static final String OPTION_PREFIX = PlatypusClientApplication.CMD_SWITCHS_PREFIX;
    public static final String CLASSPATH_OPTION_NAME = "cp"; //NOI18N
    private static final String EXT_DIRECTORY_NAME = "ext"; //NOI18N
    private static final String CLIENT_APP_NAME = "Application.jar"; //NOI18N
    private static final String JMX_AUTHENTICATE_OPTION_NAME = "Dcom.sun.management.jmxremote.authenticate"; //NOI18N
    private static final String JMX_SSL_OPTION_NAME = "Dcom.sun.management.jmxremote.ssl"; //NOI18N
    private static final String JMX_REMOTE_OPTION_NAME = "Dcom.sun.management.jmxremote"; //NOI18N
    private static final String JMX_REMOTE_OPTION_PORT_NAME = "Dcom.sun.management.jmxremote.port"; //NOI18N
    private static final String EQUALS_SIGN = "="; //NOI18N
    private static final String FALSE = "false"; //NOI18N
    private static final String LOCAL_HOSTNAME = "localhost"; //NOI18N
    private static final RequestProcessor RP = new RequestProcessor(ProjectRunner.class.getName());

    public static void run(final PlatypusProject project, final String appElementId) throws Exception {

         RP.post(new Runnable() {
         @Override
         public void run() {
        start(project, appElementId, false);
               }
         });
    }

    public static void debug(final PlatypusProject project, final String appElementId) throws Exception {
        RP.post(new Runnable() {
            @Override
            public void run() {
                Future<Integer> runningProgram = start(project, appElementId, true);
                if (runningProgram != null) {
                    try {
//                if (Boolean.TRUE.equals(project.getSettings().isUseAppServer())) {
//                    DebuggerEnvironment serverEnv = new DebuggerEnvironment();
//                    serverEnv.host = LOCAL_HOSTNAME;
//                    serverEnv.port = project.getSettings().getDebugServerPort();
//                    DebuggerUtils.attachDebugger(serverEnv);
//                    project.getOutputWindowIO().getOut().println("Application server debug activated.");
//                }
                        DebuggerEnvironment env = new DebuggerEnvironment();
                        env.host = LOCAL_HOSTNAME;
                        env.port = project.getSettings().getDebugClientPort();
                        env.runningProgram = runningProgram;
                        env.runningElement = project.getSettings().getAppSettings().getRunElement();
                        DebuggerUtils.attachDebugger(env);
                        project.getOutputWindowIO().getOut().println("Client application debug activated.");
                    } catch (Exception ex) {
                        runningProgram.cancel(true);
                        ErrorManager.getDefault().notify(ex);
                    }
                }
            }
        });
    }

    private static Future<Integer> start(PlatypusProject project, String appElementId, boolean debug) {
        InputOutput io = IOProvider.getDefault().getIO(project.getDisplayName(), false);
        File binDir;
        try {
            binDir = PlatypusPlatform.getPlatformBinDirectory();
        } catch (EmptyPlatformHomePathException | IllegalStateException ex) {
            io.getErr().println(ex.getMessage());
            if (!PlatypusPlatform.showPlatformHomeDialog()) {
                return null;
            } else {
                try {
                    binDir = PlatypusPlatform.getPlatformBinDirectory();
                } catch (EmptyPlatformHomePathException | IllegalStateException ex1) {
                    io.getErr().println(ex1.getMessage());
                    io.getOut().println("Specify path to Platypus Platform home directory and restart application.");
                    return null;
                }
            }
        }
        io.getOut().println("Starting Platypus Application..");
        PlatypusProjectSettings pps = project.getSettings();
        String webAppUrl = null;
        if (AppServerType.PLATYPUS_SERVER.equals(pps.getRunAppServerType())
                && project.getSettings().isStartServer()
                && ServerSupport.isLocalHost(pps.getServerHost())) {
            PlatypusServerInstance serverInstance = PlatypusServerInstanceProvider.getPlatypusDevServer();
            if (serverInstance.getServerState() == PlatypusServerInstance.ServerState.STOPPED) {
                io.getOut().println("Starting Platypus Server..");
                if (serverInstance.start(project, binDir, debug)) {
                    io.getOut().println("Platypus Server started.");
                    try {
                        io.getOut().println("Waiting for Platypus Server to run..");
                        ServerSupport.waitForServer(pps.getServerHost(), pps.getServerPort());
                        PlatypusServerInstanceProvider.getPlatypusDevServer().setServerState(PlatypusServerInstance.ServerState.RUNNING);
                    } catch (ServerSupport.ServerTimeOutException | InterruptedException ex) {
                        io.getErr().println(ex.getMessage());
                        return null;
                    }
                } else {
                    io.getErr().println("Could not start Platypus Server.");
                    return null;
                }
            } else {
                assert serverInstance.getProject() != null;
                if (serverInstance.getProject().getProjectDirectory().equals(project.getProjectDirectory())) {
                    io.getOut().println("Platypus Server already started.");
                } else {
                    io.getErr().println("Platypus Server started for another project: " + serverInstance.getProject().getDisplayName());
                }
            }
        } else if (AppServerType.J2EE_SERVER.equals(pps.getRunAppServerType())) {
            io.getOut().println("Starting J2EE Server..");
            PlatypusWebModuleManager webManager = project.getLookup().lookup(PlatypusWebModuleManager.class);
            if (webManager != null) {
                webAppUrl = webManager.run(appElementId,debug);
            } else {
                throw new IllegalStateException("An instance of PlatypusWebModuleManager is not found in project's lookup.");
            }
        }
        if (ClientType.PLATYPUS_CLIENT.equals(pps.getRunClientType())) {
            ExecutionDescriptor descriptor = new ExecutionDescriptor()
                    .frontWindow(true)
                    .controllable(true);
            ExternalProcessBuilder processBuilder = new ExternalProcessBuilder(JVM_RUN_COMMAND_NAME);
            if (debug) {
                processBuilder = setDebugArguments(processBuilder, project.getSettings().getDebugClientPort());
            }

            processBuilder = processBuilder.addArgument(OPTION_PREFIX + CLASSPATH_OPTION_NAME);
            processBuilder = processBuilder.addArgument(getExtendedClasspath(getExecutablePath(binDir)));

            processBuilder = processBuilder.addArgument(PlatypusClientApplication.class.getName());

            String runElementId = null;
            PlatypusSettings ps = pps.getAppSettings();
            if (appElementId != null && !appElementId.isEmpty()) {
                runElementId = appElementId;
            } else if (ps.getRunElement() != null && !ps.getRunElement().isEmpty()) {
                runElementId = pps.getAppSettings().getRunElement();
            }
            if (runElementId != null && !runElementId.isEmpty()) {
                processBuilder = processBuilder.addArgument(OPTION_PREFIX + PlatypusClientApplication.APPELEMENT_CMD_SWITCH);
                processBuilder = processBuilder.addArgument(runElementId);
                io.getOut().println("Start application element: " + runElementId);
            } else {
                io.getErr().println("Start application element is not set.");
                return null;
            }
            if (!pps.isDbAppSources()) {
                processBuilder = processBuilder.addArgument(OPTION_PREFIX + PlatypusClientApplication.APP_PATH_CMD_SWITCH1);
                processBuilder = processBuilder.addArgument(project.getProjectDirectory().getPath());
                io.getOut().println(String.format("Application sources: %s.", project.getProjectDirectory().getPath()));
            } else {
                io.getOut().println("Application sources: database.");
            }
            if (AppServerType.PLATYPUS_SERVER.equals(pps.getRunAppServerType())) {
                processBuilder = processBuilder.addArgument(OPTION_PREFIX + PlatypusClientApplication.URL_CMD_SWITCH);
                processBuilder = processBuilder.addArgument(getServerUrl(pps));
                io.getOut().println(String.format("Using application server at URL: %s.", getServerUrl(pps)));
            } else if (AppServerType.J2EE_SERVER.equals(pps.getRunAppServerType())) {
                if (webAppUrl != null && webAppUrl.isEmpty()) {
                   processBuilder = processBuilder.addArgument(OPTION_PREFIX + PlatypusClientApplication.URL_CMD_SWITCH);
                    processBuilder = processBuilder.addArgument(webAppUrl);
                    io.getOut().println(String.format("Using application J2EE server at URL: %s.", webAppUrl));
                }
            } else if (AppServerType.NONE.equals(pps.getRunAppServerType())) {
                processBuilder = processBuilder.addArgument(OPTION_PREFIX + PlatypusClientApplication.URL_CMD_SWITCH);
                processBuilder = processBuilder.addArgument(ps.getDbSettings().getUrl());
                processBuilder = processBuilder.addArgument(OPTION_PREFIX + PlatypusClientApplication.DBUSER_CMD_SWITCH);
                processBuilder = processBuilder.addArgument(ps.getDbSettings().getInfo().getProperty(ClientConstants.DB_CONNECTION_USER_PROP_NAME));
                processBuilder = processBuilder.addArgument(OPTION_PREFIX + PlatypusClientApplication.DBPASSWORD_CMD_SWITCH);
                processBuilder = processBuilder.addArgument(ps.getDbSettings().getInfo().getProperty(ClientConstants.DB_CONNECTION_PASSWORD_PROP_NAME));
                processBuilder = processBuilder.addArgument(OPTION_PREFIX + PlatypusClientApplication.DBSCHEMA_CMD_SWITCH);
                processBuilder = processBuilder.addArgument(ps.getDbSettings().getInfo().getProperty(ClientConstants.DB_CONNECTION_SCHEMA_PROP_NAME));
                io.getOut().println("Using direct connection to database.");
            }
            if (project.getSettings().getRunUser() != null && !project.getSettings().getRunUser().trim().isEmpty() && project.getSettings().getRunPassword() != null && !project.getSettings().getRunPassword().trim().isEmpty()) {
                processBuilder = processBuilder.addArgument(OPTION_PREFIX + PlatypusClientApplication.USER_CMD_SWITCH);
                processBuilder = processBuilder.addArgument(project.getSettings().getRunUser());
                processBuilder = processBuilder.addArgument(OPTION_PREFIX + PlatypusClientApplication.PASSWORD_CMD_SWITCH);
                processBuilder = processBuilder.addArgument(project.getSettings().getRunPassword());
                io.getOut().println("Log in as user: " + project.getSettings().getRunUser());
            }
            if (pps.getRunClientOptions() != null && !pps.getRunClientOptions().isEmpty()) {
                String[] optionalArgs = pps.getRunClientOptions().split(" ");
                if (optionalArgs.length > 0) {
                    for (int i = 0; i < optionalArgs.length; i++) {
                        processBuilder = processBuilder.addArgument(optionalArgs[i]);
                    }
                }
                io.getOut().println(String.format("Options: %s.", pps.getRunClientOptions()));
            }
            //set default log level if not set explicitly
            if (!isSetByOption(PlatypusClientApplication.LOGLEVEL_CMD_SWITCH, pps.getRunClientOptions())) {
                processBuilder = processBuilder.addArgument(OPTION_PREFIX + PlatypusClientApplication.LOGLEVEL_CMD_SWITCH);
                processBuilder = processBuilder.addArgument(Level.INFO.getName());
                io.getOut().println(String.format("Logging level set to: %s.", Level.INFO.getName()));
            }
            if (debug) {
                processBuilder = processBuilder.addArgument(OPTION_PREFIX + PlatypusClientApplication.STOP_BEFORE_RUN_CMD_SWITCH);
            }
            ExecutionService service = ExecutionService.newService(processBuilder, descriptor, getServiceDisplayName(project, debug));
            io.getOut().println("Starting Platypus Client..");
            Future<Integer> clientTask = service.run();
            if (clientTask != null) {
                io.getOut().println("Platypus Client started.");
                io.getOut().println();
            }
            return clientTask;
        } else if (ClientType.WEB_BROWSER.equals(pps.getRunClientType())) {
            try {
                if (webAppUrl != null) {
                    io.getOut().println(String.format("Starting web browser: %s", webAppUrl));
                    HtmlBrowser.URLDisplayer.getDefault().showURL(new URL(webAppUrl));
                }
            } catch (MalformedURLException ex) {
                ErrorManager.getDefault().notify(ex);
            }
            return null;
        }
        return null;
    }

    public static ExternalProcessBuilder setDebugArguments(ExternalProcessBuilder processBuilder, int port) {
        processBuilder = processBuilder.addArgument(OPTION_PREFIX
                + JMX_AUTHENTICATE_OPTION_NAME
                + EQUALS_SIGN
                + FALSE);
        processBuilder = processBuilder.addArgument(OPTION_PREFIX
                + JMX_SSL_OPTION_NAME
                + EQUALS_SIGN
                + FALSE);
        processBuilder = processBuilder.addArgument(OPTION_PREFIX
                + JMX_REMOTE_OPTION_NAME);
        processBuilder = processBuilder.addArgument(OPTION_PREFIX
                + JMX_REMOTE_OPTION_PORT_NAME
                + EQUALS_SIGN
                + port);
        return processBuilder;
    }

    public static String getExtendedClasspath(String executablePath) {
        StringBuilder classpathStr = new StringBuilder(executablePath);
        File extDir = getPlatformExtDirectory();
        if (extDir.exists() && extDir.isDirectory()) {
            classpathStr.append(File.pathSeparator);
            classpathStr.append(extDir);
            classpathStr.append(File.pathSeparator);
            classpathStr.append(String.format("%s/*", extDir)); //NOI18N
        }
        return classpathStr.toString();
    }

    public static boolean isSetByOption(String command, String options) {
        return options != null && options.contains(OPTION_PREFIX + command);
    }

    private static String getServiceDisplayName(PlatypusProject project, boolean debug) {
        return String.format("%s (%s)", project.getDisplayName(), //NOI18N
                debug ? NbBundle.getMessage(ProjectRunner.class, "LBL_DebugTab_Name") //NOI18N
                : NbBundle.getMessage(ProjectRunner.class, "LBL_RunTab_Name")); //NOI18N
    }

    private static File getPlatformExtDirectory() {
        assert PlatypusPlatform.getPlatformHomePath() != null;
        assert !PlatypusPlatform.getPlatformHomePath().isEmpty();
        File platformHomeDir = new File(PlatypusPlatform.getPlatformHomePath());
        File extDir = new File(platformHomeDir, EXT_DIRECTORY_NAME);
        return extDir;
    }

    private static String getExecutablePath(File aBinDir) {
        File clientAppExecutable = new File(aBinDir, CLIENT_APP_NAME);
        if (!clientAppExecutable.exists()) {
            throw new IllegalStateException("Platypus Client application executable not exists.");
        }
        return clientAppExecutable.getAbsolutePath();
    }

    private static String getServerUrl(PlatypusProjectSettings pps) {
        return String.format("%s://%s:%s", pps.getServerProtocol(), pps.getServerHost(), pps.getServerPort()); //NOI18N
    }
}
