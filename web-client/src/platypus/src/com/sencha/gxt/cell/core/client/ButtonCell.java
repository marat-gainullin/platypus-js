/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.cell.core.client;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.HasSafeHtml;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.HasHTML;
import com.sencha.gxt.core.client.Style.Anchor;
import com.sencha.gxt.core.client.Style.AnchorAlignment;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.KeyNav;
import com.sencha.gxt.widget.core.client.HasIcon;
import com.sencha.gxt.widget.core.client.event.ArrowSelectEvent;
import com.sencha.gxt.widget.core.client.event.ArrowSelectEvent.ArrowSelectHandler;
import com.sencha.gxt.widget.core.client.event.ArrowSelectEvent.HasArrowSelectHandlers;
import com.sencha.gxt.widget.core.client.event.BeforeSelectEvent;
import com.sencha.gxt.widget.core.client.event.BeforeSelectEvent.BeforeSelectHandler;
import com.sencha.gxt.widget.core.client.event.BeforeSelectEvent.HasBeforeSelectHandlers;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.HasSelectHandlers;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.menu.Menu;

public class ButtonCell<C> extends ResizeCell<C> implements HasBeforeSelectHandlers, HasSelectHandlers,
    HasArrowSelectHandlers, HasHTML, HasIcon, HasSafeHtml, FocusableCell, DisableCell {

  /**
   * Button arrow alignment enum.
   */
  public enum ButtonArrowAlign {
    /**
     * Arrow is aligned to the <b>right</b>
     */
    RIGHT,
    /**
     * Arrow is aligned to the <b>bottom</b>
     */
    BOTTOM;
  }

  public interface ButtonCellAppearance<C> {

    XElement getButtonElement(XElement parent);
    
    XElement getFocusElement(XElement parent);

    void onFocus(XElement parent, boolean focused, NativeEvent event);

    void onOver(XElement parent, boolean over, NativeEvent event);

    void onPress(XElement parent, boolean pressed, NativeEvent event);

    void onToggle(XElement parent, boolean pressed);

    void render(ButtonCell<C> cell, Context context, C value, SafeHtmlBuilder sb);

  }

  /**
   * ButtonScale enum.
   */
  public enum ButtonScale {
    NONE, SMALL, MEDIUM, LARGE;
  }

  /**
   * Icon alignment enum.
   */
  public enum IconAlign {
    /**
     * Icons are aligned to the <b>right</b>.
     */
    RIGHT,
    /**
     * Icons are aligned to the <b>bottom</b>.
     */
    BOTTOM,
    /**
     * Icons are aligned to the <b>top</b>.
     */
    TOP,
    /**
     * Icons are aligned to the <b>left</b>.
     */
    LEFT;
  }

  private class UnpushHandler implements NativePreviewHandler {

    private final XElement parent;
    private final HandlerRegistration reg;

    public UnpushHandler(XElement parent) {
      this.parent = parent;
      this.reg = Event.addNativePreviewHandler(this);
    }

    public void onPreviewNativeEvent(NativePreviewEvent event) {
      if ("mouseup".equals(event.getNativeEvent().getType())) {
        // Unregister self.
        reg.removeHandler();

        // Unpush the element.
        appearance.onOver(parent, false, event.getNativeEvent());
        appearance.onPress(parent, false, event.getNativeEvent());
      }
    }
  }

  protected ImageResource icon;
  protected Menu menu;
  protected SafeHtml text = SafeHtmlUtils.EMPTY_SAFE_HTML;
  protected final ButtonCellAppearance<C> appearance;

  private IconAlign iconAlign = IconAlign.LEFT;
  private ButtonArrowAlign arrowAlign = ButtonArrowAlign.RIGHT;
  private ButtonScale scale = ButtonScale.SMALL;
  private AnchorAlignment menuAlign = new AnchorAlignment(Anchor.TOP_LEFT, Anchor.BOTTOM_LEFT, true);
  private boolean handleMouseEvents = true;
  private int minWidth = -1;

  public ButtonCell() {
    this(GWT.<ButtonCellAppearance<C>> create(ButtonCellAppearance.class));
  }

  public ButtonCell(ButtonCellAppearance<C> appearance) {
    super("click", "keydown", "mousedown", "mouseup", "mouseover", "mouseout", "focus", "blur");
    this.appearance = appearance;
  }

  @Override
  public HandlerRegistration addArrowSelectHandler(ArrowSelectHandler handler) {
    return addHandler(handler, ArrowSelectEvent.getType());
  }

  @Override
  public HandlerRegistration addBeforeSelectHandler(BeforeSelectHandler handler) {
    return addHandler(handler, BeforeSelectEvent.getType());
  }

  @Override
  public HandlerRegistration addSelectHandler(SelectHandler handler) {
    return addHandler(handler, SelectEvent.getType());
  }

  @Override
  public void disable(com.google.gwt.cell.client.Cell.Context context, Element parent) {
    appearance.onOver(parent.<XElement>cast(), false, null);
    appearance.onFocus(parent.<XElement>cast(), false, null);
  }

  @Override
  public void enable(com.google.gwt.cell.client.Cell.Context context, Element parent) {
    appearance.onOver(parent.<XElement>cast(), false, null);
  }

  /**
   * Returns the button's appearance.
   * 
   * @return the apperance
   */
  public ButtonCellAppearance<C> getAppearance() {
    return appearance;
  }

  /**
   * Returns the button's arrow alignment.
   * 
   * @return the arrow alignment
   */
  public ButtonArrowAlign getArrowAlign() {
    return arrowAlign;
  }

  @Override
  public XElement getFocusElement(XElement parent) {
    return appearance.getFocusElement(parent);
  }

  @Override
  public String getHTML() {
    return text.asString();
  }

  @Override
  public ImageResource getIcon() {
    return icon;
  }

  /**
   * Returns the button's icon alignment.
   * 
   * @return the icon alignment
   */
  public IconAlign getIconAlign() {
    return iconAlign;
  }

  /**
   * Returns the button's menu (if it has one).
   * 
   * @return the menu
   */
  public Menu getMenu() {
    return menu;
  }

  /**
   * Returns the button's menu alignment.
   * 
   * @return the menu alignment
   */
  public AnchorAlignment getMenuAlign() {
    return menuAlign;
  }

  /**
   * Returns the button's minimum width.
   * 
   * @return the minWidth the minimum width
   */
  public int getMinWidth() {
    return minWidth;
  }

  /**
   * Returns false if mouse over effect is disabled.
   * 
   * @return false if mouse effects disabled
   */
  public boolean getMouseEvents() {
    return handleMouseEvents;
  }

  /**
   * Returns the button's scale.
   * 
   * @return the button scale
   */
  public ButtonScale getScale() {
    return scale;
  }

  @Override
  public String getText() {
    return text.asString();
  }

  /**
   * Hide this button's menu (if it has one).
   */
  public void hideMenu() {
    if (menu != null) {
      menu.hide();
    }
  }

  @Override
  public void onBrowserEvent(Cell.Context context, Element parent, C value, NativeEvent event,
      ValueUpdater<C> valueUpdater) {
    Element target = event.getEventTarget().cast();
    // ignore the parent element
    if (isDisableEvents() || !parent.getFirstChildElement().isOrHasChild(target)) {
      return;
    }

    XElement p = parent.cast();

    String eventType = event.getType();
    if ("click".equals(eventType)) {
      onClick(context, p, value, event, valueUpdater);
    } else if ("mouseover".equals(eventType)) {
      onMouseOver(p, event);
    } else if ("mouseout".equals(eventType)) {
      onMouseOut(p, event);
    } else if ("mousedown".equals(eventType)) {
      onMouseDown(p, event);
    } else if ("mouseup".equals(eventType)) {
      onMouseUp(p, event);
    } else if ("focus".equals(eventType)) {
      onFocus(p, event);
    } else if ("blur".equals(eventType)) {
      onBlur(p, event);
    } else if ("keydown".equals(eventType)) {
      if (KeyNav.getKeyEvent() == Event.ONKEYDOWN) {
        onNavigationKey(context, parent, value, event, valueUpdater);
      }
    } else if ("keypress".equals(eventType)) {
      if (KeyNav.getKeyEvent() == Event.ONKEYPRESS) {
        onNavigationKey(context, parent, value, event, valueUpdater);
      }
    }
  }

  @Override
  public boolean redrawOnResize() {
    return true;
  }

  @Override
  public void render(Context context, C value, SafeHtmlBuilder sb) {
    appearance.render(this, context, value, sb);
  }

  /**
   * Sets the arrow alignment (defaults to RIGHT).
   * 
   * @param arrowAlign the arrow alignment
   */
  public void setArrowAlign(ButtonArrowAlign arrowAlign) {
    this.arrowAlign = arrowAlign;
  }

  @Override
  public void setHTML(SafeHtml html) {
    this.text = html;
  }

  @Override
  public void setHTML(String html) {
    setHTML(SafeHtmlUtils.fromTrustedString(html));
  }

  @Override
  public void setIcon(ImageResource icon) {
    this.icon = icon;
  }

  /**
   * Sets the icon alignment (defaults to LEFT).
   * 
   * @param iconAlign the icon alignment
   */
  public void setIconAlign(IconAlign iconAlign) {
    this.iconAlign = iconAlign;
  }

  /**
   * Sets the button's menu.
   * 
   * @param menu the menu
   */
  public void setMenu(Menu menu) {
    this.menu = menu;
  }

  /**
   * Sets the position to align the menu to, see {@link XElement#alignTo} for
   * more details (defaults to 'tl-bl?', pre-render).
   * 
   * @param menuAlign the menu alignment
   */
  public void setMenuAlign(AnchorAlignment menuAlign) {
    this.menuAlign = menuAlign;
  }

  /**
   * Sets he minimum width for this button (used to give a set of buttons a
   * common width)
   * 
   * @param minWidth the minimum width
   */
  public void setMinWidth(int minWidth) {
    this.minWidth = minWidth;
  }

  /**
   * False to disable visual cues on mouseover, mouseout and mousedown (defaults
   * to true).
   * 
   * @param handleMouseEvents false to disable mouse over changes
   */
  public void setMouseEvents(boolean handleMouseEvents) {
    this.handleMouseEvents = handleMouseEvents;
  }

  /**
   * Sets the button's scale.
   * 
   * @param scale the button scale
   */
  public void setScale(ButtonScale scale) {
    this.scale = scale;
  }

  @Override
  public void setText(String text) {
    setHTML(SafeHtmlUtils.fromString(text));
  }

  /**
   * Show this button's menu (if it has one).
   * 
   * @param target the element to align to
   */
  public void showMenu(Element target) {
    menu.show(target, menuAlign);
  }

  protected void onBlur(XElement p, NativeEvent event) {
    appearance.onFocus(p, false, event);
  }

  protected void onClick(Context context, XElement p, C value, NativeEvent event, ValueUpdater<C> valueUpdater) {
    if (!isDisableEvents() && fireCancellableEvent(context, new BeforeSelectEvent(context))) {
      if (menu != null) {
        showMenu(p);
      }
      appearance.onOver(p, false, null);
      fireEvent(context, new SelectEvent(context));
    }
  }

  protected void onFocus(XElement p, NativeEvent event) {
    appearance.onFocus(p, true, event);
  }

  protected void onMouseDown(XElement parent, NativeEvent event) {
    if (handleMouseEvents) {
      Element target = event.getEventTarget().cast();
      // stop images from being dragged in firefox
      if ("IMG".equals(target.getTagName())) {
        event.preventDefault();
      }
      appearance.onPress(parent, true, event);

      new UnpushHandler(parent);
    }
  }

  protected void onMouseOut(XElement p, NativeEvent event) {
    appearance.onOver(p, false, event);
  }

  protected void onMouseOver(XElement p, NativeEvent event) {
    appearance.onOver(p, true, event);
  }

  protected void onMouseUp(XElement p, NativeEvent event) {
    appearance.onPress(p, false, event);
  }

  protected void onNavigationKey(com.google.gwt.cell.client.Cell.Context context, Element parent, C value,
      NativeEvent event, ValueUpdater<C> valueUpdater) {
    int key = event.getKeyCode();
    
    if (key == KeyCodes.KEY_ENTER || key == 32) {
      onClick(context, parent.<XElement>cast(), value, event, valueUpdater);
    }
  }
}
