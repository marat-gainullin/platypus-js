/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.plain;

import com.eas.controls.ControlsDesignInfoVisitor;
import com.eas.controls.DesignInfo;
import com.eas.store.Serial;

/**
 *
 * @author mg
 */
public class DropDownButtonDesignInfo extends ButtonDesignInfo {

    public static final String MENU = "dropDownMenu";
    protected String dropDownMenu;

    public DropDownButtonDesignInfo() {
        super();
    }

    @Override
    public void accept(ControlsDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }

    @Serial
    public String getDropDownMenu() {
        return dropDownMenu;
    }

    @Serial
    public void setDropDownMenu(String aValue) {
        String oldValue = dropDownMenu;
        dropDownMenu = aValue;
        firePropertyChange(MENU, oldValue, dropDownMenu);
    }

    @Override
    public boolean isEqual(Object obj) {
        return super.isEqual(obj);
    }

    @Override
    public void assign(DesignInfo aValue) {
        super.assign(aValue);
        if (aValue instanceof DropDownButtonDesignInfo) {
            DropDownButtonDesignInfo source = (DropDownButtonDesignInfo) aValue;
            dropDownMenu = source.dropDownMenu != null ? new String(source.dropDownMenu.toCharArray()) : null;
        }
    }
    
}
