(function() {
    var javaClass = Java.type("com.eas.client.forms.api.components.TextField");
    javaClass.setPublisher(function(aDelegate) {
        return new P.TextField(null, aDelegate);
    });
    
    /**
     * Text field component.
     * @param text the initial text for the component (optional)
     * @constructor TextField TextField
     */
    P.TextField = function TextField(text) {
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
        if(TextField.superclass)
            TextField.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "TextField", {value: TextField});
    Object.defineProperty(TextField.prototype, "cursor", {
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
    if(!TextField){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf TextField
         */
        P.TextField.prototype.cursor = {};
    }
    Object.defineProperty(TextField.prototype, "onMouseDragged", {
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
    if(!TextField){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf TextField
         */
        P.TextField.prototype.onMouseDragged = {};
    }
    Object.defineProperty(TextField.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!TextField){
        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf TextField
         */
        P.TextField.prototype.parent = {};
    }
    Object.defineProperty(TextField.prototype, "onMouseReleased", {
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
    if(!TextField){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf TextField
         */
        P.TextField.prototype.onMouseReleased = {};
    }
    Object.defineProperty(TextField.prototype, "onFocusLost", {
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
    if(!TextField){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf TextField
         */
        P.TextField.prototype.onFocusLost = {};
    }
    Object.defineProperty(TextField.prototype, "emptyText", {
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
    if(!TextField){
        /**
         * The text to be shown when component's value is absent.
         * @property emptyText
         * @memberOf TextField
         */
        P.TextField.prototype.emptyText = '';
    }
    Object.defineProperty(TextField.prototype, "onMousePressed", {
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
    if(!TextField){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf TextField
         */
        P.TextField.prototype.onMousePressed = {};
    }
    Object.defineProperty(TextField.prototype, "foreground", {
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
    if(!TextField){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf TextField
         */
        P.TextField.prototype.foreground = {};
    }
    Object.defineProperty(TextField.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!TextField){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf TextField
         */
        P.TextField.prototype.error = '';
    }
    Object.defineProperty(TextField.prototype, "enabled", {
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
    if(!TextField){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf TextField
         */
        P.TextField.prototype.enabled = true;
    }
    Object.defineProperty(TextField.prototype, "onComponentMoved", {
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
    if(!TextField){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf TextField
         */
        P.TextField.prototype.onComponentMoved = {};
    }
    Object.defineProperty(TextField.prototype, "componentPopupMenu", {
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
    if(!TextField){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf TextField
         */
        P.TextField.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(TextField.prototype, "top", {
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
    if(!TextField){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf TextField
         */
        P.TextField.prototype.top = 0;
    }
    Object.defineProperty(TextField.prototype, "onComponentResized", {
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
    if(!TextField){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf TextField
         */
        P.TextField.prototype.onComponentResized = {};
    }
    Object.defineProperty(TextField.prototype, "text", {
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
    if(!TextField){
        /**
         * The text contained in this component.
         * @property text
         * @memberOf TextField
         */
        P.TextField.prototype.text = '';
    }
    Object.defineProperty(TextField.prototype, "onMouseEntered", {
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
    if(!TextField){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf TextField
         */
        P.TextField.prototype.onMouseEntered = {};
    }
    Object.defineProperty(TextField.prototype, "value", {
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
    if(!TextField){
        /**
         * Generated property jsDoc.
         * @property value
         * @memberOf TextField
         */
        P.TextField.prototype.value = {};
    }
    Object.defineProperty(TextField.prototype, "toolTipText", {
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
    if(!TextField){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf TextField
         */
        P.TextField.prototype.toolTipText = '';
    }
    Object.defineProperty(TextField.prototype, "height", {
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
    if(!TextField){
        /**
         * Height of the component.
         * @property height
         * @memberOf TextField
         */
        P.TextField.prototype.height = 0;
    }
    Object.defineProperty(TextField.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!TextField){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf TextField
         */
        P.TextField.prototype.element = {};
    }
    Object.defineProperty(TextField.prototype, "onComponentShown", {
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
    if(!TextField){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf TextField
         */
        P.TextField.prototype.onComponentShown = {};
    }
    Object.defineProperty(TextField.prototype, "onMouseMoved", {
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
    if(!TextField){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf TextField
         */
        P.TextField.prototype.onMouseMoved = {};
    }
    Object.defineProperty(TextField.prototype, "opaque", {
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
    if(!TextField){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf TextField
         */
        P.TextField.prototype.opaque = true;
    }
    Object.defineProperty(TextField.prototype, "visible", {
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
    if(!TextField){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf TextField
         */
        P.TextField.prototype.visible = true;
    }
    Object.defineProperty(TextField.prototype, "onComponentHidden", {
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
    if(!TextField){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf TextField
         */
        P.TextField.prototype.onComponentHidden = {};
    }
    Object.defineProperty(TextField.prototype, "nextFocusableComponent", {
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
    if(!TextField){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf TextField
         */
        P.TextField.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(TextField.prototype, "onActionPerformed", {
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
    if(!TextField){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf TextField
         */
        P.TextField.prototype.onActionPerformed = {};
    }
    Object.defineProperty(TextField.prototype, "onKeyReleased", {
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
    if(!TextField){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf TextField
         */
        P.TextField.prototype.onKeyReleased = {};
    }
    Object.defineProperty(TextField.prototype, "focusable", {
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
    if(!TextField){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf TextField
         */
        P.TextField.prototype.focusable = true;
    }
    Object.defineProperty(TextField.prototype, "onKeyTyped", {
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
    if(!TextField){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf TextField
         */
        P.TextField.prototype.onKeyTyped = {};
    }
    Object.defineProperty(TextField.prototype, "onMouseWheelMoved", {
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
    if(!TextField){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf TextField
         */
        P.TextField.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(TextField.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!TextField){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf TextField
         */
        P.TextField.prototype.component = {};
    }
    Object.defineProperty(TextField.prototype, "onFocusGained", {
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
    if(!TextField){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf TextField
         */
        P.TextField.prototype.onFocusGained = {};
    }
    Object.defineProperty(TextField.prototype, "left", {
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
    if(!TextField){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf TextField
         */
        P.TextField.prototype.left = 0;
    }
    Object.defineProperty(TextField.prototype, "background", {
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
    if(!TextField){
        /**
         * The background color of this component.
         * @property background
         * @memberOf TextField
         */
        P.TextField.prototype.background = {};
    }
    Object.defineProperty(TextField.prototype, "onMouseClicked", {
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
    if(!TextField){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf TextField
         */
        P.TextField.prototype.onMouseClicked = {};
    }
    Object.defineProperty(TextField.prototype, "onMouseExited", {
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
    if(!TextField){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf TextField
         */
        P.TextField.prototype.onMouseExited = {};
    }
    Object.defineProperty(TextField.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!TextField){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf TextField
         */
        P.TextField.prototype.name = '';
    }
    Object.defineProperty(TextField.prototype, "width", {
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
    if(!TextField){
        /**
         * Width of the component.
         * @property width
         * @memberOf TextField
         */
        P.TextField.prototype.width = 0;
    }
    Object.defineProperty(TextField.prototype, "font", {
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
    if(!TextField){
        /**
         * The font of this component.
         * @property font
         * @memberOf TextField
         */
        P.TextField.prototype.font = {};
    }
    Object.defineProperty(TextField.prototype, "onKeyPressed", {
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
    if(!TextField){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf TextField
         */
        P.TextField.prototype.onKeyPressed = {};
    }
    Object.defineProperty(TextField.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!TextField){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf TextField
         */
        P.TextField.prototype.focus = function(){};
    }
})();