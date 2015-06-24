/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.project;

import com.eas.client.AppElementFiles;
import com.eas.client.application.PlatypusClientApplication;
import com.eas.client.cache.PlatypusFiles;
import com.eas.client.resourcepool.DatasourcesArgsConsumer;
import com.eas.designer.application.PlatypusUtils;
import com.eas.designer.application.platform.PlatformHomePathException;
import com.eas.designer.application.platform.PlatypusPlatform;
import com.eas.designer.application.project.AppServerType;
import com.eas.designer.application.project.ClientType;
import com.eas.designer.application.project.PlatypusProject;
import com.eas.designer.application.project.PlatypusProjectSettings;
import com.eas.designer.explorer.j2ee.PlatypusWebModuleManager;
import com.eas.designer.explorer.platform.PlatypusPlatformDialog;
import com.eas.designer.explorer.server.PlatypusServerInstance;
import com.eas.designer.explorer.server.PlatypusServerInstanceProvider;
import com.eas.designer.explorer.server.ServerState;
import com.eas.designer.explorer.server.ServerSupport;
import com.eas.server.PlatypusServer;
import com.eas.util.FileUtils;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;
import java.util.logging.Level;
import org.netbeans.api.db.explorer.ConnectionManager;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.netbeans.api.debugger.Breakpoint;
import org.netbeans.api.debugger.DebuggerEngine;
import org.netbeans.api.debugger.DebuggerInfo;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.DebuggerManagerListener;
import org.netbeans.api.debugger.Session;
import org.netbeans.api.debugger.Watch;
import org.netbeans.api.debugger.jpda.AttachingDICookie;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.ExecutionService;
import org.netbeans.api.extexecution.ExternalProcessBuilder;
import org.openide.actions.SaveAllAction;
import org.openide.awt.HtmlBrowser;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
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
    private static final String API_DIRECTORY_NAME = "api"; //NOI18N
    private static final String CLIENT_APP_NAME = "Application.jar"; //NOI18N
    private static final String LOG_LEVEL_OPTION_NAME = "D.level"; //NOI18N
    private static final String LOG_HANDLERS_OPTION_NAME = "Dhandlers"; //NOI18N
    private static final String CONSOLE_LOG_HANDLER_NAME = "java.util.logging.ConsoleHandler"; //NOI18N
    private static final String CONSOLE_LOG_HANDLER_LEVEL_OPION_NAME = "Djava.util.logging.ConsoleHandler.level"; //NOI18N
    private static final String CONSOLE_LOG_FORMATTER_OPTION_NAME = "Djava.util.logging.ConsoleHandler.formatter"; //NOI18N
    private static final String LOG_CONFIG_CLASS_OPTION_NAME = "Djava.util.logging.config.class"; //NOI18N
    private static final String JS_APPLICATION_LOG_LEVEL_OPTION_NAME = "DApplication.level"; //NOI18N
    private static final String EQUALS_SIGN = "="; //NOI18N
    private static final String FALSE = "false"; //NOI18N
    private static final String LOCAL_HOSTNAME = "localhost"; //NOI18N

    private static class DebuggerManagerAdapter implements DebuggerManagerListener {

        @Override
        public Breakpoint[] initBreakpoints() {
            return null;
        }

        @Override
        public void breakpointAdded(Breakpoint breakpoint) {
        }

        @Override
        public void breakpointRemoved(Breakpoint breakpoint) {
        }

        @Override
        public void initWatches() {
        }

        @Override
        public void watchAdded(Watch watch) {
        }

        @Override
        public void watchRemoved(Watch watch) {
        }

        @Override
        public void sessionAdded(Session session) {
        }

        @Override
        public void sessionRemoved(Session session) {
        }

        @Override
        public void engineAdded(DebuggerEngine engine) {
        }

        @Override
        public void engineRemoved(DebuggerEngine engine) {
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
        }
    }

    protected static void saveAll() {
        SaveAllAction action = SystemAction.get(SaveAllAction.class);
        if (action != null) {
            action.performAction();
        }
    }

    /**
     * Starts an application in run mode.
     *
     * @param project Application's project.
     * @param appElementName Application element's name OR relative path to the
     * executable file.
     * @throws Exception If something goes wrong.
     */
    public static void run(final PlatypusProject project, final String appElementName) throws Exception {
        saveAll();
        project.getRequestProcessor().post(() -> {
            try {
                start(project, appElementName, false);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        });
    }

    /**
     * Starts an application in debug mode.
     *
     * @param project Application's project.
     * @param appElementName Application element's name OR relative path to the
     * executable file.
     * @throws Exception If something goes wrong.
     */
    public static void debug(final PlatypusProject project, final String appElementName) throws Exception {
        saveAll();
        project.getRequestProcessor().post(() -> {
            try {
                start(project, appElementName, true);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        });
    }

    private static void start(PlatypusProject project, String appElementName, boolean debug) throws Exception {
        if (appElementName != null && !appElementName.isEmpty()) {
            FileObject appSrcDir = project.getSrcRoot();
            FileObject startJs = appSrcDir.getFileObject(PlatypusProjectSettings.START_JS_FILE_NAME);
            if (startJs == null) {
                startJs = appSrcDir.createData(PlatypusProjectSettings.START_JS_FILE_NAME);
            }
            AppElementFiles startFiles = project.getIndexer().nameToFiles(appElementName);
            if (startFiles != null) {
                String startMethod = startFiles.hasExtension(PlatypusFiles.FORM_EXTENSION) ? "show" : "execute";
                String starupScript = String.format(PlatypusProjectSettingsImpl.START_JS_FILE_TEMPLATE, appElementName, "        var m = new " + appElementName+"();\n", "        m." + startMethod+"();\n");
                FileUtils.writeString(FileUtil.toFile(startJs), starupScript, PlatypusUtils.COMMON_ENCODING_NAME);
            } else if (appElementName.toLowerCase().endsWith("." + PlatypusFiles.JAVASCRIPT_EXTENSION)) {
                String starupScript = String.format(PlatypusProjectSettingsImpl.START_JS_FILE_TEMPLATE, appElementName, "", "");
                FileUtils.writeString(FileUtil.toFile(startJs), starupScript, PlatypusUtils.COMMON_ENCODING_NAME);
            }
        } else {
            throw new IllegalStateException(NbBundle.getMessage(ProjectRunner.class, "MSG_Start_App_Element_Not_Set"));
        }
        InputOutput io = IOProvider.getDefault().getIO(project.getDisplayName(), false);
        try {
            File binDir;
            try {
                binDir = PlatypusPlatform.getPlatformBinDirectory();
            } catch (PlatformHomePathException ex) {
                if (PlatypusPlatformDialog.showPlatformHomeDialog()) {
                    binDir = PlatypusPlatform.getPlatformBinDirectory();
                } else {
                    throw ex;
                }
            }
            io.getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Application_Starting"));
            PlatypusProjectSettings pps = project.getSettings();
            String appUrl = null;
            boolean startServer = !pps.isNotStartServer();
            if (startServer) {
                if (AppServerType.PLATYPUS_SERVER.equals(pps.getRunAppServerType())) {
                    PlatypusServerInstance serverInstance = PlatypusServerInstanceProvider.getPlatypusDevServer();
                    if (serverInstance.getServerState() == ServerState.STOPPED) {
                        io.getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Starting_Platypus_Server"));//NOI18N
                        if (serverInstance.start(project, binDir, debug)) {
                            io.getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Platypus_Server_Started"));//NOI18N
                            io.getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Waiting_Platypus_Server"));//NOI18N
                            if (debug) {
                                DebuggerEngine[] startedEngines = DebuggerManager.getDebuggerManager().startDebugging(DebuggerInfo.create(AttachingDICookie.ID, new Object[]{AttachingDICookie.create(LOCAL_HOSTNAME, project.getSettings().getDebugServerPort())}));
                                DebuggerEngine justStartedEngine = startedEngines[0];
                                DebuggerManager.getDebuggerManager().addDebuggerListener(new DebuggerManagerAdapter() {

                                    @Override
                                    public void engineRemoved(DebuggerEngine engine) {
                                        if (engine == justStartedEngine) {
                                            serverInstance.stop();
                                            DebuggerManager.getDebuggerManager().removeDebuggerListener(this);
                                        }
                                    }

                                });
                            }
                            project.getOutputWindowIO().getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Server_Debug_Activated"));//NOI18N
                            ServerSupport ss = new ServerSupport(serverInstance);
                            ss.waitForServer(LOCAL_HOSTNAME, pps.getServerPort());
                            PlatypusServerInstanceProvider.getPlatypusDevServer().setServerState(ServerState.RUNNING);
                        } else {
                            throw new IllegalStateException(NbBundle.getMessage(ProjectRunner.class, "MSG_Cnt_Start_Platypus_Server"));
                        }
                    } else {
                        assert serverInstance.getProject() != null;
                        if (serverInstance.getProject().getProjectDirectory().equals(project.getProjectDirectory())) {
                            io.getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Platypus_Server_Already_Started"));//NOI18N
                        } else {
                            io.getErr().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Platypus_Server_Started_Another_Project") + serverInstance.getProject().getDisplayName());//NOI18N
                        }
                    }
                } else if (AppServerType.J2EE_SERVER.equals(pps.getRunAppServerType())) {
                    io.getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Deploying_J2EE_Container"));//NOI18N
                    PlatypusWebModuleManager webManager = project.getLookup().lookup(PlatypusWebModuleManager.class);
                    if (webManager != null) {
                        appUrl = webManager.start(debug);
                    } else {
                        throw new IllegalStateException("An instance of PlatypusWebModuleManager is not found in project's lookup.");
                    }
                }
            }
            if (ClientType.PLATYPUS_CLIENT.equals(pps.getRunClientType())) {
                ExecutionDescriptor descriptor = new ExecutionDescriptor()
                        .frontWindow(true)
                        .controllable(true);
                List<String> arguments = new ArrayList<>();
                if (pps.getRunClientVmOptions() != null && !pps.getRunClientVmOptions().isEmpty()) {
                    addArguments(arguments, pps.getRunClientVmOptions());
                    io.getOut().println(String.format(NbBundle.getMessage(ProjectRunner.class, "MSG_VM_Run_Options"), pps.getRunClientVmOptions()));//NOI18N
                }
                if (debug) {
                    setDebugArguments(arguments, project.getSettings().getDebugClientPort());
                }

                io.getOut().println(String.format(NbBundle.getMessage(ProjectRunner.class, "MSG_Logging_Level"), project.getSettings().getClientLogLevel()));//NOI18N
                setLogging(arguments, project.getSettings().getClientLogLevel());

                String classPath = getExtendedClasspath(getApiClasspath(getExecutablePath(binDir)));
                arguments.add(OPTION_PREFIX + CLASSPATH_OPTION_NAME);
                arguments.add(classPath);

                arguments.add(PlatypusClientApplication.class.getName());

                if (AppServerType.NONE.equals(pps.getRunAppServerType())) {
                    arguments.add(OPTION_PREFIX + PlatypusClientApplication.APPELEMENT_CMD_SWITCH);
                    arguments.add(PlatypusProjectSettings.START_JS_FILE_NAME);
                    io.getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Start_App_Element") + PlatypusProjectSettings.START_JS_FILE_NAME); //NOI18N
                    // Iterate through all datasources, registered in the designer.
                    // Apply them as datasources in considered server.
                    DatabaseConnection defaultDatabaseConnection = null;
                    DatabaseConnection[] dataSources = ConnectionManager.getDefault().getConnections();
                    for (DatabaseConnection connection : dataSources) {
                        if (isConnectionValid(connection)) {
                            if (connection.getDisplayName() == null ? pps.getDefaultDataSourceName() == null : connection.getDisplayName().equals(pps.getDefaultDataSourceName())) {
                                defaultDatabaseConnection = connection;
                            }
                            arguments.add(ProjectRunner.OPTION_PREFIX + DatasourcesArgsConsumer.DB_RESOURCE_CONF_PARAM);
                            arguments.add(connection.getDisplayName());// Hack because of NetBeans
                            arguments.add(ProjectRunner.OPTION_PREFIX + DatasourcesArgsConsumer.DB_URL_CONF_PARAM);
                            arguments.add(connection.getDatabaseURL());
                            arguments.add(ProjectRunner.OPTION_PREFIX + DatasourcesArgsConsumer.DB_USERNAME_CONF_PARAM);
                            arguments.add(connection.getUser());
                            arguments.add(ProjectRunner.OPTION_PREFIX + DatasourcesArgsConsumer.DB_PASSWORD_CONF_PARAM);
                            arguments.add(connection.getPassword());
                            if (connection.getSchema() != null && !connection.getSchema().isEmpty()) {
                                arguments.add(ProjectRunner.OPTION_PREFIX + DatasourcesArgsConsumer.DB_SCHEMA_CONF_PARAM);
                                arguments.add(connection.getSchema());
                            }
                        } else {
                            io.getErr().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Invalid_Database", connection.getDisplayName()));
                        }
                    }
                    io.getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Database_Direct"));//NOI18N
                    if (defaultDatabaseConnection != null) {
                        arguments.add(ProjectRunner.OPTION_PREFIX + PlatypusClientApplication.DEF_DATASOURCE_CONF_PARAM);
                        arguments.add(pps.getDefaultDataSourceName());
                    } else if (pps.getDefaultDataSourceName() != null && !pps.getDefaultDataSourceName().isEmpty()) {
                        io.getErr().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Missing_App_Database"));
                    }
                    arguments.add(ProjectRunner.OPTION_PREFIX + PlatypusClientApplication.URL_CMD_SWITCH);
                    arguments.add(project.getProjectDirectory().toURI().toASCIIString());
                    io.getOut().println(String.format(NbBundle.getMessage(ProjectRunner.class, "MSG_App_Sources"), project.getProjectDirectory().toURI().toASCIIString()));//NOI18N
                } else {
                    if (AppServerType.J2EE_SERVER.equals(pps.getRunAppServerType())) {
                        io.getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Deploying_J2EE_Container"));//NOI18N
                        PlatypusWebModuleManager webManager = project.getLookup().lookup(PlatypusWebModuleManager.class);
                        if (webManager != null) {
                            appUrl = webManager.start(debug);
                        } else {
                            throw new IllegalStateException("An instance of PlatypusWebModuleManager is not found in project's lookup.");
                        }
                    } else if (AppServerType.PLATYPUS_SERVER.equals(pps.getRunAppServerType())) {
                        appUrl = getDevPlatypusServerUrl(pps);
                    }
                    if (appUrl != null && !appUrl.isEmpty()) {
                        arguments.add(OPTION_PREFIX + PlatypusClientApplication.URL_CMD_SWITCH);
                        arguments.add(appUrl);
                        io.getOut().println(String.format(NbBundle.getMessage(ProjectRunner.class, "MSG_App_Server_URL"), appUrl));//NOI18N
                    } else {
                        throw new IllegalStateException(NbBundle.getMessage(ProjectRunner.class, "MSG_Cnt_Start_Platypus_Client"));
                    }
                }
                if (project.getSettings().getRunUser() != null && !project.getSettings().getRunUser().trim().isEmpty() && project.getSettings().getRunPassword() != null && !project.getSettings().getRunPassword().trim().isEmpty()) {
                    arguments.add(OPTION_PREFIX + PlatypusClientApplication.USER_CMD_SWITCH);
                    arguments.add(project.getSettings().getRunUser());
                    arguments.add(OPTION_PREFIX + PlatypusClientApplication.PASSWORD_CMD_SWITCH);
                    arguments.add(project.getSettings().getRunPassword());
                    io.getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Login_As_User") + project.getSettings().getRunUser());//NOI18N
                }
                if (pps.getRunClientOptions() != null && !pps.getRunClientOptions().isEmpty()) {
                    addArguments(arguments, pps.getRunClientOptions());
                    io.getOut().println(String.format(NbBundle.getMessage(ProjectRunner.class, "MSG_Run_Options"), pps.getRunClientOptions()));//NOI18N
                }
                ExternalProcessBuilder processBuilder = new ExternalProcessBuilder(JVM_RUN_COMMAND_NAME);
                for (String argument : arguments) {
                    processBuilder = processBuilder.addArgument(argument);
                }

//                processBuilder = processBuilder
//                        .addArgument("-D.level=SEVERE")
//                        .addArgument("-Dhandlers=java.util.logging.ConsoleHandler")
//                        .addArgument("-Djava.util.logging.ConsoleHandler.level=INFO")
//                        .addArgument("-DApplication.level=INFO")
//                        .addArgument("-Djava.util.logging.ConsoleHandler.formatter=com.eas.util.logging.PlatypusFormatter")
//                        .addArgument("-Djava.util.logging.config.class=com.eas.util.logging.LoggersConfig")
//                        .addArgument("-cp")
//                        .addArgument("\"/home/user/workspace/platypus js/application/bin/Application.jar:/home/user/workspace/platypus js/application/bin/Application.jar:/home/user/workspace/platypus js/application/api:/home/user/workspace/platypus js/application/ext/*:/home/user/workspace/platypus js/application/ext\"")
//                        .addArgument("com.eas.client.application.PlatypusClientApplication")
//                        .addArgument("-appelement")
//                        .addArgument("\"start.js\"")
//                        .addArgument("-url")
//                        .addArgument("file:/home/user/workspace/platypus%20tests/")
//                        ;
                ExecutionService service = ExecutionService.newService(processBuilder, descriptor, getServiceDisplayName(project, debug));
                io.getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Starting_Platypus_Client"));//NOI18N
                io.getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Command_Line") + getCommandLineStr(arguments));//NOI18N
                Future<Integer> clientTask = service.run();
                io.getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Platypus_Client_Started"));//NOI18N
                io.getOut().println();
                if (debug) {
                    DebuggerEngine[] startedEngines = DebuggerManager.getDebuggerManager().startDebugging(DebuggerInfo.create(AttachingDICookie.ID, new Object[]{AttachingDICookie.create(LOCAL_HOSTNAME, project.getSettings().getDebugClientPort())}));
                    if (startedEngines.length > 0) {
                        DebuggerEngine justStartedEngine = startedEngines[0];
                        DebuggerManager.getDebuggerManager().addDebuggerListener(new DebuggerManagerAdapter() {

                            @Override
                            public void engineRemoved(DebuggerEngine engine) {
                                if (engine == justStartedEngine) {
                                    clientTask.cancel(true);
                                    DebuggerManager.getDebuggerManager().removeDebuggerListener(this);
                                }
                            }
                        });
                    }
                }
            } else if (ClientType.WEB_BROWSER.equals(pps.getRunClientType())) {
                if (pps.isNotStartServer()) {
                    appUrl = pps.getClientUrl();
                }
                if (appUrl != null && !appUrl.isEmpty()) {
                    io.getOut().println(String.format(NbBundle.getMessage(ProjectRunner.class, "MSG_Starting_Web_Browser"), appUrl));//NOI18N
                    HtmlBrowser.URLDisplayer.getDefault().showURL(new URL(appUrl));
                } else {
                    throw new IllegalStateException(NbBundle.getMessage(ProjectRunner.class, "MSG_Cnt_Start_Web_Browser"));
                }
            }
        } catch (MalformedURLException ex) {
            io.getErr().println(ex.getMessage());
        } catch (ServerSupport.ServerTimeOutException ex) {
            io.getErr().println(ex.getMessage());
        } catch (ServerSupport.ServerStoppedException ex) {
            io.getErr().println(ex.getMessage());
        } catch (PlatformHomePathException ex) {
            io.getErr().println(ex.getMessage());
            io.getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Specify_Platypus_Platform_Path"));//NOI18N
        } catch (InterruptedException ex) {
            io.getErr().println(ex.getMessage());
        } catch (IllegalStateException ex) {
            io.getErr().println(ex.getMessage());
        }
    }

    public static void addArguments(List<String> arguments, String argsStr) {
        String[] options = argsStr.split(" ");//NOI18N
        if (options.length > 0) {
            arguments.addAll(Arrays.asList(options));
        }
    }

    public static String getCommandLineStr(List<String> arguments) {
        StringBuilder sb = new StringBuilder(JVM_RUN_COMMAND_NAME);
        arguments.stream().forEach((argument) -> {
            argument = escapeString(argument);
            sb.append(" "); //NOI18N
            sb.append(argument);
        });
        return sb.toString();
    }

    public static void setLogging(List<String> arguments, Level logLevel) {
        arguments.add(OPTION_PREFIX
                + LOG_LEVEL_OPTION_NAME
                + EQUALS_SIGN
                + logLevel.getName());
        arguments.add(OPTION_PREFIX
                + LOG_HANDLERS_OPTION_NAME
                + EQUALS_SIGN
                + CONSOLE_LOG_HANDLER_NAME);
        arguments.add(OPTION_PREFIX
                + CONSOLE_LOG_HANDLER_LEVEL_OPION_NAME
                + EQUALS_SIGN
                + logLevel.getName());
        /*
        arguments.add(OPTION_PREFIX
                + JS_APPLICATION_LOG_LEVEL_OPTION_NAME
                + EQUALS_SIGN
                + logLevel.getName());
        */        
        arguments.add(OPTION_PREFIX
                + CONSOLE_LOG_FORMATTER_OPTION_NAME
                + EQUALS_SIGN
                + com.eas.util.logging.PlatypusFormatter.class.getName());
        arguments.add(OPTION_PREFIX
                + LOG_CONFIG_CLASS_OPTION_NAME
                + EQUALS_SIGN
                + com.eas.util.logging.LoggersConfig.class.getName());
    }

    public static void setDebugArguments(List<String> arguments, int port) {
        arguments.add("-Xdebug");
        arguments.add("-Xrunjdwp:transport=dt_socket,suspend=y,server=y,address=" + port);
    }

    public static String getExtendedClasspath(String executablePath) {
        StringBuilder classpathStr = new StringBuilder(executablePath);
        File extDir = getPlatformExtDirectory();
        if (extDir.exists() && extDir.isDirectory()) {
            classpathStr.append(File.pathSeparator);
            classpathStr.append(String.format("%s/*", extDir)); //NOI18N
            classpathStr.append(File.pathSeparator);
            classpathStr.append(extDir);
        }
        return classpathStr.toString();
    }

    public static String getApiClasspath(String executablePath) {
        StringBuilder classpathStr = new StringBuilder(executablePath);
        File apiDir = getPlatformApiDirectory();
        if (apiDir.exists() && apiDir.isDirectory()) {
            classpathStr.append(File.pathSeparator);
            classpathStr.append(apiDir);
        }
        return classpathStr.toString();
    }

    public static boolean isSetByOption(String command, String options) {
        return options != null && options.contains(OPTION_PREFIX + command);
    }

    public static boolean isConnectionValid(DatabaseConnection connection) {
        return connection.getDisplayName() != null && !connection.getDisplayName().isEmpty() && !connection.getDisplayName().contains(" ") //NOI18N
                && connection.getDatabaseURL() != null && !connection.getDatabaseURL().isEmpty()
                && connection.getUser() != null && !connection.getUser().isEmpty()
                && connection.getPassword() != null && !connection.getPassword().isEmpty();
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

    private static File getPlatformApiDirectory() {
        assert PlatypusPlatform.getPlatformHomePath() != null;
        assert !PlatypusPlatform.getPlatformHomePath().isEmpty();
        File platformHomeDir = new File(PlatypusPlatform.getPlatformHomePath());
        File extDir = new File(platformHomeDir, API_DIRECTORY_NAME);
        return extDir;
    }

    private static String getExecutablePath(File aBinDir) {
        File clientAppExecutable = new File(aBinDir, CLIENT_APP_NAME);
        if (!clientAppExecutable.exists()) {
            throw new IllegalStateException("Platypus Client application executable not exists.");
        }
        return clientAppExecutable.getAbsolutePath();
    }

    private static String getDevPlatypusServerUrl(PlatypusProjectSettings pps) {
        return String.format("%s://%s:%s", PlatypusServer.DEFAULT_PROTOCOL, LOCAL_HOSTNAME, pps.getServerPort()); //NOI18N
    }

    private static String escapeString(String s) {
        if (s.length() == 0) {
            return "\"\""; // NOI18N
        }

        StringBuilder sb = new StringBuilder();

        boolean hasSpace = false;
        final int slen = s.length();
        char c;

        for (int i = 0; i < slen; i++) {
            c = s.charAt(i);

            if (Character.isWhitespace(c)) {
                hasSpace = true;
                sb.append(c);

                continue;
            }
            sb.append(c);
        }

        if (hasSpace) {
            sb.insert(0, '"'); // NOI18N
            sb.append('"'); // NOI18N
        }
        return sb.toString();
    }

}
