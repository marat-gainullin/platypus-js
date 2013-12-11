/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.plain;

import com.eas.controls.ControlsDesignInfoVisitor;

/**
 *
 * @author mg
 */
public class PasswordFieldDesignInfo extends TextFieldDesignInfo {

    public PasswordFieldDesignInfo() {
        super();
    }

    @Override
    public void accept(ControlsDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }
}
