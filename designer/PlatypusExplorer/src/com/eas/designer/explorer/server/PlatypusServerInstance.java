/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.server;

import com.eas.client.resourcepool.DatasourcesArgsConsumer;
import com.eas.designer.application.project.PlatypusProject;
import com.eas.designer.application.project.PlatypusProjectSettings;
import com.eas.designer.explorer.project.ProjectRunner;
import static com.eas.designer.explorer.project.ProjectRunner.getCommandLineStr;
import static com.eas.designer.explorer.project.ProjectRunner.setLogging;
import com.eas.server.PlatypusServer;
import com.eas.server.ServerMain;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;
import org.netbeans.api.db.explorer.ConnectionManager;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.ExecutionService;
import org.netbeans.api.extexecution.ExternalProcessBuilder;
import org.netbeans.spi.server.ServerInstanceImplementation;
import org.openide.nodes.Node;
import org.openide.util.ChangeSupport;
import org.openide.util.NbBundle;
import org.openide.windows.InputOutput;

/**
 * Platypus standalone development server.
 *
 * @author vv
 */
public final class PlatypusServerInstance implements Server, ServerInstanceImplementation {

    private final String PLATYPUS_SERVER_NAME = "Platypus Server"; // NOI18N
    private final String PLATYPUS_SERVER_INSTANCE_NAME = "Platypus Server"; // NOI18N
    private static final String ANY_LOCAL_ADRESS = "0.0.0.0";// NOI18N
    private static final String ARGUMENT_SEPARATOR = ":";// NOI18N
    private static final String SERVER_APP_NAME = "Server.jar"; //NOI18N
    private JPanel customizer;
    private Future<Integer> serverRunTask;
    private volatile ServerState serverState = ServerState.STOPPED;
    private PlatypusProject project;
    private final ChangeSupport changeSupport = new ChangeSupport(this);

    @Override
    public Node getFullNode() {
        return new PlatypusServerNode(this);
    }

    @Override
    public Node getBasicNode() {
        return new PlatypusServerNode(this);
    }

    @Override
    public JComponent getCustomizer() {
        synchronized (this) {
            if (customizer == null) {
                customizer = new PlatypusServerCustomizer(this);
            }
            return customizer;
        }
    }

    @Override
    public String getDisplayName() {
        return PLATYPUS_SERVER_INSTANCE_NAME;
    }

    @Override
    public String getServerDisplayName() {
        return PLATYPUS_SERVER_NAME;
    }

    @Override
    public boolean isRemovable() {
        return false;
    }

    @Override
    public void remove() {
        //do not remove dev server
    }

    public PlatypusProject getProject() {
        return project;
    }

    @Override
    public ServerState getServerState() {
        return serverState;
    }

    @Override
    public void setServerState(ServerState aServerState) {
        serverState = aServerState;
        changeSupport.fireChange();
    }

    public void addChangeListener(final ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    public void removeChangeListener(final ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    public boolean start(PlatypusProject aProject, File binDir, boolean debug) {
        project = aProject;
        assert project != null;
        setServerState(ServerState.STARTING);
        final InputOutput io = project.getOutputWindowIO();
        ExecutionDescriptor descriptor = new ExecutionDescriptor()
                .frontWindow(true)
                .controllable(true)
                .preExecution(() -> {
                })
                .postExecution(() -> {
                    setServerState(ServerState.STOPPED);
                    serverRunTask = null;
                    io.getOut().println(NbBundle.getMessage(PlatypusServerInstance.class, "MSG_Server_Stopped"));//NOI18N
                    io.getOut().println();
                });
        //ExternalProcessBuilder processBuilder = new ExternalProcessBuilder(ProjectRunner.JVM_RUN_COMMAND_NAME);
        List<String> arguments = new ArrayList<>();
        if (project.getSettings().getRunServerVmOptions() != null && !project.getSettings().getRunServerVmOptions().isEmpty()) {
            ProjectRunner.addArguments(arguments, project.getSettings().getRunServerVmOptions());
            io.getOut().println(String.format(NbBundle.getMessage(PlatypusServerInstance.class, "MSG_VM_Run_Options"),//NOI18N
                    project.getSettings().getRunServerVmOptions()));
        }
        if (debug) {
            ProjectRunner.setDebugArguments(arguments, project.getSettings().getDebugServerPort());
        }

        io.getOut().println(String.format(NbBundle.getMessage(ProjectRunner.class, "MSG_Logging_Level"), project.getSettings().getClientLogLevel()));//NOI18N
        setLogging(arguments, project.getSettings().getServerLogLevel());

        PlatypusProjectSettings pps = project.getSettings();

        arguments.add(ProjectRunner.OPTION_PREFIX + ProjectRunner.CLASSPATH_OPTION_NAME);
        String classPath = ProjectRunner.getExtendedClasspath(ProjectRunner.getApiClasspath(getExecutablePath(binDir)));
        arguments.add("\"" + classPath + "\"");
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
                if(connection.getPassword() != null && !connection.getPassword().isEmpty()){
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
            io.getErr().println(NbBundle.getMessage(PlatypusServerInstance.class, "MSG_Missing_App_Database"));
        }
        arguments.add(ProjectRunner.OPTION_PREFIX + ServerMain.APP_ELEMENT_CONF_PARAM);
        arguments.add(PlatypusProjectSettings.START_JS_FILE_NAME);

        arguments.add(ProjectRunner.OPTION_PREFIX + ServerMain.APP_URL_CONF_PARAM);
        arguments.add(project.getProjectDirectory().toURI().toASCIIString());
        io.getOut().println(String.format(NbBundle.getMessage(PlatypusServerInstance.class, "MSG_App_Sources"),//NOI18N
                project.getProjectDirectory().toURI().toASCIIString()));

        if (!ProjectRunner.isSetByOption(ServerMain.IFACE_CONF_PARAM, project.getSettings().getRunClientOptions())) {
            arguments.add(ProjectRunner.OPTION_PREFIX + ServerMain.IFACE_CONF_PARAM);
            arguments.add(getListenInterfaceArgument(project.getSettings()));
            io.getOut().println(String.format(NbBundle.getMessage(PlatypusServerInstance.class, "MSG_Server_Interface"),//NOI18N
                    getListenInterfaceArgument(project.getSettings())));
        }
        if (!ProjectRunner.isSetByOption(ServerMain.PROTOCOLS_CONF_PARAM, project.getSettings().getRunClientOptions())) {
            arguments.add(ProjectRunner.OPTION_PREFIX + ServerMain.PROTOCOLS_CONF_PARAM);
            arguments.add(getProtocol(project.getSettings()));
            io.getOut().println(String.format(NbBundle.getMessage(PlatypusServerInstance.class, "MSG_Server_Protocol"), getProtocol(project.getSettings())));//NOI18N
        }
        if (project.getSettings().getRunClientOptions() != null && !project.getSettings().getRunClientOptions().isEmpty()) {
            ProjectRunner.addArguments(arguments, project.getSettings().getRunClientOptions());
            io.getOut().println(String.format(NbBundle.getMessage(PlatypusServerInstance.class, "MSG_Run_Options"),//NOI18N
                    project.getSettings().getRunClientOptions()));
        }
        ExternalProcessBuilder processBuilder = new ExternalProcessBuilder(ProjectRunner.JVM_RUN_COMMAND_NAME);
        for (String argument : arguments) {
            processBuilder = processBuilder.addArgument(argument);
        }
        ExecutionService service = ExecutionService.newService(processBuilder, descriptor, "Platypus Server");
        io.getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Command_Line") + getCommandLineStr(arguments));//NOI18N
        Future<Integer> runTask = service.run();
        serverRunTask = runTask;
        return true;
    }

    public void stop() {
        if (serverRunTask != null) {
            serverRunTask.cancel(true);
            serverRunTask = null;
        }
    }

    private static String getExecutablePath(File aBinDir) {
        File clientAppExecutable = new File(aBinDir, SERVER_APP_NAME);
        if (!clientAppExecutable.exists()) {
            throw new IllegalStateException("Platypus Server executable not exists.");
        }
        return clientAppExecutable.getAbsolutePath();
    }

    private static String getListenInterfaceArgument(PlatypusProjectSettings settings) {
        return ANY_LOCAL_ADRESS + ARGUMENT_SEPARATOR + settings.getServerPort();
    }

    private static String getProtocol(PlatypusProjectSettings settings) {
        return settings.getServerPort() + ARGUMENT_SEPARATOR + PlatypusServer.DEFAULT_PROTOCOL;
    }
}
