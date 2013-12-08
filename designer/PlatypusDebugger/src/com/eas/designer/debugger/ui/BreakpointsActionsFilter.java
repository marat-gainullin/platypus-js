/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.debugger.ui;

import com.eas.designer.debugger.PlatypusBreakpoint;
import javax.swing.Action;
import org.netbeans.spi.debugger.DebuggerServiceRegistration;
import org.netbeans.spi.viewmodel.NodeActionsProvider;
import org.netbeans.spi.viewmodel.NodeActionsProviderFilter;
import org.netbeans.spi.viewmodel.UnknownTypeException;
import org.openide.text.Line;

/**
 *
 * @author mg
 */
@DebuggerServiceRegistration(path = "BreakpointsView", types={NodeActionsProviderFilter.class})
public class BreakpointsActionsFilter implements NodeActionsProviderFilter {

    public BreakpointsActionsFilter() {
        super();
    }

    @Override
    public void performDefaultAction(NodeActionsProvider original, Object node) throws UnknownTypeException {
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
            original.performDefaultAction(node);
        }
    }

    @Override
    public Action[] getActions(NodeActionsProvider original, Object node) throws UnknownTypeException {
        return original.getActions(node);
    }
}
