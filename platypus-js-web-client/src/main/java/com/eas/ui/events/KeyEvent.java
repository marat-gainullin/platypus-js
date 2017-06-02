package com.eas.ui.events;

import com.eas.ui.Widget;
import com.google.gwt.dom.client.NativeEvent;

/**
 *
 * @author mgainullin
 */
public class KeyEvent extends Event {

    public KeyEvent(Widget target, NativeEvent event) {
        super(target, target, event);
    }
    
}
