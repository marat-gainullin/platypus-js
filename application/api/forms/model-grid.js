(function() {
    var javaClass = Java.type("com.eas.client.forms.components.model.grid.ModelGrid");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ModelGrid(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor ModelGrid ModelGrid
     */
    P.ModelGrid = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.ModelGrid.superclass)
            P.ModelGrid.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "onMouseReleased", {
            get: function() {
                var value = delegate.onMouseReleased;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseReleased = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelGrid){
            /**
             * Mouse released event handler function.
             * @property onMouseReleased
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.onMouseReleased = {};
        }
        Object.defineProperty(this, "data", {
            get: function() {
                var value = delegate.data;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.data = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelGrid){
            /**
             * Generated property jsDoc.
             * @property data
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.data = {};
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
        if(!P.ModelGrid){
            /**
             * Generated property jsDoc.
             * @property foreground
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.foreground = {};
        }
        Object.defineProperty(this, "childrenField", {
            get: function() {
                var value = delegate.childrenField;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.childrenField = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelGrid){
            /**
             * Generated property jsDoc.
             * @property childrenField
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.childrenField = '';
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
        if(!P.ModelGrid){
            /**
             * Component moved event handler function.
             * @property onComponentMoved
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.onComponentMoved = {};
        }
        Object.defineProperty(this, "insertable", {
            get: function() {
                var value = delegate.insertable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.insertable = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelGrid){
            /**
             * Determines if grid allows row insertion.
             * @property insertable
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.insertable = true;
        }
        Object.defineProperty(this, "frozenColumns", {
            get: function() {
                var value = delegate.frozenColumns;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.frozenColumns = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelGrid){
            /**
             * Generated property jsDoc.
             * @property frozenColumns
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.frozenColumns = 0;
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
        if(!P.ModelGrid){
            /**
             * General render event handler.
             * This hanler be called on each cell's rendering in the case when no render handler is provided for the conrete column.
             * @property onRender
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.onRender = {};
        }
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parentWidget;
                return P.boxAsJs(value);
            }
        });
        if(!P.ModelGrid){
            /**
             * Parent container of this widget.
             * @property parentWidget
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.parent = {};
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
        if(!P.ModelGrid){
            /**
             * Mouse entered over the component event handler function.
             * @property onMouseEntered
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.onMouseEntered = {};
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
        if(!P.ModelGrid){
            /**
             * Generated property jsDoc.
             * @property toolTipText
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.toolTipText = '';
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
        if(!P.ModelGrid){
            /**
             * Height of the component.
             * @property height
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.height = 0;
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
        if(!P.ModelGrid){
            /**
             * Component shown event handler function.
             * @property onComponentShown
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.onComponentShown = {};
        }
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });
        if(!P.ModelGrid){
            /**
             * Native API. Returns low level html element. Applicable only in HTML5 client.
             * @property element
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.element = {};
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
        if(!P.ModelGrid){
            /**
             * Determines whether this component should be visible when its parent is visible.
             * @property visible
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.visible = true;
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
        if(!P.ModelGrid){
            /**
             * Component hidden event handler function.
             * @property onComponentHidden
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.onComponentHidden = {};
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
        if(!P.ModelGrid){
            /**
             * Main action performed event handler function.
             * @property onActionPerformed
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.onActionPerformed = {};
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
        if(!P.ModelGrid){
            /**
             * Key released event handler function.
             * @property onKeyReleased
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.onKeyReleased = {};
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
        if(!P.ModelGrid){
            /**
             * Determines whether this component may be focused.
             * @property focusable
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.focusable = true;
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
        if(!P.ModelGrid){
            /**
             * Key typed event handler function.
             * @property onKeyTyped
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.onKeyTyped = {};
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
        if(!P.ModelGrid){
            /**
             * Mouse wheel moved event handler function.
             * @property onMouseWheelMoved
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.onMouseWheelMoved = {};
        }
        Object.defineProperty(this, "cursorProperty", {
            get: function() {
                var value = delegate.cursorProperty;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.cursorProperty = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelGrid){
            /**
             * Determines wich property of ModelGrid's collection is responsible of "current" item. Its default value is "cursor".
             * @property cursorProperty
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.cursorProperty = '';
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
        if(!P.ModelGrid){
            /**
             * Generated property jsDoc.
             * @property field
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.field = '';
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
        if(!P.ModelGrid){
            /**
             * Horizontal coordinate of the component.
             * @property left
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.left = 0;
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
        if(!P.ModelGrid){
            /**
             * Generated property jsDoc.
             * @property background
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.background = {};
        }
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.ModelGrid){
            /**
             * Gets name of this component.
             * @property name
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.name = '';
        }
        Object.defineProperty(this, "showVerticalLines", {
            get: function() {
                var value = delegate.showVerticalLines;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.showVerticalLines = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelGrid){
            /**
             * Determines if grid shows vertical lines.
             * @property showVerticalLines
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.showVerticalLines = true;
        }
        Object.defineProperty(this, "frozenRows", {
            get: function() {
                var value = delegate.frozenRows;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.frozenRows = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelGrid){
            /**
             * Generated property jsDoc.
             * @property frozenRows
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.frozenRows = 0;
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
        if(!P.ModelGrid){
            /**
             * Mouse dragged event handler function.
             * @property onMouseDragged
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.onMouseDragged = {};
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
        if(!P.ModelGrid){
            /**
             * Keyboard focus lost by the component event handler function.
             * @property onFocusLost
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.onFocusLost = {};
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
        if(!P.ModelGrid){
            /**
             * Mouse pressed event handler function.
             * @property onMousePressed
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.onMousePressed = {};
        }
        Object.defineProperty(this, "deletable", {
            get: function() {
                var value = delegate.deletable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.deletable = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelGrid){
            /**
             * Determines if grid allows to delete rows.
             * @property deletable
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.deletable = true;
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
        if(!P.ModelGrid){
            /**
             * An error message of this component.
             * Validation procedure may set this property and subsequent focus lost event will clear it.
             * @property error
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.error = '';
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
        if(!P.ModelGrid){
            /**
             * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
             * @property enabled
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.enabled = true;
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
        if(!P.ModelGrid){
            /**
             * Generated property jsDoc.
             * @property componentPopupMenu
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.componentPopupMenu = {};
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
        if(!P.ModelGrid){
            /**
             * Vertical coordinate of the component.
             * @property top
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.top = 0;
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
        if(!P.ModelGrid){
            /**
             * Component resized event handler function.
             * @property onComponentResized
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.onComponentResized = {};
        }
        Object.defineProperty(this, "oddRowsColor", {
            get: function() {
                var value = delegate.oddRowsColor;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.oddRowsColor = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelGrid){
            /**
             * Odd rows color.
             * @property oddRowsColor
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.oddRowsColor = {};
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
        if(!P.ModelGrid){
            /**
             * Mouse moved event handler function.
             * @property onMouseMoved
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.onMouseMoved = {};
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
        if(!P.ModelGrid){
            /**
             * True if this component is completely opaque.
             * @property opaque
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.opaque = true;
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
        if(!P.ModelGrid){
            /**
             * Determines if gris cells are editable.
             * @property editable
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.editable = true;
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
        if(!P.ModelGrid){
            /**
             * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
             * @property nextFocusableComponent
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.nextFocusableComponent = {};
        }
        Object.defineProperty(this, "parentField", {
            get: function() {
                var value = delegate.parentField;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.parentField = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelGrid){
            /**
             * Generated property jsDoc.
             * @property parentField
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.parentField = '';
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
        if(!P.ModelGrid){
            /**
             * Event that is fired when selection lead changes in this ModelGrid.
             * @property onItemSelected
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.onItemSelected = {};
        }
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });
        if(!P.ModelGrid){
            /**
             * Native API. Returns low level swing component. Applicable only in J2SE swing client.
             * @property component
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.component = {};
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
        if(!P.ModelGrid){
            /**
             * Keyboard focus gained by the component event.
             * @property onFocusGained
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.onFocusGained = {};
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
        if(!P.ModelGrid){
            /**
             * Mouse clicked event handler function.
             * @property onMouseClicked
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.onMouseClicked = {};
        }
        Object.defineProperty(this, "rowsHeight", {
            get: function() {
                var value = delegate.rowsHeight;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.rowsHeight = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelGrid){
            /**
             * The height of grid's rows.
             * @property rowsHeight
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.rowsHeight = 0;
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
        if(!P.ModelGrid){
            /**
             * Mouse exited over the component event handler function.
             * @property onMouseExited
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.onMouseExited = {};
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
        if(!P.ModelGrid){
            /**
             * Width of the component.
             * @property width
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.width = 0;
        }
        Object.defineProperty(this, "showHorizontalLines", {
            get: function() {
                var value = delegate.showHorizontalLines;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.showHorizontalLines = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelGrid){
            /**
             * Determines if grid shows horizontal lines.
             * @property showHorizontalLines
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.showHorizontalLines = true;
        }
        Object.defineProperty(this, "gridColor", {
            get: function() {
                var value = delegate.gridColor;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.gridColor = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelGrid){
            /**
             * The color of the grid.
             * @property gridColor
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.gridColor = {};
        }
        Object.defineProperty(this, "showOddRowsInOtherColor", {
            get: function() {
                var value = delegate.showOddRowsInOtherColor;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.showOddRowsInOtherColor = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelGrid){
            /**
             * Determines if grid shows odd rows if other color.
             * @property showOddRowsInOtherColor
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.showOddRowsInOtherColor = true;
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
        if(!P.ModelGrid){
            /**
             * Generated property jsDoc.
             * @property font
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.font = {};
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
        if(!P.ModelGrid){
            /**
             * Key pressed event handler function.
             * @property onKeyPressed
             * @memberOf ModelGrid
             */
            P.ModelGrid.prototype.onKeyPressed = {};
        }
    };
        /**
         * Shows find dialog.
         * @deprecated Use find() instead.
         * @method find
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.find = function() {
            var delegate = this.unwrap();
            var value = delegate.find();
            return P.boxAsJs(value);
        };

        /**
         * Selects the specified element.
         * @param instance Entity's instance to be selected.
         * @method select
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.select = function(instance) {
            var delegate = this.unwrap();
            var value = delegate.select(P.boxAsJava(instance));
            return P.boxAsJs(value);
        };

        /**
         * Clears current selection.
         * @method clearSelection
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.clearSelection = function() {
            var delegate = this.unwrap();
            var value = delegate.clearSelection();
            return P.boxAsJs(value);
        };

        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.focus = function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        };

        /**
         *
         * @method elementByModelIndex
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.elementByModelIndex = function(arg0) {
            var delegate = this.unwrap();
            var value = delegate.elementByModelIndex(P.boxAsJava(arg0));
            return P.boxAsJs(value);
        };

        /**
         *
         * @method try2StopAnyEditing
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.try2StopAnyEditing = function() {
            var delegate = this.unwrap();
            var value = delegate.try2StopAnyEditing();
            return P.boxAsJs(value);
        };

        /**
         *
         * @method try2CancelAnyEditing
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.try2CancelAnyEditing = function() {
            var delegate = this.unwrap();
            var value = delegate.try2CancelAnyEditing();
            return P.boxAsJs(value);
        };

        /**
         * Redraw the component.
         * @method redraw
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.redraw = function() {
            var delegate = this.unwrap();
            var value = delegate.redraw();
            return P.boxAsJs(value);
        };

        /**
         * Unselects the specified element.
         * @param instance Entity's instance to be unselected
         * @method unselect
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.unselect = function(instance) {
            var delegate = this.unwrap();
            var value = delegate.unselect(P.boxAsJava(instance));
            return P.boxAsJs(value);
        };

        /**
         *
         * @method addColumnNode
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.addColumnNode = function(arg0) {
            var delegate = this.unwrap();
            var value = delegate.addColumnNode(P.boxAsJava(arg0));
            return P.boxAsJs(value);
        };

        /**
         *
         * @method removeColumnNode
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.removeColumnNode = function(arg0) {
            var delegate = this.unwrap();
            var value = delegate.removeColumnNode(P.boxAsJava(arg0));
            return P.boxAsJs(value);
        };

        /**
         *
         * @method insertColumnNode
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.insertColumnNode = function(arg0, arg1) {
            var delegate = this.unwrap();
            var value = delegate.insertColumnNode(P.boxAsJava(arg0), P.boxAsJava(arg1));
            return P.boxAsJs(value);
        };

        /**
         * Makes specified instance visible.
         * @param instance Entity's instance to make visible.
         * @param need2select true to select the instance (optional).
         * @method makeVisible
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.makeVisible = function(instance, need2select) {
            var delegate = this.unwrap();
            var value = delegate.makeVisible(P.boxAsJava(instance), P.boxAsJava(need2select));
            return P.boxAsJs(value);
        };

})();