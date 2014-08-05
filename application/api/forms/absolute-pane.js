(function() {
    var javaClass = Java.type("com.eas.client.forms.api.containers.AbsolutePane");
    javaClass.setPublisher(function(aDelegate) {
        return new P.AbsolutePane(aDelegate);
    });
    
    /**
     * A container with Absolute Layout.
     * @constructor AbsolutePane AbsolutePane
     */
    P.AbsolutePane = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.AbsolutePane.superclass)
            P.AbsolutePane.superclass.constructor.apply(this, arguments);
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
        if(!P.AbsolutePane){
            /**
             * The mouse <code>Cursor</code> over this component.
             * @property cursor
             * @memberOf AbsolutePane
             */
            P.AbsolutePane.prototype.cursor = {};
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
        if(!P.AbsolutePane){
            /**
             * Mouse dragged event handler function.
             * @property onMouseDragged
             * @memberOf AbsolutePane
             */
            P.AbsolutePane.prototype.onMouseDragged = {};
        }
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parent;
                return P.boxAsJs(value);
            }
        });
        if(!P.AbsolutePane){
            /**
             * Gets the parent of this component.
             * @property parent
             * @memberOf AbsolutePane
             */
            P.AbsolutePane.prototype.parent = {};
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
        if(!P.AbsolutePane){
            /**
             * Mouse released event handler function.
             * @property onMouseReleased
             * @memberOf AbsolutePane
             */
            P.AbsolutePane.prototype.onMouseReleased = {};
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
        if(!P.AbsolutePane){
            /**
             * Keyboard focus lost by the component event handler function.
             * @property onFocusLost
             * @memberOf AbsolutePane
             */
            P.AbsolutePane.prototype.onFocusLost = {};
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
        if(!P.AbsolutePane){
            /**
             * Mouse pressed event handler function.
             * @property onMousePressed
             * @memberOf AbsolutePane
             */
            P.AbsolutePane.prototype.onMousePressed = {};
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
        if(!P.AbsolutePane){
            /**
             * The foreground color of this component.
             * @property foreground
             * @memberOf AbsolutePane
             */
            P.AbsolutePane.prototype.foreground = {};
        }
        Object.defineProperty(this, "error", {
            get: function() {
                var value = delegate.error;
                return P.boxAsJs(value);
            }
        });
        if(!P.AbsolutePane){
            /**
             * An error message of this component.
             * Validation procedure may set this property and subsequent focus lost event will clear it.
             * @property error
             * @memberOf AbsolutePane
             */
            P.AbsolutePane.prototype.error = '';
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
        if(!P.AbsolutePane){
            /**
             * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
             * @property enabled
             * @memberOf AbsolutePane
             */
            P.AbsolutePane.prototype.enabled = true;
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
        if(!P.AbsolutePane){
            /**
             * Component moved event handler function.
             * @property onComponentMoved
             * @memberOf AbsolutePane
             */
            P.AbsolutePane.prototype.onComponentMoved = {};
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
        if(!P.AbsolutePane){
            /**
             * Component added event hanler function.
             * @property onComponentAdded
             * @memberOf AbsolutePane
             */
            P.AbsolutePane.prototype.onComponentAdded = {};
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
        if(!P.AbsolutePane){
            /**
             * <code>PopupMenu</code> that assigned for this component.
             * @property componentPopupMenu
             * @memberOf AbsolutePane
             */
            P.AbsolutePane.prototype.componentPopupMenu = {};
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
        if(!P.AbsolutePane){
            /**
             * Vertical coordinate of the component.
             * @property top
             * @memberOf AbsolutePane
             */
            P.AbsolutePane.prototype.top = 0;
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
        if(!P.AbsolutePane){
            /**
             * Gets the container's children components.
             * @property children
             * @memberOf AbsolutePane
             */
            P.AbsolutePane.prototype.children = [];
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
        if(!P.AbsolutePane){
            /**
             * Component resized event handler function.
             * @property onComponentResized
             * @memberOf AbsolutePane
             */
            P.AbsolutePane.prototype.onComponentResized = {};
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
        if(!P.AbsolutePane){
            /**
             * Mouse entered over the component event handler function.
             * @property onMouseEntered
             * @memberOf AbsolutePane
             */
            P.AbsolutePane.prototype.onMouseEntered = {};
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
        if(!P.AbsolutePane){
            /**
             * The tooltip string that has been set with.
             * @property toolTipText
             * @memberOf AbsolutePane
             */
            P.AbsolutePane.prototype.toolTipText = '';
        }
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });
        if(!P.AbsolutePane){
            /**
             * Native API. Returns low level html element. Applicable only in HTML5 client.
             * @property element
             * @memberOf AbsolutePane
             */
            P.AbsolutePane.prototype.element = {};
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
        if(!P.AbsolutePane){
            /**
             * Height of the component.
             * @property height
             * @memberOf AbsolutePane
             */
            P.AbsolutePane.prototype.height = 0;
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
        if(!P.AbsolutePane){
            /**
             * Component shown event handler function.
             * @property onComponentShown
             * @memberOf AbsolutePane
             */
            P.AbsolutePane.prototype.onComponentShown = {};
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
        if(!P.AbsolutePane){
            /**
             * Mouse moved event handler function.
             * @property onMouseMoved
             * @memberOf AbsolutePane
             */
            P.AbsolutePane.prototype.onMouseMoved = {};
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
        if(!P.AbsolutePane){
            /**
             * True if this component is completely opaque.
             * @property opaque
             * @memberOf AbsolutePane
             */
            P.AbsolutePane.prototype.opaque = true;
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
        if(!P.AbsolutePane){
            /**
             * Determines whether this component should be visible when its parent is visible.
             * @property visible
             * @memberOf AbsolutePane
             */
            P.AbsolutePane.prototype.visible = true;
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
        if(!P.AbsolutePane){
            /**
             * Component hidden event handler function.
             * @property onComponentHidden
             * @memberOf AbsolutePane
             */
            P.AbsolutePane.prototype.onComponentHidden = {};
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
        if(!P.AbsolutePane){
            /**
             * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
             * @property nextFocusableComponent
             * @memberOf AbsolutePane
             */
            P.AbsolutePane.prototype.nextFocusableComponent = {};
        }
        Object.defineProperty(this, "count", {
            get: function() {
                var value = delegate.count;
                return P.boxAsJs(value);
            }
        });
        if(!P.AbsolutePane){
            /**
             * Gets the number of components in this panel.
             * @property count
             * @memberOf AbsolutePane
             */
            P.AbsolutePane.prototype.count = 0;
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
        if(!P.AbsolutePane){
            /**
             * Main action performed event handler function.
             * @property onActionPerformed
             * @memberOf AbsolutePane
             */
            P.AbsolutePane.prototype.onActionPerformed = {};
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
        if(!P.AbsolutePane){
            /**
             * Key released event handler function.
             * @property onKeyReleased
             * @memberOf AbsolutePane
             */
            P.AbsolutePane.prototype.onKeyReleased = {};
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
        if(!P.AbsolutePane){
            /**
             * Determines whether this component may be focused.
             * @property focusable
             * @memberOf AbsolutePane
             */
            P.AbsolutePane.prototype.focusable = true;
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
        if(!P.AbsolutePane){
            /**
             * Key typed event handler function.
             * @property onKeyTyped
             * @memberOf AbsolutePane
             */
            P.AbsolutePane.prototype.onKeyTyped = {};
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
        if(!P.AbsolutePane){
            /**
             * Mouse wheel moved event handler function.
             * @property onMouseWheelMoved
             * @memberOf AbsolutePane
             */
            P.AbsolutePane.prototype.onMouseWheelMoved = {};
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
        if(!P.AbsolutePane){
            /**
             * Component removed event handler function.
             * @property onComponentRemoved
             * @memberOf AbsolutePane
             */
            P.AbsolutePane.prototype.onComponentRemoved = {};
        }
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });
        if(!P.AbsolutePane){
            /**
             * Native API. Returns low level swing component. Applicable only in J2SE swing client.
             * @property component
             * @memberOf AbsolutePane
             */
            P.AbsolutePane.prototype.component = {};
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
        if(!P.AbsolutePane){
            /**
             * Keyboard focus gained by the component event.
             * @property onFocusGained
             * @memberOf AbsolutePane
             */
            P.AbsolutePane.prototype.onFocusGained = {};
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
        if(!P.AbsolutePane){
            /**
             * Horizontal coordinate of the component.
             * @property left
             * @memberOf AbsolutePane
             */
            P.AbsolutePane.prototype.left = 0;
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
        if(!P.AbsolutePane){
            /**
             * The background color of this component.
             * @property background
             * @memberOf AbsolutePane
             */
            P.AbsolutePane.prototype.background = {};
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
        if(!P.AbsolutePane){
            /**
             * Mouse clicked event handler function.
             * @property onMouseClicked
             * @memberOf AbsolutePane
             */
            P.AbsolutePane.prototype.onMouseClicked = {};
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
        if(!P.AbsolutePane){
            /**
             * Mouse exited over the component event handler function.
             * @property onMouseExited
             * @memberOf AbsolutePane
             */
            P.AbsolutePane.prototype.onMouseExited = {};
        }
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.AbsolutePane){
            /**
             * Gets name of this component.
             * @property name
             * @memberOf AbsolutePane
             */
            P.AbsolutePane.prototype.name = '';
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
        if(!P.AbsolutePane){
            /**
             * Width of the component.
             * @property width
             * @memberOf AbsolutePane
             */
            P.AbsolutePane.prototype.width = 0;
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
        if(!P.AbsolutePane){
            /**
             * The font of this component.
             * @property font
             * @memberOf AbsolutePane
             */
            P.AbsolutePane.prototype.font = {};
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
        if(!P.AbsolutePane){
            /**
             * Key pressed event handler function.
             * @property onKeyPressed
             * @memberOf AbsolutePane
             */
            P.AbsolutePane.prototype.onKeyPressed = {};
        }
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    };
        /**
         * Appends the specified component at left top corner of this container.
         * @param component the component to add.
         * @param anchors the anchors object for the component, can contain the following properties: left, width, top, height.
         * @method add
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.add = function(component, anchors) {
            var delegate = this.unwrap();
            var value = delegate.add(P.boxAsJava(component), P.boxAsJava(anchors));
            return P.boxAsJs(value);
        };

        /**
         * Brings the specified component to back on this panel.
         * @param component the component
         * @param count steps to move the component (optional)
         * @method toBack
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.toBack = function(component, count) {
            var delegate = this.unwrap();
            var value = delegate.toBack(P.boxAsJava(component), P.boxAsJava(count));
            return P.boxAsJs(value);
        };

        /**
         * Brings the specified component to front on this panel.
         * @param component the component
         * @param count steps to move the component (optional)
         * @method toFront
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.toFront = function(component, count) {
            var delegate = this.unwrap();
            var value = delegate.toFront(P.boxAsJava(component), P.boxAsJava(count));
            return P.boxAsJs(value);
        };

        /**
         * Gets the container's n-th component.
         * @param index the component's index in the container
         * @return the child component
         * @method child
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.child = function(index) {
            var delegate = this.unwrap();
            var value = delegate.child(P.boxAsJava(index));
            return P.boxAsJs(value);
        };

        /**
         * Removes the specified component from this container.
         * @param component the component to remove
         * @method remove
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.remove = function(component) {
            var delegate = this.unwrap();
            var value = delegate.remove(P.boxAsJava(component));
            return P.boxAsJs(value);
        };

        /**
         * Removes all the components from this container.
         * @method clear
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.clear = function() {
            var delegate = this.unwrap();
            var value = delegate.clear();
            return P.boxAsJs(value);
        };

        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.focus = function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        };

})();