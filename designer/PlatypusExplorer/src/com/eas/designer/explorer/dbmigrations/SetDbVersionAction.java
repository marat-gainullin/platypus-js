/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.dbmigrations;

import com.eas.designer.explorer.project.PlatypusProject;
import com.eas.util.StringUtils;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.NotifyDescriptor.InputLine;
import org.openide.awt.StatusDisplayer;
import org.openide.nodes.Node;
import org.openide.util.ContextAwareAction;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.TaskListener;

/**
 *
 * @author vv
 */
public class SetDbVersionAction extends AbstractAction implements ContextAwareAction {

    private final static RequestProcessor RP = new RequestProcessor(SetDbVersionAction.class.getName(), 1, false);

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
                            InputLine d = new NotifyDescriptor.InputLine(
                                    NbBundle.getMessage(SetDbVersionAction.class, "CTL_SetDbVersionAction_Dialog_Msg"), // NOI18N
                                    NbBundle.getMessage(SetDbVersionAction.class, "CTL_SetDbVersionAction_Dialog_Title") // NOI18N
                                    );
                            d.setInputText(String.valueOf(project.getDbMigrator().getCurrentDbVersion()));
                            if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION) {
                                try {
                                    int n = Integer.parseInt(d.getInputText());
                                    if (n >= 0) {
                                        setDbVersion(project, n);
                                        return;
                                    }
                                } catch (Exception ex) {
                                    //no-op, incorrect version number
                                }
                                StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(SetDbVersionAction.class, "LBL_Set_Db_Version_Error")); // NOI18N
                            }
                        }
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }

                @Override
                public Object getValue(String key) {
                    if (Action.NAME.equals(key)) {
                        return NbBundle.getMessage(SetDbVersionAction.class, "CTL_SetDbVersionAction"); // NOI18N
                    } else {
                        return super.getValue(key);
                    }
                }
            };
        } else {
            return null;
        }
    }

    private void setDbVersion(final PlatypusProject project, final int version) {
        RequestProcessor.Task setDbVersionTask = RP.create(new Runnable() {
            @Override
            public void run() {
                project.getDbMigrator().setCurrentDbVersion(version);
            }
        });
        final ProgressHandle ph = ProgressHandleFactory.createHandle(NbBundle.getMessage(SetDbVersionAction.class, "LBL_LBL_Set_Db_Version_Progress"), setDbVersionTask); // NOI18N  
        setDbVersionTask.addTaskListener(new TaskListener() {
            @Override
            public void taskFinished(org.openide.util.Task task) {
                ph.finish();
                StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(SetDbVersionAction.class, "LBL_LBL_Set_Db_Version_Complete")); // NOI18N
            }
        });
        ph.start();
        setDbVersionTask.schedule(0);
    }
}
