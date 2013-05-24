/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.cell.core.client.form;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Event;
import com.sencha.gxt.cell.core.client.AbstractEventInputCell;
import com.sencha.gxt.cell.core.client.ResizableCell;
import com.sencha.gxt.cell.core.client.form.FieldCell.FieldViewData;
import com.sencha.gxt.core.client.GXTLogConfiguration;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.KeyNav;
import com.sencha.gxt.widget.core.client.event.BlurEvent;
import com.sencha.gxt.widget.core.client.event.FocusEvent;

public abstract class FieldCell<T> extends AbstractEventInputCell<T, FieldViewData> implements ResizableCell {

  public static interface FieldAppearance {

    void onEmpty(Element parent, boolean empty);

    void onFocus(Element parent, boolean focus);

    void onValid(Element parent, boolean valid);

    void setReadOnly(Element parent, boolean readonly);

  }

  @Override
  public boolean handlesSelection() {
    return true;
  }

  public static class FieldAppearanceOptions {
    private int width;
    private int height;
    private boolean readonly;
    private String emptyText;
    private String name = "";
    private boolean hideTrigger;
    private boolean editable = true;
    private boolean disabled;

    public FieldAppearanceOptions() {

    }

    public FieldAppearanceOptions(int width, int height, boolean readonly) {
      this.readonly = readonly;
      this.width = width;
      this.height = height;
    }

    public FieldAppearanceOptions(int width, int height, boolean readonly, String emptyText) {
      this(width, height, readonly);
      this.emptyText = emptyText;
    }

    public String getEmptyText() {
      return emptyText;
    }

    public int getHeight() {
      return height;
    }

    public String getName() {
      return name;
    }

    public int getWidth() {
      return width;
    }

    public boolean isDisabled() {
      return disabled;
    }

    public boolean isEditable() {
      return editable;
    }

    public boolean isHideTrigger() {
      return hideTrigger;
    }

    public boolean isReadonly() {
      return readonly;
    }

    public void setDisabled(boolean disabled) {
      this.disabled = disabled;
    }

    public void setEditable(boolean editable) {
      this.editable = editable;
    }

    public void setEmptyText(String emptyText) {
      this.emptyText = emptyText;
    }

    public void setHeight(int height) {
      this.height = height;
    }

    public void setHideTrigger(boolean hideTrigger) {
      this.hideTrigger = hideTrigger;
    }

    public void setName(String name) {
      this.name = name;
    }

    public void setReadonly(boolean readonly) {
      this.readonly = readonly;
    }

    public void setWidth(int width) {
      this.width = width;
    }

  }

  public static class FieldViewData extends ViewData {

    public FieldViewData(String value) {
      super(value);
    }

  }

  /**
   * Get the events consumed by the input cell.
   * 
   * @param userEvents the events consumed by the subclass
   * @return the events
   */
  private static Set<String> getConsumedEventsImpl(Set<String> userEvents) {
    Set<String> events = new HashSet<String>();
    events.add("click");
    events.add("keyup");
    events.add("keypress");
    events.add("focus");
    events.add("blur");
    events.add("mouseover");
    events.add("mouseout");
    events.add("mousedown");
    events.add("mouseup");
    events.add("change");

    if (userEvents != null && userEvents.size() > 0) {
      events.addAll(userEvents);
    }
    return events;
  }

  /**
   * Get the events consumed by the input cell.
   * 
   * @param userEvents the events consumed by the subclass
   * @return the events
   */
  private static Set<String> getConsumedEventsImpl(String... consumedEvents) {
    Set<String> userEvents = new HashSet<String>();
    if (consumedEvents != null) {
      for (String event : consumedEvents) {
        userEvents.add(event);
      }
    }
    return getConsumedEventsImpl(userEvents);
  }

  protected final FieldAppearance appearance;
  protected int height = -1;
  protected Context lastContext;
  protected XElement lastParent;
  protected T lastValue;
  protected ValueUpdater<T> lastValueUpdater;
  protected int width = -1;
  protected Context focusContext;
  private boolean readOnly;
  private boolean disabled;

  private static Logger logger = Logger.getLogger(FieldCell.class.getName());

  public FieldCell(FieldAppearance appearance, Set<String> consumedEvents) {
    super(getConsumedEventsImpl(consumedEvents));
    this.appearance = appearance;
  }

  public FieldCell(FieldAppearance appearance, String... consumedEvents) {
    super(getConsumedEventsImpl(consumedEvents));
    this.appearance = appearance;
  }

  /**
   * Disables the cell.
   * 
   * @param parent the parent element
   */
  public void disable(XElement parent) {
    this.disabled = true;
  }

  /**
   * Enables the cell.
   * 
   * @param parent the parent element
   */
  public void enable(XElement parent) {
    this.disabled = false;
  }

  @Override
  public void finishEditing(Element parent, T value, Object key, ValueUpdater<T> valueUpdater) {
    clearViewData(key);
    super.finishEditing(parent, value, key, valueUpdater);
  }

  @Override
  public int getHeight() {
    return height;
  }

  @Override
  public int getWidth() {
    return width;
  }

  /**
   * Returns the cell's disabled state.
   * 
   * @return he disabled state
   */
  public boolean isDisabled() {
    return disabled;
  }

  /**
   * Returns the cell's read only state.
   * 
   * @return the read only state
   */
  public boolean isReadOnly() {
    return readOnly;
  }

  public void onBrowserEvent(Context context, Element parent, T value, NativeEvent event, ValueUpdater<T> valueUpdater) {
    Element target = event.getEventTarget().cast();
    if (!parent.isOrHasChild(target)) {
      return;
    }

    String eventType = event.getType();

    if ((isReadOnly() || isDisabled()) && !("blur".equals(eventType) || "focus".equals(eventType))) {
      return;
    }

    super.onBrowserEvent(context, parent, value, event, valueUpdater);

    XElement x = parent.cast();

    if ("keydown".equals(eventType)) {
      onKeyDown(context, parent, value, event, valueUpdater);

      if (KeyNav.getKeyEvent() == Event.ONKEYDOWN) {
        onNavigationKey(context, parent, value, event, valueUpdater);
      }

    } else if ("keyup".equals(eventType)) {
      onKeyUp(context, parent, value, event, valueUpdater);
    } else if ("keypress".equals(eventType)) {
      onKeyPress(context, parent, value, event, valueUpdater);
      if (KeyNav.getKeyEvent() == Event.ONKEYPRESS) {
        onNavigationKey(context, parent, value, event, valueUpdater);
      }
    } else if ("blur".equals(eventType)) {
      onBlur(context, x, value, event, valueUpdater);
    } else if ("focus".equals(eventType)) {
      onFocus(context, x, value, event, valueUpdater);
    } else if ("mouseover".equals(eventType)) {
      onMouseOver(x, event);
    } else if ("mouseout".equals(eventType)) {
      onMouseOut(x, event);
    } else if ("mousedown".equals(eventType)) {
      onMouseDown(x, event);
    } else if ("click".equals(eventType)) {
      onClick(x, event);
    }
  }

  protected void onNavigationKey(Context context, Element parent, T value, NativeEvent event,
      ValueUpdater<T> valueUpdater) {
  }

  public abstract void onEmpty(XElement parent, boolean empty);

  public void onValid(XElement parent, boolean valid) {
    appearance.onValid(parent, valid);
  }

  @Override
  public boolean redrawOnResize() {
    return false;
  }

  @Override
  public void setHeight(int height) {
    this.height = height;
  }

  public void setName(XElement parent, String name) {
    getInputElement(parent).setAttribute("name", name);
  }

  public void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
  }

  @Override
  public void setSize(int width, int height) {
    this.width = width;
    this.height = height;
  }

  public void setSize(XElement parent, int width, int height) {
    this.width = width;
    this.height = height;
  }

  @Override
  public void setWidth(int width) {
    this.width = width;
  }

  protected FieldViewData checkViewData(Context context, String value) {
    Object key = context.getKey();
    FieldViewData viewData = getViewData(key);
    if (viewData != null && viewData.getCurrentValue().equals(value)) {
      clearViewData(key);
      viewData = null;
    }
    return viewData;
  }

  protected void clearContext() {
    lastContext = null;
    lastParent = null;
    lastValueUpdater = null;
    lastValue = null;
  }

  protected FieldViewData ensureViewData(Context context, XElement parent) {
    Object key = context.getKey();
    FieldViewData viewData = getViewData(key);
    if (viewData == null) {
      viewData = new FieldViewData(((InputElement) getInputElement(parent).cast()).getValue());
      setViewData(key, viewData);
    }
    return viewData;
  }

  protected boolean hasFocus(Context context, XElement parent) {
    return focusContext != null;
  }

  protected void onBlur(Context context, XElement parent, T value, NativeEvent event, ValueUpdater<T> valueUpdateer) {
    if (GXTLogConfiguration.loggingIsEnabled()) {
      logger.finest("onBlur firing blur event");
    }

    clearViewData(context.getKey());
    focusContext = null;
    clearContext();
    appearance.onFocus(parent, false);
    fireEvent(context, new BlurEvent());
  }

  protected void onClick(XElement x, NativeEvent event) {

  }

  protected void onFocus(Context context, XElement parent, T value, NativeEvent event, ValueUpdater<T> valueUpdater) {
    if (GXTLogConfiguration.loggingIsEnabled()) {
      logger.finest("onFocus firing focus event " + parent.getId());
    }

    focusContext = context;
    appearance.onFocus(parent, true);
    fireEvent(context, new FocusEvent());
  }

  protected void onKeyDown(Context context, Element parent, T value, NativeEvent event, ValueUpdater<T> valueUpdater) {

  }

  protected void onKeyPress(Context context, Element parent, T value, NativeEvent event, ValueUpdater<T> valueUpdater) {

  }

  protected void onKeyUp(Context context, Element parent, T value, NativeEvent event, ValueUpdater<T> valueUpdater) {

  }

  protected void onMouseDown(XElement parent, NativeEvent event) {

  }

  protected void onMouseOut(XElement parent, NativeEvent event) {

  }

  protected void onMouseOver(XElement parent, NativeEvent event) {

  }

  protected void onMouseUp(XElement parent, NativeEvent event) {

  }

  protected void saveContext(Context context, Element parent, NativeEvent event, ValueUpdater<T> valueUpdater, T value) {
    if (GXTLogConfiguration.loggingIsEnabled()) {
      logger.finest("saveContext");
    }

    lastContext = context;
    lastParent = parent.cast();
    lastValueUpdater = valueUpdater;
    lastValue = value;
  }

}
