(function() {
    var javaClass = Java.type("com.eas.client.forms.api.events.FocusEvent");
    javaClass.setPublisher(function(aDelegate) {
        return new P.FocusEvent(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor FocusEvent FocusEvent
     */
    P.FocusEvent = function FocusEvent() {

        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(FocusEvent.superclass)
            FocusEvent.superclass.constructor.apply(this, arguments);
        /**
         * The source component object of the event.
         * @property source
         * @memberOf FocusEvent
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