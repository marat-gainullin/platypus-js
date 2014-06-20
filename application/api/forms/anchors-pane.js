(function() {
    var javaClass = Java.type("com.eas.client.forms.api.containers.AnchorsPane");
    javaClass.setPublisher(function(aDelegate) {
        return new P.AnchorsPane(aDelegate);
    });
    
    /**
     * A container with Anchors Layout.
     * @constructor AnchorsPane AnchorsPane
     */
    P.AnchorsPane = function AnchorsPane() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(AnchorsPane.superclass)
            AnchorsPane.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "AnchorsPane", {value: AnchorsPane});
    Object.defineProperty(AnchorsPane.prototype, "cursor", {
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
    if(!AnchorsPane){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.cursor = {};
    }
    Object.defineProperty(AnchorsPane.prototype, "onMouseDragged", {
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
    if(!AnchorsPane){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.onMouseDragged = {};
    }
    Object.defineProperty(AnchorsPane.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!AnchorsPane){
        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.parent = {};
    }
    Object.defineProperty(AnchorsPane.prototype, "onMouseReleased", {
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
    if(!AnchorsPane){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.onMouseReleased = {};
    }
    Object.defineProperty(AnchorsPane.prototype, "onFocusLost", {
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
    if(!AnchorsPane){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.onFocusLost = {};
    }
    Object.defineProperty(AnchorsPane.prototype, "onMousePressed", {
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
    if(!AnchorsPane){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.onMousePressed = {};
    }
    Object.defineProperty(AnchorsPane.prototype, "foreground", {
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
    if(!AnchorsPane){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.foreground = {};
    }
    Object.defineProperty(AnchorsPane.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!AnchorsPane){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.error = '';
    }
    Object.defineProperty(AnchorsPane.prototype, "enabled", {
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
    if(!AnchorsPane){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.enabled = true;
    }
    Object.defineProperty(AnchorsPane.prototype, "onComponentMoved", {
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
    if(!AnchorsPane){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.onComponentMoved = {};
    }
    Object.defineProperty(AnchorsPane.prototype, "onComponentAdded", {
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
    if(!AnchorsPane){
        /**
         * Component added event hanler function.
         * @property onComponentAdded
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.onComponentAdded = {};
    }
    Object.defineProperty(AnchorsPane.prototype, "componentPopupMenu", {
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
    if(!AnchorsPane){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(AnchorsPane.prototype, "top", {
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
    if(!AnchorsPane){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.top = 0;
    }
    Object.defineProperty(AnchorsPane.prototype, "children", {
        get: function() {
            var delegate = this.unwrap();
            if (!invalidatable) {
                var value = delegate.children;
                invalidatable = P.boxAsJs(value);
            }
            return invalidatable;
        }
    });
    if(!AnchorsPane){
        /**
         * Gets the container's children components.
         * @property children
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.children = [];
    }
    Object.defineProperty(AnchorsPane.prototype, "onComponentResized", {
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
    if(!AnchorsPane){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.onComponentResized = {};
    }
    Object.defineProperty(AnchorsPane.prototype, "onMouseEntered", {
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
    if(!AnchorsPane){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.onMouseEntered = {};
    }
    Object.defineProperty(AnchorsPane.prototype, "toolTipText", {
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
    if(!AnchorsPane){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.toolTipText = '';
    }
    Object.defineProperty(AnchorsPane.prototype, "height", {
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
    if(!AnchorsPane){
        /**
         * Height of the component.
         * @property height
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.height = 0;
    }
    Object.defineProperty(AnchorsPane.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!AnchorsPane){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.element = {};
    }
    Object.defineProperty(AnchorsPane.prototype, "onComponentShown", {
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
    if(!AnchorsPane){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.onComponentShown = {};
    }
    Object.defineProperty(AnchorsPane.prototype, "onMouseMoved", {
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
    if(!AnchorsPane){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.onMouseMoved = {};
    }
    Object.defineProperty(AnchorsPane.prototype, "opaque", {
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
    if(!AnchorsPane){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.opaque = true;
    }
    Object.defineProperty(AnchorsPane.prototype, "visible", {
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
    if(!AnchorsPane){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.visible = true;
    }
    Object.defineProperty(AnchorsPane.prototype, "onComponentHidden", {
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
    if(!AnchorsPane){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.onComponentHidden = {};
    }
    Object.defineProperty(AnchorsPane.prototype, "nextFocusableComponent", {
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
    if(!AnchorsPane){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(AnchorsPane.prototype, "count", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.count;
            return P.boxAsJs(value);
        }
    });
    if(!AnchorsPane){
        /**
         * Gets the number of components in this panel.
         * @property count
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.count = 0;
    }
    Object.defineProperty(AnchorsPane.prototype, "onActionPerformed", {
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
    if(!AnchorsPane){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.onActionPerformed = {};
    }
    Object.defineProperty(AnchorsPane.prototype, "onKeyReleased", {
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
    if(!AnchorsPane){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.onKeyReleased = {};
    }
    Object.defineProperty(AnchorsPane.prototype, "focusable", {
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
    if(!AnchorsPane){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.focusable = true;
    }
    Object.defineProperty(AnchorsPane.prototype, "onKeyTyped", {
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
    if(!AnchorsPane){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.onKeyTyped = {};
    }
    Object.defineProperty(AnchorsPane.prototype, "onMouseWheelMoved", {
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
    if(!AnchorsPane){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(AnchorsPane.prototype, "onComponentRemoved", {
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
    if(!AnchorsPane){
        /**
         * Component removed event handler function.
         * @property onComponentRemoved
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.onComponentRemoved = {};
    }
    Object.defineProperty(AnchorsPane.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!AnchorsPane){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.component = {};
    }
    Object.defineProperty(AnchorsPane.prototype, "onFocusGained", {
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
    if(!AnchorsPane){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.onFocusGained = {};
    }
    Object.defineProperty(AnchorsPane.prototype, "left", {
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
    if(!AnchorsPane){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.left = 0;
    }
    Object.defineProperty(AnchorsPane.prototype, "background", {
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
    if(!AnchorsPane){
        /**
         * The background color of this component.
         * @property background
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.background = {};
    }
    Object.defineProperty(AnchorsPane.prototype, "onMouseClicked", {
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
    if(!AnchorsPane){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.onMouseClicked = {};
    }
    Object.defineProperty(AnchorsPane.prototype, "onMouseExited", {
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
    if(!AnchorsPane){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.onMouseExited = {};
    }
    Object.defineProperty(AnchorsPane.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!AnchorsPane){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.name = '';
    }
    Object.defineProperty(AnchorsPane.prototype, "width", {
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
    if(!AnchorsPane){
        /**
         * Width of the component.
         * @property width
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.width = 0;
    }
    Object.defineProperty(AnchorsPane.prototype, "font", {
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
    if(!AnchorsPane){
        /**
         * The font of this component.
         * @property font
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.font = {};
    }
    Object.defineProperty(AnchorsPane.prototype, "onKeyPressed", {
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
    if(!AnchorsPane){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.onKeyPressed = {};
    }
    Object.defineProperty(AnchorsPane.prototype, "add", {
        value: function(component, anchors) {
            var delegate = this.unwrap();
            var value = delegate.add(P.boxAsJava(component), P.boxAsJava(anchors));
            return P.boxAsJs(value);
        }
    });
    if(!AnchorsPane){
        /**
         * Appends the specified component to the container with specified placement.
         * @param component the component to add.
         * @param anchors the anchors object for the component, can contain the following properties: left, width, right, top, height, bottom.
         * @method add
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.add = function(component, anchors){};
    }
    Object.defineProperty(AnchorsPane.prototype, "toBack", {
        value: function(arg0, arg1) {
            var delegate = this.unwrap();
            var value = delegate.toBack(P.boxAsJava(arg0), P.boxAsJava(arg1));
            return P.boxAsJs(value);
        }
    });
    if(!AnchorsPane){
        /**
         * Brings the specified component to back on this panel.
         * @param component the component.
         * @param count steps to move the component (optional).
         * @method toBack
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.toBack = function(arg0, arg1){};
    }
    Object.defineProperty(AnchorsPane.prototype, "toFront", {
        value: function(component, count) {
            var delegate = this.unwrap();
            var value = delegate.toFront(P.boxAsJava(component), P.boxAsJava(count));
            return P.boxAsJs(value);
        }
    });
    if(!AnchorsPane){
        /**
         * Brings the specified component to front on this panel.
         * @param component the component.
         * @param count steps to move the component (optional).
         * @method toFront
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.toFront = function(component, count){};
    }
    Object.defineProperty(AnchorsPane.prototype, "child", {
        value: function(index) {
            var delegate = this.unwrap();
            var value = delegate.child(P.boxAsJava(index));
            return P.boxAsJs(value);
        }
    });
    if(!AnchorsPane){
        /**
         * Gets the container's nth component.
         * @param index the component's index in the container
         * @return the child component
         * @method child
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.child = function(index){};
    }
    Object.defineProperty(AnchorsPane.prototype, "remove", {
        value: function(component) {
            var delegate = this.unwrap();
            var value = delegate.remove(P.boxAsJava(component));
            return P.boxAsJs(value);
        }
    });
    if(!AnchorsPane){
        /**
         * Removes the specified component from this container.
         * @param component the component to remove
         * @method remove
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.remove = function(component){};
    }
    Object.defineProperty(AnchorsPane.prototype, "clear", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.clear();
            return P.boxAsJs(value);
        }
    });
    if(!AnchorsPane){
        /**
         * Removes all the components from this container.
         * @method clear
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.clear = function(){};
    }
    Object.defineProperty(AnchorsPane.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!AnchorsPane){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf AnchorsPane
         */
        P.AnchorsPane.prototype.focus = function(){};
    }
})();