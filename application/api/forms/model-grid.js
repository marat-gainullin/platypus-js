(function() {
    var javaClass = Java.type("com.eas.client.forms.api.components.model.ModelGrid");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ModelGrid(aDelegate);
    });
    
    /**
     * A model component that shows a data grid.
     * @constructor ModelGrid ModelGrid
     */
    P.ModelGrid = function () {

        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            get: function() {
                return function() {
                    return delegate;
                };
            }
        });
        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parent;
                return P.boxAsJs(value);
            }
        });

        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "onMouseReleased", {
            get: function() {
                var value = delegate.onMouseReleased;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseReleased = P.boxAsJava(aValue);
            }
        });

        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "foreground", {
            get: function() {
                var value = delegate.foreground;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.foreground = P.boxAsJava(aValue);
            }
        });

        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "onComponentMoved", {
            get: function() {
                var value = delegate.onComponentMoved;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onComponentMoved = P.boxAsJava(aValue);
            }
        });

        /**
         * Determines if grid allows row insertion.
         * @property insertable
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "insertable", {
            get: function() {
                var value = delegate.insertable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.insertable = P.boxAsJava(aValue);
            }
        });

        /**
         * General render event handler.
         * This hanler be called on each cell's rendering in the case when no render handler is provided for the conrete column.
         * @property onRender
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "onRender", {
            get: function() {
                var value = delegate.onRender;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onRender = P.boxAsJava(aValue);
            }
        });

        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "onMouseEntered", {
            get: function() {
                var value = delegate.onMouseEntered;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseEntered = P.boxAsJava(aValue);
            }
        });

        /**
         *  Gets the array of selected rows.
         * @property selected
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "selected", {
            get: function() {
                var value = delegate.selected;
                return P.boxAsJs(value);
            }
        });

        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "toolTipText", {
            get: function() {
                var value = delegate.toolTipText;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.toolTipText = P.boxAsJava(aValue);
            }
        });

        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });

        /**
         * Height of the component.
         * @property height
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "height", {
            get: function() {
                var value = delegate.height;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.height = P.boxAsJava(aValue);
            }
        });

        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "onComponentShown", {
            get: function() {
                var value = delegate.onComponentShown;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onComponentShown = P.boxAsJava(aValue);
            }
        });

        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "visible", {
            get: function() {
                var value = delegate.visible;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.visible = P.boxAsJava(aValue);
            }
        });

        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "onComponentHidden", {
            get: function() {
                var value = delegate.onComponentHidden;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onComponentHidden = P.boxAsJava(aValue);
            }
        });

        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "onActionPerformed", {
            get: function() {
                var value = delegate.onActionPerformed;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onActionPerformed = P.boxAsJava(aValue);
            }
        });

        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "onKeyReleased", {
            get: function() {
                var value = delegate.onKeyReleased;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onKeyReleased = P.boxAsJava(aValue);
            }
        });

        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "focusable", {
            get: function() {
                var value = delegate.focusable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.focusable = P.boxAsJava(aValue);
            }
        });

        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "onKeyTyped", {
            get: function() {
                var value = delegate.onKeyTyped;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onKeyTyped = P.boxAsJava(aValue);
            }
        });

        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "onMouseWheelMoved", {
            get: function() {
                var value = delegate.onMouseWheelMoved;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseWheelMoved = P.boxAsJava(aValue);
            }
        });

        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "left", {
            get: function() {
                var value = delegate.left;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.left = P.boxAsJava(aValue);
            }
        });

        /**
         * Gets all grid selected cells as an array.
         * <b>WARNING!!! All selected cells will be copied.</b>
         * @property selectedCells
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "selectedCells", {
            get: function() {
                var value = delegate.selectedCells;
                return P.boxAsJs(value);
            }
        });

        /**
         * The background color of this component.
         * @property background
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "background", {
            get: function() {
                var value = delegate.background;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.background = P.boxAsJava(aValue);
            }
        });

        /**
         * Determines if grid shows vertical lines.
         * @property showVerticalLines
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "showVerticalLines", {
            get: function() {
                var value = delegate.showVerticalLines;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.showVerticalLines = P.boxAsJava(aValue);
            }
        });

        /**
         * Gets name of this component.
         * @property name
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });

        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "cursor", {
            get: function() {
                var value = delegate.cursor;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.cursor = P.boxAsJava(aValue);
            }
        });

        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "onMouseDragged", {
            get: function() {
                var value = delegate.onMouseDragged;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseDragged = P.boxAsJava(aValue);
            }
        });

        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "onFocusLost", {
            get: function() {
                var value = delegate.onFocusLost;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onFocusLost = P.boxAsJava(aValue);
            }
        });

        /**
         * Gets grid columns as an array.
         * @property columns
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "columns", {
            get: function() {
                var value = delegate.columns;
                return P.boxAsJs(value);
            }
        });

        /**
         * Determines if grid allows to delete rows.
         * @property deletable
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "deletable", {
            get: function() {
                var value = delegate.deletable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.deletable = P.boxAsJava(aValue);
            }
        });

        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "onMousePressed", {
            get: function() {
                var value = delegate.onMousePressed;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMousePressed = P.boxAsJava(aValue);
            }
        });

        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "error", {
            get: function() {
                var value = delegate.error;
                return P.boxAsJs(value);
            }
        });

        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "enabled", {
            get: function() {
                var value = delegate.enabled;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.enabled = P.boxAsJava(aValue);
            }
        });

        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "componentPopupMenu", {
            get: function() {
                var value = delegate.componentPopupMenu;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.componentPopupMenu = P.boxAsJava(aValue);
            }
        });

        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "top", {
            get: function() {
                var value = delegate.top;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.top = P.boxAsJava(aValue);
            }
        });

        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "onComponentResized", {
            get: function() {
                var value = delegate.onComponentResized;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onComponentResized = P.boxAsJava(aValue);
            }
        });

        /**
         * Odd rows color.
         * @property oddRowsColor
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "oddRowsColor", {
            get: function() {
                var value = delegate.oddRowsColor;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.oddRowsColor = P.boxAsJava(aValue);
            }
        });

        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "onMouseMoved", {
            get: function() {
                var value = delegate.onMouseMoved;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseMoved = P.boxAsJava(aValue);
            }
        });

        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "opaque", {
            get: function() {
                var value = delegate.opaque;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.opaque = P.boxAsJava(aValue);
            }
        });

        /**
         * Determines if gris cells are editable.
         * @property editable
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "editable", {
            get: function() {
                var value = delegate.editable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.editable = P.boxAsJava(aValue);
            }
        });

        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "nextFocusableComponent", {
            get: function() {
                var value = delegate.nextFocusableComponent;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.nextFocusableComponent = P.boxAsJava(aValue);
            }
        });

        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });

        /**
         * Gets all grid cells as an array.
         * <b>WARNING!!! All cells will be copied.</b>
         * @property cells
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "cells", {
            get: function() {
                var value = delegate.cells;
                return P.boxAsJs(value);
            }
        });

        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "onFocusGained", {
            get: function() {
                var value = delegate.onFocusGained;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onFocusGained = P.boxAsJava(aValue);
            }
        });

        /**
         * The height of grid's rows.
         * @property rowsHeight
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "rowsHeight", {
            get: function() {
                var value = delegate.rowsHeight;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.rowsHeight = P.boxAsJava(aValue);
            }
        });

        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "onMouseClicked", {
            get: function() {
                var value = delegate.onMouseClicked;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseClicked = P.boxAsJava(aValue);
            }
        });

        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "onMouseExited", {
            get: function() {
                var value = delegate.onMouseExited;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseExited = P.boxAsJava(aValue);
            }
        });

        /**
         * The color of the grid.
         * @property gridColor
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "gridColor", {
            get: function() {
                var value = delegate.gridColor;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.gridColor = P.boxAsJava(aValue);
            }
        });

        /**
         * Determines if grid shows horizontal lines.
         * @property showHorizontalLines
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "showHorizontalLines", {
            get: function() {
                var value = delegate.showHorizontalLines;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.showHorizontalLines = P.boxAsJava(aValue);
            }
        });

        /**
         * Width of the component.
         * @property width
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "width", {
            get: function() {
                var value = delegate.width;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.width = P.boxAsJava(aValue);
            }
        });

        /**
         * Determines if grid shows odd rows if other color.
         * @property showOddRowsInOtherColor
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "showOddRowsInOtherColor", {
            get: function() {
                var value = delegate.showOddRowsInOtherColor;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.showOddRowsInOtherColor = P.boxAsJava(aValue);
            }
        });

        /**
         * The font of this component.
         * @property font
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "font", {
            get: function() {
                var value = delegate.font;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.font = P.boxAsJava(aValue);
            }
        });

        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "onKeyPressed", {
            get: function() {
                var value = delegate.onKeyPressed;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onKeyPressed = P.boxAsJava(aValue);
            }
        });

        /**
         * Shows find dialog.
         * @method find
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "find", {
            get: function() {
                return function() {
                    var value = delegate.find();
                    return P.boxAsJs(value);
                };
            }
        });

        /**
         *  Gets the array of selected rows.
         * @method select
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "select", {
            get: function() {
                return function(arg0) {
                    var value = delegate.select(P.boxAsJava(arg0));
                    return P.boxAsJs(value);
                };
            }
        });

        /**
         * Clears current selection.
         * @method clearSelection
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "clearSelection", {
            get: function() {
                return function() {
                    var value = delegate.clearSelection();
                    return P.boxAsJs(value);
                };
            }
        });

        /**
         * Shows find dialog.
         * @deprecated Use find() instead.
         * @method findSomething
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "findSomething", {
            get: function() {
                return function() {
                    var value = delegate.findSomething();
                    return P.boxAsJs(value);
                };
            }
        });

        /**
         * Makes specified row visible.
         * @param row the row to make visible.
         * @param need2select true to select the row (optional).
         * @method makeVisible
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "makeVisible", {
            get: function() {
                return function(arg0, arg1) {
                    var value = delegate.makeVisible(P.boxAsJava(arg0), P.boxAsJava(arg1));
                    return P.boxAsJs(value);
                };
            }
        });

        /**
         * Unselects the specified row.
         * @param row the row to be unselected
         * @method unselect
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "unselect", {
            get: function() {
                return function(row) {
                    var value = delegate.unselect(P.boxAsJava(row));
                    return P.boxAsJs(value);
                };
            }
        });

        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf ModelGrid
         */
        Object.defineProperty(this, "focus", {
            get: function() {
                return function() {
                    var value = delegate.focus();
                    return P.boxAsJs(value);
                };
            }
        });


        delegate.setPublished(this);
    };
})();