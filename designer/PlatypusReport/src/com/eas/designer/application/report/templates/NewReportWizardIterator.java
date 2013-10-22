/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.report.templates;

import com.eas.designer.explorer.files.wizard.NewApplicationElementWizardIterator;
import org.netbeans.api.templates.TemplateRegistration;

/**
 *
 * @author mg
 */
public class NewReportWizardIterator extends NewApplicationElementWizardIterator {

    public NewReportWizardIterator() {
        super();
    }

    @TemplateRegistration(
            folder = "Platypus application elements",
    position = 300,
    content = {"PlatypusReportTemplate.js", "PlatypusReportTemplate.model", "PlatypusReportTemplate.xlsx"},
    displayName = "#Templates/Other/PlatypusReportTemplate",
    description = "Report.html",
    scriptEngine = "freemarker")
    public static NewApplicationElementWizardIterator createIterator() {
        return new NewReportWizardIterator();
    }
}
