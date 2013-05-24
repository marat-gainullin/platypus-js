/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.container;

import java.util.logging.Logger;

import com.google.gwt.uibinder.client.UiChild;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IndexedPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.GXTLogConfiguration;
import com.sencha.gxt.core.client.util.Size;

/**
 * A layout container that contains multiple widgets, each fit to the container,
 * where only a single widget can be visible at any given time. This layout
 * style is most commonly used for wizards, tab implementations, etc.
 * 
 * <p/>
 * Code Snippet:
 * 
 * <pre>
    final CardLayoutContainer c = new CardLayoutContainer();
    c.add(new Label("Card 1"));
    c.add(new Label("Card 2"));
    RootPanel.get().add(c);
    Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {
      {@literal @Override}
      public boolean execute() {
        c.setActiveWidget(c.getActiveWidget() == c.getWidget(0) ? c.getWidget(1) : c.getWidget(0));
        return true;
      }
    }, 500);
 * </pre>
 */
public class CardLayoutContainer extends InsertResizeContainer implements HasActiveWidget {

  private Widget activeWidget;

  private static Logger logger = Logger.getLogger(CardLayoutContainer.class.getName());

  /**
   * Creates a card layout container.
   */
  public CardLayoutContainer() {
    setElement(DOM.createDiv());
  }

  /**
   * Adds a widget to a card layout container with specified layout parameters.
   * 
   * @param child the widget to add to the layout container
   * @param layoutData the parameters that describe how to layout the widget
   */
  @UiChild(tagname = "child")
  public void add(IsWidget child, MarginData layoutData) {
    if (child != null) {
      child.asWidget().setLayoutData(layoutData);
    }
    super.add(child);
  }

  @Override
  public Widget getActiveWidget() {
    return activeWidget;
  }

  /**
   * Inserts the widget at the specified index in the card layout container.
   * 
   * @param w the widget to insert in the layout container
   * @param beforeIndex the insert index
   * @param layoutData the parameters that describe how to lay out the widget
   */
  public void insert(IsWidget w, int beforeIndex, MarginData layoutData) {
    if (w != null) {
      w.asWidget().setLayoutData(layoutData);
    }
    super.insert(w, beforeIndex);
  }

  /**
   * Sets the active widget.
   * 
   * @param widget the widget
   */
  public void setActiveWidget(IsWidget widget) {
    setActiveWidget(asWidgetOrNull(widget));
  }

  @Override
  public void setActiveWidget(Widget widget) {
    if (activeWidget == widget) {
      return;
    }
    if (activeWidget != null) {
      activeWidget.setVisible(false);
    }
    if (widget != null) {
      if (widget.asWidget().getParent() == this) {
        activeWidget = widget;
        activeWidget.setVisible(true);
        // forcing causes layout to execute every time this method called
        // when used with TabPanel this causes layouts to execute every time tab
        // selected which is not optimal

        // EXTGWT-1550, in example, a panel is collapsed then another tab is
        // selected. when returning to tab with collapsed panel
        // the layout executes as it was forced

        // layoutRequiredThisEventLoop = true;
        // forceLayout();

        if (GXT.isIE6() || GXT.isIE7()) {
          layoutRequiredThisEventLoop = true;
          forceLayout();
        } else {
          doLayout();
        }

      } else {
        activeWidget = widget;
        activeWidget.setVisible(true);
        add(widget);
        if (widget.asWidget().getParent() == this) {
          layoutRequiredThisEventLoop = true;
          forceLayout();
        } else {
          activeWidget = null;
        }
      }
    } else {
      activeWidget = null;
    }
  }

  protected void forceLayoutOnChildren(IndexedPanel widgets) {
    if (activeWidget != null) {
      Widget w = activeWidget;
      if (w instanceof HasLayout) {
        ((HasLayout) w).forceLayout();
      } else if (w instanceof HasWidgets && isWidgetVisible(w)) {
        forceLayoutOnChildren((HasWidgets) w);
      } else if (w instanceof IndexedPanel && isWidgetVisible(w)) {
        forceLayoutOnChildren((IndexedPanel) w);
      }
    }
  }

  @Override
  protected void doLayout() {
    if (activeWidget == null && getWidgetCount() > 0) {
      setActiveWidget(getWidget(0));
    }
    if (activeWidget != null) {
      Size size = getContainerTarget().getStyleSize();
      if (GXTLogConfiguration.loggingIsEnabled()) {
        logger.finest(getId() + " doLayout size: " + size);
      }
      int width = -1;
      if (!isAutoWidth()) {
        width = size.getWidth() - getLeftRightMargins(activeWidget);
      }
      int height = -1;
      if (!isAutoHeight()) {
        height = size.getHeight() - getTopBottomMargins(activeWidget);
      }
      applyLayout(activeWidget, width, height);
    }
  }

  @Override
  protected void onInsert(int index, Widget child) {
    super.onInsert(index, child);
    if (child != activeWidget) {
      child.setVisible(false);
    }
  }

  @Override
  protected void onRemove(Widget child) {
    super.onRemove(child);
    if (activeWidget == child) {
      activeWidget = null;
    }
    child.setVisible(true);
  }
}
