(function() {
    var javaClass = Java.type("com.eas.client.forms.api.menu.MenuItem");
    javaClass.setPublisher(function(aDelegate) {
        return new P.MenuItem(null, null, null, aDelegate);
    });
    
    /**
     * A menu item that can be selected or deselected.
     * @param text the text of the component (optional).
     * @param icon the icon of the component (optional).
     * @param actionPerformed the function for the action performed handler (optional).
     * @constructor MenuItem MenuItem
     */
    P.MenuItem = function MenuItem(text, icon, actionPerformed) {
        var maxArgs = 3;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 3 ? new javaClass(P.boxAsJava(text), P.boxAsJava(icon), P.boxAsJava(actionPerformed))
            : arguments.length === 2 ? new javaClass(P.boxAsJava(text), P.boxAsJava(icon))
            : arguments.length === 1 ? new javaClass(P.boxAsJava(text))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(MenuItem.superclass)
            MenuItem.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "MenuItem", {value: MenuItem});
    Object.defineProperty(MenuItem.prototype, "cursor", {
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
    if(!MenuItem){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.cursor = {};
    }
    Object.defineProperty(MenuItem.prototype, "onMouseDragged", {
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
    if(!MenuItem){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.onMouseDragged = {};
    }
    Object.defineProperty(MenuItem.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!MenuItem){
        /**
         * The parent container.
         * @property parent
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.parent = {};
    }
    Object.defineProperty(MenuItem.prototype, "onMouseReleased", {
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
    if(!MenuItem){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.onMouseReleased = {};
    }
    Object.defineProperty(MenuItem.prototype, "onFocusLost", {
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
    if(!MenuItem){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.onFocusLost = {};
    }
    Object.defineProperty(MenuItem.prototype, "icon", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.icon;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.icon = P.boxAsJava(aValue);
        }
    });
    if(!MenuItem){
        /**
         * Image picture for the menu item.
         * @property icon
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.icon = {};
    }
    Object.defineProperty(MenuItem.prototype, "onMousePressed", {
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
    if(!MenuItem){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.onMousePressed = {};
    }
    Object.defineProperty(MenuItem.prototype, "foreground", {
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
    if(!MenuItem){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.foreground = {};
    }
    Object.defineProperty(MenuItem.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!MenuItem){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.error = '';
    }
    Object.defineProperty(MenuItem.prototype, "enabled", {
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
    if(!MenuItem){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.enabled = true;
    }
    Object.defineProperty(MenuItem.prototype, "onComponentMoved", {
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
    if(!MenuItem){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.onComponentMoved = {};
    }
    Object.defineProperty(MenuItem.prototype, "componentPopupMenu", {
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
    if(!MenuItem){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(MenuItem.prototype, "top", {
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
    if(!MenuItem){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.top = 0;
    }
    Object.defineProperty(MenuItem.prototype, "onComponentResized", {
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
    if(!MenuItem){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.onComponentResized = {};
    }
    Object.defineProperty(MenuItem.prototype, "text", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.text;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.text = P.boxAsJava(aValue);
        }
    });
    if(!MenuItem){
        /**
         * The menu item text.
         * @property text
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.text = '';
    }
    Object.defineProperty(MenuItem.prototype, "onMouseEntered", {
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
    if(!MenuItem){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.onMouseEntered = {};
    }
    Object.defineProperty(MenuItem.prototype, "toolTipText", {
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
    if(!MenuItem){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.toolTipText = '';
    }
    Object.defineProperty(MenuItem.prototype, "height", {
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
    if(!MenuItem){
        /**
         * Height of the component.
         * @property height
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.height = 0;
    }
    Object.defineProperty(MenuItem.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!MenuItem){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.element = {};
    }
    Object.defineProperty(MenuItem.prototype, "onComponentShown", {
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
    if(!MenuItem){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.onComponentShown = {};
    }
    Object.defineProperty(MenuItem.prototype, "onMouseMoved", {
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
    if(!MenuItem){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.onMouseMoved = {};
    }
    Object.defineProperty(MenuItem.prototype, "opaque", {
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
    if(!MenuItem){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.opaque = true;
    }
    Object.defineProperty(MenuItem.prototype, "visible", {
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
    if(!MenuItem){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.visible = true;
    }
    Object.defineProperty(MenuItem.prototype, "onComponentHidden", {
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
    if(!MenuItem){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.onComponentHidden = {};
    }
    Object.defineProperty(MenuItem.prototype, "nextFocusableComponent", {
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
    if(!MenuItem){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(MenuItem.prototype, "onActionPerformed", {
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
    if(!MenuItem){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.onActionPerformed = {};
    }
    Object.defineProperty(MenuItem.prototype, "onKeyReleased", {
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
    if(!MenuItem){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.onKeyReleased = {};
    }
    Object.defineProperty(MenuItem.prototype, "focusable", {
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
    if(!MenuItem){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.focusable = true;
    }
    Object.defineProperty(MenuItem.prototype, "onKeyTyped", {
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
    if(!MenuItem){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.onKeyTyped = {};
    }
    Object.defineProperty(MenuItem.prototype, "horizontalTextPosition", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.horizontalTextPosition;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.horizontalTextPosition = P.boxAsJava(aValue);
        }
    });
    if(!MenuItem){
        /**
         * Horizontal position of the text relative to the icon.
         * @property horizontalTextPosition
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.horizontalTextPosition = 0;
    }
    Object.defineProperty(MenuItem.prototype, "verticalTextPosition", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.verticalTextPosition;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.verticalTextPosition = P.boxAsJava(aValue);
        }
    });
    if(!MenuItem){
        /**
         * Vertical position of the text relative to the icon.
         * @property verticalTextPosition
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.verticalTextPosition = 0;
    }
    Object.defineProperty(MenuItem.prototype, "onMouseWheelMoved", {
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
    if(!MenuItem){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(MenuItem.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!MenuItem){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.component = {};
    }
    Object.defineProperty(MenuItem.prototype, "onFocusGained", {
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
    if(!MenuItem){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.onFocusGained = {};
    }
    Object.defineProperty(MenuItem.prototype, "left", {
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
    if(!MenuItem){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.left = 0;
    }
    Object.defineProperty(MenuItem.prototype, "background", {
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
    if(!MenuItem){
        /**
         * The background color of this component.
         * @property background
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.background = {};
    }
    Object.defineProperty(MenuItem.prototype, "onMouseClicked", {
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
    if(!MenuItem){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.onMouseClicked = {};
    }
    Object.defineProperty(MenuItem.prototype, "onMouseExited", {
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
    if(!MenuItem){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.onMouseExited = {};
    }
    Object.defineProperty(MenuItem.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!MenuItem){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.name = '';
    }
    Object.defineProperty(MenuItem.prototype, "width", {
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
    if(!MenuItem){
        /**
         * Width of the component.
         * @property width
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.width = 0;
    }
    Object.defineProperty(MenuItem.prototype, "font", {
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
    if(!MenuItem){
        /**
         * The font of this component.
         * @property font
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.font = {};
    }
    Object.defineProperty(MenuItem.prototype, "onKeyPressed", {
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
    if(!MenuItem){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.onKeyPressed = {};
    }
    Object.defineProperty(MenuItem.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!MenuItem){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.focus = function(){};
    }
})();