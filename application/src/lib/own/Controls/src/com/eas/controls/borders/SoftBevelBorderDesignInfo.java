/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.borders;

/**
 *
 * @author mg
 */
public class SoftBevelBorderDesignInfo extends BevelBorderDesignInfo {

    @Override
    public void accept(BorderDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }
}
