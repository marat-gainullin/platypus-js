package com.eas.widgets.containers;

import com.eas.client.GroupingHandlerRegistration;
import java.util.ArrayList;
import java.util.List;

import com.eas.core.HasPublished;
import com.eas.ui.Widget;
import com.eas.window.WindowPanel;
import com.eas.window.events.ActivateEvent;
import com.eas.window.events.ActivateHandler;
import com.eas.window.events.ClosedEvent;
import com.eas.window.events.ClosedHandler;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.touch.client.Point;

/**
 *
 * @author mg
 */
public class Desktop extends Anchors {

    public static final int DEFAULT_WINDOWS_SPACING_X = 25;
    public static final int DEFAULT_WINDOWS_SPACING_Y = 20;
    protected Point consideredPosition = new Point(DEFAULT_WINDOWS_SPACING_X, DEFAULT_WINDOWS_SPACING_Y);

    public Desktop() {
        super();
    }

    public List<WindowPanel> getManaged() {
        List<WindowPanel> windows = new ArrayList<>();
        for (Widget w : children) {
            if (w instanceof WindowPanel) {
                windows.add((WindowPanel) w);
            }
        }
        return windows;
    }

    public List<HasPublished> getPublishedManaged() {
        List<HasPublished> res = new ArrayList<>();
        for (Widget w : children) {
            if (w instanceof HasPublished && w instanceof WindowPanel) {
                res.add((HasPublished) w);
            }
        }
        return res;
    }

    public void minimizeAll() {
        for (Widget w : children) {
            if (w instanceof WindowPanel) {
                ((WindowPanel) w).minimize();
            }
        }
    }

    public void maximizeAll() {
        for (Widget w : children) {
            if (w instanceof WindowPanel) {
                ((WindowPanel) w).maximize();
            }
        }
    }

    public void restoreAll() {
        for (Widget w : children) {
            if (w instanceof WindowPanel) {
                ((WindowPanel) w).restore();
            }
        }
    }

    public void closeAll() {
        for (Widget w : children.toArray(new Widget[]{})) {
            if (w instanceof WindowPanel) {
                ((WindowPanel) w).close();
            }
        }
    }

    public Point getConsideredPosition() {
        return consideredPosition;
    }

    @Override
    public void add(Widget w) {
        super.add(w);
        check(w);
    }

    @Override
    public void add(Widget w, int beforeIndex) {
        super.add(w, beforeIndex);
        check(w);
    }

    private void check(Widget w) {
        if (w instanceof WindowPanel) {
            WindowPanel wnd = (WindowPanel) w;
            refreshConsideredPosition();
            final GroupingHandlerRegistration regs = new GroupingHandlerRegistration();
            regs.add(wnd.addActivateHandler(new ActivateHandler() {

                @Override
                public void onActivate(ActivateEvent anEvent) {
                    for (Widget w : children) {
                        if (w instanceof WindowPanel && w != anEvent.getTarget()) {
                            ((WindowPanel) w).deactivate();
                        }
                    }
                }

            }));
            regs.add(wnd.addClosedHandler(new ClosedHandler() {

                @Override
                public void onClosed(ClosedEvent event) {
                    Desktop.super.remove(event.getTarget());
                    regs.removeHandler();
                }

            }));
        }
    }

    @Override
    public boolean remove(Widget w) {
        if (w.getParent() == this) {
            if (w instanceof WindowPanel) {
                ((WindowPanel) w).close();
                return true;
            } else {
                return super.remove(w);
            }
        } else {
            return false;
        }
    }

    @Override
    public Widget remove(int index) {
        if (index >= 0 && index < children.size()) {
            Widget w = children.get(index);
            if (w instanceof WindowPanel) {
                ((WindowPanel) w).close();
                return w;
            } else {
                return super.remove(index);
            }
        } else {
            return null;
        }
    }

    private void refreshConsideredPosition() {
        if (consideredPosition.getX() > getElement().getClientWidth() / 2) {
            consideredPosition = new Point(0, consideredPosition.getY());// setX(0)
        }
        if (consideredPosition.getY() > getElement().getClientHeight() / 2) {
            consideredPosition = new Point(consideredPosition.getX(), 0);// setY(0)
        }
        consideredPosition = consideredPosition.plus(new Point(DEFAULT_WINDOWS_SPACING_X, DEFAULT_WINDOWS_SPACING_Y));
    }

    @Override
    protected void publish(JavaScriptObject aValue) {
        publish(this, aValue);
    }

    private native static void publish(HasPublished aWidget, JavaScriptObject published)/*-{
        Object.defineProperty(published, "forms", {
            get : function() {
                var managed = aWidget.@com.eas.widgets.DesktopPane::getPublishedManaged()();
                var res = [];
                for ( var i = 0; i < managed.@java.util.List::size()(); i++) {
                    var m = managed.@java.util.List::get(I)(i);
                    res[res.length] = m.@com.eas.core.HasPublished::getPublished()();
                }
                return res;
            }
        });
        published.closeAll = function() {
            aWidget.@com.eas.widgets.DesktopPane::closeAll()();
        }
        published.minimizeAll = function() {
            aWidget.@com.eas.widgets.DesktopPane::minimizeAll()();
        }
        published.maximizeAll = function() {
            aWidget.@com.eas.widgets.DesktopPane::maximizeAll()();
        }
        published.restoreAll = function() {
            aWidget.@com.eas.widgets.DesktopPane::restoreAll()();
        }
    }-*/;
}
