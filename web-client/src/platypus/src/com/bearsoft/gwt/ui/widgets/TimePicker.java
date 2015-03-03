package com.bearsoft.gwt.ui.widgets;

import com.google.gwt.core.client.JsDate;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Date;

public class TimePicker extends Composite {

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
	
	private int componentWidth = 28;
	private int separatorWidth = 4;
	private int componentHeight = 20;
	private boolean isShowing = false;
	
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
		
		FlowPanel upperBtnBlock = new FlowPanel();
		upperBtnBlock.add(createSeparator(true));
		setWidget(upperBtnBlock, btnUpHour, "time-picker-up");
		upperBtnBlock.add(createSeparator(true));
		setWidget(upperBtnBlock, btnUpMinute, "time-picker-up");
		upperBtnBlock.add(createSeparator(true));
		setWidget(upperBtnBlock, btnUpSecond, "time-picker-up");
		upperBtnBlock.add(createSeparator(true));
		
		widgetContainer.add(upperBtnBlock);
		
		FlowPanel textBlock = new FlowPanel();
		
		txtHour.getElement().getStyle().setWidth(componentWidth, Style.Unit.PCT);
		txtHour.getElement().getStyle().setProperty("boxSizing", "border-box");
		txtHour.setStyleName("time-picker-text");
		txtMinute.getElement().getStyle().setWidth(componentWidth, Style.Unit.PCT);
		txtMinute.getElement().getStyle().setProperty("boxSizing", "border-box");
		txtMinute.setStyleName("time-picker-text");
		txtSecond.getElement().getStyle().setWidth(componentWidth, Style.Unit.PCT);
		txtSecond.getElement().getStyle().setProperty("boxSizing", "border-box");
		txtSecond.setStyleName("time-picker-text");
		
		textBlock.add(createSeparator(true));
		textBlock.add(txtHour);
		textBlock.add(createSeparator(false));
		textBlock.add(txtMinute);
		textBlock.add(createSeparator(false));
		textBlock.add(txtSecond);
		textBlock.add(createSeparator(true));
		
		textBlock.getElement().getStyle().setDisplay(Style.Display.BLOCK );
		widgetContainer.add(textBlock);
		
		FlowPanel lowerBtnBlock = new FlowPanel();
		lowerBtnBlock.add(createSeparator(true));
		setWidget(lowerBtnBlock, btnDownHour, "time-picker-down");
		lowerBtnBlock.add(createSeparator(true));
		setWidget(lowerBtnBlock, btnDownMinute, "time-picker-down");
		lowerBtnBlock.add(createSeparator(true));
		setWidget(lowerBtnBlock, btnDownSecond, "time-picker-down");
		lowerBtnBlock.add(createSeparator(true));
		
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
			}
		}, ClickEvent.getType());
		
		btnUpMinute.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				minute = updateUpValue(txtMinute,minute,maxMinuteVal);
			}
		}, ClickEvent.getType());

		btnUpSecond.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				second = updateUpValue(txtSecond,second,maxSecondVal);
			}
		}, ClickEvent.getType());
		
		
		btnDownHour.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hour = updateDownValue(txtHour,hour,maxHourVal);
			}
		}, ClickEvent.getType());
		
		btnDownMinute.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				minute = updateDownValue(txtMinute,minute,maxMinuteVal);
			}
		}, ClickEvent.getType());

		btnDownSecond.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				second = updateDownValue(txtSecond,second,maxSecondVal);
			}
		}, ClickEvent.getType());
		
		txtHour.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				hour = updateValue(txtHour, hour, maxHourVal);
			}
		});
		txtMinute.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				minute = updateValue(txtMinute, minute, maxMinuteVal);
			}
		});
		txtSecond.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				second = updateValue(txtSecond, second, maxSecondVal);
			}
		});
		
		
	}
	
	private void setHeight(int aHeight, Unit aUnit){
		timePickerContainer.getElement().getStyle().setHeight(aHeight, aUnit);
		
	}
	
	public void show(){
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
	
	public void setValue(Date aDate){
		JsDate date = JsDate.create(aDate.getTime());
		txtHour.setValue(date.getHours());
		txtMinute.setValue(date.getMinutes());
		txtSecond.setValue(date.getSeconds());
	}
	
	public JsDate getValue(){
		JsDate date = JsDate.create();
		date.setHours(txtHour.getValue(), txtMinute.getValue(), txtSecond.getValue());
		return date;
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
	
	private Widget createSeparator(boolean invisible){
		SimplePanel separator = new SimplePanel();
		separator.getElement().getStyle().setProperty("boxSizing", "border-box");
		separator.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		separator.getElement().getStyle().setWidth(separatorWidth, Style.Unit.PCT);
		separator.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		separator.getElement().setInnerText(":");
		if(invisible){
			separator.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		}
		return separator;
	}
	
	
}
