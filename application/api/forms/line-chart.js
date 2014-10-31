(function() {
    var javaClass = Java.type("com.eas.client.chart.LineChart");
    javaClass.setPublisher(function(aDelegate) {
        return new P.LineChart(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor LineChart LineChart
     */
    P.LineChart = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.LineChart.superclass)
            P.LineChart.superclass.constructor.apply(this, arguments);
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
        if(!P.LineChart){
            /**
             * The mouse <code>Cursor</code> over this component.
             * @property cursor
             * @memberOf LineChart
             */
            P.LineChart.prototype.cursor = {};
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
        if(!P.LineChart){
            /**
             * Mouse dragged event handler function.
             * @property onMouseDragged
             * @memberOf LineChart
             */
            P.LineChart.prototype.onMouseDragged = {};
        }
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parent;
                return P.boxAsJs(value);
            }
        });
        if(!P.LineChart){
            /**
             * Gets the parent of this component.
             * @property parent
             * @memberOf LineChart
             */
            P.LineChart.prototype.parent = {};
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
        if(!P.LineChart){
            /**
             * Mouse released event handler function.
             * @property onMouseReleased
             * @memberOf LineChart
             */
            P.LineChart.prototype.onMouseReleased = {};
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
        if(!P.LineChart){
            /**
             * Keyboard focus lost by the component event handler function.
             * @property onFocusLost
             * @memberOf LineChart
             */
            P.LineChart.prototype.onFocusLost = {};
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
        if(!P.LineChart){
            /**
             * Mouse pressed event handler function.
             * @property onMousePressed
             * @memberOf LineChart
             */
            P.LineChart.prototype.onMousePressed = {};
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
        if(!P.LineChart){
            /**
             * The foreground color of this component.
             * @property foreground
             * @memberOf LineChart
             */
            P.LineChart.prototype.foreground = {};
        }
        Object.defineProperty(this, "error", {
            get: function() {
                var value = delegate.error;
                return P.boxAsJs(value);
            }
        });
        if(!P.LineChart){
            /**
             * An error message of this component.
             * Validation procedure may set this property and subsequent focus lost event will clear it.
             * @property error
             * @memberOf LineChart
             */
            P.LineChart.prototype.error = '';
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
        if(!P.LineChart){
            /**
             * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
             * @property enabled
             * @memberOf LineChart
             */
            P.LineChart.prototype.enabled = true;
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
        if(!P.LineChart){
            /**
             * Component moved event handler function.
             * @property onComponentMoved
             * @memberOf LineChart
             */
            P.LineChart.prototype.onComponentMoved = {};
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
        if(!P.LineChart){
            /**
             * <code>PopupMenu</code> that assigned for this component.
             * @property componentPopupMenu
             * @memberOf LineChart
             */
            P.LineChart.prototype.componentPopupMenu = {};
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
        if(!P.LineChart){
            /**
             * Vertical coordinate of the component.
             * @property top
             * @memberOf LineChart
             */
            P.LineChart.prototype.top = 0;
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
        if(!P.LineChart){
            /**
             * Component resized event handler function.
             * @property onComponentResized
             * @memberOf LineChart
             */
            P.LineChart.prototype.onComponentResized = {};
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
        if(!P.LineChart){
            /**
             * Mouse entered over the component event handler function.
             * @property onMouseEntered
             * @memberOf LineChart
             */
            P.LineChart.prototype.onMouseEntered = {};
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
        if(!P.LineChart){
            /**
             * The tooltip string that has been set with.
             * @property toolTipText
             * @memberOf LineChart
             */
            P.LineChart.prototype.toolTipText = '';
        }
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });
        if(!P.LineChart){
            /**
             * Native API. Returns low level html element. Applicable only in HTML5 client.
             * @property element
             * @memberOf LineChart
             */
            P.LineChart.prototype.element = {};
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
        if(!P.LineChart){
            /**
             * Height of the component.
             * @property height
             * @memberOf LineChart
             */
            P.LineChart.prototype.height = 0;
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
        if(!P.LineChart){
            /**
             * Component shown event handler function.
             * @property onComponentShown
             * @memberOf LineChart
             */
            P.LineChart.prototype.onComponentShown = {};
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
        if(!P.LineChart){
            /**
             * Mouse moved event handler function.
             * @property onMouseMoved
             * @memberOf LineChart
             */
            P.LineChart.prototype.onMouseMoved = {};
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
        if(!P.LineChart){
            /**
             * True if this component is completely opaque.
             * @property opaque
             * @memberOf LineChart
             */
            P.LineChart.prototype.opaque = true;
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
        if(!P.LineChart){
            /**
             * Determines whether this component should be visible when its parent is visible.
             * @property visible
             * @memberOf LineChart
             */
            P.LineChart.prototype.visible = true;
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
        if(!P.LineChart){
            /**
             * Component hidden event handler function.
             * @property onComponentHidden
             * @memberOf LineChart
             */
            P.LineChart.prototype.onComponentHidden = {};
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
        if(!P.LineChart){
            /**
             * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
             * @property nextFocusableComponent
             * @memberOf LineChart
             */
            P.LineChart.prototype.nextFocusableComponent = {};
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
        if(!P.LineChart){
            /**
             * Main action performed event handler function.
             * @property onActionPerformed
             * @memberOf LineChart
             */
            P.LineChart.prototype.onActionPerformed = {};
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
        if(!P.LineChart){
            /**
             * Key released event handler function.
             * @property onKeyReleased
             * @memberOf LineChart
             */
            P.LineChart.prototype.onKeyReleased = {};
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
        if(!P.LineChart){
            /**
             * Determines whether this component may be focused.
             * @property focusable
             * @memberOf LineChart
             */
            P.LineChart.prototype.focusable = true;
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
        if(!P.LineChart){
            /**
             * Key typed event handler function.
             * @property onKeyTyped
             * @memberOf LineChart
             */
            P.LineChart.prototype.onKeyTyped = {};
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
        if(!P.LineChart){
            /**
             * Mouse wheel moved event handler function.
             * @property onMouseWheelMoved
             * @memberOf LineChart
             */
            P.LineChart.prototype.onMouseWheelMoved = {};
        }
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });
        if(!P.LineChart){
            /**
             * Native API. Returns low level swing component. Applicable only in J2SE swing client.
             * @property component
             * @memberOf LineChart
             */
            P.LineChart.prototype.component = {};
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
        if(!P.LineChart){
            /**
             * Keyboard focus gained by the component event.
             * @property onFocusGained
             * @memberOf LineChart
             */
            P.LineChart.prototype.onFocusGained = {};
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
        if(!P.LineChart){
            /**
             * Horizontal coordinate of the component.
             * @property left
             * @memberOf LineChart
             */
            P.LineChart.prototype.left = 0;
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
        if(!P.LineChart){
            /**
             * The background color of this component.
             * @property background
             * @memberOf LineChart
             */
            P.LineChart.prototype.background = {};
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
        if(!P.LineChart){
            /**
             * Mouse clicked event handler function.
             * @property onMouseClicked
             * @memberOf LineChart
             */
            P.LineChart.prototype.onMouseClicked = {};
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
        if(!P.LineChart){
            /**
             * Mouse exited over the component event handler function.
             * @property onMouseExited
             * @memberOf LineChart
             */
            P.LineChart.prototype.onMouseExited = {};
        }
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.LineChart){
            /**
             * Gets name of this component.
             * @property name
             * @memberOf LineChart
             */
            P.LineChart.prototype.name = '';
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
        if(!P.LineChart){
            /**
             * Width of the component.
             * @property width
             * @memberOf LineChart
             */
            P.LineChart.prototype.width = 0;
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
        if(!P.LineChart){
            /**
             * The font of this component.
             * @property font
             * @memberOf LineChart
             */
            P.LineChart.prototype.font = {};
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
        if(!P.LineChart){
            /**
             * Key pressed event handler function.
             * @property onKeyPressed
             * @memberOf LineChart
             */
            P.LineChart.prototype.onKeyPressed = {};
        }
    };
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf LineChart
         */
        P.LineChart.prototype.focus = function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        };

})();