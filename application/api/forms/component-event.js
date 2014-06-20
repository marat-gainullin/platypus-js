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
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "ComponentEvent", {value: ComponentEvent});
    Object.defineProperty(ComponentEvent.prototype, "source", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.source;
            return P.boxAsJs(value);
        }
    });
    if(!ComponentEvent){
        /**
         * The source component object of the event.
         * @property source
         * @memberOf ComponentEvent
         */
        P.ComponentEvent.prototype.source = {};
    }
})();