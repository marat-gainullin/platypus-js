(function() {
    var javaClass = Java.type("com.eas.client.forms.components.Label");
    javaClass.setPublisher(function(aDelegate) {
        return new P.Label(null, null, null, aDelegate);
    });
    
    /**
     * Label component.
     * @param text the initial text for the component (optional)
     * @param icon the icon for the component (optional)
     * @param iconTextGap the text gap (optional)
     * @constructor Label Label
     */
    P.Label = function (text, icon, iconTextGap) {
        var maxArgs = 3;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 3 ? new javaClass(P.boxAsJava(text), P.boxAsJava(icon), P.boxAsJava(iconTextGap))
            : arguments.length === 2 ? new javaClass(P.boxAsJava(text), P.boxAsJava(icon))
            : arguments.length === 1 ? new javaClass(P.boxAsJava(text))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.Label.superclass)
            P.Label.superclass.constructor.apply(this, arguments);
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
        if(!P.Label){
            /**
             * The mouse <code>Cursor</code> over this component.
             * @property cursor
             * @memberOf Label
             */
            P.Label.prototype.cursor = {};
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
        if(!P.Label){
            /**
             * Mouse dragged event handler function.
             * @property onMouseDragged
             * @memberOf Label
             */
            P.Label.prototype.onMouseDragged = {};
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
        if(!P.Label){
            /**
             * Mouse released event handler function.
             * @property onMouseReleased
             * @memberOf Label
             */
            P.Label.prototype.onMouseReleased = {};
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
        if(!P.Label){
            /**
             * Keyboard focus lost by the component event handler function.
             * @property onFocusLost
             * @memberOf Label
             */
            P.Label.prototype.onFocusLost = {};
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
        if(!P.Label){
            /**
             * The graphic image (glyph, icon) that the label displays.
             * @property icon
             * @memberOf Label
             */
            P.Label.prototype.icon = {};
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
        if(!P.Label){
            /**
             * Mouse pressed event handler function.
             * @property onMousePressed
             * @memberOf Label
             */
            P.Label.prototype.onMousePressed = {};
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
        if(!P.Label){
            /**
             * The foreground color of this component.
             * @property foreground
             * @memberOf Label
             */
            P.Label.prototype.foreground = {};
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
        if(!P.Label){
            /**
             * An error message of this component.
             * Validation procedure may set this property and subsequent focus lost event will clear it.
             * @property error
             * @memberOf Label
             */
            P.Label.prototype.error = '';
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
        if(!P.Label){
            /**
             * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
             * @property enabled
             * @memberOf Label
             */
            P.Label.prototype.enabled = true;
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
        if(!P.Label){
            /**
             * Component moved event handler function.
             * @property onComponentMoved
             * @memberOf Label
             */
            P.Label.prototype.onComponentMoved = {};
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
        if(!P.Label){
            /**
             * <code>PopupMenu</code> that assigned for this component.
             * @property componentPopupMenu
             * @memberOf Label
             */
            P.Label.prototype.componentPopupMenu = {};
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
        if(!P.Label){
            /**
             * Vertical coordinate of the component.
             * @property top
             * @memberOf Label
             */
            P.Label.prototype.top = 0;
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
        if(!P.Label){
            /**
             * Component resized event handler function.
             * @property onComponentResized
             * @memberOf Label
             */
            P.Label.prototype.onComponentResized = {};
        }
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parentWidget;
                return P.boxAsJs(value);
            }
        });
        if(!P.Label){
            /**
             * Parent container of this widget.
             * @property parentWidget
             * @memberOf Label
             */
            P.Label.prototype.parent = {};
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
        if(!P.Label){
            /**
             * The text string that the label displays.
             * @property text
             * @memberOf Label
             */
            P.Label.prototype.text = '';
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
        if(!P.Label){
            /**
             * Mouse entered over the component event handler function.
             * @property onMouseEntered
             * @memberOf Label
             */
            P.Label.prototype.onMouseEntered = {};
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
        if(!P.Label){
            /**
             * The tooltip string that has been set with.
             * @property toolTipText
             * @memberOf Label
             */
            P.Label.prototype.toolTipText = '';
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
        if(!P.Label){
            /**
             * Height of the component.
             * @property height
             * @memberOf Label
             */
            P.Label.prototype.height = 0;
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
        if(!P.Label){
            /**
             * Component shown event handler function.
             * @property onComponentShown
             * @memberOf Label
             */
            P.Label.prototype.onComponentShown = {};
        }
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });
        if(!P.Label){
            /**
             * Native API. Returns low level html element. Applicable only in HTML5 client.
             * @property element
             * @memberOf Label
             */
            P.Label.prototype.element = {};
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
        if(!P.Label){
            /**
             * Mouse moved event handler function.
             * @property onMouseMoved
             * @memberOf Label
             */
            P.Label.prototype.onMouseMoved = {};
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
        if(!P.Label){
            /**
             * True if this component is completely opaque.
             * @property opaque
             * @memberOf Label
             */
            P.Label.prototype.opaque = true;
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
        if(!P.Label){
            /**
             * Determines whether this component should be visible when its parent is visible.
             * @property visible
             * @memberOf Label
             */
            P.Label.prototype.visible = true;
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
        if(!P.Label){
            /**
             * Component hidden event handler function.
             * @property onComponentHidden
             * @memberOf Label
             */
            P.Label.prototype.onComponentHidden = {};
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
        if(!P.Label){
            /**
             * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
             * @property nextFocusableComponent
             * @memberOf Label
             */
            P.Label.prototype.nextFocusableComponent = {};
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
        if(!P.Label){
            /**
             * The amount of space between the text and the icon displayed in this label.
             * @property iconTextGap
             * @memberOf Label
             */
            P.Label.prototype.iconTextGap = 0;
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
        if(!P.Label){
            /**
             * Main action performed event handler function.
             * @property onActionPerformed
             * @memberOf Label
             */
            P.Label.prototype.onActionPerformed = {};
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
        if(!P.Label){
            /**
             * Key released event handler function.
             * @property onKeyReleased
             * @memberOf Label
             */
            P.Label.prototype.onKeyReleased = {};
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
        if(!P.Label){
            /**
             * Determines whether this component may be focused.
             * @property focusable
             * @memberOf Label
             */
            P.Label.prototype.focusable = true;
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
        if(!P.Label){
            /**
             * Key typed event handler function.
             * @property onKeyTyped
             * @memberOf Label
             */
            P.Label.prototype.onKeyTyped = {};
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
        if(!P.Label){
            /**
             * Horizontal position of the text relative to the icon.
             * @property horizontalTextPosition
             * @memberOf Label
             */
            P.Label.prototype.horizontalTextPosition = 0;
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
        if(!P.Label){
            /**
             * Vertical position of the text relative to the icon.
             * @property verticalTextPosition
             * @memberOf Label
             */
            P.Label.prototype.verticalTextPosition = 0;
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
        if(!P.Label){
            /**
             * Mouse wheel moved event handler function.
             * @property onMouseWheelMoved
             * @memberOf Label
             */
            P.Label.prototype.onMouseWheelMoved = {};
        }
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });
        if(!P.Label){
            /**
             * Native API. Returns low level swing component. Applicable only in J2SE swing client.
             * @property component
             * @memberOf Label
             */
            P.Label.prototype.component = {};
        }
        Object.defineProperty(this, "horizontalAlignment", {
            get: function() {
                var value = delegate.horizontalAlignment;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.horizontalAlignment = P.boxAsJava(aValue);
            }
        });
        if(!P.Label){
            /**
             * Horizontal position of the text with the icon relative to the component's size.
             * @property horizontalAlignment
             * @memberOf Label
             */
            P.Label.prototype.horizontalAlignment = 0;
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
        if(!P.Label){
            /**
             * Keyboard focus gained by the component event.
             * @property onFocusGained
             * @memberOf Label
             */
            P.Label.prototype.onFocusGained = {};
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
        if(!P.Label){
            /**
             * Horizontal coordinate of the component.
             * @property left
             * @memberOf Label
             */
            P.Label.prototype.left = 0;
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
        if(!P.Label){
            /**
             * The background color of this component.
             * @property background
             * @memberOf Label
             */
            P.Label.prototype.background = {};
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
        if(!P.Label){
            /**
             * Mouse clicked event handler function.
             * @property onMouseClicked
             * @memberOf Label
             */
            P.Label.prototype.onMouseClicked = {};
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
        if(!P.Label){
            /**
             * Mouse exited over the component event handler function.
             * @property onMouseExited
             * @memberOf Label
             */
            P.Label.prototype.onMouseExited = {};
        }
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.Label){
            /**
             * Gets name of this component.
             * @property name
             * @memberOf Label
             */
            P.Label.prototype.name = '';
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
        if(!P.Label){
            /**
             * Width of the component.
             * @property width
             * @memberOf Label
             */
            P.Label.prototype.width = 0;
        }
        Object.defineProperty(this, "verticalAlignment", {
            get: function() {
                var value = delegate.verticalAlignment;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.verticalAlignment = P.boxAsJava(aValue);
            }
        });
        if(!P.Label){
            /**
             * Vertical position of the text with the icon relative to the component's size.
             * @property verticalAlignment
             * @memberOf Label
             */
            P.Label.prototype.verticalAlignment = 0;
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
        if(!P.Label){
            /**
             * The font of this component.
             * @property font
             * @memberOf Label
             */
            P.Label.prototype.font = {};
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
        if(!P.Label){
            /**
             * Key pressed event handler function.
             * @property onKeyPressed
             * @memberOf Label
             */
            P.Label.prototype.onKeyPressed = {};
        }
    };
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf Label
         */
        P.Label.prototype.focus = function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        };

})();