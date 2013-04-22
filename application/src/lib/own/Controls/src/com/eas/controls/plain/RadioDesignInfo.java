/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.controls.plain;

import com.eas.controls.ControlsDesignInfoVisitor;
import javax.swing.SwingConstants;

/**
 *
 * @author mg
 */
public class RadioDesignInfo extends ButtonDesignInfo{

    public RadioDesignInfo()
    {
        super();
        borderPainted = false;
        opaque = false;
        horizontalAlignment = SwingConstants.LEADING;
    }

    @Override
    public void accept(ControlsDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }
}
