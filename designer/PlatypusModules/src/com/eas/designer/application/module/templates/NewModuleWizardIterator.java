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
                folder = "Platypus/Resources",
                position = 300,
                content = {"PlatypusModelTemplate.model"},
                displayName = "#Templates/Other/PlatypusModelTemplate",
                description = "Model.html",
                scriptEngine = "freemarker"),
        @TemplateRegistration(
                folder = "Platypus",
                position = 300,
                content = {"PlatypusAMDModuleTemplate.js", "PlatypusAMDModuleTemplate.model"},
                displayName = "#Templates/Other/PlatypusAMDModuleTemplate",
                description = "Module.html",
                scriptEngine = "freemarker"),
        @TemplateRegistration(
                folder = "Platypus/Specific",
                position = 300,
                content = {"PlatypusAMDModuleValidatorTemplate.js", "PlatypusAMDModuleValidatorTemplate.model"},
                displayName = "#Templates/Other/PlatypusAMDModuleValidatorTemplate",
                description = "ModuleValidator.html",
                scriptEngine = "freemarker"),
        @TemplateRegistration(
                folder = "Platypus/Specific",
                position = 300,
                content = {"PlatypusAMDModuleResidentTemplate.js", "PlatypusAMDModuleResidentTemplate.model"},
                displayName = "#Templates/Other/PlatypusAMDModuleResidentTemplate",
                description = "ModuleResident.html",
                scriptEngine = "freemarker"),
        @TemplateRegistration(
                folder = "Platypus/Specific",
                position = 300,
                content = {"PlatypusAMDModuleStatelessTemplate.js", "PlatypusAMDModuleStatelessTemplate.model"},
                displayName = "#Templates/Other/PlatypusAMDModuleStatelessTemplate",
                description = "ModuleStateless.html",
                scriptEngine = "freemarker"),
        @TemplateRegistration(
                folder = "Platypus/Specific",
                position = 300,
                content = {"PlatypusAMDModuleStatefullTemplate.js", "PlatypusAMDModuleStatefullTemplate.model"},
                displayName = "#Templates/Other/PlatypusAMDModuleStatefullTemplate",
                description = "ModuleStatefull.html",
                scriptEngine = "freemarker"),
        @TemplateRegistration(
                folder = "Platypus/Global modules",
                position = 300,
                content = {"PlatypusModuleTemplate.js", "PlatypusModuleTemplate.model"},
                displayName = "#Templates/Other/PlatypusModuleTemplate",
                description = "Module.html",
                scriptEngine = "freemarker"),
        @TemplateRegistration(
                folder = "Platypus/Global modules/Specific",
                position = 300,
                content = {"PlatypusModuleValidatorTemplate.js", "PlatypusModuleValidatorTemplate.model"},
                displayName = "#Templates/Other/PlatypusModuleValidatorTemplate",
                description = "ModuleValidator.html",
                scriptEngine = "freemarker"),
        @TemplateRegistration(
                folder = "Platypus/Global modules/Specific",
                position = 300,
                content = {"PlatypusModuleResidentTemplate.js", "PlatypusModuleResidentTemplate.model"},
                displayName = "#Templates/Other/PlatypusModuleResidentTemplate",
                description = "ModuleResident.html",
                scriptEngine = "freemarker"),
        @TemplateRegistration(
                folder = "Platypus/Global modules/Specific",
                position = 300,
                content = {"PlatypusModuleStatelessTemplate.js", "PlatypusModuleStatelessTemplate.model"},
                displayName = "#Templates/Other/PlatypusModuleStatelessTemplate",
                description = "ModuleStateless.html",
                scriptEngine = "freemarker"),
        @TemplateRegistration(
                folder = "Platypus/Global modules/Specific",
                position = 300,
                content = {"PlatypusModuleStatefullTemplate.js", "PlatypusModuleStatefullTemplate.model"},
                displayName = "#Templates/Other/PlatypusModuleStatefullTemplate",
                description = "ModuleStatefull.html",
                scriptEngine = "freemarker")
    })
    public static NewApplicationElementWizardIterator createIterator() {
        return new NewModuleWizardIterator();
    }
}
