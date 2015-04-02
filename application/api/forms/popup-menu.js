(function() {
    var javaClass = Java.type("com.eas.client.forms.menu.PopupMenu");
    javaClass.setPublisher(function(aDelegate) {
        return new P.PopupMenu(aDelegate);
    });
    
    /**
     * An implementation of a popup menu -- a small window that pops up and displays a series of choices.
     * @constructor PopupMenu PopupMenu
     */
    P.PopupMenu = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.PopupMenu.superclass)
            P.PopupMenu.superclass.constructor.apply(this, arguments);
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
        if(!P.PopupMenu){
            /**
             * The mouse <code>Cursor</code> over this component.
             * @property cursor
             * @memberOf PopupMenu
             */
            P.PopupMenu.prototype.cursor = {};
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
        if(!P.PopupMenu){
            /**
             * Mouse dragged event handler function.
             * @property onMouseDragged
             * @memberOf PopupMenu
             */
            P.PopupMenu.prototype.onMouseDragged = {};
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
        if(!P.PopupMenu){
            /**
             * Mouse released event handler function.
             * @property onMouseReleased
             * @memberOf PopupMenu
             */
            P.PopupMenu.prototype.onMouseReleased = {};
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
        if(!P.PopupMenu){
            /**
             * Keyboard focus lost by the component event handler function.
             * @property onFocusLost
             * @memberOf PopupMenu
             */
            P.PopupMenu.prototype.onFocusLost = {};
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
        if(!P.PopupMenu){
            /**
             * Mouse pressed event handler function.
             * @property onMousePressed
             * @memberOf PopupMenu
             */
            P.PopupMenu.prototype.onMousePressed = {};
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
        if(!P.PopupMenu){
            /**
             * The foreground color of this component.
             * @property foreground
             * @memberOf PopupMenu
             */
            P.PopupMenu.prototype.foreground = {};
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
        if(!P.PopupMenu){
            /**
             * An error message of this component.
             * Validation procedure may set this property and subsequent focus lost event will clear it.
             * @property error
             * @memberOf PopupMenu
             */
            P.PopupMenu.prototype.error = '';
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
        if(!P.PopupMenu){
            /**
             * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
             * @property enabled
             * @memberOf PopupMenu
             */
            P.PopupMenu.prototype.enabled = true;
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
        if(!P.PopupMenu){
            /**
             * Component moved event handler function.
             * @property onComponentMoved
             * @memberOf PopupMenu
             */
            P.PopupMenu.prototype.onComponentMoved = {};
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
        if(!P.PopupMenu){
            /**
             * Component added event hanler function.
             * @property onComponentAdded
             * @memberOf PopupMenu
             */
            P.PopupMenu.prototype.onComponentAdded = {};
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
        if(!P.PopupMenu){
            /**
             * <code>PopupMenu</code> that assigned for this component.
             * @property componentPopupMenu
             * @memberOf PopupMenu
             */
            P.PopupMenu.prototype.componentPopupMenu = {};
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
        if(!P.PopupMenu){
            /**
             * Vertical coordinate of the component.
             * @property top
             * @memberOf PopupMenu
             */
            P.PopupMenu.prototype.top = 0;
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
        if(!P.PopupMenu){
            /**
             * Gets the container's children components.
             * @property children
             * @memberOf PopupMenu
             */
            P.PopupMenu.prototype.children = [];
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
        if(!P.PopupMenu){
            /**
             * Component resized event handler function.
             * @property onComponentResized
             * @memberOf PopupMenu
             */
            P.PopupMenu.prototype.onComponentResized = {};
        }
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parentWidget;
                return P.boxAsJs(value);
            }
        });
        if(!P.PopupMenu){
            /**
             * Parent container of this widget.
             * @property parentWidget
             * @memberOf PopupMenu
             */
            P.PopupMenu.prototype.parent = {};
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
        if(!P.PopupMenu){
            /**
             * Mouse entered over the component event handler function.
             * @property onMouseEntered
             * @memberOf PopupMenu
             */
            P.PopupMenu.prototype.onMouseEntered = {};
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
        if(!P.PopupMenu){
            /**
             * The tooltip string that has been set with.
             * @property toolTipText
             * @memberOf PopupMenu
             */
            P.PopupMenu.prototype.toolTipText = '';
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
        if(!P.PopupMenu){
            /**
             * Height of the component.
             * @property height
             * @memberOf PopupMenu
             */
            P.PopupMenu.prototype.height = 0;
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
        if(!P.PopupMenu){
            /**
             * Component shown event handler function.
             * @property onComponentShown
             * @memberOf PopupMenu
             */
            P.PopupMenu.prototype.onComponentShown = {};
        }
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });
        if(!P.PopupMenu){
            /**
             * Native API. Returns low level html element. Applicable only in HTML5 client.
             * @property element
             * @memberOf PopupMenu
             */
            P.PopupMenu.prototype.element = {};
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
        if(!P.PopupMenu){
            /**
             * Mouse moved event handler function.
             * @property onMouseMoved
             * @memberOf PopupMenu
             */
            P.PopupMenu.prototype.onMouseMoved = {};
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
        if(!P.PopupMenu){
            /**
             * True if this component is completely opaque.
             * @property opaque
             * @memberOf PopupMenu
             */
            P.PopupMenu.prototype.opaque = true;
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
        if(!P.PopupMenu){
            /**
             * Determines whether this component should be visible when its parent is visible.
             * @property visible
             * @memberOf PopupMenu
             */
            P.PopupMenu.prototype.visible = true;
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
        if(!P.PopupMenu){
            /**
             * Component hidden event handler function.
             * @property onComponentHidden
             * @memberOf PopupMenu
             */
            P.PopupMenu.prototype.onComponentHidden = {};
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
        if(!P.PopupMenu){
            /**
             * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
             * @property nextFocusableComponent
             * @memberOf PopupMenu
             */
            P.PopupMenu.prototype.nextFocusableComponent = {};
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
        if(!P.PopupMenu){
            /**
             * Main action performed event handler function.
             * @property onActionPerformed
             * @memberOf PopupMenu
             */
            P.PopupMenu.prototype.onActionPerformed = {};
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
        if(!P.PopupMenu){
            /**
             * Key released event handler function.
             * @property onKeyReleased
             * @memberOf PopupMenu
             */
            P.PopupMenu.prototype.onKeyReleased = {};
        }
        Object.defineProperty(this, "count", {
            get: function() {
                var value = delegate.count;
                return P.boxAsJs(value);
            }
        });
        if(!P.PopupMenu){
            /**
             * Gets the number of components in this panel.
             * @property count
             * @memberOf PopupMenu
             */
            P.PopupMenu.prototype.count = 0;
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
        if(!P.PopupMenu){
            /**
             * Determines whether this component may be focused.
             * @property focusable
             * @memberOf PopupMenu
             */
            P.PopupMenu.prototype.focusable = true;
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
        if(!P.PopupMenu){
            /**
             * Key typed event handler function.
             * @property onKeyTyped
             * @memberOf PopupMenu
             */
            P.PopupMenu.prototype.onKeyTyped = {};
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
        if(!P.PopupMenu){
            /**
             * Mouse wheel moved event handler function.
             * @property onMouseWheelMoved
             * @memberOf PopupMenu
             */
            P.PopupMenu.prototype.onMouseWheelMoved = {};
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
        if(!P.PopupMenu){
            /**
             * Component removed event handler function.
             * @property onComponentRemoved
             * @memberOf PopupMenu
             */
            P.PopupMenu.prototype.onComponentRemoved = {};
        }
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });
        if(!P.PopupMenu){
            /**
             * Native API. Returns low level swing component. Applicable only in J2SE swing client.
             * @property component
             * @memberOf PopupMenu
             */
            P.PopupMenu.prototype.component = {};
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
        if(!P.PopupMenu){
            /**
             * Keyboard focus gained by the component event.
             * @property onFocusGained
             * @memberOf PopupMenu
             */
            P.PopupMenu.prototype.onFocusGained = {};
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
        if(!P.PopupMenu){
            /**
             * Horizontal coordinate of the component.
             * @property left
             * @memberOf PopupMenu
             */
            P.PopupMenu.prototype.left = 0;
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
        if(!P.PopupMenu){
            /**
             * The background color of this component.
             * @property background
             * @memberOf PopupMenu
             */
            P.PopupMenu.prototype.background = {};
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
        if(!P.PopupMenu){
            /**
             * Mouse clicked event handler function.
             * @property onMouseClicked
             * @memberOf PopupMenu
             */
            P.PopupMenu.prototype.onMouseClicked = {};
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
        if(!P.PopupMenu){
            /**
             * Mouse exited over the component event handler function.
             * @property onMouseExited
             * @memberOf PopupMenu
             */
            P.PopupMenu.prototype.onMouseExited = {};
        }
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.PopupMenu){
            /**
             * Gets name of this component.
             * @property name
             * @memberOf PopupMenu
             */
            P.PopupMenu.prototype.name = '';
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
        if(!P.PopupMenu){
            /**
             * Width of the component.
             * @property width
             * @memberOf PopupMenu
             */
            P.PopupMenu.prototype.width = 0;
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
        if(!P.PopupMenu){
            /**
             * The font of this component.
             * @property font
             * @memberOf PopupMenu
             */
            P.PopupMenu.prototype.font = {};
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
        if(!P.PopupMenu){
            /**
             * Key pressed event handler function.
             * @property onKeyPressed
             * @memberOf PopupMenu
             */
            P.PopupMenu.prototype.onKeyPressed = {};
        }
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    };
        /**
         * Adds the item to the menu.
         * @param menu the menu component to add.
         * @method add
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.add = function(menu) {
            var delegate = this.unwrap();
            var value = delegate.add(P.boxAsJava(menu));
            return P.boxAsJs(value);
        };

        /**
         * Removes the specified component from this container.
         * @param component the component to remove
         * @method remove
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.remove = function(component) {
            var delegate = this.unwrap();
            var value = delegate.remove(P.boxAsJava(component));
            return P.boxAsJs(value);
        };

        /**
         * Removes all the components from this container.
         * @method clear
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.clear = function() {
            var delegate = this.unwrap();
            var value = delegate.clear();
            return P.boxAsJs(value);
        };

        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.focus = function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        };

        /**
         * Gets the container's n-th component.
         * @param index the component's index in the container
         * @return the child component
         * @method child
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.child = function(index) {
            var delegate = this.unwrap();
            var value = delegate.child(P.boxAsJava(index));
            return P.boxAsJs(value);
        };

})();