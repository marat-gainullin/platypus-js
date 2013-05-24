/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.gray.client.grid;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.theme.base.client.grid.GridBaseAppearance;

public class GrayGridAppearance extends GridBaseAppearance {

  public interface GrayGridStyle extends GridStyle {
    
  }
  
  public interface GrayGridResources extends GridResources  {
    
    @Source({"com/sencha/gxt/theme/base/client/grid/Grid.css", "GrayGrid.css"})
    @Override
    GrayGridStyle css();
  }
  
  
  public GrayGridAppearance() {
    this(GWT.<GrayGridResources> create(GrayGridResources.class));
  }

  public GrayGridAppearance(GrayGridResources resources) {
    super(resources);
  }
}
