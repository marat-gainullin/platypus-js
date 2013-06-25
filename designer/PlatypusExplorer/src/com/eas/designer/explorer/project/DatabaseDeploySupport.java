/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.project;

import com.eas.client.AppCache;
import com.eas.client.cache.FilesAppCache;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.awt.StatusDisplayer;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.TaskListener;
import org.openide.windows.InputOutput;

/**
 *
 * @author vv
 */
public class DatabaseDeploySupport {

    public static void deploy(final PlatypusProject project) {
        if (project.isDbConnected()) {
            RequestProcessor.Task deployTask = project.RP.create(new Runnable() {
                @Override
                public void run() {
                    InputOutput io = project.getOutputWindowIO();
                    project.getDeployer().setOut(io.getOut());
                    project.getDeployer().setErr(io.getErr());
                    project.getDeployer().deploy();
                }
            });
            final ProgressHandle ph = ProgressHandleFactory.createHandle(NbBundle.getMessage(DatabaseDeploySupport.class, "LBL_Deploy_Progress"), deployTask); // NOI18N  
            deployTask.addTaskListener(new TaskListener() {
                @Override
                public void taskFinished(org.openide.util.Task task) {
                    ph.finish();
                    StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(DatabaseDeploySupport.class, "LBL_Deploy_Complete")); // NOI18N
                }
            });
            ph.start();
            deployTask.schedule(0);
        }
    }

    public static void importApplication(final PlatypusProject project) {
        if (project.isDbConnected()) {
            RequestProcessor.Task importTask = project.RP.create(new Runnable() {
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
            final ProgressHandle ph = ProgressHandleFactory.createHandle(NbBundle.getMessage(DatabaseDeploySupport.class, "LBL_Import_Progress"), importTask); // NOI18N  
            importTask.addTaskListener(new TaskListener() {
                @Override
                public void taskFinished(org.openide.util.Task task) {
                    ph.finish();
                    StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(DatabaseDeploySupport.class, "LBL_Import_Complete")); // NOI18N
                }
            });
            ph.start();
            importTask.schedule(0);
        }
    }
}
