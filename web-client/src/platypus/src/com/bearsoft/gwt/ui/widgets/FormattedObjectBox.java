package com.bearsoft.gwt.ui.widgets;

import java.text.ParseException;

import com.google.gwt.dom.client.Document;
import com.google.gwt.text.shared.AbstractRenderer;
import com.google.gwt.text.shared.Parser;

public class FormattedObjectBox extends ExplicitValueBox<Object> {

	public static class PolymorphRenderer extends AbstractRenderer<Object> {

		protected ObjectFormat format;
		
		public PolymorphRenderer(ObjectFormat aFormat) {
			super();
			format = aFormat;
		}

		@Override
		public String render(Object value) {
			try {
				return value != null && format != null ? format.format(value) : "";
			} catch (ParseException e) {
				// Logger.getLogger(FormattedObjectBox.class.getName()).log(Level.SEVERE,
				// e.getMessage());
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
			if (format == null || text == null || "".equals(text.toString())) {
				return null;
			} else {
				return format.parse(text.toString());
			}
		}

	}

	private ObjectFormat format;
	
	public FormattedObjectBox() {
		this(new ObjectFormat());
	}	
	
	public FormattedObjectBox(ObjectFormat aFormat) {
		super(Document.get().createTextInputElement(), new PolymorphRenderer(aFormat), new PolymorphParser(aFormat));
		format = aFormat;
	}

	public void setValueType(int aType) throws ParseException {
		format.setValueType(aType);
	}

	@Override
	public void setValue(Object aValue, boolean fireEvents) {
		if (aValue != null && format.isEmpty()) {
			try {
				format.setFormatTypeByValue(aValue);
			} catch (ParseException e) {
				return;
			}
		}
		super.setValue(aValue, fireEvents);
	}

	public String getFormat() {
		return format != null ? format.getPattern() : null;
	}

	public void setFormat(String aValue) throws ParseException {
		if (format != null ? !format.equals(aValue) : aValue != null) {
			format.setPattern(aValue);
			resetText();
		}
	}
}
