package com.eas.window;

import com.eas.ui.Widget;
import com.eas.widgets.containers.Flow;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

/**
 *
 * @author mg
 */
public class Resizable extends Flow {

    protected interface Resources extends ClientBundle {

        static final Resources instance = GWT.create(Resources.class);

        public ResizableStyles resizable();
    }

    protected interface ResizableStyles extends CssResource {

        public String contentDecor();

        public String horizontalDecor();

        public String northDecor();

        public String southDecor();

        public String verticalDecor();

        public String westDecor();

        public String eastDecor();

        public String cornerDecor();

        public String northWestDecor();

        public String northEastDecor();

        public String southWestDecor();

        public String southEastDecor();
    }

    protected static ResizableStyles styles = Resources.instance.resizable();

    protected abstract class DraggableDecoration extends Draggable {

        private boolean mouseDragged;

        public DraggableDecoration(String... aClasses) {
            super(aClasses);
        }

        @Override
        protected void mousePressed() {
            focus();
        }

        @Override
        protected void mouseDragged() {
            if (!mouseDragged) {
                mouseDragged = true;
                beginResizing();
            }
        }

        @Override
        protected void mouseReleased() {
            mouseDragged = false;
            endResizing();
        }
    }

    protected Draggable n = new DraggableDecoration(styles.horizontalDecor(), styles.northDecor(), "horizontal-decor", "north-decor") {

        @Override
        protected boolean changeTarget(int dx, int dy, NativeEvent aEvent) {
            if (isNresizable()) {
                double newTop = Resizable.this.getElement().getScrollTop() + dy;
                Resizable.this.getElement().getStyle().setTop(newTop >= 0 ? newTop : 0, Style.Unit.PX);
                if (newTop >= 0) {
                    content.getElement().getStyle().setHeight(content.getElement().getOffsetHeight() - dy, Style.Unit.PX);
                }
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void activate() {
            element.addClassName("north-decor-active");
        }

        @Override
        protected void deactivate() {
            element.removeClassName("north-decor-active");
        }

    };
    protected Draggable s = new DraggableDecoration(styles.horizontalDecor(), styles.southDecor(), "horizontal-decor", "south-decor") {

        @Override
        protected boolean changeTarget(int dx, int dy, NativeEvent aEvent) {
            if (isSresizable()) {
                content.getElement().getStyle().setHeight(content.getElement().getOffsetHeight() + dy, Style.Unit.PX);
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void activate() {
            element.addClassName("south-decor-active");
        }

        @Override
        protected void deactivate() {
            element.removeClassName("south-decor-active");
        }

    };
    protected Draggable w = new DraggableDecoration(styles.verticalDecor(), styles.westDecor(), "vertical-decor", "west-decor") {

        @Override
        protected boolean changeTarget(int dx, int dy, NativeEvent aEvent) {
            if (isWresizable()) {
                double newLeft = Resizable.this.getElement().getScrollLeft() + dx;
                Resizable.this.getElement().getStyle().setLeft(newLeft >= 0 ? newLeft : 0, Style.Unit.PX);
                if (newLeft >= 0) {
                    content.getElement().getStyle().setWidth(content.getElement().getOffsetWidth() - dx, Style.Unit.PX);
                }
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void activate() {
            element.addClassName("west-decor-active");
        }

        @Override
        protected void deactivate() {
            element.removeClassName("west-decor-active");
        }

    };
    protected Draggable e = new DraggableDecoration(styles.verticalDecor(), styles.eastDecor(), "vertical-decor", "east-decor") {
        @Override
        protected boolean changeTarget(int dx, int dy, NativeEvent aEvent) {
            if (isEresizable()) {
                content.getElement().getStyle().setWidth(content.getElement().getOffsetWidth() + dx, Style.Unit.PX);
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void activate() {
            element.addClassName("east-decor-active");
        }

        @Override
        protected void deactivate() {
            element.removeClassName("east-decor-active");
        }

    };
    protected Draggable nw = new DraggableDecoration(styles.cornerDecor(), styles.northWestDecor(), "corner-decor", "north-west-decor") {

        @Override
        protected boolean changeTarget(int dx, int dy, NativeEvent aEvent) {
            n.changeTarget(dx, dy, aEvent);
            w.changeTarget(dx, dy, aEvent);
            return true;
        }

        @Override
        protected void activate() {
            element.addClassName("north-west-decor-active");
        }

        @Override
        protected void deactivate() {
            element.removeClassName("north-west-decor-active");
        }

    };
    protected Draggable ne = new DraggableDecoration(styles.cornerDecor(), styles.northEastDecor(), "corner-decor", "north-east-decor") {
        @Override
        protected boolean changeTarget(int dx, int dy, NativeEvent aEvent) {
            n.changeTarget(dx, dy, aEvent);
            e.changeTarget(dx, dy, aEvent);
            return true;
        }

        @Override
        protected void activate() {
            element.addClassName("north-east-decor-active");
        }

        @Override
        protected void deactivate() {
            element.removeClassName("north-east-decor-active");
        }

    };
    protected Draggable sw = new DraggableDecoration(styles.cornerDecor(), styles.southWestDecor(), "corner-decor", "south-west-decor") {
        @Override
        protected boolean changeTarget(int dx, int dy, NativeEvent aEvent) {
            s.changeTarget(dx, dy, aEvent);
            w.changeTarget(dx, dy, aEvent);
            return true;
        }

        @Override
        protected void activate() {
            element.addClassName("south-west-decor-active");
        }

        @Override
        protected void deactivate() {
            element.removeClassName("south-west-decor-active");
        }

    };
    protected Draggable se = new DraggableDecoration(styles.cornerDecor(), styles.southEastDecor(), "corner-decor", "south-east-decor") {
        @Override
        protected boolean changeTarget(int dx, int dy, NativeEvent aEvent) {
            s.changeTarget(dx, dy, aEvent);
            e.changeTarget(dx, dy, aEvent);
            return true;
        }

        @Override
        protected void activate() {
            element.addClassName("south-east-decor-active");
        }

        @Override
        protected void deactivate() {
            element.removeClassName("south-east-decor-active");
        }

    };

    protected Widget content;

    protected boolean undecorated;
    protected boolean resizable = true;

    public Resizable() {
        super();
        styles.ensureInjected();
    }

    public Resizable(boolean undecorated) {
        this();
        setUndecorated(undecorated);
    }

    protected void beginResizing() {
    }

    protected void endResizing() {
    }

    public Widget getContent() {
        return content;
    }

    public void setContent(Widget w) {
        content = w;
    }

    public boolean isUndecorated() {
        return undecorated;
    }

    public final void setUndecorated(boolean aValue) {
        boolean oldValue = undecorated;
        undecorated = aValue;
        if (oldValue && !undecorated) {
            decorate();
        } else if (!oldValue && undecorated) {
            undecorate();
        }
    }

    public boolean isResizable() {
        return resizable;
    }

    public void setResizable(boolean aValue) {
        resizable = aValue;
        updateDecorCursors();
    }

    public void setPosition(double aLeft, double aTop) {
        Style es = element.getStyle();
        es.setLeft(aLeft, Style.Unit.PX);
        es.setTop(aTop, Style.Unit.PX);
        es.setPosition(Style.Position.ABSOLUTE);
    }

    public void setSize(double aWidth, double aHeight) {
        if (content != null) {
            Element elem = content.getElement();
            elem.getStyle().setWidth(aWidth, Style.Unit.PX);
            elem.getStyle().setHeight(aHeight, Style.Unit.PX);
        }
    }

    public void setWidth(String width) {
        if (content != null) {
            content.getElement().getStyle().setProperty("width", width);
        }
    }

    public void setHeight(String height) {
        if (content != null) {
            content.getElement().getStyle().setProperty("height", height);
        }
    }

    protected void decorate() {
        if (content != null) {
            content.getElement().addClassName(styles.contentDecor());
        }
        element.appendChild(n.getElement());
        element.appendChild(s.getElement());
        element.appendChild(w.getElement());
        element.appendChild(e.getElement());
        element.appendChild(nw.getElement());
        element.appendChild(ne.getElement());
        element.appendChild(sw.getElement());
        element.appendChild(se.getElement());
    }

    protected void undecorate() {
        e.getElement().removeFromParent();
        w.getElement().removeFromParent();
        n.getElement().removeFromParent();
        s.getElement().removeFromParent();
        nw.getElement().removeFromParent();
        ne.getElement().removeFromParent();
        sw.getElement().removeFromParent();
        se.getElement().removeFromParent();
        if (content != null) {
            content.getElement().removeClassName(styles.contentDecor());
        }
    }

    protected boolean isNresizable() {
        return resizable;
    }

    protected boolean isSresizable() {
        return resizable;
    }

    protected boolean isWresizable() {
        return resizable;
    }

    protected boolean isEresizable() {
        return resizable;
    }

    protected void updateDecorCursors() {
        if (isNresizable()) {
            n.getElement().getStyle().clearCursor();
        } else {
            n.getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
        }
        if (isSresizable()) {
            s.getElement().getStyle().clearCursor();
        } else {
            s.getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
        }
        if (isEresizable()) {
            e.getElement().getStyle().clearCursor();
        } else {
            e.getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
        }
        if (isWresizable()) {
            w.getElement().getStyle().clearCursor();
        } else {
            w.getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
        }
        if (isNresizable() && isWresizable()) {
            nw.getElement().getStyle().clearCursor();
        } else if (isNresizable()) {
            nw.getElement().getStyle().setCursor(Style.Cursor.N_RESIZE);
        } else if (isWresizable()) {
            nw.getElement().getStyle().setCursor(Style.Cursor.W_RESIZE);
        } else {
            nw.getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
        }
        if (isNresizable() && isEresizable()) {
            ne.getElement().getStyle().clearCursor();
        } else if (isNresizable()) {
            ne.getElement().getStyle().setCursor(Style.Cursor.N_RESIZE);
        } else if (isEresizable()) {
            ne.getElement().getStyle().setCursor(Style.Cursor.E_RESIZE);
        } else {
            ne.getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
        }
        if (isSresizable() && isEresizable()) {
            se.getElement().getStyle().clearCursor();
        } else if (isSresizable()) {
            se.getElement().getStyle().setCursor(Style.Cursor.S_RESIZE);
        } else if (isEresizable()) {
            se.getElement().getStyle().setCursor(Style.Cursor.E_RESIZE);
        } else {
            se.getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
        }
        if (isSresizable() && isWresizable()) {
            sw.getElement().getStyle().clearCursor();
        } else if (isSresizable()) {
            sw.getElement().getStyle().setCursor(Style.Cursor.S_RESIZE);
        } else if (isWresizable()) {
            sw.getElement().getStyle().setCursor(Style.Cursor.W_RESIZE);
        } else {
            sw.getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
        }
    }

    protected void focus() {
    }

}
