(function() {
    var javaClass = Java.type("com.eas.client.forms.api.components.model.ModelCheckBox");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ModelCheckBox(null, aDelegate);
    });
    
    /**
     * An implementation of a model check box -- an item that can be selected or deselected, and which displays its state to the user.
     * @param text the text of the component (optional).
     * @constructor ModelCheckBox ModelCheckBox
     */
    P.ModelCheckBox = function ModelCheckBox(text) {
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
        if(ModelCheckBox.superclass)
            ModelCheckBox.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "ModelCheckBox", {value: ModelCheckBox});
    Object.defineProperty(ModelCheckBox.prototype, "cursor", {
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
    if(!ModelCheckBox){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.cursor = {};
    }
    Object.defineProperty(ModelCheckBox.prototype, "onMouseDragged", {
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
    if(!ModelCheckBox){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.onMouseDragged = {};
    }
    Object.defineProperty(ModelCheckBox.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!ModelCheckBox){
        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.parent = {};
    }
    Object.defineProperty(ModelCheckBox.prototype, "onMouseReleased", {
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
    if(!ModelCheckBox){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.onMouseReleased = {};
    }
    Object.defineProperty(ModelCheckBox.prototype, "onFocusLost", {
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
    if(!ModelCheckBox){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.onFocusLost = {};
    }
    Object.defineProperty(ModelCheckBox.prototype, "onMousePressed", {
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
    if(!ModelCheckBox){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.onMousePressed = {};
    }
    Object.defineProperty(ModelCheckBox.prototype, "foreground", {
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
    if(!ModelCheckBox){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.foreground = {};
    }
    Object.defineProperty(ModelCheckBox.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!ModelCheckBox){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.error = '';
    }
    Object.defineProperty(ModelCheckBox.prototype, "enabled", {
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
    if(!ModelCheckBox){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.enabled = true;
    }
    Object.defineProperty(ModelCheckBox.prototype, "onComponentMoved", {
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
    if(!ModelCheckBox){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.onComponentMoved = {};
    }
    Object.defineProperty(ModelCheckBox.prototype, "onSelect", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onSelect;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onSelect = P.boxAsJava(aValue);
        }
    });
    if(!ModelCheckBox){
        /**
         * Component's selection event handler function.
         * @property onSelect
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.onSelect = {};
    }
    Object.defineProperty(ModelCheckBox.prototype, "componentPopupMenu", {
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
    if(!ModelCheckBox){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(ModelCheckBox.prototype, "top", {
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
    if(!ModelCheckBox){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.top = 0;
    }
    Object.defineProperty(ModelCheckBox.prototype, "onRender", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onRender;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onRender = P.boxAsJava(aValue);
        }
    });
    if(!ModelCheckBox){
        /**
         * Component's rendering event handler function.
         * @property onRender
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.onRender = {};
    }
    Object.defineProperty(ModelCheckBox.prototype, "onComponentResized", {
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
    if(!ModelCheckBox){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.onComponentResized = {};
    }
    Object.defineProperty(ModelCheckBox.prototype, "model", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.model;
            return P.boxAsJs(value);
        }
    });
    if(!ModelCheckBox){
        /**
         * Model of the component. It will be used for data binding.
         * @property model
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.model = {};
    }
    Object.defineProperty(ModelCheckBox.prototype, "text", {
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
    if(!ModelCheckBox){
        /**
         * @property text
         * @memberOf ModelCheckBox
         * Text on the check box.*/
        P.ModelCheckBox.prototype.text = '';
    }
    Object.defineProperty(ModelCheckBox.prototype, "onMouseEntered", {
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
    if(!ModelCheckBox){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.onMouseEntered = {};
    }
    Object.defineProperty(ModelCheckBox.prototype, "value", {
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
    if(!ModelCheckBox){
        /**
         * Component's value.
         * @property value
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.value = {};
    }
    Object.defineProperty(ModelCheckBox.prototype, "toolTipText", {
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
    if(!ModelCheckBox){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.toolTipText = '';
    }
    Object.defineProperty(ModelCheckBox.prototype, "height", {
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
    if(!ModelCheckBox){
        /**
         * Height of the component.
         * @property height
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.height = 0;
    }
    Object.defineProperty(ModelCheckBox.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!ModelCheckBox){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.element = {};
    }
    Object.defineProperty(ModelCheckBox.prototype, "onComponentShown", {
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
    if(!ModelCheckBox){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.onComponentShown = {};
    }
    Object.defineProperty(ModelCheckBox.prototype, "onMouseMoved", {
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
    if(!ModelCheckBox){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.onMouseMoved = {};
    }
    Object.defineProperty(ModelCheckBox.prototype, "opaque", {
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
    if(!ModelCheckBox){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.opaque = true;
    }
    Object.defineProperty(ModelCheckBox.prototype, "visible", {
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
    if(!ModelCheckBox){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.visible = true;
    }
    Object.defineProperty(ModelCheckBox.prototype, "onComponentHidden", {
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
    if(!ModelCheckBox){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.onComponentHidden = {};
    }
    Object.defineProperty(ModelCheckBox.prototype, "editable", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.editable;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.editable = P.boxAsJava(aValue);
        }
    });
    if(!ModelCheckBox){
        /**
         * @property editable
         * @memberOf ModelCheckBox
         * Determines if component is editable.*/
        P.ModelCheckBox.prototype.editable = true;
    }
    Object.defineProperty(ModelCheckBox.prototype, "nextFocusableComponent", {
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
    if(!ModelCheckBox){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(ModelCheckBox.prototype, "onActionPerformed", {
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
    if(!ModelCheckBox){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.onActionPerformed = {};
    }
    Object.defineProperty(ModelCheckBox.prototype, "onKeyReleased", {
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
    if(!ModelCheckBox){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.onKeyReleased = {};
    }
    Object.defineProperty(ModelCheckBox.prototype, "focusable", {
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
    if(!ModelCheckBox){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.focusable = true;
    }
    Object.defineProperty(ModelCheckBox.prototype, "onKeyTyped", {
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
    if(!ModelCheckBox){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.onKeyTyped = {};
    }
    Object.defineProperty(ModelCheckBox.prototype, "onMouseWheelMoved", {
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
    if(!ModelCheckBox){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(ModelCheckBox.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!ModelCheckBox){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.component = {};
    }
    Object.defineProperty(ModelCheckBox.prototype, "field", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.field;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.field = P.boxAsJava(aValue);
        }
    });
    if(!ModelCheckBox){
        /**
         * Model entity's field.
         * @property field
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.field = {};
    }
    Object.defineProperty(ModelCheckBox.prototype, "onFocusGained", {
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
    if(!ModelCheckBox){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.onFocusGained = {};
    }
    Object.defineProperty(ModelCheckBox.prototype, "left", {
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
    if(!ModelCheckBox){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.left = 0;
    }
    Object.defineProperty(ModelCheckBox.prototype, "background", {
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
    if(!ModelCheckBox){
        /**
         * The background color of this component.
         * @property background
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.background = {};
    }
    Object.defineProperty(ModelCheckBox.prototype, "onMouseClicked", {
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
    if(!ModelCheckBox){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.onMouseClicked = {};
    }
    Object.defineProperty(ModelCheckBox.prototype, "onMouseExited", {
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
    if(!ModelCheckBox){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.onMouseExited = {};
    }
    Object.defineProperty(ModelCheckBox.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!ModelCheckBox){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.name = '';
    }
    Object.defineProperty(ModelCheckBox.prototype, "width", {
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
    if(!ModelCheckBox){
        /**
         * Width of the component.
         * @property width
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.width = 0;
    }
    Object.defineProperty(ModelCheckBox.prototype, "font", {
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
    if(!ModelCheckBox){
        /**
         * The font of this component.
         * @property font
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.font = {};
    }
    Object.defineProperty(ModelCheckBox.prototype, "onKeyPressed", {
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
    if(!ModelCheckBox){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.onKeyPressed = {};
    }
    Object.defineProperty(ModelCheckBox.prototype, "redraw", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.redraw();
            return P.boxAsJs(value);
        }
    });
    if(!ModelCheckBox){
        /**
         * Redraw the component.
         * @method redraw
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.redraw = function(){};
    }
    Object.defineProperty(ModelCheckBox.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!ModelCheckBox){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf ModelCheckBox
         */
        P.ModelCheckBox.prototype.focus = function(){};
    }
})();