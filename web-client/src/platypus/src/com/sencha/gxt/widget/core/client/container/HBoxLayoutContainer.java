/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.GXTLogConfiguration;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.resources.ThemeStyles;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.Size;
import com.sencha.gxt.core.client.util.Util;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.button.ButtonGroup;
import com.sencha.gxt.widget.core.client.button.SplitButton;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.button.ToggleButton;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent.BeforeShowHandler;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent.CheckChangeHandler;
import com.sencha.gxt.widget.core.client.event.OverflowEvent;
import com.sencha.gxt.widget.core.client.event.OverflowEvent.HasOverflowHandlers;
import com.sencha.gxt.widget.core.client.event.OverflowEvent.OverflowHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.menu.CheckMenuItem;
import com.sencha.gxt.widget.core.client.menu.HeaderMenuItem;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.menu.SeparatorMenuItem;
import com.sencha.gxt.widget.core.client.toolbar.FillToolItem;
import com.sencha.gxt.widget.core.client.toolbar.SeparatorToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;


/**
 * A layout container for horizontal rows of widgets. Provides support for
 * automatic overflow (i.e. when there are too many widgets to display in the
 * available width -- see {@link #setEnableOverflow(boolean)}).
 * 
 * <p/>
 * Code Snippet:
 * 
 * <pre>
    HBoxLayoutContainer c = new HBoxLayoutContainer();
    c.setHBoxLayoutAlign(HBoxLayoutAlign.TOP);
    BoxLayoutData layoutData = new BoxLayoutData(new Margins(5, 0, 0, 5));
    c.add(new TextButton("Button 1"), layoutData);
    c.add(new TextButton("Button 2"), layoutData);
    c.add(new TextButton("Button 3"), layoutData);
    Viewport v = new Viewport();
    v.add(c);
    RootPanel.get().add(v);
 * </pre>
 * 
 * @see ToolBar
 */
public class HBoxLayoutContainer extends BoxLayoutContainer implements HasOverflowHandlers {

  /**
   * The vertical alignment of the horizontal widgets.
   */
  public enum HBoxLayoutAlign {
    /**
     * Children are aligned horizontally at the <b>bottom</b> side of the
     * container.
     */
    BOTTOM,
    /**
     * Children are aligned horizontally at the <b>mid-height</b> of the
     * container.
     */
    MIDDLE,
    /**
     * Children are stretched vertically to fill the height of the container.
     */
    STRETCH,
    /**
     * Children heights are set the size of the largest child's height.
     */
    STRETCHMAX,
    /**
     * Children are aligned horizontally at the <b>top</b> side of the
     * container.
     */
    TOP
  }

  protected List<Widget> hiddens = new ArrayList<Widget>();
  protected boolean hasOverflow;
  protected TextButton more;
  protected Menu moreMenu;

  private boolean enableOverflow = true;
  private HBoxLayoutAlign hBoxLayoutAlign;
  private int triggerWidth = 35;
  private Map<Widget, Integer> widthMap = new HashMap<Widget, Integer>();
  private Map<Widget, Integer> loopWidthMap = new HashMap<Widget, Integer>();

  private static Logger logger = Logger.getLogger(HBoxLayoutContainer.class.getName());

  /**
   * Creates a new HBoxlayout.
   */
  public HBoxLayoutContainer() {
    this(HBoxLayoutAlign.TOP);
  }

  /**
   * Creates a new HBoxlayout.
   * 
   * @param align the horizontal alignment
   */
  public HBoxLayoutContainer(HBoxLayoutAlign align) {
    super();
    setHBoxLayoutAlign(align);
  }

  @Override
  public HandlerRegistration addOverflowHandler(OverflowHandler handler) {
    return addHandler(handler, OverflowEvent.getType());
  }

  /**
   * Returns the horizontal alignment.
   * 
   * @return the horizontal alignment
   */
  public HBoxLayoutAlign getHBoxLayoutAlign() {
    return hBoxLayoutAlign;
  }

  /**
   * Returns true if overflow is enabled.
   * 
   * @return the overflow state
   */
  public boolean isEnableOverflow() {
    return enableOverflow;
  }

  /**
   * True to show a drop down icon when the available width is less than the
   * required width (defaults to true).
   * 
   * @param enableOverflow true to enable overflow support
   */
  public void setEnableOverflow(boolean enableOverflow) {
    this.enableOverflow = enableOverflow;
  }

  /**
   * Sets the vertical alignment for child items (defaults to TOP).
   * 
   * @param hBoxLayoutAlign the vertical alignment
   */
  public void setHBoxLayoutAlign(HBoxLayoutAlign hBoxLayoutAlign) {
    this.hBoxLayoutAlign = hBoxLayoutAlign;
  }

  protected void addWidgetToMenu(Menu menu, Widget w) {
    // TODO do we really want all these types referenced here?
    if (w instanceof SeparatorToolItem) {
      menu.add(new SeparatorMenuItem());

    } else if (w instanceof SplitButton) {
      final SplitButton sb = (SplitButton) w;
      MenuItem item = new MenuItem(sb.getText(), sb.getIcon());
      item.setEnabled(sb.isEnabled());
      item.setItemId(sb.getItemId());
      if (sb.getData("gxt-menutext") != null) {
        item.setText(sb.getData("gxt-menutext").toString());
      }
      if (sb.getMenu() != null) {
        item.setSubMenu(sb.getMenu());
      }

      item.addSelectionHandler(new SelectionHandler<Item>() {

        @Override
        public void onSelection(SelectionEvent<Item> event) {
          sb.fireEvent(new SelectEvent());
        }
      });
      menu.add(item);

    } else if (w instanceof TextButton) {
      final TextButton b = (TextButton) w;

      MenuItem item = new MenuItem(b.getText(), b.getIcon());
      item.setItemId(b.getItemId());

      if (b.getData("gxt-menutext") != null) {
        item.setText(b.getData("gxt-menutext").toString());
      }
      if (b.getMenu() != null) {
        item.setHideOnClick(false);
        item.setSubMenu(b.getMenu());
      }
      item.setEnabled(b.isEnabled());

      item.addSelectionHandler(new SelectionHandler<Item>() {

        @Override
        public void onSelection(SelectionEvent<Item> event) {
          b.fireEvent(new SelectEvent());
        }
      });

      menu.add(item);
    } else if (w instanceof ButtonGroup) {
      ButtonGroup g = (ButtonGroup) w;
      g.setItemId(g.getItemId());
      menu.add(new SeparatorMenuItem());
      String heading = g.getHeadingText();
      if (heading != null && heading.length() > 0 && !heading.equals("&#160;")) {
        menu.add(new HeaderMenuItem(heading));
      }

      Widget con = g.getWidget();
      if (con != null && con instanceof HasWidgets) {
        HasWidgets widgets = (HasWidgets) con;
        Iterator<Widget> it = widgets.iterator();
        while (it.hasNext()) {
          addWidgetToMenu(menu, it.next());
        }
      }

      menu.add(new SeparatorMenuItem());
    } else if (w instanceof ToggleButton) {
      final ToggleButton b = (ToggleButton)w;
      
      final CheckMenuItem item = new CheckMenuItem(b.getText());
      item.setItemId(b.getItemId());
      item.setChecked(b.getValue());
      
      if (b.getData("gxt-menutext") != null) {
        item.setText(b.getData("gxt-menutext").toString());
      }

      item.setEnabled(b.isEnabled());
      
      item.addCheckChangeHandler(new CheckChangeHandler<CheckMenuItem>() {

        @Override
        public void onCheckChange(CheckChangeEvent<CheckMenuItem> event) {
          b.setValue(event.getItem().isChecked());
          b.fireEvent(new SelectEvent());
        }
      });

      menu.add(item);
      
    }

    if (menu.getWidgetCount() > 0) {
      if (menu.getWidget(0) instanceof SeparatorMenuItem) {
        menu.remove(menu.getWidget(0));
      }
      if (menu.getWidgetCount() > 0) {
        if (menu.getWidget(menu.getWidgetCount() - 1) instanceof SeparatorMenuItem) {
          menu.remove(menu.getWidget(menu.getWidgetCount() - 1));
        }
      }
    }
  }

  protected void clearMenu() {
    moreMenu.clear();
  }

  @Override
  protected void doLayout() {
    Size size = getElement().getStyleSize();

    if (GXTLogConfiguration.loggingIsEnabled()) {
      logger.finest(getId() + " doLayout  size: " + size);
    }
    
    if ((size.getHeight() == 0 && size.getWidth() == 0) || size.getWidth() == 0) {
      return;
    }

    final int w = size.getWidth() - getScrollOffset();
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

    int stretchHeight = h - pt - pb;
    int totalFlex = 0;
    int totalWidth = 0;
    int maxHeight = 0;

    for (int i = 0, len = getWidgetCount(); i < len; i++) {
      Widget widget = getWidget(i);

      // very strange behavior where clearing the margins in causing
      // widget.getOffsetWidth to return an invalid result (for buttons -5 px
      // actual width)
      // which is corrected by the time enable overflow code runs
      if (!GXT.isIE7()) {
        widget.getElement().getStyle().setMargin(0, Unit.PX);
      }

      if (!widget.isVisible()) {
        continue;
      }

      if (widget == more) {
        triggerWidth = widget.getOffsetWidth() + 10;
      }

      BoxLayoutData layoutData = null;
      Object d = widget.getLayoutData();
      if (d instanceof BoxLayoutData) {
        layoutData = (BoxLayoutData) d;
      } else {
        layoutData = new BoxLayoutData();
      }

      Margins cm = layoutData.getMargins();
      if (cm == null) {
        cm = new Margins(0);
      }
      
      // TODO strange issue where getOffsetWidth call in 2nd loop is returning smaller number than acutal offset
      // when packing CENTER or END so we cache the offsetWidth for use in 2nd loop
      int ww = widget.getOffsetWidth();
      loopWidthMap.put(widget, ww);

      totalFlex += layoutData.getFlex();
      totalWidth += (ww + cm.getLeft() + cm.getRight());
      maxHeight = Math.max(maxHeight, widget.getOffsetHeight() + cm.getTop() + cm.getBottom());
    }

    int innerCtHeight = maxHeight + pt + pb;

    if (hBoxLayoutAlign.equals(HBoxLayoutAlign.STRETCH)) {
      getContainerTarget().setSize(w, h, true);
    } else if (hBoxLayoutAlign.equals(HBoxLayoutAlign.MIDDLE) || hBoxLayoutAlign.equals(HBoxLayoutAlign.BOTTOM)) {
      getContainerTarget().setSize(w, h = Math.max(h, innerCtHeight), true);
    } else {
      getContainerTarget().setSize(w, innerCtHeight, true);
    }

    int extraWidth = w - totalWidth - pl - pr;
    int allocated = 0;
    int cw, ch, ct;
    int availableHeight = h - pt - pb;

    if (getPack().equals(BoxLayoutPack.CENTER)) {
      pl += extraWidth / 2;
    } else if (getPack().equals(BoxLayoutPack.END)) {
      pl += extraWidth;
    }

    for (int i = 0, len = getWidgetCount(); i < len; i++) {
      Widget widget = getWidget(i);

      if (!widget.isVisible()) {
        continue;
      }

      BoxLayoutData layoutData = null;
      Object d = widget.getLayoutData();
      if (d instanceof BoxLayoutData) {
        layoutData = (BoxLayoutData) d;
      } else {
        layoutData = new BoxLayoutData();
      }
      Margins cm = layoutData.getMargins();
      if (cm == null) {
        cm = new Margins(0);
      }

      cw = loopWidthMap.remove(widget);
      ch = widget.getOffsetHeight();
      pl += cm.getLeft();

      pl = Math.max(0, pl);
      if (hBoxLayoutAlign.equals(HBoxLayoutAlign.MIDDLE)) {
        int diff = availableHeight - (ch + cm.getTop() + cm.getBottom());
        if (diff == 0) {
          ct = pt + cm.getTop();
        } else {
          ct = pt + cm.getTop() + (diff / 2);
        }
      } else {
        if (hBoxLayoutAlign.equals(HBoxLayoutAlign.BOTTOM)) {
          ct = h - (pb + cm.getBottom() + ch);
        } else {
          ct = pt + cm.getTop();
        }

      }

      boolean component = widget instanceof Component;
      Component c = null;
      if (component) {
        c = (Component) widget;
      }

      int width = -1;
      if (component) {
        c.setPosition(pl, ct);
      } else {
        XElement.as(widget.getElement()).setLeftTop(pl, ct);
      }

      if (getPack().equals(BoxLayoutPack.START) && layoutData.getFlex() > 0) {
        int add = (int) Math.floor(extraWidth * (layoutData.getFlex() / totalFlex));
        allocated += add;
        if (isAdjustForFlexRemainder() && i == getWidgetCount() - 1) {
          add += (extraWidth - allocated);
        }

        cw += add;
        width = cw;
      }
      if (hBoxLayoutAlign.equals(HBoxLayoutAlign.STRETCH)) {
        applyLayout(
            widget,
            width,
            Util.constrain(stretchHeight - cm.getTop() - cm.getBottom(), layoutData.getMinSize(),
                layoutData.getMaxSize()));
      } else if (hBoxLayoutAlign.equals(HBoxLayoutAlign.STRETCHMAX)) {
        applyLayout(widget, width,
            Util.constrain(maxHeight - cm.getTop() - cm.getBottom(), layoutData.getMinSize(), layoutData.getMaxSize()));
      } else if (width > 0) {
        applyLayout(widget, width, -1);
      }
      pl += cw + cm.getRight();
    }

    // do we need overflow
    if (enableOverflow) {
      int runningWidth = 0;
      for (int i = 0, len = getWidgetCount(); i < len; i++) {
        Widget widget = getWidget(i);

        if (widget == more) {
          continue;
        }

        BoxLayoutData layoutData = null;
        Object d = widget.getLayoutData();
        if (d instanceof BoxLayoutData) {
          layoutData = (BoxLayoutData) d;
        } else {
          layoutData = new BoxLayoutData();
        }
        Margins cm = layoutData.getMargins();
        if (cm == null) {
          cm = new Margins(0);
        }
        runningWidth += getWidgetWidth(widget);
        runningWidth += cm.getLeft();
        runningWidth += cm.getRight();
      }

      if (runningWidth > w) {
        hasOverflow = true;
        onOverflow();
      } else {
        hasOverflow = false;
        if (more != null && more.getParent() == this) {
          onUnoverflow();
        }

      }
    }

  }

  protected int getWidgetWidth(Widget widget) {
    Integer w = widthMap.get(widget);
    if (w != null) {
      return w;
    } else {
      return widget.getOffsetWidth();
    }
  }

  protected void hideComponent(Widget w) {
    widthMap.put(w, w.getOffsetWidth());
    hiddens.add(w);
    w.setVisible(false);
  }

  protected void initMore() {
    if (more == null) {
      moreMenu = new Menu();
      moreMenu.addBeforeShowHandler(new BeforeShowHandler() {

        @Override
        public void onBeforeShow(BeforeShowEvent event) {
          clearMenu();

          for (int i = 0, len = getWidgetCount(); i < len; i++) {
            Widget w = getWidget(i);
            if (isHidden(w) && w != more) {
              addWidgetToMenu(moreMenu, w);
            }
          }

          HBoxLayoutContainer.this.fireEvent(new OverflowEvent(moreMenu));
        }
      });

      more = new TextButton();
      more.addStyleName("x-toolbar-more");
      more.setData("x-ignore-width", true);
      more.setData("gxt-more", "true");
      more.setIcon(ThemeStyles.get().moreIcon());
      more.setMenu(moreMenu);
    }

    if (more.getParent() != this) {
      add(more);
    }
  }

  protected boolean isHidden(Widget w) {
    return hiddens != null && hiddens.contains(w);
  }

  protected void onOverflow() {
    Size size = getElement().getStyleSize();
    final int w = size.getWidth() - getScrollOffset() - triggerWidth;

    boolean change = false;

    int loopWidth = 0;
    for (int i = 0; i < getWidgetCount(); i++) {
      Widget widget = getWidget(i);
      if (widget == more) continue;

      if (!(widget instanceof FillToolItem)) {
        loopWidth += getWidgetWidth(widget);
        BoxLayoutData data = (BoxLayoutData) widget.getLayoutData();
        if (data != null && data.getMargins() != null) {
          loopWidth += (data.getMargins().getLeft() + data.getMargins().getRight());
        }
        if (loopWidth >= w) {
          if (!isHidden(widget)) {
            change = true;
            hideComponent(widget);
          }
        } else {
          if (isHidden(widget)) {
            change = true;
            unhideComponent(widget);
          }
        }
      }
    }

    if (hiddens != null && hiddens.size() > 0) {
      initMore();
    }

    if (change) {
      Scheduler.get().scheduleDeferred(new ScheduledCommand() {
        @Override
        public void execute() {
          forceLayout();
        }
      });
    }
  }

  protected void onUnoverflow() {
    if (more != null) {
      Scheduler.get().scheduleDeferred(new ScheduledCommand() {

        @Override
        public void execute() {
          for (int i = 0; i < getWidgetCount(); i++) {
            Widget widget = getWidget(i);
            unhideComponent(widget);
          }
          remove(more);
          forceLayout();
        }
      });
    }
  }

  protected void unhideComponent(Widget w) {
    if (hiddens.remove(w)) {
      widthMap.remove(w);
      w.setVisible(true);
    }
  }
}
