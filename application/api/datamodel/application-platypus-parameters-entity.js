(function() {
    var javaClass = Java.type("com.eas.client.model.application.ApplicationPlatypusParametersEntity");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ApplicationPlatypusParametersEntity(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor ApplicationPlatypusParametersEntity ApplicationPlatypusParametersEntity
     */
    P.ApplicationPlatypusParametersEntity = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.ApplicationPlatypusParametersEntity.superclass)
            P.ApplicationPlatypusParametersEntity.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "cursor", {
            get: function() {
                var value = delegate.cursor;
                return P.boxAsJs(value);
            }
        });
        if(!P.ApplicationPlatypusParametersEntity){
            /**
             * Gets the row at cursor position.
             * @return the row object or <code>null</code> if cursor is before first or after last position.
             * @property cursor
             * @memberOf ApplicationPlatypusParametersEntity
             */
            P.ApplicationPlatypusParametersEntity.prototype.cursor = {};
        }
        Object.defineProperty(this, "willScroll", {
            get: function() {
                var value = delegate.willScroll;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.willScroll = P.boxAsJava(aValue);
            }
        });
        if(!P.ApplicationPlatypusParametersEntity){
            /**
             * The handler function for the event occured before the cursor position changed.
             * @property willScroll
             * @memberOf ApplicationPlatypusParametersEntity
             */
            P.ApplicationPlatypusParametersEntity.prototype.willScroll = {};
        }
        Object.defineProperty(this, "willInsert", {
            get: function() {
                var value = delegate.willInsert;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.willInsert = P.boxAsJava(aValue);
            }
        });
        if(!P.ApplicationPlatypusParametersEntity){
            /**
             * The handler function for the event occured before an entity row has been inserted.
             * @property willInsert
             * @memberOf ApplicationPlatypusParametersEntity
             */
            P.ApplicationPlatypusParametersEntity.prototype.willInsert = {};
        }
        Object.defineProperty(this, "onRequeried", {
            get: function() {
                var value = delegate.onRequeried;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onRequeried = P.boxAsJava(aValue);
            }
        });
        if(!P.ApplicationPlatypusParametersEntity){
            /**
             * The handler function for the event occured after the entity's data have been required.
             * @property onRequeried
             * @memberOf ApplicationPlatypusParametersEntity
             */
            P.ApplicationPlatypusParametersEntity.prototype.onRequeried = {};
        }
        Object.defineProperty(this, "onChanged", {
            get: function() {
                var value = delegate.onChanged;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onChanged = P.boxAsJava(aValue);
            }
        });
        if(!P.ApplicationPlatypusParametersEntity){
            /**
             * The handler function for the event occured after the entity data change.
             * @property onChanged
             * @memberOf ApplicationPlatypusParametersEntity
             */
            P.ApplicationPlatypusParametersEntity.prototype.onChanged = {};
        }
        Object.defineProperty(this, "onDeleted", {
            get: function() {
                var value = delegate.onDeleted;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onDeleted = P.boxAsJava(aValue);
            }
        });
        if(!P.ApplicationPlatypusParametersEntity){
            /**
             * The handler function for the event occured after an entity row has been deleted.
             * @property onDeleted
             * @memberOf ApplicationPlatypusParametersEntity
             */
            P.ApplicationPlatypusParametersEntity.prototype.onDeleted = {};
        }
        Object.defineProperty(this, "onScrolled", {
            get: function() {
                var value = delegate.onScrolled;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onScrolled = P.boxAsJava(aValue);
            }
        });
        if(!P.ApplicationPlatypusParametersEntity){
            /**
             * The handler function for the event occured after the cursor position changed.
             * @property onScrolled
             * @memberOf ApplicationPlatypusParametersEntity
             */
            P.ApplicationPlatypusParametersEntity.prototype.onScrolled = {};
        }
        Object.defineProperty(this, "onFiltered", {
            get: function() {
                var value = delegate.onFiltered;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onFiltered = P.boxAsJava(aValue);
            }
        });
        if(!P.ApplicationPlatypusParametersEntity){
            /**
             * The handler function for the event occured after the entity's data have been filtered.
             * @property onFiltered
             * @memberOf ApplicationPlatypusParametersEntity
             */
            P.ApplicationPlatypusParametersEntity.prototype.onFiltered = {};
        }
        Object.defineProperty(this, "substitute", {
            get: function() {
                var value = delegate.substitute;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.substitute = P.boxAsJava(aValue);
            }
        });
        if(!P.ApplicationPlatypusParametersEntity){
            /**
             * Returns cursor-substitute entity.
             * Sunstitute's cursor is used when in original entity's cursor some field's value is null.
             * @property substitute
             * @memberOf ApplicationPlatypusParametersEntity
             */
            P.ApplicationPlatypusParametersEntity.prototype.substitute = {};
        }
        Object.defineProperty(this, "empty", {
            get: function() {
                var value = delegate.empty;
                return P.boxAsJs(value);
            }
        });
        if(!P.ApplicationPlatypusParametersEntity){
            /**
             * Checks if the rowset is empty.
             * @return <code>true</code> if the rowset is empty and <code>false</code> otherwise.
             * @property empty
             * @memberOf ApplicationPlatypusParametersEntity
             */
            P.ApplicationPlatypusParametersEntity.prototype.empty = true;
        }
        Object.defineProperty(this, "onInserted", {
            get: function() {
                var value = delegate.onInserted;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onInserted = P.boxAsJava(aValue);
            }
        });
        if(!P.ApplicationPlatypusParametersEntity){
            /**
             * The handler function for the event occured after an entity row has been inserted.
             * @property onInserted
             * @memberOf ApplicationPlatypusParametersEntity
             */
            P.ApplicationPlatypusParametersEntity.prototype.onInserted = {};
        }
        Object.defineProperty(this, "willChange", {
            get: function() {
                var value = delegate.willChange;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.willChange = P.boxAsJava(aValue);
            }
        });
        if(!P.ApplicationPlatypusParametersEntity){
            /**
             * The handler function for the event occured before the entity data change.
             * @property willChange
             * @memberOf ApplicationPlatypusParametersEntity
             */
            P.ApplicationPlatypusParametersEntity.prototype.willChange = {};
        }
        Object.defineProperty(this, "size", {
            get: function() {
                var value = delegate.size;
                return P.boxAsJs(value);
            }
        });
        if(!P.ApplicationPlatypusParametersEntity){
            /**
             * The rowset size.
             * @property size
             * @memberOf ApplicationPlatypusParametersEntity
             */
            P.ApplicationPlatypusParametersEntity.prototype.size = 0;
        }
        Object.defineProperty(this, "activeFilter", {
            get: function() {
                var value = delegate.activeFilter;
                return P.boxAsJs(value);
            }
        });
        if(!P.ApplicationPlatypusParametersEntity){
            /**
             * Entity's active <code>Filter</code> object.
             * @property activeFilter
             * @memberOf ApplicationPlatypusParametersEntity
             */
            P.ApplicationPlatypusParametersEntity.prototype.activeFilter = {};
        }
        Object.defineProperty(this, "elementClass", {
            get: function() {
                var value = delegate.elementClass;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.elementClass = P.boxAsJava(aValue);
            }
        });
        if(!P.ApplicationPlatypusParametersEntity){
            /**
             * Experimental. The constructor funciton for the entity's data array elements.
             * @property elementClass
             * @memberOf ApplicationPlatypusParametersEntity
             */
            P.ApplicationPlatypusParametersEntity.prototype.elementClass = {};
        }
        Object.defineProperty(this, "willDelete", {
            get: function() {
                var value = delegate.willDelete;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.willDelete = P.boxAsJava(aValue);
            }
        });
        if(!P.ApplicationPlatypusParametersEntity){
            /**
             * The handler function for the event occured before an entity row has been deleted.
             * @property willDelete
             * @memberOf ApplicationPlatypusParametersEntity
             */
            P.ApplicationPlatypusParametersEntity.prototype.willDelete = {};
        }
    };        Object.defineProperty(P.ApplicationPlatypusParametersEntity.prototype, "next", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.next();
            return P.boxAsJs(value);
        }
    });
    if(!P.ApplicationPlatypusParametersEntity){
        /**
         * Moves the rowset cursor to the next row.
         * @return <code>true</code> if cursor moved successfully and <code>false</code> otherwise.
         * @method next
         * @memberOf ApplicationPlatypusParametersEntity
         */
        P.ApplicationPlatypusParametersEntity.prototype.next = function(){};
    }
    Object.defineProperty(P.ApplicationPlatypusParametersEntity.prototype, "find", {
        value: function(pairs) {
            var delegate = this.unwrap();
            var value = delegate.find(P.boxAsJava(pairs));
            return P.boxAsJs(value);
        }
    });
    if(!P.ApplicationPlatypusParametersEntity){
        /**
         * Finds rows using field -- field value pairs.
         * @param pairs the search conditions pairs, if a form of key-values pairs, where the key is the property object (e.g. entity.md.propName) and the value for this property.
         * @return the rows object's array accordind to the search condition or empty array if nothing is found.
         * @method find
         * @memberOf ApplicationPlatypusParametersEntity
         */
        P.ApplicationPlatypusParametersEntity.prototype.find = function(pairs){};
    }
    Object.defineProperty(P.ApplicationPlatypusParametersEntity.prototype, "execute", {
        value: function(onSuccessCallback, onFailureCallback) {
            var delegate = this.unwrap();
            var value = delegate.execute(P.boxAsJava(onSuccessCallback), P.boxAsJava(onFailureCallback));
            return P.boxAsJs(value);
        }
    });
    if(!P.ApplicationPlatypusParametersEntity){
        /**
         * Refreshes rowset, only if any of its parameters has changed.
         * @param onSuccessCallback the handler function for refresh data on success event (optional).
         * @param onFailureCallback the handler function for refresh data on failure event (optional).
         * @method execute
         * @memberOf ApplicationPlatypusParametersEntity
         */
        P.ApplicationPlatypusParametersEntity.prototype.execute = function(onSuccessCallback, onFailureCallback){};
    }
    Object.defineProperty(P.ApplicationPlatypusParametersEntity.prototype, "prev", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.prev();
            return P.boxAsJs(value);
        }
    });
    if(!P.ApplicationPlatypusParametersEntity){
        /**
         * Moves the rowset cursor to the privious row.
         * @return <code>true</code> if cursor moved successfully and <code>false</code> otherwise.
         * @method prev
         * @memberOf ApplicationPlatypusParametersEntity
         */
        P.ApplicationPlatypusParametersEntity.prototype.prev = function(){};
    }
    Object.defineProperty(P.ApplicationPlatypusParametersEntity.prototype, "insert", {
        value: function(arg0) {
            var delegate = this.unwrap();
            var value = delegate.insert(P.boxAsJava(arg0));
            return P.boxAsJs(value);
        }
    });
    if(!P.ApplicationPlatypusParametersEntity){
        /**
         * Inserts new row in the rowset and sets cursor on this row. @see push.
         * @param pairs the fields value pairs, in a form of key-values pairs, where the key is the property object (e.g. entity.md.propName) and the value for this property (optional).
         * @method insert
         * @memberOf ApplicationPlatypusParametersEntity
         */
        P.ApplicationPlatypusParametersEntity.prototype.insert = function(arg0){};
    }
    Object.defineProperty(P.ApplicationPlatypusParametersEntity.prototype, "pos", {
        value: function(index) {
            var delegate = this.unwrap();
            var value = delegate.pos(P.boxAsJava(index));
            return P.boxAsJs(value);
        }
    });
    if(!P.ApplicationPlatypusParametersEntity){
        /**
         * Positions the rowset cursor on the specified row number. Row number is 1-based.
         * @param index the row index to check, starting form <code>1</code>.
         * @return <code>true</code> if the cursor is on the row with specified index and <code>false</code> otherwise.
         * @method pos
         * @memberOf ApplicationPlatypusParametersEntity
         */
        P.ApplicationPlatypusParametersEntity.prototype.pos = function(index){};
    }
    Object.defineProperty(P.ApplicationPlatypusParametersEntity.prototype, "first", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.first();
            return P.boxAsJs(value);
        }
    });
    if(!P.ApplicationPlatypusParametersEntity){
        /**
         * Moves the rowset cursor to the first row.
         * @return <code>true</code> if cursor moved successfully and <code>false</code> otherwise.
         * @method first
         * @memberOf ApplicationPlatypusParametersEntity
         */
        P.ApplicationPlatypusParametersEntity.prototype.first = function(){};
    }
    Object.defineProperty(P.ApplicationPlatypusParametersEntity.prototype, "eof", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.eof();
            return P.boxAsJs(value);
        }
    });
    if(!P.ApplicationPlatypusParametersEntity){
        /**
         * Checks if cursor in the position before the first row.
         * @return <code>true</code> if cursor moved successfully and <code>false</code> otherwise.
         * @method eof
         * @memberOf ApplicationPlatypusParametersEntity
         */
        P.ApplicationPlatypusParametersEntity.prototype.eof = function(){};
    }
    Object.defineProperty(P.ApplicationPlatypusParametersEntity.prototype, "last", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.last();
            return P.boxAsJs(value);
        }
    });
    if(!P.ApplicationPlatypusParametersEntity){
        /**
         * Moves the rowset cursor to the last row.
         * @return <code>true</code> if cursor moved successfully and <code>false</code> otherwise.
         * @method last
         * @memberOf ApplicationPlatypusParametersEntity
         */
        P.ApplicationPlatypusParametersEntity.prototype.last = function(){};
    }
    Object.defineProperty(P.ApplicationPlatypusParametersEntity.prototype, "beforeFirst", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.beforeFirst();
            return P.boxAsJs(value);
        }
    });
    if(!P.ApplicationPlatypusParametersEntity){
        /**
         * Moves the rowset cursor to the position before the first row.
         * @method beforeFirst
         * @memberOf ApplicationPlatypusParametersEntity
         */
        P.ApplicationPlatypusParametersEntity.prototype.beforeFirst = function(){};
    }
    Object.defineProperty(P.ApplicationPlatypusParametersEntity.prototype, "afterLast", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.afterLast();
            return P.boxAsJs(value);
        }
    });
    if(!P.ApplicationPlatypusParametersEntity){
        /**
         * Moves the rowset cursor to the position after the last row.
         * @method afterLast
         * @memberOf ApplicationPlatypusParametersEntity
         */
        P.ApplicationPlatypusParametersEntity.prototype.afterLast = function(){};
    }
    Object.defineProperty(P.ApplicationPlatypusParametersEntity.prototype, "deleteAll", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.deleteAll();
            return P.boxAsJs(value);
        }
    });
    if(!P.ApplicationPlatypusParametersEntity){
        /**
         * Deletes all rows in the rowset.
         * @method deleteAll
         * @memberOf ApplicationPlatypusParametersEntity
         */
        P.ApplicationPlatypusParametersEntity.prototype.deleteAll = function(){};
    }
    Object.defineProperty(P.ApplicationPlatypusParametersEntity.prototype, "createFilter", {
        value: function(pairs) {
            var delegate = this.unwrap();
            var value = delegate.createFilter(P.boxAsJava(pairs));
            return P.boxAsJs(value);
        }
    });
    if(!P.ApplicationPlatypusParametersEntity){
        /**
         * Creates an instace of filter object to filter rowset data in-place using specified constraints objects.
         * @param pairs the search conditions pairs, if a form of key-values pairs, where the key is the property object (e.g. entity.md.propName) and the value for this property.
         * @return a comparator object.
         * @method createFilter
         * @memberOf ApplicationPlatypusParametersEntity
         */
        P.ApplicationPlatypusParametersEntity.prototype.createFilter = function(pairs){};
    }
    Object.defineProperty(P.ApplicationPlatypusParametersEntity.prototype, "getRow", {
        value: function(index) {
            var delegate = this.unwrap();
            var value = delegate.getRow(P.boxAsJava(index));
            return P.boxAsJs(value);
        }
    });
    if(!P.ApplicationPlatypusParametersEntity){
        /**
         * Gets the row at specified index.
         * @param index the row index, starting form <code>1</code>.
         * @return the row object or <code>null</code> if no row object have found at the specified index.
         * @method getRow
         * @memberOf ApplicationPlatypusParametersEntity
         */
        P.ApplicationPlatypusParametersEntity.prototype.getRow = function(index){};
    }
    Object.defineProperty(P.ApplicationPlatypusParametersEntity.prototype, "deleteRow", {
        value: function(aCursorPosOrInstance) {
            var delegate = this.unwrap();
            var value = delegate.deleteRow(P.boxAsJava(aCursorPosOrInstance));
            return P.boxAsJs(value);
        }
    });
    if(!P.ApplicationPlatypusParametersEntity){
        /**
         * Deletes the row by cursor position or by row itself.
         * @param aCursorPosOrInstance row position in terms of cursor API (1-based)| row instance itself. Note! If no cursor position or instance is passed,then row at current cursor position will b e deleted.
         * @method deleteRow
         * @memberOf ApplicationPlatypusParametersEntity
         */
        P.ApplicationPlatypusParametersEntity.prototype.deleteRow = function(aCursorPosOrInstance){};
    }
    Object.defineProperty(P.ApplicationPlatypusParametersEntity.prototype, "findById", {
        value: function(key) {
            var delegate = this.unwrap();
            var value = delegate.findById(P.boxAsJava(key));
            return P.boxAsJs(value);
        }
    });
    if(!P.ApplicationPlatypusParametersEntity){
        /**
         * Finds row by its key. Key must a single property.
         * @param key the unique identifier of the row.
         * @return a row object or <code>null</code> if nothing is found.
         * @method findById
         * @memberOf ApplicationPlatypusParametersEntity
         */
        P.ApplicationPlatypusParametersEntity.prototype.findById = function(key){};
    }
    Object.defineProperty(P.ApplicationPlatypusParametersEntity.prototype, "scrollTo", {
        value: function(row) {
            var delegate = this.unwrap();
            var value = delegate.scrollTo(P.boxAsJava(row));
            return P.boxAsJs(value);
        }
    });
    if(!P.ApplicationPlatypusParametersEntity){
        /**
         * Sets the rowset cursor to the specified row.
         * @param row the row to position the entity cursor.
         * @return <code>true</code> if the rowset scrolled successfully and <code>false</code> otherwise.
         * @method scrollTo
         * @memberOf ApplicationPlatypusParametersEntity
         */
        P.ApplicationPlatypusParametersEntity.prototype.scrollTo = function(row){};
    }
    Object.defineProperty(P.ApplicationPlatypusParametersEntity.prototype, "bof", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.bof();
            return P.boxAsJs(value);
        }
    });
    if(!P.ApplicationPlatypusParametersEntity){
        /**
         * Checks if cursor in the position before the first row.
         * @return <code>true</code> if cursor in the position before the first row and <code>false</code> otherwise.
         * @method bof
         * @memberOf ApplicationPlatypusParametersEntity
         */
        P.ApplicationPlatypusParametersEntity.prototype.bof = function(){};
    }
    Object.defineProperty(P.ApplicationPlatypusParametersEntity.prototype, "requery", {
        value: function(onSuccessCallback, onFailureCallback) {
            var delegate = this.unwrap();
            var value = delegate.requery(P.boxAsJava(onSuccessCallback), P.boxAsJava(onFailureCallback));
            return P.boxAsJs(value);
        }
    });
    if(!P.ApplicationPlatypusParametersEntity){
        /**
         * Requeries the rowset's data. Forses the rowset to refresh its data, no matter if its parameters has changed or not.
         * @param onSuccessCallback the handler function for refresh data on success event (optional).
         * @param onFailureCallback the handler function for refresh data on failure event (optional).
         * @method requery
         * @memberOf ApplicationPlatypusParametersEntity
         */
        P.ApplicationPlatypusParametersEntity.prototype.requery = function(onSuccessCallback, onFailureCallback){};
    }
    Object.defineProperty(P.ApplicationPlatypusParametersEntity.prototype, "createSorting", {
        value: function(arg0) {
            var delegate = this.unwrap();
            var value = delegate.createSorting(P.boxAsJava(arg0));
            return P.boxAsJs(value);
        }
    });
    if(!P.ApplicationPlatypusParametersEntity){
        /**
         * Creates an instance of comparator object using specified constraints objects.
         * @param pairs the search conditions pairs, in a form of key-values pairs, where the key is the property object (e.g. entity.md.propName) and the value for this property.
         * @return a comparator object to be passed as a parameter to entity's <code>sort</code> method.
         * @method createSorting
         * @memberOf ApplicationPlatypusParametersEntity
         */
        P.ApplicationPlatypusParametersEntity.prototype.createSorting = function(arg0){};
    }
    Object.defineProperty(P.ApplicationPlatypusParametersEntity.prototype, "beginUpdate", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.beginUpdate();
            return P.boxAsJs(value);
        }
    });
    if(!P.ApplicationPlatypusParametersEntity){
        /**
         * Disables automatic model update on parameters change, @see endUpdate method.
         * @method beginUpdate
         * @memberOf ApplicationPlatypusParametersEntity
         */
        P.ApplicationPlatypusParametersEntity.prototype.beginUpdate = function(){};
    }
    Object.defineProperty(P.ApplicationPlatypusParametersEntity.prototype, "endUpdate", {
        value: function() {
            var delegate = this.unwrap();
            var value = delegate.endUpdate();
            return P.boxAsJs(value);
        }
    });
    if(!P.ApplicationPlatypusParametersEntity){
        /**
         * Enables automatic model update on parameters change, @see beginUpdate method.
         * @method endUpdate
         * @memberOf ApplicationPlatypusParametersEntity
         */
        P.ApplicationPlatypusParametersEntity.prototype.endUpdate = function(){};
    }

})();