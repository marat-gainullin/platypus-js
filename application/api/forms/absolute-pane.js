(function() {
    var javaClass = Java.type("com.eas.client.forms.api.containers.AbsolutePane");
    javaClass.setPublisher(function(aDelegate) {
        return new P.AbsolutePane(aDelegate);
    });
    
    /**
     * A container with Absolute Layout.
     * @constructor AbsolutePane AbsolutePane
     */
    P.AbsolutePane = function AbsolutePane() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(AbsolutePane.superclass)
            AbsolutePane.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "AbsolutePane", {value: AbsolutePane});
    Object.defineProperty(AbsolutePane.prototype, "cursor", {
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
    if(!AbsolutePane){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.cursor = {};
    }
    Object.defineProperty(AbsolutePane.prototype, "onMouseDragged", {
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
    if(!AbsolutePane){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.onMouseDragged = {};
    }
    Object.defineProperty(AbsolutePane.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!AbsolutePane){
        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.parent = {};
    }
    Object.defineProperty(AbsolutePane.prototype, "onMouseReleased", {
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
    if(!AbsolutePane){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.onMouseReleased = {};
    }
    Object.defineProperty(AbsolutePane.prototype, "onFocusLost", {
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
    if(!AbsolutePane){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.onFocusLost = {};
    }
    Object.defineProperty(AbsolutePane.prototype, "onMousePressed", {
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
    if(!AbsolutePane){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.onMousePressed = {};
    }
    Object.defineProperty(AbsolutePane.prototype, "foreground", {
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
    if(!AbsolutePane){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.foreground = {};
    }
    Object.defineProperty(AbsolutePane.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!AbsolutePane){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.error = '';
    }
    Object.defineProperty(AbsolutePane.prototype, "enabled", {
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
    if(!AbsolutePane){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.enabled = true;
    }
    Object.defineProperty(AbsolutePane.prototype, "onComponentMoved", {
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
    if(!AbsolutePane){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.onComponentMoved = {};
    }
    Object.defineProperty(AbsolutePane.prototype, "onComponentAdded", {
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
    if(!AbsolutePane){
        /**
         * Component added event hanler function.
         * @property onComponentAdded
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.onComponentAdded = {};
    }
    Object.defineProperty(AbsolutePane.prototype, "componentPopupMenu", {
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
    if(!AbsolutePane){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(AbsolutePane.prototype, "top", {
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
    if(!AbsolutePane){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.top = 0;
    }
    Object.defineProperty(AbsolutePane.prototype, "children", {
        get: function() {
            var delegate = this.unwrap();
            if (!invalidatable) {
                var value = delegate.children;
                invalidatable = P.boxAsJs(value);
            }
            return invalidatable;
        }
    });
    if(!AbsolutePane){
        /**
         * Gets the container's children components.
         * @property children
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.children = [];
    }
    Object.defineProperty(AbsolutePane.prototype, "onComponentResized", {
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
    if(!AbsolutePane){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.onComponentResized = {};
    }
    Object.defineProperty(AbsolutePane.prototype, "onMouseEntered", {
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
    if(!AbsolutePane){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.onMouseEntered = {};
    }
    Object.defineProperty(AbsolutePane.prototype, "toolTipText", {
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
    if(!AbsolutePane){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.toolTipText = '';
    }
    Object.defineProperty(AbsolutePane.prototype, "height", {
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
    if(!AbsolutePane){
        /**
         * Height of the component.
         * @property height
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.height = 0;
    }
    Object.defineProperty(AbsolutePane.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!AbsolutePane){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.element = {};
    }
    Object.defineProperty(AbsolutePane.prototype, "onComponentShown", {
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
    if(!AbsolutePane){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.onComponentShown = {};
    }
    Object.defineProperty(AbsolutePane.prototype, "onMouseMoved", {
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
    if(!AbsolutePane){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.onMouseMoved = {};
    }
    Object.defineProperty(AbsolutePane.prototype, "opaque", {
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
    if(!AbsolutePane){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.opaque = true;
    }
    Object.defineProperty(AbsolutePane.prototype, "visible", {
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
    if(!AbsolutePane){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.visible = true;
    }
    Object.defineProperty(AbsolutePane.prototype, "onComponentHidden", {
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
    if(!AbsolutePane){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.onComponentHidden = {};
    }
    Object.defineProperty(AbsolutePane.prototype, "nextFocusableComponent", {
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
    if(!AbsolutePane){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(AbsolutePane.prototype, "count", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.count;
            return P.boxAsJs(value);
        }
    });
    if(!AbsolutePane){
        /**
         * Gets the number of components in this panel.
         * @property count
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.count = 0;
    }
    Object.defineProperty(AbsolutePane.prototype, "onActionPerformed", {
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
    if(!AbsolutePane){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.onActionPerformed = {};
    }
    Object.defineProperty(AbsolutePane.prototype, "onKeyReleased", {
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
    if(!AbsolutePane){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.onKeyReleased = {};
    }
    Object.defineProperty(AbsolutePane.prototype, "focusable", {
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
    if(!AbsolutePane){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.focusable = true;
    }
    Object.defineProperty(AbsolutePane.prototype, "onKeyTyped", {
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
    if(!AbsolutePane){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.onKeyTyped = {};
    }
    Object.defineProperty(AbsolutePane.prototype, "onMouseWheelMoved", {
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
    if(!AbsolutePane){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(AbsolutePane.prototype, "onComponentRemoved", {
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
    if(!AbsolutePane){
        /**
         * Component removed event handler function.
         * @property onComponentRemoved
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.onComponentRemoved = {};
    }
    Object.defineProperty(AbsolutePane.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!AbsolutePane){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.component = {};
    }
    Object.defineProperty(AbsolutePane.prototype, "onFocusGained", {
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
    if(!AbsolutePane){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.onFocusGained = {};
    }
    Object.defineProperty(AbsolutePane.prototype, "left", {
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
    if(!AbsolutePane){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.left = 0;
    }
    Object.defineProperty(AbsolutePane.prototype, "background", {
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
    if(!AbsolutePane){
        /**
         * The background color of this component.
         * @property background
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.background = {};
    }
    Object.defineProperty(AbsolutePane.prototype, "onMouseClicked", {
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
    if(!AbsolutePane){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.onMouseClicked = {};
    }
    Object.defineProperty(AbsolutePane.prototype, "onMouseExited", {
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
    if(!AbsolutePane){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.onMouseExited = {};
    }
    Object.defineProperty(AbsolutePane.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!AbsolutePane){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.name = '';
    }
    Object.defineProperty(AbsolutePane.prototype, "width", {
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
    if(!AbsolutePane){
        /**
         * Width of the component.
         * @property width
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.width = 0;
    }
    Object.defineProperty(AbsolutePane.prototype, "font", {
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
    if(!AbsolutePane){
        /**
         * The font of this component.
         * @property font
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.font = {};
    }
    Object.defineProperty(AbsolutePane.prototype, "onKeyPressed", {
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
    if(!AbsolutePane){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.onKeyPressed = {};
    }
    Object.defineProperty(AbsolutePane.prototype, "add", {
        value: function(component, anchors) {
            var delegate = this.unwrap();
            var value = delegate.add(P.boxAsJava(component), P.boxAsJava(anchors));
            return P.boxAsJs(value);
        }
    });
    if(!AbsolutePane){
        /**
         * Appends the specified component at left top corner of this container.
         * @param component the component to add.
         * @param anchors the anchors object for the component, can contain the following properties: left, width, top, height.
         * @method add
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.add = function(component, anchors){};
    }
    Object.defineProperty(AbsolutePane.prototype, "toBack", {
        value: function(arg0, arg1) {
            var delegate = this.unwrap();
            var value = delegate.toBack(P.boxAsJava(arg0), P.boxAsJava(arg1));
            return P.boxAsJs(value);
        }
    });
    if(!AbsolutePane){
        /**
         * Brings the specified component to back on this panel.
         * @param component the component
         * @param count steps to move the component (optional)
         * @method toBack
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.toBack = function(arg0, arg1){};
    }
    Object.defineProperty(AbsolutePane.prototype, "toFront", {
        value: function(component, count) {
            var delegate = this.unwrap();
            var value = delegate.toFront(P.boxAsJava(component), P.boxAsJava(count));
            return P.boxAsJs(value);
        }
    });
    if(!AbsolutePane){
        /**
         * Brings the specified component to front on this panel.
         * @param component the component
         * @param count steps to move the component (optional)
         * @method toFront
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.toFront = function(component, count){};
    }
    Object.defineProperty(AbsolutePane.prototype, "child", {
        value: function(index) {
            var delegate = this.unwrap();
            var value = delegate.child(P.boxAsJava(index));
            return P.boxAsJs(value);
        }
    });
    if(!AbsolutePane){
        /**
         * Gets the container's nth component.
         * @param index the component's index in the container
         * @return the child component
         * @method child
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.child = function(index){};
    }
    Object.defineProperty(AbsolutePane.prototype, "remove", {
        value: function(component) {
            var delegate = this.unwrap();
            var value = delegate.remove(P.boxAsJava(component));
            return P.boxAsJs(value);
        }
    });
    if(!AbsolutePane){
        /**
         * Removes the specified component from this container.
         * @param component the component to remove
         * @method remove
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.remove = function(component){};
    }
    Object.defineProperty(AbsolutePane.prototype, "clear", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.clear();
            return P.boxAsJs(value);
        }
    });
    if(!AbsolutePane){
        /**
         * Removes all the components from this container.
         * @method clear
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.clear = function(){};
    }
    Object.defineProperty(AbsolutePane.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!AbsolutePane){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf AbsolutePane
         */
        P.AbsolutePane.prototype.focus = function(){};
    }
})();