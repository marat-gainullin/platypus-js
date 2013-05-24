/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.container;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.uibinder.client.UiChild;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IndexedPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Size;

/**
 * A layout container that supports north (top) and south (bottom) regions.
 * 
 * <p/>
 * Code Snippet:
 * 
 * <pre>
    NorthSouthContainer c = new NorthSouthContainer();
    c.setNorthWidget(new Label("North"));
    c.setSouthWidget(new Label("South"));
    RootPanel.get().add(c);
 * </pre>
 */
public class NorthSouthContainer extends SimpleContainer implements HasNorthWidget, HasSouthWidget {

  protected Widget northWidget, southWidget;

  private boolean secondPassRequired;

  @Override
  public Widget getNorthWidget() {
    return northWidget;
  }

  @Override
  public Widget getSouthWidget() {
    return southWidget;
  }

  @UiChild(limit = 1, tagname = "north")
  public void setNorthWidget(IsWidget north) {
    if (this.northWidget != null) {
      remove(this.northWidget);
    }
    if (north != null) {
      this.northWidget = north.asWidget();
      insert(this.northWidget, 0);
    }
  }

  @UiChild(limit = 1, tagname = "south")
  @Override
  public void setSouthWidget(IsWidget south) {
    if (this.southWidget != null) {
      remove(this.southWidget);
    }
    if (south != null) {
      this.southWidget = south.asWidget();
      insert(this.southWidget, getWidgetCount());
    }
  }

  @Override
  @UiChild(limit = 1, tagname = "widget")
  public void setWidget(Widget w) {
    if (this.widget != null) {
      this.widget.removeFromParent();
    }
    this.widget = w;
    if (this.widget != null) {
      insert(widget, this.northWidget != null ? 1 : 0);
    }
  }

  @Override
  protected void doLayout() {

    Size size = getContainerTarget().getStyleSize();

    int width = -1;

    if (!isAutoWidth()) {
      width = size.getWidth() - getLeftRightMargins(widget);

      if (northWidget != null) {
        applyLayout(northWidget, size.getWidth() - getLeftRightMargins(northWidget), -1);
      }
      if (southWidget != null) {
        applyLayout(southWidget, size.getWidth() - getLeftRightMargins(southWidget), -1);
      }
    }
    if (widget != null) {
      int height = -1;
      if (!isAutoHeight()) {
        if ((northWidget instanceof HasWidgets || northWidget instanceof IndexedPanel
            || southWidget instanceof HasWidgets || southWidget instanceof IndexedPanel)
            && !secondPassRequired) {
          secondPassRequired = true;
          Scheduler.get().scheduleEntry(forceLayoutCommand);
          return;
        }

        secondPassRequired = false;

        height = size.getHeight() - getTopBottomMargins(widget);
        if (northWidget != null) {
          height -= northWidget.getOffsetHeight();
          height -= getTopBottomMargins(northWidget);
        }
        if (southWidget != null) {
          height -= southWidget.getOffsetHeight();
          height -= getTopBottomMargins(southWidget);
        }
      }
      applyLayout(widget, width, height);
    }
  }

  @Override
  protected void onRemove(Widget child) {
    super.onRemove(child);
    if (child == northWidget) {
      northWidget = null;
    } else if (child == southWidget) {
      southWidget = null;
    }
  }
}
