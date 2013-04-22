/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.containers;

import com.eas.controls.ContainerDesignInfo;
import com.eas.controls.ControlsDesignInfoVisitor;
import com.eas.controls.DesignInfo;
import com.eas.store.Serial;
import javax.swing.JTabbedPane;

/**
 *
 * @author mg
 */
public class TabsDesignInfo extends ContainerDesignInfo {

    protected int tabPlacement = JTabbedPane.TOP;
    protected int tabLayoutPolicy = JTabbedPane.WRAP_TAB_LAYOUT;
    protected String selectedComponent;

    public TabsDesignInfo() {
        super();
    }

    @Serial
    public int getTabLayoutPolicy() {
        return tabLayoutPolicy;
    }

    @Serial
    public void setTabLayoutPolicy(int aValue) {
        int oldValue = tabLayoutPolicy;
        tabLayoutPolicy = aValue;
        firePropertyChange("tabLayoutPolicy", oldValue, tabLayoutPolicy);
    }

    @Serial
    public int getTabPlacement() {
        return tabPlacement;
    }

    @Serial
    public void setTabPlacement(int aValue) {
        int oldValue = tabPlacement;
        tabPlacement = aValue;
        firePropertyChange("tabPlacement", oldValue, tabPlacement);
    }

    @Serial
    public String getSelectedComponent() {
        return selectedComponent;
    }

    @Serial
    public void setSelectedComponent(String aValue) {
        String oldValue = selectedComponent;
        selectedComponent = aValue;
        firePropertyChange("selectedComponent", oldValue, selectedComponent);
    }

    @Override
    public boolean isEqual(Object obj) {
        if (!super.isEqual(obj)) {
            return false;
        }
        TabsDesignInfo other = (TabsDesignInfo) obj;
        if ((this.selectedComponent == null) ? (other.selectedComponent != null) : !this.selectedComponent.equals(other.selectedComponent)) {
            return false;
        }
        if (this.tabPlacement != other.tabPlacement) {
            return false;
        }
        if (this.tabLayoutPolicy != other.tabLayoutPolicy) {
            return false;
        }
        return true;
    }

    @Override
    public void accept(ControlsDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }

    @Override
    public void assign(DesignInfo aValue) {
        super.assign(aValue);
        if (aValue instanceof TabsDesignInfo) {
            TabsDesignInfo source = (TabsDesignInfo) aValue;
            selectedComponent = source.selectedComponent != null ? new String(source.selectedComponent.toCharArray()) : null;
            tabPlacement = source.tabPlacement;
            tabLayoutPolicy = source.tabLayoutPolicy;
        }
    }
}
