(function() {
    var javaClass = Java.type("com.eas.client.forms.containers.BorderPane");
    javaClass.setPublisher(function(aDelegate) {
        return new P.BorderPane(null, null, aDelegate);
    });
    
    /**
     * A container with Border Layout.
     * @param hgap the horizontal gap (optional).
     * @param vgap the vertical gap (optional).
     * @constructor BorderPane BorderPane
     */
    P.BorderPane = function (hgap, vgap) {
        var maxArgs = 2;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 2 ? new javaClass(P.boxAsJava(hgap), P.boxAsJava(vgap))
            : arguments.length === 1 ? new javaClass(P.boxAsJava(hgap))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.BorderPane.superclass)
            P.BorderPane.superclass.constructor.apply(this, arguments);
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
        if(!P.BorderPane){
            /**
             * The mouse <code>Cursor</code> over this component.
             * @property cursor
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.cursor = {};
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
        if(!P.BorderPane){
            /**
             * Mouse dragged event handler function.
             * @property onMouseDragged
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.onMouseDragged = {};
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
        if(!P.BorderPane){
            /**
             * Mouse released event handler function.
             * @property onMouseReleased
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.onMouseReleased = {};
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
        if(!P.BorderPane){
            /**
             * Keyboard focus lost by the component event handler function.
             * @property onFocusLost
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.onFocusLost = {};
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
        if(!P.BorderPane){
            /**
             * Mouse pressed event handler function.
             * @property onMousePressed
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.onMousePressed = {};
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
        if(!P.BorderPane){
            /**
             * The foreground color of this component.
             * @property foreground
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.foreground = {};
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
        if(!P.BorderPane){
            /**
             * An error message of this component.
             * Validation procedure may set this property and subsequent focus lost event will clear it.
             * @property error
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.error = '';
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
        if(!P.BorderPane){
            /**
             * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
             * @property enabled
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.enabled = true;
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
        if(!P.BorderPane){
            /**
             * Component moved event handler function.
             * @property onComponentMoved
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.onComponentMoved = {};
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
        if(!P.BorderPane){
            /**
             * Component added event hanler function.
             * @property onComponentAdded
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.onComponentAdded = {};
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
        if(!P.BorderPane){
            /**
             * <code>PopupMenu</code> that assigned for this component.
             * @property componentPopupMenu
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.componentPopupMenu = {};
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
        if(!P.BorderPane){
            /**
             * Vertical coordinate of the component.
             * @property top
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.top = 0;
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
        if(!P.BorderPane){
            /**
             * Gets the container's children components.
             * @property children
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.children = [];
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
        if(!P.BorderPane){
            /**
             * Component resized event handler function.
             * @property onComponentResized
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.onComponentResized = {};
        }
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parentWidget;
                return P.boxAsJs(value);
            }
        });
        if(!P.BorderPane){
            /**
             * Parent container of this widget.
             * @property parentWidget
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.parent = {};
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
        if(!P.BorderPane){
            /**
             * Mouse entered over the component event handler function.
             * @property onMouseEntered
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.onMouseEntered = {};
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
        if(!P.BorderPane){
            /**
             * The tooltip string that has been set with.
             * @property toolTipText
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.toolTipText = '';
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
        if(!P.BorderPane){
            /**
             * Height of the component.
             * @property height
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.height = 0;
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
        if(!P.BorderPane){
            /**
             * Component shown event handler function.
             * @property onComponentShown
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.onComponentShown = {};
        }
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });
        if(!P.BorderPane){
            /**
             * Native API. Returns low level html element. Applicable only in HTML5 client.
             * @property element
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.element = {};
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
        if(!P.BorderPane){
            /**
             * Mouse moved event handler function.
             * @property onMouseMoved
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.onMouseMoved = {};
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
        if(!P.BorderPane){
            /**
             * True if this component is completely opaque.
             * @property opaque
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.opaque = true;
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
        if(!P.BorderPane){
            /**
             * Determines whether this component should be visible when its parent is visible.
             * @property visible
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.visible = true;
        }
        Object.defineProperty(this, "topComponent", {
            get: function() {
                var value = delegate.topComponent;
                return P.boxAsJs(value);
            }
        });
        if(!P.BorderPane){
            /**
             * The component added using HorizontalPosition.TOP constraint.
             * If no component at the container on this constraint then set to <code>null</code>.
             * @property topComponent
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.topComponent = {};
        }
        Object.defineProperty(this, "bottomComponent", {
            get: function() {
                var value = delegate.bottomComponent;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.bottomComponent = P.boxAsJava(aValue);
            }
        });
        if(!P.BorderPane){
            /**
             * The component added using HorizontalPosition.BOTTOM constraint.
             * If no component at the container on this constraint then set to <code>null</code>.
             * @property bottomComponent
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.bottomComponent = {};
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
        if(!P.BorderPane){
            /**
             * Component hidden event handler function.
             * @property onComponentHidden
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.onComponentHidden = {};
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
        if(!P.BorderPane){
            /**
             * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
             * @property nextFocusableComponent
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.nextFocusableComponent = {};
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
        if(!P.BorderPane){
            /**
             * Main action performed event handler function.
             * @property onActionPerformed
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.onActionPerformed = {};
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
        if(!P.BorderPane){
            /**
             * Key released event handler function.
             * @property onKeyReleased
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.onKeyReleased = {};
        }
        Object.defineProperty(this, "count", {
            get: function() {
                var value = delegate.count;
                return P.boxAsJs(value);
            }
        });
        if(!P.BorderPane){
            /**
             * Gets the number of components in this panel.
             * @property count
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.count = 0;
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
        if(!P.BorderPane){
            /**
             * Determines whether this component may be focused.
             * @property focusable
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.focusable = true;
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
        if(!P.BorderPane){
            /**
             * Key typed event handler function.
             * @property onKeyTyped
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.onKeyTyped = {};
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
        if(!P.BorderPane){
            /**
             * Mouse wheel moved event handler function.
             * @property onMouseWheelMoved
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.onMouseWheelMoved = {};
        }
        Object.defineProperty(this, "rightComponent", {
            get: function() {
                var value = delegate.rightComponent;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.rightComponent = P.boxAsJava(aValue);
            }
        });
        if(!P.BorderPane){
            /**
             * The component added using HorizontalPosition.RIGHT constraint.
             * If no component at the container on this constraint then set to <code>null</code>.
             * @property rightComponent
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.rightComponent = {};
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
        if(!P.BorderPane){
            /**
             * Component removed event handler function.
             * @property onComponentRemoved
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.onComponentRemoved = {};
        }
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });
        if(!P.BorderPane){
            /**
             * Native API. Returns low level swing component. Applicable only in J2SE swing client.
             * @property component
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.component = {};
        }
        Object.defineProperty(this, "leftComponent", {
            get: function() {
                var value = delegate.leftComponent;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.leftComponent = P.boxAsJava(aValue);
            }
        });
        if(!P.BorderPane){
            /**
             * The component added using HorizontalPosition.LEFT constraint.
             * If no component at this constraint then set to <code>null</code>.
             * @property leftComponent
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.leftComponent = {};
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
        if(!P.BorderPane){
            /**
             * Keyboard focus gained by the component event.
             * @property onFocusGained
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.onFocusGained = {};
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
        if(!P.BorderPane){
            /**
             * Horizontal coordinate of the component.
             * @property left
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.left = 0;
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
        if(!P.BorderPane){
            /**
             * The background color of this component.
             * @property background
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.background = {};
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
        if(!P.BorderPane){
            /**
             * Mouse clicked event handler function.
             * @property onMouseClicked
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.onMouseClicked = {};
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
        if(!P.BorderPane){
            /**
             * Mouse exited over the component event handler function.
             * @property onMouseExited
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.onMouseExited = {};
        }
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.BorderPane){
            /**
             * Gets name of this component.
             * @property name
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.name = '';
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
        if(!P.BorderPane){
            /**
             * Width of the component.
             * @property width
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.width = 0;
        }
        Object.defineProperty(this, "centerComponent", {
            get: function() {
                var value = delegate.centerComponent;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.centerComponent = P.boxAsJava(aValue);
            }
        });
        if(!P.BorderPane){
            /**
             * The component added using HorizontalPosition.CENTER constraint.
             * If no component at the container on this constraint then set to <code>null</code>.
             * @property centerComponent
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.centerComponent = {};
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
        if(!P.BorderPane){
            /**
             * The font of this component.
             * @property font
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.font = {};
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
        if(!P.BorderPane){
            /**
             * Key pressed event handler function.
             * @property onKeyPressed
             * @memberOf BorderPane
             */
            P.BorderPane.prototype.onKeyPressed = {};
        }
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    };
        /**
         * Appends the specified component to this container on the specified placement.
         * @param component the component to add.
         * @param place the placement in the container: <code>HorizontalPosition.LEFT</code>, <code>HorizontalPosition.CENTER</code>, <code>HorizontalPosition.RIGHT</code>, <code>VerticalPosition.TOP</code> or <code>VerticalPosition.BOTTOM</code> (optional).
         * @param size the size of the component by the provided place direction (optional).
         * @method add
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.add = function(component, place, size) {
            var delegate = this.unwrap();
            var value = delegate.add(P.boxAsJava(component), P.boxAsJava(place), P.boxAsJava(size));
            return P.boxAsJs(value);
        };

        /**
         * Removes the specified component from this container.
         * @param component the component to remove
         * @method remove
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.remove = function(component) {
            var delegate = this.unwrap();
            var value = delegate.remove(P.boxAsJava(component));
            return P.boxAsJs(value);
        };

        /**
         * Removes all the components from this container.
         * @method clear
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.clear = function() {
            var delegate = this.unwrap();
            var value = delegate.clear();
            return P.boxAsJs(value);
        };

        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.focus = function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        };

})();