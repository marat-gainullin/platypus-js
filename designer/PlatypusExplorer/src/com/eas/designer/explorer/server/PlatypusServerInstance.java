/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.server;

import com.eas.deploy.project.PlatypusSettings;
import com.eas.designer.application.project.PlatypusProject;
import com.eas.designer.application.project.PlatypusProjectSettings;
import com.eas.designer.explorer.project.ProjectRunner;
import static com.eas.designer.explorer.project.ProjectRunner.setLogging;
import com.eas.server.PlatypusServer;
import com.eas.server.ServerMain;
import java.io.File;
import java.util.concurrent.Future;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;
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
    private ChangeSupport changeSupport = new ChangeSupport(this);

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
                .preExecution(new Runnable() {
            @Override
            public void run() {
            }
        })
                .postExecution(new Runnable() {
            @Override
            public void run() {
                setServerState(ServerState.STOPPED);
                serverRunTask = null;
                io.getOut().println(NbBundle.getMessage(PlatypusServerInstance.class, "MSG_Server_Stopped"));//NOI18N
                io.getOut().println();
            }
        });
        ExternalProcessBuilder processBuilder = new ExternalProcessBuilder(ProjectRunner.JVM_RUN_COMMAND_NAME);
        if (project.getSettings().getRunServerVmOptions() != null && !project.getSettings().getRunServerVmOptions().isEmpty()) {
            processBuilder = ProjectRunner.addArguments(processBuilder, project.getSettings().getRunServerVmOptions());
            io.getOut().println(String.format(NbBundle.getMessage(PlatypusServerInstance.class, "MSG_VM_Run_Options"),//NOI18N
                    project.getSettings().getRunServerVmOptions()));
        }
        if (debug) {
            processBuilder = ProjectRunner.setDebugArguments(processBuilder, project.getSettings().getDebugServerPort());
        }
        
        io.getOut().println(String.format(NbBundle.getMessage(ProjectRunner.class, "MSG_Logging_Level"), project.getSettings().getClientLogLevel()));//NOI18N
        processBuilder = setLogging(processBuilder, project.getSettings().getServerLogLevel());
        
        PlatypusSettings ps = project.getSettings().getAppSettings();
        processBuilder = processBuilder.addArgument(ProjectRunner.OPTION_PREFIX + ProjectRunner.CLASSPATH_OPTION_NAME);
        processBuilder = processBuilder.addArgument(ProjectRunner.getExtendedClasspath(getExecutablePath(binDir)));

        processBuilder = processBuilder.addArgument(ServerMain.class.getName());

        if (!project.getSettings().isDbAppSources()) {
            processBuilder = processBuilder.addArgument(ProjectRunner.OPTION_PREFIX + ServerMain.APP_PATH_PARAM1);
            processBuilder = processBuilder.addArgument(project.getProjectDirectory().getPath());
            io.getOut().println(String.format(NbBundle.getMessage(PlatypusServerInstance.class, "MSG_App_Sources"),//NOI18N
                    project.getProjectDirectory().getPath()));
        } else {
            io.getOut().println(NbBundle.getMessage(PlatypusServerInstance.class, "MSG_App_Sources_Database"));//NOI18N
        }

        processBuilder = processBuilder.addArgument(ProjectRunner.OPTION_PREFIX + ServerMain.APP_DB_URL_CONF_PARAM);
        processBuilder = processBuilder.addArgument(ps.getDbSettings().getUrl());
        processBuilder = processBuilder.addArgument(ProjectRunner.OPTION_PREFIX + ServerMain.APP_DB_USERNAME_CONF_PARAM);
        processBuilder = processBuilder.addArgument(ps.getDbSettings().getUser());
        processBuilder = processBuilder.addArgument(ProjectRunner.OPTION_PREFIX + ServerMain.APP_DB_PASSWORD_CONF_PARAM);
        processBuilder = processBuilder.addArgument(ps.getDbSettings().getPassword());
        processBuilder = processBuilder.addArgument(ProjectRunner.OPTION_PREFIX + ServerMain.APP_DB_SCHEMA_CONF_PARAM);
        processBuilder = processBuilder.addArgument(ps.getDbSettings().getSchema());

        if (!ProjectRunner.isSetByOption(ServerMain.IFACE_CONF_PARAM, project.getSettings().getRunClientOptions())) {
            processBuilder = processBuilder.addArgument(ProjectRunner.OPTION_PREFIX + ServerMain.IFACE_CONF_PARAM);
            processBuilder = processBuilder.addArgument(getListenInterfaceArgument(project.getSettings()));
            io.getOut().println(String.format(NbBundle.getMessage(PlatypusServerInstance.class, "MSG_Server_Interface"),//NOI18N
                    getListenInterfaceArgument(project.getSettings())));
        }
        if (!ProjectRunner.isSetByOption(ServerMain.PROTOCOLS_CONF_PARAM, project.getSettings().getRunClientOptions())) {
            processBuilder = processBuilder.addArgument(ProjectRunner.OPTION_PREFIX + ServerMain.PROTOCOLS_CONF_PARAM);
            processBuilder = processBuilder.addArgument(getProtocol(project.getSettings()));
            io.getOut().println(String.format(NbBundle.getMessage(PlatypusServerInstance.class, "MSG_Server_Protocol"), getProtocol(project.getSettings())));//NOI18N
        }
        if (project.getSettings().getRunClientOptions() != null && !project.getSettings().getRunClientOptions().isEmpty()) {
            processBuilder = ProjectRunner.addArguments(processBuilder, project.getSettings().getRunClientOptions());
            io.getOut().println(String.format(NbBundle.getMessage(PlatypusServerInstance.class, "MSG_Run_Options"),//NOI18N
                    project.getSettings().getRunClientOptions()));
        }
        //set default log level if not set explicitly
        /* TODO: Take into account, that loglevel and other logging options are configured as system properties
        if (!ProjectRunner.isSetByOption(ServerMain.LOGLEVEL_CONF_PARAM, project.getSettings().getRunClientOptions())) {
            processBuilder = processBuilder.addArgument(ProjectRunner.OPTION_PREFIX + ServerMain.LOGLEVEL_CONF_PARAM);
            processBuilder = processBuilder.addArgument(project.getSettings().getServerLogLevel().getName());
            io.getOut().println(String.format(NbBundle.getMessage(PlatypusServerInstance.class, "MSG_Logging_Level"), project.getSettings().getServerLogLevel().getName()));//NOI18N
        }
        */ 
        ExecutionService service = ExecutionService.newService(processBuilder, descriptor, "Platypus Server");
        Future<Integer> runTask = service.run();
        serverRunTask = runTask;
        return runTask != null;
    }

    public void stop() {
        serverRunTask.cancel(true);
        serverRunTask = null;
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
