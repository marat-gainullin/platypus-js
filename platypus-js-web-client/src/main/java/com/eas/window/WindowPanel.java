package com.eas.window;

import com.eas.core.XElement;
import com.eas.ui.Widget;
import com.eas.window.events.ActivateEvent;
import com.eas.window.events.ActivateHandler;
import com.eas.window.events.BeforeCloseEvent;
import com.eas.window.events.BeforeCloseHandler;
import com.eas.window.events.ClosedEvent;
import com.eas.window.events.ClosedHandler;
import com.eas.window.events.DeactivateEvent;
import com.eas.window.events.DeactivateHandler;
import com.eas.window.events.HasActivateHandlers;
import com.eas.window.events.HasBeforeCloseHandlers;
import com.eas.window.events.HasClosedHandlers;
import com.eas.window.events.HasDeactivateHandlers;
import com.eas.window.events.HasMaximizeHandlers;
import com.eas.window.events.HasMinimizeHandlers;
import com.eas.window.events.HasMoveHandlers;
import com.eas.window.events.HasOpenHandlers;
import com.eas.window.events.HasRestoreHandlers;
import com.eas.window.events.MaximizeEvent;
import com.eas.window.events.MaximizeHandler;
import com.eas.window.events.MinimizeEvent;
import com.eas.window.events.MinimizeHandler;
import com.eas.window.events.MoveEvent;
import com.eas.window.events.MoveHandler;
import com.eas.window.events.OpenEvent;
import com.eas.window.events.OpenHandler;
import com.eas.window.events.RestoreEvent;
import com.eas.window.events.RestoreHandler;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.ui.HasAnimation;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author mg
 */
public class WindowPanel extends Resizable implements HasOpenHandlers, HasClosedHandlers, HasBeforeCloseHandlers,
        HasActivateHandlers, HasDeactivateHandlers,
        HasMaximizeHandlers, HasMinimizeHandlers, HasRestoreHandlers,
        HasMoveHandlers, HasAnimation {

    protected static final String ACTIVE_SUFFIX_NAME = "active";

    protected Caption caption = new Caption(this, "");
    protected boolean animationEnabled = true;
    protected boolean movable = true;
    protected boolean maximized;
    protected boolean minimized;
    protected boolean active;
    protected boolean maximizable = true;
    protected boolean minimizable = true;
    protected boolean closable = true;

    public WindowPanel() {
        super();
        super.setVisible(false); // This is for open event when visible becomes true
        element.classList.add("window-panel");
        mdHandler = caption.element.<XElement>cast().addEventListener(BrowserEvents.MOUSEDOWN, new XElement.NativeHandler() {

            @Override
            public void on(NativeEvent event) {
                event.preventDefault();
                event.stopPropagation();
                focus();
                if (movable && !maximized) {
                    DOM.setCapture(caption.element);
                    mouseScreenX = event.getScreenX();
                    mouseScreenY = event.getScreenY();
                    String tLeft = element.getStyle().getLeft();
                    targetScrollLeft = Integer.parseInt(tLeft.substring(0, tLeft.length() - 2));
                    String tTop = element.getStyle().getTop();
                    targetScrollTop = Integer.parseInt(tTop.substring(0, tTop.length() - 2));
                }
            }

        });
        muHandler = caption.element.<XElement>cast().addEventListener(BrowserEvents.MOUSEUP, new XElement.NativeHandler() {

            @Override
            public void on(NativeEvent event) {
                dragged = false;
                event.preventDefault();
                event.stopPropagation();
                if (movable && !maximized) {
                    DOM.releaseCapture(caption.element);
                    endMoving();
                }
            }

        });
        mmHandler = caption.element.<XElement>cast().addEventListener(BrowserEvents.MOUSEMOVE, new XElement.NativeHandler() {

            @Override
            public void on(NativeEvent event) {
                if (movable && !maximized) {
                    if (DOM.getCaptureElement() == caption.element) {
                        event.preventDefault();
                        event.stopPropagation();
                        int dx = event.getScreenX() - mouseScreenX;
                        int dy = event.getScreenY() - mouseScreenY;
                        if (!dragged && (dx != 0 || dy != 0)) {
                            dragged = true;
                            beginMoving();
                        }
                        setPosition(targetScrollLeft + dx >= 0 ? targetScrollLeft + dx : 0, targetScrollTop + dy >= 0 ? targetScrollTop + dy : 0);
                    }
                }
            }

        });
        element.<XElement>cast().addEventListener(BrowserEvents.MOUSEDOWN, new XElement.NativeHandler() {

            @Override
            public void on(NativeEvent event) {
                focus();
            }
        });
    }

    protected HandlerRegistration mdHandler;
    protected HandlerRegistration muHandler;
    protected HandlerRegistration mmHandler;

    protected int mouseScreenX;
    protected int mouseScreenY;
    protected int targetScrollLeft;
    protected int targetScrollTop;

    protected boolean dragged;

    @Override
    protected void decorate() {
        super.decorate();
        content.element.setClassName("content-decor");
        if (isActive()) {
            content.element.classList.add("content-decor-active");
        } else {
            content.element.classList.remove("content-decor-active");
        }
        caption.element.style.clearDisplay();
    }

    @Override
    protected void undecorate() {
        super.undecorate();
        content.element.classList.remove("content-decor");
        content.element.classList.remove("content-decor-active");
        if (caption != null) {
            caption.element.style.display ='none');
        }
    }

    @Override
    public void add(Widget w) {
        setContent(w);
    }

    @Override
    public void add(Widget w, int beforeIndex) {
        setContent(w);
    }

    @Override
    public boolean remove(Widget w) {
        if (w == content) {
            setContent(null);
        }
        return true;
    }

    @Override
    public Widget remove(int index) {
        if(index >=0 && index < children.size()){
            Widget removed = children.get(index);
            if(removed == content){
                Widget old = content;
                setContent(null);
                return old;
            } else{
                return super.remove(index);
            }
        } else {
            return null;
        }
    }

    @Override
    public void setContent(Widget w) {
        if (content != null) {
            content.element.classList.remove("window-content");
            super.remove(content);
        }
        content = w;
        if (content != null) {
            content.element.classList.add("window-content");
            super.add(w);
        }
    }

    @Override
    public Widget getContent() {
        return content;
    }

    @Override
    protected boolean isNresizable() {
        return resizable && !maximized && !minimized;
    }

    @Override
    protected boolean isSresizable() {
        return resizable && !maximized && !minimized;
    }

    @Override
    protected boolean isWresizable() {
        return resizable && !maximized;
    }

    @Override
    protected boolean isEresizable() {
        return resizable && !maximized;
    }

    public boolean isMinimized() {
        return minimized;
    }

    public boolean isMaximized() {
        return maximized;
    }

    public boolean isMinimizable() {
        return minimizable;
    }

    public void setMinimizable(boolean minimizable) {
        this.minimizable = minimizable;
    }

    public void setClosable(boolean closable) {
        this.closable = closable;
    }

    public boolean isClosable() {
        return closable;
    }

    public boolean isMaximizable() {
        return maximizable;
    }

    public void setMaximizable(boolean maximizable) {
        this.maximizable = maximizable;
    }

    public void maximize() {
        if (!maximized) {
            restoreSnapshot();
            maximized = true;
            updateDecorCursors();
            snapshotMetrics();
            // editing
            Style contentStyle = content.element.style; // content
            int leftInset = content.element.getAbsoluteLeft() - element.getAbsoluteLeft();
            int rightInset = element.getAbsoluteRight() - getContent().element.getAbsoluteRight();
            int hInsets = leftInset + rightInset;
            int topInset = getContent().element.getAbsoluteTop() - element.getAbsoluteTop();
            int bottomInset = element.getAbsoluteBottom() - getContent().element.getAbsoluteBottom();
            int vInsets = topInset + bottomInset;
            Element parentElement = element.getParentElement();
            int parentScrollWidth = parentElement.getScrollWidth();
            int parentScrollHeight;
            if (parentElement == Document.get().getBody()) {
                // Some browsers return incorrect height for body element
                parentScrollHeight = Document.get().getClientHeight();
            } else {
                parentScrollHeight = parentElement.getScrollHeight();
            }
            contentStyle.width =parentScrollWidth - hInsets+ 'px');
            contentStyle.height =parentScrollHeight - vInsets+ 'px');
            // editing
            Style wndStyle = element.getStyle(); // window
            wndStyle.setLeft(0+ 'px');
            wndStyle.top =0+ 'px');
            fireMaximizeEvent();
        }
    }

    @Override
    public void setPosition(double left, double top) {
        super.setPosition(left, top);
        fireMoveEvent(left, top);
    }

    @Override
    public void setSize(double aWidth, double aHeight) {
        super.setSize(aWidth, aHeight);
    }

    private void snapshotMetrics() throws NumberFormatException {
        // measurement
        Style contentStyle = getContent().element.style; // content
        String sHeight = contentStyle.getHeight();
        contentHeightToRestore = Double.parseDouble(sHeight.substring(0, sHeight.length() - 2));
        String sWidth = contentStyle.getWidth();
        contentWidthToRestore = Double.parseDouble(sWidth.substring(0, sWidth.length() - 2));
        Style wndStyle = element.getStyle(); // window
        String sLeft = wndStyle.getLeft();
        leftToRestore = Double.parseDouble(sLeft.substring(0, sLeft.length() - 2));
        String sTop = wndStyle.getTop();
        topToRestore = Double.parseDouble(sTop.substring(0, sTop.length() - 2));
    }

    protected boolean preMinimizedStateMaximized;
    protected boolean preMinimzedStateNormal;

    /**
     * Minimizes the window. Minimize here is setting zero as height.
     * Descendants may decide to do something else. minimize does a snapshot of
     * all window's metrics. So subsequent restore or restoreSnapshot calls will
     * lead to correct restoration of the window.
     */
    public void minimize() {
        if (!minimized) {
            preMinimizedStateMaximized = maximized;
            preMinimzedStateNormal = !maximized && !minimized;
            restoreSnapshot();
            minimized = true;
            updateDecorCursors();
            // measure
            snapshotMetrics();
            // edit
            Style targetStyle = getContent().element.style; // content
            targetStyle.height =0+ 'px');
            fireMinimizeEvent();
        }
    }

    protected Double leftToRestore;
    protected Double topToRestore;
    protected Double contentWidthToRestore;
    protected Double contentHeightToRestore;

    public void restoreSnapshot() {
        if (minimized || maximized) {
            minimized = false;
            maximized = false;
            if (leftToRestore != null) {
                element.getStyle().setLeft(leftToRestore+ 'px');
            }
            leftToRestore = null;
            if (topToRestore != null) {
                element.getStyle().top =topToRestore+ 'px');
            }
            topToRestore = null;
            if (contentWidthToRestore != null) {
                content.element.style.width =contentWidthToRestore+ 'px');
            }
            contentWidthToRestore = null;
            if (contentHeightToRestore != null) {
                content.element.style.height =contentHeightToRestore+ 'px');
            }
            contentHeightToRestore = null;
            fireRestoreEvent();
        }
    }

    public void restore() {
        if (minimized || maximized) {
            if (maximized || preMinimzedStateNormal) {
                restoreSnapshot();
                updateDecorCursors();
            } else if (preMinimizedStateMaximized) {
                maximize();
            }
        }
    }

    public boolean isActive() {
        return active;
    }

    public void activate() {
        if (!active) {
            active = true;
            if (caption != null) {
                caption.element.classList.add(Caption.CAPTION_CLASS_NAME + "-" + ACTIVE_SUFFIX_NAME);
            }
            Draggable[] draggables = new Draggable[]{n, s, w, e, ne, nw, se, sw};
            for (Draggable d : draggables) {
                d.activate();
            }
            content.element.classList.add("content-decor" + "-" + ACTIVE_SUFFIX_NAME);
            fireActivateEvent();
        }
    }

    public void deactivate() {
        if (active) {
            active = false;
            if (caption != null) {
                caption.element.classList.remove(Caption.CAPTION_CLASS_NAME + "-" + ACTIVE_SUFFIX_NAME);
            }
            Draggable[] draggables = new Draggable[]{n, s, w, e, ne, nw, se, sw};
            for (Draggable d : draggables) {
                d.deactivate();
            }
            content.element.classList.remove("content-decor" + "-" + ACTIVE_SUFFIX_NAME);
            fireDeactivateEvent();
        }
    }

    public void toFront() {
        Element parentEl = element.getParentElement();
        if (parentEl != null) {
            Element lastChild = (Element) parentEl.getLastChild();
            if (lastChild != element) {
                parentEl.insertAfter(element, lastChild);
            }
        }
    }

    /**
     * Activates and brings the window to front. Descandants may focus default
     * widget in this method.
     */
    @Override
    public void focus() {
        activate();
        toFront();
    }

    @Override
    public void setVisible(boolean aValue) {
        if (!isVisible() && aValue) {
            super.setVisible(aValue);
            fireOpenEvent();
        } else {
            super.setVisible(aValue);
        }
    }

    protected void fireCloseEvent() {
        fireClosedEvent();
    }

    public void close() {
        if (fireBeforeCloseEvent()) {
            element.removeFromParent();
            fireCloseEvent();
        }
    }

    public boolean isMovable() {
        return movable;
    }

    public void setMovable(boolean aValue) {
        movable = aValue;
        updateCaptionCursor();
    }

    @Override
    public boolean isAnimationEnabled() {
        return animationEnabled;
    }

    @Override
    public void setAnimationEnabled(boolean aValue) {
        if (animationEnabled != aValue) {
            animationEnabled = aValue;
            if (animationEnabled) {
                content.element.style.clearProperty("transition");
            } else {
                content.element.style.setProperty("transition", "none");
            }
        }
    }

    @Override
    protected void beginResizing() {
        super.beginResizing();
        content.element.style.setProperty("transition", "none");
        element.getStyle().setProperty("transition", "none");
        content.element.style.setVisibility(Style.Visibility.HIDDEN);
    }

    @Override
    protected void endResizing() {
        super.endResizing();
        content.element.style.clearVisibility();
        if (animationEnabled) {
            content.element.style.clearProperty("transition");
            element.getStyle().clearProperty("transition");
        }
    }

    protected void beginMoving() {
        element.getStyle().setProperty("transition", "none");
        content.element.style.setVisibility(Style.Visibility.HIDDEN);
    }

    protected void endMoving() {
        content.element.style.clearVisibility();
        if (animationEnabled) {
            element.getStyle().clearProperty("transition");
        }
    }

    protected void updateCaptionCursor() {
        if (caption != null) {
            if (movable) {
                caption.element.style.clearCursor();
            } else {
                caption.element.style.setCursor(Style.Cursor.DEFAULT);
            }
        }
    }

    protected Set<OpenHandler> openHandlers = new Set();

    @Override
    public HandlerRegistration addOpenHandler(OpenHandler handler) {
        openHandlers.add(handler);
        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                openHandlers.remove(handler);
            }

        };
    }

    private void fireOpenEvent() {
        OpenEvent event = new OpenEvent(this);
        for (OpenHandler h : openHandlers) {
            h.onOpen(event);
        }
    }

    protected Set<ClosedHandler> closedHandlers = new Set();

    @Override
    public HandlerRegistration addClosedHandler(ClosedHandler handler) {
        closedHandlers.add(handler);
        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                closedHandlers.remove(handler);
            }

        };
    }

    private void fireClosedEvent() {
        ClosedEvent event = new ClosedEvent(this);
        for (ClosedHandler h : closedHandlers) {
            h.onClosed(event);
        }
    }

    protected Set<BeforeCloseHandler> beforeCloseHandlers = new Set();

    @Override
    public HandlerRegistration addBeforeCloseHandler(BeforeCloseHandler handler) {
        beforeCloseHandlers.add(handler);
        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                beforeCloseHandlers.remove(handler);
            }

        };
    }

    private boolean fireBeforeCloseEvent() {
        BeforeCloseEvent event = new BeforeCloseEvent(this);
        for (BeforeCloseHandler h : beforeCloseHandlers) {
            h.onBeforeClose(event);
            if (event.isCancelled()) {
                return false;
            }
        }
        return true;
    }

    protected Set<ActivateHandler> activateHandlers = new Set();

    @Override
    public HandlerRegistration addActivateHandler(ActivateHandler handler) {
        activateHandlers.add(handler);
        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                activateHandlers.remove(handler);
            }

        };
    }

    private void fireActivateEvent() {
        ActivateEvent event = new ActivateEvent(this);
        for (ActivateHandler h : activateHandlers) {
            h.onActivate(event);
        }
    }

    protected Set<DeactivateHandler> deactivateHandlers = new Set();

    @Override
    public HandlerRegistration addDeactivateHandler(DeactivateHandler handler) {
        deactivateHandlers.add(handler);
        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                deactivateHandlers.remove(handler);
            }

        };
    }

    private void fireDeactivateEvent() {
        DeactivateEvent event = new DeactivateEvent(this);
        for (DeactivateHandler h : deactivateHandlers) {
            h.onDeactivate(event);
        }
    }

    protected Set<MinimizeHandler> minimizeHandlers = new Set();

    @Override
    public HandlerRegistration addMinimizeHandler(MinimizeHandler handler) {
        minimizeHandlers.add(handler);
        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                minimizeHandlers.remove(handler);
            }

        };
    }

    private void fireMinimizeEvent() {
        MinimizeEvent event = new MinimizeEvent(this);
        for (MinimizeHandler h : minimizeHandlers) {
            h.onMinimize(event);
        }
    }

    protected Set<MaximizeHandler> maximizeHandlers = new Set();

    @Override
    public HandlerRegistration addMaximizeHandler(MaximizeHandler handler) {
        maximizeHandlers.add(handler);
        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                maximizeHandlers.remove(handler);
            }

        };
    }

    private void fireMaximizeEvent() {
        MaximizeEvent event = new MaximizeEvent(this);
        for (MaximizeHandler h : maximizeHandlers) {
            h.onMaximize(event);
        }
    }

    protected Set<RestoreHandler> restoreHandlers = new Set();

    @Override
    public HandlerRegistration addRestoreHandler(RestoreHandler handler) {
        restoreHandlers.add(handler);
        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                restoreHandlers.remove(handler);
            }

        };
    }

    private void fireRestoreEvent() {
        RestoreEvent event = new RestoreEvent(this);
        for (RestoreHandler h : restoreHandlers) {
            h.onRestore(event);
        }
    }

    protected Set<MoveHandler> moveHandlers = new Set();

    @Override
    public HandlerRegistration addMoveHandler(MoveHandler handler) {
        moveHandlers.add(handler);
        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                moveHandlers.remove(handler);
            }

        };
    }

    private void fireMoveEvent(double left, double top) {
        MoveEvent event = new MoveEvent(this, left, top);
        for (MoveHandler h : moveHandlers) {
            h.onMove(event);
        }
    }
}
