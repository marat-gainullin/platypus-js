/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.connection.templates;

import com.eas.designer.explorer.files.wizard.NewApplicationElementWizardIterator;
import org.netbeans.api.templates.TemplateRegistration;

/**
 *
 * @author mg
 */
public class NewConnectionWizardIterator extends NewApplicationElementWizardIterator {

    public NewConnectionWizardIterator() {
        super();
    }

    @TemplateRegistration(
            folder = "Platypus application elements",
    position = 300,
    content = "PlatypusConnectionTemplate.pc",
    displayName = "#Templates/Other/PlatypusConnectionTemplate",
    description = "Connection.html",
    scriptEngine = "freemarker")
    public static NewApplicationElementWizardIterator createIterator() {
        return new NewConnectionWizardIterator();
    }
}
