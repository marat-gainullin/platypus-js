package com.eas.widgets.boxes;

import com.eas.ui.HasDecorationsWidth;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.TextArea;

public class NullableTextArea extends TextArea implements HasDecorationsWidth {

	protected TextAreaElement inputElem;
	protected String value;
	private boolean valueChangeHandlerInitialized;

	public NullableTextArea() {
	    super(Document.get().createTextAreaElement());
	    setStyleName("form-control");
	}

	public NullableTextArea(Element elem) {
		super(elem);
		inputElem = TextAreaElement.as(elem);
	}

	@Override
	public void setDecorationsWidth(int aDecorationsWidth) {
		getElement().getStyle().setPaddingRight(aDecorationsWidth, Style.Unit.PX);
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
