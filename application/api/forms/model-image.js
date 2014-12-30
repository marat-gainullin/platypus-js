(function() {
    var javaClass = Java.type("com.eas.client.forms.api.components.model.ModelImage");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ModelImage(aDelegate);
    });
    
    /**
     * Experimental. A model component that shows an image.
     * Unsupported in HTML5 client.
     * @constructor ModelImage ModelImage
     */
    P.ModelImage = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.ModelImage.superclass)
            P.ModelImage.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "cursor", {
            get: function() {
                var value = delegate.cursor;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.cursor = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelImage){
            /**
             * The mouse <code>Cursor</code> over this component.
             * @property cursor
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.cursor = {};
        }
        Object.defineProperty(this, "onMouseDragged", {
            get: function() {
                var value = delegate.onMouseDragged;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseDragged = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelImage){
            /**
             * Mouse dragged event handler function.
             * @property onMouseDragged
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.onMouseDragged = {};
        }
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parent;
                return P.boxAsJs(value);
            }
        });
        if(!P.ModelImage){
            /**
             * Gets the parent of this component.
             * @property parent
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.parent = {};
        }
        Object.defineProperty(this, "onMouseReleased", {
            get: function() {
                var value = delegate.onMouseReleased;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseReleased = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelImage){
            /**
             * Mouse released event handler function.
             * @property onMouseReleased
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.onMouseReleased = {};
        }
        Object.defineProperty(this, "onFocusLost", {
            get: function() {
                var value = delegate.onFocusLost;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onFocusLost = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelImage){
            /**
             * Keyboard focus lost by the component event handler function.
             * @property onFocusLost
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.onFocusLost = {};
        }
        Object.defineProperty(this, "onMousePressed", {
            get: function() {
                var value = delegate.onMousePressed;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMousePressed = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelImage){
            /**
             * Mouse pressed event handler function.
             * @property onMousePressed
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.onMousePressed = {};
        }
        Object.defineProperty(this, "foreground", {
            get: function() {
                var value = delegate.foreground;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.foreground = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelImage){
            /**
             * The foreground color of this component.
             * @property foreground
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.foreground = {};
        }
        Object.defineProperty(this, "error", {
            get: function() {
                var value = delegate.error;
                return P.boxAsJs(value);
            }
        });
        if(!P.ModelImage){
            /**
             * An error message of this component.
             * Validation procedure may set this property and subsequent focus lost event will clear it.
             * @property error
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.error = '';
        }
        Object.defineProperty(this, "enabled", {
            get: function() {
                var value = delegate.enabled;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.enabled = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelImage){
            /**
             * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
             * @property enabled
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.enabled = true;
        }
        Object.defineProperty(this, "onComponentMoved", {
            get: function() {
                var value = delegate.onComponentMoved;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onComponentMoved = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelImage){
            /**
             * Component moved event handler function.
             * @property onComponentMoved
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.onComponentMoved = {};
        }
        Object.defineProperty(this, "onSelect", {
            get: function() {
                var value = delegate.onSelect;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onSelect = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelImage){
            /**
             * Component's selection event handler function.
             * @property onSelect
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.onSelect = {};
        }
        Object.defineProperty(this, "componentPopupMenu", {
            get: function() {
                var value = delegate.componentPopupMenu;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.componentPopupMenu = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelImage){
            /**
             * <code>PopupMenu</code> that assigned for this component.
             * @property componentPopupMenu
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.componentPopupMenu = {};
        }
        Object.defineProperty(this, "top", {
            get: function() {
                var value = delegate.top;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.top = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelImage){
            /**
             * Vertical coordinate of the component.
             * @property top
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.top = 0;
        }
        Object.defineProperty(this, "onRender", {
            get: function() {
                var value = delegate.onRender;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onRender = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelImage){
            /**
             * Component's rendering event handler function.
             * @property onRender
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.onRender = {};
        }
        Object.defineProperty(this, "plain", {
            get: function() {
                var value = delegate.plain;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.plain = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelImage){
            /**
             * Determines if image is displayed with real dimensions and not scaled.
             * If false, the image is fitted and can be scaled with the mouse wheel.
             * @property plain
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.plain = true;
        }
        Object.defineProperty(this, "onComponentResized", {
            get: function() {
                var value = delegate.onComponentResized;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onComponentResized = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelImage){
            /**
             * Component resized event handler function.
             * @property onComponentResized
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.onComponentResized = {};
        }
        Object.defineProperty(this, "model", {
            get: function() {
                var value = delegate.model;
                return P.boxAsJs(value);
            }
        });
        if(!P.ModelImage){
            /**
             * Model of the component. It will be used for data binding.
             * @property model
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.model = {};
        }
        Object.defineProperty(this, "onMouseEntered", {
            get: function() {
                var value = delegate.onMouseEntered;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseEntered = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelImage){
            /**
             * Mouse entered over the component event handler function.
             * @property onMouseEntered
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.onMouseEntered = {};
        }
        Object.defineProperty(this, "value", {
            get: function() {
                var value = delegate.value;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.value = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelImage){
            /**
             * Component's value.
             * @property value
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.value = {};
        }
        Object.defineProperty(this, "toolTipText", {
            get: function() {
                var value = delegate.toolTipText;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.toolTipText = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelImage){
            /**
             * The tooltip string that has been set with.
             * @property toolTipText
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.toolTipText = '';
        }
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });
        if(!P.ModelImage){
            /**
             * Native API. Returns low level html element. Applicable only in HTML5 client.
             * @property element
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.element = {};
        }
        Object.defineProperty(this, "height", {
            get: function() {
                var value = delegate.height;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.height = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelImage){
            /**
             * Height of the component.
             * @property height
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.height = 0;
        }
        Object.defineProperty(this, "onComponentShown", {
            get: function() {
                var value = delegate.onComponentShown;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onComponentShown = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelImage){
            /**
             * Component shown event handler function.
             * @property onComponentShown
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.onComponentShown = {};
        }
        Object.defineProperty(this, "onMouseMoved", {
            get: function() {
                var value = delegate.onMouseMoved;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseMoved = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelImage){
            /**
             * Mouse moved event handler function.
             * @property onMouseMoved
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.onMouseMoved = {};
        }
        Object.defineProperty(this, "opaque", {
            get: function() {
                var value = delegate.opaque;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.opaque = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelImage){
            /**
             * True if this component is completely opaque.
             * @property opaque
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.opaque = true;
        }
        Object.defineProperty(this, "visible", {
            get: function() {
                var value = delegate.visible;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.visible = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelImage){
            /**
             * Determines whether this component should be visible when its parent is visible.
             * @property visible
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.visible = true;
        }
        Object.defineProperty(this, "onComponentHidden", {
            get: function() {
                var value = delegate.onComponentHidden;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onComponentHidden = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelImage){
            /**
             * Component hidden event handler function.
             * @property onComponentHidden
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.onComponentHidden = {};
        }
        Object.defineProperty(this, "editable", {
            get: function() {
                var value = delegate.editable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.editable = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelImage){
            /**
             * Determines if component is editable.
             * @property editable
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.editable = true;
        }
        Object.defineProperty(this, "nextFocusableComponent", {
            get: function() {
                var value = delegate.nextFocusableComponent;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.nextFocusableComponent = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelImage){
            /**
             * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
             * @property nextFocusableComponent
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.nextFocusableComponent = {};
        }
        Object.defineProperty(this, "onActionPerformed", {
            get: function() {
                var value = delegate.onActionPerformed;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onActionPerformed = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelImage){
            /**
             * Main action performed event handler function.
             * @property onActionPerformed
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.onActionPerformed = {};
        }
        Object.defineProperty(this, "onKeyReleased", {
            get: function() {
                var value = delegate.onKeyReleased;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onKeyReleased = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelImage){
            /**
             * Key released event handler function.
             * @property onKeyReleased
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.onKeyReleased = {};
        }
        Object.defineProperty(this, "focusable", {
            get: function() {
                var value = delegate.focusable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.focusable = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelImage){
            /**
             * Determines whether this component may be focused.
             * @property focusable
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.focusable = true;
        }
        Object.defineProperty(this, "onKeyTyped", {
            get: function() {
                var value = delegate.onKeyTyped;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onKeyTyped = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelImage){
            /**
             * Key typed event handler function.
             * @property onKeyTyped
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.onKeyTyped = {};
        }
        Object.defineProperty(this, "onMouseWheelMoved", {
            get: function() {
                var value = delegate.onMouseWheelMoved;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseWheelMoved = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelImage){
            /**
             * Mouse wheel moved event handler function.
             * @property onMouseWheelMoved
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.onMouseWheelMoved = {};
        }
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });
        if(!P.ModelImage){
            /**
             * Native API. Returns low level swing component. Applicable only in J2SE swing client.
             * @property component
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.component = {};
        }
        Object.defineProperty(this, "field", {
            get: function() {
                var value = delegate.field;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.field = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelImage){
            /**
             * Model entity's field.
             * @property field
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.field = {};
        }
        Object.defineProperty(this, "onFocusGained", {
            get: function() {
                var value = delegate.onFocusGained;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onFocusGained = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelImage){
            /**
             * Keyboard focus gained by the component event.
             * @property onFocusGained
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.onFocusGained = {};
        }
        Object.defineProperty(this, "left", {
            get: function() {
                var value = delegate.left;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.left = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelImage){
            /**
             * Horizontal coordinate of the component.
             * @property left
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.left = 0;
        }
        Object.defineProperty(this, "background", {
            get: function() {
                var value = delegate.background;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.background = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelImage){
            /**
             * The background color of this component.
             * @property background
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.background = {};
        }
        Object.defineProperty(this, "onMouseClicked", {
            get: function() {
                var value = delegate.onMouseClicked;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseClicked = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelImage){
            /**
             * Mouse clicked event handler function.
             * @property onMouseClicked
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.onMouseClicked = {};
        }
        Object.defineProperty(this, "onMouseExited", {
            get: function() {
                var value = delegate.onMouseExited;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseExited = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelImage){
            /**
             * Mouse exited over the component event handler function.
             * @property onMouseExited
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.onMouseExited = {};
        }
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.ModelImage){
            /**
             * Gets name of this component.
             * @property name
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.name = '';
        }
        Object.defineProperty(this, "width", {
            get: function() {
                var value = delegate.width;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.width = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelImage){
            /**
             * Width of the component.
             * @property width
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.width = 0;
        }
        Object.defineProperty(this, "font", {
            get: function() {
                var value = delegate.font;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.font = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelImage){
            /**
             * The font of this component.
             * @property font
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.font = {};
        }
        Object.defineProperty(this, "onKeyPressed", {
            get: function() {
                var value = delegate.onKeyPressed;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onKeyPressed = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelImage){
            /**
             * Key pressed event handler function.
             * @property onKeyPressed
             * @memberOf ModelImage
             */
            P.ModelImage.prototype.onKeyPressed = {};
        }
    };
        /**
         * Redraw the component.
         * @method redraw
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.redraw = function() {
            var delegate = this.unwrap();
            var value = delegate.redraw();
            return P.boxAsJs(value);
        };

        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf ModelImage
         */
        P.ModelImage.prototype.focus = function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        };

})();