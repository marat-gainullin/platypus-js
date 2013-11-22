/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.debugger.actions;

import com.eas.designer.debugger.PlatypusBreakpoint;
import com.eas.designer.debugger.annotations.PlatypusBreakpointAnnotation;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.netbeans.api.debugger.Breakpoint;
import org.netbeans.api.debugger.DebuggerManager;
import org.openide.text.Line;

/**
 *
 * @author mg
 */
public class ToggleBreakpointAction extends AbstractAction {

    public ToggleBreakpointAction() {
        super();
        putValue("default-action", true);
        putValue("supported-annotation-types", new String[]{
            "Platypus-Breakpoint-Annotation",
            "Platypus-Disabled-Breakpoint-Annotation"
        });
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        toggleBreakpoint();
    }

    public static void toggleBreakpoint() {
        Line line = PlatypusBreakpointAnnotation.getCurrentLine();
        if (line != null) {
            Breakpoint[] breakpoints = DebuggerManager.getDebuggerManager().getBreakpoints();
            boolean found = false;
            for (int i = 0; i < breakpoints.length; i++) {
                if (breakpoints[i] instanceof PlatypusBreakpoint && ((PlatypusBreakpoint) breakpoints[i]).getLine().equals(line)) {
                    DebuggerManager.getDebuggerManager().removeBreakpoint(breakpoints[i]);
                    found = true;
                }
            }
            if (!found) {
                DebuggerManager.getDebuggerManager().addBreakpoint(new PlatypusBreakpoint(line));
            }
        }
    }
}
