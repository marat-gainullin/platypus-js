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
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.widget.core.client.PlainTabPanel;
import com.sencha.gxt.widget.core.client.PlainTabPanel.PlainTabPanelAppearance;

/**
 * A gray-colored appearance for {@link PlainTabPanel}. This appearance differs
 * from {@link GrayTabPanelAppearance} in that it has a simplified tab strip.
 */
public class GrayPlainTabPanelAppearance extends GrayTabPanelAppearance implements PlainTabPanelAppearance {

  public interface GrayPlainTabPanelResources extends GrayTabPanelResources {

    @Source({
        "com/sencha/gxt/theme/base/client/tabs/TabPanel.css", "GrayTabPanel.css",
        "com/sencha/gxt/theme/base/client/tabs/PlainTabPanel.css", "GrayPlainTabPanel.css"})
    GrayPlainTabPanelStyle style();

  }

  public interface GrayPlainTabPanelStyle extends GrayTabPanelStyle {

    String tabStripSpacer();

  }

  public interface PlainTabPanelTemplates extends Template {

    @XTemplate(source = "com/sencha/gxt/theme/base/client/tabs/TabPanel.html")
    SafeHtml render(TabPanelStyle style);

    @XTemplate(source = "com/sencha/gxt/theme/base/client/tabs/PlainTabPanel.html")
    SafeHtml renderPlain(GrayPlainTabPanelStyle style);

  }

  private final PlainTabPanelTemplates template;
  private final GrayPlainTabPanelStyle style;

  public GrayPlainTabPanelAppearance() {
    this(GWT.<GrayPlainTabPanelResources> create(GrayPlainTabPanelResources.class),
        GWT.<PlainTabPanelTemplates> create(PlainTabPanelTemplates.class),
        GWT.<ItemTemplate> create(ItemTemplate.class));
  }

  public GrayPlainTabPanelAppearance(GrayPlainTabPanelResources resources, PlainTabPanelTemplates template,
      ItemTemplate itemTemplate) {
    super(resources, template, itemTemplate);
    this.style = resources.style();
    this.template = template;
  }

  @Override
  public void render(SafeHtmlBuilder builder) {
    builder.append(template.renderPlain(style));
  }

}
