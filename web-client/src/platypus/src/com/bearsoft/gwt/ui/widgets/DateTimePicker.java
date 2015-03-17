package com.bearsoft.gwt.ui.widgets;


import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DatePicker;

public class DateTimePicker extends DatePicker {
	
	TimePicker tmPicker;
	FlowPanel timePickerCaller;
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
		tmPicker = new TimePicker();
		calendarContainer.add(panel);
		calendarContainer.add(tmPicker);
	    widgetContainer.add(calendarContainer);
	    timePickerCaller = new FlowPanel();
		timePickerCaller.getElement().getStyle().setWidth(100, Style.Unit.PCT);
		timePickerCaller.getElement().getStyle().setHeight(20, Style.Unit.PX);
		timePickerCaller.setStyleName("time-picker-button");
		widgetContainer.add(timePickerCaller);
		timePickerCaller.addDomHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(tmPicker.isShowing()){
					timePickerCaller.setStyleName("time-picker-button");
					tmPicker.hide();
				}else{
					timePickerCaller.setStyleName("date-picker-button");
					tmPicker.show();
				}
			}
		},ClickEvent.getType());
	}
	
	public TimePicker getTimePicker(){
		return tmPicker;
	}
	
}
