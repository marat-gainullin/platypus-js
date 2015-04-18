(function() {
    var javaClass = Java.type("com.eas.client.forms.components.ToggleButton");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ToggleButton(null, null, null, null, null, aDelegate);
    });
    
    /**
     * Toggle button component.
     * @param text the text for the component (optional)
     * @param icon the icon for the component (optional)
     * @param selected the selected state of the button (optional)
     * @param iconTextGap the text gap (optional)
     * @param actionPerformed the function for the action performed handler (optional)
     * @constructor ToggleButton ToggleButton
     */
    P.ToggleButton = function (text, icon, selected, iconTextGap, actionPerformed) {
        var maxArgs = 5;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 5 ? new javaClass(P.boxAsJava(text), P.boxAsJava(icon), P.boxAsJava(selected), P.boxAsJava(iconTextGap), P.boxAsJava(actionPerformed))
            : arguments.length === 4 ? new javaClass(P.boxAsJava(text), P.boxAsJava(icon), P.boxAsJava(selected), P.boxAsJava(iconTextGap))
            : arguments.length === 3 ? new javaClass(P.boxAsJava(text), P.boxAsJava(icon), P.boxAsJava(selected))
            : arguments.length === 2 ? new javaClass(P.boxAsJava(text), P.boxAsJava(icon))
            : arguments.length === 1 ? new javaClass(P.boxAsJava(text))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.ToggleButton.superclass)
            P.ToggleButton.superclass.constructor.apply(this, arguments);
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
        if(!P.ToggleButton){
            /**
             * The mouse <code>Cursor</code> over this component.
             * @property cursor
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.cursor = {};
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
        if(!P.ToggleButton){
            /**
             * Mouse dragged event handler function.
             * @property onMouseDragged
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.onMouseDragged = {};
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
        if(!P.ToggleButton){
            /**
             * Mouse released event handler function.
             * @property onMouseReleased
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.onMouseReleased = {};
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
        if(!P.ToggleButton){
            /**
             * Keyboard focus lost by the component event handler function.
             * @property onFocusLost
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.onFocusLost = {};
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
        if(!P.ToggleButton){
            /**
             * Value change handler.
             * @property onValueChange
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.onValueChange = {};
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
        if(!P.ToggleButton){
            /**
             * Image picture for the button.
             * @property icon
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.icon = {};
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
        if(!P.ToggleButton){
            /**
             * Mouse pressed event handler function.
             * @property onMousePressed
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.onMousePressed = {};
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
        if(!P.ToggleButton){
            /**
             * The foreground color of this component.
             * @property foreground
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.foreground = {};
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
        if(!P.ToggleButton){
            /**
             * An error message of this component.
             * Validation procedure may set this property and subsequent focus lost event will clear it.
             * @property error
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.error = '';
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
        if(!P.ToggleButton){
            /**
             * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
             * @property enabled
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.enabled = true;
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
        if(!P.ToggleButton){
            /**
             * Component moved event handler function.
             * @property onComponentMoved
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.onComponentMoved = {};
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
        if(!P.ToggleButton){
            /**
             * Widget's value.
             * @property jsValue
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.value = {};
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
        if(!P.ToggleButton){
            /**
             * <code>PopupMenu</code> that assigned for this component.
             * @property componentPopupMenu
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.componentPopupMenu = {};
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
        if(!P.ToggleButton){
            /**
             * Vertical coordinate of the component.
             * @property top
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.top = 0;
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
        if(!P.ToggleButton){
            /**
             * Component resized event handler function.
             * @property onComponentResized
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.onComponentResized = {};
        }
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parentWidget;
                return P.boxAsJs(value);
            }
        });
        if(!P.ToggleButton){
            /**
             * Parent container of this widget.
             * @property parentWidget
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.parent = {};
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
        if(!P.ToggleButton){
            /**
             * Text on the button.
             * @property text
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.text = '';
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
        if(!P.ToggleButton){
            /**
             * Mouse entered over the component event handler function.
             * @property onMouseEntered
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.onMouseEntered = {};
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
        if(!P.ToggleButton){
            /**
             * The state of the button.
             * @property selected
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.selected = true;
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
        if(!P.ToggleButton){
            /**
             * The tooltip string that has been set with.
             * @property toolTipText
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.toolTipText = '';
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
        if(!P.ToggleButton){
            /**
             * Height of the component.
             * @property height
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.height = 0;
        }
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });
        if(!P.ToggleButton){
            /**
             * Native API. Returns low level html element. Applicable only in HTML5 client.
             * @property element
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.element = {};
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
        if(!P.ToggleButton){
            /**
             * Component shown event handler function.
             * @property onComponentShown
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.onComponentShown = {};
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
        if(!P.ToggleButton){
            /**
             * Mouse moved event handler function.
             * @property onMouseMoved
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.onMouseMoved = {};
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
        if(!P.ToggleButton){
            /**
             * True if this component is completely opaque.
             * @property opaque
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.opaque = true;
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
        if(!P.ToggleButton){
            /**
             * Determines whether this component should be visible when its parent is visible.
             * @property visible
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.visible = true;
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
        if(!P.ToggleButton){
            /**
             * The ButtonGroup this component belongs to.
             * @property buttonGroup
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.buttonGroup = {};
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
        if(!P.ToggleButton){
            /**
             * Component hidden event handler function.
             * @property onComponentHidden
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.onComponentHidden = {};
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
        if(!P.ToggleButton){
            /**
             * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
             * @property nextFocusableComponent
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.nextFocusableComponent = {};
        }
        Object.defineProperty(this, "iconTextGap", {
            get: function() {
                var value = delegate.iconTextGap;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.iconTextGap = P.boxAsJava(aValue);
            }
        });
        if(!P.ToggleButton){
            /**
             * The amount of space between the text and the icon displayed in this button.
             * @property iconTextGap
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.iconTextGap = 0;
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
        if(!P.ToggleButton){
            /**
             * Key released event handler function.
             * @property onKeyReleased
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.onKeyReleased = {};
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
        if(!P.ToggleButton){
            /**
             * Main action performed event handler function.
             * @property onActionPerformed
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.onActionPerformed = {};
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
        if(!P.ToggleButton){
            /**
             * Determines whether this component may be focused.
             * @property focusable
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.focusable = true;
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
        if(!P.ToggleButton){
            /**
             * Key typed event handler function.
             * @property onKeyTyped
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.onKeyTyped = {};
        }
        Object.defineProperty(this, "horizontalTextPosition", {
            get: function() {
                var value = delegate.horizontalTextPosition;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.horizontalTextPosition = P.boxAsJava(aValue);
            }
        });
        if(!P.ToggleButton){
            /**
             * Horizontal position of the text relative to the icon.
             * @property horizontalTextPosition
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.horizontalTextPosition = 0;
        }
        Object.defineProperty(this, "verticalTextPosition", {
            get: function() {
                var value = delegate.verticalTextPosition;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.verticalTextPosition = P.boxAsJava(aValue);
            }
        });
        if(!P.ToggleButton){
            /**
             * Vertical position of the text relative to the icon.
             * @property verticalTextPosition
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.verticalTextPosition = 0;
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
        if(!P.ToggleButton){
            /**
             * Mouse wheel moved event handler function.
             * @property onMouseWheelMoved
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.onMouseWheelMoved = {};
        }
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });
        if(!P.ToggleButton){
            /**
             * Native API. Returns low level swing component. Applicable only in J2SE swing client.
             * @property component
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.component = {};
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
        if(!P.ToggleButton){
            /**
             * Keyboard focus gained by the component event.
             * @property onFocusGained
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.onFocusGained = {};
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
        if(!P.ToggleButton){
            /**
             * Horizontal coordinate of the component.
             * @property left
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.left = 0;
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
        if(!P.ToggleButton){
            /**
             * The background color of this component.
             * @property background
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.background = {};
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
        if(!P.ToggleButton){
            /**
             * Mouse clicked event handler function.
             * @property onMouseClicked
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.onMouseClicked = {};
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
        if(!P.ToggleButton){
            /**
             * Mouse exited over the component event handler function.
             * @property onMouseExited
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.onMouseExited = {};
        }
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.ToggleButton){
            /**
             * Gets name of this component.
             * @property name
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.name = '';
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
        if(!P.ToggleButton){
            /**
             * Width of the component.
             * @property width
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.width = 0;
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
        if(!P.ToggleButton){
            /**
             * The font of this component.
             * @property font
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.font = {};
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
        if(!P.ToggleButton){
            /**
             * Key pressed event handler function.
             * @property onKeyPressed
             * @memberOf ToggleButton
             */
            P.ToggleButton.prototype.onKeyPressed = {};
        }
    };
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf ToggleButton
         */
        P.ToggleButton.prototype.focus = function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        };

})();