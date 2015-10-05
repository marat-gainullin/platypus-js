/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.menu;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 * @author mg
 */
public class Menu extends VerticalPanel{
    
    public Menu(){
        super();
        getElement().getStyle().setPropertyPx("cellPadding", 0);
        getElement().getStyle().setPropertyPx("cellSpacing", 0);
        getElement().getStyle().setPropertyPx("borderWidth", 0);
        getElement().getStyle().setPropertyPx("borderSpacing", 0);
    }
    
  @Override
  protected void add(Widget child, Element container) {
      super.add(child, container);
      container.addClassName("menu-row");
  }    
}
