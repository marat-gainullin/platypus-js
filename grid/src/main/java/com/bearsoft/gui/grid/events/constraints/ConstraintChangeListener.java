/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.events.constraints;

/**
 *
 * @author Gala
 */
public interface ConstraintChangeListener {

    public void constraintMinimumChanged(ConstraintMinimumChangedEvent anEvent);

    public void constraintMaximumChanged(ConstraintMaximumChangedEvent anEvent);
}
