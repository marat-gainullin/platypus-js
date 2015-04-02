(function() {
    var javaClass = Java.type("com.eas.client.forms.components.model.ModelCombo");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ModelCombo(aDelegate);
    });
    
    /**
     * A model component that combines a button or editable field and a drop-down list.
     * @constructor ModelCombo ModelCombo
     */
    P.ModelCombo = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.ModelCombo.superclass)
            P.ModelCombo.superclass.constructor.apply(this, arguments);
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
        if(!P.ModelCombo){
            /**
             * Generated property jsDoc.
             * @property selectOnly
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.selectOnly = true;
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
        if(!P.ModelCombo){
            /**
             * Mouse released event handler function.
             * @property onMouseReleased
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.onMouseReleased = {};
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
        if(!P.ModelCombo){
            /**
             * Object, bound to the widget.
             * @property data
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.data = {};
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
        if(!P.ModelCombo){
            /**
             * Generated property jsDoc.
             * @property nullable
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.nullable = true;
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
        if(!P.ModelCombo){
            /**
             * Value change handler.
             * @property onValueChange
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.onValueChange = {};
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
        if(!P.ModelCombo){
            /**
             * The foreground color of this component.
             * @property foreground
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.foreground = {};
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
        if(!P.ModelCombo){
            /**
             * Component moved event handler function.
             * @property onComponentMoved
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.onComponentMoved = {};
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
        if(!P.ModelCombo){
            /**
             * Component's rendering event handler function.
             * @property onRender
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.onRender = {};
        }
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parentWidget;
                return P.boxAsJs(value);
            }
        });
        if(!P.ModelCombo){
            /**
             * Parent container of this widget.
             * @property parentWidget
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.parent = {};
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
        if(!P.ModelCombo){
            /**
             * Generated property jsDoc.
             * @property text
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.text = '';
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
        if(!P.ModelCombo){
            /**
             * Mouse entered over the component event handler function.
             * @property onMouseEntered
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.onMouseEntered = {};
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
        if(!P.ModelCombo){
            /**
             * The tooltip string that has been set with.
             * @property toolTipText
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.toolTipText = '';
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
        if(!P.ModelCombo){
            /**
             * Height of the component.
             * @property height
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.height = 0;
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
        if(!P.ModelCombo){
            /**
             * Component shown event handler function.
             * @property onComponentShown
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.onComponentShown = {};
        }
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });
        if(!P.ModelCombo){
            /**
             * Native API. Returns low level html element. Applicable only in HTML5 client.
             * @property element
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.element = {};
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
        if(!P.ModelCombo){
            /**
             * Determines whether this component should be visible when its parent is visible.
             * @property visible
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.visible = true;
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
        if(!P.ModelCombo){
            /**
             * Component hidden event handler function.
             * @property onComponentHidden
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.onComponentHidden = {};
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
        if(!P.ModelCombo){
            /**
             * Main action performed event handler function.
             * @property onActionPerformed
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.onActionPerformed = {};
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
        if(!P.ModelCombo){
            /**
             * Key released event handler function.
             * @property onKeyReleased
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.onKeyReleased = {};
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
        if(!P.ModelCombo){
            /**
             * Determines whether this component may be focused.
             * @property focusable
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.focusable = true;
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
        if(!P.ModelCombo){
            /**
             * Key typed event handler function.
             * @property onKeyTyped
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.onKeyTyped = {};
        }
        Object.defineProperty(this, "displayList", {
            get: function() {
                var value = delegate.displayList;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.displayList = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelCombo){
            /**
             * List of displayed options in a dropdown list of the component.
             * @property displayList
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.displayList = {};
        }
        Object.defineProperty(this, "list", {
            get: function() {
                var value = delegate.list;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.list = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelCombo){
            /**
             * Determines if component shown as a list.
             * @property list
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.list = true;
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
        if(!P.ModelCombo){
            /**
             * Mouse wheel moved event handler function.
             * @property onMouseWheelMoved
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.onMouseWheelMoved = {};
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
        if(!P.ModelCombo){
            /**
             * Model binding field.
             * @property field
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.field = '';
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
        if(!P.ModelCombo){
            /**
             * Horizontal coordinate of the component.
             * @property left
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.left = 0;
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
        if(!P.ModelCombo){
            /**
             * The background color of this component.
             * @property background
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.background = {};
        }
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.ModelCombo){
            /**
             * Generated property jsDoc.
             * @property name
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.name = '';
        }
        Object.defineProperty(this, "displayField", {
            get: function() {
                var value = delegate.displayField;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.displayField = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelCombo){
            /**
             * Display field of the component.
             * @property displayField
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.displayField = '';
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
        if(!P.ModelCombo){
            /**
             * Generated property jsDoc.
             * @property cursor
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.cursor = {};
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
        if(!P.ModelCombo){
            /**
             * Mouse dragged event handler function.
             * @property onMouseDragged
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.onMouseDragged = {};
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
        if(!P.ModelCombo){
            /**
             * Keyboard focus lost by the component event handler function.
             * @property onFocusLost
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.onFocusLost = {};
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
        if(!P.ModelCombo){
            /**
             * Generated property jsDoc.
             * @property emptyText
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.emptyText = '';
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
        if(!P.ModelCombo){
            /**
             * Generated property jsDoc.
             * @property icon
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.icon = {};
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
        if(!P.ModelCombo){
            /**
             * Mouse pressed event handler function.
             * @property onMousePressed
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.onMousePressed = {};
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
        if(!P.ModelCombo){
            /**
             * An error message of this component.
             * Validation procedure may set this property and subsequent focus lost event will clear it.
             * @property error
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.error = '';
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
        if(!P.ModelCombo){
            /**
             * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
             * @property enabled
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.enabled = true;
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
        if(!P.ModelCombo){
            /**
             * Component's selection event handler function.
             * @property onSelect
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.onSelect = {};
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
        if(!P.ModelCombo){
            /**
             * Widget's value.
             * @property jsValue
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.value = {};
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
        if(!P.ModelCombo){
            /**
             * <code>PopupMenu</code> that assigned for this component.
             * @property componentPopupMenu
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.componentPopupMenu = {};
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
        if(!P.ModelCombo){
            /**
             * Vertical coordinate of the component.
             * @property top
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.top = 0;
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
        if(!P.ModelCombo){
            /**
             * Component resized event handler function.
             * @property onComponentResized
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.onComponentResized = {};
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
        if(!P.ModelCombo){
            /**
             * Mouse moved event handler function.
             * @property onMouseMoved
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.onMouseMoved = {};
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
        if(!P.ModelCombo){
            /**
             * True if this component is completely opaque.
             * @property opaque
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.opaque = true;
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
        if(!P.ModelCombo){
            /**
             * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
             * @property editable
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.editable = true;
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
        if(!P.ModelCombo){
            /**
             * Generated property jsDoc.
             * @property nextFocusableComponent
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.nextFocusableComponent = {};
        }
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });
        if(!P.ModelCombo){
            /**
             * Native API. Returns low level swing component. Applicable only in J2SE swing client.
             * @property component
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.component = {};
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
        if(!P.ModelCombo){
            /**
             * Keyboard focus gained by the component event.
             * @property onFocusGained
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.onFocusGained = {};
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
        if(!P.ModelCombo){
            /**
             * Mouse clicked event handler function.
             * @property onMouseClicked
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.onMouseClicked = {};
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
        if(!P.ModelCombo){
            /**
             * Mouse exited over the component event handler function.
             * @property onMouseExited
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.onMouseExited = {};
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
        if(!P.ModelCombo){
            /**
             * Width of the component.
             * @property width
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.width = 0;
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
        if(!P.ModelCombo){
            /**
             * Generated property jsDoc.
             * @property font
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.font = {};
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
        if(!P.ModelCombo){
            /**
             * Key pressed event handler function.
             * @property onKeyPressed
             * @memberOf ModelCombo
             */
            P.ModelCombo.prototype.onKeyPressed = {};
        }
    };
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.focus = function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        };

        /**
         * Redraw the component.
         * @method redraw
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.redraw = function() {
            var delegate = this.unwrap();
            var value = delegate.redraw();
            return P.boxAsJs(value);
        };

})();