(function() {
    var javaClass = Java.type("com.eas.client.chart.LineChart");
    javaClass.setPublisher(function(aDelegate) {
        return new P.LineChart(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor LineChart LineChart
     */
    P.LineChart = function LineChart() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(LineChart.superclass)
            LineChart.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "LineChart", {value: LineChart});
    Object.defineProperty(LineChart.prototype, "cursor", {
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
    if(!LineChart){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf LineChart
         */
        P.LineChart.prototype.cursor = {};
    }
    Object.defineProperty(LineChart.prototype, "onMouseDragged", {
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
    if(!LineChart){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf LineChart
         */
        P.LineChart.prototype.onMouseDragged = {};
    }
    Object.defineProperty(LineChart.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!LineChart){
        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf LineChart
         */
        P.LineChart.prototype.parent = {};
    }
    Object.defineProperty(LineChart.prototype, "onMouseReleased", {
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
    if(!LineChart){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf LineChart
         */
        P.LineChart.prototype.onMouseReleased = {};
    }
    Object.defineProperty(LineChart.prototype, "onFocusLost", {
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
    if(!LineChart){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf LineChart
         */
        P.LineChart.prototype.onFocusLost = {};
    }
    Object.defineProperty(LineChart.prototype, "onMousePressed", {
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
    if(!LineChart){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf LineChart
         */
        P.LineChart.prototype.onMousePressed = {};
    }
    Object.defineProperty(LineChart.prototype, "foreground", {
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
    if(!LineChart){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf LineChart
         */
        P.LineChart.prototype.foreground = {};
    }
    Object.defineProperty(LineChart.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!LineChart){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf LineChart
         */
        P.LineChart.prototype.error = '';
    }
    Object.defineProperty(LineChart.prototype, "enabled", {
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
    if(!LineChart){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf LineChart
         */
        P.LineChart.prototype.enabled = true;
    }
    Object.defineProperty(LineChart.prototype, "onComponentMoved", {
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
    if(!LineChart){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf LineChart
         */
        P.LineChart.prototype.onComponentMoved = {};
    }
    Object.defineProperty(LineChart.prototype, "componentPopupMenu", {
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
    if(!LineChart){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf LineChart
         */
        P.LineChart.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(LineChart.prototype, "top", {
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
    if(!LineChart){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf LineChart
         */
        P.LineChart.prototype.top = 0;
    }
    Object.defineProperty(LineChart.prototype, "onComponentResized", {
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
    if(!LineChart){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf LineChart
         */
        P.LineChart.prototype.onComponentResized = {};
    }
    Object.defineProperty(LineChart.prototype, "onMouseEntered", {
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
    if(!LineChart){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf LineChart
         */
        P.LineChart.prototype.onMouseEntered = {};
    }
    Object.defineProperty(LineChart.prototype, "toolTipText", {
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
    if(!LineChart){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf LineChart
         */
        P.LineChart.prototype.toolTipText = '';
    }
    Object.defineProperty(LineChart.prototype, "height", {
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
    if(!LineChart){
        /**
         * Height of the component.
         * @property height
         * @memberOf LineChart
         */
        P.LineChart.prototype.height = 0;
    }
    Object.defineProperty(LineChart.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!LineChart){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf LineChart
         */
        P.LineChart.prototype.element = {};
    }
    Object.defineProperty(LineChart.prototype, "onComponentShown", {
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
    if(!LineChart){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf LineChart
         */
        P.LineChart.prototype.onComponentShown = {};
    }
    Object.defineProperty(LineChart.prototype, "onMouseMoved", {
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
    if(!LineChart){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf LineChart
         */
        P.LineChart.prototype.onMouseMoved = {};
    }
    Object.defineProperty(LineChart.prototype, "opaque", {
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
    if(!LineChart){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf LineChart
         */
        P.LineChart.prototype.opaque = true;
    }
    Object.defineProperty(LineChart.prototype, "visible", {
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
    if(!LineChart){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf LineChart
         */
        P.LineChart.prototype.visible = true;
    }
    Object.defineProperty(LineChart.prototype, "onComponentHidden", {
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
    if(!LineChart){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf LineChart
         */
        P.LineChart.prototype.onComponentHidden = {};
    }
    Object.defineProperty(LineChart.prototype, "nextFocusableComponent", {
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
    if(!LineChart){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf LineChart
         */
        P.LineChart.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(LineChart.prototype, "onActionPerformed", {
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
    if(!LineChart){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf LineChart
         */
        P.LineChart.prototype.onActionPerformed = {};
    }
    Object.defineProperty(LineChart.prototype, "onKeyReleased", {
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
    if(!LineChart){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf LineChart
         */
        P.LineChart.prototype.onKeyReleased = {};
    }
    Object.defineProperty(LineChart.prototype, "focusable", {
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
    if(!LineChart){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf LineChart
         */
        P.LineChart.prototype.focusable = true;
    }
    Object.defineProperty(LineChart.prototype, "onKeyTyped", {
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
    if(!LineChart){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf LineChart
         */
        P.LineChart.prototype.onKeyTyped = {};
    }
    Object.defineProperty(LineChart.prototype, "onMouseWheelMoved", {
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
    if(!LineChart){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf LineChart
         */
        P.LineChart.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(LineChart.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!LineChart){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf LineChart
         */
        P.LineChart.prototype.component = {};
    }
    Object.defineProperty(LineChart.prototype, "onFocusGained", {
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
    if(!LineChart){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf LineChart
         */
        P.LineChart.prototype.onFocusGained = {};
    }
    Object.defineProperty(LineChart.prototype, "left", {
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
    if(!LineChart){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf LineChart
         */
        P.LineChart.prototype.left = 0;
    }
    Object.defineProperty(LineChart.prototype, "background", {
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
    if(!LineChart){
        /**
         * The background color of this component.
         * @property background
         * @memberOf LineChart
         */
        P.LineChart.prototype.background = {};
    }
    Object.defineProperty(LineChart.prototype, "onMouseClicked", {
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
    if(!LineChart){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf LineChart
         */
        P.LineChart.prototype.onMouseClicked = {};
    }
    Object.defineProperty(LineChart.prototype, "onMouseExited", {
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
    if(!LineChart){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf LineChart
         */
        P.LineChart.prototype.onMouseExited = {};
    }
    Object.defineProperty(LineChart.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!LineChart){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf LineChart
         */
        P.LineChart.prototype.name = '';
    }
    Object.defineProperty(LineChart.prototype, "width", {
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
    if(!LineChart){
        /**
         * Width of the component.
         * @property width
         * @memberOf LineChart
         */
        P.LineChart.prototype.width = 0;
    }
    Object.defineProperty(LineChart.prototype, "font", {
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
    if(!LineChart){
        /**
         * The font of this component.
         * @property font
         * @memberOf LineChart
         */
        P.LineChart.prototype.font = {};
    }
    Object.defineProperty(LineChart.prototype, "onKeyPressed", {
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
    if(!LineChart){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf LineChart
         */
        P.LineChart.prototype.onKeyPressed = {};
    }
    Object.defineProperty(LineChart.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!LineChart){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf LineChart
         */
        P.LineChart.prototype.focus = function(){};
    }
})();