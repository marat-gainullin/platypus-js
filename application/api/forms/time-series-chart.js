(function() {
    var javaClass = Java.type("com.eas.client.chart.TimeSeriesChart");
    javaClass.setPublisher(function(aDelegate) {
        return new P.TimeSeriesChart(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor TimeSeriesChart TimeSeriesChart
     */
    P.TimeSeriesChart = function TimeSeriesChart() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(TimeSeriesChart.superclass)
            TimeSeriesChart.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "TimeSeriesChart", {value: TimeSeriesChart});
    Object.defineProperty(TimeSeriesChart.prototype, "cursor", {
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
    if(!TimeSeriesChart){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf TimeSeriesChart
         */
        P.TimeSeriesChart.prototype.cursor = {};
    }
    Object.defineProperty(TimeSeriesChart.prototype, "onMouseDragged", {
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
    if(!TimeSeriesChart){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf TimeSeriesChart
         */
        P.TimeSeriesChart.prototype.onMouseDragged = {};
    }
    Object.defineProperty(TimeSeriesChart.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!TimeSeriesChart){
        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf TimeSeriesChart
         */
        P.TimeSeriesChart.prototype.parent = {};
    }
    Object.defineProperty(TimeSeriesChart.prototype, "onMouseReleased", {
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
    if(!TimeSeriesChart){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf TimeSeriesChart
         */
        P.TimeSeriesChart.prototype.onMouseReleased = {};
    }
    Object.defineProperty(TimeSeriesChart.prototype, "onFocusLost", {
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
    if(!TimeSeriesChart){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf TimeSeriesChart
         */
        P.TimeSeriesChart.prototype.onFocusLost = {};
    }
    Object.defineProperty(TimeSeriesChart.prototype, "onMousePressed", {
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
    if(!TimeSeriesChart){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf TimeSeriesChart
         */
        P.TimeSeriesChart.prototype.onMousePressed = {};
    }
    Object.defineProperty(TimeSeriesChart.prototype, "foreground", {
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
    if(!TimeSeriesChart){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf TimeSeriesChart
         */
        P.TimeSeriesChart.prototype.foreground = {};
    }
    Object.defineProperty(TimeSeriesChart.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!TimeSeriesChart){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf TimeSeriesChart
         */
        P.TimeSeriesChart.prototype.error = '';
    }
    Object.defineProperty(TimeSeriesChart.prototype, "enabled", {
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
    if(!TimeSeriesChart){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf TimeSeriesChart
         */
        P.TimeSeriesChart.prototype.enabled = true;
    }
    Object.defineProperty(TimeSeriesChart.prototype, "onComponentMoved", {
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
    if(!TimeSeriesChart){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf TimeSeriesChart
         */
        P.TimeSeriesChart.prototype.onComponentMoved = {};
    }
    Object.defineProperty(TimeSeriesChart.prototype, "componentPopupMenu", {
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
    if(!TimeSeriesChart){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf TimeSeriesChart
         */
        P.TimeSeriesChart.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(TimeSeriesChart.prototype, "top", {
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
    if(!TimeSeriesChart){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf TimeSeriesChart
         */
        P.TimeSeriesChart.prototype.top = 0;
    }
    Object.defineProperty(TimeSeriesChart.prototype, "onComponentResized", {
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
    if(!TimeSeriesChart){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf TimeSeriesChart
         */
        P.TimeSeriesChart.prototype.onComponentResized = {};
    }
    Object.defineProperty(TimeSeriesChart.prototype, "onMouseEntered", {
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
    if(!TimeSeriesChart){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf TimeSeriesChart
         */
        P.TimeSeriesChart.prototype.onMouseEntered = {};
    }
    Object.defineProperty(TimeSeriesChart.prototype, "toolTipText", {
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
    if(!TimeSeriesChart){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf TimeSeriesChart
         */
        P.TimeSeriesChart.prototype.toolTipText = '';
    }
    Object.defineProperty(TimeSeriesChart.prototype, "height", {
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
    if(!TimeSeriesChart){
        /**
         * Height of the component.
         * @property height
         * @memberOf TimeSeriesChart
         */
        P.TimeSeriesChart.prototype.height = 0;
    }
    Object.defineProperty(TimeSeriesChart.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!TimeSeriesChart){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf TimeSeriesChart
         */
        P.TimeSeriesChart.prototype.element = {};
    }
    Object.defineProperty(TimeSeriesChart.prototype, "onComponentShown", {
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
    if(!TimeSeriesChart){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf TimeSeriesChart
         */
        P.TimeSeriesChart.prototype.onComponentShown = {};
    }
    Object.defineProperty(TimeSeriesChart.prototype, "onMouseMoved", {
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
    if(!TimeSeriesChart){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf TimeSeriesChart
         */
        P.TimeSeriesChart.prototype.onMouseMoved = {};
    }
    Object.defineProperty(TimeSeriesChart.prototype, "opaque", {
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
    if(!TimeSeriesChart){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf TimeSeriesChart
         */
        P.TimeSeriesChart.prototype.opaque = true;
    }
    Object.defineProperty(TimeSeriesChart.prototype, "visible", {
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
    if(!TimeSeriesChart){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf TimeSeriesChart
         */
        P.TimeSeriesChart.prototype.visible = true;
    }
    Object.defineProperty(TimeSeriesChart.prototype, "onComponentHidden", {
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
    if(!TimeSeriesChart){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf TimeSeriesChart
         */
        P.TimeSeriesChart.prototype.onComponentHidden = {};
    }
    Object.defineProperty(TimeSeriesChart.prototype, "nextFocusableComponent", {
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
    if(!TimeSeriesChart){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf TimeSeriesChart
         */
        P.TimeSeriesChart.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(TimeSeriesChart.prototype, "onActionPerformed", {
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
    if(!TimeSeriesChart){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf TimeSeriesChart
         */
        P.TimeSeriesChart.prototype.onActionPerformed = {};
    }
    Object.defineProperty(TimeSeriesChart.prototype, "onKeyReleased", {
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
    if(!TimeSeriesChart){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf TimeSeriesChart
         */
        P.TimeSeriesChart.prototype.onKeyReleased = {};
    }
    Object.defineProperty(TimeSeriesChart.prototype, "focusable", {
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
    if(!TimeSeriesChart){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf TimeSeriesChart
         */
        P.TimeSeriesChart.prototype.focusable = true;
    }
    Object.defineProperty(TimeSeriesChart.prototype, "onKeyTyped", {
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
    if(!TimeSeriesChart){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf TimeSeriesChart
         */
        P.TimeSeriesChart.prototype.onKeyTyped = {};
    }
    Object.defineProperty(TimeSeriesChart.prototype, "onMouseWheelMoved", {
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
    if(!TimeSeriesChart){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf TimeSeriesChart
         */
        P.TimeSeriesChart.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(TimeSeriesChart.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!TimeSeriesChart){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf TimeSeriesChart
         */
        P.TimeSeriesChart.prototype.component = {};
    }
    Object.defineProperty(TimeSeriesChart.prototype, "onFocusGained", {
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
    if(!TimeSeriesChart){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf TimeSeriesChart
         */
        P.TimeSeriesChart.prototype.onFocusGained = {};
    }
    Object.defineProperty(TimeSeriesChart.prototype, "left", {
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
    if(!TimeSeriesChart){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf TimeSeriesChart
         */
        P.TimeSeriesChart.prototype.left = 0;
    }
    Object.defineProperty(TimeSeriesChart.prototype, "background", {
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
    if(!TimeSeriesChart){
        /**
         * The background color of this component.
         * @property background
         * @memberOf TimeSeriesChart
         */
        P.TimeSeriesChart.prototype.background = {};
    }
    Object.defineProperty(TimeSeriesChart.prototype, "onMouseClicked", {
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
    if(!TimeSeriesChart){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf TimeSeriesChart
         */
        P.TimeSeriesChart.prototype.onMouseClicked = {};
    }
    Object.defineProperty(TimeSeriesChart.prototype, "onMouseExited", {
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
    if(!TimeSeriesChart){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf TimeSeriesChart
         */
        P.TimeSeriesChart.prototype.onMouseExited = {};
    }
    Object.defineProperty(TimeSeriesChart.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!TimeSeriesChart){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf TimeSeriesChart
         */
        P.TimeSeriesChart.prototype.name = '';
    }
    Object.defineProperty(TimeSeriesChart.prototype, "width", {
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
    if(!TimeSeriesChart){
        /**
         * Width of the component.
         * @property width
         * @memberOf TimeSeriesChart
         */
        P.TimeSeriesChart.prototype.width = 0;
    }
    Object.defineProperty(TimeSeriesChart.prototype, "font", {
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
    if(!TimeSeriesChart){
        /**
         * The font of this component.
         * @property font
         * @memberOf TimeSeriesChart
         */
        P.TimeSeriesChart.prototype.font = {};
    }
    Object.defineProperty(TimeSeriesChart.prototype, "onKeyPressed", {
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
    if(!TimeSeriesChart){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf TimeSeriesChart
         */
        P.TimeSeriesChart.prototype.onKeyPressed = {};
    }
    Object.defineProperty(TimeSeriesChart.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!TimeSeriesChart){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf TimeSeriesChart
         */
        P.TimeSeriesChart.prototype.focus = function(){};
    }
})();