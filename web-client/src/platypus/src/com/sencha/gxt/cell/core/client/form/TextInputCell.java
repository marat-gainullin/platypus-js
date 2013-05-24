/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.cell.core.client.form;

import java.util.logging.Logger;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.GXTLogConfiguration;
import com.sencha.gxt.core.client.dom.XElement;

public class TextInputCell extends ValueBaseInputCell<String> {

  public interface TextFieldAppearance extends ValueBaseFieldAppearance {
    void onResize(XElement parent, int width, int height);

    void render(SafeHtmlBuilder sb, String type, String value, FieldAppearanceOptions options);
  }

  protected TextFieldAppearance appearance;

  private static Logger logger = Logger.getLogger(TextInputCell.class.getName());

  /**
   * Constructs a TextInputCell that renders its text without HTML markup.
   */
  public TextInputCell() {
    this(GWT.<TextFieldAppearance> create(TextFieldAppearance.class));
  }

  public TextInputCell(TextFieldAppearance appearance) {
    super(appearance, "change", "keyup");
    this.appearance = appearance;

    setWidth(150);
  }

  @Override
  public void finishEditing(Element parent, String value, Object key, ValueUpdater<String> valueUpdater) {
    if (GXTLogConfiguration.loggingIsEnabled()) {
      logger.finest("finishEditing");
    }

    String newValue = getText(XElement.as(parent));

    // Get the view data.
    FieldViewData vd = getViewData(key);
    if (vd == null) {
      vd = new FieldViewData(value);
      setViewData(key, vd);
    }
    vd.setCurrentValue(newValue);

    // Fire the value updater if the value has changed.
    if (valueUpdater != null && !vd.getCurrentValue().equals(vd.getLastValue())) {
      vd.setLastValue(newValue);
      valueUpdater.update(newValue);
    }

    // Blur the element.
    if (!GXT.isIE9()) {
      super.finishEditing(parent, newValue, key, valueUpdater);
    } else {
      //EXTGWT-1967
      clearViewData(key);
      clearFocusKey();
    }

  }

  public TextFieldAppearance getAppearance() {
    return appearance;
  }

  @Override
  public void onBrowserEvent(Context context, Element parent, String value, NativeEvent event,
      ValueUpdater<String> valueUpdater) {
    super.onBrowserEvent(context, parent, value, event, valueUpdater);

    // Ignore events that don't target the input.
    InputElement input = getInputElement(parent);
    Element target = event.getEventTarget().cast();
    if (!input.isOrHasChild(target)) {
      return;
    }

    String eventType = event.getType();
    Object key = context.getKey();
    if ("change".equals(eventType)) {
      if (GXTLogConfiguration.loggingIsEnabled()) {
        logger.finest("onBrowserEvent change event fired");
      }
      finishEditing(parent, value, key, valueUpdater);
    } else if ("keyup".equals(eventType)) {
      // Record keys as they are typed.
      FieldViewData vd = getViewData(key);
      if (vd == null) {
        vd = new FieldViewData(value);
        setViewData(key, vd);
      }
      vd.setCurrentValue(getText(XElement.as(parent)));
    }
  }

  @Override
  public void render(Context context, String value, SafeHtmlBuilder sb) {
    ViewData viewData = checkViewData(context, value);
    String s = (viewData != null) ? viewData.getCurrentValue() : value;

    FieldAppearanceOptions options = new FieldAppearanceOptions(getWidth(), getHeight(), isReadOnly(), getEmptyText());
    options.setName(name);
    options.setDisabled(isDisabled());
    appearance.render(sb, "text", s == null ? "" : s, options);
  }

  @Override
  public void setSize(XElement parent, int width, int height) {
    super.setSize(parent, width, height);
    getAppearance().onResize(parent, width, height);
  }

  private native void clearFocusKey() /*-{
		this.@com.google.gwt.cell.client.AbstractInputCell::focusedKey = null;
  }-*/;

}
