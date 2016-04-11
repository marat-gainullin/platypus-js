/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.h2;

import com.eas.designer.application.platform.PlatformHomePathException;
import com.eas.designer.application.platform.PlatypusPlatform;
import com.eas.designer.explorer.platform.PlatypusPlatformDialog;
import com.eas.designer.explorer.project.ProjectRunner;
import com.eas.designer.explorer.server.Server;
import com.eas.designer.explorer.server.ServerState;
import com.eas.designer.explorer.server.ServerSupport;
import com.eas.designer.application.utils.DatabaseServerType;
import java.io.File;
import java.util.concurrent.Future;
import javax.swing.event.ChangeListener;
import org.netbeans.api.db.explorer.ConnectionManager;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.ExecutionService;
import org.netbeans.api.extexecution.ExternalProcessBuilder;
import org.netbeans.spi.db.explorer.DatabaseRuntime;
import org.openide.awt.StatusDisplayer;
import org.openide.util.ChangeSupport;
import org.openide.util.NbBundle;

/**
 *
 * @author vv
 */
public class H2Dabatabase implements DatabaseRuntime, Server {

    private final String H2_INSTANCE_NAME = "H2"; // NOI18N
    private final String H2_DIRECTORY_NAME = "h2"; // NOI18N
    private final String H2_CONSOLE_CLASS_NAME = "org.h2.tools.Console"; // NOI18N
    private final String H2_TCP_OPTION = "-tcp"; // NOI18N
    private final String H2_WEB_OPTION = "-web"; // NOI18N
    private final String H2_TOOL_OPTION = "-tool"; // NOI18N
    private final int H2_DEFAULT_PORT = 9092;
    private volatile ServerState serverState = ServerState.STOPPED;
    private final ChangeSupport changeSupport = new ChangeSupport(this);
    private Future<Integer> serverRunTask;
    private static volatile H2Dabatabase platypusDevDbServer;

    private H2Dabatabase() {
    }

    public static H2Dabatabase getDefault() {
        if (platypusDevDbServer == null) {
            synchronized (H2Dabatabase.class) {
                if (platypusDevDbServer == null) {
                    platypusDevDbServer = new H2Dabatabase();
                }
            }
        }
        return platypusDevDbServer;
    }

    @Override
    public String getJDBCDriverClass() {
        return DatabaseServerType.H2.jdbcClassName;
    }

    @Override
    public boolean acceptsDatabaseURL(String url) {
        return url.trim().startsWith("jdbc:h2:tcp://");//NOI18N
    }

    @Override
    public boolean isRunning() {
        return ServerState.RUNNING == getServerState();
    }

    @Override
    public boolean canStart() {
        return ServerState.STOPPED == getServerState();
    }

    public void addChangeListener(final ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    public void removeChangeListener(final ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
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

    @Override
    public void start() {
        ExecutionDescriptor descriptor = new ExecutionDescriptor()
                .frontWindow(true)
                .controllable(true)
                .preExecution(() -> {
                })
                .postExecution(() -> {
                    setServerState(ServerState.STOPPED);
                    disconnectAllH2Connections();
                    serverRunTask = null;
                });
        File h2Dir = null;
        File libDir;
        try {
            libDir = PlatypusPlatform.getThirdpartyLibDirectory();
            h2Dir = new File(libDir, H2_DIRECTORY_NAME);
        } catch (PlatformHomePathException | IllegalStateException ex) {
            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(H2Dabatabase.class, "LBL_Unable_Start_H2_Path")); // NOI18N
            if (PlatypusPlatformDialog.showPlatformHomeDialog()) {
                try {
                    libDir = PlatypusPlatform.getThirdpartyLibDirectory();
                    h2Dir = new File(libDir, H2_DIRECTORY_NAME);
                } catch (PlatformHomePathException | IllegalStateException ex1) {
                    StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(H2Dabatabase.class, "LBL_Unable_Start_H2_Path")); // NOI18N
                    return;
                }
            } else {
                return;
            }
        }

        ExternalProcessBuilder processBuilder = new ExternalProcessBuilder(ProjectRunner.JVM_RUN_COMMAND_NAME);

        processBuilder = processBuilder.addArgument(ProjectRunner.OPTION_PREFIX + ProjectRunner.CLASSPATH_OPTION_NAME);
        processBuilder = processBuilder.addArgument(getClasspath(h2Dir));

        processBuilder = processBuilder.addArgument(H2_CONSOLE_CLASS_NAME);

        processBuilder = processBuilder.addArgument(H2_TCP_OPTION);
        processBuilder = processBuilder.addArgument(H2_WEB_OPTION);
        processBuilder = processBuilder.addArgument(H2_TOOL_OPTION);

        ExecutionService service = ExecutionService.newService(processBuilder, descriptor, "H2 Database");
        setServerState(ServerState.STARTING);
        Future<Integer> runTask = service.run();
        serverRunTask = runTask;
        if (serverRunTask != null) {
            try {
                ServerSupport ss = new ServerSupport(this);
                ss.waitForServer(ServerSupport.LOCAL_HOST, H2_DEFAULT_PORT);
            } catch (ServerSupport.ServerTimeOutException | ServerSupport.ServerStoppedException | InterruptedException ex) {
                stop();
                setServerState(ServerState.STOPPED);
                return;
            }
            setServerState(ServerState.RUNNING);
        }
    }

    @Override
    public void stop() {
        disconnectAllH2Connections();
        serverRunTask.cancel(true);
        serverRunTask = null;
    }

    private void disconnectAllH2Connections() {
        for (DatabaseConnection connection : ConnectionManager.getDefault().getConnections()) {
            if (getDefault().acceptsDatabaseURL(connection.getDatabaseURL())) {
                ConnectionManager.getDefault().disconnect(connection);
            }
        }
    }

    private String getClasspath(File dir) {
        return dir.getAbsolutePath() + "/*"; //NOI18N
    }

    protected String getDisplayName() {
        return H2_INSTANCE_NAME;
    }
}
