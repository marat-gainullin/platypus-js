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
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiChild;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.GXTLogConfiguration;
import com.sencha.gxt.core.client.Style.Direction;
import com.sencha.gxt.core.client.Style.LayoutRegion;
import com.sencha.gxt.core.client.Style.Side;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.resources.CommonStyles;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.Rectangle;
import com.sencha.gxt.core.client.util.Size;
import com.sencha.gxt.widget.core.client.CollapsePanel;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.SplitBar;
import com.sencha.gxt.widget.core.client.button.IconButton.IconConfig;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.event.BeforeCollapseEvent;
import com.sencha.gxt.widget.core.client.event.BeforeCollapseEvent.BeforeCollapseHandler;
import com.sencha.gxt.widget.core.client.event.BeforeExpandEvent;
import com.sencha.gxt.widget.core.client.event.BeforeExpandEvent.BeforeExpandHandler;
import com.sencha.gxt.widget.core.client.event.CollapseItemEvent;
import com.sencha.gxt.widget.core.client.event.CollapseItemEvent.CollapseItemHandler;
import com.sencha.gxt.widget.core.client.event.CollapseItemEvent.HasCollapseItemHandlers;
import com.sencha.gxt.widget.core.client.event.ExpandItemEvent;
import com.sencha.gxt.widget.core.client.event.ExpandItemEvent.ExpandItemHandler;
import com.sencha.gxt.widget.core.client.event.ExpandItemEvent.HasExpandItemHandlers;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.event.SplitBarDragEvent;
import com.sencha.gxt.widget.core.client.event.SplitBarDragEvent.SplitBarDragHandler;

/**
 * A multi-pane, application-oriented layout container that supports multiple
 * regions, automatic split bars between regions and built-in expanding and
 * collapsing of regions.
 * <p/>
 * Region positions are specified using compass points (e.g. north for top, west
 * for left, east for right, south for bottom) and center. The center region is
 * a privileged position that receives the remaining space not allocated to
 * other regions. Border layout containers should generally specify a center
 * region and one or more other regions.
 * <p/>
 * Region layout parameters are specified using {@link BorderLayoutData} which
 * controls the margin between the regions, the size of the region, the minimum
 * and maximum size, whether the region has a split bar, whether the region is
 * collapsible and other details.
 * <p/>
 * Region size may be specified as a percent of the parent container size or a
 * fixed number of pixels. The region size is specified as a single value that
 * describes the orientation associated with the region (height for north and
 * south, width for west and east).
 * <p/>
 * The size, split bar and collapsible attributes are specified in the
 * <code>BorderLayoutData</code> for the region adjacent to the center region.
 * <p />
 * Code Snippet:
 * 
 * <pre>
    BorderLayoutContainer con = new BorderLayoutContainer();

    ContentPanel cp = new ContentPanel();
    cp.setHeadingText("North");
    cp.add(new Label("North Content"));
    BorderLayoutData d = new BorderLayoutData(.20);
    d.setMargins(new Margins(5));
    d.setCollapsible(true);
    d.setSplit(true);
    con.setNorthWidget(cp, d);

    cp = new ContentPanel();
    cp.setHeadingText("West");
    cp.add(new Label("West Content"));
    d = new BorderLayoutData(.20);
    d.setMargins(new Margins(0, 5, 5, 5));
    d.setCollapsible(true);
    d.setSplit(true);
    d.setCollapseMini(true);
    con.setWestWidget(cp, d);

    cp = new ContentPanel();
    cp.setHeadingText("Center");
    cp.add(new Label("Center Content"));
    d = new BorderLayoutData();
    d.setMargins(new Margins(0, 5, 5, 0));
    con.setCenterWidget(cp, d);

    Viewport v = new Viewport();
    v.add(con);
    RootPanel.get().add(v);
 * </pre>
 */
public class BorderLayoutContainer extends SimpleContainer implements HasCenterWidget, HasNorthWidget, HasSouthWidget,
    HasEastWidget, HasWestWidget, HasCollapseItemHandlers<ContentPanel>, HasExpandItemHandlers<ContentPanel> {

  @SuppressWarnings("javadoc")
  public interface BorderLayoutAppearance {
    XElement getContainerTarget(XElement parent);

    void onInsert(Widget child);

    void onRemove(Widget child);

    void render(SafeHtmlBuilder sb);
  }

  /**
   * Specifies region layout parameters which control the margin between the
   * regions, the size of the region, the minimum and maximum size, whether the
   * region has a split bar, whether the region is collapsible and other
   * details. The size, split bar and collapsible attributes are specified in
   * the <code>BorderLayoutData</code> for the region adjacent to the center
   * region. The center region is allocated the remaining space.
   */
  public static class BorderLayoutData extends MarginData implements HasSize, LayoutData {

    private double size = 100;
    private boolean split;
    private boolean hidden;
    private int minSize = 50;
    private int maxSize = 500;
    private boolean collapsible;
    private boolean collapseMini;
    private boolean collapseHidden;
    private boolean floatable;
    private boolean collapsed;

    /**
     * Creates a border layout data with default values for <code>size</code>
     * (100), <code>minSize</code> (50) and <code>maxSize</code> (500).
     */
    public BorderLayoutData() {

    }

    /**
     * Creates a border layout data with default values for <code>minSize</code>
     * (50) and <code>maxSize</code> (500) and the specified value for size.
     * 
     * @param size the region size (height for north and south, width for west
     *          and east)
     */
    @UiConstructor
    public BorderLayoutData(double size) {
      setSize(size);
    }

    /**
     * Returns the region's maximum size.
     * 
     * @return the maximum size
     */
    public int getMaxSize() {
      return maxSize;
    }

    /**
     * Returns the region's minimum size.
     * 
     * @return the minimum size
     */
    public int getMinSize() {
      return minSize;
    }

    @Override
    public double getSize() {
      return size;
    }

    /**
     * Returns the collapsed state
     * 
     * @return the collapsed state
     */
    public boolean isCollapsed() {
      return collapsed;
    }

    /**
     * Returns true if the collapse panel is hidden.
     * 
     * @return the collapse hidden state
     */
    public boolean isCollapseHidden() {
      return collapseHidden;
    }

    /**
     * Returns true if the mini split bar collapse tool is enabled.
     * 
     * @return true if the mini split bar collapse tool is enabled
     */
    public boolean isCollapseMini() {
      return collapseMini;
    }

    /**
     * Returns true if collapsing is enabled.
     * 
     * @return the collapse state
     */
    public boolean isCollapsible() {
      return collapsible;
    }

    /**
     * Returns true if the region is floatable.
     * 
     * @return the float state
     */
    public boolean isFloatable() {
      return floatable;
    }

    /**
     * Returns true if the component is hidden.
     * 
     * @return the hidden state
     */
    public boolean isHidden() {
      return hidden;
    }

    /**
     * Returns true if the region is split.
     * 
     * @return the split state
     */
    public boolean isSplit() {
      return split;
    }

    /**
     * Sets the collapsed state.
     * 
     * @param collapsed true to render collapsed
     */
    public void setCollapsed(boolean collapsed) {
      this.collapsed = collapsed;
    }

    /**
     * True to hide the collapse panel when a region is collapsed (defaults to
     * false).
     * 
     * @param collapseHidden the collapse hidden state
     */
    public void setCollapseHidden(boolean collapseHidden) {
      this.collapseHidden = collapseHidden;
    }

    /**
     * True to add a mini-collapase tool in the split bar. Enabling will make
     * the region resizable.
     * 
     * @param collapseMini true to add tool
     */
    public void setCollapseMini(boolean collapseMini) {
      this.collapseMini = collapseMini;
      this.split = true;
    }

    /**
     * True to allow the user to collapse this region (defaults to false). If
     * true, an expand/collapse tool button will automatically be rendered into
     * the title bar of the region, otherwise the button will not be shown.
     * 
     * @param collapsible true to enable collapsing
     */
    public void setCollapsible(boolean collapsible) {
      this.collapsible = collapsible;
    }

    /**
     * True to allow clicking a collapsed region's bar to display the region's
     * panel floated above the layout, false to force the user to fully expand a
     * collapsed region by clicking the expand button to see it again (defaults
     * to true).
     * 
     * @param floatable true to enable floating
     */
    public void setFloatable(boolean floatable) {
      this.floatable = floatable;
    }

    /**
     * True to hide the component.
     * 
     * @param hidden true to hide
     */
    public void setHidden(boolean hidden) {
      this.hidden = hidden;
    }

    /**
     * Sets the maximum allowable size in pixels for this region (defaults to
     * 500).
     * 
     * @param maxSize the max size
     */
    public void setMaxSize(int maxSize) {
      this.maxSize = maxSize;
    }

    /**
     * Sets the minimum allowable size in pixels for this region (defaults to
     * 50).
     * 
     * @param minSize the min size
     */
    public void setMinSize(int minSize) {
      this.minSize = minSize;
    }

    @Override
    public void setSize(double size) {
      this.size = size;
    }

    /**
     * True to display a {@link SplitBar} between this region and its neighbor,
     * allowing the user to resize the regions dynamically (defaults to false).
     * When split = true, it is common to specify a {@link #minSize} and
     * {@link #maxSize} for the region.
     * 
     * @param split true to enable a split bar
     */
    public void setSplit(boolean split) {
      this.split = split;
    }
  }

  private class Handler implements BeforeCollapseHandler, BeforeExpandHandler {
    @Override
    public void onBeforeCollapse(BeforeCollapseEvent event) {
      event.setCancelled(true);
      onCollapse((ContentPanel) event.getSource());

    }

    @Override
    public void onBeforeExpand(BeforeExpandEvent event) {
      event.setCancelled(true);
      onExpand((ContentPanel) event.getSource());
    }

  }

  protected Widget east;
  protected Widget north;
  protected Widget south;
  protected Widget west;

  private Handler collapseHandler = new Handler();
  private Rectangle lastCenter;
  private final BorderLayoutAppearance appearance;

  private static Logger logger = Logger.getLogger(BorderLayoutContainer.class.getName());

  /**
   * Creates a border layout container with the default appearance.
   */
  public BorderLayoutContainer() {
    this(GWT.<BorderLayoutAppearance> create(BorderLayoutAppearance.class));
  }

  /**
   * Creates a border layout container with the specified appearance.
   * 
   * @param appearance the appearance of the border layout container
   */
  public BorderLayoutContainer(BorderLayoutAppearance appearance) {
    super(true);
    this.appearance = appearance;

    SafeHtmlBuilder builder = new SafeHtmlBuilder();
    this.appearance.render(builder);

    setElement(XDOM.create(builder.toSafeHtml()));

    getElement().makePositionable();
  }

  @Override
  public HandlerRegistration addCollapseHandler(CollapseItemHandler<ContentPanel> handler) {
    return addHandler(handler, CollapseItemEvent.getType());
  }

  @Override
  public HandlerRegistration addExpandHandler(ExpandItemHandler<ContentPanel> handler) {
    return addHandler(handler, ExpandItemEvent.getType());
  }

  /**
   * Collapses the panel in the given region. If the target widget is hidden no
   * action will be performed.
   * 
   * @param region the region to be collapsed
   */
  public void collapse(LayoutRegion region) {
    Widget w = getRegionWidget(region);
    if (w != null && w instanceof ContentPanel && !(w instanceof CollapsePanel)) {
      BorderLayoutData data = getLayoutData(w);
      if (data.isHidden()) {
        return;
      }
      onCollapse((ContentPanel) w);
    }
  }

  /**
   * Expands the panel in the given region. If the target widget is hidden no
   * action will be performed.
   * 
   * @param region the region to expand
   */
  public void expand(LayoutRegion region) {
    Widget w = getRegionWidget(region);
    if (w != null && w instanceof CollapsePanel) {
      BorderLayoutData data = getLayoutData(w);
      if (data.isHidden()) {
        return;
      }
      CollapsePanel collapse = (CollapsePanel) w;
      ContentPanel cp = (ContentPanel) collapse.getData("panel");
      onExpand(cp);
    }
  }

  @Override
  public Widget getCenterWidget() {
    return getWidget();
  }

  @Override
  public Widget getEastWidget() {
    return east;
  }

  @Override
  public Widget getNorthWidget() {
    return north;
  }

  /**
   * Returns the widget in the specified region, or null if there is no widget
   * for that region.
   * 
   * @param region the region
   * @return the widget in the specified region, or null if there is no widget
   *         for that region
   */
  public Widget getRegionWidget(LayoutRegion region) {
    switch (region) {
      case CENTER:
        return getCenterWidget();
      case NORTH:
        return getNorthWidget();
      case EAST:
        return getEastWidget();
      case SOUTH:
        return getSouthWidget();
      case WEST:
        return getWestWidget();
    }
    return null;
  }

  @Override
  public Widget getSouthWidget() {
    return south;
  }

  @Override
  public Widget getWestWidget() {
    return west;
  }

  /**
   * Hides the component in the given region.
   * 
   * @param region the layout region
   */
  public void hide(LayoutRegion region) {
    Widget c = getRegionWidget(region);
    if (c != null) {
      BorderLayoutData data = (BorderLayoutData) c.getLayoutData();
      data.setHidden(true);
      doLayout();
    }
  }

  @Override
  public void setCenterWidget(IsWidget child) {
    setWidget(child);
  }

  /**
   * Sets the widget in the center region of the border layout container. The
   * center region is a privileged position that receives the remaining space
   * not allocated to other regions. Border layout containers should generally
   * specify a center region and one or more other regions.
   * 
   * @param child the widget to put in the center region
   * @param layoutData the layout data for the widget
   */
  @UiChild(limit = 1, tagname = "center")
  public void setCenterWidget(IsWidget child, MarginData layoutData) {
    if (child != null) {
      child.asWidget().setLayoutData(layoutData);
    }
    setCenterWidget(child);
  }

  @Override
  public void setEastWidget(IsWidget child) {
    if (east != null) {
      remove(east);
    }
    if (child != null) {
      east = child.asWidget();
      insert(east, 0);

    }
  }

  /**
   * Sets the widget in the east (right) region of the border layout container.
   * 
   * @param child the widget to put in the east region
   * @param layoutData the layout data for the widget
   */
  @UiChild(limit = 1, tagname = "east")
  public void setEastWidget(IsWidget child, BorderLayoutData layoutData) {
    if (child != null) {
      child.asWidget().setLayoutData(layoutData);
    }
    setEastWidget(child);
  }

  @Override
  public void setNorthWidget(IsWidget child) {
    if (north != null) {
      remove(north);
    }
    if (child != null) {
      north = child.asWidget();

      insert(north, 0);
    }
  }

  /**
   * Sets the widget in the north (top) region of the border layout container.
   * 
   * @param child the widget to put in the north region
   * @param layoutData the layout data for the widget
   */
  @UiChild(limit = 1, tagname = "north")
  public void setNorthWidget(IsWidget child, BorderLayoutData layoutData) {
    if (child != null) {
      child.asWidget().setLayoutData(layoutData);
    }
    setNorthWidget(child);
  }

  @Override
  public void setSouthWidget(IsWidget child) {
    if (south != null) {
      remove(south);
    }
    if (child != null) {
      south = child.asWidget();
      insert(south, 0);
    }
  }

  /**
   * Sets the widget in the south (bottom) region of the border layout
   * container.
   * 
   * @param child the widget to put in the south region
   * @param layoutData the layout data for the widget
   */
  @UiChild(limit = 1, tagname = "south")
  public void setSouthWidget(IsWidget child, BorderLayoutData layoutData) {
    if (child != null) {
      child.asWidget().setLayoutData(layoutData);
    }
    setSouthWidget(child);
  }

  @Override
  public void setWestWidget(IsWidget child) {
    if (west != null) {
      remove(west);
    }
    if (child != null) {
      west = child.asWidget();
      insert(west, 0);
    }
  }

  /**
   * Sets the widget in the west (left) region of the border layout container.
   * 
   * @param child the widget to put in the west region
   * @param layoutData the layout data for the widget
   */
  @UiChild(limit = 1, tagname = "west")
  public void setWestWidget(IsWidget child, BorderLayoutData layoutData) {
    if (child != null) {
      child.asWidget().setLayoutData(layoutData);
    }
    setWestWidget(child);
  }

  /**
   * Shows the component in the given region.
   * 
   * @param region the layout region
   */
  public void show(LayoutRegion region) {
    Widget c = getRegionWidget(region);
    if (c != null) {
      BorderLayoutData data = (BorderLayoutData) c.getLayoutData();
      data.setHidden(false);
      doLayout();
    }
  }

  @Override
  protected void applyLayout(Widget component, Rectangle box) {
    super.applyLayout(component, box);
    if (component instanceof Component) {
      final SplitBar bar = ((Component) component).getData("splitBar");
      if (bar != null) {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
          @Override
          public void execute() {
            bar.getElement().getStyle().setVisibility(Visibility.VISIBLE);
          }
        });
      }
    }
  }

  protected CollapsePanel createCollapsePanel(ContentPanel panel, BorderLayoutData data, LayoutRegion region) {
    CollapsePanel cp = new CollapsePanel(panel, data, region) {
      protected void onExpandButton() {
        super.onExpandButton();
        onExpandClick(this);
      }
    };

    BorderLayoutData collapseData = new BorderLayoutData();
    collapseData.setSize(data.isCollapseHidden() ? 0 : 24);

    Margins m = data.getMargins();
    if (m == null) {
      m = new Margins();
      data.setMargins(m);
    }
    collapseData.setMargins(new Margins(m.getTop(), m.getRight(), m.getBottom(), m.getLeft()));

    if (data.isCollapseHidden()) {
      cp.collapseHidden();
      collapseData.setSize(0);
      switch (region) {
        case WEST:
          collapseData.getMargins().setLeft(0);
          break;
        case EAST:
          collapseData.getMargins().setRight(0);
          break;
        case NORTH:
          collapseData.getMargins().setTop(0);
          break;
        case SOUTH:
          collapseData.getMargins().setBottom(0);
          break;
      }
    }

    cp.setLayoutData(collapseData);
    cp.setData("panel", panel);
    panel.setData("collapse", cp);

    return cp;
  }

  protected SplitBar createSplitBar(Component component) {
    if (component == north) {
      return new SplitBar(LayoutRegion.SOUTH, component);
    } else if (component == south) {
      return new SplitBar(LayoutRegion.NORTH, component);
    } else if (component == west) {
      return new SplitBar(LayoutRegion.EAST, component);
    } else if (component == east) {
      return new SplitBar(LayoutRegion.WEST, component);
    }
    return null;
  }

  @Override
  protected void doLayout() {
    if (isStateful()) {
      for (int i = 0; i < getWidgetCount(); i++) {
        Widget w = getWidget(i);
        if (w instanceof ContentPanel && w.getLayoutData() instanceof BorderLayoutData) {
          BorderLayoutData data = (BorderLayoutData) w.getLayoutData();
          if (data.isCollapsed()) {
            switchPanels((ContentPanel) w);
          }
        }
      }
    }

    Size size = getContainerTarget().getStyleSize();

    if (GXTLogConfiguration.loggingIsEnabled()) {
      logger.finest(getId() + " doLayout size: " + size);
    }

    int w = size.getWidth();
    int h = size.getHeight();

    int sLeft = getContainerTarget().getPadding(Side.LEFT);
    int sTop = getContainerTarget().getPadding(Side.TOP);
    int centerW = w, centerH = h, centerY = 0, centerX = 0;

    if (north != null) {
      BorderLayoutData data = getLayoutData(north);
      north.setVisible(!data.isHidden());

      if (north.isVisible()) {
        Rectangle b = new Rectangle();
        Margins m = data.getMargins() != null ? data.getMargins() : new Margins();
        double s = data.getSize() <= 1 ? data.getSize() * size.getHeight() : data.getSize();
        b.setHeight((int) s);
        b.setWidth(w - (m.getLeft() + m.getRight()));
        b.setX(m.getLeft());
        b.setY(m.getTop());
        centerY = b.getHeight() + b.getY() + m.getBottom();
        centerH -= centerY;
        b.setX(b.getX() + sLeft);
        b.setY(b.getY() + sTop);
        applyLayout(north, b);
      }
    }
    if (south != null) {
      BorderLayoutData data = getLayoutData(south);
      south.setVisible(!data.isHidden());
      if (south.isVisible()) {
        Rectangle b = new Rectangle();
        Margins m = data.getMargins() != null ? data.getMargins() : new Margins();
        double s = data.getSize() <= 1 ? data.getSize() * size.getHeight() : data.getSize();
        b.setHeight((int) s);
        b.setWidth(w - (m.getLeft() + m.getRight()));
        b.setX(m.getLeft());
        int totalHeight = (b.getHeight() + m.getTop() + m.getBottom());
        b.setY(h - totalHeight + m.getTop());
        centerH -= totalHeight;
        b.setX(b.getX() + sLeft);
        b.setY(b.getY() + sTop);
        applyLayout(south, b);
      }
    }

    if (west != null) {
      BorderLayoutData data = getLayoutData(west);
      west.setVisible(!data.isHidden());
      if (west.isVisible()) {
        Rectangle box = new Rectangle();
        Margins m = data.getMargins() != null ? data.getMargins() : new Margins();
        double s = data.getSize() <= 1 ? data.getSize() * size.getWidth() : data.getSize();
        box.setWidth((int) s);
        box.setHeight(centerH - (m.getTop() + m.getBottom()));
        box.setX(m.getLeft());
        box.setY(centerY + m.getTop());
        int totalWidth = (box.getWidth() + m.getLeft() + m.getRight());
        centerX += totalWidth;
        centerW -= totalWidth;
        box.setX(box.getX() + sLeft);
        box.setY(box.getY() + sTop);
        applyLayout(west, box);
      }

    }
    if (east != null) {
      BorderLayoutData data = getLayoutData(east);
      east.setVisible(!data.isHidden());
      if (east.isVisible()) {
        Rectangle b = new Rectangle();
        Margins m = data.getMargins() != null ? data.getMargins() : new Margins();
        double s = data.getSize() <= 1 ? data.getSize() * size.getWidth() : data.getSize();
        b.setWidth((int) s);
        b.setHeight(centerH - (m.getTop() + m.getBottom()));
        int totalWidth = (b.getWidth() + m.getLeft() + m.getRight());
        b.setX(w - totalWidth + m.getLeft());
        b.setY(centerY + m.getTop());
        centerW -= totalWidth;
        b.setX(b.getX() + sLeft);
        b.setY(b.getY() + sTop);
        applyLayout(east, b);
      }
    }

    if (widget != null) {
      Object data = widget.getLayoutData();
      Margins m = null;
      if (data instanceof HasMargins) {
        m = ((HasMargins) data).getMargins();
      }
      if (m == null) {
        m = new Margins(0);
      }
      lastCenter = new Rectangle(centerX, centerY, centerW, centerH);
      lastCenter.setX(centerX + (m.getLeft() + sLeft));
      lastCenter.setY(centerY + (m.getTop() + sTop));
      lastCenter.setWidth(centerW - (m.getLeft() + m.getRight()));
      lastCenter.setHeight(centerH - (m.getTop() + m.getBottom()));
      applyLayout(widget, lastCenter);
    }
  }

  @Override
  protected XElement getContainerTarget() {
    return appearance.getContainerTarget(getElement());
  }

  protected LayoutRegion getRegion(Component component) {
    if (component == north) {
      return LayoutRegion.NORTH;
    } else if (component == south) {
      return LayoutRegion.SOUTH;
    } else if (component == west) {
      return LayoutRegion.WEST;
    } else if (component == east) {
      return LayoutRegion.EAST;
    }

    return null;
  }

  protected void onCollapse(ContentPanel panel) {
    BorderLayoutData data = (BorderLayoutData) panel.getLayoutData();

    LayoutRegion region = getRegion(panel);

    CollapsePanel cp = (CollapsePanel) panel.getData("collapse");
    if (cp == null) {
      cp = createCollapsePanel(panel, data, region);
    }

    cp.clearSizeCache();

    setCollapsed(panel, true);

    switch (region) {
      case WEST:
        setWestWidget(cp);
        break;
      case EAST:
        setEastWidget(cp);
        break;
      case NORTH:
        setNorthWidget(cp);
        break;
      case SOUTH:
        setSouthWidget(cp);
        break;
    }

    panel.clearSizeCache();

    doLayout();

    SplitBar bar = cp.getSplitBar();
    if (bar != null) {
      bar.sync();
    }

    fireEvent(new CollapseItemEvent<ContentPanel>(panel));
  }

  protected void onExpand(ContentPanel panel) {
    CollapsePanel cp = panel.getData("collapse");

    LayoutRegion region = getRegion(cp);

    switch (region) {
      case WEST:
        setWestWidget(panel);
        break;
      case EAST:
        setEastWidget(panel);
        break;
      case NORTH:
        setNorthWidget(panel);
        break;
      case SOUTH:
        setSouthWidget(panel);
        break;
    }

    setCollapsed(panel, false);

    BorderLayoutData data = (BorderLayoutData) panel.getLayoutData();
    data.setCollapsed(false);

    doLayout();

    fireEvent(new ExpandItemEvent<ContentPanel>(panel));
  }

  protected void onExpandClick(CollapsePanel collapse) {
    ContentPanel panel = collapse.getContentPanel();
    onExpand(panel);
  }

  @Override
  protected void onInsert(int index, Widget child) {
    child.addStyleName(CommonStyles.get().positionable());

    appearance.onInsert(child);

    if (!(child instanceof Component)) {
      return;
    }

    final Component c = (Component) child;

    if (c == getWidget()) {
      return;
    }

    final BorderLayoutData data = getLayoutData(c);

    if (c instanceof CollapsePanel) {
      final CollapsePanel collapse = (CollapsePanel) c;
      final BorderLayoutData panelData = (BorderLayoutData) collapse.getContentPanel().getLayoutData();
      SplitBar bar = collapse.getData("splitBar");
      if (bar == null || bar.getTargetWidget() != c) {

        bar = collapse.getSplitBar();
        bar.setCollapsible(true);
        c.setData("splitBar", bar);

        if (panelData.isCollapseHidden()) {
          collapse.collapseHidden();
        }

        bar.addSelectHandler(new SelectHandler() {
          @Override
          public void onSelect(SelectEvent event) {
            onExpandClick((CollapsePanel) c);
          }
        });
      }
    }

    if (data.isCollapsible() && c instanceof ContentPanel && !c.isRendered()) {
      final ContentPanel cp = (ContentPanel) c;
      cp.setCollapsible(true);
      cp.setHideCollapseTool(true);

      IconConfig config = ToolButton.DOUBLELEFT;

      switch (getRegion(cp)) {
        case NORTH:
          config = ToolButton.DOUBLEUP;
          break;
        case SOUTH:
          config = ToolButton.DOUBLEDOWN;
          break;
        case EAST:
          config = ToolButton.DOUBLERIGHT;
          break;
      }

      cp.getHeader().addTool(new ToolButton(config, new SelectHandler() {

        @Override
        public void onSelect(SelectEvent event) {
          cp.collapse();
        }
      }));
      cp.addBeforeCollapseHandler(collapseHandler);
      cp.addBeforeExpandHandler(collapseHandler);
    }

    if (data.isSplit()) {
      SplitBar bar = c.getData("splitBar");
      if (bar == null || bar.getTargetWidget() != c) {
        bar = createSplitBar(c);
        final SplitBar fbar = bar;

        if (data.isCollapseMini()) {
          bar.setCollapsible(true);
          switch (getRegion(c)) {
            case EAST:
              bar.updateMini(Direction.RIGHT);
              break;
            case WEST:
              bar.updateMini(Direction.LEFT);
              break;
            case NORTH:
              bar.updateMini(Direction.UP);
              break;
            case SOUTH:
              bar.updateMini(Direction.DOWN);
              break;
          }
        }
        if (data.isCollapseMini()) {
          bar.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
              ((ContentPanel) c).collapse();
            }
          });
        }
        bar.addSplitBarDragHandler(new SplitBarDragHandler() {

          @Override
          public void onDragEvent(SplitBarDragEvent event) {
            LayoutRegion region = getRegion(c);
            if (event.isStart()) {
              boolean side = region == LayoutRegion.WEST || region == LayoutRegion.EAST;
              int size = side ? c.getOffsetWidth() : c.getOffsetHeight();
              int centerSize = side ? lastCenter.getWidth() : lastCenter.getHeight();

              fbar.setMinSize(data.getMinSize());
              fbar.setMaxSize(Math.min(size + centerSize, data.getMaxSize()));

            } else {
              if (event.getSize() < 1) {
                return;
              }
              data.setSize(event.getSize());
              doLayout();
            }
          }
        });

      }
      bar.getElement().getStyle().setVisibility(Visibility.HIDDEN);
      c.setData("splitBar", bar);

      bar.setMinSize(data.getMinSize());
      bar.setMaxSize(data.getMaxSize() == 0 ? bar.getMaxSize() : data.getMaxSize());
      bar.setAutoSize(false);
    }
  }

  @Override
  protected void onRemove(Widget child) {
    super.onRemove(child);

    appearance.onRemove(child);

    if (north == child) {
      north = null;
    } else if (south == child) {
      south = null;
    } else if (east == child) {
      east = null;
    } else if (west == child) {
      west = null;
    }
    child.removeStyleName(CommonStyles.get().positionable());
  }

  private BorderLayoutData getLayoutData(Widget w) {
    Object o = w.getLayoutData();
    return (BorderLayoutData) ((o instanceof BorderLayoutData) ? o : new BorderLayoutData(100));
  }

  private native void setCollapsed(ContentPanel panel, boolean collapse) /*-{
		panel.@com.sencha.gxt.widget.core.client.ContentPanel::collapsed = collapse;
  }-*/;

  private void switchPanels(ContentPanel panel) {
    LayoutRegion region = getRegion(panel);

    BorderLayoutData data = (BorderLayoutData) getLayoutData(panel);
    remove(panel);

    CollapsePanel cp = (CollapsePanel) panel.getData("collapse");
    if (cp == null) {
      cp = createCollapsePanel(panel, data, region);
    }

    cp.clearSizeCache();

    setCollapsed(panel, true);

    switch (region) {
      case WEST:
        setWestWidget(cp);
        break;
      case EAST:
        setEastWidget(cp);
        break;
      case NORTH:
        setNorthWidget(cp);
        break;
      case SOUTH:
        setSouthWidget(cp);
        break;
    }

  }

}
