(function() {
    var javaClass = Java.type("com.eas.client.model.application.EntityInstanceInsertEvent");
    javaClass.setPublisher(function(aDelegate) {
        return new P.EntityInstanceInsertEvent(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor EntityInstanceInsertEvent EntityInstanceInsertEvent
     */
    P.EntityInstanceInsertEvent = function EntityInstanceInsertEvent() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(EntityInstanceInsertEvent.superclass)
            EntityInstanceInsertEvent.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "EntityInstanceInsertEvent", {value: EntityInstanceInsertEvent});
    Object.defineProperty(EntityInstanceInsertEvent.prototype, "inserted", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.inserted;
            return P.boxAsJs(value);
        }
    });
    if(!EntityInstanceInsertEvent){
        /**
         * The inserted element.
         * @property inserted
         * @memberOf EntityInstanceInsertEvent
         */
        P.EntityInstanceInsertEvent.prototype.inserted = {};
    }
    Object.defineProperty(EntityInstanceInsertEvent.prototype, "source", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.source;
            return P.boxAsJs(value);
        }
    });
    if(!EntityInstanceInsertEvent){
        /**
         * The source object of the event.
         * @property source
         * @memberOf EntityInstanceInsertEvent
         */
        P.EntityInstanceInsertEvent.prototype.source = {};
    }
    Object.defineProperty(EntityInstanceInsertEvent.prototype, "object", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.object;
            return P.boxAsJs(value);
        }
    });
    if(!EntityInstanceInsertEvent){
        /**
         * The inserted element.
         * @property object
         * @memberOf EntityInstanceInsertEvent
         */
        P.EntityInstanceInsertEvent.prototype.object = {};
    }
})();