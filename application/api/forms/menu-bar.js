(function() {
    var className = "com.eas.client.forms.menu.MenuBar";
    var javaClass = Java.type(className);
    var space = this['-platypus-scripts-space'];
    space.putPublisher(className, function(aDelegate) {
        return new P.MenuBar(aDelegate);
    });
    
    /**
     * An implementation of a menu bar.
     * @constructor MenuBar MenuBar
     */
    P.MenuBar = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            configurable: true,
            value: function() {
                return delegate;
            }
        });
        if(P.MenuBar.superclass)
            P.MenuBar.superclass.constructor.apply(this, arguments);
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
        if(!P.MenuBar){
            /**
             * The mouse <code>Cursor</code> over this component.
             * @property cursor
             * @memberOf MenuBar
             */
            P.MenuBar.prototype.cursor = {};
        }
        Object.defineProperty(this, "onMouseDragged", {
            get: function() {
                var value = delegate.onMouseDragged;
                return value;
            },
            set: function(aValue) {
                delegate.onMouseDragged = aValue;
            }
        });
        if(!P.MenuBar){
            /**
             * Mouse dragged event handler function.
             * @property onMouseDragged
             * @memberOf MenuBar
             */
            P.MenuBar.prototype.onMouseDragged = {};
        }
        Object.defineProperty(this, "onMouseReleased", {
            get: function() {
                var value = delegate.onMouseReleased;
                return value;
            },
            set: function(aValue) {
                delegate.onMouseReleased = aValue;
            }
        });
        if(!P.MenuBar){
            /**
             * Mouse released event handler function.
             * @property onMouseReleased
             * @memberOf MenuBar
             */
            P.MenuBar.prototype.onMouseReleased = {};
        }
        Object.defineProperty(this, "onFocusLost", {
            get: function() {
                var value = delegate.onFocusLost;
                return value;
            },
            set: function(aValue) {
                delegate.onFocusLost = aValue;
            }
        });
        if(!P.MenuBar){
            /**
             * Keyboard focus lost by the component event handler function.
             * @property onFocusLost
             * @memberOf MenuBar
             */
            P.MenuBar.prototype.onFocusLost = {};
        }
        Object.defineProperty(this, "onMousePressed", {
            get: function() {
                var value = delegate.onMousePressed;
                return value;
            },
            set: function(aValue) {
                delegate.onMousePressed = aValue;
            }
        });
        if(!P.MenuBar){
            /**
             * Mouse pressed event handler function.
             * @property onMousePressed
             * @memberOf MenuBar
             */
            P.MenuBar.prototype.onMousePressed = {};
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
        if(!P.MenuBar){
            /**
             * The foreground color of this component.
             * @property foreground
             * @memberOf MenuBar
             */
            P.MenuBar.prototype.foreground = {};
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
        if(!P.MenuBar){
            /**
             * An error message of this component.
             * Validation procedure may set this property and subsequent focus lost event will clear it.
             * @property error
             * @memberOf MenuBar
             */
            P.MenuBar.prototype.error = '';
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
        if(!P.MenuBar){
            /**
             * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
             * @property enabled
             * @memberOf MenuBar
             */
            P.MenuBar.prototype.enabled = true;
        }
        Object.defineProperty(this, "onComponentMoved", {
            get: function() {
                var value = delegate.onComponentMoved;
                return value;
            },
            set: function(aValue) {
                delegate.onComponentMoved = aValue;
            }
        });
        if(!P.MenuBar){
            /**
             * Component moved event handler function.
             * @property onComponentMoved
             * @memberOf MenuBar
             */
            P.MenuBar.prototype.onComponentMoved = {};
        }
        Object.defineProperty(this, "onComponentAdded", {
            get: function() {
                var value = delegate.onComponentAdded;
                return value;
            },
            set: function(aValue) {
                delegate.onComponentAdded = aValue;
            }
        });
        if(!P.MenuBar){
            /**
             * Component added event hanler function.
             * @property onComponentAdded
             * @memberOf MenuBar
             */
            P.MenuBar.prototype.onComponentAdded = {};
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
        if(!P.MenuBar){
            /**
             * <code>PopupMenu</code> that assigned for this component.
             * @property componentPopupMenu
             * @memberOf MenuBar
             */
            P.MenuBar.prototype.componentPopupMenu = {};
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
        if(!P.MenuBar){
            /**
             * Vertical coordinate of the component.
             * @property top
             * @memberOf MenuBar
             */
            P.MenuBar.prototype.top = 0;
        }
        Object.defineProperty(this, "onComponentResized", {
            get: function() {
                var value = delegate.onComponentResized;
                return value;
            },
            set: function(aValue) {
                delegate.onComponentResized = aValue;
            }
        });
        if(!P.MenuBar){
            /**
             * Component resized event handler function.
             * @property onComponentResized
             * @memberOf MenuBar
             */
            P.MenuBar.prototype.onComponentResized = {};
        }
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parentWidget;
                return P.boxAsJs(value);
            }
        });
        if(!P.MenuBar){
            /**
             * Parent container of this widget.
             * @property parentWidget
             * @memberOf MenuBar
             */
            P.MenuBar.prototype.parent = {};
        }
        Object.defineProperty(this, "onMouseEntered", {
            get: function() {
                var value = delegate.onMouseEntered;
                return value;
            },
            set: function(aValue) {
                delegate.onMouseEntered = aValue;
            }
        });
        if(!P.MenuBar){
            /**
             * Mouse entered over the component event handler function.
             * @property onMouseEntered
             * @memberOf MenuBar
             */
            P.MenuBar.prototype.onMouseEntered = {};
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
        if(!P.MenuBar){
            /**
             * The tooltip string that has been set with.
             * @property toolTipText
             * @memberOf MenuBar
             */
            P.MenuBar.prototype.toolTipText = '';
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
        if(!P.MenuBar){
            /**
             * Height of the component.
             * @property height
             * @memberOf MenuBar
             */
            P.MenuBar.prototype.height = 0;
        }
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });
        if(!P.MenuBar){
            /**
             * Native API. Returns low level html element. Applicable only in HTML5 client.
             * @property element
             * @memberOf MenuBar
             */
            P.MenuBar.prototype.element = {};
        }
        Object.defineProperty(this, "onComponentShown", {
            get: function() {
                var value = delegate.onComponentShown;
                return value;
            },
            set: function(aValue) {
                delegate.onComponentShown = aValue;
            }
        });
        if(!P.MenuBar){
            /**
             * Component shown event handler function.
             * @property onComponentShown
             * @memberOf MenuBar
             */
            P.MenuBar.prototype.onComponentShown = {};
        }
        Object.defineProperty(this, "onMouseMoved", {
            get: function() {
                var value = delegate.onMouseMoved;
                return value;
            },
            set: function(aValue) {
                delegate.onMouseMoved = aValue;
            }
        });
        if(!P.MenuBar){
            /**
             * Mouse moved event handler function.
             * @property onMouseMoved
             * @memberOf MenuBar
             */
            P.MenuBar.prototype.onMouseMoved = {};
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
        if(!P.MenuBar){
            /**
             * True if this component is completely opaque.
             * @property opaque
             * @memberOf MenuBar
             */
            P.MenuBar.prototype.opaque = true;
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
        if(!P.MenuBar){
            /**
             * Determines whether this component should be visible when its parent is visible.
             * @property visible
             * @memberOf MenuBar
             */
            P.MenuBar.prototype.visible = true;
        }
        Object.defineProperty(this, "onComponentHidden", {
            get: function() {
                var value = delegate.onComponentHidden;
                return value;
            },
            set: function(aValue) {
                delegate.onComponentHidden = aValue;
            }
        });
        if(!P.MenuBar){
            /**
             * Component hidden event handler function.
             * @property onComponentHidden
             * @memberOf MenuBar
             */
            P.MenuBar.prototype.onComponentHidden = {};
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
        if(!P.MenuBar){
            /**
             * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
             * @property nextFocusableComponent
             * @memberOf MenuBar
             */
            P.MenuBar.prototype.nextFocusableComponent = {};
        }
        Object.defineProperty(this, "count", {
            get: function() {
                var value = delegate.count;
                return P.boxAsJs(value);
            }
        });
        if(!P.MenuBar){
            /**
             * Gets the number of components in this panel.
             * @property count
             * @memberOf MenuBar
             */
            P.MenuBar.prototype.count = 0;
        }
        Object.defineProperty(this, "onKeyReleased", {
            get: function() {
                var value = delegate.onKeyReleased;
                return value;
            },
            set: function(aValue) {
                delegate.onKeyReleased = aValue;
            }
        });
        if(!P.MenuBar){
            /**
             * Key released event handler function.
             * @property onKeyReleased
             * @memberOf MenuBar
             */
            P.MenuBar.prototype.onKeyReleased = {};
        }
        Object.defineProperty(this, "onActionPerformed", {
            get: function() {
                var value = delegate.onActionPerformed;
                return value;
            },
            set: function(aValue) {
                delegate.onActionPerformed = aValue;
            }
        });
        if(!P.MenuBar){
            /**
             * Main action performed event handler function.
             * @property onActionPerformed
             * @memberOf MenuBar
             */
            P.MenuBar.prototype.onActionPerformed = {};
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
        if(!P.MenuBar){
            /**
             * Determines whether this component may be focused.
             * @property focusable
             * @memberOf MenuBar
             */
            P.MenuBar.prototype.focusable = true;
        }
        Object.defineProperty(this, "onKeyTyped", {
            get: function() {
                var value = delegate.onKeyTyped;
                return value;
            },
            set: function(aValue) {
                delegate.onKeyTyped = aValue;
            }
        });
        if(!P.MenuBar){
            /**
             * Key typed event handler function.
             * @property onKeyTyped
             * @memberOf MenuBar
             */
            P.MenuBar.prototype.onKeyTyped = {};
        }
        Object.defineProperty(this, "onMouseWheelMoved", {
            get: function() {
                var value = delegate.onMouseWheelMoved;
                return value;
            },
            set: function(aValue) {
                delegate.onMouseWheelMoved = aValue;
            }
        });
        if(!P.MenuBar){
            /**
             * Mouse wheel moved event handler function.
             * @property onMouseWheelMoved
             * @memberOf MenuBar
             */
            P.MenuBar.prototype.onMouseWheelMoved = {};
        }
        Object.defineProperty(this, "onComponentRemoved", {
            get: function() {
                var value = delegate.onComponentRemoved;
                return value;
            },
            set: function(aValue) {
                delegate.onComponentRemoved = aValue;
            }
        });
        if(!P.MenuBar){
            /**
             * Component removed event handler function.
             * @property onComponentRemoved
             * @memberOf MenuBar
             */
            P.MenuBar.prototype.onComponentRemoved = {};
        }
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });
        if(!P.MenuBar){
            /**
             * Native API. Returns low level swing component. Applicable only in J2SE swing client.
             * @property component
             * @memberOf MenuBar
             */
            P.MenuBar.prototype.component = {};
        }
        Object.defineProperty(this, "onFocusGained", {
            get: function() {
                var value = delegate.onFocusGained;
                return value;
            },
            set: function(aValue) {
                delegate.onFocusGained = aValue;
            }
        });
        if(!P.MenuBar){
            /**
             * Keyboard focus gained by the component event.
             * @property onFocusGained
             * @memberOf MenuBar
             */
            P.MenuBar.prototype.onFocusGained = {};
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
        if(!P.MenuBar){
            /**
             * Horizontal coordinate of the component.
             * @property left
             * @memberOf MenuBar
             */
            P.MenuBar.prototype.left = 0;
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
        if(!P.MenuBar){
            /**
             * The background color of this component.
             * @property background
             * @memberOf MenuBar
             */
            P.MenuBar.prototype.background = {};
        }
        Object.defineProperty(this, "onMouseClicked", {
            get: function() {
                var value = delegate.onMouseClicked;
                return value;
            },
            set: function(aValue) {
                delegate.onMouseClicked = aValue;
            }
        });
        if(!P.MenuBar){
            /**
             * Mouse clicked event handler function.
             * @property onMouseClicked
             * @memberOf MenuBar
             */
            P.MenuBar.prototype.onMouseClicked = {};
        }
        Object.defineProperty(this, "onMouseExited", {
            get: function() {
                var value = delegate.onMouseExited;
                return value;
            },
            set: function(aValue) {
                delegate.onMouseExited = aValue;
            }
        });
        if(!P.MenuBar){
            /**
             * Mouse exited over the component event handler function.
             * @property onMouseExited
             * @memberOf MenuBar
             */
            P.MenuBar.prototype.onMouseExited = {};
        }
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.name = P.boxAsJava(aValue);
            }
        });
        if(!P.MenuBar){
            /**
             * Gets name of this component.
             * @property name
             * @memberOf MenuBar
             */
            P.MenuBar.prototype.name = '';
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
        if(!P.MenuBar){
            /**
             * Width of the component.
             * @property width
             * @memberOf MenuBar
             */
            P.MenuBar.prototype.width = 0;
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
        if(!P.MenuBar){
            /**
             * The font of this component.
             * @property font
             * @memberOf MenuBar
             */
            P.MenuBar.prototype.font = {};
        }
        Object.defineProperty(this, "onKeyPressed", {
            get: function() {
                var value = delegate.onKeyPressed;
                return value;
            },
            set: function(aValue) {
                delegate.onKeyPressed = aValue;
            }
        });
        if(!P.MenuBar){
            /**
             * Key pressed event handler function.
             * @property onKeyPressed
             * @memberOf MenuBar
             */
            P.MenuBar.prototype.onKeyPressed = {};
        }
    };
        /**
         * Adds the item to the menu.
         * @param menu the menu component to add
         * @method add
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.add = function(menu) {
            var delegate = this.unwrap();
            var value = delegate.add(P.boxAsJava(menu));
            return P.boxAsJs(value);
        };

        /**
         * Removes the specified component from this container.
         * @param component the component to remove
         * @method remove
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.remove = function(component) {
            var delegate = this.unwrap();
            var value = delegate.remove(P.boxAsJava(component));
            return P.boxAsJs(value);
        };

        /**
         * Removes all the components from this container.
         * @method clear
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.clear = function() {
            var delegate = this.unwrap();
            var value = delegate.clear();
            return P.boxAsJs(value);
        };

        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.focus = function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        };

        /**
         * Gets the container's children components.
         * @method children
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.children = function() {
            var delegate = this.unwrap();
            var value = delegate.children();
            return P.boxAsJs(value);
        };

        /**
         * Gets the container's n-th component.
         * @param index the component's index in the container
         * @return the child component
         * @method child
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.child = function(index) {
            var delegate = this.unwrap();
            var value = delegate.child(P.boxAsJava(index));
            return P.boxAsJs(value);
        };

})();