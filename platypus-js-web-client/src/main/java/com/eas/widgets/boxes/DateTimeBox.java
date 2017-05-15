/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.widgets.boxes;

import java.util.Date;

import com.eas.core.XElement;
import com.eas.ui.CommonResources;
import com.eas.ui.HasDecorations;
import com.eas.ui.HasDecorationsWidth;
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
import com.google.gwt.user.client.ui.HasName;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.datepicker.client.DateBox;

/**
 * 
 * @author mg
 */
public class DateTimeBox extends Composite implements RequiresResize, HasValue<Date>, HasText, HasValueChangeHandlers<Date>, IsEditor<LeafValueEditor<Date>>, Focusable, HasAllKeyHandlers,
        HasFocusHandlers, HasBlurHandlers, HasDecorations, HasDecorationsWidth, HasName {

	private static final DateBox.DefaultFormat DEFAULT_FORMAT = GWT.create(DateBox.DefaultFormat.class);

	protected FlowPanel container = new FlowPanel();
	protected DateBox field;

	protected SimplePanel right = new SimplePanel();
	protected int decorationsWidth;

	protected PopupPanel popup = new PopupPanel() {

                @Override
		public void setPopupPosition(int popupLeft, int popupTop) {
			super.setPopupPosition(popupLeft, popupTop);
			if (datePicker != null) {
				int rightAbsoluteLeft = right.getElement().getAbsoluteLeft();
				datePicker.shown(rightAbsoluteLeft - popupLeft <= 30);
			}
		}

	};
	private TextBox box;
	private DateTimePicker datePicker;
	private TimePicker timePicker;

	private Date value;
	protected boolean settingValueFromJs;
	protected boolean settingValueToJs;

	protected boolean dateShown = true;
	protected boolean timeShown = true;

	protected AutoCloseBox autoCloseParent;

	public DateTimeBox() {
		this(new DateTimePicker(), null, DEFAULT_FORMAT);
	}

	public DateTimeBox(DateTimePicker aPicker, Date date, DateBox.Format format) {
		initWidget(container);
		datePicker = aPicker;
		timePicker = aPicker.getTimePicker();
		container.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		container.getElement().getStyle().setPosition(Style.Position.RELATIVE);
		container.getElement().addClassName("date-time-field");
		field = new CustomDateBox(datePicker, date, format);
		field.setFireNullValues(true);
		field.setStyleName("form-control");

		box = field.getTextBox();
		box.getElement().getStyle().setOutlineStyle(Style.OutlineStyle.NONE);
		field.addValueChangeHandler(new ValueChangeHandler<Date>() {

			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				Date newValue = event.getValue();
				if (value == null ? newValue != null : !value.equals(newValue)) {
					value = newValue;
					timePicker.setValue(value, false);
					datePicker.setValue(value, false);
					ValueChangeEvent.fire(DateTimeBox.this, newValue);
				}
			}

		});

		datePicker.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				Date datePart = event.getValue();
				Date timePart = timePicker.getValue();
				if (value == null && (new Date(0)).equals(timePart)) {
					Date currentTime = new Date();
					Date currentDate = new Date(currentTime.getTime());
					CalendarUtil.resetTime(currentDate);
					timePart = new Date(currentTime.getTime() - currentDate.getTime());
				}
				Date newValue;
				if (timePart == null) {
					newValue = datePart;
				} else {
					newValue = new Date(datePart.getTime() + timePart.getTime());
				}
				if (value == null ? newValue != null : !value.equals(newValue)) {
					value = newValue;
					field.setValue(value, false);
					timePicker.setValue(value, false);
					ValueChangeEvent.fire(DateTimeBox.this, newValue);
				}
			}
		});

		timePicker.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				Date timePart = timePicker.getValue();
				Date datePart = field.getValue();
				CalendarUtil.resetTime(datePart);
				value = new Date(datePart.getTime() + timePart.getTime());
				field.setValue(value, false);
				datePicker.setValue(value, false);
				ValueChangeEvent.fire(DateTimeBox.this, value);
			}
		});

		CommonResources.INSTANCE.commons().ensureInjected();
		field.getElement().addClassName(CommonResources.INSTANCE.commons().borderSized());

		field.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		field.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
		field.getElement().getStyle().setTop(0, Style.Unit.PX);
		field.getElement().getStyle().setHeight(100, Style.Unit.PCT);
		field.getElement().getStyle().setBottom(0, Style.Unit.PX);
		field.getElement().getStyle().setLeft(0, Style.Unit.PX);
		field.getElement().getStyle().setWidth(100, Style.Unit.PCT);
		field.getElement().getStyle().setMargin(0, Style.Unit.PX);
		field.getElement().getStyle().setBackgroundColor("inherit");
		field.getElement().getStyle().setColor("inherit");
		field.getElement().addClassName("date-time-box");

		right.getElement().addClassName("date-select");
		right.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		right.getElement().getStyle().setHeight(100, Style.Unit.PCT);
		right.getElement().getStyle().setPosition(Style.Position.RELATIVE);

		CommonResources.INSTANCE.commons().ensureInjected();
		right.getElement().addClassName(CommonResources.INSTANCE.commons().unselectable());

		popup.setStyleName("date-box-popup");
		popup.setAutoHideEnabled(true);
		container.add(field);
		container.add(right);
		right.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (!isReadonly()) {
					datePicker.setCurrentMonth(value != null ? value : new Date());
					popup.setWidget(datePicker);
					popup.showRelativeTo(right);
				}
			}
		}, ClickEvent.getType());

		redecorate();
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

	public boolean isDateVisible() {
		return dateShown;
	}

	public void setDateVisible(boolean value) {
		dateShown = value;
		changeViewPresentation();
	}

	public boolean isTimeVisible() {
		return timeShown;
	}

	public void setTimeVisible(boolean value) {
		timeShown = value;
		changeViewPresentation();
	}

	private void changeViewPresentation() {
		if (dateShown && timeShown) {
			right.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
			datePicker.setDateAndTimeView();
			right.getElement().removeClassName("time-select");
			right.getElement().addClassName("date-select");
			redecorate();
		} else if (dateShown) {
			right.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
			datePicker.setDateView();
			right.getElement().removeClassName("time-select");
			right.getElement().addClassName("date-select");
			redecorate();
		} else if (timeShown) {
			right.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
			datePicker.setTimeView();
			right.getElement().removeClassName("date-select");
			right.getElement().addClassName("time-select");
			redecorate();
		} else {
			right.getElement().getStyle().setDisplay(Display.NONE);
			redecorate();
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

	@Override
	public void setDecorationsWidth(int aDecorationsWidth) {
		decorationsWidth = aDecorationsWidth;
		redecorate();
	}

	@Override
	public HasWidgets getContainer() {
		return container;
	}

	protected void redecorate() {
		if (isAttached()) {
			int paddingRight = right.getElement().getOffsetWidth() + decorationsWidth;
			field.getElement().getStyle().setPaddingRight(paddingRight, Style.Unit.PX);
		}
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
		return value;
	}

	@Override
	public void setValue(Date value) {
		setValue(value, false);
	}

	@Override
	public void setValue(Date aValue, boolean fireEvents) {
		if (value == null ? aValue != null : !value.equals(aValue)) {
			value = aValue;
			field.setValue(aValue, false);
			timePicker.setValue(value, false);
			datePicker.setValue(value, false);
			if (fireEvents) {
				ValueChangeEvent.fire(DateTimeBox.this, value);
			}
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

        @Override
        public String getName() {
            return box.getName();
        }

        @Override
        public void setName(String name) {
            box.setName(name);
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
		redecorate();
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		redecorate();
		Widget lParent = getParent();
		while (lParent != null && !(lParent instanceof AutoCloseBox)) {
			lParent = lParent.getParent();
		}
		if (lParent instanceof AutoCloseBox)
			autoCloseParent = (AutoCloseBox) lParent;
		if (autoCloseParent != null) {
			autoCloseParent.addAutoHidePartner(popup.getElement());
		}
	}

	@Override
	protected void onDetach() {
		if (autoCloseParent != null) {
			autoCloseParent.removeAutoHidePartner(popup.getElement());
		}
		super.onDetach();
	}

	public void setReadonly(boolean aValue) {
		box.getElement().setPropertyBoolean("readOnly", aValue);
	}

	public boolean isReadonly() {
		return box.getElement().getPropertyBoolean("readOnly");
	}
}
