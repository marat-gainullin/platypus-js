(function() {
    var javaClass = Java.type("com.eas.client.forms.components.model.grid.columns.ModelColumn");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ModelColumn(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor ModelColumn ModelColumn
     */
    P.ModelColumn = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.ModelColumn.superclass)
            P.ModelColumn.superclass.constructor.apply(this, arguments);
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
        if(!P.ModelColumn){
            /**
             * Determines if column is visible.
             * @property visible
             * @memberOf ModelColumn
             */
            P.ModelColumn.prototype.visible = true;
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
        if(!P.ModelColumn){
            /**
             * Determines if column is readonly.
             * @property readonly
             * @memberOf ModelColumn
             */
            P.ModelColumn.prototype.readonly = true;
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
        if(!P.ModelColumn){
            /**
             * On render column event.
             * @property onRender
             * @memberOf ModelColumn
             */
            P.ModelColumn.prototype.onRender = {};
        }
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.ModelColumn){
            /**
             * The name of the column.
             * @property name
             * @memberOf ModelColumn
             */
            P.ModelColumn.prototype.name = '';
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
        if(!P.ModelColumn){
            /**
             * Width of the column.
             * @property width
             * @memberOf ModelColumn
             */
            P.ModelColumn.prototype.width = 0;
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
        if(!P.ModelColumn){
            /**
             * Determines if column is sortable.
             * @property sortable
             * @memberOf ModelColumn
             */
            P.ModelColumn.prototype.sortable = true;
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
        if(!P.ModelColumn){
            /**
             * Determines if column is resizeable.
             * @property resizeable
             * @memberOf ModelColumn
             */
            P.ModelColumn.prototype.resizeable = true;
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
        if(!P.ModelColumn){
            /**
             * The title of the column.
             * @property title
             * @memberOf ModelColumn
             */
            P.ModelColumn.prototype.title = '';
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
        if(!P.ModelColumn){
            /**
             * On select column event.
             * @property onSelect
             * @memberOf ModelColumn
             */
            P.ModelColumn.prototype.onSelect = {};
        }
    };
})();