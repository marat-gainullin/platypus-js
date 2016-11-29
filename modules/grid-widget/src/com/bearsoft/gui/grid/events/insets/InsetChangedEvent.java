/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.events.insets;

import com.bearsoft.gui.grid.insets.LinearInset;

/**
 *
 * @author mg
 */
public class InsetChangedEvent {

    protected LinearInset source;

    public InsetChangedEvent(LinearInset aSource) {
        super();
        source = aSource;
    }

    public LinearInset getSource() {
        return source;
    }
}
