package com.eas.ui.events;

/**
 *
 * @author mgainullin
 */
public interface SelectionHandler<T> {

    /**
     * Called when {@link SelectionEvent} is fired.
     *
     * @param event the {@link SelectionEvent} that was fired
     */
    void onSelection(SelectionEvent<T> event);
}
