/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.dbdiagram;

import com.eas.designer.explorer.PlatypusDataObject;
import javax.swing.Action;
import org.openide.loaders.DataNode;
import org.openide.nodes.FilterNode;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author mg
 */
public class DiagramDataNode extends FilterNode {

    private static final String DIAGRAM_ICON_BASE = DiagramDataNode.class.getPackage().getName().replace('.', '/') + "/dbScheme.png"; // NOI18N

    public DiagramDataNode(PlatypusDataObject fdo) {
        this(new DataNode(fdo, FilterNode.Children.LEAF));
    }

    private DiagramDataNode(DataNode orig) {
        super(orig);
        orig.setIconBaseWithExtension(DIAGRAM_ICON_BASE);
    }

    @Override
    public Action getPreferredAction() {
        return new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                PlatypusDbDiagramSupport supp = getLookup().lookup(PlatypusDbDiagramSupport.class);
                supp.open();
            }
        };
    }

    @Override
    public Action[] getActions(boolean context) {
        Action[] javaActions = super.getActions(context);
        Action[] formActions = new Action[javaActions.length + 3];
        formActions[0] = SystemAction.get(org.openide.actions.OpenAction.class);
        formActions[1] = SystemAction.get(org.openide.actions.EditAction.class);
        formActions[2] = null;
        System.arraycopy(javaActions, 0, formActions, 3, javaActions.length);
        return formActions;
    }
}
