/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.events.data;

/**
 * Treed model changes listener interface.
 * @author mg
 */
public interface TreedModelListener<T> {

    public void elementsDataChanged(ElementsDataChangedEvent<T> anEvent);

    public void elementsAdded(ElementsAddedEvent<T> anEvent);

    public void elementsRemoved(ElementsRemovedEvent<T> anEvent);
    
    /**
     * Indicates that whole structure has changed, including all data.
     * This method doesn't accept any parameters, because it has to be called only once, while performing some huge operation.
     * Such operation may be, for example data filtering operation. After filtering, it's not known what data is stay unchnaged and
     * what data is actually changed.
     */
    public void elementsStructureChanged();
}
