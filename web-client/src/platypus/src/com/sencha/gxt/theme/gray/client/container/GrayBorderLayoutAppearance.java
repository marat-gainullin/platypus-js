/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.gray.client.container;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.theme.base.client.container.BorderLayoutBaseAppearance;

public class GrayBorderLayoutAppearance extends BorderLayoutBaseAppearance {

  public interface GrayBorderLayoutResources extends BorderLayoutResources {
    @Override
    @Source({"com/sencha/gxt/theme/base/client/container/BorderLayout.css", "GrayBorderLayout.css"})
    public GrayBorderLayoutStyle css();
  }

  public interface GrayBorderLayoutStyle extends BorderLayoutStyle {

  }

  public GrayBorderLayoutAppearance() {
    super(GWT.<GrayBorderLayoutResources> create(GrayBorderLayoutResources.class));
  }

}
