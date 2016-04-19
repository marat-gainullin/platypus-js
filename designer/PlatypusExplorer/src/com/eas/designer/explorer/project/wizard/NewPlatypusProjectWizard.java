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
public class NewPlatypusProjectWizard extends PlatypusProjectWizard {
    
    @TemplateRegistration(folder = "Project/Platypus.js",
            position = 100,
            content = "/com/eas/designer/explorer/project/wizard/NewPlatypusApplicationProject",
            displayName = "com.eas.designer.explorer.project.wizard.Bundle#Templates/Project/Platypus/NewPlatypusApplicationProject",
            description = "NewPlatypusApplicationDescription.html",
            iconBase = "com/eas/designer/explorer/project/pencil-ruler.png")
    public static NewPlatypusProjectWizard createIterator() {
        return new NewPlatypusProjectWizard();
    }
    
    @Override
    protected WizardDescriptor.Panel<WizardDescriptor>[] createPanels() {
        return new WizardDescriptor.Panel[]{
                    new NewNameAndLocationWizardPanel()
                };
    }
}
