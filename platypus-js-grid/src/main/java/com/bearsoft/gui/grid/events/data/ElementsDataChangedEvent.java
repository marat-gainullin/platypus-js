/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.events.data;

import java.util.List;

/**
 * The data elements set changes event. It holds information about data elements,
 * where data have been changed and column index of that changes.
 * @author Gala
 */
public class ElementsDataChangedEvent<T> extends DataChangedEvent {

    protected List<T> element;
    protected int colIndex = -1;
    
    /**
     * The default constructor of data changed event. Column index have -1 value and elements have a null value.
     * It all means that data of all elements at all columns have been changed.
     */
    public ElementsDataChangedEvent() {
        super();
    }

    /**
     * Elements data changed event constructor. It accepts some elements, where changes have been happened.
     * @param anElement List of elements, data changes are related to.
     * It may be null, that mean that all elements have been changed. 
     * @param aColIndex Index of the column, with changed data. Index is 0-based.
     * It may have a -1 value. Such case means that data in all columns have changed.
     */
    public ElementsDataChangedEvent(List<T> anElement, int aColIndex) {
        super();
        element = anElement;
        colIndex = aColIndex;
    }

    /**
     * Elements data changed event constructor. It accepts some elements, where changes have been happened.
     * @param anElement List of elements, data changes are related to.
     * It may be null, that mean that all elements have been changed. 
     * @param aColIndex Index of the column, with changed data. Index is 0-based.
     * @param aAjusting Ajusting flag, indicating that the event is not last in some series of data changes.
     */
    public ElementsDataChangedEvent(List<T> anElement, int aColIndex, boolean aAjusting) {
        super(aAjusting);
        element = anElement;
        colIndex = aColIndex;
    }

    /**
     * Returns a list of data elements have been changed.
     * @return A list of changed data elements. 
     * May be null if the event is posted to indicate global data change.
     * E.g. if all data has changed, than null will be returned.
     */
    public List<T> getElements() {
        return element;
    }

    /**
     * Returns column index, the data change is related to.
     * @return Column index of changed data. May be -1. It it is -1, it means that all data has changed.
     */
    public int getColIndex() {
        return colIndex;
    }
}
