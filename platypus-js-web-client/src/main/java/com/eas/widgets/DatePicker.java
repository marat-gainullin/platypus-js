package com.eas.widgets;

import com.eas.ui.ValueWidget;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import java.util.Date;

/**
 *
 * TODO: Don't forget to add time button and date button and switching between them in decorated-date-time-field as well as in the picker.
 * @author mgainullin
 */
public class DatePicker extends ValueWidget {

    private static String[] weekDaysNames = new String[]{
        "week.monday",
        "week.tuesday",
        "week.wensday",
        "week.thursday",
        "week.friday",
        "week.saturday",
        "week.sunday"
    };

    private Element prevYear = Document.get().createDivElement();
    private Element prevMonth = Document.get().createDivElement();
    private Element toToday = Document.get().createDivElement();
    private Element nextYear = Document.get().createDivElement();
    private Element nextMonth = Document.get().createDivElement();
    private Element[] weekDays = new Element[7];
    private Element[][] monthWeeks = new Element[6][];
    private Element timeSelect = Document.get().createDivElement();

    public DatePicker() {
        super();
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
    public void setValue(Object value) {
        super.setValue(value);
        format();
    }
    
    public void updateJsValue(Object value) {
        this.value = (Date) value;
        format();
    }

    private void format() {
        Date today = new Date();
        Date valueToShow = value != null ? (Date)value : new Date();
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
                    monthDay.classList.add("in-month-day");
                } else {
                    monthDay.classList.add("non-month-day");
                }
                if (today.getYear() == current.getYear() && today.getMonth() == current.getMonth() && today.getDate() == current.getDate()) {
                    monthDay.classList.add("today");
                }
                if (valueToShow.getYear() == current.getYear() && valueToShow.getMonth() == current.getMonth() && valueToShow.getDate() == current.getDate()) {
                    monthDay.classList.add("picked-day");
                }
            }
        }
    }

    @Override
    protected void publish(JavaScriptObject aValue) {
    }

}
