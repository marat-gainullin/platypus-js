/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.HasBeforeSelectionHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiChild;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IndexedPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.Style.ScrollDirection;
import com.sencha.gxt.core.client.Style.Side;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.AccessStack;
import com.sencha.gxt.core.client.util.Size;
import com.sencha.gxt.fx.client.FxElement;
import com.sencha.gxt.fx.client.animation.AfterAnimateEvent;
import com.sencha.gxt.fx.client.animation.AfterAnimateEvent.AfterAnimateHandler;
import com.sencha.gxt.fx.client.animation.Fx;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.container.CardLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HasActiveWidget;
import com.sencha.gxt.widget.core.client.container.HasLayout;
import com.sencha.gxt.widget.core.client.event.BeforeCloseEvent;
import com.sencha.gxt.widget.core.client.event.BeforeCloseEvent.BeforeCloseHandler;
import com.sencha.gxt.widget.core.client.event.BeforeCloseEvent.HasBeforeCloseHandlers;
import com.sencha.gxt.widget.core.client.event.CloseEvent;
import com.sencha.gxt.widget.core.client.event.CloseEvent.CloseHandler;
import com.sencha.gxt.widget.core.client.event.CloseEvent.HasCloseHandlers;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

/**
 * A basic tab container.
 * 
 * <p />
 * Code snippet:
 * 
 * <pre>
 *   TabPanel panel = new TabPanel();
 *   panel.setTabScroll(true);
 *   panel.setAnimScroll(true);
 *   panel.add(new Label("Tab 1 Content"), new TabItemConfig("Tab 1", true));
 *   panel.add(new Label("Tab 2 Content"), new TabItemConfig("Tab 2", true));
 * </pre>
 */
public class TabPanel extends Component implements IndexedPanel.ForIsWidget, HasActiveWidget,
    HasBeforeSelectionHandlers<Widget>, HasSelectionHandlers<Widget>, HasBeforeCloseHandlers<Widget>,
    HasCloseHandlers<Widget>, HasLayout, HasWidgets {

  @SuppressWarnings("javadoc")
  public static interface TabPanelAppearance {

    void createScrollers(XElement parent);

    XElement getBar(XElement parent);

    XElement getBody(XElement parent);

    String getItemSelector();

    XElement getScrollLeft(XElement parent);

    XElement getScrollRight(XElement parent);

    XElement getStripEdge(XElement parent);

    XElement getStripWrap(XElement parent);

    void insert(XElement parent, TabItemConfig config, int index);

    boolean isClose(XElement target);

    void onDeselect(Element item);

    void onMouseOut(XElement parent, Event event);

    void onMouseOver(Event event);

    void onScrolling(XElement bar, boolean scrolling);

    void onSelect(Element item);

    void render(SafeHtmlBuilder builder);

    void setItemWidth(XElement element, int width);

    void updateItem(XElement item, TabItemConfig config);

    void updateScrollButtons(XElement parent);

  }

  public static interface TabPanelBottomAppearance extends TabPanelAppearance {

  }

  @SuppressWarnings("javadoc")
  public interface TabPanelMessages {

    String closeOtherTabs();

    String closeTab();

  }

  protected class DefaultTabPanelMessages implements TabPanelMessages {

    public String closeOtherTabs() {
      return DefaultMessages.getMessages().tabPanelItem_closeOtherText();
    }

    public String closeTab() {
      return DefaultMessages.getMessages().tabPanelItem_closeText();
    }

  }

  protected TabPanelAppearance appearance;

  protected Menu closeContextMenu;
  private boolean animScroll = true;
  private boolean autoSelect = true;
  private boolean bodyBorder = true;
  private boolean closeMenu = false;
  private TabPanelMessages messages;
  private boolean resizeTabs = false;
  private int scrollDuration = 150;
  private int scrollIncrement = 100;
  private boolean scrolling;
  private AccessStack<Widget> stack;
  private boolean tabScroll = false;
  private Widget contextMenuItem;

  private int tabMargin = 2;
  private int tabWidth = 120;
  private int minTabWidth = 30;

  private HashMap<Widget, TabItemConfig> configMap = new HashMap<Widget, TabItemConfig>();
  private CardLayoutContainer container = new CardLayoutContainer() {
    @Override
    protected Widget getParentLayoutWidget() {
      return container.getParent();
    };
  };

  /**
   * Creates a new tab panel with the default appearance.
   */
  public TabPanel() {
    this(GWT.<TabPanelAppearance> create(TabPanelAppearance.class));
  }

  /**
   * Creates a new tab panel with the specified appearance.
   * 
   * @param appearance the appearance of the tab panel
   */
  public TabPanel(TabPanelAppearance appearance) {
    this.appearance = appearance;

    SafeHtmlBuilder sb = new SafeHtmlBuilder();
    appearance.render(sb);

    setElement(XDOM.create(sb.toSafeHtml()));

    ComponentHelper.setParent(this, container);
    appearance.getBody(getElement()).appendChild(container.getElement());

    setDeferHeight(true);
  }

  /**
   * Adds an item to the tab panel with the specified text.
   * 
   * @param widget
   * @param config the configuration of the tab
   */
  @UiChild(tagname = "child")
  public void add(IsWidget widget, TabItemConfig config) {
    add(asWidgetOrNull(widget), config);
  }

  @Override
  public void add(Widget w) {
    throw new UnsupportedOperationException("this add method not supported");
  }

  /**
   * Adds an item to the tab panel with the specified text. Shorthand for
   * {@link #add(Widget, TabItemConfig)}.
   * 
   * @param widget the widget to add to the tab panel
   * @param text the text for the tab
   */
  public void add(Widget widget, String text) {
    insert(widget, getWidgetCount(), new TabItemConfig(text));
  }

  /**
   * Adds an item to the tab panel with the specified tab configuration.
   * 
   * @param widget the item to add to the tab panel
   * @param config the configuration of the tab
   */
  public void add(Widget widget, TabItemConfig config) {
    insert(widget, getWidgetCount(), config);
  }

  @Override
  public HandlerRegistration addBeforeCloseHandler(BeforeCloseHandler<Widget> handler) {
    return addHandler(handler, BeforeCloseEvent.getType());
  }

  @Override
  public HandlerRegistration addBeforeSelectionHandler(BeforeSelectionHandler<Widget> handler) {
    return addHandler(handler, BeforeSelectionEvent.getType());
  }

  @Override
  public HandlerRegistration addCloseHandler(CloseHandler<Widget> handler) {
    return addHandler(handler, CloseEvent.getType());
  }

  @Override
  public HandlerRegistration addSelectionHandler(SelectionHandler<Widget> handler) {
    return addHandler(handler, SelectionEvent.getType());
  }

  @Override
  public void clear() {
    container.clear();
  }

  /**
   * Searches for an item based on its id and optionally the item's text.
   * 
   * @param id the item id
   * @param checkText true to match the items id and text
   * @return the item
   */
  public Widget findItem(String id, boolean checkText) {
    int count = container.getWidgetCount();
    for (int i = 0; i < count; i++) {
      Widget item = container.getWidget(i);
      String widgetId = ComponentHelper.getWidgetId(item);

      if (widgetId.equals(id)) return item;
      if (item instanceof Component && ((Component) item).getItemId().equals(id)) return item;
    }
    return null;
  }

  @Override
  public void forceLayout() {
    container.forceLayout();

  }

  @Override
  public Widget getActiveWidget() {
    return container.getActiveWidget();
  }

  /**
   * Returns true if scrolling is animated.
   * 
   * @return the animation scroll state
   */
  public boolean getAnimScroll() {
    return animScroll;
  }

  /**
   * Returns true if the body border is enabled.
   * 
   * @return the body border state
   */
  public boolean getBodyBorder() {
    return bodyBorder;
  }

  /**
   * Returns the tab item config for the given widget.
   * 
   * @param widget the widget
   * @return the config or null
   */
  public TabItemConfig getConfig(Widget widget) {
    return configMap.get(widget);
  }

  /**
   * Returns the internal card layout container.
   * 
   * @return the card layout container
   */
  public CardLayoutContainer getContainer() {
    return container;
  }

  /**
   * Returns the tab panel messages.
   * 
   * @return the messages
   */
  public TabPanelMessages getMessages() {
    if (messages == null) {
      messages = new DefaultTabPanelMessages();
    }
    return messages;
  }

  /**
   * Returns the minimum tab width.
   * 
   * @return the minimum tab width
   */
  public int getMinTabWidth() {
    return minTabWidth;
  }

  /**
   * Returns true if tab resizing is enabled.
   * 
   * @return the tab resizing state
   */
  public boolean getResizeTabs() {
    return resizeTabs;
  }

  /**
   * Returns the scroll duration in milliseconds.
   * 
   * @return the duration
   */
  public int getScrollDuration() {
    return scrollDuration;
  }

  /**
   * Returns the panel's tab margin.
   * 
   * @return the margin
   */
  public int getTabMargin() {
    return tabMargin;
  }

  /**
   * Returns true if tab scrolling is enabled.
   * 
   * @return the tab scroll state
   */
  public boolean getTabScroll() {
    return tabScroll;
  }

  /**
   * Returns the default tab width.
   * 
   * @return the width
   */
  public int getTabWidth() {
    return tabWidth;
  }

  @Override
  public Widget getWidget(int index) {
    return container.getWidget(index);
  }

  @Override
  public int getWidgetCount() {
    return container.getWidgetCount();
  }

  @Override
  public int getWidgetIndex(IsWidget child) {
    return container.getWidgetIndex(child);
  }

  public int getWidgetIndex(Widget widget) {
    return container.getWidgetIndex(widget);
  }

  /**
   * Inserts the specified item into the tab panel.
   * 
   * @param widget the item to insert
   * @param index the insert index
   * @param config the configuration of the tab item
   */
  public void insert(Widget widget, int index, TabItemConfig config) {
    configMap.put(widget, config);
    container.insert(widget, index);
    appearance.insert(getElement(), config, index);

    if (getActiveWidget() == null && autoSelect) {
      setActiveWidget(widget);
    }

    delegateUpdates();

    if (getWidgetCount() == 1) {
      syncSize();
    }
  }

  /**
   * Returns true if auto select is enabled.
   * 
   * @return the auto select state
   */
  public boolean isAutoSelect() {
    return autoSelect;
  }

  /**
   * Returns true if close context menu is enabled.
   * 
   * @return the close menu state
   */
  public boolean isCloseContextMenu() {
    return closeMenu;
  }

  @Override
  public boolean isLayoutRunning() {
    return container.isLayoutRunning();
  }

  @Override
  public boolean isOrWasLayoutRunning() {
    return container.isOrWasLayoutRunning();
  }

  @Override
  public Iterator<Widget> iterator() {
    return container.iterator();
  }

  @Override
  public void onBrowserEvent(Event event) {
    XElement target = event.getEventTarget().cast();
    boolean isbar = appearance.getBar(getElement()).isOrHasChild(target);
    boolean orig = disableContextMenu;

    // allow right clicks in tab panel body
    if (!isbar && disableContextMenu) {
      disableContextMenu = false;
    }

    super.onBrowserEvent(event);

    if (!isbar) {
      disableContextMenu = orig;
    }

    if (!appearance.getBar(getElement()).isOrHasChild(target)) {
      return;
    }

    Element item = findItem(target);
    if (item != null) {
      int index = itemIndex(item);
      TabItemConfig config = getConfig(getWidget(index));
      if (config != null && !config.isEnabled()) {
        return;
      }
    }

    switch (event.getTypeInt()) {
      case Event.ONCLICK:
        onClick(event);
        if (appearance.getScrollLeft(getElement()) != null
            && target.isOrHasChild(appearance.getScrollLeft(getElement()))) {
          event.stopPropagation();
          onScrollLeft();
        }
        if (appearance.getScrollRight(getElement()) != null
            && target.isOrHasChild(appearance.getScrollRight(getElement()))) {
          event.stopPropagation();
          onScrollRight();
        }
        break;
      case Event.ONMOUSEOVER:
        appearance.onMouseOver(event);
        break;
      case Event.ONMOUSEOUT:

        appearance.onMouseOut(getElement(), event);
        break;
    }
  }

  @Override
  public boolean remove(int index) {
    return remove(getWidget(index));
  }

  public boolean remove(Widget child) {
    int idx = getWidgetIndex(child);
    Widget activeWidget = getActiveWidget();
    boolean removed = container.remove(child);

    if (removed) {
      if (stack != null) {
        stack.remove(child);
      }

      Element item = findItem(idx);
      item.removeFromParent();

      if (child == activeWidget) {
        Widget next = stack != null ? stack.next() : null;
        if (next != null) {
          setActiveWidget(next);
        } else if (getWidgetCount() > 0) {
          setActiveWidget(getWidget(0));
        } else {
          setActiveWidget(null);
        }
      }
      delegateUpdates();
    }

    return removed;
  }

  /**
   * Scrolls to a particular tab if tab scrolling is enabled.
   * 
   * @param item the item to scroll to
   * @param animate true to animate the scroll
   */
  public void scrollToTab(Widget item, boolean animate) {
    if (item == null) return;
    int pos = getScrollPos();
    int area = getScrollArea();
    XElement itemEl = findItem(container.getWidgetIndex(item)).cast();
    int left = itemEl.getOffsetsTo(getStripWrap()).getX() + pos;
    int right = left + itemEl.getOffsetWidth();
    if (left < pos) {
      scrollTo(left, animate);
    } else if (right > (pos + area)) {
      scrollTo(right - area, animate);
    }
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
  public void setActiveWidget(Widget item) {
    if (item == null || item.getParent() != container) {
      return;
    }

    if (getActiveWidget() != item) {
      BeforeSelectionEvent<Widget> event = BeforeSelectionEvent.fire(this, item);
      // event can be null if no handlers
      if (event != null && event.isCanceled()) {
        return;
      }

      if (getActiveWidget() != null) {
        appearance.onDeselect(findItem(getWidgetIndex(getActiveWidget())));
      }
      appearance.onSelect(findItem(getWidgetIndex(item)));
      container.setActiveWidget(item);

      if (stack == null) {
        stack = new AccessStack<Widget>();
      }
      stack.add(item);

      if (scrolling) {
        scrollToTab(item, getAnimScroll());
      }

      focusTab(item, false);

      SelectionEvent.fire(this, item);
    }
  }

  /**
   * True to animate tab scrolling so that hidden tabs slide smoothly into view
   * (defaults to true). Only applies when {@link #tabScroll} = true.
   * 
   * @param animScroll the animation scroll state
   */
  public void setAnimScroll(boolean animScroll) {
    this.animScroll = animScroll;
  }

  /**
   * True to have the first item selected when the panel is displayed for the
   * first time if there is not selection (defaults to true).
   * 
   * @param autoSelect the auto select state
   */
  public void setAutoSelect(boolean autoSelect) {
    this.autoSelect = autoSelect;
  }

  /**
   * True to display an interior border on the body element of the panel, false
   * to hide it (defaults to true, pre-render).
   * 
   * @param bodyBorder the body border style
   */
  public void setBodyBorder(boolean bodyBorder) {
    this.bodyBorder = bodyBorder;
  }

  /**
   * True to show the close context menu (defaults to false).
   * 
   * @param closeMenu true to show it
   */
  public void setCloseContextMenu(boolean closeMenu) {
    this.closeMenu = closeMenu;
    disableContextMenu = true;
    if (closeMenu) {
      sinkEvents(Event.ONCONTEXTMENU);
    }
  }

  /**
   * Sets the tab panel messages.
   * 
   * @param messages the messages
   */
  public void setMessages(TabPanelMessages messages) {
    this.messages = messages;
  }

  /**
   * The minimum width in pixels for each tab when {@link #resizeTabs} = true
   * (defaults to 30).
   * 
   * @param minTabWidth the minimum tab width
   */
  public void setMinTabWidth(int minTabWidth) {
    this.minTabWidth = minTabWidth;
  }

  /**
   * True to automatically resize tabs. The resize operation takes into
   * consideration the current width of the tab panel as well as the current
   * values of {@link #setTabWidth(int)} and {@link #setMinTabWidth(int)}. The
   * resulting tab width will not be less than the value specified by
   * <code>setMinTabWidth</code> nor greater than the value specified by
   * <code>setTabWidth</code>. To automatically resize the tabs to completely
   * fill the tab strip, use <code>setTabWidth(Integer.MAX_VALUE)</code> and
   * <code>setResizeTabs(true)</code>.
   * 
   * @param resizeTabs true to enable tab resizing
   */
  public void setResizeTabs(boolean resizeTabs) {
    this.resizeTabs = resizeTabs;
  }

  /**
   * Sets the number of milliseconds that each scroll animation should last
   * (defaults to 150).
   * 
   * @param scrollDuration the scroll duration
   */
  public void setScrollDuration(int scrollDuration) {
    this.scrollDuration = scrollDuration;
  }

  /**
   * Sets the number of pixels to scroll each time a tab scroll button is
   * pressed (defaults to 100, or if {@link #setResizeTabs(boolean)} = true, the
   * calculated tab width). Only applies when {@link #setTabScroll(boolean)} =
   * true.
   * 
   * @param scrollIncrement the scroll increment
   */
  public void setScrollIncrement(int scrollIncrement) {
    this.scrollIncrement = scrollIncrement;
  }

  /**
   * The number of pixels of space to calculate into the sizing and scrolling of
   * tabs (defaults to 2).
   * 
   * @param tabMargin the tab margin
   */
  public void setTabMargin(int tabMargin) {
    this.tabMargin = tabMargin;
  }

  /**
   * True to enable scrolling to tabs that may be invisible due to overflowing
   * the overall TabPanel width. Only available with tabs on top. (defaults to
   * false).
   * 
   * @param tabScroll true to enable tab scrolling
   */
  public void setTabScroll(boolean tabScroll) {
    this.tabScroll = tabScroll;
  }

  /**
   * Sets the initial width in pixels of each new tab (defaults to 120).
   * 
   * @param tabWidth the tab width
   */
  public void setTabWidth(int tabWidth) {
    this.tabWidth = tabWidth;
  }

  /**
   * Updates the appearance of the specified tab item. Must be invoked after
   * changing the tab item configuration.
   * 
   * @param widget the widget for the tab to update
   * @param config the new or updated tab item configuration
   */
  public void update(Widget widget, TabItemConfig config) {
    appearance.updateItem(findItem(getWidgetIndex(widget)), config);
  }

  protected void close(Widget item) {
    if (fireCancellableEvent(new BeforeCloseEvent<Widget>(item)) && remove(item)) {
      fireEvent(new CloseEvent<Widget>(item));
    }
  }

  @Override
  protected void doAttachChildren() {
    super.doAttachChildren();
    ComponentHelper.doAttach(container);
  }

  @Override
  protected void doDetachChildren() {
    super.doDetachChildren();
    ComponentHelper.doDetach(container);
  }

  protected Element findItem(Element target) {
    return target.<XElement> cast().findParentElement(appearance.getItemSelector(), 5);
  }

  protected XElement findItem(int index) {
    NodeList<Element> items = appearance.getBar(getElement()).select(appearance.getItemSelector());
    return items.getItem(index).cast();
  }

  protected XElement getStripWrap() {
    return appearance.getStripWrap(getElement());
  }

  protected int itemIndex(Element item) {
    NodeList<Element> items = appearance.getBar(getElement()).select(appearance.getItemSelector());
    for (int i = 0; i < items.getLength(); i++) {
      if (items.getItem(i) == item) {
        return i;
      }
    }
    return -1;
  }

  @Override
  protected void onAfterFirstAttach() {
    super.onAfterFirstAttach();

    if (!bodyBorder) {
      appearance.getBody(getElement()).getStyle().setProperty("border", "none");
    }

    getElement().setTabIndex(0);
    getElement().setAttribute("hideFocus", "true");

    sinkEvents(Event.ONCLICK | Event.MOUSEEVENTS | Event.ONKEYUP | Event.FOCUSEVENTS);
  }

  @Override
  protected void onAttach() {
    super.onAttach();
    appearance.getBar(getElement()).disableTextSelection(true);

    if (getActiveWidget() == null && autoSelect && getWidgetCount() > 0) {
      setActiveWidget(getWidget(0));
    }

    if (resizeTabs) {

      Timer t = new Timer() {
        @Override
        public void run() {
          delegateUpdates();
        }

      };
      t.schedule(1);
      delegateUpdates();
    }
  }

  @Override
  protected void onBlur(Event event) {
    super.onBlur(event);
  }

  protected void onClick(Event event) {
    XElement target = event.getEventTarget().cast();
    Element item = findItem(target);
    if (item != null) {
      event.stopPropagation();
      Widget w = getWidget(itemIndex(item));
      boolean close = appearance.isClose(target);
      if (close) {
        close(w);
      } else if (w != getActiveWidget()) {
        setActiveWidget(w);
        focusTab(w, true);
      } else if (w == getActiveWidget()) {
        focusTab(w, true);
      }
    }
  }

  @Override
  protected void onDetach() {
    appearance.getBar(getElement()).disableTextSelection(false);
    super.onDetach();
  }

  @Override
  protected void onFocus(Event event) {
    if (getWidgetCount() > 0 && getActiveWidget() == null) {
      setActiveWidget(getWidget(0));
    } else if (getActiveWidget() != null) {
      focusTab(getActiveWidget(), true);
    }
  }

  protected void onItemContextMenu(final Widget item, int x, int y) {
    if (closeMenu) {
      if (closeContextMenu == null) {
        closeContextMenu = new Menu();
        closeContextMenu.addHideHandler(new HideHandler() {

          @Override
          public void onHide(HideEvent event) {
            contextMenuItem = null;
          }
        });

        closeContextMenu.add(new MenuItem(getMessages().closeTab(), new SelectionHandler<MenuItem>() {
          @Override
          public void onSelection(SelectionEvent<MenuItem> event) {
            close(contextMenuItem);
          }
        }));

        closeContextMenu.add(new MenuItem(getMessages().closeOtherTabs(), new SelectionHandler<MenuItem>() {
          @Override
          public void onSelection(SelectionEvent<MenuItem> event) {
            List<Widget> widgets = new ArrayList<Widget>();
            for (int i = 0, len = getWidgetCount(); i < len; i++) {
              widgets.add(getWidget(i));
            }

            for (Widget w : widgets) {
              TabItemConfig config = getConfig(w);
              if (w != contextMenuItem && config.isClosable()) {
                close(w);
              }
            }
          }

        }));
      }
      TabItemConfig c = configMap.get(item);
      MenuItem mi = (MenuItem) closeContextMenu.getWidget(0);
      mi.setEnabled(c.isClosable());
      contextMenuItem = item;
      boolean hasClosable = false;

      for (int i = 0, len = getWidgetCount(); i < len; i++) {
        Widget item2 = container.getWidget(i);
        TabItemConfig config = configMap.get(item2);
        if (config.isClosable() && item2 != item) {
          hasClosable = true;
          break;
        }
      }
      MenuItem m = (MenuItem) closeContextMenu.getWidget(1);
      m.setEnabled(hasClosable);
      closeContextMenu.showAt(x, y);
    }
  }

  protected void onItemTextChange(Widget tabItem, String oldText, String newText) {
    delegateUpdates();
  }

  @Override
  protected void onResize(int width, int height) {
    super.onResize(width, height);
    Size frameWidth = getElement().getFrameSize();

    if (!isAutoHeight()) {
      height -= frameWidth.getHeight() + appearance.getBar(getElement()).getOffsetHeight();
    }
    if (!isAutoWidth()) {
      width -= frameWidth.getWidth();
    }

    appearance.getBody(getElement()).setSize(width, height, true);
    appearance.getBar(getElement()).setWidth(width, true);

    if (!isAutoHeight()) {
      height -= appearance.getBody(getElement()).getFrameWidth(Side.TOP, Side.BOTTOM);
    }
    if (!isAutoWidth()) {
      width -= appearance.getBody(getElement()).getFrameWidth(Side.LEFT, Side.RIGHT);
    }
    container.setPixelSize(width, height);

    delegateUpdates();
  }

  @Override
  protected void onRightClick(Event event) {
    Element target = event.getEventTarget().cast();
    if (appearance.getBar(getElement()).isOrHasChild(target)) {
      Element item = findItem(event.getEventTarget().<Element> cast());
      if (item != null) {
        int idx = itemIndex(item);
        if (idx != -1) {
          onItemContextMenu(getWidget(idx), event.getClientX(), event.getClientY());
        }
      }
    } else {
      super.onRightClick(event);
    }
  }

  @Override
  protected void onUnload() {
    super.onUnload();
    if (stack != null) {
      stack.clear();
    }
  }

  private void autoScrollTabs() {
    int count = getWidgetCount();
    int tw = appearance.getBar(getElement()).getClientWidth();
    if (tw == 0) {
      tw = getElement().getStyleSize().getWidth();
    }

    XElement stripWrap = appearance.getStripWrap(getElement());
    XElement edge = appearance.getStripEdge(getElement());
    XElement scrollLeft = appearance.getScrollLeft(getElement());
    XElement scrollRight = appearance.getScrollRight(getElement());

    int cw = stripWrap.getOffsetWidth();

    int pos = getScrollPos();
    int l = edge.<XElement> cast().getOffsetsTo(stripWrap).getX() + pos;

    if (!getTabScroll() || count < 1 || cw < 20) {
      return;
    }

    if (l <= tw) {
      stripWrap.<XElement> cast().setWidth(tw);
      if (scrolling) {
        stripWrap.setScrollLeft(0);
        scrolling = false;

        scrollLeft.setVisible(false);
        scrollRight.setVisible(false);
        appearance.onScrolling(getElement(), false);
      }
    } else {
      if (!scrolling) {
        appearance.onScrolling(getElement(), true);
      }
      tw -= 36;
      stripWrap.<XElement> cast().setWidth(tw > 20 ? tw : 20);
      if (!scrolling) {
        if (scrollLeft == null) {
          appearance.createScrollers(getElement());
        } else {
          scrollLeft.setVisible(true);
          scrollRight.setVisible(true);
        }
      }
      scrolling = true;
      if (pos > (l - tw)) {
        stripWrap.setScrollLeft(l - tw);
      } else {
        scrollToTab(getActiveWidget(), false);
      }
      appearance.updateScrollButtons(getElement());
    }
  }

  private void autoSizeTabs() {
    int count = getWidgetCount();
    if (count == 0) return;
    int aw = appearance.getBar(getElement()).getClientWidth();
    if (aw == 0) {
      aw = getElement().getStyleSize().getWidth();
    }
    int each = (int) Math.max(Math.min(Math.floor((aw - 4) / count) - tabMargin, tabWidth), minTabWidth);

    for (int i = 0; i < count; i++) {
      XElement el = findItem(i).cast();
      appearance.setItemWidth(el, each);
    }
  }

  private void delegateUpdates() {
    if (resizeTabs) {
      autoSizeTabs();
    }
    if (tabScroll) {
      autoScrollTabs();
    }
  }

  private void focusTab(Widget item, boolean setFocus) {
    if (setFocus && !GXT.isIE7()) {
      // item.getHeader().getElement().focus();
    }
  }

  private int getScrollArea() {
    return Math.max(0, appearance.getStripWrap(getElement()).getClientWidth());
  }

  private int getScrollIncrement() {
    return scrollIncrement;
  }

  private int getScrollPos() {
    return appearance.getStripWrap(getElement()).getScrollLeft();
  }

  private int getScrollWidth() {
    return appearance.getStripEdge(getElement()).getOffsetsTo(getStripWrap()).getX() + getScrollPos();
  }

  private void onScrollLeft() {
    int pos = getScrollPos();
    int s = Math.max(0, pos - getScrollIncrement());
    if (s != pos) {
      scrollTo(s, getAnimScroll());
    }
  }

  private void onScrollRight() {
    int sw = getScrollWidth() - getScrollArea();
    int pos = getScrollPos();
    int s = Math.min(sw, pos + getScrollIncrement());
    if (s != pos) {
      scrollTo(s, getAnimScroll());
    }
  }

  private void scrollTo(int pos, boolean animate) {
    XElement stripWrap = getStripWrap();
    if (animate) {
      Fx fx = new Fx();
      fx.addAfterAnimateHandler(new AfterAnimateHandler() {
        @Override
        public void onAfterAnimate(AfterAnimateEvent event) {
          appearance.updateScrollButtons(getElement());
        }
      });
      stripWrap.<FxElement> cast().scrollTo(ScrollDirection.LEFT, pos, true, fx);
    } else {
      stripWrap.setScrollLeft(pos);
      appearance.updateScrollButtons(getElement());
    }
  }
}
