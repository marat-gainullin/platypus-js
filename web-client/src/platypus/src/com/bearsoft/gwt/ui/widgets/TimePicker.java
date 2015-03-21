package com.bearsoft.gwt.ui.widgets;

import java.text.ParseException;
import java.util.Date;

import com.eas.client.form.published.widgets.PlatypusHtmlEditorConstants;
import com.google.gwt.core.client.JsDate;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.TimeZone;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DatePicker;

public class TimePicker extends Composite implements  HasValue<Date>, HasValueChangeHandlers<Date> {

	protected FlowPanel timePickerContainer = new FlowPanel();
	protected FlowPanel widgetContainer = new FlowPanel();
	protected FlowPanel verticalAlign = new FlowPanel();
	protected SimplePanel btnUpHour = new SimplePanel();
	protected SimplePanel btnDownHour = new SimplePanel();
	protected IntegerBox txtHour = new IntegerBox(); 
	protected SimplePanel btnUpMinute = new SimplePanel();
	protected SimplePanel btnDownMinute = new SimplePanel();
	protected IntegerBox txtMinute = new IntegerBox();
	protected SimplePanel btnUpSecond = new SimplePanel();
	protected SimplePanel btnDownSecond = new SimplePanel();
	protected IntegerBox txtSecond = new IntegerBox();
	
	private int hour;
	private int minute;
	private int second;
	private int maxHourVal = 23;
	private int maxMinuteVal = 59;
	private int maxSecondVal = maxMinuteVal;
	private int componentWidth = 25;
	private int separatorWidth = 10;
	private int separatorSmallWidth = 2;
	private int componentHeight = 20;
	private boolean isShowing = false;
	FlowPanel dateBlock;
	private Date currentDate; //для хранения и отображения текущей даты
	
	protected static TimePickerConstants constants = GWT.create(TimePickerConstants.class);
	
	public TimePicker(){
		super();
		timePickerContainer.getElement().getStyle().setPosition(Position.ABSOLUTE);
		timePickerContainer.getElement().getStyle().setHeight(0, Style.Unit.PX);
		timePickerContainer.getElement().getStyle().setProperty("width", "auto");
		timePickerContainer.getElement().getStyle().setBottom(0, Style.Unit.PCT);
		timePickerContainer.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		setStyleName(timePickerContainer.getElement(), "time-picker");
		
		verticalAlign.getElement().getStyle().setHeight(100, Style.Unit.PCT);
		verticalAlign.getElement().getStyle().setWidth(0, Style.Unit.PCT);
		
		widgetContainer.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		verticalAlign.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		dateBlock = new FlowPanel();
		dateBlock.getElement().setInnerText(constants.date());
		dateBlock.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		dateBlock.getElement().getStyle().setMarginBottom(10, Style.Unit.PCT);
		showDate();
		
		widgetContainer.add(dateBlock);
		
		FlowPanel upperBtnBlock = new FlowPanel();
		upperBtnBlock.add(createSeparator(true,separatorSmallWidth));
		setWidget(upperBtnBlock, btnUpHour, "time-picker-up");
		upperBtnBlock.add(createSeparator(true,separatorWidth));
		setWidget(upperBtnBlock, btnUpMinute, "time-picker-up");
		upperBtnBlock.add(createSeparator(true,separatorWidth));
		setWidget(upperBtnBlock, btnUpSecond, "time-picker-up");
		upperBtnBlock.add(createSeparator(true,separatorSmallWidth));
		
		widgetContainer.add(upperBtnBlock);
		
		FlowPanel textBlock = new FlowPanel();
		
		txtHour.getElement().getStyle().setWidth(componentWidth, Style.Unit.PCT);
//		txtHour.getElement().getStyle().setProperty("boxSizing", "border-box");
		txtHour.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		txtHour.setStyleName("time-picker-text");
		txtMinute.getElement().getStyle().setWidth(componentWidth, Style.Unit.PCT);
//		txtMinute.getElement().getStyle().setProperty("boxSizing", "border-box");
		txtMinute.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		txtMinute.setStyleName("time-picker-text");
		txtSecond.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		txtSecond.getElement().getStyle().setWidth(componentWidth, Style.Unit.PCT);
//		txtSecond.getElement().getStyle().setProperty("boxSizing", "border-box");
		
		txtSecond.setStyleName("time-picker-text");
		
		textBlock.add(createSeparator(true,separatorSmallWidth));
		textBlock.add(txtHour);
		textBlock.add(createSeparator(false, separatorWidth));
		textBlock.add(txtMinute);
		textBlock.add(createSeparator(false, separatorWidth));
		textBlock.add(txtSecond);
		textBlock.add(createSeparator(true,separatorSmallWidth));
		
		textBlock.getElement().getStyle().setDisplay(Style.Display.BLOCK );
		widgetContainer.add(textBlock);
		
		FlowPanel lowerBtnBlock = new FlowPanel();
		lowerBtnBlock.add(createSeparator(true,separatorSmallWidth));
		setWidget(lowerBtnBlock, btnDownHour, "time-picker-down");
		lowerBtnBlock.add(createSeparator(true,separatorWidth));
		setWidget(lowerBtnBlock, btnDownMinute, "time-picker-down");
		lowerBtnBlock.add(createSeparator(true,separatorWidth));
		setWidget(lowerBtnBlock, btnDownSecond, "time-picker-down");
		lowerBtnBlock.add(createSeparator(true,separatorSmallWidth));
		
		widgetContainer.add(lowerBtnBlock);
		widgetContainer.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
		verticalAlign.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
		
		FlowPanel hCenter = new FlowPanel();
		
		hCenter.add(widgetContainer);
		hCenter.add(verticalAlign);
		hCenter.getElement().getStyle().setHeight(100, Style.Unit.PCT);
		hCenter.getElement().getStyle().setDisplay(Display.BLOCK);
		hCenter.getElement().getStyle().setProperty("marginLeft", "auto");
		hCenter.getElement().getStyle().setProperty("marginRight", "auto");
		
		timePickerContainer.add(hCenter);
		initWidget(timePickerContainer);

		btnUpHour.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hour = updateUpValue(txtHour,hour,maxHourVal);
				ValueChangeEvent.fire(TimePicker.this, getValue());
			}
		}, ClickEvent.getType());
		
		btnUpMinute.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				minute = updateUpValue(txtMinute,minute,maxMinuteVal);
				ValueChangeEvent.fire(TimePicker.this, getValue());
			}
		}, ClickEvent.getType());

		btnUpSecond.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				second = updateUpValue(txtSecond,second,maxSecondVal);
				ValueChangeEvent.fire(TimePicker.this, getValue());
			}
		}, ClickEvent.getType());
		
		btnDownHour.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hour = updateDownValue(txtHour,hour,maxHourVal);
				ValueChangeEvent.fire(TimePicker.this, getValue());
			}
		}, ClickEvent.getType());
		
		btnDownMinute.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				minute = updateDownValue(txtMinute,minute,maxMinuteVal);
				ValueChangeEvent.fire(TimePicker.this, getValue());
			}
		}, ClickEvent.getType());

		btnDownSecond.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				second = updateDownValue(txtSecond,second,maxSecondVal);
				ValueChangeEvent.fire(TimePicker.this, getValue());
			}
		}, ClickEvent.getType());
		
		txtHour.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				hour = updateValue(txtHour, hour, maxHourVal);
				ValueChangeEvent.fire(TimePicker.this, getValue());
			}
		});
		txtMinute.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				minute = updateValue(txtMinute, minute, maxMinuteVal);
				ValueChangeEvent.fire(TimePicker.this, getValue());
			}
		});
		txtSecond.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				second = updateValue(txtSecond, second, maxSecondVal);
				ValueChangeEvent.fire(TimePicker.this, getValue());
			}
		});
		
		txtHour.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.isUpArrow()){
					hour = updateUpValue(txtHour,hour,maxHourVal);
					ValueChangeEvent.fire(TimePicker.this, getValue());
					return;
				}
				if (event.isDownArrow()){
					hour = updateDownValue(txtHour,hour,maxHourVal);
					ValueChangeEvent.fire(TimePicker.this, getValue());
					return;
				}
			}
		});
		
		txtMinute.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.isUpArrow()){
					minute = updateUpValue(txtMinute,minute,maxMinuteVal);
					ValueChangeEvent.fire(TimePicker.this, getValue());
					return;
				}
				if (event.isDownArrow()){
					minute = updateDownValue(txtMinute,minute,maxMinuteVal);
					ValueChangeEvent.fire(TimePicker.this, getValue());
					return;
				}
			}
		});
		
		txtSecond.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.isUpArrow()){
					second = updateUpValue(txtSecond,second,maxSecondVal);
					ValueChangeEvent.fire(TimePicker.this, getValue());
					return;
				}
				if (event.isDownArrow()){
					second = updateDownValue(txtSecond,second,maxSecondVal);
					ValueChangeEvent.fire(TimePicker.this, getValue());
					return;
				}
			}
		});
		
	}
	
	public void setAbsolute(){
		timePickerContainer.getElement().getStyle().setPosition(Position.ABSOLUTE);
		timePickerContainer.getElement().getStyle().setHeight(0, Style.Unit.PX);
		timePickerContainer.getElement().getStyle().setProperty("width", "auto");
		timePickerContainer.getElement().getStyle().setBottom(0, Style.Unit.PCT);
		timePickerContainer.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		setStyleName(timePickerContainer.getElement(), "time-picker");
		
		verticalAlign.getElement().getStyle().setHeight(100, Style.Unit.PCT);
		verticalAlign.getElement().getStyle().setWidth(0, Style.Unit.PCT);
		
		widgetContainer.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		verticalAlign.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		dateBlock = new FlowPanel();
		dateBlock.getElement().setInnerText(constants.date());
		dateBlock.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		dateBlock.getElement().getStyle().setMarginBottom(10, Style.Unit.PCT);
		showDate();
		
		widgetContainer.add(dateBlock);
		
		FlowPanel upperBtnBlock = new FlowPanel();
		upperBtnBlock.add(createSeparator(true,separatorSmallWidth));
		setWidget(upperBtnBlock, btnUpHour, "time-picker-up");
		upperBtnBlock.add(createSeparator(true,separatorWidth));
		setWidget(upperBtnBlock, btnUpMinute, "time-picker-up");
		upperBtnBlock.add(createSeparator(true,separatorWidth));
		setWidget(upperBtnBlock, btnUpSecond, "time-picker-up");
		upperBtnBlock.add(createSeparator(true,separatorSmallWidth));
		
		widgetContainer.add(upperBtnBlock);
		
		FlowPanel textBlock = new FlowPanel();
		
		txtHour.getElement().getStyle().setWidth(componentWidth, Style.Unit.PCT);
//		txtHour.getElement().getStyle().setProperty("boxSizing", "border-box");
		txtHour.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		txtHour.setStyleName("time-picker-text");
		txtMinute.getElement().getStyle().setWidth(componentWidth, Style.Unit.PCT);
//		txtMinute.getElement().getStyle().setProperty("boxSizing", "border-box");
		txtMinute.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		txtMinute.setStyleName("time-picker-text");
		txtSecond.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		txtSecond.getElement().getStyle().setWidth(componentWidth, Style.Unit.PCT);
//		txtSecond.getElement().getStyle().setProperty("boxSizing", "border-box");
		
		txtSecond.setStyleName("time-picker-text");
		
		textBlock.add(createSeparator(true,separatorSmallWidth));
		textBlock.add(txtHour);
		textBlock.add(createSeparator(false, separatorWidth));
		textBlock.add(txtMinute);
		textBlock.add(createSeparator(false, separatorWidth));
		textBlock.add(txtSecond);
		textBlock.add(createSeparator(true,separatorSmallWidth));
		
		textBlock.getElement().getStyle().setDisplay(Style.Display.BLOCK );
		widgetContainer.add(textBlock);
		
		FlowPanel lowerBtnBlock = new FlowPanel();
		lowerBtnBlock.add(createSeparator(true,separatorSmallWidth));
		setWidget(lowerBtnBlock, btnDownHour, "time-picker-down");
		lowerBtnBlock.add(createSeparator(true,separatorWidth));
		setWidget(lowerBtnBlock, btnDownMinute, "time-picker-down");
		lowerBtnBlock.add(createSeparator(true,separatorWidth));
		setWidget(lowerBtnBlock, btnDownSecond, "time-picker-down");
		lowerBtnBlock.add(createSeparator(true,separatorSmallWidth));
		
		widgetContainer.add(lowerBtnBlock);
		widgetContainer.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
		verticalAlign.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
		
		FlowPanel hCenter = new FlowPanel();
		
		hCenter.add(widgetContainer);
		hCenter.add(verticalAlign);
		hCenter.getElement().getStyle().setHeight(100, Style.Unit.PCT);
		hCenter.getElement().getStyle().setDisplay(Display.BLOCK);
		hCenter.getElement().getStyle().setProperty("marginLeft", "auto");
		hCenter.getElement().getStyle().setProperty("marginRight", "auto");
		
		timePickerContainer.add(hCenter);
		initWidget(timePickerContainer);
	}
	
	public void setRelative(){
		
	}
	
	private void setHeight(int aHeight, Unit aUnit){
		timePickerContainer.getElement().getStyle().setHeight(aHeight, aUnit);
	}

	private void showDate(){
		DateTimeFormat fmt = DateTimeFormat.getFormat("dd:MM:yyyy");
		if (currentDate == null){
			dateBlock.getElement().setInnerText(constants.date());
		}else{
			dateBlock.getElement().setInnerText(constants.date() + fmt.format(currentDate));
		}
		txtHour.setValue(hour);
		txtMinute.setValue(minute);
		txtSecond.setValue(second);
	}
	
	public void show(){
		showDate();
		setHeight(100,Style.Unit.PCT);
		isShowing = true;
	}
	
	public void hide(){
		setHeight(0,Style.Unit.PCT);
		isShowing = false;
	}
	
	public boolean isShowing(){
		return isShowing;
	}
	
	public Date getValue(){
		if (this.currentDate==null){
			return null;
		}else{
			return new Date(hour*60*60*1000 + minute*60*1000 + second*1000);
		}
	}

	private int updateValue(IntegerBox aInput, int aVal, int maxVal){
		if (aInput.getValue() != null) {
			
		int val = aInput.getValue();
		if (val>maxVal){
			val = maxVal;
			aInput.setValue(val);
		}
		if (val<0){
			val = 0;
			aInput.setValue(val);
		}
		aVal = val;
		}
		return aVal;
	}
	
	private int updateUpValue(IntegerBox aInput, int aVal, int maxVal){
		aVal+=1;
		if (aVal>maxVal){
			aVal=0;
		}
		aInput.setValue(aVal);
		return aVal;
	}
	
	private int updateDownValue(IntegerBox aInput, int aVal, int maxVal){
		aVal-=1;
		if (aVal<0){
			aVal=maxVal;
		}
		aInput.setValue(aVal);
		return aVal;
	}
	
	private void setWidget(Panel aPanel, Widget aWidget,String divStyleName){
		aWidget.getElement().getStyle().setWidth(componentWidth, Style.Unit.PCT);
		aWidget.getElement().getStyle().setHeight(componentHeight, Style.Unit.PX);
		aWidget.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
//		addDivToElement(aWidget.getElement(),divStyleName);
		aWidget.getElement().addClassName(divStyleName);
		aPanel.add(aWidget);
	}
	
	private void addDivToElement(Element aBase, String aName){
		if (aName.length()>0){
			Element imageDiv = Document.get().createDivElement();
			imageDiv.addClassName(aName);
			aBase.appendChild(imageDiv);
		}
	}
	
	private Widget createSeparator(boolean invisible,int width){
		SimplePanel separator = new SimplePanel();
//		separator.getElement().getStyle().setProperty("boxSizing", "border-box");
		separator.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		separator.getElement().getStyle().setWidth(width, Style.Unit.PCT);
		separator.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		separator.getElement().setInnerText(":");
		separator.setStyleName("time-picker-separator");
		if(invisible){
			separator.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		}
		return separator;
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<Date> handler) {
		 return addHandler(handler, ValueChangeEvent.getType());
	}
	
	public void setValue(Date aDate){
		setValue(aDate,false);
	}
	
	@Override
	public void setValue(Date value, boolean fireEvents) {
		this.currentDate = value;
		if (value ==null){
			hour = 0;
			minute = 0;
			second = 0;
			return;
		}else{
			hour = value.getHours();
			minute = value.getMinutes();
			second = value.getSeconds();
		}
			txtHour.setValue(hour);
			txtMinute.setValue(minute);
			txtSecond.setValue(second);
			if (fireEvents){
				ValueChangeEvent.fire(TimePicker.this, getValue());
			}
	}

}
