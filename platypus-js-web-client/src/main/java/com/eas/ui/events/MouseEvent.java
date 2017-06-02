package com.eas.ui.events;

import com.eas.ui.Widget;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;

/**
 *
 * @author mgainullin
 */
public class MouseEvent extends Event {

    public MouseEvent(Widget source, NativeEvent event) {
        super(source, (Widget) event.getEventTarget().<Element>cast().getPropertyObject("p-widget"), event);
    }

}
