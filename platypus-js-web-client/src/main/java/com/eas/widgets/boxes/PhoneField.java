package com.eas.widgets.boxes;

import com.google.gwt.dom.client.Document;

/**
 *
 * @author mgainullin
 */
public class PhoneField extends TextField {

    public PhoneField() {
        super(Document.get().createPhoneInputElement());
    }

}
