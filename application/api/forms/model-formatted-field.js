(function() {
    var javaClass = Java.type("com.eas.client.forms.api.components.model.ModelFormattedField");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ModelFormattedField(aDelegate);
    });
    
    /**
     * A model component that shows a date.
     * @constructor ModelFormattedField ModelFormattedField
     */
    P.ModelFormattedField = function ModelFormattedField() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(ModelFormattedField.superclass)
            ModelFormattedField.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "ModelFormattedField", {value: ModelFormattedField});
    Object.defineProperty(ModelFormattedField.prototype, "cursor", {
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
    if(!ModelFormattedField){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.cursor = {};
    }
    Object.defineProperty(ModelFormattedField.prototype, "onMouseDragged", {
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
    if(!ModelFormattedField){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.onMouseDragged = {};
    }
    Object.defineProperty(ModelFormattedField.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!ModelFormattedField){
        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.parent = {};
    }
    Object.defineProperty(ModelFormattedField.prototype, "onMouseReleased", {
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
    if(!ModelFormattedField){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.onMouseReleased = {};
    }
    Object.defineProperty(ModelFormattedField.prototype, "onFocusLost", {
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
    if(!ModelFormattedField){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.onFocusLost = {};
    }
    Object.defineProperty(ModelFormattedField.prototype, "emptyText", {
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
    if(!ModelFormattedField){
        /**
         * Generated property jsDoc.
         * @property emptyText
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.emptyText = '';
    }
    Object.defineProperty(ModelFormattedField.prototype, "onMousePressed", {
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
    if(!ModelFormattedField){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.onMousePressed = {};
    }
    Object.defineProperty(ModelFormattedField.prototype, "foreground", {
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
    if(!ModelFormattedField){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.foreground = {};
    }
    Object.defineProperty(ModelFormattedField.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!ModelFormattedField){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.error = '';
    }
    Object.defineProperty(ModelFormattedField.prototype, "enabled", {
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
    if(!ModelFormattedField){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.enabled = true;
    }
    Object.defineProperty(ModelFormattedField.prototype, "onComponentMoved", {
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
    if(!ModelFormattedField){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.onComponentMoved = {};
    }
    Object.defineProperty(ModelFormattedField.prototype, "onSelect", {
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
    if(!ModelFormattedField){
        /**
         * Component's selection event handler function.
         * @property onSelect
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.onSelect = {};
    }
    Object.defineProperty(ModelFormattedField.prototype, "componentPopupMenu", {
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
    if(!ModelFormattedField){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(ModelFormattedField.prototype, "top", {
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
    if(!ModelFormattedField){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.top = 0;
    }
    Object.defineProperty(ModelFormattedField.prototype, "onRender", {
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
    if(!ModelFormattedField){
        /**
         * Component's rendering event handler function.
         * @property onRender
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.onRender = {};
    }
    Object.defineProperty(ModelFormattedField.prototype, "onComponentResized", {
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
    if(!ModelFormattedField){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.onComponentResized = {};
    }
    Object.defineProperty(ModelFormattedField.prototype, "model", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.model;
            return P.boxAsJs(value);
        }
    });
    if(!ModelFormattedField){
        /**
         * Model of the component. It will be used for data binding.
         * @property model
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.model = {};
    }
    Object.defineProperty(ModelFormattedField.prototype, "text", {
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
    if(!ModelFormattedField){
        /**
         * Generated property jsDoc.
         * @property text
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.text = '';
    }
    Object.defineProperty(ModelFormattedField.prototype, "onMouseEntered", {
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
    if(!ModelFormattedField){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.onMouseEntered = {};
    }
    Object.defineProperty(ModelFormattedField.prototype, "value", {
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
    if(!ModelFormattedField){
        /**
         * Component's value.
         * @property value
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.value = {};
    }
    Object.defineProperty(ModelFormattedField.prototype, "toolTipText", {
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
    if(!ModelFormattedField){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.toolTipText = '';
    }
    Object.defineProperty(ModelFormattedField.prototype, "height", {
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
    if(!ModelFormattedField){
        /**
         * Height of the component.
         * @property height
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.height = 0;
    }
    Object.defineProperty(ModelFormattedField.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!ModelFormattedField){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.element = {};
    }
    Object.defineProperty(ModelFormattedField.prototype, "onComponentShown", {
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
    if(!ModelFormattedField){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.onComponentShown = {};
    }
    Object.defineProperty(ModelFormattedField.prototype, "onMouseMoved", {
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
    if(!ModelFormattedField){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.onMouseMoved = {};
    }
    Object.defineProperty(ModelFormattedField.prototype, "opaque", {
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
    if(!ModelFormattedField){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.opaque = true;
    }
    Object.defineProperty(ModelFormattedField.prototype, "visible", {
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
    if(!ModelFormattedField){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.visible = true;
    }
    Object.defineProperty(ModelFormattedField.prototype, "onComponentHidden", {
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
    if(!ModelFormattedField){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.onComponentHidden = {};
    }
    Object.defineProperty(ModelFormattedField.prototype, "editable", {
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
    if(!ModelFormattedField){
        /**
         * Determines if component is editable.
         * @property editable
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.editable = true;
    }
    Object.defineProperty(ModelFormattedField.prototype, "nextFocusableComponent", {
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
    if(!ModelFormattedField){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(ModelFormattedField.prototype, "format", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.format;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.format = P.boxAsJava(aValue);
        }
    });
    if(!ModelFormattedField){
        /**
         * The format string of the component.
         * @property format
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.format = '';
    }
    Object.defineProperty(ModelFormattedField.prototype, "onActionPerformed", {
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
    if(!ModelFormattedField){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.onActionPerformed = {};
    }
    Object.defineProperty(ModelFormattedField.prototype, "onKeyReleased", {
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
    if(!ModelFormattedField){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.onKeyReleased = {};
    }
    Object.defineProperty(ModelFormattedField.prototype, "focusable", {
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
    if(!ModelFormattedField){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.focusable = true;
    }
    Object.defineProperty(ModelFormattedField.prototype, "onKeyTyped", {
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
    if(!ModelFormattedField){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.onKeyTyped = {};
    }
    Object.defineProperty(ModelFormattedField.prototype, "onMouseWheelMoved", {
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
    if(!ModelFormattedField){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(ModelFormattedField.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!ModelFormattedField){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.component = {};
    }
    Object.defineProperty(ModelFormattedField.prototype, "field", {
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
    if(!ModelFormattedField){
        /**
         * Model entity's field.
         * @property field
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.field = {};
    }
    Object.defineProperty(ModelFormattedField.prototype, "onFocusGained", {
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
    if(!ModelFormattedField){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.onFocusGained = {};
    }
    Object.defineProperty(ModelFormattedField.prototype, "left", {
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
    if(!ModelFormattedField){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.left = 0;
    }
    Object.defineProperty(ModelFormattedField.prototype, "background", {
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
    if(!ModelFormattedField){
        /**
         * The background color of this component.
         * @property background
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.background = {};
    }
    Object.defineProperty(ModelFormattedField.prototype, "onMouseClicked", {
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
    if(!ModelFormattedField){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.onMouseClicked = {};
    }
    Object.defineProperty(ModelFormattedField.prototype, "onMouseExited", {
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
    if(!ModelFormattedField){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.onMouseExited = {};
    }
    Object.defineProperty(ModelFormattedField.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!ModelFormattedField){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.name = '';
    }
    Object.defineProperty(ModelFormattedField.prototype, "width", {
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
    if(!ModelFormattedField){
        /**
         * Width of the component.
         * @property width
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.width = 0;
    }
    Object.defineProperty(ModelFormattedField.prototype, "font", {
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
    if(!ModelFormattedField){
        /**
         * The font of this component.
         * @property font
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.font = {};
    }
    Object.defineProperty(ModelFormattedField.prototype, "onKeyPressed", {
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
    if(!ModelFormattedField){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.onKeyPressed = {};
    }
    Object.defineProperty(ModelFormattedField.prototype, "redraw", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.redraw();
            return P.boxAsJs(value);
        }
    });
    if(!ModelFormattedField){
        /**
         * Redraw the component.
         * @method redraw
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.redraw = function(){};
    }
    Object.defineProperty(ModelFormattedField.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!ModelFormattedField){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf ModelFormattedField
         */
        P.ModelFormattedField.prototype.focus = function(){};
    }
})();