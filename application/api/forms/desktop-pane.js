(function() {
    var javaClass = Java.type("com.eas.client.forms.api.components.DesktopPane");
    javaClass.setPublisher(function(aDelegate) {
        return new P.DesktopPane(aDelegate);
    });
    
    /**
     * Desktop pane panel component.
     * This component can be used for creating a multi-document GUI or a virtual desktop.
     * @constructor DesktopPane DesktopPane
     */
    P.DesktopPane = function DesktopPane() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(DesktopPane.superclass)
            DesktopPane.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "DesktopPane", {value: DesktopPane});
    Object.defineProperty(DesktopPane.prototype, "cursor", {
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
    if(!DesktopPane){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.cursor = {};
    }
    Object.defineProperty(DesktopPane.prototype, "onMouseDragged", {
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
    if(!DesktopPane){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.onMouseDragged = {};
    }
    Object.defineProperty(DesktopPane.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!DesktopPane){
        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.parent = {};
    }
    Object.defineProperty(DesktopPane.prototype, "onMouseReleased", {
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
    if(!DesktopPane){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.onMouseReleased = {};
    }
    Object.defineProperty(DesktopPane.prototype, "onFocusLost", {
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
    if(!DesktopPane){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.onFocusLost = {};
    }
    Object.defineProperty(DesktopPane.prototype, "onMousePressed", {
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
    if(!DesktopPane){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.onMousePressed = {};
    }
    Object.defineProperty(DesktopPane.prototype, "foreground", {
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
    if(!DesktopPane){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.foreground = {};
    }
    Object.defineProperty(DesktopPane.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!DesktopPane){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.error = '';
    }
    Object.defineProperty(DesktopPane.prototype, "enabled", {
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
    if(!DesktopPane){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.enabled = true;
    }
    Object.defineProperty(DesktopPane.prototype, "onComponentMoved", {
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
    if(!DesktopPane){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.onComponentMoved = {};
    }
    Object.defineProperty(DesktopPane.prototype, "componentPopupMenu", {
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
    if(!DesktopPane){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(DesktopPane.prototype, "top", {
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
    if(!DesktopPane){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.top = 0;
    }
    Object.defineProperty(DesktopPane.prototype, "onComponentResized", {
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
    if(!DesktopPane){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.onComponentResized = {};
    }
    Object.defineProperty(DesktopPane.prototype, "onMouseEntered", {
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
    if(!DesktopPane){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.onMouseEntered = {};
    }
    Object.defineProperty(DesktopPane.prototype, "toolTipText", {
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
    if(!DesktopPane){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.toolTipText = '';
    }
    Object.defineProperty(DesktopPane.prototype, "height", {
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
    if(!DesktopPane){
        /**
         * Height of the component.
         * @property height
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.height = 0;
    }
    Object.defineProperty(DesktopPane.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!DesktopPane){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.element = {};
    }
    Object.defineProperty(DesktopPane.prototype, "onComponentShown", {
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
    if(!DesktopPane){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.onComponentShown = {};
    }
    Object.defineProperty(DesktopPane.prototype, "onMouseMoved", {
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
    if(!DesktopPane){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.onMouseMoved = {};
    }
    Object.defineProperty(DesktopPane.prototype, "opaque", {
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
    if(!DesktopPane){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.opaque = true;
    }
    Object.defineProperty(DesktopPane.prototype, "visible", {
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
    if(!DesktopPane){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.visible = true;
    }
    Object.defineProperty(DesktopPane.prototype, "onComponentHidden", {
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
    if(!DesktopPane){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.onComponentHidden = {};
    }
    Object.defineProperty(DesktopPane.prototype, "nextFocusableComponent", {
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
    if(!DesktopPane){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(DesktopPane.prototype, "onActionPerformed", {
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
    if(!DesktopPane){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.onActionPerformed = {};
    }
    Object.defineProperty(DesktopPane.prototype, "onKeyReleased", {
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
    if(!DesktopPane){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.onKeyReleased = {};
    }
    Object.defineProperty(DesktopPane.prototype, "focusable", {
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
    if(!DesktopPane){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.focusable = true;
    }
    Object.defineProperty(DesktopPane.prototype, "onKeyTyped", {
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
    if(!DesktopPane){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.onKeyTyped = {};
    }
    Object.defineProperty(DesktopPane.prototype, "onMouseWheelMoved", {
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
    if(!DesktopPane){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(DesktopPane.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!DesktopPane){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.component = {};
    }
    Object.defineProperty(DesktopPane.prototype, "onFocusGained", {
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
    if(!DesktopPane){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.onFocusGained = {};
    }
    Object.defineProperty(DesktopPane.prototype, "left", {
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
    if(!DesktopPane){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.left = 0;
    }
    Object.defineProperty(DesktopPane.prototype, "background", {
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
    if(!DesktopPane){
        /**
         * The background color of this component.
         * @property background
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.background = {};
    }
    Object.defineProperty(DesktopPane.prototype, "onMouseClicked", {
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
    if(!DesktopPane){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.onMouseClicked = {};
    }
    Object.defineProperty(DesktopPane.prototype, "onMouseExited", {
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
    if(!DesktopPane){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.onMouseExited = {};
    }
    Object.defineProperty(DesktopPane.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!DesktopPane){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.name = '';
    }
    Object.defineProperty(DesktopPane.prototype, "width", {
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
    if(!DesktopPane){
        /**
         * Width of the component.
         * @property width
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.width = 0;
    }
    Object.defineProperty(DesktopPane.prototype, "forms", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.forms;
            return P.boxAsJs(value);
        }
    });
    if(!DesktopPane){
        /**
         * An array of all frames on the pane.
         * @property forms
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.forms = [];
    }
    Object.defineProperty(DesktopPane.prototype, "font", {
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
    if(!DesktopPane){
        /**
         * The font of this component.
         * @property font
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.font = {};
    }
    Object.defineProperty(DesktopPane.prototype, "onKeyPressed", {
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
    if(!DesktopPane){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.onKeyPressed = {};
    }
    Object.defineProperty(DesktopPane.prototype, "closeAll", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.closeAll();
            return P.boxAsJs(value);
        }
    });
    if(!DesktopPane){
        /**
         * Closes all frames on the pane.
         * @method closeAll
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.closeAll = function(){};
    }
    Object.defineProperty(DesktopPane.prototype, "minimizeAll", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.minimizeAll();
            return P.boxAsJs(value);
        }
    });
    if(!DesktopPane){
        /**
         * Minimizes all frames on the pane.
         * @method minimizeAll
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.minimizeAll = function(){};
    }
    Object.defineProperty(DesktopPane.prototype, "restoreAll", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.restoreAll();
            return P.boxAsJs(value);
        }
    });
    if(!DesktopPane){
        /**
         * Restores frames original state and location.
         * @method restoreAll
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.restoreAll = function(){};
    }
    Object.defineProperty(DesktopPane.prototype, "maximizeAll", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.maximizeAll();
            return P.boxAsJs(value);
        }
    });
    if(!DesktopPane){
        /**
         * Maximizes all frames on the pane.
         * @method maximizeAll
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.maximizeAll = function(){};
    }
    Object.defineProperty(DesktopPane.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!DesktopPane){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.focus = function(){};
    }
})();