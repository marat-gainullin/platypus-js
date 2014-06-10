package com.bearsoft.gwt.ui.widgets;

import java.util.Date;

import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DatePicker;

public class DecoratedDateBox extends DateBox {

	protected DatePicker picker;

	public DecoratedDateBox() {
		super();
	}

	public DecoratedDateBox(DatePicker aPicker, Date date, Format format) {
		super(aPicker, date, format);
		picker = aPicker;
	}

	@Override
	public Date getValue() {
		return picker.getValue();
	}

	@Override
	public void showDatePicker() {
		// no op here because of maniac keyboard processing by default
	}

}
