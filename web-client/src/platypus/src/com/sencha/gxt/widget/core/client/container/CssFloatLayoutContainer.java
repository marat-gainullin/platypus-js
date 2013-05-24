/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.container;

import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.HasScrollHandlers;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiChild;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.GXTLogConfiguration;
import com.sencha.gxt.core.client.Style.Side;
import com.sencha.gxt.core.client.dom.DefaultScrollSupport;
import com.sencha.gxt.core.client.dom.HasScrollSupport;
import com.sencha.gxt.core.client.dom.ScrollSupport;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Size;

/**
 * A layout container that uses the CSS float style to enable widgets to float
 * around other widgets.
 * 
 * <p />
 * Code Snippet:
 * 
 * <pre>
    CssFloatLayoutContainer c = new CssFloatLayoutContainer();
    HTML rectangle = new HTML("I'm a Red<br/>Rectangle");
    Label text = new Label("This text will flow around the Red Rectangle because that's the way things work in CssFloatLayoutContainer. You may need to resize the browser window to see the effect.");
    c.add(rectangle, new CssFloatData(100));
    c.add(text);
    rectangle.getElement().getStyle().setBackgroundColor("red");
    text.getElement().getStyle().setFloat(Float.NONE);
    text.getElement().getStyle().setDisplay(Display.INLINE);
    Viewport v = new Viewport();
    v.add(c);
    RootPanel.get().add(v);
 * </pre>
 */
public class CssFloatLayoutContainer extends InsertResizeContainer implements HasScrollHandlers, HasScrollSupport {

  /**
   * Specifies widget layout parameters that control the size of the widget.
   */
  public static class CssFloatData implements HasSize, LayoutData {

    private double width = -1;

    /**
     * Creates layout data for the CSS float layout container with the default
     * value for <code>width</code> (-1 = use widget's default width).
     */
    public CssFloatData() {

    }

    /**
     * Creates layout data for the CSS float layout container using the
     * specified width. Values <= 1 are treated as percentages.
     * 
     * @param width the width of the widget
     */
    public CssFloatData(double width) {
      this.width = width;
    }

    @Override
    public double getSize() {
      return width;
    }

    /**
     * Sets the width of the column.
     * 
     * @param size the width, values <= 1 treated a percentages.
     */
    @Override
    public void setSize(double size) {
      this.width = size;
    }

  }

  public interface CssFloatLayoutAppearance {

    XElement getContainerTarget(XElement parent);

    void onInsert(Widget child);

    void onRemove(Widget child);

    void render(SafeHtmlBuilder sb);

  }

  protected boolean adjustForScroll = false;
  protected final CssFloatLayoutAppearance appearance;

  private Style.Float styleFloat = LocaleInfo.getCurrentLocale().isRTL() ? Style.Float.RIGHT : Style.Float.LEFT;
  private ScrollSupport scrollSupport;

  private static Logger logger = Logger.getLogger(CssFloatLayoutContainer.class.getName());

  /**
   * Creates a CSS float layout container with the default appearance.
   */
  public CssFloatLayoutContainer() {
    this(GWT.<CssFloatLayoutAppearance> create(CssFloatLayoutAppearance.class));
  }

  /**
   * Creates a CSS float layout container with the specified appearance.
   * 
   * @param appearance the appearance of the CSS float layout container
   */
  public CssFloatLayoutContainer(CssFloatLayoutAppearance appearance) {
    this.appearance = appearance;
    SafeHtmlBuilder sb = new SafeHtmlBuilder();
    appearance.render(sb);
    setElement(XDOM.create(sb.toSafeHtml()));
  }

  /**
   * Adds a widget to the CSS float layout container with the specified layout
   * parameters.
   * 
   * @param child the widget to add to the layout container
   * @param layoutData the parameters that describe how to lay out the widget
   */
  @UiChild(tagname = "child")
  public void add(IsWidget child, CssFloatData layoutData) {
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
   * Returns the value of the CSS float property.
   * 
   * @return the value of the CSS float property
   */
  public Style.Float getStyleFloat() {
    return styleFloat;
  }

  /**
   * Inserts the widget at the specified index in the CSS float layout
   * container.
   * 
   * @param w the widget to insert in the layout container
   * @param beforeIndex the insert index
   * @param layoutData the parameters that describe how to lay out the widget
   */
  public void insert(IsWidget w, int beforeIndex, CssFloatData layoutData) {
    if (w != null) {
      w.asWidget().setLayoutData(layoutData);
    }
    super.insert(w, beforeIndex);
  }

  /**
   * Returns true if adjust for scroll is enabled.
   * 
   * @return the adjust for scroll state
   */
  public boolean isAdjustForScroll() {
    return adjustForScroll;
  }

  /**
   * True to adjust the container width calculations to account for the scroll
   * bar (defaults to false).
   * 
   * @param adjustForScroll the adjust for scroll state
   */
  public void setAdjustForScroll(boolean adjustForScroll) {
    this.adjustForScroll = adjustForScroll;
  }

  /**
   * Sets the scroll mode on the container's <code>ScrollSupport</code>
   * instance. The scroll mode will not be preserved if
   * {@link #setScrollSupport(ScrollSupport)} is called after calling this
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

  /**
   * Sets the value of the CSS float property.
   * 
   * @param styleFloat the value of the CSS float property
   */
  public void setStyleFloat(Style.Float styleFloat) {
    this.styleFloat = styleFloat;
  }

  @Override
  protected void doLayout() {
    Size size = getContainerTarget().getStyleSize();

    if (GXTLogConfiguration.loggingIsEnabled()) {
      logger.finest(getId() + " doLayout size: " + size);
    }

    int w = size.getWidth() - (adjustForScroll ? XDOM.getScrollBarWidth() : 0);
    w -= getContainerTarget().getFrameWidth(Side.LEFT, Side.RIGHT);
    int pw = w;

    int count = getWidgetCount();

    // some columns can be percentages while others are fixed
    // so we need to make 2 passes
    for (int i = 0; i < count; i++) {
      Widget widget = getWidget(i);

      CssFloatData layoutData = null;
      Object d = widget.getLayoutData();
      if (d != null && d instanceof CssFloatData) {
        layoutData = (CssFloatData) d;
      } else {
        layoutData = new CssFloatData();
      }

      if (layoutData.getSize() > 1) {
        pw -= layoutData.getSize();
      }

      pw -= getSideMargins(widget);
    }

    pw = pw < 0 ? 0 : pw;

    for (int i = 0; i < count; i++) {
      Widget widget = getWidget(i);

      CssFloatData layoutData = null;
      Object d = widget.getLayoutData();
      if (d != null && d instanceof CssFloatData) {
        layoutData = (CssFloatData) d;
      } else {
        layoutData = new CssFloatData();
      }

      int width = -1;
      double s = layoutData.getSize();
      if (s > 0 && s <= 1) {
        width = (int) (s * pw);
      } else {
        width = (int) s;
      }

      applyLayout(widget, width, -1);

    }
  }

  @Override
  protected XElement getContainerTarget() {
    return appearance.getContainerTarget(getElement());
  }

  @Override
  protected void onInsert(int index, Widget child) {
    super.onInsert(index, child);
    child.getElement().getStyle().setFloat(styleFloat);
    appearance.onInsert(child);
  }

  @Override
  protected void onRemove(Widget child) {
    super.onRemove(child);
    appearance.onRemove(child);
  }

}
