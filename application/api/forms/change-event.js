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
            get: function() {
                return function() {
                    return delegate;
                };
            }
        });
        /**
         * The source component object of the event.
         * @property source
         * @memberOf ChangeEvent
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