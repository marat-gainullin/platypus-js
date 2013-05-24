/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.toolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.util.Util;
import com.sencha.gxt.widget.core.client.Component;

/**
 * A label tool item.
 */
public class LabelToolItem extends Component {

  public interface LabelToolItemAppearance {
    void render(SafeHtmlBuilder sb);
  }

  private String label;
  private final LabelToolItemAppearance appearance;

  /**
   * Creates a new label.
   */
  public LabelToolItem() {
    this(GWT.<LabelToolItemAppearance> create(LabelToolItemAppearance.class));
  }

  public LabelToolItem(LabelToolItemAppearance appearance) {
    this.appearance = appearance;

    SafeHtmlBuilder markupBuilder = new SafeHtmlBuilder();
    this.appearance.render(markupBuilder);

    setElement(XDOM.create(markupBuilder.toSafeHtml()));
  }

  /**
   * Creates a new label.
   * 
   * @param label the label
   */
  public LabelToolItem(String label) {
    this();
    setLabel(label);
  }

  /**
   * Returns the item's label.
   * 
   * @return the label
   */
  public String getLabel() {
    return label;
  }

  /**
   * Sets the item's label.
   * 
   * @param label the item's label
   */
  public void setLabel(String label) {
    this.label = label;
    getElement().setInnerHTML(Util.isEmptyString(label) ? "&#160;" : label);
  }

}
