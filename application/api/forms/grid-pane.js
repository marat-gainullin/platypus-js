(function() {
    var javaClass = Java.type("com.eas.client.forms.containers.GridPane");
    javaClass.setPublisher(function(aDelegate) {
        return new P.GridPane(null, null, null, null, aDelegate);
    });
    
    /**
     * A container with Grid Layout.
     * @param rows the number of grid rows.
     * @param cols the number of grid columns.
     * @param hgap the horizontal gap (optional).
     * @param vgap the vertical gap (optional).
     * @constructor GridPane GridPane
     */
    P.GridPane = function (rows, cols, hgap, vgap) {
        var maxArgs = 4;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 4 ? new javaClass(P.boxAsJava(rows), P.boxAsJava(cols), P.boxAsJava(hgap), P.boxAsJava(vgap))
            : arguments.length === 3 ? new javaClass(P.boxAsJava(rows), P.boxAsJava(cols), P.boxAsJava(hgap))
            : arguments.length === 2 ? new javaClass(P.boxAsJava(rows), P.boxAsJava(cols))
            : arguments.length === 1 ? new javaClass(P.boxAsJava(rows))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.GridPane.superclass)
            P.GridPane.superclass.constructor.apply(this, arguments);
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
        if(!P.GridPane){
            /**
             * The mouse <code>Cursor</code> over this component.
             * @property cursor
             * @memberOf GridPane
             */
            P.GridPane.prototype.cursor = {};
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
        if(!P.GridPane){
            /**
             * Mouse dragged event handler function.
             * @property onMouseDragged
             * @memberOf GridPane
             */
            P.GridPane.prototype.onMouseDragged = {};
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
        if(!P.GridPane){
            /**
             * Mouse released event handler function.
             * @property onMouseReleased
             * @memberOf GridPane
             */
            P.GridPane.prototype.onMouseReleased = {};
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
        if(!P.GridPane){
            /**
             * Keyboard focus lost by the component event handler function.
             * @property onFocusLost
             * @memberOf GridPane
             */
            P.GridPane.prototype.onFocusLost = {};
        }
        Object.defineProperty(this, "columns", {
            get: function() {
                var value = delegate.columns;
                return P.boxAsJs(value);
            }
        });
        if(!P.GridPane){
            /**
             * Generated property jsDoc.
             * @property columns
             * @memberOf GridPane
             */
            P.GridPane.prototype.columns = 0;
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
        if(!P.GridPane){
            /**
             * Mouse pressed event handler function.
             * @property onMousePressed
             * @memberOf GridPane
             */
            P.GridPane.prototype.onMousePressed = {};
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
        if(!P.GridPane){
            /**
             * The foreground color of this component.
             * @property foreground
             * @memberOf GridPane
             */
            P.GridPane.prototype.foreground = {};
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
        if(!P.GridPane){
            /**
             * An error message of this component.
             * Validation procedure may set this property and subsequent focus lost event will clear it.
             * @property error
             * @memberOf GridPane
             */
            P.GridPane.prototype.error = '';
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
        if(!P.GridPane){
            /**
             * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
             * @property enabled
             * @memberOf GridPane
             */
            P.GridPane.prototype.enabled = true;
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
        if(!P.GridPane){
            /**
             * Component moved event handler function.
             * @property onComponentMoved
             * @memberOf GridPane
             */
            P.GridPane.prototype.onComponentMoved = {};
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
        if(!P.GridPane){
            /**
             * Component added event hanler function.
             * @property onComponentAdded
             * @memberOf GridPane
             */
            P.GridPane.prototype.onComponentAdded = {};
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
        if(!P.GridPane){
            /**
             * <code>PopupMenu</code> that assigned for this component.
             * @property componentPopupMenu
             * @memberOf GridPane
             */
            P.GridPane.prototype.componentPopupMenu = {};
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
        if(!P.GridPane){
            /**
             * Vertical coordinate of the component.
             * @property top
             * @memberOf GridPane
             */
            P.GridPane.prototype.top = 0;
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
        if(!P.GridPane){
            /**
             * Generated property jsDoc.
             * @property children
             * @memberOf GridPane
             */
            P.GridPane.prototype.children = [];
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
        if(!P.GridPane){
            /**
             * Component resized event handler function.
             * @property onComponentResized
             * @memberOf GridPane
             */
            P.GridPane.prototype.onComponentResized = {};
        }
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parentWidget;
                return P.boxAsJs(value);
            }
        });
        if(!P.GridPane){
            /**
             * Parent container of this widget.
             * @property parentWidget
             * @memberOf GridPane
             */
            P.GridPane.prototype.parent = {};
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
        if(!P.GridPane){
            /**
             * Mouse entered over the component event handler function.
             * @property onMouseEntered
             * @memberOf GridPane
             */
            P.GridPane.prototype.onMouseEntered = {};
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
        if(!P.GridPane){
            /**
             * The tooltip string that has been set with.
             * @property toolTipText
             * @memberOf GridPane
             */
            P.GridPane.prototype.toolTipText = '';
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
        if(!P.GridPane){
            /**
             * Height of the component.
             * @property height
             * @memberOf GridPane
             */
            P.GridPane.prototype.height = 0;
        }
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });
        if(!P.GridPane){
            /**
             * Native API. Returns low level html element. Applicable only in HTML5 client.
             * @property element
             * @memberOf GridPane
             */
            P.GridPane.prototype.element = {};
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
        if(!P.GridPane){
            /**
             * Component shown event handler function.
             * @property onComponentShown
             * @memberOf GridPane
             */
            P.GridPane.prototype.onComponentShown = {};
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
        if(!P.GridPane){
            /**
             * Mouse moved event handler function.
             * @property onMouseMoved
             * @memberOf GridPane
             */
            P.GridPane.prototype.onMouseMoved = {};
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
        if(!P.GridPane){
            /**
             * True if this component is completely opaque.
             * @property opaque
             * @memberOf GridPane
             */
            P.GridPane.prototype.opaque = true;
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
        if(!P.GridPane){
            /**
             * Determines whether this component should be visible when its parent is visible.
             * @property visible
             * @memberOf GridPane
             */
            P.GridPane.prototype.visible = true;
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
        if(!P.GridPane){
            /**
             * Component hidden event handler function.
             * @property onComponentHidden
             * @memberOf GridPane
             */
            P.GridPane.prototype.onComponentHidden = {};
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
        if(!P.GridPane){
            /**
             * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
             * @property nextFocusableComponent
             * @memberOf GridPane
             */
            P.GridPane.prototype.nextFocusableComponent = {};
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
        if(!P.GridPane){
            /**
             * Key released event handler function.
             * @property onKeyReleased
             * @memberOf GridPane
             */
            P.GridPane.prototype.onKeyReleased = {};
        }
        Object.defineProperty(this, "count", {
            get: function() {
                var value = delegate.count;
                return P.boxAsJs(value);
            }
        });
        if(!P.GridPane){
            /**
             * Generated property jsDoc.
             * @property count
             * @memberOf GridPane
             */
            P.GridPane.prototype.count = 0;
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
        if(!P.GridPane){
            /**
             * Main action performed event handler function.
             * @property onActionPerformed
             * @memberOf GridPane
             */
            P.GridPane.prototype.onActionPerformed = {};
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
        if(!P.GridPane){
            /**
             * Determines whether this component may be focused.
             * @property focusable
             * @memberOf GridPane
             */
            P.GridPane.prototype.focusable = true;
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
        if(!P.GridPane){
            /**
             * Key typed event handler function.
             * @property onKeyTyped
             * @memberOf GridPane
             */
            P.GridPane.prototype.onKeyTyped = {};
        }
        Object.defineProperty(this, "rows", {
            get: function() {
                var value = delegate.rows;
                return P.boxAsJs(value);
            }
        });
        if(!P.GridPane){
            /**
             * Generated property jsDoc.
             * @property rows
             * @memberOf GridPane
             */
            P.GridPane.prototype.rows = 0;
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
        if(!P.GridPane){
            /**
             * Mouse wheel moved event handler function.
             * @property onMouseWheelMoved
             * @memberOf GridPane
             */
            P.GridPane.prototype.onMouseWheelMoved = {};
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
        if(!P.GridPane){
            /**
             * Component removed event handler function.
             * @property onComponentRemoved
             * @memberOf GridPane
             */
            P.GridPane.prototype.onComponentRemoved = {};
        }
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });
        if(!P.GridPane){
            /**
             * Native API. Returns low level swing component. Applicable only in J2SE swing client.
             * @property component
             * @memberOf GridPane
             */
            P.GridPane.prototype.component = {};
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
        if(!P.GridPane){
            /**
             * Keyboard focus gained by the component event.
             * @property onFocusGained
             * @memberOf GridPane
             */
            P.GridPane.prototype.onFocusGained = {};
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
        if(!P.GridPane){
            /**
             * Horizontal coordinate of the component.
             * @property left
             * @memberOf GridPane
             */
            P.GridPane.prototype.left = 0;
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
        if(!P.GridPane){
            /**
             * The background color of this component.
             * @property background
             * @memberOf GridPane
             */
            P.GridPane.prototype.background = {};
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
        if(!P.GridPane){
            /**
             * Mouse clicked event handler function.
             * @property onMouseClicked
             * @memberOf GridPane
             */
            P.GridPane.prototype.onMouseClicked = {};
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
        if(!P.GridPane){
            /**
             * Mouse exited over the component event handler function.
             * @property onMouseExited
             * @memberOf GridPane
             */
            P.GridPane.prototype.onMouseExited = {};
        }
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.GridPane){
            /**
             * Gets name of this component.
             * @property name
             * @memberOf GridPane
             */
            P.GridPane.prototype.name = '';
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
        if(!P.GridPane){
            /**
             * Width of the component.
             * @property width
             * @memberOf GridPane
             */
            P.GridPane.prototype.width = 0;
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
        if(!P.GridPane){
            /**
             * The font of this component.
             * @property font
             * @memberOf GridPane
             */
            P.GridPane.prototype.font = {};
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
        if(!P.GridPane){
            /**
             * Key pressed event handler function.
             * @property onKeyPressed
             * @memberOf GridPane
             */
            P.GridPane.prototype.onKeyPressed = {};
        }
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    };
        /**
         * Appends the specified component to the end of this container.
         * @param component the component to add
         * @param row the row of the component
         * @param column the column of the component
         * @method add
         * @memberOf GridPane
         */
        P.GridPane.prototype.add = function(component, row, column) {
            var delegate = this.unwrap();
            var value = delegate.add(P.boxAsJava(component), P.boxAsJava(row), P.boxAsJava(column));
            return P.boxAsJs(value);
        };

        /**
         * Removes the specified component from this container.
         * @param component the component to remove
         * @method remove
         * @memberOf GridPane
         */
        P.GridPane.prototype.remove = function(component) {
            var delegate = this.unwrap();
            var value = delegate.remove(P.boxAsJava(component));
            return P.boxAsJs(value);
        };

        /**
         * Removes all the components from this container.
         * @method clear
         * @memberOf GridPane
         */
        P.GridPane.prototype.clear = function() {
            var delegate = this.unwrap();
            var value = delegate.clear();
            return P.boxAsJs(value);
        };

        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf GridPane
         */
        P.GridPane.prototype.focus = function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        };

        /**
         * Gets the component with the specified row and column.
         * @param row the row of the component
         * @param column the column of the component
         * @method child
         * @memberOf GridPane
         */
        P.GridPane.prototype.child = function(row, column) {
            var delegate = this.unwrap();
            var value = delegate.child(P.boxAsJava(row), P.boxAsJava(column));
            return P.boxAsJs(value);
        };

})();