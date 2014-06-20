(function() {
    var javaClass = Java.type("com.eas.client.forms.api.components.model.ModelCombo");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ModelCombo(aDelegate);
    });
    
    /**
     * A model component that combines a button or editable field and a drop-down list.
     * @constructor ModelCombo ModelCombo
     */
    P.ModelCombo = function ModelCombo() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(ModelCombo.superclass)
            ModelCombo.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "ModelCombo", {value: ModelCombo});
    Object.defineProperty(ModelCombo.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!ModelCombo){
        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.parent = {};
    }
    Object.defineProperty(ModelCombo.prototype, "onMouseReleased", {
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
    if(!ModelCombo){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.onMouseReleased = {};
    }
    Object.defineProperty(ModelCombo.prototype, "foreground", {
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
    if(!ModelCombo){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.foreground = {};
    }
    Object.defineProperty(ModelCombo.prototype, "onComponentMoved", {
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
    if(!ModelCombo){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.onComponentMoved = {};
    }
    Object.defineProperty(ModelCombo.prototype, "onRender", {
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
    if(!ModelCombo){
        /**
         * Component's rendering event handler function.
         * @property onRender
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.onRender = {};
    }
    Object.defineProperty(ModelCombo.prototype, "model", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.model;
            return P.boxAsJs(value);
        }
    });
    if(!ModelCombo){
        /**
         * Model of the component. It will be used for data binding.
         * @property model
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.model = {};
    }
    Object.defineProperty(ModelCombo.prototype, "text", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.text;
            return P.boxAsJs(value);
        }
    });
    if(!ModelCombo){
        /**
         * Generated property jsDoc.
         * @property text
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.text = '';
    }
    Object.defineProperty(ModelCombo.prototype, "onMouseEntered", {
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
    if(!ModelCombo){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.onMouseEntered = {};
    }
    Object.defineProperty(ModelCombo.prototype, "toolTipText", {
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
    if(!ModelCombo){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.toolTipText = '';
    }
    Object.defineProperty(ModelCombo.prototype, "height", {
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
    if(!ModelCombo){
        /**
         * Height of the component.
         * @property height
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.height = 0;
    }
    Object.defineProperty(ModelCombo.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!ModelCombo){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.element = {};
    }
    Object.defineProperty(ModelCombo.prototype, "onComponentShown", {
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
    if(!ModelCombo){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.onComponentShown = {};
    }
    Object.defineProperty(ModelCombo.prototype, "visible", {
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
    if(!ModelCombo){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.visible = true;
    }
    Object.defineProperty(ModelCombo.prototype, "onComponentHidden", {
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
    if(!ModelCombo){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.onComponentHidden = {};
    }
    Object.defineProperty(ModelCombo.prototype, "onActionPerformed", {
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
    if(!ModelCombo){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.onActionPerformed = {};
    }
    Object.defineProperty(ModelCombo.prototype, "onKeyReleased", {
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
    if(!ModelCombo){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.onKeyReleased = {};
    }
    Object.defineProperty(ModelCombo.prototype, "focusable", {
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
    if(!ModelCombo){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.focusable = true;
    }
    Object.defineProperty(ModelCombo.prototype, "onKeyTyped", {
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
    if(!ModelCombo){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.onKeyTyped = {};
    }
    Object.defineProperty(ModelCombo.prototype, "list", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.list;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.list = P.boxAsJava(aValue);
        }
    });
    if(!ModelCombo){
        /**
         * Determines if component shown as list.
         * @property list
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.list = true;
    }
    Object.defineProperty(ModelCombo.prototype, "onMouseWheelMoved", {
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
    if(!ModelCombo){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(ModelCombo.prototype, "field", {
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
    if(!ModelCombo){
        /**
         * Model entity's field.
         * @property field
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.field = {};
    }
    Object.defineProperty(ModelCombo.prototype, "left", {
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
    if(!ModelCombo){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.left = 0;
    }
    Object.defineProperty(ModelCombo.prototype, "background", {
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
    if(!ModelCombo){
        /**
         * The background color of this component.
         * @property background
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.background = {};
    }
    Object.defineProperty(ModelCombo.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!ModelCombo){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.name = '';
    }
    Object.defineProperty(ModelCombo.prototype, "displayField", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.displayField;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.displayField = P.boxAsJava(aValue);
        }
    });
    if(!ModelCombo){
        /**
         * Display field of the component.
         * @property displayField
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.displayField = {};
    }
    Object.defineProperty(ModelCombo.prototype, "cursor", {
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
    if(!ModelCombo){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.cursor = {};
    }
    Object.defineProperty(ModelCombo.prototype, "onMouseDragged", {
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
    if(!ModelCombo){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.onMouseDragged = {};
    }
    Object.defineProperty(ModelCombo.prototype, "onFocusLost", {
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
    if(!ModelCombo){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.onFocusLost = {};
    }
    Object.defineProperty(ModelCombo.prototype, "emptyText", {
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
    if(!ModelCombo){
        /**
         * Generated property jsDoc.
         * @property emptyText
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.emptyText = '';
    }
    Object.defineProperty(ModelCombo.prototype, "onMousePressed", {
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
    if(!ModelCombo){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.onMousePressed = {};
    }
    Object.defineProperty(ModelCombo.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!ModelCombo){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.error = '';
    }
    Object.defineProperty(ModelCombo.prototype, "enabled", {
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
    if(!ModelCombo){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.enabled = true;
    }
    Object.defineProperty(ModelCombo.prototype, "onSelect", {
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
    if(!ModelCombo){
        /**
         * Component's selection event handler function.
         * @property onSelect
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.onSelect = {};
    }
    Object.defineProperty(ModelCombo.prototype, "componentPopupMenu", {
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
    if(!ModelCombo){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(ModelCombo.prototype, "top", {
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
    if(!ModelCombo){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.top = 0;
    }
    Object.defineProperty(ModelCombo.prototype, "onComponentResized", {
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
    if(!ModelCombo){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.onComponentResized = {};
    }
    Object.defineProperty(ModelCombo.prototype, "value", {
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
    if(!ModelCombo){
        /**
         * Component's value.
         * @property value
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.value = {};
    }
    Object.defineProperty(ModelCombo.prototype, "onMouseMoved", {
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
    if(!ModelCombo){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.onMouseMoved = {};
    }
    Object.defineProperty(ModelCombo.prototype, "opaque", {
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
    if(!ModelCombo){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.opaque = true;
    }
    Object.defineProperty(ModelCombo.prototype, "editable", {
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
    if(!ModelCombo){
        /**
         * Determines if component is editable.
         * @property editable
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.editable = true;
    }
    Object.defineProperty(ModelCombo.prototype, "nextFocusableComponent", {
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
    if(!ModelCombo){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(ModelCombo.prototype, "valueField", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.valueField;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.valueField = P.boxAsJava(aValue);
        }
    });
    if(!ModelCombo){
        /**
         * Value field of the component.
         * @property valueField
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.valueField = {};
    }
    Object.defineProperty(ModelCombo.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!ModelCombo){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.component = {};
    }
    Object.defineProperty(ModelCombo.prototype, "onFocusGained", {
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
    if(!ModelCombo){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.onFocusGained = {};
    }
    Object.defineProperty(ModelCombo.prototype, "onMouseClicked", {
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
    if(!ModelCombo){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.onMouseClicked = {};
    }
    Object.defineProperty(ModelCombo.prototype, "onMouseExited", {
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
    if(!ModelCombo){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.onMouseExited = {};
    }
    Object.defineProperty(ModelCombo.prototype, "width", {
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
    if(!ModelCombo){
        /**
         * Width of the component.
         * @property width
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.width = 0;
    }
    Object.defineProperty(ModelCombo.prototype, "font", {
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
    if(!ModelCombo){
        /**
         * The font of this component.
         * @property font
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.font = {};
    }
    Object.defineProperty(ModelCombo.prototype, "onKeyPressed", {
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
    if(!ModelCombo){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.onKeyPressed = {};
    }
    Object.defineProperty(ModelCombo.prototype, "redraw", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.redraw();
            return P.boxAsJs(value);
        }
    });
    if(!ModelCombo){
        /**
         * Redraw the component.
         * @method redraw
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.redraw = function(){};
    }
    Object.defineProperty(ModelCombo.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!ModelCombo){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf ModelCombo
         */
        P.ModelCombo.prototype.focus = function(){};
    }
})();