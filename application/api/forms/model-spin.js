(function() {
    var javaClass = Java.type("com.eas.client.forms.api.components.model.ModelSpin");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ModelSpin(aDelegate);
    });
    
    /**
     * A model component that represents a combination of a numeric text box and arrow buttons to change the value incrementally.
     * @constructor ModelSpin ModelSpin
     */
    P.ModelSpin = function ModelSpin() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(ModelSpin.superclass)
            ModelSpin.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "ModelSpin", {value: ModelSpin});
    Object.defineProperty(ModelSpin.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!ModelSpin){
        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.parent = {};
    }
    Object.defineProperty(ModelSpin.prototype, "onMouseReleased", {
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
    if(!ModelSpin){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.onMouseReleased = {};
    }
    Object.defineProperty(ModelSpin.prototype, "foreground", {
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
    if(!ModelSpin){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.foreground = {};
    }
    Object.defineProperty(ModelSpin.prototype, "onComponentMoved", {
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
    if(!ModelSpin){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.onComponentMoved = {};
    }
    Object.defineProperty(ModelSpin.prototype, "onRender", {
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
    if(!ModelSpin){
        /**
         * Component's rendering event handler function.
         * @property onRender
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.onRender = {};
    }
    Object.defineProperty(ModelSpin.prototype, "model", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.model;
            return P.boxAsJs(value);
        }
    });
    if(!ModelSpin){
        /**
         * Model of the component. It will be used for data binding.
         * @property model
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.model = {};
    }
    Object.defineProperty(ModelSpin.prototype, "text", {
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
    if(!ModelSpin){
        /**
         * Generated property jsDoc.
         * @property text
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.text = '';
    }
    Object.defineProperty(ModelSpin.prototype, "onMouseEntered", {
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
    if(!ModelSpin){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.onMouseEntered = {};
    }
    Object.defineProperty(ModelSpin.prototype, "toolTipText", {
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
    if(!ModelSpin){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.toolTipText = '';
    }
    Object.defineProperty(ModelSpin.prototype, "height", {
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
    if(!ModelSpin){
        /**
         * Height of the component.
         * @property height
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.height = 0;
    }
    Object.defineProperty(ModelSpin.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!ModelSpin){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.element = {};
    }
    Object.defineProperty(ModelSpin.prototype, "onComponentShown", {
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
    if(!ModelSpin){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.onComponentShown = {};
    }
    Object.defineProperty(ModelSpin.prototype, "visible", {
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
    if(!ModelSpin){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.visible = true;
    }
    Object.defineProperty(ModelSpin.prototype, "onComponentHidden", {
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
    if(!ModelSpin){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.onComponentHidden = {};
    }
    Object.defineProperty(ModelSpin.prototype, "onActionPerformed", {
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
    if(!ModelSpin){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.onActionPerformed = {};
    }
    Object.defineProperty(ModelSpin.prototype, "onKeyReleased", {
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
    if(!ModelSpin){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.onKeyReleased = {};
    }
    Object.defineProperty(ModelSpin.prototype, "focusable", {
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
    if(!ModelSpin){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.focusable = true;
    }
    Object.defineProperty(ModelSpin.prototype, "onKeyTyped", {
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
    if(!ModelSpin){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.onKeyTyped = {};
    }
    Object.defineProperty(ModelSpin.prototype, "onMouseWheelMoved", {
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
    if(!ModelSpin){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(ModelSpin.prototype, "field", {
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
    if(!ModelSpin){
        /**
         * Model entity's field.
         * @property field
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.field = {};
    }
    Object.defineProperty(ModelSpin.prototype, "left", {
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
    if(!ModelSpin){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.left = 0;
    }
    Object.defineProperty(ModelSpin.prototype, "background", {
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
    if(!ModelSpin){
        /**
         * The background color of this component.
         * @property background
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.background = {};
    }
    Object.defineProperty(ModelSpin.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!ModelSpin){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.name = '';
    }
    Object.defineProperty(ModelSpin.prototype, "cursor", {
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
    if(!ModelSpin){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.cursor = {};
    }
    Object.defineProperty(ModelSpin.prototype, "onMouseDragged", {
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
    if(!ModelSpin){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.onMouseDragged = {};
    }
    Object.defineProperty(ModelSpin.prototype, "onFocusLost", {
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
    if(!ModelSpin){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.onFocusLost = {};
    }
    Object.defineProperty(ModelSpin.prototype, "emptyText", {
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
    if(!ModelSpin){
        /**
         * Generated property jsDoc.
         * @property emptyText
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.emptyText = '';
    }
    Object.defineProperty(ModelSpin.prototype, "onMousePressed", {
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
    if(!ModelSpin){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.onMousePressed = {};
    }
    Object.defineProperty(ModelSpin.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!ModelSpin){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.error = '';
    }
    Object.defineProperty(ModelSpin.prototype, "enabled", {
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
    if(!ModelSpin){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.enabled = true;
    }
    Object.defineProperty(ModelSpin.prototype, "onSelect", {
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
    if(!ModelSpin){
        /**
         * Component's selection event handler function.
         * @property onSelect
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.onSelect = {};
    }
    Object.defineProperty(ModelSpin.prototype, "min", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.min;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.min = P.boxAsJava(aValue);
        }
    });
    if(!ModelSpin){
        /**
         * Determines the lower bound of spinner's value. If it's null, valus is unlimited at lower bound.
         * @property min
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.min = 0;
    }
    Object.defineProperty(ModelSpin.prototype, "componentPopupMenu", {
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
    if(!ModelSpin){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(ModelSpin.prototype, "top", {
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
    if(!ModelSpin){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.top = 0;
    }
    Object.defineProperty(ModelSpin.prototype, "onComponentResized", {
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
    if(!ModelSpin){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.onComponentResized = {};
    }
    Object.defineProperty(ModelSpin.prototype, "value", {
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
    if(!ModelSpin){
        /**
         * Component's value.
         * @property value
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.value = {};
    }
    Object.defineProperty(ModelSpin.prototype, "onMouseMoved", {
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
    if(!ModelSpin){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.onMouseMoved = {};
    }
    Object.defineProperty(ModelSpin.prototype, "opaque", {
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
    if(!ModelSpin){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.opaque = true;
    }
    Object.defineProperty(ModelSpin.prototype, "max", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.max;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.max = P.boxAsJava(aValue);
        }
    });
    if(!ModelSpin){
        /**
         * Determines the upper bound of spinner's value. If it's null, valus is unlimited at upper bound.
         * @property max
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.max = 0;
    }
    Object.defineProperty(ModelSpin.prototype, "editable", {
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
    if(!ModelSpin){
        /**
         * Determines if component is editable.
         * @property editable
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.editable = true;
    }
    Object.defineProperty(ModelSpin.prototype, "nextFocusableComponent", {
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
    if(!ModelSpin){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(ModelSpin.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!ModelSpin){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.component = {};
    }
    Object.defineProperty(ModelSpin.prototype, "onFocusGained", {
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
    if(!ModelSpin){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.onFocusGained = {};
    }
    Object.defineProperty(ModelSpin.prototype, "onMouseClicked", {
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
    if(!ModelSpin){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.onMouseClicked = {};
    }
    Object.defineProperty(ModelSpin.prototype, "onMouseExited", {
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
    if(!ModelSpin){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.onMouseExited = {};
    }
    Object.defineProperty(ModelSpin.prototype, "width", {
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
    if(!ModelSpin){
        /**
         * Width of the component.
         * @property width
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.width = 0;
    }
    Object.defineProperty(ModelSpin.prototype, "step", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.step;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.step = P.boxAsJava(aValue);
        }
    });
    if(!ModelSpin){
        /**
         * Determines the spinner's value change step. Can't be null.
         * @property step
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.step = 0;
    }
    Object.defineProperty(ModelSpin.prototype, "font", {
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
    if(!ModelSpin){
        /**
         * The font of this component.
         * @property font
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.font = {};
    }
    Object.defineProperty(ModelSpin.prototype, "onKeyPressed", {
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
    if(!ModelSpin){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.onKeyPressed = {};
    }
    Object.defineProperty(ModelSpin.prototype, "redraw", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.redraw();
            return P.boxAsJs(value);
        }
    });
    if(!ModelSpin){
        /**
         * Redraw the component.
         * @method redraw
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.redraw = function(){};
    }
    Object.defineProperty(ModelSpin.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!ModelSpin){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf ModelSpin
         */
        P.ModelSpin.prototype.focus = function(){};
    }
})();