(function() {
    var javaClass = Java.type("com.eas.client.forms.api.components.model.ModelTextArea");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ModelTextArea(aDelegate);
    });
    
    /**
     * A model components for a text area.
     * @constructor ModelTextArea ModelTextArea
     */
    P.ModelTextArea = function ModelTextArea() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(ModelTextArea.superclass)
            ModelTextArea.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "ModelTextArea", {value: ModelTextArea});
    Object.defineProperty(ModelTextArea.prototype, "cursor", {
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
    if(!ModelTextArea){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.cursor = {};
    }
    Object.defineProperty(ModelTextArea.prototype, "onMouseDragged", {
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
    if(!ModelTextArea){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.onMouseDragged = {};
    }
    Object.defineProperty(ModelTextArea.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!ModelTextArea){
        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.parent = {};
    }
    Object.defineProperty(ModelTextArea.prototype, "onMouseReleased", {
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
    if(!ModelTextArea){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.onMouseReleased = {};
    }
    Object.defineProperty(ModelTextArea.prototype, "onFocusLost", {
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
    if(!ModelTextArea){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.onFocusLost = {};
    }
    Object.defineProperty(ModelTextArea.prototype, "emptyText", {
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
    if(!ModelTextArea){
        /**
         * Generated property jsDoc.
         * @property emptyText
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.emptyText = '';
    }
    Object.defineProperty(ModelTextArea.prototype, "onMousePressed", {
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
    if(!ModelTextArea){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.onMousePressed = {};
    }
    Object.defineProperty(ModelTextArea.prototype, "foreground", {
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
    if(!ModelTextArea){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.foreground = {};
    }
    Object.defineProperty(ModelTextArea.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!ModelTextArea){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.error = '';
    }
    Object.defineProperty(ModelTextArea.prototype, "enabled", {
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
    if(!ModelTextArea){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.enabled = true;
    }
    Object.defineProperty(ModelTextArea.prototype, "onComponentMoved", {
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
    if(!ModelTextArea){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.onComponentMoved = {};
    }
    Object.defineProperty(ModelTextArea.prototype, "onSelect", {
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
    if(!ModelTextArea){
        /**
         * Component's selection event handler function.
         * @property onSelect
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.onSelect = {};
    }
    Object.defineProperty(ModelTextArea.prototype, "componentPopupMenu", {
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
    if(!ModelTextArea){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(ModelTextArea.prototype, "top", {
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
    if(!ModelTextArea){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.top = 0;
    }
    Object.defineProperty(ModelTextArea.prototype, "onRender", {
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
    if(!ModelTextArea){
        /**
         * Component's rendering event handler function.
         * @property onRender
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.onRender = {};
    }
    Object.defineProperty(ModelTextArea.prototype, "onComponentResized", {
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
    if(!ModelTextArea){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.onComponentResized = {};
    }
    Object.defineProperty(ModelTextArea.prototype, "model", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.model;
            return P.boxAsJs(value);
        }
    });
    if(!ModelTextArea){
        /**
         * Model of the component. It will be used for data binding.
         * @property model
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.model = {};
    }
    Object.defineProperty(ModelTextArea.prototype, "text", {
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
    if(!ModelTextArea){
        /**
         * Generated property jsDoc.
         * @property text
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.text = '';
    }
    Object.defineProperty(ModelTextArea.prototype, "onMouseEntered", {
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
    if(!ModelTextArea){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.onMouseEntered = {};
    }
    Object.defineProperty(ModelTextArea.prototype, "value", {
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
    if(!ModelTextArea){
        /**
         * Generated property jsDoc.
         * @property value
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.value = {};
    }
    Object.defineProperty(ModelTextArea.prototype, "toolTipText", {
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
    if(!ModelTextArea){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.toolTipText = '';
    }
    Object.defineProperty(ModelTextArea.prototype, "height", {
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
    if(!ModelTextArea){
        /**
         * Height of the component.
         * @property height
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.height = 0;
    }
    Object.defineProperty(ModelTextArea.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!ModelTextArea){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.element = {};
    }
    Object.defineProperty(ModelTextArea.prototype, "onComponentShown", {
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
    if(!ModelTextArea){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.onComponentShown = {};
    }
    Object.defineProperty(ModelTextArea.prototype, "onMouseMoved", {
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
    if(!ModelTextArea){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.onMouseMoved = {};
    }
    Object.defineProperty(ModelTextArea.prototype, "opaque", {
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
    if(!ModelTextArea){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.opaque = true;
    }
    Object.defineProperty(ModelTextArea.prototype, "visible", {
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
    if(!ModelTextArea){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.visible = true;
    }
    Object.defineProperty(ModelTextArea.prototype, "onComponentHidden", {
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
    if(!ModelTextArea){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.onComponentHidden = {};
    }
    Object.defineProperty(ModelTextArea.prototype, "editable", {
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
    if(!ModelTextArea){
        /**
         * Determines if component is editable.
         * @property editable
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.editable = true;
    }
    Object.defineProperty(ModelTextArea.prototype, "nextFocusableComponent", {
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
    if(!ModelTextArea){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(ModelTextArea.prototype, "onActionPerformed", {
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
    if(!ModelTextArea){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.onActionPerformed = {};
    }
    Object.defineProperty(ModelTextArea.prototype, "onKeyReleased", {
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
    if(!ModelTextArea){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.onKeyReleased = {};
    }
    Object.defineProperty(ModelTextArea.prototype, "focusable", {
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
    if(!ModelTextArea){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.focusable = true;
    }
    Object.defineProperty(ModelTextArea.prototype, "onKeyTyped", {
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
    if(!ModelTextArea){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.onKeyTyped = {};
    }
    Object.defineProperty(ModelTextArea.prototype, "onMouseWheelMoved", {
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
    if(!ModelTextArea){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(ModelTextArea.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!ModelTextArea){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.component = {};
    }
    Object.defineProperty(ModelTextArea.prototype, "field", {
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
    if(!ModelTextArea){
        /**
         * Model entity's field.
         * @property field
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.field = {};
    }
    Object.defineProperty(ModelTextArea.prototype, "onFocusGained", {
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
    if(!ModelTextArea){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.onFocusGained = {};
    }
    Object.defineProperty(ModelTextArea.prototype, "left", {
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
    if(!ModelTextArea){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.left = 0;
    }
    Object.defineProperty(ModelTextArea.prototype, "background", {
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
    if(!ModelTextArea){
        /**
         * The background color of this component.
         * @property background
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.background = {};
    }
    Object.defineProperty(ModelTextArea.prototype, "onMouseClicked", {
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
    if(!ModelTextArea){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.onMouseClicked = {};
    }
    Object.defineProperty(ModelTextArea.prototype, "onMouseExited", {
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
    if(!ModelTextArea){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.onMouseExited = {};
    }
    Object.defineProperty(ModelTextArea.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!ModelTextArea){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.name = '';
    }
    Object.defineProperty(ModelTextArea.prototype, "width", {
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
    if(!ModelTextArea){
        /**
         * Width of the component.
         * @property width
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.width = 0;
    }
    Object.defineProperty(ModelTextArea.prototype, "font", {
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
    if(!ModelTextArea){
        /**
         * The font of this component.
         * @property font
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.font = {};
    }
    Object.defineProperty(ModelTextArea.prototype, "onKeyPressed", {
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
    if(!ModelTextArea){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.onKeyPressed = {};
    }
    Object.defineProperty(ModelTextArea.prototype, "redraw", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.redraw();
            return P.boxAsJs(value);
        }
    });
    if(!ModelTextArea){
        /**
         * Redraw the component.
         * @method redraw
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.redraw = function(){};
    }
    Object.defineProperty(ModelTextArea.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!ModelTextArea){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf ModelTextArea
         */
        P.ModelTextArea.prototype.focus = function(){};
    }
})();