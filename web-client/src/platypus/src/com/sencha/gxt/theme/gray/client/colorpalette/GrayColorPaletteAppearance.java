/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.gray.client.colorpalette;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.sencha.gxt.theme.base.client.colorpalette.ColorPaletteBaseAppearance;

public class GrayColorPaletteAppearance extends ColorPaletteBaseAppearance {

  public interface GrayColorPaletteResources extends ColorPaletteBaseAppearance.ColorPaletteResources, ClientBundle {

    @Source({"com/sencha/gxt/theme/base/client/colorpalette/ColorPalette.css", "GrayColorPalette.css"})
    GrayColorPaletteStyle style();

  }

  public interface GrayColorPaletteStyle extends ColorPaletteStyle {
  }

  public GrayColorPaletteAppearance() {
    this(GWT.<GrayColorPaletteResources> create(GrayColorPaletteResources.class),
        GWT.<BaseColorPaletteTemplate> create(BaseColorPaletteTemplate.class));
  }

  public GrayColorPaletteAppearance(GrayColorPaletteResources resources, BaseColorPaletteTemplate template) {
    super(resources, template);
  }

}
