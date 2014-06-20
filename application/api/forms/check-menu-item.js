(function() {
    var javaClass = Java.type("com.eas.client.forms.api.menu.CheckMenuItem");
    javaClass.setPublisher(function(aDelegate) {
        return new P.CheckMenuItem(null, null, null, aDelegate);
    });
    
    /**
     * A menu item that can be selected or deselected.
     * @param text the text of the component (optional).
     * @param selected <code>true</code> if selected (optional).
     * @param actionPerformed On action performed function (optional).
     * @constructor CheckMenuItem CheckMenuItem
     */
    P.CheckMenuItem = function CheckMenuItem(text, selected, actionPerformed) {
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
        if(CheckMenuItem.superclass)
            CheckMenuItem.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "CheckMenuItem", {value: CheckMenuItem});
    Object.defineProperty(CheckMenuItem.prototype, "cursor", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.cursor;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.cursor = P.boxAsJava(aValue);
        }
    });
    if(!CheckMenuItem){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf CheckMenuItem
         */
        P.CheckMenuItem.prototype.cursor = {};
    }
    Object.defineProperty(CheckMenuItem.prototype, "onMouseDragged", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onMouseDragged;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onMouseDragged = P.boxAsJava(aValue);
        }
    });
    if(!CheckMenuItem){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf CheckMenuItem
         */
        P.CheckMenuItem.prototype.onMouseDragged = {};
    }
    Object.defineProperty(CheckMenuItem.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!CheckMenuItem){
        /**
         * The parent container.
         * @property parent
         * @memberOf CheckMenuItem
         */
        P.CheckMenuItem.prototype.parent = {};
    }
    Object.defineProperty(CheckMenuItem.prototype, "onMouseReleased", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onMouseReleased;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onMouseReleased = P.boxAsJava(aValue);
        }
    });
    if(!CheckMenuItem){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf CheckMenuItem
         */
        P.CheckMenuItem.prototype.onMouseReleased = {};
    }
    Object.defineProperty(CheckMenuItem.prototype, "onFocusLost", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onFocusLost;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onFocusLost = P.boxAsJava(aValue);
        }
    });
    if(!CheckMenuItem){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf CheckMenuItem
         */
        P.CheckMenuItem.prototype.onFocusLost = {};
    }
    Object.defineProperty(CheckMenuItem.prototype, "onMousePressed", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onMousePressed;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onMousePressed = P.boxAsJava(aValue);
        }
    });
    if(!CheckMenuItem){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf CheckMenuItem
         */
        P.CheckMenuItem.prototype.onMousePressed = {};
    }
    Object.defineProperty(CheckMenuItem.prototype, "foreground", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.foreground;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.foreground = P.boxAsJava(aValue);
        }
    });
    if(!CheckMenuItem){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf CheckMenuItem
         */
        P.CheckMenuItem.prototype.foreground = {};
    }
    Object.defineProperty(CheckMenuItem.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!CheckMenuItem){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf CheckMenuItem
         */
        P.CheckMenuItem.prototype.error = '';
    }
    Object.defineProperty(CheckMenuItem.prototype, "enabled", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.enabled;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.enabled = P.boxAsJava(aValue);
        }
    });
    if(!CheckMenuItem){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf CheckMenuItem
         */
        P.CheckMenuItem.prototype.enabled = true;
    }
    Object.defineProperty(CheckMenuItem.prototype, "onComponentMoved", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onComponentMoved;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onComponentMoved = P.boxAsJava(aValue);
        }
    });
    if(!CheckMenuItem){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf CheckMenuItem
         */
        P.CheckMenuItem.prototype.onComponentMoved = {};
    }
    Object.defineProperty(CheckMenuItem.prototype, "componentPopupMenu", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.componentPopupMenu;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.componentPopupMenu = P.boxAsJava(aValue);
        }
    });
    if(!CheckMenuItem){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf CheckMenuItem
         */
        P.CheckMenuItem.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(CheckMenuItem.prototype, "top", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.top;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.top = P.boxAsJava(aValue);
        }
    });
    if(!CheckMenuItem){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf CheckMenuItem
         */
        P.CheckMenuItem.prototype.top = 0;
    }
    Object.defineProperty(CheckMenuItem.prototype, "onComponentResized", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onComponentResized;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onComponentResized = P.boxAsJava(aValue);
        }
    });
    if(!CheckMenuItem){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf CheckMenuItem
         */
        P.CheckMenuItem.prototype.onComponentResized = {};
    }
    Object.defineProperty(CheckMenuItem.prototype, "text", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.text;
            return P.boxAsJs(value);
        }
    });
    if(!CheckMenuItem){
        /**
         * The menu item text.
         * @property text
         * @memberOf CheckMenuItem
         */
        P.CheckMenuItem.prototype.text = '';
    }
    Object.defineProperty(CheckMenuItem.prototype, "onMouseEntered", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onMouseEntered;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onMouseEntered = P.boxAsJava(aValue);
        }
    });
    if(!CheckMenuItem){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf CheckMenuItem
         */
        P.CheckMenuItem.prototype.onMouseEntered = {};
    }
    Object.defineProperty(CheckMenuItem.prototype, "selected", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.selected;
            return P.boxAsJs(value);
        }
    });
    if(!CheckMenuItem){
        /**
         * <code>true</code> if the menu item is selected.
         * @property selected
         * @memberOf CheckMenuItem
         */
        P.CheckMenuItem.prototype.selected = true;
    }
    Object.defineProperty(CheckMenuItem.prototype, "toolTipText", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.toolTipText;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.toolTipText = P.boxAsJava(aValue);
        }
    });
    if(!CheckMenuItem){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf CheckMenuItem
         */
        P.CheckMenuItem.prototype.toolTipText = '';
    }
    Object.defineProperty(CheckMenuItem.prototype, "height", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.height;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.height = P.boxAsJava(aValue);
        }
    });
    if(!CheckMenuItem){
        /**
         * Height of the component.
         * @property height
         * @memberOf CheckMenuItem
         */
        P.CheckMenuItem.prototype.height = 0;
    }
    Object.defineProperty(CheckMenuItem.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!CheckMenuItem){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf CheckMenuItem
         */
        P.CheckMenuItem.prototype.element = {};
    }
    Object.defineProperty(CheckMenuItem.prototype, "onComponentShown", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onComponentShown;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onComponentShown = P.boxAsJava(aValue);
        }
    });
    if(!CheckMenuItem){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf CheckMenuItem
         */
        P.CheckMenuItem.prototype.onComponentShown = {};
    }
    Object.defineProperty(CheckMenuItem.prototype, "onMouseMoved", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onMouseMoved;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onMouseMoved = P.boxAsJava(aValue);
        }
    });
    if(!CheckMenuItem){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf CheckMenuItem
         */
        P.CheckMenuItem.prototype.onMouseMoved = {};
    }
    Object.defineProperty(CheckMenuItem.prototype, "opaque", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.opaque;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.opaque = P.boxAsJava(aValue);
        }
    });
    if(!CheckMenuItem){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf CheckMenuItem
         */
        P.CheckMenuItem.prototype.opaque = true;
    }
    Object.defineProperty(CheckMenuItem.prototype, "visible", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.visible;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.visible = P.boxAsJava(aValue);
        }
    });
    if(!CheckMenuItem){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf CheckMenuItem
         */
        P.CheckMenuItem.prototype.visible = true;
    }
    Object.defineProperty(CheckMenuItem.prototype, "onComponentHidden", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onComponentHidden;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onComponentHidden = P.boxAsJava(aValue);
        }
    });
    if(!CheckMenuItem){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf CheckMenuItem
         */
        P.CheckMenuItem.prototype.onComponentHidden = {};
    }
    Object.defineProperty(CheckMenuItem.prototype, "nextFocusableComponent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.nextFocusableComponent;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.nextFocusableComponent = P.boxAsJava(aValue);
        }
    });
    if(!CheckMenuItem){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf CheckMenuItem
         */
        P.CheckMenuItem.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(CheckMenuItem.prototype, "onActionPerformed", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onActionPerformed;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onActionPerformed = P.boxAsJava(aValue);
        }
    });
    if(!CheckMenuItem){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf CheckMenuItem
         */
        P.CheckMenuItem.prototype.onActionPerformed = {};
    }
    Object.defineProperty(CheckMenuItem.prototype, "onKeyReleased", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onKeyReleased;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onKeyReleased = P.boxAsJava(aValue);
        }
    });
    if(!CheckMenuItem){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf CheckMenuItem
         */
        P.CheckMenuItem.prototype.onKeyReleased = {};
    }
    Object.defineProperty(CheckMenuItem.prototype, "focusable", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.focusable;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.focusable = P.boxAsJava(aValue);
        }
    });
    if(!CheckMenuItem){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf CheckMenuItem
         */
        P.CheckMenuItem.prototype.focusable = true;
    }
    Object.defineProperty(CheckMenuItem.prototype, "onKeyTyped", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onKeyTyped;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onKeyTyped = P.boxAsJava(aValue);
        }
    });
    if(!CheckMenuItem){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf CheckMenuItem
         */
        P.CheckMenuItem.prototype.onKeyTyped = {};
    }
    Object.defineProperty(CheckMenuItem.prototype, "onMouseWheelMoved", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onMouseWheelMoved;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onMouseWheelMoved = P.boxAsJava(aValue);
        }
    });
    if(!CheckMenuItem){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf CheckMenuItem
         */
        P.CheckMenuItem.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(CheckMenuItem.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!CheckMenuItem){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf CheckMenuItem
         */
        P.CheckMenuItem.prototype.component = {};
    }
    Object.defineProperty(CheckMenuItem.prototype, "onFocusGained", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onFocusGained;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onFocusGained = P.boxAsJava(aValue);
        }
    });
    if(!CheckMenuItem){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf CheckMenuItem
         */
        P.CheckMenuItem.prototype.onFocusGained = {};
    }
    Object.defineProperty(CheckMenuItem.prototype, "left", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.left;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.left = P.boxAsJava(aValue);
        }
    });
    if(!CheckMenuItem){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf CheckMenuItem
         */
        P.CheckMenuItem.prototype.left = 0;
    }
    Object.defineProperty(CheckMenuItem.prototype, "background", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.background;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.background = P.boxAsJava(aValue);
        }
    });
    if(!CheckMenuItem){
        /**
         * The background color of this component.
         * @property background
         * @memberOf CheckMenuItem
         */
        P.CheckMenuItem.prototype.background = {};
    }
    Object.defineProperty(CheckMenuItem.prototype, "onMouseClicked", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onMouseClicked;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onMouseClicked = P.boxAsJava(aValue);
        }
    });
    if(!CheckMenuItem){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf CheckMenuItem
         */
        P.CheckMenuItem.prototype.onMouseClicked = {};
    }
    Object.defineProperty(CheckMenuItem.prototype, "onMouseExited", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onMouseExited;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onMouseExited = P.boxAsJava(aValue);
        }
    });
    if(!CheckMenuItem){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf CheckMenuItem
         */
        P.CheckMenuItem.prototype.onMouseExited = {};
    }
    Object.defineProperty(CheckMenuItem.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!CheckMenuItem){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf CheckMenuItem
         */
        P.CheckMenuItem.prototype.name = '';
    }
    Object.defineProperty(CheckMenuItem.prototype, "width", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.width;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.width = P.boxAsJava(aValue);
        }
    });
    if(!CheckMenuItem){
        /**
         * Width of the component.
         * @property width
         * @memberOf CheckMenuItem
         */
        P.CheckMenuItem.prototype.width = 0;
    }
    Object.defineProperty(CheckMenuItem.prototype, "font", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.font;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.font = P.boxAsJava(aValue);
        }
    });
    if(!CheckMenuItem){
        /**
         * The font of this component.
         * @property font
         * @memberOf CheckMenuItem
         */
        P.CheckMenuItem.prototype.font = {};
    }
    Object.defineProperty(CheckMenuItem.prototype, "onKeyPressed", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onKeyPressed;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onKeyPressed = P.boxAsJava(aValue);
        }
    });
    if(!CheckMenuItem){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf CheckMenuItem
         */
        P.CheckMenuItem.prototype.onKeyPressed = {};
    }
    Object.defineProperty(CheckMenuItem.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!CheckMenuItem){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf CheckMenuItem
         */
        P.CheckMenuItem.prototype.focus = function(){};
    }
})();