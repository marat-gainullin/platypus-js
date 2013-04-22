/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.controls.menus;

import com.eas.controls.ControlsDesignInfoVisitor;

/**
 *
 * @author mg
 */
public class MenuRadioItemDesignInfo extends MenuItemDesignInfo{

    public MenuRadioItemDesignInfo()
    {
        super();
    }

    @Override
    public void accept(ControlsDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }

}
