(function() {
    var javaClass = Java.type("com.eas.client.events.PublishedSourcedEvent");
    javaClass.setPublisher(function(aDelegate) {
        return new P.PublishedSourcedEvent(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor PublishedSourcedEvent PublishedSourcedEvent
     */
    P.PublishedSourcedEvent = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.PublishedSourcedEvent.superclass)
            P.PublishedSourcedEvent.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "source", {
            get: function() {
                var value = delegate.source;
                return P.boxAsJs(value);
            }
        });
        if(!P.PublishedSourcedEvent){
            /**
             * The source object of the event.
             * @property source
             * @memberOf PublishedSourcedEvent
             */
            P.PublishedSourcedEvent.prototype.source = {};
        }
    };
})();