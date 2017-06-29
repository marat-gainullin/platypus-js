package com.eas.widgets;

import com.google.gwt.dom.client.Document;

/**
 *
 * @author mgainullin
 */
public class DateField extends TextField {

    public DateField() {
        super(Document.get().createDateInputElement());
    }

}
