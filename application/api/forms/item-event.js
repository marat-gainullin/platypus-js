/* global Java */

define(['boxing'], function(B) {
    var className = "com.eas.client.forms.events.ItemEvent";
    var javaClass = Java.type(className);
    /**
     * Generated constructor.
     * @constructor ItemEvent ItemEvent
     */
    function ItemEvent() {
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
        if(ItemEvent.superclass)
            ItemEvent.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "item", {
            get: function() {
                var value = delegate.item;
                return value;
            }
        });

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
        return new ItemEvent(aDelegate);
    });
    return ItemEvent;
});