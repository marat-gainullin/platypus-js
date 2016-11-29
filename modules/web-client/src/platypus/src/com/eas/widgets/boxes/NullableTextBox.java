package com.eas.widgets.boxes;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.TextBox;

public class NullableTextBox extends TextBox {

	protected InputElement inputElem;
	protected String value;
	private boolean valueChangeHandlerInitialized;

	public NullableTextBox() {
		this(Document.get().createTextInputElement());
		setStyleName("gwt-TextBox");
	}

	public NullableTextBox(Element elem) {
		super(elem);
		inputElem = InputElement.as(elem);
	}

	@Override
	public void setValue(String aValue, boolean fireEvents) {
		String oldValue = getValue();
		value = aValue;
		if (value == null ? oldValue != null : !value.equals(oldValue)) {
			setText(aValue != null ? aValue : "");
			if (fireEvents) {
				ValueChangeEvent.fire(this, aValue);
			}
		}
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
		// Initialization code
		if (!valueChangeHandlerInitialized) {
			valueChangeHandlerInitialized = true;
			addChangeHandler(new ChangeHandler() {
				public void onChange(ChangeEvent event) {
					setValue(getText(), true);
				}
			});
		}
		return addHandler(handler, ValueChangeEvent.getType());
	}
}
