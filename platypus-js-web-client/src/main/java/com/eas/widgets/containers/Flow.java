package com.eas.widgets.containers;

import com.eas.core.HasPublished;
import com.eas.ui.Widget;
import com.eas.ui.HasChildrenPosition;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style;

/**
 *
 * @author mg
 */
public class Flow extends Container implements HasChildrenPosition {

    protected int hgap;
    protected int vgap;

    public Flow() {
        super();
        element.getStyle().setWhiteSpace(Style.WhiteSpace.NORMAL);
        element.getStyle().setLineHeight(0, Style.Unit.PX);
	element.getStyle().setOverflow(Style.Overflow.AUTO);
    }

    public Flow(int aVGap, int aHGap) {
        this();
        setHgap(aHGap);
        setVgap(aVGap);
    }

    public int getHgap() {
        return hgap;
    }

    public final void setHgap(int aValue) {
        hgap = aValue;
        for (int i = 0; i < children.size(); i++) {
            Widget w = children.get(i);
            w.getElement().getStyle().setMarginLeft(hgap, Style.Unit.PX);
        }
    }

    public int getVgap() {
        return vgap;
    }

    public final void setVgap(int aValue) {
        vgap = aValue;
        for (int i = 0; i < children.size(); i++) {
            Widget w = children.get(i);
            w.getElement().getStyle().setMarginTop(vgap, Style.Unit.PX);
        }
    }

    @Override
    public void add(Widget w) {
        format(w);
        super.add(w);
    }

    @Override
    public void add(Widget w, int beforeIndex) {
        format(w);
        super.add(w, beforeIndex);
    }

    private void format(Widget w) {
        Style ws = w.getElement().getStyle();
        ws.setMarginLeft(hgap, Style.Unit.PX);
        ws.setMarginTop(vgap, Style.Unit.PX);
        ws.setDisplay(Style.Display.INLINE_BLOCK);
        ws.setVerticalAlign(Style.VerticalAlign.BOTTOM);
    }

    @Override
    public int getTop(Widget aWidget) {
        assert aWidget.getParent() == this : "widget should be a child of this container";
        return aWidget.getElement().getOffsetTop();
    }

    @Override
    public int getLeft(Widget aWidget) {
        assert aWidget.getParent() == this : "widget should be a child of this container";
        return aWidget.getElement().getOffsetLeft();
    }

    @Override
    protected void publish(JavaScriptObject aValue) {
        publish(this, aValue);
    }

    private native static void publish(HasPublished aWidget, JavaScriptObject published)/*-{
        Object.defineProperty(published, "hgap", {
            get : function(){
                return aWidget.@com.eas.widgets.FlowPane::getHgap()();
            },
            set : function(aValue){
                aWidget.@com.eas.widgets.FlowPane::setHgap(I)(aValue);
            }
        });
        Object.defineProperty(published, "vgap", {
            get : function(){
                return aWidget.@com.eas.widgets.FlowPane::getVgap()();
            },
            set : function(aValue){
                aWidget.@com.eas.widgets.FlowPane::setVgap(I)(aValue);
            }
        });
        published.add = function(toAdd){
            if(toAdd && toAdd.unwrap){
                if(toAdd.parent == published)
                    throw 'A widget already added to this container';
                aWidget.@com.eas.widgets.FlowPane::add(Lcom/google/gwt/user/client/ui/Widget;)(toAdd.unwrap());
            }
        }
    }-*/;
}
