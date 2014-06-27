(function() {
    var javaClass = Java.type("com.eas.client.model.application.EntityInstanceChangeEvent");
    javaClass.setPublisher(function(aDelegate) {
        return new P.EntityInstanceChangeEvent(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor EntityInstanceChangeEvent EntityInstanceChangeEvent
     */
    P.EntityInstanceChangeEvent = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.EntityInstanceChangeEvent.superclass)
            P.EntityInstanceChangeEvent.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "newValue", {
            get: function() {
                var value = delegate.newValue;
                return P.boxAsJs(value);
            }
        });
        if(!P.EntityInstanceChangeEvent){
            /**
             * The new value.
             * @property newValue
             * @memberOf EntityInstanceChangeEvent
             */
            P.EntityInstanceChangeEvent.prototype.newValue = {};
        }
        Object.defineProperty(this, "propertyName", {
            get: function() {
                var value = delegate.propertyName;
                return P.boxAsJs(value);
            }
        });
        if(!P.EntityInstanceChangeEvent){
            /**
             * The changed property name.
             * @property propertyName
             * @memberOf EntityInstanceChangeEvent
             */
            P.EntityInstanceChangeEvent.prototype.propertyName = '';
        }
        Object.defineProperty(this, "oldValue", {
            get: function() {
                var value = delegate.oldValue;
                return P.boxAsJs(value);
            }
        });
        if(!P.EntityInstanceChangeEvent){
            /**
             * The old value.
             * @property oldValue
             * @memberOf EntityInstanceChangeEvent
             */
            P.EntityInstanceChangeEvent.prototype.oldValue = {};
        }
        Object.defineProperty(this, "source", {
            get: function() {
                var value = delegate.source;
                return P.boxAsJs(value);
            }
        });
        if(!P.EntityInstanceChangeEvent){
            /**
             * The source object of the event.
             * @property source
             * @memberOf EntityInstanceChangeEvent
             */
            P.EntityInstanceChangeEvent.prototype.source = {};
        }
        Object.defineProperty(this, "object", {
            get: function() {
                var value = delegate.object;
                return P.boxAsJs(value);
            }
        });
        if(!P.EntityInstanceChangeEvent){
            /**
             * The updated element.
             * @property object
             * @memberOf EntityInstanceChangeEvent
             */
            P.EntityInstanceChangeEvent.prototype.object = {};
        }
    };
})();