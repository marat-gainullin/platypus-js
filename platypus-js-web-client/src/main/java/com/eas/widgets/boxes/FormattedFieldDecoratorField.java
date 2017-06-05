package com.eas.widgets.boxes;

import java.text.ParseException;

import com.google.gwt.core.client.JavaScriptObject;

public class FormattedFieldDecoratorField extends ValueDecoratorField {

    public FormattedFieldDecoratorField() {
        super(new FormattedField());
    }

    public String getFormat() {
        return ((FormattedField) decorated).getPattern();
    }

    public void setFormat(String aValue) throws ParseException {
        ((FormattedField) decorated).setPattern(aValue);
    }

    public int getValueType() {
        return ((FormattedField) decorated).getValueType();
    }

    public void setValueType(int aValue) throws ParseException {
        ((FormattedField) decorated).setValueType(aValue);
    }

    public JavaScriptObject getOnParse() {
        return ((FormattedField) decorated).getOnParse();
    }

    public void setOnParse(JavaScriptObject aValue) {
        ((FormattedField) decorated).setOnParse(aValue);
    }

    public JavaScriptObject getOnFormat() {
        return ((FormattedField) decorated).getOnFormat();
    }

    public void setOnFormat(JavaScriptObject aValue) {
        ((FormattedField) decorated).setOnFormat(aValue);
    }

    @Override
    protected void publish(JavaScriptObject aValue) {
        publish(this, aValue);
    }

    private native static void publish(FormattedFieldDecoratorField aWidget, JavaScriptObject aPublished);
}
