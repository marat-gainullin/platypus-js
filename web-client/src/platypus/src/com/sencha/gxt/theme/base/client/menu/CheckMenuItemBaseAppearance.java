/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.base.client.menu;

import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.menu.CheckMenuItem.CheckMenuItemAppearance;

public abstract class CheckMenuItemBaseAppearance extends MenuItemBaseAppearance implements CheckMenuItemAppearance {

  public interface CheckMenuItemResources extends MenuItemResources {

    CheckMenuItemStyle checkStyle();
    
    ImageResource checked();
    
    ImageResource unchecked();
    
    ImageResource groupChecked();

  }

  public interface CheckMenuItemStyle extends CssResource {

    String menuItemChecked();

  }

  private final CheckMenuItemResources resources;
  private final CheckMenuItemStyle checkStyle;

  public CheckMenuItemBaseAppearance(CheckMenuItemResources resources, MenuItemTemplate template) {
    super(resources, template);
    this.resources = resources;
    checkStyle = resources.checkStyle();
  }

  @Override
  public void applyChecked(XElement parent, boolean state) {
    parent.setClassName(checkStyle.menuItemChecked(), state);
  }

  @Override
  public ImageResource checked() {
    return resources.checked();
  }

  @Override
  public ImageResource unchecked() {
    return resources.unchecked();
  }

  @Override
  public ImageResource radio() {
    return resources.groupChecked();
  }

}
