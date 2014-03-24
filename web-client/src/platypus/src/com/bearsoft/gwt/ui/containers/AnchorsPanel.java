/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bearsoft.gwt.ui.containers;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 * @author mg
 */
public class AnchorsPanel extends LayoutPanel{

    public AnchorsPanel() {
        super();
    }

    @Override
    public void insert(Widget widget, int beforeIndex) {
        super.insert(widget, beforeIndex);
        if("button".equalsIgnoreCase(widget.getElement().getTagName())){
            widget.getElement().getStyle().setWidth(100, Style.Unit.PCT);
        }        
    }

    @Override
    public void onResize() {
        super.onResize();
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        forceLayout();
    }

}
