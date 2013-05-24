/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.container;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiChild;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.resources.CommonStyles;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.Padding;

/**
 * A base class for box layout containers. Provides behavior common to all box
 * layout containers, including packing, padding and the action to take on
 * insert and remove.
 */
public abstract class BoxLayoutContainer extends InsertResizeContainer {

  @SuppressWarnings("javadoc")
  public interface BoxLayoutContainerAppearance {

    public void render(SafeHtmlBuilder sb);

  }

  /**
   * Specifies box layout parameters which control the minimum and maximum size,
   * as well as the "flex" value.
   */
  public static class BoxLayoutData extends MarginData implements LayoutData {

    private double flex;

    private int maxSize = Integer.MAX_VALUE;

    private int minSize;

    /**
     * Creates a box layout data with default values for <code>margins</code>
     * (0), <code>minSize</code> (0), <code>maxSize</code> (unlimited) and
     * <code>flex</code> (unflexed).
     */
    public BoxLayoutData() {
    }

    /**
     * Creates a box layout data with the specified margins.
     * 
     * @param margins the margins for the box layout data
     */
    public BoxLayoutData(Margins margins) {
      super(margins);
    }

    /**
     * Returns the flex value which is a weight used by the layout for sizing
     * calculations.
     * 
     * @return the flex
     */
    public double getFlex() {
      return flex;
    }

    /**
     * Returns the maximum size to which widgets will be constrained (defaults
     * to unlimited).
     * 
     * @return the maximum allowable size when resizing widgets
     */
    public int getMaxSize() {
      return maxSize;
    }

    /**
     * Returns the minimum size to which widgets will be constrained (defaults
     * to 0)
     * 
     * @return the minimum allowable size when resizing widgets
     */
    public int getMinSize() {
      return minSize;
    }

    /**
     * Sets the weighted flex value. Each child item with a <tt>flex</tt> value
     * will be flexed <b>horizontally</b> according to each item's
     * <b>relative</b> <tt>flex</tt> value compared to the sum of all items with
     * a <tt>flex</tt> value specified. Values of 0 (default) will cause the
     * child to not be 'flexed' with the initial width not being changed.
     * 
     * @param flex the flex value
     */
    public void setFlex(double flex) {
      this.flex = flex;
    }

    /**
     * Sets the maximum size to which widgets will be constrained.
     * 
     * @param maxSize the maximum allowable size when resizing widgets
     */
    public void setMaxSize(int maxSize) {
      this.maxSize = maxSize;
    }

    /**
     * Sets the minimum size to which widgets will be constrained.
     * 
     * @param minSize the minimum allowable size when resizing widgets
     */
    public void setMinSize(int minSize) {
      this.minSize = minSize;
    }

  }
  /**
   * BoxLayoutPack enumeration. Box Layout can have three states:
   */
  public enum BoxLayoutPack {
    /**
     * Children are packed together at <b>mid-height</b> of container.
     */
    CENTER,
    /**
     * Children are packed together at <b>bottom</b> side of container.
     */
    END,
    /**
     * Children are packed together at <b>top</b> side of container.
     */
    START;
  }

  private boolean adjustForFlexRemainder;
  private BoxLayoutPack pack = BoxLayoutPack.START;
  private Padding padding;
  private int scrollOffset = 0;

  /**
   * Creates a box layout container with the default appearance.
   */
  public BoxLayoutContainer() {
    this(GWT.<BoxLayoutContainerAppearance> create(BoxLayoutContainerAppearance.class));
  }

  /**
   * Creates a box layout container with the specified appearance.
   * 
   * @param appearance the box layout container appearance
   */
  public BoxLayoutContainer(BoxLayoutContainerAppearance appearance) {
    SafeHtmlBuilder sb = new SafeHtmlBuilder();
    appearance.render(sb);
    setElement(XDOM.create(sb.toSafeHtml()));
  }

  /**
   * Adds a widget to this box layout container with the specified layout
   * parameters.
   * 
   * @param child the widget to add
   * @param layoutData the layout parameters
   */
  @UiChild(tagname = "child")
  public void add(IsWidget child, BoxLayoutData layoutData) {
    if (child != null) {
      child.asWidget().setLayoutData(layoutData);
    }
    super.add(child);
  }

  /**
   * Returns the layout's pack value.
   * 
   * @return the pack value
   */
  public BoxLayoutPack getPack() {
    return pack;
  }

  /**
   * Returns the padding between widgets.
   * 
   * @return the padding between widgets.
   */
  public Padding getPadding() {
    return padding;
  }

  /**
   * Returns the scroll offset.
   * 
   * @return the scroll offset
   */
  public int getScrollOffset() {
    return scrollOffset;
  }

  /**
   * Inserts a widget into the box layout container.
   * 
   * @param w the widget
   * @param beforeIndex the insert index
   * @param layoutData the layout parameters
   */
  public void insert(IsWidget w, int beforeIndex, BoxLayoutData layoutData) {
    if (w != null) {
      w.asWidget().setLayoutData(layoutData);
    }
    super.insert(w, beforeIndex);
  }

  /**
   * Returns true if the remaining space after flex calculation is applied to
   * the last widget being flexed.
   * 
   * @return true if adjusting for flex remainder
   */
  public boolean isAdjustForFlexRemainder() {
    return adjustForFlexRemainder;
  }

  /**
   * Set to true if the remaining space after flex calculation should be applied
   * to the last widget being flexed.
   * 
   * @param adjustForFlexRemainder true to add the space
   */
  public void setAdjustForFlexRemainder(boolean adjustForFlexRemainder) {
    this.adjustForFlexRemainder = adjustForFlexRemainder;
  }

  /**
   * Sets how the child items of the container are packed together.
   * 
   * @param pack the pack value
   */
  public void setPack(BoxLayoutPack pack) {
    this.pack = pack;
  }

  /**
   * Sets the padding to use for the box layout container (i.e. the container
   * itself, independent of any contained widgets).
   * 
   * @param padding the padding
   */
  public void setPadding(Padding padding) {
    this.padding = padding;
  }

  /**
   * Sets the scroll offset (defaults to 0).
   * 
   * @param scrollOffset the scroll offset
   */
  public void setScrollOffset(int scrollOffset) {
    this.scrollOffset = scrollOffset;
  }

  @Override
  protected XElement getContainerTarget() {
    return (XElement) getElement().getFirstChildElement();
  }

  @Override
  protected void onInsert(int index, Widget child) {
    child.addStyleName(CommonStyles.get().positionable());
  }

  @Override
  protected void onRemove(Widget child) {
    child.removeStyleName(CommonStyles.get().positionable());
  }

}
