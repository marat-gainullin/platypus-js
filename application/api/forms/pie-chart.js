(function() {
    var javaClass = Java.type("com.eas.client.chart.PieChart");
    javaClass.setPublisher(function(aDelegate) {
        return new P.PieChart(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor PieChart PieChart
     */
    P.PieChart = function PieChart() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(PieChart.superclass)
            PieChart.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "PieChart", {value: PieChart});
    Object.defineProperty(PieChart.prototype, "cursor", {
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
    if(!PieChart){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf PieChart
         */
        P.PieChart.prototype.cursor = {};
    }
    Object.defineProperty(PieChart.prototype, "onMouseDragged", {
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
    if(!PieChart){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf PieChart
         */
        P.PieChart.prototype.onMouseDragged = {};
    }
    Object.defineProperty(PieChart.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!PieChart){
        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf PieChart
         */
        P.PieChart.prototype.parent = {};
    }
    Object.defineProperty(PieChart.prototype, "onMouseReleased", {
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
    if(!PieChart){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf PieChart
         */
        P.PieChart.prototype.onMouseReleased = {};
    }
    Object.defineProperty(PieChart.prototype, "onFocusLost", {
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
    if(!PieChart){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf PieChart
         */
        P.PieChart.prototype.onFocusLost = {};
    }
    Object.defineProperty(PieChart.prototype, "onMousePressed", {
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
    if(!PieChart){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf PieChart
         */
        P.PieChart.prototype.onMousePressed = {};
    }
    Object.defineProperty(PieChart.prototype, "foreground", {
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
    if(!PieChart){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf PieChart
         */
        P.PieChart.prototype.foreground = {};
    }
    Object.defineProperty(PieChart.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!PieChart){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf PieChart
         */
        P.PieChart.prototype.error = '';
    }
    Object.defineProperty(PieChart.prototype, "enabled", {
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
    if(!PieChart){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf PieChart
         */
        P.PieChart.prototype.enabled = true;
    }
    Object.defineProperty(PieChart.prototype, "onComponentMoved", {
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
    if(!PieChart){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf PieChart
         */
        P.PieChart.prototype.onComponentMoved = {};
    }
    Object.defineProperty(PieChart.prototype, "componentPopupMenu", {
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
    if(!PieChart){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf PieChart
         */
        P.PieChart.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(PieChart.prototype, "top", {
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
    if(!PieChart){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf PieChart
         */
        P.PieChart.prototype.top = 0;
    }
    Object.defineProperty(PieChart.prototype, "onComponentResized", {
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
    if(!PieChart){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf PieChart
         */
        P.PieChart.prototype.onComponentResized = {};
    }
    Object.defineProperty(PieChart.prototype, "onMouseEntered", {
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
    if(!PieChart){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf PieChart
         */
        P.PieChart.prototype.onMouseEntered = {};
    }
    Object.defineProperty(PieChart.prototype, "toolTipText", {
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
    if(!PieChart){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf PieChart
         */
        P.PieChart.prototype.toolTipText = '';
    }
    Object.defineProperty(PieChart.prototype, "height", {
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
    if(!PieChart){
        /**
         * Height of the component.
         * @property height
         * @memberOf PieChart
         */
        P.PieChart.prototype.height = 0;
    }
    Object.defineProperty(PieChart.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!PieChart){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf PieChart
         */
        P.PieChart.prototype.element = {};
    }
    Object.defineProperty(PieChart.prototype, "onComponentShown", {
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
    if(!PieChart){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf PieChart
         */
        P.PieChart.prototype.onComponentShown = {};
    }
    Object.defineProperty(PieChart.prototype, "onMouseMoved", {
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
    if(!PieChart){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf PieChart
         */
        P.PieChart.prototype.onMouseMoved = {};
    }
    Object.defineProperty(PieChart.prototype, "opaque", {
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
    if(!PieChart){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf PieChart
         */
        P.PieChart.prototype.opaque = true;
    }
    Object.defineProperty(PieChart.prototype, "visible", {
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
    if(!PieChart){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf PieChart
         */
        P.PieChart.prototype.visible = true;
    }
    Object.defineProperty(PieChart.prototype, "onComponentHidden", {
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
    if(!PieChart){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf PieChart
         */
        P.PieChart.prototype.onComponentHidden = {};
    }
    Object.defineProperty(PieChart.prototype, "nextFocusableComponent", {
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
    if(!PieChart){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf PieChart
         */
        P.PieChart.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(PieChart.prototype, "onActionPerformed", {
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
    if(!PieChart){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf PieChart
         */
        P.PieChart.prototype.onActionPerformed = {};
    }
    Object.defineProperty(PieChart.prototype, "onKeyReleased", {
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
    if(!PieChart){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf PieChart
         */
        P.PieChart.prototype.onKeyReleased = {};
    }
    Object.defineProperty(PieChart.prototype, "focusable", {
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
    if(!PieChart){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf PieChart
         */
        P.PieChart.prototype.focusable = true;
    }
    Object.defineProperty(PieChart.prototype, "onKeyTyped", {
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
    if(!PieChart){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf PieChart
         */
        P.PieChart.prototype.onKeyTyped = {};
    }
    Object.defineProperty(PieChart.prototype, "onMouseWheelMoved", {
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
    if(!PieChart){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf PieChart
         */
        P.PieChart.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(PieChart.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!PieChart){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf PieChart
         */
        P.PieChart.prototype.component = {};
    }
    Object.defineProperty(PieChart.prototype, "onFocusGained", {
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
    if(!PieChart){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf PieChart
         */
        P.PieChart.prototype.onFocusGained = {};
    }
    Object.defineProperty(PieChart.prototype, "left", {
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
    if(!PieChart){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf PieChart
         */
        P.PieChart.prototype.left = 0;
    }
    Object.defineProperty(PieChart.prototype, "background", {
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
    if(!PieChart){
        /**
         * The background color of this component.
         * @property background
         * @memberOf PieChart
         */
        P.PieChart.prototype.background = {};
    }
    Object.defineProperty(PieChart.prototype, "onMouseClicked", {
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
    if(!PieChart){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf PieChart
         */
        P.PieChart.prototype.onMouseClicked = {};
    }
    Object.defineProperty(PieChart.prototype, "onMouseExited", {
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
    if(!PieChart){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf PieChart
         */
        P.PieChart.prototype.onMouseExited = {};
    }
    Object.defineProperty(PieChart.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!PieChart){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf PieChart
         */
        P.PieChart.prototype.name = '';
    }
    Object.defineProperty(PieChart.prototype, "width", {
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
    if(!PieChart){
        /**
         * Width of the component.
         * @property width
         * @memberOf PieChart
         */
        P.PieChart.prototype.width = 0;
    }
    Object.defineProperty(PieChart.prototype, "font", {
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
    if(!PieChart){
        /**
         * The font of this component.
         * @property font
         * @memberOf PieChart
         */
        P.PieChart.prototype.font = {};
    }
    Object.defineProperty(PieChart.prototype, "onKeyPressed", {
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
    if(!PieChart){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf PieChart
         */
        P.PieChart.prototype.onKeyPressed = {};
    }
    Object.defineProperty(PieChart.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!PieChart){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf PieChart
         */
        P.PieChart.prototype.focus = function(){};
    }
})();