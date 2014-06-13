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
            get: function() {
                return function() {
                    return delegate;
                };
            }
        });
        /**
         * The new value.
         * @property newValue
         * @memberOf EntityInstanceChangeEvent
         */
        Object.defineProperty(this, "newValue", {
            get: function() {
                var value = delegate.newValue;
                return P.boxAsJs(value);
            }
        });

        /**
         * The changed property name.
         * @property propertyName
         * @memberOf EntityInstanceChangeEvent
         */
        Object.defineProperty(this, "propertyName", {
            get: function() {
                var value = delegate.propertyName;
                return P.boxAsJs(value);
            }
        });

        /**
         * The old value.
         * @property oldValue
         * @memberOf EntityInstanceChangeEvent
         */
        Object.defineProperty(this, "oldValue", {
            get: function() {
                var value = delegate.oldValue;
                return P.boxAsJs(value);
            }
        });

        /**
         * The source object of the event.
         * @property source
         * @memberOf EntityInstanceChangeEvent
         */
        Object.defineProperty(this, "source", {
            get: function() {
                var value = delegate.source;
                return P.boxAsJs(value);
            }
        });

        /**
         * The updated element.
         * @property object
         * @memberOf EntityInstanceChangeEvent
         */
        Object.defineProperty(this, "object", {
            get: function() {
                var value = delegate.object;
                return P.boxAsJs(value);
            }
        });


        delegate.setPublished(this);
    };
})();