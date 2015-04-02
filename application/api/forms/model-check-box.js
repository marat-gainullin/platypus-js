(function() {
    var javaClass = Java.type("com.eas.client.forms.components.model.ModelCheckBox");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ModelCheckBox(null, aDelegate);
    });
    
    /**
     * An implementation of a model check box -- an item that can be selected or deselected, and which displays its state to the user.
     * @param text the text of the component (optional).
     * @constructor ModelCheckBox ModelCheckBox
     */
    P.ModelCheckBox = function (text) {
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
        if(P.ModelCheckBox.superclass)
            P.ModelCheckBox.superclass.constructor.apply(this, arguments);
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
        if(!P.ModelCheckBox){
            /**
             * Generated property jsDoc.
             * @property cursor
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.cursor = {};
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
        if(!P.ModelCheckBox){
            /**
             * Mouse dragged event handler function.
             * @property onMouseDragged
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.onMouseDragged = {};
        }
        Object.defineProperty(this, "selectOnly", {
            get: function() {
                var value = delegate.selectOnly;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.selectOnly = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelCheckBox){
            /**
             * Generated property jsDoc.
             * @property selectOnly
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.selectOnly = true;
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
        if(!P.ModelCheckBox){
            /**
             * Mouse released event handler function.
             * @property onMouseReleased
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.onMouseReleased = {};
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
        if(!P.ModelCheckBox){
            /**
             * Object, bound to the widget.
             * @property data
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.data = {};
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
        if(!P.ModelCheckBox){
            /**
             * Generated property jsDoc.
             * @property nullable
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.nullable = true;
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
        if(!P.ModelCheckBox){
            /**
             * Keyboard focus lost by the component event handler function.
             * @property onFocusLost
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.onFocusLost = {};
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
        if(!P.ModelCheckBox){
            /**
             * Value change handler.
             * @property onValueChange
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.onValueChange = {};
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
        if(!P.ModelCheckBox){
            /**
             * Generated property jsDoc.
             * @property icon
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.icon = {};
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
        if(!P.ModelCheckBox){
            /**
             * Mouse pressed event handler function.
             * @property onMousePressed
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.onMousePressed = {};
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
        if(!P.ModelCheckBox){
            /**
             * The foreground color of this component.
             * @property foreground
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.foreground = {};
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
        if(!P.ModelCheckBox){
            /**
             * An error message of this component.
             * Validation procedure may set this property and subsequent focus lost event will clear it.
             * @property error
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.error = '';
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
        if(!P.ModelCheckBox){
            /**
             * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
             * @property enabled
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.enabled = true;
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
        if(!P.ModelCheckBox){
            /**
             * Component moved event handler function.
             * @property onComponentMoved
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.onComponentMoved = {};
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
        if(!P.ModelCheckBox){
            /**
             * Component's selection event handler function.
             * @property onSelect
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.onSelect = {};
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
        if(!P.ModelCheckBox){
            /**
             * Widget's value.
             * @property jsValue
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.value = {};
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
        if(!P.ModelCheckBox){
            /**
             * <code>PopupMenu</code> that assigned for this component.
             * @property componentPopupMenu
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.componentPopupMenu = {};
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
        if(!P.ModelCheckBox){
            /**
             * Vertical coordinate of the component.
             * @property top
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.top = 0;
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
        if(!P.ModelCheckBox){
            /**
             * Component's rendering event handler function.
             * @property onRender
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.onRender = {};
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
        if(!P.ModelCheckBox){
            /**
             * Component resized event handler function.
             * @property onComponentResized
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.onComponentResized = {};
        }
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parentWidget;
                return P.boxAsJs(value);
            }
        });
        if(!P.ModelCheckBox){
            /**
             * Parent container of this widget.
             * @property parentWidget
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.parent = {};
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
        if(!P.ModelCheckBox){
            /**
             * @property text
             * @memberOf ModelCheckBox
             * Text on the check box.*/
            P.ModelCheckBox.prototype.text = '';
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
        if(!P.ModelCheckBox){
            /**
             * Mouse entered over the component event handler function.
             * @property onMouseEntered
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.onMouseEntered = {};
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
        if(!P.ModelCheckBox){
            /**
             * The tooltip string that has been set with.
             * @property toolTipText
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.toolTipText = '';
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
        if(!P.ModelCheckBox){
            /**
             * Height of the component.
             * @property height
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.height = 0;
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
        if(!P.ModelCheckBox){
            /**
             * Component shown event handler function.
             * @property onComponentShown
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.onComponentShown = {};
        }
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });
        if(!P.ModelCheckBox){
            /**
             * Native API. Returns low level html element. Applicable only in HTML5 client.
             * @property element
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.element = {};
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
        if(!P.ModelCheckBox){
            /**
             * Mouse moved event handler function.
             * @property onMouseMoved
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.onMouseMoved = {};
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
        if(!P.ModelCheckBox){
            /**
             * True if this component is completely opaque.
             * @property opaque
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.opaque = true;
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
        if(!P.ModelCheckBox){
            /**
             * Determines whether this component should be visible when its parent is visible.
             * @property visible
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.visible = true;
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
        if(!P.ModelCheckBox){
            /**
             * Component hidden event handler function.
             * @property onComponentHidden
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.onComponentHidden = {};
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
        if(!P.ModelCheckBox){
            /**
             * Generated property jsDoc.
             * @property nextFocusableComponent
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.nextFocusableComponent = {};
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
        if(!P.ModelCheckBox){
            /**
             * Main action performed event handler function.
             * @property onActionPerformed
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.onActionPerformed = {};
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
        if(!P.ModelCheckBox){
            /**
             * Key released event handler function.
             * @property onKeyReleased
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.onKeyReleased = {};
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
        if(!P.ModelCheckBox){
            /**
             * Determines whether this component may be focused.
             * @property focusable
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.focusable = true;
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
        if(!P.ModelCheckBox){
            /**
             * Key typed event handler function.
             * @property onKeyTyped
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.onKeyTyped = {};
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
        if(!P.ModelCheckBox){
            /**
             * Mouse wheel moved event handler function.
             * @property onMouseWheelMoved
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.onMouseWheelMoved = {};
        }
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });
        if(!P.ModelCheckBox){
            /**
             * Native API. Returns low level swing component. Applicable only in J2SE swing client.
             * @property component
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.component = {};
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
        if(!P.ModelCheckBox){
            /**
             * Model binding field.
             * @property field
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.field = '';
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
        if(!P.ModelCheckBox){
            /**
             * Keyboard focus gained by the component event.
             * @property onFocusGained
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.onFocusGained = {};
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
        if(!P.ModelCheckBox){
            /**
             * Horizontal coordinate of the component.
             * @property left
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.left = 0;
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
        if(!P.ModelCheckBox){
            /**
             * The background color of this component.
             * @property background
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.background = {};
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
        if(!P.ModelCheckBox){
            /**
             * Mouse clicked event handler function.
             * @property onMouseClicked
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.onMouseClicked = {};
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
        if(!P.ModelCheckBox){
            /**
             * Mouse exited over the component event handler function.
             * @property onMouseExited
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.onMouseExited = {};
        }
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.ModelCheckBox){
            /**
             * Generated property jsDoc.
             * @property name
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.name = '';
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
        if(!P.ModelCheckBox){
            /**
             * Width of the component.
             * @property width
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.width = 0;
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
        if(!P.ModelCheckBox){
            /**
             * Generated property jsDoc.
             * @property font
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.font = {};
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
        if(!P.ModelCheckBox){
            /**
             * Key pressed event handler function.
             * @property onKeyPressed
             * @memberOf ModelCheckBox
             */
            P.ModelCheckBox.prototype.onKeyPressed = {};
        }
    };
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.focus = function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        };

        /**
         * Redraw the component.
         * @method redraw
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.redraw = function() {
            var delegate = this.unwrap();
            var value = delegate.redraw();
            return P.boxAsJs(value);
        };

})();