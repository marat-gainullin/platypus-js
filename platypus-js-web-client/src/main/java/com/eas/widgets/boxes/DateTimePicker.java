package com.eas.widgets.boxes;

import com.eas.ui.HasJsValue;
import com.eas.ui.Widget;
import com.eas.ui.events.HasValueChangeHandlers;
import com.eas.ui.events.ValueChangeEvent;
import com.eas.ui.events.ValueChangeHandler;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.HandlerRegistration;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author mgainullin
 */
public class DateTimePicker extends Widget implements HasJsValue, HasValueChangeHandlers {

    // TODO: add localization features
    private static String[] weekDaysNames = new String[]{
        "week.monday",
        "week.tuesday",
        "week.wensday",
        "week.thursday",
        "week.friday",
        "week.saturday",
        "week.sunday"
    };

    private Date value;
    private TimePicker timePicker;
    private Element prevYear = Document.get().createDivElement();
    private Element prevMonth = Document.get().createDivElement();
    private Element toToday = Document.get().createDivElement();
    private Element nextYear = Document.get().createDivElement();
    private Element nextMonth = Document.get().createDivElement();
    private Element[] weekDays = new Element[7];
    private Element[][] monthWeeks = new Element[6][];
    private Element timeSelect = Document.get().createDivElement();

    public DateTimePicker() {
        super();
        timePicker = new TimePicker();
        element.setClassName("date-picker");
        prevYear.setClassName("prev-year");
        prevMonth.setClassName("prev-month");
        toToday.setClassName("to-today");
        nextMonth.setClassName("nextMonth");
        nextYear.setClassName("next-year");
        timeSelect.setClassName("time-select");
        timeSelect.setTitle(Localization.get("time"));
        element.appendChild(prevYear);
        element.appendChild(prevMonth);
        element.appendChild(toToday);
        element.appendChild(nextMonth);
        element.appendChild(nextYear);
        for (int i = 0; i < weekDays.length; i++) {
            Element weekDay = Document.get().createDivElement();
            weekDays[i] = weekDay;
            weekDay.setInnerText(Localization.get(weekDaysNames[i]));
            weekDay.setClassName("week-day");
            element.appendChild(weekDay);
        }
        for (int d = 0; d < monthWeeks.length; d++) {
            monthWeeks[d] = new Element[weekDays.length];
            for (int w = 0; w < weekDays.length; w++) {
                Element monthDay = Document.get().createDivElement();
                monthWeeks[d][w] = monthDay;
                element.appendChild(monthDay);
            }
            Element weekEnd = Document.get().createBRElement();
            element.appendChild(weekEnd);
        }
        element.appendChild(timeSelect);
    }

    @Override
    public Date getJsValue() {
        return value;
    }

    @Override
    public void setJsValue(Object value) {
        this.value = (Date) value;
        format();
    }

    private void format() {
        Date today = new Date();
        Date valueToShow = value != null ? value : new Date();
        int monthToShow = valueToShow.getMonth();
        Date current = valueToShow;
        // set start's day of month to 1st
        // set start's day of week to 1st
        for (int d = 0; d < monthWeeks.length; d++) {
            for (int w = 0; w < monthWeeks[d].length; w++) {
                //start.addOneDay();
                int dayOfMonth = 1;//start.getDayOfMonth();
                Element monthDay = monthWeeks[d][w];
                monthDay.setClassName("month-day");
                monthDay.setInnerText(dayOfMonth + "");
                if (monthToShow == current.getMonth()) {
                    monthDay.addClassName("in-month-day");
                } else {
                    monthDay.addClassName("non-month-day");
                }
                if (today.getYear() == current.getYear() && today.getMonth() == current.getMonth() && today.getDate() == current.getDate()) {
                    monthDay.addClassName("today");
                }
                if (valueToShow.getYear() == current.getYear() && valueToShow.getMonth() == current.getMonth() && valueToShow.getDate() == current.getDate()) {
                    monthDay.addClassName("picked-day");
                }
            }
        }
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
        ValueChangeEvent event = new ValueChangeEvent(this, oldValue, value);
        for (ValueChangeHandler h : valueChangeHandlers) {
            h.onValueChange(event);
        }
    }

    @Override
    protected void publish(JavaScriptObject aValue) {
    }

}
