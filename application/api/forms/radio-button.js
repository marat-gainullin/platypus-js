(function() {
    var javaClass = Java.type("com.eas.client.forms.components.RadioButton");
    javaClass.setPublisher(function(aDelegate) {
        return new P.RadioButton(null, null, null, aDelegate);
    });
    
    /**
     * Radio button component.
     * @param text Component's text (optional)
     * @param selected <code>true</code> if component is selected (optional)
     * @param actionPerformed On action performed function (optional)
     * @constructor RadioButton RadioButton
     */
    P.RadioButton = function (text, selected, actionPerformed) {
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
        if(P.RadioButton.superclass)
            P.RadioButton.superclass.constructor.apply(this, arguments);
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
        if(!P.RadioButton){
            /**
             * The mouse <code>Cursor</code> over this component.
             * @property cursor
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.cursor = {};
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
        if(!P.RadioButton){
            /**
             * Mouse dragged event handler function.
             * @property onMouseDragged
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.onMouseDragged = {};
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
        if(!P.RadioButton){
            /**
             * Mouse released event handler function.
             * @property onMouseReleased
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.onMouseReleased = {};
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
        if(!P.RadioButton){
            /**
             * Keyboard focus lost by the component event handler function.
             * @property onFocusLost
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.onFocusLost = {};
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
        if(!P.RadioButton){
            /**
             * Value change handler.
             * @property onValueChange
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.onValueChange = {};
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
        if(!P.RadioButton){
            /**
             * The default icon.
             * @property icon
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.icon = {};
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
        if(!P.RadioButton){
            /**
             * Mouse pressed event handler function.
             * @property onMousePressed
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.onMousePressed = {};
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
        if(!P.RadioButton){
            /**
             * The foreground color of this component.
             * @property foreground
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.foreground = {};
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
        if(!P.RadioButton){
            /**
             * An error message of this component.
             * Validation procedure may set this property and subsequent focus lost event will clear it.
             * @property error
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.error = '';
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
        if(!P.RadioButton){
            /**
             * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
             * @property enabled
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.enabled = true;
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
        if(!P.RadioButton){
            /**
             * Component moved event handler function.
             * @property onComponentMoved
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.onComponentMoved = {};
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
        if(!P.RadioButton){
            /**
             * Widget's value.
             * @property jsValue
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.value = {};
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
        if(!P.RadioButton){
            /**
             * <code>PopupMenu</code> that assigned for this component.
             * @property componentPopupMenu
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.componentPopupMenu = {};
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
        if(!P.RadioButton){
            /**
             * Vertical coordinate of the component.
             * @property top
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.top = 0;
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
        if(!P.RadioButton){
            /**
             * Component resized event handler function.
             * @property onComponentResized
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.onComponentResized = {};
        }
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parentWidget;
                return P.boxAsJs(value);
            }
        });
        if(!P.RadioButton){
            /**
             * Parent container of this widget.
             * @property parentWidget
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.parent = {};
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
        if(!P.RadioButton){
            /**
             * The button's text.
             * @property text
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.text = '';
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
        if(!P.RadioButton){
            /**
             * Mouse entered over the component event handler function.
             * @property onMouseEntered
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.onMouseEntered = {};
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
        if(!P.RadioButton){
            /**
             * The tooltip string that has been set with.
             * @property toolTipText
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.toolTipText = '';
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
        if(!P.RadioButton){
            /**
             * The state of the button.
             * @property selected
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.selected = true;
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
        if(!P.RadioButton){
            /**
             * Height of the component.
             * @property height
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.height = 0;
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
        if(!P.RadioButton){
            /**
             * Component shown event handler function.
             * @property onComponentShown
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.onComponentShown = {};
        }
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });
        if(!P.RadioButton){
            /**
             * Native API. Returns low level html element. Applicable only in HTML5 client.
             * @property element
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.element = {};
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
        if(!P.RadioButton){
            /**
             * Mouse moved event handler function.
             * @property onMouseMoved
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.onMouseMoved = {};
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
        if(!P.RadioButton){
            /**
             * True if this component is completely opaque.
             * @property opaque
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.opaque = true;
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
        if(!P.RadioButton){
            /**
             * Determines whether this component should be visible when its parent is visible.
             * @property visible
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.visible = true;
        }
        Object.defineProperty(this, "buttonGroup", {
            get: function() {
                var value = delegate.buttonGroup;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.buttonGroup = P.boxAsJava(aValue);
            }
        });
        if(!P.RadioButton){
            /**
             * The ButtonGroup this component belongs to.
             * @property buttonGroup
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.buttonGroup = {};
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
        if(!P.RadioButton){
            /**
             * Component hidden event handler function.
             * @property onComponentHidden
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.onComponentHidden = {};
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
        if(!P.RadioButton){
            /**
             * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
             * @property nextFocusableComponent
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.nextFocusableComponent = {};
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
        if(!P.RadioButton){
            /**
             * Main action performed event handler function.
             * @property onActionPerformed
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.onActionPerformed = {};
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
        if(!P.RadioButton){
            /**
             * Key released event handler function.
             * @property onKeyReleased
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.onKeyReleased = {};
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
        if(!P.RadioButton){
            /**
             * Determines whether this component may be focused.
             * @property focusable
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.focusable = true;
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
        if(!P.RadioButton){
            /**
             * Key typed event handler function.
             * @property onKeyTyped
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.onKeyTyped = {};
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
        if(!P.RadioButton){
            /**
             * Mouse wheel moved event handler function.
             * @property onMouseWheelMoved
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.onMouseWheelMoved = {};
        }
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });
        if(!P.RadioButton){
            /**
             * Native API. Returns low level swing component. Applicable only in J2SE swing client.
             * @property component
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.component = {};
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
        if(!P.RadioButton){
            /**
             * Keyboard focus gained by the component event.
             * @property onFocusGained
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.onFocusGained = {};
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
        if(!P.RadioButton){
            /**
             * Horizontal coordinate of the component.
             * @property left
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.left = 0;
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
        if(!P.RadioButton){
            /**
             * The background color of this component.
             * @property background
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.background = {};
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
        if(!P.RadioButton){
            /**
             * Mouse clicked event handler function.
             * @property onMouseClicked
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.onMouseClicked = {};
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
        if(!P.RadioButton){
            /**
             * Mouse exited over the component event handler function.
             * @property onMouseExited
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.onMouseExited = {};
        }
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.RadioButton){
            /**
             * Gets name of this component.
             * @property name
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.name = '';
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
        if(!P.RadioButton){
            /**
             * Width of the component.
             * @property width
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.width = 0;
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
        if(!P.RadioButton){
            /**
             * The font of this component.
             * @property font
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.font = {};
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
        if(!P.RadioButton){
            /**
             * Key pressed event handler function.
             * @property onKeyPressed
             * @memberOf RadioButton
             */
            P.RadioButton.prototype.onKeyPressed = {};
        }
    };
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf RadioButton
         */
        P.RadioButton.prototype.focus = function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        };

})();