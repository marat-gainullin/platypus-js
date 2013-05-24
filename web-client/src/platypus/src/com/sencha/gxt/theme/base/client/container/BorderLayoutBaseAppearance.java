/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.base.client.container;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutAppearance;

public abstract class BorderLayoutBaseAppearance implements BorderLayoutAppearance {

  public interface BorderLayoutResources extends ClientBundle {
    @Source("BorderLayout.css")
    BorderLayoutStyle css();
  }

  public interface BorderLayoutStyle extends CssResource {
    String container();

    String child();
  }

  private final BorderLayoutResources resources;
  private final BorderLayoutStyle style;

  public BorderLayoutBaseAppearance() {
    this(GWT.<BorderLayoutResources> create(BorderLayoutResources.class));
  }

  public BorderLayoutBaseAppearance(BorderLayoutResources resources) {
    this.resources = resources;
    this.style = this.resources.css();
    this.style.ensureInjected();
  }

  @Override
  public XElement getContainerTarget(XElement parent) {
    return parent;
  }

  @Override
  public void render(SafeHtmlBuilder sb) {
    sb.appendHtmlConstant("<div class='" + style.container() + "'></div>");
  }

  @Override
  public void onInsert(Widget child) {
    child.addStyleName(style.child());
  }

  @Override
  public void onRemove(Widget child) {
    child.removeStyleName(style.child());
  }

}
