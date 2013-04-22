/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.layouts.constraints;

import com.eas.store.Serial;
import com.eas.controls.DesignInfo;

/**
 *
 * @author mg
 */
public class LayersLayoutConstraintsDesignInfo extends AbsoluteConstraintsDesignInfo {

    protected int layer;

    public LayersLayoutConstraintsDesignInfo() {
        super();
    }

    @Serial
    public int getLayer() {
        return layer;
    }

    @Serial
    public void setLayer(int aValue) {
        int oldValue = layer;
        layer = aValue;
        firePropertyChange("layer", oldValue, layer);
    }

    @Override
    public void accept(ConstraintsDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }

    @Override
    public boolean isEqual(Object obj) {
        if (!super.isEqual(obj)) {
            return false;
        }
        LayersLayoutConstraintsDesignInfo other = (LayersLayoutConstraintsDesignInfo) obj;
        if (layer != other.layer) {
            return false;
        }
        return true;
    }

    @Override
    public void assign(DesignInfo aSource) {
        super.assign(aSource);
        if (aSource instanceof LayersLayoutConstraintsDesignInfo) {
            LayersLayoutConstraintsDesignInfo source = (LayersLayoutConstraintsDesignInfo) aSource;
            layer = source.layer;
        }
    }
}
