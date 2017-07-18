define(['./item-event'], function(SelectionEvent){
function Cards(vgap, hgap) {
    Container.call(this);
    
    var self = this;
    
    element.style.overflow = 'hidden';
    element.style.position = 'relative';

    var cards = new Map();
    
    var visibleWidget;

    Object.defineProperty(this, 'hgap', {
        get: function(){
            return hgap;
        },
        set: function(aValue){
            hgap = aValue;
            children.forEach(function(w) {
                var we = w.element;
                we.style.marginLeft = hgap + 'px';
                we.style.marginRight = hgap + 'px';
            });
        }
    });
    
    Object.defineProperty(this, 'vgap', {
        get: function(){
            return vgap;
        },
        set: function(aValue){
            vgap = aValue;
            children.forEach(function(w) {
                var we = w.element;
                we.style.marginTop = vgap + 'px';
                we.style.marginBottom = vgap + 'px';
            });
        }
    });
    
    function add(w) {
        format(w);
        if (!visibleWidget) {
            showWidget(w);
        }
        super.add(w);
    }

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
    function showWidget(toBeShown) {
        var oldWidget = visibleWidget;
        visibleWidget = toBeShown;

        if (visibleWidget != oldWidget) {
            visibleWidget.visible = true;
            visibleWidget.element.addClassName("card-shown");
            visibleWidget.element.removeClassName("card-hidden");

            if (oldWidget != null) {
                oldWidget.element.addClassName("card-hidden");
                oldWidget.element.removeClassName("card-shown");
                oldWidget.visible = false;
            }
            if (oldWidget != visibleWidget) {
                var event = new SelectionEvent(self, visibleWidget);
                selectionHandlers.forEach(function(h){
                    h(event);
                });
            }
        }
    }

    function show(aCardName) {
        if (cards.has(aCardName)) {
            var toShow = cards.get(aCardName);
            showWidget(toShow);
        }
    }

    function getWidget(aCardName) {
        return cards.get(aCardName);
    }

    /**
     * Setup the container around the widget.
     */
    function format(w) {
        // Set all anchors by default.
        var ws = w.element.style;
        ws.position = 'absolute';
        ws.width= '';
        ws.height = '';
        ws.left = '0px';
        ws.right = '0px';
        ws.top = '0px';
        ws.bottom = '0px';
        ws.marginLeft = hgap + 'px';
        ws.marginRight = hgap + 'px';
        ws.marginTop = vgap + 'px';
        ws.marginBottom = vgap + 'px';
        w.visible = false;
    }

    var selectionHandlers = new Set();

    function addSelectionHandler(handler) {
        selectionHandlers.add(handler);
        return {
            removeHandler: function() {
                selectionHandlers.delete(handler);
            }
        };
    }

    var onItemSelected;
    var selectedReg;
    Object.defineProperty(this, 'onItemSelected', {
        get: function(){
            return onItemSelected;
        },
        set: function(aValue){
            if (onItemSelected != aValue) {
                if (selectedReg) {
                    selectedReg.removeHandler();
                    selectedReg = null;
                }
                onItemSelected = aValue;
                if (onItemSelected) {
                    selectedReg = addSelectionHandler(function(event) {
                            if (onItemSelected) {
                                onItemSelected(event);
                            }
                    });
                }
            }
        }
    });

    private native static void publish(HasPublished aWidget, JavaScriptObject published)/*-{
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
});