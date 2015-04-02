(function() {
    var javaClass = Java.type("com.eas.client.model.application.ApplicationPlatypusEntity");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ApplicationPlatypusEntity(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor ApplicationPlatypusEntity ApplicationPlatypusEntity
     */
    P.ApplicationPlatypusEntity = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.ApplicationPlatypusEntity.superclass)
            P.ApplicationPlatypusEntity.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "cursor", {
            get: function() {
                var value = delegate.cursor;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.cursor = P.boxAsJava(aValue);
            }
        });
        if(!P.ApplicationPlatypusEntity){
            /**
             * Gets the row at cursor position.
             * @return the row object or <code>null</code> if cursor is before first or after last position.
             * @property cursor
             * @memberOf ApplicationPlatypusEntity
             */
            P.ApplicationPlatypusEntity.prototype.cursor = {};
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
        if(!P.ApplicationPlatypusEntity){
            /**
             * The handler function for the event occured after an entity row has been inserted.
             * @property onInserted
             * @memberOf ApplicationPlatypusEntity
             */
            P.ApplicationPlatypusEntity.prototype.onInserted = {};
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
        if(!P.ApplicationPlatypusEntity){
            /**
             * The handler function for the event occured after the entity's data have been requeried.
             * @property onRequeried
             * @memberOf ApplicationPlatypusEntity
             */
            P.ApplicationPlatypusEntity.prototype.onRequeried = {};
        }
        Object.defineProperty(this, "activeFilter", {
            get: function() {
                var value = delegate.activeFilter;
                return P.boxAsJs(value);
            }
        });
        if(!P.ApplicationPlatypusEntity){
            /**
             * Entity's active <code>Filter</code> object.
             * @property activeFilter
             * @memberOf ApplicationPlatypusEntity
             */
            P.ApplicationPlatypusEntity.prototype.activeFilter = {};
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
        if(!P.ApplicationPlatypusEntity){
            /**
             * The handler function for the event occured after an entity row has been deleted.
             * @property onDeleted
             * @memberOf ApplicationPlatypusEntity
             */
            P.ApplicationPlatypusEntity.prototype.onDeleted = {};
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
        if(!P.ApplicationPlatypusEntity){
            /**
             * The handler function for the event occured after the cursor position changed.
             * @property onScrolled
             * @memberOf ApplicationPlatypusEntity
             */
            P.ApplicationPlatypusEntity.prototype.onScrolled = {};
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
        if(!P.ApplicationPlatypusEntity){
            /**
             * Current position of cursor (1 - based). There are two special values: 0 - before first; length + 1 - after last;
             * @property cursorPos
             * @memberOf ApplicationPlatypusEntity
             */
            P.ApplicationPlatypusEntity.prototype.cursorPos = 0;
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
        if(!P.ApplicationPlatypusEntity){
            /**
             * Experimental. The constructor funciton for the entity's data array elements.
             * @property elementClass
             * @memberOf ApplicationPlatypusEntity
             */
            P.ApplicationPlatypusEntity.prototype.elementClass = {};
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
        if(!P.ApplicationPlatypusEntity){
            /**
             * The handler function for the event occured after the entity's data have been filtered.
             * @property onFiltered
             * @memberOf ApplicationPlatypusEntity
             */
            P.ApplicationPlatypusEntity.prototype.onFiltered = {};
        }
    };
        /**
         * Adds the updates into the change log as a command.
         * @method enqueueUpdate
         * @memberOf ApplicationPlatypusEntity
         */
        P.ApplicationPlatypusEntity.prototype.enqueueUpdate = function() {
            var delegate = this.unwrap();
            var value = delegate.enqueueUpdate();
            return P.boxAsJs(value);
        };

        /**
         * Deletes a object by cursor position or by object itself.
         * @param aCursorPosOrInstance Object position in terms of cursor API (1-based)| object instance itself. Note! If no cursor position or instance is passed,then object at current cursor position will be deleted.
         * @method remove
         * @memberOf ApplicationPlatypusEntity
         */
        P.ApplicationPlatypusEntity.prototype.remove = function(aCursorPosOrInstance) {
            var delegate = this.unwrap();
            var value = delegate.remove(P.boxAsJava(aCursorPosOrInstance));
            return P.boxAsJs(value);
        };

        /**
         * Finds rows using field - value pairs.
         * @param pairs the search conditions pairs, if a form of key-values pairs, where the key is the property object (e.g. entity.schema.propName or just a prop name in a string form) and the value for this property.
         * @return the rows object's array accordind to the search condition or empty array if nothing is found.
         * @method find
         * @memberOf ApplicationPlatypusEntity
         */
        P.ApplicationPlatypusEntity.prototype.find = function(criteria) {
            var delegate = this.unwrap();
            var value = delegate.find(P.boxAsJava(criteria));
            return P.boxAsJs(value);
        };

        /**
         * Refreshes entity, only if any of its parameters has changed.
         * @param onSuccess The handler function for refresh data on success event (optional).
         * @param onFailure The handler function for refresh data on failure event (optional).
         * @method execute
         * @memberOf ApplicationPlatypusEntity
         */
        P.ApplicationPlatypusEntity.prototype.execute = function(onSuccess, onFailure) {
            var delegate = this.unwrap();
            var value = delegate.execute(P.boxAsJava(onSuccess), P.boxAsJava(onFailure));
            return P.boxAsJs(value);
        };

        /**
         * Deletes all rows in the rowset.
         * @method removeAll
         * @memberOf ApplicationPlatypusEntity
         */
        P.ApplicationPlatypusEntity.prototype.removeAll = function() {
            var delegate = this.unwrap();
            var value = delegate.removeAll();
            return P.boxAsJs(value);
        };

        /**
         * Sorts data according to comparator object returned by createSorting() or by comparator function.
         * @param comparator A comparator function or object returned from createSorting() method.
         * @method sort
         * @memberOf ApplicationPlatypusEntity
         */
        P.ApplicationPlatypusEntity.prototype.sort = function(comparator) {
            var delegate = this.unwrap();
            var value = delegate.sort(P.boxAsJava(comparator));
            return P.boxAsJs(value);
        };

        /**
         * Creates an instace of filter object to filter rowset data in-place using specified constraints objects.
         * @param fields The filter conditions fields in following form: entity.schema.propName or just a propName in a string form.
         * @return a comparator object.
         * @method createFilter
         * @memberOf ApplicationPlatypusEntity
         */
        P.ApplicationPlatypusEntity.prototype.createFilter = function(fields) {
            var delegate = this.unwrap();
            var value = delegate.createFilter(P.boxAsJava(fields));
            return P.boxAsJs(value);
        };

        /**
         * Finds an object by its key. Key must be a single property.
         * @param key the unique identifier of the row.
         * @return An object or <code>null</code> if nothing is found.
         * @method findById
         * @memberOf ApplicationPlatypusEntity
         */
        P.ApplicationPlatypusEntity.prototype.findById = function(key) {
            var delegate = this.unwrap();
            var value = delegate.findById(P.boxAsJava(key));
            return P.boxAsJs(value);
        };

        /**
         * Sets the array cursor to the specified object.
         * @param object the object to position the entity cursor on.
         * @return <code>true</code> if the cursor changed successfully and <code>false</code> otherwise.
         * @method scrollTo
         * @memberOf ApplicationPlatypusEntity
         */
        P.ApplicationPlatypusEntity.prototype.scrollTo = function(row) {
            var delegate = this.unwrap();
            var value = delegate.scrollTo(P.boxAsJava(row));
            return P.boxAsJs(value);
        };

        /**
         * Requeries the entity's data. Forses the entity to refresh its data, no matter if its parameters has changed or not.
         * @param onSuccess The callback function for refresh data on success event (optional).
         * @param onFailure The callback function for refresh data on failure event (optional).
         * @method requery
         * @memberOf ApplicationPlatypusEntity
         */
        P.ApplicationPlatypusEntity.prototype.requery = function(onSuccess, onFailure) {
            var delegate = this.unwrap();
            var value = delegate.requery(P.boxAsJava(onSuccess), P.boxAsJava(onFailure));
            return P.boxAsJs(value);
        };

        /**
         * Finds an object by its key. Key must be a single property.
         * @param key the unique identifier of the row.
         * @return An object or <code>null</code> if nothing is found.
         * @method findByKey
         * @memberOf ApplicationPlatypusEntity
         */
        P.ApplicationPlatypusEntity.prototype.findByKey = function(key) {
            var delegate = this.unwrap();
            var value = delegate.findByKey(P.boxAsJava(key));
            return P.boxAsJs(value);
        };

        /**
         * Creates an instance of comparator object using specified constraints objects.
         * @param pairs the sort criteria pairs, in a form of property object (e.g. entity.schema.propName or just a propName in a string form) and the order of sort (ascending - true; descending - false).
         * @return a comparator object to be passed as a parameter to entity's <code>sort</code> method.
         * @method createSorting
         * @memberOf ApplicationPlatypusEntity
         */
        P.ApplicationPlatypusEntity.prototype.createSorting = function(pairs) {
            var delegate = this.unwrap();
            var value = delegate.createSorting(P.boxAsJava(pairs));
            return P.boxAsJs(value);
        };

})();