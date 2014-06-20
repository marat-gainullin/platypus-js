(function() {
    var javaClass = Java.type("com.eas.client.forms.api.components.model.ModelImage");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ModelImage(aDelegate);
    });
    
    /**
     * A model component that shows an image.
     * @constructor ModelImage ModelImage
     */
    P.ModelImage = function ModelImage() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(ModelImage.superclass)
            ModelImage.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "ModelImage", {value: ModelImage});
    Object.defineProperty(ModelImage.prototype, "cursor", {
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
    if(!ModelImage){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.cursor = {};
    }
    Object.defineProperty(ModelImage.prototype, "onMouseDragged", {
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
    if(!ModelImage){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.onMouseDragged = {};
    }
    Object.defineProperty(ModelImage.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!ModelImage){
        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.parent = {};
    }
    Object.defineProperty(ModelImage.prototype, "onMouseReleased", {
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
    if(!ModelImage){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.onMouseReleased = {};
    }
    Object.defineProperty(ModelImage.prototype, "onFocusLost", {
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
    if(!ModelImage){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.onFocusLost = {};
    }
    Object.defineProperty(ModelImage.prototype, "onMousePressed", {
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
    if(!ModelImage){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.onMousePressed = {};
    }
    Object.defineProperty(ModelImage.prototype, "foreground", {
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
    if(!ModelImage){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.foreground = {};
    }
    Object.defineProperty(ModelImage.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!ModelImage){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.error = '';
    }
    Object.defineProperty(ModelImage.prototype, "enabled", {
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
    if(!ModelImage){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.enabled = true;
    }
    Object.defineProperty(ModelImage.prototype, "onComponentMoved", {
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
    if(!ModelImage){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.onComponentMoved = {};
    }
    Object.defineProperty(ModelImage.prototype, "onSelect", {
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
    if(!ModelImage){
        /**
         * Component's selection event handler function.
         * @property onSelect
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.onSelect = {};
    }
    Object.defineProperty(ModelImage.prototype, "componentPopupMenu", {
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
    if(!ModelImage){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(ModelImage.prototype, "top", {
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
    if(!ModelImage){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.top = 0;
    }
    Object.defineProperty(ModelImage.prototype, "onRender", {
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
    if(!ModelImage){
        /**
         * Component's rendering event handler function.
         * @property onRender
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.onRender = {};
    }
    Object.defineProperty(ModelImage.prototype, "plain", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.plain;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.plain = P.boxAsJava(aValue);
        }
    });
    if(!ModelImage){
        /**
         * Determines if image is displayed with real dimensions and not scaled.
         * If false, the image is fitted and can be scaled with the mouse wheel.
         * @property plain
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.plain = true;
    }
    Object.defineProperty(ModelImage.prototype, "onComponentResized", {
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
    if(!ModelImage){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.onComponentResized = {};
    }
    Object.defineProperty(ModelImage.prototype, "model", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.model;
            return P.boxAsJs(value);
        }
    });
    if(!ModelImage){
        /**
         * Model of the component. It will be used for data binding.
         * @property model
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.model = {};
    }
    Object.defineProperty(ModelImage.prototype, "onMouseEntered", {
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
    if(!ModelImage){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.onMouseEntered = {};
    }
    Object.defineProperty(ModelImage.prototype, "value", {
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
    if(!ModelImage){
        /**
         * Component's value.
         * @property value
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.value = {};
    }
    Object.defineProperty(ModelImage.prototype, "toolTipText", {
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
    if(!ModelImage){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.toolTipText = '';
    }
    Object.defineProperty(ModelImage.prototype, "height", {
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
    if(!ModelImage){
        /**
         * Height of the component.
         * @property height
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.height = 0;
    }
    Object.defineProperty(ModelImage.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!ModelImage){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.element = {};
    }
    Object.defineProperty(ModelImage.prototype, "onComponentShown", {
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
    if(!ModelImage){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.onComponentShown = {};
    }
    Object.defineProperty(ModelImage.prototype, "onMouseMoved", {
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
    if(!ModelImage){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.onMouseMoved = {};
    }
    Object.defineProperty(ModelImage.prototype, "opaque", {
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
    if(!ModelImage){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.opaque = true;
    }
    Object.defineProperty(ModelImage.prototype, "visible", {
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
    if(!ModelImage){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.visible = true;
    }
    Object.defineProperty(ModelImage.prototype, "onComponentHidden", {
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
    if(!ModelImage){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.onComponentHidden = {};
    }
    Object.defineProperty(ModelImage.prototype, "editable", {
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
    if(!ModelImage){
        /**
         * Determines if component is editable.
         * @property editable
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.editable = true;
    }
    Object.defineProperty(ModelImage.prototype, "nextFocusableComponent", {
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
    if(!ModelImage){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(ModelImage.prototype, "onActionPerformed", {
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
    if(!ModelImage){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.onActionPerformed = {};
    }
    Object.defineProperty(ModelImage.prototype, "onKeyReleased", {
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
    if(!ModelImage){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.onKeyReleased = {};
    }
    Object.defineProperty(ModelImage.prototype, "focusable", {
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
    if(!ModelImage){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.focusable = true;
    }
    Object.defineProperty(ModelImage.prototype, "onKeyTyped", {
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
    if(!ModelImage){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.onKeyTyped = {};
    }
    Object.defineProperty(ModelImage.prototype, "onMouseWheelMoved", {
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
    if(!ModelImage){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(ModelImage.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!ModelImage){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.component = {};
    }
    Object.defineProperty(ModelImage.prototype, "field", {
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
    if(!ModelImage){
        /**
         * Model entity's field.
         * @property field
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.field = {};
    }
    Object.defineProperty(ModelImage.prototype, "onFocusGained", {
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
    if(!ModelImage){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.onFocusGained = {};
    }
    Object.defineProperty(ModelImage.prototype, "left", {
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
    if(!ModelImage){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.left = 0;
    }
    Object.defineProperty(ModelImage.prototype, "background", {
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
    if(!ModelImage){
        /**
         * The background color of this component.
         * @property background
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.background = {};
    }
    Object.defineProperty(ModelImage.prototype, "onMouseClicked", {
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
    if(!ModelImage){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.onMouseClicked = {};
    }
    Object.defineProperty(ModelImage.prototype, "onMouseExited", {
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
    if(!ModelImage){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.onMouseExited = {};
    }
    Object.defineProperty(ModelImage.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!ModelImage){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.name = '';
    }
    Object.defineProperty(ModelImage.prototype, "width", {
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
    if(!ModelImage){
        /**
         * Width of the component.
         * @property width
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.width = 0;
    }
    Object.defineProperty(ModelImage.prototype, "font", {
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
    if(!ModelImage){
        /**
         * The font of this component.
         * @property font
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.font = {};
    }
    Object.defineProperty(ModelImage.prototype, "onKeyPressed", {
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
    if(!ModelImage){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.onKeyPressed = {};
    }
    Object.defineProperty(ModelImage.prototype, "redraw", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.redraw();
            return P.boxAsJs(value);
        }
    });
    if(!ModelImage){
        /**
         * Redraw the component.
         * @method redraw
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.redraw = function(){};
    }
    Object.defineProperty(ModelImage.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!ModelImage){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.focus = function(){};
    }
})();