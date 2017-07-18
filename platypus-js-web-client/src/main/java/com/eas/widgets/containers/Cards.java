package com.eas.widgets.containers;

import com.eas.core.HasPublished;
import com.eas.core.Logger;
import com.eas.core.Utils;
import com.eas.ui.CommonResources;
import com.eas.ui.EventsPublisher;
import com.eas.ui.Widget;
import com.eas.ui.events.HasSelectionHandlers;
import com.eas.ui.events.SelectionEvent;
import com.eas.ui.events.SelectionHandler;
import com.eas.ui.HasChildrenPosition;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerRegistration;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author mg
 */
public class Cards extends Container implements HasSelectionHandlers<Widget>, HasChildrenPosition {

    private final Map<String, Widget> cards = new Map();
    protected Widget visibleWidget;

    private int hgap;
    private int vgap;

    protected JavaScriptObject onItemSelected;
    /**
     * Creates an empty deck panel.
     */
    public Cards(int aVGap, int aHGap) {
        super();
        CommonResources.INSTANCE.commons().ensureInjected();
        element.getStyle().setOverflow(Style.Overflow.HIDDEN);
        element.getStyle().setPosition(Style.Position.RELATIVE);
        setHgap(aHGap);
        setVgap(aVGap);
    }

    public int getHgap() {
        return hgap;
    }

    public final void setHgap(int aValue) {
        hgap = aValue;
        for (Widget w : children) {
            Element we = w.getElement();
            we.getStyle().setMarginLeft(hgap, Style.Unit.PX);
            we.getStyle().setMarginRight(hgap, Style.Unit.PX);
        }
    }

    public int getVgap() {
        return vgap;
    }

    public final void setVgap(int aValue) {
        vgap = aValue;
        for (Widget w : children) {
            Style ws = w.getElement().getStyle();
            ws.setMarginTop(vgap, Style.Unit.PX);
            ws.setMarginBottom(vgap, Style.Unit.PX);
        }
    }

    @Override
    public void add(Widget w) {
        format(w);
        if (visibleWidget == null) {
            showWidget(w);
        }
        super.add(w);
    }

    @Override
    public void add(Widget w, int beforeIndex) {
        format(w);
        super.add(w, beforeIndex);
    }

    public void add(Widget w, String aCardName) {
        format(w);
        if (cards.has(aCardName)) {
            super.remove(cards.get(aCardName));
        }
        super.add(w);
        cards.put(aCardName, w);
    }

    @Override
    public void clear() {
        cards.clear();
        super.clear();
    }

    public Widget getVisibleWidget() {
        return visibleWidget;
    }

    @Override
    public boolean remove(Widget w) {
        removeCard(w);
        boolean removed = super.remove(w);
        if (removed && visibleWidget == w) {
            visibleWidget = null;
        }
        return removed;
    }

    private void removeCard(Widget w) {
        if (w != null) {
            for (String cardName : cards.keySet()) {
                if (cards.get(cardName) == w) {
                    cards.remove(cardName);
                    break;
                }
            }
        }
    }

    @Override
    public Widget remove(int index) {
        Widget w = super.remove(index);
        removeCard(w);
        if (visibleWidget == w) {
            visibleWidget = null;
        }
        return w;
    }

    /**
     * Shows the widget at the specified index. This causes the currently-
     * visible widget to be hidden.
     *
     * @param toBeShown the widget to be shown
     */
    public void showWidget(Widget toBeShown) {
        Widget oldWidget = visibleWidget;
        visibleWidget = toBeShown;

        if (visibleWidget != oldWidget) {
            visibleWidget.setVisible(true);
            visibleWidget.getElement().addClassName("card-shown");
            visibleWidget.getElement().removeClassName("card-hidden");

            if (oldWidget != null) {
                oldWidget.getElement().addClassName("card-hidden");
                oldWidget.getElement().removeClassName("card-shown");
                oldWidget.setVisible(false);
            }
            if (oldWidget != visibleWidget) {
                SelectionEvent<Widget> event = new SelectionEvent<>(this, visibleWidget);
                for(SelectionHandler<Widget> h: selectionHandlers){
                    h.onSelection(event);
                }
            }
        }
    }

    public void show(String aCardName) {
        if (cards.has(aCardName)) {
            Widget toShow = cards.get(aCardName);
            showWidget(toShow);
        }
    }

    public Widget getWidget(String aCardName) {
        return cards.get(aCardName);
    }

    /**
     * Setup the container around the widget.
     */
    private void format(Widget w) {
        // Set all anchors by default.
        Style ws = w.getElement().getStyle();
        ws.setPosition(Style.Position.ABSOLUTE);
        ws.clearWidth();
        ws.clearHeight();
        ws.setLeft(0, Style.Unit.PX);
        ws.setRight(0, Style.Unit.PX);
        ws.setTop(0, Style.Unit.PX);
        ws.setBottom(0, Style.Unit.PX);
        ws.setMarginLeft(hgap, Style.Unit.PX);
        ws.setMarginRight(hgap, Style.Unit.PX);
        ws.setMarginTop(vgap, Style.Unit.PX);
        ws.setMarginBottom(vgap, Style.Unit.PX);
        w.getElement().addClassName(CommonResources.INSTANCE.commons().borderSized());
        w.setVisible(false);
    }

    private final Set<SelectionHandler<Widget>> selectionHandlers = new Set();

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

    private native static void publish(HasPublished aWidget, JavaScriptObject published)/*-{
        Object.defineProperty(published, "hgap", {
            get : function(){
                return aWidget.@com.eas.widgets.CardPane::getHgap()();
            },
            set : function(aValue){
                aWidget.@com.eas.widgets.CardPane::setHgap(I)(aValue);
            }
        });
        Object.defineProperty(published, "vgap", {
            get : function(){
                return aWidget.@com.eas.widgets.CardPane::getVgap()();
            },
            set : function(aValue){
                aWidget.@com.eas.widgets.CardPane::setVgap(I)(aValue);
            }
        });
        published.add = function(toAdd, aCardName){
            if(toAdd && toAdd.unwrap){
                if(toAdd.parent == published)
                    throw 'A widget already added to this container';
                aWidget.@com.eas.widgets.CardPane::add(Lcom/google/gwt/user/client/ui/Widget;Ljava/lang/String;)(toAdd.unwrap(), aCardName);
            }
        };
        published.child = function(aIndex_or_aCardName) {
            var widget;
            if (isNaN(aIndex_or_aCardName)) {
                widget = aWidget.@com.eas.widgets.CardPane::getWidget(Ljava/lang/String;)(aIndex_or_aCardName);
            }else {
                var index = parseInt(aIndex_or_aCardName, 10);
                widget = aWidget.@com.eas.widgets.CardPane::getWidget(I)(index);
            }
            return @com.eas.core.Utils::checkPublishedComponent(Ljava/lang/Object;)(widget);
        };
        published.show = function(aCardName) {
            aWidget.@com.eas.widgets.CardPane::show(Ljava/lang/String;)(aCardName);
        };			
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
