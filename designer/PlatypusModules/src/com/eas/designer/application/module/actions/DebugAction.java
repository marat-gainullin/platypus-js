/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.actions;

import com.eas.designer.application.indexer.IndexerQuery;
import com.eas.designer.explorer.project.PlatypusProjectImpl;
import com.eas.designer.explorer.project.ProjectRunner;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.openide.ErrorManager;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;

@ActionID(category = "File",
id = "com.eas.designer.application.module.actions.DebugAction")
@ActionRegistration(displayName = "#CTL_DebugAction")
@ActionReferences({
    @ActionReference(path = "Loaders/text/javascript/Actions", position = 162)
})
public final class DebugAction implements ActionListener {

    private final DataObject dataObject;

    public DebugAction(DataObject aContext) {
        super();
        dataObject = aContext;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        Project project = FileOwnerQuery.getOwner(dataObject.getPrimaryFile());
        if (project instanceof PlatypusProjectImpl) {
            try {
                PlatypusProjectImpl pProject = (PlatypusProjectImpl) project;
                String appElementName = IndexerQuery.file2AppElementId(dataObject.getPrimaryFile());
                if (appElementName == null) {
                    appElementName = FileUtil.getRelativePath(pProject.getSrcRoot(), dataObject.getPrimaryFile());
                }
                ProjectRunner.debug(pProject, appElementName);
            } catch (Exception ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
    }
}
