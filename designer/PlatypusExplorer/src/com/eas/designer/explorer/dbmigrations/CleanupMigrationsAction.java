/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.dbmigrations;

import com.eas.designer.explorer.project.PlatypusProject;
import java.awt.event.ActionEvent;
import java.net.URL;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.HtmlBrowser;
import org.openide.awt.StatusDisplayer;
import org.openide.nodes.Node;
import org.openide.util.ContextAwareAction;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.TaskListener;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

/**
 *
 * @author vv
 */
public class CleanupMigrationsAction extends AbstractAction implements ContextAwareAction {

    private final static RequestProcessor RP = new RequestProcessor(CleanupMigrationsAction.class.getName(), 1, false);

    @Override
    public void actionPerformed(ActionEvent e) {
        assert false;
    }

    @Override
    public Action createContextAwareInstance(Lookup actionContext) {
        Node contextNode = actionContext.lookup(Node.class);
        if (contextNode != null && contextNode.getLookup() != null) {
            final PlatypusProject project = contextNode.getLookup().lookup(PlatypusProject.class);
            return new AbstractAction() {
                @Override
                public boolean isEnabled() {
                    return true;
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        
                        J2eeModuleProvider jmp = project.getLookup().lookup(J2eeModuleProvider.class);
                        String url = Deployment.getDefault().deploy(jmp, Deployment.Mode.RUN, null, "", false);
                        HtmlBrowser.URLDisplayer.getDefault().showURL(new URL(url));
                        /*
                        NotifyDescriptor d = new NotifyDescriptor.Confirmation(
                                NbBundle.getMessage(CleanupMigrationsAction.class, "CTL_CleanupMigrationsAction_Dialog_Msg"), // NOI18N
                                NbBundle.getMessage(CleanupMigrationsAction.class, "CTL_CleanupMigrationsAction_Dialog_Title"), // NOI18N
                                NotifyDescriptor.YES_NO_OPTION);
                        if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.YES_OPTION) {
                            cleanupMigrations(project);
                        }*/
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }

                @Override
                public Object getValue(String key) {
                    if (Action.NAME.equals(key)) {
                        return NbBundle.getMessage(CleanupMigrationsAction.class, "CTL_CleanupMigrationsAction"); // NOI18N
                    } else {
                        return super.getValue(key);
                    }
                }
            };
        } else {
            return null;
        }
    }

    private void cleanupMigrations(final PlatypusProject project) {
        RequestProcessor.Task cleanupTask = RP.create(new Runnable() {
            @Override
            public void run() {
                InputOutput io = IOProvider.getDefault().getIO(project.getDisplayName(), false);
                project.getDbMigrator().setOut(io.getOut());
                project.getDbMigrator().setErr(io.getErr());
                project.getDbMigrator().cleanup();
            }
        });
        final ProgressHandle ph = ProgressHandleFactory.createHandle(NbBundle.getMessage(CleanupMigrationsAction.class, "LBL_Cleanup_Migrations_Progress"), cleanupTask); // NOI18N  
        cleanupTask.addTaskListener(new TaskListener() {
            @Override
            public void taskFinished(org.openide.util.Task task) {
                ph.finish();
                StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(CleanupMigrationsAction.class, "LBL_Cleanup_Migrations_Complete")); // NOI18N
            }
        });
        ph.start();
        cleanupTask.schedule(0);
    }
}
