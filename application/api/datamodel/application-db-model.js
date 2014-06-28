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
         * Saves model data changes.
         * If model can't apply the changed data, than exception is thrown. In this case, application can call model.save() another time to save the changes.
         * If an application needs to abort futher attempts and discard model data changes, use <code>model.revert()</code>.
         * @param callback the function to be envoked after the data changes saved (optional)
         * @method save
         * @memberOf ApplicationDbModel
         */
        P.ApplicationDbModel.prototype.save = function(callback) {
            var delegate = this.unwrap();
            var value = delegate.save(P.boxAsJava(callback));
            return P.boxAsJs(value);
        };

        /**
         * Requeries the model data. Forses the model data refresh, no matter if its parameters has changed or not.
         * @param onSuccessCallback the handler function for refresh data on success event (optional).
         * @param onFailureCallback the handler function for refresh data on failure event (optional).
         * @method requery
         * @memberOf ApplicationDbModel
         */
        P.ApplicationDbModel.prototype.requery = function(onSuccessCallback, onFailureCallback) {
            var delegate = this.unwrap();
            var value = delegate.requery(P.boxAsJava(onSuccessCallback), P.boxAsJava(onFailureCallback));
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
         * Executes a SQL query against specific datasource. This method works only in two tier components of a system.
         * @param sqlText SQL text for the new entity.
         * @param dbId Optional. the concrete database ID (optional).
         * @return an entity instance.
         * @method executeSql
         * @memberOf ApplicationDbModel
         */
        P.ApplicationDbModel.prototype.executeSql = function(sqlText, datasourceName) {
            var delegate = this.unwrap();
            var value = delegate.executeSql(P.boxAsJava(sqlText), P.boxAsJava(datasourceName));
            return P.boxAsJs(value);
        };

        /**
         * Refreshes the model, only if any of its parameters has changed.
         * @param onSuccessCallback the handler function for refresh data on success event (optional).
         * @param onFailureCallback the handler function for refresh data on failure event (optional).
         * @method execute
         * @memberOf ApplicationDbModel
         */
        P.ApplicationDbModel.prototype.execute = function(onSuccessCallback, onFailureCallback) {
            var delegate = this.unwrap();
            var value = delegate.execute(P.boxAsJava(onSuccessCallback), P.boxAsJava(onFailureCallback));
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