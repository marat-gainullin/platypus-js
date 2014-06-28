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
            value: function() {
                return delegate;
            }
        });
        if(P.ScriptableColumn.superclass)
            P.ScriptableColumn.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "visible", {
            get: function() {
                var value = delegate.visible;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.visible = P.boxAsJava(aValue);
            }
        });
        if(!P.ScriptableColumn){
            /**
             * Determines if column is visible.
             * @property visible
             * @memberOf ScriptableColumn
             */
            P.ScriptableColumn.prototype.visible = true;
        }
        Object.defineProperty(this, "readonly", {
            get: function() {
                var value = delegate.readonly;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.readonly = P.boxAsJava(aValue);
            }
        });
        if(!P.ScriptableColumn){
            /**
             * Determines if column is readonly.
             * @property readonly
             * @memberOf ScriptableColumn
             */
            P.ScriptableColumn.prototype.readonly = true;
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
        if(!P.ScriptableColumn){
            /**
             * On render column event.
             * @property onRender
             * @memberOf ScriptableColumn
             */
            P.ScriptableColumn.prototype.onRender = {};
        }
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.ScriptableColumn){
            /**
             * The name of the column.
             * @property name
             * @memberOf ScriptableColumn
             */
            P.ScriptableColumn.prototype.name = '';
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
        if(!P.ScriptableColumn){
            /**
             * Width of the column.
             * @property width
             * @memberOf ScriptableColumn
             */
            P.ScriptableColumn.prototype.width = 0;
        }
        Object.defineProperty(this, "resizeable", {
            get: function() {
                var value = delegate.resizeable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.resizeable = P.boxAsJava(aValue);
            }
        });
        if(!P.ScriptableColumn){
            /**
             * Determines if column is resizeable.
             * @property resizeable
             * @memberOf ScriptableColumn
             */
            P.ScriptableColumn.prototype.resizeable = true;
        }
        Object.defineProperty(this, "sortable", {
            get: function() {
                var value = delegate.sortable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.sortable = P.boxAsJava(aValue);
            }
        });
        if(!P.ScriptableColumn){
            /**
             * Determines if column is sortable.
             * @property sortable
             * @memberOf ScriptableColumn
             */
            P.ScriptableColumn.prototype.sortable = true;
        }
        Object.defineProperty(this, "title", {
            get: function() {
                var value = delegate.title;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.title = P.boxAsJava(aValue);
            }
        });
        if(!P.ScriptableColumn){
            /**
             * The title of the column.
             * @property title
             * @memberOf ScriptableColumn
             */
            P.ScriptableColumn.prototype.title = '';
        }
        Object.defineProperty(this, "onSelect", {
            get: function() {
                var value = delegate.onSelect;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onSelect = P.boxAsJava(aValue);
            }
        });
        if(!P.ScriptableColumn){
            /**
             * On select column event.
             * @property onSelect
             * @memberOf ScriptableColumn
             */
            P.ScriptableColumn.prototype.onSelect = {};
        }
    };
})();