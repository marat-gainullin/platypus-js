(function() {
    var javaClass = Java.type("com.eas.client.forms.api.components.ToggleButton");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ToggleButton(null, null, null, null, aDelegate);
    });
    
    /**
     * Toggle button component.
     * @param text the text for the component (optional)
     * @param icon the icon for the component (optional)
     * @param selected the selected state of the button (optional)
     * @param iconTextGap the text gap (optional)
     * @param actionPerformed the function for the action performed handler (optional)
     * @constructor ToggleButton ToggleButton
     */
    P.ToggleButton = function ToggleButton(text, icon, iconTextGap, actionPerformed) {
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
        if(ToggleButton.superclass)
            ToggleButton.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "ToggleButton", {value: ToggleButton});
    Object.defineProperty(ToggleButton.prototype, "cursor", {
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
    if(!ToggleButton){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.cursor = {};
    }
    Object.defineProperty(ToggleButton.prototype, "onMouseDragged", {
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
    if(!ToggleButton){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.onMouseDragged = {};
    }
    Object.defineProperty(ToggleButton.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!ToggleButton){
        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.parent = {};
    }
    Object.defineProperty(ToggleButton.prototype, "onMouseReleased", {
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
    if(!ToggleButton){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.onMouseReleased = {};
    }
    Object.defineProperty(ToggleButton.prototype, "onFocusLost", {
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
    if(!ToggleButton){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.onFocusLost = {};
    }
    Object.defineProperty(ToggleButton.prototype, "icon", {
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
    if(!ToggleButton){
        /**
         * Image picture for the button.
         * @property icon
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.icon = {};
    }
    Object.defineProperty(ToggleButton.prototype, "onMousePressed", {
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
    if(!ToggleButton){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.onMousePressed = {};
    }
    Object.defineProperty(ToggleButton.prototype, "foreground", {
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
    if(!ToggleButton){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.foreground = {};
    }
    Object.defineProperty(ToggleButton.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!ToggleButton){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.error = '';
    }
    Object.defineProperty(ToggleButton.prototype, "enabled", {
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
    if(!ToggleButton){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.enabled = true;
    }
    Object.defineProperty(ToggleButton.prototype, "onComponentMoved", {
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
    if(!ToggleButton){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.onComponentMoved = {};
    }
    Object.defineProperty(ToggleButton.prototype, "componentPopupMenu", {
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
    if(!ToggleButton){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(ToggleButton.prototype, "top", {
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
    if(!ToggleButton){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.top = 0;
    }
    Object.defineProperty(ToggleButton.prototype, "onComponentResized", {
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
    if(!ToggleButton){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.onComponentResized = {};
    }
    Object.defineProperty(ToggleButton.prototype, "text", {
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
    if(!ToggleButton){
        /**
         * Text on the button.
         * @property text
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.text = '';
    }
    Object.defineProperty(ToggleButton.prototype, "onMouseEntered", {
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
    if(!ToggleButton){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.onMouseEntered = {};
    }
    Object.defineProperty(ToggleButton.prototype, "selected", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.selected;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.selected = P.boxAsJava(aValue);
        }
    });
    if(!ToggleButton){
        /**
         * The state of the button.
         * @property selected
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.selected = true;
    }
    Object.defineProperty(ToggleButton.prototype, "toolTipText", {
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
    if(!ToggleButton){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.toolTipText = '';
    }
    Object.defineProperty(ToggleButton.prototype, "height", {
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
    if(!ToggleButton){
        /**
         * Height of the component.
         * @property height
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.height = 0;
    }
    Object.defineProperty(ToggleButton.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!ToggleButton){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.element = {};
    }
    Object.defineProperty(ToggleButton.prototype, "onComponentShown", {
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
    if(!ToggleButton){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.onComponentShown = {};
    }
    Object.defineProperty(ToggleButton.prototype, "onMouseMoved", {
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
    if(!ToggleButton){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.onMouseMoved = {};
    }
    Object.defineProperty(ToggleButton.prototype, "buttonGroup", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.buttonGroup;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.buttonGroup = P.boxAsJava(aValue);
        }
    });
    if(!ToggleButton){
        /**
         * The ButtonGroup this component belongs to.
         * @property buttonGroup
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.buttonGroup = {};
    }
    Object.defineProperty(ToggleButton.prototype, "opaque", {
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
    if(!ToggleButton){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.opaque = true;
    }
    Object.defineProperty(ToggleButton.prototype, "visible", {
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
    if(!ToggleButton){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.visible = true;
    }
    Object.defineProperty(ToggleButton.prototype, "onComponentHidden", {
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
    if(!ToggleButton){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.onComponentHidden = {};
    }
    Object.defineProperty(ToggleButton.prototype, "iconTextGap", {
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
    if(!ToggleButton){
        /**
         * The amount of space between the text and the icon displayed in this button.
         * @property iconTextGap
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.iconTextGap = 0;
    }
    Object.defineProperty(ToggleButton.prototype, "nextFocusableComponent", {
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
    if(!ToggleButton){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(ToggleButton.prototype, "onActionPerformed", {
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
    if(!ToggleButton){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.onActionPerformed = {};
    }
    Object.defineProperty(ToggleButton.prototype, "onKeyReleased", {
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
    if(!ToggleButton){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.onKeyReleased = {};
    }
    Object.defineProperty(ToggleButton.prototype, "focusable", {
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
    if(!ToggleButton){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.focusable = true;
    }
    Object.defineProperty(ToggleButton.prototype, "onKeyTyped", {
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
    if(!ToggleButton){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.onKeyTyped = {};
    }
    Object.defineProperty(ToggleButton.prototype, "horizontalTextPosition", {
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
    if(!ToggleButton){
        /**
         * Horizontal position of the text relative to the icon.
         * @property horizontalTextPosition
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.horizontalTextPosition = 0;
    }
    Object.defineProperty(ToggleButton.prototype, "verticalTextPosition", {
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
    if(!ToggleButton){
        /**
         * Vertical position of the text relative to the icon.
         * @property verticalTextPosition
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.verticalTextPosition = 0;
    }
    Object.defineProperty(ToggleButton.prototype, "onMouseWheelMoved", {
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
    if(!ToggleButton){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(ToggleButton.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!ToggleButton){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.component = {};
    }
    Object.defineProperty(ToggleButton.prototype, "onFocusGained", {
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
    if(!ToggleButton){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.onFocusGained = {};
    }
    Object.defineProperty(ToggleButton.prototype, "left", {
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
    if(!ToggleButton){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.left = 0;
    }
    Object.defineProperty(ToggleButton.prototype, "background", {
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
    if(!ToggleButton){
        /**
         * The background color of this component.
         * @property background
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.background = {};
    }
    Object.defineProperty(ToggleButton.prototype, "onMouseClicked", {
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
    if(!ToggleButton){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.onMouseClicked = {};
    }
    Object.defineProperty(ToggleButton.prototype, "onMouseExited", {
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
    if(!ToggleButton){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.onMouseExited = {};
    }
    Object.defineProperty(ToggleButton.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!ToggleButton){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.name = '';
    }
    Object.defineProperty(ToggleButton.prototype, "width", {
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
    if(!ToggleButton){
        /**
         * Width of the component.
         * @property width
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.width = 0;
    }
    Object.defineProperty(ToggleButton.prototype, "font", {
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
    if(!ToggleButton){
        /**
         * The font of this component.
         * @property font
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.font = {};
    }
    Object.defineProperty(ToggleButton.prototype, "onKeyPressed", {
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
    if(!ToggleButton){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.onKeyPressed = {};
    }
    Object.defineProperty(ToggleButton.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!ToggleButton){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.focus = function(){};
    }
})();