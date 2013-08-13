/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.project.ui;

import com.eas.designer.explorer.project.PlatypusProjectImpl;
import javax.swing.JComponent;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.ErrorManager;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author mg
 */
@ProjectCustomizer.CompositeCategoryProvider.Registration(category = "applicationdatabase", categoryLabel = "#applicationdatabase", projectType = "org-netbeans-modules-platypus", position=20)
public class ApplicationDatabaseCategoryProvider implements ProjectCustomizer.CompositeCategoryProvider {

    @Override
    public ProjectCustomizer.Category createCategory(Lookup lkp) {
        return ProjectCustomizer.Category.create("applicationdatabase", NbBundle.getMessage(PlatypusProjectCustomizerProvider.class, "applicationdatabase"), null, new ProjectCustomizer.Category[]{});
    }

    @Override
    public JComponent createComponent(ProjectCustomizer.Category ctgr, Lookup lkp) {
        try {
            PlatypusProjectImpl project = lkp.lookup(PlatypusProjectImpl.class);
            return new ProjectDatabaseCustomizer(project);
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
            return null;
        }
    }
}
