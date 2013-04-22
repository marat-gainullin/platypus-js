/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.debugger.ui;

import com.eas.designer.debugger.PlatypusBreakpoint;
import java.util.HashSet;
import java.util.Set;
import org.netbeans.spi.viewmodel.ModelEvent;
import org.netbeans.spi.viewmodel.ModelListener;
import org.netbeans.spi.viewmodel.NodeModel;
import org.netbeans.spi.viewmodel.UnknownTypeException;
import org.openide.filesystems.FileObject;

/**
 *
 * @author mg
 */
public class BreakpointModel implements NodeModel {

    public static final String RESOURCE_PREFIX =
            "com/eas/designer/debugger/resources/";
    public static final String LINE_BREAKPOINT = RESOURCE_PREFIX
            + "breakpoint";//.gif
    public static final String DISABLED_LINE_BREAKPOINT = RESOURCE_PREFIX
            + "disabled-breakpoint";//.gif
    private Set<ModelListener> listeners = new HashSet<>();

    public BreakpointModel() {
        super();
    }

    // NodeModel implementation ................................................
    /**
     * Returns display name for given node.
     *
     * @throws  ComputingException if the display name resolving process
     *          is time consuming, and the value will be updated later
     * @throws  UnknownTypeException if this NodeModel implementation is not
     *          able to resolve display name for given node type
     * @return  display name for given node
     */
    @Override
    public String getDisplayName(Object node) throws UnknownTypeException {
        if (node instanceof PlatypusBreakpoint) {
            PlatypusBreakpoint breakpoint = (PlatypusBreakpoint) node;
            FileObject fileObject = breakpoint.getLine().
                    getLookup().lookup(FileObject.class);
            return fileObject.getName() + ":" + (breakpoint.getLine().getLineNumber() + 1);
        } else {
            throw new UnknownTypeException(node);
        }
    }

    /**
     * Returns icon for given node.
     *
     * @throws  ComputingException if the icon resolving process
     *          is time consuming, and the value will be updated later
     * @throws  UnknownTypeException if this NodeModel implementation is not
     *          able to resolve icon for given node type
     * @return  icon for given node
     */
    @Override
    public String getIconBase(Object node) throws UnknownTypeException {
        if (node instanceof PlatypusBreakpoint) {
            PlatypusBreakpoint breakpoint = (PlatypusBreakpoint) node;
            if (!breakpoint.isEnabled()) {
                return DISABLED_LINE_BREAKPOINT;
            }
            return LINE_BREAKPOINT;
        } else {
            throw new UnknownTypeException(node);
        }
    }

    /**
     * Returns tooltip for given node.
     *
     * @throws  ComputingException if the tooltip resolving process
     *          is time consuming, and the value will be updated later
     * @throws  UnknownTypeException if this NodeModel implementation is not
     *          able to resolve tooltip for given node type
     * @return  tooltip for given node
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
