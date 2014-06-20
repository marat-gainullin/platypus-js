(function() {
    var javaClass = Java.type("com.eas.client.forms.api.containers.BorderPane");
    javaClass.setPublisher(function(aDelegate) {
        return new P.BorderPane(null, null, aDelegate);
    });
    
    /**
     * A container with Border Layout.
     * @param hgap the horizontal gap (optional).
     * @param vgap the vertical gap (optional).
     * @constructor BorderPane BorderPane
     */
    P.BorderPane = function BorderPane(hgap, vgap) {
        var maxArgs = 2;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 2 ? new javaClass(P.boxAsJava(hgap), P.boxAsJava(vgap))
            : arguments.length === 1 ? new javaClass(P.boxAsJava(hgap))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(BorderPane.superclass)
            BorderPane.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "BorderPane", {value: BorderPane});
    Object.defineProperty(BorderPane.prototype, "cursor", {
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
    if(!BorderPane){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.cursor = {};
    }
    Object.defineProperty(BorderPane.prototype, "onMouseDragged", {
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
    if(!BorderPane){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.onMouseDragged = {};
    }
    Object.defineProperty(BorderPane.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!BorderPane){
        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.parent = {};
    }
    Object.defineProperty(BorderPane.prototype, "onMouseReleased", {
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
    if(!BorderPane){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.onMouseReleased = {};
    }
    Object.defineProperty(BorderPane.prototype, "onFocusLost", {
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
    if(!BorderPane){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.onFocusLost = {};
    }
    Object.defineProperty(BorderPane.prototype, "onMousePressed", {
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
    if(!BorderPane){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.onMousePressed = {};
    }
    Object.defineProperty(BorderPane.prototype, "foreground", {
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
    if(!BorderPane){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.foreground = {};
    }
    Object.defineProperty(BorderPane.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!BorderPane){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.error = '';
    }
    Object.defineProperty(BorderPane.prototype, "enabled", {
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
    if(!BorderPane){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.enabled = true;
    }
    Object.defineProperty(BorderPane.prototype, "onComponentMoved", {
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
    if(!BorderPane){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.onComponentMoved = {};
    }
    Object.defineProperty(BorderPane.prototype, "onComponentAdded", {
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
    if(!BorderPane){
        /**
         * Component added event hanler function.
         * @property onComponentAdded
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.onComponentAdded = {};
    }
    Object.defineProperty(BorderPane.prototype, "componentPopupMenu", {
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
    if(!BorderPane){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(BorderPane.prototype, "top", {
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
    if(!BorderPane){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.top = 0;
    }
    Object.defineProperty(BorderPane.prototype, "children", {
        get: function() {
            var delegate = this.unwrap();
            if (!invalidatable) {
                var value = delegate.children;
                invalidatable = P.boxAsJs(value);
            }
            return invalidatable;
        }
    });
    if(!BorderPane){
        /**
         * Gets the container's children components.
         * @property children
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.children = [];
    }
    Object.defineProperty(BorderPane.prototype, "onComponentResized", {
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
    if(!BorderPane){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.onComponentResized = {};
    }
    Object.defineProperty(BorderPane.prototype, "onMouseEntered", {
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
    if(!BorderPane){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.onMouseEntered = {};
    }
    Object.defineProperty(BorderPane.prototype, "toolTipText", {
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
    if(!BorderPane){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.toolTipText = '';
    }
    Object.defineProperty(BorderPane.prototype, "height", {
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
    if(!BorderPane){
        /**
         * Height of the component.
         * @property height
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.height = 0;
    }
    Object.defineProperty(BorderPane.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!BorderPane){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.element = {};
    }
    Object.defineProperty(BorderPane.prototype, "onComponentShown", {
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
    if(!BorderPane){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.onComponentShown = {};
    }
    Object.defineProperty(BorderPane.prototype, "onMouseMoved", {
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
    if(!BorderPane){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.onMouseMoved = {};
    }
    Object.defineProperty(BorderPane.prototype, "topComponent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.topComponent;
            return P.boxAsJs(value);
        }
    });
    if(!BorderPane){
        /**
         * The component added using HorizontalPosition.TOP constraint.
         * If no component at the container on this constraint then set to <code>null</code>.
         * @property topComponent
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.topComponent = {};
    }
    Object.defineProperty(BorderPane.prototype, "opaque", {
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
    if(!BorderPane){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.opaque = true;
    }
    Object.defineProperty(BorderPane.prototype, "visible", {
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
    if(!BorderPane){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.visible = true;
    }
    Object.defineProperty(BorderPane.prototype, "bottomComponent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.bottomComponent;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.bottomComponent = P.boxAsJava(aValue);
        }
    });
    if(!BorderPane){
        /**
         * The component added using HorizontalPosition.BOTTOM constraint.
         * If no component at the container on this constraint then set to <code>null</code>.
         * @property bottomComponent
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.bottomComponent = {};
    }
    Object.defineProperty(BorderPane.prototype, "onComponentHidden", {
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
    if(!BorderPane){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.onComponentHidden = {};
    }
    Object.defineProperty(BorderPane.prototype, "nextFocusableComponent", {
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
    if(!BorderPane){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(BorderPane.prototype, "count", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.count;
            return P.boxAsJs(value);
        }
    });
    if(!BorderPane){
        /**
         * Gets the number of components in this panel.
         * @property count
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.count = 0;
    }
    Object.defineProperty(BorderPane.prototype, "onActionPerformed", {
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
    if(!BorderPane){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.onActionPerformed = {};
    }
    Object.defineProperty(BorderPane.prototype, "onKeyReleased", {
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
    if(!BorderPane){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.onKeyReleased = {};
    }
    Object.defineProperty(BorderPane.prototype, "focusable", {
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
    if(!BorderPane){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.focusable = true;
    }
    Object.defineProperty(BorderPane.prototype, "onKeyTyped", {
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
    if(!BorderPane){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.onKeyTyped = {};
    }
    Object.defineProperty(BorderPane.prototype, "rightComponent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.rightComponent;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.rightComponent = P.boxAsJava(aValue);
        }
    });
    if(!BorderPane){
        /**
         * The component added using HorizontalPosition.RIGHT constraint.
         * If no component at the container on this constraint then set to <code>null</code>.
         * @property rightComponent
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.rightComponent = {};
    }
    Object.defineProperty(BorderPane.prototype, "onMouseWheelMoved", {
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
    if(!BorderPane){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(BorderPane.prototype, "onComponentRemoved", {
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
    if(!BorderPane){
        /**
         * Component removed event handler function.
         * @property onComponentRemoved
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.onComponentRemoved = {};
    }
    Object.defineProperty(BorderPane.prototype, "leftComponent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.leftComponent;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.leftComponent = P.boxAsJava(aValue);
        }
    });
    if(!BorderPane){
        /**
         * The component added using HorizontalPosition.LEFT constraint.
         * If no component at this constraint then set to <code>null</code>.
         * @property leftComponent
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.leftComponent = {};
    }
    Object.defineProperty(BorderPane.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!BorderPane){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.component = {};
    }
    Object.defineProperty(BorderPane.prototype, "onFocusGained", {
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
    if(!BorderPane){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.onFocusGained = {};
    }
    Object.defineProperty(BorderPane.prototype, "left", {
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
    if(!BorderPane){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.left = 0;
    }
    Object.defineProperty(BorderPane.prototype, "background", {
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
    if(!BorderPane){
        /**
         * The background color of this component.
         * @property background
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.background = {};
    }
    Object.defineProperty(BorderPane.prototype, "onMouseClicked", {
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
    if(!BorderPane){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.onMouseClicked = {};
    }
    Object.defineProperty(BorderPane.prototype, "onMouseExited", {
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
    if(!BorderPane){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.onMouseExited = {};
    }
    Object.defineProperty(BorderPane.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!BorderPane){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.name = '';
    }
    Object.defineProperty(BorderPane.prototype, "width", {
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
    if(!BorderPane){
        /**
         * Width of the component.
         * @property width
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.width = 0;
    }
    Object.defineProperty(BorderPane.prototype, "centerComponent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.centerComponent;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.centerComponent = P.boxAsJava(aValue);
        }
    });
    if(!BorderPane){
        /**
         * The component added using HorizontalPosition.CENTER constraint.
         * If no component at the container on this constraint then set to <code>null</code>.
         * @property centerComponent
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.centerComponent = {};
    }
    Object.defineProperty(BorderPane.prototype, "font", {
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
    if(!BorderPane){
        /**
         * The font of this component.
         * @property font
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.font = {};
    }
    Object.defineProperty(BorderPane.prototype, "onKeyPressed", {
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
    if(!BorderPane){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.onKeyPressed = {};
    }
    Object.defineProperty(BorderPane.prototype, "add", {
        value: function(component, place, size) {
            var delegate = this.unwrap();
            var value = delegate.add(P.boxAsJava(component), P.boxAsJava(place), P.boxAsJava(size));
            return P.boxAsJs(value);
        }
    });
    if(!BorderPane){
        /**
         * Appends the specified component to this container on the specified placement.
         * @param component the component to add.
         * @param place the placement in the container: <code>HorizontalPosition.LEFT</code>, <code>HorizontalPosition.CENTER</code>, <code>HorizontalPosition.RIGHT</code>, <code>VerticalPosition.TOP</code> or <code>VerticalPosition.BOTTOM</code> (optional).
         * @param size the size of the component by the provided place direction (optional).
         * @method add
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.add = function(component, place, size){};
    }
    Object.defineProperty(BorderPane.prototype, "child", {
        value: function(index) {
            var delegate = this.unwrap();
            var value = delegate.child(P.boxAsJava(index));
            return P.boxAsJs(value);
        }
    });
    if(!BorderPane){
        /**
         * Gets the container's nth component.
         * @param index the component's index in the container
         * @return the child component
         * @method child
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.child = function(index){};
    }
    Object.defineProperty(BorderPane.prototype, "remove", {
        value: function(component) {
            var delegate = this.unwrap();
            var value = delegate.remove(P.boxAsJava(component));
            return P.boxAsJs(value);
        }
    });
    if(!BorderPane){
        /**
         * Removes the specified component from this container.
         * @param component the component to remove
         * @method remove
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.remove = function(component){};
    }
    Object.defineProperty(BorderPane.prototype, "clear", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.clear();
            return P.boxAsJs(value);
        }
    });
    if(!BorderPane){
        /**
         * Removes all the components from this container.
         * @method clear
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.clear = function(){};
    }
    Object.defineProperty(BorderPane.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!BorderPane){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf BorderPane
         */
        P.BorderPane.prototype.focus = function(){};
    }
})();