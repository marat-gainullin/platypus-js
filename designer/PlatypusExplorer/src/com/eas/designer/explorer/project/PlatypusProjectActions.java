/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.project;

import com.eas.client.AppCache;
import com.eas.client.cache.FilesAppCache;
import com.eas.designer.explorer.j2ee.PlatypusWebModuleManager;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.ui.support.DefaultProjectOperations;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.TaskListener;
import org.openide.windows.InputOutput;

/**
 *
 * @author mg
 */
public class PlatypusProjectActions implements ActionProvider {

    private final static RequestProcessor RP = new RequestProcessor(PlatypusProjectActions.class.getName(), 1, false);
    public static final String COMMAND_DEPLOY = "deploy"; // NOI18N
    public static final String COMMAND_IMPORT = "import"; // NOI18N
    public static final String COMMAND_CONNECT = "connect-to-db"; // NOI18N
    public static final String COMMAND_DISCONNECT = "disconnect-from-db"; // NOI18N
    public static final String COMMAND_CLEAN = "clear"; // NOI18N
    /**
     * Some routine global actions for which we can supply a display name. These
     * are IDE-specific.
     */
    private static final Set<String> COMMON_IDE_GLOBAL_ACTIONS = new HashSet<>(Arrays.asList(
            COMMAND_RUN,
            COMMAND_DEBUG,
            COMMAND_DELETE,
            COMMAND_COPY,
            COMMAND_MOVE,
            COMMAND_RENAME,
            COMMAND_DEPLOY,
            COMMAND_IMPORT,
            COMMAND_CONNECT,
            COMMAND_DISCONNECT,
            COMMAND_CLEAN));
    protected PlatypusProject project;

    public PlatypusProjectActions(PlatypusProject aProject) {
        super();
        project = aProject;
    }

    @Override
    public String[] getSupportedActions() {
        return COMMON_IDE_GLOBAL_ACTIONS.toArray(new String[0]);
    }

    @Override
    public void invokeAction(String actionCommand, Lookup aLookup) throws IllegalArgumentException {
        try {
            switch (actionCommand) {
                case COMMAND_DELETE:
                    DefaultProjectOperations.performDefaultDeleteOperation(project);
                    break;
                case COMMAND_COPY:
                    DefaultProjectOperations.performDefaultCopyOperation(project);
                    break;
                case COMMAND_RENAME:
                    DefaultProjectOperations.performDefaultRenameOperation(project, null);
                    break;
                case COMMAND_MOVE:
                    DefaultProjectOperations.performDefaultMoveOperation(project);
                    break;
                case COMMAND_RUN:
                    ProjectRunner.run(project, project.getSettings().getAppSettings().getRunElement());
                    break;
                case COMMAND_DEBUG:
                    ProjectRunner.debug(project, project.getSettings().getAppSettings().getRunElement());
                    break;
                case COMMAND_DEPLOY:
                    deploy();
                    break;
                case COMMAND_IMPORT:
                    importApplication();
                    break;
                case COMMAND_CONNECT:
                    project.startConnecting2db();
                    break;
                case COMMAND_DISCONNECT:
                    project.disconnectFormDb();
                    break;
                case COMMAND_CLEAN:
                    clean();
                    break;
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    @Override
    public boolean isActionEnabled(String command, Lookup aLookup) throws IllegalArgumentException {
        if (COMMAND_DISCONNECT.equals(command)) {
            return project.isDbConnected();
        } else if (COMMAND_CONNECT.equals(command)) {
            return !project.isDbConnected();
        } else if (COMMAND_DEPLOY.equals(command) || COMMAND_IMPORT.equals(command)) {
            return project.isDbConnected() && !project.getDeployer().isBusy();
        } else if (COMMAND_CLEAN.equals(command)) {
            PlatypusWebModuleManager pwmm = project.getLookup().lookup(PlatypusWebModuleManager.class);
            assert pwmm != null;
            return pwmm.webDirExists();
        }else if (COMMON_IDE_GLOBAL_ACTIONS.contains(command)) {
            return true;
        }
        return false;
    }

    private void deploy() {
        if (project.isDbConnected()) {
            RequestProcessor.Task deployTask = RP.create(new Runnable() {
                @Override
                public void run() {
                    InputOutput io = project.getOutputWindowIO();
                    project.getDeployer().setOut(io.getOut());
                    project.getDeployer().setErr(io.getErr());
                    project.getDeployer().deploy();
                }
            });
            final ProgressHandle ph = ProgressHandleFactory.createHandle(NbBundle.getMessage(PlatypusProjectActions.class, "LBL_Deploy_Progress"), deployTask); // NOI18N  
            deployTask.addTaskListener(new TaskListener() {
                @Override
                public void taskFinished(org.openide.util.Task task) {
                    ph.finish();
                    StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(PlatypusProjectActions.class, "LBL_Deploy_Complete")); // NOI18N
                }
            });
            ph.start();
            deployTask.schedule(0);
        }
    }

    private void importApplication() {
        if (project.isDbConnected()) {
            RequestProcessor.Task importTask = RP.create(new Runnable() {
                @Override
                public void run() {
                    InputOutput io = project.getOutputWindowIO();
                    project.getDeployer().setOut(io.getOut());
                    project.getDeployer().setErr(io.getErr());

                    try {
                        AppCache cache = project.getClient().getAppCache();
                        if (cache instanceof FilesAppCache) {
                            ((FilesAppCache) cache).unwatch();
                        }
                        try {
                            project.getDeployer().importApplication();
                        } finally {
                            if (cache instanceof FilesAppCache) {
                                ((FilesAppCache) cache).watch();
                            }
                        }
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
            final ProgressHandle ph = ProgressHandleFactory.createHandle(NbBundle.getMessage(PlatypusProjectActions.class, "LBL_Import_Progress"), importTask); // NOI18N  
            importTask.addTaskListener(new TaskListener() {
                @Override
                public void taskFinished(org.openide.util.Task task) {
                    ph.finish();
                    StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(PlatypusProjectActions.class, "LBL_Import_Complete")); // NOI18N
                }
            });
            ph.start();
            importTask.schedule(0);
        }
    }

    private void clean() {     
        PlatypusWebModuleManager pwmm = project.getLookup().lookup(PlatypusWebModuleManager.class);
        assert pwmm != null;
        try {
            project.getOutputWindowIO().getOut().println(NbBundle.getMessage(PlatypusProjectActions.class, "MSG_Cleaning_Web_Dir")); // NOI18N
            pwmm.clearWebDir();
            project.getOutputWindowIO().getOut().println(NbBundle.getMessage(PlatypusProjectActions.class, "MSG_Cleaning_Web_Dir_Complete")); // NOI18N
        } catch (IOException ex) {
            Logger.getLogger(PlatypusProjectActions.class.getName()).log(Level.SEVERE, "Error clearning web directory", ex);
            project.getOutputWindowIO().getErr().println(ex.getMessage());
        }   
    }
}
