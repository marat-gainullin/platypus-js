(function() {
    var javaClass = Java.type("com.eas.client.forms.api.components.FormattedField");
    javaClass.setPublisher(function(aDelegate) {
        return new P.FormattedField(null, aDelegate);
    });
    
    /**
     * Formatted field component.
     * @param value the value for the formatted field (optional)
     * @constructor FormattedField FormattedField
     */
    P.FormattedField = function FormattedField(value) {
        var maxArgs = 1;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 1 ? new javaClass(P.boxAsJava(value))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(FormattedField.superclass)
            FormattedField.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "FormattedField", {value: FormattedField});
    Object.defineProperty(FormattedField.prototype, "cursor", {
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
    if(!FormattedField){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.cursor = {};
    }
    Object.defineProperty(FormattedField.prototype, "onMouseDragged", {
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
    if(!FormattedField){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.onMouseDragged = {};
    }
    Object.defineProperty(FormattedField.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!FormattedField){
        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.parent = {};
    }
    Object.defineProperty(FormattedField.prototype, "onMouseReleased", {
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
    if(!FormattedField){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.onMouseReleased = {};
    }
    Object.defineProperty(FormattedField.prototype, "onFocusLost", {
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
    if(!FormattedField){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.onFocusLost = {};
    }
    Object.defineProperty(FormattedField.prototype, "emptyText", {
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
    if(!FormattedField){
        /**
         * The text to be shown when component's value is absent.
         * @property emptyText
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.emptyText = '';
    }
    Object.defineProperty(FormattedField.prototype, "onMousePressed", {
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
    if(!FormattedField){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.onMousePressed = {};
    }
    Object.defineProperty(FormattedField.prototype, "foreground", {
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
    if(!FormattedField){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.foreground = {};
    }
    Object.defineProperty(FormattedField.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!FormattedField){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.error = '';
    }
    Object.defineProperty(FormattedField.prototype, "enabled", {
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
    if(!FormattedField){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.enabled = true;
    }
    Object.defineProperty(FormattedField.prototype, "onComponentMoved", {
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
    if(!FormattedField){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.onComponentMoved = {};
    }
    Object.defineProperty(FormattedField.prototype, "componentPopupMenu", {
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
    if(!FormattedField){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(FormattedField.prototype, "top", {
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
    if(!FormattedField){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.top = 0;
    }
    Object.defineProperty(FormattedField.prototype, "onComponentResized", {
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
    if(!FormattedField){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.onComponentResized = {};
    }
    Object.defineProperty(FormattedField.prototype, "text", {
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
    if(!FormattedField){
        /**
         * Text on the component.
         * @property text
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.text = '';
    }
    Object.defineProperty(FormattedField.prototype, "onMouseEntered", {
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
    if(!FormattedField){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.onMouseEntered = {};
    }
    Object.defineProperty(FormattedField.prototype, "value", {
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
    if(!FormattedField){
        /**
         * The value of this component.
         * @property value
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.value = {};
    }
    Object.defineProperty(FormattedField.prototype, "toolTipText", {
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
    if(!FormattedField){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.toolTipText = '';
    }
    Object.defineProperty(FormattedField.prototype, "height", {
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
    if(!FormattedField){
        /**
         * Height of the component.
         * @property height
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.height = 0;
    }
    Object.defineProperty(FormattedField.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!FormattedField){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.element = {};
    }
    Object.defineProperty(FormattedField.prototype, "onComponentShown", {
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
    if(!FormattedField){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.onComponentShown = {};
    }
    Object.defineProperty(FormattedField.prototype, "onMouseMoved", {
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
    if(!FormattedField){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.onMouseMoved = {};
    }
    Object.defineProperty(FormattedField.prototype, "opaque", {
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
    if(!FormattedField){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.opaque = true;
    }
    Object.defineProperty(FormattedField.prototype, "visible", {
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
    if(!FormattedField){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.visible = true;
    }
    Object.defineProperty(FormattedField.prototype, "onComponentHidden", {
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
    if(!FormattedField){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.onComponentHidden = {};
    }
    Object.defineProperty(FormattedField.prototype, "nextFocusableComponent", {
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
    if(!FormattedField){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(FormattedField.prototype, "format", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.format;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.format = P.boxAsJava(aValue);
        }
    });
    if(!FormattedField){
        /**
         * Field text format.
         * @property format
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.format = '';
    }
    Object.defineProperty(FormattedField.prototype, "onActionPerformed", {
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
    if(!FormattedField){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.onActionPerformed = {};
    }
    Object.defineProperty(FormattedField.prototype, "onKeyReleased", {
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
    if(!FormattedField){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.onKeyReleased = {};
    }
    Object.defineProperty(FormattedField.prototype, "focusable", {
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
    if(!FormattedField){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.focusable = true;
    }
    Object.defineProperty(FormattedField.prototype, "onKeyTyped", {
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
    if(!FormattedField){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.onKeyTyped = {};
    }
    Object.defineProperty(FormattedField.prototype, "onMouseWheelMoved", {
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
    if(!FormattedField){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(FormattedField.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!FormattedField){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.component = {};
    }
    Object.defineProperty(FormattedField.prototype, "onFocusGained", {
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
    if(!FormattedField){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.onFocusGained = {};
    }
    Object.defineProperty(FormattedField.prototype, "left", {
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
    if(!FormattedField){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.left = 0;
    }
    Object.defineProperty(FormattedField.prototype, "background", {
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
    if(!FormattedField){
        /**
         * The background color of this component.
         * @property background
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.background = {};
    }
    Object.defineProperty(FormattedField.prototype, "onMouseClicked", {
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
    if(!FormattedField){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.onMouseClicked = {};
    }
    Object.defineProperty(FormattedField.prototype, "onMouseExited", {
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
    if(!FormattedField){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.onMouseExited = {};
    }
    Object.defineProperty(FormattedField.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!FormattedField){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.name = '';
    }
    Object.defineProperty(FormattedField.prototype, "width", {
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
    if(!FormattedField){
        /**
         * Width of the component.
         * @property width
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.width = 0;
    }
    Object.defineProperty(FormattedField.prototype, "font", {
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
    if(!FormattedField){
        /**
         * The font of this component.
         * @property font
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.font = {};
    }
    Object.defineProperty(FormattedField.prototype, "onKeyPressed", {
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
    if(!FormattedField){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.onKeyPressed = {};
    }
    Object.defineProperty(FormattedField.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!FormattedField){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.focus = function(){};
    }
})();