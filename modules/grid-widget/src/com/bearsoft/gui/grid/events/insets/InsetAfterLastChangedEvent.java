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
public class InsetAfterLastChangedEvent extends InsetPreFirstChangedEvent {

    public InsetAfterLastChangedEvent(LinearInset aSource, int aOldValue, int aNewValue) {
        super(aSource, aOldValue, aNewValue);
    }
}
