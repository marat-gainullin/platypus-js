(function() {
    var javaClass = Java.type("com.eas.client.forms.components.model.grid.header.ServiceGridColumn");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ServiceGridColumn(aDelegate);
    });
    
    /**
     *
     * @constructor ServiceGridColumn ServiceGridColumn
     */
    P.ServiceGridColumn = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.ServiceGridColumn.superclass)
            P.ServiceGridColumn.superclass.constructor.apply(this, arguments);
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
        if(!P.ServiceGridColumn){
            /**
             * Generated property jsDoc.
             * @property preferredWidth
             * @memberOf ServiceGridColumn
             */
            P.ServiceGridColumn.prototype.preferredWidth = 0;
        }
        Object.defineProperty(this, "visible", {
            get: function() {
                var value = delegate.visible;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.visible = P.boxAsJava(aValue);
            }
        });
        if(!P.ServiceGridColumn){
            /**
             * Generated property jsDoc.
             * @property visible
             * @memberOf ServiceGridColumn
             */
            P.ServiceGridColumn.prototype.visible = true;
        }
        Object.defineProperty(this, "resizable", {
            get: function() {
                var value = delegate.resizable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.resizable = P.boxAsJava(aValue);
            }
        });
        if(!P.ServiceGridColumn){
            /**
             * Generated property jsDoc.
             * @property resizable
             * @memberOf ServiceGridColumn
             */
            P.ServiceGridColumn.prototype.resizable = true;
        }
        Object.defineProperty(this, "minWidth", {
            get: function() {
                var value = delegate.minWidth;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.minWidth = P.boxAsJava(aValue);
            }
        });
        if(!P.ServiceGridColumn){
            /**
             * Generated property jsDoc.
             * @property minWidth
             * @memberOf ServiceGridColumn
             */
            P.ServiceGridColumn.prototype.minWidth = 0;
        }
        Object.defineProperty(this, "foreground", {
            get: function() {
                var value = delegate.foreground;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.foreground = P.boxAsJava(aValue);
            }
        });
        if(!P.ServiceGridColumn){
            /**
             * Generated property jsDoc.
             * @property foreground
             * @memberOf ServiceGridColumn
             */
            P.ServiceGridColumn.prototype.foreground = {};
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
        if(!P.ServiceGridColumn){
            /**
             * Generated property jsDoc.
             * @property sortable
             * @memberOf ServiceGridColumn
             */
            P.ServiceGridColumn.prototype.sortable = true;
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
        if(!P.ServiceGridColumn){
            /**
             * Generated property jsDoc.
             * @property title
             * @memberOf ServiceGridColumn
             */
            P.ServiceGridColumn.prototype.title = '';
        }
        Object.defineProperty(this, "movable", {
            get: function() {
                var value = delegate.movable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.movable = P.boxAsJava(aValue);
            }
        });
        if(!P.ServiceGridColumn){
            /**
             * Generated property jsDoc.
             * @property movable
             * @memberOf ServiceGridColumn
             */
            P.ServiceGridColumn.prototype.movable = true;
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
        if(!P.ServiceGridColumn){
            /**
             * Generated property jsDoc.
             * @property readonly
             * @memberOf ServiceGridColumn
             */
            P.ServiceGridColumn.prototype.readonly = true;
        }
        Object.defineProperty(this, "background", {
            get: function() {
                var value = delegate.background;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.background = P.boxAsJava(aValue);
            }
        });
        if(!P.ServiceGridColumn){
            /**
             * Generated property jsDoc.
             * @property background
             * @memberOf ServiceGridColumn
             */
            P.ServiceGridColumn.prototype.background = {};
        }
        Object.defineProperty(this, "width", {
            get: function() {
                var value = delegate.width;
                return P.boxAsJs(value);
            }
        });
        if(!P.ServiceGridColumn){
            /**
             * Generated property jsDoc.
             * @property width
             * @memberOf ServiceGridColumn
             */
            P.ServiceGridColumn.prototype.width = 0;
        }
        Object.defineProperty(this, "font", {
            get: function() {
                var value = delegate.font;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.font = P.boxAsJava(aValue);
            }
        });
        if(!P.ServiceGridColumn){
            /**
             * Generated property jsDoc.
             * @property font
             * @memberOf ServiceGridColumn
             */
            P.ServiceGridColumn.prototype.font = {};
        }
        Object.defineProperty(this, "maxWidth", {
            get: function() {
                var value = delegate.maxWidth;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.maxWidth = P.boxAsJava(aValue);
            }
        });
        if(!P.ServiceGridColumn){
            /**
             * Generated property jsDoc.
             * @property maxWidth
             * @memberOf ServiceGridColumn
             */
            P.ServiceGridColumn.prototype.maxWidth = 0;
        }
    };
})();