/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form;

import com.eas.designer.explorer.PlatypusDataObject;
import javax.swing.Action;
import org.openide.loaders.DataNode;
import org.openide.nodes.FilterNode;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author mg
 */
public class LayoutDataNode extends FilterNode {
    
    private static final String LAYOUT_ICON_BASE = "com/bearsoft/org/netbeans/modules/form/resources/layout.gif"; // NOI18N
    
    public LayoutDataNode(PlatypusDataObject fdo) {
        this(new DataNode(fdo, Children.LEAF));
    }

    private LayoutDataNode(DataNode orig) {
        super(orig);
        orig.setIconBaseWithExtension(LAYOUT_ICON_BASE);
    }

    @Override
    public Action getPreferredAction() {
        return new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                PlatypusLayoutSupport supp = getLookup().lookup(PlatypusLayoutSupport.class);
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
        // Skipping the first (e.g. Open) action
        System.arraycopy(javaActions, 0, formActions, 3, javaActions.length);
        return formActions;
    }
}
