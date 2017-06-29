package com.eas.widgets;

import com.google.gwt.dom.client.Document;

/**
 *
 * @author mgainullin
 */
public class EMailField extends TextField {

    public EMailField() {
        super(Document.get().createEMailInputElement());
    }

}
