package com.eas.designer.debugger.ui;

import com.eas.debugger.jmx.server.DebuggerMBean;
import com.eas.designer.application.indexer.IndexerQuery;
import com.eas.designer.debugger.CodePointInfo;
import com.eas.designer.debugger.DebuggerConstants;
import com.eas.designer.debugger.DebuggerEnvironment;
import com.eas.designer.debugger.DebuggerUtils;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.MBeanServerConnection;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.swing.Action;
import org.netbeans.spi.debugger.ContextProvider;
import org.netbeans.spi.debugger.ui.Constants;
import org.netbeans.spi.viewmodel.*;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 *
 * @author Jan Jancura
 */
public class CallStackModel implements TreeModel, NodeModel,
        NodeActionsProvider, TableModel, NotificationListener {

    protected class StackFrameInfo {

        public FileObject file;
        public CodePointInfo cInfo;

        @Override
        public String toString() {
            if (file != null) {
                String fileSData = file.getName();
                if (cInfo.functionName != null) {
                    fileSData += "." + cInfo.functionName;
                }
                fileSData += " : " + String.valueOf(cInfo.lineNo + 1) + " [ " + String.valueOf(cInfo.appElementId) + " ]";
                return fileSData;
            } else {
                return NbBundle.getMessage(DebuggerUtils.class, "LBL_file_unavailable");
            }
        }
    }
    public static final String CALL_STACK =
            "org/netbeans/modules/debugger/resources/callStackView/NonCurrentFrame";
    public static final String CURRENT_CALL_STACK =
            "org/netbeans/modules/debugger/resources/callStackView/CurrentFrame";
    private Set<ModelListener> listeners = new HashSet<>();
    protected DebuggerEnvironment environment;
    protected StackFrameInfo[] stack;

    public CallStackModel(ContextProvider contextProvider) throws Exception {
        super();
        environment = contextProvider.lookupFirst(DebuggerConstants.DEBUGGER_SERVICERS_PATH, DebuggerEnvironment.class);
        ObjectName mBeanDebuggerName = new ObjectName(DebuggerMBean.DEBUGGER_MBEAN_NAME);
        MBeanServerConnection jmxConnection = contextProvider.lookupFirst(DebuggerConstants.DEBUGGER_SERVICERS_PATH, MBeanServerConnection.class);
        jmxConnection.addNotificationListener(mBeanDebuggerName, this, null, null);
    }

    protected void validateStack() throws Exception {
        if (stack == null) {
            String[][] sStack = environment.mDebugger.getCallStack();
            if (sStack != null) {
                stack = new StackFrameInfo[sStack.length];
                for (int i = 0; i < stack.length; i++) {
                    StackFrameInfo info = new StackFrameInfo();
                    info.cInfo = CodePointInfo.valueOf(sStack[i]);
                    info.file = IndexerQuery.appElementId2File(info.cInfo.appElementId);
                    if (info.file == null) {
                        Logger.getLogger(CallStackModel.class.getName()).log(Level.WARNING, NbBundle.getMessage(DebuggerUtils.class, "LBL_file_unavailable_for_app_element", info.cInfo.appElementId));
                    }
                    stack[i] = info;
                }
            }
        }
    }

    protected void invalidateStack() {
        stack = null;
    }

    // TreeModel implementation ................................................
    /**
     * Returns the root node of the tree or null, if the tree is empty.
     *
     * @return the root node of the tree or null
     */
    @Override
    public Object getRoot() {
        return ROOT;
    }

    /**
     * Returns children for given parent on given indexes.
     *
     * @param parent a parent of returned nodes
     * @param from a start index
     * @param to a end index
     *
     * @throws NoInformationException if the set of children can not be resolved
     * @throws ComputingException if the children resolving process is time
     * consuming, and will be performed off-line
     * @throws UnknownTypeException if this TreeModel implementation is not able
     * to resolve children for given node type
     *
     * @return children for given parent on given indexes
     */
    @Override
    public Object[] getChildren(Object parent, int from, int to) throws UnknownTypeException {
        if (parent == ROOT) {
            try {
                validateStack();
                if (stack != null) {
                    return stack;
                }
            } catch (Exception ex) {
                throw new UnknownTypeException(ex);
            }
        }
        throw new UnknownTypeException(parent);
    }

    /**
     * Returns true if node is leaf.
     *
     * @throws UnknownTypeException if this TreeModel implementation is not able
     * to resolve dchildren for given node type
     * @return true if node is leaf
     */
    @Override
    public boolean isLeaf(Object node) throws UnknownTypeException {
        if (node == ROOT) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Returns number of children for given node.
     *
     * @param node the parent node
     * @throws NoInformationException if the set of children can not be resolved
     * @throws ComputingException if the children resolving process is time
     * consuming, and will be performed off-line
     * @throws UnknownTypeException if this TreeModel implementation is not able
     * to resolve children for given node type
     *
     * @return true if node is leaf
     * @since 1.1
     */
    @Override
    public int getChildrenCount(Object node) throws UnknownTypeException {
        if (node == ROOT) {
            try {
                validateStack();
                if (stack != null) {
                    return stack.length;
                } else {
                    return 0;
                }
            } catch (Exception ex) {
                throw new UnknownTypeException(ex);
            }
        }
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
        if (node == ROOT) {
            return ROOT;
        } else if (node instanceof StackFrameInfo) {
            StackFrameInfo frame = (StackFrameInfo) node;
            return frame.toString();
        }
        throw new UnknownTypeException(node);
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
        if (node == ROOT) {
            return null;
        } else {
            return CALL_STACK;
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
        throw new UnknownTypeException(node);
    }

    // NodeActionsProvider implementation ......................................
    /**
     * Performs default action for given node.
     *
     * @throws UnknownTypeException if this NodeActionsProvider implementation
     * is not able to resolve actions for given node type
     * @return display name for given node
     */
    @Override
    public void performDefaultAction(Object node) throws UnknownTypeException {
        if (node instanceof StackFrameInfo) {
            try {
                StackFrameInfo frame = (StackFrameInfo) node;
                frame.cInfo.show();
            } catch (Exception ex) {
                throw new UnknownTypeException(node);
            }
        }
        throw new UnknownTypeException(node);
    }

    /**
     * Returns set of actions for given node.
     *
     * @throws UnknownTypeException if this NodeActionsProvider implementation
     * is not able to resolve actions for given node type
     * @return display name for given node
     */
    @Override
    public Action[] getActions(Object node) throws UnknownTypeException {
        return new Action[]{};
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
        if (node instanceof StackFrameInfo && Constants.CALL_STACK_FRAME_LOCATION_COLUMN_ID.equals(columnID)) {
            try {
                StackFrameInfo frame = (StackFrameInfo) node;
                return frame.file.getPath();
            } catch (Exception ex) {
                throw new UnknownTypeException(node);
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
        return true;
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
        throw new UnknownTypeException(node);
    }

    // other mothods ...........................................................
    protected void fireChanges() {
        invalidateStack();
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
