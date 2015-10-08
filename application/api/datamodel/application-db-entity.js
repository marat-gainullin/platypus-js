/* global Java */

define(['boxing'], function(B) {
    var className = "com.eas.client.model.application.ApplicationDbEntity";
    var javaClass = Java.type(className);
    /**
     * Generated constructor.
     * @constructor ApplicationDbEntity ApplicationDbEntity
     */
    function ApplicationDbEntity() {
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
        if(ApplicationDbEntity.superclass)
            ApplicationDbEntity.superclass.constructor.apply(this, arguments);
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

        Object.defineProperty(this, "elementClass", {
            get: function() {
                var value = delegate.elementClass;
                return value;
            },
            set: function(aValue) {
                delegate.elementClass = aValue;
            }
        });

    };
    /**
     * Applies the updates into the database and commits the transaction.
     * @param onSuccess Success callback. It has an argument, - updates rows count.
     * @param onFailure Failure callback. It has an argument, - exception occured while applying updates into the database.
     * @method executeUpdate
     * @memberOf ApplicationDbEntity
     */
    ApplicationDbEntity.prototype.executeUpdate = function(onSuccess, onFailure) {
        var delegate = this.unwrap();
        var value = delegate.executeUpdate(B.boxAsJava(onSuccess), B.boxAsJava(onFailure));
        return B.boxAsJs(value);
    };

    /**
     * Adds the updates into the change log as a command.
     * @method enqueueUpdate
     * @memberOf ApplicationDbEntity
     */
    ApplicationDbEntity.prototype.enqueueUpdate = function() {
        var delegate = this.unwrap();
        var value = delegate.enqueueUpdate();
        return B.boxAsJs(value);
    };

    /**
     * Append data to the entity's data. Appended data will be managed by ORM.* @param data The plain js objects array to be appended.
     * @method append
     * @memberOf ApplicationDbEntity
     */
    ApplicationDbEntity.prototype.append = function(data) {
        var delegate = this.unwrap();
        var value = delegate.append(B.boxAsJava(data));
        return B.boxAsJs(value);
    };

    /**
     * Refreshes entity, only if any of its parameters has changed.
     * @param onSuccess The handler function for refresh data on success event (optional).
     * @param onFailure The handler function for refresh data on failure event (optional).
     * @method execute
     * @memberOf ApplicationDbEntity
     */
    ApplicationDbEntity.prototype.execute = function(onSuccess, onFailure) {
        var delegate = this.unwrap();
        var value = delegate.execute(B.boxAsJava(onSuccess), B.boxAsJava(onFailure));
        return B.boxAsJs(value);
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
    ApplicationDbEntity.prototype.query = function(params, onSuccess, onFailure) {
        var delegate = this.unwrap();
        var value = delegate.query(B.boxAsJava(params), B.boxAsJava(onSuccess), B.boxAsJava(onFailure));
        return B.boxAsJs(value);
    };

    /**
     * Requeries the entity's data. Forses the entity to refresh its data, no matter if its parameters has changed or not.
     * @param onSuccess The callback function for refreshed data on success event (optional).
     * @param onFailure The callback function for refreshed data on failure event (optional).
     * @method requery
     * @memberOf ApplicationDbEntity
     */
    ApplicationDbEntity.prototype.requery = function(onSuccess, onFailure) {
        var delegate = this.unwrap();
        var value = delegate.requery(B.boxAsJava(onSuccess), B.boxAsJava(onFailure));
        return B.boxAsJs(value);
    };


    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var space = ScriptsClass.getSpace();
    space.putPublisher(className, function(aDelegate) {
        return new ApplicationDbEntity(aDelegate);
    });
    return ApplicationDbEntity;
});