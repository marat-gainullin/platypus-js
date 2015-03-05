/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.report;

import com.eas.designer.application.module.PlatypusModuleDatamodelDescription;
import com.eas.designer.application.module.PlatypusModuleSourceDescription;
import com.eas.designer.application.module.PlatypusModuleSupport;
import java.io.IOException;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewFactory;
import org.openide.text.CloneableEditorSupport;
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
    public void save() throws IOException {
        saveDocument();
    }

    @Override
    protected void notifyModuleChanged() {
        // Reports are not allowed for use as datasource modules, rather then simple modules.
        // Because of this we have to ignore changes.
    }
}
