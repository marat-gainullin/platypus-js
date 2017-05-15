package com.eas.widgets.boxes;

import java.text.ParseException;

import com.eas.client.converters.StringValueConverter;
import com.eas.core.Utils;
import com.eas.core.Utils.JsObject;
import com.eas.ui.CommonResources;
import com.eas.ui.HasDecorationsWidth;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.text.shared.AbstractRenderer;
import com.google.gwt.text.shared.Parser;
import com.google.gwt.user.client.ui.HasName;

public class FormattedObjectBox extends ExplicitValueBox<Object> implements HasDecorationsWidth {

	public static class PolymorphRenderer extends AbstractRenderer<Object> {

		protected FormattedObjectBox box;
		
		public PolymorphRenderer() {
			super();
		}

		public FormattedObjectBox getBox() {
                        return box;
                }
		
		public void setBox(FormattedObjectBox aValue) {
                        box = aValue;
                }
		
		@Override
		public String render(Object value) {
			if (box.onFormat != null) {
				JsObject jsEvent = JsObject.createObject().cast();
				jsEvent.setJs("source", box.eventThis);
				jsEvent.setJava("value", value);
				Object jsRendered = box.onFormat.<JsObject> cast().call(box.eventThis, jsEvent);
				return new StringValueConverter().convert(Utils.toJava(jsRendered));
			} else {
				try {
					return value != null && box.format != null ? box.format.format(value) : "";
				} catch (ParseException e) {
					// Logger.getLogger(FormattedObjectBox.class.getName()).log(Level.SEVERE,
					// e.getMessage());
					return null;
				}
			}
		}

	}

	public static class PolymorphParser implements Parser<Object> {

		protected FormattedObjectBox box;
		
		public PolymorphParser() {
			super();
		}

		public FormattedObjectBox getBox() {
                        return box;
                }
		
		public void setBox(FormattedObjectBox aValue) {
                        box = aValue;
                }
		
		@Override
		public Object parse(CharSequence text) throws ParseException {
			if (box.onParse != null) {
				JsObject jsEvent = JsObject.createObject().cast();
				jsEvent.setJs("source", box.eventThis);
				jsEvent.setJava("text", text);
				return box.onParse.<JsObject> cast().call(box.eventThis, jsEvent);
			} else {
				if (box.format == null || text == null || "".equals(text.toString())) {
					return null;
				} else {
					return box.format.parse(text.toString());
				}
			}
		}

	}

	private ObjectFormat format;
	private JavaScriptObject onFormat;
	private JavaScriptObject onParse;
	private JavaScriptObject eventThis;

	public FormattedObjectBox() {
		super(Document.get().createTextInputElement(), new PolymorphRenderer(), new PolymorphParser());
		setStyleName("form-control");
                CommonResources.INSTANCE.commons().ensureInjected();
                getElement().addClassName(CommonResources.INSTANCE.commons().borderSized());
		format = new ObjectFormat();
		((PolymorphRenderer)renderer).setBox(this);
		((PolymorphParser)parser).setBox(this);
	}

	@Override
	public void setDecorationsWidth(int aDecorationsWidth) {
		getElement().getStyle().setPaddingRight(aDecorationsWidth, Style.Unit.PX);
	}
	
	public JavaScriptObject getEventThis() {
		return eventThis;
	}

	public void setEventThis(JavaScriptObject aValue) {
		eventThis = aValue;
	}

	public JavaScriptObject getOnParse() {
		return onParse;
	}

	public void setOnParse(JavaScriptObject aValue) {
		onParse = aValue;
	}

	public JavaScriptObject getOnFormat() {
		return onFormat;
	}

	public void setOnFormat(JavaScriptObject aValue) {
		onFormat = aValue;
	}

	public int getValueType() {
		return format.getValueType();
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
