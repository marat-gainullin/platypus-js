(function() {
    var javaClass = Java.type("com.eas.client.forms.api.components.PasswordField");
    javaClass.setPublisher(function(aDelegate) {
        return new P.PasswordField(null, aDelegate);
    });
    
    /**
     * Password field component.
     * @param text the text for the component (optional).
     * @constructor PasswordField PasswordField
     */
    P.PasswordField = function PasswordField(text) {
        var maxArgs = 1;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 1 ? new javaClass(P.boxAsJava(text))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(PasswordField.superclass)
            PasswordField.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "PasswordField", {value: PasswordField});
    Object.defineProperty(PasswordField.prototype, "cursor", {
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
    if(!PasswordField){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.cursor = {};
    }
    Object.defineProperty(PasswordField.prototype, "onMouseDragged", {
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
    if(!PasswordField){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.onMouseDragged = {};
    }
    Object.defineProperty(PasswordField.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!PasswordField){
        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.parent = {};
    }
    Object.defineProperty(PasswordField.prototype, "onMouseReleased", {
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
    if(!PasswordField){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.onMouseReleased = {};
    }
    Object.defineProperty(PasswordField.prototype, "onFocusLost", {
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
    if(!PasswordField){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.onFocusLost = {};
    }
    Object.defineProperty(PasswordField.prototype, "emptyText", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.emptyText;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.emptyText = P.boxAsJava(aValue);
        }
    });
    if(!PasswordField){
        /**
         * The text to be shown when component's value is absent.
         * @property emptyText
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.emptyText = '';
    }
    Object.defineProperty(PasswordField.prototype, "onMousePressed", {
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
    if(!PasswordField){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.onMousePressed = {};
    }
    Object.defineProperty(PasswordField.prototype, "foreground", {
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
    if(!PasswordField){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.foreground = {};
    }
    Object.defineProperty(PasswordField.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!PasswordField){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.error = '';
    }
    Object.defineProperty(PasswordField.prototype, "enabled", {
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
    if(!PasswordField){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.enabled = true;
    }
    Object.defineProperty(PasswordField.prototype, "onComponentMoved", {
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
    if(!PasswordField){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.onComponentMoved = {};
    }
    Object.defineProperty(PasswordField.prototype, "componentPopupMenu", {
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
    if(!PasswordField){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(PasswordField.prototype, "top", {
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
    if(!PasswordField){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.top = 0;
    }
    Object.defineProperty(PasswordField.prototype, "onComponentResized", {
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
    if(!PasswordField){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.onComponentResized = {};
    }
    Object.defineProperty(PasswordField.prototype, "text", {
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
    if(!PasswordField){
        /**
         * The text contained in this component.
         * @property text
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.text = '';
    }
    Object.defineProperty(PasswordField.prototype, "onMouseEntered", {
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
    if(!PasswordField){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.onMouseEntered = {};
    }
    Object.defineProperty(PasswordField.prototype, "value", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.value;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.value = P.boxAsJava(aValue);
        }
    });
    if(!PasswordField){
        /**
         * The value of this component.
         * @property value
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.value = '';
    }
    Object.defineProperty(PasswordField.prototype, "toolTipText", {
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
    if(!PasswordField){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.toolTipText = '';
    }
    Object.defineProperty(PasswordField.prototype, "height", {
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
    if(!PasswordField){
        /**
         * Height of the component.
         * @property height
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.height = 0;
    }
    Object.defineProperty(PasswordField.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!PasswordField){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.element = {};
    }
    Object.defineProperty(PasswordField.prototype, "onComponentShown", {
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
    if(!PasswordField){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.onComponentShown = {};
    }
    Object.defineProperty(PasswordField.prototype, "onMouseMoved", {
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
    if(!PasswordField){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.onMouseMoved = {};
    }
    Object.defineProperty(PasswordField.prototype, "opaque", {
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
    if(!PasswordField){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.opaque = true;
    }
    Object.defineProperty(PasswordField.prototype, "visible", {
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
    if(!PasswordField){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.visible = true;
    }
    Object.defineProperty(PasswordField.prototype, "onComponentHidden", {
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
    if(!PasswordField){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.onComponentHidden = {};
    }
    Object.defineProperty(PasswordField.prototype, "nextFocusableComponent", {
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
    if(!PasswordField){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(PasswordField.prototype, "onActionPerformed", {
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
    if(!PasswordField){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.onActionPerformed = {};
    }
    Object.defineProperty(PasswordField.prototype, "onKeyReleased", {
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
    if(!PasswordField){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.onKeyReleased = {};
    }
    Object.defineProperty(PasswordField.prototype, "focusable", {
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
    if(!PasswordField){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.focusable = true;
    }
    Object.defineProperty(PasswordField.prototype, "onKeyTyped", {
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
    if(!PasswordField){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.onKeyTyped = {};
    }
    Object.defineProperty(PasswordField.prototype, "onMouseWheelMoved", {
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
    if(!PasswordField){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(PasswordField.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!PasswordField){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.component = {};
    }
    Object.defineProperty(PasswordField.prototype, "onFocusGained", {
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
    if(!PasswordField){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.onFocusGained = {};
    }
    Object.defineProperty(PasswordField.prototype, "left", {
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
    if(!PasswordField){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.left = 0;
    }
    Object.defineProperty(PasswordField.prototype, "background", {
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
    if(!PasswordField){
        /**
         * The background color of this component.
         * @property background
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.background = {};
    }
    Object.defineProperty(PasswordField.prototype, "onMouseClicked", {
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
    if(!PasswordField){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.onMouseClicked = {};
    }
    Object.defineProperty(PasswordField.prototype, "onMouseExited", {
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
    if(!PasswordField){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.onMouseExited = {};
    }
    Object.defineProperty(PasswordField.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!PasswordField){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.name = '';
    }
    Object.defineProperty(PasswordField.prototype, "width", {
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
    if(!PasswordField){
        /**
         * Width of the component.
         * @property width
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.width = 0;
    }
    Object.defineProperty(PasswordField.prototype, "font", {
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
    if(!PasswordField){
        /**
         * The font of this component.
         * @property font
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.font = {};
    }
    Object.defineProperty(PasswordField.prototype, "onKeyPressed", {
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
    if(!PasswordField){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.onKeyPressed = {};
    }
    Object.defineProperty(PasswordField.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!PasswordField){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.focus = function(){};
    }
})();