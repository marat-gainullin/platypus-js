(function() {
    var javaClass = Java.type("com.eas.client.forms.components.model.grid.header.ModelGridColumn");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ModelGridColumn(aDelegate);
    });
    
    /**
     *
     * @constructor ModelGridColumn ModelGridColumn
     */
    P.ModelGridColumn = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.ModelGridColumn.superclass)
            P.ModelGridColumn.superclass.constructor.apply(this, arguments);
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
        if(!P.ModelGridColumn){
            /**
             * Generated property jsDoc.
             * @property preferredWidth
             * @memberOf ModelGridColumn
             */
            P.ModelGridColumn.prototype.preferredWidth = 0;
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
        if(!P.ModelGridColumn){
            /**
             * Generated property jsDoc.
             * @property visible
             * @memberOf ModelGridColumn
             */
            P.ModelGridColumn.prototype.visible = true;
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
        if(!P.ModelGridColumn){
            /**
             * Generated property jsDoc.
             * @property resizable
             * @memberOf ModelGridColumn
             */
            P.ModelGridColumn.prototype.resizable = true;
        }
        Object.defineProperty(this, "editable", {
            get: function() {
                var value = delegate.editable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.editable = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelGridColumn){
            /**
             * Generated property jsDoc.
             * @property editable
             * @memberOf ModelGridColumn
             */
            P.ModelGridColumn.prototype.editable = true;
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
        if(!P.ModelGridColumn){
            /**
             * Generated property jsDoc.
             * @property minWidth
             * @memberOf ModelGridColumn
             */
            P.ModelGridColumn.prototype.minWidth = 0;
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
        if(!P.ModelGridColumn){
            /**
             * Generated property jsDoc.
             * @property foreground
             * @memberOf ModelGridColumn
             */
            P.ModelGridColumn.prototype.foreground = {};
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
        if(!P.ModelGridColumn){
            /**
             * Generated property jsDoc.
             * @property sortable
             * @memberOf ModelGridColumn
             */
            P.ModelGridColumn.prototype.sortable = true;
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
        if(!P.ModelGridColumn){
            /**
             * Generated property jsDoc.
             * @property title
             * @memberOf ModelGridColumn
             */
            P.ModelGridColumn.prototype.title = '';
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
        if(!P.ModelGridColumn){
            /**
             * Generated property jsDoc.
             * @property movable
             * @memberOf ModelGridColumn
             */
            P.ModelGridColumn.prototype.movable = true;
        }
        Object.defineProperty(this, "field", {
            get: function() {
                var value = delegate.field;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.field = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelGridColumn){
            /**
             * Generated property jsDoc.
             * @property field
             * @memberOf ModelGridColumn
             */
            P.ModelGridColumn.prototype.field = '';
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
        if(!P.ModelGridColumn){
            /**
             * Generated property jsDoc.
             * @property background
             * @memberOf ModelGridColumn
             */
            P.ModelGridColumn.prototype.background = {};
        }
        Object.defineProperty(this, "width", {
            get: function() {
                var value = delegate.width;
                return P.boxAsJs(value);
            }
        });
        if(!P.ModelGridColumn){
            /**
             * Generated property jsDoc.
             * @property width
             * @memberOf ModelGridColumn
             */
            P.ModelGridColumn.prototype.width = 0;
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
        if(!P.ModelGridColumn){
            /**
             * Generated property jsDoc.
             * @property font
             * @memberOf ModelGridColumn
             */
            P.ModelGridColumn.prototype.font = {};
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
        if(!P.ModelGridColumn){
            /**
             * Generated property jsDoc.
             * @property maxWidth
             * @memberOf ModelGridColumn
             */
            P.ModelGridColumn.prototype.maxWidth = 0;
        }
    };
})();