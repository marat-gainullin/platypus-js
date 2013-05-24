/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.button;

import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

public  class Tools {

  private ToolResources resources;
  private ToolStyle style;
  
  Tools() {
   
  }

  public Tools(ToolResources resources) {
    this.resources = resources;
    this.style = this.resources.style();
    this.style.ensureInjected();
  }
  
  public ToolResources resources() {
    return resources;
  }
  
  public ToolStyle icons() {
    return style;
  }

  public interface ToolStyle extends CssResource {

    String close();

    String closeOver();

    String collapse();

    String collapseOver();

    String doubleDown();

    String doubleDownOver();

    String doubleLeft();

    String doubleLeftOver();

    String doubleRight();

    String doubleRightOver();

    String doubleUp();

    String doubleUpOver();

    String down();

    String downOver();

    String expand();

    String expandOver();

    String gear();

    String gearOver();

    String left();

    String leftOver();

    String maximize();

    String maximizeOver();

    String minimize();

    String minimizeOver();

    String minus();

    String minusOver();

    String pin();

    String pinOver();

    String unpin();

    String unpinOver();

    String plus();

    String plusOver();

    String print();

    String printOver();

    String question();

    String questionOver();

    String refresh();

    String refreshOver();

    String restore();

    String restoreOver();

    String right();

    String rightOver();

    String save();

    String saveOver();

    String search();

    String searchOver();

    String up();

    String upOver();

  }

  public interface ToolResources {

    ToolStyle style();

    ImageResource closeIcon();

    ImageResource closeOverIcon();

    ImageResource collapseIcon();

    ImageResource collapseOverIcon();

    ImageResource doubleDownIcon();

    ImageResource doubleDownOverIcon();

    ImageResource doubleLeftIcon();

    ImageResource doubleLeftOverIcon();

    ImageResource doubleRightIcon();

    ImageResource doubleRightOverIcon();

    ImageResource doubleUpIcon();

    ImageResource doubleUpOverIcon();

    ImageResource downIcon();

    ImageResource downOverIcon();

    ImageResource expandIcon();

    ImageResource expandOverIcon();

    ImageResource gearIcon();

    ImageResource gearOverIcon();

    ImageResource leftIcon();

    ImageResource leftOverIcon();

    ImageResource maximizeIcon();

    ImageResource maximizeOverIcon();

    ImageResource minimizeIcon();

    ImageResource minimizeOverIcon();

    ImageResource minusIcon();

    ImageResource minusOverIcon();

    ImageResource pinIcon();

    ImageResource pinOverIcon();

    ImageResource unpinIcon();

    ImageResource unpinOverIcon();

    ImageResource plusIcon();

    ImageResource plusOverIcon();

    ImageResource printIcon();

    ImageResource printOverIcon();

    ImageResource questionIcon();

    ImageResource questionOverIcon();

    ImageResource refreshIcon();

    ImageResource refreshOverIcon();

    ImageResource restoreIcon();

    ImageResource restoreOverIcon();

    ImageResource rightIcon();

    ImageResource rightOverIcon();

    ImageResource saveIcon();

    ImageResource saveOverIcon();

    ImageResource searchIcon();

    ImageResource searchOverIcon();

    ImageResource upIcon();

    ImageResource upOverIcon();

  }
}
