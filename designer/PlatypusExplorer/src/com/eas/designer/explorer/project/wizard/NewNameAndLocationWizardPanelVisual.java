/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.project.wizard;

import java.io.File;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

/**
 *
 * @author mg
 */
public class NewNameAndLocationWizardPanelVisual extends NameAndLocationWizardPanelVisual {

    public NewNameAndLocationWizardPanelVisual(NameAndLocationWizardPanel aPanel) {
        super(aPanel);
    }

    @Override
    protected boolean isProjectDestFolderValid(WizardDescriptor wizardDescriptor, String aPath) {
        final File destFolder = FileUtil.normalizeFile(new File(aPath).getAbsoluteFile());
        if (destFolder.exists()) {
            wizardDescriptor.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE,
                    NbBundle.getMessage(NewNameAndLocationWizardPanelVisual.class, "PlatypusApplicationPanelVisual.projectFileAlreadyExists"));
            return false;
        }
        return true;
    }

}
