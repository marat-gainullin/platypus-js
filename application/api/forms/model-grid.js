/* global Java */

define(['boxing'], function(P) {
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
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.foreground = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "childrenField", {
            get: function() {
                var value = delegate.childrenField;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.childrenField = P.boxAsJava(aValue);
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
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.insertable = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "frozenColumns", {
            get: function() {
                var value = delegate.frozenColumns;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.frozenColumns = P.boxAsJava(aValue);
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
                return P.boxAsJs(value);
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
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.toolTipText = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "height", {
            get: function() {
                var value = delegate.height;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.height = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
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
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.visible = P.boxAsJava(aValue);
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

        Object.defineProperty(this, "onKeyReleased", {
            get: function() {
                var value = delegate.onKeyReleased;
                return value;
            },
            set: function(aValue) {
                delegate.onKeyReleased = aValue;
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

        Object.defineProperty(this, "focusable", {
            get: function() {
                var value = delegate.focusable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.focusable = P.boxAsJava(aValue);
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
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.cursorProperty = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "field", {
            get: function() {
                var value = delegate.field;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.field = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "left", {
            get: function() {
                var value = delegate.left;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.left = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "background", {
            get: function() {
                var value = delegate.background;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.background = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.name = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "showVerticalLines", {
            get: function() {
                var value = delegate.showVerticalLines;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.showVerticalLines = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "frozenRows", {
            get: function() {
                var value = delegate.frozenRows;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.frozenRows = P.boxAsJava(aValue);
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

        Object.defineProperty(this, "onFocusLost", {
            get: function() {
                var value = delegate.onFocusLost;
                return value;
            },
            set: function(aValue) {
                delegate.onFocusLost = aValue;
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
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.deletable = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "error", {
            get: function() {
                var value = delegate.error;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.error = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "enabled", {
            get: function() {
                var value = delegate.enabled;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.enabled = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "componentPopupMenu", {
            get: function() {
                var value = delegate.componentPopupMenu;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.componentPopupMenu = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "top", {
            get: function() {
                var value = delegate.top;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.top = P.boxAsJava(aValue);
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
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.oddRowsColor = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "headerVisible", {
            get: function() {
                var value = delegate.headerVisible;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.headerVisible = P.boxAsJava(aValue);
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
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.opaque = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "editable", {
            get: function() {
                var value = delegate.editable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.editable = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "nextFocusableComponent", {
            get: function() {
                var value = delegate.nextFocusableComponent;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.nextFocusableComponent = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "parentField", {
            get: function() {
                var value = delegate.parentField;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.parentField = P.boxAsJava(aValue);
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
                return P.boxAsJs(value);
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
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.rowsHeight = P.boxAsJava(aValue);
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
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.width = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "gridColor", {
            get: function() {
                var value = delegate.gridColor;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.gridColor = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "showHorizontalLines", {
            get: function() {
                var value = delegate.showHorizontalLines;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.showHorizontalLines = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "showOddRowsInOtherColor", {
            get: function() {
                var value = delegate.showOddRowsInOtherColor;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.showOddRowsInOtherColor = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "draggableRows", {
            get: function() {
                var value = delegate.draggableRows;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.draggableRows = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "font", {
            get: function() {
                var value = delegate.font;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.font = P.boxAsJava(aValue);
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
        P.ModelGrid.prototype.find = function() {
            var delegate = this.unwrap();
            var value = delegate.find();
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
         * @method columnNodes
         * @memberOf ModelGrid
         */
        P.ModelGrid.prototype.columnNodes = function() {
            var delegate = this.unwrap();
            var value = delegate.columnNodes();
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
         * @method unsort
         * @memberOf ModelGrid
         * Clears sort on all columns, works only in HTML5 */
        P.ModelGrid.prototype.unsort = function() {
            var delegate = this.unwrap();
            var value = delegate.unsort();
            return P.boxAsJs(value);
        };


    var className = "com.eas.client.forms.components.model.grid.ModelGrid";
    var javaClass = Java.type(className);
    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var space = ScriptsClass.getSpace();
    space.putPublisher(className, function(aDelegate) {
        return new ModelGrid(aDelegate);
    });
    return ModelGrid;
});