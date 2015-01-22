(function() {
    var javaClass = Java.type("com.eas.client.forms.components.model.grid.header.CheckGridColumn");
    javaClass.setPublisher(function(aDelegate) {
        return new P.CheckGridColumn(aDelegate);
    });
    
    /**
     *
     * @constructor CheckGridColumn CheckGridColumn
     */
    P.CheckGridColumn = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.CheckGridColumn.superclass)
            P.CheckGridColumn.superclass.constructor.apply(this, arguments);
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
        if(!P.CheckGridColumn){
            /**
             * Generated property jsDoc.
             * @property preferredWidth
             * @memberOf CheckGridColumn
             */
            P.CheckGridColumn.prototype.preferredWidth = 0;
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
        if(!P.CheckGridColumn){
            /**
             * Generated property jsDoc.
             * @property visible
             * @memberOf CheckGridColumn
             */
            P.CheckGridColumn.prototype.visible = true;
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
        if(!P.CheckGridColumn){
            /**
             * Generated property jsDoc.
             * @property resizable
             * @memberOf CheckGridColumn
             */
            P.CheckGridColumn.prototype.resizable = true;
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
        if(!P.CheckGridColumn){
            /**
             * Generated property jsDoc.
             * @property minWidth
             * @memberOf CheckGridColumn
             */
            P.CheckGridColumn.prototype.minWidth = 0;
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
        if(!P.CheckGridColumn){
            /**
             * Generated property jsDoc.
             * @property foreground
             * @memberOf CheckGridColumn
             */
            P.CheckGridColumn.prototype.foreground = {};
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
        if(!P.CheckGridColumn){
            /**
             * Generated property jsDoc.
             * @property sortable
             * @memberOf CheckGridColumn
             */
            P.CheckGridColumn.prototype.sortable = true;
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
        if(!P.CheckGridColumn){
            /**
             * Generated property jsDoc.
             * @property title
             * @memberOf CheckGridColumn
             */
            P.CheckGridColumn.prototype.title = '';
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
        if(!P.CheckGridColumn){
            /**
             * Generated property jsDoc.
             * @property movable
             * @memberOf CheckGridColumn
             */
            P.CheckGridColumn.prototype.movable = true;
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
        if(!P.CheckGridColumn){
            /**
             * Generated property jsDoc.
             * @property readonly
             * @memberOf CheckGridColumn
             */
            P.CheckGridColumn.prototype.readonly = true;
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
        if(!P.CheckGridColumn){
            /**
             * Generated property jsDoc.
             * @property background
             * @memberOf CheckGridColumn
             */
            P.CheckGridColumn.prototype.background = {};
        }
        Object.defineProperty(this, "width", {
            get: function() {
                var value = delegate.width;
                return P.boxAsJs(value);
            }
        });
        if(!P.CheckGridColumn){
            /**
             * Generated property jsDoc.
             * @property width
             * @memberOf CheckGridColumn
             */
            P.CheckGridColumn.prototype.width = 0;
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
        if(!P.CheckGridColumn){
            /**
             * Generated property jsDoc.
             * @property font
             * @memberOf CheckGridColumn
             */
            P.CheckGridColumn.prototype.font = {};
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
        if(!P.CheckGridColumn){
            /**
             * Generated property jsDoc.
             * @property maxWidth
             * @memberOf CheckGridColumn
             */
            P.CheckGridColumn.prototype.maxWidth = 0;
        }
    };
})();