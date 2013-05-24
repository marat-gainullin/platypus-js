/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.info;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.sencha.gxt.core.client.XTemplates;

public class TemplateInfoConfig<T> extends InfoConfig {

  interface DataRenderer<T> extends XTemplates {
    SafeHtml render(T data);
  }

  private DataRenderer<T> renderer;
  private T data;

  public TemplateInfoConfig(DataRenderer<T> renderer) {
    this.renderer = renderer;
  }

  public DataRenderer<T> getRenderer() {
    return renderer;
  }

  public void setRenderer(DataRenderer<T> renderer) {
    this.renderer = renderer;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  @Override
  protected SafeHtml render(Info info) {
    return renderer.render(data);
  }

}
