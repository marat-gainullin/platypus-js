package com.bearsoft.gwt.ui.widgets;


import java.util.Date;

import com.google.gwt.core.client.JsDate;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;

public class DateTimePicker extends DatePicker {
	
	TimePicker tmPicker;
	public DateTimePicker() {
		super();
	}

	@Override
	protected void setup() {
		FlowPanel widgetContainer = new FlowPanel();
		widgetContainer.setStyleName("date-time-picker");
		
		FlowPanel calendarContainer = new FlowPanel();
		calendarContainer.getElement().getStyle().setPosition(Position.RELATIVE);
		initWidget(widgetContainer);
		
	    VerticalPanel panel = new VerticalPanel();
	    setStyleName(panel.getElement(), "gwt-DatePicker");
	    panel.getElement().getStyle().setBorderWidth(0, Style.Unit.PX);
	    panel.add(this.getMonthSelector());
		panel.add(this.getView());
		tmPicker = new TimePicker(this);

		calendarContainer.add(panel);
		calendarContainer.add(tmPicker);
		
	    widgetContainer.add(calendarContainer);
	    
		FlowPanel timePickerCaller = new FlowPanel();
		timePickerCaller.getElement().getStyle().setWidth(100, Style.Unit.PCT);
		timePickerCaller.getElement().getStyle().setHeight(20, Style.Unit.PX);
		timePickerCaller.setStyleName("time-picker-button");
		
		widgetContainer.add(timePickerCaller);
		
		timePickerCaller.addDomHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				tmPicker.setValue(DateTimePicker.this.getValue());
				if(tmPicker.isShowing()){
					tmPicker.hide();
				}else{
					tmPicker.show();
				}
			}
		},ClickEvent.getType());
	}
	
	
	public Date getElementValue(Date calendarDate) {
		Date day = calendarDate;
		Date combine = new Date(day.getTime());
			Date dayTime = tmPicker.getValue();
			if (dayTime!=null){
				combine.setHours(dayTime.getHours());
				combine.setMinutes(dayTime.getMinutes());
				combine.setSeconds(dayTime.getSeconds());
			}
		return new Date(combine.getTime());
	}
	
}
