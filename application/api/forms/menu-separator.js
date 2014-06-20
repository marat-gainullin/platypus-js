(function() {
    var javaClass = Java.type("com.eas.client.forms.api.menu.MenuSeparator");
    javaClass.setPublisher(function(aDelegate) {
        return new P.MenuSeparator(aDelegate);
    });
    
    /**
     * MenuSeparator provides a general purpose component for
     * implementing divider lines - most commonly used as a divider
     * between menu items that breaks them up into logical groupings.
     * @constructor MenuSeparator MenuSeparator
     */
    P.MenuSeparator = function MenuSeparator() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(MenuSeparator.superclass)
            MenuSeparator.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "MenuSeparator", {value: MenuSeparator});
    Object.defineProperty(MenuSeparator.prototype, "cursor", {
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
    if(!MenuSeparator){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf MenuSeparator
         */
        P.MenuSeparator.prototype.cursor = {};
    }
    Object.defineProperty(MenuSeparator.prototype, "onMouseDragged", {
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
    if(!MenuSeparator){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf MenuSeparator
         */
        P.MenuSeparator.prototype.onMouseDragged = {};
    }
    Object.defineProperty(MenuSeparator.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!MenuSeparator){
        /**
         * The parent container.
         * @property parent
         * @memberOf MenuSeparator
         */
        P.MenuSeparator.prototype.parent = {};
    }
    Object.defineProperty(MenuSeparator.prototype, "onMouseReleased", {
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
    if(!MenuSeparator){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf MenuSeparator
         */
        P.MenuSeparator.prototype.onMouseReleased = {};
    }
    Object.defineProperty(MenuSeparator.prototype, "onFocusLost", {
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
    if(!MenuSeparator){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf MenuSeparator
         */
        P.MenuSeparator.prototype.onFocusLost = {};
    }
    Object.defineProperty(MenuSeparator.prototype, "onMousePressed", {
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
    if(!MenuSeparator){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf MenuSeparator
         */
        P.MenuSeparator.prototype.onMousePressed = {};
    }
    Object.defineProperty(MenuSeparator.prototype, "foreground", {
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
    if(!MenuSeparator){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf MenuSeparator
         */
        P.MenuSeparator.prototype.foreground = {};
    }
    Object.defineProperty(MenuSeparator.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!MenuSeparator){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf MenuSeparator
         */
        P.MenuSeparator.prototype.error = '';
    }
    Object.defineProperty(MenuSeparator.prototype, "enabled", {
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
    if(!MenuSeparator){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf MenuSeparator
         */
        P.MenuSeparator.prototype.enabled = true;
    }
    Object.defineProperty(MenuSeparator.prototype, "onComponentMoved", {
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
    if(!MenuSeparator){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf MenuSeparator
         */
        P.MenuSeparator.prototype.onComponentMoved = {};
    }
    Object.defineProperty(MenuSeparator.prototype, "componentPopupMenu", {
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
    if(!MenuSeparator){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf MenuSeparator
         */
        P.MenuSeparator.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(MenuSeparator.prototype, "top", {
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
    if(!MenuSeparator){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf MenuSeparator
         */
        P.MenuSeparator.prototype.top = 0;
    }
    Object.defineProperty(MenuSeparator.prototype, "onComponentResized", {
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
    if(!MenuSeparator){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf MenuSeparator
         */
        P.MenuSeparator.prototype.onComponentResized = {};
    }
    Object.defineProperty(MenuSeparator.prototype, "onMouseEntered", {
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
    if(!MenuSeparator){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf MenuSeparator
         */
        P.MenuSeparator.prototype.onMouseEntered = {};
    }
    Object.defineProperty(MenuSeparator.prototype, "toolTipText", {
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
    if(!MenuSeparator){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf MenuSeparator
         */
        P.MenuSeparator.prototype.toolTipText = '';
    }
    Object.defineProperty(MenuSeparator.prototype, "height", {
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
    if(!MenuSeparator){
        /**
         * Height of the component.
         * @property height
         * @memberOf MenuSeparator
         */
        P.MenuSeparator.prototype.height = 0;
    }
    Object.defineProperty(MenuSeparator.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!MenuSeparator){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf MenuSeparator
         */
        P.MenuSeparator.prototype.element = {};
    }
    Object.defineProperty(MenuSeparator.prototype, "onComponentShown", {
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
    if(!MenuSeparator){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf MenuSeparator
         */
        P.MenuSeparator.prototype.onComponentShown = {};
    }
    Object.defineProperty(MenuSeparator.prototype, "onMouseMoved", {
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
    if(!MenuSeparator){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf MenuSeparator
         */
        P.MenuSeparator.prototype.onMouseMoved = {};
    }
    Object.defineProperty(MenuSeparator.prototype, "opaque", {
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
    if(!MenuSeparator){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf MenuSeparator
         */
        P.MenuSeparator.prototype.opaque = true;
    }
    Object.defineProperty(MenuSeparator.prototype, "visible", {
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
    if(!MenuSeparator){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf MenuSeparator
         */
        P.MenuSeparator.prototype.visible = true;
    }
    Object.defineProperty(MenuSeparator.prototype, "onComponentHidden", {
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
    if(!MenuSeparator){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf MenuSeparator
         */
        P.MenuSeparator.prototype.onComponentHidden = {};
    }
    Object.defineProperty(MenuSeparator.prototype, "nextFocusableComponent", {
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
    if(!MenuSeparator){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf MenuSeparator
         */
        P.MenuSeparator.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(MenuSeparator.prototype, "onActionPerformed", {
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
    if(!MenuSeparator){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf MenuSeparator
         */
        P.MenuSeparator.prototype.onActionPerformed = {};
    }
    Object.defineProperty(MenuSeparator.prototype, "onKeyReleased", {
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
    if(!MenuSeparator){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf MenuSeparator
         */
        P.MenuSeparator.prototype.onKeyReleased = {};
    }
    Object.defineProperty(MenuSeparator.prototype, "focusable", {
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
    if(!MenuSeparator){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf MenuSeparator
         */
        P.MenuSeparator.prototype.focusable = true;
    }
    Object.defineProperty(MenuSeparator.prototype, "onKeyTyped", {
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
    if(!MenuSeparator){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf MenuSeparator
         */
        P.MenuSeparator.prototype.onKeyTyped = {};
    }
    Object.defineProperty(MenuSeparator.prototype, "onMouseWheelMoved", {
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
    if(!MenuSeparator){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf MenuSeparator
         */
        P.MenuSeparator.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(MenuSeparator.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!MenuSeparator){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf MenuSeparator
         */
        P.MenuSeparator.prototype.component = {};
    }
    Object.defineProperty(MenuSeparator.prototype, "onFocusGained", {
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
    if(!MenuSeparator){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf MenuSeparator
         */
        P.MenuSeparator.prototype.onFocusGained = {};
    }
    Object.defineProperty(MenuSeparator.prototype, "left", {
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
    if(!MenuSeparator){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf MenuSeparator
         */
        P.MenuSeparator.prototype.left = 0;
    }
    Object.defineProperty(MenuSeparator.prototype, "background", {
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
    if(!MenuSeparator){
        /**
         * The background color of this component.
         * @property background
         * @memberOf MenuSeparator
         */
        P.MenuSeparator.prototype.background = {};
    }
    Object.defineProperty(MenuSeparator.prototype, "onMouseClicked", {
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
    if(!MenuSeparator){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf MenuSeparator
         */
        P.MenuSeparator.prototype.onMouseClicked = {};
    }
    Object.defineProperty(MenuSeparator.prototype, "onMouseExited", {
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
    if(!MenuSeparator){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf MenuSeparator
         */
        P.MenuSeparator.prototype.onMouseExited = {};
    }
    Object.defineProperty(MenuSeparator.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!MenuSeparator){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf MenuSeparator
         */
        P.MenuSeparator.prototype.name = '';
    }
    Object.defineProperty(MenuSeparator.prototype, "width", {
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
    if(!MenuSeparator){
        /**
         * Width of the component.
         * @property width
         * @memberOf MenuSeparator
         */
        P.MenuSeparator.prototype.width = 0;
    }
    Object.defineProperty(MenuSeparator.prototype, "font", {
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
    if(!MenuSeparator){
        /**
         * The font of this component.
         * @property font
         * @memberOf MenuSeparator
         */
        P.MenuSeparator.prototype.font = {};
    }
    Object.defineProperty(MenuSeparator.prototype, "onKeyPressed", {
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
    if(!MenuSeparator){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf MenuSeparator
         */
        P.MenuSeparator.prototype.onKeyPressed = {};
    }
    Object.defineProperty(MenuSeparator.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!MenuSeparator){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf MenuSeparator
         */
        P.MenuSeparator.prototype.focus = function(){};
    }
})();