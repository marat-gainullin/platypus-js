(function() {
    var javaClass = Java.type("com.eas.client.forms.events.WindowEvent");
    javaClass.setPublisher(function(aDelegate) {
        return new P.WindowEvent(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor WindowEvent WindowEvent
     */
    P.WindowEvent = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(P.WindowEvent.superclass)
            P.WindowEvent.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "source", {
            get: function() {
                var value = delegate.source;
                return P.boxAsJs(value);
            }
        });
        if(!P.WindowEvent){
            /**
             * The source object of the event.
             * @property source
             * @memberOf WindowEvent
             */
            P.WindowEvent.prototype.source = {};
        }
    };
})();