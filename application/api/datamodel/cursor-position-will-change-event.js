(function() {
    var javaClass = Java.type("com.eas.client.model.application.CursorPositionWillChangeEvent");
    javaClass.setPublisher(function(aDelegate) {
        return new P.CursorPositionWillChangeEvent(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor CursorPositionWillChangeEvent CursorPositionWillChangeEvent
     */
    P.CursorPositionWillChangeEvent = function CursorPositionWillChangeEvent() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(CursorPositionWillChangeEvent.superclass)
            CursorPositionWillChangeEvent.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "CursorPositionWillChangeEvent", {value: CursorPositionWillChangeEvent});
    Object.defineProperty(CursorPositionWillChangeEvent.prototype, "oldIndex", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.oldIndex;
            return P.boxAsJs(value);
        }
    });
    if(!CursorPositionWillChangeEvent){
        /**
         * Cursor position the cursor is still on.
         * @property oldIndex
         * @memberOf CursorPositionWillChangeEvent
         */
        P.CursorPositionWillChangeEvent.prototype.oldIndex = 0;
    }
    Object.defineProperty(CursorPositionWillChangeEvent.prototype, "source", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.source;
            return P.boxAsJs(value);
        }
    });
    if(!CursorPositionWillChangeEvent){
        /**
         * The source object of the event.
         * @property source
         * @memberOf CursorPositionWillChangeEvent
         */
        P.CursorPositionWillChangeEvent.prototype.source = {};
    }
    Object.defineProperty(CursorPositionWillChangeEvent.prototype, "newIndex", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.newIndex;
            return P.boxAsJs(value);
        }
    });
    if(!CursorPositionWillChangeEvent){
        /**
         * Cursor position the cursor will be set on.
         * @property newIndex
         * @memberOf CursorPositionWillChangeEvent
         */
        P.CursorPositionWillChangeEvent.prototype.newIndex = 0;
    }
})();