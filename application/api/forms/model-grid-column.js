/* global Java */

define(['boxing'], function(P) {
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
        Object.defineProperty(this, "preferredWidth", {
            get: function() {
                var value = delegate.preferredWidth;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.preferredWidth = P.boxAsJava(aValue);
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

        Object.defineProperty(this, "resizable", {
            get: function() {
                var value = delegate.resizable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.resizable = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "minWidth", {
            get: function() {
                var value = delegate.minWidth;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.minWidth = P.boxAsJava(aValue);
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

        Object.defineProperty(this, "sortable", {
            get: function() {
                var value = delegate.sortable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.sortable = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "title", {
            get: function() {
                var value = delegate.title;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.title = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "onSelect", {
            get: function() {
                var value = delegate.onSelect;
                return value;
            },
            set: function(aValue) {
                delegate.onSelect = aValue;
            }
        });

        Object.defineProperty(this, "movable", {
            get: function() {
                var value = delegate.movable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.movable = P.boxAsJava(aValue);
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

        Object.defineProperty(this, "readonly", {
            get: function() {
                var value = delegate.readonly;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.readonly = P.boxAsJava(aValue);
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

        Object.defineProperty(this, "background", {
            get: function() {
                var value = delegate.background;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.background = P.boxAsJava(aValue);
            }
        });

        Object.defineProperty(this, "width", {
            get: function() {
                var value = delegate.width;
                return P.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "sortField", {
            get: function() {
                var value = delegate.sortField;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.sortField = P.boxAsJava(aValue);
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

        Object.defineProperty(this, "maxWidth", {
            get: function() {
                var value = delegate.maxWidth;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.maxWidth = P.boxAsJava(aValue);
            }
        });

    };
    /**
     * @method sort
     * @memberOf ModelGridColumn
     * Column sort, works only in HTML5 */
    ModelGridColumn.prototype.sort = function() {
        var delegate = this.unwrap();
        var value = delegate.sort();
        return P.boxAsJs(value);
    };

    /**
     *
     * @method addColumnNode
     * @memberOf ModelGridColumn
     */
    ModelGridColumn.prototype.addColumnNode = function(node) {
        var delegate = this.unwrap();
        var value = delegate.addColumnNode(P.boxAsJava(node));
        return P.boxAsJs(value);
    };

    /**
     *
     * @method columnNodes
     * @memberOf ModelGridColumn
     */
    ModelGridColumn.prototype.columnNodes = function() {
        var delegate = this.unwrap();
        var value = delegate.columnNodes();
        return P.boxAsJs(value);
    };

    /**
     *
     * @method removeColumnNode
     * @memberOf ModelGridColumn
     */
    ModelGridColumn.prototype.removeColumnNode = function(node) {
        var delegate = this.unwrap();
        var value = delegate.removeColumnNode(P.boxAsJava(node));
        return P.boxAsJs(value);
    };

    /**
     *
     * @method insertColumnNode
     * @memberOf ModelGridColumn
     */
    ModelGridColumn.prototype.insertColumnNode = function(position, node) {
        var delegate = this.unwrap();
        var value = delegate.insertColumnNode(P.boxAsJava(position), P.boxAsJava(node));
        return P.boxAsJs(value);
    };

    /**
     * @method unsort
     * @memberOf ModelGridColumn
     * Clears sort column, works only in HTML5 */
    ModelGridColumn.prototype.unsort = function() {
        var delegate = this.unwrap();
        var value = delegate.unsort();
        return P.boxAsJs(value);
    };

    /**
     * @method sortDesc
     * @memberOf ModelGridColumn
     * Descending column sort, works only in HTML5 */
    ModelGridColumn.prototype.sortDesc = function() {
        var delegate = this.unwrap();
        var value = delegate.sortDesc();
        return P.boxAsJs(value);
    };


    var className = "com.eas.client.forms.components.model.grid.header.ModelGridColumn";
    var javaClass = Java.type(className);
    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var space = ScriptsClass.getSpace();
    space.putPublisher(className, function(aDelegate) {
        return new ModelGridColumn(aDelegate);
    });
    return ModelGridColumn;
});