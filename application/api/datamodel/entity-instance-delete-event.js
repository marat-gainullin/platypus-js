(function() {
    var javaClass = Java.type("com.eas.client.model.application.EntityInstanceDeleteEvent");
    javaClass.setPublisher(function(aDelegate) {
        return new P.EntityInstanceDeleteEvent(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor EntityInstanceDeleteEvent EntityInstanceDeleteEvent
     */
    P.EntityInstanceDeleteEvent = function EntityInstanceDeleteEvent() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(EntityInstanceDeleteEvent.superclass)
            EntityInstanceDeleteEvent.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "EntityInstanceDeleteEvent", {value: EntityInstanceDeleteEvent});
    Object.defineProperty(EntityInstanceDeleteEvent.prototype, "deleted", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.deleted;
            return P.boxAsJs(value);
        }
    });
    if(!EntityInstanceDeleteEvent){
        /**
         * The deleted element.
         * @property deleted
         * @memberOf EntityInstanceDeleteEvent
         */
        P.EntityInstanceDeleteEvent.prototype.deleted = {};
    }
    Object.defineProperty(EntityInstanceDeleteEvent.prototype, "source", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.source;
            return P.boxAsJs(value);
        }
    });
    if(!EntityInstanceDeleteEvent){
        /**
         * The source object of the event.
         * @property source
         * @memberOf EntityInstanceDeleteEvent
         */
        P.EntityInstanceDeleteEvent.prototype.source = {};
    }
})();