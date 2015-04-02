(function() {
    var javaClass = Java.type("com.eas.client.forms.components.ProgressBar");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ProgressBar(null, null, aDelegate);
    });
    
    /**
     * Progress bar component.
     * @param min the minimum value (optional)
     * @param max the maximum value (optional)
     * @constructor ProgressBar ProgressBar
     */
    P.ProgressBar = function (min, max) {
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
        if(P.ProgressBar.superclass)
            P.ProgressBar.superclass.constructor.apply(this, arguments);
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
        if(!P.ProgressBar){
            /**
             * The mouse <code>Cursor</code> over this component.
             * @property cursor
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.cursor = {};
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
        if(!P.ProgressBar){
            /**
             * Mouse dragged event handler function.
             * @property onMouseDragged
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.onMouseDragged = {};
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
        if(!P.ProgressBar){
            /**
             * Mouse released event handler function.
             * @property onMouseReleased
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.onMouseReleased = {};
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
        if(!P.ProgressBar){
            /**
             * Keyboard focus lost by the component event handler function.
             * @property onFocusLost
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.onFocusLost = {};
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
        if(!P.ProgressBar){
            /**
             * Value change handler.
             * @property onValueChange
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.onValueChange = {};
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
        if(!P.ProgressBar){
            /**
             * Mouse pressed event handler function.
             * @property onMousePressed
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.onMousePressed = {};
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
        if(!P.ProgressBar){
            /**
             * The foreground color of this component.
             * @property foreground
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.foreground = {};
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
        if(!P.ProgressBar){
            /**
             * An error message of this component.
             * Validation procedure may set this property and subsequent focus lost event will clear it.
             * @property error
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.error = '';
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
        if(!P.ProgressBar){
            /**
             * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
             * @property enabled
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.enabled = true;
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
        if(!P.ProgressBar){
            /**
             * Component moved event handler function.
             * @property onComponentMoved
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.onComponentMoved = {};
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
        if(!P.ProgressBar){
            /**
             * <code>PopupMenu</code> that assigned for this component.
             * @property componentPopupMenu
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.componentPopupMenu = {};
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
        if(!P.ProgressBar){
            /**
             * Vertical coordinate of the component.
             * @property top
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.top = 0;
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
        if(!P.ProgressBar){
            /**
             * Component resized event handler function.
             * @property onComponentResized
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.onComponentResized = {};
        }
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parentWidget;
                return P.boxAsJs(value);
            }
        });
        if(!P.ProgressBar){
            /**
             * Parent container of this widget.
             * @property parentWidget
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.parent = {};
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
        if(!P.ProgressBar){
            /**
             * String representation of the current progress.
             * @property text
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.text = '';
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
        if(!P.ProgressBar){
            /**
             * Mouse entered over the component event handler function.
             * @property onMouseEntered
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.onMouseEntered = {};
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
        if(!P.ProgressBar){
            /**
             * The current value of the progress bar.
             * @property value
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.value = 0;
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
        if(!P.ProgressBar){
            /**
             * The tooltip string that has been set with.
             * @property toolTipText
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.toolTipText = '';
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
        if(!P.ProgressBar){
            /**
             * Height of the component.
             * @property height
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.height = 0;
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
        if(!P.ProgressBar){
            /**
             * Component shown event handler function.
             * @property onComponentShown
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.onComponentShown = {};
        }
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });
        if(!P.ProgressBar){
            /**
             * Native API. Returns low level html element. Applicable only in HTML5 client.
             * @property element
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.element = {};
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
        if(!P.ProgressBar){
            /**
             * Mouse moved event handler function.
             * @property onMouseMoved
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.onMouseMoved = {};
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
        if(!P.ProgressBar){
            /**
             * True if this component is completely opaque.
             * @property opaque
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.opaque = true;
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
        if(!P.ProgressBar){
            /**
             * Determines whether this component should be visible when its parent is visible.
             * @property visible
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.visible = true;
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
        if(!P.ProgressBar){
            /**
             * Component hidden event handler function.
             * @property onComponentHidden
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.onComponentHidden = {};
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
        if(!P.ProgressBar){
            /**
             * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
             * @property nextFocusableComponent
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.nextFocusableComponent = {};
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
        if(!P.ProgressBar){
            /**
             * Main action performed event handler function.
             * @property onActionPerformed
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.onActionPerformed = {};
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
        if(!P.ProgressBar){
            /**
             * Key released event handler function.
             * @property onKeyReleased
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.onKeyReleased = {};
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
        if(!P.ProgressBar){
            /**
             * Determines whether this component may be focused.
             * @property focusable
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.focusable = true;
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
        if(!P.ProgressBar){
            /**
             * Key typed event handler function.
             * @property onKeyTyped
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.onKeyTyped = {};
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
        if(!P.ProgressBar){
            /**
             * Mouse wheel moved event handler function.
             * @property onMouseWheelMoved
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.onMouseWheelMoved = {};
        }
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });
        if(!P.ProgressBar){
            /**
             * Native API. Returns low level swing component. Applicable only in J2SE swing client.
             * @property component
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.component = {};
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
        if(!P.ProgressBar){
            /**
             * Keyboard focus gained by the component event.
             * @property onFocusGained
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.onFocusGained = {};
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
        if(!P.ProgressBar){
            /**
             * Horizontal coordinate of the component.
             * @property left
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.left = 0;
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
        if(!P.ProgressBar){
            /**
             * The background color of this component.
             * @property background
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.background = {};
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
        if(!P.ProgressBar){
            /**
             * Mouse clicked event handler function.
             * @property onMouseClicked
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.onMouseClicked = {};
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
        if(!P.ProgressBar){
            /**
             * Mouse exited over the component event handler function.
             * @property onMouseExited
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.onMouseExited = {};
        }
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.ProgressBar){
            /**
             * Gets name of this component.
             * @property name
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.name = '';
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
        if(!P.ProgressBar){
            /**
             * Width of the component.
             * @property width
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.width = 0;
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
        if(!P.ProgressBar){
            /**
             * The progress bar's maximum value.
             * @property maximum
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.maximum = 0;
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
        if(!P.ProgressBar){
            /**
             * The progress bar's minimum value.
             * @property minimum
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.minimum = 0;
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
        if(!P.ProgressBar){
            /**
             * The font of this component.
             * @property font
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.font = {};
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
        if(!P.ProgressBar){
            /**
             * Key pressed event handler function.
             * @property onKeyPressed
             * @memberOf ProgressBar
             */
            P.ProgressBar.prototype.onKeyPressed = {};
        }
    };
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf ProgressBar
         */
        P.ProgressBar.prototype.focus = function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        };

})();