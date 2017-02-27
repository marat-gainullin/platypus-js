/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.events.constraints;

import com.bearsoft.gui.grid.constraints.LinearConstraint;

/**
 *
 * @author mg
 */
public class ConstraintChangedEvent {

    protected LinearConstraint source;

    public ConstraintChangedEvent(LinearConstraint aSource) {
        super();
        source = aSource;
    }

    public LinearConstraint getSource() {
        return source;
    }
}
