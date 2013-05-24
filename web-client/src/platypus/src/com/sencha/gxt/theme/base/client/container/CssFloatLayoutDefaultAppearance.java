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
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer.CssFloatLayoutAppearance;

public class CssFloatLayoutDefaultAppearance implements CssFloatLayoutAppearance {

  public interface CssFloatLayoutTemplate extends XTemplates {
    @XTemplate("<div class='{style.container}'><div class='{style.inner}'></div></div>")
    SafeHtml render(CssFloatLayoutStyle style);
  }

  public interface CssFloatLayoutResources extends ClientBundle {
    @Source("CssFloatLayout.css")
    CssFloatLayoutStyle css();
  }

  public interface CssFloatLayoutStyle extends CssResource {
    String container();

    String inner();
    
    String child();
  }

  private final CssFloatLayoutResources resources;
  private final CssFloatLayoutStyle style;
  private CssFloatLayoutTemplate template;

  public CssFloatLayoutDefaultAppearance() {
    this(GWT.<CssFloatLayoutResources> create(CssFloatLayoutResources.class));
  }

  public CssFloatLayoutDefaultAppearance(CssFloatLayoutResources resources) {
    this.resources = resources;
    this.style = this.resources.css();
    this.style.ensureInjected();

    this.template = GWT.create(CssFloatLayoutTemplate.class);
  }

  @Override
  public void render(SafeHtmlBuilder sb) {
    sb.append(template.render(style));
  }

  @Override
  public XElement getContainerTarget(XElement parent) {
    return parent.getFirstChildElement().cast();
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
