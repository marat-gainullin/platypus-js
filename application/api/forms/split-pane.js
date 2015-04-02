(function() {
    var javaClass = Java.type("com.eas.client.forms.containers.SplitPane");
    javaClass.setPublisher(function(aDelegate) {
        return new P.SplitPane(null, aDelegate);
    });
    
    /**
     * <code>SplitPane</code> is used to divide two (and only two) components. By default uses horisontal orientation.
     * @param orientation <code>Orientation.HORIZONTAL</code> or <code>Orientation.VERTICAL</code> (optional).
     * @constructor SplitPane SplitPane
     */
    P.SplitPane = function (orientation) {
        var maxArgs = 1;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 1 ? new javaClass(P.boxAsJava(orientation))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.SplitPane.superclass)
            P.SplitPane.superclass.constructor.apply(this, arguments);
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
        if(!P.SplitPane){
            /**
             * The mouse <code>Cursor</code> over this component.
             * @property cursor
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.cursor = {};
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
        if(!P.SplitPane){
            /**
             * Mouse dragged event handler function.
             * @property onMouseDragged
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.onMouseDragged = {};
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
        if(!P.SplitPane){
            /**
             * Mouse released event handler function.
             * @property onMouseReleased
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.onMouseReleased = {};
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
        if(!P.SplitPane){
            /**
             * Keyboard focus lost by the component event handler function.
             * @property onFocusLost
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.onFocusLost = {};
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
        if(!P.SplitPane){
            /**
             * Mouse pressed event handler function.
             * @property onMousePressed
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.onMousePressed = {};
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
        if(!P.SplitPane){
            /**
             * The foreground color of this component.
             * @property foreground
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.foreground = {};
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
        if(!P.SplitPane){
            /**
             * An error message of this component.
             * Validation procedure may set this property and subsequent focus lost event will clear it.
             * @property error
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.error = '';
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
        if(!P.SplitPane){
            /**
             * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
             * @property enabled
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.enabled = true;
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
        if(!P.SplitPane){
            /**
             * Component moved event handler function.
             * @property onComponentMoved
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.onComponentMoved = {};
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
        if(!P.SplitPane){
            /**
             * Component added event hanler function.
             * @property onComponentAdded
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.onComponentAdded = {};
        }
        Object.defineProperty(this, "dividerLocation", {
            get: function() {
                var value = delegate.dividerLocation;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.dividerLocation = P.boxAsJava(aValue);
            }
        });
        if(!P.SplitPane){
            /**
             * The split pane divider's location in pixels.
             * @property dividerLocation
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.dividerLocation = 0;
        }
        Object.defineProperty(this, "secondComponent", {
            get: function() {
                var value = delegate.secondComponent;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.secondComponent = P.boxAsJava(aValue);
            }
        });
        if(!P.SplitPane){
            /**
             * The second component of the container.
             * @property secondComponent
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.secondComponent = {};
        }
        Object.defineProperty(this, "firstComponent", {
            get: function() {
                var value = delegate.firstComponent;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.firstComponent = P.boxAsJava(aValue);
            }
        });
        if(!P.SplitPane){
            /**
             * The first component of the container.
             * @property firstComponent
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.firstComponent = {};
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
        if(!P.SplitPane){
            /**
             * <code>PopupMenu</code> that assigned for this component.
             * @property componentPopupMenu
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.componentPopupMenu = {};
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
        if(!P.SplitPane){
            /**
             * Vertical coordinate of the component.
             * @property top
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.top = 0;
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
        if(!P.SplitPane){
            /**
             * Gets the container's children components.
             * @property children
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.children = [];
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
        if(!P.SplitPane){
            /**
             * Component resized event handler function.
             * @property onComponentResized
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.onComponentResized = {};
        }
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parentWidget;
                return P.boxAsJs(value);
            }
        });
        if(!P.SplitPane){
            /**
             * Parent container of this widget.
             * @property parentWidget
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.parent = {};
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
        if(!P.SplitPane){
            /**
             * Mouse entered over the component event handler function.
             * @property onMouseEntered
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.onMouseEntered = {};
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
        if(!P.SplitPane){
            /**
             * The tooltip string that has been set with.
             * @property toolTipText
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.toolTipText = '';
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
        if(!P.SplitPane){
            /**
             * Height of the component.
             * @property height
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.height = 0;
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
        if(!P.SplitPane){
            /**
             * Component shown event handler function.
             * @property onComponentShown
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.onComponentShown = {};
        }
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });
        if(!P.SplitPane){
            /**
             * Native API. Returns low level html element. Applicable only in HTML5 client.
             * @property element
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.element = {};
        }
        Object.defineProperty(this, "orientation", {
            get: function() {
                var value = delegate.orientation;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.orientation = P.boxAsJava(aValue);
            }
        });
        if(!P.SplitPane){
            /**
             * The orientation of the container.
             * @property orientation
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.orientation = 0;
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
        if(!P.SplitPane){
            /**
             * Mouse moved event handler function.
             * @property onMouseMoved
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.onMouseMoved = {};
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
        if(!P.SplitPane){
            /**
             * True if this component is completely opaque.
             * @property opaque
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.opaque = true;
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
        if(!P.SplitPane){
            /**
             * Determines whether this component should be visible when its parent is visible.
             * @property visible
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.visible = true;
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
        if(!P.SplitPane){
            /**
             * Component hidden event handler function.
             * @property onComponentHidden
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.onComponentHidden = {};
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
        if(!P.SplitPane){
            /**
             * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
             * @property nextFocusableComponent
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.nextFocusableComponent = {};
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
        if(!P.SplitPane){
            /**
             * Main action performed event handler function.
             * @property onActionPerformed
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.onActionPerformed = {};
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
        if(!P.SplitPane){
            /**
             * Key released event handler function.
             * @property onKeyReleased
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.onKeyReleased = {};
        }
        Object.defineProperty(this, "count", {
            get: function() {
                var value = delegate.count;
                return P.boxAsJs(value);
            }
        });
        if(!P.SplitPane){
            /**
             * Gets the number of components in this panel.
             * @property count
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.count = 0;
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
        if(!P.SplitPane){
            /**
             * Determines whether this component may be focused.
             * @property focusable
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.focusable = true;
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
        if(!P.SplitPane){
            /**
             * Key typed event handler function.
             * @property onKeyTyped
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.onKeyTyped = {};
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
        if(!P.SplitPane){
            /**
             * Mouse wheel moved event handler function.
             * @property onMouseWheelMoved
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.onMouseWheelMoved = {};
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
        if(!P.SplitPane){
            /**
             * Component removed event handler function.
             * @property onComponentRemoved
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.onComponentRemoved = {};
        }
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });
        if(!P.SplitPane){
            /**
             * Native API. Returns low level swing component. Applicable only in J2SE swing client.
             * @property component
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.component = {};
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
        if(!P.SplitPane){
            /**
             * Keyboard focus gained by the component event.
             * @property onFocusGained
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.onFocusGained = {};
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
        if(!P.SplitPane){
            /**
             * Horizontal coordinate of the component.
             * @property left
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.left = 0;
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
        if(!P.SplitPane){
            /**
             * The background color of this component.
             * @property background
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.background = {};
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
        if(!P.SplitPane){
            /**
             * Mouse clicked event handler function.
             * @property onMouseClicked
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.onMouseClicked = {};
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
        if(!P.SplitPane){
            /**
             * Mouse exited over the component event handler function.
             * @property onMouseExited
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.onMouseExited = {};
        }
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.SplitPane){
            /**
             * Gets name of this component.
             * @property name
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.name = '';
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
        if(!P.SplitPane){
            /**
             * Width of the component.
             * @property width
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.width = 0;
        }
        Object.defineProperty(this, "oneTouchExpandable", {
            get: function() {
                var value = delegate.oneTouchExpandable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.oneTouchExpandable = P.boxAsJava(aValue);
            }
        });
        if(!P.SplitPane){
            /**
             * <code>true</code> if the pane is one touch expandable.
             * @property oneTouchExpandable
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.oneTouchExpandable = true;
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
        if(!P.SplitPane){
            /**
             * The font of this component.
             * @property font
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.font = {};
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
        if(!P.SplitPane){
            /**
             * Key pressed event handler function.
             * @property onKeyPressed
             * @memberOf SplitPane
             */
            P.SplitPane.prototype.onKeyPressed = {};
        }
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    };
        /**
         * Appends the specified component to the end of this container.
         * @param component the component to add.
         * @method add
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.add = function(component) {
            var delegate = this.unwrap();
            var value = delegate.add(P.boxAsJava(component));
            return P.boxAsJs(value);
        };

        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.focus = function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        };

        /**
         * Gets the container's n-th component.
         * @param index the component's index in the container
         * @return the child component
         * @method child
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.child = function(index) {
            var delegate = this.unwrap();
            var value = delegate.child(P.boxAsJava(index));
            return P.boxAsJs(value);
        };

})();