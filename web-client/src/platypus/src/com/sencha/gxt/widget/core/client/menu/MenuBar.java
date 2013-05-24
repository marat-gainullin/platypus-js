/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.Style.Anchor;
import com.sencha.gxt.core.client.Style.AnchorAlignment;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.util.KeyNav;
import com.sencha.gxt.widget.core.client.ComponentHelper;
import com.sencha.gxt.widget.core.client.container.InsertContainer;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;

public class MenuBar extends InsertContainer {

  public static interface MenuBarAppearance {

    void render(SafeHtmlBuilder builder);
  }

  class Handler implements HideHandler {

    @Override
    public void onHide(HideEvent event) {
      autoSelect = false;
      focus();
      autoSelect = true;
      if (active != null) active.expanded = false;
    }

  }
  protected MenuBarItem active;

  protected final MenuBarAppearance appearance;

  private boolean autoSelect = true;
  private Handler handler = new Handler();

  public MenuBar() {
    this(GWT.<MenuBarAppearance> create(MenuBarAppearance.class));
  }

  public MenuBar(MenuBarAppearance appearance) {
    this.appearance = appearance;

    SafeHtmlBuilder builder = new SafeHtmlBuilder();
    appearance.render(builder);

    setElement(XDOM.create(builder.toSafeHtml()));
    sinkEvents(Event.ONCLICK | Event.ONFOCUS | Event.ONBLUR);

    getElement().setTabIndex(-1);
    getElement().setAttribute("hideFocus", "true");

    new KeyNav(this) {
      @Override
      public void onKeyPress(NativeEvent evt) {
        MenuBar.this.onKeyPress(evt);
      }
    };
  }

  @Override
  public void onBrowserEvent(Event event) {
    super.onBrowserEvent(event);
    switch (event.getTypeInt()) {
      case Event.ONCLICK:
        onClick(event);
        break;
      case Event.ONFOCUS:
        if (autoSelect && active == null && getWidgetCount() > 0) {
          setActiveItem((MenuBarItem)getWidget(0), false);
        }
        break;
      case Event.ONBLUR:
        if (active != null && !active.expanded) {
          onDeactivate(active);
        }
        break;
    }
  }

  /**
   * Sets the active item.
   * 
   * @param item the item to activate
   * @param expand true to expand the item's menu
   */
  public void setActiveItem(final MenuBarItem item, boolean expand) {
    if (active != item) {
      if (active != null) {
        onDeactivate(active);
      }
      onActivate(item);

      // if (GXT.isFocusManagerEnabled()) {
      // FocusFrame.get().frame(active);
      // }

      if (expand) {
        expand(item, true);
      }
    }
  }

  /**
   * Toggles the given item.
   * 
   * @param item the item to toggle
   */
  public void toggle(MenuBarItem item) {
    if (item == active) {
      if (item.expanded) {
        collapse(item);
      } else {
        expand(item, false);
      }
    } else {
      setActiveItem(item, true);
    }
  }

  protected void collapse(MenuBarItem item) {
    item.menu.hide();
    item.expanded = false;
  }

  protected void expand(MenuBarItem item, boolean selectFirst) {
    item.menu.setFocusOnShow(false);
    item.menu.show(item.getElement(), new AnchorAlignment(Anchor.TOP_LEFT, Anchor.BOTTOM_LEFT), new int[] {0, 1});
    item.expanded = true;
    if (item.menu.getWidgetCount() > 0 && selectFirst) {
      item.menu.setActiveItem(item.menu.getWidget(0), false);
    }
  }

  protected void onActivate(MenuBarItem item) {
    active = item;
    // Accessibility.setState(getElement(), "aria-activedescendant",
    // item.getId());

    item.getAppearance().onActive(item.getElement(), true);
    // item.addStyleName(item.getBaseStyle() + "-active");
    // item.addStyleName(item.getBaseStyle() + "-over");
  }

  protected void onClick(Event event) {
    event.stopPropagation();
    event.preventDefault();

    MenuBarItem item = (MenuBarItem) findWidget(event.getEventTarget().<Element> cast());
    if (item != null) {
      toggle(item);
    }
  }

  protected void onDeactivate(MenuBarItem item) {
    if (item.expanded) {
      item.menu.hide();
      item.expanded = false;
    }

    item.getAppearance().onActive(item.getElement(), false);
    item.getAppearance().onOver(item.getElement(), false);
    // item.removeStyleName(item.getBaseStyle() + "-active");
    // item.removeStyleName(item.getBaseStyle() + "-over");
    // if (GXT.isFocusManagerEnabled()) {
    // FocusFrame.get().unframe();
    // }
    if (active == item) {
      active = null;
    }
  }

  protected void onDown(NativeEvent event) {
    if (active != null && getWidgetCount() > 0) {
      event.preventDefault();
      event.stopPropagation();
      if (active.expanded) {
        active.menu.focus();
        active.menu.setActiveItem(active.menu.getWidget(0), false);
      } else {
        expand(active, true);
      }
    }
  }

  @Override
  protected void onInsert(int index, Widget child) {
    super.onInsert(index, child);
    MenuBarItem item = (MenuBarItem) child;
    item.menu.addHideHandler(handler);
  }

  protected void onKeyPress(NativeEvent evt) {
    switch (evt.getKeyCode()) {
      case KeyCodes.KEY_DOWN:
        onDown(evt);
        break;
      case KeyCodes.KEY_LEFT:
        onLeft(evt);
        break;

      case KeyCodes.KEY_RIGHT:
        onRight(evt);
        break;

    }

  }

  protected void onLeft(NativeEvent event) {
    if (active != null && getWidgetCount() > 1) {
      int idx = getWidgetIndex(active);
      idx = idx != 0 ? idx - 1 : getWidgetCount() - 1;
      MenuBarItem item = (MenuBarItem) getWidget(idx);
      setActiveItem(item, item.expanded);
    }
  }

  @Override
  protected void onRemove(Widget child) {
    super.onRemove(child);
    MenuBarItem item = (MenuBarItem) child;
    ComponentHelper.removeHandler(item.menu, HideEvent.getType(), handler);
  }

  protected void onRight(NativeEvent event) {
    if (active != null && getWidgetCount() > 1) {
      int idx = getWidgetIndex(active);
      idx = idx != getWidgetCount() - 1 ? idx + 1 : 0;
      MenuBarItem item = (MenuBarItem) getWidget(idx);
      setActiveItem(item, item.expanded);
    }
  }

}
