package com.eas.widgets;

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
