(function() {
    var className = "com.eas.client.forms.containers.CardPane";
    var javaClass = Java.type(className);
    var space = this['-platypus-scripts-space'];
    space.putPublisher(className, function(aDelegate) {
        return new P.CardPane(null, null, aDelegate);
    });
    
    /**
     * A container with Card Layout. It treats each component in the container as a card. Only one card is visible at a time, and the container acts as a stack of cards.
     * @param hgap the horizontal gap (optional).
     * @param vgap the vertical gap (optional).
     * @constructor CardPane CardPane
     */
    P.CardPane = function (hgap, vgap) {
        var maxArgs = 2;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : arguments.length === 2 ? new javaClass(P.boxAsJava(hgap), P.boxAsJava(vgap))
            : arguments.length === 1 ? new javaClass(P.boxAsJava(hgap))
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            configurable: true,
            value: function() {
                return delegate;
            }
        });
        if(P.CardPane.superclass)
            P.CardPane.superclass.constructor.apply(this, arguments);
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
        if(!P.CardPane){
            /**
             * The mouse <code>Cursor</code> over this component.
             * @property cursor
             * @memberOf CardPane
             */
            P.CardPane.prototype.cursor = {};
        }
        Object.defineProperty(this, "onMouseDragged", {
            get: function() {
                var value = delegate.onMouseDragged;
                return value;
            },
            set: function(aValue) {
                delegate.onMouseDragged = aValue;
            }
        });
        if(!P.CardPane){
            /**
             * Mouse dragged event handler function.
             * @property onMouseDragged
             * @memberOf CardPane
             */
            P.CardPane.prototype.onMouseDragged = {};
        }
        Object.defineProperty(this, "onMouseReleased", {
            get: function() {
                var value = delegate.onMouseReleased;
                return value;
            },
            set: function(aValue) {
                delegate.onMouseReleased = aValue;
            }
        });
        if(!P.CardPane){
            /**
             * Mouse released event handler function.
             * @property onMouseReleased
             * @memberOf CardPane
             */
            P.CardPane.prototype.onMouseReleased = {};
        }
        Object.defineProperty(this, "onFocusLost", {
            get: function() {
                var value = delegate.onFocusLost;
                return value;
            },
            set: function(aValue) {
                delegate.onFocusLost = aValue;
            }
        });
        if(!P.CardPane){
            /**
             * Keyboard focus lost by the component event handler function.
             * @property onFocusLost
             * @memberOf CardPane
             */
            P.CardPane.prototype.onFocusLost = {};
        }
        Object.defineProperty(this, "onMousePressed", {
            get: function() {
                var value = delegate.onMousePressed;
                return value;
            },
            set: function(aValue) {
                delegate.onMousePressed = aValue;
            }
        });
        if(!P.CardPane){
            /**
             * Mouse pressed event handler function.
             * @property onMousePressed
             * @memberOf CardPane
             */
            P.CardPane.prototype.onMousePressed = {};
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
        if(!P.CardPane){
            /**
             * The foreground color of this component.
             * @property foreground
             * @memberOf CardPane
             */
            P.CardPane.prototype.foreground = {};
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
        if(!P.CardPane){
            /**
             * An error message of this component.
             * Validation procedure may set this property and subsequent focus lost event will clear it.
             * @property error
             * @memberOf CardPane
             */
            P.CardPane.prototype.error = '';
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
        if(!P.CardPane){
            /**
             * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
             * @property enabled
             * @memberOf CardPane
             */
            P.CardPane.prototype.enabled = true;
        }
        Object.defineProperty(this, "onComponentMoved", {
            get: function() {
                var value = delegate.onComponentMoved;
                return value;
            },
            set: function(aValue) {
                delegate.onComponentMoved = aValue;
            }
        });
        if(!P.CardPane){
            /**
             * Component moved event handler function.
             * @property onComponentMoved
             * @memberOf CardPane
             */
            P.CardPane.prototype.onComponentMoved = {};
        }
        Object.defineProperty(this, "onComponentAdded", {
            get: function() {
                var value = delegate.onComponentAdded;
                return value;
            },
            set: function(aValue) {
                delegate.onComponentAdded = aValue;
            }
        });
        if(!P.CardPane){
            /**
             * Component added event hanler function.
             * @property onComponentAdded
             * @memberOf CardPane
             */
            P.CardPane.prototype.onComponentAdded = {};
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
        if(!P.CardPane){
            /**
             * <code>PopupMenu</code> that assigned for this component.
             * @property componentPopupMenu
             * @memberOf CardPane
             */
            P.CardPane.prototype.componentPopupMenu = {};
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
        if(!P.CardPane){
            /**
             * Vertical coordinate of the component.
             * @property top
             * @memberOf CardPane
             */
            P.CardPane.prototype.top = 0;
        }
        Object.defineProperty(this, "onComponentResized", {
            get: function() {
                var value = delegate.onComponentResized;
                return value;
            },
            set: function(aValue) {
                delegate.onComponentResized = aValue;
            }
        });
        if(!P.CardPane){
            /**
             * Component resized event handler function.
             * @property onComponentResized
             * @memberOf CardPane
             */
            P.CardPane.prototype.onComponentResized = {};
        }
        Object.defineProperty(this, "vgap", {
            get: function() {
                var value = delegate.vgap;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.vgap = P.boxAsJava(aValue);
            }
        });
        if(!P.CardPane){
            /**
             * Vertical gap between card and container's edge.
             * @property vgap
             * @memberOf CardPane
             */
            P.CardPane.prototype.vgap = 0;
        }
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parentWidget;
                return P.boxAsJs(value);
            }
        });
        if(!P.CardPane){
            /**
             * Parent container of this widget.
             * @property parentWidget
             * @memberOf CardPane
             */
            P.CardPane.prototype.parent = {};
        }
        Object.defineProperty(this, "onMouseEntered", {
            get: function() {
                var value = delegate.onMouseEntered;
                return value;
            },
            set: function(aValue) {
                delegate.onMouseEntered = aValue;
            }
        });
        if(!P.CardPane){
            /**
             * Mouse entered over the component event handler function.
             * @property onMouseEntered
             * @memberOf CardPane
             */
            P.CardPane.prototype.onMouseEntered = {};
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
        if(!P.CardPane){
            /**
             * The tooltip string that has been set with.
             * @property toolTipText
             * @memberOf CardPane
             */
            P.CardPane.prototype.toolTipText = '';
        }
        Object.defineProperty(this, "hgap", {
            get: function() {
                var value = delegate.hgap;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.hgap = P.boxAsJava(aValue);
            }
        });
        if(!P.CardPane){
            /**
             * Horizontal gap between card and container's edge.
             * @property hgap
             * @memberOf CardPane
             */
            P.CardPane.prototype.hgap = 0;
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
        if(!P.CardPane){
            /**
             * Height of the component.
             * @property height
             * @memberOf CardPane
             */
            P.CardPane.prototype.height = 0;
        }
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });
        if(!P.CardPane){
            /**
             * Native API. Returns low level html element. Applicable only in HTML5 client.
             * @property element
             * @memberOf CardPane
             */
            P.CardPane.prototype.element = {};
        }
        Object.defineProperty(this, "onComponentShown", {
            get: function() {
                var value = delegate.onComponentShown;
                return value;
            },
            set: function(aValue) {
                delegate.onComponentShown = aValue;
            }
        });
        if(!P.CardPane){
            /**
             * Component shown event handler function.
             * @property onComponentShown
             * @memberOf CardPane
             */
            P.CardPane.prototype.onComponentShown = {};
        }
        Object.defineProperty(this, "onMouseMoved", {
            get: function() {
                var value = delegate.onMouseMoved;
                return value;
            },
            set: function(aValue) {
                delegate.onMouseMoved = aValue;
            }
        });
        if(!P.CardPane){
            /**
             * Mouse moved event handler function.
             * @property onMouseMoved
             * @memberOf CardPane
             */
            P.CardPane.prototype.onMouseMoved = {};
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
        if(!P.CardPane){
            /**
             * True if this component is completely opaque.
             * @property opaque
             * @memberOf CardPane
             */
            P.CardPane.prototype.opaque = true;
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
        if(!P.CardPane){
            /**
             * Determines whether this component should be visible when its parent is visible.
             * @property visible
             * @memberOf CardPane
             */
            P.CardPane.prototype.visible = true;
        }
        Object.defineProperty(this, "onComponentHidden", {
            get: function() {
                var value = delegate.onComponentHidden;
                return value;
            },
            set: function(aValue) {
                delegate.onComponentHidden = aValue;
            }
        });
        if(!P.CardPane){
            /**
             * Component hidden event handler function.
             * @property onComponentHidden
             * @memberOf CardPane
             */
            P.CardPane.prototype.onComponentHidden = {};
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
        if(!P.CardPane){
            /**
             * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
             * @property nextFocusableComponent
             * @memberOf CardPane
             */
            P.CardPane.prototype.nextFocusableComponent = {};
        }
        Object.defineProperty(this, "count", {
            get: function() {
                var value = delegate.count;
                return P.boxAsJs(value);
            }
        });
        if(!P.CardPane){
            /**
             * Gets the number of components in this panel.
             * @property count
             * @memberOf CardPane
             */
            P.CardPane.prototype.count = 0;
        }
        Object.defineProperty(this, "onKeyReleased", {
            get: function() {
                var value = delegate.onKeyReleased;
                return value;
            },
            set: function(aValue) {
                delegate.onKeyReleased = aValue;
            }
        });
        if(!P.CardPane){
            /**
             * Key released event handler function.
             * @property onKeyReleased
             * @memberOf CardPane
             */
            P.CardPane.prototype.onKeyReleased = {};
        }
        Object.defineProperty(this, "onActionPerformed", {
            get: function() {
                var value = delegate.onActionPerformed;
                return value;
            },
            set: function(aValue) {
                delegate.onActionPerformed = aValue;
            }
        });
        if(!P.CardPane){
            /**
             * Main action performed event handler function.
             * @property onActionPerformed
             * @memberOf CardPane
             */
            P.CardPane.prototype.onActionPerformed = {};
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
        if(!P.CardPane){
            /**
             * Determines whether this component may be focused.
             * @property focusable
             * @memberOf CardPane
             */
            P.CardPane.prototype.focusable = true;
        }
        Object.defineProperty(this, "onKeyTyped", {
            get: function() {
                var value = delegate.onKeyTyped;
                return value;
            },
            set: function(aValue) {
                delegate.onKeyTyped = aValue;
            }
        });
        if(!P.CardPane){
            /**
             * Key typed event handler function.
             * @property onKeyTyped
             * @memberOf CardPane
             */
            P.CardPane.prototype.onKeyTyped = {};
        }
        Object.defineProperty(this, "onMouseWheelMoved", {
            get: function() {
                var value = delegate.onMouseWheelMoved;
                return value;
            },
            set: function(aValue) {
                delegate.onMouseWheelMoved = aValue;
            }
        });
        if(!P.CardPane){
            /**
             * Mouse wheel moved event handler function.
             * @property onMouseWheelMoved
             * @memberOf CardPane
             */
            P.CardPane.prototype.onMouseWheelMoved = {};
        }
        Object.defineProperty(this, "onComponentRemoved", {
            get: function() {
                var value = delegate.onComponentRemoved;
                return value;
            },
            set: function(aValue) {
                delegate.onComponentRemoved = aValue;
            }
        });
        if(!P.CardPane){
            /**
             * Component removed event handler function.
             * @property onComponentRemoved
             * @memberOf CardPane
             */
            P.CardPane.prototype.onComponentRemoved = {};
        }
        Object.defineProperty(this, "onItemSelected", {
            get: function() {
                var value = delegate.onItemSelected;
                return value;
            },
            set: function(aValue) {
                delegate.onItemSelected = aValue;
            }
        });
        if(!P.CardPane){
            /**
             * Event that is fired when one of the components is selected in this card pane.
             * @property onItemSelected
             * @memberOf CardPane
             */
            P.CardPane.prototype.onItemSelected = {};
        }
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });
        if(!P.CardPane){
            /**
             * Native API. Returns low level swing component. Applicable only in J2SE swing client.
             * @property component
             * @memberOf CardPane
             */
            P.CardPane.prototype.component = {};
        }
        Object.defineProperty(this, "onFocusGained", {
            get: function() {
                var value = delegate.onFocusGained;
                return value;
            },
            set: function(aValue) {
                delegate.onFocusGained = aValue;
            }
        });
        if(!P.CardPane){
            /**
             * Keyboard focus gained by the component event.
             * @property onFocusGained
             * @memberOf CardPane
             */
            P.CardPane.prototype.onFocusGained = {};
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
        if(!P.CardPane){
            /**
             * Horizontal coordinate of the component.
             * @property left
             * @memberOf CardPane
             */
            P.CardPane.prototype.left = 0;
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
        if(!P.CardPane){
            /**
             * The background color of this component.
             * @property background
             * @memberOf CardPane
             */
            P.CardPane.prototype.background = {};
        }
        Object.defineProperty(this, "onMouseClicked", {
            get: function() {
                var value = delegate.onMouseClicked;
                return value;
            },
            set: function(aValue) {
                delegate.onMouseClicked = aValue;
            }
        });
        if(!P.CardPane){
            /**
             * Mouse clicked event handler function.
             * @property onMouseClicked
             * @memberOf CardPane
             */
            P.CardPane.prototype.onMouseClicked = {};
        }
        Object.defineProperty(this, "onMouseExited", {
            get: function() {
                var value = delegate.onMouseExited;
                return value;
            },
            set: function(aValue) {
                delegate.onMouseExited = aValue;
            }
        });
        if(!P.CardPane){
            /**
             * Mouse exited over the component event handler function.
             * @property onMouseExited
             * @memberOf CardPane
             */
            P.CardPane.prototype.onMouseExited = {};
        }
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.name = P.boxAsJava(aValue);
            }
        });
        if(!P.CardPane){
            /**
             * Gets name of this component.
             * @property name
             * @memberOf CardPane
             */
            P.CardPane.prototype.name = '';
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
        if(!P.CardPane){
            /**
             * Width of the component.
             * @property width
             * @memberOf CardPane
             */
            P.CardPane.prototype.width = 0;
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
        if(!P.CardPane){
            /**
             * The font of this component.
             * @property font
             * @memberOf CardPane
             */
            P.CardPane.prototype.font = {};
        }
        Object.defineProperty(this, "onKeyPressed", {
            get: function() {
                var value = delegate.onKeyPressed;
                return value;
            },
            set: function(aValue) {
                delegate.onKeyPressed = aValue;
            }
        });
        if(!P.CardPane){
            /**
             * Key pressed event handler function.
             * @property onKeyPressed
             * @memberOf CardPane
             */
            P.CardPane.prototype.onKeyPressed = {};
        }
    };
        /**
         * Appends the component to this container with the specified name.
         * @param component the component to add.
         * @param cardName the name of the card.
         * @method add
         * @memberOf CardPane
         */
        P.CardPane.prototype.add = function(component, cardName) {
            var delegate = this.unwrap();
            var value = delegate.add(P.boxAsJava(component), P.boxAsJava(cardName));
            return P.boxAsJs(value);
        };

        /**
         * Removes the specified component from this container.
         * @param component the component to remove
         * @method remove
         * @memberOf CardPane
         */
        P.CardPane.prototype.remove = function(component) {
            var delegate = this.unwrap();
            var value = delegate.remove(P.boxAsJava(component));
            return P.boxAsJs(value);
        };

        /**
         * Removes all the components from this container.
         * @method clear
         * @memberOf CardPane
         */
        P.CardPane.prototype.clear = function() {
            var delegate = this.unwrap();
            var value = delegate.clear();
            return P.boxAsJs(value);
        };

        /**
         * Flips to the component that was added to this layout with the specified name.
         * @param name the card name
         * @method show
         * @memberOf CardPane
         */
        P.CardPane.prototype.show = function(name) {
            var delegate = this.unwrap();
            var value = delegate.show(P.boxAsJava(name));
            return P.boxAsJs(value);
        };

        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf CardPane
         */
        P.CardPane.prototype.focus = function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        };

        /**
         * Gets the container's children components.
         * @method children
         * @memberOf CardPane
         */
        P.CardPane.prototype.children = function() {
            var delegate = this.unwrap();
            var value = delegate.children();
            return P.boxAsJs(value);
        };

        /**
         * Gets child component, associated with the specified card.
         * @param cardName Name of the card.
         * @return the child component.
         * @method child
         * @memberOf CardPane
         */
        P.CardPane.prototype.child = function(cardName) {
            var delegate = this.unwrap();
            var value = delegate.child(P.boxAsJava(cardName));
            return P.boxAsJs(value);
        };

})();