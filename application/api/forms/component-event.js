(function() {
    var javaClass = Java.type("com.eas.client.forms.api.events.ComponentEvent");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ComponentEvent(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor ComponentEvent ComponentEvent
     */
    P.ComponentEvent = function ComponentEvent() {

        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(ComponentEvent.superclass)
            ComponentEvent.superclass.constructor.apply(this, arguments);
        /**
         * The source component object of the event.
         * @property source
         * @memberOf ComponentEvent
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