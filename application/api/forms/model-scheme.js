(function() {
    var javaClass = Java.type("com.eas.client.forms.api.components.model.ModelScheme");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ModelScheme(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor ModelScheme ModelScheme
     */
    P.ModelScheme = function ModelScheme() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(ModelScheme.superclass)
            ModelScheme.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "ModelScheme", {value: ModelScheme});
    Object.defineProperty(ModelScheme.prototype, "cursor", {
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
    if(!ModelScheme){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf ModelScheme
         */
        P.ModelScheme.prototype.cursor = {};
    }
    Object.defineProperty(ModelScheme.prototype, "onMouseDragged", {
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
    if(!ModelScheme){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf ModelScheme
         */
        P.ModelScheme.prototype.onMouseDragged = {};
    }
    Object.defineProperty(ModelScheme.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!ModelScheme){
        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf ModelScheme
         */
        P.ModelScheme.prototype.parent = {};
    }
    Object.defineProperty(ModelScheme.prototype, "onMouseReleased", {
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
    if(!ModelScheme){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf ModelScheme
         */
        P.ModelScheme.prototype.onMouseReleased = {};
    }
    Object.defineProperty(ModelScheme.prototype, "onFocusLost", {
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
    if(!ModelScheme){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf ModelScheme
         */
        P.ModelScheme.prototype.onFocusLost = {};
    }
    Object.defineProperty(ModelScheme.prototype, "onMousePressed", {
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
    if(!ModelScheme){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf ModelScheme
         */
        P.ModelScheme.prototype.onMousePressed = {};
    }
    Object.defineProperty(ModelScheme.prototype, "foreground", {
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
    if(!ModelScheme){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf ModelScheme
         */
        P.ModelScheme.prototype.foreground = {};
    }
    Object.defineProperty(ModelScheme.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!ModelScheme){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf ModelScheme
         */
        P.ModelScheme.prototype.error = '';
    }
    Object.defineProperty(ModelScheme.prototype, "enabled", {
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
    if(!ModelScheme){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf ModelScheme
         */
        P.ModelScheme.prototype.enabled = true;
    }
    Object.defineProperty(ModelScheme.prototype, "onComponentMoved", {
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
    if(!ModelScheme){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf ModelScheme
         */
        P.ModelScheme.prototype.onComponentMoved = {};
    }
    Object.defineProperty(ModelScheme.prototype, "componentPopupMenu", {
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
    if(!ModelScheme){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf ModelScheme
         */
        P.ModelScheme.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(ModelScheme.prototype, "top", {
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
    if(!ModelScheme){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf ModelScheme
         */
        P.ModelScheme.prototype.top = 0;
    }
    Object.defineProperty(ModelScheme.prototype, "onComponentResized", {
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
    if(!ModelScheme){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf ModelScheme
         */
        P.ModelScheme.prototype.onComponentResized = {};
    }
    Object.defineProperty(ModelScheme.prototype, "onMouseEntered", {
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
    if(!ModelScheme){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf ModelScheme
         */
        P.ModelScheme.prototype.onMouseEntered = {};
    }
    Object.defineProperty(ModelScheme.prototype, "toolTipText", {
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
    if(!ModelScheme){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf ModelScheme
         */
        P.ModelScheme.prototype.toolTipText = '';
    }
    Object.defineProperty(ModelScheme.prototype, "height", {
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
    if(!ModelScheme){
        /**
         * Height of the component.
         * @property height
         * @memberOf ModelScheme
         */
        P.ModelScheme.prototype.height = 0;
    }
    Object.defineProperty(ModelScheme.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!ModelScheme){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf ModelScheme
         */
        P.ModelScheme.prototype.element = {};
    }
    Object.defineProperty(ModelScheme.prototype, "onComponentShown", {
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
    if(!ModelScheme){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf ModelScheme
         */
        P.ModelScheme.prototype.onComponentShown = {};
    }
    Object.defineProperty(ModelScheme.prototype, "onMouseMoved", {
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
    if(!ModelScheme){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf ModelScheme
         */
        P.ModelScheme.prototype.onMouseMoved = {};
    }
    Object.defineProperty(ModelScheme.prototype, "opaque", {
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
    if(!ModelScheme){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf ModelScheme
         */
        P.ModelScheme.prototype.opaque = true;
    }
    Object.defineProperty(ModelScheme.prototype, "visible", {
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
    if(!ModelScheme){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf ModelScheme
         */
        P.ModelScheme.prototype.visible = true;
    }
    Object.defineProperty(ModelScheme.prototype, "onComponentHidden", {
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
    if(!ModelScheme){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf ModelScheme
         */
        P.ModelScheme.prototype.onComponentHidden = {};
    }
    Object.defineProperty(ModelScheme.prototype, "nextFocusableComponent", {
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
    if(!ModelScheme){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf ModelScheme
         */
        P.ModelScheme.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(ModelScheme.prototype, "onActionPerformed", {
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
    if(!ModelScheme){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf ModelScheme
         */
        P.ModelScheme.prototype.onActionPerformed = {};
    }
    Object.defineProperty(ModelScheme.prototype, "onKeyReleased", {
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
    if(!ModelScheme){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf ModelScheme
         */
        P.ModelScheme.prototype.onKeyReleased = {};
    }
    Object.defineProperty(ModelScheme.prototype, "focusable", {
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
    if(!ModelScheme){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf ModelScheme
         */
        P.ModelScheme.prototype.focusable = true;
    }
    Object.defineProperty(ModelScheme.prototype, "onKeyTyped", {
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
    if(!ModelScheme){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf ModelScheme
         */
        P.ModelScheme.prototype.onKeyTyped = {};
    }
    Object.defineProperty(ModelScheme.prototype, "onMouseWheelMoved", {
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
    if(!ModelScheme){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf ModelScheme
         */
        P.ModelScheme.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(ModelScheme.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!ModelScheme){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf ModelScheme
         */
        P.ModelScheme.prototype.component = {};
    }
    Object.defineProperty(ModelScheme.prototype, "onFocusGained", {
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
    if(!ModelScheme){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf ModelScheme
         */
        P.ModelScheme.prototype.onFocusGained = {};
    }
    Object.defineProperty(ModelScheme.prototype, "left", {
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
    if(!ModelScheme){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf ModelScheme
         */
        P.ModelScheme.prototype.left = 0;
    }
    Object.defineProperty(ModelScheme.prototype, "background", {
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
    if(!ModelScheme){
        /**
         * The background color of this component.
         * @property background
         * @memberOf ModelScheme
         */
        P.ModelScheme.prototype.background = {};
    }
    Object.defineProperty(ModelScheme.prototype, "onMouseClicked", {
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
    if(!ModelScheme){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf ModelScheme
         */
        P.ModelScheme.prototype.onMouseClicked = {};
    }
    Object.defineProperty(ModelScheme.prototype, "onMouseExited", {
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
    if(!ModelScheme){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf ModelScheme
         */
        P.ModelScheme.prototype.onMouseExited = {};
    }
    Object.defineProperty(ModelScheme.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!ModelScheme){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf ModelScheme
         */
        P.ModelScheme.prototype.name = '';
    }
    Object.defineProperty(ModelScheme.prototype, "width", {
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
    if(!ModelScheme){
        /**
         * Width of the component.
         * @property width
         * @memberOf ModelScheme
         */
        P.ModelScheme.prototype.width = 0;
    }
    Object.defineProperty(ModelScheme.prototype, "font", {
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
    if(!ModelScheme){
        /**
         * The font of this component.
         * @property font
         * @memberOf ModelScheme
         */
        P.ModelScheme.prototype.font = {};
    }
    Object.defineProperty(ModelScheme.prototype, "onKeyPressed", {
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
    if(!ModelScheme){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf ModelScheme
         */
        P.ModelScheme.prototype.onKeyPressed = {};
    }
    Object.defineProperty(ModelScheme.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!ModelScheme){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf ModelScheme
         */
        P.ModelScheme.prototype.focus = function(){};
    }
})();