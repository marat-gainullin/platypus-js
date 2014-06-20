(function() {
    var javaClass = Java.type("com.eas.client.forms.api.components.Slider");
    javaClass.setPublisher(function(aDelegate) {
        return new P.Slider(null, null, null, aDelegate);
    });
    
    /**
     * Slider component.
     * @param min the minimum value (optional)
     * @param max the maximum value (optional)
     * @param value the initial value (optional)
     * @constructor Slider Slider
     */
    P.Slider = function Slider(min, max, value) {
        var maxArgs = 3;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 3 ? new javaClass(P.boxAsJava(min), P.boxAsJava(max), P.boxAsJava(value))
            : arguments.length === 2 ? new javaClass(P.boxAsJava(min), P.boxAsJava(max))
            : arguments.length === 1 ? new javaClass(P.boxAsJava(min))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(Slider.superclass)
            Slider.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "Slider", {value: Slider});
    Object.defineProperty(Slider.prototype, "cursor", {
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
    if(!Slider){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf Slider
         */
        P.Slider.prototype.cursor = {};
    }
    Object.defineProperty(Slider.prototype, "onMouseDragged", {
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
    if(!Slider){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf Slider
         */
        P.Slider.prototype.onMouseDragged = {};
    }
    Object.defineProperty(Slider.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!Slider){
        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf Slider
         */
        P.Slider.prototype.parent = {};
    }
    Object.defineProperty(Slider.prototype, "onMouseReleased", {
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
    if(!Slider){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf Slider
         */
        P.Slider.prototype.onMouseReleased = {};
    }
    Object.defineProperty(Slider.prototype, "onFocusLost", {
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
    if(!Slider){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf Slider
         */
        P.Slider.prototype.onFocusLost = {};
    }
    Object.defineProperty(Slider.prototype, "onMousePressed", {
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
    if(!Slider){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf Slider
         */
        P.Slider.prototype.onMousePressed = {};
    }
    Object.defineProperty(Slider.prototype, "foreground", {
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
    if(!Slider){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf Slider
         */
        P.Slider.prototype.foreground = {};
    }
    Object.defineProperty(Slider.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!Slider){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf Slider
         */
        P.Slider.prototype.error = '';
    }
    Object.defineProperty(Slider.prototype, "enabled", {
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
    if(!Slider){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf Slider
         */
        P.Slider.prototype.enabled = true;
    }
    Object.defineProperty(Slider.prototype, "onComponentMoved", {
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
    if(!Slider){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf Slider
         */
        P.Slider.prototype.onComponentMoved = {};
    }
    Object.defineProperty(Slider.prototype, "componentPopupMenu", {
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
    if(!Slider){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf Slider
         */
        P.Slider.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(Slider.prototype, "top", {
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
    if(!Slider){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf Slider
         */
        P.Slider.prototype.top = 0;
    }
    Object.defineProperty(Slider.prototype, "onComponentResized", {
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
    if(!Slider){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf Slider
         */
        P.Slider.prototype.onComponentResized = {};
    }
    Object.defineProperty(Slider.prototype, "text", {
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
    if(!Slider){
        /**
         * Generated property jsDoc.
         * @property text
         * @memberOf Slider
         */
        P.Slider.prototype.text = '';
    }
    Object.defineProperty(Slider.prototype, "onMouseEntered", {
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
    if(!Slider){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf Slider
         */
        P.Slider.prototype.onMouseEntered = {};
    }
    Object.defineProperty(Slider.prototype, "value", {
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
    if(!Slider){
        /**
         * The slider's current value.
         * @property value
         * @memberOf Slider
         */
        P.Slider.prototype.value = 0;
    }
    Object.defineProperty(Slider.prototype, "toolTipText", {
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
    if(!Slider){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf Slider
         */
        P.Slider.prototype.toolTipText = '';
    }
    Object.defineProperty(Slider.prototype, "height", {
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
    if(!Slider){
        /**
         * Height of the component.
         * @property height
         * @memberOf Slider
         */
        P.Slider.prototype.height = 0;
    }
    Object.defineProperty(Slider.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!Slider){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf Slider
         */
        P.Slider.prototype.element = {};
    }
    Object.defineProperty(Slider.prototype, "onComponentShown", {
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
    if(!Slider){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf Slider
         */
        P.Slider.prototype.onComponentShown = {};
    }
    Object.defineProperty(Slider.prototype, "orientation", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.orientation;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.orientation = P.boxAsJava(aValue);
        }
    });
    if(!Slider){
        /**
         * This slider's vertical or horizontal orientation: Orientation.VERTICAL or Orientation.HORIZONTAL
         * @property orientation
         * @memberOf Slider
         */
        P.Slider.prototype.orientation = 0;
    }
    Object.defineProperty(Slider.prototype, "onMouseMoved", {
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
    if(!Slider){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf Slider
         */
        P.Slider.prototype.onMouseMoved = {};
    }
    Object.defineProperty(Slider.prototype, "opaque", {
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
    if(!Slider){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf Slider
         */
        P.Slider.prototype.opaque = true;
    }
    Object.defineProperty(Slider.prototype, "visible", {
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
    if(!Slider){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf Slider
         */
        P.Slider.prototype.visible = true;
    }
    Object.defineProperty(Slider.prototype, "onComponentHidden", {
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
    if(!Slider){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf Slider
         */
        P.Slider.prototype.onComponentHidden = {};
    }
    Object.defineProperty(Slider.prototype, "nextFocusableComponent", {
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
    if(!Slider){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf Slider
         */
        P.Slider.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(Slider.prototype, "onActionPerformed", {
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
    if(!Slider){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf Slider
         */
        P.Slider.prototype.onActionPerformed = {};
    }
    Object.defineProperty(Slider.prototype, "onKeyReleased", {
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
    if(!Slider){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf Slider
         */
        P.Slider.prototype.onKeyReleased = {};
    }
    Object.defineProperty(Slider.prototype, "focusable", {
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
    if(!Slider){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf Slider
         */
        P.Slider.prototype.focusable = true;
    }
    Object.defineProperty(Slider.prototype, "onKeyTyped", {
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
    if(!Slider){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf Slider
         */
        P.Slider.prototype.onKeyTyped = {};
    }
    Object.defineProperty(Slider.prototype, "onMouseWheelMoved", {
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
    if(!Slider){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf Slider
         */
        P.Slider.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(Slider.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!Slider){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf Slider
         */
        P.Slider.prototype.component = {};
    }
    Object.defineProperty(Slider.prototype, "onFocusGained", {
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
    if(!Slider){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf Slider
         */
        P.Slider.prototype.onFocusGained = {};
    }
    Object.defineProperty(Slider.prototype, "left", {
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
    if(!Slider){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf Slider
         */
        P.Slider.prototype.left = 0;
    }
    Object.defineProperty(Slider.prototype, "background", {
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
    if(!Slider){
        /**
         * The background color of this component.
         * @property background
         * @memberOf Slider
         */
        P.Slider.prototype.background = {};
    }
    Object.defineProperty(Slider.prototype, "onMouseClicked", {
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
    if(!Slider){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf Slider
         */
        P.Slider.prototype.onMouseClicked = {};
    }
    Object.defineProperty(Slider.prototype, "onMouseExited", {
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
    if(!Slider){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf Slider
         */
        P.Slider.prototype.onMouseExited = {};
    }
    Object.defineProperty(Slider.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!Slider){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf Slider
         */
        P.Slider.prototype.name = '';
    }
    Object.defineProperty(Slider.prototype, "width", {
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
    if(!Slider){
        /**
         * Width of the component.
         * @property width
         * @memberOf Slider
         */
        P.Slider.prototype.width = 0;
    }
    Object.defineProperty(Slider.prototype, "maximum", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.maximum;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.maximum = P.boxAsJava(aValue);
        }
    });
    if(!Slider){
        /**
         * The maximum value supported by the slider.
         * @property maximum
         * @memberOf Slider
         */
        P.Slider.prototype.maximum = 0;
    }
    Object.defineProperty(Slider.prototype, "minimum", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.minimum;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.minimum = P.boxAsJava(aValue);
        }
    });
    if(!Slider){
        /**
         * The minimum value supported by the slider.
         * @property minimum
         * @memberOf Slider
         */
        P.Slider.prototype.minimum = 0;
    }
    Object.defineProperty(Slider.prototype, "font", {
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
    if(!Slider){
        /**
         * The font of this component.
         * @property font
         * @memberOf Slider
         */
        P.Slider.prototype.font = {};
    }
    Object.defineProperty(Slider.prototype, "onKeyPressed", {
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
    if(!Slider){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf Slider
         */
        P.Slider.prototype.onKeyPressed = {};
    }
    Object.defineProperty(Slider.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!Slider){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf Slider
         */
        P.Slider.prototype.focus = function(){};
    }
})();