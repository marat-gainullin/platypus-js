(function() {
    var javaClass = Java.type("com.eas.client.forms.api.containers.BoxPane");
    javaClass.setPublisher(function(aDelegate) {
        return new P.BoxPane(null, aDelegate);
    });
    
    /**
     * A container with Box Layout. By default uses horisontal orientation.
     * @param orientation Orientation.HORIZONTAL or Orientation.VERTICAL (optional).
     * @constructor BoxPane BoxPane
     */
    P.BoxPane = function BoxPane(orientation) {
        var maxArgs = 1;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 1 ? new javaClass(P.boxAsJava(orientation))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(BoxPane.superclass)
            BoxPane.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "BoxPane", {value: BoxPane});
    Object.defineProperty(BoxPane.prototype, "cursor", {
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
    if(!BoxPane){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.cursor = {};
    }
    Object.defineProperty(BoxPane.prototype, "onMouseDragged", {
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
    if(!BoxPane){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.onMouseDragged = {};
    }
    Object.defineProperty(BoxPane.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!BoxPane){
        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.parent = {};
    }
    Object.defineProperty(BoxPane.prototype, "onMouseReleased", {
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
    if(!BoxPane){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.onMouseReleased = {};
    }
    Object.defineProperty(BoxPane.prototype, "onFocusLost", {
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
    if(!BoxPane){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.onFocusLost = {};
    }
    Object.defineProperty(BoxPane.prototype, "onMousePressed", {
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
    if(!BoxPane){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.onMousePressed = {};
    }
    Object.defineProperty(BoxPane.prototype, "foreground", {
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
    if(!BoxPane){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.foreground = {};
    }
    Object.defineProperty(BoxPane.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!BoxPane){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.error = '';
    }
    Object.defineProperty(BoxPane.prototype, "enabled", {
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
    if(!BoxPane){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.enabled = true;
    }
    Object.defineProperty(BoxPane.prototype, "onComponentMoved", {
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
    if(!BoxPane){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.onComponentMoved = {};
    }
    Object.defineProperty(BoxPane.prototype, "onComponentAdded", {
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
    if(!BoxPane){
        /**
         * Component added event hanler function.
         * @property onComponentAdded
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.onComponentAdded = {};
    }
    Object.defineProperty(BoxPane.prototype, "componentPopupMenu", {
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
    if(!BoxPane){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(BoxPane.prototype, "top", {
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
    if(!BoxPane){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.top = 0;
    }
    Object.defineProperty(BoxPane.prototype, "children", {
        get: function() {
            var delegate = this.unwrap();
            if (!invalidatable) {
                var value = delegate.children;
                invalidatable = P.boxAsJs(value);
            }
            return invalidatable;
        }
    });
    if(!BoxPane){
        /**
         * Gets the container's children components.
         * @property children
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.children = [];
    }
    Object.defineProperty(BoxPane.prototype, "onComponentResized", {
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
    if(!BoxPane){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.onComponentResized = {};
    }
    Object.defineProperty(BoxPane.prototype, "onMouseEntered", {
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
    if(!BoxPane){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.onMouseEntered = {};
    }
    Object.defineProperty(BoxPane.prototype, "toolTipText", {
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
    if(!BoxPane){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.toolTipText = '';
    }
    Object.defineProperty(BoxPane.prototype, "height", {
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
    if(!BoxPane){
        /**
         * Height of the component.
         * @property height
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.height = 0;
    }
    Object.defineProperty(BoxPane.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!BoxPane){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.element = {};
    }
    Object.defineProperty(BoxPane.prototype, "onComponentShown", {
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
    if(!BoxPane){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.onComponentShown = {};
    }
    Object.defineProperty(BoxPane.prototype, "orientation", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.orientation;
            return P.boxAsJs(value);
        }
    });
    if(!BoxPane){
        /**
         * Box orientation of this container.
         * @property orientation
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.orientation = 0;
    }
    Object.defineProperty(BoxPane.prototype, "onMouseMoved", {
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
    if(!BoxPane){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.onMouseMoved = {};
    }
    Object.defineProperty(BoxPane.prototype, "opaque", {
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
    if(!BoxPane){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.opaque = true;
    }
    Object.defineProperty(BoxPane.prototype, "visible", {
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
    if(!BoxPane){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.visible = true;
    }
    Object.defineProperty(BoxPane.prototype, "onComponentHidden", {
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
    if(!BoxPane){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.onComponentHidden = {};
    }
    Object.defineProperty(BoxPane.prototype, "nextFocusableComponent", {
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
    if(!BoxPane){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(BoxPane.prototype, "count", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.count;
            return P.boxAsJs(value);
        }
    });
    if(!BoxPane){
        /**
         * Gets the number of components in this panel.
         * @property count
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.count = 0;
    }
    Object.defineProperty(BoxPane.prototype, "onActionPerformed", {
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
    if(!BoxPane){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.onActionPerformed = {};
    }
    Object.defineProperty(BoxPane.prototype, "onKeyReleased", {
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
    if(!BoxPane){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.onKeyReleased = {};
    }
    Object.defineProperty(BoxPane.prototype, "focusable", {
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
    if(!BoxPane){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.focusable = true;
    }
    Object.defineProperty(BoxPane.prototype, "onKeyTyped", {
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
    if(!BoxPane){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.onKeyTyped = {};
    }
    Object.defineProperty(BoxPane.prototype, "onMouseWheelMoved", {
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
    if(!BoxPane){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(BoxPane.prototype, "onComponentRemoved", {
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
    if(!BoxPane){
        /**
         * Component removed event handler function.
         * @property onComponentRemoved
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.onComponentRemoved = {};
    }
    Object.defineProperty(BoxPane.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!BoxPane){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.component = {};
    }
    Object.defineProperty(BoxPane.prototype, "onFocusGained", {
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
    if(!BoxPane){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.onFocusGained = {};
    }
    Object.defineProperty(BoxPane.prototype, "left", {
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
    if(!BoxPane){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.left = 0;
    }
    Object.defineProperty(BoxPane.prototype, "background", {
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
    if(!BoxPane){
        /**
         * The background color of this component.
         * @property background
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.background = {};
    }
    Object.defineProperty(BoxPane.prototype, "onMouseClicked", {
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
    if(!BoxPane){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.onMouseClicked = {};
    }
    Object.defineProperty(BoxPane.prototype, "onMouseExited", {
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
    if(!BoxPane){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.onMouseExited = {};
    }
    Object.defineProperty(BoxPane.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!BoxPane){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.name = '';
    }
    Object.defineProperty(BoxPane.prototype, "width", {
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
    if(!BoxPane){
        /**
         * Width of the component.
         * @property width
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.width = 0;
    }
    Object.defineProperty(BoxPane.prototype, "font", {
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
    if(!BoxPane){
        /**
         * The font of this component.
         * @property font
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.font = {};
    }
    Object.defineProperty(BoxPane.prototype, "onKeyPressed", {
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
    if(!BoxPane){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.onKeyPressed = {};
    }
    Object.defineProperty(BoxPane.prototype, "add", {
        value: function(component) {
            var delegate = this.unwrap();
            var value = delegate.add(P.boxAsJava(component));
            return P.boxAsJs(value);
        }
    });
    if(!BoxPane){
        /**
         * Appends the specified component to the end of this container.
         * @param component the component to add
         * @method add
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.add = function(component){};
    }
    Object.defineProperty(BoxPane.prototype, "remove", {
        value: function(component) {
            var delegate = this.unwrap();
            var value = delegate.remove(P.boxAsJava(component));
            return P.boxAsJs(value);
        }
    });
    if(!BoxPane){
        /**
         * Removes the specified component from this container.
         * @param component the component to remove
         * @method remove
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.remove = function(component){};
    }
    Object.defineProperty(BoxPane.prototype, "clear", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.clear();
            return P.boxAsJs(value);
        }
    });
    if(!BoxPane){
        /**
         * Removes all the components from this container.
         * @method clear
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.clear = function(){};
    }
    Object.defineProperty(BoxPane.prototype, "child", {
        value: function(index) {
            var delegate = this.unwrap();
            var value = delegate.child(P.boxAsJava(index));
            return P.boxAsJs(value);
        }
    });
    if(!BoxPane){
        /**
         * Gets the container's nth component.
         * @param index the component's index in the container
         * @return the child component
         * @method child
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.child = function(index){};
    }
    Object.defineProperty(BoxPane.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!BoxPane){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf BoxPane
         */
        P.BoxPane.prototype.focus = function(){};
    }
})();