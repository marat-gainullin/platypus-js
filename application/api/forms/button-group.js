(function() {
    var javaClass = Java.type("com.eas.client.forms.api.containers.ButtonGroup");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ButtonGroup(aDelegate);
    });
    
    /**
     * Creates a multiple-exclusion scope for a set of buttons.
     * Creating a set of buttons with the same <code>ButtonGroup</code> object means that turning "on" one of those buttons turns off all other buttons in the group.
     * @constructor ButtonGroup ButtonGroup
     */
    P.ButtonGroup = function ButtonGroup() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(ButtonGroup.superclass)
            ButtonGroup.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "ButtonGroup", {value: ButtonGroup});
    Object.defineProperty(ButtonGroup.prototype, "cursor", {
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
    if(!ButtonGroup){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.cursor = {};
    }
    Object.defineProperty(ButtonGroup.prototype, "onMouseDragged", {
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
    if(!ButtonGroup){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.onMouseDragged = {};
    }
    Object.defineProperty(ButtonGroup.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!ButtonGroup){
        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.parent = {};
    }
    Object.defineProperty(ButtonGroup.prototype, "onMouseReleased", {
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
    if(!ButtonGroup){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.onMouseReleased = {};
    }
    Object.defineProperty(ButtonGroup.prototype, "onFocusLost", {
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
    if(!ButtonGroup){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.onFocusLost = {};
    }
    Object.defineProperty(ButtonGroup.prototype, "onMousePressed", {
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
    if(!ButtonGroup){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.onMousePressed = {};
    }
    Object.defineProperty(ButtonGroup.prototype, "foreground", {
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
    if(!ButtonGroup){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.foreground = {};
    }
    Object.defineProperty(ButtonGroup.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!ButtonGroup){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.error = '';
    }
    Object.defineProperty(ButtonGroup.prototype, "enabled", {
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
    if(!ButtonGroup){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.enabled = true;
    }
    Object.defineProperty(ButtonGroup.prototype, "onComponentMoved", {
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
    if(!ButtonGroup){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.onComponentMoved = {};
    }
    Object.defineProperty(ButtonGroup.prototype, "onComponentAdded", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onComponentAdded;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onComponentAdded = P.boxAsJava(aValue);
        }
    });
    if(!ButtonGroup){
        /**
         * Component added event hanler function.
         * @property onComponentAdded
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.onComponentAdded = {};
    }
    Object.defineProperty(ButtonGroup.prototype, "componentPopupMenu", {
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
    if(!ButtonGroup){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(ButtonGroup.prototype, "top", {
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
    if(!ButtonGroup){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.top = 0;
    }
    Object.defineProperty(ButtonGroup.prototype, "children", {
        get: function() {
            var delegate = this.unwrap();
            if (!invalidatable) {
                var value = delegate.children;
                invalidatable = P.boxAsJs(value);
            }
            return invalidatable;
        }
    });
    if(!ButtonGroup){
        /**
         * Gets the container's children components.
         * @property children
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.children = [];
    }
    Object.defineProperty(ButtonGroup.prototype, "onComponentResized", {
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
    if(!ButtonGroup){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.onComponentResized = {};
    }
    Object.defineProperty(ButtonGroup.prototype, "onMouseEntered", {
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
    if(!ButtonGroup){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.onMouseEntered = {};
    }
    Object.defineProperty(ButtonGroup.prototype, "toolTipText", {
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
    if(!ButtonGroup){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.toolTipText = '';
    }
    Object.defineProperty(ButtonGroup.prototype, "height", {
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
    if(!ButtonGroup){
        /**
         * Height of the component.
         * @property height
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.height = 0;
    }
    Object.defineProperty(ButtonGroup.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!ButtonGroup){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.element = {};
    }
    Object.defineProperty(ButtonGroup.prototype, "onComponentShown", {
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
    if(!ButtonGroup){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.onComponentShown = {};
    }
    Object.defineProperty(ButtonGroup.prototype, "onMouseMoved", {
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
    if(!ButtonGroup){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.onMouseMoved = {};
    }
    Object.defineProperty(ButtonGroup.prototype, "opaque", {
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
    if(!ButtonGroup){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.opaque = true;
    }
    Object.defineProperty(ButtonGroup.prototype, "visible", {
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
    if(!ButtonGroup){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.visible = true;
    }
    Object.defineProperty(ButtonGroup.prototype, "onComponentHidden", {
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
    if(!ButtonGroup){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.onComponentHidden = {};
    }
    Object.defineProperty(ButtonGroup.prototype, "nextFocusableComponent", {
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
    if(!ButtonGroup){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(ButtonGroup.prototype, "count", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.count;
            return P.boxAsJs(value);
        }
    });
    if(!ButtonGroup){
        /**
         * Gets the number of components in this panel.
         * @property count
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.count = 0;
    }
    Object.defineProperty(ButtonGroup.prototype, "onActionPerformed", {
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
    if(!ButtonGroup){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.onActionPerformed = {};
    }
    Object.defineProperty(ButtonGroup.prototype, "onKeyReleased", {
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
    if(!ButtonGroup){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.onKeyReleased = {};
    }
    Object.defineProperty(ButtonGroup.prototype, "focusable", {
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
    if(!ButtonGroup){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.focusable = true;
    }
    Object.defineProperty(ButtonGroup.prototype, "onKeyTyped", {
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
    if(!ButtonGroup){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.onKeyTyped = {};
    }
    Object.defineProperty(ButtonGroup.prototype, "onMouseWheelMoved", {
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
    if(!ButtonGroup){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(ButtonGroup.prototype, "onItemSelected", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onItemSelected;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onItemSelected = P.boxAsJava(aValue);
        }
    });
    if(!ButtonGroup){
        /**
         * Event that is fired when one of the components is selected in this group.
         * @property onItemSelected
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.onItemSelected = {};
    }
    Object.defineProperty(ButtonGroup.prototype, "onComponentRemoved", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onComponentRemoved;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onComponentRemoved = P.boxAsJava(aValue);
        }
    });
    if(!ButtonGroup){
        /**
         * Component removed event handler function.
         * @property onComponentRemoved
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.onComponentRemoved = {};
    }
    Object.defineProperty(ButtonGroup.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!ButtonGroup){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.component = {};
    }
    Object.defineProperty(ButtonGroup.prototype, "onFocusGained", {
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
    if(!ButtonGroup){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.onFocusGained = {};
    }
    Object.defineProperty(ButtonGroup.prototype, "left", {
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
    if(!ButtonGroup){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.left = 0;
    }
    Object.defineProperty(ButtonGroup.prototype, "background", {
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
    if(!ButtonGroup){
        /**
         * The background color of this component.
         * @property background
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.background = {};
    }
    Object.defineProperty(ButtonGroup.prototype, "onMouseClicked", {
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
    if(!ButtonGroup){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.onMouseClicked = {};
    }
    Object.defineProperty(ButtonGroup.prototype, "onMouseExited", {
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
    if(!ButtonGroup){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.onMouseExited = {};
    }
    Object.defineProperty(ButtonGroup.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!ButtonGroup){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.name = '';
    }
    Object.defineProperty(ButtonGroup.prototype, "width", {
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
    if(!ButtonGroup){
        /**
         * Width of the component.
         * @property width
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.width = 0;
    }
    Object.defineProperty(ButtonGroup.prototype, "font", {
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
    if(!ButtonGroup){
        /**
         * The font of this component.
         * @property font
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.font = {};
    }
    Object.defineProperty(ButtonGroup.prototype, "onKeyPressed", {
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
    if(!ButtonGroup){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.onKeyPressed = {};
    }
    Object.defineProperty(ButtonGroup.prototype, "add", {
        value: function(arg0) {
            var delegate = this.unwrap();
            var value = delegate.add(P.boxAsJava(arg0));
            return P.boxAsJs(value);
        }
    });
    if(!ButtonGroup){
        /**
         * Appends the specified component to the end of this group.
         * @method add
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.add = function(arg0){};
    }
    Object.defineProperty(ButtonGroup.prototype, "remove", {
        value: function(arg0) {
            var delegate = this.unwrap();
            var value = delegate.remove(P.boxAsJava(arg0));
            return P.boxAsJs(value);
        }
    });
    if(!ButtonGroup){
        /**
         * Removes the specified component from the group.
         * @method remove
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.remove = function(arg0){};
    }
    Object.defineProperty(ButtonGroup.prototype, "child", {
        value: function(index) {
            var delegate = this.unwrap();
            var value = delegate.child(P.boxAsJava(index));
            return P.boxAsJs(value);
        }
    });
    if(!ButtonGroup){
        /**
         * Gets the container's nth component.
         * @param index the component's index in the container
         * @return the child component
         * @method child
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.child = function(index){};
    }
    Object.defineProperty(ButtonGroup.prototype, "clear", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.clear();
            return P.boxAsJs(value);
        }
    });
    if(!ButtonGroup){
        /**
         * Removes all the components from this container.
         * @method clear
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.clear = function(){};
    }
    Object.defineProperty(ButtonGroup.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!ButtonGroup){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.focus = function(){};
    }
})();