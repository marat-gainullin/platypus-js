/* global Java */

define(['boxing'], function(B) {
    var className = "com.eas.client.model.application.CursorPositionWillChangeEvent";
    var javaClass = Java.type(className);
    /**
     * Generated constructor.
     * @constructor CursorPositionWillChangeEvent CursorPositionWillChangeEvent
     */
    function CursorPositionWillChangeEvent() {
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
        if(CursorPositionWillChangeEvent.superclass)
            CursorPositionWillChangeEvent.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        /**
         * Cursor position the cursor is still on.
         */
        this.oldIndex = 0;
        Object.defineProperty(this, "oldIndex", {
            get: function() {
                var value = delegate.oldIndex;
                return B.boxAsJs(value);
            }
        });

        /**
         * The source object of the event.
         */
        this.source = new Object();
        Object.defineProperty(this, "source", {
            get: function() {
                var value = delegate.source;
                return B.boxAsJs(value);
            }
        });

        /**
         * Cursor position the cursor will be set on.
         */
        this.newIndex = 0;
        Object.defineProperty(this, "newIndex", {
            get: function() {
                var value = delegate.newIndex;
                return B.boxAsJs(value);
            }
        });

    }

    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var space = ScriptsClass.getSpace();
    space.putPublisher(className, function(aDelegate) {
        return new CursorPositionWillChangeEvent(aDelegate);
    });
    return CursorPositionWillChangeEvent;
});