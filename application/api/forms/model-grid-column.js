/* global Java */

define(['boxing', 'common-utils/color', 'common-utils/cursor', 'common-utils/font', './cell-render-event'], function(B, Color, Cursor, Font, RenderEvent) {
    var className = "com.eas.client.forms.components.model.grid.header.ModelGridColumn";
    var javaClass = Java.type(className);
    /**
     *
     * @constructor ModelGridColumn ModelGridColumn
     */
    function ModelGridColumn() {
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
        if(ModelGridColumn.superclass)
            ModelGridColumn.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        this.preferredWidth = 0;
        Object.defineProperty(this, "preferredWidth", {
            get: function() {
                var value = delegate.preferredWidth;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.preferredWidth = B.boxAsJava(aValue);
            }
        });

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

        this.resizable = true;
        Object.defineProperty(this, "resizable", {
            get: function() {
                var value = delegate.resizable;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.resizable = B.boxAsJava(aValue);
            }
        });

        this.minWidth = 0;
        Object.defineProperty(this, "minWidth", {
            get: function() {
                var value = delegate.minWidth;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.minWidth = B.boxAsJava(aValue);
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

        this.sortable = true;
        Object.defineProperty(this, "sortable", {
            get: function() {
                var value = delegate.sortable;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.sortable = B.boxAsJava(aValue);
            }
        });

        this.title = '';
        Object.defineProperty(this, "title", {
            get: function() {
                var value = delegate.title;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.title = B.boxAsJava(aValue);
            }
        });

        /**
         * Returns script handler, used for select a value of the cell.
         */
        this.onSelect = new Object();
        Object.defineProperty(this, "onSelect", {
            get: function() {
                var value = delegate.onSelect;
                return value;
            },
            set: function(aValue) {
                delegate.onSelect = aValue;
            }
        });

        this.movable = true;
        Object.defineProperty(this, "movable", {
            get: function() {
                var value = delegate.movable;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.movable = B.boxAsJava(aValue);
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

        this.readonly = true;
        Object.defineProperty(this, "readonly", {
            get: function() {
                var value = delegate.readonly;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.readonly = B.boxAsJava(aValue);
            }
        });

        /**
         * Returns script handler, used for calculate cell's data, display value and style attributes.
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

        this.width = 0;
        Object.defineProperty(this, "width", {
            get: function() {
                var value = delegate.width;
                return B.boxAsJs(value);
            }
        });

        this.sortField = '';
        Object.defineProperty(this, "sortField", {
            get: function() {
                var value = delegate.sortField;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.sortField = B.boxAsJava(aValue);
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

        this.maxWidth = 0;
        Object.defineProperty(this, "maxWidth", {
            get: function() {
                var value = delegate.maxWidth;
                return B.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.maxWidth = B.boxAsJava(aValue);
            }
        });

    }
    /**
     * Column sort, works only in HTML5
     * @method sort
     * @memberOf ModelGridColumn
     */
    ModelGridColumn.prototype.sort = function() {
        var delegate = this.unwrap();
        var value = delegate.sort();
        return B.boxAsJs(value);
    };

    /**
     * Clears sort column, works only in HTML5
     * @method unsort
     * @memberOf ModelGridColumn
     */
    ModelGridColumn.prototype.unsort = function() {
        var delegate = this.unwrap();
        var value = delegate.unsort();
        return B.boxAsJs(value);
    };

    /**
     *
     * @method addColumnNode
     * @memberOf ModelGridColumn
     */
    ModelGridColumn.prototype.addColumnNode = function(node) {
        var delegate = this.unwrap();
        var value = delegate.addColumnNode(B.boxAsJava(node));
        return B.boxAsJs(value);
    };

    /**
     *
     * @method columnNodes
     * @memberOf ModelGridColumn
     */
    ModelGridColumn.prototype.columnNodes = function() {
        var delegate = this.unwrap();
        var value = delegate.columnNodes();
        return B.boxAsJs(value);
    };

    /**
     *
     * @method removeColumnNode
     * @memberOf ModelGridColumn
     */
    ModelGridColumn.prototype.removeColumnNode = function(node) {
        var delegate = this.unwrap();
        var value = delegate.removeColumnNode(B.boxAsJava(node));
        return B.boxAsJs(value);
    };

    /**
     *
     * @method insertColumnNode
     * @memberOf ModelGridColumn
     */
    ModelGridColumn.prototype.insertColumnNode = function(position, node) {
        var delegate = this.unwrap();
        var value = delegate.insertColumnNode(B.boxAsJava(position), B.boxAsJava(node));
        return B.boxAsJs(value);
    };

    /**
     * Descending column sort, works only in HTML5
     * @method sortDesc
     * @memberOf ModelGridColumn
     */
    ModelGridColumn.prototype.sortDesc = function() {
        var delegate = this.unwrap();
        var value = delegate.sortDesc();
        return B.boxAsJs(value);
    };


    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var space = ScriptsClass.getSpace();
    space.putPublisher(className, function(aDelegate) {
        return new ModelGridColumn(aDelegate);
    });
    return ModelGridColumn;
});