/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module;

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
public class PlatypusModuleDatamodelDescription implements MultiViewDescription, Serializable {

    public static final String MODULE_DATAMODEL_VIEW_NAME = "module-datamodel";
    static final long serialVersionUID = 35114132223497718L;
    protected PlatypusModuleDataObject dataObject;

    public PlatypusModuleDatamodelDescription(PlatypusModuleDataObject aDataObject) {
        super();
        dataObject = aDataObject;
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ONLY_OPENED;
    }

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(PlatypusModuleDatamodelDescription.class, MODULE_DATAMODEL_VIEW_NAME);
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
        return MODULE_DATAMODEL_VIEW_NAME;
    }

    @Override
    public MultiViewElement createElement() {
        try {
            return new PlatypusDatamodelView(dataObject);
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
            return null;
        }
    }
}
