(function() {
    var className = "com.eas.client.forms.menu.Menu";
    var javaClass = Java.type(className);
    var space = this['-platypus-scripts-space'];
    space.putPublisher(className, function(aDelegate) {
        return new P.Menu(null, aDelegate);
    });
    
    /**
     * An implementation of a menu -- a popup window containing MenuItems
     * that is displayed when the user selects an item on the MenuBar.
     * In addition to <code>MenuItems</code>, a <code>Menu</code> can also contain <code>MenuSeparators</code>.
     * @param text the text for the menu label (optional).
     * @constructor Menu Menu
     */
    P.Menu = function (text) {
        var maxArgs = 1;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 1 ? new javaClass(P.boxAsJava(text))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            configurable: true,
            value: function() {
                return delegate;
            }
        });
        if(P.Menu.superclass)
            P.Menu.superclass.constructor.apply(this, arguments);
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
        if(!P.Menu){
            /**
             * The mouse <code>Cursor</code> over this component.
             * @property cursor
             * @memberOf Menu
             */
            P.Menu.prototype.cursor = {};
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
        if(!P.Menu){
            /**
             * Mouse dragged event handler function.
             * @property onMouseDragged
             * @memberOf Menu
             */
            P.Menu.prototype.onMouseDragged = {};
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
        if(!P.Menu){
            /**
             * Mouse released event handler function.
             * @property onMouseReleased
             * @memberOf Menu
             */
            P.Menu.prototype.onMouseReleased = {};
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
        if(!P.Menu){
            /**
             * Keyboard focus lost by the component event handler function.
             * @property onFocusLost
             * @memberOf Menu
             */
            P.Menu.prototype.onFocusLost = {};
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
        if(!P.Menu){
            /**
             * Mouse pressed event handler function.
             * @property onMousePressed
             * @memberOf Menu
             */
            P.Menu.prototype.onMousePressed = {};
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
        if(!P.Menu){
            /**
             * The foreground color of this component.
             * @property foreground
             * @memberOf Menu
             */
            P.Menu.prototype.foreground = {};
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
        if(!P.Menu){
            /**
             * An error message of this component.
             * Validation procedure may set this property and subsequent focus lost event will clear it.
             * @property error
             * @memberOf Menu
             */
            P.Menu.prototype.error = '';
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
        if(!P.Menu){
            /**
             * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
             * @property enabled
             * @memberOf Menu
             */
            P.Menu.prototype.enabled = true;
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
        if(!P.Menu){
            /**
             * Component moved event handler function.
             * @property onComponentMoved
             * @memberOf Menu
             */
            P.Menu.prototype.onComponentMoved = {};
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
        if(!P.Menu){
            /**
             * Component added event hanler function.
             * @property onComponentAdded
             * @memberOf Menu
             */
            P.Menu.prototype.onComponentAdded = {};
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
        if(!P.Menu){
            /**
             * <code>PopupMenu</code> that assigned for this component.
             * @property componentPopupMenu
             * @memberOf Menu
             */
            P.Menu.prototype.componentPopupMenu = {};
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
        if(!P.Menu){
            /**
             * Vertical coordinate of the component.
             * @property top
             * @memberOf Menu
             */
            P.Menu.prototype.top = 0;
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
        if(!P.Menu){
            /**
             * Component resized event handler function.
             * @property onComponentResized
             * @memberOf Menu
             */
            P.Menu.prototype.onComponentResized = {};
        }
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parentWidget;
                return P.boxAsJs(value);
            }
        });
        if(!P.Menu){
            /**
             * Parent container of this widget.
             * @property parentWidget
             * @memberOf Menu
             */
            P.Menu.prototype.parent = {};
        }
        Object.defineProperty(this, "text", {
            get: function() {
                var value = delegate.text;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.text = P.boxAsJava(aValue);
            }
        });
        if(!P.Menu){
            /**
             * The text of the menu.
             * @property text
             * @memberOf Menu
             */
            P.Menu.prototype.text = '';
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
        if(!P.Menu){
            /**
             * Mouse entered over the component event handler function.
             * @property onMouseEntered
             * @memberOf Menu
             */
            P.Menu.prototype.onMouseEntered = {};
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
        if(!P.Menu){
            /**
             * The tooltip string that has been set with.
             * @property toolTipText
             * @memberOf Menu
             */
            P.Menu.prototype.toolTipText = '';
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
        if(!P.Menu){
            /**
             * Height of the component.
             * @property height
             * @memberOf Menu
             */
            P.Menu.prototype.height = 0;
        }
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });
        if(!P.Menu){
            /**
             * Native API. Returns low level html element. Applicable only in HTML5 client.
             * @property element
             * @memberOf Menu
             */
            P.Menu.prototype.element = {};
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
        if(!P.Menu){
            /**
             * Component shown event handler function.
             * @property onComponentShown
             * @memberOf Menu
             */
            P.Menu.prototype.onComponentShown = {};
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
        if(!P.Menu){
            /**
             * Mouse moved event handler function.
             * @property onMouseMoved
             * @memberOf Menu
             */
            P.Menu.prototype.onMouseMoved = {};
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
        if(!P.Menu){
            /**
             * True if this component is completely opaque.
             * @property opaque
             * @memberOf Menu
             */
            P.Menu.prototype.opaque = true;
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
        if(!P.Menu){
            /**
             * Determines whether this component should be visible when its parent is visible.
             * @property visible
             * @memberOf Menu
             */
            P.Menu.prototype.visible = true;
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
        if(!P.Menu){
            /**
             * Component hidden event handler function.
             * @property onComponentHidden
             * @memberOf Menu
             */
            P.Menu.prototype.onComponentHidden = {};
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
        if(!P.Menu){
            /**
             * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
             * @property nextFocusableComponent
             * @memberOf Menu
             */
            P.Menu.prototype.nextFocusableComponent = {};
        }
        Object.defineProperty(this, "count", {
            get: function() {
                var value = delegate.count;
                return P.boxAsJs(value);
            }
        });
        if(!P.Menu){
            /**
             * The count of the menu items.
             * @property count
             * @memberOf Menu
             */
            P.Menu.prototype.count = 0;
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
        if(!P.Menu){
            /**
             * Key released event handler function.
             * @property onKeyReleased
             * @memberOf Menu
             */
            P.Menu.prototype.onKeyReleased = {};
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
        if(!P.Menu){
            /**
             * Main action performed event handler function.
             * @property onActionPerformed
             * @memberOf Menu
             */
            P.Menu.prototype.onActionPerformed = {};
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
        if(!P.Menu){
            /**
             * Determines whether this component may be focused.
             * @property focusable
             * @memberOf Menu
             */
            P.Menu.prototype.focusable = true;
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
        if(!P.Menu){
            /**
             * Key typed event handler function.
             * @property onKeyTyped
             * @memberOf Menu
             */
            P.Menu.prototype.onKeyTyped = {};
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
        if(!P.Menu){
            /**
             * Mouse wheel moved event handler function.
             * @property onMouseWheelMoved
             * @memberOf Menu
             */
            P.Menu.prototype.onMouseWheelMoved = {};
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
        if(!P.Menu){
            /**
             * Component removed event handler function.
             * @property onComponentRemoved
             * @memberOf Menu
             */
            P.Menu.prototype.onComponentRemoved = {};
        }
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });
        if(!P.Menu){
            /**
             * Native API. Returns low level swing component. Applicable only in J2SE swing client.
             * @property component
             * @memberOf Menu
             */
            P.Menu.prototype.component = {};
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
        if(!P.Menu){
            /**
             * Keyboard focus gained by the component event.
             * @property onFocusGained
             * @memberOf Menu
             */
            P.Menu.prototype.onFocusGained = {};
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
        if(!P.Menu){
            /**
             * Horizontal coordinate of the component.
             * @property left
             * @memberOf Menu
             */
            P.Menu.prototype.left = 0;
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
        if(!P.Menu){
            /**
             * The background color of this component.
             * @property background
             * @memberOf Menu
             */
            P.Menu.prototype.background = {};
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
        if(!P.Menu){
            /**
             * Mouse clicked event handler function.
             * @property onMouseClicked
             * @memberOf Menu
             */
            P.Menu.prototype.onMouseClicked = {};
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
        if(!P.Menu){
            /**
             * Mouse exited over the component event handler function.
             * @property onMouseExited
             * @memberOf Menu
             */
            P.Menu.prototype.onMouseExited = {};
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
        if(!P.Menu){
            /**
             * Gets name of this component.
             * @property name
             * @memberOf Menu
             */
            P.Menu.prototype.name = '';
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
        if(!P.Menu){
            /**
             * Width of the component.
             * @property width
             * @memberOf Menu
             */
            P.Menu.prototype.width = 0;
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
        if(!P.Menu){
            /**
             * The font of this component.
             * @property font
             * @memberOf Menu
             */
            P.Menu.prototype.font = {};
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
        if(!P.Menu){
            /**
             * Key pressed event handler function.
             * @property onKeyPressed
             * @memberOf Menu
             */
            P.Menu.prototype.onKeyPressed = {};
        }
    };
        /**
         * Adds an item to the menu.
         * @param component the component to add
         * @method add
         * @memberOf Menu
         */
        P.Menu.prototype.add = function(component) {
            var delegate = this.unwrap();
            var value = delegate.add(P.boxAsJava(component));
            return P.boxAsJs(value);
        };

        /**
         * Removes the specified component from this container.
         * @param component the component to remove
         * @method remove
         * @memberOf Menu
         */
        P.Menu.prototype.remove = function(component) {
            var delegate = this.unwrap();
            var value = delegate.remove(P.boxAsJava(component));
            return P.boxAsJs(value);
        };

        /**
         * Removes all the components from this container.
         * @method clear
         * @memberOf Menu
         */
        P.Menu.prototype.clear = function() {
            var delegate = this.unwrap();
            var value = delegate.clear();
            return P.boxAsJs(value);
        };

        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf Menu
         */
        P.Menu.prototype.focus = function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        };

        /**
         * Gets the container's children components.
         * @method children
         * @memberOf Menu
         */
        P.Menu.prototype.children = function() {
            var delegate = this.unwrap();
            var value = delegate.children();
            return P.boxAsJs(value);
        };

        /**
         * Gets the container's n-th component.
         * @param index the component's index in the container
         * @return the child component
         * @method child
         * @memberOf Menu
         */
        P.Menu.prototype.child = function(index) {
            var delegate = this.unwrap();
            var value = delegate.child(P.boxAsJava(index));
            return P.boxAsJs(value);
        };

})();