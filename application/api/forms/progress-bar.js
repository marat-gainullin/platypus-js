(function() {
    var javaClass = Java.type("com.eas.client.forms.api.components.ProgressBar");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ProgressBar(null, null, aDelegate);
    });
    
    /**
     * Progress bar component.
     * @param min the minimum value (optional)
     * @param max the maximum value (optional)
     * @constructor ProgressBar ProgressBar
     */
    P.ProgressBar = function ProgressBar(min, max) {
        var maxArgs = 2;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 2 ? new javaClass(P.boxAsJava(min), P.boxAsJava(max))
            : arguments.length === 1 ? new javaClass(P.boxAsJava(min))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(ProgressBar.superclass)
            ProgressBar.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "ProgressBar", {value: ProgressBar});
    Object.defineProperty(ProgressBar.prototype, "cursor", {
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
    if(!ProgressBar){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.cursor = {};
    }
    Object.defineProperty(ProgressBar.prototype, "onMouseDragged", {
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
    if(!ProgressBar){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.onMouseDragged = {};
    }
    Object.defineProperty(ProgressBar.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!ProgressBar){
        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.parent = {};
    }
    Object.defineProperty(ProgressBar.prototype, "onMouseReleased", {
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
    if(!ProgressBar){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.onMouseReleased = {};
    }
    Object.defineProperty(ProgressBar.prototype, "onFocusLost", {
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
    if(!ProgressBar){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.onFocusLost = {};
    }
    Object.defineProperty(ProgressBar.prototype, "onMousePressed", {
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
    if(!ProgressBar){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.onMousePressed = {};
    }
    Object.defineProperty(ProgressBar.prototype, "foreground", {
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
    if(!ProgressBar){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.foreground = {};
    }
    Object.defineProperty(ProgressBar.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!ProgressBar){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.error = '';
    }
    Object.defineProperty(ProgressBar.prototype, "enabled", {
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
    if(!ProgressBar){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.enabled = true;
    }
    Object.defineProperty(ProgressBar.prototype, "onComponentMoved", {
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
    if(!ProgressBar){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.onComponentMoved = {};
    }
    Object.defineProperty(ProgressBar.prototype, "componentPopupMenu", {
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
    if(!ProgressBar){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(ProgressBar.prototype, "top", {
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
    if(!ProgressBar){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.top = 0;
    }
    Object.defineProperty(ProgressBar.prototype, "onComponentResized", {
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
    if(!ProgressBar){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.onComponentResized = {};
    }
    Object.defineProperty(ProgressBar.prototype, "text", {
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
    if(!ProgressBar){
        /**
         * String representation of the current progress.
         * @property text
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.text = '';
    }
    Object.defineProperty(ProgressBar.prototype, "onMouseEntered", {
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
    if(!ProgressBar){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.onMouseEntered = {};
    }
    Object.defineProperty(ProgressBar.prototype, "value", {
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
    if(!ProgressBar){
        /**
         * The current value of the progress bar.
         * @property value
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.value = 0;
    }
    Object.defineProperty(ProgressBar.prototype, "toolTipText", {
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
    if(!ProgressBar){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.toolTipText = '';
    }
    Object.defineProperty(ProgressBar.prototype, "height", {
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
    if(!ProgressBar){
        /**
         * Height of the component.
         * @property height
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.height = 0;
    }
    Object.defineProperty(ProgressBar.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!ProgressBar){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.element = {};
    }
    Object.defineProperty(ProgressBar.prototype, "onComponentShown", {
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
    if(!ProgressBar){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.onComponentShown = {};
    }
    Object.defineProperty(ProgressBar.prototype, "onMouseMoved", {
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
    if(!ProgressBar){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.onMouseMoved = {};
    }
    Object.defineProperty(ProgressBar.prototype, "opaque", {
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
    if(!ProgressBar){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.opaque = true;
    }
    Object.defineProperty(ProgressBar.prototype, "visible", {
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
    if(!ProgressBar){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.visible = true;
    }
    Object.defineProperty(ProgressBar.prototype, "onComponentHidden", {
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
    if(!ProgressBar){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.onComponentHidden = {};
    }
    Object.defineProperty(ProgressBar.prototype, "nextFocusableComponent", {
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
    if(!ProgressBar){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(ProgressBar.prototype, "onActionPerformed", {
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
    if(!ProgressBar){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.onActionPerformed = {};
    }
    Object.defineProperty(ProgressBar.prototype, "onKeyReleased", {
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
    if(!ProgressBar){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.onKeyReleased = {};
    }
    Object.defineProperty(ProgressBar.prototype, "focusable", {
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
    if(!ProgressBar){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.focusable = true;
    }
    Object.defineProperty(ProgressBar.prototype, "onKeyTyped", {
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
    if(!ProgressBar){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.onKeyTyped = {};
    }
    Object.defineProperty(ProgressBar.prototype, "onMouseWheelMoved", {
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
    if(!ProgressBar){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(ProgressBar.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!ProgressBar){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.component = {};
    }
    Object.defineProperty(ProgressBar.prototype, "onFocusGained", {
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
    if(!ProgressBar){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.onFocusGained = {};
    }
    Object.defineProperty(ProgressBar.prototype, "left", {
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
    if(!ProgressBar){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.left = 0;
    }
    Object.defineProperty(ProgressBar.prototype, "background", {
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
    if(!ProgressBar){
        /**
         * The background color of this component.
         * @property background
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.background = {};
    }
    Object.defineProperty(ProgressBar.prototype, "onMouseClicked", {
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
    if(!ProgressBar){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.onMouseClicked = {};
    }
    Object.defineProperty(ProgressBar.prototype, "onMouseExited", {
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
    if(!ProgressBar){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.onMouseExited = {};
    }
    Object.defineProperty(ProgressBar.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!ProgressBar){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.name = '';
    }
    Object.defineProperty(ProgressBar.prototype, "width", {
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
    if(!ProgressBar){
        /**
         * Width of the component.
         * @property width
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.width = 0;
    }
    Object.defineProperty(ProgressBar.prototype, "maximum", {
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
    if(!ProgressBar){
        /**
         * The progress bar's maximum value.
         * @property maximum
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.maximum = 0;
    }
    Object.defineProperty(ProgressBar.prototype, "minimum", {
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
    if(!ProgressBar){
        /**
         * The progress bar's minimum value.
         * @property minimum
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.minimum = 0;
    }
    Object.defineProperty(ProgressBar.prototype, "font", {
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
    if(!ProgressBar){
        /**
         * The font of this component.
         * @property font
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.font = {};
    }
    Object.defineProperty(ProgressBar.prototype, "onKeyPressed", {
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
    if(!ProgressBar){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.onKeyPressed = {};
    }
    Object.defineProperty(ProgressBar.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!ProgressBar){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.focus = function(){};
    }
})();