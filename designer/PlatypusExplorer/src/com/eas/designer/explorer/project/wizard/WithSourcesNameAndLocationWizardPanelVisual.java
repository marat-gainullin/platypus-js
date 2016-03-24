/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.project.wizard;

import com.eas.designer.explorer.project.PlatypusProjectFactory;
import com.eas.designer.explorer.project.PlatypusProjectSettingsImpl;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author mg
 */
public class WithSourcesNameAndLocationWizardPanelVisual extends NameAndLocationWizardPanelVisual {

    public WithSourcesNameAndLocationWizardPanelVisual(NameAndLocationWizardPanel aPanel) {
        super(aPanel);
    }

    @Override
    protected boolean isProjectDestFolderValid(WizardDescriptor wizardDescriptor, String aPath) {
        Path destPath = Paths.get(Utilities.toURI(new File(aPath)));
        Path destAppPath = destPath.resolve(PlatypusProjectSettingsImpl.DEFAULT_APP_FOLDER);
        Path destPropertiesPath = destPath.resolve(PlatypusProjectSettingsImpl.PROJECT_SETTINGS_FILE);
        if (destPath.toFile().exists()
                && destAppPath.toFile().exists()
                && destPropertiesPath.toFile().exists()) {
            wizardDescriptor.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE,
                    NbBundle.getMessage(NewNameAndLocationWizardPanelVisual.class, "PlatypusApplicationPanelVisual.alreadyPlatypusJs"));
            return false;
        }
        return true;
    }

}
