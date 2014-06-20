(function() {
    var javaClass = Java.type("com.eas.client.forms.api.components.DropDownButton");
    javaClass.setPublisher(function(aDelegate) {
        return new P.DropDownButton(null, null, null, null, aDelegate);
    });
    
    /**
     * Drop-down button component.
     * @param text the text of the component (optional).
     * @param icon the icon of the component (optional).
     * @param iconTextGap the text gap (optional).
     * @param actionPerformed the function for the action performed handler (optional).
     * @constructor DropDownButton DropDownButton
     */
    P.DropDownButton = function DropDownButton(text, icon, iconTextGap, actionPerformed) {
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
        if(DropDownButton.superclass)
            DropDownButton.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "DropDownButton", {value: DropDownButton});
    Object.defineProperty(DropDownButton.prototype, "cursor", {
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
    if(!DropDownButton){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.cursor = {};
    }
    Object.defineProperty(DropDownButton.prototype, "onMouseDragged", {
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
    if(!DropDownButton){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.onMouseDragged = {};
    }
    Object.defineProperty(DropDownButton.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!DropDownButton){
        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.parent = {};
    }
    Object.defineProperty(DropDownButton.prototype, "onMouseReleased", {
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
    if(!DropDownButton){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.onMouseReleased = {};
    }
    Object.defineProperty(DropDownButton.prototype, "onFocusLost", {
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
    if(!DropDownButton){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.onFocusLost = {};
    }
    Object.defineProperty(DropDownButton.prototype, "icon", {
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
    if(!DropDownButton){
        /**
         * Image picture for the button.
         * @property icon
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.icon = {};
    }
    Object.defineProperty(DropDownButton.prototype, "onMousePressed", {
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
    if(!DropDownButton){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.onMousePressed = {};
    }
    Object.defineProperty(DropDownButton.prototype, "foreground", {
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
    if(!DropDownButton){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.foreground = {};
    }
    Object.defineProperty(DropDownButton.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!DropDownButton){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.error = '';
    }
    Object.defineProperty(DropDownButton.prototype, "enabled", {
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
    if(!DropDownButton){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.enabled = true;
    }
    Object.defineProperty(DropDownButton.prototype, "onComponentMoved", {
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
    if(!DropDownButton){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.onComponentMoved = {};
    }
    Object.defineProperty(DropDownButton.prototype, "componentPopupMenu", {
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
    if(!DropDownButton){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(DropDownButton.prototype, "top", {
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
    if(!DropDownButton){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.top = 0;
    }
    Object.defineProperty(DropDownButton.prototype, "onComponentResized", {
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
    if(!DropDownButton){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.onComponentResized = {};
    }
    Object.defineProperty(DropDownButton.prototype, "text", {
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
    if(!DropDownButton){
        /**
         * Text on the button.
         * @property text
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.text = '';
    }
    Object.defineProperty(DropDownButton.prototype, "dropDownMenu", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.dropDownMenu;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.dropDownMenu = P.boxAsJava(aValue);
        }
    });
    if(!DropDownButton){
        /**
         * <code>PopupMenu</code> for the component.
         * @property dropDownMenu
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.dropDownMenu = {};
    }
    Object.defineProperty(DropDownButton.prototype, "onMouseEntered", {
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
    if(!DropDownButton){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.onMouseEntered = {};
    }
    Object.defineProperty(DropDownButton.prototype, "toolTipText", {
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
    if(!DropDownButton){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.toolTipText = '';
    }
    Object.defineProperty(DropDownButton.prototype, "height", {
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
    if(!DropDownButton){
        /**
         * Height of the component.
         * @property height
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.height = 0;
    }
    Object.defineProperty(DropDownButton.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!DropDownButton){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.element = {};
    }
    Object.defineProperty(DropDownButton.prototype, "onComponentShown", {
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
    if(!DropDownButton){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.onComponentShown = {};
    }
    Object.defineProperty(DropDownButton.prototype, "onMouseMoved", {
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
    if(!DropDownButton){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.onMouseMoved = {};
    }
    Object.defineProperty(DropDownButton.prototype, "opaque", {
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
    if(!DropDownButton){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.opaque = true;
    }
    Object.defineProperty(DropDownButton.prototype, "visible", {
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
    if(!DropDownButton){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.visible = true;
    }
    Object.defineProperty(DropDownButton.prototype, "onComponentHidden", {
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
    if(!DropDownButton){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.onComponentHidden = {};
    }
    Object.defineProperty(DropDownButton.prototype, "iconTextGap", {
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
    if(!DropDownButton){
        /**
         * The amount of space between the text and the icon displayed in this button.
         * @property iconTextGap
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.iconTextGap = 0;
    }
    Object.defineProperty(DropDownButton.prototype, "nextFocusableComponent", {
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
    if(!DropDownButton){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(DropDownButton.prototype, "onActionPerformed", {
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
    if(!DropDownButton){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.onActionPerformed = {};
    }
    Object.defineProperty(DropDownButton.prototype, "onKeyReleased", {
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
    if(!DropDownButton){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.onKeyReleased = {};
    }
    Object.defineProperty(DropDownButton.prototype, "focusable", {
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
    if(!DropDownButton){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.focusable = true;
    }
    Object.defineProperty(DropDownButton.prototype, "onKeyTyped", {
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
    if(!DropDownButton){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.onKeyTyped = {};
    }
    Object.defineProperty(DropDownButton.prototype, "horizontalTextPosition", {
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
    if(!DropDownButton){
        /**
         * Horizontal position of the text relative to the icon.
         * @property horizontalTextPosition
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.horizontalTextPosition = 0;
    }
    Object.defineProperty(DropDownButton.prototype, "verticalTextPosition", {
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
    if(!DropDownButton){
        /**
         * Vertical position of the text relative to the icon.
         * @property verticalTextPosition
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.verticalTextPosition = 0;
    }
    Object.defineProperty(DropDownButton.prototype, "onMouseWheelMoved", {
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
    if(!DropDownButton){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(DropDownButton.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!DropDownButton){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.component = {};
    }
    Object.defineProperty(DropDownButton.prototype, "onFocusGained", {
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
    if(!DropDownButton){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.onFocusGained = {};
    }
    Object.defineProperty(DropDownButton.prototype, "left", {
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
    if(!DropDownButton){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.left = 0;
    }
    Object.defineProperty(DropDownButton.prototype, "background", {
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
    if(!DropDownButton){
        /**
         * The background color of this component.
         * @property background
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.background = {};
    }
    Object.defineProperty(DropDownButton.prototype, "onMouseClicked", {
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
    if(!DropDownButton){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.onMouseClicked = {};
    }
    Object.defineProperty(DropDownButton.prototype, "onMouseExited", {
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
    if(!DropDownButton){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.onMouseExited = {};
    }
    Object.defineProperty(DropDownButton.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!DropDownButton){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.name = '';
    }
    Object.defineProperty(DropDownButton.prototype, "width", {
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
    if(!DropDownButton){
        /**
         * Width of the component.
         * @property width
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.width = 0;
    }
    Object.defineProperty(DropDownButton.prototype, "font", {
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
    if(!DropDownButton){
        /**
         * The font of this component.
         * @property font
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.font = {};
    }
    Object.defineProperty(DropDownButton.prototype, "onKeyPressed", {
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
    if(!DropDownButton){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.onKeyPressed = {};
    }
    Object.defineProperty(DropDownButton.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!DropDownButton){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.focus = function(){};
    }
})();