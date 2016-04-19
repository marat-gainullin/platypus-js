/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.server;

import com.eas.client.resourcepool.DatasourcesArgsConsumer;
import com.eas.designer.application.project.PlatypusProject;
import com.eas.designer.application.project.PlatypusProjectSettings;
import com.eas.designer.explorer.project.ProjectRunner;
import com.eas.server.PlatypusServer;
import com.eas.server.ServerMain;
import com.eas.util.StringUtils;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import org.netbeans.api.db.explorer.ConnectionManager;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.ExecutionService;
import org.netbeans.api.extexecution.ExternalProcessBuilder;
import org.openide.util.NbBundle;
import org.openide.windows.InputOutput;

/**
 * Platypus standalone development server runner.
 *
 * @author vv
 */
public final class PlatypusServerRunner {

    private static final String ARGUMENT_SEPARATOR = ":";// NOI18N
    private static final int SERVER_POLL_TIMEOUT = 60 * 1000;
    private static final int SERVER_POLL_RECONNECT_TIME = 200;

    @SuppressWarnings("SleepWhileInLoop") //NOI18N
    public static void waitForServer(String host, int port) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < SERVER_POLL_TIMEOUT) {
            try {
                try (Socket s = new Socket(host, port)) {
                    assert s != null;
                    return;
                }
            } catch (IOException ex) {
                //no-op
            }
            Thread.sleep(SERVER_POLL_RECONNECT_TIME);
        }
    }

    public static Future<Integer> start(PlatypusProject aProject, boolean aDebugEnabled) {
        final InputOutput io = aProject.getOutputWindowIO();
        ExecutionDescriptor descriptor = new ExecutionDescriptor()
                .frontWindow(true)
                .controllable(true)
                .preExecution(() -> {
                })
                .postExecution(() -> {
                    io.getOut().println(NbBundle.getMessage(PlatypusServerRunner.class, "MSG_Server_Stopped"));//NOI18N
                    io.getOut().println();
                });
        List<String> arguments = new ArrayList<>();
        if (aProject.getSettings().getRunServerVmOptions() != null && !aProject.getSettings().getRunServerVmOptions().isEmpty()) {
            ProjectRunner.addArguments(arguments, aProject.getSettings().getRunServerVmOptions());
            io.getOut().println(String.format(NbBundle.getMessage(PlatypusServerRunner.class, "MSG_VM_Run_Options"),//NOI18N
                    aProject.getSettings().getRunServerVmOptions()));
        }
        if (aDebugEnabled) {
            ProjectRunner.setDebugArguments(arguments, aProject.getSettings().getDebugServerPort());
        }

        io.getOut().println(String.format(NbBundle.getMessage(PlatypusServerRunner.class, "MSG_Logging_Level"), aProject.getSettings().getServerLogLevel()));//NOI18N
        ProjectRunner.setupLogging(arguments, aProject.getSettings().getServerLogLevel());

        PlatypusProjectSettings pps = aProject.getSettings();

        arguments.add(ProjectRunner.OPTION_PREFIX + ProjectRunner.CLASSPATH_OPTION_NAME);

        String classPath = StringUtils.join(File.pathSeparator, aProject.getApiRoot().getPath(), ProjectRunner.getDirectoryClasspath(aProject.getLibRoot()));
        arguments.add(classPath);
        arguments.add(ServerMain.class.getName());

        // Iterate through all datasources, registered in the designer.
        // Apply them as datasources in considered server.
        DatabaseConnection defaultDatabaseConnection = null;
        DatabaseConnection[] dataSources = ConnectionManager.getDefault().getConnections();
        for (DatabaseConnection connection : dataSources) {
            if (ProjectRunner.isConnectionValid(connection)) {
                if (connection.getDisplayName() == null ? pps.getDefaultDataSourceName() == null : connection.getDisplayName().equals(pps.getDefaultDataSourceName())) {
                    defaultDatabaseConnection = connection;
                }
                arguments.add(ProjectRunner.OPTION_PREFIX + DatasourcesArgsConsumer.DB_RESOURCE_CONF_PARAM);
                arguments.add(connection.getDisplayName());// Hack because of netbeans
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

        if (defaultDatabaseConnection != null) {
            arguments.add(ProjectRunner.OPTION_PREFIX + ServerMain.DEF_DATASOURCE_CONF_PARAM);
            arguments.add(pps.getDefaultDataSourceName());
        } else if (pps.getDefaultDataSourceName() != null && !pps.getDefaultDataSourceName().isEmpty()) {
            io.getErr().println(NbBundle.getMessage(PlatypusServerRunner.class, "MSG_Missing_App_Database"));
        }
        if (aProject.getSettings().getGlobalAPI()) {
            arguments.add(ProjectRunner.OPTION_PREFIX + ServerMain.GLOBAL_API_CONF_PARAM);
        }

        arguments.add(ProjectRunner.OPTION_PREFIX + ServerMain.APP_ELEMENT_CONF_PARAM);
        arguments.add(PlatypusProjectSettings.START_JS_FILE_NAME);

        arguments.add(ProjectRunner.OPTION_PREFIX + ServerMain.APP_URL_CONF_PARAM);
        arguments.add(aProject.getProjectDirectory().toURI().toASCIIString());
        io.getOut().println(String.format(NbBundle.getMessage(PlatypusServerRunner.class, "MSG_App_Sources"),//NOI18N
                aProject.getProjectDirectory().toURI().toASCIIString()));
        if (aProject.getSettings().getSourcePath() != null && !aProject.getSettings().getSourcePath().isEmpty()) {
            arguments.add(ProjectRunner.OPTION_PREFIX + ServerMain.SOURCE_PATH_CONF_PARAM);
            arguments.add(aProject.getSettings().getSourcePath());
        }

        if (!ProjectRunner.isSetByOption(ServerMain.IFACE_CONF_PARAM, aProject.getSettings().getRunServerOptions())) {
            arguments.add(ProjectRunner.OPTION_PREFIX + ServerMain.IFACE_CONF_PARAM);
            arguments.add(getListenInterfaceArgument(aProject.getSettings()));
            io.getOut().println(String.format(NbBundle.getMessage(PlatypusServerRunner.class, "MSG_Server_Interface"),//NOI18N
                    getListenInterfaceArgument(aProject.getSettings())));
        }
        if (!ProjectRunner.isSetByOption(ServerMain.PROTOCOLS_CONF_PARAM, aProject.getSettings().getRunServerOptions())) {
            arguments.add(ProjectRunner.OPTION_PREFIX + ServerMain.PROTOCOLS_CONF_PARAM);
            arguments.add(getProtocol(aProject.getSettings()));
            io.getOut().println(String.format(NbBundle.getMessage(PlatypusServerRunner.class, "MSG_Server_Protocol"), getProtocol(aProject.getSettings())));//NOI18N
        }
        if (aProject.getSettings().getRunServerOptions() != null && !aProject.getSettings().getRunServerOptions().isEmpty()) {
            ProjectRunner.addArguments(arguments, aProject.getSettings().getRunServerOptions());
            io.getOut().println(String.format(NbBundle.getMessage(PlatypusServerRunner.class, "MSG_Run_Options"),//NOI18N
                    aProject.getSettings().getRunServerOptions()));
        }
        ExternalProcessBuilder processBuilder = new ExternalProcessBuilder(ProjectRunner.JVM_RUN_COMMAND_NAME);
        for (String argument : arguments) {
            processBuilder = processBuilder.addArgument(argument);
        }
        ExecutionService service = ExecutionService.newService(processBuilder, descriptor, getServerDisplayName(aProject, aDebugEnabled));
        io.getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Command_Line") + ProjectRunner.getCommandLineStr(arguments));//NOI18N
        return service.run();
    }

    private static String getServerDisplayName(PlatypusProject project, boolean debug) {
        return String.format("%s (%s)", project.getDisplayName(), //NOI18N
                debug ? NbBundle.getMessage(PlatypusServerRunner.class, "LBL_Server_DebugTab_Name") //NOI18N
                        : NbBundle.getMessage(PlatypusServerRunner.class, "LBL_Server_RunTab_Name")); //NOI18N
    }

    private static String getListenInterfaceArgument(PlatypusProjectSettings aSettings) {
        return "0.0.0.0" + ARGUMENT_SEPARATOR + aSettings.getServerPort();
    }

    private static String getProtocol(PlatypusProjectSettings aSettings) {
        return aSettings.getServerPort() + ARGUMENT_SEPARATOR + PlatypusServer.DEFAULT_PROTOCOL;
    }
}
