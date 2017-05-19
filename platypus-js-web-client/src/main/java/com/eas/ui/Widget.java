package com.eas.ui;

import com.eas.core.Utils;
import com.eas.core.XElement;
import com.eas.menu.HasComponentPopupMenu;
import com.eas.menu.PlatypusPopupMenu;
import com.eas.ui.events.EventsExecutor;
import com.eas.ui.events.HasHideHandlers;
import com.eas.ui.events.HasShowHandlers;
import com.eas.ui.events.HideEvent;
import com.eas.ui.events.HideHandler;
import com.eas.ui.events.ShowEvent;
import com.eas.ui.events.ShowHandler;
import com.eas.widgets.containers.Container;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasEnabled;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author mgainullin
 */
public abstract class Widget implements HasJsFacade, HasEnabled, HasComponentPopupMenu, HasEventsExecutor,
        HasShowHandlers, HasHideHandlers {

    protected JavaScriptObject published;
    protected Element element;
    protected Container parent;
    private Style.Display visibleDisplay = Style.Display.INLINE_BLOCK;
    protected PlatypusPopupMenu menu;
    protected boolean enabled = true;
    protected String name;
    protected EventsExecutor eventsExecutor;

    public Widget(){
        super();
    }
    
    @Override
    public PlatypusPopupMenu getPlatypusPopupMenu() {
        return menu;
    }

    protected HandlerRegistration menuTriggerReg;

    @Override
    public void setPlatypusPopupMenu(PlatypusPopupMenu aMenu) {
        if (menu != aMenu) {
            if (menuTriggerReg != null) {
                menuTriggerReg.removeHandler();
            }
            menu = aMenu;
            if (menu != null) {
                menuTriggerReg = element.<XElement>cast().addEventListener(BrowserEvents.CONTEXTMENU, new XElement.NativeHandler() {

                    @Override
                    public void on(NativeEvent event) {
                        event.preventDefault();
                        event.stopPropagation();
                        menu.setPopupPosition(event.getClientX(), event.getClientY());
                        menu.show();
                    }
                });
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean aValue) {
        boolean oldValue = enabled;
        enabled = aValue;
        if (!oldValue && enabled) {
            getElement().<XElement>cast().unmask();
        } else if (oldValue && !enabled) {
            getElement().<XElement>cast().disabledMask();
        }
    }

    @Override
    public String getJsName() {
        return name;
    }

    @Override
    public void setJsName(String aValue) {
        name = aValue;
    }

    public Style.Display getVisibleDisplay() {
        return visibleDisplay;
    }

    public void setVisibleDisplay(Style.Display aValue) {
        this.visibleDisplay = aValue;
    }

    public Container getParent() {
        return parent;
    }

    public void setParent(Container aValue) {
        parent = aValue;
    }

    public Element getElement() {
        return element;
    }

    public boolean isVisible() {
        return !Style.Display.NONE.name().equals(element.getStyle().getDisplay());
    }

    public void setVisible(boolean aValue) {
        boolean oldValue = isVisible();
        if (oldValue != aValue) {
            if (aValue) {
                element.getStyle().setDisplay(visibleDisplay);
            } else {
                element.getStyle().setDisplay(Style.Display.NONE);
            }
            if (aValue) {
                ShowEvent event = new ShowEvent(this);
                for (ShowHandler h : showHandlers) {
                    h.onShow(event);
                }
            } else {
                HideEvent event = new HideEvent(this);
                for (HideHandler h : hideHandlers) {
                    h.onHide(event);
                }
            }
        }
    }

    @Override
    public EventsExecutor getEventsExecutor() {
        return eventsExecutor;
    }

    @Override
    public void setEventsExecutor(EventsExecutor aExecutor) {
        eventsExecutor = aExecutor;
    }

    @Override
    public JavaScriptObject getPublished() {
        return published;
    }

    @Override
    public void setPublished(JavaScriptObject aValue) {
        if (published != aValue) {
            published = aValue;
            if (published != null) {
                publish(aValue);
            }
        }
    }

    protected abstract void publish(JavaScriptObject aValue);

    private final Set<HideHandler> hideHandlers = new HashSet<>();

    @Override
    public HandlerRegistration addHideHandler(HideHandler handler) {
        hideHandlers.add(handler);
        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                hideHandlers.remove(handler);
            }
        };
    }

    private final Set<ShowHandler> showHandlers = new HashSet<>();

    @Override
    public HandlerRegistration addShowHandler(ShowHandler handler) {
        showHandlers.add(handler);
        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                showHandlers.remove(handler);
            }
        };
    }

    public boolean isAttached() {
        Element cursor = element;
        while (cursor != null && element != Document.get().getBody()) {
            cursor = cursor.getParentElement();
        }
        return cursor != null;
    }

}
