/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.actions;

import com.bearsoft.org.netbeans.modules.form.RADComponent;
import com.bearsoft.org.netbeans.modules.form.RADComponentCookie;
import com.bearsoft.org.netbeans.modules.form.bound.RADModelGrid;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

public final class FillGridColumnsAction extends CookieAction {

    public static boolean isEditableComponent(RADComponent<?> aComponent) {
        return aComponent instanceof RADModelGrid;
    }

    public FillGridColumnsAction() {
        super();
    }

    @Override
    protected int mode() {
        return MODE_ONE;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }

    @Override
    protected Class<?>[] cookieClasses() {
        return new Class<?>[]{RADComponentCookie.class};
    }

    @Override
    protected void performAction(Node[] activatedNodes) {
        if (activatedNodes != null && activatedNodes.length == 1 && activatedNodes[0].getLookup().lookup(RADComponentCookie.class) != null
                && activatedNodes[0].getLookup().lookup(RADComponentCookie.class).getRADComponent() instanceof RADModelGrid) {
            try {
                RADModelGrid radGrid = (RADModelGrid) activatedNodes[0].getLookup().lookup(RADComponentCookie.class).getRADComponent();
                radGrid.fillColumns();
            } catch (Exception ex) {
                Logger.getLogger(FillGridColumnsAction.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(FillGridColumnsAction.class, "CTL_FillGridColumnsAction");
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }
}
