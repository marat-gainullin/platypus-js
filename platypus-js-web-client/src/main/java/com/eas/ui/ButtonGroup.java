package com.eas.ui;

import com.eas.core.HasPublished;
import com.eas.core.Logger;
import com.eas.core.Utils;
import com.eas.ui.events.ContainerEvent;
import com.eas.ui.events.AddHandler;
import com.eas.ui.events.HasAddHandlers;
import com.eas.ui.events.HasRemoveHandlers;
import com.eas.ui.events.HasSelectionHandlers;
import com.eas.ui.events.HasValueChangeHandlers;
import com.eas.ui.events.RemoveHandler;
import com.eas.ui.events.SelectionEvent;
import com.eas.ui.events.SelectionHandler;
import com.eas.ui.events.ValueChangeEvent;
import com.eas.ui.events.ValueChangeHandler;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HandlerRegistration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ButtonGroup implements HasPublished, HasName, HasAddHandlers, HasRemoveHandlers, HasSelectionHandlers<Widget> {

    protected final List<Widget> children = new ArrayList<>();
    protected Map<Widget, HandlerRegistration> childrenValueHanlders = new HashMap<>();
    protected String name;
    protected JavaScriptObject published;
    protected JavaScriptObject onItemSelected;

    public ButtonGroup() {
        super();
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
        if (w instanceof HasButtonGroup) {
            ((HasButtonGroup) w).setButtonGroup(this);
        }
        fireAdded(w);
    }

    public void add(Widget w, int beforeIndex) {
        children.add(beforeIndex, w);
        if (w instanceof HasButtonGroup) {
            ((HasButtonGroup) w).setButtonGroup(this);
        }
        fireAdded(w);
    }

    public boolean remove(Widget w) {
        boolean removed = children.remove(w);
        if (removed) {
            if (w instanceof HasButtonGroup) {
                ((HasButtonGroup) w).setButtonGroup(null);
            }
            fireRemoved(w);
        }
        return removed;
    }

    public Widget remove(int index) {
        if (index >= 0 && index < children.size()) {
            Widget w = children.remove(index);
            if (w instanceof HasButtonGroup) {
                ((HasButtonGroup) w).setButtonGroup(null);
            }
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
        if (w instanceof HasValueChangeHandlers) {
            childrenValueHanlders.put(w, ((HasValueChangeHandlers) w).addValueChangeHandler(new ValueChangeHandler() {
                @Override
                public void onValueChange(ValueChangeEvent event) {
                    if (Boolean.TRUE.equals(event.getNewValue())) {
                        for (int i = 0; i < children.size(); i++) {
                            Widget ch = children.get(i);
                            if (ch != w && ch instanceof HasValue) {
                                ((HasValue) ch).setValue(Boolean.FALSE);
                            }
                        }
                    }
                }
            }));
        }
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
        HandlerRegistration childReg = childrenValueHanlders.remove(w);
        if (childReg != null) {
            childReg.removeHandler();
        }
        ContainerEvent event = new ContainerEvent(this, w);
        for (RemoveHandler h : removeHandlers) {
            h.onRemove(event);
        }
    }

    private Set<SelectionHandler<Widget>> selectionHandlers = new HashSet<>();

    @Override
    public HandlerRegistration addSelectionHandler(SelectionHandler<Widget> handler) {
        selectionHandlers.add(handler);
        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                selectionHandlers.remove(handler);
            }
        };
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
                            try {
                                Utils.executeScriptEventVoid(published, componentAdded, EventsPublisher.publishContainerEvent(event));
                            } catch (Exception ex) {
                                Logger.severe(ex);
                            }
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
                            try {
                                Utils.executeScriptEventVoid(published, componentRemoved, EventsPublisher.publishContainerEvent(event));
                            } catch (Exception ex) {
                                Logger.severe(ex);
                            }
                        }
                    }
                });
            }
        }
    }

    public JavaScriptObject getOnItemSelected() {
        return onItemSelected;
    }

    private HandlerRegistration selectedReg;

    public void setOnItemSelected(JavaScriptObject aValue) {
        if (onItemSelected != aValue) {
            if (selectedReg != null) {
                selectedReg.removeHandler();
                selectedReg = null;
            }
            onItemSelected = aValue;
            if (onItemSelected != null) {
                selectedReg = addSelectionHandler(new SelectionHandler<Widget>() {

                    @Override
                    public void onSelection(SelectionEvent<Widget> event) {
                        if (onItemSelected != null) {
                            try {
                                JavaScriptObject jsItem = event.getSelectedItem() instanceof HasPublished ? ((HasPublished) event.getSelectedItem()).getPublished() : null;
                                Utils.executeScriptEventVoid(published, onItemSelected, EventsPublisher.publishItemEvent(published, jsItem));
                            } catch (Exception e) {
                                Logger.severe(e);
                            }
                        }
                    }

                });
            }
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String aValue) {
        name = aValue;
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
                publish(this, aValue);
            }
        }
    }

    private native static void publish(HasPublished aWidget, JavaScriptObject published)/*-{
		published.add = function(toAdd){
			if(toAdd && toAdd.unwrap) {
				if(toAdd.buttonGroup == published)
					throw 'A widget already added to this group';
				aWidget.@com.eas.ui.ButtonGroup::add(Lcom/eas/core/HasPublished;)(toAdd.unwrap());
			}
		}
		published.remove = function(toRemove) {
			if(toRemove && toRemove.unwrap) {
				aWidget.@com.eas.ui.ButtonGroup::remove(Lcom/eas/core/HasPublished;)(toRemove.unwrap());
			}
		}
		published.clear = function() {
			aWidget.@com.eas.ui.ButtonGroup::clear()();				
		}
		published.child = function(aIndex) {
			var comp = aWidget.@com.eas.ui.ButtonGroup::getChild(I)(aIndex);
		    return @com.eas.core.Utils::checkPublishedComponent(Ljava/lang/Object;)(comp);					
		};
		Object.defineProperty(published, "children", {
			value : function() {
				var ch = [];
				for(var i = 0; i < published.count; i++)
					ch.push(published.child(i));
				return ch;
			}
		});
		Object.defineProperty(published, "count", {
			get : function() {
				return aWidget.@com.eas.ui.ButtonGroup::size()();
			}
		});
	    Object.defineProperty(published, "name", {
		    get : function() {
		    	return aWidget.@com.eas.ui.HasJsName::getJsName()();
		    }
 	    });
		Object.defineProperty(published, "onItemSelected", {
			get : function() {
				return aWidget.@com.eas.ui.ButtonGroup::getItemSelected()();
			},
			set : function(aValue) {
				aWidget.@com.eas.ui.ButtonGroup::setItemSelected(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			},
			configurable : true
		});
	}-*/;
}
