/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.base.client.toolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.widget.core.client.toolbar.SeparatorToolItem.SeparatorToolItemAppearance;

public class SeparatorToolItemDefaultAppearance implements SeparatorToolItemAppearance {

  public interface SeparatorToolItemResources extends ClientBundle {

    @Source("SeparatorToolItem.css")
    SeparatorToolItemStyle css();

    @ImageOptions(repeatStyle = RepeatStyle.Vertical)
    ImageResource background();
  }
  
  public interface SeparatorToolItemStyle extends CssResource {
    
    String separator();
  }
  
  public interface Template extends XTemplates {
    @XTemplate("<div class=\"{style.separator}\"></div>")
    SafeHtml render(SeparatorToolItemStyle style);
  }
  
  protected final SeparatorToolItemStyle style;
  protected Template template;
  
  public SeparatorToolItemDefaultAppearance() {
    this(GWT.<SeparatorToolItemResources>create(SeparatorToolItemResources.class), GWT.<Template>create(Template.class));
  }
  
  public SeparatorToolItemDefaultAppearance(SeparatorToolItemResources resources, Template template) {
    this.style = resources.css();
    this.style.ensureInjected();
    this.template = template; 
  }
  
  @Override
  public void render(SafeHtmlBuilder sb) {
    sb.append(template.render(style));
  }

}
