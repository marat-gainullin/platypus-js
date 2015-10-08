/* global Java */

define(['boxing'], function(B) {
    var className = "com.eas.client.model.application.ApplicationDbModel";
    var javaClass = Java.type(className);
    /**
     * Generated constructor.
     * @constructor ApplicationDbModel ApplicationDbModel
     */
    function ApplicationDbModel() {
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
        if(ApplicationDbModel.superclass)
            ApplicationDbModel.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "modified", {
            get: function() {
                var value = delegate.modified;
                return B.boxAsJs(value);
            }
        });

    };
    /**
     * Creates new entity of model, based on passed sql query. This method works only in two tier components of a system.
     * @param sqlText SQL text for the new entity.
     * @param datasourceName the concrete database ID (optional).
     * @return an entity instance.
     * @method createEntity
     * @memberOf ApplicationDbModel
     */
    ApplicationDbModel.prototype.createEntity = function(sqlText, datasourceName) {
        var delegate = this.unwrap();
        var value = delegate.createEntity(B.boxAsJava(sqlText), B.boxAsJava(datasourceName));
        return B.boxAsJs(value);
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
    ApplicationDbModel.prototype.executeSql = function(sqlText, datasourceName, arg2, arg3) {
        var delegate = this.unwrap();
        var value = delegate.executeSql(B.boxAsJava(sqlText), B.boxAsJava(datasourceName), B.boxAsJava(arg2), B.boxAsJava(arg3));
        return B.boxAsJs(value);
    };

    /**
     * Reverts model data changes.
     * After this method call, no data changes are avaliable for <code>model.save()</code> method.
     * @method revert
     * @memberOf ApplicationDbModel
     */
    ApplicationDbModel.prototype.revert = function() {
        var delegate = this.unwrap();
        var value = delegate.revert();
        return B.boxAsJs(value);
    };

    /**
     * Requeries the model data. Forces the model data refresh, no matter if its parameters has changed or not.
     * @param onSuccess The handler function for refresh data on success event (optional).
     * @param onFailure The handler function for refresh data on failure event (optional).
     * @method requery
     * @memberOf ApplicationDbModel
     */
    ApplicationDbModel.prototype.requery = function(onSuccess, onFailure) {
        var delegate = this.unwrap();
        var value = delegate.requery(B.boxAsJava(onSuccess), B.boxAsJava(onFailure));
        return B.boxAsJs(value);
    };

    /**
     * Refreshes the model, only if any of its parameters has changed.
     * @param onSuccess The handler function for refresh data on success event (optional).
     * @param onFailure The handler function for refresh data on failure event (optional).
     * @method execute
     * @memberOf ApplicationDbModel
     */
    ApplicationDbModel.prototype.execute = function(onSuccess, onFailure) {
        var delegate = this.unwrap();
        var value = delegate.execute(B.boxAsJava(onSuccess), B.boxAsJava(onFailure));
        return B.boxAsJs(value);
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
    ApplicationDbModel.prototype.save = function(onSuccess, onFailure) {
        var delegate = this.unwrap();
        var value = delegate.save(B.boxAsJava(onSuccess), B.boxAsJava(onFailure));
        return B.boxAsJs(value);
    };

    /**
     * Creates new entity of model, based on application query.
     * @param queryId the query application element ID.
     * @return a new entity.
     * @method loadEntity
     * @memberOf ApplicationDbModel
     */
    ApplicationDbModel.prototype.loadEntity = function(queryId) {
        var delegate = this.unwrap();
        var value = delegate.loadEntity(B.boxAsJava(queryId));
        return B.boxAsJs(value);
    };


    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var space = ScriptsClass.getSpace();
    space.putPublisher(className, function(aDelegate) {
        return new ApplicationDbModel(aDelegate);
    });
    return ApplicationDbModel;
});