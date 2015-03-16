package com.bearsoft.gwt.ui.widgets;

import java.util.Date;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.datepicker.client.DateBox;

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
		return super.getValue();
	}

	@Override
	public void setValue(Date date, boolean fireEvents) {
		super.setValue(date, fireEvents);
	}

	@Override
	public void showDatePicker() {
	}
	

}
