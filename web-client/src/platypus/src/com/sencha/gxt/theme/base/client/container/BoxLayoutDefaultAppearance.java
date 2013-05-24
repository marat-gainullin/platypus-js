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
import com.sencha.gxt.core.client.resources.StyleInjectorHelper;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutContainerAppearance;

public class BoxLayoutDefaultAppearance implements BoxLayoutContainerAppearance {

  private final BoxLayoutBaseResources resources;
  private final BoxLayoutTemplate template;

  public interface BoxLayoutBaseResources extends ClientBundle {

    @Source("BoxLayout.css")
    BoxLayoutStyle style();

  }

  public interface BoxLayoutStyle extends CssResource {

    String container();

    String inner();

  }

  public interface BoxLayoutTemplate extends XTemplates {

    @XTemplate("<div class=\"{style.container}\"><div class=\"{style.inner}\"></div></div>")
    SafeHtml template(BoxLayoutStyle style);

  }

  public BoxLayoutDefaultAppearance() {
    this(GWT.<BoxLayoutBaseResources> create(BoxLayoutBaseResources.class),
        GWT.<BoxLayoutTemplate> create(BoxLayoutTemplate.class));
  }

  public BoxLayoutDefaultAppearance(BoxLayoutBaseResources resources,
      BoxLayoutTemplate template) {
    this.resources = resources;
    this.template = template;
    
    StyleInjectorHelper.ensureInjected(resources.style(), true);
  }

  @Override
  public void render(SafeHtmlBuilder sb) {
    sb.append(template.template(resources.style()));
  }

}
