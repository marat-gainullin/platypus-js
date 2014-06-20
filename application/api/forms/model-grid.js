(function() {
    var javaClass = Java.type("com.eas.client.forms.api.components.model.ModelGrid");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ModelGrid(aDelegate);
    });
    
    /**
     * A model component that shows a data grid.
     * @constructor ModelGrid ModelGrid
     */
    P.ModelGrid = function ModelGrid() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(ModelGrid.superclass)
            ModelGrid.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "ModelGrid", {value: ModelGrid});
    Object.defineProperty(ModelGrid.prototype, "parent", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.parent;
            return P.boxAsJs(value);
        }
    });
    if(!ModelGrid){
        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.parent = {};
    }
    Object.defineProperty(ModelGrid.prototype, "onMouseReleased", {
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
    if(!ModelGrid){
        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.onMouseReleased = {};
    }
    Object.defineProperty(ModelGrid.prototype, "foreground", {
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
    if(!ModelGrid){
        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.foreground = {};
    }
    Object.defineProperty(ModelGrid.prototype, "onComponentMoved", {
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
    if(!ModelGrid){
        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.onComponentMoved = {};
    }
    Object.defineProperty(ModelGrid.prototype, "insertable", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.insertable;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.insertable = P.boxAsJava(aValue);
        }
    });
    if(!ModelGrid){
        /**
         * Determines if grid allows row insertion.
         * @property insertable
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.insertable = true;
    }
    Object.defineProperty(ModelGrid.prototype, "onRender", {
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
    if(!ModelGrid){
        /**
         * General render event handler.
         * This hanler be called on each cell's rendering in the case when no render handler is provided for the conrete column.
         * @property onRender
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.onRender = {};
    }
    Object.defineProperty(ModelGrid.prototype, "onMouseEntered", {
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
    if(!ModelGrid){
        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.onMouseEntered = {};
    }
    Object.defineProperty(ModelGrid.prototype, "selected", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.selected;
            return P.boxAsJs(value);
        }
    });
    if(!ModelGrid){
        /**
         *  Gets the array of selected rows.
         * @property selected
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.selected = {};
    }
    Object.defineProperty(ModelGrid.prototype, "toolTipText", {
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
    if(!ModelGrid){
        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.toolTipText = '';
    }
    Object.defineProperty(ModelGrid.prototype, "height", {
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
    if(!ModelGrid){
        /**
         * Height of the component.
         * @property height
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.height = 0;
    }
    Object.defineProperty(ModelGrid.prototype, "element", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.element;
            return P.boxAsJs(value);
        }
    });
    if(!ModelGrid){
        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.element = {};
    }
    Object.defineProperty(ModelGrid.prototype, "onComponentShown", {
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
    if(!ModelGrid){
        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.onComponentShown = {};
    }
    Object.defineProperty(ModelGrid.prototype, "visible", {
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
    if(!ModelGrid){
        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.visible = true;
    }
    Object.defineProperty(ModelGrid.prototype, "onComponentHidden", {
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
    if(!ModelGrid){
        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.onComponentHidden = {};
    }
    Object.defineProperty(ModelGrid.prototype, "onActionPerformed", {
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
    if(!ModelGrid){
        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.onActionPerformed = {};
    }
    Object.defineProperty(ModelGrid.prototype, "onKeyReleased", {
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
    if(!ModelGrid){
        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.onKeyReleased = {};
    }
    Object.defineProperty(ModelGrid.prototype, "focusable", {
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
    if(!ModelGrid){
        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.focusable = true;
    }
    Object.defineProperty(ModelGrid.prototype, "onKeyTyped", {
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
    if(!ModelGrid){
        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.onKeyTyped = {};
    }
    Object.defineProperty(ModelGrid.prototype, "onMouseWheelMoved", {
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
    if(!ModelGrid){
        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.onMouseWheelMoved = {};
    }
    Object.defineProperty(ModelGrid.prototype, "left", {
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
    if(!ModelGrid){
        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.left = 0;
    }
    Object.defineProperty(ModelGrid.prototype, "selectedCells", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.selectedCells;
            return P.boxAsJs(value);
        }
    });
    if(!ModelGrid){
        /**
         * Gets all grid selected cells as an array.
         * <b>WARNING!!! All selected cells will be copied.</b>
         * @property selectedCells
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.selectedCells = [];
    }
    Object.defineProperty(ModelGrid.prototype, "background", {
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
    if(!ModelGrid){
        /**
         * The background color of this component.
         * @property background
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.background = {};
    }
    Object.defineProperty(ModelGrid.prototype, "showVerticalLines", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.showVerticalLines;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.showVerticalLines = P.boxAsJava(aValue);
        }
    });
    if(!ModelGrid){
        /**
         * Determines if grid shows vertical lines.
         * @property showVerticalLines
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.showVerticalLines = true;
    }
    Object.defineProperty(ModelGrid.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!ModelGrid){
        /**
         * Gets name of this component.
         * @property name
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.name = '';
    }
    Object.defineProperty(ModelGrid.prototype, "cursor", {
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
    if(!ModelGrid){
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.cursor = {};
    }
    Object.defineProperty(ModelGrid.prototype, "onMouseDragged", {
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
    if(!ModelGrid){
        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.onMouseDragged = {};
    }
    Object.defineProperty(ModelGrid.prototype, "onFocusLost", {
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
    if(!ModelGrid){
        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.onFocusLost = {};
    }
    Object.defineProperty(ModelGrid.prototype, "columns", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.columns;
            return P.boxAsJs(value);
        }
    });
    if(!ModelGrid){
        /**
         * Gets grid columns as an array.
         * @property columns
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.columns = {};
    }
    Object.defineProperty(ModelGrid.prototype, "deletable", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.deletable;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.deletable = P.boxAsJava(aValue);
        }
    });
    if(!ModelGrid){
        /**
         * Determines if grid allows to delete rows.
         * @property deletable
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.deletable = true;
    }
    Object.defineProperty(ModelGrid.prototype, "onMousePressed", {
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
    if(!ModelGrid){
        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.onMousePressed = {};
    }
    Object.defineProperty(ModelGrid.prototype, "error", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.error;
            return P.boxAsJs(value);
        }
    });
    if(!ModelGrid){
        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.error = '';
    }
    Object.defineProperty(ModelGrid.prototype, "enabled", {
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
    if(!ModelGrid){
        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.enabled = true;
    }
    Object.defineProperty(ModelGrid.prototype, "componentPopupMenu", {
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
    if(!ModelGrid){
        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.componentPopupMenu = {};
    }
    Object.defineProperty(ModelGrid.prototype, "top", {
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
    if(!ModelGrid){
        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.top = 0;
    }
    Object.defineProperty(ModelGrid.prototype, "onComponentResized", {
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
    if(!ModelGrid){
        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.onComponentResized = {};
    }
    Object.defineProperty(ModelGrid.prototype, "oddRowsColor", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.oddRowsColor;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.oddRowsColor = P.boxAsJava(aValue);
        }
    });
    if(!ModelGrid){
        /**
         * Odd rows color.
         * @property oddRowsColor
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.oddRowsColor = {};
    }
    Object.defineProperty(ModelGrid.prototype, "onMouseMoved", {
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
    if(!ModelGrid){
        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.onMouseMoved = {};
    }
    Object.defineProperty(ModelGrid.prototype, "opaque", {
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
    if(!ModelGrid){
        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.opaque = true;
    }
    Object.defineProperty(ModelGrid.prototype, "editable", {
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
    if(!ModelGrid){
        /**
         * Determines if gris cells are editable.
         * @property editable
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.editable = true;
    }
    Object.defineProperty(ModelGrid.prototype, "nextFocusableComponent", {
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
    if(!ModelGrid){
        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.nextFocusableComponent = {};
    }
    Object.defineProperty(ModelGrid.prototype, "component", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.component;
            return P.boxAsJs(value);
        }
    });
    if(!ModelGrid){
        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.component = {};
    }
    Object.defineProperty(ModelGrid.prototype, "cells", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.cells;
            return P.boxAsJs(value);
        }
    });
    if(!ModelGrid){
        /**
         * Gets all grid cells as an array.
         * <b>WARNING!!! All cells will be copied.</b>
         * @property cells
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.cells = [];
    }
    Object.defineProperty(ModelGrid.prototype, "onFocusGained", {
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
    if(!ModelGrid){
        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.onFocusGained = {};
    }
    Object.defineProperty(ModelGrid.prototype, "rowsHeight", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.rowsHeight;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.rowsHeight = P.boxAsJava(aValue);
        }
    });
    if(!ModelGrid){
        /**
         * The height of grid's rows.
         * @property rowsHeight
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.rowsHeight = 0;
    }
    Object.defineProperty(ModelGrid.prototype, "onMouseClicked", {
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
    if(!ModelGrid){
        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.onMouseClicked = {};
    }
    Object.defineProperty(ModelGrid.prototype, "onMouseExited", {
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
    if(!ModelGrid){
        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.onMouseExited = {};
    }
    Object.defineProperty(ModelGrid.prototype, "gridColor", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.gridColor;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.gridColor = P.boxAsJava(aValue);
        }
    });
    if(!ModelGrid){
        /**
         * The color of the grid.
         * @property gridColor
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.gridColor = {};
    }
    Object.defineProperty(ModelGrid.prototype, "showHorizontalLines", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.showHorizontalLines;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.showHorizontalLines = P.boxAsJava(aValue);
        }
    });
    if(!ModelGrid){
        /**
         * Determines if grid shows horizontal lines.
         * @property showHorizontalLines
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.showHorizontalLines = true;
    }
    Object.defineProperty(ModelGrid.prototype, "width", {
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
    if(!ModelGrid){
        /**
         * Width of the component.
         * @property width
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.width = 0;
    }
    Object.defineProperty(ModelGrid.prototype, "showOddRowsInOtherColor", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.showOddRowsInOtherColor;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.showOddRowsInOtherColor = P.boxAsJava(aValue);
        }
    });
    if(!ModelGrid){
        /**
         * Determines if grid shows odd rows if other color.
         * @property showOddRowsInOtherColor
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.showOddRowsInOtherColor = true;
    }
    Object.defineProperty(ModelGrid.prototype, "font", {
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
    if(!ModelGrid){
        /**
         * The font of this component.
         * @property font
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.font = {};
    }
    Object.defineProperty(ModelGrid.prototype, "onKeyPressed", {
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
    if(!ModelGrid){
        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.onKeyPressed = {};
    }
    Object.defineProperty(ModelGrid.prototype, "find", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.find();
            return P.boxAsJs(value);
        }
    });
    if(!ModelGrid){
        /**
         * Shows find dialog.
         * @method find
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.find = function(){};
    }
    Object.defineProperty(ModelGrid.prototype, "select", {
        value: function(arg0) {
            var delegate = this.unwrap();
            var value = delegate.select(P.boxAsJava(arg0));
            return P.boxAsJs(value);
        }
    });
    if(!ModelGrid){
        /**
         *  Gets the array of selected rows.
         * @method select
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.select = function(arg0){};
    }
    Object.defineProperty(ModelGrid.prototype, "clearSelection", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.clearSelection();
            return P.boxAsJs(value);
        }
    });
    if(!ModelGrid){
        /**
         * Clears current selection.
         * @method clearSelection
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.clearSelection = function(){};
    }
    Object.defineProperty(ModelGrid.prototype, "makeVisible", {
        value: function(arg0, arg1) {
            var delegate = this.unwrap();
            var value = delegate.makeVisible(P.boxAsJava(arg0), P.boxAsJava(arg1));
            return P.boxAsJs(value);
        }
    });
    if(!ModelGrid){
        /**
         * Makes specified row visible.
         * @param row the row to make visible.
         * @param need2select true to select the row (optional).
         * @method makeVisible
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.makeVisible = function(arg0, arg1){};
    }
    Object.defineProperty(ModelGrid.prototype, "unselect", {
        value: function(row) {
            var delegate = this.unwrap();
            var value = delegate.unselect(P.boxAsJava(row));
            return P.boxAsJs(value);
        }
    });
    if(!ModelGrid){
        /**
         * Unselects the specified row.
         * @param row the row to be unselected
         * @method unselect
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.unselect = function(row){};
    }
    Object.defineProperty(ModelGrid.prototype, "findSomething", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.findSomething();
            return P.boxAsJs(value);
        }
    });
    if(!ModelGrid){
        /**
         * Shows find dialog.
         * @deprecated Use find() instead.
         * @method findSomething
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.findSomething = function(){};
    }
    Object.defineProperty(ModelGrid.prototype, "focus", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.focus();
            return P.boxAsJs(value);
        }
    });
    if(!ModelGrid){
        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.focus = function(){};
    }
})();