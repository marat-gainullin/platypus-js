(function() {
    var javaClass = Java.type("com.eas.client.forms.api.containers.SplitPane");
    javaClass.setPublisher(function(aDelegate) {
        return new P.SplitPane(null, aDelegate);
    });
    
    /**
     * <code>SplitPane</code> is used to divide two (and only two) components. By default uses horisontal orientation.
     * @param orientation <code>Orientation.HORIZONTAL</code> or <code>Orientation.VERTICAL</code> (optional).
     * @constructor SplitPane SplitPane
     */
    P.SplitPane = function SplitPane(orientation) {
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
        if(SplitPane.superclass)
            SplitPane.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "SplitPane", {value: SplitPane});
    Object.defineProperty(SplitPane.prototype, "cursor", {
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
    if(!SplitPane){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.cursor = {};
    }
    Object.defineProperty(SplitPane.prototype, "onMouseDragged", {
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
    if(!SplitPane){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.onMouseDragged = {};
    }
    Object.defineProperty(SplitPane.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!SplitPane){
        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.parent = {};
    }
    Object.defineProperty(SplitPane.prototype, "onMouseReleased", {
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
    if(!SplitPane){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.onMouseReleased = {};
    }
    Object.defineProperty(SplitPane.prototype, "onFocusLost", {
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
    if(!SplitPane){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.onFocusLost = {};
    }
    Object.defineProperty(SplitPane.prototype, "onMousePressed", {
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
    if(!SplitPane){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.onMousePressed = {};
    }
    Object.defineProperty(SplitPane.prototype, "foreground", {
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
    if(!SplitPane){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.foreground = {};
    }
    Object.defineProperty(SplitPane.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!SplitPane){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.error = '';
    }
    Object.defineProperty(SplitPane.prototype, "enabled", {
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
    if(!SplitPane){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.enabled = true;
    }
    Object.defineProperty(SplitPane.prototype, "onComponentMoved", {
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
    if(!SplitPane){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.onComponentMoved = {};
    }
    Object.defineProperty(SplitPane.prototype, "dividerLocation", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.dividerLocation;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.dividerLocation = P.boxAsJava(aValue);
        }
    });
    if(!SplitPane){
        /**
         * The split pane divider's location in pixels.
         * @property dividerLocation
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.dividerLocation = 0;
    }
    Object.defineProperty(SplitPane.prototype, "onComponentAdded", {
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
    if(!SplitPane){
        /**
         * Component added event hanler function.
         * @property onComponentAdded
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.onComponentAdded = {};
    }
    Object.defineProperty(SplitPane.prototype, "secondComponent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.secondComponent;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.secondComponent = P.boxAsJava(aValue);
        }
    });
    if(!SplitPane){
        /**
         * The second component of the container.
         * @property secondComponent
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.secondComponent = {};
    }
    Object.defineProperty(SplitPane.prototype, "firstComponent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.firstComponent;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.firstComponent = P.boxAsJava(aValue);
        }
    });
    if(!SplitPane){
        /**
         * The first component of the container.
         * @property firstComponent
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.firstComponent = {};
    }
    Object.defineProperty(SplitPane.prototype, "componentPopupMenu", {
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
    if(!SplitPane){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(SplitPane.prototype, "top", {
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
    if(!SplitPane){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.top = 0;
    }
    Object.defineProperty(SplitPane.prototype, "children", {
        get: function() {
            var delegate = this.unwrap();
            if (!invalidatable) {
                var value = delegate.children;
                invalidatable = P.boxAsJs(value);
            }
            return invalidatable;
        }
    });
    if(!SplitPane){
        /**
         * Gets the container's children components.
         * @property children
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.children = [];
    }
    Object.defineProperty(SplitPane.prototype, "onComponentResized", {
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
    if(!SplitPane){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.onComponentResized = {};
    }
    Object.defineProperty(SplitPane.prototype, "onMouseEntered", {
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
    if(!SplitPane){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.onMouseEntered = {};
    }
    Object.defineProperty(SplitPane.prototype, "toolTipText", {
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
    if(!SplitPane){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.toolTipText = '';
    }
    Object.defineProperty(SplitPane.prototype, "height", {
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
    if(!SplitPane){
        /**
         * Height of the component.
         * @property height
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.height = 0;
    }
    Object.defineProperty(SplitPane.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!SplitPane){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.element = {};
    }
    Object.defineProperty(SplitPane.prototype, "onComponentShown", {
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
    if(!SplitPane){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.onComponentShown = {};
    }
    Object.defineProperty(SplitPane.prototype, "orientation", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.orientation;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.orientation = P.boxAsJava(aValue);
        }
    });
    if(!SplitPane){
        /**
         * The orientation of the container.
         * @property orientation
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.orientation = 0;
    }
    Object.defineProperty(SplitPane.prototype, "onMouseMoved", {
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
    if(!SplitPane){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.onMouseMoved = {};
    }
    Object.defineProperty(SplitPane.prototype, "opaque", {
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
    if(!SplitPane){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.opaque = true;
    }
    Object.defineProperty(SplitPane.prototype, "visible", {
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
    if(!SplitPane){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.visible = true;
    }
    Object.defineProperty(SplitPane.prototype, "onComponentHidden", {
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
    if(!SplitPane){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.onComponentHidden = {};
    }
    Object.defineProperty(SplitPane.prototype, "nextFocusableComponent", {
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
    if(!SplitPane){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(SplitPane.prototype, "count", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.count;
            return P.boxAsJs(value);
        }
    });
    if(!SplitPane){
        /**
         * Gets the number of components in this panel.
         * @property count
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.count = 0;
    }
    Object.defineProperty(SplitPane.prototype, "onActionPerformed", {
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
    if(!SplitPane){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.onActionPerformed = {};
    }
    Object.defineProperty(SplitPane.prototype, "onKeyReleased", {
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
    if(!SplitPane){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.onKeyReleased = {};
    }
    Object.defineProperty(SplitPane.prototype, "focusable", {
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
    if(!SplitPane){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.focusable = true;
    }
    Object.defineProperty(SplitPane.prototype, "onKeyTyped", {
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
    if(!SplitPane){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.onKeyTyped = {};
    }
    Object.defineProperty(SplitPane.prototype, "onMouseWheelMoved", {
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
    if(!SplitPane){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(SplitPane.prototype, "onComponentRemoved", {
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
    if(!SplitPane){
        /**
         * Component removed event handler function.
         * @property onComponentRemoved
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.onComponentRemoved = {};
    }
    Object.defineProperty(SplitPane.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!SplitPane){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.component = {};
    }
    Object.defineProperty(SplitPane.prototype, "onFocusGained", {
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
    if(!SplitPane){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.onFocusGained = {};
    }
    Object.defineProperty(SplitPane.prototype, "left", {
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
    if(!SplitPane){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.left = 0;
    }
    Object.defineProperty(SplitPane.prototype, "background", {
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
    if(!SplitPane){
        /**
         * The background color of this component.
         * @property background
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.background = {};
    }
    Object.defineProperty(SplitPane.prototype, "onMouseClicked", {
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
    if(!SplitPane){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.onMouseClicked = {};
    }
    Object.defineProperty(SplitPane.prototype, "onMouseExited", {
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
    if(!SplitPane){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.onMouseExited = {};
    }
    Object.defineProperty(SplitPane.prototype, "oneTouchExpandable", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.oneTouchExpandable;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.oneTouchExpandable = P.boxAsJava(aValue);
        }
    });
    if(!SplitPane){
        /**
         * <code>true</code> if the pane is one touch expandable.
         * @property oneTouchExpandable
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.oneTouchExpandable = true;
    }
    Object.defineProperty(SplitPane.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!SplitPane){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.name = '';
    }
    Object.defineProperty(SplitPane.prototype, "width", {
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
    if(!SplitPane){
        /**
         * Width of the component.
         * @property width
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.width = 0;
    }
    Object.defineProperty(SplitPane.prototype, "font", {
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
    if(!SplitPane){
        /**
         * The font of this component.
         * @property font
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.font = {};
    }
    Object.defineProperty(SplitPane.prototype, "onKeyPressed", {
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
    if(!SplitPane){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.onKeyPressed = {};
    }
    Object.defineProperty(SplitPane.prototype, "add", {
        value: function(component) {
            var delegate = this.unwrap();
            var value = delegate.add(P.boxAsJava(component));
            return P.boxAsJs(value);
        }
    });
    if(!SplitPane){
        /**
         * Appends the specified component to the end of this container.
         * @param component the component to add.
         * @method add
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.add = function(component){};
    }
    Object.defineProperty(SplitPane.prototype, "child", {
        value: function(index) {
            var delegate = this.unwrap();
            var value = delegate.child(P.boxAsJava(index));
            return P.boxAsJs(value);
        }
    });
    if(!SplitPane){
        /**
         * Gets the container's nth component.
         * @param index the component's index in the container
         * @return the child component
         * @method child
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.child = function(index){};
    }
    Object.defineProperty(SplitPane.prototype, "remove", {
        value: function(component) {
            var delegate = this.unwrap();
            var value = delegate.remove(P.boxAsJava(component));
            return P.boxAsJs(value);
        }
    });
    if(!SplitPane){
        /**
         * Removes the specified component from this container.
         * @param component the component to remove
         * @method remove
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.remove = function(component){};
    }
    Object.defineProperty(SplitPane.prototype, "clear", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.clear();
            return P.boxAsJs(value);
        }
    });
    if(!SplitPane){
        /**
         * Removes all the components from this container.
         * @method clear
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.clear = function(){};
    }
    Object.defineProperty(SplitPane.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!SplitPane){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf SplitPane
         */
        P.SplitPane.prototype.focus = function(){};
    }
})();