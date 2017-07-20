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
                double newTop = Resizable.this.element.getScrollTop() + dy;
                Resizable.this.element.style.top =newTop >= 0 ? newTop : 0+ 'px');
                if (newTop >= 0) {
                    content.element.style.height =content.element.getOffsetHeight() - dy+ 'px');
                }
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void activate() {
            element.classList.add("north-decor-active");
        }

        @Override
        protected void deactivate() {
            element.classList.remove("north-decor-active");
        }

    };
    protected Draggable s = new DraggableDecoration(styles.horizontalDecor(), styles.southDecor(), "horizontal-decor", "south-decor") {

        @Override
        protected boolean changeTarget(int dx, int dy, NativeEvent aEvent) {
            if (isSresizable()) {
                content.element.style.height =content.element.getOffsetHeight() + dy+ 'px');
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void activate() {
            element.classList.add("south-decor-active");
        }

        @Override
        protected void deactivate() {
            element.classList.remove("south-decor-active");
        }

    };
    protected Draggable w = new DraggableDecoration(styles.verticalDecor(), styles.westDecor(), "vertical-decor", "west-decor") {

        @Override
        protected boolean changeTarget(int dx, int dy, NativeEvent aEvent) {
            if (isWresizable()) {
                double newLeft = Resizable.this.element.getScrollLeft() + dx;
                Resizable.this.element.style.setLeft(newLeft >= 0 ? newLeft : 0+ 'px');
                if (newLeft >= 0) {
                    content.element.style.width =content.element.getOffsetWidth() - dx+ 'px');
                }
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void activate() {
            element.classList.add("west-decor-active");
        }

        @Override
        protected void deactivate() {
            element.classList.remove("west-decor-active");
        }

    };
    protected Draggable e = new DraggableDecoration(styles.verticalDecor(), styles.eastDecor(), "vertical-decor", "east-decor") {
        @Override
        protected boolean changeTarget(int dx, int dy, NativeEvent aEvent) {
            if (isEresizable()) {
                content.element.style.width =content.element.getOffsetWidth() + dx+ 'px');
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void activate() {
            element.classList.add("east-decor-active");
        }

        @Override
        protected void deactivate() {
            element.classList.remove("east-decor-active");
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
            element.classList.add("north-west-decor-active");
        }

        @Override
        protected void deactivate() {
            element.classList.remove("north-west-decor-active");
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
            element.classList.add("north-east-decor-active");
        }

        @Override
        protected void deactivate() {
            element.classList.remove("north-east-decor-active");
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
            element.classList.add("south-west-decor-active");
        }

        @Override
        protected void deactivate() {
            element.classList.remove("south-west-decor-active");
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
            element.classList.add("south-east-decor-active");
        }

        @Override
        protected void deactivate() {
            element.classList.remove("south-east-decor-active");
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
        es.setLeft(aLeft+ 'px');
        es.top =aTop+ 'px');
        es.position = 'absolute';
    }

    public void setSize(double aWidth, double aHeight) {
        if (content != null) {
            Element elem = content.element;
            elem.getStyle().width =aWidth+ 'px');
            elem.getStyle().height =aHeight+ 'px');
        }
    }

    public void setWidth(String width) {
        if (content != null) {
            content.element.style.setProperty("width", width);
        }
    }

    public void setHeight(String height) {
        if (content != null) {
            content.element.style.setProperty("height", height);
        }
    }

    protected void decorate() {
        if (content != null) {
            content.element.classList.add(styles.contentDecor());
        }
        element.appendChild(n.element);
        element.appendChild(s.element);
        element.appendChild(w.element);
        element.appendChild(e.element);
        element.appendChild(nw.element);
        element.appendChild(ne.element);
        element.appendChild(sw.element);
        element.appendChild(se.element);
    }

    protected void undecorate() {
        e.element.removeFromParent();
        w.element.removeFromParent();
        n.element.removeFromParent();
        s.element.removeFromParent();
        nw.element.removeFromParent();
        ne.element.removeFromParent();
        sw.element.removeFromParent();
        se.element.removeFromParent();
        if (content != null) {
            content.element.classList.remove(styles.contentDecor());
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
            n.element.style.clearCursor();
        } else {
            n.element.style.setCursor(Style.Cursor.DEFAULT);
        }
        if (isSresizable()) {
            s.element.style.clearCursor();
        } else {
            s.element.style.setCursor(Style.Cursor.DEFAULT);
        }
        if (isEresizable()) {
            e.element.style.clearCursor();
        } else {
            e.element.style.setCursor(Style.Cursor.DEFAULT);
        }
        if (isWresizable()) {
            w.element.style.clearCursor();
        } else {
            w.element.style.setCursor(Style.Cursor.DEFAULT);
        }
        if (isNresizable() && isWresizable()) {
            nw.element.style.clearCursor();
        } else if (isNresizable()) {
            nw.element.style.setCursor(Style.Cursor.N_RESIZE);
        } else if (isWresizable()) {
            nw.element.style.setCursor(Style.Cursor.W_RESIZE);
        } else {
            nw.element.style.setCursor(Style.Cursor.DEFAULT);
        }
        if (isNresizable() && isEresizable()) {
            ne.element.style.clearCursor();
        } else if (isNresizable()) {
            ne.element.style.setCursor(Style.Cursor.N_RESIZE);
        } else if (isEresizable()) {
            ne.element.style.setCursor(Style.Cursor.E_RESIZE);
        } else {
            ne.element.style.setCursor(Style.Cursor.DEFAULT);
        }
        if (isSresizable() && isEresizable()) {
            se.element.style.clearCursor();
        } else if (isSresizable()) {
            se.element.style.setCursor(Style.Cursor.S_RESIZE);
        } else if (isEresizable()) {
            se.element.style.setCursor(Style.Cursor.E_RESIZE);
        } else {
            se.element.style.setCursor(Style.Cursor.DEFAULT);
        }
        if (isSresizable() && isWresizable()) {
            sw.element.style.clearCursor();
        } else if (isSresizable()) {
            sw.element.style.setCursor(Style.Cursor.S_RESIZE);
        } else if (isWresizable()) {
            sw.element.style.setCursor(Style.Cursor.W_RESIZE);
        } else {
            sw.element.style.setCursor(Style.Cursor.DEFAULT);
        }
    }

    protected void focus() {
    }

}
