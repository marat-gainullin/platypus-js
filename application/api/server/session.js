/* global Java */

define(['boxing'], function(B) {
    var className = "com.eas.server.Session";
    var javaClass = Java.type(className);
    /**
     * Generated constructor.
     * @constructor Session Session
     */
    function Session() {
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
        if(Session.superclass)
            Session.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "modules", {
            get: function() {
                var value = delegate.modules;
                return value;
            }
        });

    };

    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var space = ScriptsClass.getSpace();
    space.putPublisher(className, function(aDelegate) {
        return new Session(aDelegate);
    });
    return Session;
});