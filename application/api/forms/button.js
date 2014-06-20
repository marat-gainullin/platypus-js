(function() {
    var javaClass = Java.type("com.eas.client.forms.api.components.Button");
    javaClass.setPublisher(function(aDelegate) {
        return new P.Button(null, null, null, null, aDelegate);
    });
    
    /**
     * Simple button component.
     * @param text the text of the component (optional)
     * @param icon the icon of the component (optional)
     * @param iconTextGap the text gap (optional)
     * @param actionPerformed the function for the action performed handler(optional)
     * @constructor Button Button
     */
    P.Button = function Button(text, icon, iconTextGap, actionPerformed) {
        var maxArgs = 4;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 4 ? new javaClass(P.boxAsJava(text), P.boxAsJava(icon), P.boxAsJava(iconTextGap), P.boxAsJava(actionPerformed))
            : arguments.length === 3 ? new javaClass(P.boxAsJava(text), P.boxAsJava(icon), P.boxAsJava(iconTextGap))
            : arguments.length === 2 ? new javaClass(P.boxAsJava(text), P.boxAsJava(icon))
            : arguments.length === 1 ? new javaClass(P.boxAsJava(text))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(Button.superclass)
            Button.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "Button", {value: Button});
    Object.defineProperty(Button.prototype, "cursor", {
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
    if(!Button){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf Button
         */
        P.Button.prototype.cursor = {};
    }
    Object.defineProperty(Button.prototype, "onMouseDragged", {
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
    if(!Button){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf Button
         */
        P.Button.prototype.onMouseDragged = {};
    }
    Object.defineProperty(Button.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!Button){
        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf Button
         */
        P.Button.prototype.parent = {};
    }
    Object.defineProperty(Button.prototype, "onMouseReleased", {
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
    if(!Button){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf Button
         */
        P.Button.prototype.onMouseReleased = {};
    }
    Object.defineProperty(Button.prototype, "onFocusLost", {
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
    if(!Button){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf Button
         */
        P.Button.prototype.onFocusLost = {};
    }
    Object.defineProperty(Button.prototype, "icon", {
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
    if(!Button){
        /**
         * Image picture for the button.
         * @property icon
         * @memberOf Button
         */
        P.Button.prototype.icon = {};
    }
    Object.defineProperty(Button.prototype, "onMousePressed", {
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
    if(!Button){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf Button
         */
        P.Button.prototype.onMousePressed = {};
    }
    Object.defineProperty(Button.prototype, "foreground", {
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
    if(!Button){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf Button
         */
        P.Button.prototype.foreground = {};
    }
    Object.defineProperty(Button.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!Button){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf Button
         */
        P.Button.prototype.error = '';
    }
    Object.defineProperty(Button.prototype, "enabled", {
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
    if(!Button){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf Button
         */
        P.Button.prototype.enabled = true;
    }
    Object.defineProperty(Button.prototype, "onComponentMoved", {
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
    if(!Button){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf Button
         */
        P.Button.prototype.onComponentMoved = {};
    }
    Object.defineProperty(Button.prototype, "componentPopupMenu", {
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
    if(!Button){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf Button
         */
        P.Button.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(Button.prototype, "top", {
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
    if(!Button){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf Button
         */
        P.Button.prototype.top = 0;
    }
    Object.defineProperty(Button.prototype, "onComponentResized", {
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
    if(!Button){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf Button
         */
        P.Button.prototype.onComponentResized = {};
    }
    Object.defineProperty(Button.prototype, "text", {
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
    if(!Button){
        /**
         * Text on the button.
         * @property text
         * @memberOf Button
         */
        P.Button.prototype.text = '';
    }
    Object.defineProperty(Button.prototype, "onMouseEntered", {
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
    if(!Button){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf Button
         */
        P.Button.prototype.onMouseEntered = {};
    }
    Object.defineProperty(Button.prototype, "toolTipText", {
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
    if(!Button){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf Button
         */
        P.Button.prototype.toolTipText = '';
    }
    Object.defineProperty(Button.prototype, "height", {
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
    if(!Button){
        /**
         * Height of the component.
         * @property height
         * @memberOf Button
         */
        P.Button.prototype.height = 0;
    }
    Object.defineProperty(Button.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!Button){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf Button
         */
        P.Button.prototype.element = {};
    }
    Object.defineProperty(Button.prototype, "onComponentShown", {
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
    if(!Button){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf Button
         */
        P.Button.prototype.onComponentShown = {};
    }
    Object.defineProperty(Button.prototype, "onMouseMoved", {
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
    if(!Button){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf Button
         */
        P.Button.prototype.onMouseMoved = {};
    }
    Object.defineProperty(Button.prototype, "opaque", {
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
    if(!Button){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf Button
         */
        P.Button.prototype.opaque = true;
    }
    Object.defineProperty(Button.prototype, "visible", {
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
    if(!Button){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf Button
         */
        P.Button.prototype.visible = true;
    }
    Object.defineProperty(Button.prototype, "onComponentHidden", {
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
    if(!Button){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf Button
         */
        P.Button.prototype.onComponentHidden = {};
    }
    Object.defineProperty(Button.prototype, "iconTextGap", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.iconTextGap;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.iconTextGap = P.boxAsJava(aValue);
        }
    });
    if(!Button){
        /**
         * The amount of space between the text and the icon displayed in this button.
         * @property iconTextGap
         * @memberOf Button
         */
        P.Button.prototype.iconTextGap = 0;
    }
    Object.defineProperty(Button.prototype, "nextFocusableComponent", {
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
    if(!Button){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf Button
         */
        P.Button.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(Button.prototype, "onActionPerformed", {
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
    if(!Button){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf Button
         */
        P.Button.prototype.onActionPerformed = {};
    }
    Object.defineProperty(Button.prototype, "onKeyReleased", {
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
    if(!Button){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf Button
         */
        P.Button.prototype.onKeyReleased = {};
    }
    Object.defineProperty(Button.prototype, "focusable", {
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
    if(!Button){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf Button
         */
        P.Button.prototype.focusable = true;
    }
    Object.defineProperty(Button.prototype, "onKeyTyped", {
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
    if(!Button){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf Button
         */
        P.Button.prototype.onKeyTyped = {};
    }
    Object.defineProperty(Button.prototype, "horizontalTextPosition", {
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
    if(!Button){
        /**
         * Horizontal position of the text relative to the icon.
         * @property horizontalTextPosition
         * @memberOf Button
         */
        P.Button.prototype.horizontalTextPosition = 0;
    }
    Object.defineProperty(Button.prototype, "verticalTextPosition", {
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
    if(!Button){
        /**
         * Vertical position of the text relative to the icon.
         * @property verticalTextPosition
         * @memberOf Button
         */
        P.Button.prototype.verticalTextPosition = 0;
    }
    Object.defineProperty(Button.prototype, "onMouseWheelMoved", {
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
    if(!Button){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf Button
         */
        P.Button.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(Button.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!Button){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf Button
         */
        P.Button.prototype.component = {};
    }
    Object.defineProperty(Button.prototype, "onFocusGained", {
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
    if(!Button){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf Button
         */
        P.Button.prototype.onFocusGained = {};
    }
    Object.defineProperty(Button.prototype, "left", {
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
    if(!Button){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf Button
         */
        P.Button.prototype.left = 0;
    }
    Object.defineProperty(Button.prototype, "background", {
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
    if(!Button){
        /**
         * The background color of this component.
         * @property background
         * @memberOf Button
         */
        P.Button.prototype.background = {};
    }
    Object.defineProperty(Button.prototype, "onMouseClicked", {
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
    if(!Button){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf Button
         */
        P.Button.prototype.onMouseClicked = {};
    }
    Object.defineProperty(Button.prototype, "onMouseExited", {
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
    if(!Button){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf Button
         */
        P.Button.prototype.onMouseExited = {};
    }
    Object.defineProperty(Button.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!Button){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf Button
         */
        P.Button.prototype.name = '';
    }
    Object.defineProperty(Button.prototype, "width", {
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
    if(!Button){
        /**
         * Width of the component.
         * @property width
         * @memberOf Button
         */
        P.Button.prototype.width = 0;
    }
    Object.defineProperty(Button.prototype, "font", {
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
    if(!Button){
        /**
         * The font of this component.
         * @property font
         * @memberOf Button
         */
        P.Button.prototype.font = {};
    }
    Object.defineProperty(Button.prototype, "onKeyPressed", {
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
    if(!Button){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf Button
         */
        P.Button.prototype.onKeyPressed = {};
    }
    Object.defineProperty(Button.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!Button){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf Button
         */
        P.Button.prototype.focus = function(){};
    }
})();