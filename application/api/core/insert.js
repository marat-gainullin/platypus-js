/* global Java */

define(['boxing'], function(B) {
    var className = "com.eas.client.changes.Insert";
    var javaClass = Java.type(className);
    /**
     * Generated constructor.
     * @constructor Insert Insert
     */
    function Insert() {
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
        if(Insert.superclass)
            Insert.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "data", {
            get: function() {
                var value = delegate.data;
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
        return new Insert(aDelegate);
    });
    return Insert;
});