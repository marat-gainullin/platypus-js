/* global Java */

define(['boxing'], function(B) {
    var className = "com.eas.client.changes.Delete";
    var javaClass = Java.type(className);
    /**
     * Generated constructor.
     * @constructor Delete Delete
     */
    function Delete() {
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
        if(Delete.superclass)
            Delete.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "keys", {
            get: function() {
                var value = delegate.keys;
                return B.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "type", {
            get: function() {
                var value = delegate.type;
                return B.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "entity", {
            get: function() {
                var value = delegate.entity;
                return B.boxAsJs(value);
            }
        });

    };

    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var space = ScriptsClass.getSpace();
    space.putPublisher(className, function(aDelegate) {
        return new Delete(aDelegate);
    });
    return Delete;
});