/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.toolbar;

import com.google.gwt.user.client.DOM;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;

/**
 * Fills the toolbar width, pushing any newly added items to the right.
 */
public class FillToolItem extends Component {

  /**
   * Creates a new fill item.
   */
  public FillToolItem() {
    setElement(DOM.createDiv());
    
    BoxLayoutData data = new BoxLayoutData();
    data.setFlex(1.0);
    setLayoutData(data);
  }

}
