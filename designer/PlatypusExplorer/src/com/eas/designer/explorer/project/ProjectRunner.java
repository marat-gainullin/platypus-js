/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.project;

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
import com.eas.designer.explorer.server.PlatypusServerRunner;
import com.eas.server.PlatypusServer;
import com.eas.util.FileUtils;
import com.eas.util.StringUtils;
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
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.actions.SaveAllAction;
import org.openide.awt.HtmlBrowser;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
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
    private static final String LOG_LEVEL_OPTION_NAME = "D.level"; //NOI18N
    private static final String LOG_HANDLERS_OPTION_NAME = "Dhandlers"; //NOI18N
    private static final String CONSOLE_LOG_HANDLER_NAME = "java.util.logging.ConsoleHandler"; //NOI18N
    private static final String CONSOLE_LOG_HANDLER_LEVEL_OPION_NAME = "Djava.util.logging.ConsoleHandler.level"; //NOI18N
    private static final String CONSOLE_LOG_FORMATTER_OPTION_NAME = "Djava.util.logging.ConsoleHandler.formatter"; //NOI18N
    private static final String LOG_CONFIG_CLASS_OPTION_NAME = "Djava.util.logging.config.class"; //NOI18N
    private static final String EQUALS_SIGN = "="; //NOI18N
    private static final String LOCAL_HOSTNAME = "localhost"; //NOI18N
    private static volatile RequestProcessor.Task runTask;

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
    public static void run(final PlatypusProjectImpl project, final String appElementName) throws Exception {
        saveAll();
        if (runTask == null) {
            runTask = project.getRequestProcessor().create(() -> {
                try {
                    start(project, appElementName, false);
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            });
            final ProgressHandle ph = ProgressHandleFactory.createHandle(NbBundle.getMessage(ProjectRunner.class, "LBL_Application_Running", project.getDisplayName()), runTask); // NOI18N  
            runTask.addTaskListener((org.openide.util.Task task) -> {
                ph.finish();
                runTask = null;
            });
            ph.start();
            runTask.schedule(0);
        }
    }

    /**
     * Starts an application in debug mode.
     *
     * @param project Application's project.
     * @param appElementName Application element's name OR relative path to the
     * executable file.
     * @throws Exception If something goes wrong.
     */
    public static void debug(final PlatypusProjectImpl project, final String appElementName) throws Exception {
        saveAll();
        project.getRequestProcessor().post(() -> {
            try {
                start(project, appElementName, true);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        });
    }

    private static void start(PlatypusProjectImpl aProject, String aModuleName, boolean aDebug) throws Exception {
        if (aModuleName != null && !aModuleName.isEmpty()) {
            FileObject appSrcDir = aProject.getSrcRoot();
            FileObject startJs = appSrcDir.getFileObject(PlatypusProjectSettings.START_JS_FILE_NAME);
            if (startJs == null) {
                startJs = appSrcDir.createData(PlatypusProjectSettings.START_JS_FILE_NAME);
            }
            File startModule = aProject.getIndexer().nameToFile(aModuleName);
            if (startModule != null) {
                String requireCallabckArg = moduleIdToVarName(aModuleName);
                FileObject layoutBrother = FileUtil.findBrother(FileUtil.toFileObject(startModule), PlatypusFiles.FORM_EXTENSION);
                String startMethod = layoutBrother != null ? "show" : "execute";
                String starupScript = String.format(PlatypusProjectSettingsImpl.START_JS_FILE_TEMPLATE, aProject.getSettings().getGlobalAPI() ? "facade" : "environment", aProject.getSettings().getBrowserCacheBusting() ? "" : "//", aProject.getSettings().getGlobalAPI() ? "" : "//", aModuleName, requireCallabckArg, "        var m = new " + requireCallabckArg + "();\n", "        m." + startMethod + "();\n", aModuleName);
                FileUtils.writeString(FileUtil.toFile(startJs), starupScript, PlatypusUtils.COMMON_ENCODING_NAME);
            } else if (aModuleName.toLowerCase().endsWith(PlatypusFiles.JAVASCRIPT_FILE_END)) {
                String moduleId = aModuleName.substring(0, aModuleName.length() - PlatypusFiles.JAVASCRIPT_FILE_END.length());
                String requireCallabckArg = moduleIdToVarName(moduleId);
                String starupScript = String.format(PlatypusProjectSettingsImpl.START_JS_FILE_TEMPLATE, aProject.getSettings().getGlobalAPI() ? "facade" : "environment", aProject.getSettings().getBrowserCacheBusting() ? "" : "//", aProject.getSettings().getGlobalAPI() ? "" : "//", aModuleName, requireCallabckArg, "", "    //...\n", aModuleName);
                FileUtils.writeString(FileUtil.toFile(startJs), starupScript, PlatypusUtils.COMMON_ENCODING_NAME);
            }
        } else {
            throw new IllegalStateException(NbBundle.getMessage(ProjectRunner.class, "MSG_Start_App_Element_Not_Set"));
        }
        InputOutput io = IOProvider.getDefault().getIO(aProject.getDisplayName(), false);
        try {
            io.getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Application_Starting"));
            PlatypusProjectSettings pps = aProject.getSettings();
            String appUrl = null;
            switch (pps.getRunAppServerType()) {
                case J2EE_SERVER:
                    io.getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Deploying_J2EE_Container"));//NOI18N
                    PlatypusWebModuleManager webManager = aProject.getLookup().lookup(PlatypusWebModuleManager.class);
                    if (webManager != null) {
                        appUrl = webManager.start(aDebug);
                    } else {
                        throw new IllegalStateException("An instance of PlatypusWebModuleManager is not found in project's lookup.");
                    }
                    break;
                case PLATYPUS_SERVER:
                    // Because of undeploy() before update Platypus.js runtime in case of web application
                    aProject.getSettings().load();
                    aProject.forceUpdatePlatypusRuntime();
                    boolean startServer = !pps.isNotStartServer();
                    if (startServer) {
                        io.getOut().println("");
                        io.getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Starting_Platypus_Server"));//NOI18N
                        Future<Integer> serverStarting = PlatypusServerRunner.start(aProject, aDebug);
                        if (aDebug) {
                            DebuggerEngine[] startedEngines = DebuggerManager.getDebuggerManager().startDebugging(DebuggerInfo.create(AttachingDICookie.ID, new Object[]{AttachingDICookie.create(LOCAL_HOSTNAME, aProject.getSettings().getDebugServerPort())}));
                            DebuggerEngine justStartedEngine = startedEngines[0];
                            DebuggerManager.getDebuggerManager().addDebuggerListener(new DebuggerManagerAdapter() {

                                @Override
                                public void engineRemoved(DebuggerEngine engine) {
                                    if (engine == justStartedEngine) {
                                        serverStarting.cancel(true);
                                        DebuggerManager.getDebuggerManager().removeDebuggerListener(this);
                                    }
                                }

                            });
                            aProject.getOutputWindowIO().getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Server_Debug_Activated"));//NOI18N
                        }
                        io.getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Waiting_Platypus_Server"));//NOI18N
                        PlatypusServerRunner.waitForServer(LOCAL_HOSTNAME, pps.getServerPort());
                        io.getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Platypus_Server_Started"));//NOI18N
                    }
                    break;
                default:
                    // Because of undeploy() before update Platypus.js runtime in case of web application
                    aProject.getSettings().load();
                    aProject.forceUpdatePlatypusRuntime();
                    break;
            }
            // Clients...
            if (ClientType.PLATYPUS_CLIENT.equals(aProject.getSettings().getRunClientType())) {
                io.getOut().println("");//NOI18N
                io.getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Starting_Platypus_Client"));//NOI18N
                ExecutionDescriptor descriptor = new ExecutionDescriptor()
                        .frontWindow(true)
                        .controllable(true);
                List<String> arguments = new ArrayList<>();
                if (pps.getRunClientVmOptions() != null && !pps.getRunClientVmOptions().isEmpty()) {
                    addArguments(arguments, pps.getRunClientVmOptions());
                    io.getOut().println(String.format(NbBundle.getMessage(ProjectRunner.class, "MSG_VM_Run_Options"), pps.getRunClientVmOptions()));//NOI18N
                }
                if (aDebug) {
                    setDebugArguments(arguments, aProject.getSettings().getDebugClientPort());
                }

                io.getOut().println(String.format(NbBundle.getMessage(ProjectRunner.class, "MSG_Logging_Level"), aProject.getSettings().getClientLogLevel()));//NOI18N
                setupLogging(arguments, aProject.getSettings().getClientLogLevel());

                if (pps.getRunClientOptions() != null && !pps.getRunClientOptions().isEmpty()) {
                    addArguments(arguments, pps.getRunClientOptions());
                    io.getOut().println(String.format(NbBundle.getMessage(ProjectRunner.class, "MSG_Run_Options"), pps.getRunClientOptions()));//NOI18N
                }

                if (AppServerType.NONE.equals(pps.getRunAppServerType())) {
                    String classPath = StringUtils.join(File.pathSeparator, aProject.getApiRoot().getPath(), getDirectoryClasspath(aProject.getLibRoot()));
                    arguments.add(OPTION_PREFIX + CLASSPATH_OPTION_NAME);
                    arguments.add(classPath);

                    arguments.add(PlatypusClientApplication.class.getName());

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
                            if (connection.getPassword() != null && !connection.getPassword().isEmpty()) {
                                arguments.add(ProjectRunner.OPTION_PREFIX + DatasourcesArgsConsumer.DB_PASSWORD_CONF_PARAM);
                                arguments.add(connection.getPassword());
                            }
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
                    arguments.add(aProject.getProjectDirectory().toURI().toASCIIString());
                    io.getOut().println(String.format(NbBundle.getMessage(ProjectRunner.class, "MSG_App_Sources"), aProject.getProjectDirectory().toURI().toASCIIString()));//NOI18N
                } else {
                    try {
                        String classPath = StringUtils.join(File.pathSeparator, PlatypusPlatform.getPlatformBinDirectory().getAbsolutePath() + File.separator + "Application.jar", PlatypusPlatform.getPlatformApiDirectory().getAbsolutePath(), getDirectoryClasspath(FileUtil.toFileObject(PlatypusPlatform.getPlatformExtDirectory())));
                        arguments.add(OPTION_PREFIX + CLASSPATH_OPTION_NAME);
                        arguments.add(classPath);
                    } catch (PlatformHomePathException ex) {
                        io.getOut().println(ex.getMessage());//NOI18N
                        throw new IllegalStateException(ex);
                    }

                    arguments.add(PlatypusClientApplication.class.getName());

                    if (AppServerType.J2EE_SERVER.equals(pps.getRunAppServerType())) {
                        io.getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Deploying_J2EE_Container"));//NOI18N
                        PlatypusWebModuleManager webManager = aProject.getLookup().lookup(PlatypusWebModuleManager.class);
                        if (webManager != null) {
                            appUrl = webManager.start(aDebug);
                        } else {
                            throw new IllegalStateException("An instance of PlatypusWebModuleManager is not found in project's lookup.");
                        }
                    } else/* if (AppServerType.PLATYPUS_SERVER.equals(pps.getRunAppServerType())) */ {
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
                if (aProject.getSettings().getSourcePath() != null && !aProject.getSettings().getSourcePath().isEmpty()) {
                    arguments.add(OPTION_PREFIX + PlatypusClientApplication.SOURCE_PATH_CONF_PARAM);
                    arguments.add(aProject.getSettings().getSourcePath());
                }
                if (aProject.getSettings().getRunUser() != null && !aProject.getSettings().getRunUser().trim().isEmpty() && aProject.getSettings().getRunPassword() != null && !aProject.getSettings().getRunPassword().trim().isEmpty()) {
                    arguments.add(OPTION_PREFIX + PlatypusClientApplication.USER_CMD_SWITCH);
                    arguments.add(aProject.getSettings().getRunUser());
                    arguments.add(OPTION_PREFIX + PlatypusClientApplication.PASSWORD_CMD_SWITCH);
                    arguments.add(aProject.getSettings().getRunPassword());
                    io.getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Login_As_User") + aProject.getSettings().getRunUser());//NOI18N
                }
                ExternalProcessBuilder processBuilder = new ExternalProcessBuilder(JVM_RUN_COMMAND_NAME);
                for (String argument : arguments) {
                    processBuilder = processBuilder.addArgument(argument);
                }
                ExecutionService service = ExecutionService.newService(processBuilder, descriptor, getClientDisplayName(aProject, aDebug));
                io.getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Command_Line") + getCommandLineStr(arguments));//NOI18N
                Future<Integer> clientTask = service.run();
                io.getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Platypus_Client_Started"));//NOI18N
                io.getOut().println();
                if (aDebug) {
                    DebuggerEngine[] startedEngines = DebuggerManager.getDebuggerManager().startDebugging(DebuggerInfo.create(AttachingDICookie.ID, new Object[]{AttachingDICookie.create(LOCAL_HOSTNAME, aProject.getSettings().getDebugClientPort())}));
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
        } catch (MalformedURLException | IllegalStateException ex) {
            io.getErr().println(ex.getMessage());
            io.setErrVisible(true);
            io.select();
        }
    }

    protected static String moduleIdToVarName(String moduleId) {
        String requireCallabckArg = moduleId;
        int lastFileSepIndex = requireCallabckArg.lastIndexOf(File.separator);
        if (lastFileSepIndex != -1) {
            requireCallabckArg = requireCallabckArg.substring(lastFileSepIndex + 1);
        }
        lastFileSepIndex = requireCallabckArg.lastIndexOf("/");
        if (lastFileSepIndex != -1) {
            requireCallabckArg = requireCallabckArg.substring(lastFileSepIndex + 1);
        }
        return StringUtils.replaceUnsupportedSymbols(requireCallabckArg);
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

    public static void setupLogging(List<String> arguments, Level logLevel) {
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

    public static String getDirectoryClasspath(FileObject aDirectory) {
        StringBuilder classpathStr = new StringBuilder();
        File extDir = FileUtil.toFile(aDirectory);
        if (extDir.exists() && extDir.isDirectory()) {
            classpathStr.append(String.format("%s/*", extDir)); //NOI18N
            classpathStr.append(File.pathSeparator);
            classpathStr.append(extDir);
        }
        return classpathStr.toString();
    }

    public static boolean isSetByOption(String command, String options) {
        return options != null && options.contains(OPTION_PREFIX + command);
    }

    public static boolean isConnectionValid(DatabaseConnection connection) {
        return connection.getDisplayName() != null && !connection.getDisplayName().isEmpty() && !connection.getDisplayName().contains(" ") //NOI18N
                && connection.getDatabaseURL() != null && !connection.getDatabaseURL().isEmpty()
                && connection.getUser() != null && !connection.getUser().isEmpty();
    }

    private static String getClientDisplayName(PlatypusProject project, boolean debug) {
        return String.format("%s (%s)", project.getDisplayName(), //NOI18N
                debug ? NbBundle.getMessage(ProjectRunner.class, "LBL_Client_DebugTab_Name") //NOI18N
                        : NbBundle.getMessage(ProjectRunner.class, "LBL_Client_RunTab_Name")); //NOI18N
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
