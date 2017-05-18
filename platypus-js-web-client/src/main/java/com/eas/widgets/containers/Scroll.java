package com.eas.widgets.containers;

import com.eas.core.HasPublished;
import com.eas.ui.CommonResources;
import com.google.gwt.dom.client.Style;
import com.eas.ui.HasScroll;
import com.eas.ui.HorizontalScrollFiller;
import com.eas.ui.Orientation;
import com.eas.ui.VerticalScrollFiller;
import com.eas.ui.Widget;
import com.eas.ui.HasChildrenPosition;
import com.google.gwt.core.client.JavaScriptObject;

/**
 *
 * @author mg
 */
public class Scroll extends Container implements HasScroll,
        HorizontalScrollFiller, VerticalScrollFiller,
        HasChildrenPosition {

    /**
     * Used to set the horizontal scroll bar policy so that horizontal
     * scrollbars are displayed only when needed.
     */
    public static final int SCROLLBAR_AS_NEEDED = 30;
    /**
     * Used to set the horizontal scroll bar policy so that horizontal
     * scrollbars are never displayed.
     */
    public static final int SCROLLBAR_NEVER = 31;
    /**
     * Used to set the horizontal scroll bar policy so that horizontal
     * scrollbars are always displayed.
     */
    public static final int SCROLLBAR_ALWAYS = 32;

    public enum ScrollPolicy {

        ALLWAYS,
        NEVER,
        AUTO;
    }
    protected ScrollPolicy horizontalScrollPolicy = ScrollPolicy.AUTO;
    protected ScrollPolicy verticalScrollPolicy = ScrollPolicy.AUTO;

    protected int verticalScrollBarPolicy = SCROLLBAR_AS_NEEDED;
    protected int horizontalScrollBarPolicy = SCROLLBAR_AS_NEEDED;

    protected Widget widget;

    public Scroll() {
        super();
        CommonResources.INSTANCE.commons().ensureInjected();
    }

    public Scroll(Widget w) {
        this();
        setWidget(w);
    }

    public ScrollPolicy getHorizontalScrollPolicy() {
        return horizontalScrollPolicy;
    }

    public void setHorizontalScrollPolicy(ScrollPolicy aValue) {
        horizontalScrollPolicy = aValue;
        applyHorizontalScrollPolicy();
    }

    public ScrollPolicy getVerticalScrollPolicy() {
        return verticalScrollPolicy;
    }

    public void setVerticalScrollPolicy(ScrollPolicy aValue) {
        verticalScrollPolicy = aValue;
        applyVerticalScrollPolicy();
    }

    private boolean isHorizontalScrollFiller(Widget aWidget) {
        return aWidget instanceof HorizontalScrollFiller
                || (aWidget instanceof Box && ((Box) aWidget).getOrientation() == Orientation.VERTICAL);
    }

    private boolean isVerticalScrollFiller(Widget aWidget) {
        return aWidget instanceof VerticalScrollFiller
                || (aWidget instanceof Box && ((Box) aWidget).getOrientation() == Orientation.HORIZONTAL);
    }

    public void ajustWidth(Widget w, int aWidth) {
        if (!isHorizontalScrollFiller(w)) {
            w.getElement().getStyle().setWidth(aWidth, Style.Unit.PX);
        }
    }

    public void ajustHeight(Widget w, int aHeight) {
        if (!isVerticalScrollFiller(w)) {
            w.getElement().getStyle().setHeight(aHeight, Style.Unit.PX);
        }
    }

    public void setWidget(Widget w) {
        if (isHorizontalScrollFiller(w)) {
            w.getElement().getStyle().setWidth(100, Style.Unit.PCT);
        }
        if (isVerticalScrollFiller(w)) {
            w.getElement().getStyle().setHeight(100, Style.Unit.PCT);
        }
        format(w);
        Widget old = widget;
        if (old != null) {
            super.remove(old);
        }
        widget = w;
        super.add(w);
    }

    private void format(Widget w){
        w.getElement().addClassName(CommonResources.INSTANCE.commons().borderSized());
    }
    
    private void applyVerticalScrollPolicy() {
        Style.Overflow value = Style.Overflow.AUTO;
        if (verticalScrollPolicy == ScrollPolicy.ALLWAYS) {
            value = Style.Overflow.SCROLL;
        } else if (verticalScrollPolicy == ScrollPolicy.NEVER) {
            value = Style.Overflow.HIDDEN;
        }
        if (widget instanceof HasScroll) {
            value = Style.Overflow.HIDDEN;
        }
        element.getStyle().setOverflowY(value);
    }

    private void applyHorizontalScrollPolicy() {
        Style.Overflow value = Style.Overflow.AUTO;
        if (horizontalScrollPolicy == ScrollPolicy.ALLWAYS) {
            value = Style.Overflow.SCROLL;
        } else if (horizontalScrollPolicy == ScrollPolicy.NEVER) {
            value = Style.Overflow.HIDDEN;
        }
        if (widget instanceof HasScroll) {
            value = Style.Overflow.HIDDEN;
        }
        element.getStyle().setOverflowX(value);
    }

    public int getVerticalScrollBarPolicy() {
        return verticalScrollBarPolicy;
    }

    public int getHorizontalScrollBarPolicy() {
        return horizontalScrollBarPolicy;
    }

    public void setVerticalScrollBarPolicy(int aValue) {
        verticalScrollBarPolicy = aValue;
        applyPolicies();
    }

    public void setHorizontalScrollBarPolicy(int aValue) {
        horizontalScrollBarPolicy = aValue;
        applyPolicies();
    }

    protected void applyPolicies() {
        switch (horizontalScrollBarPolicy) {
            case SCROLLBAR_ALWAYS:
                setHorizontalScrollPolicy(ScrollPolicy.ALLWAYS);
                break;
            case SCROLLBAR_AS_NEEDED:
                setHorizontalScrollPolicy(ScrollPolicy.AUTO);
                break;
            case SCROLLBAR_NEVER:
                setHorizontalScrollPolicy(ScrollPolicy.NEVER);
                break;
            default:
                setHorizontalScrollPolicy(ScrollPolicy.AUTO);
        }
        switch (verticalScrollBarPolicy) {
            case SCROLLBAR_ALWAYS:
                setVerticalScrollPolicy(ScrollPolicy.ALLWAYS);
                break;
            case SCROLLBAR_AS_NEEDED:
                setVerticalScrollPolicy(ScrollPolicy.AUTO);
                break;
            case SCROLLBAR_NEVER:
                setVerticalScrollPolicy(ScrollPolicy.NEVER);
                break;
            default:
                setVerticalScrollPolicy(ScrollPolicy.AUTO);
        }
    }

    @Override
    public void add(Widget w) {
        clear();
        setWidget(w);
    }

    @Override
    public void add(Widget w, int beforeIndex) {
        clear();
        setWidget(w);
    }

    @Override
    public Widget remove(int index) {
        Widget removed = super.remove(index);
        if (removed == widget) {
            widget = null;
        }
        return removed;
    }

    @Override
    public boolean remove(Widget w) {
        if (w == widget) {
            widget = null;
        }
        return super.remove(w);
    }

    @Override
    protected void publish(JavaScriptObject aValue) {
        publish(this, aValue);
    }

    private native static void publish(HasPublished aWidget, JavaScriptObject published)/*-{
        published.add = function(toAdd){
            if(toAdd && toAdd.unwrap){
                if(toAdd.parent == published)
                    throw 'A widget already added to this container';
                aWidget.@com.eas.widgets.ScrollPane::setWidget(Lcom/google/gwt/user/client/ui/Widget;)(toAdd.unwrap());
            }
        }
        Object.defineProperty(published, "view", {
            get : function(){
                var widget = aWidget.@com.eas.widgets.ScrollPane::getWidget()();
                return @com.eas.core.Utils::checkPublishedComponent(Ljava/lang/Object;)(widget);
            },
            set : function(aValue){
                if(aValue != null)
                    published.add(aValue);
                else
                    published.clear();
            }
        });
        Object.defineProperty(published, "horizontalScrollBarPolicy", {
            get: function(){
                return aWidget.@com.eas.widgets.ScrollPane::getHorizontalScrollBarPolicy()();
            },
            set: function(aValue){
                aWidget.@com.eas.widgets.ScrollPane::setHorizontalScrollBarPolicy(I)(+aValue);
            }
        });
        Object.defineProperty(published, "verticalScrollBarPolicy", {
            get: function(){
                return aWidget.@com.eas.widgets.ScrollPane::getVerticalScrollBarPolicy()();
            },
            set: function(aValue){
                aWidget.@com.eas.widgets.ScrollPane::setVerticalScrollBarPolicy(I)(+aValue);
            }
        });
    }-*/;

    @Override
    public int getTop(Widget aWidget) {
        assert aWidget.getParent() == this : "widget should be a child of this container";
        return 0;
    }

    @Override
    public int getLeft(Widget aWidget) {
        assert aWidget.getParent() == this : "widget should be a child of this container";
        return 0;
    }
}
