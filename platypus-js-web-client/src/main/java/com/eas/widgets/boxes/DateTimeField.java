package com.eas.widgets.boxes;

import com.google.gwt.dom.client.Document;

/**
 *
 * @author mgainullin
 */
public class DateTimeField extends TextField {

    public DateTimeField() {
        super(Document.get().createDateTimeInputElement());
    }

}
