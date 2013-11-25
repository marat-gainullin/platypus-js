/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.debugger.ui;

import java.util.HashSet;
import java.util.Set;
import org.netbeans.spi.debugger.DebuggerServiceRegistration;
import org.netbeans.spi.viewmodel.ModelListener;
import org.netbeans.spi.viewmodel.NodeModel;
import org.netbeans.spi.viewmodel.NodeModelFilter;
import org.netbeans.spi.viewmodel.UnknownTypeException;

/**
 *
 * @author mg
 */
@DebuggerServiceRegistration(path = "PlatypusJsSession/WatchesView",
        types = {NodeModelFilter.class},
        position = 10004)
public class JsWatchesNodeFilter implements NodeModelFilter {

    public static final String LOCAL =
            "org/netbeans/modules/debugger/resources/localsView/LocalVariable";
    protected Set<ModelListener> listeners = new HashSet<>();

    @Override
    public String getDisplayName(NodeModel original, Object node) throws UnknownTypeException {
        if (node instanceof ChildWatch) {
            ChildWatch cw = (ChildWatch) node;
            return cw.displayExpression;
        } else {
            return original.getDisplayName(node);
        }
    }

    @Override
    public String getIconBase(NodeModel original, Object node) throws UnknownTypeException {
        if (node instanceof ChildWatch) {
            return LOCAL;
        } else {
            return original.getIconBase(node);
        }
    }

    @Override
    public String getShortDescription(NodeModel original, Object node) throws UnknownTypeException {
        return original.getShortDescription(node);
    }

    /**
     * Registers given listener.
     *
     * @param l the listener to add
     */
    @Override
    public void addModelListener(ModelListener l) {
        listeners.add(l);
    }

    /**
     * Unregisters given listener.
     *
     * @param l the listener to remove
     */
    @Override
    public void removeModelListener(ModelListener l) {
        listeners.remove(l);
    }
}
