/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client;

import com.google.gwt.user.client.ui.Widget;

/**
 * Creates a widget from a widget. This allows widgets to be treated as
 * components.
 */
public class WidgetComponent extends Composite {

  /**
   * Creates a {@link Component} from the specified {@link Widget}.
   * 
   * @param widget the widget to wrap
   */
  public WidgetComponent(Widget widget) {
    super();
    initWidget(widget);
  }
  
  public Widget getWidget() {
    // make public
    return super.getWidget();
  };
  
}
