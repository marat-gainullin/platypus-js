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
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.GXTLogConfiguration;
import com.sencha.gxt.core.client.dom.XElement;

public class TextAreaInputCell extends ValueBaseInputCell<String> {

  public enum Resizable {
    NONE, VERTICAL, HORIZONTAL, BOTH;
  }

  public static interface TextAreaAppearance extends ValueBaseFieldAppearance {
    void onResize(XElement parent, int width, int height);

    void render(SafeHtmlBuilder sb, String value, FieldAppearanceOptions options);
  }

  public static class TextAreaCellOptions extends FieldAppearanceOptions {
    private Resizable resizable;

    public TextAreaCellOptions() {
      super();
    }

    public TextAreaCellOptions(int width, int height, boolean readonly) {
      super(width, height, readonly);
    }

    public TextAreaCellOptions(int width, int height, boolean readonly, String emptyText) {
      super(width, height, readonly, emptyText);
    }

    public Resizable getResizable() {
      return resizable;
    }

    public void setResizable(Resizable resizable) {
      this.resizable = resizable;
    }

  }

  private final TextAreaAppearance appearance;
  private boolean preventScrollbars;
  private Resizable resizable = Resizable.NONE;
  
  private static Logger logger = Logger.getLogger(TextAreaInputCell.class.getName());

  public TextAreaInputCell() {
    this(GWT.<TextAreaAppearance> create(TextAreaAppearance.class));
  }

  public TextAreaInputCell(TextAreaAppearance appearance) {
    super(appearance);
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
    super.finishEditing(parent, newValue, key, valueUpdater);
  }

  @Override
  public TextAreaAppearance getAppearance() {
    return appearance;
  }

  /**
   * Returns the resizable value.
   * 
   * @return the resize value
   */
  public Resizable getResizable() {
    return resizable;
  }

  /**
   * Returns true if scroll bars are disabled.
   * 
   * @return the scroll bar state
   */
  public boolean isPreventScrollbars() {
    return preventScrollbars;
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
      finishEditing(parent, value, key, valueUpdater);
    } else if ("keyup".equals(eventType)) {
      // Record keys as they are typed.
      FieldViewData vd = getViewData(key);
      if (vd == null) {
        vd = new FieldViewData(value);
        setViewData(key, vd);
      }
      vd.setCurrentValue(value);
    }
  }

  @Override
  public void render(Context context, String value, SafeHtmlBuilder sb) {
    ViewData viewData = checkViewData(context, value);
    String s = (viewData != null) ? viewData.getCurrentValue() : value;

    TextAreaCellOptions options = new TextAreaCellOptions(getWidth(), getHeight(), isReadOnly());
    options.setName(name);
    options.setEmptyText(getEmptyText());
    options.setResizable(resizable);
    options.setDisabled(isDisabled());

    appearance.render(sb, s == null ? "" : s, options);
  }

  /**
   * True to prevent scrollbars from appearing regardless of how much text is in
   * the field (equivalent to setting overflow: hidden, defaults to false.
   * 
   * @param preventScrollbars true to disable scroll bars
   */
  public void setPreventScrollbars(XElement parent, boolean preventScrollbars) {
    this.preventScrollbars = preventScrollbars;
    appearance.getInputElement(parent).getStyle().setOverflow(preventScrollbars ? Overflow.HIDDEN : Overflow.VISIBLE);
  }

  /**
   * Sets whether the field can be resized (defaults to NONE). This method uses
   * the CSS resize property which is only supported on browsers that support
   * CSS3.
   * 
   * @param parent the parent element
   * @param resizable the resizable value
   */
  public void setResizable(XElement parent, Resizable resizable) {
    this.resizable = resizable;
    XElement area = appearance.getInputElement(parent);
    area.getStyle().setProperty("resize", resizable.name().toLowerCase());
  }

  @Override
  public void setSize(XElement parent, int width, int height) {
    super.setSize(parent, width, height);
    getAppearance().onResize(parent, width, height);
  }

  @Override
  protected void onEnterKeyDown(com.google.gwt.cell.client.Cell.Context context, Element parent, String value,
      NativeEvent event, ValueUpdater<String> valueUpdater) {

  }

  @Override
  public int getCursorPos(XElement parent) {
    return impl.getTextAreaCursorPos(getInputElement(parent).<XElement> cast());
  }
}
