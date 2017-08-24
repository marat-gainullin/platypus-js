package com.eas.widgets;

import com.google.gwt.dom.client.Document;

/**
 *
 * @author mgainullin
 */
public class CreditCardField extends TextField {

    // TODO: switch of auto completion due to security considerations
    public CreditCardField() {
        super(Document.get().createCreditCardInputElement());
    }

}
