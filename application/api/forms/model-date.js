(function() {
    var javaClass = Java.type("com.eas.client.forms.api.components.model.ModelDate");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ModelDate(aDelegate);
    });
    
    /**
     * A model component that shows a date.
     * @constructor ModelDate ModelDate
     */
    P.ModelDate = function ModelDate() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(ModelDate.superclass)
            ModelDate.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "ModelDate", {value: ModelDate});
    Object.defineProperty(ModelDate.prototype, "cursor", {
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
    if(!ModelDate){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.cursor = {};
    }
    Object.defineProperty(ModelDate.prototype, "onMouseDragged", {
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
    if(!ModelDate){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.onMouseDragged = {};
    }
    Object.defineProperty(ModelDate.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!ModelDate){
        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.parent = {};
    }
    Object.defineProperty(ModelDate.prototype, "onMouseReleased", {
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
    if(!ModelDate){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.onMouseReleased = {};
    }
    Object.defineProperty(ModelDate.prototype, "onFocusLost", {
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
    if(!ModelDate){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.onFocusLost = {};
    }
    Object.defineProperty(ModelDate.prototype, "dateFormat", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.dateFormat;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.dateFormat = P.boxAsJava(aValue);
        }
    });
    if(!ModelDate){
        /**
         * Generated property jsDoc.
         * @property dateFormat
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.dateFormat = '';
    }
    Object.defineProperty(ModelDate.prototype, "emptyText", {
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
    if(!ModelDate){
        /**
         * Generated property jsDoc.
         * @property emptyText
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.emptyText = '';
    }
    Object.defineProperty(ModelDate.prototype, "onMousePressed", {
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
    if(!ModelDate){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.onMousePressed = {};
    }
    Object.defineProperty(ModelDate.prototype, "foreground", {
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
    if(!ModelDate){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.foreground = {};
    }
    Object.defineProperty(ModelDate.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!ModelDate){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.error = '';
    }
    Object.defineProperty(ModelDate.prototype, "enabled", {
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
    if(!ModelDate){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.enabled = true;
    }
    Object.defineProperty(ModelDate.prototype, "onComponentMoved", {
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
    if(!ModelDate){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.onComponentMoved = {};
    }
    Object.defineProperty(ModelDate.prototype, "onSelect", {
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
    if(!ModelDate){
        /**
         * Component's selection event handler function.
         * @property onSelect
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.onSelect = {};
    }
    Object.defineProperty(ModelDate.prototype, "expanded", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.expanded;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.expanded = P.boxAsJava(aValue);
        }
    });
    if(!ModelDate){
        /**
         * Sets up the control appearance. If true, than calndar panel is displayed, otherwise date/time combo is displayed.
         * @property expanded
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.expanded = true;
    }
    Object.defineProperty(ModelDate.prototype, "componentPopupMenu", {
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
    if(!ModelDate){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(ModelDate.prototype, "top", {
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
    if(!ModelDate){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.top = 0;
    }
    Object.defineProperty(ModelDate.prototype, "onRender", {
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
    if(!ModelDate){
        /**
         * Component's rendering event handler function.
         * @property onRender
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.onRender = {};
    }
    Object.defineProperty(ModelDate.prototype, "onComponentResized", {
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
    if(!ModelDate){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.onComponentResized = {};
    }
    Object.defineProperty(ModelDate.prototype, "model", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.model;
            return P.boxAsJs(value);
        }
    });
    if(!ModelDate){
        /**
         * Model of the component. It will be used for data binding.
         * @property model
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.model = {};
    }
    Object.defineProperty(ModelDate.prototype, "text", {
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
    if(!ModelDate){
        /**
         * Generated property jsDoc.
         * @property text
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.text = '';
    }
    Object.defineProperty(ModelDate.prototype, "onMouseEntered", {
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
    if(!ModelDate){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.onMouseEntered = {};
    }
    Object.defineProperty(ModelDate.prototype, "value", {
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
    if(!ModelDate){
        /**
         * Component's value.
         * @property value
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.value = {};
    }
    Object.defineProperty(ModelDate.prototype, "toolTipText", {
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
    if(!ModelDate){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.toolTipText = '';
    }
    Object.defineProperty(ModelDate.prototype, "height", {
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
    if(!ModelDate){
        /**
         * Height of the component.
         * @property height
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.height = 0;
    }
    Object.defineProperty(ModelDate.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!ModelDate){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.element = {};
    }
    Object.defineProperty(ModelDate.prototype, "onComponentShown", {
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
    if(!ModelDate){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.onComponentShown = {};
    }
    Object.defineProperty(ModelDate.prototype, "onMouseMoved", {
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
    if(!ModelDate){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.onMouseMoved = {};
    }
    Object.defineProperty(ModelDate.prototype, "opaque", {
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
    if(!ModelDate){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.opaque = true;
    }
    Object.defineProperty(ModelDate.prototype, "visible", {
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
    if(!ModelDate){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.visible = true;
    }
    Object.defineProperty(ModelDate.prototype, "onComponentHidden", {
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
    if(!ModelDate){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.onComponentHidden = {};
    }
    Object.defineProperty(ModelDate.prototype, "editable", {
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
    if(!ModelDate){
        /**
         * Determines if component is editable.
         * @property editable
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.editable = true;
    }
    Object.defineProperty(ModelDate.prototype, "nextFocusableComponent", {
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
    if(!ModelDate){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(ModelDate.prototype, "onActionPerformed", {
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
    if(!ModelDate){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.onActionPerformed = {};
    }
    Object.defineProperty(ModelDate.prototype, "onKeyReleased", {
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
    if(!ModelDate){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.onKeyReleased = {};
    }
    Object.defineProperty(ModelDate.prototype, "focusable", {
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
    if(!ModelDate){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.focusable = true;
    }
    Object.defineProperty(ModelDate.prototype, "onKeyTyped", {
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
    if(!ModelDate){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.onKeyTyped = {};
    }
    Object.defineProperty(ModelDate.prototype, "onMouseWheelMoved", {
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
    if(!ModelDate){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(ModelDate.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!ModelDate){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.component = {};
    }
    Object.defineProperty(ModelDate.prototype, "field", {
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
    if(!ModelDate){
        /**
         * Model entity's field.
         * @property field
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.field = {};
    }
    Object.defineProperty(ModelDate.prototype, "onFocusGained", {
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
    if(!ModelDate){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.onFocusGained = {};
    }
    Object.defineProperty(ModelDate.prototype, "left", {
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
    if(!ModelDate){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.left = 0;
    }
    Object.defineProperty(ModelDate.prototype, "background", {
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
    if(!ModelDate){
        /**
         * The background color of this component.
         * @property background
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.background = {};
    }
    Object.defineProperty(ModelDate.prototype, "onMouseClicked", {
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
    if(!ModelDate){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.onMouseClicked = {};
    }
    Object.defineProperty(ModelDate.prototype, "onMouseExited", {
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
    if(!ModelDate){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.onMouseExited = {};
    }
    Object.defineProperty(ModelDate.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!ModelDate){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.name = '';
    }
    Object.defineProperty(ModelDate.prototype, "width", {
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
    if(!ModelDate){
        /**
         * Width of the component.
         * @property width
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.width = 0;
    }
    Object.defineProperty(ModelDate.prototype, "font", {
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
    if(!ModelDate){
        /**
         * The font of this component.
         * @property font
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.font = {};
    }
    Object.defineProperty(ModelDate.prototype, "onKeyPressed", {
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
    if(!ModelDate){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.onKeyPressed = {};
    }
    Object.defineProperty(ModelDate.prototype, "redraw", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.redraw();
            return P.boxAsJs(value);
        }
    });
    if(!ModelDate){
        /**
         * Redraw the component.
         * @method redraw
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.redraw = function(){};
    }
    Object.defineProperty(ModelDate.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!ModelDate){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf ModelDate
         */
        P.ModelDate.prototype.focus = function(){};
    }
})();