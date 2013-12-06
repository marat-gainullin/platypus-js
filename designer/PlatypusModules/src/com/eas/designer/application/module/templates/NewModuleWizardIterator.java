/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.templates;

import com.eas.designer.explorer.files.wizard.NewApplicationElementWizardIterator;
import org.netbeans.api.templates.TemplateRegistration;
import org.netbeans.api.templates.TemplateRegistrations;

/**
 *
 * @author mg
 */
public class NewModuleWizardIterator extends NewApplicationElementWizardIterator {

    public NewModuleWizardIterator() {
        super();
    }

    @TemplateRegistrations({
        @TemplateRegistration(
                folder = "Platypus application elements",
                position = 300,
                content = {"PlatypusModuleTemplate.js", "PlatypusModuleTemplate.model"},
                displayName = "#Templates/Other/PlatypusModuleTemplate",
                description = "Module.html",
                scriptEngine = "freemarker"),
        @TemplateRegistration(
                folder = "Platypus application elements",
                position = 300,
                content = {"PlatypusModuleDatasourceTemplate.js", "PlatypusModuleDatasourceTemplate.model"},
                displayName = "#Templates/Other/PlatypusModuleDatasourceTemplate",
                description = "ModuleDatasource.html",
                scriptEngine = "freemarker"),
        @TemplateRegistration(
                folder = "Platypus application elements",
                position = 300,
                content = {"PlatypusModuleValidatorTemplate.js", "PlatypusModuleValidatorTemplate.model"},
                displayName = "#Templates/Other/PlatypusModuleValidatorTemplate",
                description = "ModuleValidator.html",
                scriptEngine = "freemarker")})
    public static NewApplicationElementWizardIterator createIterator() {
        return new NewModuleWizardIterator();
    }
}
