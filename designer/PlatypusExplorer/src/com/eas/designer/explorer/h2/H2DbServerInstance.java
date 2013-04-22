/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.h2;

import com.eas.designer.explorer.platform.EmptyPlatformHomePathException;
import com.eas.designer.explorer.platform.PlatypusPlatform;
import com.eas.designer.explorer.project.PlatypusProjectActions;
import com.eas.designer.explorer.project.ProjectRunner;
import com.eas.designer.explorer.server.ServerSupport;
import java.io.File;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.ExecutionService;
import org.netbeans.api.extexecution.ExternalProcessBuilder;
import org.openide.awt.StatusDisplayer;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author vv
 */
public class H2DbServerInstance {

    
    private final String H2_INSTANCE_NAME = "H2"; // NOI18N
    private final String H2_DIRECTORY_NAME = "h2"; // NOI18N
    private final String H2_CONSOLE_CLASS_NAME = "org.h2.tools.Console"; // NOI18N
    private final String H2_TCP_OPTION = "-tcp"; // NOI18N
    private final String H2_WEB_OPTION = "-web"; // NOI18N
    private final String H2_TOOL_OPTION = "-tool"; // NOI18N
    private final int H2_DEFAULT_PORT = 9092;
    private volatile ServerState serverState = ServerState.STOPPED;
    private ChangeSupport changeSupport = new ChangeSupport(this);
    private Future<Integer> serverRunTask;
    private static H2DbServerInstance platypusDevDbServer;

    public static synchronized H2DbServerInstance getInstance() {
        if (platypusDevDbServer == null) {
            platypusDevDbServer = new H2DbServerInstance();
        }
        return platypusDevDbServer;
    }

    public void addChangeListener(final ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    public void removeChangeListener(final ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    public ServerState getServerState() {
        return serverState;
    }

    public void setServerState(ServerState aServerState) {
        serverState = aServerState;
        changeSupport.fireChange();
    }

    public void start() {
        ExecutionDescriptor descriptor = new ExecutionDescriptor()
                .frontWindow(true)
                .controllable(true)
                .preExecution(new Runnable() {
            @Override
            public void run() {
                setServerState(ServerState.STARTING);
            }
        })
                .postExecution(new Runnable() {
            @Override
            public void run() {
                setServerState(ServerState.STOPPED);
                serverRunTask = null;
            }
        });
        File h2Dir = null;
        File libDir;
        try {       
            libDir = PlatypusPlatform.getThirdpartyLibDirectory();
            h2Dir = new File(libDir, H2_DIRECTORY_NAME);         
        } catch (EmptyPlatformHomePathException | IllegalStateException ex) {
            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(H2DbServerInstance.class, "LBL_Unable_Start_H2_Path")); // NOI18N
            if (PlatypusPlatform.showPlatformHomeDialog()) {
                try {
                    libDir = PlatypusPlatform.getThirdpartyLibDirectory();
                    h2Dir = new File(libDir, H2_DIRECTORY_NAME);
                } catch (EmptyPlatformHomePathException | IllegalStateException ex1) {
                    StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(H2DbServerInstance.class, "LBL_Unable_Start_H2_Path")); // NOI18N
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
        Future<Integer> runTask = service.run();
        serverRunTask = runTask;
        if (serverRunTask != null) {
            try {
                ServerSupport.waitForServer(ServerSupport.LOCAL_HOST, H2_DEFAULT_PORT);
            } catch (ServerSupport.ServerTimeOutException | InterruptedException ex) {
                stop();
                setServerState(ServerState.STOPPED);
                return;
            } 
            setServerState(ServerState.RUNNING);
        }
    }

    public void stop() {
        serverRunTask.cancel(true);
        serverRunTask = null;
    }

    private String getClasspath(File dir) {
        return dir.getAbsolutePath() + "/*"; //NOI18N
    }

    public static enum ServerState {

        STARTING,
        RUNNING,
        STOPPED,
        UNKNOWN
    }

    protected String getDisplayName() {
        return H2_INSTANCE_NAME;
    }
}
