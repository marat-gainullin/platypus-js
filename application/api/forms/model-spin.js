(function() {
    var javaClass = Java.type("com.eas.client.forms.components.model.ModelSpin");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ModelSpin(aDelegate);
    });
    
    /**
     * A model component that represents a combination of a numeric text box and arrow buttons to change the value incrementally.
     * @constructor ModelSpin ModelSpin
     */
    P.ModelSpin = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.ModelSpin.superclass)
            P.ModelSpin.superclass.constructor.apply(this, arguments);
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
        if(!P.ModelSpin){
            /**
             * Generated property jsDoc.
             * @property selectOnly
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.selectOnly = true;
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
        if(!P.ModelSpin){
            /**
             * Mouse released event handler function.
             * @property onMouseReleased
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.onMouseReleased = {};
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
        if(!P.ModelSpin){
            /**
             * Object, bound to the widget.
             * @property data
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.data = {};
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
        if(!P.ModelSpin){
            /**
             * Generated property jsDoc.
             * @property nullable
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.nullable = true;
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
        if(!P.ModelSpin){
            /**
             * Value change handler.
             * @property onValueChange
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.onValueChange = {};
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
        if(!P.ModelSpin){
            /**
             * The foreground color of this component.
             * @property foreground
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.foreground = {};
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
        if(!P.ModelSpin){
            /**
             * Component moved event handler function.
             * @property onComponentMoved
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.onComponentMoved = {};
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
        if(!P.ModelSpin){
            /**
             * Component's rendering event handler function.
             * @property onRender
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.onRender = {};
        }
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parentWidget;
                return P.boxAsJs(value);
            }
        });
        if(!P.ModelSpin){
            /**
             * Parent container of this widget.
             * @property parentWidget
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.parent = {};
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
        if(!P.ModelSpin){
            /**
             * Generated property jsDoc.
             * @property text
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.text = '';
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
        if(!P.ModelSpin){
            /**
             * Mouse entered over the component event handler function.
             * @property onMouseEntered
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.onMouseEntered = {};
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
        if(!P.ModelSpin){
            /**
             * The tooltip string that has been set with.
             * @property toolTipText
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.toolTipText = '';
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
        if(!P.ModelSpin){
            /**
             * Height of the component.
             * @property height
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.height = 0;
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
        if(!P.ModelSpin){
            /**
             * Component shown event handler function.
             * @property onComponentShown
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.onComponentShown = {};
        }
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });
        if(!P.ModelSpin){
            /**
             * Native API. Returns low level html element. Applicable only in HTML5 client.
             * @property element
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.element = {};
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
        if(!P.ModelSpin){
            /**
             * Determines whether this component should be visible when its parent is visible.
             * @property visible
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.visible = true;
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
        if(!P.ModelSpin){
            /**
             * Component hidden event handler function.
             * @property onComponentHidden
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.onComponentHidden = {};
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
        if(!P.ModelSpin){
            /**
             * Main action performed event handler function.
             * @property onActionPerformed
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.onActionPerformed = {};
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
        if(!P.ModelSpin){
            /**
             * Key released event handler function.
             * @property onKeyReleased
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.onKeyReleased = {};
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
        if(!P.ModelSpin){
            /**
             * Determines whether this component may be focused.
             * @property focusable
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.focusable = true;
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
        if(!P.ModelSpin){
            /**
             * Key typed event handler function.
             * @property onKeyTyped
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.onKeyTyped = {};
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
        if(!P.ModelSpin){
            /**
             * Mouse wheel moved event handler function.
             * @property onMouseWheelMoved
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.onMouseWheelMoved = {};
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
        if(!P.ModelSpin){
            /**
             * Model binding field.
             * @property field
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.field = '';
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
        if(!P.ModelSpin){
            /**
             * Horizontal coordinate of the component.
             * @property left
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.left = 0;
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
        if(!P.ModelSpin){
            /**
             * The background color of this component.
             * @property background
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.background = {};
        }
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.ModelSpin){
            /**
             * Generated property jsDoc.
             * @property name
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.name = '';
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
        if(!P.ModelSpin){
            /**
             * Generated property jsDoc.
             * @property cursor
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.cursor = {};
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
        if(!P.ModelSpin){
            /**
             * Mouse dragged event handler function.
             * @property onMouseDragged
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.onMouseDragged = {};
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
        if(!P.ModelSpin){
            /**
             * Keyboard focus lost by the component event handler function.
             * @property onFocusLost
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.onFocusLost = {};
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
        if(!P.ModelSpin){
            /**
             * Generated property jsDoc.
             * @property emptyText
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.emptyText = '';
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
        if(!P.ModelSpin){
            /**
             * Generated property jsDoc.
             * @property icon
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.icon = {};
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
        if(!P.ModelSpin){
            /**
             * Mouse pressed event handler function.
             * @property onMousePressed
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.onMousePressed = {};
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
        if(!P.ModelSpin){
            /**
             * An error message of this component.
             * Validation procedure may set this property and subsequent focus lost event will clear it.
             * @property error
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.error = '';
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
        if(!P.ModelSpin){
            /**
             * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
             * @property enabled
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.enabled = true;
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
        if(!P.ModelSpin){
            /**
             * Component's selection event handler function.
             * @property onSelect
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.onSelect = {};
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
        if(!P.ModelSpin){
            /**
             * Widget's value.
             * @property jsValue
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.value = {};
        }
        Object.defineProperty(this, "min", {
            get: function() {
                var value = delegate.min;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.min = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelSpin){
            /**
             * Determines the lower bound of spinner's value. If it's null, valus is unlimited at lower bound.
             * @property min
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.min = 0;
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
        if(!P.ModelSpin){
            /**
             * <code>PopupMenu</code> that assigned for this component.
             * @property componentPopupMenu
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.componentPopupMenu = {};
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
        if(!P.ModelSpin){
            /**
             * Vertical coordinate of the component.
             * @property top
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.top = 0;
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
        if(!P.ModelSpin){
            /**
             * Component resized event handler function.
             * @property onComponentResized
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.onComponentResized = {};
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
        if(!P.ModelSpin){
            /**
             * Mouse moved event handler function.
             * @property onMouseMoved
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.onMouseMoved = {};
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
        if(!P.ModelSpin){
            /**
             * True if this component is completely opaque.
             * @property opaque
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.opaque = true;
        }
        Object.defineProperty(this, "max", {
            get: function() {
                var value = delegate.max;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.max = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelSpin){
            /**
             * Determines the upper bound of spinner's value. If it's null, valus is unlimited at upper bound.
             * @property max
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.max = 0;
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
        if(!P.ModelSpin){
            /**
             * Determines if component is editable.
             * @property editable
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.editable = true;
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
        if(!P.ModelSpin){
            /**
             * Generated property jsDoc.
             * @property nextFocusableComponent
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.nextFocusableComponent = {};
        }
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });
        if(!P.ModelSpin){
            /**
             * Native API. Returns low level swing component. Applicable only in J2SE swing client.
             * @property component
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.component = {};
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
        if(!P.ModelSpin){
            /**
             * Keyboard focus gained by the component event.
             * @property onFocusGained
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.onFocusGained = {};
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
        if(!P.ModelSpin){
            /**
             * Mouse clicked event handler function.
             * @property onMouseClicked
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.onMouseClicked = {};
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
        if(!P.ModelSpin){
            /**
             * Mouse exited over the component event handler function.
             * @property onMouseExited
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.onMouseExited = {};
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
        if(!P.ModelSpin){
            /**
             * Width of the component.
             * @property width
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.width = 0;
        }
        Object.defineProperty(this, "step", {
            get: function() {
                var value = delegate.step;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.step = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelSpin){
            /**
             * Determines the spinner's value change step. Can't be null.
             * @property step
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.step = 0;
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
        if(!P.ModelSpin){
            /**
             * Generated property jsDoc.
             * @property font
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.font = {};
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
        if(!P.ModelSpin){
            /**
             * Key pressed event handler function.
             * @property onKeyPressed
             * @memberOf ModelSpin
             */
            P.ModelSpin.prototype.onKeyPressed = {};
        }
    };
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.focus = function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        };

        /**
         * Redraw the component.
         * @method redraw
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.redraw = function() {
            var delegate = this.unwrap();
            var value = delegate.redraw();
            return P.boxAsJs(value);
        };

})();