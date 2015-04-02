(function() {
    var javaClass = Java.type("com.eas.client.forms.components.model.ModelDate");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ModelDate(aDelegate);
    });
    
    /**
     * A model component that shows a date.
     * @constructor ModelDate ModelDate
     */
    P.ModelDate = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.ModelDate.superclass)
            P.ModelDate.superclass.constructor.apply(this, arguments);
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
        if(!P.ModelDate){
            /**
             * Generated property jsDoc.
             * @property selectOnly
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.selectOnly = true;
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
        if(!P.ModelDate){
            /**
             * Mouse released event handler function.
             * @property onMouseReleased
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.onMouseReleased = {};
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
        if(!P.ModelDate){
            /**
             * Object, bound to the widget.
             * @property data
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.data = {};
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
        if(!P.ModelDate){
            /**
             * Generated property jsDoc.
             * @property nullable
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.nullable = true;
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
        if(!P.ModelDate){
            /**
             * Value change handler.
             * @property onValueChange
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.onValueChange = {};
        }
        Object.defineProperty(this, "dateFormat", {
            get: function() {
                var value = delegate.dateFormat;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.dateFormat = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelDate){
            /**
             * Generated property jsDoc.
             * @property dateFormat
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.dateFormat = '';
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
        if(!P.ModelDate){
            /**
             * The foreground color of this component.
             * @property foreground
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.foreground = {};
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
        if(!P.ModelDate){
            /**
             * Component moved event handler function.
             * @property onComponentMoved
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.onComponentMoved = {};
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
        if(!P.ModelDate){
            /**
             * Component's rendering event handler function.
             * @property onRender
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.onRender = {};
        }
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parentWidget;
                return P.boxAsJs(value);
            }
        });
        if(!P.ModelDate){
            /**
             * Parent container of this widget.
             * @property parentWidget
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.parent = {};
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
        if(!P.ModelDate){
            /**
             * Generated property jsDoc.
             * @property text
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.text = '';
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
        if(!P.ModelDate){
            /**
             * Mouse entered over the component event handler function.
             * @property onMouseEntered
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.onMouseEntered = {};
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
        if(!P.ModelDate){
            /**
             * The tooltip string that has been set with.
             * @property toolTipText
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.toolTipText = '';
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
        if(!P.ModelDate){
            /**
             * Height of the component.
             * @property height
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.height = 0;
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
        if(!P.ModelDate){
            /**
             * Component shown event handler function.
             * @property onComponentShown
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.onComponentShown = {};
        }
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });
        if(!P.ModelDate){
            /**
             * Native API. Returns low level html element. Applicable only in HTML5 client.
             * @property element
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.element = {};
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
        if(!P.ModelDate){
            /**
             * Determines whether this component should be visible when its parent is visible.
             * @property visible
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.visible = true;
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
        if(!P.ModelDate){
            /**
             * Component hidden event handler function.
             * @property onComponentHidden
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.onComponentHidden = {};
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
        if(!P.ModelDate){
            /**
             * Main action performed event handler function.
             * @property onActionPerformed
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.onActionPerformed = {};
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
        if(!P.ModelDate){
            /**
             * Key released event handler function.
             * @property onKeyReleased
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.onKeyReleased = {};
        }
        Object.defineProperty(this, "datePicker", {
            get: function() {
                var value = delegate.datePicker;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.datePicker = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelDate){
            /**
             * Generated property jsDoc.
             * @property datePicker
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.datePicker = true;
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
        if(!P.ModelDate){
            /**
             * Determines whether this component may be focused.
             * @property focusable
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.focusable = true;
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
        if(!P.ModelDate){
            /**
             * Key typed event handler function.
             * @property onKeyTyped
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.onKeyTyped = {};
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
        if(!P.ModelDate){
            /**
             * Mouse wheel moved event handler function.
             * @property onMouseWheelMoved
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.onMouseWheelMoved = {};
        }
        Object.defineProperty(this, "timePicker", {
            get: function() {
                var value = delegate.timePicker;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.timePicker = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelDate){
            /**
             * Generated property jsDoc.
             * @property timePicker
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.timePicker = true;
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
        if(!P.ModelDate){
            /**
             * Model binding field.
             * @property field
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.field = '';
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
        if(!P.ModelDate){
            /**
             * Horizontal coordinate of the component.
             * @property left
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.left = 0;
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
        if(!P.ModelDate){
            /**
             * The background color of this component.
             * @property background
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.background = {};
        }
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.ModelDate){
            /**
             * Generated property jsDoc.
             * @property name
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.name = '';
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
        if(!P.ModelDate){
            /**
             * Generated property jsDoc.
             * @property cursor
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.cursor = {};
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
        if(!P.ModelDate){
            /**
             * Mouse dragged event handler function.
             * @property onMouseDragged
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.onMouseDragged = {};
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
        if(!P.ModelDate){
            /**
             * Keyboard focus lost by the component event handler function.
             * @property onFocusLost
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.onFocusLost = {};
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
        if(!P.ModelDate){
            /**
             * Generated property jsDoc.
             * @property emptyText
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.emptyText = '';
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
        if(!P.ModelDate){
            /**
             * Generated property jsDoc.
             * @property icon
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.icon = {};
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
        if(!P.ModelDate){
            /**
             * Mouse pressed event handler function.
             * @property onMousePressed
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.onMousePressed = {};
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
        if(!P.ModelDate){
            /**
             * An error message of this component.
             * Validation procedure may set this property and subsequent focus lost event will clear it.
             * @property error
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.error = '';
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
        if(!P.ModelDate){
            /**
             * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
             * @property enabled
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.enabled = true;
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
        if(!P.ModelDate){
            /**
             * Component's selection event handler function.
             * @property onSelect
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.onSelect = {};
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
        if(!P.ModelDate){
            /**
             * Widget's value.
             * @property jsValue
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.value = {};
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
        if(!P.ModelDate){
            /**
             * <code>PopupMenu</code> that assigned for this component.
             * @property componentPopupMenu
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.componentPopupMenu = {};
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
        if(!P.ModelDate){
            /**
             * Vertical coordinate of the component.
             * @property top
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.top = 0;
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
        if(!P.ModelDate){
            /**
             * Component resized event handler function.
             * @property onComponentResized
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.onComponentResized = {};
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
        if(!P.ModelDate){
            /**
             * Mouse moved event handler function.
             * @property onMouseMoved
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.onMouseMoved = {};
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
        if(!P.ModelDate){
            /**
             * True if this component is completely opaque.
             * @property opaque
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.opaque = true;
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
        if(!P.ModelDate){
            /**
             * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
             * @property editable
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.editable = true;
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
        if(!P.ModelDate){
            /**
             * Generated property jsDoc.
             * @property nextFocusableComponent
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.nextFocusableComponent = {};
        }
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });
        if(!P.ModelDate){
            /**
             * Native API. Returns low level swing component. Applicable only in J2SE swing client.
             * @property component
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.component = {};
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
        if(!P.ModelDate){
            /**
             * Keyboard focus gained by the component event.
             * @property onFocusGained
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.onFocusGained = {};
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
        if(!P.ModelDate){
            /**
             * Mouse clicked event handler function.
             * @property onMouseClicked
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.onMouseClicked = {};
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
        if(!P.ModelDate){
            /**
             * Mouse exited over the component event handler function.
             * @property onMouseExited
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.onMouseExited = {};
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
        if(!P.ModelDate){
            /**
             * Width of the component.
             * @property width
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.width = 0;
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
        if(!P.ModelDate){
            /**
             * Generated property jsDoc.
             * @property font
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.font = {};
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
        if(!P.ModelDate){
            /**
             * Key pressed event handler function.
             * @property onKeyPressed
             * @memberOf ModelDate
             */
            P.ModelDate.prototype.onKeyPressed = {};
        }
    };
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.focus = function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        };

        /**
         * Redraw the component.
         * @method redraw
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.redraw = function() {
            var delegate = this.unwrap();
            var value = delegate.redraw();
            return P.boxAsJs(value);
        };

})();