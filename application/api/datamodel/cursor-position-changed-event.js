(function() {
    var javaClass = Java.type("com.eas.client.model.application.CursorPositionChangedEvent");
    javaClass.setPublisher(function(aDelegate) {
        return new P.CursorPositionChangedEvent(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor CursorPositionChangedEvent CursorPositionChangedEvent
     */
    P.CursorPositionChangedEvent = function CursorPositionChangedEvent() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(CursorPositionChangedEvent.superclass)
            CursorPositionChangedEvent.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "CursorPositionChangedEvent", {value: CursorPositionChangedEvent});
    Object.defineProperty(CursorPositionChangedEvent.prototype, "oldIndex", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.oldIndex;
            return P.boxAsJs(value);
        }
    });
    if(!CursorPositionChangedEvent){
        /**
         * Cursor position the cursor was on.
         * @property oldIndex
         * @memberOf CursorPositionChangedEvent
         */
        P.CursorPositionChangedEvent.prototype.oldIndex = 0;
    }
    Object.defineProperty(CursorPositionChangedEvent.prototype, "source", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.source;
            return P.boxAsJs(value);
        }
    });
    if(!CursorPositionChangedEvent){
        /**
         * The source object of the event.
         * @property source
         * @memberOf CursorPositionChangedEvent
         */
        P.CursorPositionChangedEvent.prototype.source = {};
    }
    Object.defineProperty(CursorPositionChangedEvent.prototype, "newIndex", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.newIndex;
            return P.boxAsJs(value);
        }
    });
    if(!CursorPositionChangedEvent){
        /**
         * Cursor position the cursor has been set on.
         * @property newIndex
         * @memberOf CursorPositionChangedEvent
         */
        P.CursorPositionChangedEvent.prototype.newIndex = 0;
    }
})();