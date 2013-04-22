/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.events.constraints;

import com.bearsoft.gui.grid.constraints.LinearConstraint;

/**
 *
 * @author Gala
 */
public class ConstraintMaximumChangedEvent extends ConstraintMinimumChangedEvent {

    public ConstraintMaximumChangedEvent(LinearConstraint aSource, int aOldValue, int aNewValue) {
        super(aSource, aOldValue, aNewValue);
    }
}
