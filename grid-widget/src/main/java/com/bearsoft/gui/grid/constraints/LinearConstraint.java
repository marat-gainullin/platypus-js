/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.constraints;

import com.bearsoft.gui.grid.events.constraints.ConstraintChangeListener;
import com.bearsoft.gui.grid.events.constraints.ConstraintMaximumChangedEvent;
import com.bearsoft.gui.grid.events.constraints.ConstraintMinimumChangedEvent;
import java.util.HashSet;
import java.util.Set;

/**
 * Simple one-dimensional constraint. It comprises left and right constraint.
 * Constraints value are inclusive.
 * @author Gala
 */
public class LinearConstraint {

    protected int min = 0;
    protected int max = Integer.MAX_VALUE;
    protected Set<ConstraintChangeListener> listeners = new HashSet<>();

    /**
     * Constraint constructor
     * @param aMin Left boundary of the constraint
     * @param aMax Right boundary of the constraint.
     */
    public LinearConstraint(int aMin, int aMax) {
        super();
        min = aMin;
        max = aMax;
    }

    /**
     * Tests if constraint is valid.
     * @return True if constraint is valid and false otherwise.
     */
    public boolean isValid() {
        return min >= 0 && max >= min;
    }

    /**
     * Tests if a value lies in constaint space inclusive.
     * @param aValue A value to test.
     * @return True if parameter value lies in constaint space inclusive.
     */
    public boolean inConstraint(int aValue) {
        return aValue >= min && aValue <= max;
    }

    /**
     * Calculate unconstraint value.
     * @param aVal Value in constrained space.
     * @return Value in general (unconstraint) space.
     */
    public int unconstraint(int aVal) {
        if (isValid()) {
            return aVal + min;
        } else {
            return aVal;
        }
    }

    /**
     * Calculate constraint value.
     * @param aVal Value in general (unconstraint) space.
     * @return Value in constrained space.
     */
    public int constraint(int aVal) {
        if (isValid()) {
            if (aVal < min) {
                aVal = min;
            }
            if (aVal > max) {
                aVal = max;
            }
            return aVal - min;
        } else {
            return aVal;
        }
    }

    /**
     * Returns left boundary of the constraint.
     * @return Left boundary of the constraint.
     */
    public int getMin() {
        return min;
    }

    /**
     * Sets left boundary of the constraint.
     * @param aValue left boundary value.
     */
    public void setMin(int aValue) {
        int oldValue = min;
        min = aValue;
        fireConstraintMinimumChanged(oldValue, min);
    }

    /**
     * Returns right boundary of the constraint.
     * @return Right boundary of the constraint.
     */
    public int getMax() {
        return max;
    }

    /**
     * Sets right boundary of the constraint.
     * @param aValue right boundary value.
     */
    public void setMax(int aValue) {
        int oldValue = max;
        max = aValue;
        fireConstraintMaximumChanged(oldValue, max);
    }

    protected void fireConstraintMinimumChanged(int aOldValue, int aNewValue) {
        ConstraintMinimumChangedEvent event = new ConstraintMinimumChangedEvent(this, aOldValue, aNewValue);
        for (ConstraintChangeListener l : listeners) {
            l.constraintMinimumChanged(event);
        }
    }

    protected void fireConstraintMaximumChanged(int aOldValue, int aNewValue) {
        ConstraintMaximumChangedEvent event = new ConstraintMaximumChangedEvent(this, aOldValue, aNewValue);
        for (ConstraintChangeListener l : listeners) {
            l.constraintMaximumChanged(event);
        }
    }

    /**
     * Adds a <code>ConstraintChangeListener</code> instance to this <code>LinearConstraint</code> listeneners.
     * It will recieve this constraint boudaries changes.
     * @param aListener <code>ConstraintChangeListener</code> instance to add.
     */
    public void addConstraintChangeListener(ConstraintChangeListener aListener) {
        listeners.add(aListener);
    }

    /**
     * Removes a <code>ConstraintChangeListener</code> instance from this <code>LinearConstraint</code> listeneners.
     * @param aListener <code>ConstraintChangeListener</code> instance to remove.
     */
    public void removeConstraintChangeListener(ConstraintChangeListener aListener) {
        listeners.remove(aListener);
    }

    @Override
    public String toString() {
        return String.format("Constraint [%d;%d]", min, max);
    }
}
