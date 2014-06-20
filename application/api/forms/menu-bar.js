(function() {
    var javaClass = Java.type("com.eas.client.forms.api.menu.MenuBar");
    javaClass.setPublisher(function(aDelegate) {
        return new P.MenuBar(aDelegate);
    });
    
    /**
     * An implementation of a menu bar.
     * @constructor MenuBar MenuBar
     */
    P.MenuBar = function MenuBar() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(MenuBar.superclass)
            MenuBar.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "MenuBar", {value: MenuBar});
    Object.defineProperty(MenuBar.prototype, "cursor", {
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
    if(!MenuBar){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.cursor = {};
    }
    Object.defineProperty(MenuBar.prototype, "onMouseDragged", {
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
    if(!MenuBar){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.onMouseDragged = {};
    }
    Object.defineProperty(MenuBar.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!MenuBar){
        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.parent = {};
    }
    Object.defineProperty(MenuBar.prototype, "onMouseReleased", {
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
    if(!MenuBar){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.onMouseReleased = {};
    }
    Object.defineProperty(MenuBar.prototype, "onFocusLost", {
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
    if(!MenuBar){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.onFocusLost = {};
    }
    Object.defineProperty(MenuBar.prototype, "onMousePressed", {
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
    if(!MenuBar){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.onMousePressed = {};
    }
    Object.defineProperty(MenuBar.prototype, "foreground", {
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
    if(!MenuBar){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.foreground = {};
    }
    Object.defineProperty(MenuBar.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!MenuBar){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.error = '';
    }
    Object.defineProperty(MenuBar.prototype, "enabled", {
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
    if(!MenuBar){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.enabled = true;
    }
    Object.defineProperty(MenuBar.prototype, "onComponentMoved", {
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
    if(!MenuBar){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.onComponentMoved = {};
    }
    Object.defineProperty(MenuBar.prototype, "onComponentAdded", {
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
    if(!MenuBar){
        /**
         * Component added event hanler function.
         * @property onComponentAdded
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.onComponentAdded = {};
    }
    Object.defineProperty(MenuBar.prototype, "componentPopupMenu", {
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
    if(!MenuBar){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(MenuBar.prototype, "top", {
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
    if(!MenuBar){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.top = 0;
    }
    Object.defineProperty(MenuBar.prototype, "children", {
        get: function() {
            var delegate = this.unwrap();
            if (!invalidatable) {
                var value = delegate.children;
                invalidatable = P.boxAsJs(value);
            }
            return invalidatable;
        }
    });
    if(!MenuBar){
        /**
         * Gets the container's children components.
         * @property children
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.children = [];
    }
    Object.defineProperty(MenuBar.prototype, "onComponentResized", {
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
    if(!MenuBar){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.onComponentResized = {};
    }
    Object.defineProperty(MenuBar.prototype, "onMouseEntered", {
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
    if(!MenuBar){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.onMouseEntered = {};
    }
    Object.defineProperty(MenuBar.prototype, "toolTipText", {
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
    if(!MenuBar){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.toolTipText = '';
    }
    Object.defineProperty(MenuBar.prototype, "height", {
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
    if(!MenuBar){
        /**
         * Height of the component.
         * @property height
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.height = 0;
    }
    Object.defineProperty(MenuBar.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!MenuBar){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.element = {};
    }
    Object.defineProperty(MenuBar.prototype, "onComponentShown", {
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
    if(!MenuBar){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.onComponentShown = {};
    }
    Object.defineProperty(MenuBar.prototype, "onMouseMoved", {
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
    if(!MenuBar){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.onMouseMoved = {};
    }
    Object.defineProperty(MenuBar.prototype, "opaque", {
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
    if(!MenuBar){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.opaque = true;
    }
    Object.defineProperty(MenuBar.prototype, "visible", {
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
    if(!MenuBar){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.visible = true;
    }
    Object.defineProperty(MenuBar.prototype, "onComponentHidden", {
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
    if(!MenuBar){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.onComponentHidden = {};
    }
    Object.defineProperty(MenuBar.prototype, "nextFocusableComponent", {
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
    if(!MenuBar){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(MenuBar.prototype, "count", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.count;
            return P.boxAsJs(value);
        }
    });
    if(!MenuBar){
        /**
         * Gets the number of components in this panel.
         * @property count
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.count = 0;
    }
    Object.defineProperty(MenuBar.prototype, "onActionPerformed", {
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
    if(!MenuBar){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.onActionPerformed = {};
    }
    Object.defineProperty(MenuBar.prototype, "onKeyReleased", {
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
    if(!MenuBar){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.onKeyReleased = {};
    }
    Object.defineProperty(MenuBar.prototype, "focusable", {
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
    if(!MenuBar){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.focusable = true;
    }
    Object.defineProperty(MenuBar.prototype, "onKeyTyped", {
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
    if(!MenuBar){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.onKeyTyped = {};
    }
    Object.defineProperty(MenuBar.prototype, "onMouseWheelMoved", {
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
    if(!MenuBar){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(MenuBar.prototype, "onComponentRemoved", {
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
    if(!MenuBar){
        /**
         * Component removed event handler function.
         * @property onComponentRemoved
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.onComponentRemoved = {};
    }
    Object.defineProperty(MenuBar.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!MenuBar){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.component = {};
    }
    Object.defineProperty(MenuBar.prototype, "onFocusGained", {
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
    if(!MenuBar){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.onFocusGained = {};
    }
    Object.defineProperty(MenuBar.prototype, "left", {
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
    if(!MenuBar){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.left = 0;
    }
    Object.defineProperty(MenuBar.prototype, "background", {
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
    if(!MenuBar){
        /**
         * The background color of this component.
         * @property background
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.background = {};
    }
    Object.defineProperty(MenuBar.prototype, "onMouseClicked", {
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
    if(!MenuBar){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.onMouseClicked = {};
    }
    Object.defineProperty(MenuBar.prototype, "onMouseExited", {
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
    if(!MenuBar){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.onMouseExited = {};
    }
    Object.defineProperty(MenuBar.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!MenuBar){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.name = '';
    }
    Object.defineProperty(MenuBar.prototype, "width", {
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
    if(!MenuBar){
        /**
         * Width of the component.
         * @property width
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.width = 0;
    }
    Object.defineProperty(MenuBar.prototype, "font", {
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
    if(!MenuBar){
        /**
         * The font of this component.
         * @property font
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.font = {};
    }
    Object.defineProperty(MenuBar.prototype, "onKeyPressed", {
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
    if(!MenuBar){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.onKeyPressed = {};
    }
    Object.defineProperty(MenuBar.prototype, "add", {
        value: function(menu) {
            var delegate = this.unwrap();
            var value = delegate.add(P.boxAsJava(menu));
            return P.boxAsJs(value);
        }
    });
    if(!MenuBar){
        /**
         * Adds the item to the menu.
         * @param menu the menu component to add
         * @method add
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.add = function(menu){};
    }
    Object.defineProperty(MenuBar.prototype, "child", {
        value: function(index) {
            var delegate = this.unwrap();
            var value = delegate.child(P.boxAsJava(index));
            return P.boxAsJs(value);
        }
    });
    if(!MenuBar){
        /**
         * Gets the container's nth component.
         * @param index the component's index in the container
         * @return the child component
         * @method child
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.child = function(index){};
    }
    Object.defineProperty(MenuBar.prototype, "remove", {
        value: function(component) {
            var delegate = this.unwrap();
            var value = delegate.remove(P.boxAsJava(component));
            return P.boxAsJs(value);
        }
    });
    if(!MenuBar){
        /**
         * Removes the specified component from this container.
         * @param component the component to remove
         * @method remove
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.remove = function(component){};
    }
    Object.defineProperty(MenuBar.prototype, "clear", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.clear();
            return P.boxAsJs(value);
        }
    });
    if(!MenuBar){
        /**
         * Removes all the components from this container.
         * @method clear
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.clear = function(){};
    }
    Object.defineProperty(MenuBar.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!MenuBar){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf MenuBar
         */
        P.MenuBar.prototype.focus = function(){};
    }
})();