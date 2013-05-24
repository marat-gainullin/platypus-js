/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.gray.client.tabs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.TabPanel.TabPanelBottomAppearance;

public class GrayTabPanelBottomAppearance extends GrayTabPanelAppearance implements TabPanelBottomAppearance {

  public interface BottomTemplate extends Template {

    @XTemplate(source = "TabPanelBottom.html")
    SafeHtml render(TabPanelStyle style);

  }

  public GrayTabPanelBottomAppearance() {
    this(GWT.<GrayTabPanelResources> create(GrayTabPanelResources.class), GWT
        .<BottomTemplate> create(BottomTemplate.class), GWT.<ItemTemplate> create(ItemTemplate.class));
  }

  public GrayTabPanelBottomAppearance(GrayTabPanelResources resources, BottomTemplate template,
      ItemTemplate itemTemplate) {
    super(resources, template, itemTemplate);
  }

  @Override
  public XElement getBar(XElement parent) {
    return parent.selectNode("." + style.tabFooter());
  }

  @Override
  public XElement getStrip(XElement parent) {
    return parent.selectNode("." + style.tabStripBottom());
  }

}
