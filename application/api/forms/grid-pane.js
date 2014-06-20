(function() {
    var javaClass = Java.type("com.eas.client.forms.api.containers.GridPane");
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
    P.GridPane = function GridPane(rows, cols, hgap, vgap) {
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
        if(GridPane.superclass)
            GridPane.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "GridPane", {value: GridPane});
    Object.defineProperty(GridPane.prototype, "cursor", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.cursor;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.cursor = P.boxAsJava(aValue);
        }
    });
    if(!GridPane){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf GridPane
         */
        P.GridPane.prototype.cursor = {};
    }
    Object.defineProperty(GridPane.prototype, "onMouseDragged", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onMouseDragged;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onMouseDragged = P.boxAsJava(aValue);
        }
    });
    if(!GridPane){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf GridPane
         */
        P.GridPane.prototype.onMouseDragged = {};
    }
    Object.defineProperty(GridPane.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!GridPane){
        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf GridPane
         */
        P.GridPane.prototype.parent = {};
    }
    Object.defineProperty(GridPane.prototype, "onMouseReleased", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onMouseReleased;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onMouseReleased = P.boxAsJava(aValue);
        }
    });
    if(!GridPane){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf GridPane
         */
        P.GridPane.prototype.onMouseReleased = {};
    }
    Object.defineProperty(GridPane.prototype, "onFocusLost", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onFocusLost;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onFocusLost = P.boxAsJava(aValue);
        }
    });
    if(!GridPane){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf GridPane
         */
        P.GridPane.prototype.onFocusLost = {};
    }
    Object.defineProperty(GridPane.prototype, "columns", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.columns;
            return P.boxAsJs(value);
        }
    });
    if(!GridPane){
        /**
         * Generated property jsDoc.
         * @property columns
         * @memberOf GridPane
         */
        P.GridPane.prototype.columns = 0;
    }
    Object.defineProperty(GridPane.prototype, "onMousePressed", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onMousePressed;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onMousePressed = P.boxAsJava(aValue);
        }
    });
    if(!GridPane){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf GridPane
         */
        P.GridPane.prototype.onMousePressed = {};
    }
    Object.defineProperty(GridPane.prototype, "foreground", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.foreground;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.foreground = P.boxAsJava(aValue);
        }
    });
    if(!GridPane){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf GridPane
         */
        P.GridPane.prototype.foreground = {};
    }
    Object.defineProperty(GridPane.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!GridPane){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf GridPane
         */
        P.GridPane.prototype.error = '';
    }
    Object.defineProperty(GridPane.prototype, "enabled", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.enabled;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.enabled = P.boxAsJava(aValue);
        }
    });
    if(!GridPane){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf GridPane
         */
        P.GridPane.prototype.enabled = true;
    }
    Object.defineProperty(GridPane.prototype, "onComponentMoved", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onComponentMoved;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onComponentMoved = P.boxAsJava(aValue);
        }
    });
    if(!GridPane){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf GridPane
         */
        P.GridPane.prototype.onComponentMoved = {};
    }
    Object.defineProperty(GridPane.prototype, "onComponentAdded", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onComponentAdded;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onComponentAdded = P.boxAsJava(aValue);
        }
    });
    if(!GridPane){
        /**
         * Component added event hanler function.
         * @property onComponentAdded
         * @memberOf GridPane
         */
        P.GridPane.prototype.onComponentAdded = {};
    }
    Object.defineProperty(GridPane.prototype, "componentPopupMenu", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.componentPopupMenu;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.componentPopupMenu = P.boxAsJava(aValue);
        }
    });
    if(!GridPane){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf GridPane
         */
        P.GridPane.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(GridPane.prototype, "top", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.top;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.top = P.boxAsJava(aValue);
        }
    });
    if(!GridPane){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf GridPane
         */
        P.GridPane.prototype.top = 0;
    }
    Object.defineProperty(GridPane.prototype, "children", {
        get: function() {
            var delegate = this.unwrap();
            if (!invalidatable) {
                var value = delegate.children;
                invalidatable = P.boxAsJs(value);
            }
            return invalidatable;
        }
    });
    if(!GridPane){
        /**
         * Generated property jsDoc.
         * @property children
         * @memberOf GridPane
         */
        P.GridPane.prototype.children = [];
    }
    Object.defineProperty(GridPane.prototype, "onComponentResized", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onComponentResized;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onComponentResized = P.boxAsJava(aValue);
        }
    });
    if(!GridPane){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf GridPane
         */
        P.GridPane.prototype.onComponentResized = {};
    }
    Object.defineProperty(GridPane.prototype, "onMouseEntered", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onMouseEntered;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onMouseEntered = P.boxAsJava(aValue);
        }
    });
    if(!GridPane){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf GridPane
         */
        P.GridPane.prototype.onMouseEntered = {};
    }
    Object.defineProperty(GridPane.prototype, "toolTipText", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.toolTipText;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.toolTipText = P.boxAsJava(aValue);
        }
    });
    if(!GridPane){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf GridPane
         */
        P.GridPane.prototype.toolTipText = '';
    }
    Object.defineProperty(GridPane.prototype, "height", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.height;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.height = P.boxAsJava(aValue);
        }
    });
    if(!GridPane){
        /**
         * Height of the component.
         * @property height
         * @memberOf GridPane
         */
        P.GridPane.prototype.height = 0;
    }
    Object.defineProperty(GridPane.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!GridPane){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf GridPane
         */
        P.GridPane.prototype.element = {};
    }
    Object.defineProperty(GridPane.prototype, "onComponentShown", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onComponentShown;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onComponentShown = P.boxAsJava(aValue);
        }
    });
    if(!GridPane){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf GridPane
         */
        P.GridPane.prototype.onComponentShown = {};
    }
    Object.defineProperty(GridPane.prototype, "onMouseMoved", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onMouseMoved;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onMouseMoved = P.boxAsJava(aValue);
        }
    });
    if(!GridPane){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf GridPane
         */
        P.GridPane.prototype.onMouseMoved = {};
    }
    Object.defineProperty(GridPane.prototype, "opaque", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.opaque;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.opaque = P.boxAsJava(aValue);
        }
    });
    if(!GridPane){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf GridPane
         */
        P.GridPane.prototype.opaque = true;
    }
    Object.defineProperty(GridPane.prototype, "visible", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.visible;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.visible = P.boxAsJava(aValue);
        }
    });
    if(!GridPane){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf GridPane
         */
        P.GridPane.prototype.visible = true;
    }
    Object.defineProperty(GridPane.prototype, "onComponentHidden", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onComponentHidden;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onComponentHidden = P.boxAsJava(aValue);
        }
    });
    if(!GridPane){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf GridPane
         */
        P.GridPane.prototype.onComponentHidden = {};
    }
    Object.defineProperty(GridPane.prototype, "nextFocusableComponent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.nextFocusableComponent;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.nextFocusableComponent = P.boxAsJava(aValue);
        }
    });
    if(!GridPane){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf GridPane
         */
        P.GridPane.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(GridPane.prototype, "count", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.count;
            return P.boxAsJs(value);
        }
    });
    if(!GridPane){
        /**
         * Generated property jsDoc.
         * @property count
         * @memberOf GridPane
         */
        P.GridPane.prototype.count = 0;
    }
    Object.defineProperty(GridPane.prototype, "onActionPerformed", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onActionPerformed;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onActionPerformed = P.boxAsJava(aValue);
        }
    });
    if(!GridPane){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf GridPane
         */
        P.GridPane.prototype.onActionPerformed = {};
    }
    Object.defineProperty(GridPane.prototype, "onKeyReleased", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onKeyReleased;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onKeyReleased = P.boxAsJava(aValue);
        }
    });
    if(!GridPane){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf GridPane
         */
        P.GridPane.prototype.onKeyReleased = {};
    }
    Object.defineProperty(GridPane.prototype, "focusable", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.focusable;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.focusable = P.boxAsJava(aValue);
        }
    });
    if(!GridPane){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf GridPane
         */
        P.GridPane.prototype.focusable = true;
    }
    Object.defineProperty(GridPane.prototype, "onKeyTyped", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onKeyTyped;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onKeyTyped = P.boxAsJava(aValue);
        }
    });
    if(!GridPane){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf GridPane
         */
        P.GridPane.prototype.onKeyTyped = {};
    }
    Object.defineProperty(GridPane.prototype, "rows", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.rows;
            return P.boxAsJs(value);
        }
    });
    if(!GridPane){
        /**
         * Generated property jsDoc.
         * @property rows
         * @memberOf GridPane
         */
        P.GridPane.prototype.rows = 0;
    }
    Object.defineProperty(GridPane.prototype, "onMouseWheelMoved", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onMouseWheelMoved;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onMouseWheelMoved = P.boxAsJava(aValue);
        }
    });
    if(!GridPane){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf GridPane
         */
        P.GridPane.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(GridPane.prototype, "onComponentRemoved", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onComponentRemoved;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onComponentRemoved = P.boxAsJava(aValue);
        }
    });
    if(!GridPane){
        /**
         * Component removed event handler function.
         * @property onComponentRemoved
         * @memberOf GridPane
         */
        P.GridPane.prototype.onComponentRemoved = {};
    }
    Object.defineProperty(GridPane.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!GridPane){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf GridPane
         */
        P.GridPane.prototype.component = {};
    }
    Object.defineProperty(GridPane.prototype, "onFocusGained", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onFocusGained;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onFocusGained = P.boxAsJava(aValue);
        }
    });
    if(!GridPane){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf GridPane
         */
        P.GridPane.prototype.onFocusGained = {};
    }
    Object.defineProperty(GridPane.prototype, "left", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.left;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.left = P.boxAsJava(aValue);
        }
    });
    if(!GridPane){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf GridPane
         */
        P.GridPane.prototype.left = 0;
    }
    Object.defineProperty(GridPane.prototype, "background", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.background;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.background = P.boxAsJava(aValue);
        }
    });
    if(!GridPane){
        /**
         * The background color of this component.
         * @property background
         * @memberOf GridPane
         */
        P.GridPane.prototype.background = {};
    }
    Object.defineProperty(GridPane.prototype, "onMouseClicked", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onMouseClicked;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onMouseClicked = P.boxAsJava(aValue);
        }
    });
    if(!GridPane){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf GridPane
         */
        P.GridPane.prototype.onMouseClicked = {};
    }
    Object.defineProperty(GridPane.prototype, "onMouseExited", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onMouseExited;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onMouseExited = P.boxAsJava(aValue);
        }
    });
    if(!GridPane){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf GridPane
         */
        P.GridPane.prototype.onMouseExited = {};
    }
    Object.defineProperty(GridPane.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!GridPane){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf GridPane
         */
        P.GridPane.prototype.name = '';
    }
    Object.defineProperty(GridPane.prototype, "width", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.width;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.width = P.boxAsJava(aValue);
        }
    });
    if(!GridPane){
        /**
         * Width of the component.
         * @property width
         * @memberOf GridPane
         */
        P.GridPane.prototype.width = 0;
    }
    Object.defineProperty(GridPane.prototype, "font", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.font;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.font = P.boxAsJava(aValue);
        }
    });
    if(!GridPane){
        /**
         * The font of this component.
         * @property font
         * @memberOf GridPane
         */
        P.GridPane.prototype.font = {};
    }
    Object.defineProperty(GridPane.prototype, "onKeyPressed", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onKeyPressed;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onKeyPressed = P.boxAsJava(aValue);
        }
    });
    if(!GridPane){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf GridPane
         */
        P.GridPane.prototype.onKeyPressed = {};
    }
    Object.defineProperty(GridPane.prototype, "add", {
        value: function(component, row, column) {
            var delegate = this.unwrap();
            var value = delegate.add(P.boxAsJava(component), P.boxAsJava(row), P.boxAsJava(column));
            return P.boxAsJs(value);
        }
    });
    if(!GridPane){
        /**
         * Appends the specified component to the end of this container.
         * @param component the component to add
         * @param row the row of the component
         * @param column the column of the component
         * @method add
         * @memberOf GridPane
         */
        P.GridPane.prototype.add = function(component, row, column){};
    }
    Object.defineProperty(GridPane.prototype, "remove", {
        value: function(component) {
            var delegate = this.unwrap();
            var value = delegate.remove(P.boxAsJava(component));
            return P.boxAsJs(value);
        }
    });
    if(!GridPane){
        /**
         * Removes the specified component from this container.
         * @param component the component to remove
         * @method remove
         * @memberOf GridPane
         */
        P.GridPane.prototype.remove = function(component){};
    }
    Object.defineProperty(GridPane.prototype, "child", {
        value: function(row, column) {
            var delegate = this.unwrap();
            var value = delegate.child(P.boxAsJava(row), P.boxAsJava(column));
            return P.boxAsJs(value);
        }
    });
    if(!GridPane){
        /**
         * Gets the component with the specified row and column.
         * @param row the row of the component
         * @param column the column of the component
         * @method child
         * @memberOf GridPane
         */
        P.GridPane.prototype.child = function(row, column){};
    }
    Object.defineProperty(GridPane.prototype, "clear", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.clear();
            return P.boxAsJs(value);
        }
    });
    if(!GridPane){
        /**
         * Removes all the components from this container.
         * @method clear
         * @memberOf GridPane
         */
        P.GridPane.prototype.clear = function(){};
    }
    Object.defineProperty(GridPane.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!GridPane){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf GridPane
         */
        P.GridPane.prototype.focus = function(){};
    }
})();