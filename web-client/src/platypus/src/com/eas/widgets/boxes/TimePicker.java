package com.eas.widgets.boxes;

import java.util.Date;

import com.google.gwt.core.shared.GWT;
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
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class TimePicker extends Composite implements HasValue<Date>, HasValueChangeHandlers<Date> {

	protected FlowPanel timePickerContainer = new FlowPanel();
	FlowPanel dateBlock = new FlowPanel();
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
	FlowPanel hCenter = new FlowPanel();

	SimplePanel separatorUp1 = new SimplePanel();
	SimplePanel separatorUp2 = new SimplePanel();
	SimplePanel separatorTime1 = new SimplePanel();
	SimplePanel separatorTime2 = new SimplePanel();
	SimplePanel separatorDown1 = new SimplePanel();
	SimplePanel separatorDown2 = new SimplePanel();

	private int hour;
	private int minute;
	private int second;
	private int maxHourVal = 23;
	private int maxMinuteVal = 59;
	private int maxSecondVal = maxMinuteVal;
	private int componentWidth = 25;
	private int componentWidthRelative = 25;
	private int separatorWidthRelative = 5;
	private int separatorWidth = 10;
	private int componentHeight = 20;
	private int relativeWidth = 90;
	private boolean showing;
	private int marginLeft = 2;
	private int marginRight = 2;
	private Date currentDate;

	protected static TimePickerConstants constants = GWT.create(TimePickerConstants.class);

	public TimePicker() {
		super();
		dateBlock.getElement().setInnerText(constants.date());
		dateBlock.getElement().setClassName("time-picker-date");
		widgetContainer.add(dateBlock);

		FlowPanel upperBtnBlock = new FlowPanel();
		upperBtnBlock.add(btnUpHour);
		upperBtnBlock.add(separatorUp1);
		upperBtnBlock.add(btnUpMinute);
		upperBtnBlock.add(separatorUp2);
		upperBtnBlock.add(btnUpSecond);

		widgetContainer.add(upperBtnBlock);

		FlowPanel textBlock = new FlowPanel();

		textBlock.add(txtHour);
		textBlock.add(separatorTime1);
		textBlock.add(txtMinute);
		textBlock.add(separatorTime2);
		textBlock.add(txtSecond);

		textBlock.getElement().getStyle().setDisplay(Style.Display.BLOCK);
		widgetContainer.add(textBlock);

		FlowPanel lowerBtnBlock = new FlowPanel();
		lowerBtnBlock.add(btnDownHour);
		lowerBtnBlock.add(separatorDown1);
		lowerBtnBlock.add(btnDownMinute);
		lowerBtnBlock.add(separatorDown2);
		lowerBtnBlock.add(btnDownSecond);

		widgetContainer.add(lowerBtnBlock);
		hCenter.add(widgetContainer);
		hCenter.add(verticalAlign);

		timePickerContainer.add(hCenter);
		initWidget(timePickerContainer);

		setAbsolute();

		btnUpHour.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hour = updateUpValue(txtHour, hour, maxHourVal);
				ValueChangeEvent.fire(TimePicker.this, getValue());
			}
		}, ClickEvent.getType());

		btnUpMinute.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				minute = updateUpValue(txtMinute, minute, maxMinuteVal);
				ValueChangeEvent.fire(TimePicker.this, getValue());
			}
		}, ClickEvent.getType());

		btnUpSecond.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				second = updateUpValue(txtSecond, second, maxSecondVal);
				ValueChangeEvent.fire(TimePicker.this, getValue());
			}
		}, ClickEvent.getType());

		btnDownHour.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hour = updateDownValue(txtHour, hour, maxHourVal);
				ValueChangeEvent.fire(TimePicker.this, getValue());
			}
		}, ClickEvent.getType());

		btnDownMinute.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				minute = updateDownValue(txtMinute, minute, maxMinuteVal);
				ValueChangeEvent.fire(TimePicker.this, getValue());
			}
		}, ClickEvent.getType());

		btnDownSecond.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				second = updateDownValue(txtSecond, second, maxSecondVal);
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
				if (event.isUpArrow()) {
					hour = updateUpValue(txtHour, hour, maxHourVal);
					ValueChangeEvent.fire(TimePicker.this, getValue());
					return;
				}
				if (event.isDownArrow()) {
					hour = updateDownValue(txtHour, hour, maxHourVal);
					ValueChangeEvent.fire(TimePicker.this, getValue());
					return;
				}
			}
		});

		txtMinute.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.isUpArrow()) {
					minute = updateUpValue(txtMinute, minute, maxMinuteVal);
					ValueChangeEvent.fire(TimePicker.this, getValue());
					return;
				}
				if (event.isDownArrow()) {
					minute = updateDownValue(txtMinute, minute, maxMinuteVal);
					ValueChangeEvent.fire(TimePicker.this, getValue());
					return;
				}
			}
		});

		txtSecond.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.isUpArrow()) {
					second = updateUpValue(txtSecond, second, maxSecondVal);
					ValueChangeEvent.fire(TimePicker.this, getValue());
					return;
				}
				if (event.isDownArrow()) {
					second = updateDownValue(txtSecond, second, maxSecondVal);
					ValueChangeEvent.fire(TimePicker.this, getValue());
					return;
				}
			}
		});

	}

	public void setAbsolute() {
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

		dateBlock.getElement().getStyle().setDisplay(Display.BLOCK);
		dateBlock.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		dateBlock.getElement().getStyle().setMarginBottom(10, Style.Unit.PCT);

		txtHour.getElement().getStyle().setWidth(componentWidth, Style.Unit.PCT);
		txtHour.getElement().getStyle().setMarginLeft(marginLeft, Style.Unit.PX);
		txtHour.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		txtHour.setStyleName("time-picker-text");
		txtMinute.getElement().getStyle().setWidth(componentWidth, Style.Unit.PCT);
		txtMinute.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		txtMinute.setStyleName("time-picker-text");
		txtSecond.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		txtSecond.getElement().getStyle().setWidth(componentWidth, Style.Unit.PCT);
		txtSecond.setStyleName("time-picker-text");
		txtSecond.getElement().getStyle().setMarginRight(marginRight, Style.Unit.PX);

		widgetContainer.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
		verticalAlign.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);

		hCenter.getElement().getStyle().setHeight(100, Style.Unit.PCT);
		hCenter.getElement().getStyle().setDisplay(Display.BLOCK);
		hCenter.getElement().getStyle().setProperty("marginLeft", "auto");
		hCenter.getElement().getStyle().setProperty("marginRight", "auto");

		setButtonStyleAbsolute(btnUpHour, "time-picker-up");
		btnUpHour.getElement().getStyle().setMarginLeft(marginLeft, Style.Unit.PX);
		setButtonStyleAbsolute(btnUpMinute, "time-picker-up");
		setButtonStyleAbsolute(btnUpSecond, "time-picker-up");
		btnUpSecond.getElement().getStyle().setMarginRight(marginRight, Style.Unit.PX);

		setButtonStyleAbsolute(btnDownHour, "time-picker-down");
		btnDownHour.getElement().getStyle().setMarginLeft(marginLeft, Style.Unit.PX);
		setButtonStyleAbsolute(btnDownMinute, "time-picker-down");
		setButtonStyleAbsolute(btnDownSecond, "time-picker-down");
		btnDownSecond.getElement().getStyle().setMarginRight(marginRight, Style.Unit.PX);

		setSeparatorStyle(separatorUp1, true);
		setSeparatorStyle(separatorUp2, true);
		setSeparatorStyle(separatorTime1, false);
		setSeparatorStyle(separatorTime2, false);
		setSeparatorStyle(separatorDown1, true);
		setSeparatorStyle(separatorDown2, true);
	}

	public void setRelative() {
		timePickerContainer.getElement().getStyle().setPosition(Position.RELATIVE);
		timePickerContainer.getElement().getStyle().setHeight(0, Style.Unit.PX);
		timePickerContainer.getElement().getStyle().setBottom(0, Style.Unit.PCT);
		timePickerContainer.getElement().getStyle().setOverflow(Overflow.HIDDEN);

		setStyleName(timePickerContainer.getElement(), "time-picker");
		timePickerContainer.getElement().getStyle().setWidth(relativeWidth, Style.Unit.PX);

		verticalAlign.getElement().getStyle().setHeight(100, Style.Unit.PCT);
		verticalAlign.getElement().getStyle().setWidth(0, Style.Unit.PCT);
		widgetContainer.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		verticalAlign.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

		dateBlock.getElement().getStyle().setDisplay(Display.NONE);

		txtHour.getElement().getStyle().setWidth(componentWidthRelative, Style.Unit.PX);
		txtHour.getElement().getStyle().setMarginLeft(marginLeft, Style.Unit.PX);
		txtHour.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		txtHour.setStyleName("time-picker-text");
		txtMinute.getElement().getStyle().setWidth(componentWidthRelative, Style.Unit.PX);
		txtMinute.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		txtMinute.setStyleName("time-picker-text");
		txtSecond.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		txtSecond.getElement().getStyle().setWidth(componentWidthRelative, Style.Unit.PX);
		txtSecond.setStyleName("time-picker-text");
		txtSecond.getElement().getStyle().setMarginRight(marginRight, Style.Unit.PX);

		widgetContainer.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
		verticalAlign.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);

		hCenter.getElement().getStyle().setHeight(100, Style.Unit.PCT);
		hCenter.getElement().getStyle().setDisplay(Display.BLOCK);
		hCenter.getElement().getStyle().setProperty("marginLeft", "auto");
		hCenter.getElement().getStyle().setProperty("marginRight", "auto");

		setButtonStyleRelative(btnUpHour, "time-picker-up");
		btnUpHour.getElement().getStyle().setMarginLeft(marginLeft, Style.Unit.PX);
		setButtonStyleRelative(btnUpMinute, "time-picker-up");
		setButtonStyleRelative(btnUpSecond, "time-picker-up");
		btnUpSecond.getElement().getStyle().setMarginRight(marginRight, Style.Unit.PX);

		setButtonStyleRelative(btnDownHour, "time-picker-down");
		btnDownHour.getElement().getStyle().setMarginLeft(marginLeft, Style.Unit.PX);
		setButtonStyleRelative(btnDownMinute, "time-picker-down");
		setButtonStyleRelative(btnDownSecond, "time-picker-down");
		btnDownSecond.getElement().getStyle().setMarginRight(marginRight, Style.Unit.PX);

		setSeparatorStyleRelative(separatorUp1, true);
		setSeparatorStyleRelative(separatorUp2, true);
		setSeparatorStyleRelative(separatorTime1, false);
		setSeparatorStyleRelative(separatorTime2, false);
		setSeparatorStyleRelative(separatorDown1, true);
		setSeparatorStyleRelative(separatorDown2, true);
		setHeight(100, Style.Unit.PCT);
	}

	private void setHeight(int aHeight, Unit aUnit) {
		timePickerContainer.getElement().getStyle().setHeight(aHeight, aUnit);
	}

	public void show() {
		DateTimeFormat fmt = DateTimeFormat.getFormat("dd:MM:yyyy");
		if (currentDate == null) {
			dateBlock.getElement().setInnerText(constants.date());
		} else {
			dateBlock.getElement().setInnerText(constants.date() + fmt.format(currentDate));
		}
		setHeight(100, Style.Unit.PCT);
		showing = true;
	}

	public void hide() {
		setHeight(0, Style.Unit.PCT);
		showing = false;
	}

	public boolean isShowing() {
		return showing;
	}

	public Date getValue() {
		return new Date(hour * 60 * 60 * 1000 + minute * 60 * 1000 + second * 1000);
	}

	private int updateValue(IntegerBox aInput, int aVal, int maxVal) {
		if (aInput.getValue() != null) {
			int val = aInput.getValue();
			if (val > maxVal) {
				val = maxVal;
				aInput.setValue(val);
			}
			if (val < 0) {
				val = 0;
				aInput.setValue(val);
			}
			aVal = val;
		}
		return aVal;
	}

	private int updateUpValue(IntegerBox aInput, int aVal, int maxVal) {
		aVal += 1;
		if (aVal > maxVal) {
			aVal = 0;
		}
		aInput.setValue(aVal);
		return aVal;
	}

	private int updateDownValue(IntegerBox aInput, int aVal, int maxVal) {
		aVal -= 1;
		if (aVal < 0) {
			aVal = maxVal;
		}
		aInput.setValue(aVal);
		return aVal;
	}

	private void setButtonStyleAbsolute(Widget aWidget, String divStyleName) {
		aWidget.getElement().getStyle().setWidth(componentWidth, Style.Unit.PCT);
		aWidget.getElement().getStyle().setHeight(componentHeight, Style.Unit.PX);
		aWidget.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		aWidget.getElement().addClassName(divStyleName);
	}

	private void setButtonStyleRelative(Widget aWidget, String divStyleName) {
		aWidget.getElement().getStyle().setWidth(componentWidthRelative, Style.Unit.PX);
		aWidget.getElement().getStyle().setHeight(componentHeight, Style.Unit.PX);
		aWidget.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		aWidget.getElement().addClassName(divStyleName);
	}

	private void setSeparatorStyle(Widget separator, boolean invisible) {
		separator.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		separator.getElement().getStyle().setWidth(separatorWidth, Style.Unit.PCT);
		separator.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		separator.getElement().setInnerText(":");
		separator.setStyleName("time-picker-separator");
		if (invisible) {
			separator.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		}
	}

	private void setSeparatorStyleRelative(Widget separator, boolean invisible) {
		separator.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		separator.getElement().getStyle().setWidth(separatorWidthRelative, Style.Unit.PX);
		separator.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		separator.getElement().setInnerText(":");
		separator.setStyleName("time-picker-separator");
		if (invisible) {
			separator.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		}
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Date> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	public void setValue(Date aDate) {
		setValue(aDate, false);
	}

	@Override
	public void setValue(Date value, boolean fireEvents) {
		currentDate = value;
		if (value == null) {
			hour = 0;
			minute = 0;
			second = 0;
		} else {
			hour = value.getHours();
			minute = value.getMinutes();
			second = value.getSeconds();
		}
		txtHour.setValue(hour);
		txtMinute.setValue(minute);
		txtSecond.setValue(second);
		if (fireEvents) {
			ValueChangeEvent.fire(TimePicker.this, getValue());
		}
	}

}
