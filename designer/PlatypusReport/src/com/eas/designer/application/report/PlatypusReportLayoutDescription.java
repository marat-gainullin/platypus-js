/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.designer.application.report;

import java.awt.Image;
import java.beans.BeanInfo;
import java.io.Serializable;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.openide.ErrorManager;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 *
 * @author mg
 */
public class PlatypusReportLayoutDescription implements MultiViewDescription, Serializable {

    protected static final String REPORT_LAYOUT_VIEW_NAME = "report-layout";
    static final long serialVersionUID = 35114132213490015L;
    protected PlatypusReportDataObject dataObject;

    public PlatypusReportLayoutDescription(PlatypusReportDataObject aDataObject) {
        super();
        dataObject = aDataObject;
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ONLY_OPENED;
    }

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(PlatypusReportLayoutDescription.class, REPORT_LAYOUT_VIEW_NAME);
    }

    @Override
    public Image getIcon() {
        return dataObject.getNodeDelegate().getIcon(BeanInfo.ICON_COLOR_16x16);
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }

    @Override
    public String preferredID() {
        return REPORT_LAYOUT_VIEW_NAME;
    }

    @Override
    public MultiViewElement createElement() {
        try {
            return new PlatypusReportLayoutView(dataObject);
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
            return null;
        }
    }
}
