/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jeta.forms.store.properties;

import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.form.GridView;
import java.awt.Component;
import java.util.HashMap;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;

/**
 *
 * @author mg
 */
public class TabbedPaneSelectedComponentProperty extends DynamicComponentRefProperty {

    public TabbedPaneSelectedComponentProperty() {
        super("selectedTab", JComponent.class);
    }

    @Override
    public void resolveReferences(JETABean jbean, HashMap<Long, GridComponent> aAllComps, String propName) {
        super.resolveReferences(jbean, aAllComps, propName);
    }

    @Override
    public void setValue(Object obj) {
        if (obj != null) {
            if (obj instanceof ComponentRefProperty) {
                ComponentRefProperty ppr = (ComponentRefProperty) obj;
                gridComponent = ppr.getGridComponent();
                if (gridComponent != null) {
                    m_ComponentID = gridComponent.getComponentID();
                } else {
                    m_ComponentID = ppr.getComponentID();
                }
                if (m_name == null || m_name.isEmpty()) {
                    m_name = ppr.getName();
                }
            }
        } else {
            super.setValue(obj);
        }
    }

    @Override
    public void setGridComponent(GridComponent aGridComponent) {
        if (aGridComponent != null) {
            if (aGridComponent instanceof FormComponent
                    && ((FormComponent) aGridComponent).isTopParent()
                    && aGridComponent.getBeanDelegate() instanceof GridView) {
                GridView gv = (GridView) aGridComponent.getBeanDelegate();
                GridComponent gc = gv.getGridComponent(0);
                assert gc instanceof FormComponent;
                super.setGridComponent(gc);
            } else if (aGridComponent instanceof FormComponent
                    && ((FormComponent) aGridComponent).isEmbedded()) {
                super.setGridComponent(aGridComponent);
            }
        } else {
            super.setGridComponent(aGridComponent);
        }
    }

    @Override
    protected void setValue2Bean(Component comp) {
        assert comp instanceof JTabbedPane;
        if (gridComponent != null) {
            if (gridComponent.getParent() == comp) {
                ((JTabbedPane) comp).setSelectedComponent(gridComponent);
            } else if (gridComponent instanceof FormComponent) {
                FormComponent fc = (FormComponent) gridComponent;
                if (fc.getParentForm() != null
                        && fc.getParentForm().getParent() == comp) {
                    ((JTabbedPane) comp).setSelectedComponent(fc.getParentForm());
                }
            }
        }
    }
}
