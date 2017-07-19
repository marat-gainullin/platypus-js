public class Box extends Container implements HasChildrenPosition {

    protected int orientation; // horizontal
    protected int hgap;
    protected int vgap;

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
        applyOrientation(aValue);
    }

    protected void applyOrientation(int aValue) {
        if (orientation != aValue) {
            orientation = aValue;
            for (int i = 0; i < children.size(); i++) {
                format(children.get(i));
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

    protected void format(Widget w) {
        boolean visible = !w.getElement().hasAttribute("aria-hidden");
        Style ws = w.getElement().getStyle();
        ws.setMarginLeft(0, Style.Unit.PX);
        ws.setMarginRight(0, Style.Unit.PX);
        ws.setMarginTop(0, Style.Unit.PX);
        if (orientation == Orientation.HORIZONTAL) {
            if (element.getFirstChildElement() != w.getElement()) {
                ws.setMarginLeft(hgap, Style.Unit.PX);
                ws.setMarginRight(0, Style.Unit.PX);
            }
            ws.clearTop();
            ws.clearBottom();
            ws.setPosition(Style.Position.RELATIVE);
            ws.setHeight(100, Style.Unit.PCT);
            ws.setDisplay(visible ? Style.Display.INLINE_BLOCK : Style.Display.NONE);
            ws.clearFloat();
        } else {
            if (element.getFirstChildElement() != w.getElement()) {
                ws.setMarginTop(vgap, Style.Unit.PX);
                ws.setMarginBottom(0, Style.Unit.PX);
            }
            ws.setPosition(Style.Position.RELATIVE);
            ws.setDisplay(visible ? Style.Display.BLOCK : Style.Display.NONE);
            ws.setLeft(0, Style.Unit.PX);
            ws.clearRight();
            ws.setWidth(100, Style.Unit.PCT);
        }
        ws.setVerticalAlign(Style.VerticalAlign.MIDDLE);
        w.getElement().addClassName(CommonResources.INSTANCE.commons().borderSized());
    }

    @Override
    public void add(Widget w) {
        if (orientation == Orientation.HORIZONTAL) {
            super.add(w);
            format(w);
        } else {
            super.add(w);
            format(w);
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
    public int getTop(Widget w) {
        assert w.getParent() == this : "widget should be a child of this container";
        return orientation == Orientation.HORIZONTAL ? 0 : w.getElement().getOffsetTop();
    }

    @Override
    public int getLeft(Widget w) {
        assert w.getParent() == this : "widget should be a child of this container";
        return orientation == Orientation.HORIZONTAL ? w.getElement().getOffsetLeft() : 0;
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
