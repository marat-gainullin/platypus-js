package com.bearsoft.gwt.ui.widgets;

import java.util.Date;

import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DatePicker;

public class DecoratedDateBox extends DateBox {

//	protected DatePicker picker;
	protected DateTimePicker picker;
	public DecoratedDateBox() {
		super();
	}

	public DecoratedDateBox(DateTimePicker aPicker, Date date, Format format) {
		super(aPicker, date, format);
		picker = aPicker;
	}

	@Override
	public Date getValue() {
		Date date = picker.getElementValue();
		this.getTextBox().setText(this.getFormat().format(this, date));
		return date;
		
	}

	@Override
	public void setValue(Date date, boolean fireEvents) {
		super.setValue(date, fireEvents);
	}

	@Override
	public void showDatePicker() {
	}
	

}
