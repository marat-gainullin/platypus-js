/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.layouts;

import com.eas.store.Serial;
import com.eas.controls.DesignInfo;

/**
 *
 * @author mg
 */
public class BorderLayoutDesignInfo extends LayoutDesignInfo {

    protected int hgap;
    protected int vgap;

    public BorderLayoutDesignInfo()
    {
        super();
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
        final BorderLayoutDesignInfo other = (BorderLayoutDesignInfo) obj;
        if (this.hgap != other.hgap) {
            return false;
        }
        if (this.vgap != other.vgap) {
            return false;
        }
        return true;
    }

    @Override
    public void assign(DesignInfo aSource) {
        if (aSource instanceof BorderLayoutDesignInfo) {
            BorderLayoutDesignInfo source = (BorderLayoutDesignInfo) aSource;
            hgap = source.hgap;
            vgap = source.vgap;
        }
    }
}
