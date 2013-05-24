/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.container;

import java.util.logging.Logger;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.GXTLogConfiguration;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.Size;
import com.sencha.gxt.core.client.util.Util;
import com.sencha.gxt.widget.core.client.Component;

/**
 * A layout container for vertical columns of widgets.
 * <p/>
 * Code Snippet:
 * 
 * <pre>
    VBoxLayoutContainer c = new VBoxLayoutContainer();
    c.setVBoxLayoutAlign(VBoxLayoutAlign.LEFT);
    BoxLayoutData layoutData = new BoxLayoutData(new Margins(5, 0, 0, 5));
    c.add(new TextButton("Button 1"), layoutData);
    c.add(new TextButton("Button 2"), layoutData);
    c.add(new TextButton("Button 3"), layoutData);
    Viewport v = new Viewport();
    v.add(c);
    RootPanel.get().add(v);
 * </pre>
 */
public class VBoxLayoutContainer extends BoxLayoutContainer {
  /**
   * Alignment enumeration for horizontal alignment.
   */
  public enum VBoxLayoutAlign {
    /**
     * Children are aligned vertically at the <b>mid-width</b> of the container.
     */
    CENTER,
    /**
     * Children are aligned vertically at the <b>left</b> side of the container
     * (default).
     */
    LEFT,
    /**
     * Children are aligned vertically at the <b>right</b> side of the container
     * (default).
     */
    RIGHT,
    /**
     * Children are stretched horizontally to fill the width of the container.
     */
    STRETCH,
    /**
     * Children widths are set the size of the largest child's width.
     */
    STRETCHMAX
  }

  private VBoxLayoutAlign vBoxLayoutAlign;

  private static Logger logger = Logger.getLogger(VBoxLayoutContainer.class.getName());

  /**
   * Creates a vbox layout.
   */
  public VBoxLayoutContainer() {
    this(VBoxLayoutAlign.LEFT);
  }

  /**
   * Creates a vbox layout with the specified alignment.
   * 
   * @param align the horizontal alignment
   */
  public VBoxLayoutContainer(VBoxLayoutAlign align) {
    super();
    setVBoxLayoutAlign(align);
  }

  /**
   * Returns the horizontal alignment.
   * 
   * @return the horizontal alignment
   */
  public VBoxLayoutAlign getVBoxLayoutAlign() {
    return vBoxLayoutAlign;
  }

  /**
   * Sets the horizontal alignment for child items (defaults to LEFT).
   * 
   * @param vBoxLayoutAlign the horizontal alignment
   */
  public void setVBoxLayoutAlign(VBoxLayoutAlign vBoxLayoutAlign) {
    this.vBoxLayoutAlign = vBoxLayoutAlign;
  }

  @Override
  protected void doLayout() {
    Size size = getElement().getStyleSize();

    if (GXTLogConfiguration.loggingIsEnabled()) {
      logger.finest(getId() + " doLayout  size: " + size);
    }

    int w = size.getWidth() - getScrollOffset();
    int h = size.getHeight();

    int pl = 0;
    int pt = 0;
    int pb = 0;
    int pr = 0;
    if (getPadding() != null) {
      pl = getPadding().getLeft();
      pt = getPadding().getTop();
      pb = getPadding().getBottom();
      pr = getPadding().getRight();
    }

    int stretchWidth = w - pl - pr;
    int totalFlex = 0;
    int totalHeight = 0;
    int maxWidth = 0;
    for (int i = 0, len = getWidgetCount(); i < len; i++) {
      Widget widget = getWidget(i);
      widget.getElement().getStyle().setMargin(0, Unit.PX);

      // callLayout(widget, false);

      BoxLayoutData layoutData = null;
      Object d = widget.getLayoutData();
      if (d != null && d instanceof BoxLayoutData) {
        layoutData = (BoxLayoutData) d;
      } else {
        layoutData = new BoxLayoutData();
      }
      Margins cm = layoutData.getMargins();
      if (cm == null) {
        cm = new Margins(0);
      }
      totalFlex += layoutData.getFlex();
      totalHeight += widget.getOffsetHeight() + cm.getTop() + cm.getBottom();
      maxWidth = Math.max(maxWidth, widget.getOffsetWidth() + cm.getLeft() + cm.getRight());
    }

    int innerCtWidth = maxWidth + pl + pr;

    if (vBoxLayoutAlign.equals(VBoxLayoutAlign.STRETCH)) {
      getContainerTarget().setSize(w, h, true);
    } else {
      getContainerTarget().setSize(w = Math.max(w, innerCtWidth), h, true);
    }

    int extraHeight = h - totalHeight - pt - pb;
    int allocated = 0;
    int cw, ch, cl;
    int availableWidth = w - pl - pr;

    if (getPack().equals(BoxLayoutPack.CENTER)) {
      pt += extraHeight / 2;
    } else if (getPack().equals(BoxLayoutPack.END)) {
      pt += extraHeight;
    }

    for (int i = 0, len = getWidgetCount(); i < len; i++) {
      Widget widget = getWidget(i);
      BoxLayoutData layoutData = null;
      Object d = widget.getLayoutData();
      if (d != null && d instanceof BoxLayoutData) {
        layoutData = (BoxLayoutData) d;
      } else {
        layoutData = new BoxLayoutData();
      }
      Margins cm = layoutData.getMargins();
      if (cm == null) {
        cm = new Margins(0);
      }
      cw = widget.getOffsetWidth();
      ch = widget.getOffsetHeight();
      pt += cm.getTop();
      if (vBoxLayoutAlign.equals(VBoxLayoutAlign.CENTER)) {
        int diff = availableWidth - (cw + cm.getLeft() + cm.getRight());
        if (diff == 0) {
          cl = pl + cm.getLeft();
        } else {
          cl = pl + cm.getLeft() + (diff / 2);
        }
      } else {
        if (vBoxLayoutAlign.equals(VBoxLayoutAlign.RIGHT)) {
          cl = w - (pr + cm.getRight() + cw);
        } else {
          cl = pl + cm.getLeft();
        }
      }

      boolean component = widget instanceof Component;
      Component c = null;
      if (component) {
        c = (Component) widget;
      }

      int height = -1;
      if (component) {
        c.setPosition(cl, pt);
      } else {
        XElement.as(widget.getElement()).setLeftTop(cl, pt);
      }

      if (getPack().equals(BoxLayoutPack.START) && layoutData.getFlex() > 0) {
        int add = (int) Math.floor(extraHeight * (layoutData.getFlex() / totalFlex));
        allocated += add;
        if (isAdjustForFlexRemainder() && i == getWidgetCount() - 1) {
          add += extraHeight - allocated;
        }
        ch += add;
        height = ch;
      }
      if (vBoxLayoutAlign.equals(VBoxLayoutAlign.STRETCH)) {
        applyLayout(
            widget,
            Util.constrain(stretchWidth - cm.getLeft() - cm.getRight(), layoutData.getMinSize(), layoutData.getMaxSize()),
            height);
      } else if (vBoxLayoutAlign.equals(VBoxLayoutAlign.STRETCHMAX)) {
        applyLayout(widget,
            Util.constrain(maxWidth - cm.getLeft() - cm.getRight(), layoutData.getMinSize(), layoutData.getMaxSize()),
            height);
      } else if (height > 0) {
        applyLayout(widget, -1, height);
      }
      pt += ch + cm.getBottom();
    }
  }

}
