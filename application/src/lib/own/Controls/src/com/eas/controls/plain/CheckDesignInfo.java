/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.plain;

import com.eas.store.Serial;
import com.eas.controls.DesignInfo;
import com.eas.controls.ControlsDesignInfoVisitor;
import javax.swing.SwingConstants;

/**
 *
 * @author mg
 */
public class CheckDesignInfo extends ButtonDesignInfo {

    protected boolean borderPaintedFlat;

    public CheckDesignInfo() {
        super();
        borderPainted = false;
        opaque = false;
        horizontalAlignment = SwingConstants.LEADING;
    }

    @Serial
    public boolean isBorderPaintedFlat() {
        return borderPaintedFlat;
    }

    @Serial
    public void setBorderPaintedFlat(boolean aValue) {
        boolean oldValue = borderPaintedFlat;
        borderPaintedFlat = aValue;
        firePropertyChange("borderPaintedFlat", oldValue, borderPaintedFlat);
    }

    @Override
    public boolean isEqual(Object obj) {
        if (!super.isEqual(obj)) {
            return false;
        }
        CheckDesignInfo other = (CheckDesignInfo) obj;
        if (this.borderPaintedFlat != other.borderPaintedFlat) {
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
        if (aValue instanceof CheckDesignInfo) {
            CheckDesignInfo source = (CheckDesignInfo) aValue;
            borderPaintedFlat = source.borderPaintedFlat;
        }
    }
}
