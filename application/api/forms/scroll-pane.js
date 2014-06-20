(function() {
    var javaClass = Java.type("com.eas.client.forms.api.containers.ScrollPane");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ScrollPane(null, aDelegate);
    });
    
    /**
     * Provides a scrollable view of a lightweight component.
     * @param view the component to display in the scrollpane's viewport (optional)
     * @constructor ScrollPane ScrollPane
     */
    P.ScrollPane = function ScrollPane(view) {
        var maxArgs = 1;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 1 ? new javaClass(P.boxAsJava(view))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(ScrollPane.superclass)
            ScrollPane.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "ScrollPane", {value: ScrollPane});
    Object.defineProperty(ScrollPane.prototype, "cursor", {
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
    if(!ScrollPane){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.cursor = {};
    }
    Object.defineProperty(ScrollPane.prototype, "onMouseDragged", {
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
    if(!ScrollPane){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.onMouseDragged = {};
    }
    Object.defineProperty(ScrollPane.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!ScrollPane){
        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.parent = {};
    }
    Object.defineProperty(ScrollPane.prototype, "onMouseReleased", {
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
    if(!ScrollPane){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.onMouseReleased = {};
    }
    Object.defineProperty(ScrollPane.prototype, "onFocusLost", {
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
    if(!ScrollPane){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.onFocusLost = {};
    }
    Object.defineProperty(ScrollPane.prototype, "onMousePressed", {
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
    if(!ScrollPane){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.onMousePressed = {};
    }
    Object.defineProperty(ScrollPane.prototype, "foreground", {
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
    if(!ScrollPane){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.foreground = {};
    }
    Object.defineProperty(ScrollPane.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!ScrollPane){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.error = '';
    }
    Object.defineProperty(ScrollPane.prototype, "enabled", {
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
    if(!ScrollPane){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.enabled = true;
    }
    Object.defineProperty(ScrollPane.prototype, "onComponentMoved", {
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
    if(!ScrollPane){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.onComponentMoved = {};
    }
    Object.defineProperty(ScrollPane.prototype, "onComponentAdded", {
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
    if(!ScrollPane){
        /**
         * Component added event hanler function.
         * @property onComponentAdded
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.onComponentAdded = {};
    }
    Object.defineProperty(ScrollPane.prototype, "view", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.view;
            return P.boxAsJs(value);
        }
    });
    if(!ScrollPane){
        /**
         * The specified component as the scroll pane view.
         * @property view
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.view = {};
    }
    Object.defineProperty(ScrollPane.prototype, "componentPopupMenu", {
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
    if(!ScrollPane){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(ScrollPane.prototype, "top", {
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
    if(!ScrollPane){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.top = 0;
    }
    Object.defineProperty(ScrollPane.prototype, "children", {
        get: function() {
            var delegate = this.unwrap();
            if (!invalidatable) {
                var value = delegate.children;
                invalidatable = P.boxAsJs(value);
            }
            return invalidatable;
        }
    });
    if(!ScrollPane){
        /**
         * Gets the container's children components.
         * @property children
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.children = [];
    }
    Object.defineProperty(ScrollPane.prototype, "onComponentResized", {
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
    if(!ScrollPane){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.onComponentResized = {};
    }
    Object.defineProperty(ScrollPane.prototype, "onMouseEntered", {
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
    if(!ScrollPane){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.onMouseEntered = {};
    }
    Object.defineProperty(ScrollPane.prototype, "toolTipText", {
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
    if(!ScrollPane){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.toolTipText = '';
    }
    Object.defineProperty(ScrollPane.prototype, "height", {
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
    if(!ScrollPane){
        /**
         * Height of the component.
         * @property height
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.height = 0;
    }
    Object.defineProperty(ScrollPane.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!ScrollPane){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.element = {};
    }
    Object.defineProperty(ScrollPane.prototype, "onComponentShown", {
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
    if(!ScrollPane){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.onComponentShown = {};
    }
    Object.defineProperty(ScrollPane.prototype, "onMouseMoved", {
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
    if(!ScrollPane){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.onMouseMoved = {};
    }
    Object.defineProperty(ScrollPane.prototype, "opaque", {
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
    if(!ScrollPane){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.opaque = true;
    }
    Object.defineProperty(ScrollPane.prototype, "visible", {
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
    if(!ScrollPane){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.visible = true;
    }
    Object.defineProperty(ScrollPane.prototype, "onComponentHidden", {
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
    if(!ScrollPane){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.onComponentHidden = {};
    }
    Object.defineProperty(ScrollPane.prototype, "nextFocusableComponent", {
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
    if(!ScrollPane){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(ScrollPane.prototype, "count", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.count;
            return P.boxAsJs(value);
        }
    });
    if(!ScrollPane){
        /**
         * Gets the number of components in this panel.
         * @property count
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.count = 0;
    }
    Object.defineProperty(ScrollPane.prototype, "onActionPerformed", {
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
    if(!ScrollPane){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.onActionPerformed = {};
    }
    Object.defineProperty(ScrollPane.prototype, "onKeyReleased", {
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
    if(!ScrollPane){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.onKeyReleased = {};
    }
    Object.defineProperty(ScrollPane.prototype, "focusable", {
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
    if(!ScrollPane){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.focusable = true;
    }
    Object.defineProperty(ScrollPane.prototype, "onKeyTyped", {
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
    if(!ScrollPane){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.onKeyTyped = {};
    }
    Object.defineProperty(ScrollPane.prototype, "onMouseWheelMoved", {
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
    if(!ScrollPane){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(ScrollPane.prototype, "onComponentRemoved", {
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
    if(!ScrollPane){
        /**
         * Component removed event handler function.
         * @property onComponentRemoved
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.onComponentRemoved = {};
    }
    Object.defineProperty(ScrollPane.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!ScrollPane){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.component = {};
    }
    Object.defineProperty(ScrollPane.prototype, "onFocusGained", {
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
    if(!ScrollPane){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.onFocusGained = {};
    }
    Object.defineProperty(ScrollPane.prototype, "left", {
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
    if(!ScrollPane){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.left = 0;
    }
    Object.defineProperty(ScrollPane.prototype, "background", {
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
    if(!ScrollPane){
        /**
         * The background color of this component.
         * @property background
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.background = {};
    }
    Object.defineProperty(ScrollPane.prototype, "onMouseClicked", {
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
    if(!ScrollPane){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.onMouseClicked = {};
    }
    Object.defineProperty(ScrollPane.prototype, "onMouseExited", {
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
    if(!ScrollPane){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.onMouseExited = {};
    }
    Object.defineProperty(ScrollPane.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!ScrollPane){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.name = '';
    }
    Object.defineProperty(ScrollPane.prototype, "width", {
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
    if(!ScrollPane){
        /**
         * Width of the component.
         * @property width
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.width = 0;
    }
    Object.defineProperty(ScrollPane.prototype, "font", {
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
    if(!ScrollPane){
        /**
         * The font of this component.
         * @property font
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.font = {};
    }
    Object.defineProperty(ScrollPane.prototype, "onKeyPressed", {
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
    if(!ScrollPane){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.onKeyPressed = {};
    }
    Object.defineProperty(ScrollPane.prototype, "add", {
        value: function(component) {
            var delegate = this.unwrap();
            var value = delegate.add(P.boxAsJava(component));
            return P.boxAsJs(value);
        }
    });
    if(!ScrollPane){
        /**
         * Sets the specified component as the scroll's view, replacing old view component.
         * @param component the component to add
         * @method add
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.add = function(component){};
    }
    Object.defineProperty(ScrollPane.prototype, "remove", {
        value: function(component) {
            var delegate = this.unwrap();
            var value = delegate.remove(P.boxAsJava(component));
            return P.boxAsJs(value);
        }
    });
    if(!ScrollPane){
        /**
         * Removes the specified component from this container.
         * @param component the component to remove
         * @method remove
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.remove = function(component){};
    }
    Object.defineProperty(ScrollPane.prototype, "child", {
        value: function(arg0) {
            var delegate = this.unwrap();
            var value = delegate.child(P.boxAsJava(arg0));
            return P.boxAsJs(value);
        }
    });
    if(!ScrollPane){
        /**
         * Gets the container's nth component.
         * @param index the component's index in the container
         * @return the child component
         * @method child
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.child = function(arg0){};
    }
    Object.defineProperty(ScrollPane.prototype, "clear", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.clear();
            return P.boxAsJs(value);
        }
    });
    if(!ScrollPane){
        /**
         * Removes all the components from this container.
         * @method clear
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.clear = function(){};
    }
    Object.defineProperty(ScrollPane.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!ScrollPane){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf ScrollPane
         */
        P.ScrollPane.prototype.focus = function(){};
    }
})();