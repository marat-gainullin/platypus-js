/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.report;

import com.eas.designer.explorer.PlatypusDataObject;
import javax.swing.Action;
import org.openide.loaders.DataNode;
import org.openide.nodes.FilterNode;

/**
 *
 * @author mg
 */
public class ReportTemplateDataNode extends FilterNode {

    private static final String REPORT_ICON_BASE = ReportTemplateDataNode.class.getPackage().getName().replace('.', '/') + "/xlsx.png"; // NOI18N

    public ReportTemplateDataNode(PlatypusDataObject fdo) {
        this(new DataNode(fdo, Children.LEAF));
    }

    private ReportTemplateDataNode(DataNode orig) {
        super(orig);
        orig.setIconBaseWithExtension(REPORT_ICON_BASE);
    }

    @Override
    public Action getPreferredAction() {
        return new OpenTemplateAction(((DataNode) getOriginal()).getDataObject());
    }
}
