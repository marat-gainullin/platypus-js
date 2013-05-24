/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.base.client.status;

import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.IconHelper;
import com.sencha.gxt.widget.core.client.Status;

public abstract class StatusBaseAppearance implements Status.StatusAppearance {

  public interface StatusResources {

    ImageResource loading();

    StatusStyle style();

  }

  public interface StatusStyle extends CssResource {

    String status();

    String statusIcon();

    String statusText();

  }

  public interface Template extends XTemplates {

    @XTemplate(source = "Status.html")
    SafeHtml template(StatusStyle style);

  }

  private final StatusResources resources;
  private final StatusStyle style;
  private Template template;

  public StatusBaseAppearance(StatusResources resources, Template template) {
    this.resources = resources;
    style = resources.style();
    style.ensureInjected();

    this.template = template;
  }

  @Override
  public XElement getTextElem(XElement parent) {
    return parent.selectNode("." + style.statusText());
  }


  @Override
  public void render(SafeHtmlBuilder sb) {
    sb.append(template.template(style));
  }

  @Override
  public ImageResource getBusyIcon() {
    return resources.loading();
  }

  @Override
  public void onUpdateIcon(XElement parent, ImageResource icon) {
    XElement iconWrap = parent.selectNode("." + style.statusIcon());
    iconWrap.setVisible(icon != null);
    if (icon != null) {
      iconWrap.removeChildren();
      iconWrap.appendChild(IconHelper.getElement(icon));
    }
  }

}
