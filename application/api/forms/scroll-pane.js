(function() {
    var javaClass = Java.type("com.eas.client.forms.containers.ScrollPane");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ScrollPane(null, aDelegate);
    });
    
    /**
     * Provides a scrollable view of a lightweight component.
     * @param view the component to display in the scrollpane's viewport (optional)
     * @constructor ScrollPane ScrollPane
     */
    P.ScrollPane = function (view) {
        var maxArgs = 1;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 1 ? new javaClass(P.boxAsJava(view))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.ScrollPane.superclass)
            P.ScrollPane.superclass.constructor.apply(this, arguments);
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
        if(!P.ScrollPane){
            /**
             * The mouse <code>Cursor</code> over this component.
             * @property cursor
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.cursor = {};
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
        if(!P.ScrollPane){
            /**
             * Mouse dragged event handler function.
             * @property onMouseDragged
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.onMouseDragged = {};
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
        if(!P.ScrollPane){
            /**
             * Mouse released event handler function.
             * @property onMouseReleased
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.onMouseReleased = {};
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
        if(!P.ScrollPane){
            /**
             * Keyboard focus lost by the component event handler function.
             * @property onFocusLost
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.onFocusLost = {};
        }
        Object.defineProperty(this, "horizontalScrollBarPolicy", {
            get: function() {
                var value = delegate.horizontalScrollBarPolicy;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.horizontalScrollBarPolicy = P.boxAsJava(aValue);
            }
        });
        if(!P.ScrollPane){
            /**
             * Generated property jsDoc.
             * @property horizontalScrollBarPolicy
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.horizontalScrollBarPolicy = 0;
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
        if(!P.ScrollPane){
            /**
             * Mouse pressed event handler function.
             * @property onMousePressed
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.onMousePressed = {};
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
        if(!P.ScrollPane){
            /**
             * The foreground color of this component.
             * @property foreground
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.foreground = {};
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
        if(!P.ScrollPane){
            /**
             * An error message of this component.
             * Validation procedure may set this property and subsequent focus lost event will clear it.
             * @property error
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.error = '';
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
        if(!P.ScrollPane){
            /**
             * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
             * @property enabled
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.enabled = true;
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
        if(!P.ScrollPane){
            /**
             * Component moved event handler function.
             * @property onComponentMoved
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.onComponentMoved = {};
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
        if(!P.ScrollPane){
            /**
             * Component added event hanler function.
             * @property onComponentAdded
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.onComponentAdded = {};
        }
        Object.defineProperty(this, "verticalScrollBarPolicy", {
            get: function() {
                var value = delegate.verticalScrollBarPolicy;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.verticalScrollBarPolicy = P.boxAsJava(aValue);
            }
        });
        if(!P.ScrollPane){
            /**
             * Generated property jsDoc.
             * @property verticalScrollBarPolicy
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.verticalScrollBarPolicy = 0;
        }
        Object.defineProperty(this, "view", {
            get: function() {
                var value = delegate.view;
                return P.boxAsJs(value);
            }
        });
        if(!P.ScrollPane){
            /**
             * The specified component as the scroll pane view.
             * @property view
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.view = {};
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
        if(!P.ScrollPane){
            /**
             * <code>PopupMenu</code> that assigned for this component.
             * @property componentPopupMenu
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.componentPopupMenu = {};
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
        if(!P.ScrollPane){
            /**
             * Vertical coordinate of the component.
             * @property top
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.top = 0;
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
        if(!P.ScrollPane){
            /**
             * Generated property jsDoc.
             * @property children
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.children = [];
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
        if(!P.ScrollPane){
            /**
             * Component resized event handler function.
             * @property onComponentResized
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.onComponentResized = {};
        }
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parentWidget;
                return P.boxAsJs(value);
            }
        });
        if(!P.ScrollPane){
            /**
             * Parent container of this widget.
             * @property parentWidget
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.parent = {};
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
        if(!P.ScrollPane){
            /**
             * Mouse entered over the component event handler function.
             * @property onMouseEntered
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.onMouseEntered = {};
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
        if(!P.ScrollPane){
            /**
             * The tooltip string that has been set with.
             * @property toolTipText
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.toolTipText = '';
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
        if(!P.ScrollPane){
            /**
             * Height of the component.
             * @property height
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.height = 0;
        }
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });
        if(!P.ScrollPane){
            /**
             * Native API. Returns low level html element. Applicable only in HTML5 client.
             * @property element
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.element = {};
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
        if(!P.ScrollPane){
            /**
             * Component shown event handler function.
             * @property onComponentShown
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.onComponentShown = {};
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
        if(!P.ScrollPane){
            /**
             * Mouse moved event handler function.
             * @property onMouseMoved
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.onMouseMoved = {};
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
        if(!P.ScrollPane){
            /**
             * True if this component is completely opaque.
             * @property opaque
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.opaque = true;
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
        if(!P.ScrollPane){
            /**
             * Determines whether this component should be visible when its parent is visible.
             * @property visible
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.visible = true;
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
        if(!P.ScrollPane){
            /**
             * Component hidden event handler function.
             * @property onComponentHidden
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.onComponentHidden = {};
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
        if(!P.ScrollPane){
            /**
             * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
             * @property nextFocusableComponent
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.nextFocusableComponent = {};
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
        if(!P.ScrollPane){
            /**
             * Key released event handler function.
             * @property onKeyReleased
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.onKeyReleased = {};
        }
        Object.defineProperty(this, "count", {
            get: function() {
                var value = delegate.count;
                return P.boxAsJs(value);
            }
        });
        if(!P.ScrollPane){
            /**
             * Gets the number of components in this panel.
             * @property count
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.count = 0;
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
        if(!P.ScrollPane){
            /**
             * Main action performed event handler function.
             * @property onActionPerformed
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.onActionPerformed = {};
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
        if(!P.ScrollPane){
            /**
             * Determines whether this component may be focused.
             * @property focusable
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.focusable = true;
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
        if(!P.ScrollPane){
            /**
             * Key typed event handler function.
             * @property onKeyTyped
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.onKeyTyped = {};
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
        if(!P.ScrollPane){
            /**
             * Mouse wheel moved event handler function.
             * @property onMouseWheelMoved
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.onMouseWheelMoved = {};
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
        if(!P.ScrollPane){
            /**
             * Component removed event handler function.
             * @property onComponentRemoved
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.onComponentRemoved = {};
        }
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });
        if(!P.ScrollPane){
            /**
             * Native API. Returns low level swing component. Applicable only in J2SE swing client.
             * @property component
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.component = {};
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
        if(!P.ScrollPane){
            /**
             * Keyboard focus gained by the component event.
             * @property onFocusGained
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.onFocusGained = {};
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
        if(!P.ScrollPane){
            /**
             * Horizontal coordinate of the component.
             * @property left
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.left = 0;
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
        if(!P.ScrollPane){
            /**
             * The background color of this component.
             * @property background
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.background = {};
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
        if(!P.ScrollPane){
            /**
             * Mouse clicked event handler function.
             * @property onMouseClicked
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.onMouseClicked = {};
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
        if(!P.ScrollPane){
            /**
             * Mouse exited over the component event handler function.
             * @property onMouseExited
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.onMouseExited = {};
        }
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.ScrollPane){
            /**
             * Gets name of this component.
             * @property name
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.name = '';
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
        if(!P.ScrollPane){
            /**
             * Width of the component.
             * @property width
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.width = 0;
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
        if(!P.ScrollPane){
            /**
             * The font of this component.
             * @property font
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.font = {};
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
        if(!P.ScrollPane){
            /**
             * Key pressed event handler function.
             * @property onKeyPressed
             * @memberOf ScrollPane
             */
            P.ScrollPane.prototype.onKeyPressed = {};
        }
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    };
        /**
         * Sets the specified component as the scroll's view, replacing old view component.
         * @param component the component to add
         * @method add
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.add = function(component) {
            var delegate = this.unwrap();
            var value = delegate.add(P.boxAsJava(component));
            return P.boxAsJs(value);
        };

        /**
         * Removes the specified component from this container.
         * @param component the component to remove
         * @method remove
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.remove = function(component) {
            var delegate = this.unwrap();
            var value = delegate.remove(P.boxAsJava(component));
            return P.boxAsJs(value);
        };

        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.focus = function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        };

        /**
         * Returns child component by index. For the ScrollPane allways returns view component
         * @method child
         * @memberOf ScrollPane
         * @param index Index of compoentnt to return. Ignored. */
        P.ScrollPane.prototype.child = function(index) {
            var delegate = this.unwrap();
            var value = delegate.child(P.boxAsJava(index));
            return P.boxAsJs(value);
        };

})();