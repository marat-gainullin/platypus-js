/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.base.client.statusproxy;

import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.resources.StyleInjectorHelper;
import com.sencha.gxt.dnd.core.client.StatusProxy.StatusProxyAppearance;

public abstract class StatusProxyBaseAppearance implements StatusProxyAppearance {

  public interface StatusProxyResources {

    ImageResource dropAllowed();

    ImageResource dropNotAllowed();

    StatusProxyStyle style();

  }

  public interface StatusProxyStyle extends CssResource {

    String dragGhost();

    String dropAllowed();

    String dropDisallowed();

    String dropIcon();

    String proxy();

  }

  public interface StatusProxyTemplates extends XTemplates {

    @XTemplate("<div class=\"{style.proxy}\"><div class=\"{style.dropIcon}\"></div><div class=\"{style.dragGhost}\"></div></div>")
    SafeHtml template(StatusProxyBaseAppearance.StatusProxyStyle style);

  }

  private final StatusProxyTemplates templates;
  private final StatusProxyResources resources;
  private final StatusProxyStyle style;

  public StatusProxyBaseAppearance(StatusProxyBaseAppearance.StatusProxyResources resources,
      StatusProxyBaseAppearance.StatusProxyTemplates templates) {
    this.resources = resources;
    style = resources.style();
    this.templates = templates;

    StyleInjectorHelper.ensureInjected(style, true);
  }

  @Override
  public void render(SafeHtmlBuilder builder) {
    builder.append(templates.template(style));
  }

  public void setStatus(Element parent, boolean allowed) {
    if (allowed) {
      setStatus(parent, resources.dropAllowed());
    } else {
      setStatus(parent, resources.dropNotAllowed());
    }
  }

  public void setStatus(Element parent, ImageResource icon) {
    XElement wrap = iconWrap(parent);
    wrap.setInnerHTML("");
    if (icon != null) {
      wrap.appendChild(getImage(icon));
    }
  }

  @Override
  public void update(Element parent, String html) {
    getDragGhost(parent).setInnerHTML(html);
  }

  protected XElement iconWrap(Element parent) {
    return parent.getFirstChildElement().cast();
  }

  private Element getDragGhost(Element parent) {
    return XElement.as(parent).select("." + style.dragGhost()).getItem(0);
  }

  private Element getImage(ImageResource ir) {
    return AbstractImagePrototype.create(ir).createElement();
  }

}
