/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.base.client.resizable;

import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.resources.StyleInjectorHelper;
import com.sencha.gxt.widget.core.client.Resizable.Dir;
import com.sencha.gxt.widget.core.client.Resizable.ResizableAppearance;

public abstract class ResizableBaseAppearance implements ResizableAppearance {

  public interface ResizableResources {

    ResizableStyle style();

  }

  public interface ResizableStyle extends CssResource {

    String handle();

    String handleEast();

    String handleNorth();

    String handleNortheast();

    String handleNorthwest();

    String handleSouth();

    String handleSoutheast();

    String handleSouthwest();

    String handleWest();

    String over();

    String overlay();

    String pinned();

    String proxy();

  }

  private final ResizableResources resources;
  private final ResizableStyle style;

  public ResizableBaseAppearance(ResizableResources resources) {
    this.resources = resources;
    this.style = resources.style();
    StyleInjectorHelper.ensureInjected(style, true);
  }

  @Override
  public Element createProxy() {
    XElement elem = DOM.createDiv().<XElement> cast();
    elem.addClassName(resources.style().proxy());
    elem.disableTextSelection(true);
    return elem;
  }

  public String getHandleStyles(Dir dir) {
    StringBuilder styleBuilder = new StringBuilder();
    styleBuilder.append(style.handle());
    styleBuilder.append(' ');
    switch (dir) {
      case E:
        styleBuilder.append(style.handleEast());
        break;
      case N:
        styleBuilder.append(style.handleNorth());
        break;
      case W:
        styleBuilder.append(style.handleWest());
        break;
      case S:
        styleBuilder.append(style.handleSouth());
        break;
      case NE:
        styleBuilder.append(style.handleNortheast());
        break;
      case NW:
        styleBuilder.append(style.handleNorthwest());
        break;
      case SW:
        styleBuilder.append(style.handleSouthwest());
        break;
      case SE:
        styleBuilder.append(style.handleSoutheast());
        break;
    }
    return styleBuilder.toString();
  }

}
