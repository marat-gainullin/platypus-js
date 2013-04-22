/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.containers;

import com.eas.controls.ContainerDesignInfo;
import com.eas.controls.ControlsDesignInfoVisitor;

/**
 *
 * @author mg
 */
public class LayersDesignInfo extends ContainerDesignInfo {

    public LayersDesignInfo()
    {
        super();
        opaque = false;
    }

    @Override
    public void accept(ControlsDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }
}
