(function() {
    var javaClass = Java.type("com.eas.client.events.PublishedSourcedEvent");
    javaClass.setPublisher(function(aDelegate) {
        return new P.PublishedSourcedEvent(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor PublishedSourcedEvent PublishedSourcedEvent
     */
    P.PublishedSourcedEvent = function PublishedSourcedEvent() {

        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(PublishedSourcedEvent.superclass)
            PublishedSourcedEvent.superclass.constructor.apply(this, arguments);
        /**
         * The source object of the event.
         * @property source
         * @memberOf PublishedSourcedEvent
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