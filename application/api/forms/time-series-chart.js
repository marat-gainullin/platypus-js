(function() {
    var javaClass = Java.type("com.eas.client.chart.TimeSeriesChart");
    javaClass.setPublisher(function(aDelegate) {
        return new P.TimeSeriesChart(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor TimeSeriesChart TimeSeriesChart
     */
    P.TimeSeriesChart = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.TimeSeriesChart.superclass)
            P.TimeSeriesChart.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "cursor", {
            get: function() {
                var value = delegate.cursor;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.cursor = P.boxAsJava(aValue);
            }
        });
        if(!P.TimeSeriesChart){
            /**
             * The mouse <code>Cursor</code> over this component.
             * @property cursor
             * @memberOf TimeSeriesChart
             */
            P.TimeSeriesChart.prototype.cursor = {};
        }
        Object.defineProperty(this, "onMouseDragged", {
            get: function() {
                var value = delegate.onMouseDragged;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseDragged = P.boxAsJava(aValue);
            }
        });
        if(!P.TimeSeriesChart){
            /**
             * Mouse dragged event handler function.
             * @property onMouseDragged
             * @memberOf TimeSeriesChart
             */
            P.TimeSeriesChart.prototype.onMouseDragged = {};
        }
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parent;
                return P.boxAsJs(value);
            }
        });
        if(!P.TimeSeriesChart){
            /**
             * Gets the parent of this component.
             * @property parent
             * @memberOf TimeSeriesChart
             */
            P.TimeSeriesChart.prototype.parent = {};
        }
        Object.defineProperty(this, "onMouseReleased", {
            get: function() {
                var value = delegate.onMouseReleased;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseReleased = P.boxAsJava(aValue);
            }
        });
        if(!P.TimeSeriesChart){
            /**
             * Mouse released event handler function.
             * @property onMouseReleased
             * @memberOf TimeSeriesChart
             */
            P.TimeSeriesChart.prototype.onMouseReleased = {};
        }
        Object.defineProperty(this, "onFocusLost", {
            get: function() {
                var value = delegate.onFocusLost;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onFocusLost = P.boxAsJava(aValue);
            }
        });
        if(!P.TimeSeriesChart){
            /**
             * Keyboard focus lost by the component event handler function.
             * @property onFocusLost
             * @memberOf TimeSeriesChart
             */
            P.TimeSeriesChart.prototype.onFocusLost = {};
        }
        Object.defineProperty(this, "onMousePressed", {
            get: function() {
                var value = delegate.onMousePressed;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMousePressed = P.boxAsJava(aValue);
            }
        });
        if(!P.TimeSeriesChart){
            /**
             * Mouse pressed event handler function.
             * @property onMousePressed
             * @memberOf TimeSeriesChart
             */
            P.TimeSeriesChart.prototype.onMousePressed = {};
        }
        Object.defineProperty(this, "foreground", {
            get: function() {
                var value = delegate.foreground;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.foreground = P.boxAsJava(aValue);
            }
        });
        if(!P.TimeSeriesChart){
            /**
             * The foreground color of this component.
             * @property foreground
             * @memberOf TimeSeriesChart
             */
            P.TimeSeriesChart.prototype.foreground = {};
        }
        Object.defineProperty(this, "error", {
            get: function() {
                var value = delegate.error;
                return P.boxAsJs(value);
            }
        });
        if(!P.TimeSeriesChart){
            /**
             * An error message of this component.
             * Validation procedure may set this property and subsequent focus lost event will clear it.
             * @property error
             * @memberOf TimeSeriesChart
             */
            P.TimeSeriesChart.prototype.error = '';
        }
        Object.defineProperty(this, "enabled", {
            get: function() {
                var value = delegate.enabled;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.enabled = P.boxAsJava(aValue);
            }
        });
        if(!P.TimeSeriesChart){
            /**
             * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
             * @property enabled
             * @memberOf TimeSeriesChart
             */
            P.TimeSeriesChart.prototype.enabled = true;
        }
        Object.defineProperty(this, "onComponentMoved", {
            get: function() {
                var value = delegate.onComponentMoved;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onComponentMoved = P.boxAsJava(aValue);
            }
        });
        if(!P.TimeSeriesChart){
            /**
             * Component moved event handler function.
             * @property onComponentMoved
             * @memberOf TimeSeriesChart
             */
            P.TimeSeriesChart.prototype.onComponentMoved = {};
        }
        Object.defineProperty(this, "componentPopupMenu", {
            get: function() {
                var value = delegate.componentPopupMenu;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.componentPopupMenu = P.boxAsJava(aValue);
            }
        });
        if(!P.TimeSeriesChart){
            /**
             * <code>PopupMenu</code> that assigned for this component.
             * @property componentPopupMenu
             * @memberOf TimeSeriesChart
             */
            P.TimeSeriesChart.prototype.componentPopupMenu = {};
        }
        Object.defineProperty(this, "top", {
            get: function() {
                var value = delegate.top;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.top = P.boxAsJava(aValue);
            }
        });
        if(!P.TimeSeriesChart){
            /**
             * Vertical coordinate of the component.
             * @property top
             * @memberOf TimeSeriesChart
             */
            P.TimeSeriesChart.prototype.top = 0;
        }
        Object.defineProperty(this, "onComponentResized", {
            get: function() {
                var value = delegate.onComponentResized;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onComponentResized = P.boxAsJava(aValue);
            }
        });
        if(!P.TimeSeriesChart){
            /**
             * Component resized event handler function.
             * @property onComponentResized
             * @memberOf TimeSeriesChart
             */
            P.TimeSeriesChart.prototype.onComponentResized = {};
        }
        Object.defineProperty(this, "onMouseEntered", {
            get: function() {
                var value = delegate.onMouseEntered;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseEntered = P.boxAsJava(aValue);
            }
        });
        if(!P.TimeSeriesChart){
            /**
             * Mouse entered over the component event handler function.
             * @property onMouseEntered
             * @memberOf TimeSeriesChart
             */
            P.TimeSeriesChart.prototype.onMouseEntered = {};
        }
        Object.defineProperty(this, "toolTipText", {
            get: function() {
                var value = delegate.toolTipText;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.toolTipText = P.boxAsJava(aValue);
            }
        });
        if(!P.TimeSeriesChart){
            /**
             * The tooltip string that has been set with.
             * @property toolTipText
             * @memberOf TimeSeriesChart
             */
            P.TimeSeriesChart.prototype.toolTipText = '';
        }
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });
        if(!P.TimeSeriesChart){
            /**
             * Native API. Returns low level html element. Applicable only in HTML5 client.
             * @property element
             * @memberOf TimeSeriesChart
             */
            P.TimeSeriesChart.prototype.element = {};
        }
        Object.defineProperty(this, "height", {
            get: function() {
                var value = delegate.height;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.height = P.boxAsJava(aValue);
            }
        });
        if(!P.TimeSeriesChart){
            /**
             * Height of the component.
             * @property height
             * @memberOf TimeSeriesChart
             */
            P.TimeSeriesChart.prototype.height = 0;
        }
        Object.defineProperty(this, "onComponentShown", {
            get: function() {
                var value = delegate.onComponentShown;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onComponentShown = P.boxAsJava(aValue);
            }
        });
        if(!P.TimeSeriesChart){
            /**
             * Component shown event handler function.
             * @property onComponentShown
             * @memberOf TimeSeriesChart
             */
            P.TimeSeriesChart.prototype.onComponentShown = {};
        }
        Object.defineProperty(this, "onMouseMoved", {
            get: function() {
                var value = delegate.onMouseMoved;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseMoved = P.boxAsJava(aValue);
            }
        });
        if(!P.TimeSeriesChart){
            /**
             * Mouse moved event handler function.
             * @property onMouseMoved
             * @memberOf TimeSeriesChart
             */
            P.TimeSeriesChart.prototype.onMouseMoved = {};
        }
        Object.defineProperty(this, "opaque", {
            get: function() {
                var value = delegate.opaque;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.opaque = P.boxAsJava(aValue);
            }
        });
        if(!P.TimeSeriesChart){
            /**
             * True if this component is completely opaque.
             * @property opaque
             * @memberOf TimeSeriesChart
             */
            P.TimeSeriesChart.prototype.opaque = true;
        }
        Object.defineProperty(this, "visible", {
            get: function() {
                var value = delegate.visible;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.visible = P.boxAsJava(aValue);
            }
        });
        if(!P.TimeSeriesChart){
            /**
             * Determines whether this component should be visible when its parent is visible.
             * @property visible
             * @memberOf TimeSeriesChart
             */
            P.TimeSeriesChart.prototype.visible = true;
        }
        Object.defineProperty(this, "onComponentHidden", {
            get: function() {
                var value = delegate.onComponentHidden;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onComponentHidden = P.boxAsJava(aValue);
            }
        });
        if(!P.TimeSeriesChart){
            /**
             * Component hidden event handler function.
             * @property onComponentHidden
             * @memberOf TimeSeriesChart
             */
            P.TimeSeriesChart.prototype.onComponentHidden = {};
        }
        Object.defineProperty(this, "nextFocusableComponent", {
            get: function() {
                var value = delegate.nextFocusableComponent;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.nextFocusableComponent = P.boxAsJava(aValue);
            }
        });
        if(!P.TimeSeriesChart){
            /**
             * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
             * @property nextFocusableComponent
             * @memberOf TimeSeriesChart
             */
            P.TimeSeriesChart.prototype.nextFocusableComponent = {};
        }
        Object.defineProperty(this, "onActionPerformed", {
            get: function() {
                var value = delegate.onActionPerformed;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onActionPerformed = P.boxAsJava(aValue);
            }
        });
        if(!P.TimeSeriesChart){
            /**
             * Main action performed event handler function.
             * @property onActionPerformed
             * @memberOf TimeSeriesChart
             */
            P.TimeSeriesChart.prototype.onActionPerformed = {};
        }
        Object.defineProperty(this, "onKeyReleased", {
            get: function() {
                var value = delegate.onKeyReleased;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onKeyReleased = P.boxAsJava(aValue);
            }
        });
        if(!P.TimeSeriesChart){
            /**
             * Key released event handler function.
             * @property onKeyReleased
             * @memberOf TimeSeriesChart
             */
            P.TimeSeriesChart.prototype.onKeyReleased = {};
        }
        Object.defineProperty(this, "focusable", {
            get: function() {
                var value = delegate.focusable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.focusable = P.boxAsJava(aValue);
            }
        });
        if(!P.TimeSeriesChart){
            /**
             * Determines whether this component may be focused.
             * @property focusable
             * @memberOf TimeSeriesChart
             */
            P.TimeSeriesChart.prototype.focusable = true;
        }
        Object.defineProperty(this, "onKeyTyped", {
            get: function() {
                var value = delegate.onKeyTyped;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onKeyTyped = P.boxAsJava(aValue);
            }
        });
        if(!P.TimeSeriesChart){
            /**
             * Key typed event handler function.
             * @property onKeyTyped
             * @memberOf TimeSeriesChart
             */
            P.TimeSeriesChart.prototype.onKeyTyped = {};
        }
        Object.defineProperty(this, "onMouseWheelMoved", {
            get: function() {
                var value = delegate.onMouseWheelMoved;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseWheelMoved = P.boxAsJava(aValue);
            }
        });
        if(!P.TimeSeriesChart){
            /**
             * Mouse wheel moved event handler function.
             * @property onMouseWheelMoved
             * @memberOf TimeSeriesChart
             */
            P.TimeSeriesChart.prototype.onMouseWheelMoved = {};
        }
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });
        if(!P.TimeSeriesChart){
            /**
             * Native API. Returns low level swing component. Applicable only in J2SE swing client.
             * @property component
             * @memberOf TimeSeriesChart
             */
            P.TimeSeriesChart.prototype.component = {};
        }
        Object.defineProperty(this, "onFocusGained", {
            get: function() {
                var value = delegate.onFocusGained;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onFocusGained = P.boxAsJava(aValue);
            }
        });
        if(!P.TimeSeriesChart){
            /**
             * Keyboard focus gained by the component event.
             * @property onFocusGained
             * @memberOf TimeSeriesChart
             */
            P.TimeSeriesChart.prototype.onFocusGained = {};
        }
        Object.defineProperty(this, "left", {
            get: function() {
                var value = delegate.left;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.left = P.boxAsJava(aValue);
            }
        });
        if(!P.TimeSeriesChart){
            /**
             * Horizontal coordinate of the component.
             * @property left
             * @memberOf TimeSeriesChart
             */
            P.TimeSeriesChart.prototype.left = 0;
        }
        Object.defineProperty(this, "background", {
            get: function() {
                var value = delegate.background;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.background = P.boxAsJava(aValue);
            }
        });
        if(!P.TimeSeriesChart){
            /**
             * The background color of this component.
             * @property background
             * @memberOf TimeSeriesChart
             */
            P.TimeSeriesChart.prototype.background = {};
        }
        Object.defineProperty(this, "onMouseClicked", {
            get: function() {
                var value = delegate.onMouseClicked;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseClicked = P.boxAsJava(aValue);
            }
        });
        if(!P.TimeSeriesChart){
            /**
             * Mouse clicked event handler function.
             * @property onMouseClicked
             * @memberOf TimeSeriesChart
             */
            P.TimeSeriesChart.prototype.onMouseClicked = {};
        }
        Object.defineProperty(this, "onMouseExited", {
            get: function() {
                var value = delegate.onMouseExited;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseExited = P.boxAsJava(aValue);
            }
        });
        if(!P.TimeSeriesChart){
            /**
             * Mouse exited over the component event handler function.
             * @property onMouseExited
             * @memberOf TimeSeriesChart
             */
            P.TimeSeriesChart.prototype.onMouseExited = {};
        }
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.TimeSeriesChart){
            /**
             * Gets name of this component.
             * @property name
             * @memberOf TimeSeriesChart
             */
            P.TimeSeriesChart.prototype.name = '';
        }
        Object.defineProperty(this, "width", {
            get: function() {
                var value = delegate.width;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.width = P.boxAsJava(aValue);
            }
        });
        if(!P.TimeSeriesChart){
            /**
             * Width of the component.
             * @property width
             * @memberOf TimeSeriesChart
             */
            P.TimeSeriesChart.prototype.width = 0;
        }
        Object.defineProperty(this, "font", {
            get: function() {
                var value = delegate.font;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.font = P.boxAsJava(aValue);
            }
        });
        if(!P.TimeSeriesChart){
            /**
             * The font of this component.
             * @property font
             * @memberOf TimeSeriesChart
             */
            P.TimeSeriesChart.prototype.font = {};
        }
        Object.defineProperty(this, "onKeyPressed", {
            get: function() {
                var value = delegate.onKeyPressed;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onKeyPressed = P.boxAsJava(aValue);
            }
        });
        if(!P.TimeSeriesChart){
            /**
             * Key pressed event handler function.
             * @property onKeyPressed
             * @memberOf TimeSeriesChart
             */
            P.TimeSeriesChart.prototype.onKeyPressed = {};
        }
    };
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf TimeSeriesChart
         */
        P.TimeSeriesChart.prototype.focus = function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        };

})();