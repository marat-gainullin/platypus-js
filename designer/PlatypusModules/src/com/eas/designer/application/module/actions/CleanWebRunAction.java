/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.actions;

import com.eas.designer.explorer.j2ee.PlatypusWebModuleManager;
import com.eas.designer.explorer.project.PlatypusProjectActions;
import com.eas.designer.explorer.project.PlatypusProjectImpl;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

@ActionID(category = "File",
        id = "com.eas.designer.application.module.actions.CleanWebRunAction")
@ActionRegistration(displayName = "#CTL_CleanRunAction")
@ActionReferences({
    @ActionReference(path = "Loaders/text/javascript/Actions", position = 151, separatorBefore = 125, separatorAfter = 175)
})
public final class CleanWebRunAction extends RunAction {

    public CleanWebRunAction(DataObject aContext) {
        super(aContext);
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        Project _project = FileOwnerQuery.getOwner(dataObject.getPrimaryFile());
        if (_project instanceof PlatypusProjectImpl) {
            PlatypusProjectImpl project = (PlatypusProjectImpl) _project;
            PlatypusWebModuleManager pwmm = project.getLookup().lookup(PlatypusWebModuleManager.class);
            assert pwmm != null;
            try {
                pwmm.undeploy();
                project.getOutputWindowIO().getOut().println(NbBundle.getMessage(PlatypusProjectActions.class, "MSG_Cleaning_Web_Dir")); // NOI18N
                pwmm.clearWebDir();
                project.getOutputWindowIO().getOut().println(NbBundle.getMessage(PlatypusProjectActions.class, "MSG_Cleaning_Web_Dir_Complete")); // NOI18N
            } catch (Deployment.DeploymentException | MissingResourceException | IOException ex) {
                Logger.getLogger(PlatypusProjectActions.class.getName()).log(Level.WARNING, NbBundle.getMessage(PlatypusProjectActions.class, "MSG_Cleaning_Web_Dir") + " - Failed", ex);
                project.getOutputWindowIO().getErr().println(ex.getMessage());
                try {
                    pwmm.prepareWebApplication(true);
                } catch (Exception ex1) {
                    Exceptions.printStackTrace(ex1);
                }
            }
        }
        super.actionPerformed(ev);
    }
}
