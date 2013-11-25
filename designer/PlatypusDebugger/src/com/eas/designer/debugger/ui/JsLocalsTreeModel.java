/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.debugger.ui;

import org.netbeans.spi.debugger.ContextProvider;
import org.netbeans.spi.debugger.DebuggerServiceRegistration;
import org.netbeans.spi.viewmodel.TreeModel;
import org.netbeans.spi.viewmodel.UnknownTypeException;

/**
 *
 * @author mg
 */
@DebuggerServiceRegistration(path = "PlatypusJsSession/LocalsView",
        types = {TreeModel.class},
        position = 10003)
public class JsLocalsTreeModel extends JsWatchesTreeModel{

    protected ChildWatch[] locals;

    public JsLocalsTreeModel(ContextProvider aProvider) throws Exception {
        super(aProvider);
    }

    protected void checkLocals() throws UnknownTypeException {
        try {
            if (locals == null) {
                String[] sLocals = environment.mDebugger.locals();
                if (sLocals != null) {
                    locals = new ChildWatch[sLocals.length];
                    for (int i = 0; i < locals.length; i++) {
                        locals[i] = new ChildWatch();
                        locals[i].displayExpression = sLocals[i];
                        locals[i].expression = sLocals[i];
                    }
                }
            }
        } catch (Exception ex) {
            throw new UnknownTypeException(ex);
        }
    }

    @Override
    protected void fireChanges() {
        locals = null;
        super.fireChanges();
    }

    @Override
    public Object[] getChildren(Object parent, int from, int to) throws UnknownTypeException {
        if (parent == ROOT) {
            checkLocals();
            return locals != null ? locals : new ChildWatch[]{};
        } else {
            return super.getChildren(parent, from, to);
        }
    }

    @Override
    public int getChildrenCount(Object node) throws UnknownTypeException {
        if (node == ROOT) {
            checkLocals();
            return locals != null ? locals.length : 0;
        } else {
            return super.getChildrenCount(node);
        }
    }

    @Override
    public boolean isLeaf(Object node) throws UnknownTypeException {
        if (node == ROOT) {
            checkLocals();
            return locals == null || locals.length == 0;
        } else {
            return super.isLeaf(node);
        }
    }
}
