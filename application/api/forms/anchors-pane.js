(function() {
    var javaClass = Java.type("com.eas.client.forms.containers.AnchorsPane");
    javaClass.setPublisher(function(aDelegate) {
        return new P.AnchorsPane(aDelegate);
    });
    
    /**
     * A container with Anchors Layout.
     * @constructor AnchorsPane AnchorsPane
     */
    P.AnchorsPane = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.AnchorsPane.superclass)
            P.AnchorsPane.superclass.constructor.apply(this, arguments);
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
        if(!P.AnchorsPane){
            /**
             * The mouse <code>Cursor</code> over this component.
             * @property cursor
             * @memberOf AnchorsPane
             */
            P.AnchorsPane.prototype.cursor = {};
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
        if(!P.AnchorsPane){
            /**
             * Mouse dragged event handler function.
             * @property onMouseDragged
             * @memberOf AnchorsPane
             */
            P.AnchorsPane.prototype.onMouseDragged = {};
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
        if(!P.AnchorsPane){
            /**
             * Mouse released event handler function.
             * @property onMouseReleased
             * @memberOf AnchorsPane
             */
            P.AnchorsPane.prototype.onMouseReleased = {};
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
        if(!P.AnchorsPane){
            /**
             * Keyboard focus lost by the component event handler function.
             * @property onFocusLost
             * @memberOf AnchorsPane
             */
            P.AnchorsPane.prototype.onFocusLost = {};
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
        if(!P.AnchorsPane){
            /**
             * Mouse pressed event handler function.
             * @property onMousePressed
             * @memberOf AnchorsPane
             */
            P.AnchorsPane.prototype.onMousePressed = {};
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
        if(!P.AnchorsPane){
            /**
             * The foreground color of this component.
             * @property foreground
             * @memberOf AnchorsPane
             */
            P.AnchorsPane.prototype.foreground = {};
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
        if(!P.AnchorsPane){
            /**
             * An error message of this component.
             * Validation procedure may set this property and subsequent focus lost event will clear it.
             * @property error
             * @memberOf AnchorsPane
             */
            P.AnchorsPane.prototype.error = '';
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
        if(!P.AnchorsPane){
            /**
             * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
             * @property enabled
             * @memberOf AnchorsPane
             */
            P.AnchorsPane.prototype.enabled = true;
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
        if(!P.AnchorsPane){
            /**
             * Component moved event handler function.
             * @property onComponentMoved
             * @memberOf AnchorsPane
             */
            P.AnchorsPane.prototype.onComponentMoved = {};
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
        if(!P.AnchorsPane){
            /**
             * Component added event hanler function.
             * @property onComponentAdded
             * @memberOf AnchorsPane
             */
            P.AnchorsPane.prototype.onComponentAdded = {};
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
        if(!P.AnchorsPane){
            /**
             * <code>PopupMenu</code> that assigned for this component.
             * @property componentPopupMenu
             * @memberOf AnchorsPane
             */
            P.AnchorsPane.prototype.componentPopupMenu = {};
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
        if(!P.AnchorsPane){
            /**
             * Vertical coordinate of the component.
             * @property top
             * @memberOf AnchorsPane
             */
            P.AnchorsPane.prototype.top = 0;
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
        if(!P.AnchorsPane){
            /**
             * Gets the container's children components.
             * @property children
             * @memberOf AnchorsPane
             */
            P.AnchorsPane.prototype.children = [];
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
        if(!P.AnchorsPane){
            /**
             * Component resized event handler function.
             * @property onComponentResized
             * @memberOf AnchorsPane
             */
            P.AnchorsPane.prototype.onComponentResized = {};
        }
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parentWidget;
                return P.boxAsJs(value);
            }
        });
        if(!P.AnchorsPane){
            /**
             * Parent container of this widget.
             * @property parentWidget
             * @memberOf AnchorsPane
             */
            P.AnchorsPane.prototype.parent = {};
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
        if(!P.AnchorsPane){
            /**
             * Mouse entered over the component event handler function.
             * @property onMouseEntered
             * @memberOf AnchorsPane
             */
            P.AnchorsPane.prototype.onMouseEntered = {};
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
        if(!P.AnchorsPane){
            /**
             * The tooltip string that has been set with.
             * @property toolTipText
             * @memberOf AnchorsPane
             */
            P.AnchorsPane.prototype.toolTipText = '';
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
        if(!P.AnchorsPane){
            /**
             * Height of the component.
             * @property height
             * @memberOf AnchorsPane
             */
            P.AnchorsPane.prototype.height = 0;
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
        if(!P.AnchorsPane){
            /**
             * Component shown event handler function.
             * @property onComponentShown
             * @memberOf AnchorsPane
             */
            P.AnchorsPane.prototype.onComponentShown = {};
        }
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });
        if(!P.AnchorsPane){
            /**
             * Native API. Returns low level html element. Applicable only in HTML5 client.
             * @property element
             * @memberOf AnchorsPane
             */
            P.AnchorsPane.prototype.element = {};
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
        if(!P.AnchorsPane){
            /**
             * Mouse moved event handler function.
             * @property onMouseMoved
             * @memberOf AnchorsPane
             */
            P.AnchorsPane.prototype.onMouseMoved = {};
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
        if(!P.AnchorsPane){
            /**
             * True if this component is completely opaque.
             * @property opaque
             * @memberOf AnchorsPane
             */
            P.AnchorsPane.prototype.opaque = true;
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
        if(!P.AnchorsPane){
            /**
             * Determines whether this component should be visible when its parent is visible.
             * @property visible
             * @memberOf AnchorsPane
             */
            P.AnchorsPane.prototype.visible = true;
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
        if(!P.AnchorsPane){
            /**
             * Component hidden event handler function.
             * @property onComponentHidden
             * @memberOf AnchorsPane
             */
            P.AnchorsPane.prototype.onComponentHidden = {};
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
        if(!P.AnchorsPane){
            /**
             * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
             * @property nextFocusableComponent
             * @memberOf AnchorsPane
             */
            P.AnchorsPane.prototype.nextFocusableComponent = {};
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
        if(!P.AnchorsPane){
            /**
             * Main action performed event handler function.
             * @property onActionPerformed
             * @memberOf AnchorsPane
             */
            P.AnchorsPane.prototype.onActionPerformed = {};
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
        if(!P.AnchorsPane){
            /**
             * Key released event handler function.
             * @property onKeyReleased
             * @memberOf AnchorsPane
             */
            P.AnchorsPane.prototype.onKeyReleased = {};
        }
        Object.defineProperty(this, "count", {
            get: function() {
                var value = delegate.count;
                return P.boxAsJs(value);
            }
        });
        if(!P.AnchorsPane){
            /**
             * Gets the number of components in this panel.
             * @property count
             * @memberOf AnchorsPane
             */
            P.AnchorsPane.prototype.count = 0;
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
        if(!P.AnchorsPane){
            /**
             * Determines whether this component may be focused.
             * @property focusable
             * @memberOf AnchorsPane
             */
            P.AnchorsPane.prototype.focusable = true;
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
        if(!P.AnchorsPane){
            /**
             * Key typed event handler function.
             * @property onKeyTyped
             * @memberOf AnchorsPane
             */
            P.AnchorsPane.prototype.onKeyTyped = {};
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
        if(!P.AnchorsPane){
            /**
             * Mouse wheel moved event handler function.
             * @property onMouseWheelMoved
             * @memberOf AnchorsPane
             */
            P.AnchorsPane.prototype.onMouseWheelMoved = {};
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
        if(!P.AnchorsPane){
            /**
             * Component removed event handler function.
             * @property onComponentRemoved
             * @memberOf AnchorsPane
             */
            P.AnchorsPane.prototype.onComponentRemoved = {};
        }
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });
        if(!P.AnchorsPane){
            /**
             * Native API. Returns low level swing component. Applicable only in J2SE swing client.
             * @property component
             * @memberOf AnchorsPane
             */
            P.AnchorsPane.prototype.component = {};
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
        if(!P.AnchorsPane){
            /**
             * Keyboard focus gained by the component event.
             * @property onFocusGained
             * @memberOf AnchorsPane
             */
            P.AnchorsPane.prototype.onFocusGained = {};
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
        if(!P.AnchorsPane){
            /**
             * Horizontal coordinate of the component.
             * @property left
             * @memberOf AnchorsPane
             */
            P.AnchorsPane.prototype.left = 0;
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
        if(!P.AnchorsPane){
            /**
             * The background color of this component.
             * @property background
             * @memberOf AnchorsPane
             */
            P.AnchorsPane.prototype.background = {};
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
        if(!P.AnchorsPane){
            /**
             * Mouse clicked event handler function.
             * @property onMouseClicked
             * @memberOf AnchorsPane
             */
            P.AnchorsPane.prototype.onMouseClicked = {};
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
        if(!P.AnchorsPane){
            /**
             * Mouse exited over the component event handler function.
             * @property onMouseExited
             * @memberOf AnchorsPane
             */
            P.AnchorsPane.prototype.onMouseExited = {};
        }
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.AnchorsPane){
            /**
             * Gets name of this component.
             * @property name
             * @memberOf AnchorsPane
             */
            P.AnchorsPane.prototype.name = '';
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
        if(!P.AnchorsPane){
            /**
             * Width of the component.
             * @property width
             * @memberOf AnchorsPane
             */
            P.AnchorsPane.prototype.width = 0;
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
        if(!P.AnchorsPane){
            /**
             * The font of this component.
             * @property font
             * @memberOf AnchorsPane
             */
            P.AnchorsPane.prototype.font = {};
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
        if(!P.AnchorsPane){
            /**
             * Key pressed event handler function.
             * @property onKeyPressed
             * @memberOf AnchorsPane
             */
            P.AnchorsPane.prototype.onKeyPressed = {};
        }
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    };
        /**
         * Appends the specified component to the container with specified placement.
         * @param component the component to add.
         * @param anchors the anchors object for the component, can contain the following properties: left, width, right, top, height, bottom.
         * @method add
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.add = function(component, anchors) {
            var delegate = this.unwrap();
            var value = delegate.add(P.boxAsJava(component), P.boxAsJava(anchors));
            return P.boxAsJs(value);
        };

        /**
         * Removes the specified component from this container.
         * @param component the component to remove
         * @method remove
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.remove = function(component) {
            var delegate = this.unwrap();
            var value = delegate.remove(P.boxAsJava(component));
            return P.boxAsJs(value);
        };

        /**
         * Removes all the components from this container.
         * @method clear
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.clear = function() {
            var delegate = this.unwrap();
            var value = delegate.clear();
            return P.boxAsJs(value);
        };

        /**
         * Brings the specified component to back on this panel.
         * @param component the component.
         * @param count steps to move the component (optional).
         * @method toBack
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.toBack = function(arg0, arg1) {
            var delegate = this.unwrap();
            var value = delegate.toBack(P.boxAsJava(arg0), P.boxAsJava(arg1));
            return P.boxAsJs(value);
        };

        /**
         * Brings the specified component to front on this panel.
         * @param component the component.
         * @param count steps to move the component (optional).
         * @method toFront
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.toFront = function(component, count) {
            var delegate = this.unwrap();
            var value = delegate.toFront(P.boxAsJava(component), P.boxAsJava(count));
            return P.boxAsJs(value);
        };

        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.focus = function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        };

})();