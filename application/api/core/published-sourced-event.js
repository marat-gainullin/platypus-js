(function() {
    var className = "com.eas.client.events.PublishedSourcedEvent";
    var javaClass = Java.type(className);
    var space = this['-platypus-scripts-space'];
    space.putPublisher(className, function(aDelegate) {
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
            configurable: true,
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