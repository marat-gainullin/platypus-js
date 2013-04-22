/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.dbmigrations;

import com.eas.designer.explorer.project.PlatypusProject;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
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
public class ApplyMigrationsAction extends AbstractAction implements ContextAwareAction {

    private final static RequestProcessor RP = new RequestProcessor(ApplyMigrationsAction.class.getName(), 1, false);

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
                    return project.isDbConnected();
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (project.isDbConnected()) {
                            applyMigrations(project);
                        }
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }

                @Override
                public Object getValue(String key) {
                    if (Action.NAME.equals(key)) {
                        return NbBundle.getMessage(ApplyMigrationsAction.class, "CTL_ApplyMigrationsAction"); // NOI18N
                    } else {
                        return super.getValue(key);
                    }
                }
            };
        } else {
            return null;
        }
    }

    private void applyMigrations(final PlatypusProject project) {
        RequestProcessor.Task applyTask = RP.create(new Runnable() {
            @Override
            public void run() {
                InputOutput io = project.getOutputWindowIO();
                project.getDbMigrator().setOut(io.getOut());
                project.getDbMigrator().setErr(io.getErr());
                project.getDbMigrator().applyMigrations();
            }
        });
        final ProgressHandle ph = ProgressHandleFactory.createHandle(NbBundle.getMessage(ApplyMigrationsAction.class, "LBL_Apply_Migrations_Progress"), applyTask); // NOI18N  
        applyTask.addTaskListener(new TaskListener() {
            @Override
            public void taskFinished(org.openide.util.Task task) {
                ph.finish();
                StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(ApplyMigrationsAction.class, "LBL_Apply_Migrations_Complete")); // NOI18N
            }
        });
        ph.start();
        applyTask.schedule(0);
    }
}
