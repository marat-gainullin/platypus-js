(function() {
    var javaClass = Java.type("com.eas.client.forms.api.menu.PopupMenu");
    javaClass.setPublisher(function(aDelegate) {
        return new P.PopupMenu(aDelegate);
    });
    
    /**
     * An implementation of a popup menu -- a small window that pops up and displays a series of choices.
     * @constructor PopupMenu PopupMenu
     */
    P.PopupMenu = function PopupMenu() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(PopupMenu.superclass)
            PopupMenu.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "PopupMenu", {value: PopupMenu});
    Object.defineProperty(PopupMenu.prototype, "cursor", {
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
    if(!PopupMenu){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.cursor = {};
    }
    Object.defineProperty(PopupMenu.prototype, "onMouseDragged", {
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
    if(!PopupMenu){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.onMouseDragged = {};
    }
    Object.defineProperty(PopupMenu.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!PopupMenu){
        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.parent = {};
    }
    Object.defineProperty(PopupMenu.prototype, "onMouseReleased", {
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
    if(!PopupMenu){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.onMouseReleased = {};
    }
    Object.defineProperty(PopupMenu.prototype, "onFocusLost", {
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
    if(!PopupMenu){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.onFocusLost = {};
    }
    Object.defineProperty(PopupMenu.prototype, "onMousePressed", {
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
    if(!PopupMenu){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.onMousePressed = {};
    }
    Object.defineProperty(PopupMenu.prototype, "foreground", {
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
    if(!PopupMenu){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.foreground = {};
    }
    Object.defineProperty(PopupMenu.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!PopupMenu){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.error = '';
    }
    Object.defineProperty(PopupMenu.prototype, "enabled", {
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
    if(!PopupMenu){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.enabled = true;
    }
    Object.defineProperty(PopupMenu.prototype, "onComponentMoved", {
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
    if(!PopupMenu){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.onComponentMoved = {};
    }
    Object.defineProperty(PopupMenu.prototype, "onComponentAdded", {
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
    if(!PopupMenu){
        /**
         * Component added event hanler function.
         * @property onComponentAdded
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.onComponentAdded = {};
    }
    Object.defineProperty(PopupMenu.prototype, "componentPopupMenu", {
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
    if(!PopupMenu){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(PopupMenu.prototype, "top", {
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
    if(!PopupMenu){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.top = 0;
    }
    Object.defineProperty(PopupMenu.prototype, "children", {
        get: function() {
            var delegate = this.unwrap();
            if (!invalidatable) {
                var value = delegate.children;
                invalidatable = P.boxAsJs(value);
            }
            return invalidatable;
        }
    });
    if(!PopupMenu){
        /**
         * Gets the container's children components.
         * @property children
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.children = [];
    }
    Object.defineProperty(PopupMenu.prototype, "onComponentResized", {
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
    if(!PopupMenu){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.onComponentResized = {};
    }
    Object.defineProperty(PopupMenu.prototype, "onMouseEntered", {
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
    if(!PopupMenu){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.onMouseEntered = {};
    }
    Object.defineProperty(PopupMenu.prototype, "toolTipText", {
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
    if(!PopupMenu){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.toolTipText = '';
    }
    Object.defineProperty(PopupMenu.prototype, "height", {
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
    if(!PopupMenu){
        /**
         * Height of the component.
         * @property height
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.height = 0;
    }
    Object.defineProperty(PopupMenu.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!PopupMenu){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.element = {};
    }
    Object.defineProperty(PopupMenu.prototype, "onComponentShown", {
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
    if(!PopupMenu){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.onComponentShown = {};
    }
    Object.defineProperty(PopupMenu.prototype, "onMouseMoved", {
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
    if(!PopupMenu){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.onMouseMoved = {};
    }
    Object.defineProperty(PopupMenu.prototype, "opaque", {
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
    if(!PopupMenu){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.opaque = true;
    }
    Object.defineProperty(PopupMenu.prototype, "visible", {
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
    if(!PopupMenu){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.visible = true;
    }
    Object.defineProperty(PopupMenu.prototype, "onComponentHidden", {
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
    if(!PopupMenu){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.onComponentHidden = {};
    }
    Object.defineProperty(PopupMenu.prototype, "nextFocusableComponent", {
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
    if(!PopupMenu){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(PopupMenu.prototype, "count", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.count;
            return P.boxAsJs(value);
        }
    });
    if(!PopupMenu){
        /**
         * Gets the number of components in this panel.
         * @property count
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.count = 0;
    }
    Object.defineProperty(PopupMenu.prototype, "onActionPerformed", {
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
    if(!PopupMenu){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.onActionPerformed = {};
    }
    Object.defineProperty(PopupMenu.prototype, "onKeyReleased", {
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
    if(!PopupMenu){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.onKeyReleased = {};
    }
    Object.defineProperty(PopupMenu.prototype, "focusable", {
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
    if(!PopupMenu){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.focusable = true;
    }
    Object.defineProperty(PopupMenu.prototype, "onKeyTyped", {
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
    if(!PopupMenu){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.onKeyTyped = {};
    }
    Object.defineProperty(PopupMenu.prototype, "onMouseWheelMoved", {
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
    if(!PopupMenu){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(PopupMenu.prototype, "onComponentRemoved", {
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
    if(!PopupMenu){
        /**
         * Component removed event handler function.
         * @property onComponentRemoved
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.onComponentRemoved = {};
    }
    Object.defineProperty(PopupMenu.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!PopupMenu){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.component = {};
    }
    Object.defineProperty(PopupMenu.prototype, "onFocusGained", {
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
    if(!PopupMenu){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.onFocusGained = {};
    }
    Object.defineProperty(PopupMenu.prototype, "left", {
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
    if(!PopupMenu){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.left = 0;
    }
    Object.defineProperty(PopupMenu.prototype, "background", {
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
    if(!PopupMenu){
        /**
         * The background color of this component.
         * @property background
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.background = {};
    }
    Object.defineProperty(PopupMenu.prototype, "onMouseClicked", {
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
    if(!PopupMenu){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.onMouseClicked = {};
    }
    Object.defineProperty(PopupMenu.prototype, "onMouseExited", {
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
    if(!PopupMenu){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.onMouseExited = {};
    }
    Object.defineProperty(PopupMenu.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!PopupMenu){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.name = '';
    }
    Object.defineProperty(PopupMenu.prototype, "width", {
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
    if(!PopupMenu){
        /**
         * Width of the component.
         * @property width
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.width = 0;
    }
    Object.defineProperty(PopupMenu.prototype, "font", {
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
    if(!PopupMenu){
        /**
         * The font of this component.
         * @property font
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.font = {};
    }
    Object.defineProperty(PopupMenu.prototype, "onKeyPressed", {
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
    if(!PopupMenu){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.onKeyPressed = {};
    }
    Object.defineProperty(PopupMenu.prototype, "add", {
        value: function(menu) {
            var delegate = this.unwrap();
            var value = delegate.add(P.boxAsJava(menu));
            return P.boxAsJs(value);
        }
    });
    if(!PopupMenu){
        /**
         * Adds the item to the menu.
         * @param menu the menu component to add.
         * @method add
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.add = function(menu){};
    }
    Object.defineProperty(PopupMenu.prototype, "child", {
        value: function(index) {
            var delegate = this.unwrap();
            var value = delegate.child(P.boxAsJava(index));
            return P.boxAsJs(value);
        }
    });
    if(!PopupMenu){
        /**
         * Gets the container's nth component.
         * @param index the component's index in the container
         * @return the child component
         * @method child
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.child = function(index){};
    }
    Object.defineProperty(PopupMenu.prototype, "remove", {
        value: function(component) {
            var delegate = this.unwrap();
            var value = delegate.remove(P.boxAsJava(component));
            return P.boxAsJs(value);
        }
    });
    if(!PopupMenu){
        /**
         * Removes the specified component from this container.
         * @param component the component to remove
         * @method remove
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.remove = function(component){};
    }
    Object.defineProperty(PopupMenu.prototype, "clear", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.clear();
            return P.boxAsJs(value);
        }
    });
    if(!PopupMenu){
        /**
         * Removes all the components from this container.
         * @method clear
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.clear = function(){};
    }
    Object.defineProperty(PopupMenu.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!PopupMenu){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf PopupMenu
         */
        P.PopupMenu.prototype.focus = function(){};
    }
})();