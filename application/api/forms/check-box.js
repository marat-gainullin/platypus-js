(function() {
    var javaClass = Java.type("com.eas.client.forms.api.components.CheckBox");
    javaClass.setPublisher(function(aDelegate) {
        return new P.CheckBox(null, null, null, aDelegate);
    });
    
    /**
     * Check box component.
     * @param text the text of the check box (optional).
     * @param selected <code>true</code> if selected (optional).
     * @param actionPerformed the function for the action performed (optional).
     * @constructor CheckBox CheckBox
     */
    P.CheckBox = function CheckBox(text, selected, actionPerformed) {
        var maxArgs = 3;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 3 ? new javaClass(P.boxAsJava(text), P.boxAsJava(selected), P.boxAsJava(actionPerformed))
            : arguments.length === 2 ? new javaClass(P.boxAsJava(text), P.boxAsJava(selected))
            : arguments.length === 1 ? new javaClass(P.boxAsJava(text))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(CheckBox.superclass)
            CheckBox.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "CheckBox", {value: CheckBox});
    Object.defineProperty(CheckBox.prototype, "cursor", {
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
    if(!CheckBox){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf CheckBox
         */
        P.CheckBox.prototype.cursor = {};
    }
    Object.defineProperty(CheckBox.prototype, "onMouseDragged", {
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
    if(!CheckBox){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf CheckBox
         */
        P.CheckBox.prototype.onMouseDragged = {};
    }
    Object.defineProperty(CheckBox.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!CheckBox){
        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf CheckBox
         */
        P.CheckBox.prototype.parent = {};
    }
    Object.defineProperty(CheckBox.prototype, "onMouseReleased", {
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
    if(!CheckBox){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf CheckBox
         */
        P.CheckBox.prototype.onMouseReleased = {};
    }
    Object.defineProperty(CheckBox.prototype, "onFocusLost", {
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
    if(!CheckBox){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf CheckBox
         */
        P.CheckBox.prototype.onFocusLost = {};
    }
    Object.defineProperty(CheckBox.prototype, "onMousePressed", {
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
    if(!CheckBox){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf CheckBox
         */
        P.CheckBox.prototype.onMousePressed = {};
    }
    Object.defineProperty(CheckBox.prototype, "foreground", {
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
    if(!CheckBox){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf CheckBox
         */
        P.CheckBox.prototype.foreground = {};
    }
    Object.defineProperty(CheckBox.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!CheckBox){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf CheckBox
         */
        P.CheckBox.prototype.error = '';
    }
    Object.defineProperty(CheckBox.prototype, "enabled", {
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
    if(!CheckBox){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf CheckBox
         */
        P.CheckBox.prototype.enabled = true;
    }
    Object.defineProperty(CheckBox.prototype, "onComponentMoved", {
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
    if(!CheckBox){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf CheckBox
         */
        P.CheckBox.prototype.onComponentMoved = {};
    }
    Object.defineProperty(CheckBox.prototype, "componentPopupMenu", {
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
    if(!CheckBox){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf CheckBox
         */
        P.CheckBox.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(CheckBox.prototype, "top", {
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
    if(!CheckBox){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf CheckBox
         */
        P.CheckBox.prototype.top = 0;
    }
    Object.defineProperty(CheckBox.prototype, "onComponentResized", {
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
    if(!CheckBox){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf CheckBox
         */
        P.CheckBox.prototype.onComponentResized = {};
    }
    Object.defineProperty(CheckBox.prototype, "text", {
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
    if(!CheckBox){
        /**
         * @property text
         * @memberOf CheckBox
         * Text of the check box.*/
        P.CheckBox.prototype.text = '';
    }
    Object.defineProperty(CheckBox.prototype, "onMouseEntered", {
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
    if(!CheckBox){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf CheckBox
         */
        P.CheckBox.prototype.onMouseEntered = {};
    }
    Object.defineProperty(CheckBox.prototype, "selected", {
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
    if(!CheckBox){
        /**
         * @property selected
         * @memberOf CheckBox
         * Determines whether this component is selected.*/
        P.CheckBox.prototype.selected = true;
    }
    Object.defineProperty(CheckBox.prototype, "toolTipText", {
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
    if(!CheckBox){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf CheckBox
         */
        P.CheckBox.prototype.toolTipText = '';
    }
    Object.defineProperty(CheckBox.prototype, "height", {
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
    if(!CheckBox){
        /**
         * Height of the component.
         * @property height
         * @memberOf CheckBox
         */
        P.CheckBox.prototype.height = 0;
    }
    Object.defineProperty(CheckBox.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!CheckBox){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf CheckBox
         */
        P.CheckBox.prototype.element = {};
    }
    Object.defineProperty(CheckBox.prototype, "onComponentShown", {
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
    if(!CheckBox){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf CheckBox
         */
        P.CheckBox.prototype.onComponentShown = {};
    }
    Object.defineProperty(CheckBox.prototype, "onMouseMoved", {
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
    if(!CheckBox){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf CheckBox
         */
        P.CheckBox.prototype.onMouseMoved = {};
    }
    Object.defineProperty(CheckBox.prototype, "opaque", {
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
    if(!CheckBox){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf CheckBox
         */
        P.CheckBox.prototype.opaque = true;
    }
    Object.defineProperty(CheckBox.prototype, "visible", {
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
    if(!CheckBox){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf CheckBox
         */
        P.CheckBox.prototype.visible = true;
    }
    Object.defineProperty(CheckBox.prototype, "onComponentHidden", {
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
    if(!CheckBox){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf CheckBox
         */
        P.CheckBox.prototype.onComponentHidden = {};
    }
    Object.defineProperty(CheckBox.prototype, "nextFocusableComponent", {
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
    if(!CheckBox){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf CheckBox
         */
        P.CheckBox.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(CheckBox.prototype, "onActionPerformed", {
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
    if(!CheckBox){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf CheckBox
         */
        P.CheckBox.prototype.onActionPerformed = {};
    }
    Object.defineProperty(CheckBox.prototype, "onKeyReleased", {
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
    if(!CheckBox){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf CheckBox
         */
        P.CheckBox.prototype.onKeyReleased = {};
    }
    Object.defineProperty(CheckBox.prototype, "focusable", {
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
    if(!CheckBox){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf CheckBox
         */
        P.CheckBox.prototype.focusable = true;
    }
    Object.defineProperty(CheckBox.prototype, "onKeyTyped", {
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
    if(!CheckBox){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf CheckBox
         */
        P.CheckBox.prototype.onKeyTyped = {};
    }
    Object.defineProperty(CheckBox.prototype, "onMouseWheelMoved", {
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
    if(!CheckBox){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf CheckBox
         */
        P.CheckBox.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(CheckBox.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!CheckBox){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf CheckBox
         */
        P.CheckBox.prototype.component = {};
    }
    Object.defineProperty(CheckBox.prototype, "onFocusGained", {
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
    if(!CheckBox){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf CheckBox
         */
        P.CheckBox.prototype.onFocusGained = {};
    }
    Object.defineProperty(CheckBox.prototype, "left", {
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
    if(!CheckBox){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf CheckBox
         */
        P.CheckBox.prototype.left = 0;
    }
    Object.defineProperty(CheckBox.prototype, "background", {
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
    if(!CheckBox){
        /**
         * The background color of this component.
         * @property background
         * @memberOf CheckBox
         */
        P.CheckBox.prototype.background = {};
    }
    Object.defineProperty(CheckBox.prototype, "onMouseClicked", {
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
    if(!CheckBox){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf CheckBox
         */
        P.CheckBox.prototype.onMouseClicked = {};
    }
    Object.defineProperty(CheckBox.prototype, "onMouseExited", {
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
    if(!CheckBox){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf CheckBox
         */
        P.CheckBox.prototype.onMouseExited = {};
    }
    Object.defineProperty(CheckBox.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!CheckBox){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf CheckBox
         */
        P.CheckBox.prototype.name = '';
    }
    Object.defineProperty(CheckBox.prototype, "width", {
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
    if(!CheckBox){
        /**
         * Width of the component.
         * @property width
         * @memberOf CheckBox
         */
        P.CheckBox.prototype.width = 0;
    }
    Object.defineProperty(CheckBox.prototype, "font", {
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
    if(!CheckBox){
        /**
         * The font of this component.
         * @property font
         * @memberOf CheckBox
         */
        P.CheckBox.prototype.font = {};
    }
    Object.defineProperty(CheckBox.prototype, "onKeyPressed", {
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
    if(!CheckBox){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf CheckBox
         */
        P.CheckBox.prototype.onKeyPressed = {};
    }
    Object.defineProperty(CheckBox.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!CheckBox){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf CheckBox
         */
        P.CheckBox.prototype.focus = function(){};
    }
})();