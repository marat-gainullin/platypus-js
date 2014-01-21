/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.dbmigrations;

import com.eas.designer.explorer.project.PlatypusProjectImpl;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.ErrorManager;
import org.openide.awt.StatusDisplayer;
import org.openide.nodes.Node;
import org.openide.util.ContextAwareAction;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.TaskListener;
import org.openide.windows.InputOutput;

/**
 *
 * @author vv
 */
public class AddDdMetadataMigrationAction extends AbstractAction implements ContextAwareAction {

    private final static RequestProcessor RP = new RequestProcessor(AddDdMetadataMigrationAction.class.getName(), 1, false);

    @Override
    public void actionPerformed(ActionEvent e) {
        assert false;
    }

    @Override
    public Action createContextAwareInstance(Lookup actionContext) {
        Node contextNode = actionContext.lookup(Node.class);
        if (contextNode != null && contextNode.getLookup() != null) {
            final PlatypusProjectImpl project = contextNode.getLookup().lookup(PlatypusProjectImpl.class);
            return new AbstractAction() {
                @Override
                public boolean isEnabled() {
                    return project.isDbConnected(project.getSettings().getAppSettings().getDefaultDatasource());
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        createDbMetadataMigration(project);
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }

                @Override
                public Object getValue(String key) {
                    if (Action.NAME.equals(key)) {
                        return NbBundle.getMessage(AddDdMetadataMigrationAction.class, "CTL_AddMetadataMigrationAction"); // NOI18N
                    } else {
                        return super.getValue(key);
                    }
                }
            };
        } else {
            return null;
        }
    }

    private void createDbMetadataMigration(final PlatypusProjectImpl project) {
        RequestProcessor.Task createMigrationTask = RP.create(new Runnable() {
            @Override
            public void run() {
                InputOutput io = project.getOutputWindowIO();
                project.getDbMigrator().setOut(io.getOut());
                project.getDbMigrator().setErr(io.getErr());
                project.getDbMigrator().createDbMetadataMigration();
            }
        });
        final ProgressHandle ph = ProgressHandleFactory.createHandle(NbBundle.getMessage(AddSqlMigrationAction.class, "LBL_Create_Mtd_Migration_Progress"), createMigrationTask); // NOI18N  
        createMigrationTask.addTaskListener(new TaskListener() {
            @Override
            public void taskFinished(org.openide.util.Task task) {
                ph.finish();
                StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(AddSqlMigrationAction.class, "LBL_Create_Mtd_Migration_Complete")); // NOI18N
                try {
                    project.getDbMigrationsRoot().refresh();
                } catch (Exception ex) {
                    ErrorManager.getDefault().notify(ex);
                }
            }
        });
        ph.start();
        createMigrationTask.schedule(0);
    }
}
