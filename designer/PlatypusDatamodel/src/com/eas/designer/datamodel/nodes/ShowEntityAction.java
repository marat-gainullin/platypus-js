/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.designer.datamodel.nodes;

import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallbackSystemAction;

/**
 *
 * @author mg
 */
@ActionID(id = "com.eas.designer.explorer.model.nodes.ShowEntityAction", category = "Edit")
@ActionRegistration(displayName = "#CTL_ShowEntityAction", lazy = true)
public class ShowEntityAction extends CallbackSystemAction {

    @Override
    public Object getActionMapKey() {
        return ShowEntityAction.class.getSimpleName();
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(ShowEntityAction.class, "CTL_ShowEntityAction");
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}
