/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;
import com.sencha.gxt.core.client.Style.Direction;
import com.sencha.gxt.core.client.Style.LayoutRegion;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.resources.CommonStyles;
import com.sencha.gxt.core.client.util.DelayedTask;
import com.sencha.gxt.core.client.util.Rectangle;
import com.sencha.gxt.core.shared.event.GroupingHandlerRegistration;
import com.sencha.gxt.fx.client.DragCancelEvent;
import com.sencha.gxt.fx.client.DragEndEvent;
import com.sencha.gxt.fx.client.DragHandler;
import com.sencha.gxt.fx.client.DragMoveEvent;
import com.sencha.gxt.fx.client.DragStartEvent;
import com.sencha.gxt.fx.client.Draggable;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.MoveEvent;
import com.sencha.gxt.widget.core.client.event.MoveEvent.MoveHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.HasSelectHandlers;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.event.ShowEvent;
import com.sencha.gxt.widget.core.client.event.ShowEvent.ShowHandler;
import com.sencha.gxt.widget.core.client.event.SplitBarDragEvent;
import com.sencha.gxt.widget.core.client.event.SplitBarDragEvent.HasSplitBarDragHandlers;
import com.sencha.gxt.widget.core.client.event.SplitBarDragEvent.SplitBarDragHandler;

/**
 * Creates a draggable splitter on the side of a widget.
 */
public class SplitBar extends Component implements HasClickHandlers, HasDoubleClickHandlers, HasSplitBarDragHandlers,
    HasSelectHandlers {

  @SuppressWarnings("javadoc")
  public interface SplitBarAppearance {
    String miniClass(Direction direction);

    String miniSelector();

    void onMiniOver(XElement mini, boolean over);

    String proxyClass();

    void render(SafeHtmlBuilder sb, LayoutRegion region);
  }

  private class Handler implements AttachEvent.Handler, ResizeHandler, MoveHandler, HideHandler, ShowHandler {

    @Override
    public void onAttachOrDetach(AttachEvent event) {
      if (event.isAttached()) {
        onHandleAttach();
      } else {
        onHandleDetach();
      }
    }

    @Override
    public void onHide(HideEvent event) {
      onHandleHide(event);
    }

    @Override
    public void onMove(MoveEvent event) {
      delay.delay(10);
    }

    @Override
    public void onResize(ResizeEvent event) {
      delay.delay(10);
    }

    @Override
    public void onShow(ShowEvent event) {
      onHandleShow(event);
    }
  }

  private boolean autoSize = true;
  private int yOffset = 0;
  private int xOffset = 0;

  private int minSize = 10;
  private int maxSize = 2000;

  private int handleWidth = 5;
  private int barWidth = 2;
  private XElement resizeEl, miniEl;
  private Component resizeWidget;
  private Component containerWidget;
  private Draggable draggable;
  private Rectangle startBounds;
  private DelayedTask delay;
  protected LayoutRegion region;

  private GroupingHandlerRegistration handlerRegistration;
  private boolean collapsible;
  private final SplitBarAppearance appearance;
  private boolean disableDragging;

  /**
   * Creates a split for bar for the specified layout region and target.
   * 
   * @param region the layout region
   * @param target the split bar container
   */
  public SplitBar(LayoutRegion region, Component target) {
    this(GWT.<SplitBarAppearance> create(SplitBarAppearance.class), region, target);
  }

  /**
   * Creates a new split bar.
   * 
   * @param style the bar location
   * @param resizeWidget the widget being resized
   * @param container the widget the split bar proxy will be sized to
   */
  public SplitBar(LayoutRegion style, Component resizeWidget, Component container) {
    this(style, resizeWidget);
    this.containerWidget = container;
    draggable.setContainer(container);
  }

  /**
   * Creates a new split bar with the specified appearance
   * 
   * @param appearance the split bar appearance
   * @param region the bar location
   * @param resizeWidget the widget being resized
   */
  public SplitBar(SplitBarAppearance appearance, LayoutRegion region, Component resizeWidget) {
    this.appearance = appearance;
    this.region = region;
    this.resizeWidget = resizeWidget;
    this.resizeEl = resizeWidget.getElement();

    SafeHtmlBuilder builder = new SafeHtmlBuilder();
    this.appearance.render(builder, region);

    setElement(XDOM.create(builder.toSafeHtml()));

    setAllowTextSelection(false);
    getElement().makePositionable(true);

    Handler handler = new Handler();
    handlerRegistration = new GroupingHandlerRegistration();
    handlerRegistration.add(resizeWidget.addAttachHandler(handler));
    handlerRegistration.add(resizeWidget.addMoveHandler(handler));
    handlerRegistration.add(resizeWidget.addResizeHandler(handler));
    handlerRegistration.add(resizeWidget.addHideHandler(handler));
    handlerRegistration.add(resizeWidget.addShowHandler(handler));

    draggable = new Draggable(this);
    draggable.setUpdateZIndex(false);
    draggable.setStartDragDistance(0);
    draggable.setProxyStyle(appearance.proxyClass());

    DragHandler dragHandler = new DragHandler() {
      @Override
      public void onDragCancel(DragCancelEvent event) {
        onCancelDrag(event);
      }

      @Override
      public void onDragEnd(DragEndEvent event) {
        onEndDrag(event);
      }

      @Override
      public void onDragMove(DragMoveEvent event) {

      }

      @Override
      public void onDragStart(DragStartEvent event) {
        onStartDrag(event);
      }
    };

    draggable.addDragHandler(dragHandler);

    if (resizeWidget.isAttached()) {
      onHandleAttach();
    }

    delay = new DelayedTask() {

      @Override
      public void onExecute() {
        sync();
      }
    };

    sinkEvents(Event.ONCLICK | Event.ONMOUSEOVER | Event.ONMOUSEOUT);
  }

  @Override
  public HandlerRegistration addClickHandler(ClickHandler handler) {
    return addDomHandler(handler, ClickEvent.getType());
  }

  @Override
  public HandlerRegistration addDoubleClickHandler(DoubleClickHandler handler) {
    return addDomHandler(handler, DoubleClickEvent.getType());
  }

  @Override
  public HandlerRegistration addSelectHandler(SelectHandler handler) {
    return addHandler(handler, SelectEvent.getType());
  }

  @Override
  public HandlerRegistration addSplitBarDragHandler(SplitBarDragHandler handler) {
    return addHandler(handler, SplitBarDragEvent.getType());
  }

  /**
   * Prevents the split bar from being dragged.
   */
  public void disableDragging() {
    if (!disableDragging) {
      disableDragging = true;
      draggable.release();
      getElement().getStyle().setCursor(Cursor.DEFAULT);
    }
  }

  /**
   * Returns the split bar appearance.
   * 
   * @return the split bar appearance
   */
  public SplitBarAppearance getAppearance() {
    return appearance;
  }

  /**
   * Returns the bar width.
   * 
   * @return the bar width
   */
  public int getBarWidth() {
    return barWidth;
  }

  /**
   * Returns the container widget.
   * 
   * @return the container widget
   */
  public Component getContainer() {
    return containerWidget;
  }

  /**
   * Returns the split bar's draggable instance.
   * 
   * @return the draggable instance
   */
  public Draggable getDraggable() {
    return draggable;
  }

  /**
   * Returns the handle width.
   * 
   * @return the handle width
   */
  public int getHandleWidth() {
    return handleWidth;
  }

  /**
   * Returns the maximum size.
   * 
   * @return the max size
   */
  public int getMaxSize() {
    return maxSize;
  }

  /**
   * @return the minSize
   */
  public int getMinSize() {
    return minSize;
  }

  /**
   * Returns the resize widget.
   * 
   * @return the resize widget
   */
  public Component getTargetWidget() {
    return resizeWidget;
  }

  /**
   * Returns the x offset.
   * 
   * @return the x offset value
   */
  public int getXOffset() {
    return xOffset;
  }

  /**
   * Returns the y offset.
   * 
   * @return the y offset
   */
  public int getYOffset() {
    return yOffset;
  }

  /**
   * Returns the auto size state.
   * 
   * @return true if auto size is enabled
   */
  public boolean isAutoSize() {
    return autoSize;
  }

  /**
   * Return true if the mini-collapse tool is enabled.
   * 
   * @return true if mini-collapse enabled
   */
  public boolean isCollapsible() {
    return collapsible;
  }

  @Override
  public void onBrowserEvent(Event event) {
    super.onBrowserEvent(event);
    switch (event.getTypeInt()) {
      case Event.ONCLICK:
        onClick(event);
        break;
      case Event.ONMOUSEOVER:
        onMouseOver(event);
        break;

      case Event.ONMOUSEOUT:
        onMouseOut(event);
        break;
    }
  }

  /**
   * Removes the split bar from the resize widget.
   */
  public void release() {
    handlerRegistration.removeHandler();

    removeSplitBar();
    draggable.release();
  }

  /**
   * True to update the size of the the resize widget after a drag operation
   * using a proxy (defaults to true).
   * 
   * @param autoSize the auto size state
   */
  public void setAutoSize(boolean autoSize) {
    this.autoSize = autoSize;
  }

  /**
   * Sets the width of drag proxy during resizing (defaults to 2).
   * 
   * @param barWidth the bar width
   */
  public void setBarWidth(int barWidth) {
    this.barWidth = barWidth;
  }

  /**
   * True to show a mini-collapse tool in the split bar (defaults to false).
   * When clicked, the collapse event is fired.
   * 
   * @param collapsible true to add the mini-collapse tool
   */
  public void setCollapsible(boolean collapsible) {
    this.collapsible = collapsible;
    if (miniEl == null) {
      miniEl = XElement.createElement("div");
      miniEl.setClassName(CommonStyles.get().nodrag() + " " + appearance.miniClass(Direction.LEFT));
      getElement().appendChild(miniEl);

    }
    miniEl.setDisplayed(collapsible);
  }

  /**
   * Sets the width of the drag handles (defaults to 5).
   * 
   * @param handleWidth the handle width
   */
  public void setHandleWidth(int handleWidth) {
    this.handleWidth = handleWidth;
  }

  /**
   * Sets the maximum size of the resize widget (defaults to 2000).
   * 
   * @param maxSize the maximum size
   */
  public void setMaxSize(int maxSize) {
    this.maxSize = maxSize;
  }

  /**
   * Sets he minimum size of the resize widget (defaults to 10).
   * 
   * @param minSize the minimum size
   */
  public void setMinSize(int minSize) {
    this.minSize = minSize;
  }

  /**
   * The amount of pixels the bar should be offset to the left (defaults to 0).
   * 
   * @param x the xOffset to set
   */
  public void setXOffset(int x) {
    this.xOffset = x;
  }

  /**
   * Sets the amount of pixels the bar should be offset to the top (defaults to
   * 0).
   * 
   * @param y the yOffset to set
   */
  public void setYOffset(int y) {
    this.yOffset = y;
  }

  /**
   * Updates the spitbar's bounds to match the target widget.
   */
  public void sync() {
    if (!isAttached() || !resizeWidget.isAttached()) {
      return;
    }

    Rectangle rect = resizeEl.getBounds();
    int x = rect.getX();
    int y = rect.getY();

    // if (!GXT.isBorderBox()) {
    // y -= resizeEl.getFrameWidth(Side.TOP);
    // x -= resizeEl.getFrameWidth(Side.LEFT);
    // }

    y = Math.max(y, 0);

    int w = rect.getWidth();
    int h = rect.getHeight();

    switch (region) {
      case SOUTH:
        getElement().setBounds(x + getXOffset(), y + h + getYOffset(), w, getHandleWidth(), false);
        break;
      case WEST:
        getElement().setBounds(x - getHandleWidth() + getYOffset(), y + getXOffset(), getHandleWidth(), h, false);
        break;
      case NORTH:
        getElement().setBounds(x + getXOffset(), y - getHandleWidth() + getYOffset(), w, getHandleWidth(), false);
        break;
      case EAST:
        getElement().setBounds(Math.max(0, x + w + getXOffset()), y + getYOffset(), getHandleWidth(), h, false);
        break;
    }

  }

  /**
   * Sets the visual style indicating the direction of the mini collapse tool.
   * 
   * @param direction the collapse direction
   */
  public void updateMini(Direction direction) {
    if (miniEl != null) {
      miniEl.setClassName(CommonStyles.get().nodrag() + " " + appearance.miniClass(direction));
    }
  }

  protected void onClick(Event event) {
    XElement target = event.getEventTarget().<XElement> cast();
    if (target == miniEl) {
      event.stopPropagation();
      onMiniClick();
    }
  }

  protected void onHandleAttach() {
    if (!disabled) {
      resizeWidget.getParent().getElement().appendChild(getElement());
      ComponentHelper.doAttach(SplitBar.this);
      sync();
    }
  }

  protected void onHandleDetach() {
    if (!disabled) {
      removeSplitBar();
    }
  }

  protected void onHandleHide(HideEvent event) {
    hide();
  }

  protected void onHandleShow(ShowEvent event) {
    show();
    sync();
  }

  protected void onMiniClick() {
    fireEvent(new SelectEvent());
  }

  protected void onMouseOut(Event event) {
    if (event.getEventTarget().cast() == miniEl) {
      appearance.onMiniOver(miniEl, false);
    }
  }

  protected void onMouseOver(Event event) {
    if (event.getEventTarget().cast() == miniEl) {
      appearance.onMiniOver(miniEl, true);
    }
  }

  protected void removeSplitBar() {
    ComponentHelper.doDetach(this);
    getElement().removeFromParent();
  }

  private void onCancelDrag(DragCancelEvent be) {
    resizeWidget.enableEvents();
    sync();
  }

  private void onEndDrag(DragEndEvent bee) {
    int x = bee.getX();
    int y = bee.getY();
    int width = resizeWidget.getOffsetWidth();
    int height = resizeWidget.getOffsetHeight();

    int diffY = y - startBounds.getY();
    int diffX = x - startBounds.getX();

    resizeWidget.enableEvents();

    int size = 0;

    switch (region) {
      case NORTH: {
        size = height - diffY;
        if (autoSize) {
          resizeEl.setY(y);
          resizeEl.setHeight(height);
        }
        break;
      }
      case SOUTH: {
        size = height + diffY;
        if (autoSize) {
          resizeWidget.setHeight(diffY);
        }
        break;
      }
      case WEST: {
        size = width - diffX;
        if (autoSize) {
          getElement().setX(x);
          resizeWidget.setWidth(width - diffX);
        }
        break;
      }
      case EAST: {
        size = width + diffX;
        if (autoSize) {
          resizeWidget.setWidth(size);
        }
        break;
      }
    }
    fireEvent(new SplitBarDragEvent(false, size));
  }

  private void onStartDrag(DragStartEvent de) {
    // adjust width of proxy
    if (region == LayoutRegion.WEST || region == LayoutRegion.EAST) {
      de.setWidth(getBarWidth());
    } else {
      de.setHeight(getBarWidth());
    }

    fireEvent(new SplitBarDragEvent(true, 0));

    resizeWidget.enableEvents();

    if (containerWidget != null) {
      switch (region) {
        case WEST:
        case EAST:
          int h = containerWidget.getOffsetHeight(true);
          de.setHeight(h);
          break;
        case NORTH:
        case SOUTH:
          int w = containerWidget.getOffsetWidth(true);
          de.setWidth(w);
          break;
      }
    }
    startBounds = new Rectangle();
    startBounds.setY(de.getY());
    startBounds.setX(de.getX());

    boolean v = region == LayoutRegion.WEST || region == LayoutRegion.EAST;
    int size;
    if (v) {
      size = resizeEl.getOffsetWidth();
    } else {
      size = resizeEl.getOffsetHeight();
    }

    int c1 = size - getMinSize();
    if (size < getMinSize()) {
      c1 = 0;
    }
    int c2 = Math.max(getMaxSize() - size, 0);
    if (v) {
      draggable.setConstrainVertical(true);
      draggable.setXConstraint(region == LayoutRegion.WEST ? c2 : c1, region == LayoutRegion.WEST ? c1 : c2);
    } else {
      draggable.setConstrainHorizontal(true);
      draggable.setYConstraint(region == LayoutRegion.NORTH ? c2 : c1, region == LayoutRegion.NORTH ? c1 : c2);
    }
  }

}
