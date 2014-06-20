(function() {
    var javaClass = Java.type("com.eas.dbcontrols.grid.rt.columns.ScriptableColumn");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ScriptableColumn(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor ScriptableColumn ScriptableColumn
     */
    P.ScriptableColumn = function ScriptableColumn() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(ScriptableColumn.superclass)
            ScriptableColumn.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "ScriptableColumn", {value: ScriptableColumn});
    Object.defineProperty(ScriptableColumn.prototype, "visible", {
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
    if(!ScriptableColumn){
        /**
         * Determines if column is visible.
         * @property visible
         * @memberOf ScriptableColumn
         */
        P.ScriptableColumn.prototype.visible = true;
    }
    Object.defineProperty(ScriptableColumn.prototype, "readonly", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.readonly;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.readonly = P.boxAsJava(aValue);
        }
    });
    if(!ScriptableColumn){
        /**
         * Determines if column is readonly.
         * @property readonly
         * @memberOf ScriptableColumn
         */
        P.ScriptableColumn.prototype.readonly = true;
    }
    Object.defineProperty(ScriptableColumn.prototype, "onRender", {
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
    if(!ScriptableColumn){
        /**
         * On render column event.
         * @property onRender
         * @memberOf ScriptableColumn
         */
        P.ScriptableColumn.prototype.onRender = {};
    }
    Object.defineProperty(ScriptableColumn.prototype, "name", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.name;
            return P.boxAsJs(value);
        }
    });
    if(!ScriptableColumn){
        /**
         * The name of the column.
         * @property name
         * @memberOf ScriptableColumn
         */
        P.ScriptableColumn.prototype.name = '';
    }
    Object.defineProperty(ScriptableColumn.prototype, "width", {
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
    if(!ScriptableColumn){
        /**
         * Width of the column.
         * @property width
         * @memberOf ScriptableColumn
         */
        P.ScriptableColumn.prototype.width = 0;
    }
    Object.defineProperty(ScriptableColumn.prototype, "resizeable", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.resizeable;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.resizeable = P.boxAsJava(aValue);
        }
    });
    if(!ScriptableColumn){
        /**
         * Determines if column is resizeable.
         * @property resizeable
         * @memberOf ScriptableColumn
         */
        P.ScriptableColumn.prototype.resizeable = true;
    }
    Object.defineProperty(ScriptableColumn.prototype, "sortable", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.sortable;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.sortable = P.boxAsJava(aValue);
        }
    });
    if(!ScriptableColumn){
        /**
         * Determines if column is sortable.
         * @property sortable
         * @memberOf ScriptableColumn
         */
        P.ScriptableColumn.prototype.sortable = true;
    }
    Object.defineProperty(ScriptableColumn.prototype, "title", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.title;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.title = P.boxAsJava(aValue);
        }
    });
    if(!ScriptableColumn){
        /**
         * The title of the column.
         * @property title
         * @memberOf ScriptableColumn
         */
        P.ScriptableColumn.prototype.title = '';
    }
    Object.defineProperty(ScriptableColumn.prototype, "onSelect", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.onSelect;
            return P.boxAsJs(value);
        },
        set: function(aValue) {
            var delegate = this.unwrap();
            delegate.onSelect = P.boxAsJava(aValue);
        }
    });
    if(!ScriptableColumn){
        /**
         * On select column event.
         * @property onSelect
         * @memberOf ScriptableColumn
         */
        P.ScriptableColumn.prototype.onSelect = {};
    }
})();