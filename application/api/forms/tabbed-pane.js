(function() {
    var javaClass = Java.type("com.eas.client.forms.containers.TabbedPane");
    javaClass.setPublisher(function(aDelegate) {
        return new P.TabbedPane(aDelegate);
    });
    
    /**
     * A component that lets the user switch between a group of components by
     * clicking on a tab with a given title and/or icon.
     * @constructor TabbedPane TabbedPane
     */
    P.TabbedPane = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.TabbedPane.superclass)
            P.TabbedPane.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "cursor", {
            get: function() {
                var value = delegate.cursor;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.cursor = P.boxAsJava(aValue);
            }
        });
        if(!P.TabbedPane){
            /**
             * The mouse <code>Cursor</code> over this component.
             * @property cursor
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.cursor = {};
        }
        Object.defineProperty(this, "onMouseDragged", {
            get: function() {
                var value = delegate.onMouseDragged;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseDragged = P.boxAsJava(aValue);
            }
        });
        if(!P.TabbedPane){
            /**
             * Mouse dragged event handler function.
             * @property onMouseDragged
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.onMouseDragged = {};
        }
        Object.defineProperty(this, "onMouseReleased", {
            get: function() {
                var value = delegate.onMouseReleased;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseReleased = P.boxAsJava(aValue);
            }
        });
        if(!P.TabbedPane){
            /**
             * Mouse released event handler function.
             * @property onMouseReleased
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.onMouseReleased = {};
        }
        Object.defineProperty(this, "onFocusLost", {
            get: function() {
                var value = delegate.onFocusLost;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onFocusLost = P.boxAsJava(aValue);
            }
        });
        if(!P.TabbedPane){
            /**
             * Keyboard focus lost by the component event handler function.
             * @property onFocusLost
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.onFocusLost = {};
        }
        Object.defineProperty(this, "onMousePressed", {
            get: function() {
                var value = delegate.onMousePressed;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMousePressed = P.boxAsJava(aValue);
            }
        });
        if(!P.TabbedPane){
            /**
             * Mouse pressed event handler function.
             * @property onMousePressed
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.onMousePressed = {};
        }
        Object.defineProperty(this, "foreground", {
            get: function() {
                var value = delegate.foreground;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.foreground = P.boxAsJava(aValue);
            }
        });
        if(!P.TabbedPane){
            /**
             * The foreground color of this component.
             * @property foreground
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.foreground = {};
        }
        Object.defineProperty(this, "error", {
            get: function() {
                var value = delegate.error;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.error = P.boxAsJava(aValue);
            }
        });
        if(!P.TabbedPane){
            /**
             * An error message of this component.
             * Validation procedure may set this property and subsequent focus lost event will clear it.
             * @property error
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.error = '';
        }
        Object.defineProperty(this, "enabled", {
            get: function() {
                var value = delegate.enabled;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.enabled = P.boxAsJava(aValue);
            }
        });
        if(!P.TabbedPane){
            /**
             * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
             * @property enabled
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.enabled = true;
        }
        Object.defineProperty(this, "onComponentMoved", {
            get: function() {
                var value = delegate.onComponentMoved;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onComponentMoved = P.boxAsJava(aValue);
            }
        });
        if(!P.TabbedPane){
            /**
             * Component moved event handler function.
             * @property onComponentMoved
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.onComponentMoved = {};
        }
        Object.defineProperty(this, "onComponentAdded", {
            get: function() {
                var value = delegate.onComponentAdded;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onComponentAdded = P.boxAsJava(aValue);
            }
        });
        if(!P.TabbedPane){
            /**
             * Component added event hanler function.
             * @property onComponentAdded
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.onComponentAdded = {};
        }
        Object.defineProperty(this, "componentPopupMenu", {
            get: function() {
                var value = delegate.componentPopupMenu;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.componentPopupMenu = P.boxAsJava(aValue);
            }
        });
        if(!P.TabbedPane){
            /**
             * <code>PopupMenu</code> that assigned for this component.
             * @property componentPopupMenu
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.componentPopupMenu = {};
        }
        Object.defineProperty(this, "top", {
            get: function() {
                var value = delegate.top;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.top = P.boxAsJava(aValue);
            }
        });
        if(!P.TabbedPane){
            /**
             * Vertical coordinate of the component.
             * @property top
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.top = 0;
        }
        Object.defineProperty(this, "children", {
            get: function() {
                if (!invalidatable) {
                    var value = delegate.children;
                    invalidatable = P.boxAsJs(value);
                }
                return invalidatable;
            }
        });
        if(!P.TabbedPane){
            /**
             * Gets the container's children components.
             * @property children
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.children = [];
        }
        Object.defineProperty(this, "onComponentResized", {
            get: function() {
                var value = delegate.onComponentResized;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onComponentResized = P.boxAsJava(aValue);
            }
        });
        if(!P.TabbedPane){
            /**
             * Component resized event handler function.
             * @property onComponentResized
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.onComponentResized = {};
        }
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parentWidget;
                return P.boxAsJs(value);
            }
        });
        if(!P.TabbedPane){
            /**
             * Parent container of this widget.
             * @property parentWidget
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.parent = {};
        }
        Object.defineProperty(this, "onMouseEntered", {
            get: function() {
                var value = delegate.onMouseEntered;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseEntered = P.boxAsJava(aValue);
            }
        });
        if(!P.TabbedPane){
            /**
             * Mouse entered over the component event handler function.
             * @property onMouseEntered
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.onMouseEntered = {};
        }
        Object.defineProperty(this, "toolTipText", {
            get: function() {
                var value = delegate.toolTipText;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.toolTipText = P.boxAsJava(aValue);
            }
        });
        if(!P.TabbedPane){
            /**
             * The tooltip string that has been set with.
             * @property toolTipText
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.toolTipText = '';
        }
        Object.defineProperty(this, "height", {
            get: function() {
                var value = delegate.height;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.height = P.boxAsJava(aValue);
            }
        });
        if(!P.TabbedPane){
            /**
             * Height of the component.
             * @property height
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.height = 0;
        }
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });
        if(!P.TabbedPane){
            /**
             * Native API. Returns low level html element. Applicable only in HTML5 client.
             * @property element
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.element = {};
        }
        Object.defineProperty(this, "onComponentShown", {
            get: function() {
                var value = delegate.onComponentShown;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onComponentShown = P.boxAsJava(aValue);
            }
        });
        if(!P.TabbedPane){
            /**
             * Component shown event handler function.
             * @property onComponentShown
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.onComponentShown = {};
        }
        Object.defineProperty(this, "selectedComponent", {
            get: function() {
                var value = delegate.selectedComponent;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.selectedComponent = P.boxAsJava(aValue);
            }
        });
        if(!P.TabbedPane){
            /**
             * The selected component.
             * @property selectedComponent
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.selectedComponent = {};
        }
        Object.defineProperty(this, "onMouseMoved", {
            get: function() {
                var value = delegate.onMouseMoved;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseMoved = P.boxAsJava(aValue);
            }
        });
        if(!P.TabbedPane){
            /**
             * Mouse moved event handler function.
             * @property onMouseMoved
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.onMouseMoved = {};
        }
        Object.defineProperty(this, "opaque", {
            get: function() {
                var value = delegate.opaque;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.opaque = P.boxAsJava(aValue);
            }
        });
        if(!P.TabbedPane){
            /**
             * True if this component is completely opaque.
             * @property opaque
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.opaque = true;
        }
        Object.defineProperty(this, "visible", {
            get: function() {
                var value = delegate.visible;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.visible = P.boxAsJava(aValue);
            }
        });
        if(!P.TabbedPane){
            /**
             * Determines whether this component should be visible when its parent is visible.
             * @property visible
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.visible = true;
        }
        Object.defineProperty(this, "onComponentHidden", {
            get: function() {
                var value = delegate.onComponentHidden;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onComponentHidden = P.boxAsJava(aValue);
            }
        });
        if(!P.TabbedPane){
            /**
             * Component hidden event handler function.
             * @property onComponentHidden
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.onComponentHidden = {};
        }
        Object.defineProperty(this, "nextFocusableComponent", {
            get: function() {
                var value = delegate.nextFocusableComponent;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.nextFocusableComponent = P.boxAsJava(aValue);
            }
        });
        if(!P.TabbedPane){
            /**
             * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
             * @property nextFocusableComponent
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.nextFocusableComponent = {};
        }
        Object.defineProperty(this, "onKeyReleased", {
            get: function() {
                var value = delegate.onKeyReleased;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onKeyReleased = P.boxAsJava(aValue);
            }
        });
        if(!P.TabbedPane){
            /**
             * Key released event handler function.
             * @property onKeyReleased
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.onKeyReleased = {};
        }
        Object.defineProperty(this, "count", {
            get: function() {
                var value = delegate.count;
                return P.boxAsJs(value);
            }
        });
        if(!P.TabbedPane){
            /**
             * Gets the number of components in this panel.
             * @property count
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.count = 0;
        }
        Object.defineProperty(this, "onActionPerformed", {
            get: function() {
                var value = delegate.onActionPerformed;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onActionPerformed = P.boxAsJava(aValue);
            }
        });
        if(!P.TabbedPane){
            /**
             * Main action performed event handler function.
             * @property onActionPerformed
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.onActionPerformed = {};
        }
        Object.defineProperty(this, "focusable", {
            get: function() {
                var value = delegate.focusable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.focusable = P.boxAsJava(aValue);
            }
        });
        if(!P.TabbedPane){
            /**
             * Determines whether this component may be focused.
             * @property focusable
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.focusable = true;
        }
        Object.defineProperty(this, "onKeyTyped", {
            get: function() {
                var value = delegate.onKeyTyped;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onKeyTyped = P.boxAsJava(aValue);
            }
        });
        if(!P.TabbedPane){
            /**
             * Key typed event handler function.
             * @property onKeyTyped
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.onKeyTyped = {};
        }
        Object.defineProperty(this, "onMouseWheelMoved", {
            get: function() {
                var value = delegate.onMouseWheelMoved;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseWheelMoved = P.boxAsJava(aValue);
            }
        });
        if(!P.TabbedPane){
            /**
             * Mouse wheel moved event handler function.
             * @property onMouseWheelMoved
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.onMouseWheelMoved = {};
        }
        Object.defineProperty(this, "selectedIndex", {
            get: function() {
                var value = delegate.selectedIndex;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.selectedIndex = P.boxAsJava(aValue);
            }
        });
        if(!P.TabbedPane){
            /**
             * The selected component's index.
             * @property selectedIndex
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.selectedIndex = 0;
        }
        Object.defineProperty(this, "onComponentRemoved", {
            get: function() {
                var value = delegate.onComponentRemoved;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onComponentRemoved = P.boxAsJava(aValue);
            }
        });
        if(!P.TabbedPane){
            /**
             * Component removed event handler function.
             * @property onComponentRemoved
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.onComponentRemoved = {};
        }
        Object.defineProperty(this, "onItemSelected", {
            get: function() {
                var value = delegate.onItemSelected;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onItemSelected = P.boxAsJava(aValue);
            }
        });
        if(!P.TabbedPane){
            /**
             * Event that is fired when one of the components is selected in this tabbed pane.
             * @property onItemSelected
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.onItemSelected = {};
        }
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });
        if(!P.TabbedPane){
            /**
             * Native API. Returns low level swing component. Applicable only in J2SE swing client.
             * @property component
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.component = {};
        }
        Object.defineProperty(this, "onFocusGained", {
            get: function() {
                var value = delegate.onFocusGained;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onFocusGained = P.boxAsJava(aValue);
            }
        });
        if(!P.TabbedPane){
            /**
             * Keyboard focus gained by the component event.
             * @property onFocusGained
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.onFocusGained = {};
        }
        Object.defineProperty(this, "left", {
            get: function() {
                var value = delegate.left;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.left = P.boxAsJava(aValue);
            }
        });
        if(!P.TabbedPane){
            /**
             * Horizontal coordinate of the component.
             * @property left
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.left = 0;
        }
        Object.defineProperty(this, "background", {
            get: function() {
                var value = delegate.background;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.background = P.boxAsJava(aValue);
            }
        });
        if(!P.TabbedPane){
            /**
             * The background color of this component.
             * @property background
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.background = {};
        }
        Object.defineProperty(this, "onMouseClicked", {
            get: function() {
                var value = delegate.onMouseClicked;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseClicked = P.boxAsJava(aValue);
            }
        });
        if(!P.TabbedPane){
            /**
             * Mouse clicked event handler function.
             * @property onMouseClicked
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.onMouseClicked = {};
        }
        Object.defineProperty(this, "onMouseExited", {
            get: function() {
                var value = delegate.onMouseExited;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseExited = P.boxAsJava(aValue);
            }
        });
        if(!P.TabbedPane){
            /**
             * Mouse exited over the component event handler function.
             * @property onMouseExited
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.onMouseExited = {};
        }
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.TabbedPane){
            /**
             * Gets name of this component.
             * @property name
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.name = '';
        }
        Object.defineProperty(this, "width", {
            get: function() {
                var value = delegate.width;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.width = P.boxAsJava(aValue);
            }
        });
        if(!P.TabbedPane){
            /**
             * Width of the component.
             * @property width
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.width = 0;
        }
        Object.defineProperty(this, "font", {
            get: function() {
                var value = delegate.font;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.font = P.boxAsJava(aValue);
            }
        });
        if(!P.TabbedPane){
            /**
             * The font of this component.
             * @property font
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.font = {};
        }
        Object.defineProperty(this, "onKeyPressed", {
            get: function() {
                var value = delegate.onKeyPressed;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onKeyPressed = P.boxAsJava(aValue);
            }
        });
        if(!P.TabbedPane){
            /**
             * Key pressed event handler function.
             * @property onKeyPressed
             * @memberOf TabbedPane
             */
            P.TabbedPane.prototype.onKeyPressed = {};
        }
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    };
        /**
         * Appends the component whith specified text to the end of this container.
         * @param component the component to add.
         * @param text the text for the tab.
         * @param icon the icon for the tab (optional).
         * @method add
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.add = function(component, text, icon) {
            var delegate = this.unwrap();
            var value = delegate.add(P.boxAsJava(component), P.boxAsJava(text), P.boxAsJava(icon));
            return P.boxAsJs(value);
        };

        /**
         * Removes the specified component from this container.
         * @param component the component to remove
         * @method remove
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.remove = function(component) {
            var delegate = this.unwrap();
            var value = delegate.remove(P.boxAsJava(component));
            return P.boxAsJs(value);
        };

        /**
         * Removes all the components from this container.
         * @method clear
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.clear = function() {
            var delegate = this.unwrap();
            var value = delegate.clear();
            return P.boxAsJs(value);
        };

        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.focus = function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        };

        /**
         * Gets the container's n-th component.
         * @param index the component's index in the container
         * @return the child component
         * @method child
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.child = function(index) {
            var delegate = this.unwrap();
            var value = delegate.child(P.boxAsJava(index));
            return P.boxAsJs(value);
        };

})();