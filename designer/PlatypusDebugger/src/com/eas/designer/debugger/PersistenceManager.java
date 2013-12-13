/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.debugger;

import java.beans.PropertyChangeEvent;
import org.netbeans.api.debugger.Breakpoint;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.DebuggerManagerAdapter;
import org.netbeans.api.debugger.Properties;

public class PersistenceManager extends DebuggerManagerAdapter {

    public static final String DEBUGGER_PROP_NAME = "platypus-debugger";

    @Override
    public Breakpoint[] initBreakpoints() {
        Properties p = Properties.getDefault().getProperties(DEBUGGER_PROP_NAME);
        Breakpoint[] breakpoints = (Breakpoint[]) p.getArray(
                DebuggerManager.PROP_BREAKPOINTS,
                new Breakpoint[]{});
        for (Breakpoint breakpoint : breakpoints) {
            breakpoint.addPropertyChangeListener(this);
        }
        return breakpoints;
    }

    @Override
    public String[] getProperties() {
        return new String[]{
            DebuggerManager.PROP_BREAKPOINTS_INIT,
            DebuggerManager.PROP_BREAKPOINTS
        };
    }

    @Override
    public void breakpointAdded(Breakpoint breakpoint) {
        if (breakpoint instanceof PlatypusBreakpoint) {
            Properties p = Properties.getDefault().getProperties(DEBUGGER_PROP_NAME);
            p.setArray(
                    DebuggerManager.PROP_BREAKPOINTS,
                    DebuggerManager.getDebuggerManager().getBreakpoints());
            breakpoint.addPropertyChangeListener(this);
        }
    }

    @Override
    public void breakpointRemoved(Breakpoint breakpoint) {
        if (breakpoint instanceof PlatypusBreakpoint) {
            Properties p = Properties.getDefault().getProperties(DEBUGGER_PROP_NAME);
            p.setArray(
                    DebuggerManager.PROP_BREAKPOINTS,
                    DebuggerManager.getDebuggerManager().getBreakpoints());
            breakpoint.removePropertyChangeListener(this);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof PlatypusBreakpoint) {
            Properties.getDefault().getProperties(DEBUGGER_PROP_NAME).setArray(
                    DebuggerManager.PROP_BREAKPOINTS,
                    DebuggerManager.getDebuggerManager().getBreakpoints());
        }
    }
}
