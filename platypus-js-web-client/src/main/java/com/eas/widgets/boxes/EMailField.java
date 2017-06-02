package com.eas.widgets.boxes;

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
