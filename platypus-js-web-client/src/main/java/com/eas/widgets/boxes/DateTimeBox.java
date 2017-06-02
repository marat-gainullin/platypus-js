package com.eas.widgets.boxes;

import java.util.Date;

import com.eas.ui.CommonResources;
import com.eas.ui.HasDecorations;
import com.eas.ui.HasDecorationsWidth;
import com.eas.ui.Widget;
import com.eas.widgets.WidgetsUtils;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 *
 * @author mg
 */
public class DateTimeBox extends Widget implements HasJsValue, HasText, HasValueChangeHandlers, Focusable, HasKeyUpHandlers,
        HasKeyDownHandlers, HasKeyPressHandlers,
        HasFocusHandlers, HasBlurHandlers, HasDecorations, HasDecorationsWidth {

    protected String formatPattern = "dd.MM.yyyy";
    protected Flow container = new Flow();
    protected DateBox field;

    protected SimplePanel right = new SimplePanel();
    protected int decorationsWidth;

    private TextBox box;
    private OldDateTimePicker datePicker;
    private TimePicker timePicker;

    private Date value;
    protected boolean settingValueFromJs;
    protected boolean settingValueToJs;

    protected boolean dateShown = true;
    protected boolean timeShown = true;

    public DateTimeBox() {
        this(new OldDateTimePicker(), null, DEFAULT_FORMAT);
    }

    public DateTimeBox(OldDateTimePicker aPicker, Date date, DateBox.Format format) {
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

    public String getFormatPattern() {
        return formatPattern;
    }

    public void setFormatPattern(String aValue) {
        formatPattern = aValue;
        if (formatPattern != null) {
            formatPattern = WidgetsUtils.convertDateFormatString(formatPattern);
        }
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
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Date> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    public void setReadonly(boolean aValue) {
        box.getElement().setPropertyBoolean("readOnly", aValue);
    }

    public boolean isReadonly() {
        return box.getElement().getPropertyBoolean("readOnly");
    }
}
