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
import com.sencha.gxt.widget.core.client.Component;

/**
 * A tool bar separator.
 */
public class SeparatorToolItem extends Component {

  public interface SeparatorToolItemAppearance {
    void render(SafeHtmlBuilder sb);
  }

  protected SeparatorToolItemAppearance appearance;

  public SeparatorToolItem() {
    this(GWT.<SeparatorToolItemAppearance>create(SeparatorToolItemAppearance.class));
  }

  public SeparatorToolItem(SeparatorToolItemAppearance appearance) {
    this.appearance = appearance;

    SafeHtmlBuilder markupBuilder = new SafeHtmlBuilder();
    appearance.render(markupBuilder);

    setElement(XDOM.create(markupBuilder.toSafeHtml()));
  }

}
