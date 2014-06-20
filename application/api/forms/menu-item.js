(function() {
    var javaClass = Java.type("com.eas.client.forms.api.menu.MenuItem");
    javaClass.setPublisher(function(aDelegate) {
        return new P.MenuItem(null, null, null, aDelegate);
    });
    
    /**
     * A menu item that can be selected or deselected.
     * @param text the text of the component (optional).
     * @param icon the icon of the component (optional).
     * @param actionPerformed the function for the action performed handler (optional).
     * @constructor MenuItem MenuItem
     */
    P.MenuItem = function (text, icon, actionPerformed) {
        var maxArgs = 3;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 3 ? new javaClass(P.boxAsJava(text), P.boxAsJava(icon), P.boxAsJava(actionPerformed))
            : arguments.length === 2 ? new javaClass(P.boxAsJava(text), P.boxAsJava(icon))
            : arguments.length === 1 ? new javaClass(P.boxAsJava(text))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.MenuItem.superclass)
            P.MenuItem.superclass.constructor.apply(this, arguments);
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
        if(!P.MenuItem){
            /**
             * The mouse <code>Cursor</code> over this component.
             * @property cursor
             * @memberOf MenuItem
             */
            P.MenuItem.prototype.cursor = {};
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
        if(!P.MenuItem){
            /**
             * Mouse dragged event handler function.
             * @property onMouseDragged
             * @memberOf MenuItem
             */
            P.MenuItem.prototype.onMouseDragged = {};
        }
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parent;
                return P.boxAsJs(value);
            }
        });
        if(!P.MenuItem){
            /**
             * The parent container.
             * @property parent
             * @memberOf MenuItem
             */
            P.MenuItem.prototype.parent = {};
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
        if(!P.MenuItem){
            /**
             * Mouse released event handler function.
             * @property onMouseReleased
             * @memberOf MenuItem
             */
            P.MenuItem.prototype.onMouseReleased = {};
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
        if(!P.MenuItem){
            /**
             * Keyboard focus lost by the component event handler function.
             * @property onFocusLost
             * @memberOf MenuItem
             */
            P.MenuItem.prototype.onFocusLost = {};
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
        if(!P.MenuItem){
            /**
             * Image picture for the menu item.
             * @property icon
             * @memberOf MenuItem
             */
            P.MenuItem.prototype.icon = {};
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
        if(!P.MenuItem){
            /**
             * Mouse pressed event handler function.
             * @property onMousePressed
             * @memberOf MenuItem
             */
            P.MenuItem.prototype.onMousePressed = {};
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
        if(!P.MenuItem){
            /**
             * The foreground color of this component.
             * @property foreground
             * @memberOf MenuItem
             */
            P.MenuItem.prototype.foreground = {};
        }
        Object.defineProperty(this, "error", {
            get: function() {
                var value = delegate.error;
                return P.boxAsJs(value);
            }
        });
        if(!P.MenuItem){
            /**
             * An error message of this component.
             * Validation procedure may set this property and subsequent focus lost event will clear it.
             * @property error
             * @memberOf MenuItem
             */
            P.MenuItem.prototype.error = '';
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
        if(!P.MenuItem){
            /**
             * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
             * @property enabled
             * @memberOf MenuItem
             */
            P.MenuItem.prototype.enabled = true;
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
        if(!P.MenuItem){
            /**
             * Component moved event handler function.
             * @property onComponentMoved
             * @memberOf MenuItem
             */
            P.MenuItem.prototype.onComponentMoved = {};
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
        if(!P.MenuItem){
            /**
             * <code>PopupMenu</code> that assigned for this component.
             * @property componentPopupMenu
             * @memberOf MenuItem
             */
            P.MenuItem.prototype.componentPopupMenu = {};
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
        if(!P.MenuItem){
            /**
             * Vertical coordinate of the component.
             * @property top
             * @memberOf MenuItem
             */
            P.MenuItem.prototype.top = 0;
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
        if(!P.MenuItem){
            /**
             * Component resized event handler function.
             * @property onComponentResized
             * @memberOf MenuItem
             */
            P.MenuItem.prototype.onComponentResized = {};
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
        if(!P.MenuItem){
            /**
             * The menu item text.
             * @property text
             * @memberOf MenuItem
             */
            P.MenuItem.prototype.text = '';
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
        if(!P.MenuItem){
            /**
             * Mouse entered over the component event handler function.
             * @property onMouseEntered
             * @memberOf MenuItem
             */
            P.MenuItem.prototype.onMouseEntered = {};
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
        if(!P.MenuItem){
            /**
             * The tooltip string that has been set with.
             * @property toolTipText
             * @memberOf MenuItem
             */
            P.MenuItem.prototype.toolTipText = '';
        }
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });
        if(!P.MenuItem){
            /**
             * Native API. Returns low level html element. Applicable only in HTML5 client.
             * @property element
             * @memberOf MenuItem
             */
            P.MenuItem.prototype.element = {};
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
        if(!P.MenuItem){
            /**
             * Height of the component.
             * @property height
             * @memberOf MenuItem
             */
            P.MenuItem.prototype.height = 0;
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
        if(!P.MenuItem){
            /**
             * Component shown event handler function.
             * @property onComponentShown
             * @memberOf MenuItem
             */
            P.MenuItem.prototype.onComponentShown = {};
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
        if(!P.MenuItem){
            /**
             * Mouse moved event handler function.
             * @property onMouseMoved
             * @memberOf MenuItem
             */
            P.MenuItem.prototype.onMouseMoved = {};
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
        if(!P.MenuItem){
            /**
             * True if this component is completely opaque.
             * @property opaque
             * @memberOf MenuItem
             */
            P.MenuItem.prototype.opaque = true;
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
        if(!P.MenuItem){
            /**
             * Determines whether this component should be visible when its parent is visible.
             * @property visible
             * @memberOf MenuItem
             */
            P.MenuItem.prototype.visible = true;
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
        if(!P.MenuItem){
            /**
             * Component hidden event handler function.
             * @property onComponentHidden
             * @memberOf MenuItem
             */
            P.MenuItem.prototype.onComponentHidden = {};
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
        if(!P.MenuItem){
            /**
             * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
             * @property nextFocusableComponent
             * @memberOf MenuItem
             */
            P.MenuItem.prototype.nextFocusableComponent = {};
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
        if(!P.MenuItem){
            /**
             * Key released event handler function.
             * @property onKeyReleased
             * @memberOf MenuItem
             */
            P.MenuItem.prototype.onKeyReleased = {};
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
        if(!P.MenuItem){
            /**
             * Main action performed event handler function.
             * @property onActionPerformed
             * @memberOf MenuItem
             */
            P.MenuItem.prototype.onActionPerformed = {};
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
        if(!P.MenuItem){
            /**
             * Determines whether this component may be focused.
             * @property focusable
             * @memberOf MenuItem
             */
            P.MenuItem.prototype.focusable = true;
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
        if(!P.MenuItem){
            /**
             * Key typed event handler function.
             * @property onKeyTyped
             * @memberOf MenuItem
             */
            P.MenuItem.prototype.onKeyTyped = {};
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
        if(!P.MenuItem){
            /**
             * Horizontal position of the text relative to the icon.
             * @property horizontalTextPosition
             * @memberOf MenuItem
             */
            P.MenuItem.prototype.horizontalTextPosition = 0;
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
        if(!P.MenuItem){
            /**
             * Vertical position of the text relative to the icon.
             * @property verticalTextPosition
             * @memberOf MenuItem
             */
            P.MenuItem.prototype.verticalTextPosition = 0;
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
        if(!P.MenuItem){
            /**
             * Mouse wheel moved event handler function.
             * @property onMouseWheelMoved
             * @memberOf MenuItem
             */
            P.MenuItem.prototype.onMouseWheelMoved = {};
        }
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });
        if(!P.MenuItem){
            /**
             * Native API. Returns low level swing component. Applicable only in J2SE swing client.
             * @property component
             * @memberOf MenuItem
             */
            P.MenuItem.prototype.component = {};
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
        if(!P.MenuItem){
            /**
             * Keyboard focus gained by the component event.
             * @property onFocusGained
             * @memberOf MenuItem
             */
            P.MenuItem.prototype.onFocusGained = {};
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
        if(!P.MenuItem){
            /**
             * Horizontal coordinate of the component.
             * @property left
             * @memberOf MenuItem
             */
            P.MenuItem.prototype.left = 0;
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
        if(!P.MenuItem){
            /**
             * The background color of this component.
             * @property background
             * @memberOf MenuItem
             */
            P.MenuItem.prototype.background = {};
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
        if(!P.MenuItem){
            /**
             * Mouse clicked event handler function.
             * @property onMouseClicked
             * @memberOf MenuItem
             */
            P.MenuItem.prototype.onMouseClicked = {};
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
        if(!P.MenuItem){
            /**
             * Mouse exited over the component event handler function.
             * @property onMouseExited
             * @memberOf MenuItem
             */
            P.MenuItem.prototype.onMouseExited = {};
        }
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.MenuItem){
            /**
             * Gets name of this component.
             * @property name
             * @memberOf MenuItem
             */
            P.MenuItem.prototype.name = '';
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
        if(!P.MenuItem){
            /**
             * Width of the component.
             * @property width
             * @memberOf MenuItem
             */
            P.MenuItem.prototype.width = 0;
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
        if(!P.MenuItem){
            /**
             * The font of this component.
             * @property font
             * @memberOf MenuItem
             */
            P.MenuItem.prototype.font = {};
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
        if(!P.MenuItem){
            /**
             * Key pressed event handler function.
             * @property onKeyPressed
             * @memberOf MenuItem
             */
            P.MenuItem.prototype.onKeyPressed = {};
        }
    };        Object.defineProperty(P.MenuItem.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!P.MenuItem){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf MenuItem
         */
        P.MenuItem.prototype.focus = function(){};
    }

})();