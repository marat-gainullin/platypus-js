/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bearsoft.gui.grid.events.insets;

import com.bearsoft.gui.grid.insets.LinearInset;

/**
 *
 * @author Gala
 */
public class InsetPreFirstChangedEvent extends InsetChangedEvent{

    protected int oldValue;
    protected int newValue;

    public InsetPreFirstChangedEvent(LinearInset aSource, int aOldValue, int aNewValue)
    {
        super(aSource);
        oldValue = aOldValue;
        newValue = aNewValue;
    }

    public int getNewValue() {
        return newValue;
    }

    public int getOldValue() {
        return oldValue;
    }
}
