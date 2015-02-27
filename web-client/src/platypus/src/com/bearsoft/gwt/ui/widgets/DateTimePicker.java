package com.bearsoft.gwt.ui.widgets;


import java.util.Date;

import com.google.gwt.core.client.JsDate;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
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
		FlowPanel calendarContainer = new FlowPanel();
		calendarContainer.getElement().getStyle().setPosition(Position.RELATIVE);
		initWidget(widgetContainer);
		
	    VerticalPanel panel = new VerticalPanel();
	    setStyleName(panel.getElement(), "gwt-DatePicker");
	    setStyleName("gwt-DatePicker");
	    panel.add(this.getMonthSelector());
		panel.add(this.getView());
		tmPicker = new TimePicker();

		calendarContainer.add(panel);
		calendarContainer.add(tmPicker);
		
	    widgetContainer.add(calendarContainer);
		
		FlowPanel timePickerCaller = new FlowPanel();
		timePickerCaller.getElement().getStyle().setWidth(100, Style.Unit.PCT);
		widgetContainer.add(timePickerCaller);
		
		timePickerCaller.addDomHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
//				int count = container.getWidgetCount();
//				Widget first = container.getWidget(0);
//				int height = first.getOffsetHeight();
//				int width = first.getOffsetWidth();
//				Widget second = container.getWidget(1);
//				height += second.getOffsetHeight();
				if(tmPicker.isShowing()){
					tmPicker.hide();	
				}else{
					tmPicker.show();
				}
			}
		},ClickEvent.getType());
//		container.add(new TimePicker());
	}
	
	
	public Date getElementValue() {
		JsDate time = tmPicker.getValue();
		Date day = this.getValue();
		JsDate combine = JsDate.create(day.getTime());
		combine.setHours(time.getHours());
		return new Date();
	}
}
