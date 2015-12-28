/* global Java */

define(['boxing', 'common-utils/color', 'common-utils/cursor', 'common-utils/font', './action-event', './cell-render-event', './component-event', './focus-event', './item-event', './key-event', './value-change-event', './popup-menu', 'grid/cell-data', './service-grid-column', './check-grid-column', './radio-grid-column', './model-check-box', './model-combo', './model-date', './model-formatted-field', './model-grid-column', './model-spin', './model-text-area'], function(B, Color, Cursor, Font, ActionEvent, RenderEvent, ComponentEvent, FocusEvent, ItemEvent, KeyEvent, ValueChangeEvent, PopupMenu, CellData, RenderEvent, ItemEvent, ServiceGridColumn, CheckGridColumn, RadioGridColumn, ModelCheckBox, ModelCombo, ModelDate, ModelFormattedField, ModelGridColumn, ModelSpin, ModelTextArea) {
    var className = "com.eas.client.forms.components.model.grid.ModelGrid";
    var javaClass = Java.type(className);
    /**
     * Generated constructor.
     * @constructor ModelGrid ModelGrid
     */
    function ModelGrid() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            configurable: true,
            value: function() {
                return delegate;
            }
        });
        if(ModelGrid.superclass)
            ModelGrid.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        /**
         * Mouse released event handler function.
         */
        this.onMouseReleased = new Object();
        Object.defineProperty(this, "onMouseReleased", {
            get: function() {
                var value = delegate.onMouseReleased;
                return value;
            },
            set: function(aValue) {
                delegate.onMouseReleased = aValue;
            }
        });

        this.selected = new Object();
        Object.defineProperty(this, "selected", {
            get: function() {
                var value = delegate.jsSelected;
                return value;
            }
        });

        this.data = new Object();
        Object.defineProperty(this, "data", {
            get: function() {
                var value = delegate.data;
                return value;
            },
            set: function(aValue) {
                delegate.data = aValue;
            }
        });

        this.foreground = new Object();
        Object.defineProperty(this, "foreground", {
            get: function() {
                var value = delegate.foreground;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.foreground = B.boxAsJava(aValue);
            }
        });

        this.childrenField = '';
        Object.defineProperty(this, "childrenField", {
            get: function() {
                var value = delegate.childrenField;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.childrenField = B.boxAsJava(aValue);
            }
        });

        /**
         * Component moved event handler function.
         */
        this.onComponentMoved = new Object();
        Object.defineProperty(this, "onComponentMoved", {
            get: function() {
                var value = delegate.onComponentMoved;
                return value;
            },
            set: function(aValue) {
                delegate.onComponentMoved = aValue;
            }
        });

        /**
         * Determines if grid allows row insertion.
         */
        this.insertable = true;
        Object.defineProperty(this, "insertable", {
            get: function() {
                var value = delegate.insertable;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.insertable = B.boxAsJava(aValue);
            }
        });

        this.frozenColumns = 0;
        Object.defineProperty(this, "frozenColumns", {
            get: function() {
                var value = delegate.frozenColumns;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.frozenColumns = B.boxAsJava(aValue);
            }
        });

        /**
         * General render event handler.
         * This hanler be called on each cell's rendering in the case when no render handler is provided for the conrete column.
         */
        this.onRender = new Object();
        Object.defineProperty(this, "onRender", {
            get: function() {
                var value = delegate.onRender;
                return value;
            },
            set: function(aValue) {
                delegate.onRender = aValue;
            }
        });

        /**
         * Parent container of this widget.
         */
        this.parent = new Object();
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parentWidget;
                return B.boxAsJs(value);
            }
        });

        /**
         * Mouse entered over the component event handler function.
         */
        this.onMouseEntered = new Object();
        Object.defineProperty(this, "onMouseEntered", {
            get: function() {
                var value = delegate.onMouseEntered;
                return value;
            },
            set: function(aValue) {
                delegate.onMouseEntered = aValue;
            }
        });

        this.toolTipText = '';
        Object.defineProperty(this, "toolTipText", {
            get: function() {
                var value = delegate.toolTipText;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.toolTipText = B.boxAsJava(aValue);
            }
        });

        /**
         * Height of the component.
         */
        this.height = 0;
        Object.defineProperty(this, "height", {
            get: function() {
                var value = delegate.height;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.height = B.boxAsJava(aValue);
            }
        });

        /**
         * Component shown event handler function.
         */
        this.onComponentShown = new Object();
        Object.defineProperty(this, "onComponentShown", {
            get: function() {
                var value = delegate.onComponentShown;
                return value;
            },
            set: function(aValue) {
                delegate.onComponentShown = aValue;
            }
        });

        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         */
        this.element = new Object();
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return B.boxAsJs(value);
            }
        });

        /**
         * Determines whether this component should be visible when its parent is visible.
         */
        this.visible = true;
        Object.defineProperty(this, "visible", {
            get: function() {
                var value = delegate.visible;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.visible = B.boxAsJava(aValue);
            }
        });

        /**
         * Component hidden event handler function.
         */
        this.onComponentHidden = new Object();
        Object.defineProperty(this, "onComponentHidden", {
            get: function() {
                var value = delegate.onComponentHidden;
                return value;
            },
            set: function(aValue) {
                delegate.onComponentHidden = aValue;
            }
        });

        /**
         * Main action performed event handler function.
         */
        this.onActionPerformed = new Object();
        Object.defineProperty(this, "onActionPerformed", {
            get: function() {
                var value = delegate.onActionPerformed;
                return value;
            },
            set: function(aValue) {
                delegate.onActionPerformed = aValue;
            }
        });

        /**
         * Key released event handler function.
         */
        this.onKeyReleased = new Object();
        Object.defineProperty(this, "onKeyReleased", {
            get: function() {
                var value = delegate.onKeyReleased;
                return value;
            },
            set: function(aValue) {
                delegate.onKeyReleased = aValue;
            }
        });

        /**
         * Determines whether this component may be focused.
         */
        this.focusable = true;
        Object.defineProperty(this, "focusable", {
            get: function() {
                var value = delegate.focusable;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.focusable = B.boxAsJava(aValue);
            }
        });

        /**
         * Key typed event handler function.
         */
        this.onKeyTyped = new Object();
        Object.defineProperty(this, "onKeyTyped", {
            get: function() {
                var value = delegate.onKeyTyped;
                return value;
            },
            set: function(aValue) {
                delegate.onKeyTyped = aValue;
            }
        });

        /**
         * Mouse wheel moved event handler function.
         */
        this.onMouseWheelMoved = new Object();
        Object.defineProperty(this, "onMouseWheelMoved", {
            get: function() {
                var value = delegate.onMouseWheelMoved;
                return value;
            },
            set: function(aValue) {
                delegate.onMouseWheelMoved = aValue;
            }
        });

        /**
         * Determines wich property of ModelGrid's collection is responsible of "current" item. Its default value is "cursor".
         */
        this.cursorProperty = '';
        Object.defineProperty(this, "cursorProperty", {
            get: function() {
                var value = delegate.cursorProperty;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.cursorProperty = B.boxAsJava(aValue);
            }
        });

        this.field = '';
        Object.defineProperty(this, "field", {
            get: function() {
                var value = delegate.field;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.field = B.boxAsJava(aValue);
            }
        });

        /**
         * Horizontal coordinate of the component.
         */
        this.left = 0;
        Object.defineProperty(this, "left", {
            get: function() {
                var value = delegate.left;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.left = B.boxAsJava(aValue);
            }
        });

        this.background = new Object();
        Object.defineProperty(this, "background", {
            get: function() {
                var value = delegate.background;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.background = B.boxAsJava(aValue);
            }
        });

        /**
         * Gets name of this component.
         */
        this.name = '';
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.name = B.boxAsJava(aValue);
            }
        });

        /**
         * Determines if grid shows vertical lines.
         */
        this.showVerticalLines = true;
        Object.defineProperty(this, "showVerticalLines", {
            get: function() {
                var value = delegate.showVerticalLines;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.showVerticalLines = B.boxAsJava(aValue);
            }
        });

        this.frozenRows = 0;
        Object.defineProperty(this, "frozenRows", {
            get: function() {
                var value = delegate.frozenRows;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.frozenRows = B.boxAsJava(aValue);
            }
        });

        /**
         * Mouse dragged event handler function.
         */
        this.onMouseDragged = new Object();
        Object.defineProperty(this, "onMouseDragged", {
            get: function() {
                var value = delegate.onMouseDragged;
                return value;
            },
            set: function(aValue) {
                delegate.onMouseDragged = aValue;
            }
        });

        /**
         * Event that is fired when user collapses ModelGrid's row.
         */
        this.onCollapse = new Object();
        Object.defineProperty(this, "onCollapse", {
            get: function() {
                var value = delegate.onCollapse;
                return value;
            },
            set: function(aValue) {
                delegate.onCollapse = aValue;
            }
        });

        /**
         * Event that is fired when user expands ModelGrid's row.
         */
        this.onExpand = new Object();
        Object.defineProperty(this, "onExpand", {
            get: function() {
                var value = delegate.onExpand;
                return value;
            },
            set: function(aValue) {
                delegate.onExpand = aValue;
            }
        });

        /**
         * Keyboard focus lost by the component event handler function.
         */
        this.onFocusLost = new Object();
        Object.defineProperty(this, "onFocusLost", {
            get: function() {
                var value = delegate.onFocusLost;
                return value;
            },
            set: function(aValue) {
                delegate.onFocusLost = aValue;
            }
        });

        /**
         * Mouse pressed event handler function.
         */
        this.onMousePressed = new Object();
        Object.defineProperty(this, "onMousePressed", {
            get: function() {
                var value = delegate.onMousePressed;
                return value;
            },
            set: function(aValue) {
                delegate.onMousePressed = aValue;
            }
        });

        /**
         * Determines if grid allows to delete rows.
         */
        this.deletable = true;
        Object.defineProperty(this, "deletable", {
            get: function() {
                var value = delegate.deletable;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.deletable = B.boxAsJava(aValue);
            }
        });

        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         */
        this.error = '';
        Object.defineProperty(this, "error", {
            get: function() {
                var value = delegate.error;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.error = B.boxAsJava(aValue);
            }
        });

        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         */
        this.enabled = true;
        Object.defineProperty(this, "enabled", {
            get: function() {
                var value = delegate.enabled;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.enabled = B.boxAsJava(aValue);
            }
        });

        this.componentPopupMenu = new Object();
        Object.defineProperty(this, "componentPopupMenu", {
            get: function() {
                var value = delegate.componentPopupMenu;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.componentPopupMenu = B.boxAsJava(aValue);
            }
        });

        /**
         * Vertical coordinate of the component.
         */
        this.top = 0;
        Object.defineProperty(this, "top", {
            get: function() {
                var value = delegate.top;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.top = B.boxAsJava(aValue);
            }
        });

        /**
         * Component resized event handler function.
         */
        this.onComponentResized = new Object();
        Object.defineProperty(this, "onComponentResized", {
            get: function() {
                var value = delegate.onComponentResized;
                return value;
            },
            set: function(aValue) {
                delegate.onComponentResized = aValue;
            }
        });

        /**
         * Odd rows color.
         */
        this.oddRowsColor = new Object();
        Object.defineProperty(this, "oddRowsColor", {
            get: function() {
                var value = delegate.oddRowsColor;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.oddRowsColor = B.boxAsJava(aValue);
            }
        });

        this.headerVisible = true;
        Object.defineProperty(this, "headerVisible", {
            get: function() {
                var value = delegate.headerVisible;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.headerVisible = B.boxAsJava(aValue);
            }
        });

        /**
         * Mouse moved event handler function.
         */
        this.onMouseMoved = new Object();
        Object.defineProperty(this, "onMouseMoved", {
            get: function() {
                var value = delegate.onMouseMoved;
                return value;
            },
            set: function(aValue) {
                delegate.onMouseMoved = aValue;
            }
        });

        /**
         * True if this component is completely opaque.
         */
        this.opaque = true;
        Object.defineProperty(this, "opaque", {
            get: function() {
                var value = delegate.opaque;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.opaque = B.boxAsJava(aValue);
            }
        });

        /**
         * Determines if gris cells are editable.
         */
        this.editable = true;
        Object.defineProperty(this, "editable", {
            get: function() {
                var value = delegate.editable;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.editable = B.boxAsJava(aValue);
            }
        });

        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         */
        this.nextFocusableComponent = new Object();
        Object.defineProperty(this, "nextFocusableComponent", {
            get: function() {
                var value = delegate.nextFocusableComponent;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.nextFocusableComponent = B.boxAsJava(aValue);
            }
        });

        this.parentField = '';
        Object.defineProperty(this, "parentField", {
            get: function() {
                var value = delegate.parentField;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.parentField = B.boxAsJava(aValue);
            }
        });

        /**
         * Event that is fired when selection lead changes in this ModelGrid.
         */
        this.onItemSelected = new Object();
        Object.defineProperty(this, "onItemSelected", {
            get: function() {
                var value = delegate.onItemSelected;
                return value;
            },
            set: function(aValue) {
                delegate.onItemSelected = aValue;
            }
        });

        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         */
        this.component = new Object();
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return B.boxAsJs(value);
            }
        });

        /**
         * Keyboard focus gained by the component event.
         */
        this.onFocusGained = new Object();
        Object.defineProperty(this, "onFocusGained", {
            get: function() {
                var value = delegate.onFocusGained;
                return value;
            },
            set: function(aValue) {
                delegate.onFocusGained = aValue;
            }
        });

        /**
         * Mouse clicked event handler function.
         */
        this.onMouseClicked = new Object();
        Object.defineProperty(this, "onMouseClicked", {
            get: function() {
                var value = delegate.onMouseClicked;
                return value;
            },
            set: function(aValue) {
                delegate.onMouseClicked = aValue;
            }
        });

        /**
         * The height of grid's rows.
         */
        this.rowsHeight = 0;
        Object.defineProperty(this, "rowsHeight", {
            get: function() {
                var value = delegate.rowsHeight;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.rowsHeight = B.boxAsJava(aValue);
            }
        });

        /**
         * Mouse exited over the component event handler function.
         */
        this.onMouseExited = new Object();
        Object.defineProperty(this, "onMouseExited", {
            get: function() {
                var value = delegate.onMouseExited;
                return value;
            },
            set: function(aValue) {
                delegate.onMouseExited = aValue;
            }
        });

        /**
         * Width of the component.
         */
        this.width = 0;
        Object.defineProperty(this, "width", {
            get: function() {
                var value = delegate.width;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.width = B.boxAsJava(aValue);
            }
        });

        /**
         * Determines if grid shows horizontal lines.
         */
        this.showHorizontalLines = true;
        Object.defineProperty(this, "showHorizontalLines", {
            get: function() {
                var value = delegate.showHorizontalLines;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.showHorizontalLines = B.boxAsJava(aValue);
            }
        });

        /**
         * The color of the grid.
         */
        this.gridColor = new Object();
        Object.defineProperty(this, "gridColor", {
            get: function() {
                var value = delegate.gridColor;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.gridColor = B.boxAsJava(aValue);
            }
        });

        /**
         * Determines if grid shows odd rows if other color.
         */
        this.showOddRowsInOtherColor = true;
        Object.defineProperty(this, "showOddRowsInOtherColor", {
            get: function() {
                var value = delegate.showOddRowsInOtherColor;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.showOddRowsInOtherColor = B.boxAsJava(aValue);
            }
        });

        this.draggableRows = new Object();
        Object.defineProperty(this, "draggableRows", {
            get: function() {
                var value = delegate.draggableRows;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.draggableRows = B.boxAsJava(aValue);
            }
        });

        this.font = new Object();
        Object.defineProperty(this, "font", {
            get: function() {
                var value = delegate.font;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.font = B.boxAsJava(aValue);
            }
        });

        /**
         * Key pressed event handler function.
         */
        this.onKeyPressed = new Object();
        Object.defineProperty(this, "onKeyPressed", {
            get: function() {
                var value = delegate.onKeyPressed;
                return value;
            },
            set: function(aValue) {
                delegate.onKeyPressed = aValue;
            }
        });

    }
    /**
     * Shows find dialog.
     * @deprecated Use find() instead.
     * @method find
     * @memberOf ModelGrid
     */
    ModelGrid.prototype.find = function() {
        var delegate = this.unwrap();
        var value = delegate.find();
        return B.boxAsJs(value);
    };

    /**
     * Makes node of specified .data array element expanded.
     * @param instance .data array element to expand.
     * @method expand
     * @memberOf ModelGrid
     */
    ModelGrid.prototype.expand = function(instance) {
        var delegate = this.unwrap();
        var value = delegate.expand(B.boxAsJava(instance));
        return B.boxAsJs(value);
    };

    /**
     * Selects the specified element.
     * @param instance Entity's instance to be selected.
     * @method select
     * @memberOf ModelGrid
     */
    ModelGrid.prototype.select = function(instance) {
        var delegate = this.unwrap();
        var value = delegate.select(B.boxAsJava(instance));
        return B.boxAsJs(value);
    };

    /**
     * Clears current selection.
     * @method clearSelection
     * @memberOf ModelGrid
     */
    ModelGrid.prototype.clearSelection = function() {
        var delegate = this.unwrap();
        var value = delegate.clearSelection();
        return B.boxAsJs(value);
    };

    /**
     *
     * @method elementByModelIndex
     * @memberOf ModelGrid
     */
    ModelGrid.prototype.elementByModelIndex = function(arg0) {
        var delegate = this.unwrap();
        var value = delegate.elementByModelIndex(B.boxAsJava(arg0));
        return B.boxAsJs(value);
    };

    /**
     * Tries to acquire focus for this component.
     * @method focus
     * @memberOf ModelGrid
     */
    ModelGrid.prototype.focus = function() {
        var delegate = this.unwrap();
        var value = delegate.focus();
        return B.boxAsJs(value);
    };

    /**
     * Redraw the component.
     * @method redraw
     * @memberOf ModelGrid
     */
    ModelGrid.prototype.redraw = function() {
        var delegate = this.unwrap();
        var value = delegate.redraw();
        return B.boxAsJs(value);
    };

    /**
     * Unselects the specified element.
     * @param instance Entity's instance to be unselected
     * @method unselect
     * @memberOf ModelGrid
     */
    ModelGrid.prototype.unselect = function(instance) {
        var delegate = this.unwrap();
        var value = delegate.unselect(B.boxAsJava(instance));
        return B.boxAsJs(value);
    };

    /**
     * Tests if node of specified .data array element is expanded.
     * @param instance .data array element to test.
     * @method expanded
     * @memberOf ModelGrid
     */
    ModelGrid.prototype.expanded = function(instance) {
        var delegate = this.unwrap();
        var value = delegate.expanded(B.boxAsJava(instance));
        return B.boxAsJs(value);
    };

    /**
     * Makes node of specified .data array element collapsed.
     * @param instance .data array element to collapsed.
     * @method collapse
     * @memberOf ModelGrid
     */
    ModelGrid.prototype.collapse = function(instance) {
        var delegate = this.unwrap();
        var value = delegate.collapse(B.boxAsJava(instance));
        return B.boxAsJs(value);
    };

    /**
     * Makes node of specified .data array element expanded if it was already collapsed and collapsed otherwise.
     * @param instance .data array element to expand or collpase.
     * @method toggle
     * @memberOf ModelGrid
     */
    ModelGrid.prototype.toggle = function(instance) {
        var delegate = this.unwrap();
        var value = delegate.toggle(B.boxAsJava(instance));
        return B.boxAsJs(value);
    };

    /**
     * @method unsort
     * @memberOf ModelGrid
     * Clears sort on all columns, works only in HTML5 */
    ModelGrid.prototype.unsort = function() {
        var delegate = this.unwrap();
        var value = delegate.unsort();
        return B.boxAsJs(value);
    };

    /**
     *
     * @method try2StopAnyEditing
     * @memberOf ModelGrid
     */
    ModelGrid.prototype.try2StopAnyEditing = function() {
        var delegate = this.unwrap();
        var value = delegate.try2StopAnyEditing();
        return B.boxAsJs(value);
    };

    /**
     *
     * @method try2CancelAnyEditing
     * @memberOf ModelGrid
     */
    ModelGrid.prototype.try2CancelAnyEditing = function() {
        var delegate = this.unwrap();
        var value = delegate.try2CancelAnyEditing();
        return B.boxAsJs(value);
    };

    /**
     *
     * @method addColumnNode
     * @memberOf ModelGrid
     */
    ModelGrid.prototype.addColumnNode = function(arg0) {
        var delegate = this.unwrap();
        var value = delegate.addColumnNode(B.boxAsJava(arg0));
        return B.boxAsJs(value);
    };

    /**
     *
     * @method columnNodes
     * @memberOf ModelGrid
     */
    ModelGrid.prototype.columnNodes = function() {
        var delegate = this.unwrap();
        var value = delegate.columnNodes();
        return B.boxAsJs(value);
    };

    /**
     *
     * @method removeColumnNode
     * @memberOf ModelGrid
     */
    ModelGrid.prototype.removeColumnNode = function(arg0) {
        var delegate = this.unwrap();
        var value = delegate.removeColumnNode(B.boxAsJava(arg0));
        return B.boxAsJs(value);
    };

    /**
     *
     * @method insertColumnNode
     * @memberOf ModelGrid
     */
    ModelGrid.prototype.insertColumnNode = function(arg0, arg1) {
        var delegate = this.unwrap();
        var value = delegate.insertColumnNode(B.boxAsJava(arg0), B.boxAsJava(arg1));
        return B.boxAsJs(value);
    };

    /**
     * Makes specified instance visible.
     * @param instance Entity's instance to make visible.
     * @param need2select true to select the instance (optional).
     * @method makeVisible
     * @memberOf ModelGrid
     */
    ModelGrid.prototype.makeVisible = function(instance, need2select) {
        var delegate = this.unwrap();
        var value = delegate.makeVisible(B.boxAsJava(instance), B.boxAsJava(need2select));
        return B.boxAsJs(value);
    };


    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var space = ScriptsClass.getSpace();
    space.putPublisher(className, function(aDelegate) {
        return new ModelGrid(aDelegate);
    });
    return ModelGrid;
});