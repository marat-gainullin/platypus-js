(function() {
    var javaClass = Java.type("com.eas.client.model.application.CursorPositionWillChangeEvent");
    javaClass.setPublisher(function(aDelegate) {
        return new P.CursorPositionWillChangeEvent(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @namespace CursorPositionWillChangeEvent
     */
    P.CursorPositionWillChangeEvent = function () {

        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
            arguments[maxArgs] : new javaClass();

        Object.defineProperty(this, "unwrap", {
            get: function() {
                return function() {
                    return delegate;
                };
            }
        });
        /**
        * Cursor position the cursor is still on.
         * @property oldIndex
         * @memberOf CursorPositionWillChangeEvent
        */
        Object.defineProperty(this, "oldIndex", {
            get: function() {
                var value = delegate.oldIndex;
                return P.boxAsJs(value);
            }
        });

        /**
        * The source object of the event.
         * @property source
         * @memberOf CursorPositionWillChangeEvent
        */
        Object.defineProperty(this, "source", {
            get: function() {
                var value = delegate.source;
                return P.boxAsJs(value);
            }
        });

        /**
        * Cursor position the cursor will be set on.
         * @property newIndex
         * @memberOf CursorPositionWillChangeEvent
        */
        Object.defineProperty(this, "newIndex", {
            get: function() {
                var value = delegate.newIndex;
                return P.boxAsJs(value);
            }
        });


        delegate.setPublished(this);
    };
})();