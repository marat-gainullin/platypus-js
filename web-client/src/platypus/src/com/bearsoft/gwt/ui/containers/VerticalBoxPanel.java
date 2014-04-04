/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gwt.ui.containers;

import com.bearsoft.gwt.ui.CommonResources;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 * @author mg
 */
public class VerticalBoxPanel extends ComplexPanel implements RequiresResize, ProvidesResize {

    protected int vgap = 5;

    public VerticalBoxPanel() {
        super();
        setElement(Document.get().createDivElement());
        getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
    }

    public int getVgap() {
        return vgap;
    }

    public void setVgap(int aValue) {
        vgap = aValue;
        for (int i = 1; i < getWidgetCount(); i++) {
            Widget w = getWidget(i);
            w.getElement().getStyle().setMarginTop(aValue, Style.Unit.PX);
        }
    }

    @Override
    public void add(Widget child) {
        child.getElement().getStyle().setPosition(Style.Position.RELATIVE);
        child.getElement().getStyle().setDisplay(Style.Display.BLOCK);
        child.getElement().getStyle().setLeft(0, Style.Unit.PX);
        if(child instanceof FocusWidget){
            child.getElement().getStyle().clearRight();
            child.getElement().getStyle().setWidth(100, Style.Unit.PCT);
            CommonResources.INSTANCE.commons().ensureInjected();
            child.getElement().addClassName(CommonResources.INSTANCE.commons().borderSized());
        } else {
            child.getElement().getStyle().setRight(0, Style.Unit.PX);
            child.getElement().getStyle().clearWidth();
        }
        if (getWidgetCount() > 0) {
            child.getElement().getStyle().setMarginTop(vgap, Style.Unit.PX);
        }
        super.add(child, getElement().<Element>cast());
        ajustHeight();
    }

    @Override
    public boolean remove(Widget w) {
        boolean res = super.remove(w);
        if (res) {
            ajustHeight();
        }
        return res;
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        ajustHeight();
    }

    @Override
    public void onResize() {
        if (!ajustHeight()) {
            for (Widget child : getChildren()) {
                if (child instanceof RequiresResize) {
                    ((RequiresResize) child).onResize();
                }
            }
        }
    }

    protected boolean ajustHeight() {
        if (isAttached() && getParent() instanceof ScrollPanel) {
            int height = 0;
            for (int i = 0; i < getWidgetCount(); i++) {
                Widget w = getWidget(i);
                height += w.getOffsetHeight();
            }
            height += (getWidgetCount() - 1) * vgap;
            setHeight(height + "px");
            /*
             int parentClientWidth = getParent().getElement().getClientWidth();
             setWidth(parentClientWidth + "px");
             int clientWidth = getElement().getClientWidth();
             int scrollWidth = getElement().getScrollWidth();
             int widthDelta = scrollWidth - clientWidth;
             setWidth((parentClientWidth - widthDelta) + "px");
             */
            for (Widget child : getChildren()) {
                if (child instanceof RequiresResize) {
                    ((RequiresResize) child).onResize();
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
