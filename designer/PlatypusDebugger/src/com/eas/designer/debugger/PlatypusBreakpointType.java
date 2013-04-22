/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.designer.debugger;

import javax.swing.JComponent;
import org.netbeans.spi.debugger.ui.BreakpointType;
import org.openide.util.NbBundle;

/**
 *
 * @author mg
 */
@BreakpointType.Registration(path=DebuggerConstants.DEBUGGER_SERVICERS_PATH, displayName="#breakpointTypeName")
public class PlatypusBreakpointType extends BreakpointType{

    @Override
    public String getCategoryDisplayName() {
        return NbBundle.getMessage(PlatypusBreakpointType.class, "breakpointTypeName");
    }

    @Override
    public JComponent getCustomizer() {
        return null;
    }

    @Override
    public boolean isDefault() {
        return true;
    }

}
