/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.menus;

import com.eas.controls.ControlsDesignInfoVisitor;
import com.eas.controls.DesignInfo;
import com.eas.store.Serial;

/**
 *
 * @author mg
 */
public class MenuDesignInfo extends MenuItemDesignInfo {

    protected int delay = 200;
    protected int orientation;
    
    public MenuDesignInfo()
    {
        super();
    }

    @Serial
    public int getDelay() {
        return delay;
    }

    @Serial
    public void setDelay(int aValue) {
        int oldValue = delay;
        delay = aValue;
        firePropertyChange("delay", oldValue, delay);
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
        MenuDesignInfo other = (MenuDesignInfo) obj;
        if (this.delay != other.delay) {
            return false;
        }
        if (this.orientation != other.orientation) {
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
        if (aValue instanceof MenuDesignInfo) {
            MenuDesignInfo source = (MenuDesignInfo) aValue;
            delay = source.delay;
            orientation = source.orientation;
        }
    }
}
