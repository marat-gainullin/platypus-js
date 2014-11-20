package com.bearsoft.gwt.ui.widgets;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DatePicker;

public class DateTimePicker extends DatePicker {
	public DateTimePicker() {
		super();
	}

	@Override
	protected void setup() {
		super.setup();
		VerticalPanel container = (VerticalPanel)getWidget();
	}
}
