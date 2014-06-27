(function() {
    var javaClass = Java.type("com.eas.client.model.application.CursorPositionWillChangeEvent");
    javaClass.setPublisher(function(aDelegate) {
        return new P.CursorPositionWillChangeEvent(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor CursorPositionWillChangeEvent CursorPositionWillChangeEvent
     */
    P.CursorPositionWillChangeEvent = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.CursorPositionWillChangeEvent.superclass)
            P.CursorPositionWillChangeEvent.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "oldIndex", {
            get: function() {
                var value = delegate.oldIndex;
                return P.boxAsJs(value);
            }
        });
        if(!P.CursorPositionWillChangeEvent){
            /**
             * Cursor position the cursor is still on.
             * @property oldIndex
             * @memberOf CursorPositionWillChangeEvent
             */
            P.CursorPositionWillChangeEvent.prototype.oldIndex = 0;
        }
        Object.defineProperty(this, "source", {
            get: function() {
                var value = delegate.source;
                return P.boxAsJs(value);
            }
        });
        if(!P.CursorPositionWillChangeEvent){
            /**
             * The source object of the event.
             * @property source
             * @memberOf CursorPositionWillChangeEvent
             */
            P.CursorPositionWillChangeEvent.prototype.source = {};
        }
        Object.defineProperty(this, "newIndex", {
            get: function() {
                var value = delegate.newIndex;
                return P.boxAsJs(value);
            }
        });
        if(!P.CursorPositionWillChangeEvent){
            /**
             * Cursor position the cursor will be set on.
             * @property newIndex
             * @memberOf CursorPositionWillChangeEvent
             */
            P.CursorPositionWillChangeEvent.prototype.newIndex = 0;
        }
    };
})();