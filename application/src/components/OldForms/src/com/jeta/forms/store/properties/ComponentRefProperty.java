/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jeta.forms.store.properties;

import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;
import com.jeta.open.rules.JETARule;
import java.awt.Component;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author Marat
 */
public class ComponentRefProperty extends JETAProperty {

    static final long serialVersionUID = -7259719736183198546L;
    /**
     * The current version number of this class
     */
    public static final int VERSION = 1;
    public static final String NAME_COMPONENT_REF = "componentRef";
    protected JETARule compClassRule = null;
    protected JETABean m_jbean = null;
    /**
     * The name for this property
     */
    protected String m_name = "";
    protected Long m_ComponentID = -1L;
    protected transient GridComponent gridComponent = null;

    public ComponentRefProperty() {
        super();
        compClassRule = new ComponentClassRule(getNeededClass());
    }

    public ComponentRefProperty(Class<?> aClass) {
        super();
        compClassRule = new ComponentClassRule(aClass);
    }

    public ComponentRefProperty(ComponentRefProperty aValue) {
        this();
        setGridComponent(aValue.getGridComponent());
        setComponentID(aValue.getComponentID());
    }

    public void clearReferences() {
        gridComponent = null;
    }

    protected Class getNeededClass() {
        return Component.class;
    }

    public JETARule getNeededRule() {
        return compClassRule;
    }

    public void setPropertyValueBaseClass(Class<?> aClass) {
        if (compClassRule instanceof ComponentClassRule) {
            ((ComponentClassRule) compClassRule).setClass(aClass);
        }
    }

    public void setGridComponent(GridComponent aGridComponent) {
        gridComponent = aGridComponent;
        if (gridComponent != null) {
            setComponentID(gridComponent.getComponentID());
        } else {
            setComponentID(-1L);
        }
    }

    public GridComponent getGridComponent() {
        return gridComponent;
    }

    @Override
    public void setValue(Object obj) {
        if (obj instanceof ComponentRefProperty) {
            ComponentRefProperty ppr = (ComponentRefProperty) obj;
            m_ComponentID = ppr.getComponentID();
            gridComponent = ppr.getGridComponent();
            if (m_name == null || m_name.isEmpty()) {
                m_name = ppr.getName();
            }
        }
    }

    public void setComponentID(Long aComponentID) {
        m_ComponentID = aComponentID;
    }

    public Long getComponentID() {
        return m_ComponentID;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ComponentRefProperty) {
            ComponentRefProperty crp = (ComponentRefProperty) object;
            return (gridComponent == crp.getGridComponent() || m_ComponentID == crp.getComponentID());
        }
        return super.equals(object);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + (this.m_ComponentID != null ? this.m_ComponentID.hashCode() : 0);
        hash = 53 * hash + (this.gridComponent != null ? this.gridComponent.hashCode() : 0);
        return hash;
    }

    @Override
    public String getName() {
        return m_name;
    }

    public void setName(String aName) {
        m_name = aName;
    }

    @Override
    public boolean isPreferred() {
        return true;
    }

    @Override
    public boolean isTransient() {
        return false;
    }

    @Override
    public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
        super.read(in.getSuperClassInput());
        int version = in.readVersion();
        m_ComponentID = (Long) in.readObject(NAME_COMPONENT_REF, -1L);
    }

    @Override
    public void write(JETAObjectOutput out) throws IOException {
        super.write(out.getSuperClassOutput(JETAProperty.class));
        out.writeVersion(VERSION);
        out.writeObject(NAME_COMPONENT_REF, m_ComponentID);
    }

    @Override
    public void updateBean(JETABean jbean) {
        // no op;
    }

    @Override
    public void resolveReferences(JETABean jbean, HashMap<Long, GridComponent> aAllComps, String propName) {
        gridComponent = null;
        GridComponent gc = aAllComps.get(m_ComponentID);
        if (gc != null) {
            setGridComponent(gc);
            Component comp = gc.getBeanDelegate();
            if (comp != null) {
                updateBean(jbean);
            }
        }
    }
}
