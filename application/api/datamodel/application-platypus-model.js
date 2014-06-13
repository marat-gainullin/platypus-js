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
            get: function() {
                return function() {
                    return delegate;
                };
            }
        });
        /**
         * Saves model data changes. Calls aCallback when done.
         * If model can't apply the changed, than exception is thrown.
         * In this case, application can call model.save() another time to save the changes.
         * @method save
         * @memberOf ApplicationPlatypusModel
         * If an application need to abort futher attempts and discard model data changes, than it can call model.revert().
        Object.defineProperty(this, "save", {
            get: function() {
                return function(arg0) {
                    var value = delegate.save(P.boxAsJava(arg0));
                    return P.boxAsJs(value);
                };
            }
        });

        /**
         * Requeries model data. Calls onSuccess callback when complete and onError callback if error occured.
         * @method requery
         * @memberOf ApplicationPlatypusModel
         */
        Object.defineProperty(this, "requery", {
            get: function() {
                return function(onSuccess, onFailure) {
                    var value = delegate.requery(P.boxAsJava(onSuccess), P.boxAsJava(onFailure));
                    return P.boxAsJs(value);
                };
            }
        });

        /**
         * Refreshes the model, only if any of its parameters has changed.
         * @param onSuccessCallback the handler function for refresh data on success event (optional).
         * @param onFailureCallback the handler function for refresh data on failure event (optional).
         * @method execute
         * @memberOf ApplicationPlatypusModel
         */
        Object.defineProperty(this, "execute", {
            get: function() {
                return function(onSuccessCallback, onFailureCallback) {
                    var value = delegate.execute(P.boxAsJava(onSuccessCallback), P.boxAsJava(onFailureCallback));
                    return P.boxAsJs(value);
                };
            }
        });

        /**
         * Creates new entity of model, based on application query.
         * @param queryId the query application element ID.
         * @return a new entity.
         * @method loadEntity
         * @memberOf ApplicationPlatypusModel
         */
        Object.defineProperty(this, "loadEntity", {
            get: function() {
                return function(queryId) {
                    var value = delegate.loadEntity(P.boxAsJava(queryId));
                    return P.boxAsJs(value);
                };
            }
        });


        delegate.setPublished(this);
    };
})();