package com.eas.client.form.published.widgets;

import com.bearsoft.gwt.ui.widgets.DateTimeBox;
import com.eas.client.form.ControlsUtils;
import com.eas.client.form.published.HasPublished;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.datepicker.client.DateBox;

public class PlatypusDateField extends DateTimeBox implements HasPublished {

    private static final DateBox.DefaultFormat DEFAULT_FORMAT = GWT.create(DateBox.DefaultFormat.class);
    
	protected JavaScriptObject published;
	protected String formatPattern;

	public PlatypusDateField() {
		super();
		formatPattern = DEFAULT_FORMAT.getDateTimeFormat().getPattern();
	}

	public PlatypusDateField(DateTimeFormat aFormat) {
		super(null, null, new DateBox.DefaultFormat(aFormat));
		formatPattern = aFormat.getPattern();
	}
	
	public String getFormatPattern() {
		return formatPattern;
	}

	public void setFormatPattern(String aValue) {
		formatPattern = aValue;
		if (formatPattern != null)
			formatPattern = ControlsUtils.convertDateFormatString(formatPattern);
		DateTimeFormat dtFormat = formatPattern != null ? DateTimeFormat.getFormat(formatPattern) : DateTimeFormat.getFormat("dd.MM.yyyy");
		setFormat(new DateBox.DefaultFormat(dtFormat));
	}
	
	public JavaScriptObject getPublished() {
		return published;
	}

	@Override
	public void setPublished(JavaScriptObject aValue) {
		if (published != aValue) {
			published = aValue;
			if (published != null) {
				publish(this, aValue);
			}
		}
	}

	private native static void publish(HasPublished aWidget, JavaScriptObject aPublished)/*-{
	}-*/;
}
