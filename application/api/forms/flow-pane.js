(function() {
    var javaClass = Java.type("com.eas.client.forms.api.containers.FlowPane");
    javaClass.setPublisher(function(aDelegate) {
        return new P.FlowPane(null, null, aDelegate);
    });
    
    /**
     * A container with Flow Layout.
     * @param hgap the horizontal gap (optional).
     * @param vgap the vertical gap (optional).
     * @constructor FlowPane FlowPane
     */
    P.FlowPane = function FlowPane(hgap, vgap) {
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
        if(FlowPane.superclass)
            FlowPane.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "FlowPane", {value: FlowPane});
    Object.defineProperty(FlowPane.prototype, "cursor", {
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
    if(!FlowPane){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.cursor = {};
    }
    Object.defineProperty(FlowPane.prototype, "onMouseDragged", {
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
    if(!FlowPane){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.onMouseDragged = {};
    }
    Object.defineProperty(FlowPane.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!FlowPane){
        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.parent = {};
    }
    Object.defineProperty(FlowPane.prototype, "onMouseReleased", {
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
    if(!FlowPane){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.onMouseReleased = {};
    }
    Object.defineProperty(FlowPane.prototype, "onFocusLost", {
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
    if(!FlowPane){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.onFocusLost = {};
    }
    Object.defineProperty(FlowPane.prototype, "onMousePressed", {
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
    if(!FlowPane){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.onMousePressed = {};
    }
    Object.defineProperty(FlowPane.prototype, "foreground", {
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
    if(!FlowPane){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.foreground = {};
    }
    Object.defineProperty(FlowPane.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!FlowPane){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.error = '';
    }
    Object.defineProperty(FlowPane.prototype, "enabled", {
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
    if(!FlowPane){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.enabled = true;
    }
    Object.defineProperty(FlowPane.prototype, "onComponentMoved", {
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
    if(!FlowPane){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.onComponentMoved = {};
    }
    Object.defineProperty(FlowPane.prototype, "onComponentAdded", {
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
    if(!FlowPane){
        /**
         * Component added event hanler function.
         * @property onComponentAdded
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.onComponentAdded = {};
    }
    Object.defineProperty(FlowPane.prototype, "componentPopupMenu", {
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
    if(!FlowPane){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(FlowPane.prototype, "top", {
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
    if(!FlowPane){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.top = 0;
    }
    Object.defineProperty(FlowPane.prototype, "children", {
        get: function() {
            var delegate = this.unwrap();
            if (!invalidatable) {
                var value = delegate.children;
                invalidatable = P.boxAsJs(value);
            }
            return invalidatable;
        }
    });
    if(!FlowPane){
        /**
         * Gets the container's children components.
         * @property children
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.children = [];
    }
    Object.defineProperty(FlowPane.prototype, "onComponentResized", {
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
    if(!FlowPane){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.onComponentResized = {};
    }
    Object.defineProperty(FlowPane.prototype, "onMouseEntered", {
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
    if(!FlowPane){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.onMouseEntered = {};
    }
    Object.defineProperty(FlowPane.prototype, "toolTipText", {
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
    if(!FlowPane){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.toolTipText = '';
    }
    Object.defineProperty(FlowPane.prototype, "height", {
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
    if(!FlowPane){
        /**
         * Height of the component.
         * @property height
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.height = 0;
    }
    Object.defineProperty(FlowPane.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!FlowPane){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.element = {};
    }
    Object.defineProperty(FlowPane.prototype, "onComponentShown", {
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
    if(!FlowPane){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.onComponentShown = {};
    }
    Object.defineProperty(FlowPane.prototype, "onMouseMoved", {
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
    if(!FlowPane){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.onMouseMoved = {};
    }
    Object.defineProperty(FlowPane.prototype, "opaque", {
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
    if(!FlowPane){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.opaque = true;
    }
    Object.defineProperty(FlowPane.prototype, "visible", {
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
    if(!FlowPane){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.visible = true;
    }
    Object.defineProperty(FlowPane.prototype, "onComponentHidden", {
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
    if(!FlowPane){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.onComponentHidden = {};
    }
    Object.defineProperty(FlowPane.prototype, "nextFocusableComponent", {
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
    if(!FlowPane){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(FlowPane.prototype, "count", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.count;
            return P.boxAsJs(value);
        }
    });
    if(!FlowPane){
        /**
         * Gets the number of components in this panel.
         * @property count
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.count = 0;
    }
    Object.defineProperty(FlowPane.prototype, "onActionPerformed", {
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
    if(!FlowPane){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.onActionPerformed = {};
    }
    Object.defineProperty(FlowPane.prototype, "onKeyReleased", {
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
    if(!FlowPane){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.onKeyReleased = {};
    }
    Object.defineProperty(FlowPane.prototype, "focusable", {
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
    if(!FlowPane){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.focusable = true;
    }
    Object.defineProperty(FlowPane.prototype, "onKeyTyped", {
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
    if(!FlowPane){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.onKeyTyped = {};
    }
    Object.defineProperty(FlowPane.prototype, "onMouseWheelMoved", {
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
    if(!FlowPane){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(FlowPane.prototype, "onComponentRemoved", {
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
    if(!FlowPane){
        /**
         * Component removed event handler function.
         * @property onComponentRemoved
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.onComponentRemoved = {};
    }
    Object.defineProperty(FlowPane.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!FlowPane){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.component = {};
    }
    Object.defineProperty(FlowPane.prototype, "onFocusGained", {
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
    if(!FlowPane){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.onFocusGained = {};
    }
    Object.defineProperty(FlowPane.prototype, "left", {
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
    if(!FlowPane){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.left = 0;
    }
    Object.defineProperty(FlowPane.prototype, "background", {
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
    if(!FlowPane){
        /**
         * The background color of this component.
         * @property background
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.background = {};
    }
    Object.defineProperty(FlowPane.prototype, "onMouseClicked", {
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
    if(!FlowPane){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.onMouseClicked = {};
    }
    Object.defineProperty(FlowPane.prototype, "onMouseExited", {
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
    if(!FlowPane){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.onMouseExited = {};
    }
    Object.defineProperty(FlowPane.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!FlowPane){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.name = '';
    }
    Object.defineProperty(FlowPane.prototype, "width", {
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
    if(!FlowPane){
        /**
         * Width of the component.
         * @property width
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.width = 0;
    }
    Object.defineProperty(FlowPane.prototype, "font", {
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
    if(!FlowPane){
        /**
         * The font of this component.
         * @property font
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.font = {};
    }
    Object.defineProperty(FlowPane.prototype, "onKeyPressed", {
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
    if(!FlowPane){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.onKeyPressed = {};
    }
    Object.defineProperty(FlowPane.prototype, "add", {
        value: function(component) {
            var delegate = this.unwrap();
            var value = delegate.add(P.boxAsJava(component));
            return P.boxAsJs(value);
        }
    });
    if(!FlowPane){
        /**
         * Appends the specified component to the end of this container.
         * @param component the component to add
         * @method add
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.add = function(component){};
    }
    Object.defineProperty(FlowPane.prototype, "child", {
        value: function(index) {
            var delegate = this.unwrap();
            var value = delegate.child(P.boxAsJava(index));
            return P.boxAsJs(value);
        }
    });
    if(!FlowPane){
        /**
         * Gets the container's nth component.
         * @param index the component's index in the container
         * @return the child component
         * @method child
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.child = function(index){};
    }
    Object.defineProperty(FlowPane.prototype, "remove", {
        value: function(component) {
            var delegate = this.unwrap();
            var value = delegate.remove(P.boxAsJava(component));
            return P.boxAsJs(value);
        }
    });
    if(!FlowPane){
        /**
         * Removes the specified component from this container.
         * @param component the component to remove
         * @method remove
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.remove = function(component){};
    }
    Object.defineProperty(FlowPane.prototype, "clear", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.clear();
            return P.boxAsJs(value);
        }
    });
    if(!FlowPane){
        /**
         * Removes all the components from this container.
         * @method clear
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.clear = function(){};
    }
    Object.defineProperty(FlowPane.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!FlowPane){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf FlowPane
         */
        P.FlowPane.prototype.focus = function(){};
    }
})();