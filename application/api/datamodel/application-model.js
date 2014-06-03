(function() {
    var javaClass = Java.type("com.eas.client.model.application.ApplicationModel");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ApplicationModel(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @namespace ApplicationModel
     */
    P.ApplicationModel = function () {

        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
            arguments[maxArgs] : new javaClass();

        Object.defineProperty(this, "unwrap", {
            get: function() {
                return function() {
                    return delegate;
                };
            }
        });
        /**
        * Refreshes the model, only if any of its parameters has changed.
        * @param onSuccessCallback the handler function for refresh data on success event (optional).
        * @param onFailureCallback the handler function for refresh data on failure event (optional).
         * @method execute
         * @memberOf ApplicationModel
        */
        Object.defineProperty(this, "execute", {
            get: function() {
                return function() {
                    var args = [];
                    for(var a = 0; a < arguments.length; a++){
                        args[a] = P.boxAsJava(arguments[a]);
                    }
                    var value = delegate.execute.apply(delegate, args);
                    return P.boxAsJs(value);
                };
            }
        });

        /**
        * Saves model data changes.
        * If model can't apply the changed data, than exception is thrown. In this case, application can call model.save() another time to save the changes.
        * If an application needs to abort futher attempts and discard model data changes, use <code>model.revert()</code>.
        * @param callback the function to be envoked after the data changes saved (optional).
         * @method save
         * @memberOf ApplicationModel
        */
        Object.defineProperty(this, "save", {
            get: function() {
                return function() {
                    var args = [];
                    for(var a = 0; a < arguments.length; a++){
                        args[a] = P.boxAsJava(arguments[a]);
                    }
                    var value = delegate.save.apply(delegate, args);
                    return P.boxAsJs(value);
                };
            }
        });

        /**
        * Reverts model data changes.
        * After this method call, no data changes are avaliable for <code>model.save()</code> method, but the model still attempts to commit.
        * Call <code>model.save()</code> on commitable and unchanged model nevertheless leads to a commit.
         * @method revert
         * @memberOf ApplicationModel
        */
        Object.defineProperty(this, "revert", {
            get: function() {
                return function() {
                    var args = [];
                    for(var a = 0; a < arguments.length; a++){
                        args[a] = P.boxAsJava(arguments[a]);
                    }
                    var value = delegate.revert.apply(delegate, args);
                    return P.boxAsJs(value);
                };
            }
        });

        /**
        * Requeries the model data. Forses the model data refresh, no matter if its parameters has changed or not.
        * @param onSuccessCallback the handler function for refresh data on success event (optional).
        * @param onFailureCallback the handler function for refresh data on failure event (optional).
         * @method requery
         * @memberOf ApplicationModel
        */
        Object.defineProperty(this, "requery", {
            get: function() {
                return function() {
                    var args = [];
                    for(var a = 0; a < arguments.length; a++){
                        args[a] = P.boxAsJava(arguments[a]);
                    }
                    var value = delegate.requery.apply(delegate, args);
                    return P.boxAsJs(value);
                };
            }
        });

        /**
        * Creates new entity of model, based on application query.
        * @param queryId the query application element ID.
        * @return a new entity.
         * @method loadEntity
         * @memberOf ApplicationModel
        */
        Object.defineProperty(this, "loadEntity", {
            get: function() {
                return function() {
                    var args = [];
                    for(var a = 0; a < arguments.length; a++){
                        args[a] = P.boxAsJava(arguments[a]);
                    }
                    var value = delegate.loadEntity.apply(delegate, args);
                    return P.boxAsJs(value);
                };
            }
        });


        delegate.setPublished(this);
    };
})();