(function() {
    var javaClass = Java.type("com.eas.client.forms.api.components.HtmlArea");
    javaClass.setPublisher(function(aDelegate) {
        return new P.HtmlArea(null, aDelegate);
    });
    
    /**
     * HTML area component.
     * @param text the initial text for the HTML area (optional)
     * @constructor HtmlArea HtmlArea
     */
    P.HtmlArea = function HtmlArea(text) {
        var maxArgs = 1;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 1 ? new javaClass(P.boxAsJava(text))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(HtmlArea.superclass)
            HtmlArea.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "HtmlArea", {value: HtmlArea});
    Object.defineProperty(HtmlArea.prototype, "cursor", {
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
    if(!HtmlArea){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.cursor = {};
    }
    Object.defineProperty(HtmlArea.prototype, "onMouseDragged", {
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
    if(!HtmlArea){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.onMouseDragged = {};
    }
    Object.defineProperty(HtmlArea.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!HtmlArea){
        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.parent = {};
    }
    Object.defineProperty(HtmlArea.prototype, "onMouseReleased", {
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
    if(!HtmlArea){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.onMouseReleased = {};
    }
    Object.defineProperty(HtmlArea.prototype, "onFocusLost", {
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
    if(!HtmlArea){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.onFocusLost = {};
    }
    Object.defineProperty(HtmlArea.prototype, "emptyText", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.emptyText;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.emptyText = P.boxAsJava(aValue);
        }
    });
    if(!HtmlArea){
        /**
         * The text to be shown when component's value is absent.
         * @property emptyText
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.emptyText = '';
    }
    Object.defineProperty(HtmlArea.prototype, "onMousePressed", {
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
    if(!HtmlArea){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.onMousePressed = {};
    }
    Object.defineProperty(HtmlArea.prototype, "foreground", {
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
    if(!HtmlArea){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.foreground = {};
    }
    Object.defineProperty(HtmlArea.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!HtmlArea){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.error = '';
    }
    Object.defineProperty(HtmlArea.prototype, "enabled", {
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
    if(!HtmlArea){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.enabled = true;
    }
    Object.defineProperty(HtmlArea.prototype, "onComponentMoved", {
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
    if(!HtmlArea){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.onComponentMoved = {};
    }
    Object.defineProperty(HtmlArea.prototype, "componentPopupMenu", {
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
    if(!HtmlArea){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(HtmlArea.prototype, "top", {
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
    if(!HtmlArea){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.top = 0;
    }
    Object.defineProperty(HtmlArea.prototype, "onComponentResized", {
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
    if(!HtmlArea){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.onComponentResized = {};
    }
    Object.defineProperty(HtmlArea.prototype, "text", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.text;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.text = P.boxAsJava(aValue);
        }
    });
    if(!HtmlArea){
        /**
         * Text of the component.
         * @property text
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.text = '';
    }
    Object.defineProperty(HtmlArea.prototype, "onMouseEntered", {
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
    if(!HtmlArea){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.onMouseEntered = {};
    }
    Object.defineProperty(HtmlArea.prototype, "value", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.value;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.value = P.boxAsJava(aValue);
        }
    });
    if(!HtmlArea){
        /**
         * Value of the component.
         * @property value
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.value = '';
    }
    Object.defineProperty(HtmlArea.prototype, "toolTipText", {
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
    if(!HtmlArea){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.toolTipText = '';
    }
    Object.defineProperty(HtmlArea.prototype, "height", {
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
    if(!HtmlArea){
        /**
         * Height of the component.
         * @property height
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.height = 0;
    }
    Object.defineProperty(HtmlArea.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!HtmlArea){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.element = {};
    }
    Object.defineProperty(HtmlArea.prototype, "onComponentShown", {
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
    if(!HtmlArea){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.onComponentShown = {};
    }
    Object.defineProperty(HtmlArea.prototype, "onMouseMoved", {
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
    if(!HtmlArea){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.onMouseMoved = {};
    }
    Object.defineProperty(HtmlArea.prototype, "opaque", {
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
    if(!HtmlArea){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.opaque = true;
    }
    Object.defineProperty(HtmlArea.prototype, "visible", {
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
    if(!HtmlArea){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.visible = true;
    }
    Object.defineProperty(HtmlArea.prototype, "onComponentHidden", {
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
    if(!HtmlArea){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.onComponentHidden = {};
    }
    Object.defineProperty(HtmlArea.prototype, "nextFocusableComponent", {
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
    if(!HtmlArea){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(HtmlArea.prototype, "onActionPerformed", {
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
    if(!HtmlArea){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.onActionPerformed = {};
    }
    Object.defineProperty(HtmlArea.prototype, "onKeyReleased", {
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
    if(!HtmlArea){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.onKeyReleased = {};
    }
    Object.defineProperty(HtmlArea.prototype, "focusable", {
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
    if(!HtmlArea){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.focusable = true;
    }
    Object.defineProperty(HtmlArea.prototype, "onKeyTyped", {
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
    if(!HtmlArea){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.onKeyTyped = {};
    }
    Object.defineProperty(HtmlArea.prototype, "onMouseWheelMoved", {
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
    if(!HtmlArea){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(HtmlArea.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!HtmlArea){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.component = {};
    }
    Object.defineProperty(HtmlArea.prototype, "onFocusGained", {
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
    if(!HtmlArea){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.onFocusGained = {};
    }
    Object.defineProperty(HtmlArea.prototype, "left", {
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
    if(!HtmlArea){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.left = 0;
    }
    Object.defineProperty(HtmlArea.prototype, "background", {
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
    if(!HtmlArea){
        /**
         * The background color of this component.
         * @property background
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.background = {};
    }
    Object.defineProperty(HtmlArea.prototype, "onMouseClicked", {
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
    if(!HtmlArea){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.onMouseClicked = {};
    }
    Object.defineProperty(HtmlArea.prototype, "onMouseExited", {
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
    if(!HtmlArea){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.onMouseExited = {};
    }
    Object.defineProperty(HtmlArea.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!HtmlArea){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.name = '';
    }
    Object.defineProperty(HtmlArea.prototype, "width", {
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
    if(!HtmlArea){
        /**
         * Width of the component.
         * @property width
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.width = 0;
    }
    Object.defineProperty(HtmlArea.prototype, "font", {
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
    if(!HtmlArea){
        /**
         * The font of this component.
         * @property font
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.font = {};
    }
    Object.defineProperty(HtmlArea.prototype, "onKeyPressed", {
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
    if(!HtmlArea){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.onKeyPressed = {};
    }
    Object.defineProperty(HtmlArea.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!HtmlArea){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.focus = function(){};
    }
})();