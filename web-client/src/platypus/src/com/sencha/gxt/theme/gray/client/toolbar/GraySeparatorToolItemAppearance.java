/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.gray.client.toolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.sencha.gxt.theme.base.client.toolbar.SeparatorToolItemDefaultAppearance;

public class GraySeparatorToolItemAppearance extends SeparatorToolItemDefaultAppearance {

  public interface GraySeparatorToolItemResources extends SeparatorToolItemResources {
    @Override
    @Source(value = "separatorBackground.gif")
    public ImageResource background();
  }
  
  public GraySeparatorToolItemAppearance() {
    this(GWT.<GraySeparatorToolItemResources>create(GraySeparatorToolItemResources.class), GWT.<Template>create(Template.class));
  }
  
  public GraySeparatorToolItemAppearance(GraySeparatorToolItemResources resources, Template template) {
    super(resources, template);
  }

}
