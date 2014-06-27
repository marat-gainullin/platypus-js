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
            value: function() {
                return delegate;
            }
        });
        if(P.CursorPositionChangedEvent.superclass)
            P.CursorPositionChangedEvent.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "oldIndex", {
            get: function() {
                var value = delegate.oldIndex;
                return P.boxAsJs(value);
            }
        });
        if(!P.CursorPositionChangedEvent){
            /**
             * Cursor position the cursor was on.
             * @property oldIndex
             * @memberOf CursorPositionChangedEvent
             */
            P.CursorPositionChangedEvent.prototype.oldIndex = 0;
        }
        Object.defineProperty(this, "source", {
            get: function() {
                var value = delegate.source;
                return P.boxAsJs(value);
            }
        });
        if(!P.CursorPositionChangedEvent){
            /**
             * The source object of the event.
             * @property source
             * @memberOf CursorPositionChangedEvent
             */
            P.CursorPositionChangedEvent.prototype.source = {};
        }
        Object.defineProperty(this, "newIndex", {
            get: function() {
                var value = delegate.newIndex;
                return P.boxAsJs(value);
            }
        });
        if(!P.CursorPositionChangedEvent){
            /**
             * Cursor position the cursor has been set on.
             * @property newIndex
             * @memberOf CursorPositionChangedEvent
             */
            P.CursorPositionChangedEvent.prototype.newIndex = 0;
        }
    };
})();