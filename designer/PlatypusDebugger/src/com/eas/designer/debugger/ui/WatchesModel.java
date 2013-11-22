package com.eas.designer.debugger.ui;

import com.eas.debugger.jmx.server.DebuggerMBean;
import com.eas.designer.debugger.DebuggerConstants;
import com.eas.designer.debugger.DebuggerEnvironment;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.management.MBeanServerConnection;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import org.netbeans.api.debugger.Breakpoint;
import org.netbeans.api.debugger.DebuggerEngine;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.DebuggerManagerListener;
import org.netbeans.api.debugger.Session;

import org.netbeans.api.debugger.Watch;
import org.netbeans.spi.debugger.ContextProvider;
import org.netbeans.spi.debugger.DebuggerServiceRegistration;
import org.netbeans.spi.debugger.ui.Constants;
import org.netbeans.spi.viewmodel.ModelEvent;
import org.netbeans.spi.viewmodel.NodeModel;
import org.netbeans.spi.viewmodel.ModelListener;
import org.netbeans.spi.viewmodel.UnknownTypeException;
import org.netbeans.spi.viewmodel.TreeModel;
import org.netbeans.spi.viewmodel.TableModel;
import org.netbeans.spi.viewmodel.NodeModelFilter;
import static org.netbeans.spi.viewmodel.TreeModel.ROOT;

/**
 *
 * @author mg
 */
/*
 @DebuggerServiceRegistrations({
 @DebuggerServiceRegistration(path = "PlatypusJsSession/LocalsView",
 types = {TreeModel.class, TableModel.class, NodeModelFilter.class},
 position = 10000),
 @DebuggerServiceRegistration(path = "PlatypusJsSession/WatchesView",
 types = {TreeModel.class, TableModel.class, NodeModelFilter.class},
 position = 10000),
 @DebuggerServiceRegistration(path = "PlatypusJsSession/ToolTipView",
 types = {TreeModel.class, TableModel.class, NodeModelFilter.class},
 position = 10000)
 })
 */
@DebuggerServiceRegistration(path = "PlatypusJsSession/WatchesView",
        types = {TreeModel.class, TableModel.class, NodeModelFilter.class},
        position = 10000)
public class WatchesModel implements TreeModel, TableModel, NodeModelFilter, NotificationListener, DebuggerManagerListener {

    private String extractWatchExpression(Object w) {
        String parentExpression = w instanceof Watch ? ((Watch) w).getExpression() : ((ChildWatch) w).expression;
        return parentExpression;
    }

    protected class ChildWatch {

        public String displayExpression;
        public String expression;
    }
    protected Set<ModelListener> listeners = new HashSet<>();
    protected DebuggerEnvironment environment;
    protected Map<Object, ChildWatch[]> structure = new HashMap<>();

    public WatchesModel(ContextProvider contextProvider) throws Exception {
        super();
        environment = contextProvider.lookupFirst(DebuggerConstants.DEBUGGER_SERVICERS_PATH, DebuggerEnvironment.class);
        ObjectName mBeanDebuggerName = new ObjectName(DebuggerMBean.DEBUGGER_MBEAN_NAME);
        MBeanServerConnection jmxConnection = contextProvider.lookupFirst(DebuggerConstants.DEBUGGER_SERVICERS_PATH, MBeanServerConnection.class);
        jmxConnection.addNotificationListener(mBeanDebuggerName, this, null, null);
        DebuggerManager.getDebuggerManager().addDebuggerListener(this);
    }

    // NodeModelFilter implementation ................................................
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
    public String getDisplayName(NodeModel model, Object node) throws UnknownTypeException {
        return node instanceof ChildWatch ? ((ChildWatch) node).displayExpression : model.getDisplayName(node);
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
    public String getIconBase(NodeModel model, Object node) throws UnknownTypeException {
        return model.getIconBase(node);
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
    public String getShortDescription(NodeModel model, Object node) throws UnknownTypeException {
        return model.getShortDescription(node);
    }

    // TableModel implementation ...............................................
    /**
     * Returns value to be displayed in column
     * <code>columnID</code> and row identified by
     * <code>node</code>. Column ID is defined in by {@link ColumnModel#getID},
     * and rows are defined by values returned from
     * {@link org.netbeans.spi.viewmodel.TreeModel#getChildren}.
     *
     * @param node a object returned from
     * {@link org.netbeans.spi.viewmodel.TreeModel#getChildren} for this row
     * @param columnID a id of column defined by {@link ColumnModel#getID}
     * @throws ComputingException if the value is not known yet and will be
     * computed later
     * @throws UnknownTypeException if there is no TableModel defined for given
     * parameter type
     *
     * @return value of variable representing given position in tree table.
     */
    @Override
    public Object getValueAt(Object node, String columnID) throws UnknownTypeException {
        if (/*Constants.WATCH_TO_STRING_COLUMN_ID.equals(columnID)
                 || */Constants.WATCH_VALUE_COLUMN_ID.equals(columnID)
                || Constants.LOCALS_VALUE_COLUMN_ID.equals(columnID)) {
            String expression = extractWatchExpression(node);
            try {
                return environment.mDebugger.evaluate(expression);
            } catch (Exception ex) {
                return ex.getMessage();
            }
        }
        if (Constants.WATCH_TYPE_COLUMN_ID.equals(columnID)
                || Constants.LOCALS_TYPE_COLUMN_ID.equals(columnID)) {
            String expression = extractWatchExpression(node);
            try {
                return environment.mDebugger.evaluate("typeof(" + expression + ")");
            } catch (Exception ex) {
                return ex.getMessage();
            }
        }
        throw new UnknownTypeException(node);
    }

    /**
     * Returns true if value displayed in column
     * <code>columnID</code> and row
     * <code>node</code> is read only. Column ID is defined in by
     * {@link ColumnModel#getID}, and rows are defined by values returned from
     * {@link TreeModel#getChildren}.
     *
     * @param node a object returned from {@link TreeModel#getChildren} for this
     * row
     * @param columnID a id of column defined by {@link ColumnModel#getID}
     * @throws UnknownTypeException if there is no TableModel defined for given
     * parameter type
     *
     * @return true if variable on given position is read only
     */
    @Override
    public boolean isReadOnly(Object node, String columnID) throws UnknownTypeException {
        return Constants.WATCH_TO_STRING_COLUMN_ID.equals(columnID)
                || Constants.WATCH_VALUE_COLUMN_ID.equals(columnID)
                || Constants.WATCH_TYPE_COLUMN_ID.equals(columnID);
    }

    /**
     * Changes a value displayed in column
     * <code>columnID</code> and row
     * <code>node</code>. Column ID is defined in by {@link ColumnModel#getID},
     * and rows are defined by values returned from
     * {@link TreeModel#getChildren}.
     *
     * @param node a object returned from {@link TreeModel#getChildren} for this
     * row
     * @param columnID a id of column defined by {@link ColumnModel#getID}
     * @param value a new value of variable on given position
     * @throws UnknownTypeException if there is no TableModel defined for given
     * parameter type
     */
    @Override
    public void setValueAt(Object node, String columnID, Object value) throws UnknownTypeException {
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

    // other mothods ...........................................................
    protected void fireChanges() {
        structure.clear();
        ModelEvent.TreeChanged event = new ModelEvent.TreeChanged(this);
        ModelListener[] v = listeners.toArray(new ModelListener[]{});
        for (ModelListener l : v) {
            l.modelChanged(event);
        }
    }

    @Override
    public void handleNotification(Notification notification, Object handback) {
        fireChanges();
    }

    @Override
    public Object getRoot() {
        return ROOT;
    }

    @Override
    public Object[] getChildren(Object parent, int from, int to) throws UnknownTypeException {
        if (parent == ROOT) {
            Object[] ws = DebuggerManager.getDebuggerManager().getWatches();
            to = Math.min(ws.length, to);
            from = Math.min(ws.length, from);
            Object[] fws = new Object[to - from];
            System.arraycopy(ws, from, fws, 0, to - from);
            return fws;
        } else {
            ChildWatch[] children = checkChildren(parent);
            return children != null ? children : new Watch[]{};
        }
    }

    @Override
    public boolean isLeaf(Object node) throws UnknownTypeException {
        if (node != ROOT) {
            ChildWatch[] children = checkChildren(node);
            return children == null || children.length == 0;
        }
        return node != ROOT;
    }

    @Override
    public int getChildrenCount(Object node) throws UnknownTypeException {
        if (node == ROOT) {
            Object[] ws = DebuggerManager.getDebuggerManager().getWatches();
            return ws.length;
        } else {
            ChildWatch[] children = checkChildren(node);
            return children != null ? children.length : 0;
        }
    }

    /*
     @Override
     public boolean canReorder(Object parent) throws UnknownTypeException {
     return parent == ROOT;
     }

     @Override
     public void reorder(Object parent, int[] perm) throws UnknownTypeException {
     if (parent == ROOT) {
     int numWatches = DebuggerManager.getDebuggerManager().getWatches().length;
     // Resize - filters can add or remove children
     perm = resizePermutation(perm, numWatches);
     DebuggerManager.getDebuggerManager().reorderWatches(perm);
     } else {
     throw new UnknownTypeException(parent);
     }
     }

     private static int[] resizePermutation(int[] perm, int size) {
     if (size == perm.length) {
     return perm;
     }
     int[] nperm = new int[size];
     if (size < perm.length) {
     int j = 0;
     for (int i = 0; i < perm.length; i++) {
     int p = perm[i];
     if (p < size) {
     nperm[j++] = p;
     }
     }
     } else {
     System.arraycopy(perm, 0, nperm, 0, perm.length);
     for (int i = perm.length; i < size; i++) {
     nperm[i] = i;
     }
     }
     return nperm;
     }
     */
    @Override
    public Breakpoint[] initBreakpoints() {
        fireChanges();
        return null;
    }

    @Override
    public void breakpointAdded(Breakpoint breakpoint) {
        fireChanges();
    }

    @Override
    public void breakpointRemoved(Breakpoint breakpoint) {
        fireChanges();
    }

    @Override
    public void initWatches() {
        fireChanges();
    }

    @Override
    public void watchAdded(Watch watch) {
        watch.addPropertyChangeListener(this);
        fireChanges();
    }

    @Override
    public void watchRemoved(Watch watch) {
        watch.removePropertyChangeListener(this);
        fireChanges();
    }

    @Override
    public void sessionAdded(Session session) {
        fireChanges();
    }

    @Override
    public void sessionRemoved(Session session) {
        fireChanges();
    }

    @Override
    public void engineAdded(DebuggerEngine engine) {
        fireChanges();
    }

    @Override
    public void engineRemoved(DebuggerEngine engine) {
        fireChanges();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        fireChanges();
    }

    private ChildWatch[] checkChildren(Object w) throws UnknownTypeException {
        try {
            ChildWatch[] children = structure.get(w);
            if (children == null) {
                String parentExpression = extractWatchExpression(w);
                String[] childNames = environment.mDebugger.props(parentExpression);
                children = new ChildWatch[childNames.length];
                for (int i = 0; i < children.length; i++) {
                    children[i] = new ChildWatch();
                    children[i].displayExpression = childNames[i];
                    children[i].expression = "(" + parentExpression + ")." + childNames[i];
                }
                structure.put(w, children);
            }
            return children;
        } catch (Exception ex) {
            throw new UnknownTypeException(ex);
        }
    }
}
