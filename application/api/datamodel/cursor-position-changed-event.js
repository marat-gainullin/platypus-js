(function() {
    var javaClass = Java.type("com.eas.client.model.application.CursorPositionChangedEvent");
    javaClass.setPublisher(function(aDelegate) {
        return new P.CursorPositionChangedEvent(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor CursorPositionChangedEvent CursorPositionChangedEvent
     */
    P.CursorPositionChangedEvent = function () {

        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            get: function() {
                return function() {
                    return delegate;
                };
            }
        });
        /**
         * Cursor position the cursor was on.
         * @property oldIndex
         * @memberOf CursorPositionChangedEvent
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
         * @memberOf CursorPositionChangedEvent
         */
        Object.defineProperty(this, "source", {
            get: function() {
                var value = delegate.source;
                return P.boxAsJs(value);
            }
        });

        /**
         * Cursor position the cursor has been set on.
         * @property newIndex
         * @memberOf CursorPositionChangedEvent
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