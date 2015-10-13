/* global Java */

define(['boxing'], function(B) {
    var className = "com.eas.client.model.application.ApplicationPlatypusModel";
    var javaClass = Java.type(className);
    /**
     * Generated constructor.
     * @constructor ApplicationPlatypusModel ApplicationPlatypusModel
     */
    function ApplicationPlatypusModel() {
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
        if(ApplicationPlatypusModel.superclass)
            ApplicationPlatypusModel.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
    };
    /**
     * Saves model data changes.
     * If model can't apply the changed data, than exception is thrown. In this case, application can call model.save() another time to save the changes.
     * If an application needs to abort further attempts and discard model data changes, use <code>model.revert()</code>.
     * Note, that a <code>model.save()</code> call on unchanged model nevertheless leads to a commit.
     * @param onSuccess The function to be invoked after the data changes saved (optional).
     * @param onFailure The function to be invoked when exception raised while commit process (optional).
     * @method save
     * @memberOf ApplicationPlatypusModel
     */
    ApplicationPlatypusModel.prototype.save = function(onSuccess, onFailure) {
        var delegate = this.unwrap();
        var value = delegate.save(B.boxAsJava(onSuccess), B.boxAsJava(onFailure));
        return B.boxAsJs(value);
    };

    /**
     * Reverts model data changes.
     * After this method call, no data changes are avaliable for <code>model.save()</code> method.
     * @method revert
     * @memberOf ApplicationPlatypusModel
     */
    ApplicationPlatypusModel.prototype.revert = function() {
        var delegate = this.unwrap();
        var value = delegate.revert();
        return B.boxAsJs(value);
    };

    /**
     * Requeries the model data. Forces the model data refresh, no matter if its parameters has changed or not.
     * @param onSuccess The handler function for refresh data on success event (optional).
     * @param onFailure The handler function for refresh data on failure event (optional).
     * @method requery
     * @memberOf ApplicationPlatypusModel
     */
    ApplicationPlatypusModel.prototype.requery = function(onSuccess, onFailure) {
        var delegate = this.unwrap();
        var value = delegate.requery(B.boxAsJava(onSuccess), B.boxAsJava(onFailure));
        return B.boxAsJs(value);
    };

    /**
     * Refreshes the model, only if any of its parameters has changed.
     * @param onSuccess The handler function for refresh data on success event (optional).
     * @param onFailure The handler function for refresh data on failure event (optional).
     * @method execute
     * @memberOf ApplicationPlatypusModel
     */
    ApplicationPlatypusModel.prototype.execute = function(onSuccess, onFailure) {
        var delegate = this.unwrap();
        var value = delegate.execute(B.boxAsJava(onSuccess), B.boxAsJava(onFailure));
        return B.boxAsJs(value);
    };

    /**
     * Creates new entity of model, based on application query.
     * @param queryId the query application element ID.
     * @return a new entity.
     * @method loadEntity
     * @memberOf ApplicationPlatypusModel
     */
    ApplicationPlatypusModel.prototype.loadEntity = function(queryId) {
        var delegate = this.unwrap();
        var value = delegate.loadEntity(B.boxAsJava(queryId));
        return B.boxAsJs(value);
    };


    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var space = ScriptsClass.getSpace();
    space.putPublisher(className, function(aDelegate) {
        return new ApplicationPlatypusModel(aDelegate);
    });
    return ApplicationPlatypusModel;
});