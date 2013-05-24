/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.blue.client.resizable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.sencha.gxt.theme.base.client.resizable.ResizableBaseAppearance;

public class BlueResizableAppearance extends ResizableBaseAppearance {

  public interface BlueResizableResources extends ResizableResources, ClientBundle {

    ImageResource handleEast();

    ImageResource handleNortheast();

    ImageResource handleNorthwest();

    ImageResource handleSouth();

    ImageResource handleSoutheast();

    ImageResource handleSouthwest();

    @Source({"com/sencha/gxt/theme/base/client/resizable/Resizable.css", "BlueResizable.css"})
    BlueResizableStyle style();

  }

  public interface BlueResizableStyle extends ResizableStyle {
  }

  public BlueResizableAppearance() {
    this(GWT.<BlueResizableResources> create(BlueResizableResources.class));
  }

  public BlueResizableAppearance(ResizableResources resources) {
    super(resources);
  }

}
