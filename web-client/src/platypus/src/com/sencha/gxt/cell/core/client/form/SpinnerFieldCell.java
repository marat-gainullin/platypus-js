/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.cell.core.client.form;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.HasBeforeSelectionHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.cell.HandlerManagerContext;
import com.sencha.gxt.widget.core.client.event.CellBeforeSelectionEvent;
import com.sencha.gxt.widget.core.client.event.CellSelectionEvent;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor;

/**
 * A numeric cell with up / down arrows that increment / decrement the value.
 * 
 * @param <N>
 */
public class SpinnerFieldCell<N extends Number> extends NumberInputCell<N> implements HasBeforeSelectionHandlers<N>,
    HasSelectionHandlers<N> {

  public interface SpinnerFieldAppearance extends TwinTriggerFieldAppearance {

  }

  private Number minValue = Double.MIN_VALUE;
  private Number maxValue = Double.MAX_VALUE;

  public SpinnerFieldCell(NumberPropertyEditor<N> propertyEditor) {
    this(propertyEditor, GWT.<SpinnerFieldAppearance> create(SpinnerFieldAppearance.class));
  }

  public SpinnerFieldCell(NumberPropertyEditor<N> propertyEditor, SpinnerFieldAppearance appearance) {
    super(propertyEditor, appearance);
  }

  /**
   * Adds a {@link BeforeSelectionEvent} handler. The handler will be passed an
   * instance of {@link CellBeforeSelectionEvent} which can be cast to.
   * 
   * @param handler the handler
   * @return the registration for the event
   */
  @Override
  public HandlerRegistration addBeforeSelectionHandler(BeforeSelectionHandler<N> handler) {
    return addHandler(handler, BeforeSelectionEvent.getType());
  }

  /**
   * Adds a {@link SelectionEvent} handler. The handler will be passed an
   * instance of {@link CellSelectionEvent} which can be cast to.
   * 
   * @param handler the handler
   * @return the registration for the event
   */
  @Override
  public HandlerRegistration addSelectionHandler(SelectionHandler<N> handler) {
    return addHandler(handler, SelectionEvent.getType());
  }

  /**
   * Sets the increment value.
   * 
   * @param context the context
   * @return the increment
   */
  public N getIncrement(Context context) {
    return getPropertyEditor().getIncrement();
  }

  /**
   * Returns the fields max value.
   * 
   * @param context the context
   * @return the max value
   */
  public Number getMaxValue(Context context) {
    return maxValue;
  }

  /**
   * Returns the field's minimum value.
   * 
   * @param context the context
   * @return the min value
   */
  public Number getMinValue(Context context) {
    return minValue;
  }

  /**
   * Sets the increment that should be used (defaults to 1d).
   * 
   * @param increment the increment to set.
   */
  public void setIncrement(N increment) {
    getPropertyEditor().setIncrement(increment);
  }

  /**
   * Sets the field's max allowable value.
   * 
   * @param maxValue the max value
   */
  public void setMaxValue(Number maxValue) {
    this.maxValue = maxValue.doubleValue();
  }

  /**
   * Sets the field's minimum allowed value.
   * 
   * @param minValue the minimum value
   */
  public void setMinValue(Number minValue) {
    this.minValue = minValue.doubleValue();
  }

  protected void doSpin(Cell.Context context, XElement parent, N value, ValueUpdater<N> updater, boolean up) {
    if (!isReadOnly()) {
      // use the current value in the input element
      InputElement input = getInputElement(parent);
      String v = input.getValue();

      if (!"".equals(v)) {
        try {
          value = getPropertyEditor().parse(v);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

      boolean cancelled = false;
      if (context instanceof HandlerManagerContext) {
        HandlerManager manager = ((HandlerManagerContext) context).getHandlerManager();
        CellBeforeSelectionEvent<N> event = CellBeforeSelectionEvent.fire(manager, context, value);
        if (event != null && event.isCanceled()) {
          cancelled = true;
        }
      } else {
        CellBeforeSelectionEvent<N> event = CellBeforeSelectionEvent.fire(this, context, value);
        if (!fireCancellableEvent(event)) {
          cancelled = true;
        }
      }

      if (!cancelled) {
        N newVal = null;
        if (up) {
          newVal = getPropertyEditor().incr(value);
          if (newVal.doubleValue() > maxValue.doubleValue() || newVal.doubleValue() < minValue.doubleValue()) {
            return;
          }
          input.setValue(getPropertyEditor().render(newVal));
        } else {
          newVal = getPropertyEditor().decr(value);
          if (newVal.doubleValue() > maxValue.doubleValue() || newVal.doubleValue() < minValue.doubleValue()) {
            return;
          }
          input.setValue(getPropertyEditor().render(newVal));
        }
        if (context instanceof HandlerManagerContext) {
          HandlerManager manager = ((HandlerManagerContext) context).getHandlerManager();
          CellSelectionEvent.fire(manager, context, newVal);
        } else {
          CellSelectionEvent.fire(this, lastContext, newVal);
        }
      }
    }
  }

  @Override
  protected void onNavigationKey(Context context, Element parent, N value, NativeEvent event,
      ValueUpdater<N> valueUpdater) {
    super.onNavigationKey(context, parent, value, event, valueUpdater);
    switch (event.getKeyCode()) {
      case KeyCodes.KEY_UP:
        event.stopPropagation();
        event.preventDefault();
        doSpin(context, parent.<XElement> cast(), value, valueUpdater, true);
        break;
      case KeyCodes.KEY_DOWN:
        event.stopPropagation();
        event.preventDefault();
        doSpin(context, parent.<XElement> cast(), value, valueUpdater, false);
        break;
    }
  }

  @Override
  protected void onTriggerClick(Context context, XElement parent, NativeEvent event, N value, ValueUpdater<N> updater) {
    super.onTriggerClick(context, parent, event, value, updater);
    if (!isReadOnly() && !isDisabled()) {
      doSpin(context, parent, value, updater, true);
    }
    getInputElement(parent).focus();
  }

  @Override
  protected void onTwinTriggerClick(Context context, XElement parent, NativeEvent event, N value,
      ValueUpdater<N> updater) {
    super.onTwinTriggerClick(context, parent, event, value, updater);
    if (!isReadOnly() && !isDisabled()) {
      doSpin(context, parent, value, updater, false);
    }
    getInputElement(parent).focus();
  }

}
