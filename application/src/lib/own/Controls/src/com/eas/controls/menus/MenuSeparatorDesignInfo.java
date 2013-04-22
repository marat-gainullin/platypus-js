/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.menus;

import com.eas.controls.ControlDesignInfo;
import com.eas.controls.ControlsDesignInfoVisitor;

/**
 *
 * @author mg
 */
public class MenuSeparatorDesignInfo extends ControlDesignInfo {

    public MenuSeparatorDesignInfo()
    {
        super();
        focusable = false;
        opaque = false;
    }

    @Override
    public void accept(ControlsDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }
}
