(function() {
    var javaClass = Java.type("com.eas.client.forms.components.CheckBox");
    javaClass.setPublisher(function(aDelegate) {
        return new P.CheckBox(null, null, null, aDelegate);
    });
    
    /**
     * Check box component.
     * @param text the text of the check box (optional).
     * @param selected <code>true</code> if selected (optional).
     * @param actionPerformed the function for the action performed (optional).
     * @constructor CheckBox CheckBox
     */
    P.CheckBox = function (text, selected, actionPerformed) {
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
        if(P.CheckBox.superclass)
            P.CheckBox.superclass.constructor.apply(this, arguments);
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
        if(!P.CheckBox){
            /**
             * The mouse <code>Cursor</code> over this component.
             * @property cursor
             * @memberOf CheckBox
             */
            P.CheckBox.prototype.cursor = {};
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
        if(!P.CheckBox){
            /**
             * Mouse dragged event handler function.
             * @property onMouseDragged
             * @memberOf CheckBox
             */
            P.CheckBox.prototype.onMouseDragged = {};
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
        if(!P.CheckBox){
            /**
             * Mouse released event handler function.
             * @property onMouseReleased
             * @memberOf CheckBox
             */
            P.CheckBox.prototype.onMouseReleased = {};
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
        if(!P.CheckBox){
            /**
             * Keyboard focus lost by the component event handler function.
             * @property onFocusLost
             * @memberOf CheckBox
             */
            P.CheckBox.prototype.onFocusLost = {};
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
        if(!P.CheckBox){
            /**
             * Value change handler.
             * @property onValueChange
             * @memberOf CheckBox
             */
            P.CheckBox.prototype.onValueChange = {};
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
        if(!P.CheckBox){
            /**
             * Mouse pressed event handler function.
             * @property onMousePressed
             * @memberOf CheckBox
             */
            P.CheckBox.prototype.onMousePressed = {};
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
        if(!P.CheckBox){
            /**
             * The foreground color of this component.
             * @property foreground
             * @memberOf CheckBox
             */
            P.CheckBox.prototype.foreground = {};
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
        if(!P.CheckBox){
            /**
             * An error message of this component.
             * Validation procedure may set this property and subsequent focus lost event will clear it.
             * @property error
             * @memberOf CheckBox
             */
            P.CheckBox.prototype.error = '';
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
        if(!P.CheckBox){
            /**
             * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
             * @property enabled
             * @memberOf CheckBox
             */
            P.CheckBox.prototype.enabled = true;
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
        if(!P.CheckBox){
            /**
             * Component moved event handler function.
             * @property onComponentMoved
             * @memberOf CheckBox
             */
            P.CheckBox.prototype.onComponentMoved = {};
        }
        Object.defineProperty(this, "value", {
            get: function() {
                var value = delegate.jsValue;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.jsValue = P.boxAsJava(aValue);
            }
        });
        if(!P.CheckBox){
            /**
             * Widget's value.
             * @property jsValue
             * @memberOf CheckBox
             */
            P.CheckBox.prototype.value = {};
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
        if(!P.CheckBox){
            /**
             * <code>PopupMenu</code> that assigned for this component.
             * @property componentPopupMenu
             * @memberOf CheckBox
             */
            P.CheckBox.prototype.componentPopupMenu = {};
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
        if(!P.CheckBox){
            /**
             * Vertical coordinate of the component.
             * @property top
             * @memberOf CheckBox
             */
            P.CheckBox.prototype.top = 0;
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
        if(!P.CheckBox){
            /**
             * Component resized event handler function.
             * @property onComponentResized
             * @memberOf CheckBox
             */
            P.CheckBox.prototype.onComponentResized = {};
        }
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parentWidget;
                return P.boxAsJs(value);
            }
        });
        if(!P.CheckBox){
            /**
             * Parent container of this widget.
             * @property parentWidget
             * @memberOf CheckBox
             */
            P.CheckBox.prototype.parent = {};
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
        if(!P.CheckBox){
            /**
             * @property text
             * @memberOf CheckBox
             * Text of the check box.*/
            P.CheckBox.prototype.text = '';
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
        if(!P.CheckBox){
            /**
             * Mouse entered over the component event handler function.
             * @property onMouseEntered
             * @memberOf CheckBox
             */
            P.CheckBox.prototype.onMouseEntered = {};
        }
        Object.defineProperty(this, "selected", {
            get: function() {
                var value = delegate.selected;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.selected = P.boxAsJava(aValue);
            }
        });
        if(!P.CheckBox){
            /**
             * @property selected
             * @memberOf CheckBox
             * Determines whether this component is selected.*/
            P.CheckBox.prototype.selected = true;
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
        if(!P.CheckBox){
            /**
             * The tooltip string that has been set with.
             * @property toolTipText
             * @memberOf CheckBox
             */
            P.CheckBox.prototype.toolTipText = '';
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
        if(!P.CheckBox){
            /**
             * Height of the component.
             * @property height
             * @memberOf CheckBox
             */
            P.CheckBox.prototype.height = 0;
        }
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });
        if(!P.CheckBox){
            /**
             * Native API. Returns low level html element. Applicable only in HTML5 client.
             * @property element
             * @memberOf CheckBox
             */
            P.CheckBox.prototype.element = {};
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
        if(!P.CheckBox){
            /**
             * Component shown event handler function.
             * @property onComponentShown
             * @memberOf CheckBox
             */
            P.CheckBox.prototype.onComponentShown = {};
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
        if(!P.CheckBox){
            /**
             * Mouse moved event handler function.
             * @property onMouseMoved
             * @memberOf CheckBox
             */
            P.CheckBox.prototype.onMouseMoved = {};
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
        if(!P.CheckBox){
            /**
             * True if this component is completely opaque.
             * @property opaque
             * @memberOf CheckBox
             */
            P.CheckBox.prototype.opaque = true;
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
        if(!P.CheckBox){
            /**
             * Determines whether this component should be visible when its parent is visible.
             * @property visible
             * @memberOf CheckBox
             */
            P.CheckBox.prototype.visible = true;
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
        if(!P.CheckBox){
            /**
             * Component hidden event handler function.
             * @property onComponentHidden
             * @memberOf CheckBox
             */
            P.CheckBox.prototype.onComponentHidden = {};
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
        if(!P.CheckBox){
            /**
             * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
             * @property nextFocusableComponent
             * @memberOf CheckBox
             */
            P.CheckBox.prototype.nextFocusableComponent = {};
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
        if(!P.CheckBox){
            /**
             * Key released event handler function.
             * @property onKeyReleased
             * @memberOf CheckBox
             */
            P.CheckBox.prototype.onKeyReleased = {};
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
        if(!P.CheckBox){
            /**
             * Main action performed event handler function.
             * @property onActionPerformed
             * @memberOf CheckBox
             */
            P.CheckBox.prototype.onActionPerformed = {};
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
        if(!P.CheckBox){
            /**
             * Determines whether this component may be focused.
             * @property focusable
             * @memberOf CheckBox
             */
            P.CheckBox.prototype.focusable = true;
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
        if(!P.CheckBox){
            /**
             * Key typed event handler function.
             * @property onKeyTyped
             * @memberOf CheckBox
             */
            P.CheckBox.prototype.onKeyTyped = {};
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
        if(!P.CheckBox){
            /**
             * Mouse wheel moved event handler function.
             * @property onMouseWheelMoved
             * @memberOf CheckBox
             */
            P.CheckBox.prototype.onMouseWheelMoved = {};
        }
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });
        if(!P.CheckBox){
            /**
             * Native API. Returns low level swing component. Applicable only in J2SE swing client.
             * @property component
             * @memberOf CheckBox
             */
            P.CheckBox.prototype.component = {};
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
        if(!P.CheckBox){
            /**
             * Keyboard focus gained by the component event.
             * @property onFocusGained
             * @memberOf CheckBox
             */
            P.CheckBox.prototype.onFocusGained = {};
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
        if(!P.CheckBox){
            /**
             * Horizontal coordinate of the component.
             * @property left
             * @memberOf CheckBox
             */
            P.CheckBox.prototype.left = 0;
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
        if(!P.CheckBox){
            /**
             * The background color of this component.
             * @property background
             * @memberOf CheckBox
             */
            P.CheckBox.prototype.background = {};
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
        if(!P.CheckBox){
            /**
             * Mouse clicked event handler function.
             * @property onMouseClicked
             * @memberOf CheckBox
             */
            P.CheckBox.prototype.onMouseClicked = {};
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
        if(!P.CheckBox){
            /**
             * Mouse exited over the component event handler function.
             * @property onMouseExited
             * @memberOf CheckBox
             */
            P.CheckBox.prototype.onMouseExited = {};
        }
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.CheckBox){
            /**
             * Gets name of this component.
             * @property name
             * @memberOf CheckBox
             */
            P.CheckBox.prototype.name = '';
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
        if(!P.CheckBox){
            /**
             * Width of the component.
             * @property width
             * @memberOf CheckBox
             */
            P.CheckBox.prototype.width = 0;
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
        if(!P.CheckBox){
            /**
             * The font of this component.
             * @property font
             * @memberOf CheckBox
             */
            P.CheckBox.prototype.font = {};
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
        if(!P.CheckBox){
            /**
             * Key pressed event handler function.
             * @property onKeyPressed
             * @memberOf CheckBox
             */
            P.CheckBox.prototype.onKeyPressed = {};
        }
    };
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf CheckBox
         */
        P.CheckBox.prototype.focus = function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        };

})();