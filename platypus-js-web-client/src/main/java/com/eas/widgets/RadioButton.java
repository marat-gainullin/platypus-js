package com.eas.widgets;

import com.google.gwt.dom.client.Document;

/**
 *
 * @author mgainullin
 */
public class RadioButton extends CheckBox {

    public RadioButton() {
        super(Document.get().createRadioInputElement(""), Document.get().createLabelElement());
        input.setClassName("radio-box");
        label.addClassName("radio-label");
    }
    
}
