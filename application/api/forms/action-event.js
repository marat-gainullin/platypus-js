(function() {
    var javaClass = Java.type("com.eas.client.forms.api.events.ActionEvent");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ActionEvent(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor ActionEvent ActionEvent
     */
    P.ActionEvent = function () {

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
         * @memberOf ActionEvent
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