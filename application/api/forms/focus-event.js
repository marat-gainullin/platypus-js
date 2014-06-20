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
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "FocusEvent", {value: FocusEvent});
    Object.defineProperty(FocusEvent.prototype, "source", {
        get: function() {
            var delegate = this.unwrap();
            var value = delegate.source;
            return P.boxAsJs(value);
        }
    });
    if(!FocusEvent){
        /**
         * The source component object of the event.
         * @property source
         * @memberOf FocusEvent
         */
        P.FocusEvent.prototype.source = {};
    }
})();