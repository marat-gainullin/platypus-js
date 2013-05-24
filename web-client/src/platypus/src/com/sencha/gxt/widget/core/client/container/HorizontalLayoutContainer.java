/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.container;

import java.util.logging.Logger;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.HasScrollHandlers;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiChild;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IndexedPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.GXTLogConfiguration;
import com.sencha.gxt.core.client.Style.Side;
import com.sencha.gxt.core.client.dom.DefaultScrollSupport;
import com.sencha.gxt.core.client.dom.HasScrollSupport;
import com.sencha.gxt.core.client.dom.ScrollSupport;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.Rectangle;
import com.sencha.gxt.core.client.util.Size;

/**
 * A layout container that lays out its children in a single row. The layout
 * properties for each child are specified using {@link HorizontalLayoutData}.
 * 
 * <p/>
 * Code Snippet:
 * 
 * <pre>
    HorizontalLayoutContainer c = new HorizontalLayoutContainer();
    HorizontalLayoutData layoutData = new HorizontalLayoutData(100, 100, new Margins(5));
    c.add(new TextButton("Button 1"), layoutData);
    c.add(new TextButton("Button 2"), layoutData);
    c.add(new TextButton("Button 3"), layoutData);
    RootPanel.get().add(c);
 * </pre>
 */
public class HorizontalLayoutContainer extends InsertResizeContainer implements HasScrollHandlers, HasScrollSupport {

  /**
   * Specifies parameters that control the layout of the widget in the
   * container.
   */
  public static class HorizontalLayoutData extends MarginData implements HasWidth, HasHeight, LayoutData {

    private double height = -1d;
    private double width = -1d;

    /**
     * Creates horizontal layout parameters with default values for
     * <code>height</code> (-1) and <code>width</code> (-1) and
     * <code>margins</code> (none).
     */
    @UiConstructor
    public HorizontalLayoutData() {

    }

    /**
     * Creates horizontal layout parameters with the specified values.
     * 
     * @param width the width specification (see {@link HasWidth})
     * @param height the height specification (see {@link HasHeight})
     */
    public HorizontalLayoutData(double width, double height) {
      setWidth(width);
      setHeight(height);
    }

    /**
     * Creates horizontal layout parameters with the specified values.
     * 
     * @param width the width specification (see {@link HasWidth})
     * @param height the height specification (see {@link HasHeight})
     * @param margins the margin specification (see {@link HasMargins})
     */
    public HorizontalLayoutData(double width, double height, Margins margins) {
      super(margins);
      setWidth(width);
      setHeight(height);
    }

    public double getHeight() {
      return height;
    }

    public double getWidth() {
      return width;
    }

    public void setHeight(double height) {
      this.height = height;
    }

    public void setWidth(double width) {
      this.width = width;
    }

  }

  private boolean adjustForScroll;
  private boolean secondPassRequired;
  private ScrollSupport scrollSupport;

  private static Logger logger = Logger.getLogger(HorizontalLayoutContainer.class.getName());

  /**
   * Creates a horizontal layout container.
   */
  public HorizontalLayoutContainer() {
    setElement(DOM.createDiv());
    getContainerTarget().makePositionable(true);
  }

  /**
   * Adds a widget to the horizontal layout container with the specified layout
   * parameters.
   * 
   * @param child the widget to add to the layout container
   * @param layoutData the parameters that describe how to lay out the widget
   */
  @UiChild(tagname = "child")
  public void add(IsWidget child, HorizontalLayoutData layoutData) {
    if (child != null) {
      child.asWidget().setLayoutData(layoutData);
    }
    super.add(child);
  }

  @Override
  public HandlerRegistration addScrollHandler(ScrollHandler handler) {
    DOM.sinkEvents(getContainerTarget(), Event.ONSCROLL | DOM.getEventsSunk(getContainerTarget()));
    return addDomHandler(handler, ScrollEvent.getType());
  }

  /**
   * Returns the scroll mode from the container's <code>ScrollSupport</code>
   * instance.
   * 
   * @return the scroll mode
   */
  public ScrollMode getScrollMode() {
    return getScrollSupport().getScrollMode();
  }

  @Override
  public ScrollSupport getScrollSupport() {
    if (scrollSupport == null) {
      scrollSupport = new DefaultScrollSupport(getContainerTarget());
    }
    return scrollSupport;
  }

  /**
   * Inserts the widget at the specified index in the horizontal layout
   * container.
   * 
   * @param w the widget to insert in the layout container
   * @param beforeIndex the insert index
   * @param layoutData the parameters that describe how to lay out the widget
   */
  public void insert(IsWidget w, int beforeIndex, HorizontalLayoutData layoutData) {
    if (w != null) {
      w.asWidget().setLayoutData(layoutData);
    }
    super.insert(w, beforeIndex);
  }

  /**
   * Returns true if the container reserves space for the scroll bar.
   * 
   * @return true if the container reserves space for the scroll bar
   */
  public boolean isAdjustForScroll() {
    return adjustForScroll;
  }

  /**
   * True to request that the container reserve space for the scroll bar
   * (defaults to false).
   * 
   * @param adjustForScroll true to reserve space for the scroll bar
   */
  public void setAdjustForScroll(boolean adjustForScroll) {
    this.adjustForScroll = adjustForScroll;
  }

  /**
   * Sets the scroll mode on the container's <code>ScrollSupport</code>
   * instance. The scroll mode will not be preserved if
   * {@link #setScrollSupport(ScrollSupport)} is called AFTER calling this
   * method.
   * 
   * @param scrollMode the scroll mode
   */
  public void setScrollMode(ScrollMode scrollMode) {
    getScrollSupport().setScrollMode(scrollMode);
  }

  @Override
  public void setScrollSupport(ScrollSupport support) {
    this.scrollSupport = support;
  }

  @Override
  protected void doLayout() {
    Size size = getContainerTarget().getStyleSize();

    if (GXTLogConfiguration.loggingIsEnabled()) {
      logger.finest(getId() + " doLayout size: " + size);
    }

    int w = size.getWidth() - (adjustForScroll ? XDOM.getScrollBarWidth() : 0);
    int h = size.getHeight();
    int pw = w;

    int count = getWidgetCount();

    // some columns can be percentages while others are fixed
    // so we need to make 2 passes
    for (int i = 0; i < count; i++) {
      Widget c = getWidget(i);
      c.getElement().getStyle().setPosition(Position.ABSOLUTE);

      double width = -1;

      Object d = c.getLayoutData();
      if (d instanceof HasWidth) {
        width = ((HasWidth) d).getWidth();
      }

      if (width > 1) {
        pw -= width;
      } else if (width == -1) {
        if ((c instanceof HasWidgets || c instanceof IndexedPanel) && !secondPassRequired) {
          secondPassRequired = true;
          Scheduler.get().scheduleEntry(layoutCommand);
          return;
        }

        pw -= c.getOffsetWidth();
      } else if (width < -1) {
        pw -= (w + width);
      }

      pw -= getLeftRightMargins(c);
    }

    secondPassRequired = false;

    pw = pw < 0 ? 0 : pw;

    int x = getContainerTarget().getPadding(Side.LEFT);
    int sTop = getContainerTarget().getPadding(Side.TOP);

    for (int i = 0; i < count; i++) {
      Widget c = getWidget(i);
      c.getElement().getStyle().setMargin(0, Unit.PX);

      double width = -1;
      double height = -1;

      Object d = c.getLayoutData();
      if (d instanceof HasWidth) {
        width = ((HasWidth) d).getWidth();
      }
      if (d instanceof HasHeight) {
        height = ((HasHeight) d).getHeight();
      }

      if (height >= 0 && height <= 1) {
        height = height * h;
      } else if (height < -1) {
        height = h + height;
      }

      if (width >= 0 && width <= 1) {
        width = width * pw;
      } else if (width < -1) {
        width = w + width;
      }

      int tx = x;
      int ty = sTop;
      int tw = (int) Math.floor(width);
      int th = (int) Math.floor(height);

      Margins m = null;
      if (d instanceof HasMargins) {
        m = ((HasMargins) d).getMargins();
        if (m != null) {
          tx += m.getLeft();
          ty += m.getTop();
          if (th != -1) {
            th -= m.getTop();
            th -= m.getBottom();
          }
        }
      }

      applyLayout(c, new Rectangle(tx, ty, tw, th));

      if (tw == -1) {
        tw = c.getOffsetWidth();
      }

      x += tw + (m != null ? (m.getLeft() + m.getRight()) : 0);
    }
  }

  @Override
  protected void onInsert(int index, Widget child) {
    // no op, super applies margins
  }

}
