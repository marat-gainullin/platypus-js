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
public class BorderLayoutConstraintsDesignInfo extends LayoutConstraintsDesignInfo {

    protected String place;

    public BorderLayoutConstraintsDesignInfo() {
        super();
    }

    @Serial
    public String getPlace() {
        return place;
    }

    @Serial
    public void setPlace(String aValue) {
        String oldValue = place;
        place = aValue;
        firePropertyChange("place", oldValue, place);
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
        BorderLayoutConstraintsDesignInfo other = (BorderLayoutConstraintsDesignInfo) obj;
        if (place == null ? other.place != null : !place.equals(other.place)) {
            return false;
        }
        return true;
    }

    @Override
    public void assign(DesignInfo aSource) {
        if (aSource instanceof BorderLayoutConstraintsDesignInfo) {
            BorderLayoutConstraintsDesignInfo source = (BorderLayoutConstraintsDesignInfo) aSource;
            place = source.place != null ? new String(source.place.toCharArray()) : null;
        }
    }
}
