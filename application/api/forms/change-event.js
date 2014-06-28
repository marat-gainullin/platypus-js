(function() {
    var javaClass = Java.type("com.eas.client.forms.api.events.ChangeEvent");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ChangeEvent(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor ChangeEvent ChangeEvent
     */
    P.ChangeEvent = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.ChangeEvent.superclass)
            P.ChangeEvent.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "source", {
            get: function() {
                var value = delegate.source;
                return P.boxAsJs(value);
            }
        });
        if(!P.ChangeEvent){
            /**
             * The source component object of the event.
             * @property source
             * @memberOf ChangeEvent
             */
            P.ChangeEvent.prototype.source = {};
        }
    };
})();