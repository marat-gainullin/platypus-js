/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gwt.ui.widgets;

import java.util.Date;

import com.bearsoft.gwt.ui.CommonResources;
import com.bearsoft.gwt.ui.XElement;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasAllKeyHandlers;
import com.google.gwt.event.dom.client.HasBlurHandlers;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.datepicker.client.DateBox;

/**
 * 
 * @author mg
 */
public class DateTimeBox extends Composite implements RequiresResize, HasValue<Date>, HasText, HasValueChangeHandlers<Date>, IsEditor<LeafValueEditor<Date>>, Focusable, HasAllKeyHandlers,
        HasFocusHandlers, HasBlurHandlers {

	private static final DateBox.DefaultFormat DEFAULT_FORMAT = GWT.create(DateBox.DefaultFormat.class);

	protected FlowPanel container = new FlowPanel();
	protected SimplePanel fieldWrapper = new SimplePanel();
	protected DateBox field;

	protected SimplePanel right = new SimplePanel();

	protected PopupPanel popup = new PopupPanel();
	private TextBox box;
	private DateTimePicker picker; // combines date and time picker

	private Date value;
	protected boolean settingValueFromJs;
	protected boolean settingValueToJs;

	protected boolean isDateShow = true;
	protected boolean isTimeShow = true;

	public DateTimeBox() {
		this(new DateTimePicker(), null, DEFAULT_FORMAT);
	}

	public DateTimeBox(DateTimePicker aPicker, Date date, DateBox.Format format) {
		initWidget(container);
		picker = aPicker;
		container.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		container.getElement().getStyle().setPosition(Style.Position.RELATIVE);
		container.getElement().addClassName("date-time-field");
		field = new DecoratedDateBox(picker, date, format);
		field.setFireNullValues(true);

		box = field.getTextBox();
		box.getElement().getStyle().setOutlineStyle(Style.OutlineStyle.NONE);
		box.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				Date newValue = getValue();
				if (value == null ? newValue != null : !value.equals(newValue)) {
					value = newValue;
					updateTimeValue(value, true);
					ValueChangeEvent.fire(DateTimeBox.this, newValue);
				}
			}
		});

		box.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				switch (event.getNativeKeyCode()) {
				case KeyCodes.KEY_ENTER:
				case KeyCodes.KEY_TAB:
					Date newValue = field.getValue();
					if (value == null ? newValue != null : !value.equals(newValue)) {
						value = newValue;
						updateTimeValue(value, true);
						ValueChangeEvent.fire(DateTimeBox.this, newValue);
					}
					break;
				}
			}
		});

		field.getDatePicker().addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				if (!settingValueFromJs) {
					settingValueToJs = true;
					Date datePart = event.getValue();
					Date timePart = picker.getTimePicker().getValue();
					Date newValue;
					if (timePart == null) {
						newValue = new Date(datePart.getTime());
					} else {
						newValue = new Date(datePart.getTime() + timePart.getTime());
					}

					if (value == null ? newValue != null : !value.equals(newValue)) {
						value = newValue;
						field.setValue(value, true);
						updateTimeValue(value, true);
						picker.getTimePicker().setValue(value);
						ValueChangeEvent.fire(DateTimeBox.this, newValue);
					}

					settingValueToJs = false;
				}
			}
		});

		picker.getTimePicker().addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				if (!settingValueFromJs) {
					settingValueToJs = true;
					Date timePart = picker.getTimePicker().getValue();
					Date datePart = field.getValue();
					if (timePart == null) {
						return;
					}
					CalendarUtil.resetTime(datePart);
					value = new Date(datePart.getTime() + timePart.getTime());
					field.setValue(value, true);
					ValueChangeEvent.fire(DateTimeBox.this, value);
					settingValueToJs = false;
				}
			}
		});

		fieldWrapper.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		fieldWrapper.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
		fieldWrapper.getElement().getStyle().setTop(0, Style.Unit.PX);
		fieldWrapper.getElement().getStyle().setHeight(100, Style.Unit.PCT);
		fieldWrapper.getElement().getStyle().setLeft(0, Style.Unit.PX);
		fieldWrapper.getElement().getStyle().setBorderWidth(0, Style.Unit.PX);
		fieldWrapper.getElement().getStyle().setMargin(0, Style.Unit.PX);
		fieldWrapper.getElement().getStyle().setPadding(0, Style.Unit.PX);
		CommonResources.INSTANCE.commons().ensureInjected();
		fieldWrapper.getElement().addClassName(CommonResources.INSTANCE.commons().borderSized());

		field.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		field.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
		field.getElement().getStyle().setTop(0, Style.Unit.PX);
		field.getElement().getStyle().setHeight(100, Style.Unit.PCT);
		field.getElement().getStyle().setLeft(0, Style.Unit.PX);
		field.getElement().getStyle().setWidth(100, Style.Unit.PCT);
		field.getElement().getStyle().setBorderWidth(0, Style.Unit.PX);
		field.getElement().getStyle().setMargin(0, Style.Unit.PX);
		field.getElement().getStyle().setPadding(0, Style.Unit.PX);
		field.getElement().getStyle().setBackgroundColor("inherit");
		field.getElement().getStyle().setColor("inherit");
		fieldWrapper.setWidget(field);

		right.getElement().addClassName("date-select");
		right.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		right.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
		right.getElement().getStyle().setTop(0, Style.Unit.PX);
		right.getElement().getStyle().setHeight(100, Style.Unit.PCT);
		right.getElement().getStyle().setRight(0, Style.Unit.PX);

		CommonResources.INSTANCE.commons().ensureInjected();
		right.getElement().addClassName(CommonResources.INSTANCE.commons().unselectable());

		popup.setStyleName("dateBoxPopup");
		popup.setAutoHideEnabled(true);
		container.add(fieldWrapper);
		container.add(right);
		right.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (!isReadonly()) {
					popup.setWidget(field.getDatePicker());
					popup.showRelativeTo(right);
				}
			}
		}, ClickEvent.getType());
		organizeFieldWrapperRight();
		getElement().<XElement> cast().addResizingTransitionEnd(this);
		if (field.getTextBox() instanceof HasKeyDownHandlers) {
			((HasKeyDownHandlers) field.getTextBox()).addKeyDownHandler(new KeyDownHandler() {

				@Override
				public void onKeyDown(KeyDownEvent event) {
					KeyDownEvent.fireNativeEvent(event.getNativeEvent(), DateTimeBox.this);
				}
			});
		}
		if (field.getTextBox() instanceof HasKeyUpHandlers) {
			((HasKeyUpHandlers) field.getTextBox()).addKeyUpHandler(new KeyUpHandler() {

				@Override
				public void onKeyUp(KeyUpEvent event) {
					KeyUpEvent.fireNativeEvent(event.getNativeEvent(), DateTimeBox.this);
				}
			});
		}
		if (field.getTextBox() instanceof HasKeyPressHandlers) {
			((HasKeyPressHandlers) field.getTextBox()).addKeyPressHandler(new KeyPressHandler() {

				@Override
				public void onKeyPress(KeyPressEvent event) {
					KeyPressEvent.fireNativeEvent(event.getNativeEvent(), DateTimeBox.this);
				}
			});
		}
		if (field.getTextBox() instanceof HasFocusHandlers) {
			((HasFocusHandlers) field.getTextBox()).addFocusHandler(new FocusHandler() {

				@Override
				public void onFocus(FocusEvent event) {
					FocusEvent.fireNativeEvent(event.getNativeEvent(), DateTimeBox.this);
				}
			});
		}
		if (field.getTextBox() instanceof HasBlurHandlers) {
			((HasBlurHandlers) field.getTextBox()).addBlurHandler(new BlurHandler() {

				@Override
				public void onBlur(BlurEvent event) {
					BlurEvent.fireNativeEvent(event.getNativeEvent(), DateTimeBox.this);
				}
			});
		}
		changeViewPresentation();
	}

	private void updateTimeValue(Date aValue, boolean fireEvents) {

		if (!settingValueToJs) {
			settingValueFromJs = true;
			picker.getTimePicker().setValue(aValue, fireEvents);
			settingValueFromJs = false;
		}
	}

	public boolean isDateVisible() {
		return isDateShow;
	}

	public void setDateVisible(boolean value) {
		isDateShow = value;
		changeViewPresentation();
	}

	public boolean isTimeVisible() {
		return isTimeShow;
	}

	public void setTimeVisible(boolean value) {
		isTimeShow = value;
		changeViewPresentation();
	}

	private void changeViewPresentation() {

		if (isDateShow == true && isTimeShow == true) {
			right.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
			picker.setDateAndTimeView();
			organizeFieldWrapperRight();
			right.getElement().removeClassName("time-select");
			right.getElement().addClassName("date-select");
			return;
		} else if (isDateShow == true) {
			right.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
			picker.setDateView();
			organizeFieldWrapperRight();
			right.getElement().removeClassName("time-select");
			right.getElement().addClassName("date-select");
			return;
		} else if (isTimeShow == true) {
			right.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
			picker.setTimeView();
			right.getElement().removeClassName("date-select");
			right.getElement().addClassName("time-select");
			organizeFieldWrapperRight();
			return;
		} else {
			right.getElement().getStyle().setDisplay(Display.NONE);
			organizeFieldWrapperRight();
			return;
		}

	}

	@Override
	public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
		return super.addHandler(handler, KeyDownEvent.getType());
	}

	@Override
	public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
		return super.addHandler(handler, KeyPressEvent.getType());
	}

	public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
		return super.addHandler(handler, KeyUpEvent.getType());
	}

	@Override
	public HandlerRegistration addFocusHandler(FocusHandler handler) {
		return addHandler(handler, FocusEvent.getType());
	}

	@Override
	public HandlerRegistration addBlurHandler(BlurHandler handler) {
		return addHandler(handler, BlurEvent.getType());
	}

	protected void organizeFieldWrapperRight() {
		fieldWrapper.getElement().getStyle().setRight(right.getElement().getOffsetWidth(), Style.Unit.PX);
	}

	@Override
	public void setFocus(boolean focused) {
		field.setFocus(focused);
	}

	@Override
	public void setAccessKey(char key) {
		field.setAccessKey(key);
	}

	@Override
	public int getTabIndex() {
		return field.getTabIndex();
	}

	@Override
	public void setTabIndex(int index) {
		field.setTabIndex(index);
	}

	@Override
	public Date getValue() {
		// return field.getValue();
		return value;
	}

	@Override
	public void setValue(Date value) {
		setValue(value, false);
	}

	@Override
	public void setValue(Date aValue, boolean fireEvents) {
		this.value = aValue;

		field.setValue(aValue, fireEvents);
		updateTimeValue(aValue, fireEvents);

		if (fireEvents) {
			ValueChangeEvent.fire(DateTimeBox.this, value);
		}
	}

	public String getText() {
		return field.getTextBox().getText();
	}

	@Override
	public void setText(String text) {
		Date newValue = field.getFormat().parse(field, text, false);
		if (newValue != null) {
			setValue(newValue, true);
		}
	}

	/**
	 * Gets the format instance used to control formatting and parsing of this
	 * {@link DateBox}.
	 * 
	 * @return the format
	 */
	public DateBox.Format getFormat() {
		return field.getFormat();
	}

	/**
	 * Sets the format used to control formatting and parsing of dates in this
	 * {@link DateBox}. If this {@link DateBox} is not empty, the contents of
	 * date box will be replaced with current contents in the new format.
	 * 
	 * @param format
	 *            the new date format
	 */
	public void setFormat(DateBox.Format format) {
		field.setFormat(format);
	}

	@Override
	public LeafValueEditor<Date> asEditor() {
		return field.asEditor();
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Date> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public void onResize() {
		organizeFieldWrapperRight();
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		organizeFieldWrapperRight();
	}

	@Override
	protected void onDetach() {
		super.onDetach();
	}

	public void setReadonly(boolean aValue) {
		box.getElement().setPropertyBoolean("readOnly", aValue);
	}

	public boolean isReadonly() {
		return box.getElement().getPropertyBoolean("readOnly");
	}
}
