(function() {
    var className = "com.eas.client.model.application.ApplicationDbEntity";
    var javaClass = Java.type(className);
    var space = this['-platypus-scripts-space'];
    space.putPublisher(className, function(aDelegate) {
        return new P.ApplicationDbEntity(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor ApplicationDbEntity ApplicationDbEntity
     */
    P.ApplicationDbEntity = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            configurable: true,
            value: function() {
                return delegate;
            }
        });
        if(P.ApplicationDbEntity.superclass)
            P.ApplicationDbEntity.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "onRequeried", {
            get: function() {
                var value = delegate.onRequeried;
                return value;
            },
            set: function(aValue) {
                delegate.onRequeried = aValue;
            }
        });
        if(!P.ApplicationDbEntity){
            /**
             * The handler function for the event occured after the entity's data have been requeried.
             * @property onRequeried
             * @memberOf ApplicationDbEntity
             */
            P.ApplicationDbEntity.prototype.onRequeried = {};
        }
        Object.defineProperty(this, "elementClass", {
            get: function() {
                var value = delegate.elementClass;
                return value;
            },
            set: function(aValue) {
                delegate.elementClass = aValue;
            }
        });
        if(!P.ApplicationDbEntity){
            /**
             * Experimental. The constructor funciton for the entity's data array elements.
             * @property elementClass
             * @memberOf ApplicationDbEntity
             */
            P.ApplicationDbEntity.prototype.elementClass = {};
        }
    };
        /**
         * Applies the updates into the database and commits the transaction.
         * @param onSuccess Success callback. It has an argument, - updates rows count.
         * @param onFailure Failure callback. It has an argument, - exception occured while applying updates into the database.
         * @method executeUpdate
         * @memberOf ApplicationDbEntity
         */
        P.ApplicationDbEntity.prototype.executeUpdate = function(onSuccess, onFailure) {
            var delegate = this.unwrap();
            var value = delegate.executeUpdate(P.boxAsJava(onSuccess), P.boxAsJava(onFailure));
            return P.boxAsJs(value);
        };

        /**
         * Adds the updates into the change log as a command.
         * @method enqueueUpdate
         * @memberOf ApplicationDbEntity
         */
        P.ApplicationDbEntity.prototype.enqueueUpdate = function() {
            var delegate = this.unwrap();
            var value = delegate.enqueueUpdate();
            return P.boxAsJs(value);
        };

        /**
         * Append data to the entity's data. Appended data will be managed by ORM.* @param data The plain js objects array to be appended.
         * @method append
         * @memberOf ApplicationDbEntity
         */
        P.ApplicationDbEntity.prototype.append = function(data) {
            var delegate = this.unwrap();
            var value = delegate.append(P.boxAsJava(data));
            return P.boxAsJs(value);
        };

        /**
         * Refreshes entity, only if any of its parameters has changed.
         * @param onSuccess The handler function for refresh data on success event (optional).
         * @param onFailure The handler function for refresh data on failure event (optional).
         * @method execute
         * @memberOf ApplicationDbEntity
         */
        P.ApplicationDbEntity.prototype.execute = function(onSuccess, onFailure) {
            var delegate = this.unwrap();
            var value = delegate.execute(P.boxAsJava(onSuccess), P.boxAsJava(onFailure));
            return P.boxAsJs(value);
        };

        /**
         * Queries the entity's data. Data will be fresh copy. A call to query() will be independent from other calls.
         * Subsequent calls will not cancel requests made within previous calls.
         * @param params The params object with parameters' values of query. These values will not be written to entity's parameters.
         * @param onSuccess The callback function for fresh data on success event (optional).
         * @param onFailure The callback function for fresh data on failure event (optional).
         * @method query
         * @memberOf ApplicationDbEntity
         */
        P.ApplicationDbEntity.prototype.query = function(params, onSuccess, onFailure) {
            var delegate = this.unwrap();
            var value = delegate.query(P.boxAsJava(params), P.boxAsJava(onSuccess), P.boxAsJava(onFailure));
            return P.boxAsJs(value);
        };

        /**
         * Requeries the entity's data. Forses the entity to refresh its data, no matter if its parameters has changed or not.
         * @param onSuccess The callback function for refreshed data on success event (optional).
         * @param onFailure The callback function for refreshed data on failure event (optional).
         * @method requery
         * @memberOf ApplicationDbEntity
         */
        P.ApplicationDbEntity.prototype.requery = function(onSuccess, onFailure) {
            var delegate = this.unwrap();
            var value = delegate.requery(P.boxAsJava(onSuccess), P.boxAsJava(onFailure));
            return P.boxAsJs(value);
        };

})();