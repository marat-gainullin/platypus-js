package com.eas.designer.debugger.ui;

import com.eas.debugger.jmx.server.DebuggerMBean;
import com.eas.designer.debugger.DebuggerConstants;
import com.eas.designer.debugger.DebuggerEnvironment;
import java.util.HashSet;
import java.util.Set;
import javax.management.MBeanServerConnection;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;

import org.netbeans.api.debugger.Watch;
import org.netbeans.spi.debugger.ContextProvider;
import org.netbeans.spi.viewmodel.ModelEvent;
import org.netbeans.spi.viewmodel.NodeModel;
import org.netbeans.spi.viewmodel.TableModel;
import org.netbeans.spi.viewmodel.ModelListener;
import org.netbeans.spi.viewmodel.UnknownTypeException;
import org.netbeans.spi.debugger.ui.Constants;
import org.netbeans.spi.viewmodel.NodeModelFilter;

/**
 *
 * @author   Jan Jancura
 */
public class WatchesModel implements NodeModelFilter, TableModel, NotificationListener {

    private Set<ModelListener> listeners = new HashSet<>();
    protected DebuggerEnvironment environment;

    public WatchesModel(ContextProvider contextProvider) throws Exception {
        super();
        environment = contextProvider.lookupFirst(DebuggerConstants.DEBUGGER_SERVICERS_PATH, DebuggerEnvironment.class);
        ObjectName mBeanDebuggerName = new ObjectName(DebuggerMBean.DEBUGGER_MBEAN_NAME);
        MBeanServerConnection jmxConnection = contextProvider.lookupFirst(DebuggerConstants.DEBUGGER_SERVICERS_PATH, MBeanServerConnection.class);
        jmxConnection.addNotificationListener(mBeanDebuggerName, this, null, null);
    }

    // NodeModelFilter implementation ................................................
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
    public String getDisplayName(NodeModel model, Object node) throws UnknownTypeException {
        return model.getDisplayName(node);
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
    public String getIconBase(NodeModel model, Object node) throws UnknownTypeException {
        return model.getIconBase(node);
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
    public String getShortDescription(NodeModel model, Object node) throws UnknownTypeException {
        return model.getShortDescription(node);
    }

    // TableModel implementation ...............................................
    /**
     * Returns value to be displayed in column <code>columnID</code>
     * and row identified by <code>node</code>. Column ID is defined in by 
     * {@link ColumnModel#getID}, and rows are defined by values returned from 
     * {@link org.netbeans.spi.viewmodel.TreeModel#getChildren}.
     *
     * @param node a object returned from 
     *         {@link org.netbeans.spi.viewmodel.TreeModel#getChildren} for this row
     * @param columnID a id of column defined by {@link ColumnModel#getID}
     * @throws ComputingException if the value is not known yet and will 
     *         be computed later
     * @throws UnknownTypeException if there is no TableModel defined for given
     *         parameter type
     *
     * @return value of variable representing given position in tree table.
     */
    @Override
    public Object getValueAt(Object node, String columnID) throws UnknownTypeException {
        if (Constants.WATCH_TO_STRING_COLUMN_ID.equals(columnID)
                || Constants.WATCH_VALUE_COLUMN_ID.equals(columnID)) {
            if (node instanceof Watch) {
                String expression = ((Watch) node).getExpression();
                try {
                    return environment.mDebugger.evaluate(expression);
                } catch (Exception ex) {
                    return ex.getMessage();
                }
            }
        }
        if (Constants.WATCH_TYPE_COLUMN_ID.equals(columnID) && node instanceof Watch) {
            return "";
        }
        throw new UnknownTypeException(node);
    }

    /**
     * Returns true if value displayed in column <code>columnID</code>
     * and row <code>node</code> is read only. Column ID is defined in by 
     * {@link ColumnModel#getID}, and rows are defined by values returned from 
     * {@link TreeModel#getChildren}.
     *
     * @param node a object returned from {@link TreeModel#getChildren} for this row
     * @param columnID a id of column defined by {@link ColumnModel#getID}
     * @throws UnknownTypeException if there is no TableModel defined for given
     *         parameter type
     *
     * @return true if variable on given position is read only
     */
    @Override
    public boolean isReadOnly(Object node, String columnID) throws UnknownTypeException {
        if (Constants.WATCH_TO_STRING_COLUMN_ID.equals(columnID)
                || Constants.WATCH_VALUE_COLUMN_ID.equals(columnID)
                || Constants.WATCH_TYPE_COLUMN_ID.equals(columnID)) {
            if (node instanceof Watch) {
                return true;
            }
        }
        throw new UnknownTypeException(node);
    }

    /**
     * Changes a value displayed in column <code>columnID</code>
     * and row <code>node</code>. Column ID is defined in by 
     * {@link ColumnModel#getID}, and rows are defined by values returned from 
     * {@link TreeModel#getChildren}.
     *
     * @param node a object returned from {@link TreeModel#getChildren} for this row
     * @param columnID a id of column defined by {@link ColumnModel#getID}
     * @param value a new value of variable on given position
     * @throws UnknownTypeException if there is no TableModel defined for given
     *         parameter type
     */
    @Override
    public void setValueAt(Object node, String columnID, Object value) throws UnknownTypeException {
        throw new UnknownTypeException(node);
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
        ModelEvent.TreeChanged event = new ModelEvent.TreeChanged(this);
        ModelListener[] v = listeners.toArray(new ModelListener[0]);
        for (ModelListener l : v) {
            l.modelChanged(event);
        }
    }

    @Override
    public void handleNotification(Notification notification, Object handback) {
        fireChanges();
    }

}
