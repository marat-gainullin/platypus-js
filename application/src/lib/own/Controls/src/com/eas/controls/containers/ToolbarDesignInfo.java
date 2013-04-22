/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.containers;

import com.eas.controls.ContainerDesignInfo;
import com.eas.controls.ControlsDesignInfoVisitor;
import com.eas.controls.DesignInfo;
import com.eas.controls.SerialInsets;
import com.eas.store.Serial;
import java.awt.Insets;

/**
 *
 * @author mg
 */
public class ToolbarDesignInfo extends ContainerDesignInfo {

    protected Insets margin;
    protected boolean borderPainted = true;
    protected boolean floatable = true;
    protected boolean rollover;
    protected int orientation;

    public ToolbarDesignInfo() {
        super();
        autoscrolls = false;
    }

    @Serial
    public SerialInsets getEasMargin() {
        return margin != null ? new SerialInsets(margin) : null;
    }

    @Serial
    public void setEasMargin(SerialInsets aValue) {
        Insets oldValue = margin;
        if (aValue != null) {
            margin = aValue.getDelegate();
        } else {
            margin = null;
        }
        firePropertyChange("margin", oldValue, margin);
    }

    public Insets getMargin() {
        return margin;
    }

    public void setMargin(Insets aValue) {
        Insets oldValue = margin;
        margin = aValue;
        firePropertyChange("margin", oldValue, margin);
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
    public boolean isFloatable() {
        return floatable;
    }

    @Serial
    public void setFloatable(boolean aValue) {
        boolean oldValue = floatable;
        floatable = aValue;
        firePropertyChange("floatable", oldValue, floatable);
    }

    @Serial
    public boolean isRollover() {
        return rollover;
    }

    @Serial
    public void setRollover(boolean aValue) {
        boolean oldValue = rollover;
        rollover = aValue;
        firePropertyChange("rollover", oldValue, rollover);
    }

    @Serial
    public int getOrientation() {
        return orientation;
    }

    @Serial
    public void setOrientation(int aValue) {
        int oldValue = orientation;
        orientation = aValue;
        firePropertyChange("orientation", oldValue, orientation);
    }

    @Override
    public boolean isEqual(Object obj) {
        if (!super.isEqual(obj)) {
            return false;
        }
        ToolbarDesignInfo other = (ToolbarDesignInfo) obj;
        if ((this.margin == null) ? (other.margin != null) : !this.margin.equals(other.margin)) {
            return false;
        }
        if (borderPainted != other.borderPainted) {
            return false;
        }
        if (floatable != other.floatable) {
            return false;
        }
        if (rollover != other.rollover) {
            return false;
        }
        if (orientation != other.orientation) {
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
        if (aValue instanceof ToolbarDesignInfo) {
            ToolbarDesignInfo source = (ToolbarDesignInfo) aValue;
            margin = source.margin != null ? new Insets(source.margin.top, source.margin.left, source.margin.bottom, source.margin.right) : null;
            borderPainted = source.borderPainted;
            floatable = source.floatable;
            rollover = source.rollover;
            orientation = source.orientation;
        }
    }
}
