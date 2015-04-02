(function() {
    var javaClass = Java.type("com.eas.client.forms.components.model.ModelFormattedField");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ModelFormattedField(aDelegate);
    });
    
    /**
     * A model component that shows a date.
     * @constructor ModelFormattedField ModelFormattedField
     */
    P.ModelFormattedField = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.ModelFormattedField.superclass)
            P.ModelFormattedField.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "selectOnly", {
            get: function() {
                var value = delegate.selectOnly;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.selectOnly = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelFormattedField){
            /**
             * Generated property jsDoc.
             * @property selectOnly
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.selectOnly = true;
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
        if(!P.ModelFormattedField){
            /**
             * Mouse released event handler function.
             * @property onMouseReleased
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.onMouseReleased = {};
        }
        Object.defineProperty(this, "data", {
            get: function() {
                var value = delegate.data;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.data = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelFormattedField){
            /**
             * Object, bound to the widget.
             * @property data
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.data = {};
        }
        Object.defineProperty(this, "nullable", {
            get: function() {
                var value = delegate.nullable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.nullable = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelFormattedField){
            /**
             * Generated property jsDoc.
             * @property nullable
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.nullable = true;
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
        if(!P.ModelFormattedField){
            /**
             * Value change handler.
             * @property onValueChange
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.onValueChange = {};
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
        if(!P.ModelFormattedField){
            /**
             * Generated property jsDoc.
             * @property onParse
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.onParse = {};
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
        if(!P.ModelFormattedField){
            /**
             * The foreground color of this component.
             * @property foreground
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.foreground = {};
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
        if(!P.ModelFormattedField){
            /**
             * Component moved event handler function.
             * @property onComponentMoved
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.onComponentMoved = {};
        }
        Object.defineProperty(this, "onRender", {
            get: function() {
                var value = delegate.onRender;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onRender = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelFormattedField){
            /**
             * Component's rendering event handler function.
             * @property onRender
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.onRender = {};
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
        if(!P.ModelFormattedField){
            /**
             * ValueType hint for the field. It is used to determine, how to interpret format pattern.
             * @property valueType
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.valueType = 0;
        }
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parentWidget;
                return P.boxAsJs(value);
            }
        });
        if(!P.ModelFormattedField){
            /**
             * Parent container of this widget.
             * @property parentWidget
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.parent = {};
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
        if(!P.ModelFormattedField){
            /**
             * Generated property jsDoc.
             * @property text
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.text = '';
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
        if(!P.ModelFormattedField){
            /**
             * Mouse entered over the component event handler function.
             * @property onMouseEntered
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.onMouseEntered = {};
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
        if(!P.ModelFormattedField){
            /**
             * The tooltip string that has been set with.
             * @property toolTipText
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.toolTipText = '';
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
        if(!P.ModelFormattedField){
            /**
             * Height of the component.
             * @property height
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.height = 0;
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
        if(!P.ModelFormattedField){
            /**
             * Component shown event handler function.
             * @property onComponentShown
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.onComponentShown = {};
        }
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });
        if(!P.ModelFormattedField){
            /**
             * Native API. Returns low level html element. Applicable only in HTML5 client.
             * @property element
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.element = {};
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
        if(!P.ModelFormattedField){
            /**
             * Determines whether this component should be visible when its parent is visible.
             * @property visible
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.visible = true;
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
        if(!P.ModelFormattedField){
            /**
             * Component hidden event handler function.
             * @property onComponentHidden
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.onComponentHidden = {};
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
        if(!P.ModelFormattedField){
            /**
             * The format string of the component.
             * @property format
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.format = '';
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
        if(!P.ModelFormattedField){
            /**
             * Main action performed event handler function.
             * @property onActionPerformed
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.onActionPerformed = {};
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
        if(!P.ModelFormattedField){
            /**
             * Key released event handler function.
             * @property onKeyReleased
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.onKeyReleased = {};
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
        if(!P.ModelFormattedField){
            /**
             * Determines whether this component may be focused.
             * @property focusable
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.focusable = true;
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
        if(!P.ModelFormattedField){
            /**
             * Key typed event handler function.
             * @property onKeyTyped
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.onKeyTyped = {};
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
        if(!P.ModelFormattedField){
            /**
             * Mouse wheel moved event handler function.
             * @property onMouseWheelMoved
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.onMouseWheelMoved = {};
        }
        Object.defineProperty(this, "field", {
            get: function() {
                var value = delegate.field;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.field = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelFormattedField){
            /**
             * Model binding field.
             * @property field
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.field = '';
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
        if(!P.ModelFormattedField){
            /**
             * Horizontal coordinate of the component.
             * @property left
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.left = 0;
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
        if(!P.ModelFormattedField){
            /**
             * The background color of this component.
             * @property background
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.background = {};
        }
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.ModelFormattedField){
            /**
             * Generated property jsDoc.
             * @property name
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.name = '';
        }
        Object.defineProperty(this, "cursor", {
            get: function() {
                var value = delegate.cursor;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.cursor = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelFormattedField){
            /**
             * Generated property jsDoc.
             * @property cursor
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.cursor = {};
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
        if(!P.ModelFormattedField){
            /**
             * Mouse dragged event handler function.
             * @property onMouseDragged
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.onMouseDragged = {};
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
        if(!P.ModelFormattedField){
            /**
             * Keyboard focus lost by the component event handler function.
             * @property onFocusLost
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.onFocusLost = {};
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
        if(!P.ModelFormattedField){
            /**
             * Generated property jsDoc.
             * @property emptyText
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.emptyText = '';
        }
        Object.defineProperty(this, "icon", {
            get: function() {
                var value = delegate.icon;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.icon = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelFormattedField){
            /**
             * Generated property jsDoc.
             * @property icon
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.icon = {};
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
        if(!P.ModelFormattedField){
            /**
             * Mouse pressed event handler function.
             * @property onMousePressed
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.onMousePressed = {};
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
        if(!P.ModelFormattedField){
            /**
             * An error message of this component.
             * Validation procedure may set this property and subsequent focus lost event will clear it.
             * @property error
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.error = '';
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
        if(!P.ModelFormattedField){
            /**
             * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
             * @property enabled
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.enabled = true;
        }
        Object.defineProperty(this, "onSelect", {
            get: function() {
                var value = delegate.onSelect;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onSelect = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelFormattedField){
            /**
             * Component's selection event handler function.
             * @property onSelect
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.onSelect = {};
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
        if(!P.ModelFormattedField){
            /**
             * Widget's value.
             * @property jsValue
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.value = {};
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
        if(!P.ModelFormattedField){
            /**
             * <code>PopupMenu</code> that assigned for this component.
             * @property componentPopupMenu
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.componentPopupMenu = {};
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
        if(!P.ModelFormattedField){
            /**
             * Vertical coordinate of the component.
             * @property top
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.top = 0;
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
        if(!P.ModelFormattedField){
            /**
             * Component resized event handler function.
             * @property onComponentResized
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.onComponentResized = {};
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
        if(!P.ModelFormattedField){
            /**
             * Generated property jsDoc.
             * @property onFormat
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.onFormat = {};
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
        if(!P.ModelFormattedField){
            /**
             * Mouse moved event handler function.
             * @property onMouseMoved
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.onMouseMoved = {};
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
        if(!P.ModelFormattedField){
            /**
             * True if this component is completely opaque.
             * @property opaque
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.opaque = true;
        }
        Object.defineProperty(this, "editable", {
            get: function() {
                var value = delegate.editable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.editable = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelFormattedField){
            /**
             * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
             * @property editable
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.editable = true;
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
        if(!P.ModelFormattedField){
            /**
             * Generated property jsDoc.
             * @property nextFocusableComponent
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.nextFocusableComponent = {};
        }
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });
        if(!P.ModelFormattedField){
            /**
             * Native API. Returns low level swing component. Applicable only in J2SE swing client.
             * @property component
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.component = {};
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
        if(!P.ModelFormattedField){
            /**
             * Keyboard focus gained by the component event.
             * @property onFocusGained
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.onFocusGained = {};
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
        if(!P.ModelFormattedField){
            /**
             * Mouse clicked event handler function.
             * @property onMouseClicked
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.onMouseClicked = {};
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
        if(!P.ModelFormattedField){
            /**
             * Mouse exited over the component event handler function.
             * @property onMouseExited
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.onMouseExited = {};
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
        if(!P.ModelFormattedField){
            /**
             * Width of the component.
             * @property width
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.width = 0;
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
        if(!P.ModelFormattedField){
            /**
             * Generated property jsDoc.
             * @property font
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.font = {};
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
        if(!P.ModelFormattedField){
            /**
             * Key pressed event handler function.
             * @property onKeyPressed
             * @memberOf ModelFormattedField
             */
            P.ModelFormattedField.prototype.onKeyPressed = {};
        }
    };
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.focus = function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        };

        /**
         * Redraw the component.
         * @method redraw
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.redraw = function() {
            var delegate = this.unwrap();
            var value = delegate.redraw();
            return P.boxAsJs(value);
        };

})();