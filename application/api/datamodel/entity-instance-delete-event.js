(function() {
    var javaClass = Java.type("com.eas.client.model.application.EntityInstanceDeleteEvent");
    javaClass.setPublisher(function(aDelegate) {
        return new P.EntityInstanceDeleteEvent(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor EntityInstanceDeleteEvent EntityInstanceDeleteEvent
     */
    P.EntityInstanceDeleteEvent = function () {

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
         * The deleted element.
         * @property deleted
         * @memberOf EntityInstanceDeleteEvent
         */
        Object.defineProperty(this, "deleted", {
            get: function() {
                var value = delegate.deleted;
                return P.boxAsJs(value);
            }
        });

        /**
         * The source object of the event.
         * @property source
         * @memberOf EntityInstanceDeleteEvent
         */
        Object.defineProperty(this, "source", {
            get: function() {
                var value = delegate.source;
                return P.boxAsJs(value);
            }
        });


        delegate.setPublished(this);
    };
})();