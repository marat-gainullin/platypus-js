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
import org.netbeans.core.spi.multiview.SourceViewMarker;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 *
 * @author mg
 */
public class PlatypusModuleSourceDescription implements MultiViewDescription, SourceViewMarker, Serializable {

    static final long serialVersionUID = 53142032923497718L;
    public static final String MODULE_SOURCE_VIEW_NAME = "module-source";
    protected PlatypusModuleDataObject dataObject;

    public PlatypusModuleSourceDescription(PlatypusModuleDataObject aDataObject) {
        super();
        dataObject = aDataObject;
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ONLY_OPENED;
    }

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(PlatypusModuleSourceDescription.class, MODULE_SOURCE_VIEW_NAME);
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
        return MODULE_SOURCE_VIEW_NAME;
    }

    @Override
    public MultiViewElement createElement() {
        PlatypusModuleSupport support = dataObject.getLookup().lookup(PlatypusModuleSupport.class);
        support.prepareDocument();
        return (PlatypusModuleSourceView) support.createCloneableEditor();
    }
}
