(function() {
    var javaClass = Java.type("com.eas.client.forms.components.HtmlArea");
    javaClass.setPublisher(function(aDelegate) {
        return new P.HtmlArea(null, aDelegate);
    });
    
    /**
     * HTML area component.
     * @param text the initial text for the HTML area (optional)
     * @constructor HtmlArea HtmlArea
     */
    P.HtmlArea = function (text) {
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
        if(P.HtmlArea.superclass)
            P.HtmlArea.superclass.constructor.apply(this, arguments);
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
        if(!P.HtmlArea){
            /**
             * The mouse <code>Cursor</code> over this component.
             * @property cursor
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.cursor = {};
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
        if(!P.HtmlArea){
            /**
             * Mouse dragged event handler function.
             * @property onMouseDragged
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.onMouseDragged = {};
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
        if(!P.HtmlArea){
            /**
             * Mouse released event handler function.
             * @property onMouseReleased
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.onMouseReleased = {};
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
        if(!P.HtmlArea){
            /**
             * Keyboard focus lost by the component event handler function.
             * @property onFocusLost
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.onFocusLost = {};
        }
        Object.defineProperty(this, "onValueChange", {
            get: function() {
                var value = delegate.onValueChange;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onValueChange = P.boxAsJava(aValue);
            }
        });
        if(!P.HtmlArea){
            /**
             * Value change handler.
             * @property onValueChange
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.onValueChange = {};
        }
        Object.defineProperty(this, "emptyText", {
            get: function() {
                var value = delegate.emptyText;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.emptyText = P.boxAsJava(aValue);
            }
        });
        if(!P.HtmlArea){
            /**
             * Generated property jsDoc.
             * @property emptyText
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.emptyText = '';
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
        if(!P.HtmlArea){
            /**
             * Mouse pressed event handler function.
             * @property onMousePressed
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.onMousePressed = {};
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
        if(!P.HtmlArea){
            /**
             * The foreground color of this component.
             * @property foreground
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.foreground = {};
        }
        Object.defineProperty(this, "error", {
            get: function() {
                var value = delegate.error;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.error = P.boxAsJava(aValue);
            }
        });
        if(!P.HtmlArea){
            /**
             * An error message of this component.
             * Validation procedure may set this property and subsequent focus lost event will clear it.
             * @property error
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.error = '';
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
        if(!P.HtmlArea){
            /**
             * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
             * @property enabled
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.enabled = true;
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
        if(!P.HtmlArea){
            /**
             * Component moved event handler function.
             * @property onComponentMoved
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.onComponentMoved = {};
        }
        Object.defineProperty(this, "value", {
            get: function() {
                var value = delegate.jsValue;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.jsValue = P.boxAsJava(aValue);
            }
        });
        if(!P.HtmlArea){
            /**
             * Widget's value.
             * @property jsValue
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.value = {};
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
        if(!P.HtmlArea){
            /**
             * <code>PopupMenu</code> that assigned for this component.
             * @property componentPopupMenu
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.componentPopupMenu = {};
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
        if(!P.HtmlArea){
            /**
             * Vertical coordinate of the component.
             * @property top
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.top = 0;
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
        if(!P.HtmlArea){
            /**
             * Component resized event handler function.
             * @property onComponentResized
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.onComponentResized = {};
        }
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parentWidget;
                return P.boxAsJs(value);
            }
        });
        if(!P.HtmlArea){
            /**
             * Parent container of this widget.
             * @property parentWidget
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.parent = {};
        }
        Object.defineProperty(this, "text", {
            get: function() {
                var value = delegate.text;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.text = P.boxAsJava(aValue);
            }
        });
        if(!P.HtmlArea){
            /**
             * Text of the component.
             * @property text
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.text = '';
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
        if(!P.HtmlArea){
            /**
             * Mouse entered over the component event handler function.
             * @property onMouseEntered
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.onMouseEntered = {};
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
        if(!P.HtmlArea){
            /**
             * Generated property jsDoc.
             * @property value
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.value = {};
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
        if(!P.HtmlArea){
            /**
             * The tooltip string that has been set with.
             * @property toolTipText
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.toolTipText = '';
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
        if(!P.HtmlArea){
            /**
             * Height of the component.
             * @property height
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.height = 0;
        }
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });
        if(!P.HtmlArea){
            /**
             * Native API. Returns low level html element. Applicable only in HTML5 client.
             * @property element
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.element = {};
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
        if(!P.HtmlArea){
            /**
             * Component shown event handler function.
             * @property onComponentShown
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.onComponentShown = {};
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
        if(!P.HtmlArea){
            /**
             * Mouse moved event handler function.
             * @property onMouseMoved
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.onMouseMoved = {};
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
        if(!P.HtmlArea){
            /**
             * True if this component is completely opaque.
             * @property opaque
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.opaque = true;
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
        if(!P.HtmlArea){
            /**
             * Determines whether this component should be visible when its parent is visible.
             * @property visible
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.visible = true;
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
        if(!P.HtmlArea){
            /**
             * Component hidden event handler function.
             * @property onComponentHidden
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.onComponentHidden = {};
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
        if(!P.HtmlArea){
            /**
             * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
             * @property nextFocusableComponent
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.nextFocusableComponent = {};
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
        if(!P.HtmlArea){
            /**
             * Key released event handler function.
             * @property onKeyReleased
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.onKeyReleased = {};
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
        if(!P.HtmlArea){
            /**
             * Main action performed event handler function.
             * @property onActionPerformed
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.onActionPerformed = {};
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
        if(!P.HtmlArea){
            /**
             * Determines whether this component may be focused.
             * @property focusable
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.focusable = true;
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
        if(!P.HtmlArea){
            /**
             * Key typed event handler function.
             * @property onKeyTyped
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.onKeyTyped = {};
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
        if(!P.HtmlArea){
            /**
             * Mouse wheel moved event handler function.
             * @property onMouseWheelMoved
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.onMouseWheelMoved = {};
        }
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });
        if(!P.HtmlArea){
            /**
             * Native API. Returns low level swing component. Applicable only in J2SE swing client.
             * @property component
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.component = {};
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
        if(!P.HtmlArea){
            /**
             * Keyboard focus gained by the component event.
             * @property onFocusGained
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.onFocusGained = {};
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
        if(!P.HtmlArea){
            /**
             * Horizontal coordinate of the component.
             * @property left
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.left = 0;
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
        if(!P.HtmlArea){
            /**
             * The background color of this component.
             * @property background
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.background = {};
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
        if(!P.HtmlArea){
            /**
             * Mouse clicked event handler function.
             * @property onMouseClicked
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.onMouseClicked = {};
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
        if(!P.HtmlArea){
            /**
             * Mouse exited over the component event handler function.
             * @property onMouseExited
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.onMouseExited = {};
        }
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.HtmlArea){
            /**
             * Gets name of this component.
             * @property name
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.name = '';
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
        if(!P.HtmlArea){
            /**
             * Width of the component.
             * @property width
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.width = 0;
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
        if(!P.HtmlArea){
            /**
             * The font of this component.
             * @property font
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.font = {};
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
        if(!P.HtmlArea){
            /**
             * Key pressed event handler function.
             * @property onKeyPressed
             * @memberOf HtmlArea
             */
            P.HtmlArea.prototype.onKeyPressed = {};
        }
    };
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf HtmlArea
         */
        P.HtmlArea.prototype.focus = function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        };

})();