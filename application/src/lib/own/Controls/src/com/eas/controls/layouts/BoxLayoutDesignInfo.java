/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.layouts;

import com.eas.controls.DesignInfo;
import com.eas.store.Serial;
import javax.swing.BoxLayout;

/**
 *
 * @author mg
 */
public class BoxLayoutDesignInfo extends LayoutDesignInfo {

    protected int axis = BoxLayout.LINE_AXIS;

    public BoxLayoutDesignInfo()
    {
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
