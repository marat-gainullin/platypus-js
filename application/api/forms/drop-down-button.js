(function() {
    var javaClass = Java.type("com.eas.client.forms.components.DropDownButton");
    javaClass.setPublisher(function(aDelegate) {
        return new P.DropDownButton(null, null, null, null, aDelegate);
    });
    
    /**
     * Drop-down button component.
     * @param text the text of the component (optional).
     * @param icon the icon of the component (optional).
     * @param iconTextGap the text gap (optional).
     * @param actionPerformed the function for the action performed handler (optional).
     * @constructor DropDownButton DropDownButton
     */
    P.DropDownButton = function (text, icon, iconTextGap, actionPerformed) {
        var maxArgs = 4;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 4 ? new javaClass(P.boxAsJava(text), P.boxAsJava(icon), P.boxAsJava(iconTextGap), P.boxAsJava(actionPerformed))
            : arguments.length === 3 ? new javaClass(P.boxAsJava(text), P.boxAsJava(icon), P.boxAsJava(iconTextGap))
            : arguments.length === 2 ? new javaClass(P.boxAsJava(text), P.boxAsJava(icon))
            : arguments.length === 1 ? new javaClass(P.boxAsJava(text))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.DropDownButton.superclass)
            P.DropDownButton.superclass.constructor.apply(this, arguments);
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
        if(!P.DropDownButton){
            /**
             * The mouse <code>Cursor</code> over this component.
             * @property cursor
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.cursor = {};
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
        if(!P.DropDownButton){
            /**
             * Mouse dragged event handler function.
             * @property onMouseDragged
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.onMouseDragged = {};
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
        if(!P.DropDownButton){
            /**
             * Mouse released event handler function.
             * @property onMouseReleased
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.onMouseReleased = {};
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
        if(!P.DropDownButton){
            /**
             * Keyboard focus lost by the component event handler function.
             * @property onFocusLost
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.onFocusLost = {};
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
        if(!P.DropDownButton){
            /**
             * Image picture for the button.
             * @property icon
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.icon = {};
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
        if(!P.DropDownButton){
            /**
             * Mouse pressed event handler function.
             * @property onMousePressed
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.onMousePressed = {};
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
        if(!P.DropDownButton){
            /**
             * The foreground color of this component.
             * @property foreground
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.foreground = {};
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
        if(!P.DropDownButton){
            /**
             * An error message of this component.
             * Validation procedure may set this property and subsequent focus lost event will clear it.
             * @property error
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.error = '';
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
        if(!P.DropDownButton){
            /**
             * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
             * @property enabled
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.enabled = true;
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
        if(!P.DropDownButton){
            /**
             * Component moved event handler function.
             * @property onComponentMoved
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.onComponentMoved = {};
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
        if(!P.DropDownButton){
            /**
             * <code>PopupMenu</code> that assigned for this component.
             * @property componentPopupMenu
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.componentPopupMenu = {};
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
        if(!P.DropDownButton){
            /**
             * Vertical coordinate of the component.
             * @property top
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.top = 0;
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
        if(!P.DropDownButton){
            /**
             * Component resized event handler function.
             * @property onComponentResized
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.onComponentResized = {};
        }
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parentWidget;
                return P.boxAsJs(value);
            }
        });
        if(!P.DropDownButton){
            /**
             * Parent container of this widget.
             * @property parentWidget
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.parent = {};
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
        if(!P.DropDownButton){
            /**
             * Text on the button.
             * @property text
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.text = '';
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
        if(!P.DropDownButton){
            /**
             * Mouse entered over the component event handler function.
             * @property onMouseEntered
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.onMouseEntered = {};
        }
        Object.defineProperty(this, "dropDownMenu", {
            get: function() {
                var value = delegate.dropDownMenu;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.dropDownMenu = P.boxAsJava(aValue);
            }
        });
        if(!P.DropDownButton){
            /**
             * <code>PopupMenu</code> for the component.
             * @property dropDownMenu
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.dropDownMenu = {};
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
        if(!P.DropDownButton){
            /**
             * The tooltip string that has been set with.
             * @property toolTipText
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.toolTipText = '';
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
        if(!P.DropDownButton){
            /**
             * Height of the component.
             * @property height
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.height = 0;
        }
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });
        if(!P.DropDownButton){
            /**
             * Native API. Returns low level html element. Applicable only in HTML5 client.
             * @property element
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.element = {};
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
        if(!P.DropDownButton){
            /**
             * Component shown event handler function.
             * @property onComponentShown
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.onComponentShown = {};
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
        if(!P.DropDownButton){
            /**
             * Mouse moved event handler function.
             * @property onMouseMoved
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.onMouseMoved = {};
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
        if(!P.DropDownButton){
            /**
             * True if this component is completely opaque.
             * @property opaque
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.opaque = true;
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
        if(!P.DropDownButton){
            /**
             * Determines whether this component should be visible when its parent is visible.
             * @property visible
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.visible = true;
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
        if(!P.DropDownButton){
            /**
             * Component hidden event handler function.
             * @property onComponentHidden
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.onComponentHidden = {};
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
        if(!P.DropDownButton){
            /**
             * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
             * @property nextFocusableComponent
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.nextFocusableComponent = {};
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
        if(!P.DropDownButton){
            /**
             * The amount of space between the text and the icon displayed in this button.
             * @property iconTextGap
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.iconTextGap = 0;
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
        if(!P.DropDownButton){
            /**
             * Key released event handler function.
             * @property onKeyReleased
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.onKeyReleased = {};
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
        if(!P.DropDownButton){
            /**
             * Main action performed event handler function.
             * @property onActionPerformed
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.onActionPerformed = {};
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
        if(!P.DropDownButton){
            /**
             * Determines whether this component may be focused.
             * @property focusable
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.focusable = true;
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
        if(!P.DropDownButton){
            /**
             * Key typed event handler function.
             * @property onKeyTyped
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.onKeyTyped = {};
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
        if(!P.DropDownButton){
            /**
             * Horizontal position of the text relative to the icon.
             * @property horizontalTextPosition
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.horizontalTextPosition = 0;
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
        if(!P.DropDownButton){
            /**
             * Vertical position of the text relative to the icon.
             * @property verticalTextPosition
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.verticalTextPosition = 0;
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
        if(!P.DropDownButton){
            /**
             * Mouse wheel moved event handler function.
             * @property onMouseWheelMoved
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.onMouseWheelMoved = {};
        }
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });
        if(!P.DropDownButton){
            /**
             * Native API. Returns low level swing component. Applicable only in J2SE swing client.
             * @property component
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.component = {};
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
        if(!P.DropDownButton){
            /**
             * Keyboard focus gained by the component event.
             * @property onFocusGained
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.onFocusGained = {};
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
        if(!P.DropDownButton){
            /**
             * Horizontal coordinate of the component.
             * @property left
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.left = 0;
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
        if(!P.DropDownButton){
            /**
             * The background color of this component.
             * @property background
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.background = {};
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
        if(!P.DropDownButton){
            /**
             * Mouse clicked event handler function.
             * @property onMouseClicked
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.onMouseClicked = {};
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
        if(!P.DropDownButton){
            /**
             * Mouse exited over the component event handler function.
             * @property onMouseExited
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.onMouseExited = {};
        }
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.DropDownButton){
            /**
             * Gets name of this component.
             * @property name
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.name = '';
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
        if(!P.DropDownButton){
            /**
             * Width of the component.
             * @property width
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.width = 0;
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
        if(!P.DropDownButton){
            /**
             * The font of this component.
             * @property font
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.font = {};
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
        if(!P.DropDownButton){
            /**
             * Key pressed event handler function.
             * @property onKeyPressed
             * @memberOf DropDownButton
             */
            P.DropDownButton.prototype.onKeyPressed = {};
        }
    };
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf DropDownButton
         */
        P.DropDownButton.prototype.focus = function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        };

})();