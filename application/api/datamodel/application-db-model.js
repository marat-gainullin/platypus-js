(function() {
    var javaClass = Java.type("com.eas.client.model.application.ApplicationDbModel");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ApplicationDbModel(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor ApplicationDbModel ApplicationDbModel
     */
    P.ApplicationDbModel = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.ApplicationDbModel.superclass)
            P.ApplicationDbModel.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
    };
        /**
         * Requeries the model data. Forces the model data refresh, no matter if its parameters has changed or not.
         * @param onSuccess The handler function for refresh data on success event (optional).
         * @param onFailure The handler function for refresh data on failure event (optional).
         * @method requery
         * @memberOf ApplicationDbModel
         */
        P.ApplicationDbModel.prototype.requery = function(onSuccess, onFailure) {
            var delegate = this.unwrap();
            var value = delegate.requery(P.boxAsJava(onSuccess), P.boxAsJava(onFailure));
            return P.boxAsJs(value);
        };

        /**
         * Reverts model data changes.
         * After this method call, no data changes are avaliable for <code>model.save()</code> method.
         * @method revert
         * @memberOf ApplicationDbModel
         */
        P.ApplicationDbModel.prototype.revert = function() {
            var delegate = this.unwrap();
            var value = delegate.revert();
            return P.boxAsJs(value);
        };

        /**
         * Executes a SQL query against specific datasource. This method works only in two tier components of a system.
         * @param sqlText SQL text for the new entity.
         * @param datasourceName. The specific databsource name (optional).
         * @param onSuccess Success callback. Have a number argument, indicating updated rows count (optional).
         * @param onFailure Failure callback. Have a string argument, indicating an error occured (optional).
         * @return an entity instance.
         * @method executeSql
         * @memberOf ApplicationDbModel
         */
        P.ApplicationDbModel.prototype.executeSql = function(sqlText, datasourceName, arg2, arg3) {
            var delegate = this.unwrap();
            var value = delegate.executeSql(P.boxAsJava(sqlText), P.boxAsJava(datasourceName), P.boxAsJava(arg2), P.boxAsJava(arg3));
            return P.boxAsJs(value);
        };

        /**
         * Creates new entity of model, based on passed sql query. This method works only in two tier components of a system.
         * @param sqlText SQL text for the new entity.
         * @param dbId the concrete database ID (optional).
         * @return an entity instance.
         * @method createEntity
         * @memberOf ApplicationDbModel
         */
        P.ApplicationDbModel.prototype.createEntity = function(sqlText, datasourceName) {
            var delegate = this.unwrap();
            var value = delegate.createEntity(P.boxAsJava(sqlText), P.boxAsJava(datasourceName));
            return P.boxAsJs(value);
        };

        /**
         * Refreshes the model, only if any of its parameters has changed.
         * @param onSuccess The handler function for refresh data on success event (optional).
         * @param onFailure The handler function for refresh data on failure event (optional).
         * @method execute
         * @memberOf ApplicationDbModel
         */
        P.ApplicationDbModel.prototype.execute = function(onSuccess, onFailure) {
            var delegate = this.unwrap();
            var value = delegate.execute(P.boxAsJava(onSuccess), P.boxAsJava(onFailure));
            return P.boxAsJs(value);
        };

        /**
         * Saves model data changes.
         * If model can't apply the changed data, than exception is thrown. In this case, application can call model.save() another time to save the changes.
         * If an application needs to abort further attempts and discard model data changes, use <code>model.revert()</code>.
         * Note, that a <code>model.save()</code> call on unchanged model nevertheless leads to a commit.
         * @param onSuccess The function to be invoked after the data changes saved (optional).
         * @param onFailure The function to be invoked when exception raised while commit process (optional).
         * @method save
         * @memberOf ApplicationDbModel
         */
        P.ApplicationDbModel.prototype.save = function(onSuccess, onFailure) {
            var delegate = this.unwrap();
            var value = delegate.save(P.boxAsJava(onSuccess), P.boxAsJava(onFailure));
            return P.boxAsJs(value);
        };

        /**
         * Creates new entity of model, based on application query.
         * @param queryId the query application element ID.
         * @return a new entity.
         * @method loadEntity
         * @memberOf ApplicationDbModel
         */
        P.ApplicationDbModel.prototype.loadEntity = function(queryId) {
            var delegate = this.unwrap();
            var value = delegate.loadEntity(P.boxAsJava(queryId));
            return P.boxAsJs(value);
        };

})();