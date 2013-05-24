/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.base.client.field;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.resources.StyleInjectorHelper;
import com.sencha.gxt.widget.core.client.form.FieldSet.FieldSetAppearance;

public class FieldSetDefaultAppearance implements FieldSetAppearance {

  public interface Template extends XTemplates {
    @XTemplate(source = "FieldSet.html")
    SafeHtml render(FieldSetStyle style);
  }
  
  public interface FieldSetResources extends ClientBundle {

    @Source({"FieldSet.css"})
    FieldSetStyle css();

  }

  public interface FieldSetStyle extends CssResource {
    String fieldSet();
    
    String legend();
    
    String toolWrap();
    
    String header();
    
    String body();
    
    String collapsed();
    
    String noborder();
  }

  private final FieldSetResources resources;
  private final FieldSetStyle style;
  private final Template template;

  public FieldSetDefaultAppearance() {
    this(GWT.<FieldSetResources> create(FieldSetResources.class));
  }

  public FieldSetDefaultAppearance(FieldSetResources resources) {
    this.resources = resources;
    this.style = this.resources.css();
   
    StyleInjectorHelper.ensureInjected(this.style, true);
    
    this.template = GWT.create(Template.class);
  }

  @Override
  public void render(SafeHtmlBuilder sb) {
    sb.append(template.render(style));
  }

  @Override
  public XElement getTextElement(XElement parent) {
    return parent.selectNode("." + style.header());
  }

  @Override
  public XElement getToolElement(XElement parent) {
    return parent.selectNode("." + style.toolWrap());
  }

  @Override
  public XElement getContainerTarget(XElement parent) {
    return parent.selectNode("." + style.body());
  }


  @Override
  public void onCollapse(XElement parent, boolean collapse) {
    
    parent.setClassName(style.collapsed(), collapse);
  }

}
