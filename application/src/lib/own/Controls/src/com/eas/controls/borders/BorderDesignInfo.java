/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.borders;

import com.eas.controls.DesignInfo;

/**
 *
 * @author mg
 */
public abstract class BorderDesignInfo extends DesignInfo{

    public abstract void accept(BorderDesignInfoVisitor aVisitor);
    
    @Override
    public boolean isEqual(Object obj) {
        return obj != null && getClass().isAssignableFrom(obj.getClass());
    }
    
}
