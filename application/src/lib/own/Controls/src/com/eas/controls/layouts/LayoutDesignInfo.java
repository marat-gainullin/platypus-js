/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.layouts;

import com.eas.controls.DesignInfo;

/**
 *
 * @author mg
 */
public abstract class LayoutDesignInfo extends DesignInfo {

    @Override
    public boolean isEqual(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return true;
    }

    public abstract void accept(LayoutDesignInfoVisitor aVisitor);
}
