(function() {
    var javaClass = Java.type("com.eas.client.model.application.EntityInstanceChangeEvent");
    javaClass.setPublisher(function(aDelegate) {
        return new P.EntityInstanceChangeEvent(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor EntityInstanceChangeEvent EntityInstanceChangeEvent
     */
    P.EntityInstanceChangeEvent = function EntityInstanceChangeEvent() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(EntityInstanceChangeEvent.superclass)
            EntityInstanceChangeEvent.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "EntityInstanceChangeEvent", {value: EntityInstanceChangeEvent});
    Object.defineProperty(EntityInstanceChangeEvent.prototype, "newValue", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.newValue;
            return P.boxAsJs(value);
        }
    });
    if(!EntityInstanceChangeEvent){
        /**
         * The new value.
         * @property newValue
         * @memberOf EntityInstanceChangeEvent
         */
        P.EntityInstanceChangeEvent.prototype.newValue = {};
    }
    Object.defineProperty(EntityInstanceChangeEvent.prototype, "propertyName", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.propertyName;
            return P.boxAsJs(value);
        }
    });
    if(!EntityInstanceChangeEvent){
        /**
         * The changed property name.
         * @property propertyName
         * @memberOf EntityInstanceChangeEvent
         */
        P.EntityInstanceChangeEvent.prototype.propertyName = '';
    }
    Object.defineProperty(EntityInstanceChangeEvent.prototype, "oldValue", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.oldValue;
            return P.boxAsJs(value);
        }
    });
    if(!EntityInstanceChangeEvent){
        /**
         * The old value.
         * @property oldValue
         * @memberOf EntityInstanceChangeEvent
         */
        P.EntityInstanceChangeEvent.prototype.oldValue = {};
    }
    Object.defineProperty(EntityInstanceChangeEvent.prototype, "source", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.source;
            return P.boxAsJs(value);
        }
    });
    if(!EntityInstanceChangeEvent){
        /**
         * The source object of the event.
         * @property source
         * @memberOf EntityInstanceChangeEvent
         */
        P.EntityInstanceChangeEvent.prototype.source = {};
    }
    Object.defineProperty(EntityInstanceChangeEvent.prototype, "object", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.object;
            return P.boxAsJs(value);
        }
    });
    if(!EntityInstanceChangeEvent){
        /**
         * The updated element.
         * @property object
         * @memberOf EntityInstanceChangeEvent
         */
        P.EntityInstanceChangeEvent.prototype.object = {};
    }
})();