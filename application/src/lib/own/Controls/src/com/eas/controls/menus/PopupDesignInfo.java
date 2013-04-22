/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.menus;

import com.eas.controls.ControlDesignInfo;
import com.eas.store.Serial;
import com.eas.controls.DesignInfo;
import com.eas.controls.ControlsDesignInfoVisitor;

/**
 *
 * @author mg
 */
public class PopupDesignInfo extends ControlDesignInfo {

    protected boolean borderPainted = true;
    protected String label;

    public PopupDesignInfo() {
        super();
    }

    @Serial
    public boolean isBorderPainted() {
        return borderPainted;
    }

    @Serial
    public void setBorderPainted(boolean aValue) {
        boolean oldValue = borderPainted;
        borderPainted = aValue;
        firePropertyChange("borderPainted", oldValue, borderPainted);
    }

    @Serial
    public String getLabel() {
        return label;
    }

    @Serial
    public void setLabel(String aValue) {
        String oldValue = label;
        label = aValue;
        firePropertyChange("label", oldValue, label);
    }

    @Override
    public boolean isEqual(Object obj) {
        if (!super.isEqual(obj)) {
            return false;
        }
        PopupDesignInfo other = (PopupDesignInfo) obj;
        if (borderPainted != other.borderPainted) {
            return false;
        }
        if ((this.label == null) ? (other.label != null) : !this.label.equals(other.label)) {
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
        if (aValue instanceof PopupDesignInfo) {
            PopupDesignInfo source = (PopupDesignInfo) aValue;
            borderPainted = source.borderPainted;
            label = source.label != null ? new String(source.label.toCharArray()) : null;
        }
    }
}
