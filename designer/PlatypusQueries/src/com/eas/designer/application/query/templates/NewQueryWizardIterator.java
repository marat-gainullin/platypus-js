/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.templates;

import com.eas.designer.explorer.files.wizard.NewApplicationElementWizardIterator;
import com.eas.designer.explorer.files.wizard.NewApplicationElementWizardNamePanel;
import com.eas.designer.explorer.project.PlatypusProjectImpl;
import java.util.Map;
import org.netbeans.api.project.Project;
import org.netbeans.api.templates.TemplateRegistration;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.ErrorManager;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author mg
 */
public class NewQueryWizardIterator extends NewApplicationElementWizardIterator {

    public static final String PLATYPUS_QUERY_DB_ID_PARAM_NAME = "datamodelDbId";

    public NewQueryWizardIterator() {
        super();
    }

    @TemplateRegistration(
            folder = "Platypus",
    position = 300,
    content = {"PlatypusQueryTemplate.sql",     "PlatypusQueryTemplate.model",
               "PlatypusQueryTemplate.dialect", "PlatypusQueryTemplate.out"},
    displayName = "#Templates/Other/PlatypusQueryTemplate",
    description = "Query.html",
    scriptEngine = "freemarker")
    public static NewApplicationElementWizardIterator createIterator() {
        return new NewQueryWizardIterator();
    }

    @Override
    protected WizardDescriptor.Panel<WizardDescriptor>[] createPanels(WizardDescriptor wiz) {
        try {
            Project project = Templates.getProject(wiz);
            assert project instanceof PlatypusProjectImpl;
            PlatypusProjectImpl pProject = (PlatypusProjectImpl) project;
            return new WizardDescriptor.Panel[]{new NewQueryWizardSettingsPanel(pProject), new NewApplicationElementWizardNamePanel()};
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
            return new WizardDescriptor.Panel[]{};
        }
    }

    @Override
    protected String[] createSteps() {
        return new String[]{
                    NbBundle.getMessage(NewApplicationElementWizardIterator.class, "LBL_ChooseFileTypeStep"),
                    NbBundle.getMessage(NewQueryWizardSettingsPanel.class, "LBL_QuerySettingsStep"),
                    NbBundle.getMessage(NewApplicationElementWizardIterator.class, "LBL_CreateApplicationElementStep")
                };
    }

    @Override
    protected Map<String, String> achieveParameters(Project project, WizardDescriptor aWiz) {
        Map<String, String> parameters = super.achieveParameters(project, aWiz);
        String dbId = null;
        Object oConnection = aWiz.getProperty(NewQueryWizardSettingsPanel.CONNECTION_PROP_NAME);
        if (oConnection instanceof String) {
            dbId = (String) oConnection;
        }
        parameters.put(PLATYPUS_QUERY_DB_ID_PARAM_NAME, String.valueOf(dbId));
        return parameters;
    }
}
