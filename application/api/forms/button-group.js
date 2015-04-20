(function() {
    var javaClass = Java.type("com.eas.client.forms.containers.ButtonGroup");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ButtonGroup(aDelegate);
    });
    
    /**
     * Creates a multiple-exclusion scope for a set of radio or toggle buttons or for a menu item with radio button.
     * Creating a set of buttons with the same <code>ButtonGroup</code> object means that turning "on" one of those buttons turns off all other buttons in the group.
     * @constructor ButtonGroup ButtonGroup
     */
    P.ButtonGroup = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.ButtonGroup.superclass)
            P.ButtonGroup.superclass.constructor.apply(this, arguments);
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
        if(!P.ButtonGroup){
            /**
             * The mouse <code>Cursor</code> over this component.
             * @property cursor
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.cursor = {};
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
        if(!P.ButtonGroup){
            /**
             * Mouse dragged event handler function.
             * @property onMouseDragged
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.onMouseDragged = {};
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
        if(!P.ButtonGroup){
            /**
             * Mouse released event handler function.
             * @property onMouseReleased
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.onMouseReleased = {};
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
        if(!P.ButtonGroup){
            /**
             * Keyboard focus lost by the component event handler function.
             * @property onFocusLost
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.onFocusLost = {};
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
        if(!P.ButtonGroup){
            /**
             * Mouse pressed event handler function.
             * @property onMousePressed
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.onMousePressed = {};
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
        if(!P.ButtonGroup){
            /**
             * The foreground color of this component.
             * @property foreground
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.foreground = {};
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
        if(!P.ButtonGroup){
            /**
             * An error message of this component.
             * Validation procedure may set this property and subsequent focus lost event will clear it.
             * @property error
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.error = '';
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
        if(!P.ButtonGroup){
            /**
             * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
             * @property enabled
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.enabled = true;
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
        if(!P.ButtonGroup){
            /**
             * Component moved event handler function.
             * @property onComponentMoved
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.onComponentMoved = {};
        }
        Object.defineProperty(this, "onComponentAdded", {
            get: function() {
                var value = delegate.onComponentAdded;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onComponentAdded = P.boxAsJava(aValue);
            }
        });
        if(!P.ButtonGroup){
            /**
             * Component added event hanler function.
             * @property onComponentAdded
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.onComponentAdded = {};
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
        if(!P.ButtonGroup){
            /**
             * <code>PopupMenu</code> that assigned for this component.
             * @property componentPopupMenu
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.componentPopupMenu = {};
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
        if(!P.ButtonGroup){
            /**
             * Vertical coordinate of the component.
             * @property top
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.top = 0;
        }
        Object.defineProperty(this, "children", {
            get: function() {
                if (!invalidatable) {
                    var value = delegate.children;
                    invalidatable = P.boxAsJs(value);
                }
                return invalidatable;
            }
        });
        if(!P.ButtonGroup){
            /**
             * Gets the container's children components.
             * @property children
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.children = [];
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
        if(!P.ButtonGroup){
            /**
             * Component resized event handler function.
             * @property onComponentResized
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.onComponentResized = {};
        }
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parentWidget;
                return P.boxAsJs(value);
            }
        });
        if(!P.ButtonGroup){
            /**
             * Parent container of this widget.
             * @property parentWidget
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.parent = {};
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
        if(!P.ButtonGroup){
            /**
             * Mouse entered over the component event handler function.
             * @property onMouseEntered
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.onMouseEntered = {};
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
        if(!P.ButtonGroup){
            /**
             * The tooltip string that has been set with.
             * @property toolTipText
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.toolTipText = '';
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
        if(!P.ButtonGroup){
            /**
             * Height of the component.
             * @property height
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.height = 0;
        }
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });
        if(!P.ButtonGroup){
            /**
             * Native API. Returns low level html element. Applicable only in HTML5 client.
             * @property element
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.element = {};
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
        if(!P.ButtonGroup){
            /**
             * Component shown event handler function.
             * @property onComponentShown
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.onComponentShown = {};
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
        if(!P.ButtonGroup){
            /**
             * Mouse moved event handler function.
             * @property onMouseMoved
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.onMouseMoved = {};
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
        if(!P.ButtonGroup){
            /**
             * True if this component is completely opaque.
             * @property opaque
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.opaque = true;
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
        if(!P.ButtonGroup){
            /**
             * Determines whether this component should be visible when its parent is visible.
             * @property visible
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.visible = true;
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
        if(!P.ButtonGroup){
            /**
             * Component hidden event handler function.
             * @property onComponentHidden
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.onComponentHidden = {};
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
        if(!P.ButtonGroup){
            /**
             * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
             * @property nextFocusableComponent
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.nextFocusableComponent = {};
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
        if(!P.ButtonGroup){
            /**
             * Key released event handler function.
             * @property onKeyReleased
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.onKeyReleased = {};
        }
        Object.defineProperty(this, "count", {
            get: function() {
                var value = delegate.count;
                return P.boxAsJs(value);
            }
        });
        if(!P.ButtonGroup){
            /**
             * Gets the number of components in this panel.
             * @property count
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.count = 0;
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
        if(!P.ButtonGroup){
            /**
             * Main action performed event handler function.
             * @property onActionPerformed
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.onActionPerformed = {};
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
        if(!P.ButtonGroup){
            /**
             * Determines whether this component may be focused.
             * @property focusable
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.focusable = true;
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
        if(!P.ButtonGroup){
            /**
             * Key typed event handler function.
             * @property onKeyTyped
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.onKeyTyped = {};
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
        if(!P.ButtonGroup){
            /**
             * Mouse wheel moved event handler function.
             * @property onMouseWheelMoved
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.onMouseWheelMoved = {};
        }
        Object.defineProperty(this, "onComponentRemoved", {
            get: function() {
                var value = delegate.onComponentRemoved;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onComponentRemoved = P.boxAsJava(aValue);
            }
        });
        if(!P.ButtonGroup){
            /**
             * Component removed event handler function.
             * @property onComponentRemoved
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.onComponentRemoved = {};
        }
        Object.defineProperty(this, "onItemSelected", {
            get: function() {
                var value = delegate.onItemSelected;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onItemSelected = P.boxAsJava(aValue);
            }
        });
        if(!P.ButtonGroup){
            /**
             * Event that is fired when one of the components is selected in this group.
             * @property onItemSelected
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.onItemSelected = {};
        }
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });
        if(!P.ButtonGroup){
            /**
             * Native API. Returns low level swing component. Applicable only in J2SE swing client.
             * @property component
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.component = {};
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
        if(!P.ButtonGroup){
            /**
             * Keyboard focus gained by the component event.
             * @property onFocusGained
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.onFocusGained = {};
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
        if(!P.ButtonGroup){
            /**
             * Horizontal coordinate of the component.
             * @property left
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.left = 0;
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
        if(!P.ButtonGroup){
            /**
             * The background color of this component.
             * @property background
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.background = {};
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
        if(!P.ButtonGroup){
            /**
             * Mouse clicked event handler function.
             * @property onMouseClicked
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.onMouseClicked = {};
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
        if(!P.ButtonGroup){
            /**
             * Mouse exited over the component event handler function.
             * @property onMouseExited
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.onMouseExited = {};
        }
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.ButtonGroup){
            /**
             * Gets name of this component.
             * @property name
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.name = '';
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
        if(!P.ButtonGroup){
            /**
             * Width of the component.
             * @property width
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.width = 0;
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
        if(!P.ButtonGroup){
            /**
             * The font of this component.
             * @property font
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.font = {};
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
        if(!P.ButtonGroup){
            /**
             * Key pressed event handler function.
             * @property onKeyPressed
             * @memberOf ButtonGroup
             */
            P.ButtonGroup.prototype.onKeyPressed = {};
        }
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    };
        /**
         * Appends the specified component to the end of this group.
         * @param component Component to add to the group.
         * @method add
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.add = function(component) {
            var delegate = this.unwrap();
            var value = delegate.add(P.boxAsJava(component));
            return P.boxAsJs(value);
        };

        /**
         * Removes the specified component from the group.
         * @param component Component to remove from the group.
         * @method remove
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.remove = function(component) {
            var delegate = this.unwrap();
            var value = delegate.remove(P.boxAsJava(component));
            return P.boxAsJs(value);
        };

        /**
         * Removes all the components from this container.
         * @method clear
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.clear = function() {
            var delegate = this.unwrap();
            var value = delegate.clear();
            return P.boxAsJs(value);
        };

        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.focus = function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        };

        /**
         * Gets the container's n-th component.
         * @param index the component's index in the container
         * @return the child component
         * @method child
         * @memberOf ButtonGroup
         */
        P.ButtonGroup.prototype.child = function(index) {
            var delegate = this.unwrap();
            var value = delegate.child(P.boxAsJava(index));
            return P.boxAsJs(value);
        };

})();