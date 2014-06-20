(function() {
    var javaClass = Java.type("com.eas.client.forms.api.containers.CardPane");
    javaClass.setPublisher(function(aDelegate) {
        return new P.CardPane(null, null, aDelegate);
    });
    
    /**
     * A container with Card Layout. It treats each component in the container as a card. Only one card is visible at a time, and the container acts as a stack of cards.
     * @param hgap the horizontal gap (optional).
     * @param vgap the vertical gap (optional).
     * @constructor CardPane CardPane
     */
    P.CardPane = function CardPane(hgap, vgap) {
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
        if(CardPane.superclass)
            CardPane.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "CardPane", {value: CardPane});
    Object.defineProperty(CardPane.prototype, "cursor", {
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
    if(!CardPane){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf CardPane
         */
        P.CardPane.prototype.cursor = {};
    }
    Object.defineProperty(CardPane.prototype, "onMouseDragged", {
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
    if(!CardPane){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf CardPane
         */
        P.CardPane.prototype.onMouseDragged = {};
    }
    Object.defineProperty(CardPane.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!CardPane){
        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf CardPane
         */
        P.CardPane.prototype.parent = {};
    }
    Object.defineProperty(CardPane.prototype, "onMouseReleased", {
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
    if(!CardPane){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf CardPane
         */
        P.CardPane.prototype.onMouseReleased = {};
    }
    Object.defineProperty(CardPane.prototype, "onFocusLost", {
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
    if(!CardPane){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf CardPane
         */
        P.CardPane.prototype.onFocusLost = {};
    }
    Object.defineProperty(CardPane.prototype, "onMousePressed", {
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
    if(!CardPane){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf CardPane
         */
        P.CardPane.prototype.onMousePressed = {};
    }
    Object.defineProperty(CardPane.prototype, "foreground", {
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
    if(!CardPane){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf CardPane
         */
        P.CardPane.prototype.foreground = {};
    }
    Object.defineProperty(CardPane.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!CardPane){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf CardPane
         */
        P.CardPane.prototype.error = '';
    }
    Object.defineProperty(CardPane.prototype, "enabled", {
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
    if(!CardPane){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf CardPane
         */
        P.CardPane.prototype.enabled = true;
    }
    Object.defineProperty(CardPane.prototype, "onComponentMoved", {
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
    if(!CardPane){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf CardPane
         */
        P.CardPane.prototype.onComponentMoved = {};
    }
    Object.defineProperty(CardPane.prototype, "onComponentAdded", {
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
    if(!CardPane){
        /**
         * Component added event hanler function.
         * @property onComponentAdded
         * @memberOf CardPane
         */
        P.CardPane.prototype.onComponentAdded = {};
    }
    Object.defineProperty(CardPane.prototype, "componentPopupMenu", {
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
    if(!CardPane){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf CardPane
         */
        P.CardPane.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(CardPane.prototype, "top", {
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
    if(!CardPane){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf CardPane
         */
        P.CardPane.prototype.top = 0;
    }
    Object.defineProperty(CardPane.prototype, "children", {
        get: function() {
            var delegate = this.unwrap();
            if (!invalidatable) {
                var value = delegate.children;
                invalidatable = P.boxAsJs(value);
            }
            return invalidatable;
        }
    });
    if(!CardPane){
        /**
         * Gets the container's children components.
         * @property children
         * @memberOf CardPane
         */
        P.CardPane.prototype.children = [];
    }
    Object.defineProperty(CardPane.prototype, "onComponentResized", {
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
    if(!CardPane){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf CardPane
         */
        P.CardPane.prototype.onComponentResized = {};
    }
    Object.defineProperty(CardPane.prototype, "onMouseEntered", {
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
    if(!CardPane){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf CardPane
         */
        P.CardPane.prototype.onMouseEntered = {};
    }
    Object.defineProperty(CardPane.prototype, "toolTipText", {
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
    if(!CardPane){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf CardPane
         */
        P.CardPane.prototype.toolTipText = '';
    }
    Object.defineProperty(CardPane.prototype, "height", {
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
    if(!CardPane){
        /**
         * Height of the component.
         * @property height
         * @memberOf CardPane
         */
        P.CardPane.prototype.height = 0;
    }
    Object.defineProperty(CardPane.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!CardPane){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf CardPane
         */
        P.CardPane.prototype.element = {};
    }
    Object.defineProperty(CardPane.prototype, "onComponentShown", {
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
    if(!CardPane){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf CardPane
         */
        P.CardPane.prototype.onComponentShown = {};
    }
    Object.defineProperty(CardPane.prototype, "onMouseMoved", {
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
    if(!CardPane){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf CardPane
         */
        P.CardPane.prototype.onMouseMoved = {};
    }
    Object.defineProperty(CardPane.prototype, "opaque", {
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
    if(!CardPane){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf CardPane
         */
        P.CardPane.prototype.opaque = true;
    }
    Object.defineProperty(CardPane.prototype, "visible", {
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
    if(!CardPane){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf CardPane
         */
        P.CardPane.prototype.visible = true;
    }
    Object.defineProperty(CardPane.prototype, "onComponentHidden", {
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
    if(!CardPane){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf CardPane
         */
        P.CardPane.prototype.onComponentHidden = {};
    }
    Object.defineProperty(CardPane.prototype, "nextFocusableComponent", {
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
    if(!CardPane){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf CardPane
         */
        P.CardPane.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(CardPane.prototype, "count", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.count;
            return P.boxAsJs(value);
        }
    });
    if(!CardPane){
        /**
         * Gets the number of components in this panel.
         * @property count
         * @memberOf CardPane
         */
        P.CardPane.prototype.count = 0;
    }
    Object.defineProperty(CardPane.prototype, "onActionPerformed", {
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
    if(!CardPane){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf CardPane
         */
        P.CardPane.prototype.onActionPerformed = {};
    }
    Object.defineProperty(CardPane.prototype, "onKeyReleased", {
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
    if(!CardPane){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf CardPane
         */
        P.CardPane.prototype.onKeyReleased = {};
    }
    Object.defineProperty(CardPane.prototype, "focusable", {
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
    if(!CardPane){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf CardPane
         */
        P.CardPane.prototype.focusable = true;
    }
    Object.defineProperty(CardPane.prototype, "onKeyTyped", {
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
    if(!CardPane){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf CardPane
         */
        P.CardPane.prototype.onKeyTyped = {};
    }
    Object.defineProperty(CardPane.prototype, "onMouseWheelMoved", {
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
    if(!CardPane){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf CardPane
         */
        P.CardPane.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(CardPane.prototype, "onItemSelected", {
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
    if(!CardPane){
        /**
         * Event that is fired when one of the components is selected in this card pane.
         * @property onItemSelected
         * @memberOf CardPane
         */
        P.CardPane.prototype.onItemSelected = {};
    }
    Object.defineProperty(CardPane.prototype, "onComponentRemoved", {
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
    if(!CardPane){
        /**
         * Component removed event handler function.
         * @property onComponentRemoved
         * @memberOf CardPane
         */
        P.CardPane.prototype.onComponentRemoved = {};
    }
    Object.defineProperty(CardPane.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!CardPane){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf CardPane
         */
        P.CardPane.prototype.component = {};
    }
    Object.defineProperty(CardPane.prototype, "onFocusGained", {
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
    if(!CardPane){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf CardPane
         */
        P.CardPane.prototype.onFocusGained = {};
    }
    Object.defineProperty(CardPane.prototype, "left", {
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
    if(!CardPane){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf CardPane
         */
        P.CardPane.prototype.left = 0;
    }
    Object.defineProperty(CardPane.prototype, "background", {
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
    if(!CardPane){
        /**
         * The background color of this component.
         * @property background
         * @memberOf CardPane
         */
        P.CardPane.prototype.background = {};
    }
    Object.defineProperty(CardPane.prototype, "onMouseClicked", {
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
    if(!CardPane){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf CardPane
         */
        P.CardPane.prototype.onMouseClicked = {};
    }
    Object.defineProperty(CardPane.prototype, "onMouseExited", {
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
    if(!CardPane){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf CardPane
         */
        P.CardPane.prototype.onMouseExited = {};
    }
    Object.defineProperty(CardPane.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!CardPane){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf CardPane
         */
        P.CardPane.prototype.name = '';
    }
    Object.defineProperty(CardPane.prototype, "width", {
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
    if(!CardPane){
        /**
         * Width of the component.
         * @property width
         * @memberOf CardPane
         */
        P.CardPane.prototype.width = 0;
    }
    Object.defineProperty(CardPane.prototype, "font", {
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
    if(!CardPane){
        /**
         * The font of this component.
         * @property font
         * @memberOf CardPane
         */
        P.CardPane.prototype.font = {};
    }
    Object.defineProperty(CardPane.prototype, "onKeyPressed", {
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
    if(!CardPane){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf CardPane
         */
        P.CardPane.prototype.onKeyPressed = {};
    }
    Object.defineProperty(CardPane.prototype, "add", {
        value: function(arg0, arg1) {
            var delegate = this.unwrap();
            var value = delegate.add(P.boxAsJava(arg0), P.boxAsJava(arg1));
            return P.boxAsJs(value);
        }
    });
    if(!CardPane){
        /**
         * Appends the component to this container with the specified name.
         * @param component the component to add.
         * @param cardName the name of the card.
         * @method add
         * @memberOf CardPane
         */
        P.CardPane.prototype.add = function(arg0, arg1){};
    }
    Object.defineProperty(CardPane.prototype, "show", {
        value: function(name) {
            var delegate = this.unwrap();
            var value = delegate.show(P.boxAsJava(name));
            return P.boxAsJs(value);
        }
    });
    if(!CardPane){
        /**
         * Flips to the component that was added to this layout with the specified name.
         * @param name the card name
         * @method show
         * @memberOf CardPane
         */
        P.CardPane.prototype.show = function(name){};
    }
    Object.defineProperty(CardPane.prototype, "child", {
        value: function(name) {
            var delegate = this.unwrap();
            var value = delegate.child(P.boxAsJava(name));
            return P.boxAsJs(value);
        }
    });
    if(!CardPane){
        /**
         * Gets the container's nth component.
         * @param index the component's index in the container
         * @return the child component
         * @method child
         * @memberOf CardPane
         */
        P.CardPane.prototype.child = function(name){};
    }
    Object.defineProperty(CardPane.prototype, "remove", {
        value: function(component) {
            var delegate = this.unwrap();
            var value = delegate.remove(P.boxAsJava(component));
            return P.boxAsJs(value);
        }
    });
    if(!CardPane){
        /**
         * Removes the specified component from this container.
         * @param component the component to remove
         * @method remove
         * @memberOf CardPane
         */
        P.CardPane.prototype.remove = function(component){};
    }
    Object.defineProperty(CardPane.prototype, "clear", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.clear();
            return P.boxAsJs(value);
        }
    });
    if(!CardPane){
        /**
         * Removes all the components from this container.
         * @method clear
         * @memberOf CardPane
         */
        P.CardPane.prototype.clear = function(){};
    }
    Object.defineProperty(CardPane.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!CardPane){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf CardPane
         */
        P.CardPane.prototype.focus = function(){};
    }
})();