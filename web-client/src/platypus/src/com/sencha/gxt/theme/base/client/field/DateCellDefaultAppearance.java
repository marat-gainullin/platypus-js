/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.base.client.field;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.sencha.gxt.cell.core.client.form.DateCell.DateCellAppearance;

public class DateCellDefaultAppearance extends TriggerFieldDefaultAppearance implements DateCellAppearance {

  public interface DateCellResources extends TriggerFieldResources {

    @Source({"ValueBaseField.css", "TextField.css", "TriggerField.css"})
    DateCellStyle css();

    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    ImageResource textBackground();

    @Source("dateArrow.png")
    ImageResource triggerArrow();

    @Source("dateArrowOver.png")
    ImageResource triggerArrowOver();

    @Source("dateArrowClick.png")
    ImageResource triggerArrowClick();

    @Source("dateArrowFocus.png")
    ImageResource triggerArrowFocus();

    ImageResource triggerArrowFocusOver();

    ImageResource triggerArrowFocusClick();

  }
  
  public interface DateCellStyle extends TriggerFieldStyle {
    
  }

  public DateCellDefaultAppearance() {
    this(GWT.<DateCellResources> create(DateCellResources.class));
  }

  public DateCellDefaultAppearance(DateCellResources resources) {
    super(resources);
  }

}