/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.gray.client.resizable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.sencha.gxt.theme.base.client.resizable.ResizableBaseAppearance;

public class GrayResizableAppearance extends ResizableBaseAppearance {

  public interface GrayResizableResources extends ResizableResources, ClientBundle {

    ImageResource handleEast();

    ImageResource handleNortheast();

    ImageResource handleNorthwest();

    ImageResource handleSouth();

    ImageResource handleSoutheast();

    ImageResource handleSouthwest();

    @Source({"com/sencha/gxt/theme/base/client/resizable/Resizable.css", "GrayResizable.css"})
    GrayResizableStyle style();

  }

  public interface GrayResizableStyle extends ResizableStyle {
  }

  public GrayResizableAppearance() {
    this(GWT.<GrayResizableResources> create(GrayResizableResources.class));
  }

  public GrayResizableAppearance(ResizableResources resources) {
    super(resources);
  }

}
