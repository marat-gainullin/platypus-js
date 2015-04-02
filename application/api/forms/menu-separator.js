(function() {
    var javaClass = Java.type("com.eas.client.forms.menu.MenuSeparator");
    javaClass.setPublisher(function(aDelegate) {
        return new P.MenuSeparator(aDelegate);
    });
    
    /**
     * MenuSeparator provides a general purpose component for
     * implementing divider lines - most commonly used as a divider
     * between menu items that breaks them up into logical groupings.
     * @constructor MenuSeparator MenuSeparator
     */
    P.MenuSeparator = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.MenuSeparator.superclass)
            P.MenuSeparator.superclass.constructor.apply(this, arguments);
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
        if(!P.MenuSeparator){
            /**
             * The mouse <code>Cursor</code> over this component.
             * @property cursor
             * @memberOf MenuSeparator
             */
            P.MenuSeparator.prototype.cursor = {};
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
        if(!P.MenuSeparator){
            /**
             * Mouse dragged event handler function.
             * @property onMouseDragged
             * @memberOf MenuSeparator
             */
            P.MenuSeparator.prototype.onMouseDragged = {};
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
        if(!P.MenuSeparator){
            /**
             * Mouse released event handler function.
             * @property onMouseReleased
             * @memberOf MenuSeparator
             */
            P.MenuSeparator.prototype.onMouseReleased = {};
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
        if(!P.MenuSeparator){
            /**
             * Keyboard focus lost by the component event handler function.
             * @property onFocusLost
             * @memberOf MenuSeparator
             */
            P.MenuSeparator.prototype.onFocusLost = {};
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
        if(!P.MenuSeparator){
            /**
             * Mouse pressed event handler function.
             * @property onMousePressed
             * @memberOf MenuSeparator
             */
            P.MenuSeparator.prototype.onMousePressed = {};
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
        if(!P.MenuSeparator){
            /**
             * The foreground color of this component.
             * @property foreground
             * @memberOf MenuSeparator
             */
            P.MenuSeparator.prototype.foreground = {};
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
        if(!P.MenuSeparator){
            /**
             * An error message of this component.
             * Validation procedure may set this property and subsequent focus lost event will clear it.
             * @property error
             * @memberOf MenuSeparator
             */
            P.MenuSeparator.prototype.error = '';
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
        if(!P.MenuSeparator){
            /**
             * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
             * @property enabled
             * @memberOf MenuSeparator
             */
            P.MenuSeparator.prototype.enabled = true;
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
        if(!P.MenuSeparator){
            /**
             * Component moved event handler function.
             * @property onComponentMoved
             * @memberOf MenuSeparator
             */
            P.MenuSeparator.prototype.onComponentMoved = {};
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
        if(!P.MenuSeparator){
            /**
             * <code>PopupMenu</code> that assigned for this component.
             * @property componentPopupMenu
             * @memberOf MenuSeparator
             */
            P.MenuSeparator.prototype.componentPopupMenu = {};
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
        if(!P.MenuSeparator){
            /**
             * Vertical coordinate of the component.
             * @property top
             * @memberOf MenuSeparator
             */
            P.MenuSeparator.prototype.top = 0;
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
        if(!P.MenuSeparator){
            /**
             * Component resized event handler function.
             * @property onComponentResized
             * @memberOf MenuSeparator
             */
            P.MenuSeparator.prototype.onComponentResized = {};
        }
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parentWidget;
                return P.boxAsJs(value);
            }
        });
        if(!P.MenuSeparator){
            /**
             * Parent container of this widget.
             * @property parentWidget
             * @memberOf MenuSeparator
             */
            P.MenuSeparator.prototype.parent = {};
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
        if(!P.MenuSeparator){
            /**
             * Mouse entered over the component event handler function.
             * @property onMouseEntered
             * @memberOf MenuSeparator
             */
            P.MenuSeparator.prototype.onMouseEntered = {};
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
        if(!P.MenuSeparator){
            /**
             * The tooltip string that has been set with.
             * @property toolTipText
             * @memberOf MenuSeparator
             */
            P.MenuSeparator.prototype.toolTipText = '';
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
        if(!P.MenuSeparator){
            /**
             * Height of the component.
             * @property height
             * @memberOf MenuSeparator
             */
            P.MenuSeparator.prototype.height = 0;
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
        if(!P.MenuSeparator){
            /**
             * Component shown event handler function.
             * @property onComponentShown
             * @memberOf MenuSeparator
             */
            P.MenuSeparator.prototype.onComponentShown = {};
        }
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });
        if(!P.MenuSeparator){
            /**
             * Native API. Returns low level html element. Applicable only in HTML5 client.
             * @property element
             * @memberOf MenuSeparator
             */
            P.MenuSeparator.prototype.element = {};
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
        if(!P.MenuSeparator){
            /**
             * Mouse moved event handler function.
             * @property onMouseMoved
             * @memberOf MenuSeparator
             */
            P.MenuSeparator.prototype.onMouseMoved = {};
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
        if(!P.MenuSeparator){
            /**
             * True if this component is completely opaque.
             * @property opaque
             * @memberOf MenuSeparator
             */
            P.MenuSeparator.prototype.opaque = true;
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
        if(!P.MenuSeparator){
            /**
             * Determines whether this component should be visible when its parent is visible.
             * @property visible
             * @memberOf MenuSeparator
             */
            P.MenuSeparator.prototype.visible = true;
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
        if(!P.MenuSeparator){
            /**
             * Component hidden event handler function.
             * @property onComponentHidden
             * @memberOf MenuSeparator
             */
            P.MenuSeparator.prototype.onComponentHidden = {};
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
        if(!P.MenuSeparator){
            /**
             * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
             * @property nextFocusableComponent
             * @memberOf MenuSeparator
             */
            P.MenuSeparator.prototype.nextFocusableComponent = {};
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
        if(!P.MenuSeparator){
            /**
             * Main action performed event handler function.
             * @property onActionPerformed
             * @memberOf MenuSeparator
             */
            P.MenuSeparator.prototype.onActionPerformed = {};
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
        if(!P.MenuSeparator){
            /**
             * Key released event handler function.
             * @property onKeyReleased
             * @memberOf MenuSeparator
             */
            P.MenuSeparator.prototype.onKeyReleased = {};
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
        if(!P.MenuSeparator){
            /**
             * Determines whether this component may be focused.
             * @property focusable
             * @memberOf MenuSeparator
             */
            P.MenuSeparator.prototype.focusable = true;
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
        if(!P.MenuSeparator){
            /**
             * Key typed event handler function.
             * @property onKeyTyped
             * @memberOf MenuSeparator
             */
            P.MenuSeparator.prototype.onKeyTyped = {};
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
        if(!P.MenuSeparator){
            /**
             * Mouse wheel moved event handler function.
             * @property onMouseWheelMoved
             * @memberOf MenuSeparator
             */
            P.MenuSeparator.prototype.onMouseWheelMoved = {};
        }
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });
        if(!P.MenuSeparator){
            /**
             * Native API. Returns low level swing component. Applicable only in J2SE swing client.
             * @property component
             * @memberOf MenuSeparator
             */
            P.MenuSeparator.prototype.component = {};
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
        if(!P.MenuSeparator){
            /**
             * Keyboard focus gained by the component event.
             * @property onFocusGained
             * @memberOf MenuSeparator
             */
            P.MenuSeparator.prototype.onFocusGained = {};
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
        if(!P.MenuSeparator){
            /**
             * Horizontal coordinate of the component.
             * @property left
             * @memberOf MenuSeparator
             */
            P.MenuSeparator.prototype.left = 0;
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
        if(!P.MenuSeparator){
            /**
             * The background color of this component.
             * @property background
             * @memberOf MenuSeparator
             */
            P.MenuSeparator.prototype.background = {};
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
        if(!P.MenuSeparator){
            /**
             * Mouse clicked event handler function.
             * @property onMouseClicked
             * @memberOf MenuSeparator
             */
            P.MenuSeparator.prototype.onMouseClicked = {};
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
        if(!P.MenuSeparator){
            /**
             * Mouse exited over the component event handler function.
             * @property onMouseExited
             * @memberOf MenuSeparator
             */
            P.MenuSeparator.prototype.onMouseExited = {};
        }
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.MenuSeparator){
            /**
             * Gets name of this component.
             * @property name
             * @memberOf MenuSeparator
             */
            P.MenuSeparator.prototype.name = '';
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
        if(!P.MenuSeparator){
            /**
             * Width of the component.
             * @property width
             * @memberOf MenuSeparator
             */
            P.MenuSeparator.prototype.width = 0;
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
        if(!P.MenuSeparator){
            /**
             * The font of this component.
             * @property font
             * @memberOf MenuSeparator
             */
            P.MenuSeparator.prototype.font = {};
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
        if(!P.MenuSeparator){
            /**
             * Key pressed event handler function.
             * @property onKeyPressed
             * @memberOf MenuSeparator
             */
            P.MenuSeparator.prototype.onKeyPressed = {};
        }
    };
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf MenuSeparator
         */
        P.MenuSeparator.prototype.focus = function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        };

})();