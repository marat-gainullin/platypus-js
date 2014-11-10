(function() {
    var javaClass = Java.type("com.eas.client.model.application.ApplicationDbParametersEntity");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ApplicationDbParametersEntity(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor ApplicationDbParametersEntity ApplicationDbParametersEntity
     */
    P.ApplicationDbParametersEntity = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.ApplicationDbParametersEntity.superclass)
            P.ApplicationDbParametersEntity.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "cursor", {
            get: function() {
                var value = delegate.cursor;
                return P.boxAsJs(value);
            }
        });
        if(!P.ApplicationDbParametersEntity){
            /**
             * Gets the row at cursor position.
             * @return the row object or <code>null</code> if cursor is before first or after last position.
             * @property cursor
             * @memberOf ApplicationDbParametersEntity
             */
            P.ApplicationDbParametersEntity.prototype.cursor = {};
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
        if(!P.ApplicationDbParametersEntity){
            /**
             * The handler function for the event occured before the cursor position changed.
             * @property willScroll
             * @memberOf ApplicationDbParametersEntity
             */
            P.ApplicationDbParametersEntity.prototype.willScroll = {};
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
        if(!P.ApplicationDbParametersEntity){
            /**
             * The handler function for the event occured before an entity row has been inserted.
             * @property willInsert
             * @memberOf ApplicationDbParametersEntity
             */
            P.ApplicationDbParametersEntity.prototype.willInsert = {};
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
        if(!P.ApplicationDbParametersEntity){
            /**
             * The handler function for the event occured after the entity's data have been requeried.
             * @property onRequeried
             * @memberOf ApplicationDbParametersEntity
             */
            P.ApplicationDbParametersEntity.prototype.onRequeried = {};
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
        if(!P.ApplicationDbParametersEntity){
            /**
             * The handler function for the event occured after the entity data change.
             * @property onChanged
             * @memberOf ApplicationDbParametersEntity
             */
            P.ApplicationDbParametersEntity.prototype.onChanged = {};
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
        if(!P.ApplicationDbParametersEntity){
            /**
             * The handler function for the event occured after an entity row has been deleted.
             * @property onDeleted
             * @memberOf ApplicationDbParametersEntity
             */
            P.ApplicationDbParametersEntity.prototype.onDeleted = {};
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
        if(!P.ApplicationDbParametersEntity){
            /**
             * The handler function for the event occured after the cursor position changed.
             * @property onScrolled
             * @memberOf ApplicationDbParametersEntity
             */
            P.ApplicationDbParametersEntity.prototype.onScrolled = {};
        }
        Object.defineProperty(this, "cursorPos", {
            get: function() {
                var value = delegate.cursorPos;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.cursorPos = P.boxAsJava(aValue);
            }
        });
        if(!P.ApplicationDbParametersEntity){
            /**
             * Current position of cursor (1-based). There are two special values: 0 - before first; length + 1 - after last;
             * @property cursorPos
             * @memberOf ApplicationDbParametersEntity
             */
            P.ApplicationDbParametersEntity.prototype.cursorPos = 0;
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
        if(!P.ApplicationDbParametersEntity){
            /**
             * The handler function for the event occured after the entity's data have been filtered.
             * @property onFiltered
             * @memberOf ApplicationDbParametersEntity
             */
            P.ApplicationDbParametersEntity.prototype.onFiltered = {};
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
        if(!P.ApplicationDbParametersEntity){
            /**
             * The handler function for the event occured after an entity row has been inserted.
             * @property onInserted
             * @memberOf ApplicationDbParametersEntity
             */
            P.ApplicationDbParametersEntity.prototype.onInserted = {};
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
        if(!P.ApplicationDbParametersEntity){
            /**
             * The handler function for the event occured before the entity data change.
             * @property willChange
             * @memberOf ApplicationDbParametersEntity
             */
            P.ApplicationDbParametersEntity.prototype.willChange = {};
        }
        Object.defineProperty(this, "activeFilter", {
            get: function() {
                var value = delegate.activeFilter;
                return P.boxAsJs(value);
            }
        });
        if(!P.ApplicationDbParametersEntity){
            /**
             * Entity's active <code>Filter</code> object.
             * @property activeFilter
             * @memberOf ApplicationDbParametersEntity
             */
            P.ApplicationDbParametersEntity.prototype.activeFilter = {};
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
        if(!P.ApplicationDbParametersEntity){
            /**
             * Experimental. The constructor funciton for the entity's data array elements.
             * @property elementClass
             * @memberOf ApplicationDbParametersEntity
             */
            P.ApplicationDbParametersEntity.prototype.elementClass = {};
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
        if(!P.ApplicationDbParametersEntity){
            /**
             * The handler function for the event occured before an entity row has been deleted.
             * @property willDelete
             * @memberOf ApplicationDbParametersEntity
             */
            P.ApplicationDbParametersEntity.prototype.willDelete = {};
        }
    };
        /**
         * Applies the updates into the database and commits the transaction.
         * @param onSuccess Success callback. It has an argument, - updates rows count.
         * @param onFailure Failure callback. It has an argument, - exception occured while applying updates into the database.
         * @method executeUpdate
         * @memberOf ApplicationDbParametersEntity
         */
        P.ApplicationDbParametersEntity.prototype.executeUpdate = function(arg0, arg1) {
            var delegate = this.unwrap();
            var value = delegate.executeUpdate(P.boxAsJava(arg0), P.boxAsJava(arg1));
            return P.boxAsJs(value);
        };

        /**
         * Adds the updates into the change log as a command.
         * @method enqueueUpdate
         * @memberOf ApplicationDbParametersEntity
         */
        P.ApplicationDbParametersEntity.prototype.enqueueUpdate = function() {
            var delegate = this.unwrap();
            var value = delegate.enqueueUpdate();
            return P.boxAsJs(value);
        };

        /**
         * Deletes a object by cursor position or by object itself.
         * @param aCursorPosOrInstance Object position in terms of cursor API (1-based)| object instance itself. Note! If no cursor position or instance is passed,then object at current cursor position will be deleted.
         * @method remove
         * @memberOf ApplicationDbParametersEntity
         */
        P.ApplicationDbParametersEntity.prototype.remove = function(aCursorPosOrInstance) {
            var delegate = this.unwrap();
            var value = delegate.remove(P.boxAsJava(aCursorPosOrInstance));
            return P.boxAsJs(value);
        };

        /**
         * Finds rows using field - value pairs.
         * @param pairs the search conditions pairs, if a form of key-values pairs, where the key is the property object (e.g. entity.schema.propName or just a prop name in a string form) and the value for this property.
         * @return the rows object's array accordind to the search condition or empty array if nothing is found.
         * @method find
         * @memberOf ApplicationDbParametersEntity
         */
        P.ApplicationDbParametersEntity.prototype.find = function(pairs) {
            var delegate = this.unwrap();
            var value = delegate.find(P.boxAsJava(pairs));
            return P.boxAsJs(value);
        };

        /**
         * Refreshes entity, only if any of its parameters has changed.
         * @param onSuccess The handler function for refresh data on success event (optional).
         * @param onFailure The handler function for refresh data on failure event (optional).
         * @method execute
         * @memberOf ApplicationDbParametersEntity
         */
        P.ApplicationDbParametersEntity.prototype.execute = function(onSuccess, onFailure) {
            var delegate = this.unwrap();
            var value = delegate.execute(P.boxAsJava(onSuccess), P.boxAsJava(onFailure));
            return P.boxAsJs(value);
        };

        /**
         * Deletes all rows in the rowset.
         * @method removeAll
         * @memberOf ApplicationDbParametersEntity
         */
        P.ApplicationDbParametersEntity.prototype.removeAll = function() {
            var delegate = this.unwrap();
            var value = delegate.removeAll();
            return P.boxAsJs(value);
        };

        /**
         * Sorts data according to comparator object returned by createSorting() or by comparator function.
         * @param comparator A comparator function or object returned from createSorting() method.
         * @method sort
         * @memberOf ApplicationDbParametersEntity
         */
        P.ApplicationDbParametersEntity.prototype.sort = function(comparator) {
            var delegate = this.unwrap();
            var value = delegate.sort(P.boxAsJava(comparator));
            return P.boxAsJs(value);
        };

        /**
         * Creates an instace of filter object to filter rowset data in-place using specified constraints objects.
         * @param fields The filter conditions fields in following form: entity.schema.propName or just a propName in a string form.
         * @return a comparator object.
         * @method createFilter
         * @memberOf ApplicationDbParametersEntity
         */
        P.ApplicationDbParametersEntity.prototype.createFilter = function(fields) {
            var delegate = this.unwrap();
            var value = delegate.createFilter(P.boxAsJava(fields));
            return P.boxAsJs(value);
        };

        /**
         * Creates an instance of comparator object using specified constraints objects.
         * @param pairs the sort criteria pairs, in a form of property object (e.g. entity.schema.propName or just a propName in a string form) and the order of sort (ascending - true; descending - false).
         * @return a comparator object to be passed as a parameter to entity's <code>sort</code> method.
         * @method createSorting
         * @memberOf ApplicationDbParametersEntity
         */
        P.ApplicationDbParametersEntity.prototype.createSorting = function(pairs) {
            var delegate = this.unwrap();
            var value = delegate.createSorting(P.boxAsJava(pairs));
            return P.boxAsJs(value);
        };

        /**
         * Disables automatic model update on parameters change, @see endUpdate method.
         * @method beginUpdate
         * @memberOf ApplicationDbParametersEntity
         */
        P.ApplicationDbParametersEntity.prototype.beginUpdate = function() {
            var delegate = this.unwrap();
            var value = delegate.beginUpdate();
            return P.boxAsJs(value);
        };

        /**
         * Enables automatic model update on parameters change, @see beginUpdate method.
         * @method endUpdate
         * @memberOf ApplicationDbParametersEntity
         */
        P.ApplicationDbParametersEntity.prototype.endUpdate = function() {
            var delegate = this.unwrap();
            var value = delegate.endUpdate();
            return P.boxAsJs(value);
        };

        /**
         * Finds row by its key. Key must a single property.
         * @param key the unique identifier of the row.
         * @return a row object or <code>null</code> if nothing is found.
         * @method findById
         * @memberOf ApplicationDbParametersEntity
         */
        P.ApplicationDbParametersEntity.prototype.findById = function(key) {
            var delegate = this.unwrap();
            var value = delegate.findById(P.boxAsJava(key));
            return P.boxAsJs(value);
        };

        /**
         * Sets the array cursor to the specified object.
         * @param object the object to position the entity cursor on.
         * @return <code>true</code> if the cursor changed successfully and <code>false</code> otherwise.
         * @method scrollTo
         * @memberOf ApplicationDbParametersEntity
         */
        P.ApplicationDbParametersEntity.prototype.scrollTo = function(row) {
            var delegate = this.unwrap();
            var value = delegate.scrollTo(P.boxAsJava(row));
            return P.boxAsJs(value);
        };

        /**
         * Requeries the entity's data. Forses the entity to refresh its data, no matter if its parameters has changed or not.
         * @param onSuccess The callback function for refresh data on success event (optional).
         * @param onFailure The callback function for refresh data on failure event (optional).
         * @method requery
         * @memberOf ApplicationDbParametersEntity
         */
        P.ApplicationDbParametersEntity.prototype.requery = function(onSuccess, onFailure) {
            var delegate = this.unwrap();
            var value = delegate.requery(P.boxAsJava(onSuccess), P.boxAsJava(onFailure));
            return P.boxAsJs(value);
        };

})();