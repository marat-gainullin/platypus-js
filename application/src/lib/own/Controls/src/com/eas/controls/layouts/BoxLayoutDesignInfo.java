/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.layouts;

import com.eas.controls.DesignInfo;
import com.eas.client.forms.layouts.BoxLayout;
import com.eas.store.Serial;

/**
 *
 * @author mg
 */
public class BoxLayoutDesignInfo extends LayoutDesignInfo {

    protected int axis = BoxLayout.LINE_AXIS;
    protected int hgap;
    protected int vgap;

    public BoxLayoutDesignInfo() {
        super();
    }

    @Serial
    public int getAxis() {
        return axis;
    }

    @Serial
    public void setAxis(int aValue) {
        int oldValue = axis;
        axis = aValue;
        firePropertyChange("axis", oldValue, axis);
    }

    @Serial
    public int getHgap() {
        return hgap;
    }

    @Serial
    public void setHgap(int aValue) {
        int oldValue = hgap;
        hgap = aValue;
        firePropertyChange("hgap", oldValue, hgap);
    }

    @Serial
    public int getVgap() {
        return vgap;
    }

    @Serial
    public void setVgap(int aValue) {
        int oldValue = vgap;
        vgap = aValue;
        firePropertyChange("vgap", oldValue, vgap);
    }

    @Override
    public void accept(LayoutDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }

    @Override
    public boolean isEqual(Object obj) {
        if (!super.isEqual(obj)) {
            return false;
        }
        final BoxLayoutDesignInfo other = (BoxLayoutDesignInfo) obj;
        if (axis != other.axis) {
            return false;
        }
        return true;
    }

    @Override
    public void assign(DesignInfo aSource) {
        if (aSource instanceof BoxLayoutDesignInfo) {
            BoxLayoutDesignInfo source = (BoxLayoutDesignInfo) aSource;
            axis = source.axis;
        }
    }
}
