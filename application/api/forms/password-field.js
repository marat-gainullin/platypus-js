(function() {
    var javaClass = Java.type("com.eas.client.forms.components.PasswordField");
    javaClass.setPublisher(function(aDelegate) {
        return new P.PasswordField(null, aDelegate);
    });
    
    /**
     * Password field component.
     * @param text the text for the component (optional).
     * @constructor PasswordField PasswordField
     */
    P.PasswordField = function (text) {
        var maxArgs = 1;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 1 ? new javaClass(P.boxAsJava(text))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.PasswordField.superclass)
            P.PasswordField.superclass.constructor.apply(this, arguments);
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
        if(!P.PasswordField){
            /**
             * The mouse <code>Cursor</code> over this component.
             * @property cursor
             * @memberOf PasswordField
             */
            P.PasswordField.prototype.cursor = {};
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
        if(!P.PasswordField){
            /**
             * Mouse dragged event handler function.
             * @property onMouseDragged
             * @memberOf PasswordField
             */
            P.PasswordField.prototype.onMouseDragged = {};
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
        if(!P.PasswordField){
            /**
             * Mouse released event handler function.
             * @property onMouseReleased
             * @memberOf PasswordField
             */
            P.PasswordField.prototype.onMouseReleased = {};
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
        if(!P.PasswordField){
            /**
             * Keyboard focus lost by the component event handler function.
             * @property onFocusLost
             * @memberOf PasswordField
             */
            P.PasswordField.prototype.onFocusLost = {};
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
        if(!P.PasswordField){
            /**
             * Value change handler.
             * @property onValueChange
             * @memberOf PasswordField
             */
            P.PasswordField.prototype.onValueChange = {};
        }
        Object.defineProperty(this, "emptyText", {
            get: function() {
                var value = delegate.emptyText;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.emptyText = P.boxAsJava(aValue);
            }
        });
        if(!P.PasswordField){
            /**
             * The text to be shown when component's value is absent.
             * @property emptyText
             * @memberOf PasswordField
             */
            P.PasswordField.prototype.emptyText = '';
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
        if(!P.PasswordField){
            /**
             * Mouse pressed event handler function.
             * @property onMousePressed
             * @memberOf PasswordField
             */
            P.PasswordField.prototype.onMousePressed = {};
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
        if(!P.PasswordField){
            /**
             * The foreground color of this component.
             * @property foreground
             * @memberOf PasswordField
             */
            P.PasswordField.prototype.foreground = {};
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
        if(!P.PasswordField){
            /**
             * An error message of this component.
             * Validation procedure may set this property and subsequent focus lost event will clear it.
             * @property error
             * @memberOf PasswordField
             */
            P.PasswordField.prototype.error = '';
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
        if(!P.PasswordField){
            /**
             * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
             * @property enabled
             * @memberOf PasswordField
             */
            P.PasswordField.prototype.enabled = true;
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
        if(!P.PasswordField){
            /**
             * Component moved event handler function.
             * @property onComponentMoved
             * @memberOf PasswordField
             */
            P.PasswordField.prototype.onComponentMoved = {};
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
        if(!P.PasswordField){
            /**
             * <code>PopupMenu</code> that assigned for this component.
             * @property componentPopupMenu
             * @memberOf PasswordField
             */
            P.PasswordField.prototype.componentPopupMenu = {};
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
        if(!P.PasswordField){
            /**
             * Vertical coordinate of the component.
             * @property top
             * @memberOf PasswordField
             */
            P.PasswordField.prototype.top = 0;
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
        if(!P.PasswordField){
            /**
             * Component resized event handler function.
             * @property onComponentResized
             * @memberOf PasswordField
             */
            P.PasswordField.prototype.onComponentResized = {};
        }
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parentWidget;
                return P.boxAsJs(value);
            }
        });
        if(!P.PasswordField){
            /**
             * Parent container of this widget.
             * @property parentWidget
             * @memberOf PasswordField
             */
            P.PasswordField.prototype.parent = {};
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
        if(!P.PasswordField){
            /**
             * The text contained in this component.
             * @property text
             * @memberOf PasswordField
             */
            P.PasswordField.prototype.text = '';
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
        if(!P.PasswordField){
            /**
             * Mouse entered over the component event handler function.
             * @property onMouseEntered
             * @memberOf PasswordField
             */
            P.PasswordField.prototype.onMouseEntered = {};
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
        if(!P.PasswordField){
            /**
             * The value of this component.
             * @property value
             * @memberOf PasswordField
             */
            P.PasswordField.prototype.value = {};
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
        if(!P.PasswordField){
            /**
             * The tooltip string that has been set with.
             * @property toolTipText
             * @memberOf PasswordField
             */
            P.PasswordField.prototype.toolTipText = '';
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
        if(!P.PasswordField){
            /**
             * Height of the component.
             * @property height
             * @memberOf PasswordField
             */
            P.PasswordField.prototype.height = 0;
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
        if(!P.PasswordField){
            /**
             * Component shown event handler function.
             * @property onComponentShown
             * @memberOf PasswordField
             */
            P.PasswordField.prototype.onComponentShown = {};
        }
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });
        if(!P.PasswordField){
            /**
             * Native API. Returns low level html element. Applicable only in HTML5 client.
             * @property element
             * @memberOf PasswordField
             */
            P.PasswordField.prototype.element = {};
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
        if(!P.PasswordField){
            /**
             * Mouse moved event handler function.
             * @property onMouseMoved
             * @memberOf PasswordField
             */
            P.PasswordField.prototype.onMouseMoved = {};
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
        if(!P.PasswordField){
            /**
             * True if this component is completely opaque.
             * @property opaque
             * @memberOf PasswordField
             */
            P.PasswordField.prototype.opaque = true;
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
        if(!P.PasswordField){
            /**
             * Determines whether this component should be visible when its parent is visible.
             * @property visible
             * @memberOf PasswordField
             */
            P.PasswordField.prototype.visible = true;
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
        if(!P.PasswordField){
            /**
             * Component hidden event handler function.
             * @property onComponentHidden
             * @memberOf PasswordField
             */
            P.PasswordField.prototype.onComponentHidden = {};
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
        if(!P.PasswordField){
            /**
             * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
             * @property nextFocusableComponent
             * @memberOf PasswordField
             */
            P.PasswordField.prototype.nextFocusableComponent = {};
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
        if(!P.PasswordField){
            /**
             * Main action performed event handler function.
             * @property onActionPerformed
             * @memberOf PasswordField
             */
            P.PasswordField.prototype.onActionPerformed = {};
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
        if(!P.PasswordField){
            /**
             * Key released event handler function.
             * @property onKeyReleased
             * @memberOf PasswordField
             */
            P.PasswordField.prototype.onKeyReleased = {};
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
        if(!P.PasswordField){
            /**
             * Determines whether this component may be focused.
             * @property focusable
             * @memberOf PasswordField
             */
            P.PasswordField.prototype.focusable = true;
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
        if(!P.PasswordField){
            /**
             * Key typed event handler function.
             * @property onKeyTyped
             * @memberOf PasswordField
             */
            P.PasswordField.prototype.onKeyTyped = {};
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
        if(!P.PasswordField){
            /**
             * Mouse wheel moved event handler function.
             * @property onMouseWheelMoved
             * @memberOf PasswordField
             */
            P.PasswordField.prototype.onMouseWheelMoved = {};
        }
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });
        if(!P.PasswordField){
            /**
             * Native API. Returns low level swing component. Applicable only in J2SE swing client.
             * @property component
             * @memberOf PasswordField
             */
            P.PasswordField.prototype.component = {};
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
        if(!P.PasswordField){
            /**
             * Keyboard focus gained by the component event.
             * @property onFocusGained
             * @memberOf PasswordField
             */
            P.PasswordField.prototype.onFocusGained = {};
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
        if(!P.PasswordField){
            /**
             * Horizontal coordinate of the component.
             * @property left
             * @memberOf PasswordField
             */
            P.PasswordField.prototype.left = 0;
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
        if(!P.PasswordField){
            /**
             * The background color of this component.
             * @property background
             * @memberOf PasswordField
             */
            P.PasswordField.prototype.background = {};
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
        if(!P.PasswordField){
            /**
             * Mouse clicked event handler function.
             * @property onMouseClicked
             * @memberOf PasswordField
             */
            P.PasswordField.prototype.onMouseClicked = {};
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
        if(!P.PasswordField){
            /**
             * Mouse exited over the component event handler function.
             * @property onMouseExited
             * @memberOf PasswordField
             */
            P.PasswordField.prototype.onMouseExited = {};
        }
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.PasswordField){
            /**
             * Gets name of this component.
             * @property name
             * @memberOf PasswordField
             */
            P.PasswordField.prototype.name = '';
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
        if(!P.PasswordField){
            /**
             * Width of the component.
             * @property width
             * @memberOf PasswordField
             */
            P.PasswordField.prototype.width = 0;
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
        if(!P.PasswordField){
            /**
             * The font of this component.
             * @property font
             * @memberOf PasswordField
             */
            P.PasswordField.prototype.font = {};
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
        if(!P.PasswordField){
            /**
             * Key pressed event handler function.
             * @property onKeyPressed
             * @memberOf PasswordField
             */
            P.PasswordField.prototype.onKeyPressed = {};
        }
    };
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf PasswordField
         */
        P.PasswordField.prototype.focus = function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        };

})();