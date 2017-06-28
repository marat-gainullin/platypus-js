package com.eas.grid;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 *
 * @author mgainullin
 */
public interface HasSortHandlers {

    HandlerRegistration addSortHandler(SortHandler handler);
}
