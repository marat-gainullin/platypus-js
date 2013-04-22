/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.layouts;

import com.eas.store.Serial;
import com.eas.controls.DesignInfo;
import java.awt.FlowLayout;

/**
 *
 * @author mg
 */
public class FlowLayoutDesignInfo extends LayoutDesignInfo {

    protected int hgap = 5;
    protected int vgap = 5;
    protected boolean alignOnBaseline;
    protected int alignment = FlowLayout.CENTER;

    public FlowLayoutDesignInfo() {
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

    @Serial
    public boolean isAlignOnBaseline() {
        return alignOnBaseline;
    }

    @Serial
    public void setAlignOnBaseline(boolean aValue) {
        boolean oldValue = alignOnBaseline;
        alignOnBaseline = aValue;
        firePropertyChange("alignOnBaseline", oldValue, alignOnBaseline);
    }

    @Serial
    public int getAlignment() {
        return alignment;
    }

    @Serial
    public void setAlignment(int aValue) {
        int oldValue = alignment;
        alignment = aValue;
        firePropertyChange("alignment", oldValue, alignment);
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
        final FlowLayoutDesignInfo other = (FlowLayoutDesignInfo) obj;
        if (this.hgap != other.hgap) {
            return false;
        }
        if (this.vgap != other.vgap) {
            return false;
        }
        if (this.alignOnBaseline != other.alignOnBaseline) {
            return false;
        }
        if (this.alignment != other.alignment) {
            return false;
        }
        return true;
    }

    @Override
    public void assign(DesignInfo aSource) {
        if (aSource instanceof FlowLayoutDesignInfo) {
            FlowLayoutDesignInfo source = (FlowLayoutDesignInfo) aSource;
            hgap = source.hgap;
            vgap = source.vgap;
            alignOnBaseline = source.alignOnBaseline;
            alignment = source.alignment;
        }
    }
}
