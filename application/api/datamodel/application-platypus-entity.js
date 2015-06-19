(function() {
    var className = "com.eas.client.model.application.ApplicationPlatypusEntity";
    var javaClass = Java.type(className);
    var space = this['-platypus-scripts-space'];
    space.putPublisher(className, function(aDelegate) {
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
         * Append data to the entity's data. Appended data will be managed by ORM.* @param data The plain js objects array to be appended.
         * @method append
         * @memberOf ApplicationPlatypusEntity
         */
        P.ApplicationPlatypusEntity.prototype.append = function(data) {
            var delegate = this.unwrap();
            var value = delegate.append(P.boxAsJava(data));
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
         * Queries the entity's data. Data will be fresh copy. A call to query() will be independent from other calls.
         * Subsequent calls will not cancel requests made within previous calls.
         * @param params The params object with parameters' values of query. These values will not be written to entity's parameters.
         * @param onSuccess The callback function for fresh data on success event (optional).
         * @param onFailure The callback function for fresh data on failure event (optional).
         * @method query
         * @memberOf ApplicationPlatypusEntity
         */
        P.ApplicationPlatypusEntity.prototype.query = function(params, onSuccess, onFailure) {
            var delegate = this.unwrap();
            var value = delegate.query(P.boxAsJava(params), P.boxAsJava(onSuccess), P.boxAsJava(onFailure));
            return P.boxAsJs(value);
        };

        /**
         * Requeries the entity's data. Forses the entity to refresh its data, no matter if its parameters has changed or not.
         * @param onSuccess The callback function for refreshed data on success event (optional).
         * @param onFailure The callback function for refreshed data on failure event (optional).
         * @method requery
         * @memberOf ApplicationPlatypusEntity
         */
        P.ApplicationPlatypusEntity.prototype.requery = function(onSuccess, onFailure) {
            var delegate = this.unwrap();
            var value = delegate.requery(P.boxAsJava(onSuccess), P.boxAsJava(onFailure));
            return P.boxAsJs(value);
        };

})();