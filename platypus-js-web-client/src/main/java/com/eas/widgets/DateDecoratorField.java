package com.eas.widgets;

import java.util.Date;
import com.eas.core.Utils;
import com.eas.core.XElement;
import com.eas.ui.Widget;
import com.eas.ui.events.HasValueChangeHandlers;
import com.eas.ui.events.ValueChangeEvent;
import com.eas.ui.events.ValueChangeHandler;
import com.eas.widgets.WidgetsUtils;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.eas.ui.HasValue;

/**
 *
 * @author mg
 */
public class DateDecoratorField extends ValueDecoratorField {

    private String formatPattern = FormattedField.DEFAULT_DATE_PATTERN + " " + FormattedField.DEFAULT_TIME_PATTERN;
    private DatePicker datePicker = new DatePicker();
    private TimePicker timePicker = new TimePicker();
    private Element dateSelect = Document.get().createDivElement();
    private Element timeSelect = Document.get().createDivElement();

    protected boolean dateShown = true;
    protected boolean timeShown = true;

    public DateDecoratorField() {
        super(Utils.isMobile() ? new DateTimeField() : new FormattedField(FormattedField.DATE));
        if (Utils.isMobile()) {
            ((FormattedField) decorated).setPattern(formatPattern);
        }
        element.addClassName("date-time-field");
        ((HasValueChangeHandlers) decorated).addValueChangeHandler(new ValueChangeHandler() {

            @Override
            public void onValueChange(ValueChangeEvent event) {
                Object newValue = event.getNewValue();
                timePicker.updateJsValue(newValue);
                datePicker.updateJsValue(newValue);
            }

        });

        datePicker.addValueChangeHandler(new ValueChangeHandler() {

            @Override
            public void onValueChange(ValueChangeEvent event) {
                Date datePart = (Date) event.getNewValue();
                Date timePart = (Date) timePicker.getValue();
                if (timePart == null) {
                    Date currentTime = new Date();
                    Date currentDate = new Date(currentTime.getTime()); // copy of currentTime
                    CalendarUtil.resetTime(currentDate);
                    timePart = new Date(currentTime.getTime() - currentDate.getTime());
                }
                Date newValue = new Date(datePart.getTime() + timePart.getTime());
                ((HasValue) decorated).setValue(newValue);
            }
        });

        timePicker.addValueChangeHandler(new ValueChangeHandler() {

            @Override
            public void onValueChange(ValueChangeEvent event) {
                Date timePart = (Date) event.getNewValue();
                Date datePart = (Date) datePicker.getValue();
                if (datePart == null) {
                    datePart = new Date();
                    CalendarUtil.resetTime(datePart);
                }
                Date newValue = new Date(datePart.getTime() + timePart.getTime());
                ((HasValue) decorated).setValue(newValue);
            }
        });

        // TODO: check all widgets againts unselectable feature of DOM elements
        dateSelect.setClassName("date-select");
        timeSelect.setClassName("time-select");

        dateSelect.<XElement>cast().addEventListener(BrowserEvents.CLICK, new XElement.NativeHandler() {

            @Override
            public void on(NativeEvent event) {
                if (!isReadonly()) {
                    datePicker.showRelativeTo(dateSelect);
                }
            }

        });

        timeSelect.<XElement>cast().addEventListener(BrowserEvents.CLICK, new XElement.NativeHandler() {

            @Override
            public void on(NativeEvent event) {
                if (!isReadonly()) {
                    timePicker.showRelativeTo(timeSelect);
                }
            }

        });
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
            dateSelect.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
            timeSelect.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        } else if (dateShown) {
            dateSelect.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
            timeSelect.getStyle().setDisplay(Style.Display.NONE);
        } else if (timeShown) {
            timeSelect.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
            dateSelect.getStyle().setDisplay(Style.Display.NONE);
        } else {
            dateSelect.getStyle().setDisplay(Style.Display.NONE);
            timeSelect.getStyle().setDisplay(Style.Display.NONE);
        }

    }

    @Override
    protected void publish(JavaScriptObject aValue) {
        publish(this, aValue);
    }

    private native static void publish(Widget aWidget, JavaScriptObject aPublished)/*-{
        var B = @com.eas.core.Predefine::boxing;
        aPublished.redraw = function(){
            aWidget.@com.eas.bound.ModelDate::rebind()();
        };
        Object.defineProperty(aPublished, "emptyText", {
            get : function() {
                return aWidget.@com.eas.ui.HasEmptyText::getEmptyText()();
            },
            set : function(aValue) {
                aWidget.@com.eas.ui.HasEmptyText::setEmptyText(Ljava/lang/String;)(aValue != null ? '' + aValue : null);
            }
        });
        Object.defineProperty(aPublished, "value", {
            get : function() {
                return B.boxAsJs(aWidget.@com.eas.bound.ModelDate::getJsValue()());
            },
            set : function(aValue) {
                aWidget.@com.eas.bound.ModelDate::setJsValue(Ljava/lang/Object;)(B.boxAsJava(aValue));
            }
        });
        Object.defineProperty(aPublished, "text", {
            get : function() {
                return B.boxAsJs(aWidget.@com.eas.bound.ModelDate::getText()());
            }
        });
        Object.defineProperty(aPublished, "format", {
            get : function() {
                return aWidget.@com.eas.bound.ModelDate::getFormat()();
            },
            set : function(aValue) {
                aWidget.@com.eas.bound.ModelDate::setFormat(Ljava/lang/String;)('' + aValue);
            }
        });
        Object.defineProperty(aPublished, "datePicker", {
            get : function() {
                return aWidget.@com.eas.bound.ModelDate::isDateShown()();
            },
            set : function(aValue) {
                aWidget.@com.eas.bound.ModelDate::setDateShown(Z)(aValue);
            }
        });
        Object.defineProperty(aPublished, "timePicker", {
            get : function() {
                return aWidget.@com.eas.bound.ModelDate::isTimeShown()();
            },
            set : function(aValue) {
                aWidget.@com.eas.bound.ModelDate::setTimeShown(Z)(aValue);
            }
        });
    }-*/;
}
