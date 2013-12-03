/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.debugger.ui;

import com.eas.designer.debugger.PlatypusBreakpoint;
import org.netbeans.modules.debugger.ui.models.BreakpointsActionsProvider;
import org.netbeans.spi.viewmodel.UnknownTypeException;
import org.openide.text.Line;

/**
 *
 * @author mg
 */
public class PlatypusBreakpointsActionsProvider extends BreakpointsActionsProvider {

    public PlatypusBreakpointsActionsProvider() {
        super();
    }

    @Override
    public void performDefaultAction(Object node) throws UnknownTypeException {
        if (node instanceof PlatypusBreakpoint) {
            try {
                PlatypusBreakpoint breakPoint = (PlatypusBreakpoint) node;
                if (breakPoint.getLine() != null) {
                    breakPoint.getLine().show(Line.ShowOpenType.OPEN, Line.ShowVisibilityType.FOCUS);
                }
            } catch (Exception ex) {
                throw new UnknownTypeException(node);
            }
        } else {
            super.performDefaultAction(node);
        }
    }
}
