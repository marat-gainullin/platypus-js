(function() {
    var javaClass = Java.type("com.eas.client.forms.api.components.Label");
    javaClass.setPublisher(function(aDelegate) {
        return new P.Label(null, null, null, aDelegate);
    });
    
    /**
     * Label component.
     * @param text the initial text for the component (optional)
     * @param icon the icon for the component (optional)
     * @param iconTextGap the text gap (optional)
     * @constructor Label Label
     */
    P.Label = function Label(text, icon, iconTextGap) {
        var maxArgs = 3;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 3 ? new javaClass(P.boxAsJava(text), P.boxAsJava(icon), P.boxAsJava(iconTextGap))
            : arguments.length === 2 ? new javaClass(P.boxAsJava(text), P.boxAsJava(icon))
            : arguments.length === 1 ? new javaClass(P.boxAsJava(text))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(Label.superclass)
            Label.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "Label", {value: Label});
    Object.defineProperty(Label.prototype, "cursor", {
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
    if(!Label){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf Label
         */
        P.Label.prototype.cursor = {};
    }
    Object.defineProperty(Label.prototype, "onMouseDragged", {
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
    if(!Label){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf Label
         */
        P.Label.prototype.onMouseDragged = {};
    }
    Object.defineProperty(Label.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!Label){
        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf Label
         */
        P.Label.prototype.parent = {};
    }
    Object.defineProperty(Label.prototype, "onMouseReleased", {
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
    if(!Label){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf Label
         */
        P.Label.prototype.onMouseReleased = {};
    }
    Object.defineProperty(Label.prototype, "onFocusLost", {
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
    if(!Label){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf Label
         */
        P.Label.prototype.onFocusLost = {};
    }
    Object.defineProperty(Label.prototype, "icon", {
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
    if(!Label){
        /**
         * The graphic image (glyph, icon) that the label displays.
         * @property icon
         * @memberOf Label
         */
        P.Label.prototype.icon = {};
    }
    Object.defineProperty(Label.prototype, "onMousePressed", {
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
    if(!Label){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf Label
         */
        P.Label.prototype.onMousePressed = {};
    }
    Object.defineProperty(Label.prototype, "foreground", {
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
    if(!Label){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf Label
         */
        P.Label.prototype.foreground = {};
    }
    Object.defineProperty(Label.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!Label){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf Label
         */
        P.Label.prototype.error = '';
    }
    Object.defineProperty(Label.prototype, "enabled", {
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
    if(!Label){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf Label
         */
        P.Label.prototype.enabled = true;
    }
    Object.defineProperty(Label.prototype, "onComponentMoved", {
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
    if(!Label){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf Label
         */
        P.Label.prototype.onComponentMoved = {};
    }
    Object.defineProperty(Label.prototype, "componentPopupMenu", {
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
    if(!Label){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf Label
         */
        P.Label.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(Label.prototype, "top", {
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
    if(!Label){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf Label
         */
        P.Label.prototype.top = 0;
    }
    Object.defineProperty(Label.prototype, "onComponentResized", {
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
    if(!Label){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf Label
         */
        P.Label.prototype.onComponentResized = {};
    }
    Object.defineProperty(Label.prototype, "text", {
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
    if(!Label){
        /**
         * The text string that the label displays.
         * @property text
         * @memberOf Label
         */
        P.Label.prototype.text = '';
    }
    Object.defineProperty(Label.prototype, "onMouseEntered", {
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
    if(!Label){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf Label
         */
        P.Label.prototype.onMouseEntered = {};
    }
    Object.defineProperty(Label.prototype, "toolTipText", {
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
    if(!Label){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf Label
         */
        P.Label.prototype.toolTipText = '';
    }
    Object.defineProperty(Label.prototype, "height", {
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
    if(!Label){
        /**
         * Height of the component.
         * @property height
         * @memberOf Label
         */
        P.Label.prototype.height = 0;
    }
    Object.defineProperty(Label.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!Label){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf Label
         */
        P.Label.prototype.element = {};
    }
    Object.defineProperty(Label.prototype, "onComponentShown", {
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
    if(!Label){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf Label
         */
        P.Label.prototype.onComponentShown = {};
    }
    Object.defineProperty(Label.prototype, "onMouseMoved", {
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
    if(!Label){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf Label
         */
        P.Label.prototype.onMouseMoved = {};
    }
    Object.defineProperty(Label.prototype, "opaque", {
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
    if(!Label){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf Label
         */
        P.Label.prototype.opaque = true;
    }
    Object.defineProperty(Label.prototype, "visible", {
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
    if(!Label){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf Label
         */
        P.Label.prototype.visible = true;
    }
    Object.defineProperty(Label.prototype, "onComponentHidden", {
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
    if(!Label){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf Label
         */
        P.Label.prototype.onComponentHidden = {};
    }
    Object.defineProperty(Label.prototype, "iconTextGap", {
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
    if(!Label){
        /**
         * The amount of space between the text and the icon displayed in this label.
         * @property iconTextGap
         * @memberOf Label
         */
        P.Label.prototype.iconTextGap = 0;
    }
    Object.defineProperty(Label.prototype, "nextFocusableComponent", {
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
    if(!Label){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf Label
         */
        P.Label.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(Label.prototype, "onActionPerformed", {
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
    if(!Label){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf Label
         */
        P.Label.prototype.onActionPerformed = {};
    }
    Object.defineProperty(Label.prototype, "onKeyReleased", {
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
    if(!Label){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf Label
         */
        P.Label.prototype.onKeyReleased = {};
    }
    Object.defineProperty(Label.prototype, "focusable", {
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
    if(!Label){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf Label
         */
        P.Label.prototype.focusable = true;
    }
    Object.defineProperty(Label.prototype, "onKeyTyped", {
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
    if(!Label){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf Label
         */
        P.Label.prototype.onKeyTyped = {};
    }
    Object.defineProperty(Label.prototype, "horizontalTextPosition", {
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
    if(!Label){
        /**
         * Horizontal position of the text relative to the icon.
         * @property horizontalTextPosition
         * @memberOf Label
         */
        P.Label.prototype.horizontalTextPosition = 0;
    }
    Object.defineProperty(Label.prototype, "verticalTextPosition", {
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
    if(!Label){
        /**
         * Vertical position of the text relative to the icon.
         * @property verticalTextPosition
         * @memberOf Label
         */
        P.Label.prototype.verticalTextPosition = 0;
    }
    Object.defineProperty(Label.prototype, "onMouseWheelMoved", {
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
    if(!Label){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf Label
         */
        P.Label.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(Label.prototype, "horizontalAlignment", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.horizontalAlignment;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.horizontalAlignment = P.boxAsJava(aValue);
        }
    });
    if(!Label){
        /**
         * Horizontal position of the text with the icon relative to the component's size.
         * @property horizontalAlignment
         * @memberOf Label
         */
        P.Label.prototype.horizontalAlignment = 0;
    }
    Object.defineProperty(Label.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!Label){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf Label
         */
        P.Label.prototype.component = {};
    }
    Object.defineProperty(Label.prototype, "onFocusGained", {
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
    if(!Label){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf Label
         */
        P.Label.prototype.onFocusGained = {};
    }
    Object.defineProperty(Label.prototype, "left", {
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
    if(!Label){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf Label
         */
        P.Label.prototype.left = 0;
    }
    Object.defineProperty(Label.prototype, "background", {
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
    if(!Label){
        /**
         * The background color of this component.
         * @property background
         * @memberOf Label
         */
        P.Label.prototype.background = {};
    }
    Object.defineProperty(Label.prototype, "onMouseClicked", {
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
    if(!Label){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf Label
         */
        P.Label.prototype.onMouseClicked = {};
    }
    Object.defineProperty(Label.prototype, "onMouseExited", {
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
    if(!Label){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf Label
         */
        P.Label.prototype.onMouseExited = {};
    }
    Object.defineProperty(Label.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!Label){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf Label
         */
        P.Label.prototype.name = '';
    }
    Object.defineProperty(Label.prototype, "width", {
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
    if(!Label){
        /**
         * Width of the component.
         * @property width
         * @memberOf Label
         */
        P.Label.prototype.width = 0;
    }
    Object.defineProperty(Label.prototype, "verticalAlignment", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.verticalAlignment;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.verticalAlignment = P.boxAsJava(aValue);
        }
    });
    if(!Label){
        /**
         * Vertical position of the text with the icon relative to the component's size.
         * @property verticalAlignment
         * @memberOf Label
         */
        P.Label.prototype.verticalAlignment = 0;
    }
    Object.defineProperty(Label.prototype, "font", {
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
    if(!Label){
        /**
         * The font of this component.
         * @property font
         * @memberOf Label
         */
        P.Label.prototype.font = {};
    }
    Object.defineProperty(Label.prototype, "onKeyPressed", {
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
    if(!Label){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf Label
         */
        P.Label.prototype.onKeyPressed = {};
    }
    Object.defineProperty(Label.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!Label){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf Label
         */
        P.Label.prototype.focus = function(){};
    }
})();