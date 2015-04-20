(function() {
    var javaClass = Java.type("com.eas.client.forms.components.Button");
    javaClass.setPublisher(function(aDelegate) {
        return new P.Button(null, null, null, null, aDelegate);
    });
    
    /**
     * Simple button component.
     * @param text the text of the component (optional)
     * @param icon the icon of the component (optional)
     * @param iconTextGap the text gap (optional)
     * @param actionPerformed the function for the action performed handler(optional)
     * @constructor Button Button
     */
    P.Button = function (text, icon, iconTextGap, actionPerformed) {
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
        if(P.Button.superclass)
            P.Button.superclass.constructor.apply(this, arguments);
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
        if(!P.Button){
            /**
             * The mouse <code>Cursor</code> over this component.
             * @property cursor
             * @memberOf Button
             */
            P.Button.prototype.cursor = {};
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
        if(!P.Button){
            /**
             * Mouse dragged event handler function.
             * @property onMouseDragged
             * @memberOf Button
             */
            P.Button.prototype.onMouseDragged = {};
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
        if(!P.Button){
            /**
             * Mouse released event handler function.
             * @property onMouseReleased
             * @memberOf Button
             */
            P.Button.prototype.onMouseReleased = {};
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
        if(!P.Button){
            /**
             * Keyboard focus lost by the component event handler function.
             * @property onFocusLost
             * @memberOf Button
             */
            P.Button.prototype.onFocusLost = {};
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
        if(!P.Button){
            /**
             * Image picture for the button.
             * @property icon
             * @memberOf Button
             */
            P.Button.prototype.icon = {};
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
        if(!P.Button){
            /**
             * Mouse pressed event handler function.
             * @property onMousePressed
             * @memberOf Button
             */
            P.Button.prototype.onMousePressed = {};
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
        if(!P.Button){
            /**
             * The foreground color of this component.
             * @property foreground
             * @memberOf Button
             */
            P.Button.prototype.foreground = {};
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
        if(!P.Button){
            /**
             * An error message of this component.
             * Validation procedure may set this property and subsequent focus lost event will clear it.
             * @property error
             * @memberOf Button
             */
            P.Button.prototype.error = '';
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
        if(!P.Button){
            /**
             * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
             * @property enabled
             * @memberOf Button
             */
            P.Button.prototype.enabled = true;
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
        if(!P.Button){
            /**
             * Component moved event handler function.
             * @property onComponentMoved
             * @memberOf Button
             */
            P.Button.prototype.onComponentMoved = {};
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
        if(!P.Button){
            /**
             * <code>PopupMenu</code> that assigned for this component.
             * @property componentPopupMenu
             * @memberOf Button
             */
            P.Button.prototype.componentPopupMenu = {};
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
        if(!P.Button){
            /**
             * Vertical coordinate of the component.
             * @property top
             * @memberOf Button
             */
            P.Button.prototype.top = 0;
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
        if(!P.Button){
            /**
             * Component resized event handler function.
             * @property onComponentResized
             * @memberOf Button
             */
            P.Button.prototype.onComponentResized = {};
        }
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parentWidget;
                return P.boxAsJs(value);
            }
        });
        if(!P.Button){
            /**
             * Parent container of this widget.
             * @property parentWidget
             * @memberOf Button
             */
            P.Button.prototype.parent = {};
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
        if(!P.Button){
            /**
             * Text on the button.
             * @property text
             * @memberOf Button
             */
            P.Button.prototype.text = '';
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
        if(!P.Button){
            /**
             * Mouse entered over the component event handler function.
             * @property onMouseEntered
             * @memberOf Button
             */
            P.Button.prototype.onMouseEntered = {};
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
        if(!P.Button){
            /**
             * The tooltip string that has been set with.
             * @property toolTipText
             * @memberOf Button
             */
            P.Button.prototype.toolTipText = '';
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
        if(!P.Button){
            /**
             * Height of the component.
             * @property height
             * @memberOf Button
             */
            P.Button.prototype.height = 0;
        }
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });
        if(!P.Button){
            /**
             * Native API. Returns low level html element. Applicable only in HTML5 client.
             * @property element
             * @memberOf Button
             */
            P.Button.prototype.element = {};
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
        if(!P.Button){
            /**
             * Component shown event handler function.
             * @property onComponentShown
             * @memberOf Button
             */
            P.Button.prototype.onComponentShown = {};
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
        if(!P.Button){
            /**
             * Mouse moved event handler function.
             * @property onMouseMoved
             * @memberOf Button
             */
            P.Button.prototype.onMouseMoved = {};
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
        if(!P.Button){
            /**
             * True if this component is completely opaque.
             * @property opaque
             * @memberOf Button
             */
            P.Button.prototype.opaque = true;
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
        if(!P.Button){
            /**
             * Determines whether this component should be visible when its parent is visible.
             * @property visible
             * @memberOf Button
             */
            P.Button.prototype.visible = true;
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
        if(!P.Button){
            /**
             * Component hidden event handler function.
             * @property onComponentHidden
             * @memberOf Button
             */
            P.Button.prototype.onComponentHidden = {};
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
        if(!P.Button){
            /**
             * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
             * @property nextFocusableComponent
             * @memberOf Button
             */
            P.Button.prototype.nextFocusableComponent = {};
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
        if(!P.Button){
            /**
             * The amount of space between the text and the icon displayed in this button.
             * @property iconTextGap
             * @memberOf Button
             */
            P.Button.prototype.iconTextGap = 0;
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
        if(!P.Button){
            /**
             * Key released event handler function.
             * @property onKeyReleased
             * @memberOf Button
             */
            P.Button.prototype.onKeyReleased = {};
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
        if(!P.Button){
            /**
             * Main action performed event handler function.
             * @property onActionPerformed
             * @memberOf Button
             */
            P.Button.prototype.onActionPerformed = {};
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
        if(!P.Button){
            /**
             * Determines whether this component may be focused.
             * @property focusable
             * @memberOf Button
             */
            P.Button.prototype.focusable = true;
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
        if(!P.Button){
            /**
             * Key typed event handler function.
             * @property onKeyTyped
             * @memberOf Button
             */
            P.Button.prototype.onKeyTyped = {};
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
        if(!P.Button){
            /**
             * Horizontal position of the text relative to the icon.
             * @property horizontalTextPosition
             * @memberOf Button
             */
            P.Button.prototype.horizontalTextPosition = 0;
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
        if(!P.Button){
            /**
             * Vertical position of the text relative to the icon.
             * @property verticalTextPosition
             * @memberOf Button
             */
            P.Button.prototype.verticalTextPosition = 0;
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
        if(!P.Button){
            /**
             * Mouse wheel moved event handler function.
             * @property onMouseWheelMoved
             * @memberOf Button
             */
            P.Button.prototype.onMouseWheelMoved = {};
        }
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });
        if(!P.Button){
            /**
             * Native API. Returns low level swing component. Applicable only in J2SE swing client.
             * @property component
             * @memberOf Button
             */
            P.Button.prototype.component = {};
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
        if(!P.Button){
            /**
             * Keyboard focus gained by the component event.
             * @property onFocusGained
             * @memberOf Button
             */
            P.Button.prototype.onFocusGained = {};
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
        if(!P.Button){
            /**
             * Horizontal coordinate of the component.
             * @property left
             * @memberOf Button
             */
            P.Button.prototype.left = 0;
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
        if(!P.Button){
            /**
             * The background color of this component.
             * @property background
             * @memberOf Button
             */
            P.Button.prototype.background = {};
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
        if(!P.Button){
            /**
             * Mouse clicked event handler function.
             * @property onMouseClicked
             * @memberOf Button
             */
            P.Button.prototype.onMouseClicked = {};
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
        if(!P.Button){
            /**
             * Mouse exited over the component event handler function.
             * @property onMouseExited
             * @memberOf Button
             */
            P.Button.prototype.onMouseExited = {};
        }
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.Button){
            /**
             * Gets name of this component.
             * @property name
             * @memberOf Button
             */
            P.Button.prototype.name = '';
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
        if(!P.Button){
            /**
             * Width of the component.
             * @property width
             * @memberOf Button
             */
            P.Button.prototype.width = 0;
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
        if(!P.Button){
            /**
             * The font of this component.
             * @property font
             * @memberOf Button
             */
            P.Button.prototype.font = {};
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
        if(!P.Button){
            /**
             * Key pressed event handler function.
             * @property onKeyPressed
             * @memberOf Button
             */
            P.Button.prototype.onKeyPressed = {};
        }
    };
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf Button
         */
        P.Button.prototype.focus = function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        };

})();