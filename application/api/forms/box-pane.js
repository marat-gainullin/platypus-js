(function() {
    var javaClass = Java.type("com.eas.client.forms.containers.BoxPane");
    javaClass.setPublisher(function(aDelegate) {
        return new P.BoxPane(null, null, null, aDelegate);
    });
    
    /**
     * A container with Box Layout. By default uses horisontal orientation.
     * @param orientation Orientation.HORIZONTAL or Orientation.VERTICAL (optional).
     * @param hgap the horizontal gap (optional).
     * @param vgap the vertical gap (optional).
     * @constructor BoxPane BoxPane
     */
    P.BoxPane = function (orientation, hgap, vgap) {
        var maxArgs = 3;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 3 ? new javaClass(P.boxAsJava(orientation), P.boxAsJava(hgap), P.boxAsJava(vgap))
            : arguments.length === 2 ? new javaClass(P.boxAsJava(orientation), P.boxAsJava(hgap))
            : arguments.length === 1 ? new javaClass(P.boxAsJava(orientation))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.BoxPane.superclass)
            P.BoxPane.superclass.constructor.apply(this, arguments);
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
        if(!P.BoxPane){
            /**
             * The mouse <code>Cursor</code> over this component.
             * @property cursor
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.cursor = {};
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
        if(!P.BoxPane){
            /**
             * Mouse dragged event handler function.
             * @property onMouseDragged
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.onMouseDragged = {};
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
        if(!P.BoxPane){
            /**
             * Mouse released event handler function.
             * @property onMouseReleased
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.onMouseReleased = {};
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
        if(!P.BoxPane){
            /**
             * Keyboard focus lost by the component event handler function.
             * @property onFocusLost
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.onFocusLost = {};
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
        if(!P.BoxPane){
            /**
             * Mouse pressed event handler function.
             * @property onMousePressed
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.onMousePressed = {};
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
        if(!P.BoxPane){
            /**
             * The foreground color of this component.
             * @property foreground
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.foreground = {};
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
        if(!P.BoxPane){
            /**
             * An error message of this component.
             * Validation procedure may set this property and subsequent focus lost event will clear it.
             * @property error
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.error = '';
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
        if(!P.BoxPane){
            /**
             * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
             * @property enabled
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.enabled = true;
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
        if(!P.BoxPane){
            /**
             * Component moved event handler function.
             * @property onComponentMoved
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.onComponentMoved = {};
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
        if(!P.BoxPane){
            /**
             * Component added event hanler function.
             * @property onComponentAdded
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.onComponentAdded = {};
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
        if(!P.BoxPane){
            /**
             * <code>PopupMenu</code> that assigned for this component.
             * @property componentPopupMenu
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.componentPopupMenu = {};
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
        if(!P.BoxPane){
            /**
             * Vertical coordinate of the component.
             * @property top
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.top = 0;
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
        if(!P.BoxPane){
            /**
             * Gets the container's children components.
             * @property children
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.children = [];
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
        if(!P.BoxPane){
            /**
             * Component resized event handler function.
             * @property onComponentResized
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.onComponentResized = {};
        }
        Object.defineProperty(this, "vgap", {
            get: function() {
                var value = delegate.vgap;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.vgap = P.boxAsJava(aValue);
            }
        });
        if(!P.BoxPane){
            /**
             * Box vertical gap between components.
             * @property vgap
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.vgap = 0;
        }
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parentWidget;
                return P.boxAsJs(value);
            }
        });
        if(!P.BoxPane){
            /**
             * Parent container of this widget.
             * @property parentWidget
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.parent = {};
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
        if(!P.BoxPane){
            /**
             * Mouse entered over the component event handler function.
             * @property onMouseEntered
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.onMouseEntered = {};
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
        if(!P.BoxPane){
            /**
             * The tooltip string that has been set with.
             * @property toolTipText
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.toolTipText = '';
        }
        Object.defineProperty(this, "hgap", {
            get: function() {
                var value = delegate.hgap;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.hgap = P.boxAsJava(aValue);
            }
        });
        if(!P.BoxPane){
            /**
             * Box horizontal gap between components.
             * @property hgap
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.hgap = 0;
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
        if(!P.BoxPane){
            /**
             * Height of the component.
             * @property height
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.height = 0;
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
        if(!P.BoxPane){
            /**
             * Component shown event handler function.
             * @property onComponentShown
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.onComponentShown = {};
        }
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });
        if(!P.BoxPane){
            /**
             * Native API. Returns low level html element. Applicable only in HTML5 client.
             * @property element
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.element = {};
        }
        Object.defineProperty(this, "orientation", {
            get: function() {
                var value = delegate.orientation;
                return P.boxAsJs(value);
            }
        });
        if(!P.BoxPane){
            /**
             * Box orientation of this container.
             * @property orientation
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.orientation = 0;
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
        if(!P.BoxPane){
            /**
             * Mouse moved event handler function.
             * @property onMouseMoved
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.onMouseMoved = {};
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
        if(!P.BoxPane){
            /**
             * True if this component is completely opaque.
             * @property opaque
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.opaque = true;
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
        if(!P.BoxPane){
            /**
             * Determines whether this component should be visible when its parent is visible.
             * @property visible
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.visible = true;
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
        if(!P.BoxPane){
            /**
             * Component hidden event handler function.
             * @property onComponentHidden
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.onComponentHidden = {};
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
        if(!P.BoxPane){
            /**
             * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
             * @property nextFocusableComponent
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.nextFocusableComponent = {};
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
        if(!P.BoxPane){
            /**
             * Main action performed event handler function.
             * @property onActionPerformed
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.onActionPerformed = {};
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
        if(!P.BoxPane){
            /**
             * Key released event handler function.
             * @property onKeyReleased
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.onKeyReleased = {};
        }
        Object.defineProperty(this, "count", {
            get: function() {
                var value = delegate.count;
                return P.boxAsJs(value);
            }
        });
        if(!P.BoxPane){
            /**
             * Gets the number of components in this panel.
             * @property count
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.count = 0;
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
        if(!P.BoxPane){
            /**
             * Determines whether this component may be focused.
             * @property focusable
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.focusable = true;
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
        if(!P.BoxPane){
            /**
             * Key typed event handler function.
             * @property onKeyTyped
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.onKeyTyped = {};
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
        if(!P.BoxPane){
            /**
             * Mouse wheel moved event handler function.
             * @property onMouseWheelMoved
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.onMouseWheelMoved = {};
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
        if(!P.BoxPane){
            /**
             * Component removed event handler function.
             * @property onComponentRemoved
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.onComponentRemoved = {};
        }
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });
        if(!P.BoxPane){
            /**
             * Native API. Returns low level swing component. Applicable only in J2SE swing client.
             * @property component
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.component = {};
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
        if(!P.BoxPane){
            /**
             * Keyboard focus gained by the component event.
             * @property onFocusGained
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.onFocusGained = {};
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
        if(!P.BoxPane){
            /**
             * Horizontal coordinate of the component.
             * @property left
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.left = 0;
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
        if(!P.BoxPane){
            /**
             * The background color of this component.
             * @property background
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.background = {};
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
        if(!P.BoxPane){
            /**
             * Mouse clicked event handler function.
             * @property onMouseClicked
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.onMouseClicked = {};
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
        if(!P.BoxPane){
            /**
             * Mouse exited over the component event handler function.
             * @property onMouseExited
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.onMouseExited = {};
        }
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.BoxPane){
            /**
             * Gets name of this component.
             * @property name
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.name = '';
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
        if(!P.BoxPane){
            /**
             * Width of the component.
             * @property width
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.width = 0;
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
        if(!P.BoxPane){
            /**
             * The font of this component.
             * @property font
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.font = {};
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
        if(!P.BoxPane){
            /**
             * Key pressed event handler function.
             * @property onKeyPressed
             * @memberOf BoxPane
             */
            P.BoxPane.prototype.onKeyPressed = {};
        }
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    };
        /**
         * Appends the specified component to the end of this container.
         * @param component the component to add
         * @method add
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.add = function(component) {
            var delegate = this.unwrap();
            var value = delegate.add(P.boxAsJava(component));
            return P.boxAsJs(value);
        };

        /**
         * Removes the specified component from this container.
         * @param component the component to remove
         * @method remove
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.remove = function(component) {
            var delegate = this.unwrap();
            var value = delegate.remove(P.boxAsJava(component));
            return P.boxAsJs(value);
        };

        /**
         * Removes all the components from this container.
         * @method clear
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.clear = function() {
            var delegate = this.unwrap();
            var value = delegate.clear();
            return P.boxAsJs(value);
        };

        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.focus = function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        };

})();