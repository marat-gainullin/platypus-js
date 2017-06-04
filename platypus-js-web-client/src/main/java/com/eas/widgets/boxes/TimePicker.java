package com.eas.widgets.boxes;

import com.eas.core.XElement;
import com.eas.ui.HasJsValue;
import com.eas.ui.Widget;
import com.eas.ui.events.HasValueChangeHandlers;
import com.eas.ui.events.ValueChangeEvent;
import com.eas.ui.events.ValueChangeHandler;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import java.util.Date;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import java.util.HashSet;
import java.util.Set;

public class TimePicker extends Widget implements HasJsValue, HasValueChangeHandlers {

    protected Element dateBlock = Document.get().createDivElement();
    protected Element widgetContainer = Document.get().createDivElement();
    protected Element verticalAlign = Document.get().createDivElement();
    protected Element btnUpHour = Document.get().createDivElement();
    protected Element btnDownHour = Document.get().createDivElement();
    protected InputElement txtHour = Document.get().createTextInputElement();
    protected Element btnUpMinute = Document.get().createDivElement();
    protected Element btnDownMinute = Document.get().createDivElement();
    protected InputElement txtMinute = Document.get().createTextInputElement();
    protected Element btnUpSecond = Document.get().createDivElement();
    protected Element btnDownSecond = Document.get().createDivElement();
    protected InputElement txtSecond = Document.get().createTextInputElement();

    protected Element separatorUp1 = Document.get().createDivElement();
    protected Element separatorUp2 = Document.get().createDivElement();
    protected Element separatorTime1 = Document.get().createDivElement();
    protected Element separatorTime2 = Document.get().createDivElement();
    protected Element separatorDown1 = Document.get().createDivElement();
    protected Element separatorDown2 = Document.get().createDivElement();

    private int hour;
    private int minute;
    private int second;
    private int maxHourVal = 23;
    private int maxMinuteVal = 59;
    private int maxSecondVal = maxMinuteVal;
    private boolean showing;
    private Date currentDate;

    public TimePicker() {
        super();
        dateBlock.setInnerText(Localization.get("date"));
        dateBlock.setClassName("time-picker-date");
        widgetContainer.appendChild(dateBlock);

        Element upperBtnBlock = Document.get().createDivElement();
        upperBtnBlock.appendChild(btnUpHour);
        upperBtnBlock.appendChild(separatorUp1);
        upperBtnBlock.appendChild(btnUpMinute);
        upperBtnBlock.appendChild(separatorUp2);
        upperBtnBlock.appendChild(btnUpSecond);
        widgetContainer.appendChild(upperBtnBlock);

        separatorTime1.setInnerText(":");
        separatorTime1.setClassName("time-picker-separator");
        separatorTime2.setInnerText(":");
        separatorTime2.setClassName("time-picker-separator");
        Element textBlock = Document.get().createDivElement();
        textBlock.appendChild(txtHour);
        textBlock.appendChild(separatorTime1);
        textBlock.appendChild(txtMinute);
        textBlock.appendChild(separatorTime2);
        textBlock.appendChild(txtSecond);
        textBlock.getStyle().setDisplay(Style.Display.BLOCK);
        widgetContainer.appendChild(textBlock);

        Element lowerBtnBlock = Document.get().createDivElement();
        lowerBtnBlock.appendChild(btnDownHour);
        lowerBtnBlock.appendChild(separatorDown1);
        lowerBtnBlock.appendChild(btnDownMinute);
        lowerBtnBlock.appendChild(separatorDown2);
        lowerBtnBlock.appendChild(btnDownSecond);

        widgetContainer.appendChild(lowerBtnBlock);
        element.appendChild(widgetContainer);
        element.appendChild(verticalAlign);

        btnUpHour.<XElement>cast().addEventListener(BrowserEvents.CLICK, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent event) {
                Date oldValue = getJsValue();
                hour = updateUpValue(txtHour, hour, maxHourVal);
                fireValueChange(oldValue);
            }
        });

        btnUpMinute.<XElement>cast().addEventListener(BrowserEvents.CLICK, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent event) {
                Date oldValue = getJsValue();
                minute = updateUpValue(txtMinute, minute, maxMinuteVal);
                fireValueChange(oldValue);
            }
        });

        btnUpSecond.<XElement>cast().addEventListener(BrowserEvents.CLICK, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent event) {
                Date oldValue = getJsValue();
                second = updateUpValue(txtSecond, second, maxSecondVal);
                fireValueChange(oldValue);
            }
        });

        btnDownHour.<XElement>cast().addEventListener(BrowserEvents.CLICK, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent event) {
                Date oldValue = getJsValue();
                hour = updateDownValue(txtHour, hour, maxHourVal);
                fireValueChange(oldValue);
            }
        });

        btnDownMinute.<XElement>cast().addEventListener(BrowserEvents.CLICK, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent event) {
                Date oldValue = getJsValue();
                minute = updateDownValue(txtMinute, minute, maxMinuteVal);
                fireValueChange(oldValue);
            }
        });

        btnDownSecond.<XElement>cast().addEventListener(BrowserEvents.CLICK, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent event) {
                Date oldValue = getJsValue();
                second = updateDownValue(txtSecond, second, maxSecondVal);
                fireValueChange(oldValue);
            }
        });

        txtHour.<XElement>cast().addEventListener(BrowserEvents.CHANGE, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent event) {
                Date oldValue = getJsValue();
                hour = updateValue(txtHour, hour, maxHourVal);
                fireValueChange(oldValue);
            }
        });
        txtMinute.<XElement>cast().addEventListener(BrowserEvents.CHANGE, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent event) {
                Date oldValue = getJsValue();
                minute = updateValue(txtMinute, minute, maxMinuteVal);
                fireValueChange(oldValue);
            }
        });
        txtSecond.<XElement>cast().addEventListener(BrowserEvents.CHANGE, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent event) {
                Date oldValue = getJsValue();
                second = updateValue(txtSecond, second, maxSecondVal);
                fireValueChange(oldValue);
            }
        });

        txtHour.<XElement>cast().addEventListener(BrowserEvents.KEYDOWN, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent event) {
                Date oldValue = getJsValue();
                if (event.getKeyCode() == KeyCodes.KEY_UP) {
                    hour = updateUpValue(txtHour, hour, maxHourVal);
                    fireValueChange(oldValue);
                } else if (event.getKeyCode() == KeyCodes.KEY_DOWN) {
                    hour = updateDownValue(txtHour, hour, maxHourVal);
                    fireValueChange(oldValue);
                }
            }
        });

        txtMinute.<XElement>cast().addEventListener(BrowserEvents.KEYDOWN, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent event) {
                Date oldValue = getJsValue();
                if (event.getKeyCode() == KeyCodes.KEY_UP) {
                    minute = updateUpValue(txtMinute, minute, maxMinuteVal);
                    fireValueChange(oldValue);
                } else if (event.getKeyCode() == KeyCodes.KEY_DOWN) {
                    minute = updateDownValue(txtMinute, minute, maxMinuteVal);
                    fireValueChange(oldValue);
                }
            }
        });

        txtSecond.<XElement>cast().addEventListener(BrowserEvents.KEYDOWN, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent event) {
                Date oldValue = getJsValue();
                if (event.getKeyCode() == KeyCodes.KEY_UP) {
                    second = updateUpValue(txtSecond, second, maxSecondVal);
                    fireValueChange(oldValue);
                } else if (event.getKeyCode() == KeyCodes.KEY_DOWN) {
                    second = updateDownValue(txtSecond, second, maxSecondVal);
                    fireValueChange(oldValue);
                }
            }
        });

    }

    public void show() {
        DateTimeFormat fmt = DateTimeFormat.getFormat("dd:MM:yyyy");
        if (currentDate == null) {
            dateBlock.setInnerText(Localization.get("date"));
        } else {
            dateBlock.setInnerText(Localization.get("date") + fmt.format(currentDate));
        }
        element.getStyle().setHeight(100, Style.Unit.PCT);
        showing = true;
    }

    public void hide() {
        element.getStyle().setHeight(0, Style.Unit.PCT);
        showing = false;
    }

    public boolean isShowing() {
        return showing;
    }

    private int updateValue(InputElement aInput, int aVal, int maxVal) {
        if (aInput.getValue() != null) {
            int val = Integer.valueOf(aInput.getValue());
            if (val > maxVal) {
                val = maxVal;
                aInput.setValue(val + "");
            }
            if (val < 0) {
                val = 0;
                aInput.setValue(val + "");
            }
            aVal = val;
        }
        return aVal;
    }

    private int updateUpValue(InputElement aInput, int aVal, int maxVal) {
        aVal += 1;
        if (aVal > maxVal) {
            aVal = 0;
        }
        aInput.setValue(aVal + "");
        return aVal;
    }

    private int updateDownValue(InputElement aInput, int aVal, int maxVal) {
        aVal -= 1;
        if (aVal < 0) {
            aVal = maxVal;
        }
        aInput.setValue(aVal + "");
        return aVal;
    }

    protected final Set<ValueChangeHandler> valueChangeHandlers = new HashSet<>();

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler handler) {
        valueChangeHandlers.add(handler);
        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                valueChangeHandlers.remove(handler);
            }

        };
    }

    protected void fireValueChange(Object oldValue) {
        ValueChangeEvent event = new ValueChangeEvent(this, oldValue, getJsValue());
        for (ValueChangeHandler h : valueChangeHandlers) {
            h.onValueChange(event);
        }
    }

    @Override
    public Date getJsValue() {
        return new Date(hour * 60 * 60 * 1000 + minute * 60 * 1000 + second * 1000);
    }

    @Override
    public void setJsValue(Object value) {
        Date oldValue = getJsValue();
        updateJsValue(value);
        fireValueChange(oldValue);
    }
    
    public void updateJsValue(Object value) {
        currentDate = (Date) value;
        if (value == null) {
            hour = 0;
            minute = 0;
            second = 0;
        } else {
            hour = currentDate.getHours();
            minute = currentDate.getMinutes();
            second = currentDate.getSeconds();
        }
        txtHour.setValue(hour + "");
        txtMinute.setValue(minute + "");
        txtSecond.setValue(second + "");
    }

    @Override
    protected void publish(JavaScriptObject aValue) {
    }

}
