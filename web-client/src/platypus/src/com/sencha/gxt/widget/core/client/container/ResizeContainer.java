/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.container;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.AnimatedLayout;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IndexedPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.Style.Side;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.Rectangle;
import com.sencha.gxt.core.client.util.Size;
import com.sencha.gxt.widget.core.client.Component;

/**
 * A base class for layout containers that require resize support.
 */
public abstract class ResizeContainer extends Container implements RequiresResize, ProvidesResize, HasLayout {

  protected ScheduledCommand forceLayoutCommand = new ScheduledCommand() {
    @Override
    public void execute() {
      forceLayout();
    }
  };

  protected ScheduledCommand layoutCommand = new ScheduledCommand() {
    @Override
    public void execute() {
      doLayout();
    }
  };

  protected boolean hadLayoutRunning;
  protected boolean layoutRequiredThisEventLoop = true;
  protected boolean layoutRunning;

  /**
   * By default, a container calls doLayout when resized which does not force
   * children to be laid out. To force the layout, set to true.
   */
  protected boolean forceLayoutOnResize = false;

  @Override
  public void forceLayout() {
    if (layoutRequiredThisEventLoop && isAttached() && !isLayoutRunning() && !isParentLayoutRunning()
        && parentIsOrWasLayoutRunning() && isWidgetVisible(this)) {
      layoutRequiredThisEventLoop = false;
      hadLayoutRunning = true;
      Scheduler.get().scheduleFinally(new ScheduledCommand() {
        @Override
        public void execute() {
          layoutRequiredThisEventLoop = true;
        }
      });
      onBeforeDoLayout();
      layoutRunning = true;

      doLayout();
      layoutRunning = false;
      forceLayoutOnChildren((IndexedPanel) this);
    }
  }

  @Override
  public boolean isLayoutRunning() {
    return layoutRunning;
  }

  @Override
  public boolean isOrWasLayoutRunning() {
    return hadLayoutRunning;
  }

  public void onResize() {
    if (isAttached()) {
      Size s = XElement.as(getElement().getParentElement()).getSize(true);
      setPixelSize(s.getWidth(), s.getHeight());
    }
  }

  protected void applyLayout(final Widget widget, int width, int height) {
    if (widget instanceof Component) {
      int ww = width == -1 ? Integer.MIN_VALUE : width;
      int hh = height == -1 ? Integer.MIN_VALUE : height;
      ((Component) widget).setPixelSize(ww, hh);
    } else {
      XElement.as(widget.getElement()).setSize(width, height, true);
      if (widget instanceof RequiresResize) {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
          @Override
          public void execute() {
            ((RequiresResize) widget).onResize();
          }
        });
      }
    }
  }

  protected void applyLayout(final Widget component, Rectangle box) {
    if (component instanceof Component) {
      Component c = (Component) component;
      c.setPosition(box.getX(), box.getY());
      applyLayout(c, box.getWidth(), box.getHeight());
    } else {
      XElement.as(component.getElement()).setLeftTop(box.getX(), box.getY());
      applyLayout(component, box.getWidth(), box.getHeight());
    }
  }

  protected abstract void doLayout();

  protected void forceLayoutOnChildren(HasWidgets widgets) {
    for (Widget w : widgets) {
      if (w instanceof HasLayout) {
        ((HasLayout) w).forceLayout();
      } else if (w instanceof HasWidgets && isWidgetVisible(w)) {
        forceLayoutOnChildren((HasWidgets) w);
      } else if (w instanceof IndexedPanel && isWidgetVisible(w)) {
        forceLayoutOnChildren((IndexedPanel) w);
      }
    }
  }

  protected void forceLayoutOnChildren(IndexedPanel widgets) {
    for (int index = 0, len = widgets.getWidgetCount(); index < len; index++) {
      Widget w = widgets.getWidget(index);
      if (w instanceof HasLayout) {
        ((HasLayout) w).forceLayout();
      } else if (w instanceof HasWidgets && isWidgetVisible(w)) {
        forceLayoutOnChildren((HasWidgets) w);
      } else if (w instanceof IndexedPanel && isWidgetVisible(w)) {
        forceLayoutOnChildren((IndexedPanel) w);
      }
    }
  }

  protected int getLeftRightMargins(Widget w) {
    if (w == null) {
      return 0;
    }
    Object data = w.getLayoutData();
    if (data instanceof HasMargins) {
      Margins margins = ((HasMargins) data).getMargins();
      if (margins == null) {
        return 0;
      }
      int tot = 0;
      if (margins.getLeft() != -1) {
        tot += margins.getLeft();
      }
      if (margins.getRight() != -1) {
        tot += margins.getRight();
      }
      return tot;
    }
    return 0;
  }

  protected Widget getParentLayoutWidget() {
    return this;
  }

  protected int getSideMargins(Widget w) {
    if (GXT.isWebKit()) {
      Object data = w.getLayoutData();
      if (data != null && data instanceof MarginData) {
        MarginData m = (MarginData) data;
        Margins margins = m.getMargins();
        if (margins == null) {
          return 0;
        }
        int tot = 0;
        if (margins.getLeft() != -1) {
          tot += margins.getLeft();
        }
        if (margins.getRight() != -1) {
          tot += margins.getRight();
        }
        return tot;
      }
    } else {
      return w.getElement().<XElement> cast().getMargins(Side.LEFT, Side.RIGHT);
    }
    return 0;
  }

  protected int getTopBottomMargins(Widget w) {
    if (w == null) {
      return 0;
    }
    Object data = w.getLayoutData();
    if (data instanceof HasMargins) {
      Margins margins = ((HasMargins) data).getMargins();
      if (margins == null) {
        return 0;
      }
      int tot = 0;
      if (margins.getLeft() != -1) {
        tot += margins.getTop();
      }
      if (margins.getRight() != -1) {
        tot += margins.getBottom();
      }
      return tot;
    }

    return 0;
  }

  protected boolean isWidgetVisible(Widget w) {
    return !XElement.as(w.getElement()).isStyleAttribute("display", "none");
  }

  protected void onBeforeDoLayout() {
  }

  @Override
  protected void onLoad() {
    super.onLoad();
    if (this.getParent() instanceof AnimatedLayout) {
      Scheduler.get().scheduleDeferred(forceLayoutCommand);
    } else {
      Scheduler.get().scheduleFinally(forceLayoutCommand);
    }
  }

  @Override
  protected void onResize(int width, int height) {
    super.onResize(width, height);
    if (forceLayoutOnResize) {
      layoutRequiredThisEventLoop = true;
      Scheduler.get().scheduleFinally(forceLayoutCommand);
    } else {
      Scheduler.get().scheduleFinally(layoutCommand);
    }
  }

  private boolean isParentLayoutRunning() {
    Widget parent = getParentLayoutWidget();
    while ((parent = parent.getParent()) != null) {
      if (parent instanceof HasWidgets || parent instanceof IndexedPanel) {
        if (parent instanceof HasLayout && ((HasLayout) parent).isLayoutRunning()) {
          return true;
        }
      } else {
        return false;
      }
    }
    return false;
  }

  private boolean parentIsOrWasLayoutRunning() {
    Widget parent = getParentLayoutWidget();
    while ((parent = parent.getParent()) != null) {
      if (parent instanceof HasWidgets || parent instanceof IndexedPanel) {
        if (parent instanceof HasLayout) {
          if (((HasLayout) parent).isOrWasLayoutRunning()) {
            return true;
          } else {
            return false;
          }
        }
      } else {
        return true;
      }
    }
    return true;
  }
}
