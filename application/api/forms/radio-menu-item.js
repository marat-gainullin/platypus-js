(function() {
    var javaClass = Java.type("com.eas.client.forms.api.menu.RadioMenuItem");
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
    P.RadioMenuItem = function RadioMenuItem(text, selected, actionPerformed) {
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
        if(RadioMenuItem.superclass)
            RadioMenuItem.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "RadioMenuItem", {value: RadioMenuItem});
    Object.defineProperty(RadioMenuItem.prototype, "cursor", {
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
    if(!RadioMenuItem){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.cursor = {};
    }
    Object.defineProperty(RadioMenuItem.prototype, "onMouseDragged", {
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
    if(!RadioMenuItem){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.onMouseDragged = {};
    }
    Object.defineProperty(RadioMenuItem.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!RadioMenuItem){
        /**
         * The parent container.
         * @property parent
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.parent = {};
    }
    Object.defineProperty(RadioMenuItem.prototype, "onMouseReleased", {
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
    if(!RadioMenuItem){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.onMouseReleased = {};
    }
    Object.defineProperty(RadioMenuItem.prototype, "onFocusLost", {
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
    if(!RadioMenuItem){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.onFocusLost = {};
    }
    Object.defineProperty(RadioMenuItem.prototype, "onMousePressed", {
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
    if(!RadioMenuItem){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.onMousePressed = {};
    }
    Object.defineProperty(RadioMenuItem.prototype, "foreground", {
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
    if(!RadioMenuItem){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.foreground = {};
    }
    Object.defineProperty(RadioMenuItem.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!RadioMenuItem){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.error = '';
    }
    Object.defineProperty(RadioMenuItem.prototype, "enabled", {
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
    if(!RadioMenuItem){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.enabled = true;
    }
    Object.defineProperty(RadioMenuItem.prototype, "onComponentMoved", {
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
    if(!RadioMenuItem){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.onComponentMoved = {};
    }
    Object.defineProperty(RadioMenuItem.prototype, "componentPopupMenu", {
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
    if(!RadioMenuItem){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(RadioMenuItem.prototype, "top", {
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
    if(!RadioMenuItem){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.top = 0;
    }
    Object.defineProperty(RadioMenuItem.prototype, "onComponentResized", {
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
    if(!RadioMenuItem){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.onComponentResized = {};
    }
    Object.defineProperty(RadioMenuItem.prototype, "text", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.text;
            return P.boxAsJs(value);
        }
    });
    if(!RadioMenuItem){
        /**
         * The menu item text.
         * @property text
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.text = '';
    }
    Object.defineProperty(RadioMenuItem.prototype, "onMouseEntered", {
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
    if(!RadioMenuItem){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.onMouseEntered = {};
    }
    Object.defineProperty(RadioMenuItem.prototype, "selected", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.selected;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.selected = P.boxAsJava(aValue);
        }
    });
    if(!RadioMenuItem){
        /**
         * <code>true</code> if the menu item is selected.
         * @property selected
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.selected = true;
    }
    Object.defineProperty(RadioMenuItem.prototype, "toolTipText", {
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
    if(!RadioMenuItem){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.toolTipText = '';
    }
    Object.defineProperty(RadioMenuItem.prototype, "height", {
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
    if(!RadioMenuItem){
        /**
         * Height of the component.
         * @property height
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.height = 0;
    }
    Object.defineProperty(RadioMenuItem.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!RadioMenuItem){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.element = {};
    }
    Object.defineProperty(RadioMenuItem.prototype, "onComponentShown", {
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
    if(!RadioMenuItem){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.onComponentShown = {};
    }
    Object.defineProperty(RadioMenuItem.prototype, "onMouseMoved", {
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
    if(!RadioMenuItem){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.onMouseMoved = {};
    }
    Object.defineProperty(RadioMenuItem.prototype, "buttonGroup", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.buttonGroup;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.buttonGroup = P.boxAsJava(aValue);
        }
    });
    if(!RadioMenuItem){
        /**
         * The ButtonGroup this component belongs to.
         * @property buttonGroup
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.buttonGroup = {};
    }
    Object.defineProperty(RadioMenuItem.prototype, "opaque", {
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
    if(!RadioMenuItem){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.opaque = true;
    }
    Object.defineProperty(RadioMenuItem.prototype, "visible", {
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
    if(!RadioMenuItem){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.visible = true;
    }
    Object.defineProperty(RadioMenuItem.prototype, "onComponentHidden", {
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
    if(!RadioMenuItem){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.onComponentHidden = {};
    }
    Object.defineProperty(RadioMenuItem.prototype, "nextFocusableComponent", {
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
    if(!RadioMenuItem){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(RadioMenuItem.prototype, "onActionPerformed", {
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
    if(!RadioMenuItem){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.onActionPerformed = {};
    }
    Object.defineProperty(RadioMenuItem.prototype, "onKeyReleased", {
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
    if(!RadioMenuItem){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.onKeyReleased = {};
    }
    Object.defineProperty(RadioMenuItem.prototype, "focusable", {
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
    if(!RadioMenuItem){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.focusable = true;
    }
    Object.defineProperty(RadioMenuItem.prototype, "onKeyTyped", {
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
    if(!RadioMenuItem){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.onKeyTyped = {};
    }
    Object.defineProperty(RadioMenuItem.prototype, "onMouseWheelMoved", {
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
    if(!RadioMenuItem){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(RadioMenuItem.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!RadioMenuItem){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.component = {};
    }
    Object.defineProperty(RadioMenuItem.prototype, "onFocusGained", {
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
    if(!RadioMenuItem){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.onFocusGained = {};
    }
    Object.defineProperty(RadioMenuItem.prototype, "left", {
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
    if(!RadioMenuItem){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.left = 0;
    }
    Object.defineProperty(RadioMenuItem.prototype, "background", {
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
    if(!RadioMenuItem){
        /**
         * The background color of this component.
         * @property background
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.background = {};
    }
    Object.defineProperty(RadioMenuItem.prototype, "onMouseClicked", {
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
    if(!RadioMenuItem){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.onMouseClicked = {};
    }
    Object.defineProperty(RadioMenuItem.prototype, "onMouseExited", {
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
    if(!RadioMenuItem){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.onMouseExited = {};
    }
    Object.defineProperty(RadioMenuItem.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!RadioMenuItem){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.name = '';
    }
    Object.defineProperty(RadioMenuItem.prototype, "width", {
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
    if(!RadioMenuItem){
        /**
         * Width of the component.
         * @property width
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.width = 0;
    }
    Object.defineProperty(RadioMenuItem.prototype, "font", {
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
    if(!RadioMenuItem){
        /**
         * The font of this component.
         * @property font
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.font = {};
    }
    Object.defineProperty(RadioMenuItem.prototype, "onKeyPressed", {
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
    if(!RadioMenuItem){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.onKeyPressed = {};
    }
    Object.defineProperty(RadioMenuItem.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!RadioMenuItem){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf RadioMenuItem
         */
        P.RadioMenuItem.prototype.focus = function(){};
    }
})();