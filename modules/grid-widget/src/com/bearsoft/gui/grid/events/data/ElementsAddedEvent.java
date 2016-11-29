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
public class ElementsAddedEvent<T> extends ElementsRemovedEvent<T> {

    public ElementsAddedEvent(List<T> aElements) {
        super(aElements);
    }

    public ElementsAddedEvent(List<T> aElements, boolean aAjusting) {
        super(aElements, aAjusting);
    }
}
