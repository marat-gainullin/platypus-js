package com.eas.widgets.containers;

import com.eas.core.HasPublished;
import com.eas.core.Logger;
import com.eas.core.Utils;
import com.eas.ui.EventsPublisher;
import com.eas.ui.HasChildrenPosition;
import com.eas.ui.Widget;
import com.eas.ui.events.ContainerEvent;
import com.eas.ui.events.AddHandler;
import com.eas.ui.events.HasSelectionHandlers;
import com.eas.ui.events.RemoveHandler;
import com.eas.ui.events.SelectionEvent;
import com.eas.ui.events.SelectionHandler;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HandlerRegistration;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mgainullin
 */
public class Tabs extends Borders implements HasSelectionHandlers<Widget>, HasChildrenPosition {

    private class TabCaption extends Widget {

        public TabCaption(String title, String image, String toolTip) {
            super();
        }

        @Override
        protected void publish(JavaScriptObject aValue) {
            // noop since js api for this widget is absent.
        }

    }

    private final Toolbar tabs = new Toolbar();
    private final Cards content = new Cards(0, 0);
    private final Map<Widget, Widget> tabsOf = new HashMap<>();
    protected JavaScriptObject onItemSelected;

    public Tabs() {
        super();
        setTopComponent(tabs, 30); // TODO: make content driven markup possible
        setCenterComponent(content);
    }

    private void addCaptionFor(Widget w, String title, String toolTip, String image, int beforeIndex) {
        if (title == null) {
            title = w.getName() != null && !w.getName().isEmpty() ? w.getName() : "Unnamed - " + tabs.getCount();
        }
        TabCaption label = new TabCaption(title, image, toolTip);
        tabs.add(label, beforeIndex);
        tabsOf.put(w, label);
    }

    @Override
    public void add(Widget w) {
        content.add(w);
        w.setParent(this);
        addCaptionFor(w, null, null, null, tabs.getCount());
    }

    @Override
    public void add(Widget w, int beforeIndex) {
        content.add(w, beforeIndex);
        w.setParent(this);
        addCaptionFor(w, null, null, null, beforeIndex);
    }

    public void add(Widget w, String title, String image) {
        content.add(w);
        w.setParent(this);
        addCaptionFor(w, title, null, image, tabs.getCount());
    }

    public void add(Widget w, String title, String toolTip, String image) {
        content.add(w);
        w.setParent(this);
        addCaptionFor(w, title, toolTip, image, tabs.getCount());
    }

    public void add(Widget w, int beforeIndex, String title, String image) {
        content.add(w, beforeIndex);
        w.setParent(this);
        addCaptionFor(w, title, null, image, beforeIndex);
    }

    @Override
    public void add(Widget w, int aPlace, int aSize) {
        // no op since this is not pure Borders.
    }

    @Override
    public boolean remove(Widget w) {
        boolean removed = content.remove(w);
        tabs.remove(tabsOf.get(w));
        return removed;
    }

    @Override
    public Widget remove(int index) {
        Widget removed = content.remove(index);
        tabs.remove(tabsOf.get(removed));
        return removed;
    }

    @Override
    public HandlerRegistration addAddHandler(AddHandler handler) {
        return super.addAddHandler(new AddHandler() {
            @Override
            public void onAdd(ContainerEvent anEvent) {
                anEvent.getWidget().setParent(Tabs.this);
                anEvent.setSource(Tabs.this);
                handler.onAdd(anEvent);
            }

        });
    }

    @Override
    public HandlerRegistration addRemoveHandler(RemoveHandler handler) {
        return super.addRemoveHandler(new RemoveHandler() {
            @Override
            public void onRemove(ContainerEvent anEvent) {
                anEvent.setSource(Tabs.this);
                handler.onRemove(anEvent);
            }

        });
    }

    @Override
    public HandlerRegistration addSelectionHandler(SelectionHandler<Widget> handler) {
        return content.addSelectionHandler(new SelectionHandler<Widget>() {
            @Override
            public void onSelection(SelectionEvent<Widget> event) {
                event.setSource(Tabs.this);
                handler.onSelection(event);
            }

        });
    }
    
    public Widget getSelectedComponent(){
        return content.getVisibleWidget();
    }
    
    public int getSelectedIndex(){
        Widget w = content.getVisibleWidget();
        return content.indexOf(w);
    }
    
    @Override
    public int getTop(Widget aWidget) {
        assert aWidget.getParent() == this : "widget should be a child of this container";
        return tabs.getElement().getOffsetTop();
    }

    @Override
    public int getLeft(Widget aWidget) {
        assert aWidget.getParent() == this : "widget should be a child of this container";
        return 0;
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
    protected void publish(JavaScriptObject aValue) {
        publish(this, aValue);
    }

    private native static void publish(HasPublished aWidget, JavaScriptObject published)/*-{
        Object.defineProperty(published, "selectedIndex", {
                get : function() {
                    return aWidget.@com.eas.widgets.TabbedPane::getSelectedIndex()();
                },
                set : function(aValue) {
                    aWidget.@com.eas.widgets.TabbedPane::setSelectedIndex(I)(aValue);
                }
        });
        Object.defineProperty(published, "selectedComponent", {
                get : function() {
                    var comp = aWidget.@com.eas.widgets.TabbedPane::getSelected()();
                    return @com.eas.core.Utils::checkPublishedComponent(Ljava/lang/Object;)(comp);
                },
                set : function(aValue) {
                    if(aValue != null)
                        aWidget.@com.eas.widgets.TabbedPane::setSelected(Lcom/google/gwt/user/client/ui/Widget;)(aValue.unwrap());
                }
        });
        published.add = function(toAdd, aTabTitle, aTabIcon){
            if(toAdd && toAdd.unwrap){
                if(toAdd.parent == published)
                    throw 'A widget already added to this container';
                if(!aTabTitle)
                    aTabTitle = "";
                if(!aTabIcon)
                    aTabIcon = null;
                if(aTabTitle.indexOf("<html>") == 0)
                    aWidget.@com.eas.widgets.TabbedPane::add(Lcom/google/gwt/user/client/ui/Widget;Ljava/lang/String;ZLcom/google/gwt/resources/client/ImageResource;)(toAdd.unwrap(), aTabTitle.substring(6), true, aTabIcon);
                else
                    aWidget.@com.eas.widgets.TabbedPane::add(Lcom/google/gwt/user/client/ui/Widget;Ljava/lang/String;ZLcom/google/gwt/resources/client/ImageResource;)(toAdd.unwrap(), aTabTitle, false, aTabIcon);
            }
        };
    }-*/;

}
