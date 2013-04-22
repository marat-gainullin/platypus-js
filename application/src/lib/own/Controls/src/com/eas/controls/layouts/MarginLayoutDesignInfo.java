/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.layouts;

import com.eas.controls.DesignInfo;

/**
 *
 * @author lkolesnikov
 */
public class MarginLayoutDesignInfo extends LayoutDesignInfo {

    @Override
    public void accept(LayoutDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }

    @Override
    public void assign(DesignInfo aSource) {
    }
}
