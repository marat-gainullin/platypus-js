package com.eas.widgets.containers;

import com.eas.ui.EventsPublisher;
import com.eas.ui.Widget;
import com.eas.ui.events.ContainerEvent;
import com.eas.ui.events.AddHandler;
import com.eas.ui.events.HasAddHandlers;
import com.eas.ui.events.HasRemoveHandlers;
import com.eas.ui.events.RemoveHandler;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerRegistration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author mgainullin
 */
public abstract class Container extends Widget implements HasAddHandlers, HasRemoveHandlers {

    protected final List<Widget> children = new ArrayList<>();

    protected Container() {
        super();
        element.getStyle().setPosition(Style.Position.RELATIVE);
        element.getStyle().setOverflow(Style.Overflow.HIDDEN);
    }

    public int getCount() {
        return children.size();
    }

    public Widget get(int index) {
        if (index >= 0 && index < children.size()) {
            return children.get(index);
        } else {
            return null;
        }
    }

    public int indexOf(Widget w) {
        return children.indexOf(w);
    }

    public void add(Widget w) {
        children.add(w);
        element.appendChild(w.getElement());
        fireAdded(w);
    }

    public void add(Widget w, int beforeIndex) {
        children.add(beforeIndex, w);
        w.setParent(this);
        if (beforeIndex < children.size()) {
            element.insertBefore(w.getElement(), children.get(beforeIndex).getElement());
        } else {
            element.appendChild(w.getElement());
        }
        fireAdded(w);
    }

    public boolean remove(Widget w) {
        boolean removed = children.remove(w);
        if (removed) {
            w.setParent(null);
            w.getElement().removeFromParent();
            fireRemoved(w);
        }
        return removed;
    }

    public Widget remove(int index) {
        if (index >= 0 && index < children.size()) {
            Widget w = children.remove(index);
            w.setParent(null);
            w.getElement().removeFromParent();
            fireRemoved(w);
            return w;
        } else {
            return null;
        }
    }

    public void clear() {
        for (int i = 0; i < children.size(); i++) {
            remove(i);
        }
        children.clear();
    }

    private final Set<AddHandler> addHandlers = new HashSet<>();

    @Override
    public HandlerRegistration addAddHandler(AddHandler handler) {
        addHandlers.add(handler);
        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                addHandlers.remove(handler);
            }
        };
    }

    private void fireAdded(Widget w) {
        ContainerEvent event = new ContainerEvent(this, w);
        for (AddHandler h : addHandlers) {
            h.onAdd(event);
        }
    }

    private final Set<RemoveHandler> removeHandlers = new HashSet<>();

    @Override
    public HandlerRegistration addRemoveHandler(RemoveHandler handler) {
        removeHandlers.add(handler);
        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                removeHandlers.remove(handler);
            }
        };
    }

    private void fireRemoved(Widget w) {
        ContainerEvent event = new ContainerEvent(this, w);
        for (RemoveHandler h : removeHandlers) {
            h.onRemove(event);
        }
    }

    private JavaScriptObject componentAdded;
    
    public JavaScriptObject getComponentAdded() {
        return componentAdded;
    }

    protected HandlerRegistration componentAddedReg;

    public void setComponentAdded(JavaScriptObject aValue) {
        if (componentAdded != aValue) {
            if (componentAddedReg != null) {
                componentAddedReg.removeHandler();
                componentAddedReg = null;
            }
            componentAdded = aValue;
            if (componentAdded != null) {
                componentAddedReg = addAddHandler(new AddHandler() {
                    @Override
                    public void onAdd(ContainerEvent event) {
                        if (componentAdded != null) {
                            executeEvent(componentAdded, EventsPublisher.publishContainerEvent(event));
                        }
                    }
                });
            }
        }
    }

    private JavaScriptObject componentRemoved;
    
    public JavaScriptObject getComponentRemoved() {
        return componentRemoved;
    }

    protected HandlerRegistration componentRemovedReg;

    public void setComponentRemoved(JavaScriptObject aValue) {
        if (componentRemoved != aValue) {
            if (componentRemovedReg != null) {
                componentRemovedReg.removeHandler();
                componentRemovedReg = null;
            }
            componentRemoved = aValue;
            if (componentRemoved != null) {
                componentRemovedReg = addRemoveHandler(new RemoveHandler() {
                    @Override
                    public void onRemove(ContainerEvent event) {
                        if (componentRemoved != null) {
                            executeEvent(componentRemoved, EventsPublisher.publishContainerEvent(event));
                        }
                    }
                });
            }
        }
    }
}
