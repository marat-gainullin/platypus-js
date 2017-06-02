package com.eas.ui.events;

import com.eas.ui.Widget;
import com.google.gwt.dom.client.NativeEvent;

/**
 *
 * @author mgainullin
 */
public class KeyPressEvent extends KeyEvent {

    public KeyPressEvent(Widget target, NativeEvent event) {
        super(target, event);
    }
    
}
