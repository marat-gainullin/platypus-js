(function() {
    var javaClass = Java.type("com.eas.client.model.application.EntityInstanceInsertEvent");
    javaClass.setPublisher(function(aDelegate) {
        return new P.EntityInstanceInsertEvent(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor EntityInstanceInsertEvent EntityInstanceInsertEvent
     */
    P.EntityInstanceInsertEvent = function () {

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
         * The inserted element.
         * @property inserted
         * @memberOf EntityInstanceInsertEvent
         */
        Object.defineProperty(this, "inserted", {
            get: function() {
                var value = delegate.inserted;
                return P.boxAsJs(value);
            }
        });

        /**
         * The source object of the event.
         * @property source
         * @memberOf EntityInstanceInsertEvent
         */
        Object.defineProperty(this, "source", {
            get: function() {
                var value = delegate.source;
                return P.boxAsJs(value);
            }
        });

        /**
         * The inserted element.
         * @property object
         * @memberOf EntityInstanceInsertEvent
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