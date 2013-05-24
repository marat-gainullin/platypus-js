/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.base.client.info;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.widget.core.client.info.DefaultInfoConfig;

public class DefaultInfoConfigDefaultAppearance implements DefaultInfoConfig.DefaultInfoConfigAppearance {

  public interface InfoConfigStyle extends CssResource {
    
    String info();
    
    String infoTitle();
    
    String infoMessage();
  }
  
  public interface InfoConfigResources extends ClientBundle {
    
    @Source("InfoDefault.css")
    InfoConfigStyle style();
    
  }
  
  private InfoConfigResources resources;
  private InfoConfigStyle style;
  
  public DefaultInfoConfigDefaultAppearance() {
    this((InfoConfigResources)GWT.create(InfoConfigResources.class));
  }
  
  public DefaultInfoConfigDefaultAppearance(InfoConfigResources resources) {
    this.resources = resources;
    this.style = this.resources.style();
    this.style.ensureInjected();
  }
  
  @Override
  public void render(SafeHtmlBuilder sb, String title, String message) {

    if (title != null) {
      sb.appendHtmlConstant("<div class='" + style.infoTitle() + "'>");
      sb.appendHtmlConstant(title);
      sb.appendHtmlConstant("</div>");
    }
    if (message != null) {
      sb.appendHtmlConstant("<div class='" + style.infoMessage() + "'>");
      sb.appendHtmlConstant(message);
      sb.appendHtmlConstant("</div>");
    }
  }


}
