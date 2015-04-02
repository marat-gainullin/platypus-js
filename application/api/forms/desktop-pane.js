(function() {
    var javaClass = Java.type("com.eas.client.forms.components.DesktopPane");
    javaClass.setPublisher(function(aDelegate) {
        return new P.DesktopPane(aDelegate);
    });
    
    /**
     * Desktop pane panel component.
     * This component can be used for creating a multi-document GUI or a virtual desktop.
     * @constructor DesktopPane DesktopPane
     */
    P.DesktopPane = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.DesktopPane.superclass)
            P.DesktopPane.superclass.constructor.apply(this, arguments);
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
        if(!P.DesktopPane){
            /**
             * The mouse <code>Cursor</code> over this component.
             * @property cursor
             * @memberOf DesktopPane
             */
            P.DesktopPane.prototype.cursor = {};
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
        if(!P.DesktopPane){
            /**
             * Mouse dragged event handler function.
             * @property onMouseDragged
             * @memberOf DesktopPane
             */
            P.DesktopPane.prototype.onMouseDragged = {};
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
        if(!P.DesktopPane){
            /**
             * Mouse released event handler function.
             * @property onMouseReleased
             * @memberOf DesktopPane
             */
            P.DesktopPane.prototype.onMouseReleased = {};
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
        if(!P.DesktopPane){
            /**
             * Keyboard focus lost by the component event handler function.
             * @property onFocusLost
             * @memberOf DesktopPane
             */
            P.DesktopPane.prototype.onFocusLost = {};
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
        if(!P.DesktopPane){
            /**
             * Mouse pressed event handler function.
             * @property onMousePressed
             * @memberOf DesktopPane
             */
            P.DesktopPane.prototype.onMousePressed = {};
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
        if(!P.DesktopPane){
            /**
             * The foreground color of this component.
             * @property foreground
             * @memberOf DesktopPane
             */
            P.DesktopPane.prototype.foreground = {};
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
        if(!P.DesktopPane){
            /**
             * An error message of this component.
             * Validation procedure may set this property and subsequent focus lost event will clear it.
             * @property error
             * @memberOf DesktopPane
             */
            P.DesktopPane.prototype.error = '';
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
        if(!P.DesktopPane){
            /**
             * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
             * @property enabled
             * @memberOf DesktopPane
             */
            P.DesktopPane.prototype.enabled = true;
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
        if(!P.DesktopPane){
            /**
             * Component moved event handler function.
             * @property onComponentMoved
             * @memberOf DesktopPane
             */
            P.DesktopPane.prototype.onComponentMoved = {};
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
        if(!P.DesktopPane){
            /**
             * <code>PopupMenu</code> that assigned for this component.
             * @property componentPopupMenu
             * @memberOf DesktopPane
             */
            P.DesktopPane.prototype.componentPopupMenu = {};
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
        if(!P.DesktopPane){
            /**
             * Vertical coordinate of the component.
             * @property top
             * @memberOf DesktopPane
             */
            P.DesktopPane.prototype.top = 0;
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
        if(!P.DesktopPane){
            /**
             * Component resized event handler function.
             * @property onComponentResized
             * @memberOf DesktopPane
             */
            P.DesktopPane.prototype.onComponentResized = {};
        }
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parentWidget;
                return P.boxAsJs(value);
            }
        });
        if(!P.DesktopPane){
            /**
             * Parent container of this widget.
             * @property parentWidget
             * @memberOf DesktopPane
             */
            P.DesktopPane.prototype.parent = {};
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
        if(!P.DesktopPane){
            /**
             * Mouse entered over the component event handler function.
             * @property onMouseEntered
             * @memberOf DesktopPane
             */
            P.DesktopPane.prototype.onMouseEntered = {};
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
        if(!P.DesktopPane){
            /**
             * The tooltip string that has been set with.
             * @property toolTipText
             * @memberOf DesktopPane
             */
            P.DesktopPane.prototype.toolTipText = '';
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
        if(!P.DesktopPane){
            /**
             * Height of the component.
             * @property height
             * @memberOf DesktopPane
             */
            P.DesktopPane.prototype.height = 0;
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
        if(!P.DesktopPane){
            /**
             * Component shown event handler function.
             * @property onComponentShown
             * @memberOf DesktopPane
             */
            P.DesktopPane.prototype.onComponentShown = {};
        }
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });
        if(!P.DesktopPane){
            /**
             * Native API. Returns low level html element. Applicable only in HTML5 client.
             * @property element
             * @memberOf DesktopPane
             */
            P.DesktopPane.prototype.element = {};
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
        if(!P.DesktopPane){
            /**
             * Mouse moved event handler function.
             * @property onMouseMoved
             * @memberOf DesktopPane
             */
            P.DesktopPane.prototype.onMouseMoved = {};
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
        if(!P.DesktopPane){
            /**
             * True if this component is completely opaque.
             * @property opaque
             * @memberOf DesktopPane
             */
            P.DesktopPane.prototype.opaque = true;
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
        if(!P.DesktopPane){
            /**
             * Determines whether this component should be visible when its parent is visible.
             * @property visible
             * @memberOf DesktopPane
             */
            P.DesktopPane.prototype.visible = true;
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
        if(!P.DesktopPane){
            /**
             * Component hidden event handler function.
             * @property onComponentHidden
             * @memberOf DesktopPane
             */
            P.DesktopPane.prototype.onComponentHidden = {};
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
        if(!P.DesktopPane){
            /**
             * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
             * @property nextFocusableComponent
             * @memberOf DesktopPane
             */
            P.DesktopPane.prototype.nextFocusableComponent = {};
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
        if(!P.DesktopPane){
            /**
             * Main action performed event handler function.
             * @property onActionPerformed
             * @memberOf DesktopPane
             */
            P.DesktopPane.prototype.onActionPerformed = {};
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
        if(!P.DesktopPane){
            /**
             * Key released event handler function.
             * @property onKeyReleased
             * @memberOf DesktopPane
             */
            P.DesktopPane.prototype.onKeyReleased = {};
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
        if(!P.DesktopPane){
            /**
             * Determines whether this component may be focused.
             * @property focusable
             * @memberOf DesktopPane
             */
            P.DesktopPane.prototype.focusable = true;
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
        if(!P.DesktopPane){
            /**
             * Key typed event handler function.
             * @property onKeyTyped
             * @memberOf DesktopPane
             */
            P.DesktopPane.prototype.onKeyTyped = {};
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
        if(!P.DesktopPane){
            /**
             * Mouse wheel moved event handler function.
             * @property onMouseWheelMoved
             * @memberOf DesktopPane
             */
            P.DesktopPane.prototype.onMouseWheelMoved = {};
        }
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });
        if(!P.DesktopPane){
            /**
             * Native API. Returns low level swing component. Applicable only in J2SE swing client.
             * @property component
             * @memberOf DesktopPane
             */
            P.DesktopPane.prototype.component = {};
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
        if(!P.DesktopPane){
            /**
             * Keyboard focus gained by the component event.
             * @property onFocusGained
             * @memberOf DesktopPane
             */
            P.DesktopPane.prototype.onFocusGained = {};
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
        if(!P.DesktopPane){
            /**
             * Horizontal coordinate of the component.
             * @property left
             * @memberOf DesktopPane
             */
            P.DesktopPane.prototype.left = 0;
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
        if(!P.DesktopPane){
            /**
             * The background color of this component.
             * @property background
             * @memberOf DesktopPane
             */
            P.DesktopPane.prototype.background = {};
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
        if(!P.DesktopPane){
            /**
             * Mouse clicked event handler function.
             * @property onMouseClicked
             * @memberOf DesktopPane
             */
            P.DesktopPane.prototype.onMouseClicked = {};
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
        if(!P.DesktopPane){
            /**
             * Mouse exited over the component event handler function.
             * @property onMouseExited
             * @memberOf DesktopPane
             */
            P.DesktopPane.prototype.onMouseExited = {};
        }
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.DesktopPane){
            /**
             * Gets name of this component.
             * @property name
             * @memberOf DesktopPane
             */
            P.DesktopPane.prototype.name = '';
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
        if(!P.DesktopPane){
            /**
             * Width of the component.
             * @property width
             * @memberOf DesktopPane
             */
            P.DesktopPane.prototype.width = 0;
        }
        Object.defineProperty(this, "forms", {
            get: function() {
                var value = delegate.forms;
                return P.boxAsJs(value);
            }
        });
        if(!P.DesktopPane){
            /**
             * An array of all frames on the pane.
             * @property forms
             * @memberOf DesktopPane
             */
            P.DesktopPane.prototype.forms = [];
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
        if(!P.DesktopPane){
            /**
             * The font of this component.
             * @property font
             * @memberOf DesktopPane
             */
            P.DesktopPane.prototype.font = {};
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
        if(!P.DesktopPane){
            /**
             * Key pressed event handler function.
             * @property onKeyPressed
             * @memberOf DesktopPane
             */
            P.DesktopPane.prototype.onKeyPressed = {};
        }
    };
        /**
         * Closes all frames on the pane.
         * @method closeAll
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.closeAll = function() {
            var delegate = this.unwrap();
            var value = delegate.closeAll();
            return P.boxAsJs(value);
        };

        /**
         * Minimizes all frames on the pane.
         * @method minimizeAll
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.minimizeAll = function() {
            var delegate = this.unwrap();
            var value = delegate.minimizeAll();
            return P.boxAsJs(value);
        };

        /**
         * Restores frames original state and location.
         * @method restoreAll
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.restoreAll = function() {
            var delegate = this.unwrap();
            var value = delegate.restoreAll();
            return P.boxAsJs(value);
        };

        /**
         * Maximizes all frames on the pane.
         * @method maximizeAll
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.maximizeAll = function() {
            var delegate = this.unwrap();
            var value = delegate.maximizeAll();
            return P.boxAsJs(value);
        };

        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf DesktopPane
         */
        P.DesktopPane.prototype.focus = function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        };

})();