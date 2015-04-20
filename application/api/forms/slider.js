(function() {
    var javaClass = Java.type("com.eas.client.forms.components.Slider");
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
    P.Slider = function (min, max, value) {
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
        if(P.Slider.superclass)
            P.Slider.superclass.constructor.apply(this, arguments);
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
        if(!P.Slider){
            /**
             * The mouse <code>Cursor</code> over this component.
             * @property cursor
             * @memberOf Slider
             */
            P.Slider.prototype.cursor = {};
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
        if(!P.Slider){
            /**
             * Mouse dragged event handler function.
             * @property onMouseDragged
             * @memberOf Slider
             */
            P.Slider.prototype.onMouseDragged = {};
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
        if(!P.Slider){
            /**
             * Mouse released event handler function.
             * @property onMouseReleased
             * @memberOf Slider
             */
            P.Slider.prototype.onMouseReleased = {};
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
        if(!P.Slider){
            /**
             * Keyboard focus lost by the component event handler function.
             * @property onFocusLost
             * @memberOf Slider
             */
            P.Slider.prototype.onFocusLost = {};
        }
        Object.defineProperty(this, "onValueChange", {
            get: function() {
                var value = delegate.onValueChange;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onValueChange = P.boxAsJava(aValue);
            }
        });
        if(!P.Slider){
            /**
             * Value change handler.
             * @property onValueChange
             * @memberOf Slider
             */
            P.Slider.prototype.onValueChange = {};
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
        if(!P.Slider){
            /**
             * Mouse pressed event handler function.
             * @property onMousePressed
             * @memberOf Slider
             */
            P.Slider.prototype.onMousePressed = {};
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
        if(!P.Slider){
            /**
             * The foreground color of this component.
             * @property foreground
             * @memberOf Slider
             */
            P.Slider.prototype.foreground = {};
        }
        Object.defineProperty(this, "error", {
            get: function() {
                var value = delegate.error;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.error = P.boxAsJava(aValue);
            }
        });
        if(!P.Slider){
            /**
             * An error message of this component.
             * Validation procedure may set this property and subsequent focus lost event will clear it.
             * @property error
             * @memberOf Slider
             */
            P.Slider.prototype.error = '';
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
        if(!P.Slider){
            /**
             * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
             * @property enabled
             * @memberOf Slider
             */
            P.Slider.prototype.enabled = true;
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
        if(!P.Slider){
            /**
             * Component moved event handler function.
             * @property onComponentMoved
             * @memberOf Slider
             */
            P.Slider.prototype.onComponentMoved = {};
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
        if(!P.Slider){
            /**
             * <code>PopupMenu</code> that assigned for this component.
             * @property componentPopupMenu
             * @memberOf Slider
             */
            P.Slider.prototype.componentPopupMenu = {};
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
        if(!P.Slider){
            /**
             * Vertical coordinate of the component.
             * @property top
             * @memberOf Slider
             */
            P.Slider.prototype.top = 0;
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
        if(!P.Slider){
            /**
             * Component resized event handler function.
             * @property onComponentResized
             * @memberOf Slider
             */
            P.Slider.prototype.onComponentResized = {};
        }
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parentWidget;
                return P.boxAsJs(value);
            }
        });
        if(!P.Slider){
            /**
             * Parent container of this widget.
             * @property parentWidget
             * @memberOf Slider
             */
            P.Slider.prototype.parent = {};
        }
        Object.defineProperty(this, "text", {
            get: function() {
                var value = delegate.text;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.text = P.boxAsJava(aValue);
            }
        });
        if(!P.Slider){
            /**
             * Generated property jsDoc.
             * @property text
             * @memberOf Slider
             */
            P.Slider.prototype.text = '';
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
        if(!P.Slider){
            /**
             * Mouse entered over the component event handler function.
             * @property onMouseEntered
             * @memberOf Slider
             */
            P.Slider.prototype.onMouseEntered = {};
        }
        Object.defineProperty(this, "value", {
            get: function() {
                var value = delegate.value;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.value = P.boxAsJava(aValue);
            }
        });
        if(!P.Slider){
            /**
             * Slider's value. Can't be null.
             * @property value
             * @memberOf Slider
             */
            P.Slider.prototype.value = 0;
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
        if(!P.Slider){
            /**
             * The tooltip string that has been set with.
             * @property toolTipText
             * @memberOf Slider
             */
            P.Slider.prototype.toolTipText = '';
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
        if(!P.Slider){
            /**
             * Height of the component.
             * @property height
             * @memberOf Slider
             */
            P.Slider.prototype.height = 0;
        }
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });
        if(!P.Slider){
            /**
             * Native API. Returns low level html element. Applicable only in HTML5 client.
             * @property element
             * @memberOf Slider
             */
            P.Slider.prototype.element = {};
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
        if(!P.Slider){
            /**
             * Component shown event handler function.
             * @property onComponentShown
             * @memberOf Slider
             */
            P.Slider.prototype.onComponentShown = {};
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
        if(!P.Slider){
            /**
             * Mouse moved event handler function.
             * @property onMouseMoved
             * @memberOf Slider
             */
            P.Slider.prototype.onMouseMoved = {};
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
        if(!P.Slider){
            /**
             * True if this component is completely opaque.
             * @property opaque
             * @memberOf Slider
             */
            P.Slider.prototype.opaque = true;
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
        if(!P.Slider){
            /**
             * Determines whether this component should be visible when its parent is visible.
             * @property visible
             * @memberOf Slider
             */
            P.Slider.prototype.visible = true;
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
        if(!P.Slider){
            /**
             * Component hidden event handler function.
             * @property onComponentHidden
             * @memberOf Slider
             */
            P.Slider.prototype.onComponentHidden = {};
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
        if(!P.Slider){
            /**
             * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
             * @property nextFocusableComponent
             * @memberOf Slider
             */
            P.Slider.prototype.nextFocusableComponent = {};
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
        if(!P.Slider){
            /**
             * Key released event handler function.
             * @property onKeyReleased
             * @memberOf Slider
             */
            P.Slider.prototype.onKeyReleased = {};
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
        if(!P.Slider){
            /**
             * Main action performed event handler function.
             * @property onActionPerformed
             * @memberOf Slider
             */
            P.Slider.prototype.onActionPerformed = {};
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
        if(!P.Slider){
            /**
             * Determines whether this component may be focused.
             * @property focusable
             * @memberOf Slider
             */
            P.Slider.prototype.focusable = true;
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
        if(!P.Slider){
            /**
             * Key typed event handler function.
             * @property onKeyTyped
             * @memberOf Slider
             */
            P.Slider.prototype.onKeyTyped = {};
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
        if(!P.Slider){
            /**
             * Mouse wheel moved event handler function.
             * @property onMouseWheelMoved
             * @memberOf Slider
             */
            P.Slider.prototype.onMouseWheelMoved = {};
        }
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });
        if(!P.Slider){
            /**
             * Native API. Returns low level swing component. Applicable only in J2SE swing client.
             * @property component
             * @memberOf Slider
             */
            P.Slider.prototype.component = {};
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
        if(!P.Slider){
            /**
             * Keyboard focus gained by the component event.
             * @property onFocusGained
             * @memberOf Slider
             */
            P.Slider.prototype.onFocusGained = {};
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
        if(!P.Slider){
            /**
             * Horizontal coordinate of the component.
             * @property left
             * @memberOf Slider
             */
            P.Slider.prototype.left = 0;
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
        if(!P.Slider){
            /**
             * The background color of this component.
             * @property background
             * @memberOf Slider
             */
            P.Slider.prototype.background = {};
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
        if(!P.Slider){
            /**
             * Mouse clicked event handler function.
             * @property onMouseClicked
             * @memberOf Slider
             */
            P.Slider.prototype.onMouseClicked = {};
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
        if(!P.Slider){
            /**
             * Mouse exited over the component event handler function.
             * @property onMouseExited
             * @memberOf Slider
             */
            P.Slider.prototype.onMouseExited = {};
        }
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.Slider){
            /**
             * Gets name of this component.
             * @property name
             * @memberOf Slider
             */
            P.Slider.prototype.name = '';
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
        if(!P.Slider){
            /**
             * Width of the component.
             * @property width
             * @memberOf Slider
             */
            P.Slider.prototype.width = 0;
        }
        Object.defineProperty(this, "maximum", {
            get: function() {
                var value = delegate.maximum;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.maximum = P.boxAsJava(aValue);
            }
        });
        if(!P.Slider){
            /**
             * The maximum value supported by the slider.
             * @property maximum
             * @memberOf Slider
             */
            P.Slider.prototype.maximum = 0;
        }
        Object.defineProperty(this, "minimum", {
            get: function() {
                var value = delegate.minimum;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.minimum = P.boxAsJava(aValue);
            }
        });
        if(!P.Slider){
            /**
             * The minimum value supported by the slider.
             * @property minimum
             * @memberOf Slider
             */
            P.Slider.prototype.minimum = 0;
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
        if(!P.Slider){
            /**
             * The font of this component.
             * @property font
             * @memberOf Slider
             */
            P.Slider.prototype.font = {};
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
        if(!P.Slider){
            /**
             * Key pressed event handler function.
             * @property onKeyPressed
             * @memberOf Slider
             */
            P.Slider.prototype.onKeyPressed = {};
        }
    };
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf Slider
         */
        P.Slider.prototype.focus = function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        };

})();