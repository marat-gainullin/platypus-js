package com.eas.widgets.boxes;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DatePicker;

public class DateTimePicker extends DatePicker {

	TimePicker tmPicker;
	FlowPanel timePickerCaller;
	FlowPanel widgetContainer;
	FlowPanel calendarContainer;
	VerticalPanel panel;
	Element triangle;

	public DateTimePicker() {
		super();
	}

	@Override
	protected void setup() {
		widgetContainer = new FlowPanel();
		widgetContainer.setStyleName("date-time-picker");
		triangle = Document.get().createDivElement();
		triangle.addClassName("date-time-triangle");
		widgetContainer.getElement().appendChild(triangle);
		calendarContainer = new FlowPanel();
		calendarContainer.getElement().getStyle().setPosition(Position.RELATIVE);

		initWidget(widgetContainer);
		panel = new VerticalPanel();
		setStyleName(panel.getElement(), "gwt-DatePicker");

		panel.getElement().getStyle().setBorderWidth(0, Style.Unit.PX);
		panel.add(getMonthSelector());
		panel.add(getView());
		tmPicker = new TimePicker();

		timePickerCaller = new FlowPanel();
		timePickerCaller.getElement().getStyle().setWidth(100, Style.Unit.PCT);
		timePickerCaller.getElement().getStyle().setHeight(20, Style.Unit.PX);
		timePickerCaller.setStyleName("time-picker-button");

		calendarContainer.add(panel);
		calendarContainer.add(tmPicker);
		widgetContainer.add(calendarContainer);
		widgetContainer.add(timePickerCaller);

		setDateAndTimeView();

		timePickerCaller.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (tmPicker.isShowing()) {
					timePickerCaller.setStyleName("time-picker-button");
					tmPicker.hide();
				} else {
					timePickerCaller.setStyleName("date-picker-button");
					tmPicker.show();
				}
			}
		}, ClickEvent.getType());
	}

	public void setDateAndTimeView() {
		tmPicker.setAbsolute();
		panel.getElement().getStyle().setDisplay(Display.BLOCK);
		timePickerCaller.getElement().getStyle().setDisplay(Display.BLOCK);
		tmPicker.getElement().getStyle().setDisplay(Display.BLOCK);
	}

	public void setDateView() {
		panel.getElement().getStyle().setDisplay(Display.BLOCK);
		timePickerCaller.getElement().getStyle().setDisplay(Display.NONE);
		tmPicker.getElement().getStyle().setDisplay(Display.NONE);
	}

	public void setTimeView() {
		tmPicker.setRelative();
		tmPicker.getElement().getStyle().setDisplay(Display.BLOCK);
		panel.getElement().getStyle().setDisplay(Display.NONE);
		timePickerCaller.getElement().getStyle().setDisplay(Display.NONE);
	}

	public TimePicker getTimePicker() {
		return tmPicker;
	}

	public void shown(boolean onTheRight) {
		if(onTheRight){
			triangle.removeClassName("date-time-triangle-left");
		}else{
			triangle.addClassName("date-time-triangle-left");
		}
    }

}
