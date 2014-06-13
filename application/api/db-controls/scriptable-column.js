(function() {
    var javaClass = Java.type("com.eas.dbcontrols.grid.rt.columns.ScriptableColumn");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ScriptableColumn(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor ScriptableColumn ScriptableColumn
     */
    P.ScriptableColumn = function () {

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
         * Determines if column is visible.
         * @property visible
         * @memberOf ScriptableColumn
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
         * Determines if column is readonly.
         * @property readonly
         * @memberOf ScriptableColumn
         */
        Object.defineProperty(this, "readonly", {
            get: function() {
                var value = delegate.readonly;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.readonly = P.boxAsJava(aValue);
            }
        });

        /**
         * On render column event.
         * @property onRender
         * @memberOf ScriptableColumn
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
         * The name of the column.
         * @property name
         * @memberOf ScriptableColumn
         */
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });

        /**
         * Width of the column.
         * @property width
         * @memberOf ScriptableColumn
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
         * Determines if column is resizeable.
         * @property resizeable
         * @memberOf ScriptableColumn
         */
        Object.defineProperty(this, "resizeable", {
            get: function() {
                var value = delegate.resizeable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.resizeable = P.boxAsJava(aValue);
            }
        });

        /**
         * Determines if column is sortable.
         * @property sortable
         * @memberOf ScriptableColumn
         */
        Object.defineProperty(this, "sortable", {
            get: function() {
                var value = delegate.sortable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.sortable = P.boxAsJava(aValue);
            }
        });

        /**
         * The title of the column.
         * @property title
         * @memberOf ScriptableColumn
         */
        Object.defineProperty(this, "title", {
            get: function() {
                var value = delegate.title;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.title = P.boxAsJava(aValue);
            }
        });

        /**
         * On select column event.
         * @property onSelect
         * @memberOf ScriptableColumn
         */
        Object.defineProperty(this, "onSelect", {
            get: function() {
                var value = delegate.onSelect;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onSelect = P.boxAsJava(aValue);
            }
        });


        delegate.setPublished(this);
    };
})();