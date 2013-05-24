/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.cell.core.client.form;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.event.TwinTriggerClickEvent;

public class TwinTriggerFieldCell<T> extends TriggerFieldCell<T> {

  public interface TwinTriggerFieldAppearance extends TriggerFieldAppearance {
    boolean twinTriggerIsOrHasChild(XElement parent, Element target);

    void onTwinTriggerOver(XElement parent, boolean over);

    void onTwinTriggerClick(XElement parent, boolean click);

  }

  private final TwinTriggerFieldAppearance appearance;

  public TwinTriggerFieldCell() {
    this(GWT.<TwinTriggerFieldAppearance> create(TwinTriggerFieldAppearance.class));
  }

  public TwinTriggerFieldCell(TwinTriggerFieldAppearance appearance) {
    super(appearance);
    this.appearance = appearance;
  }

  @Override
  public TwinTriggerFieldAppearance getAppearance() {
    return appearance;
  }

  protected void onClick(Context context, XElement parent, NativeEvent event, T value, ValueUpdater<T> updater) {
    Element target = event.getEventTarget().cast();

    if (!isReadOnly() && appearance.twinTriggerIsOrHasChild(parent, target)) {
      onTwinTriggerClick(context, parent, event, value, updater);
    }

    if (!isReadOnly() && appearance.triggerIsOrHasChild(parent, target)) {
      onTriggerClick(context, parent, event, value, updater);
    }

  };

  protected void onTwinTriggerClick(Context context, XElement parent, NativeEvent event, T value,
      ValueUpdater<T> updater) {
    fireEvent(context, new TwinTriggerClickEvent());
  }

  @Override
  protected void onMouseOver(XElement parent, NativeEvent event) {
    super.onMouseOver(parent, event);
    XElement target = event.getEventTarget().cast();
    if (!isReadOnly() && appearance.twinTriggerIsOrHasChild(parent, target)) {
      appearance.onTwinTriggerOver(parent, true);
    }
  }

  @Override
  protected void onMouseOut(XElement parent, NativeEvent event) {
    super.onMouseOut(parent, event);
    XElement target = event.getEventTarget().cast();
    if (!isReadOnly() && appearance.twinTriggerIsOrHasChild(parent, target)) {
      appearance.onTwinTriggerOver(parent, false);
    }
  }
  
}
