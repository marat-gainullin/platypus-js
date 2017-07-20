package com.eas.widgets.containers;

import com.eas.core.HasPublished;
import com.eas.ui.HorizontalPosition;
import com.eas.ui.VerticalPosition;
import com.eas.ui.Widget;
import com.eas.ui.HasChildrenPosition;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style;

/**
 *
 * @author mg
 */
public class Borders extends Container implements HasChildrenPosition {

    private Widget center;
    private Widget left;
    private Widget right;
    private Widget top;
    private Widget bottom;
    private double bottomHeight;
    private double topHeight;
    private double leftWidth;
    private double rightWidth;

    private int hgap;
    private int vgap;

    public Borders() {
        super();
        element.getStyle().setOverflow(Style.Overflow.HIDDEN);
        element.getStyle().position = 'relative';
        com.eas.ui.CommonResources.INSTANCE.commons().ensureInjected();
    }

    public Borders(int aVGap, int aHGap) {
        this();
        setHgap(aHGap);
        setVgap(aVGap);
    }

    public final int getHgap() {
        return hgap;
    }

    public final void setHgap(int aValue) {
        hgap = aValue;
        recalcCenterMargins();
    }

    public final int getVgap() {
        return vgap;
    }

    public final void setVgap(int aValue) {
        vgap = aValue;
        recalcCenterMargins();
    }

    public void add(Widget w, int aPlace, int aSize) {
        switch (aPlace) {
            case HorizontalPosition.LEFT:
                setLeftComponent(w, aSize);
                break;
            case HorizontalPosition.RIGHT:
                setRightComponent(w, aSize);
                break;
            case VerticalPosition.TOP:
                setTopComponent(w, aSize);
                break;
            case VerticalPosition.BOTTOM:
                setBottomComponent(w, aSize);
                break;
            default:
                setCenterComponent(w);
                break;
        }
    }

    public Widget getLeftComponent() {
        return left;
    }

    public void setLeftComponent(Widget w, double size) {
        Widget old = left;
        left = w;
        leftWidth = size;
        if (old != w) {
            if (old != null) {
                super.remove(old);
            }
            if (w != null) {
                formatLeft();
                super.add(w);
            }
        }
    }

    public Widget getRightComponent() {
        return right;
    }

    public void setRightComponent(Widget w, double size) {
        Widget old = getRightComponent();
        right = w;
        rightWidth = size;
        if (old != w) {
            if (old != null) {
                super.remove(old);
            }
            if (w != null) {
                formatRight();
                super.add(w);
            }
        }
    }

    public Widget getTopComponent() {
        return top;
    }

    public void setTopComponent(Widget w, double size) {
        Widget old = getTopComponent();
        top = w;
        topHeight = size;
        if (old != w) {
            if (old != null) {
                super.remove(old);
            }
            if (w != null) {
                formatTop();
                super.add(w);
            }
        }
    }

    public Widget getBottomComponent() {
        return bottom;
    }

    public void setBottomComponent(Widget w, double size) {
        Widget old = getBottomComponent();
        bottom = w;
        bottomHeight = size;
        if (old != w) {
            if (old != null) {
                super.remove(old);
            }
            if (w != null) {
                formatBottom();
                super.add(w);
            }
        }
    }

    public Widget getCenterComponent() {
        return center;
    }

    public void setCenterComponent(Widget w) {
        Widget old = center;
        if (old != w) {
            center = w;
            if (old != null) {
                super.remove(old);
            }
            if (w != null) {
                formatCenter();
                recalcCenterMargins();
                super.add(w);
            }
        }
    }

    private void formatLeft() {
        if (left != null) {
            left.element.classList.add(com.eas.ui.CommonResources.INSTANCE.commons().borderSized());
            Style ls = left.element.style;
            ls.position = 'absolute';
            ls.setOverflow(Style.Overflow.HIDDEN);
            ls.setLeft(0+ 'px');
            ls.width =leftWidth+ 'px');
            ls.top =topHeight+ 'px');
            ls.bottom =bottomHeight+ 'px');
        }
    }

    private void formatRight() {
        if (right != null) {
            right.element.classList.add(com.eas.ui.CommonResources.INSTANCE.commons().borderSized());
            Style rs = right.element.style;
            rs.position = 'absolute';
            rs.setOverflow(Style.Overflow.HIDDEN);
            rs.right =0+ 'px');
            rs.width =rightWidth+ 'px');
            rs.top =topHeight+ 'px');
            rs.bottom =bottomHeight+ 'px');
        }
    }

    private void formatTop() {
        if (top != null) {
            top.element.classList.add(com.eas.ui.CommonResources.INSTANCE.commons().borderSized());
            Style ts = top.element.style;
            ts.position = 'absolute';
            ts.setOverflow(Style.Overflow.HIDDEN);
            ts.top =0+ 'px');
            ts.height =topHeight+ 'px');
            ts.setLeft(leftWidth+ 'px');
            ts.right =rightWidth+ 'px');
        }
    }

    private void formatBottom() {
        if (bottom != null) {
            bottom.element.classList.add(com.eas.ui.CommonResources.INSTANCE.commons().borderSized());
            Style bs = bottom.element.style;
            bs.position = 'absolute';
            bs.setOverflow(Style.Overflow.HIDDEN);
            bs.bottom =0+ 'px');
            bs.height =bottomHeight+ 'px');
            bs.setLeft(leftWidth+ 'px');
            bs.right =rightWidth+ 'px');
        }
    }

    private void formatCenter() {
        if (center != null) {
            center.element.classList.add(com.eas.ui.CommonResources.INSTANCE.commons().borderSized());
            Style cs = center.element.style;
            cs.position = 'absolute';
            cs.setOverflow(Style.Overflow.HIDDEN);
            cs.bottom =bottomHeight+ 'px');
            cs.top =topHeight+ 'px');
            cs.setLeft(leftWidth+ 'px');
            cs.right =rightWidth+ 'px');
        }
    }

    @Override
    public void add(Widget w) {
        setCenterComponent(w);
    }

    @Override
    public void add(Widget w, int beforeIndex) {
        setCenterComponent(w);
    }

    private void checkParts(Widget w) {
        if (left == w) {
            left = null;
            leftWidth = 0;
        }
        if (right == w) {
            right = null;
            rightWidth = 0;
        }
        if (top == w) {
            top = null;
            topHeight = 0;
        }
        if (bottom == w) {
            bottom = null;
            bottomHeight = 0;
        }
        if (center == w) {
            center = null;
        }
    }

    @Override
    public boolean remove(Widget w) {
        checkParts(w);
        return super.remove(w);
    }

    @Override
    public Widget remove(int index) {
        Widget w = super.remove(index);
        checkParts(w);
        return w;
    }

    protected void recalcCenterMargins() {
        if (center != null) {
            Style cs = center.element.style;
            cs.setMarginLeft(hgap+ 'px');
            cs.setMarginRight(hgap+ 'px');
            cs.setMarginTop(vgap+ 'px');
            cs.setMarginBottom(vgap+ 'px');
        }
    }

    public void ajustWidth(Widget w, int width) {
        if (left == w) {
            leftWidth = width;
            formatLeft();
            formatTop();
            formatBottom();
            formatCenter();
        } else if (right == w) {
            rightWidth = width;
            formatRight();
            formatTop();
            formatBottom();
            formatCenter();
        }
    }

    public void ajustHeight(Widget w, int height) {
        if (top == w) {
            topHeight = height;
            formatTop();
            formatLeft();
            formatRight();
            formatCenter();
        } else if (bottom == w) {
            bottomHeight = height;
            formatBottom();
            formatLeft();
            formatRight();
            formatCenter();
        }
    }

    @Override
    public int getTop(Widget w) {
        assert w.parent == this : "widget should be a child of this container";
        return w.element.getOffsetTop();
    }

    @Override
    public int getLeft(Widget w) {
        assert w.parent == this : "widget should be a child of this container";
        return w.element.getOffsetLeft();
    }

    @Override
    protected void publish(JavaScriptObject aValue) {
        publish(this, aValue);
    }

    private native static void publish(HasPublished aWidget, JavaScriptObject published)/*-{
        var VerticalPosition = @com.eas.ui.JsUi::VerticalPosition;		
        var HorizontalPosition = @com.eas.ui.JsUi::HorizontalPosition;		
        Object.defineProperty(published, "hgap", {
            get : function(){
                    return aWidget.@com.eas.widgets.BorderPane::getHgap()();
            },
            set : function(aValue){
                    aWidget.@com.eas.widgets.BorderPane::setHgap(I)(aValue);
            }
        });
        Object.defineProperty(published, "vgap", {
            get : function(){
                    return aWidget.@com.eas.widgets.BorderPane::getVgap()();
            },
            set : function(aValue){
                    aWidget.@com.eas.widgets.BorderPane::setVgap(I)(aValue);
            }
        });
        Object.defineProperty(published, "leftComponent", {
            get : function() {
                    var comp = aWidget.@com.eas.widgets.BorderPane::getLeftComponent()();
                    return @com.eas.core.Utils::checkPublishedComponent(Ljava/lang/Object;)(comp);
            },
            set : function(aChild) {
                    aWidget.@com.eas.widgets.BorderPane::setLeftComponent(Lcom/google/gwt/user/client/ui/Widget;D)(aChild.unwrap(), toAdd.width);
            }
        });
        Object.defineProperty(published, "rightComponent", {
            get : function() {
                    var comp = aWidget.@com.eas.widgets.BorderPane::getRightComponent()();
                    return @com.eas.core.Utils::checkPublishedComponent(Ljava/lang/Object;)(comp);
            },
            set : function(aChild) {
                    aWidget.@com.eas.widgets.BorderPane::setRightComponent(Lcom/google/gwt/user/client/ui/Widget;D)(aChild.unwrap(), toAdd.width);
            }
        });
        Object.defineProperty(published, "topComponent", {
            get : function() {
                    var comp = aWidget.@com.eas.widgets.BorderPane::getTopComponent()();
                    return @com.eas.core.Utils::checkPublishedComponent(Ljava/lang/Object;)(comp);
            },
            set : function(aChild) {
                    aWidget.@com.eas.widgets.BorderPane::setTopComponent(Lcom/google/gwt/user/client/ui/Widget;D)(aChild.unwrap(), toAdd.height);
            }
        });
        Object.defineProperty(published, "bottomComponent", {
            get : function() {
                    var comp = aWidget.@com.eas.widgets.BorderPane::getBottomComponent()();
                    return @com.eas.core.Utils::checkPublishedComponent(Ljava/lang/Object;)(comp);
            },
            set : function(aChild) {
                    aWidget.@com.eas.widgets.BorderPane::setBottomComponent(Lcom/google/gwt/user/client/ui/Widget;D)(aChild.unwrap(), toAdd.height);
            }
        });
        Object.defineProperty(published, "centerComponent", {
            get : function() {
                    var comp = aWidget.@com.eas.widgets.BorderPane::getCenterComponent()();
                    return @com.eas.core.Utils::checkPublishedComponent(Ljava/lang/Object;)(comp);
            },
            set : function(aChild) {
                    aWidget.@com.eas.widgets.BorderPane::setCenterComponent(Lcom/google/gwt/user/client/ui/Widget;)(aChild.unwrap());
            }
        });
        published.add = function(toAdd, region, aSize) {
            if(toAdd != undefined && toAdd != null && toAdd.unwrap != undefined){
                if(toAdd.parent == published)
                        throw 'A widget already added to this container';
                if(!region){
                        region = VerticalPosition.CENTER;
                }
                switch (region) {
                    case VerticalPosition.CENTER:
                        aWidget.@com.eas.widgets.BorderPane::setCenterComponent(Lcom/google/gwt/user/client/ui/Widget;)(toAdd.unwrap());
                    break;  
                    case VerticalPosition.TOP: 
                        if (!aSize) {
                            aSize = toAdd.height;
                            if (!aSize) {
                                aSize = 32;
                            }
                        }
                        aWidget.@com.eas.widgets.BorderPane::setTopComponent(Lcom/google/gwt/user/client/ui/Widget;D)(toAdd.unwrap(), aSize);
                    break;  
                    case VerticalPosition.BOTTOM: 
                        if (!aSize) {
                            aSize = toAdd.height;
                            if (!aSize) {
                                aSize = 32;
                            }
                        }
                        aWidget.@com.eas.widgets.BorderPane::setBottomComponent(Lcom/google/gwt/user/client/ui/Widget;D)(toAdd.unwrap(), aSize);
                    break;  
                    case HorizontalPosition.LEFT: 
                        if (!aSize) {
                            aSize = toAdd.width;
                            if (!aSize) {
                                aSize = 32
                            }
                        }
                        aWidget.@com.eas.widgets.BorderPane::setLeftComponent(Lcom/google/gwt/user/client/ui/Widget;D)(toAdd.unwrap(), aSize);
                    break;  
                    case HorizontalPosition.RIGHT: 
                        if (!aSize) {
                            aSize = toAdd.width;
                            if (!aSize) {
                                aSize = 32
                            }
                        }
                        aWidget.@com.eas.widgets.BorderPane::setRightComponent(Lcom/google/gwt/user/client/ui/Widget;D)(toAdd.unwrap(), aSize);
                    break;  
                }
            }
        }
    }-*/;
}
