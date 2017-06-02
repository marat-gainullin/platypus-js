package com.eas.ui.events;

import com.eas.ui.Widget;
import com.google.gwt.dom.client.NativeEvent;

/**
 *
 * @author mgainullin
 */
public class KeyUpEvent extends KeyEvent {

    public KeyUpEvent(Widget target, NativeEvent event) {
        super(target, event);
    }
    
}
