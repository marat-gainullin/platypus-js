(function() {
    var javaClass = Java.type("com.eas.client.forms.events.ComponentEvent");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ComponentEvent(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor ComponentEvent ComponentEvent
     */
    P.ComponentEvent = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.ComponentEvent.superclass)
            P.ComponentEvent.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "source", {
            get: function() {
                var value = delegate.source;
                return P.boxAsJs(value);
            }
        });
        if(!P.ComponentEvent){
            /**
             * The source object of the event.
             * @property source
             * @memberOf ComponentEvent
             */
            P.ComponentEvent.prototype.source = {};
        }
    };
})();