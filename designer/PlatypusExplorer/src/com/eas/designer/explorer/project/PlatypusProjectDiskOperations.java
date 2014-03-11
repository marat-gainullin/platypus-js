/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.project;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.CopyOperationImplementation;
import org.netbeans.spi.project.DataFilesProviderImplementation;
import org.netbeans.spi.project.DeleteOperationImplementation;
import org.netbeans.spi.project.MoveOperationImplementation;
import org.netbeans.spi.project.MoveOrRenameOperationImplementation;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Gala
 */
public class PlatypusProjectDiskOperations implements DataFilesProviderImplementation,
        DeleteOperationImplementation,
        CopyOperationImplementation,
        MoveOperationImplementation,
        MoveOrRenameOperationImplementation {

    protected PlatypusProjectImpl project;

    public PlatypusProjectDiskOperations(PlatypusProjectImpl aProject) {
        super();
        project = aProject;
    }

    /*
     * DataFilesProviderImplementation
     */
    @Override
    public List<FileObject> getMetadataFiles() {
        return Collections.unmodifiableList(Arrays.asList(project.getSettings().getProjectSettingsFileObject()));
    }

    @Override
    public List<FileObject> getDataFiles() {
        return Collections.unmodifiableList(Arrays.asList(project.getSrcRoot()));
    }

    /*
     * DeleteOperationImplementation
     */
    @Override
    public void notifyDeleting() throws IOException {
    }

    @Override
    public void notifyDeleted() throws IOException {
        project.getState().notifyDeleted();
    }

    /*
     * CopyOperationImplementation
     */
    @Override
    public void notifyCopying() throws IOException {
    }

    @Override
    public void notifyCopied(Project original, File file, String aNewName) throws IOException {
        if (original != null) // call is on the new project, original is the really original project
        {
            //project - newly, just created project
            //original - old (original) project
            assert original != project;
            project.getProjectInfo().setDisplayName(project.getProjectInfo().getDisplayName() + " ["+aNewName+"]");
            project.getState().markModified();
        }
    }

    /*
     * MoveOperationImplementation
     */
    @Override
    public void notifyMoving() throws IOException {
    }

    @Override
    public void notifyMoved(Project original, File file, String aNewProjectFolderName) throws IOException {
        if (original != null) // call is on the new project, original is the really original project
        {
            //project - newly, just created project
            //original - old (original) project
            assert original != project;
            ((PlatypusProjectImpl)original).getState().notifyDeleted();
        }
    }

    /*
     * MoveOrRenameOperationImplementation
     */
    @Override
    public void notifyRenaming() throws IOException {
    }

    @Override
    public void notifyRenamed(String aNewDisplayName) throws IOException {
        project.getProjectInfo().setDisplayName(aNewDisplayName);
    }
}
