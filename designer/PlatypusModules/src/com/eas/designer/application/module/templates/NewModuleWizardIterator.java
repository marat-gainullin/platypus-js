/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.templates;

import com.eas.designer.explorer.files.wizard.NewApplicationElementWizardIterator;
import org.netbeans.api.templates.TemplateRegistration;

/**
 *
 * @author mg
 */
public class NewModuleWizardIterator extends NewApplicationElementWizardIterator {

    public NewModuleWizardIterator() {
        super();
    }

    @TemplateRegistration(
            folder = "Platypus application elements",
    position = 300,
    content = {"PlatypusModuleTemplate.js", "PlatypusModuleTemplate.model"},
    displayName = "#Templates/Other/PlatypusModuleTemplate",
    description = "Module.html",
    scriptEngine = "freemarker")
    public static NewApplicationElementWizardIterator createIterator() {
        return new NewModuleWizardIterator();
    }
}
