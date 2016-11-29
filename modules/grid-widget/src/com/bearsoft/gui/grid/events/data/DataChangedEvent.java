/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.events.data;

/**
 *
 * @author Gala
 */
public class DataChangedEvent {

    protected boolean ajusting;

    public DataChangedEvent() {
        super();
    }

    public DataChangedEvent(boolean aAjusting) {
        this();
        ajusting = aAjusting;
    }

    public boolean isAjusting() {
        return ajusting;
    }
}
