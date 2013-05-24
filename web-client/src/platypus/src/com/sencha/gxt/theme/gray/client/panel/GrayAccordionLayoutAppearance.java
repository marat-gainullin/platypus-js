/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.gray.client.panel;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.theme.base.client.panel.AccordionLayoutBaseAppearance;
import com.sencha.gxt.theme.base.client.widget.HeaderDefaultAppearance;

public class GrayAccordionLayoutAppearance extends AccordionLayoutBaseAppearance {
  
  public interface GrayAccordionLayoutResources extends ContentPanelResources {

    @Source({"com/sencha/gxt/theme/base/client/panel/ContentPanel.css", "GrayContentPanel.css"})
    @Override
    GrayAccordionLayoutStyle style();

  }

  public interface GrayAccordionLayoutStyle extends ContentPanelStyle {

  }
  
  public GrayAccordionLayoutAppearance() {
    super(GWT.<GrayAccordionLayoutResources> create(GrayAccordionLayoutResources.class));
  }

  public GrayAccordionLayoutAppearance(GrayAccordionLayoutResources resources) {
    super(resources);
  }
  
  @Override
  public HeaderDefaultAppearance getHeaderAppearance() {
    return new GrayAccordionHeaderAppearance();
  }
}
