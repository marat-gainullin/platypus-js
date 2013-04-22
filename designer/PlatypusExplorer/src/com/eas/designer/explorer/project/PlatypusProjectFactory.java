/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.project;

import com.eas.deploy.BaseDeployer;
import java.io.IOException;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Gala
 */
@org.openide.util.lookup.ServiceProvider(service = org.netbeans.spi.project.ProjectFactory.class, position = 839)
public final class PlatypusProjectFactory implements ProjectFactory {

    @Override
    public boolean isProject(FileObject fo) {
        return fo.isFolder()
                && fo.getFileObject(BaseDeployer.PLATYPUS_SETTINGS_FILE) != null
                && fo.getFileObject(PlatypusProjectSettings.PROJECT_SETTINGS_FILE) != null;
    }

    @Override
    public Project loadProject(FileObject fo, ProjectState ps) throws IOException {
        try {
            if (isProject(fo)) {
                return new PlatypusProject(fo, ps);
            } else {
                return null;
            }
        } catch (Exception ex) {
            // no-op
        }
        return null;
    }

    @Override
    public void saveProject(Project aProject) throws IOException, ClassCastException {
        if (aProject instanceof PlatypusProject) {
            try {
                PlatypusProject project = (PlatypusProject) aProject;
                project.save();
            } catch (Exception ex) {
                throw new IOException(ex);
            }
        }
    }
}
