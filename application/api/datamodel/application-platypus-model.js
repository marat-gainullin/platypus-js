(function() {
    var javaClass = Java.type("com.eas.client.model.application.ApplicationPlatypusModel");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ApplicationPlatypusModel(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor ApplicationPlatypusModel ApplicationPlatypusModel
     */
    P.ApplicationPlatypusModel = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.ApplicationPlatypusModel.superclass)
            P.ApplicationPlatypusModel.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
    };
        /**
         * Saves model data changes. Calls onSuccess when done.
         * If model can't apply the changed, than exception is thrown.
         * In this case, application can call model.save() another time to save the changes.
         * If an application need to abort futher attempts and discard model data changes, than it can call model.revert().
         * @param onSuccess Success callback.
         * @method save
         * @memberOf ApplicationPlatypusModel
         * @param onFailure Failure callback.
        P.ApplicationPlatypusModel.prototype.save = function(arg0, arg1) {
            var delegate = this.unwrap();
            var value = delegate.save(P.boxAsJava(arg0), P.boxAsJava(arg1));
            return P.boxAsJs(value);
        };

        /**
         * Requeries the model data. Forces the model data refresh, no matter if its parameters has changed or not.
         * @param onSuccess The handler function for refresh data on success event (optional).
         * @param onFailure The handler function for refresh data on failure event (optional).
         * @method requery
         * @memberOf ApplicationPlatypusModel
         */
        P.ApplicationPlatypusModel.prototype.requery = function(onSuccess, onFailure) {
            var delegate = this.unwrap();
            var value = delegate.requery(P.boxAsJava(onSuccess), P.boxAsJava(onFailure));
            return P.boxAsJs(value);
        };

        /**
         * Refreshes the model, only if any of its parameters has changed.
         * @param onSuccess The handler function for refresh data on success event (optional).
         * @param onFailure The handler function for refresh data on failure event (optional).
         * @method execute
         * @memberOf ApplicationPlatypusModel
         */
        P.ApplicationPlatypusModel.prototype.execute = function(onSuccess, onFailure) {
            var delegate = this.unwrap();
            var value = delegate.execute(P.boxAsJava(onSuccess), P.boxAsJava(onFailure));
            return P.boxAsJs(value);
        };

        /**
         * Creates new entity of model, based on application query.
         * @param queryId the query application element ID.
         * @return a new entity.
         * @method loadEntity
         * @memberOf ApplicationPlatypusModel
         */
        P.ApplicationPlatypusModel.prototype.loadEntity = function(queryId) {
            var delegate = this.unwrap();
            var value = delegate.loadEntity(P.boxAsJava(queryId));
            return P.boxAsJs(value);
        };

})();