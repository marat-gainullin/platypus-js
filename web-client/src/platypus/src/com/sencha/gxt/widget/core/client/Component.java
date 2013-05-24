/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client;

import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.Style;
import com.sencha.gxt.core.client.Style.HideMode;
import com.sencha.gxt.core.client.Style.Side;
import com.sencha.gxt.core.client.dom.Layer;
import com.sencha.gxt.core.client.dom.Layer.ShadowPosition;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.resources.CommonStyles;
import com.sencha.gxt.core.client.util.DelayedTask;
import com.sencha.gxt.core.client.util.Point;
import com.sencha.gxt.core.client.util.Rectangle;
import com.sencha.gxt.core.client.util.Size;
import com.sencha.gxt.core.client.util.Util;
import com.sencha.gxt.core.shared.FastMap;
import com.sencha.gxt.core.shared.event.CancellableEvent;
import com.sencha.gxt.widget.core.client.event.BeforeHideEvent;
import com.sencha.gxt.widget.core.client.event.BeforeHideEvent.BeforeHideHandler;
import com.sencha.gxt.widget.core.client.event.BeforeHideEvent.HasBeforeHideHandlers;
import com.sencha.gxt.widget.core.client.event.BeforeShowContextMenuEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowContextMenuEvent.BeforeShowContextMenuHandler;
import com.sencha.gxt.widget.core.client.event.BeforeShowContextMenuEvent.HasBeforeShowContextMenuHandler;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent.BeforeShowHandler;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent.HasBeforeShowHandlers;
import com.sencha.gxt.widget.core.client.event.BlurEvent;
import com.sencha.gxt.widget.core.client.event.BlurEvent.BlurHandler;
import com.sencha.gxt.widget.core.client.event.BlurEvent.HasBlurHandlers;
import com.sencha.gxt.widget.core.client.event.DisableEvent;
import com.sencha.gxt.widget.core.client.event.DisableEvent.DisableHandler;
import com.sencha.gxt.widget.core.client.event.DisableEvent.HasDisableHandlers;
import com.sencha.gxt.widget.core.client.event.EnableEvent;
import com.sencha.gxt.widget.core.client.event.EnableEvent.EnableHandler;
import com.sencha.gxt.widget.core.client.event.EnableEvent.HasEnableHandlers;
import com.sencha.gxt.widget.core.client.event.FocusEvent;
import com.sencha.gxt.widget.core.client.event.FocusEvent.FocusHandler;
import com.sencha.gxt.widget.core.client.event.FocusEvent.HasFocusHandlers;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HasHideHandlers;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.MoveEvent;
import com.sencha.gxt.widget.core.client.event.MoveEvent.HasMoveHandlers;
import com.sencha.gxt.widget.core.client.event.MoveEvent.MoveHandler;
import com.sencha.gxt.widget.core.client.event.ShowContextMenuEvent;
import com.sencha.gxt.widget.core.client.event.ShowContextMenuEvent.HasShowContextMenuHandler;
import com.sencha.gxt.widget.core.client.event.ShowContextMenuEvent.ShowContextMenuHandler;
import com.sencha.gxt.widget.core.client.event.ShowEvent;
import com.sencha.gxt.widget.core.client.event.ShowEvent.HasShowHandlers;
import com.sencha.gxt.widget.core.client.event.ShowEvent.ShowHandler;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.tips.ToolTip;
import com.sencha.gxt.widget.core.client.tips.ToolTipConfig;

/**
 * Base class for all GXT widgets.
 */
public class Component extends Widget implements HasFocusHandlers, HasBlurHandlers, HasBeforeHideHandlers,
    HasHideHandlers, HasBeforeShowHandlers, HasShowHandlers, HasEnableHandlers, HasDisableHandlers,
    HasBeforeShowContextMenuHandler, HasShowContextMenuHandler, HasMoveHandlers, HasResizeHandlers, HasItemId,
    HasFocusSupport, HasEnabled {

  private static int componentId = 0;

  static {
    GXT.init();
  }

  /**
   * True to adjust sizes for box model issues to ensure actual size matches set
   * size.
   */
  protected boolean adjustSize = true;

  /**
   * True to cache size calculation (defaults to true) for better performance.
   */
  protected boolean cacheSizes = true;

  /**
   * True if the widget is disabled. Read only.
   */
  protected boolean disabled;

  /**
   * The style used when a widget is disabled (defaults to
   * CommonStyles.get().disabled()).
   */
  protected String disabledStyle = CommonStyles.get().disabled();

  protected boolean allowTextSelection = true;

  /**
   * Set this to true if you have sizing issues in initial collapsed or hidden
   * items. It defaults to false for performance reasons. You should not set
   * this to true for all components. If this is enabled than components are
   * made visible for the browser during a call to setSize if they were hidden
   * or collapsed. The user wont see this change. In the end of setSize the
   * status of the widget is reverted again to the normal state. (defaults to
   * false)
   */
  protected boolean ensureVisibilityOnSizing;

  /**
   * True if this widget is hidden. Read only.
   */
  protected boolean hidden;

  /**
   * The size of the widget that last time it was changed.
   */
  protected Size lastSize;
  protected String height;
  protected Layer layer;
  protected int left = Style.DEFAULT, top = Style.DEFAULT;
  protected boolean mask;
  protected String maskMessage;
  protected boolean monitorWindowResize;
  protected int pageX = Style.DEFAULT, pageY = Style.DEFAULT;
  protected HandlerRegistration resizeHandler;

  /**
   * True to enable a shim which uses a transparent iframe to stop content from
   * bleeding through.
   */
  protected boolean shim;
  protected ToolTip toolTip;
  protected ToolTipConfig toolTipConfig;
  protected boolean disableContextMenu = false;

  protected String width;
  protected int windowResizeDelay = !GWT.isScript() ? 100 : 0;
  protected DelayedTask windowResizeTask;
  private Menu contextMenu;
  private Map<String, Object> dataMap;
  private boolean deferHeight;
  private boolean disableEvents;
  private FocusManagerSupport focusManagerSupport;
  private HideMode hideMode = HideMode.DISPLAY;
  private String itemId;
  private Map<String, String> overElements;

  private boolean shadow;
  private ShadowPosition shadowPosition = ShadowPosition.SIDES;
  private boolean stateful;
  private String stateId;
  protected int tabIndex;

  protected Component() {

  }

  public HandlerRegistration addBeforeHideHandler(BeforeHideHandler handler) {
    return addHandler(handler, BeforeHideEvent.getType());
  }

  @Override
  public HandlerRegistration addBeforeShowContextMenuHandler(BeforeShowContextMenuHandler handler) {
    return addHandler(handler, BeforeShowContextMenuEvent.getType());
  }

  @Override
  public HandlerRegistration addBeforeShowHandler(BeforeShowHandler handler) {
    return addHandler(handler, BeforeShowEvent.getType());
  }

  @Override
  public HandlerRegistration addBlurHandler(BlurHandler handler) {
    return addHandler(handler, BlurEvent.getType());
  }

  @Override
  public HandlerRegistration addDisableHandler(DisableHandler handler) {
    return addHandler(handler, DisableEvent.getType());
  }

  @Override
  public HandlerRegistration addEnableHandler(EnableHandler handler) {
    return addHandler(handler, EnableEvent.getType());
  }

  @Override
  public HandlerRegistration addFocusHandler(FocusHandler handler) {
    return addHandler(handler, FocusEvent.getType());
  }

  @Override
  public HandlerRegistration addHideHandler(HideHandler handler) {
    return addHandler(handler, HideEvent.getType());
  }

  @Override
  public HandlerRegistration addMoveHandler(MoveHandler handler) {
    return addHandler(handler, MoveEvent.getType());
  }

  @Override
  public HandlerRegistration addResizeHandler(ResizeHandler handler) {
    return addHandler(handler, ResizeEvent.getType());
  }

  @Override
  public HandlerRegistration addShowContextMenuHandler(ShowContextMenuHandler handler) {
    return addHandler(handler, ShowContextMenuEvent.getType());
  }

  @Override
  public HandlerRegistration addShowHandler(ShowHandler handler) {
    return addHandler(handler, ShowEvent.getType());
  }

  /**
   * Adds a style to the given element on mouseover. The widget must be sinking
   * mouse events for the over style to function.
   * 
   * @param elem the over element
   * @param style the style to add
   */
  public void addStyleOnOver(Element elem, String style) {
    if (overElements == null) {
      overElements = new FastMap<String>();
    }
    overElements.put(elem.getId(), style);
  }

  /**
   * Clears the size cache (the size of the widget the last time it was
   * changed).
   */
  public void clearSizeCache() {
    lastSize = null;
  }

  /**
   * Disable this widget.
   */
  public void disable() {
    onDisable();
    disabled = true;
    fireEvent(new DisableEvent());
  }

  /**
   * True to disable event processing.
   */
  public void disableEvents() {
    disableEvents = true;
  }

  /**
   * Enable this widget.
   */
  public void enable() {
    onEnable();
    disabled = false;
    fireEvent(new EnableEvent());
  }

  /**
   * True to enable event processing.
   */
  public void enableEvents() {
    disableEvents = false;
  }

  @Override
  public void fireEvent(GwtEvent<?> event) {
    if (disableEvents) return;
    super.fireEvent(event);
  }

  /**
   * Try to focus this widget.
   */
  public void focus() {
    getFocusEl().focus();
  }

  /**
   * Returns the application defined property for the given name, or
   * <code>null</code> if it has not been set.
   * 
   * @param key the name of the property
   * @return the value or <code>null</code> if it has not been set
   */
  @SuppressWarnings("unchecked")
  public <X> X getData(String key) {
    if (dataMap == null) return null;
    return (X) dataMap.get(key);
  }

  /**
   * Gets a handle to the object's underlying DOM element. This method should
   * not be overridden. It is non-final solely to support legacy code that
   * depends upon overriding it. If it is overridden, the subclass
   * implementation must not return a different element than was previously set
   * using {@link #setElement(com.google.gwt.user.client.Element)}.
   * 
   * @return the object's browser element
   */
  @Override
  public XElement getElement() {
    return super.getElement().<XElement> cast();
  }

  /**
   * Returns the focus manager support configuration. Only applicable when the
   * focus manager has been enabled.
   * 
   * @return the focus manager configuration
   */
  public FocusManagerSupport getFocusSupport() {
    if (focusManagerSupport == null) {
      focusManagerSupport = new FocusManagerSupport(this);
    }
    return focusManagerSupport;
  }

  /**
   * Returns the widget's hide mode.
   * 
   * @return the hide mode
   */
  public HideMode getHideMode() {
    return hideMode;
  }

  /**
   * Returns the widget's id.
   * 
   * @return the widget id
   */
  public String getId() {
    String id = getElement().getId();
    if ("".equals(id)) {
      id = "x-widget-" + ++componentId;
      getElement().setId(id);
    }
    return id;
  }

  /**
   * Returns the item id of this widget. Unlike the widget's id, the item id
   * does not have to be unique.
   * 
   * @return the widget's item id
   */
  @Override
  public String getItemId() {
    return itemId != null ? itemId : getId();
  }

  /**
   * Returns the widget's height.
   * 
   * @param content true to get the height minus borders and padding
   * @return the element's height
   */
  public int getOffsetHeight(boolean content) {
    int h = getOffsetHeight();
    if (content) {
      h -= getElement().getFrameWidth(Side.TOP, Side.BOTTOM);
    }
    return Math.max(0, h);
  }

  /**
   * Returns the element's width.
   * 
   * @param content true to get the width minus borders and padding
   * @return the width
   */
  public int getOffsetWidth(boolean content) {
    int w = getOffsetWidth();
    if (content) {
      w -= getElement().getFrameWidth(Side.LEFT, Side.RIGHT);
    }
    return Math.max(0, w);
  }

  /**
   * Returns true if the shadow is enabled.
   * 
   * @return the shadow the shadow state
   */
  public boolean getShadow() {
    return shadow;
  }

  /**
   * Returns the widget's state id. If a state id is specified, it is used as
   * the key when saving and retrieving the widget's state.
   * 
   * @return the state id
   */
  public String getStateId() {
    if (stateId == null) {
      stateId = getId();
    }
    return stateId;
  }
  
  /**
   * Returns the current tabIndex of the component. By default this is the tabIndex
   * of the root element, but some subclasses may modify this behavior.
   * 
   * @return the tabIndex of the component
   */
  public int getTabIndex() {
    return tabIndex;
  }

  /**
   * Returns the widget's tool tip.
   * 
   * @return the tool tip
   */
  public ToolTip getToolTip() {
    if (toolTip == null && toolTipConfig != null) {
      toolTip = new ToolTip(this, toolTipConfig);
    }
    return toolTip;
  }

  /**
   * Hide this widget.
   */
  public void hide() {
    if (fireCancellableEvent(new BeforeHideEvent())) {
      hidden = true;
      onHide();
      notifyHide();
      fireEvent(new HideEvent());
    }
  }

  /**
   * Hides the widget's tool tip (if one exists).
   */
  public void hideToolTip() {
    if (toolTip != null) {
      toolTip.hide();
    }
  }

  /**
   * Returns the enable text selection state.
   * 
   * @return true if enable, false if disabled
   */
  public boolean isAllowTextSelection() {
    return allowTextSelection;
  }

  /**
   * Returns the auto height state.
   * 
   * @return the auto height state
   */
  public boolean isAutoHeight() {
    return height == null;
  }

  /**
   * Returns the auto width state.
   * 
   * @return true of auto width
   */
  public boolean isAutoWidth() {
    return width == null;
  }

  /**
   * Returns true if the height is being deferred
   * 
   * @return the defer height state
   */
  public boolean isDeferHeight() {
    return deferHeight;
  }

  /**
   * Returns <code>true</code> if the widget is enabled.
   * 
   * @return the enabled state
   */
  public boolean isEnabled() {
    return !disabled;
  }

  /**
   * Returns true if the component has been rendered.
   * 
   * @return true if rendered
   */
  public boolean isRendered() {
    return super.isOrWasAttached();
  }

  /**
   * Returns true if the widget is saving and restore it's state.
   * 
   * @return true if stateful
   */
  public boolean isStateful() {
    return stateful;
  }

  @Override
  public boolean isVisible() {
    return isVisible(false);
  }

  /**
   * Returns <code>true</code> if the widget is visible.
   * 
   * @param deep true to search up the widget hierarchy
   * @return true if the widget is visible
   */
  public boolean isVisible(boolean deep) {
    Widget w = getParent();
    if (deep && w != null) {
      if (w instanceof Component) {
        Component c = (Component) w;
        return isAttached() && !hidden && getElement().isVisible(false) && c.isVisible(deep);
      } else {
        return isAttached() && !hidden && w.isVisible() && getElement().isVisible(deep);
      }
    } else {
      return isAttached() && !hidden && getElement().isVisible(deep);
    }
  }

  /**
   * Puts a mask over this widget to disable user interaction.
   */
  public void mask() {
    mask(null);
  }

  /**
   * Puts a mask over this widget to disable user interaction.
   * 
   * @param message a message to display in the mask
   */
  public void mask(String message) {
    if (!mask) {
      mask = true;
      maskMessage = message;
      getElement().mask(message);
    }
  }

  @Override
  public void onBrowserEvent(Event event) {
    switch (event.getTypeInt()) {
      case Event.ONFOCUS:
        onFocus(event);
        break;
      case Event.ONBLUR:
        onBlur(event);
        break;
      case Event.ONCONTEXTMENU:
        if (disableContextMenu) {
          event.preventDefault();
        }
        onRightClick(event);
        break;
    }
    int type = event.getTypeInt();
    // specialized support for mouse overs
    if (overElements != null && (type == Event.ONMOUSEOVER || type == Event.ONMOUSEOUT)) {
      XElement target = event.getEventTarget().cast();
      if (target != null) {
        String style = overElements.get(target.getId());
        if (style != null) {
          target.setClassName(style, type == Event.ONMOUSEOVER);
        }
      }
    }

    // we are not calling super so must fire dom events
    DomEvent.fireNativeEvent(event, this, this.getElement());
  }

  /**
   * Removes the components tooltip (if one exists).
   */
  public void removeToolTip() {
    if (toolTip != null) {
      toolTip.initTarget(null);
      toolTip = null;
      toolTipConfig = null;
    }
  }

  /**
   * Enables and disables text selection for the widget.
   * 
   * @param enable true to enable, false to disable
   */
  public void setAllowTextSelection(boolean enable) {
    allowTextSelection = enable;
    if (isAttached()) {
      getElement().disableTextSelection(!enable);
    }
  }

  /**
   * Adds or removes a border.
   * 
   * @param show <code>true</code> to display a border
   */
  public void setBorders(boolean show) {
    XElement.as(getStyleElement()).setBorders(show);
  }

  /**
   * Sets the widget's size.
   * 
   * @param x the x coordinate
   * @param y the y coordinate
   * @param width the width
   * @param height the height
   */
  public void setBounds(int x, int y, int width, int height) {
    setPagePosition(x, y);
    setPixelSize(width, height);
  }

  /**
   * Sets the widget's size.
   * 
   * @param bounds the update box
   */
  public void setBounds(Rectangle bounds) {
    setBounds(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
  }

  /**
   * Sets the widget's context menu.
   * 
   * @param menu the context menu
   */
  public void setContextMenu(Menu menu) {
    contextMenu = menu;
    disableContextMenu(true);
  }

  /**
   * Sets the application defined property with the given name.
   * 
   * @param key the name of the property
   * @param data the new value for the property
   */
  public void setData(String key, Object data) {
    if (dataMap == null) dataMap = new FastMap<Object>();
    dataMap.put(key, data);
  }

  /**
   * True to defer height calculations to an external widget, false to allow
   * this widget to set its own height (defaults to false).
   * 
   * @param deferHeight true to defer height
   */
  public void setDeferHeight(boolean deferHeight) {
    this.deferHeight = deferHeight;
  }

  /**
   * Convenience function for setting disabled/enabled by boolean.
   * 
   * @param enabled the enabled state
   */
  public void setEnabled(boolean enabled) {
    if (!enabled) {
      disable();
    } else {
      enable();
    }
  }

  /**
   * Sets the widget's height. This method fires the <i>Resize</i> event.
   * element.
   * 
   * @param height the new height
   */
  public void setHeight(int height) {
    setPixelSize(Integer.MIN_VALUE, height);
  }

  /**
   * Sets the height of the widget. This method fires the <i>Resize</i> event.
   * element.
   * 
   * @param height the new height to set
   */
  public void setHeight(String height) {
    setSize(Style.UNDEFINED, height);
  }

  /**
   * Sets the components hide mode (defaults to HideMode.DISPLAY).
   * 
   * @param hideMode the hide mode.
   */
  public void setHideMode(HideMode hideMode) {
    this.hideMode = hideMode;
  }

  /**
   * Sets the component's id.
   * 
   * @param id the id
   */
  public void setId(String id) {
    getElement().setId(id);
  }

  /**
   * Sets the widget's item id. Unlike a widget's id, the widget's item id is
   * not tied to id attribute of the widget's root element. As such, the item id
   * does not have to be unique.
   * 
   * @param id the item id
   */
  @Override
  public void setItemId(String id) {
    this.itemId = id;
  }

  /**
   * Sets the page XY position of the widget. To set the left and top instead,
   * use {@link #setPosition}.
   * 
   * @param x the x coordinate
   * @param y the y coordinate
   */
  public void setPagePosition(int x, int y) {
    if (x != Style.DEFAULT) {
      pageX = x;
    }
    if (y != Style.DEFAULT) {
      pageY = y;
    }

    if (!isAttached()) {
      return;
    }
    Point p = getPositionEl().translatePoints(new Point(x, y));
    setPosition(p.getX(), p.getY());
  }

  /**
   * Sets the component's size. Unlike GWT widget's, when setting sizes, the
   * component's actual size will match exactly the size specified independent
   * of borders and padding.
   * 
   * @param width new width, in pixels
   * @param height height, in pixels
   */
  @Override
  public void setPixelSize(int width, int height) {
    int w, h;
    if (width == -1) {
      w = width;
      this.width = null;
    } else if (width != Integer.MIN_VALUE) {
      w = width;
      this.width = width + "px";
    } else {
      w = Util.parseInt(this.width, -1);
    }

    if (height == -1) {
      this.height = null;
      h = height;
    } else if (height != Integer.MIN_VALUE) {
      h = height;
      this.height = height + "px";
    } else {
      h = Util.parseInt(this.height, -1);
    }

    if (!isAttached()) {
      return;
    }

    Size size = new Size(w, h);
    if (cacheSizes && lastSize != null && lastSize.equals(size)) {
      return;
    }

    List<FastMap<Object>> list = makeVisible();

    lastSize = size;

    Size ads = adjustSize(size);

    int aw = ads.getWidth();
    int ah = ads.getHeight();

    if (width != -1 && height != -1 && width != Integer.MIN_VALUE && height != Integer.MIN_VALUE && !deferHeight) {
      getElement().setSize(aw, ah, adjustSize);
    } else if (width != -1 && width != Integer.MIN_VALUE) {
      getElement().setWidth(aw, adjustSize);
      if (height != Integer.MIN_VALUE) {
        getElement().getStyle().clearHeight();
      }
    } else if (height != -1 && !deferHeight) {
      getElement().setHeight(ah, adjustSize);
      if (width != Integer.MIN_VALUE) {
        getElement().getStyle().clearWidth();
      }
    }

    restoreVisible(list);

    onResize(aw, ah);

    Scheduler.get().scheduleFinally(new ScheduledCommand() {

      @Override
      public void execute() {
        sync(true);
      }
    });

    ResizeEvent.fire(this, aw, ah);
  }

  /**
   * Sets the left and top of the widget. To set the page XY position instead,
   * use {@link #setPagePosition}.
   * 
   * @param left the new left
   * @param top the new top
   */
  public void setPosition(int left, int top) {
    this.left = left;
    this.top = top;

    if (!isAttached()) {
      return;
    }

    getElement().makePositionable();

    Point p = new Point(left, top);

    p = adjustPosition(p);
    int ax = p.getX(), ay = p.getY();

    XElement pel = getPositionEl();

    if (ax != Style.DEFAULT || ay != Style.DEFAULT) {
      if (ax != Style.DEFAULT) {
        pel.setLeft(ax);
      }
      if (ay != Style.DEFAULT) {
        pel.setTop(ay);
      }

      onPosition(ax, ay);

      fireEvent(new MoveEvent(ax, ay));
    }
  }

  /**
   * True to enable a shadow that will be displayed behind the widget (defaults
   * to false).
   * 
   * @param shadow true to enable the shadow
   */
  public void setShadow(boolean shadow) {
    this.shadow = shadow;
  }

  /**
   * Sets the width and height of the widget. This method fires the
   * <i>Resize</i> event.
   * 
   * @param width the new width to set
   * @param height the new height to set
   */
  @Override
  public void setSize(String width, String height) {
    if (width != null && !Style.UNDEFINED.equals(width)) {
      width = XElement.addUnits(width, "px");
    }

    if (height != null && !Style.UNDEFINED.equals(height)) {
      height = XElement.addUnits(height, "px");
    }

    if ((height == null && width != null && width.endsWith("px"))
        || (width == null && height != null && height.endsWith("px"))
        || (width != null && height != null && width.endsWith("px") && height.endsWith("px"))) {
      int w, h;
      if (width == null) {
        w = -1;
      } else if (Style.UNDEFINED.equals(width)) {
        w = Integer.MIN_VALUE;
      } else {
        w = Util.parseInt(width, -1);
      }
      if (height == null) {
        h = -1;
      } else if (Style.UNDEFINED.equals(height)) {
        h = Integer.MIN_VALUE;
      } else {
        h = Util.parseInt(height, -1);
      }
      setPixelSize(w, h);
      return;
    }

    if (width == null) {
      this.width = null;
    } else if (width.equals(Style.UNDEFINED)) {
      width = this.width;
    } else {
      this.width = width;
    }

    if (height == null) {
      this.height = null;
    } else if (height.equals(Style.UNDEFINED)) {
      height = this.height;
    } else {
      this.height = height;
    }

    if (!isAttached()) {
      return;
    }

    if (width != null) {
      getElement().setWidth(width);
    } else {
      getElement().getStyle().clearWidth();
    }

    if (height != null) {
      if (!deferHeight) {
        getElement().setHeight(height);
      }
    } else {
      getElement().getStyle().clearHeight();
    }

    int w = -1;
    int h = -1;

    List<FastMap<Object>> list = makeVisible();

    if (width == null) {
      w = -1;
    } else if (width.indexOf("px") != -1) {
      w = Util.parseInt(width, -1);
    } else {
      w = getOffsetWidth();
    }
    if (height == null) {
      h = -1;
    } else if (height.indexOf("px") != -1) {
      h = Util.parseInt(height, -1);
    } else {
      h = getOffsetHeight();
    }

    Size size = new Size(w, h);
    if (cacheSizes && lastSize != null && lastSize.equals(size)) {
      return;
    }

    lastSize = size;

    onResize(w, h);

    sync(true);

    restoreVisible(list);

    ResizeEvent.fire(this, w, h);
  }

  /**
   * A flag which specifies if the component is stateful (defaults to false).
   * The widget must have either a {@link #setStateId(String)} or
   * {@link #setId(String)} assigned for state to be managed. Auto-generated ids
   * are not guaranteed to be stable across page loads and cannot be relied upon
   * to save and restore the same state for a widget.
   * 
   * @param stateful true to enable state
   */
  public void setStateful(boolean stateful) {
    this.stateful = stateful;
  }

  /**
   * Sets the widget's state id which is a unique id for this widget to use for
   * state management purposes (defaults to the widget id if one was set,
   * otherwise null if the widget is using a generated id).
   * 
   * @param stateId the state id
   */
  public void setStateId(String stateId) {
    this.stateId = stateId;
  }

  /**
   * Sets the component's tab index. Subclasses may override this to set the tab index on a specific
   * element for better focus behavior - they are also responsible for setting the tabIndex field.
   * 
   * @param tabIndex the tab index
   */
  public void setTabIndex(int tabIndex) {
    this.tabIndex = tabIndex;
    getElement().setTabIndex(tabIndex);
  }

  /**
   * Sets the widget's tool tip.
   * 
   * @param text the text
   */
  public void setToolTip(String text) {
    if (toolTipConfig == null) {
      toolTipConfig = new ToolTipConfig();
    }
    toolTipConfig.setBodyHtml(text);
    setToolTipConfig(toolTipConfig);
  }

  /**
   * Sets the widget's tool tip with the given config.
   * 
   * @param config the tool tip config
   */
  public void setToolTipConfig(ToolTipConfig config) {
    this.toolTipConfig = config;
    if (config != null) {
      if (toolTip == null) {
        toolTip = new ToolTip(this, config);
      } else {
        toolTip.update(config);
      }
    } else if (config == null) {
      removeToolTip();
    }
  }

  /**
   * Convenience function to hide or show this widget by boolean.
   * 
   * @param visible the visible state
   */
  @Override
  public void setVisible(boolean visible) {
    if (visible) {
      show();
    } else {
      hide();
    }
  }

  /**
   * Sets the width of the widget. This method fires the <i>Resize</i> event.
   * 
   * @param width the new width to set
   */
  public void setWidth(int width) {
    setPixelSize(width, Integer.MIN_VALUE);
  }

  /**
   * Sets the width of the widget. This method fires the <i>Resize</i> event.
   * 
   * @param width the new width to set
   */
  public void setWidth(String width) {
    setSize(width, Style.UNDEFINED);
  }

  /**
   * Show this widget.
   */
  public void show() {
    if (fireCancellableEvent(new BeforeShowEvent())) {
      hidden = false;
      onShow();
      notifyShow();
      fireEvent(new ShowEvent());
    }
  }

  /**
   * Syncs the layer of the widget.
   * 
   * @param show true to show the layer
   */
  public void sync(boolean show) {
    if (layer != null) {
      layer.sync(show);
    }
  }

  /**
   * Clears the size cache and resets to the last known size.
   */
  public void syncSize() {
    Size oldSize = lastSize;
    lastSize = null;
    if (isAttached() && oldSize != null) {
      setPixelSize(oldSize.getWidth(), oldSize.getHeight());
    }
  }

  /**
   * Unmasks the widget.
   */
  public void unmask() {
    mask = false;
    maskMessage = null;
    getElement().unmask();
  }

  /**
   * Adds a dependent style name to a child element.
   * 
   * @param element the element
   * @param style the style name
   */
  protected void addStyleDependentName(Element element, String style) {
    element.addClassName(getStylePrimaryName() + "-" + style);
  }

  protected Point adjustPosition(Point point) {
    return point;
  }

  protected Size adjustSize(Size size) {
    return size;
  }

  protected void applyState(Map<String, Object> state) {

  }

  protected void assertAfterRender() {
    assert isOrWasAttached() : "Method must be called after the widget is rendered";
  }

  protected void assertPreRender() {
    assert !isOrWasAttached() : "Method must be called before the widget is rendered";
  }

  /**
   * Tries to remove focus from the widget.
   */
  protected void blur() {
    getFocusEl().blur();
  }

  /**
   * Enables and disables the widget's context menu.
   * 
   * @param disable <code>true</code> to disable the context menu
   */
  protected void disableContextMenu(boolean disable) {
    disableContextMenu = disable;
    if (disable) {
      sinkEvents(Event.ONCONTEXTMENU);
    }
  }

  protected boolean fireCancellableEvent(GwtEvent<?> event) {
    if (disableEvents) return true;
    fireEvent(event);
    if (event instanceof CancellableEvent) {
      return !((CancellableEvent) event).isCancelled();
    }
    return true;
  }

  protected XElement getFocusEl() {
    return getElement();
  }

  /**
   * Returns the element to be used when positioning the widget. Subclasses may
   * override as needed. Default method returns the widget's root element.
   * 
   * @return the position element
   */
  protected XElement getPositionEl() {
    return getElement();
  }

  protected void hideShadow() {
    if (layer != null) {
      layer.hideShadow();
    }
  }

  /**
   * Returns true if browser resizing is monitored
   * 
   * @return true if window resize monitoring is enabled
   */
  protected boolean isMonitorWindowResize() {
    return monitorWindowResize;
  }

  protected void notifyHide() {
  }

  protected void notifyShow() {
  }

  /**
   * Called immediately after the first time the widget becomes attached to the
   * browser's document only the first time.
   */
  protected void onAfterFirstAttach() {
    if (shadow || (shim && GXT.isUseShims())) {
      layer = new Layer(getElement());
      if (shadow) {
        layer.enableShadow();
        layer.setShadowPosition(shadowPosition);
      }
      if (shim && GXT.isUseShims()) {
        layer.enableShim();
      }
    }

    if (left != Style.DEFAULT || top != Style.DEFAULT) {
      setPosition(left, top);
    }
    if (pageX != Style.DEFAULT || pageY != Style.DEFAULT) {
      setPagePosition(pageX, pageY);
    }
  }

  @Override
  protected void onAttach() {
    boolean isOrWasAttached = isOrWasAttached();
    super.onAttach();

    if (!isOrWasAttached) {
      onAfterFirstAttach();
    }

    if (width != null || height != null) {
      setSize(width, height);
    }

  }

  protected void onBlur(Event event) {
    fireEvent(new BlurEvent());
  }

  @Override
  protected void onDetach() {
    super.onDetach();

    hideToolTip();

    if (layer != null) {
      layer.hideUnders();
    }
  }

  protected void onDisable() {
    if (disabledStyle != null) {
      addStyleName(disabledStyle);
    }
  }

  protected void onEnable() {
    if (disabledStyle != null) {
      removeStyleName(disabledStyle);
    }
  }

  protected void onFocus(Event event) {
    fireEvent(new FocusEvent());
  }

  protected void onHide() {
    addStyleName(hideMode.value());
    if (layer != null) {
      layer.hideUnders();
    }
    hideToolTip();
  }

  protected void onHideContextMenu(HideEvent event) {

  }

  @Override
  protected void onLoad() {
    super.onLoad();

    if (!allowTextSelection) {
      setAllowTextSelection(false);
    }

    if (monitorWindowResize) {
      if (windowResizeTask == null) {
        windowResizeTask = new DelayedTask() {
          @Override
          public void onExecute() {
            onWindowResize(Window.getClientWidth(), Window.getClientHeight());
          }
        };
      }
      resizeHandler = Window.addResizeHandler(new ResizeHandler() {
        public void onResize(ResizeEvent event) {
          windowResizeTask.delay(windowResizeDelay);
        }
      });
    }
  }

  /**
   * Called after the widget is moved, this method is empty by default but can
   * be implemented by any subclass that needs to perform custom logic after a
   * move occurs.
   * 
   * @param x the new x position
   * @param y the new y position
   */
  protected void onPosition(int x, int y) {
  }

  /**
   * Called after the widget is resized, this method is empty by default but can
   * be implemented by any subclass that needs to perform custom logic after a
   * resize occurs.
   * 
   * @param width the width
   * @param height the height
   */
  protected void onResize(int width, int height) {
    if (mask) {
      mask(maskMessage);
    }
  }

  protected void onRightClick(Event event) {
    if (contextMenu != null && fireCancellableEvent(new BeforeShowContextMenuEvent(contextMenu))) {
      event.preventDefault();
      event.stopPropagation();

      final int x = event.getClientX();
      final int y = event.getClientY();
      Scheduler.get().scheduleDeferred(new ScheduledCommand() {

        @Override
        public void execute() {
          onShowContextMenu(x, y);
        }
      });
    }
  }

  protected void onShow() {
    removeStyleName(hideMode.value());
    sync(true);
    Scheduler.get().scheduleFinally(new ScheduledCommand() {
      @Override
      public void execute() {
        sync(true);
      }
    });
  }

  protected void onShowContextMenu(int clientX, int clientY) {
    contextMenu.showAt(clientX, clientY);
    if (contextMenu.isVisible()) {
      fireEvent(new ShowContextMenuEvent(contextMenu));
      contextMenu.addHideHandler(new HideHandler() {

        @Override
        public void onHide(HideEvent event) {
          ComponentHelper.removeHandler(contextMenu, HideEvent.getType(), this);
          onHideContextMenu(event);
        }
      });
    }
  }

  @Override
  protected void onUnload() {
    super.onUnload();

    if (!allowTextSelection) {
      getElement().disableTextSelection(false);
    }

    if (resizeHandler != null) {
      resizeHandler.removeHandler();
      resizeHandler = null;
    }
  }

  protected void onWindowResize(int width, int height) {
  }

  /**
   * Removes a dependent style name from a child element.
   * 
   * @param element the element
   * @param style the style name
   */
  protected void removeStyleDependentName(Element element, String style) {
    element.removeClassName(getStylePrimaryName() + "-" + style);
  }

  protected void removeStyleOnOver(Element elem) {
    if (overElements != null) {
      overElements.remove(elem.getId());
    }
  }

  /**
   * True to have onWindowResize executed when the browser window is resized
   * (default to false).
   * 
   * You need to override onWindowResize to get your needed functionality
   * 
   * @param monitorWindowResize true to monitor window resizing
   */
  protected void setMonitorWindowResize(boolean monitorWindowResize) {
    this.monitorWindowResize = monitorWindowResize;
  }

  /**
   * Adds or removes a dependent style name to a child element.
   * 
   * @param element the element
   * @param style the style name
   * @param add true to add, otherwise remove
   */
  protected void setStyleDependentName(Element element, String style, boolean add) {
    element.<XElement> cast().setClassName(getStylePrimaryName() + "-" + style, add);
  }

  private List<FastMap<Object>> makeVisible() {
    if (ensureVisibilityOnSizing) {
      return getElement().ensureVisible();
    }
    return null;
  }

  private void restoreVisible(List<FastMap<Object>> list) {
    if (ensureVisibilityOnSizing && list != null) {
      getElement().restoreVisible(list);
    }
  }

}
