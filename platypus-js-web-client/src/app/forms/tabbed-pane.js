define([
    '../extend',
    './border-pane',
    './tool-bar',
    './card-pane'], function (
        extend,
        BorderPane,
        Toolbar,
        Cards) {
    function TabbedPane() {
        BorderPane.call(this);

        var self = this;

        var tabs = new Toolbar();
        var content = new Cards(0, 0);
        var tabsOf = new Map();
        var onItemSelected;

        this.topComponent = tabs;
        this.centerComponent = content;

        function addCaptionFor(w, title, image, toolTip, beforeIndex) {
            if (!title) {
                title = w.name ? w.name : "Unnamed - " + tabs.getCount();
            }
            var label = new TabCaption(title, image, toolTip);
            tabs.add(label, beforeIndex);
            tabsOf.put(w, label);
        }

        // TODO: Add <html> prefix in tab title feature 
        function add(w, title, image, tooltip) {
            content.add(w);
            w.parent = self;
            addCaptionFor(w, arguments.length < 2 ? null : title, arguments.length < 3 ? null : image, arguments.length < 4 ? '' : tooltip, tabs.count);
        }
        Object.defineProperty(this, 'add', {
            get: function () {
                return add;
            }
        });

        function remove(widgetOrIndex) {
            var removed = content.remove(widgetOrIndex);
            if (removed)
                tabs.remove(tabsOf.get(removed));
            return removed;
        }
        Object.defineProperty(this, 'remove', {
            get: function () {
                return remove;
            }
        });

        function addAddHandler(handler) {
            return content.addAddHandler(function (anEvent) {
                anEvent.source = self;
                handler(anEvent);
            });
        }
        Object.defineProperty(this, 'addAddHandler', {
            get: function () {
                return addAddHandler;
            }
        });

        function addRemoveHandler(handler) {
            return content.addRemoveHandler(function (anEvent) {
                anEvent.source = self;
                handler(anEvent);
            });
        }
        Object.defineProperty(this, 'addRemoveHandler', {
            get: function () {
                return addRemoveHandler;
            }
        });

        function addSelectionHandler(handler) {
            return content.addSelectionHandler(function (event) {
                event.source = self;
                handler(event);
            });
        }
        Object.defineProperty(this, 'addSelectionHandler', {
            get: function () {
                return addSelectionHandler;
            }
        });

        Object.defineProperty(this, 'selectedComponent', {
            get: function () {
                return content.visibleWidget;
            }
        });

        Object.defineProperty(this, 'selectedIndex', {
            get: function () {
                if (content.visibleWidget)
                    return content.indexOf(content.visibleWidget);
                else
                    return -1;
            }
        });

        var selectedReg;
        Object.defineProperty(this, 'onItemSelected', {
            get: function () {
                return onItemSelected;
            },
            set: function (aValue) {
                if (onItemSelected !== aValue) {
                    if (selectedReg) {
                        selectedReg.removeHandler();
                        selectedReg = null;
                    }
                    onItemSelected = aValue;
                    if (onItemSelected) {
                        selectedReg = addSelectionHandler(function (event) {
                            if (onItemSelected) {
                                onItemSelected(event);
                            }
                        });
                    }
                }

            }
        });
    }
    extend(TabbedPane, BorderPane);
    return TabbedPane;
});