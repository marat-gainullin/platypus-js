package com.eas.widgets.containers;

import com.eas.core.HasPublished;
import com.eas.ui.CommonResources;
import com.eas.ui.Orientation;
import com.eas.ui.Widget;
import com.eas.ui.HasChildrenPosition;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style;
import com.google.gwt.i18n.client.HasDirection;

/**
 *
 * @author mg
 */
public class Box extends Container implements HasChildrenPosition {

    protected int orientation; // horizontal
    protected int hgap;
    protected int vgap;
    protected HasDirection.Direction direction = HasDirection.Direction.LTR;

    public Box() {
        super();
        CommonResources.INSTANCE.commons().ensureInjected();
        element.getStyle().setWhiteSpace(Style.WhiteSpace.NOWRAP);
        element.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        element.getStyle().setOverflow(Style.Overflow.HIDDEN);
        element.getStyle().setPosition(Style.Position.RELATIVE);
    }

    public Box(int aOrientation, int aHGap, int aVGap) {
        this();
        setHgap(aHGap);
        setVgap(aVGap);
        setOrientation(aOrientation);
    }

    public int getOrientation() {
        return orientation;
    }

    public final void setOrientation(int aValue) {
        if (orientation != aValue) {
            orientation = aValue;
            for (int i = 0; i < children.size(); i++) {
                Widget w = children.get(i);
                format(w);
                Style es = w.getElement().getStyle();
                es.setMarginLeft(0, Style.Unit.PX);
                es.setMarginRight(0, Style.Unit.PX);
                es.setMarginTop(0, Style.Unit.PX);
                if (i > 0) {
                    if (orientation == Orientation.HORIZONTAL) {
                        if (direction == HasDirection.Direction.LTR) {
                            es.setMarginLeft(hgap, Style.Unit.PX);
                            es.setMarginRight(0, Style.Unit.PX);
                        } else {
                            es.setMarginLeft(0, Style.Unit.PX);
                            es.setMarginRight(hgap, Style.Unit.PX);
                        }
                    } else {
                        es.setMarginTop(vgap, Style.Unit.PX);
                    }
                }
            }
        }
    }

    public int getHgap() {
        return hgap;
    }

    public final void setHgap(int aValue) {
        if (aValue >= 0) {
            hgap = aValue;
            if (orientation == Orientation.HORIZONTAL) {
                for (int i = 1; i < children.size(); i++) {
                    Widget w = children.get(i);
                    w.getElement().getStyle().setMarginLeft(aValue, Style.Unit.PX);
                }
            }
        }
    }

    public int getVgap() {
        return vgap;
    }

    public final void setVgap(int aValue) {
        if (aValue >= 0) {
            vgap = aValue;
            if (orientation == Orientation.VERTICAL) {
                for (int i = 1; i < children.size(); i++) {
                    Widget w = children.get(i);
                    w.getElement().getStyle().setMarginTop(aValue, Style.Unit.PX);
                }
            }
        }
    }

    protected void format(Widget child) {
        boolean visible = !child.getElement().hasAttribute("aria-hidden");
        Style ws = child.getElement().getStyle();
        if (orientation == Orientation.HORIZONTAL) {
            ws.clearTop();
            ws.clearBottom();
            ws.setPosition(Style.Position.RELATIVE);
            ws.setHeight(100, Style.Unit.PCT);
            ws.setDisplay(visible ? Style.Display.INLINE_BLOCK : Style.Display.NONE);
            if (direction == HasDirection.Direction.LTR) {
                ws.clearFloat();
            } else {
                ws.setFloat(Style.Float.RIGHT);
            }
        } else {
            ws.setPosition(Style.Position.RELATIVE);
            ws.setDisplay(visible ? Style.Display.BLOCK : Style.Display.NONE);
            ws.setLeft(0, Style.Unit.PX);
            ws.clearRight();
            ws.setWidth(100, Style.Unit.PCT);
        }
        ws.setVerticalAlign(Style.VerticalAlign.MIDDLE);
        child.getElement().addClassName(CommonResources.INSTANCE.commons().borderSized());
    }

    public void ajustDisplay(Widget child) {
        if (child.getParent() == this) {
            boolean visible = !child.getElement().hasAttribute("aria-hidden");
            if (orientation == Orientation.HORIZONTAL) {
                child.getElement().getStyle().setDisplay(visible ? Style.Display.INLINE_BLOCK : Style.Display.NONE);
            } else {
                child.getElement().getStyle().setDisplay(visible ? Style.Display.BLOCK : Style.Display.NONE);
            }
        }
    }

    @Override
    public void add(Widget child) {
        if (orientation == Orientation.HORIZONTAL) {
            if (children.size() > 0) {
                if (direction == HasDirection.Direction.LTR) {
                    child.getElement().getStyle().setMarginLeft(hgap, Style.Unit.PX);
                    child.getElement().getStyle().setMarginRight(0, Style.Unit.PX);
                } else {
                    child.getElement().getStyle().setMarginLeft(0, Style.Unit.PX);
                    child.getElement().getStyle().setMarginRight(hgap, Style.Unit.PX);
                }
            }
            format(child);
            super.add(child);
        } else {
            if (children.size() > 0) {
                child.getElement().getStyle().setMarginTop(vgap, Style.Unit.PX);
            }
            format(child);
            super.add(child);
        }
    }

    /*
    @Override
    protected void onAttach() {
        super.onAttach();
        if (orientation == Orientation.HORIZONTAL) {
            if (getParent() instanceof ScrollPanel) {
                getElement().getStyle().setHeight(100, Style.Unit.PCT);
            }
        } else {
            if (getParent() instanceof ScrollPanel) {
                getElement().getStyle().setWidth(100, Style.Unit.PCT);
            }
        }
    }
     */
    @Override
    public int getTop(Widget aWidget) {
        assert aWidget.getParent() == this : "widget should be a child of this container";
        return 0;
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

    private native static void publish(HasPublished aWidget, JavaScriptObject aPublished)/*-{
        Object.defineProperty(aPublished, "hgap", {
            get : function(){
                return aWidget.@com.eas.widgets.CardPane::getHgap()();
            },
            set : function(aValue){
                aWidget.@com.eas.widgets.CardPane::setHgap(I)(aValue);
            }
        });
        Object.defineProperty(aPublished, "vgap", {
            get : function(){
                return aWidget.@com.eas.widgets.CardPane::getVgap()();
            },
            set : function(aValue){
                aWidget.@com.eas.widgets.CardPane::setVgap(I)(aValue);
            }
        });
    }-*/;

}
