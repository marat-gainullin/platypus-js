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
public class ConstraintMinimumChangedEvent extends ConstraintChangedEvent {

    protected int oldValue;
    protected int newValue;

    public ConstraintMinimumChangedEvent(LinearConstraint aSource, int aOldValue, int aNewValue) {
        super(aSource);
        oldValue = aOldValue;
        newValue = aNewValue;
    }

    public int getOldValue() {
        return oldValue;
    }

    public int getNewValue() {
        return newValue;
    }
}
