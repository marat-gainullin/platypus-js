/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.resources.templates;

import com.eas.designer.explorer.files.wizard.NewApplicationElementWizardIterator;
import org.netbeans.api.templates.TemplateRegistration;
import org.netbeans.api.templates.TemplateRegistrations;

/**
 *
 * @author mg
 */
public class NewFormWizardIterator extends NewApplicationElementWizardIterator {

    public NewFormWizardIterator() {
        super();
    }

    @TemplateRegistrations({
        @TemplateRegistration(
                folder = "Platypus application elements",
                position = 400,
                content = {
                    "PlatypusFormTemplate.js", "PlatypusFormTemplate.layout", "PlatypusFormTemplate.model"
                },
                displayName = "#Templates/Other/PlatypusFormTemplate",
                description = "Form.html",
                scriptEngine = "freemarker"),
        @TemplateRegistration(
                folder = "Platypus application elements/Resources",
                position = 410,
                content = {
                    "PlatypusLayoutTemplate.layout"
                },
                displayName = "#Templates/Other/PlatypusLayoutTemplate",
                description = "Layout.html",
                scriptEngine = "freemarker")})
    public static NewFormWizardIterator createIterator() {
        return new NewFormWizardIterator();
    }
}
