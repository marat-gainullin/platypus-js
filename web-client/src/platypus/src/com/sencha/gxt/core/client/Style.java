/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.core.client;

import com.sencha.gxt.core.client.resources.CommonStyles;

/**
 * Defines GXT public constants and enumerations.
 */
public class Style {

  /**
   * Direction enumeration.
   */
  public enum Direction {
    UP, DOWN, LEFT, RIGHT;
  }
  
  public enum Side {
    LEFT, RIGHT, TOP, BOTTOM;
  }

  public enum Anchor {
    TOP("t"), TOP_RIGHT("tr"), RIGHT("r"), BOTTOM_RIGHT("br"), BOTTOM("b"), BOTTOM_LEFT("bl"), LEFT("l"), TOP_LEFT("tl"), CENTER(
        "c");

    private final String value;

    private Anchor(String value) {
      this.value = value;
    }

    public String value() {
      return value;
    }
  }

  public static class AnchorAlignment {

    private Anchor align;
    private Anchor targetAlign;
    private boolean constrainViewport;
    
    public AnchorAlignment(Anchor align) {
      this(align, Anchor.BOTTOM_LEFT);
    }

    public AnchorAlignment(Anchor align, Anchor targetAlign) {
      this(align, targetAlign, true);
    }

    public AnchorAlignment(Anchor align, Anchor targetAlign, boolean constrainViewport) {
      this.align = align;
      this.targetAlign = targetAlign;
      this.constrainViewport = constrainViewport;
    }

    public Anchor getAlign() {
      return align;
    }

    public Anchor getTargetAlign() {
      return targetAlign;
    }

    public boolean isConstrainViewport() {
      return constrainViewport;
    }

  }

  /**
   * HideMode enumeration.
   */
  public enum HideMode {
    OFFSETS(CommonStyles.get().hideOffsets()), VISIBILITY(CommonStyles.get().hideVisibility()), DISPLAY(
        CommonStyles.get().hideDisplay());

    private final String value;

    private HideMode(String value) {
      this.value = value;
    }

    public String value() {
      return value;
    }
  }

  /**
   * Horizontal alignment enumeration.
   */
  public enum HorizontalAlignment {
    LEFT, CENTER, RIGHT
  }

  /**
   * Vertical alignment enumerations.
   */
  public enum VerticalAlignment {
    TOP, MIDDLE, BOTTOM;
  }

  /**
   * Layout out regions.
   */
  public enum LayoutRegion {
    NORTH, EAST, SOUTH, WEST, CENTER;
  }

  /**
   * Orientation enumeration.
   */
  public enum Orientation {
    VERTICAL, HORIZONTAL;
  }

  /**
   * Scroll direction enumeration.
   */
  public enum ScrollDir {
    VERTICAL, HORIZONTAL;
  }

  /**
   * Scroll direction enumeration.
   */
  public enum ScrollDirection {
    LEFT, TOP;
  }

  /**
   * Selection mode enumeration.
   */
  public enum SelectionMode {
    SINGLE, SIMPLE, MULTI;
  }

  /**
   * Indicates that a default value should be used (value is -1).
   */
  public static final int DEFAULT = -1;

  /**
   * Constant for marking a string as undefined rather than null.
   */
  public static final String UNDEFINED = "undefined";
}
