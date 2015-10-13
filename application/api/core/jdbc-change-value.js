/* global Java */

define(['boxing'], function(B) {
    var className = "com.eas.client.changes.JdbcChangeValue";
    var javaClass = Java.type(className);
    /**
     * Generated constructor.
     * @constructor JdbcChangeValue JdbcChangeValue
     */
    function JdbcChangeValue() {
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
        if(JdbcChangeValue.superclass)
            JdbcChangeValue.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return B.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "value", {
            get: function() {
                var value = delegate.value;
                return B.boxAsJs(value);
            }
        });

    };

    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var space = ScriptsClass.getSpace();
    space.putPublisher(className, function(aDelegate) {
        return new JdbcChangeValue(aDelegate);
    });
    return JdbcChangeValue;
});