(function() {
    var javaClass = Java.type("com.eas.client.forms.api.components.PasswordField");
    javaClass.setPublisher(function(aDelegate) {
        return new P.PasswordField(null, aDelegate);
    });
    
    /**
     * Password field component.
     * @param text the text for the component (optional).
     * @constructor PasswordField PasswordField
     */
    P.PasswordField = function (text) {

        var maxArgs = 1;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 1 ? new javaClass(P.boxAsJava(text))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            get: function() {
                return function() {
                    return delegate;
                };
            }
        });
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf PasswordField
         */
        Object.defineProperty(this, "cursor", {
            get: function() {
                var value = delegate.cursor;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.cursor = P.boxAsJava(aValue);
            }
        });

        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf PasswordField
         */
        Object.defineProperty(this, "onMouseDragged", {
            get: function() {
                var value = delegate.onMouseDragged;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseDragged = P.boxAsJava(aValue);
            }
        });

        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf PasswordField
         */
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parent;
                return P.boxAsJs(value);
            }
        });

        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf PasswordField
         */
        Object.defineProperty(this, "onMouseReleased", {
            get: function() {
                var value = delegate.onMouseReleased;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseReleased = P.boxAsJava(aValue);
            }
        });

        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf PasswordField
         */
        Object.defineProperty(this, "onFocusLost", {
            get: function() {
                var value = delegate.onFocusLost;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onFocusLost = P.boxAsJava(aValue);
            }
        });

        /**
         * The text to be shown when component's value is absent.
         * @property emptyText
         * @memberOf PasswordField
         */
        Object.defineProperty(this, "emptyText", {
            get: function() {
                var value = delegate.emptyText;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.emptyText = P.boxAsJava(aValue);
            }
        });

        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf PasswordField
         */
        Object.defineProperty(this, "onMousePressed", {
            get: function() {
                var value = delegate.onMousePressed;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMousePressed = P.boxAsJava(aValue);
            }
        });

        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf PasswordField
         */
        Object.defineProperty(this, "foreground", {
            get: function() {
                var value = delegate.foreground;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.foreground = P.boxAsJava(aValue);
            }
        });

        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf PasswordField
         */
        Object.defineProperty(this, "error", {
            get: function() {
                var value = delegate.error;
                return P.boxAsJs(value);
            }
        });

        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf PasswordField
         */
        Object.defineProperty(this, "enabled", {
            get: function() {
                var value = delegate.enabled;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.enabled = P.boxAsJava(aValue);
            }
        });

        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf PasswordField
         */
        Object.defineProperty(this, "onComponentMoved", {
            get: function() {
                var value = delegate.onComponentMoved;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onComponentMoved = P.boxAsJava(aValue);
            }
        });

        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf PasswordField
         */
        Object.defineProperty(this, "componentPopupMenu", {
            get: function() {
                var value = delegate.componentPopupMenu;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.componentPopupMenu = P.boxAsJava(aValue);
            }
        });

        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf PasswordField
         */
        Object.defineProperty(this, "top", {
            get: function() {
                var value = delegate.top;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.top = P.boxAsJava(aValue);
            }
        });

        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf PasswordField
         */
        Object.defineProperty(this, "onComponentResized", {
            get: function() {
                var value = delegate.onComponentResized;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onComponentResized = P.boxAsJava(aValue);
            }
        });

        /**
         * The text contained in this component.
         * @property text
         * @memberOf PasswordField
         */
        Object.defineProperty(this, "text", {
            get: function() {
                var value = delegate.text;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.text = P.boxAsJava(aValue);
            }
        });

        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf PasswordField
         */
        Object.defineProperty(this, "onMouseEntered", {
            get: function() {
                var value = delegate.onMouseEntered;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseEntered = P.boxAsJava(aValue);
            }
        });

        /**
         * The value of this component.
         * @property value
         * @memberOf PasswordField
         */
        Object.defineProperty(this, "value", {
            get: function() {
                var value = delegate.value;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.value = P.boxAsJava(aValue);
            }
        });

        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf PasswordField
         */
        Object.defineProperty(this, "toolTipText", {
            get: function() {
                var value = delegate.toolTipText;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.toolTipText = P.boxAsJava(aValue);
            }
        });

        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf PasswordField
         */
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });

        /**
         * Height of the component.
         * @property height
         * @memberOf PasswordField
         */
        Object.defineProperty(this, "height", {
            get: function() {
                var value = delegate.height;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.height = P.boxAsJava(aValue);
            }
        });

        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf PasswordField
         */
        Object.defineProperty(this, "onComponentShown", {
            get: function() {
                var value = delegate.onComponentShown;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onComponentShown = P.boxAsJava(aValue);
            }
        });

        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf PasswordField
         */
        Object.defineProperty(this, "onMouseMoved", {
            get: function() {
                var value = delegate.onMouseMoved;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseMoved = P.boxAsJava(aValue);
            }
        });

        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf PasswordField
         */
        Object.defineProperty(this, "opaque", {
            get: function() {
                var value = delegate.opaque;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.opaque = P.boxAsJava(aValue);
            }
        });

        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf PasswordField
         */
        Object.defineProperty(this, "visible", {
            get: function() {
                var value = delegate.visible;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.visible = P.boxAsJava(aValue);
            }
        });

        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf PasswordField
         */
        Object.defineProperty(this, "onComponentHidden", {
            get: function() {
                var value = delegate.onComponentHidden;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onComponentHidden = P.boxAsJava(aValue);
            }
        });

        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf PasswordField
         */
        Object.defineProperty(this, "nextFocusableComponent", {
            get: function() {
                var value = delegate.nextFocusableComponent;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.nextFocusableComponent = P.boxAsJava(aValue);
            }
        });

        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf PasswordField
         */
        Object.defineProperty(this, "onActionPerformed", {
            get: function() {
                var value = delegate.onActionPerformed;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onActionPerformed = P.boxAsJava(aValue);
            }
        });

        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf PasswordField
         */
        Object.defineProperty(this, "onKeyReleased", {
            get: function() {
                var value = delegate.onKeyReleased;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onKeyReleased = P.boxAsJava(aValue);
            }
        });

        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf PasswordField
         */
        Object.defineProperty(this, "focusable", {
            get: function() {
                var value = delegate.focusable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.focusable = P.boxAsJava(aValue);
            }
        });

        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf PasswordField
         */
        Object.defineProperty(this, "onKeyTyped", {
            get: function() {
                var value = delegate.onKeyTyped;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onKeyTyped = P.boxAsJava(aValue);
            }
        });

        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf PasswordField
         */
        Object.defineProperty(this, "onMouseWheelMoved", {
            get: function() {
                var value = delegate.onMouseWheelMoved;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseWheelMoved = P.boxAsJava(aValue);
            }
        });

        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf PasswordField
         */
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });

        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf PasswordField
         */
        Object.defineProperty(this, "onFocusGained", {
            get: function() {
                var value = delegate.onFocusGained;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onFocusGained = P.boxAsJava(aValue);
            }
        });

        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf PasswordField
         */
        Object.defineProperty(this, "left", {
            get: function() {
                var value = delegate.left;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.left = P.boxAsJava(aValue);
            }
        });

        /**
         * The background color of this component.
         * @property background
         * @memberOf PasswordField
         */
        Object.defineProperty(this, "background", {
            get: function() {
                var value = delegate.background;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.background = P.boxAsJava(aValue);
            }
        });

        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf PasswordField
         */
        Object.defineProperty(this, "onMouseClicked", {
            get: function() {
                var value = delegate.onMouseClicked;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseClicked = P.boxAsJava(aValue);
            }
        });

        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf PasswordField
         */
        Object.defineProperty(this, "onMouseExited", {
            get: function() {
                var value = delegate.onMouseExited;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseExited = P.boxAsJava(aValue);
            }
        });

        /**
         * Gets name of this component.
         * @property name
         * @memberOf PasswordField
         */
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });

        /**
         * Width of the component.
         * @property width
         * @memberOf PasswordField
         */
        Object.defineProperty(this, "width", {
            get: function() {
                var value = delegate.width;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.width = P.boxAsJava(aValue);
            }
        });

        /**
         * The font of this component.
         * @property font
         * @memberOf PasswordField
         */
        Object.defineProperty(this, "font", {
            get: function() {
                var value = delegate.font;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.font = P.boxAsJava(aValue);
            }
        });

        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf PasswordField
         */
        Object.defineProperty(this, "onKeyPressed", {
            get: function() {
                var value = delegate.onKeyPressed;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onKeyPressed = P.boxAsJava(aValue);
            }
        });

        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf PasswordField
         */
        Object.defineProperty(this, "focus", {
            get: function() {
                return function() {
                    var value = delegate.focus();
                    return P.boxAsJs(value);
                };
            }
        });


        delegate.setPublished(this);
    };
})();