/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.project;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.ui.support.DefaultProjectOperations;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author mg
 */
public class PlatypusProjectActions implements ActionProvider {

    public static final String COMMAND_CONNECT = "connect-to-db"; // NOI18N
    public static final String COMMAND_DISCONNECT = "disconnect-from-db"; // NOI18N
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
            COMMAND_CONNECT,
            COMMAND_DISCONNECT));

    protected PlatypusProjectImpl project;

    public PlatypusProjectActions(PlatypusProjectImpl aProject) {
        super();
        project = aProject;
    }

    @Override
    public String[] getSupportedActions() {
        return COMMON_IDE_GLOBAL_ACTIONS.toArray(new String[]{});
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
                    run(false);
                    break;
                case COMMAND_DEBUG:
                    run(true);
                    break;
                case COMMAND_CONNECT:
                    project.startConnecting2db(project.getSettings().getDefaultDataSourceName());
                    break;
                case COMMAND_DISCONNECT:
                    project.disconnectFormDb(project.getSettings().getDefaultDataSourceName());
                    break;
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    @Override
    public boolean isActionEnabled(String command, Lookup aLookup) throws IllegalArgumentException {
        if (COMMAND_DISCONNECT.equals(command)) {
            return project.isDbConnected(project.getSettings().getDefaultDataSourceName());
        } else if (COMMAND_CONNECT.equals(command)) {
            return !project.isDbConnected(project.getSettings().getDefaultDataSourceName());
        } else if (COMMON_IDE_GLOBAL_ACTIONS.contains(command)) {
            return true;
        }
        return false;
    }

    private void run(boolean debug) throws Exception {
        String runAppElement = project.getSettings().getRunElement();
        if (runAppElement == null || runAppElement.isEmpty()) {
            final SelectAppElementPanel panel = new SelectAppElementPanel(project);
            DialogDescriptor dd = new DialogDescriptor(panel, NbBundle.getMessage(PlatypusProjectActions.class, "MSG_Run_Element_Dialog"));//NOI18N
            if (DialogDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(dd))) {
                runAppElement = panel.getAppElementId();
                if (panel.isSaveAsDefault()) {
                    project.getSettings().setRunElement(runAppElement);
                    project.getSettings().save();
                }
            } else {
                return;
            }
        }
        if (!debug) {
            ProjectRunner.run(project, runAppElement);
        } else {
            ProjectRunner.debug(project, runAppElement);
        }
    }
}
