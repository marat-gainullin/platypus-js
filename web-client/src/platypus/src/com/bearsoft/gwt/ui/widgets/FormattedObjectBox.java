package com.bearsoft.gwt.ui.widgets;

import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.text.shared.AbstractRenderer;
import com.google.gwt.user.client.ui.ValueBox;
import com.google.gwt.text.shared.Parser;

public class FormattedObjectBox extends ValueBox<Object> {

	public static class NoopRenderer extends AbstractRenderer<Object> {

		@Override
		public String render(Object object) {
			return String.valueOf(object);
		}

	}

	public static class NoopParser implements Parser<Object> {

		@Override
		public Object parse(CharSequence text) throws ParseException {
			return text != null ? text.toString() : null;
		}

	}

	public static class PolymorphRenderer extends AbstractRenderer<Object> {

		protected ObjectFormat format;

		public PolymorphRenderer(ObjectFormat aFormat) {
			super();
			format = aFormat;
		}

		@Override
		public String render(Object value) {
			try {
				return format.format(value);
			} catch (ParseException e) {
				Logger.getLogger(FormattedObjectBox.class.getName()).log(Level.SEVERE, null, e);
				return null;
			}
		}

	}

	public static class PolymorphParser implements Parser<Object> {

		protected ObjectFormat format;

		public PolymorphParser(ObjectFormat aFormat) {
			super();
			format = aFormat;
		}

		@Override
		public Object parse(CharSequence text) throws ParseException {
			if ("".equals(text.toString())) {
				return null;
			} else {
				return format.parse(text.toString());
			}
		}

	}

	protected Object value;
	protected ObjectFormat format;
	protected PolymorphParser parser;
	protected PolymorphRenderer renderer;

	public FormattedObjectBox() {
		super(Document.get().createTextInputElement(), new NoopRenderer(), new NoopParser());
	}

	public void setFormatType(int aFormatType, String aPattern) throws ParseException {
		format = new ObjectFormat(aFormatType, aPattern);
		renderer = new PolymorphRenderer(format);
		parser = new PolymorphParser(format);
		setValue(getValue(), false);
	}

	/**
	 * Return the parsed value, or null if the field is empty.
	 * 
	 * @throws ParseException
	 *             if the value cannot be parsed
	 */
	@Override
	public Object getValueOrThrow() throws ParseException {
		String text = getText();
		if ("".equals(text)) {
			return null;
		} else
			return parser.parse(text);
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public void setValue(Object aValue, boolean fireEvents) {
		if (aValue != null && format == null) {
			try {
				format = new ObjectFormat(aValue);
				renderer = new PolymorphRenderer(format);
				parser = new PolymorphParser(format);
			} catch (ParseException e) {
				return;
			}
		}
		Object oldValue = fireEvents ? getValue() : null;
		setText(renderer.render(aValue));
		value = aValue;
		if (fireEvents) {
			Object newValue = getValue();
			ValueChangeEvent.fireIfNotEqual(this, oldValue, newValue);
		}
	}

	public String getPattern() {
		return format != null ? format.getPattern() : null;
	}

	public void setPattern(String aPattern) throws ParseException {
		if (format != null && (aPattern == null ? getPattern() != null : !aPattern.equals(getPattern()))) {
			format.setPattern(aPattern);
			setValue(getValue(), false);
		}
	}
}
