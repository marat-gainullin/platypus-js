(function() {
    var javaClass = Java.type("com.eas.client.forms.menu.RadioMenuItem");
    javaClass.setPublisher(function(aDelegate) {
        return new P.RadioMenuItem(null, null, null, aDelegate);
    });
    
    /**
     * An implementation of a radio button menu item.
     * @param text the text of the component (optional).
     * @param selected <code>true</code> if selected (optional).
     * @param actionPerformed On action performed function (optional).
     * @constructor RadioMenuItem RadioMenuItem
     */
    P.RadioMenuItem = function (text, selected, actionPerformed) {
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
        if(P.RadioMenuItem.superclass)
            P.RadioMenuItem.superclass.constructor.apply(this, arguments);
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
        if(!P.RadioMenuItem){
            /**
             * The mouse <code>Cursor</code> over this component.
             * @property cursor
             * @memberOf RadioMenuItem
             */
            P.RadioMenuItem.prototype.cursor = {};
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
        if(!P.RadioMenuItem){
            /**
             * Mouse dragged event handler function.
             * @property onMouseDragged
             * @memberOf RadioMenuItem
             */
            P.RadioMenuItem.prototype.onMouseDragged = {};
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
        if(!P.RadioMenuItem){
            /**
             * Mouse released event handler function.
             * @property onMouseReleased
             * @memberOf RadioMenuItem
             */
            P.RadioMenuItem.prototype.onMouseReleased = {};
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
        if(!P.RadioMenuItem){
            /**
             * Keyboard focus lost by the component event handler function.
             * @property onFocusLost
             * @memberOf RadioMenuItem
             */
            P.RadioMenuItem.prototype.onFocusLost = {};
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
        if(!P.RadioMenuItem){
            /**
             * Mouse pressed event handler function.
             * @property onMousePressed
             * @memberOf RadioMenuItem
             */
            P.RadioMenuItem.prototype.onMousePressed = {};
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
        if(!P.RadioMenuItem){
            /**
             * The foreground color of this component.
             * @property foreground
             * @memberOf RadioMenuItem
             */
            P.RadioMenuItem.prototype.foreground = {};
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
        if(!P.RadioMenuItem){
            /**
             * An error message of this component.
             * Validation procedure may set this property and subsequent focus lost event will clear it.
             * @property error
             * @memberOf RadioMenuItem
             */
            P.RadioMenuItem.prototype.error = '';
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
        if(!P.RadioMenuItem){
            /**
             * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
             * @property enabled
             * @memberOf RadioMenuItem
             */
            P.RadioMenuItem.prototype.enabled = true;
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
        if(!P.RadioMenuItem){
            /**
             * Component moved event handler function.
             * @property onComponentMoved
             * @memberOf RadioMenuItem
             */
            P.RadioMenuItem.prototype.onComponentMoved = {};
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
        if(!P.RadioMenuItem){
            /**
             * Widget's value.
             * @property jsValue
             * @memberOf RadioMenuItem
             */
            P.RadioMenuItem.prototype.value = {};
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
        if(!P.RadioMenuItem){
            /**
             * <code>PopupMenu</code> that assigned for this component.
             * @property componentPopupMenu
             * @memberOf RadioMenuItem
             */
            P.RadioMenuItem.prototype.componentPopupMenu = {};
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
        if(!P.RadioMenuItem){
            /**
             * Vertical coordinate of the component.
             * @property top
             * @memberOf RadioMenuItem
             */
            P.RadioMenuItem.prototype.top = 0;
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
        if(!P.RadioMenuItem){
            /**
             * Component resized event handler function.
             * @property onComponentResized
             * @memberOf RadioMenuItem
             */
            P.RadioMenuItem.prototype.onComponentResized = {};
        }
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parentWidget;
                return P.boxAsJs(value);
            }
        });
        if(!P.RadioMenuItem){
            /**
             * Parent container of this widget.
             * @property parentWidget
             * @memberOf RadioMenuItem
             */
            P.RadioMenuItem.prototype.parent = {};
        }
        Object.defineProperty(this, "text", {
            get: function() {
                var value = delegate.text;
                return P.boxAsJs(value);
            }
        });
        if(!P.RadioMenuItem){
            /**
             * The menu item text.
             * @property text
             * @memberOf RadioMenuItem
             */
            P.RadioMenuItem.prototype.text = '';
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
        if(!P.RadioMenuItem){
            /**
             * Mouse entered over the component event handler function.
             * @property onMouseEntered
             * @memberOf RadioMenuItem
             */
            P.RadioMenuItem.prototype.onMouseEntered = {};
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
        if(!P.RadioMenuItem){
            /**
             * The tooltip string that has been set with.
             * @property toolTipText
             * @memberOf RadioMenuItem
             */
            P.RadioMenuItem.prototype.toolTipText = '';
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
        if(!P.RadioMenuItem){
            /**
             * <code>true</code> if the menu item is selected.
             * @property selected
             * @memberOf RadioMenuItem
             */
            P.RadioMenuItem.prototype.selected = true;
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
        if(!P.RadioMenuItem){
            /**
             * Height of the component.
             * @property height
             * @memberOf RadioMenuItem
             */
            P.RadioMenuItem.prototype.height = 0;
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
        if(!P.RadioMenuItem){
            /**
             * Component shown event handler function.
             * @property onComponentShown
             * @memberOf RadioMenuItem
             */
            P.RadioMenuItem.prototype.onComponentShown = {};
        }
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });
        if(!P.RadioMenuItem){
            /**
             * Native API. Returns low level html element. Applicable only in HTML5 client.
             * @property element
             * @memberOf RadioMenuItem
             */
            P.RadioMenuItem.prototype.element = {};
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
        if(!P.RadioMenuItem){
            /**
             * Mouse moved event handler function.
             * @property onMouseMoved
             * @memberOf RadioMenuItem
             */
            P.RadioMenuItem.prototype.onMouseMoved = {};
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
        if(!P.RadioMenuItem){
            /**
             * True if this component is completely opaque.
             * @property opaque
             * @memberOf RadioMenuItem
             */
            P.RadioMenuItem.prototype.opaque = true;
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
        if(!P.RadioMenuItem){
            /**
             * Determines whether this component should be visible when its parent is visible.
             * @property visible
             * @memberOf RadioMenuItem
             */
            P.RadioMenuItem.prototype.visible = true;
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
        if(!P.RadioMenuItem){
            /**
             * The ButtonGroup this component belongs to.
             * @property buttonGroup
             * @memberOf RadioMenuItem
             */
            P.RadioMenuItem.prototype.buttonGroup = {};
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
        if(!P.RadioMenuItem){
            /**
             * Component hidden event handler function.
             * @property onComponentHidden
             * @memberOf RadioMenuItem
             */
            P.RadioMenuItem.prototype.onComponentHidden = {};
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
        if(!P.RadioMenuItem){
            /**
             * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
             * @property nextFocusableComponent
             * @memberOf RadioMenuItem
             */
            P.RadioMenuItem.prototype.nextFocusableComponent = {};
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
        if(!P.RadioMenuItem){
            /**
             * Main action performed event handler function.
             * @property onActionPerformed
             * @memberOf RadioMenuItem
             */
            P.RadioMenuItem.prototype.onActionPerformed = {};
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
        if(!P.RadioMenuItem){
            /**
             * Key released event handler function.
             * @property onKeyReleased
             * @memberOf RadioMenuItem
             */
            P.RadioMenuItem.prototype.onKeyReleased = {};
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
        if(!P.RadioMenuItem){
            /**
             * Determines whether this component may be focused.
             * @property focusable
             * @memberOf RadioMenuItem
             */
            P.RadioMenuItem.prototype.focusable = true;
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
        if(!P.RadioMenuItem){
            /**
             * Key typed event handler function.
             * @property onKeyTyped
             * @memberOf RadioMenuItem
             */
            P.RadioMenuItem.prototype.onKeyTyped = {};
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
        if(!P.RadioMenuItem){
            /**
             * Mouse wheel moved event handler function.
             * @property onMouseWheelMoved
             * @memberOf RadioMenuItem
             */
            P.RadioMenuItem.prototype.onMouseWheelMoved = {};
        }
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });
        if(!P.RadioMenuItem){
            /**
             * Native API. Returns low level swing component. Applicable only in J2SE swing client.
             * @property component
             * @memberOf RadioMenuItem
             */
            P.RadioMenuItem.prototype.component = {};
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
        if(!P.RadioMenuItem){
            /**
             * Keyboard focus gained by the component event.
             * @property onFocusGained
             * @memberOf RadioMenuItem
             */
            P.RadioMenuItem.prototype.onFocusGained = {};
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
        if(!P.RadioMenuItem){
            /**
             * Horizontal coordinate of the component.
             * @property left
             * @memberOf RadioMenuItem
             */
            P.RadioMenuItem.prototype.left = 0;
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
        if(!P.RadioMenuItem){
            /**
             * The background color of this component.
             * @property background
             * @memberOf RadioMenuItem
             */
            P.RadioMenuItem.prototype.background = {};
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
        if(!P.RadioMenuItem){
            /**
             * Mouse clicked event handler function.
             * @property onMouseClicked
             * @memberOf RadioMenuItem
             */
            P.RadioMenuItem.prototype.onMouseClicked = {};
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
        if(!P.RadioMenuItem){
            /**
             * Mouse exited over the component event handler function.
             * @property onMouseExited
             * @memberOf RadioMenuItem
             */
            P.RadioMenuItem.prototype.onMouseExited = {};
        }
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.RadioMenuItem){
            /**
             * Gets name of this component.
             * @property name
             * @memberOf RadioMenuItem
             */
            P.RadioMenuItem.prototype.name = '';
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
        if(!P.RadioMenuItem){
            /**
             * Width of the component.
             * @property width
             * @memberOf RadioMenuItem
             */
            P.RadioMenuItem.prototype.width = 0;
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
        if(!P.RadioMenuItem){
            /**
             * The font of this component.
             * @property font
             * @memberOf RadioMenuItem
             */
            P.RadioMenuItem.prototype.font = {};
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
        if(!P.RadioMenuItem){
            /**
             * Key pressed event handler function.
             * @property onKeyPressed
             * @memberOf RadioMenuItem
             */
            P.RadioMenuItem.prototype.onKeyPressed = {};
        }
    };
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.focus = function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        };

})();