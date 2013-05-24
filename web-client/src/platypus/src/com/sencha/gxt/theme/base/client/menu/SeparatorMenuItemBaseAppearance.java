/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.base.client.menu;

import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.resources.StyleInjectorHelper;
import com.sencha.gxt.widget.core.client.menu.SeparatorMenuItem.SeparatorMenuItemAppearance;

public abstract class SeparatorMenuItemBaseAppearance implements SeparatorMenuItemAppearance {

  public interface SeparatorMenuItemResources {

    SeparatorMenuItemStyle style();

  }

  public interface SeparatorMenuItemStyle extends CssResource {

    String menuSep();

  }

  public interface SeparatorMenuItemTemplate extends XTemplates {

    @XTemplate(source = "SeparatorMenuItem.html")
    SafeHtml template(SeparatorMenuItemStyle style);

  }

  protected final SeparatorMenuItemResources resources;
  protected SeparatorMenuItemTemplate template;

  private SeparatorMenuItemStyle style;

  public SeparatorMenuItemBaseAppearance(SeparatorMenuItemResources resources,
      SeparatorMenuItemTemplate template) {
    this.resources = resources;
    this.style = resources.style();
    
    StyleInjectorHelper.ensureInjected(this.style, true);
    
    this.template = template;
  }

  public void render(SafeHtmlBuilder result) {
    result.append(template.template(style));
  }

}
