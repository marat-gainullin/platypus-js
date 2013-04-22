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
public class GridBagLayoutDesignInfo extends LayoutDesignInfo {

    public GridBagLayoutDesignInfo()
    {
        super();
    }

    @Override
    public void accept(LayoutDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }

    @Override
    public void assign(DesignInfo aSource) {
    }

}
