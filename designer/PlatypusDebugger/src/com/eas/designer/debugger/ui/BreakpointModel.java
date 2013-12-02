/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.debugger.ui;

import com.eas.designer.debugger.PlatypusBreakpoint;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;
import org.netbeans.spi.debugger.DebuggerServiceRegistration;
import org.netbeans.spi.viewmodel.ModelEvent;
import org.netbeans.spi.viewmodel.ModelListener;
import org.netbeans.spi.viewmodel.NodeModel;
import org.netbeans.spi.viewmodel.UnknownTypeException;
import org.openide.filesystems.FileObject;

/**
 *
 * @author mg
 */
@DebuggerServiceRegistration(path = "BreakpointsView",
        types = {NodeModel.class},
        position = 10000)
public class BreakpointModel implements NodeModel {

    public static final String RESOURCE_PREFIX =
            "org/netbeans/modules/debugger/resources/breakpointsView/";
    public static final String LINE_BREAKPOINT =
            RESOURCE_PREFIX + "Breakpoint";
    public static final String LINE_BREAKPOINT_PC =
            RESOURCE_PREFIX + "BreakpointHit";
    public static final String DISABLED_LINE_BREAKPOINT =
            RESOURCE_PREFIX + "DisabledBreakpoint";
    public static final String DISABLED_LINE_BREAKPOINT_PC =
            RESOURCE_PREFIX + "DisabledBreakpointHit";
    private Set<ModelListener> listeners = new HashSet<>();
    protected PropertyChangeListener linesListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            fireChanges();
        }
    };

    public BreakpointModel() {
        super();
    }

    // NodeModel implementation ................................................
    /**
     * Returns display name for given node.
     *
     * @throws ComputingException if the display name resolving process is time
     * consuming, and the value will be updated later
     * @throws UnknownTypeException if this NodeModel implementation is not able
     * to resolve display name for given node type
     * @return display name for given node
     */
    @Override
    public String getDisplayName(Object node) throws UnknownTypeException {
        if (node instanceof PlatypusBreakpoint) {
            PlatypusBreakpoint breakpoint = (PlatypusBreakpoint) node;
            FileObject fileObject = breakpoint.getLine().
                    getLookup().lookup(FileObject.class);
            String res = fileObject.getName() + ": " + (breakpoint.getLine().getLineNumber() + 1);
            if (breakpoint.isHitted()) {
                return "<html><b>" + res;
            } else {
                return res;
            }
        } else {
            throw new UnknownTypeException(node);
        }
    }

    /**
     * Returns icon for given node.
     *
     * @throws ComputingException if the icon resolving process is time
     * consuming, and the value will be updated later
     * @throws UnknownTypeException if this NodeModel implementation is not able
     * to resolve icon for given node type
     * @return icon for given node
     */
    @Override
    public String getIconBase(Object node) throws UnknownTypeException {
        if (node instanceof PlatypusBreakpoint) {
            PlatypusBreakpoint breakpoint = (PlatypusBreakpoint) node;
            if (breakpoint.getLine() != null) {
                breakpoint.getLine().removePropertyChangeListener(linesListener);
                breakpoint.getLine().addPropertyChangeListener(linesListener);
            }
            if (breakpoint.isEnabled()) {
                if (breakpoint.isHitted()) {
                    return LINE_BREAKPOINT_PC;
                } else {
                    return LINE_BREAKPOINT;
                }
            } else {
                if (breakpoint.isHitted()) {
                    return DISABLED_LINE_BREAKPOINT_PC;
                } else {
                    return DISABLED_LINE_BREAKPOINT;
                }
            }
        } else {
            throw new UnknownTypeException(node);
        }
    }

    /**
     * Returns tooltip for given node.
     *
     * @throws ComputingException if the tooltip resolving process is time
     * consuming, and the value will be updated later
     * @throws UnknownTypeException if this NodeModel implementation is not able
     * to resolve tooltip for given node type
     * @return tooltip for given node
     */
    @Override
    public String getShortDescription(Object node) throws UnknownTypeException {
        if (node instanceof PlatypusBreakpoint) {
            PlatypusBreakpoint breakpoint = (PlatypusBreakpoint) node;
            return breakpoint.getLine().getDisplayName();
        } else {
            throw new UnknownTypeException(node);
        }
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

    public void fireChanges() {
        ModelListener[] listenersArray = listeners.toArray(new ModelListener[0]);
        for (ModelListener l : listenersArray) {
            l.modelChanged(new ModelEvent.TreeChanged(this));
        }
    }
}
