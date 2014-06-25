/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.report;

import com.eas.designer.application.module.PlatypusModuleDatamodelDescription;
import com.eas.designer.application.module.PlatypusModuleSourceDescription;
import com.eas.designer.application.module.PlatypusModuleSupport;
import java.io.IOException;
import java.util.MissingResourceException;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewFactory;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.text.CloneableEditorSupport;
import org.openide.util.NbBundle;
import org.openide.windows.CloneableTopComponent;

/**
 *
 * @author mg
 */
public class PlatypusReportSupport extends PlatypusModuleSupport {

    public PlatypusReportSupport(PlatypusReportDataObject aObject) {
        super(aObject);
    }

    @Override
    protected CloneableEditorSupport.Pane createPane() {
        PlatypusModuleSourceDescription sourceDesc = new PlatypusModuleSourceDescription(dataObject);
        PlatypusModuleDatamodelDescription modelDesc = new PlatypusModuleDatamodelDescription(dataObject);
        PlatypusReportLayoutDescription layoutDesc = new PlatypusReportLayoutDescription((PlatypusReportDataObject) dataObject);
        MultiViewDescription[] descs = new MultiViewDescription[]{sourceDesc, layoutDesc, modelDesc};
        CloneableTopComponent mv = MultiViewFactory.createCloneableMultiView(descs, layoutDesc, new CloseHandler(dataObject));
        return (CloneableEditorSupport.Pane) mv;
    }

    @Override
    protected boolean canClose() {
        try {
            PlatypusReportDataObject reportObject = (PlatypusReportDataObject) dataObject;
            boolean res = reportObject.isTemplateValid() && super.canClose();
            if (!res)// We can't close document while report layout been edited.
            {       // In this case we have to warn the user about this situation.
                NotifyDescriptor message = new NotifyDescriptor.Message(NbBundle.getMessage(PlatypusReportSupport.class, "Can_tCloseWhileEditingReportLayout"), NotifyDescriptor.Message.INFORMATION_MESSAGE);
                DialogDisplayer.getDefault().notify(message);
            }
            return res;
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
            return true;
        }
    }

    @Override
    public void save() throws IOException {
        try {
            PlatypusReportDataObject reportObject = (PlatypusReportDataObject) dataObject;
            if (reportObject.isTemplateValid()) {
                // save js source and datamodel
                super.save();
            } else // We can't save document while report layout been edited.
            {     // In this case we have to warn the user about this situation.
                NotifyDescriptor message = new NotifyDescriptor.Message(NbBundle.getMessage(PlatypusReportSupport.class, "Can_tSaveWhileEditingReportLayout"), NotifyDescriptor.Message.INFORMATION_MESSAGE);
                DialogDisplayer.getDefault().notify(message);
            }
        } catch (IOException ex) {
            throw ex;
        } catch (MissingResourceException ex) {
            throw new IOException(ex);
        }
    }
}
