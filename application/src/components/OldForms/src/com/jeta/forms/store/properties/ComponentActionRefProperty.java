/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jeta.forms.store.properties;

import com.jeta.forms.gui.actions.*;
import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;
import com.jeta.open.i18n.I18N;
import com.jeta.open.rules.JETARule;
import com.jeta.open.rules.RuleResult;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.Action;

class IsActionsRule implements JETARule {

    IsActionsRule() {
        //no op
    }

    @Override
    public RuleResult check(Object[] params) {
        if (params != null && params.length > 0
                && params[0] instanceof Component) {
            Component lcomp = (Component) params[0];
            if (lcomp != null) {
                GridComponent lgc = FormUtils.getFirstGridComponent(lcomp);
                if (lgc != null && (lcomp == lgc || lcomp == lgc.getBeanDelegate()) && lgc.containsAnyActions()) {
                    return RuleResult.SUCCESS;
                }
            }
        }
        return RuleResult.FAIL;
    }
}

/**
 *
 * @author Marat
 */
public class ComponentActionRefProperty extends ComponentRefProperty implements Action, PropertyChangeListener {

    static final long serialVersionUID = -8205658876641104577L;
    /**
     * The current version number of this class
     */
    public static final int VERSION = 1;
    public Long componentActionID = null;
    public Action componentAction = null;
    public static final String ACTION_ID_NAME = "actionID";
    public static final ComponentActionRefProperty EMPTY_ACTION_REF = new ComponentActionRefProperty();
    private boolean enabled = true;
    private HashSet<PropertyChangeListener> listeners = new HashSet<PropertyChangeListener>();
    private HashMap<String, Object> values = new HashMap<String, Object>();
    private static final String ACTION_PROP_NAME = "componentAction";

    public ComponentActionRefProperty() {
        super();
        compClassRule = new IsActionsRule();
    }

    @Override
    public void clearReferences() {
        if (componentAction != null) {
            componentAction.removePropertyChangeListener(this);
        }
        super.clearReferences();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (componentAction != null && e.getSource() != null
                && e.getSource() instanceof Component) {
            Component lSource = (Component) e.getSource();
            componentAction.actionPerformed(new ComponentActionEvent(getGridComponent(), lSource, 0, "", System.currentTimeMillis(), 0));
        }
    }

    @Override
    public Object getValue(String key) {
        if (componentAction != null) {
            return componentAction.getValue(key);
        } else {
            return values.get(key);
        }
    }

    @Override
    public void putValue(String key, Object value) {
        values.put(key, value);
    }

    @Override
    public void setEnabled(boolean b) {
        enabled = b;
    }

    @Override
    public boolean isEnabled() {
        return enabled && (componentAction == null || componentAction.isEnabled());
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listeners.remove(listener);
    }

    @Override
    public String getName() {
        return ACTION_PROP_NAME;
    }

    @Override
    public void setValue(Object obj) {
        if (componentAction != null) {
            componentAction.removePropertyChangeListener(this);
        }
        super.setValue(obj);
        if (obj instanceof ComponentActionRefProperty) {
            ComponentActionRefProperty cap = (ComponentActionRefProperty) obj;
            m_ComponentID = cap.getComponentID();
            componentAction = null; // It's essensial to avoid memory leaks (it's possible to listen property changes in rutime from design action)
            componentActionID = cap.getComponentActionID();
        }
        if (componentAction != null) {
            componentAction.addPropertyChangeListener(this);
        }
    }

    @Override
    public String toString() {
        return I18N.getLocalizedMessage("action");
    }

    @Override
    public JETARule getNeededRule() {
        return new IsActionsRule();
    }

    public void setComponentAction(ComponentAction action) {
        if (componentAction != null) {
            componentAction.removePropertyChangeListener(this);
        }
        this.componentAction = action;
        if (componentAction != null) {
            setComponentActionID(action.getID());
        } else {
            setComponentActionID(-1L);
        }
        if (componentAction != null) {
            componentAction.addPropertyChangeListener(this);
        }
    }

    public void setAction(Action action) {
        if (componentAction != null) {
            componentAction.removePropertyChangeListener(this);
        }
        componentAction = action;
        if (componentAction instanceof ComponentAction) {
            setComponentAction((ComponentAction) action);
        } else {
            if (componentAction != null) {
                Object oId = componentAction.getValue(GridComponent.ACTIONMAP_KEY_OF_ACTION);
                if (oId != null && oId instanceof Long) {
                    setComponentActionID((Long) oId);
                } else {
                    setComponentActionID(-1L);
                }
            } else {
                setComponentActionID(-1L);
            }
        }
        if (componentAction != null) {
            componentAction.addPropertyChangeListener(this);
        }
    }

    public Action getComponentAction() {
        return componentAction;
    }

    public void setComponentActionID(Long componentActionID) {
        this.componentActionID = componentActionID;
    }

    public Long getComponentActionID() {
        return componentActionID;
    }

    @Override
    public void resolveReferences(JETABean jbean, HashMap<Long, GridComponent> aAllComps, String propName) {
        gridComponent = null;
        GridComponent gc = aAllComps.get(m_ComponentID);
        if (gc != null) {
            setGridComponent(gc);
            Action action = gc.getAction(componentActionID);
            if (action != null) {
                if (action instanceof ComponentAction) {
                    setComponentAction((ComponentAction) action);
                } else {
                    setAction(action);
                }
            }
        } else {
            gridComponent = null;
            componentAction = null;
        }
    }

    @Override
    public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
        super.read(in.getSuperClassInput());
        int version = in.readVersion();
        componentActionID = (Long) in.readObject(ACTION_ID_NAME, -1L);
    }

    @Override
    public void write(JETAObjectOutput out) throws IOException {
        super.write(out.getSuperClassOutput(ComponentRefProperty.class));
        out.writeVersion(VERSION);
        out.writeObject(ACTION_ID_NAME, componentActionID);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        for (PropertyChangeListener l : listeners) {
            l.propertyChange(evt);
        }
    }
}
