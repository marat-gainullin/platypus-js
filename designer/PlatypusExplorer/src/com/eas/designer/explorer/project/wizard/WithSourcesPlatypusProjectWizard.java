/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.project.wizard;

import org.netbeans.api.templates.TemplateRegistration;
import org.openide.WizardDescriptor;

/**
 *
 * @author mg
 */
public class WithSourcesPlatypusProjectWizard extends PlatypusProjectWizard {

    @TemplateRegistration(folder = "Project/Platypus.js",
            position = 110,
            content = "/com/eas/designer/explorer/project/wizard/WithSourcesPlatypusApplicationProject",
            displayName = "com.eas.designer.explorer.project.wizard.Bundle#Templates/Project/Platypus/WithSourcesPlatypusApplicationProject",
            description = "WithSourcesPlatypusApplicationDescription.html",
            iconBase = "com/eas/designer/explorer/project/pencil-ruler.png")
    public static WithSourcesPlatypusProjectWizard createIterator() {
        return new WithSourcesPlatypusProjectWizard();
    }

    @Override
    protected WizardDescriptor.Panel<WizardDescriptor>[] createPanels() {
        return new WizardDescriptor.Panel[]{
                    new WithSourcesNameAndLocationWizardPanel()
                };
    }
}
