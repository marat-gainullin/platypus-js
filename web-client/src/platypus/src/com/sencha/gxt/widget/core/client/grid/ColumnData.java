/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.grid;

import com.google.gwt.safecss.shared.SafeStyles;

public class ColumnData {

  /**
   * The column styles.
   */
  private SafeStyles styles;

  /**
   * The column css.
   */
  private String css;

  public String getClassNames() {
    return css;
  }

  public SafeStyles getStyles() {
    return styles;
  }

  public void setClassNames(String css) {
    this.css = css;
  }

  public void setStyles(SafeStyles styles) {
    this.styles = styles;
  }

}
