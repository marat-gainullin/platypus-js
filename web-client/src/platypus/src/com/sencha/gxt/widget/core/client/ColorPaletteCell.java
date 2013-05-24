/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client;

import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.cell.HandlerManagerContext;
import com.sencha.gxt.widget.core.client.event.CellSelectionEvent;

/**
 * A Cell Widget representing a palette of colors.
 * <p>
 * Note: the word <code>Cell</code> in the class name refers to a GWT cell, not
 * an individual cell in a color palette.
 * </p>
 */
public class ColorPaletteCell extends AbstractEditableCell<String, String> implements HasSelectionHandlers<String> {

  /**
   * The appearance of a color palette. The color palette consists of elements
   * that are arranged in a grid and can be selected using the keyboard or
   * mouse.
   */
  public static interface ColorPaletteAppearance {

    /**
     * Gets the color of the element above the specified element. The element is
     * specified by its parent and color.
     * 
     * @param parent the parent of the specified element
     * @param value the color of the specified element
     * @return the color of the element above the specified element
     */
    public String getAboveColor(XElement parent, String value);

    /**
     * Gets the color of the element below the specified element. The element is
     * specified by its parent and color.
     * 
     * @param parent the parent of the specified element
     * @param value the color of the specified element
     * @return the color of the element below the specified element
     */
    public String getBelowColor(XElement parent, String value);

    /**
     * Gets the child element with the specified color.
     * 
     * @param parent the parent of the child element
     * @param color the color
     * @return the child element with the specified color
     */
    public XElement getChildElement(XElement parent, String color);

    /**
     * Gets the color associated with the specified child element.
     * 
     * @param parent the parent
     * @param target the child
     * @return the color of the child element
     */
    public String getClickedColor(XElement parent, Element target);

    /**
     * Gets the color element associated with the specified child element.
     * 
     * @param parent the parent
     * @param target the child
     * @return the color element of the child element
     */
    public Element getColorElement(XElement parent, Element target);

    /**
     * Gets the list of color elements for the specified parent.
     * 
     * @param parent the parent
     * @return a list of color elements for the parent
     */
    public NodeList<Element> getColorElements(XElement parent);

    /**
     * Gets the color of the element to the left of the specified element. The
     * element is specified by its parent and color.
     * 
     * @param parent the parent of the specified element
     * @param value the color of the specified element
     * @return the color of the element to the left of the specified element
     */
    public String getLeftColor(XElement parent, String value);

    /**
     * Gets the color of the element to the right of the specified element. The
     * element is specified by its parent and color.
     * 
     * @param parent the parent of the specified element
     * @param value the color of the specified element
     * @return the color of the element to the right of the specified element
     */
    public String getRightColor(XElement parent, String value);

    /**
     * Modifies the appearance to indicate whether the specified child color
     * element is being hovered over.
     * 
     * @param parent the parent
     * @param target the child
     * @param entering true to indicate the hover has just started
     */
    public void hover(XElement parent, Element target, boolean entering);

    /**
     * Modifies the appearance to indicate that the mouse has moved off the
     * specified child color element.
     * 
     * @param parent the parent
     * @param target the child
     * @param event the browser event associated with mousing out
     */
    public void onMouseOut(XElement parent, Element target, NativeEvent event);

    /**
     * Modifies the appearance to indicate that the mouse has moved over the
     * specified child color element.
     * 
     * @param parent the parent
     * @param target the child
     * @param event the browser event associated with mousing over
     */
    public void onMouseOver(XElement parent, Element target, NativeEvent event);

    /**
     * Renders the appearance of a color palette cell as HTML into a
     * {@link SafeHtmlBuilder}, suitable for passing to
     * {@link Element#setInnerHTML(String)} on a container element.
     * 
     * @param context contains information about context of the element
     * @param value the color of the currently selected element
     * @param colors the colors, each consisting of a six digit hex value in
     *          RRGGBB format
     * @param labels the color names, in the same order as <code>colors</code>
     * @param builder receives the rendered appearance
     */
    public void render(Context context, String value, String[] colors, String[] labels, SafeHtmlBuilder builder);

  }

  protected ColorPaletteAppearance appearance;

  private String[] colors = new String[] {
      "E9967A", "B22222", "FFB6C1", "DB7093", "FF6347", "FFD700", "FFFACD", "FFDAB9", "BDB76B", "EE82EE", "FF00FF",
      "9400D3", "800080", "7B68EE", "7CFC00", "90EE90", "3CB371", "006400", "808000", "20B2AA", "00FFFF", "7FFFD4",
      "5F9EA0", "B0E0E6", "00BFFF", "4169E1", "000080", "FFEBCD", "DEB887", "F4A460", "D2691E", "A52A2A", "F0FFF0",
      "F0F8FF", "F5F5DC", "FFFFF0", "FFE4E1", "C0C0C0", "778899", "000000"};

  private boolean editing;

  private String[] labels = new String[] {
      "Dark Salmon", "Fire Brick", "Light Pink", "Pale Violet Red", "Tomato", "Gold", "Lemon Chiffon", "Peach Puff",
      "Dark Khaki", "Violet", "Magenta", "Dark Violet", "Purple", "Medium Slate Blue", "Lawn Green", "Light Green",
      "Medium Sea Green", "Dark Green", "Olive", "Light Sea Green", "Cyan", "Aquamarine", "Cadet Blue", "Powder Blue",
      "Deep Sky Blue", "Royal Blue", "Navy", "Blanched Almond", "Burly Wood", "Sandy Brown", "Chocolate", "Brown",
      "Honeydew", "Alice Blue", "Beige", "Ivory", "Misty Rose", "Silver", "Light Slate Gray", "Black"};

  private HandlerManager handlerManager;

  /**
   * Creates a color palette cell with a default set of colors.
   */
  public ColorPaletteCell() {
    this(null, null);
  }

  /**
   * Creates a color palette cell with the specified appearance.
   * 
   * @param appearance the color palette appearance
   */
  public ColorPaletteCell(ColorPaletteAppearance appearance) {
    this(appearance, null, null);
  }

  /**
   * Creates a color palette cell with the specified appearance, colors and
   * labels.
   * 
   * @param appearance the color palette appearance
   * @param colors the colors, each consisting of a six digit hex value in
   *          RRGGBB format
   * @param labels the color names, in the same order as <code>colors</code>
   */
  public ColorPaletteCell(ColorPaletteAppearance appearance, String[] colors, String[] labels) {
    super("click", "mouseover", "mouseout", "focus", "blur");
    this.appearance = appearance;
    if (colors != null) {
      this.colors = colors;
    }
    if (labels != null) {
      this.labels = labels;
    }
  }

  /**
   * Creates a new color palette.
   * 
   * @param colors the colors, each consisting of a six digit hex value in
   *          RRGGBB format
   * @param labels the color names, in the same order as <code>colors</code>
   */
  public ColorPaletteCell(String[] colors, String[] labels) {
    this(GWT.<ColorPaletteAppearance> create(ColorPaletteAppearance.class), colors, labels);
  }

  @Override
  public HandlerRegistration addSelectionHandler(SelectionHandler<String> handler) {
    return ensureHandlers().addHandler(SelectionEvent.getType(), handler);
  }

  @Override
  public void fireEvent(GwtEvent<?> event) {
    ensureHandlers().fireEvent(event);
  }

  /**
   * Returns the colors.
   * 
   * @return the colors
   */
  public String[] getColors() {
    return colors;
  }

  /**
   * Returns the labels.
   * 
   * @return the labels
   */
  public String[] getLabels() {
    return labels;
  }

  @Override
  public boolean isEditing(Context context, Element parent, String value) {
    return editing;
  }

  @Override
  public void onBrowserEvent(Context context, Element parent, String value, NativeEvent event,
      ValueUpdater<String> valueUpdater) {
    Element target = event.getEventTarget().cast();
    if (!parent.isOrHasChild(target)) {
      return;
    }
    super.onBrowserEvent(context, parent, value, event, valueUpdater);
    String type = event.getType();
    XElement xParent = parent.<XElement> cast();
    if ("click".equals(type)) {
      onClick(context, xParent, value, event, valueUpdater);
    } else if ("mouseover".equals(type)) {
      onMouseOver(context, xParent, value, event, valueUpdater);
    } else if ("mouseout".equals(type)) {
      onMouseOut(context, xParent, value, event, valueUpdater);
    } else if ("focus".equals(type)) {
      onFocus(context, xParent, value, event, valueUpdater);
    } else if ("blur".equals(type)) {
      onBlur(context, xParent, value, event, valueUpdater);
    }

  }

  @Override
  public void render(Context context, String value, SafeHtmlBuilder sb) {
    appearance.render(context, value, colors, labels, sb);
  }

  /**
   * Selects the color.
   * 
   * @param parent the parent of the color element to select
   * @param context information about the context of the cell
   * @param color the color of the element to select
   * @param value the currently selected value
   * @param valueUpdater the cell's value updater which will receive the new
   *          value
   */
  public void select(XElement parent, Context context, String color, String value, ValueUpdater<String> valueUpdater) {
    select(parent, context, color, value, valueUpdater, false);
  }

  /**
   * Selects the color.
   * 
   * @param parent the parent of the color element to select
   * @param context information about the context of the cell
   * @param newValue the color of the element to select
   * @param currentValue the current cell value
   * @param valueUpdater the cell's value updater which will receive the new
   *          value
   * @param suppressEvent true to suppress the select event
   */
  public void select(XElement parent, Context context, String newValue, String currentValue,
      ValueUpdater<String> valueUpdater, boolean suppressEvent) {

    newValue = newValue.replace("#", "");

    setValue(context, parent, newValue);
    valueUpdater.update(newValue);

    if (!suppressEvent) {
      if (context instanceof HandlerManagerContext) {
        HandlerManager manager = ((HandlerManagerContext) context).getHandlerManager();
        CellSelectionEvent.fire(manager, context, newValue);
      } else {
        CellSelectionEvent.fire(this, context, newValue);
      }
    }
  }

  /**
   * Creates the {@link HandlerManager} used by this Widget. You can override
   * this method to create a custom {@link HandlerManager}.
   * 
   * @return the {@link HandlerManager} you want to use
   */
  protected HandlerManager createHandlerManager() {
    return new HandlerManager(this);
  }

  protected void onBlur(Context context, XElement parent, String currentValue, NativeEvent event,
      ValueUpdater<String> valueUpdater) {
    editing = false;
  }

  protected void onClick(Context context, XElement parent, String currentValue, NativeEvent event,
      ValueUpdater<String> valueUpdater) {
    event.preventDefault();
    // if (!disabled) {

    EventTarget from = event.getEventTarget();
    if (from != null && Element.is(from)) {
      String color = appearance.getClickedColor(parent, Element.as(from));
      if (color != null) {
        select(parent, context, color, currentValue, valueUpdater);
      }
    }
    // }
  }

  protected void onFocus(Context context, XElement parent, String currentValue, NativeEvent event,
      ValueUpdater<String> valueUpdater) {
    editing = true;
  }

  protected void onKeyDown(Context context, XElement parent, String currentValue, NativeEvent event,
      ValueUpdater<String> valueUpdater) {
    String color = appearance.getBelowColor(parent, currentValue);
    if (color != null) {
      select(parent, context, color, currentValue, valueUpdater, true);
    }
  }

  protected void onKeyLeft(Context context, XElement parent, String currentValue, NativeEvent event,
      ValueUpdater<String> valueUpdater) {
    String color = appearance.getLeftColor(parent, currentValue);
    if (color != null) {
      select(parent, context, color, currentValue, valueUpdater, true);
    }
  }

  protected void onKeyRight(Context context, XElement parent, String currentValue, NativeEvent event,
      ValueUpdater<String> valueUpdater) {
    String color = appearance.getRightColor(parent, currentValue);
    if (color != null) {
      select(parent, context, color, currentValue, valueUpdater, true);
    }
  }

  protected void onKeyUp(Context context, XElement parent, String currentValue, NativeEvent event,
      ValueUpdater<String> valueUpdater) {
    String color = appearance.getAboveColor(parent, currentValue);
    if (color != null) {
      select(parent, context, color, currentValue, valueUpdater, true);
    }
  }

  protected void onMouseOut(Context context, XElement parent, String currentValue, NativeEvent event,
      ValueUpdater<String> valueUpdater) {
    EventTarget from = event.getEventTarget();
    if (from != null && Element.is(from)) {
      appearance.onMouseOut(parent, Element.as(from), event);
    }
  }

  protected void onMouseOver(Context context, XElement parent, String currentValue, NativeEvent event,
      ValueUpdater<String> valueUpdater) {
    EventTarget from = event.getEventTarget();
    if (from != null && Element.is(from)) {
      appearance.onMouseOver(parent, Element.as(from), event);
    }
  }

  /**
   * Ensures the existence of the handler manager.
   * 
   * @return the handler manager
   * */
  protected HandlerManager ensureHandlers() {
    return handlerManager == null ? handlerManager = createHandlerManager() : handlerManager;
  }

}
