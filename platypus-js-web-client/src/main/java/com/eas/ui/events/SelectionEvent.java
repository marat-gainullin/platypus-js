package com.eas.ui.events;

/**
 *
 * @author mgainullin
 */
public class SelectionEvent<T> extends Event {

    private T selectedItem;

    public SelectionEvent(Object aSource, T aItem) {
        super(aSource, aItem);
        selectedItem = aItem;
    }

    public T getSelectedItem() {
        return selectedItem;
    }

}
