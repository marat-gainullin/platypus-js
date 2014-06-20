(function() {
    var javaClass = Java.type("com.eas.client.forms.api.components.TextArea");
    javaClass.setPublisher(function(aDelegate) {
        return new P.TextArea(null, aDelegate);
    });
    
    /**
     * Text area component.
     * @param text the text for the component (optional)
     * @constructor TextArea TextArea
     */
    P.TextArea = function TextArea(text) {
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
        if(TextArea.superclass)
            TextArea.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "TextArea", {value: TextArea});
    Object.defineProperty(TextArea.prototype, "cursor", {
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
    if(!TextArea){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf TextArea
         */
        P.TextArea.prototype.cursor = {};
    }
    Object.defineProperty(TextArea.prototype, "onMouseDragged", {
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
    if(!TextArea){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf TextArea
         */
        P.TextArea.prototype.onMouseDragged = {};
    }
    Object.defineProperty(TextArea.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!TextArea){
        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf TextArea
         */
        P.TextArea.prototype.parent = {};
    }
    Object.defineProperty(TextArea.prototype, "onMouseReleased", {
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
    if(!TextArea){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf TextArea
         */
        P.TextArea.prototype.onMouseReleased = {};
    }
    Object.defineProperty(TextArea.prototype, "onFocusLost", {
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
    if(!TextArea){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf TextArea
         */
        P.TextArea.prototype.onFocusLost = {};
    }
    Object.defineProperty(TextArea.prototype, "emptyText", {
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
    if(!TextArea){
        /**
         * The text to be shown when component's value is absent.
         * @property emptyText
         * @memberOf TextArea
         */
        P.TextArea.prototype.emptyText = '';
    }
    Object.defineProperty(TextArea.prototype, "onMousePressed", {
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
    if(!TextArea){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf TextArea
         */
        P.TextArea.prototype.onMousePressed = {};
    }
    Object.defineProperty(TextArea.prototype, "foreground", {
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
    if(!TextArea){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf TextArea
         */
        P.TextArea.prototype.foreground = {};
    }
    Object.defineProperty(TextArea.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!TextArea){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf TextArea
         */
        P.TextArea.prototype.error = '';
    }
    Object.defineProperty(TextArea.prototype, "enabled", {
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
    if(!TextArea){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf TextArea
         */
        P.TextArea.prototype.enabled = true;
    }
    Object.defineProperty(TextArea.prototype, "onComponentMoved", {
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
    if(!TextArea){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf TextArea
         */
        P.TextArea.prototype.onComponentMoved = {};
    }
    Object.defineProperty(TextArea.prototype, "componentPopupMenu", {
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
    if(!TextArea){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf TextArea
         */
        P.TextArea.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(TextArea.prototype, "top", {
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
    if(!TextArea){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf TextArea
         */
        P.TextArea.prototype.top = 0;
    }
    Object.defineProperty(TextArea.prototype, "onComponentResized", {
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
    if(!TextArea){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf TextArea
         */
        P.TextArea.prototype.onComponentResized = {};
    }
    Object.defineProperty(TextArea.prototype, "text", {
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
    if(!TextArea){
        /**
         * The text contained in this component.
         * @property text
         * @memberOf TextArea
         */
        P.TextArea.prototype.text = '';
    }
    Object.defineProperty(TextArea.prototype, "onMouseEntered", {
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
    if(!TextArea){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf TextArea
         */
        P.TextArea.prototype.onMouseEntered = {};
    }
    Object.defineProperty(TextArea.prototype, "value", {
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
    if(!TextArea){
        /**
         * Generated property jsDoc.
         * @property value
         * @memberOf TextArea
         */
        P.TextArea.prototype.value = {};
    }
    Object.defineProperty(TextArea.prototype, "toolTipText", {
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
    if(!TextArea){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf TextArea
         */
        P.TextArea.prototype.toolTipText = '';
    }
    Object.defineProperty(TextArea.prototype, "height", {
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
    if(!TextArea){
        /**
         * Height of the component.
         * @property height
         * @memberOf TextArea
         */
        P.TextArea.prototype.height = 0;
    }
    Object.defineProperty(TextArea.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!TextArea){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf TextArea
         */
        P.TextArea.prototype.element = {};
    }
    Object.defineProperty(TextArea.prototype, "onComponentShown", {
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
    if(!TextArea){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf TextArea
         */
        P.TextArea.prototype.onComponentShown = {};
    }
    Object.defineProperty(TextArea.prototype, "onMouseMoved", {
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
    if(!TextArea){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf TextArea
         */
        P.TextArea.prototype.onMouseMoved = {};
    }
    Object.defineProperty(TextArea.prototype, "opaque", {
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
    if(!TextArea){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf TextArea
         */
        P.TextArea.prototype.opaque = true;
    }
    Object.defineProperty(TextArea.prototype, "visible", {
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
    if(!TextArea){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf TextArea
         */
        P.TextArea.prototype.visible = true;
    }
    Object.defineProperty(TextArea.prototype, "onComponentHidden", {
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
    if(!TextArea){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf TextArea
         */
        P.TextArea.prototype.onComponentHidden = {};
    }
    Object.defineProperty(TextArea.prototype, "nextFocusableComponent", {
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
    if(!TextArea){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf TextArea
         */
        P.TextArea.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(TextArea.prototype, "onActionPerformed", {
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
    if(!TextArea){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf TextArea
         */
        P.TextArea.prototype.onActionPerformed = {};
    }
    Object.defineProperty(TextArea.prototype, "onKeyReleased", {
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
    if(!TextArea){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf TextArea
         */
        P.TextArea.prototype.onKeyReleased = {};
    }
    Object.defineProperty(TextArea.prototype, "focusable", {
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
    if(!TextArea){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf TextArea
         */
        P.TextArea.prototype.focusable = true;
    }
    Object.defineProperty(TextArea.prototype, "onKeyTyped", {
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
    if(!TextArea){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf TextArea
         */
        P.TextArea.prototype.onKeyTyped = {};
    }
    Object.defineProperty(TextArea.prototype, "onMouseWheelMoved", {
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
    if(!TextArea){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf TextArea
         */
        P.TextArea.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(TextArea.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!TextArea){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf TextArea
         */
        P.TextArea.prototype.component = {};
    }
    Object.defineProperty(TextArea.prototype, "onFocusGained", {
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
    if(!TextArea){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf TextArea
         */
        P.TextArea.prototype.onFocusGained = {};
    }
    Object.defineProperty(TextArea.prototype, "left", {
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
    if(!TextArea){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf TextArea
         */
        P.TextArea.prototype.left = 0;
    }
    Object.defineProperty(TextArea.prototype, "background", {
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
    if(!TextArea){
        /**
         * The background color of this component.
         * @property background
         * @memberOf TextArea
         */
        P.TextArea.prototype.background = {};
    }
    Object.defineProperty(TextArea.prototype, "onMouseClicked", {
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
    if(!TextArea){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf TextArea
         */
        P.TextArea.prototype.onMouseClicked = {};
    }
    Object.defineProperty(TextArea.prototype, "onMouseExited", {
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
    if(!TextArea){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf TextArea
         */
        P.TextArea.prototype.onMouseExited = {};
    }
    Object.defineProperty(TextArea.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!TextArea){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf TextArea
         */
        P.TextArea.prototype.name = '';
    }
    Object.defineProperty(TextArea.prototype, "width", {
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
    if(!TextArea){
        /**
         * Width of the component.
         * @property width
         * @memberOf TextArea
         */
        P.TextArea.prototype.width = 0;
    }
    Object.defineProperty(TextArea.prototype, "font", {
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
    if(!TextArea){
        /**
         * The font of this component.
         * @property font
         * @memberOf TextArea
         */
        P.TextArea.prototype.font = {};
    }
    Object.defineProperty(TextArea.prototype, "onKeyPressed", {
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
    if(!TextArea){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf TextArea
         */
        P.TextArea.prototype.onKeyPressed = {};
    }
    Object.defineProperty(TextArea.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!TextArea){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf TextArea
         */
        P.TextArea.prototype.focus = function(){};
    }
})();