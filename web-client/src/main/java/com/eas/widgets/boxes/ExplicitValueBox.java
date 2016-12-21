package com.eas.widgets.boxes;

import java.text.ParseException;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.text.shared.Parser;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.ValueBox;

/**
 * 
 * @author mg ValueBoxBase<T> in native GWT has a drawback: It assumes, that
 *         value has to store in html element's text! This class stored the
 *         value as explicit value property and uses renderer and parser in
 *         standard way. It leads to single value and multiple text
 *         representations with ability to change format dynamically without of
 *         value change.
 * @param <T>
 */
public class ExplicitValueBox<T> extends ValueBox<T> {

	private T value;
	protected Renderer<T> renderer;
	protected Parser<T> parser;
	private boolean settingValue;

	protected ExplicitValueBox(Element aElement, Renderer<T> aRenderer, Parser<T> aParser) {
		super(aElement, aRenderer, aParser);
		renderer = aRenderer;
		parser = aParser;
		addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				if (!settingValue) {
					String text = ExplicitValueBox.super.getText();
					setText(text);
				}
			}
		});
	}

	/**
	 * Return the parsed value, or null if the field is empty.
	 * 
	 * @throws ParseException
	 *             if the value cannot be parsed
	 */
	@Override
	public T getValueOrThrow() throws ParseException {
		String text = getText();
		if ("".equals(text)) {
			return null;
		} else {
			return parser.parse(text);
		}
	}

	@Override
	public T getValue() {
		return value;
	}

	public void setValue(T newValue, boolean fireEvents) {
		T oldValue = value;
		if (oldValue != newValue && (oldValue == null || !oldValue.equals(newValue))) {
			value = newValue;
			settingValue = true;
			try {
				super.setText(renderer.render(newValue));
			} finally {
				settingValue = false;
			}
			if (fireEvents) {
				ValueChangeEvent.fire(this, newValue);
			}
		}
	}

	@Override
	public String getText() {
		return super.getText();//renderer.render(value);
	}

	@Override
	public void setText(String text) {
		try {
			T newValue;
			if ("".equals(text)) {
				newValue = null;
			} else
				newValue = parser.parse(text);
			setValue(newValue, true);
		} catch (ParseException e) {
			super.setText(text);
		}
	}

	protected void resetText() {
		settingValue = true;
		try {
			super.setText(renderer.render(getValue()));
		} finally {
			settingValue = false;
		}
	}

	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<T> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

}
