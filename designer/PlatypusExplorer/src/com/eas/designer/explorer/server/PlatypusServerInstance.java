/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.server;

import com.eas.client.ClientConstants;
import com.eas.deploy.project.PlatypusSettings;
import com.eas.designer.explorer.project.PlatypusProject;
import com.eas.designer.explorer.project.PlatypusProjectSettings;
import com.eas.designer.explorer.project.ProjectRunner;
import com.eas.server.PlatypusServer;
import com.eas.server.ServerMain;
import java.io.File;
import java.util.concurrent.Future;
import java.util.logging.Level;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.ExecutionService;
import org.netbeans.api.extexecution.ExternalProcessBuilder;
import org.netbeans.spi.server.ServerInstanceImplementation;
import org.openide.nodes.Node;
import org.openide.util.ChangeSupport;
import org.openide.windows.InputOutput;

/**
 * Platypus standalone development server.
 *
 * @author vv
 */
public final class PlatypusServerInstance implements ServerInstanceImplementation {

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

    public ServerState getServerState() {
        return serverState;
    }

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
        final InputOutput io = project.getOutputWindowIO();
        ExecutionDescriptor descriptor = new ExecutionDescriptor()
                .frontWindow(true)
                .controllable(true)
                .preExecution(new Runnable() {
            @Override
            public void run() {
                setServerState(PlatypusServerInstance.ServerState.STARTING);
            }
        })
                .postExecution(new Runnable() {
            @Override
            public void run() {
                setServerState(PlatypusServerInstance.ServerState.STOPPED);
                serverRunTask = null;
                io.getOut().println("Platypus Server stopped.");
                io.getOut().println();
            }
        });
        ExternalProcessBuilder processBuilder = new ExternalProcessBuilder(ProjectRunner.JVM_RUN_COMMAND_NAME);
        if (debug) {
            processBuilder = ProjectRunner.setDebugArguments(processBuilder, project.getSettings().getDebugServerPort());
        }
        PlatypusSettings ps = project.getSettings().getAppSettings();
        processBuilder = processBuilder.addArgument(ProjectRunner.OPTION_PREFIX + ProjectRunner.CLASSPATH_OPTION_NAME);
        processBuilder = processBuilder.addArgument(ProjectRunner.getExtendedClasspath(getExecutablePath(binDir)));

        processBuilder = processBuilder.addArgument(ServerMain.class.getName());

        if (!project.getSettings().isDbAppSources()) {
            processBuilder = processBuilder.addArgument(ProjectRunner.OPTION_PREFIX + ServerMain.APP_PATH_PARAM1);
            processBuilder = processBuilder.addArgument(project.getProjectDirectory().getPath());
            io.getOut().println(String.format("Server application sources: %s.", project.getProjectDirectory().getPath()));
        }

        processBuilder = processBuilder.addArgument(ProjectRunner.OPTION_PREFIX + ServerMain.APP_DB_URL_CONF_PARAM);
        processBuilder = processBuilder.addArgument(ps.getDbSettings().getUrl());
        processBuilder = processBuilder.addArgument(ProjectRunner.OPTION_PREFIX + ServerMain.APP_DB_USERNAME_CONF_PARAM);
        processBuilder = processBuilder.addArgument(ps.getDbSettings().getInfo().getProperty(ClientConstants.DB_CONNECTION_USER_PROP_NAME));
        processBuilder = processBuilder.addArgument(ProjectRunner.OPTION_PREFIX + ServerMain.APP_DB_PASSWORD_CONF_PARAM);
        processBuilder = processBuilder.addArgument(ps.getDbSettings().getInfo().getProperty(ClientConstants.DB_CONNECTION_PASSWORD_PROP_NAME));
        processBuilder = processBuilder.addArgument(ProjectRunner.OPTION_PREFIX + ServerMain.APP_DB_SCHEMA_CONF_PARAM);
        processBuilder = processBuilder.addArgument(ps.getDbSettings().getInfo().getProperty(ClientConstants.DB_CONNECTION_SCHEMA_PROP_NAME));

        if (!ProjectRunner.isSetByOption(ServerMain.IFACE_CONF_PARAM, project.getSettings().getRunClientOptions())) {
            processBuilder = processBuilder.addArgument(ProjectRunner.OPTION_PREFIX + ServerMain.IFACE_CONF_PARAM);
            processBuilder = processBuilder.addArgument(getListenInterfaceArgument(project.getSettings()));
            io.getOut().println(String.format("Server interface: %s.", getListenInterfaceArgument(project.getSettings())));
        }
        if (!ProjectRunner.isSetByOption(ServerMain.PROTOCOLS_CONF_PARAM, project.getSettings().getRunClientOptions())) {
            processBuilder = processBuilder.addArgument(ProjectRunner.OPTION_PREFIX + ServerMain.PROTOCOLS_CONF_PARAM);
            processBuilder = processBuilder.addArgument(getProtocol(project.getSettings()));
            io.getOut().println(String.format("Server protocol: %s.", getProtocol(project.getSettings())));
        }
        if (project.getSettings().getRunClientOptions() != null && !project.getSettings().getRunClientOptions().isEmpty()) {
            String[] optionalArgs = project.getSettings().getRunClientOptions().split(" ");// NOI18N
            if (optionalArgs.length > 0) {
                for (int i = 0; i < optionalArgs.length; i++) {
                    processBuilder = processBuilder.addArgument(optionalArgs[i]);
                }
            }
            io.getOut().println(String.format("Server options: %s.", project.getSettings().getRunClientOptions()));
        }
        //set default log level if not set explicitly
        if (!ProjectRunner.isSetByOption(ServerMain.LOGLEVEL_CONF_PARAM, project.getSettings().getRunClientOptions())) {
            processBuilder = processBuilder.addArgument(ProjectRunner.OPTION_PREFIX + ServerMain.LOGLEVEL_CONF_PARAM);
            processBuilder = processBuilder.addArgument(Level.INFO.getName());
            io.getOut().println(String.format("Server logging level set to: %s.", Level.INFO.getName()));
        }
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

    public static enum ServerState {

        STARTING,
        RUNNING,
        STOPPED,
        UNKNOWN
    }
}
