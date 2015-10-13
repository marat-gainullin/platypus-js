/* global Java */

define(['boxing', 'common-utils/color', 'common-utils/cursor', 'common-utils/font', './action-event', './cell-render-event', './component-event', './focus-event', './item-event', './key-event', './value-change-event', './popup-menu', 'grid/cell-data', './cell-render-event', './item-event', './service-grid-column', './check-grid-column', './radio-grid-column', './model-check-box', './model-combo', './model-date', './model-formatted-field', './model-grid-column', './model-spin', './model-text-area'], function(B, Color, Cursor, Font, ActionEvent, RenderEvent, ComponentEvent, FocusEvent, ItemEvent, KeyEvent, ValueChangeEvent, PopupMenu, CellData, RenderEvent, ItemEvent, ServiceGridColumn, CheckGridColumn, RadioGridColumn, ModelCheckBox, ModelCombo, ModelDate, ModelFormattedField, ModelGridColumn, ModelSpin, ModelTextArea) {
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
        Object.defineProperty(this, "onMouseReleased", {
            get: function() {
                var value = delegate.onMouseReleased;
                return value;
            },
            set: function(aValue) {
                delegate.onMouseReleased = aValue;
            }
        });

        Object.defineProperty(this, "selected", {
            get: function() {
                var value = delegate.jsSelected;
                return value;
            }
        });

        Object.defineProperty(this, "data", {
            get: function() {
                var value = delegate.data;
                return value;
            },
            set: function(aValue) {
                delegate.data = aValue;
            }
        });

        Object.defineProperty(this, "foreground", {
            get: function() {
                var value = delegate.foreground;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.foreground = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "childrenField", {
            get: function() {
                var value = delegate.childrenField;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.childrenField = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "onComponentMoved", {
            get: function() {
                var value = delegate.onComponentMoved;
                return value;
            },
            set: function(aValue) {
                delegate.onComponentMoved = aValue;
            }
        });

        Object.defineProperty(this, "insertable", {
            get: function() {
                var value = delegate.insertable;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.insertable = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "frozenColumns", {
            get: function() {
                var value = delegate.frozenColumns;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.frozenColumns = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "onRender", {
            get: function() {
                var value = delegate.onRender;
                return value;
            },
            set: function(aValue) {
                delegate.onRender = aValue;
            }
        });

        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parentWidget;
                return B.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "onMouseEntered", {
            get: function() {
                var value = delegate.onMouseEntered;
                return value;
            },
            set: function(aValue) {
                delegate.onMouseEntered = aValue;
            }
        });

        Object.defineProperty(this, "toolTipText", {
            get: function() {
                var value = delegate.toolTipText;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.toolTipText = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "height", {
            get: function() {
                var value = delegate.height;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.height = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return B.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "onComponentShown", {
            get: function() {
                var value = delegate.onComponentShown;
                return value;
            },
            set: function(aValue) {
                delegate.onComponentShown = aValue;
            }
        });

        Object.defineProperty(this, "visible", {
            get: function() {
                var value = delegate.visible;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.visible = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "onComponentHidden", {
            get: function() {
                var value = delegate.onComponentHidden;
                return value;
            },
            set: function(aValue) {
                delegate.onComponentHidden = aValue;
            }
        });

        Object.defineProperty(this, "onActionPerformed", {
            get: function() {
                var value = delegate.onActionPerformed;
                return value;
            },
            set: function(aValue) {
                delegate.onActionPerformed = aValue;
            }
        });

        Object.defineProperty(this, "onKeyReleased", {
            get: function() {
                var value = delegate.onKeyReleased;
                return value;
            },
            set: function(aValue) {
                delegate.onKeyReleased = aValue;
            }
        });

        Object.defineProperty(this, "focusable", {
            get: function() {
                var value = delegate.focusable;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.focusable = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "onKeyTyped", {
            get: function() {
                var value = delegate.onKeyTyped;
                return value;
            },
            set: function(aValue) {
                delegate.onKeyTyped = aValue;
            }
        });

        Object.defineProperty(this, "onMouseWheelMoved", {
            get: function() {
                var value = delegate.onMouseWheelMoved;
                return value;
            },
            set: function(aValue) {
                delegate.onMouseWheelMoved = aValue;
            }
        });

        Object.defineProperty(this, "cursorProperty", {
            get: function() {
                var value = delegate.cursorProperty;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.cursorProperty = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "field", {
            get: function() {
                var value = delegate.field;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.field = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "left", {
            get: function() {
                var value = delegate.left;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.left = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "background", {
            get: function() {
                var value = delegate.background;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.background = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.name = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "showVerticalLines", {
            get: function() {
                var value = delegate.showVerticalLines;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.showVerticalLines = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "frozenRows", {
            get: function() {
                var value = delegate.frozenRows;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.frozenRows = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "onMouseDragged", {
            get: function() {
                var value = delegate.onMouseDragged;
                return value;
            },
            set: function(aValue) {
                delegate.onMouseDragged = aValue;
            }
        });

        Object.defineProperty(this, "onCollapse", {
            get: function() {
                var value = delegate.onCollapse;
                return value;
            },
            set: function(aValue) {
                delegate.onCollapse = aValue;
            }
        });

        Object.defineProperty(this, "onExpand", {
            get: function() {
                var value = delegate.onExpand;
                return value;
            },
            set: function(aValue) {
                delegate.onExpand = aValue;
            }
        });

        Object.defineProperty(this, "onFocusLost", {
            get: function() {
                var value = delegate.onFocusLost;
                return value;
            },
            set: function(aValue) {
                delegate.onFocusLost = aValue;
            }
        });

        Object.defineProperty(this, "onMousePressed", {
            get: function() {
                var value = delegate.onMousePressed;
                return value;
            },
            set: function(aValue) {
                delegate.onMousePressed = aValue;
            }
        });

        Object.defineProperty(this, "deletable", {
            get: function() {
                var value = delegate.deletable;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.deletable = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "error", {
            get: function() {
                var value = delegate.error;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.error = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "enabled", {
            get: function() {
                var value = delegate.enabled;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.enabled = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "componentPopupMenu", {
            get: function() {
                var value = delegate.componentPopupMenu;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.componentPopupMenu = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "top", {
            get: function() {
                var value = delegate.top;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.top = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "onComponentResized", {
            get: function() {
                var value = delegate.onComponentResized;
                return value;
            },
            set: function(aValue) {
                delegate.onComponentResized = aValue;
            }
        });

        Object.defineProperty(this, "oddRowsColor", {
            get: function() {
                var value = delegate.oddRowsColor;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.oddRowsColor = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "headerVisible", {
            get: function() {
                var value = delegate.headerVisible;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.headerVisible = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "onMouseMoved", {
            get: function() {
                var value = delegate.onMouseMoved;
                return value;
            },
            set: function(aValue) {
                delegate.onMouseMoved = aValue;
            }
        });

        Object.defineProperty(this, "opaque", {
            get: function() {
                var value = delegate.opaque;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.opaque = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "editable", {
            get: function() {
                var value = delegate.editable;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.editable = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "nextFocusableComponent", {
            get: function() {
                var value = delegate.nextFocusableComponent;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.nextFocusableComponent = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "parentField", {
            get: function() {
                var value = delegate.parentField;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.parentField = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "onItemSelected", {
            get: function() {
                var value = delegate.onItemSelected;
                return value;
            },
            set: function(aValue) {
                delegate.onItemSelected = aValue;
            }
        });

        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return B.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "onFocusGained", {
            get: function() {
                var value = delegate.onFocusGained;
                return value;
            },
            set: function(aValue) {
                delegate.onFocusGained = aValue;
            }
        });

        Object.defineProperty(this, "onMouseClicked", {
            get: function() {
                var value = delegate.onMouseClicked;
                return value;
            },
            set: function(aValue) {
                delegate.onMouseClicked = aValue;
            }
        });

        Object.defineProperty(this, "rowsHeight", {
            get: function() {
                var value = delegate.rowsHeight;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.rowsHeight = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "onMouseExited", {
            get: function() {
                var value = delegate.onMouseExited;
                return value;
            },
            set: function(aValue) {
                delegate.onMouseExited = aValue;
            }
        });

        Object.defineProperty(this, "width", {
            get: function() {
                var value = delegate.width;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.width = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "gridColor", {
            get: function() {
                var value = delegate.gridColor;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.gridColor = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "showHorizontalLines", {
            get: function() {
                var value = delegate.showHorizontalLines;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.showHorizontalLines = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "showOddRowsInOtherColor", {
            get: function() {
                var value = delegate.showOddRowsInOtherColor;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.showOddRowsInOtherColor = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "draggableRows", {
            get: function() {
                var value = delegate.draggableRows;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.draggableRows = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "font", {
            get: function() {
                var value = delegate.font;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.font = B.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "onKeyPressed", {
            get: function() {
                var value = delegate.onKeyPressed;
                return value;
            },
            set: function(aValue) {
                delegate.onKeyPressed = aValue;
            }
        });

    };
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
     * @method unsort
     * @memberOf ModelGrid
     * Clears sort on all columns, works only in HTML5 */
    ModelGrid.prototype.unsort = function() {
        var delegate = this.unwrap();
        var value = delegate.unsort();
        return B.boxAsJs(value);
    };


    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var space = ScriptsClass.getSpace();
    space.putPublisher(className, function(aDelegate) {
        return new ModelGrid(aDelegate);
    });
    return ModelGrid;
});