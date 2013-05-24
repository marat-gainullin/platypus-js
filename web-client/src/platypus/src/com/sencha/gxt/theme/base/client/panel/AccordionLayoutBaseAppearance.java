/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.base.client.panel;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.widget.core.client.Header.HeaderAppearance;
import com.sencha.gxt.widget.core.client.button.IconButton.IconConfig;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer.AccordionLayoutAppearance;

public abstract class AccordionLayoutBaseAppearance extends ContentPanelBaseAppearance implements AccordionLayoutAppearance{

  public AccordionLayoutBaseAppearance(ContentPanelResources resources) {
    super(resources);
  }
  
  @Override
  public HeaderAppearance getHeaderAppearance() {
    return GWT.create(HeaderAppearance.class);
  }
  
  @Override
  public IconConfig collapseIcon() {
    return ToolButton.MINUS;
  }
  
  @Override
  public IconConfig expandIcon() {
    return ToolButton.PLUS;
  }

}
