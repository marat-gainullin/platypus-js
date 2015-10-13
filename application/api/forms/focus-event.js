/* global Java */

define(['boxing'], function(B) {
    var className = "com.eas.client.forms.events.FocusEvent";
    var javaClass = Java.type(className);
    /**
     * Generated constructor.
     * @constructor FocusEvent FocusEvent
     */
    function FocusEvent() {
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
        if(FocusEvent.superclass)
            FocusEvent.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "source", {
            get: function() {
                var value = delegate.source;
                return B.boxAsJs(value);
            }
        });

    };

    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var space = ScriptsClass.getSpace();
    space.putPublisher(className, function(aDelegate) {
        return new FocusEvent(aDelegate);
    });
    return FocusEvent;
});