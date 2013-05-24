/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.base.client.container;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.resources.StyleInjectorHelper;
import com.sencha.gxt.widget.core.client.box.MessageBox.MessageBoxAppearance;


public class MessageBoxDefaultAppearance implements MessageBoxAppearance {

  public interface MessageBoxResources extends ClientBundle {
    
    @Source("MessageBox.css")
    MessageBoxBaseStyle style();
  }
  
  public interface MessageBoxBaseStyle extends CssResource {
    String content();
    
    String icon();
    
    String message();
  }
  
  interface Template extends XTemplates {
    @XTemplate(source = "MessageBox.html")
    SafeHtml render(MessageBoxBaseStyle style);
  }
  
  protected MessageBoxResources resources;
  protected MessageBoxBaseStyle style;
  protected Template template;

  public MessageBoxDefaultAppearance() {
    this(GWT.<MessageBoxResources>create(MessageBoxResources.class));
  }
  
  public MessageBoxDefaultAppearance(MessageBoxResources resources) {
    this.resources = resources;
    this.style = resources.style();
    
    StyleInjectorHelper.ensureInjected(this.style, true);
    
    this.template = GWT.<Template>create(Template.class);
  }

  @Override
  public XElement getContentElement(XElement parent) {
    return parent.selectNode("." + style.content());
  }

  @Override
  public XElement getIconElement(XElement parent) {
    return parent.selectNode("." + style.icon());
  }

  @Override
  public XElement getMessageElement(XElement parent) {
    return parent.selectNode("." + style.message());
  }

  @Override
  public void render(SafeHtmlBuilder sb) {
    sb.append(template.render(style));
  }
  
}
