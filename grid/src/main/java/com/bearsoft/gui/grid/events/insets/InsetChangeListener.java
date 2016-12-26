/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.events.insets;

/**
 *
 * @author Gala
 */
public interface InsetChangeListener {

    public void insetPreFirstChanged(InsetPreFirstChangedEvent anEvent);

    public void insetAfterLastChanged(InsetAfterLastChangedEvent anEvent);
}
