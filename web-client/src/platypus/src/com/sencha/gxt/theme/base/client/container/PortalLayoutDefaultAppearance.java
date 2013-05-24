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
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.widget.core.client.container.PortalLayoutContainer.PortalLayoutAppearance;

public class PortalLayoutDefaultAppearance implements PortalLayoutAppearance {

  public interface PortalLayoutTemplate extends XTemplates {
    @XTemplate("<div class='{style.insert}'><div></div></div>")
    SafeHtml render(PortalLayoutStyle style);
  }

  public interface PortalLayoutResources extends ClientBundle {
    @Source("PortalLayout.css")
    PortalLayoutStyle css();
  }

  public interface PortalLayoutStyle extends CssResource {
    String insert();
  }

  private final PortalLayoutResources resources;
  private PortalLayoutTemplate template;

  public PortalLayoutDefaultAppearance() {
    this(GWT.<PortalLayoutResources> create(PortalLayoutResources.class));
  }

  public PortalLayoutDefaultAppearance(PortalLayoutResources resources) {
    this.resources = resources;
    this.resources.css().ensureInjected();

    this.template = GWT.create(PortalLayoutTemplate.class);
  }

  @Override
  public void renderInsert(SafeHtmlBuilder sb) {
    sb.append(template.render(resources.css()));
  }

}
