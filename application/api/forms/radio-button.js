(function() {
    var javaClass = Java.type("com.eas.client.forms.api.components.RadioButton");
    javaClass.setPublisher(function(aDelegate) {
        return new P.RadioButton(null, null, null, aDelegate);
    });
    
    /**
     * Radio button component.
     * @param text Component's text (optional)
     * @param selected <code>true</code> if component is selected (optional)
     * @param actionPerformed On action performed function (optional)
     * @constructor RadioButton RadioButton
     */
    P.RadioButton = function RadioButton(text, selected, actionPerformed) {
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
        if(RadioButton.superclass)
            RadioButton.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "RadioButton", {value: RadioButton});
    Object.defineProperty(RadioButton.prototype, "cursor", {
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
    if(!RadioButton){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.cursor = {};
    }
    Object.defineProperty(RadioButton.prototype, "onMouseDragged", {
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
    if(!RadioButton){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.onMouseDragged = {};
    }
    Object.defineProperty(RadioButton.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!RadioButton){
        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.parent = {};
    }
    Object.defineProperty(RadioButton.prototype, "onMouseReleased", {
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
    if(!RadioButton){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.onMouseReleased = {};
    }
    Object.defineProperty(RadioButton.prototype, "onFocusLost", {
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
    if(!RadioButton){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.onFocusLost = {};
    }
    Object.defineProperty(RadioButton.prototype, "icon", {
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
    if(!RadioButton){
        /**
         * The default icon.
         * @property icon
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.icon = {};
    }
    Object.defineProperty(RadioButton.prototype, "onMousePressed", {
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
    if(!RadioButton){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.onMousePressed = {};
    }
    Object.defineProperty(RadioButton.prototype, "foreground", {
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
    if(!RadioButton){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.foreground = {};
    }
    Object.defineProperty(RadioButton.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!RadioButton){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.error = '';
    }
    Object.defineProperty(RadioButton.prototype, "enabled", {
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
    if(!RadioButton){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.enabled = true;
    }
    Object.defineProperty(RadioButton.prototype, "onComponentMoved", {
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
    if(!RadioButton){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.onComponentMoved = {};
    }
    Object.defineProperty(RadioButton.prototype, "componentPopupMenu", {
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
    if(!RadioButton){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(RadioButton.prototype, "top", {
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
    if(!RadioButton){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.top = 0;
    }
    Object.defineProperty(RadioButton.prototype, "onComponentResized", {
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
    if(!RadioButton){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.onComponentResized = {};
    }
    Object.defineProperty(RadioButton.prototype, "text", {
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
    if(!RadioButton){
        /**
         * The button's text.
         * @property text
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.text = '';
    }
    Object.defineProperty(RadioButton.prototype, "onMouseEntered", {
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
    if(!RadioButton){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.onMouseEntered = {};
    }
    Object.defineProperty(RadioButton.prototype, "selected", {
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
    if(!RadioButton){
        /**
         * The state of the button.
         * @property selected
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.selected = true;
    }
    Object.defineProperty(RadioButton.prototype, "toolTipText", {
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
    if(!RadioButton){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.toolTipText = '';
    }
    Object.defineProperty(RadioButton.prototype, "height", {
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
    if(!RadioButton){
        /**
         * Height of the component.
         * @property height
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.height = 0;
    }
    Object.defineProperty(RadioButton.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!RadioButton){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.element = {};
    }
    Object.defineProperty(RadioButton.prototype, "onComponentShown", {
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
    if(!RadioButton){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.onComponentShown = {};
    }
    Object.defineProperty(RadioButton.prototype, "onMouseMoved", {
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
    if(!RadioButton){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.onMouseMoved = {};
    }
    Object.defineProperty(RadioButton.prototype, "buttonGroup", {
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
    if(!RadioButton){
        /**
         * The ButtonGroup this component belongs to.
         * @property buttonGroup
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.buttonGroup = {};
    }
    Object.defineProperty(RadioButton.prototype, "opaque", {
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
    if(!RadioButton){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.opaque = true;
    }
    Object.defineProperty(RadioButton.prototype, "visible", {
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
    if(!RadioButton){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.visible = true;
    }
    Object.defineProperty(RadioButton.prototype, "onComponentHidden", {
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
    if(!RadioButton){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.onComponentHidden = {};
    }
    Object.defineProperty(RadioButton.prototype, "nextFocusableComponent", {
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
    if(!RadioButton){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(RadioButton.prototype, "onActionPerformed", {
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
    if(!RadioButton){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.onActionPerformed = {};
    }
    Object.defineProperty(RadioButton.prototype, "onKeyReleased", {
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
    if(!RadioButton){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.onKeyReleased = {};
    }
    Object.defineProperty(RadioButton.prototype, "focusable", {
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
    if(!RadioButton){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.focusable = true;
    }
    Object.defineProperty(RadioButton.prototype, "onKeyTyped", {
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
    if(!RadioButton){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.onKeyTyped = {};
    }
    Object.defineProperty(RadioButton.prototype, "onMouseWheelMoved", {
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
    if(!RadioButton){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(RadioButton.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!RadioButton){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.component = {};
    }
    Object.defineProperty(RadioButton.prototype, "onFocusGained", {
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
    if(!RadioButton){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.onFocusGained = {};
    }
    Object.defineProperty(RadioButton.prototype, "left", {
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
    if(!RadioButton){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.left = 0;
    }
    Object.defineProperty(RadioButton.prototype, "background", {
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
    if(!RadioButton){
        /**
         * The background color of this component.
         * @property background
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.background = {};
    }
    Object.defineProperty(RadioButton.prototype, "onMouseClicked", {
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
    if(!RadioButton){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.onMouseClicked = {};
    }
    Object.defineProperty(RadioButton.prototype, "onMouseExited", {
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
    if(!RadioButton){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.onMouseExited = {};
    }
    Object.defineProperty(RadioButton.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!RadioButton){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.name = '';
    }
    Object.defineProperty(RadioButton.prototype, "width", {
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
    if(!RadioButton){
        /**
         * Width of the component.
         * @property width
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.width = 0;
    }
    Object.defineProperty(RadioButton.prototype, "font", {
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
    if(!RadioButton){
        /**
         * The font of this component.
         * @property font
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.font = {};
    }
    Object.defineProperty(RadioButton.prototype, "onKeyPressed", {
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
    if(!RadioButton){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.onKeyPressed = {};
    }
    Object.defineProperty(RadioButton.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!RadioButton){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.focus = function(){};
    }
})();