(function() {
    var javaClass = Java.type("com.eas.client.forms.events.FocusEvent");
    javaClass.setPublisher(function(aDelegate) {
        return new P.FocusEvent(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor FocusEvent FocusEvent
     */
    P.FocusEvent = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.FocusEvent.superclass)
            P.FocusEvent.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "source", {
            get: function() {
                var value = delegate.source;
                return P.boxAsJs(value);
            }
        });
        if(!P.FocusEvent){
            /**
             * The source object of the event.
             * @property source
             * @memberOf FocusEvent
             */
            P.FocusEvent.prototype.source = {};
        }
    };
})();