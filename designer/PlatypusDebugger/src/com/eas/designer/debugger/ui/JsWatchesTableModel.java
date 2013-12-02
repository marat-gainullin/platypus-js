/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.debugger.ui;

import com.eas.designer.debugger.DebuggerConstants;
import com.eas.designer.debugger.DebuggerEnvironment;
import java.util.HashSet;
import java.util.Set;
import org.netbeans.api.debugger.Watch;
import org.netbeans.modules.debugger.ui.models.WatchesTableModel;
import org.netbeans.spi.debugger.ContextProvider;
import org.netbeans.spi.debugger.DebuggerServiceRegistration;
import org.netbeans.spi.debugger.DebuggerServiceRegistrations;
import org.netbeans.spi.debugger.ui.Constants;
import org.netbeans.spi.viewmodel.ModelListener;
import org.netbeans.spi.viewmodel.TableModel;
import org.netbeans.spi.viewmodel.UnknownTypeException;

/**
 *
 * @author mg
 */
@DebuggerServiceRegistrations({
    @DebuggerServiceRegistration(path = "PlatypusJsSession/WatchesView",
            types = TableModel.class,
            position = 10000),
    @DebuggerServiceRegistration(path = "PlatypusJsSession/LocalsView",
            types = TableModel.class,
            position = 10001)})
public class JsWatchesTableModel extends WatchesTableModel {

    protected Set<ModelListener> listeners = new HashSet<>();
    protected DebuggerEnvironment environment;

    public JsWatchesTableModel(ContextProvider contextProvider) throws Exception {
        super();
        environment = contextProvider.lookupFirst(DebuggerConstants.DEBUGGER_SERVICERS_PATH, DebuggerEnvironment.class);
    }

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
        if (node instanceof Watch || node instanceof ChildWatch) {
            if (Constants.WATCH_TO_STRING_COLUMN_ID.equals(columnID)
                    || Constants.WATCH_VALUE_COLUMN_ID.equals(columnID)
                    || Constants.LOCALS_TO_STRING_COLUMN_ID.equals(columnID)
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
            return "";// May be extra columns in in future
        } else {
            throw new UnknownTypeException(node);
        }
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
        if (node instanceof Watch || node instanceof ChildWatch) {
            return Constants.WATCH_TO_STRING_COLUMN_ID.equals(columnID)
                    || Constants.WATCH_VALUE_COLUMN_ID.equals(columnID)
                    || Constants.WATCH_TYPE_COLUMN_ID.equals(columnID)
                    || Constants.LOCALS_TO_STRING_COLUMN_ID.equals(columnID)
                    || Constants.LOCALS_VALUE_COLUMN_ID.equals(columnID)
                    || Constants.LOCALS_TYPE_COLUMN_ID.equals(columnID);
        } else {
            throw new UnknownTypeException(node);
        }
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
        if (node instanceof Watch || node instanceof ChildWatch) {
            if (Constants.WATCH_VALUE_COLUMN_ID.equals(columnID)
                    || Constants.LOCALS_VALUE_COLUMN_ID.equals(columnID)) {
                String expression = extractWatchExpression(node);
                try {
                    environment.mDebugger.evaluate(expression + " = " + String.valueOf(value));
                } catch (Exception ex) {
                }
            }
        } else {
            throw new UnknownTypeException(node);
        }
    }

    public static String extractWatchExpression(Object w) {
        if (w instanceof Watch) {
            return ((Watch) w).getExpression();
        } else if (w instanceof ChildWatch) {
            return ((ChildWatch) w).expression;
        } else {
            return "";
        }
    }
}
