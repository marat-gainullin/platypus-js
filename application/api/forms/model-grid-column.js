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
        Object.defineProperty(this, "onSelect", {
            get: function() {
                var value = delegate.onSelect;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onSelect = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelGridColumn){
            /**
             * Returns script handler, used for select a value of the cell.
             * @property onSelect
             * @memberOf ModelGridColumn
             */
            P.ModelGridColumn.prototype.onSelect = {};
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
        Object.defineProperty(this, "readonly", {
            get: function() {
                var value = delegate.readonly;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.readonly = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelGridColumn){
            /**
             * Generated property jsDoc.
             * @property readonly
             * @memberOf ModelGridColumn
             */
            P.ModelGridColumn.prototype.readonly = true;
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
        if(!P.ModelGridColumn){
            /**
             * Returns script handler, used for calculate cell's data, display value and style attributes.
             * @property onRender
             * @memberOf ModelGridColumn
             */
            P.ModelGridColumn.prototype.onRender = {};
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
        Object.defineProperty(this, "sortField", {
            get: function() {
                var value = delegate.sortField;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.sortField = P.boxAsJava(aValue);
            }
        });
        if(!P.ModelGridColumn){
            /**
             * Generated property jsDoc.
             * @property sortField
             * @memberOf ModelGridColumn
             */
            P.ModelGridColumn.prototype.sortField = '';
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
        /**
         * @method sort
         * @memberOf ModelGridColumn
         * Column sort, works only in HTML5 */
        P.ModelGridColumn.prototype.sort = function() {
            var delegate = this.unwrap();
            var value = delegate.sort();
            return P.boxAsJs(value);
        };

        /**
         * @method sortDesc
         * @memberOf ModelGridColumn
         * Descending column sort, works only in HTML5 */
        P.ModelGridColumn.prototype.sortDesc = function() {
            var delegate = this.unwrap();
            var value = delegate.sortDesc();
            return P.boxAsJs(value);
        };

        /**
         *
         * @method insertColumnNode
         * @memberOf ModelGridColumn
         */
        P.ModelGridColumn.prototype.insertColumnNode = function(position, node) {
            var delegate = this.unwrap();
            var value = delegate.insertColumnNode(P.boxAsJava(position), P.boxAsJava(node));
            return P.boxAsJs(value);
        };

        /**
         *
         * @method addColumnNode
         * @memberOf ModelGridColumn
         */
        P.ModelGridColumn.prototype.addColumnNode = function(node) {
            var delegate = this.unwrap();
            var value = delegate.addColumnNode(P.boxAsJava(node));
            return P.boxAsJs(value);
        };

        /**
         * @method unsort
         * @memberOf ModelGridColumn
         * Clears sort column, works only in HTML5 */
        P.ModelGridColumn.prototype.unsort = function() {
            var delegate = this.unwrap();
            var value = delegate.unsort();
            return P.boxAsJs(value);
        };

        /**
         *
         * @method removeColumnNode
         * @memberOf ModelGridColumn
         */
        P.ModelGridColumn.prototype.removeColumnNode = function(node) {
            var delegate = this.unwrap();
            var value = delegate.removeColumnNode(P.boxAsJava(node));
            return P.boxAsJs(value);
        };

})();