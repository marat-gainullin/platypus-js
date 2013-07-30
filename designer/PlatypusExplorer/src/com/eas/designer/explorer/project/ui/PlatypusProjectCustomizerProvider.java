/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.project.ui;

import com.eas.designer.application.project.PlatypusProject;
import com.eas.designer.explorer.project.PlatypusProjectImpl;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.spi.project.ui.CustomizerProvider;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author mg
 */
public class PlatypusProjectCustomizerProvider implements CustomizerProvider {

    private final PlatypusProject project;
    public static final String CUSTOMIZER_FOLDER_PATH = "Projects/org-netbeans-modules-platypus/Customizer"; //NO18N
    private static Map<Project, Dialog> project2Dialog = new HashMap<>();

    public PlatypusProjectCustomizerProvider(PlatypusProject aProject) {
        super();
        project = aProject;
    }

    @Override
    public void showCustomizer() {
        Dialog dialog = project2Dialog.get(project);
        if (dialog != null) {
            dialog.setVisible(true);
        } else {
            OptionListener listener = new OptionListener(project);
            dialog = ProjectCustomizer.createCustomizerDialog(CUSTOMIZER_FOLDER_PATH, Lookups.singleton(project), null, listener, null);
            dialog.addWindowListener(listener);
            dialog.setTitle(NbBundle.getMessage(PlatypusProjectCustomizerProvider.class, "LBL_Customizer_Title", ProjectUtils.getInformation(project).getDisplayName()));

            project2Dialog.put(project, dialog);
            dialog.setVisible(true);
        }
    }

    /** Listens to the actions on the Customizer's option buttons */
    private class OptionListener extends WindowAdapter implements ActionListener {

        private Project project;

        OptionListener(Project project) {
            this.project = project;
        }

        // Listening to OK button ----------------------------------------------
        @Override
        public void actionPerformed(ActionEvent e) {
            // Close & dispose the the dialog
            Dialog dialog = project2Dialog.get(project);
            if (dialog != null) {
                dialog.setVisible(false);
                dialog.dispose();
            }
        }

        // Listening to window events ------------------------------------------
        @Override
        public void windowClosed(WindowEvent e) {
            project2Dialog.remove(project);
        }

        @Override
        public void windowClosing(WindowEvent e) {
            //Dispose the dialog otherwsie the {@link WindowAdapter#windowClosed}
            //may not be called
            Dialog dialog = project2Dialog.get(project);
            if (dialog != null) {
                dialog.setVisible(false);
                dialog.dispose();
            }
        }
    }
}
