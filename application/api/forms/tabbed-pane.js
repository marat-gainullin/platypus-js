(function() {
    var javaClass = Java.type("com.eas.client.forms.api.containers.TabbedPane");
    javaClass.setPublisher(function(aDelegate) {
        return new P.TabbedPane(aDelegate);
    });
    
    /**
     * A component that lets the user switch between a group of components by
     * clicking on a tab with a given title and/or icon.
     * @constructor TabbedPane TabbedPane
     */
    P.TabbedPane = function TabbedPane() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(TabbedPane.superclass)
            TabbedPane.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "TabbedPane", {value: TabbedPane});
    Object.defineProperty(TabbedPane.prototype, "cursor", {
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
    if(!TabbedPane){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.cursor = {};
    }
    Object.defineProperty(TabbedPane.prototype, "onMouseDragged", {
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
    if(!TabbedPane){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.onMouseDragged = {};
    }
    Object.defineProperty(TabbedPane.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!TabbedPane){
        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.parent = {};
    }
    Object.defineProperty(TabbedPane.prototype, "onMouseReleased", {
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
    if(!TabbedPane){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.onMouseReleased = {};
    }
    Object.defineProperty(TabbedPane.prototype, "onFocusLost", {
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
    if(!TabbedPane){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.onFocusLost = {};
    }
    Object.defineProperty(TabbedPane.prototype, "onMousePressed", {
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
    if(!TabbedPane){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.onMousePressed = {};
    }
    Object.defineProperty(TabbedPane.prototype, "foreground", {
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
    if(!TabbedPane){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.foreground = {};
    }
    Object.defineProperty(TabbedPane.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!TabbedPane){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.error = '';
    }
    Object.defineProperty(TabbedPane.prototype, "enabled", {
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
    if(!TabbedPane){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.enabled = true;
    }
    Object.defineProperty(TabbedPane.prototype, "onComponentMoved", {
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
    if(!TabbedPane){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.onComponentMoved = {};
    }
    Object.defineProperty(TabbedPane.prototype, "onComponentAdded", {
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
    if(!TabbedPane){
        /**
         * Component added event hanler function.
         * @property onComponentAdded
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.onComponentAdded = {};
    }
    Object.defineProperty(TabbedPane.prototype, "componentPopupMenu", {
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
    if(!TabbedPane){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(TabbedPane.prototype, "top", {
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
    if(!TabbedPane){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.top = 0;
    }
    Object.defineProperty(TabbedPane.prototype, "children", {
        get: function() {
            var delegate = this.unwrap();
            if (!invalidatable) {
                var value = delegate.children;
                invalidatable = P.boxAsJs(value);
            }
            return invalidatable;
        }
    });
    if(!TabbedPane){
        /**
         * Gets the container's children components.
         * @property children
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.children = [];
    }
    Object.defineProperty(TabbedPane.prototype, "onComponentResized", {
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
    if(!TabbedPane){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.onComponentResized = {};
    }
    Object.defineProperty(TabbedPane.prototype, "onMouseEntered", {
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
    if(!TabbedPane){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.onMouseEntered = {};
    }
    Object.defineProperty(TabbedPane.prototype, "toolTipText", {
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
    if(!TabbedPane){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.toolTipText = '';
    }
    Object.defineProperty(TabbedPane.prototype, "height", {
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
    if(!TabbedPane){
        /**
         * Height of the component.
         * @property height
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.height = 0;
    }
    Object.defineProperty(TabbedPane.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!TabbedPane){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.element = {};
    }
    Object.defineProperty(TabbedPane.prototype, "onComponentShown", {
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
    if(!TabbedPane){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.onComponentShown = {};
    }
    Object.defineProperty(TabbedPane.prototype, "selectedComponent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.selectedComponent;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.selectedComponent = P.boxAsJava(aValue);
        }
    });
    if(!TabbedPane){
        /**
         * The selected component.
         * @property selectedComponent
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.selectedComponent = {};
    }
    Object.defineProperty(TabbedPane.prototype, "onMouseMoved", {
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
    if(!TabbedPane){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.onMouseMoved = {};
    }
    Object.defineProperty(TabbedPane.prototype, "opaque", {
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
    if(!TabbedPane){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.opaque = true;
    }
    Object.defineProperty(TabbedPane.prototype, "visible", {
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
    if(!TabbedPane){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.visible = true;
    }
    Object.defineProperty(TabbedPane.prototype, "onComponentHidden", {
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
    if(!TabbedPane){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.onComponentHidden = {};
    }
    Object.defineProperty(TabbedPane.prototype, "nextFocusableComponent", {
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
    if(!TabbedPane){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(TabbedPane.prototype, "count", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.count;
            return P.boxAsJs(value);
        }
    });
    if(!TabbedPane){
        /**
         * Gets the number of components in this panel.
         * @property count
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.count = 0;
    }
    Object.defineProperty(TabbedPane.prototype, "onActionPerformed", {
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
    if(!TabbedPane){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.onActionPerformed = {};
    }
    Object.defineProperty(TabbedPane.prototype, "onKeyReleased", {
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
    if(!TabbedPane){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.onKeyReleased = {};
    }
    Object.defineProperty(TabbedPane.prototype, "focusable", {
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
    if(!TabbedPane){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.focusable = true;
    }
    Object.defineProperty(TabbedPane.prototype, "onKeyTyped", {
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
    if(!TabbedPane){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.onKeyTyped = {};
    }
    Object.defineProperty(TabbedPane.prototype, "onMouseWheelMoved", {
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
    if(!TabbedPane){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(TabbedPane.prototype, "selectedIndex", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.selectedIndex;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.selectedIndex = P.boxAsJava(aValue);
        }
    });
    if(!TabbedPane){
        /**
         * The selected component's index.
         * @property selectedIndex
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.selectedIndex = 0;
    }
    Object.defineProperty(TabbedPane.prototype, "onItemSelected", {
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
    if(!TabbedPane){
        /**
         * Event that is fired when one of the components is selected in this tabbed pane.
         * @property onItemSelected
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.onItemSelected = {};
    }
    Object.defineProperty(TabbedPane.prototype, "onComponentRemoved", {
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
    if(!TabbedPane){
        /**
         * Component removed event handler function.
         * @property onComponentRemoved
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.onComponentRemoved = {};
    }
    Object.defineProperty(TabbedPane.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!TabbedPane){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.component = {};
    }
    Object.defineProperty(TabbedPane.prototype, "onFocusGained", {
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
    if(!TabbedPane){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.onFocusGained = {};
    }
    Object.defineProperty(TabbedPane.prototype, "left", {
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
    if(!TabbedPane){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.left = 0;
    }
    Object.defineProperty(TabbedPane.prototype, "background", {
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
    if(!TabbedPane){
        /**
         * The background color of this component.
         * @property background
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.background = {};
    }
    Object.defineProperty(TabbedPane.prototype, "onMouseClicked", {
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
    if(!TabbedPane){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.onMouseClicked = {};
    }
    Object.defineProperty(TabbedPane.prototype, "onMouseExited", {
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
    if(!TabbedPane){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.onMouseExited = {};
    }
    Object.defineProperty(TabbedPane.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!TabbedPane){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.name = '';
    }
    Object.defineProperty(TabbedPane.prototype, "width", {
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
    if(!TabbedPane){
        /**
         * Width of the component.
         * @property width
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.width = 0;
    }
    Object.defineProperty(TabbedPane.prototype, "font", {
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
    if(!TabbedPane){
        /**
         * The font of this component.
         * @property font
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.font = {};
    }
    Object.defineProperty(TabbedPane.prototype, "onKeyPressed", {
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
    if(!TabbedPane){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.onKeyPressed = {};
    }
    Object.defineProperty(TabbedPane.prototype, "add", {
        value: function(component, text) {
            var delegate = this.unwrap();
            var value = delegate.add(P.boxAsJava(component), P.boxAsJava(text));
            return P.boxAsJs(value);
        }
    });
    if(!TabbedPane){
        /**
         * Appends the component whith specified text to the end of this container.
         * @param component the component to add.
         * @param text the text for the tab.
         * @param icon the icon for the tab (optional).
         * @method add
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.add = function(component, text){};
    }
    Object.defineProperty(TabbedPane.prototype, "child", {
        value: function(index) {
            var delegate = this.unwrap();
            var value = delegate.child(P.boxAsJava(index));
            return P.boxAsJs(value);
        }
    });
    if(!TabbedPane){
        /**
         * Gets the container's nth component.
         * @param index the component's index in the container
         * @return the child component
         * @method child
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.child = function(index){};
    }
    Object.defineProperty(TabbedPane.prototype, "remove", {
        value: function(component) {
            var delegate = this.unwrap();
            var value = delegate.remove(P.boxAsJava(component));
            return P.boxAsJs(value);
        }
    });
    if(!TabbedPane){
        /**
         * Removes the specified component from this container.
         * @param component the component to remove
         * @method remove
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.remove = function(component){};
    }
    Object.defineProperty(TabbedPane.prototype, "clear", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.clear();
            return P.boxAsJs(value);
        }
    });
    if(!TabbedPane){
        /**
         * Removes all the components from this container.
         * @method clear
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.clear = function(){};
    }
    Object.defineProperty(TabbedPane.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!TabbedPane){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf TabbedPane
         */
        P.TabbedPane.prototype.focus = function(){};
    }
})();