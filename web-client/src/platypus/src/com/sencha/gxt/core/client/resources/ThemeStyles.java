/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.core.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * Common theme specific styles. Themes are responsible for specifying a GWT
 * module rule to provide implementation of <code>ThemeAppearance</code>.
 */
public class ThemeStyles {

  public interface ThemeAppearance {
    Styles style();

    ImageResource moreIcon();
  }

  private static ThemeAppearance instance = GWT.create(ThemeAppearance.class);

  public interface Styles extends CssResource {

    String borderColor();

    String borderColorLight();

    String backgroundColorLight();

    String border();

    String borderLeft();

    String borderRight();

    String borderTop();

    String borderBottom();

  }

  /**
   * Returns the theme styles.
   * 
   * @return the styles
   */
  public static Styles getStyle() {
    return instance.style();
  }

  /**
   * Returns the singleton instance.
   * 
   * @return the instance
   */
  public static ThemeAppearance get() {
    return instance;
  }

}
