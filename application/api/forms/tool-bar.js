(function() {
    var javaClass = Java.type("com.eas.client.forms.api.containers.ToolBar");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ToolBar(aDelegate);
    });
    
    /**
     * <code>ToolBar</code> provides a component that is useful for displaying commonly used actions or controls.
     * @param floatable if <code>true</code>, the tool bar can be moved; <code>false</code> otherwise (optional).
     * @constructor ToolBar ToolBar
     */
    P.ToolBar = function ToolBar() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(ToolBar.superclass)
            ToolBar.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "ToolBar", {value: ToolBar});
    Object.defineProperty(ToolBar.prototype, "cursor", {
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
    if(!ToolBar){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.cursor = {};
    }
    Object.defineProperty(ToolBar.prototype, "onMouseDragged", {
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
    if(!ToolBar){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.onMouseDragged = {};
    }
    Object.defineProperty(ToolBar.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!ToolBar){
        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.parent = {};
    }
    Object.defineProperty(ToolBar.prototype, "onMouseReleased", {
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
    if(!ToolBar){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.onMouseReleased = {};
    }
    Object.defineProperty(ToolBar.prototype, "onFocusLost", {
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
    if(!ToolBar){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.onFocusLost = {};
    }
    Object.defineProperty(ToolBar.prototype, "onMousePressed", {
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
    if(!ToolBar){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.onMousePressed = {};
    }
    Object.defineProperty(ToolBar.prototype, "foreground", {
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
    if(!ToolBar){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.foreground = {};
    }
    Object.defineProperty(ToolBar.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!ToolBar){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.error = '';
    }
    Object.defineProperty(ToolBar.prototype, "enabled", {
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
    if(!ToolBar){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.enabled = true;
    }
    Object.defineProperty(ToolBar.prototype, "onComponentMoved", {
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
    if(!ToolBar){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.onComponentMoved = {};
    }
    Object.defineProperty(ToolBar.prototype, "onComponentAdded", {
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
    if(!ToolBar){
        /**
         * Component added event hanler function.
         * @property onComponentAdded
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.onComponentAdded = {};
    }
    Object.defineProperty(ToolBar.prototype, "componentPopupMenu", {
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
    if(!ToolBar){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(ToolBar.prototype, "top", {
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
    if(!ToolBar){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.top = 0;
    }
    Object.defineProperty(ToolBar.prototype, "children", {
        get: function() {
            var delegate = this.unwrap();
            if (!invalidatable) {
                var value = delegate.children;
                invalidatable = P.boxAsJs(value);
            }
            return invalidatable;
        }
    });
    if(!ToolBar){
        /**
         * Gets the container's children components.
         * @property children
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.children = [];
    }
    Object.defineProperty(ToolBar.prototype, "onComponentResized", {
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
    if(!ToolBar){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.onComponentResized = {};
    }
    Object.defineProperty(ToolBar.prototype, "onMouseEntered", {
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
    if(!ToolBar){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.onMouseEntered = {};
    }
    Object.defineProperty(ToolBar.prototype, "toolTipText", {
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
    if(!ToolBar){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.toolTipText = '';
    }
    Object.defineProperty(ToolBar.prototype, "height", {
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
    if(!ToolBar){
        /**
         * Height of the component.
         * @property height
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.height = 0;
    }
    Object.defineProperty(ToolBar.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!ToolBar){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.element = {};
    }
    Object.defineProperty(ToolBar.prototype, "onComponentShown", {
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
    if(!ToolBar){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.onComponentShown = {};
    }
    Object.defineProperty(ToolBar.prototype, "onMouseMoved", {
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
    if(!ToolBar){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.onMouseMoved = {};
    }
    Object.defineProperty(ToolBar.prototype, "opaque", {
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
    if(!ToolBar){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.opaque = true;
    }
    Object.defineProperty(ToolBar.prototype, "visible", {
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
    if(!ToolBar){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.visible = true;
    }
    Object.defineProperty(ToolBar.prototype, "onComponentHidden", {
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
    if(!ToolBar){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.onComponentHidden = {};
    }
    Object.defineProperty(ToolBar.prototype, "nextFocusableComponent", {
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
    if(!ToolBar){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(ToolBar.prototype, "count", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.count;
            return P.boxAsJs(value);
        }
    });
    if(!ToolBar){
        /**
         * Gets the number of components in this panel.
         * @property count
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.count = 0;
    }
    Object.defineProperty(ToolBar.prototype, "onActionPerformed", {
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
    if(!ToolBar){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.onActionPerformed = {};
    }
    Object.defineProperty(ToolBar.prototype, "onKeyReleased", {
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
    if(!ToolBar){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.onKeyReleased = {};
    }
    Object.defineProperty(ToolBar.prototype, "focusable", {
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
    if(!ToolBar){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.focusable = true;
    }
    Object.defineProperty(ToolBar.prototype, "onKeyTyped", {
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
    if(!ToolBar){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.onKeyTyped = {};
    }
    Object.defineProperty(ToolBar.prototype, "onMouseWheelMoved", {
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
    if(!ToolBar){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(ToolBar.prototype, "onComponentRemoved", {
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
    if(!ToolBar){
        /**
         * Component removed event handler function.
         * @property onComponentRemoved
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.onComponentRemoved = {};
    }
    Object.defineProperty(ToolBar.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!ToolBar){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.component = {};
    }
    Object.defineProperty(ToolBar.prototype, "onFocusGained", {
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
    if(!ToolBar){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.onFocusGained = {};
    }
    Object.defineProperty(ToolBar.prototype, "left", {
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
    if(!ToolBar){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.left = 0;
    }
    Object.defineProperty(ToolBar.prototype, "background", {
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
    if(!ToolBar){
        /**
         * The background color of this component.
         * @property background
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.background = {};
    }
    Object.defineProperty(ToolBar.prototype, "onMouseClicked", {
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
    if(!ToolBar){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.onMouseClicked = {};
    }
    Object.defineProperty(ToolBar.prototype, "onMouseExited", {
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
    if(!ToolBar){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.onMouseExited = {};
    }
    Object.defineProperty(ToolBar.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!ToolBar){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.name = '';
    }
    Object.defineProperty(ToolBar.prototype, "width", {
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
    if(!ToolBar){
        /**
         * Width of the component.
         * @property width
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.width = 0;
    }
    Object.defineProperty(ToolBar.prototype, "font", {
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
    if(!ToolBar){
        /**
         * The font of this component.
         * @property font
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.font = {};
    }
    Object.defineProperty(ToolBar.prototype, "onKeyPressed", {
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
    if(!ToolBar){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.onKeyPressed = {};
    }
    Object.defineProperty(ToolBar.prototype, "add", {
        value: function(component) {
            var delegate = this.unwrap();
            var value = delegate.add(P.boxAsJava(component));
            return P.boxAsJs(value);
        }
    });
    if(!ToolBar){
        /**
         * Appends the specified component to the end of this container.
         * @param component the component to add.
         * @method add
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.add = function(component){};
    }
    Object.defineProperty(ToolBar.prototype, "child", {
        value: function(index) {
            var delegate = this.unwrap();
            var value = delegate.child(P.boxAsJava(index));
            return P.boxAsJs(value);
        }
    });
    if(!ToolBar){
        /**
         * Gets the container's nth component.
         * @param index the component's index in the container
         * @return the child component
         * @method child
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.child = function(index){};
    }
    Object.defineProperty(ToolBar.prototype, "remove", {
        value: function(component) {
            var delegate = this.unwrap();
            var value = delegate.remove(P.boxAsJava(component));
            return P.boxAsJs(value);
        }
    });
    if(!ToolBar){
        /**
         * Removes the specified component from this container.
         * @param component the component to remove
         * @method remove
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.remove = function(component){};
    }
    Object.defineProperty(ToolBar.prototype, "clear", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.clear();
            return P.boxAsJs(value);
        }
    });
    if(!ToolBar){
        /**
         * Removes all the components from this container.
         * @method clear
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.clear = function(){};
    }
    Object.defineProperty(ToolBar.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!ToolBar){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf ToolBar
         */
        P.ToolBar.prototype.focus = function(){};
    }
})();