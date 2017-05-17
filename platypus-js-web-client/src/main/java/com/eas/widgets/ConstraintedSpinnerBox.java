package com.eas.widgets;

import com.eas.core.Utils;
import com.eas.ui.events.HasHideHandlers;
import com.eas.ui.events.HasShowHandlers;
import com.eas.ui.events.HideEvent;
import com.eas.ui.events.HideHandler;
import com.eas.ui.events.ShowEvent;
import com.eas.ui.events.ShowHandler;
import com.eas.widgets.boxes.SpinnerBox;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasName;
import com.google.gwt.user.client.ui.ValueBox;

public class ConstraintedSpinnerBox extends SpinnerBox<Double> implements HasShowHandlers, HasHideHandlers,
        HasResizeHandlers, HasName {

    protected double step = 1.0;
    protected Double min;
    protected Double max;

    public ConstraintedSpinnerBox(ValueBox<Double> aDecorated) {
        super(aDecorated);
        if (Utils.isMobile()) {
            field.getElement().<InputElement>cast().setAttribute("type", "number");
        }
    }

    @Override
    public HandlerRegistration addResizeHandler(ResizeHandler handler) {
        return addHandler(handler, ResizeEvent.getType());
    }

    @Override
    public void onResize() {
        super.onResize();
        if (isAttached()) {
            ResizeEvent.fire(this, getElement().getOffsetWidth(), getElement().getOffsetHeight());
        }
    }

    @Override
    public HandlerRegistration addHideHandler(HideHandler handler) {
        return addHandler(handler, HideEvent.getType());
    }

    @Override
    public HandlerRegistration addShowHandler(ShowHandler handler) {
        return addHandler(handler, ShowEvent.getType());
    }

    @Override
    public void setVisible(boolean visible) {
        boolean oldValue = isVisible();
        super.setVisible(visible);
        if (oldValue != visible) {
            if (visible) {
                ShowEvent.fire(this, this);
            } else {
                HideEvent.fire(this, this);
            }
        }
    }

    @Override
    protected void increment() {
        Double oldValue = getValue();
        Double newValue = (oldValue != null ? oldValue : 0) + step;
        if (max == null || newValue <= max) {
            setValue(newValue, true);
        }
    }

    @Override
    protected void decrement() {
        Double oldValue = getValue();
        Double newValue = (oldValue != null ? oldValue : 0) - step;
        if (min == null || newValue >= min) {
            setValue(newValue, true);
        }
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double aValue) {
        min = aValue;
        field.getElement().setAttribute("min", aValue + "");
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double aValue) {
        max = aValue;
        field.getElement().setAttribute("max", aValue + "");
    }

    public Double getStep() {
        return step;
    }

    public void setStep(Double aValue) {
        step = aValue;
        field.getElement().setAttribute("step", aValue + "");
    }
}
