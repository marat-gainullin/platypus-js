(function() {
    var javaClass = Java.type("com.eas.client.forms.components.FormattedField");
    javaClass.setPublisher(function(aDelegate) {
        return new P.FormattedField(null, aDelegate);
    });
    
    /**
     * Formatted field component.
     * @param value the value for the formatted field (optional)
     * @constructor FormattedField FormattedField
     */
    P.FormattedField = function (value) {
        var maxArgs = 1;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 1 ? new javaClass(P.boxAsJava(value))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.FormattedField.superclass)
            P.FormattedField.superclass.constructor.apply(this, arguments);
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
        if(!P.FormattedField){
            /**
             * The mouse <code>Cursor</code> over this component.
             * @property cursor
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.cursor = {};
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
        if(!P.FormattedField){
            /**
             * Mouse dragged event handler function.
             * @property onMouseDragged
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.onMouseDragged = {};
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
        if(!P.FormattedField){
            /**
             * Mouse released event handler function.
             * @property onMouseReleased
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.onMouseReleased = {};
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
        if(!P.FormattedField){
            /**
             * Keyboard focus lost by the component event handler function.
             * @property onFocusLost
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.onFocusLost = {};
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
        if(!P.FormattedField){
            /**
             * Value change handler.
             * @property onValueChange
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.onValueChange = {};
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
        if(!P.FormattedField){
            /**
             * The text to be shown when component's value is absent.
             * @property emptyText
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.emptyText = '';
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
        if(!P.FormattedField){
            /**
             * Mouse pressed event handler function.
             * @property onMousePressed
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.onMousePressed = {};
        }
        Object.defineProperty(this, "onParse", {
            get: function() {
                var value = delegate.onParse;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onParse = P.boxAsJava(aValue);
            }
        });
        if(!P.FormattedField){
            /**
             * Generated property jsDoc.
             * @property onParse
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.onParse = {};
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
        if(!P.FormattedField){
            /**
             * The foreground color of this component.
             * @property foreground
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.foreground = {};
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
        if(!P.FormattedField){
            /**
             * An error message of this component.
             * Validation procedure may set this property and subsequent focus lost event will clear it.
             * @property error
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.error = '';
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
        if(!P.FormattedField){
            /**
             * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
             * @property enabled
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.enabled = true;
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
        if(!P.FormattedField){
            /**
             * Component moved event handler function.
             * @property onComponentMoved
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.onComponentMoved = {};
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
        if(!P.FormattedField){
            /**
             * Widget's value.
             * @property jsValue
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.value = {};
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
        if(!P.FormattedField){
            /**
             * <code>PopupMenu</code> that assigned for this component.
             * @property componentPopupMenu
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.componentPopupMenu = {};
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
        if(!P.FormattedField){
            /**
             * Vertical coordinate of the component.
             * @property top
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.top = 0;
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
        if(!P.FormattedField){
            /**
             * Component resized event handler function.
             * @property onComponentResized
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.onComponentResized = {};
        }
        Object.defineProperty(this, "valueType", {
            get: function() {
                var value = delegate.valueType;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.valueType = P.boxAsJava(aValue);
            }
        });
        if(!P.FormattedField){
            /**
             * ValueType hint for the field. It is used to determine, how to interpret format pattern.
             * @property valueType
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.valueType = 0;
        }
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parentWidget;
                return P.boxAsJs(value);
            }
        });
        if(!P.FormattedField){
            /**
             * Parent container of this widget.
             * @property parentWidget
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.parent = {};
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
        if(!P.FormattedField){
            /**
             * Text on the component.
             * @property text
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.text = '';
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
        if(!P.FormattedField){
            /**
             * Mouse entered over the component event handler function.
             * @property onMouseEntered
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.onMouseEntered = {};
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
        if(!P.FormattedField){
            /**
             * The tooltip string that has been set with.
             * @property toolTipText
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.toolTipText = '';
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
        if(!P.FormattedField){
            /**
             * Height of the component.
             * @property height
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.height = 0;
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
        if(!P.FormattedField){
            /**
             * Component shown event handler function.
             * @property onComponentShown
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.onComponentShown = {};
        }
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });
        if(!P.FormattedField){
            /**
             * Native API. Returns low level html element. Applicable only in HTML5 client.
             * @property element
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.element = {};
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
        if(!P.FormattedField){
            /**
             * Mouse moved event handler function.
             * @property onMouseMoved
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.onMouseMoved = {};
        }
        Object.defineProperty(this, "onFormat", {
            get: function() {
                var value = delegate.onFormat;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onFormat = P.boxAsJava(aValue);
            }
        });
        if(!P.FormattedField){
            /**
             * Generated property jsDoc.
             * @property onFormat
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.onFormat = {};
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
        if(!P.FormattedField){
            /**
             * True if this component is completely opaque.
             * @property opaque
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.opaque = true;
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
        if(!P.FormattedField){
            /**
             * Determines whether this component should be visible when its parent is visible.
             * @property visible
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.visible = true;
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
        if(!P.FormattedField){
            /**
             * Component hidden event handler function.
             * @property onComponentHidden
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.onComponentHidden = {};
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
        if(!P.FormattedField){
            /**
             * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
             * @property nextFocusableComponent
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.nextFocusableComponent = {};
        }
        Object.defineProperty(this, "format", {
            get: function() {
                var value = delegate.format;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.format = P.boxAsJava(aValue);
            }
        });
        if(!P.FormattedField){
            /**
             * Field text format.
             * @property format
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.format = '';
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
        if(!P.FormattedField){
            /**
             * Main action performed event handler function.
             * @property onActionPerformed
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.onActionPerformed = {};
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
        if(!P.FormattedField){
            /**
             * Key released event handler function.
             * @property onKeyReleased
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.onKeyReleased = {};
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
        if(!P.FormattedField){
            /**
             * Determines whether this component may be focused.
             * @property focusable
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.focusable = true;
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
        if(!P.FormattedField){
            /**
             * Key typed event handler function.
             * @property onKeyTyped
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.onKeyTyped = {};
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
        if(!P.FormattedField){
            /**
             * Mouse wheel moved event handler function.
             * @property onMouseWheelMoved
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.onMouseWheelMoved = {};
        }
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });
        if(!P.FormattedField){
            /**
             * Native API. Returns low level swing component. Applicable only in J2SE swing client.
             * @property component
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.component = {};
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
        if(!P.FormattedField){
            /**
             * Keyboard focus gained by the component event.
             * @property onFocusGained
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.onFocusGained = {};
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
        if(!P.FormattedField){
            /**
             * Horizontal coordinate of the component.
             * @property left
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.left = 0;
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
        if(!P.FormattedField){
            /**
             * The background color of this component.
             * @property background
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.background = {};
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
        if(!P.FormattedField){
            /**
             * Mouse clicked event handler function.
             * @property onMouseClicked
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.onMouseClicked = {};
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
        if(!P.FormattedField){
            /**
             * Mouse exited over the component event handler function.
             * @property onMouseExited
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.onMouseExited = {};
        }
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.FormattedField){
            /**
             * Gets name of this component.
             * @property name
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.name = '';
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
        if(!P.FormattedField){
            /**
             * Width of the component.
             * @property width
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.width = 0;
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
        if(!P.FormattedField){
            /**
             * The font of this component.
             * @property font
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.font = {};
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
        if(!P.FormattedField){
            /**
             * Key pressed event handler function.
             * @property onKeyPressed
             * @memberOf FormattedField
             */
            P.FormattedField.prototype.onKeyPressed = {};
        }
    };
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf FormattedField
         */
        P.FormattedField.prototype.focus = function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        };

})();