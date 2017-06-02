package com.eas.widgets.boxes;

import com.google.gwt.dom.client.InputElement;

/**
 *
 * @author mgainullin
 */
public class FormattedField extends TextField {

    public FormattedField() {
        super();
    }

    // TODO: Add using of date-format.js
    // TODO: Add Intl HTML5 API for dates and numbers.
    // TODO: Don't forget about fine tuning of HTML5 API formatters
    @Override
    protected Object valueFromInput() {
        String text = element.<InputElement>cast().getValue();
        return text;
    }

    @Override
    protected void valueToInput(Object aValue) {
        String text = aValue != null ? aValue + "" : "";
        element.<InputElement>cast().setValue(text);
    }

}
