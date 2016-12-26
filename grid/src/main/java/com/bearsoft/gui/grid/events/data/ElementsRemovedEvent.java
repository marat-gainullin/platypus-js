/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.events.data;

import java.util.List;

/**
 *
 * @author Gala
 */
public class ElementsRemovedEvent<T> extends DataChangedEvent{

    protected List<T> elements;

    public ElementsRemovedEvent(List<T> aElements) {
        super();
        elements = aElements;
    }

    public ElementsRemovedEvent(List<T> aElements, boolean aAjusting) {
        super(aAjusting);
        elements = aElements;
    }

    public List<T> getElements() {
        return elements;
    }
}
