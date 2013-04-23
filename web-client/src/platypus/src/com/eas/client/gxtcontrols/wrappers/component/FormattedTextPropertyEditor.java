package com.eas.client.gxtcontrols.wrappers.component;

import java.text.ParseException;

import com.sencha.gxt.widget.core.client.form.PropertyEditor;

public class FormattedTextPropertyEditor extends PropertyEditor<Object> {
	protected ObjectFormat format;

	public FormattedTextPropertyEditor(ObjectFormat aFormat) {
		super();
		format = aFormat;
	}

	public ObjectFormat getFormat() {
		return format;
	}

	public void setFormat(ObjectFormat aFormat) {
		format = aFormat;
	}

	@Override
	public String render(Object aValue) {
		try {
	        return format != null ? format.format(aValue) : aValue != null ? aValue.toString() : null;
        } catch (ParseException e) {
        	return null;
        }
	}

	@Override
	public Object parse(CharSequence text) throws ParseException {
		if (format != null) {
			return text != null ? format.parse(text.toString()) : null;
		} else {
			return text != null ? text.toString() : null;
		}
	}

}
