/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.debugger.ui;

import com.eas.debugger.jmx.server.DebuggerMBean;
import com.eas.designer.debugger.DebuggerConstants;
import com.eas.designer.debugger.DebuggerEnvironment;
import java.beans.PropertyChangeEvent;
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
import org.netbeans.modules.debugger.ui.models.WatchesTreeModel;
import org.netbeans.spi.debugger.ContextProvider;
import org.netbeans.spi.debugger.DebuggerServiceRegistration;
import org.netbeans.spi.viewmodel.ModelEvent;
import org.netbeans.spi.viewmodel.ModelListener;
import org.netbeans.spi.viewmodel.TreeModel;
import org.netbeans.spi.viewmodel.UnknownTypeException;

/**
 *
 * @author mg
 */
@DebuggerServiceRegistration(path = "PlatypusJsSession/WatchesView",
        types = {TreeModel.class},
        position = 10002)
public class JsWatchesTreeModel extends WatchesTreeModel implements NotificationListener, DebuggerManagerListener {

    protected Set<ModelListener> listeners = new HashSet<>();
    protected DebuggerEnvironment environment;
    protected Map<Object, ChildWatch[]> structure = new HashMap<>();

    public JsWatchesTreeModel(ContextProvider contextProvider) throws Exception {
        super();
        environment = contextProvider.lookupFirst(DebuggerConstants.DEBUGGER_SERVICERS_PATH, DebuggerEnvironment.class);
        ObjectName mBeanDebuggerName = new ObjectName(DebuggerMBean.DEBUGGER_MBEAN_NAME);
        MBeanServerConnection jmxConnection = contextProvider.lookupFirst(DebuggerConstants.DEBUGGER_SERVICERS_PATH, MBeanServerConnection.class);
        jmxConnection.addNotificationListener(mBeanDebuggerName, this, null, null);
        DebuggerManager.getDebuggerManager().addDebuggerListener(this);
        super.addModelListener(new ModelListener(){

            @Override
            public void modelChanged(ModelEvent event) {
                structure.clear();
                for(ModelListener l : listeners.toArray(new ModelListener[]{})){
                    l.modelChanged(event);
                }
            }
        });
    }

    @Override
    public Object[] getChildren(Object parent, int from, int to) throws UnknownTypeException {
        if (parent instanceof Watch || parent instanceof ChildWatch) {
            ChildWatch[] children = checkChildren(parent);
            return children != null ? children : new Watch[]{};
        } else {
            return super.getChildren(parent, from, to);
        }
    }

    @Override
    public boolean isLeaf(Object node) throws UnknownTypeException {
        if (node instanceof Watch || node instanceof ChildWatch) {
            ChildWatch[] children = checkChildren(node);
            return children == null || children.length == 0;
        } else {
            return super.isLeaf(node);
        }
    }

    @Override
    public int getChildrenCount(Object node) throws UnknownTypeException {
        if (node instanceof Watch || node instanceof ChildWatch) {
            ChildWatch[] children = checkChildren(node);
            return children != null ? children.length : 0;
        } else {
            return super.getChildrenCount(node);
        }
    }

    private ChildWatch[] checkChildren(Object w) throws UnknownTypeException {
        try {
            ChildWatch[] children = structure.get(w);
            if (children == null) {
                String parentExpression = JsWatchesTableModel.extractWatchExpression(w);
                String[] childNames = environment.mDebugger.props(parentExpression);
                children = new ChildWatch[childNames.length];
                for (int i = 0; i < children.length; i++) {
                    children[i] = new ChildWatch();
                    children[i].displayExpression = childNames[i];
                    children[i].expression = "(" + parentExpression + ")[\"" + childNames[i]+"\"]";
                }
                structure.put(w, children);
            }
            return children;
        } catch (Exception ex) {
            throw new UnknownTypeException(ex);
        }
    }

    @Override
    public void handleNotification(Notification notification, Object handback) {
        fireChanges();
    }

    private void fireTreeChanged() {
        ModelListener[] v = listeners.toArray(new ModelListener[]{});
        int k = v.length;
        for (int i = 0; i < k; i++) {
            v[i].modelChanged(
                    new ModelEvent.NodeChanged(this, ROOT));
            v[i].modelChanged(
                    new ModelEvent.NodeChanged(this, ROOT, ModelEvent.NodeChanged.CHILDREN_MASK));
        }
    }

    private void fireWatchPropertyChanged(Watch b, String propertyName) {
        ModelListener[] v = listeners.toArray(new ModelListener[]{});
        int k = v.length;
        for (int i = 0; i < k; i++) {
            v[i].modelChanged(
                    new ModelEvent.NodeChanged(this, b));
        }
    }

    protected void fireChanges() {
        structure.clear();
        fireTreeChanged();
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
        if (evt.getSource() instanceof Watch) {
            fireWatchPropertyChanged((Watch) evt.getSource(), null);
        }
    }
}
